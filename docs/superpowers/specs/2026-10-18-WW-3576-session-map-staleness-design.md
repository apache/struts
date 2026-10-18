# WW-3576 — SessionMap staleness and the execAndWait session snapshot

Ticket: [WW-3576](https://issues.apache.org/jira/browse/WW-3576) — fix version 7.5.0.
Previous attempt: PR #1577 (closed 2026-10-17; changed the lock to `this` and swallowed `IllegalStateException`).

## Intent

`SessionMap` is a wrapper around `HttpSession`, but it caches state that belongs to the container: the
`session` reference and the `entries` snapshot. Any operation on the real session — invalidation by
another request, attributes set directly on `HttpSession` — makes the wrapper outdated. The ticket's
two symptoms are both staleness:

1. **NPE race (confirmed on `main`).** Every method does `if (session == null) return;` then
   `synchronized (session.getId().intern()) { session.… }`. The field is re-read inside the lock; a
   concurrent `invalidate()` on the same map nulls it in between. Realistic trigger: `execAndWait`,
   where the background thread binds the *same* `ActionContext` (and therefore the same `SessionMap`)
   as the request that started it (`StrutsBackgroundProcess#beforeInvocation`).
2. **`IllegalStateException` "already invalidated"** (the reporter's stress-test symptom). Another
   request invalidated the `HttpSession`; this request's `SessionMap` still holds the dead reference.

Fix the staleness, not the locking: one map that caches nothing but the session it pinned and notices
when that session dies (request threads), and one map that is stale *by design* and explicit about it
(the `execAndWait` background thread), with a controlled write-back.

## Goals

- Request-thread `SessionMap` always reflects the pinned session's live attributes; an invalidated
  session reads as "no session" instead of throwing.
- `execAndWait` background code gets a session view that does not depend on the (recycled) request.
- Background writes reach the real session once, at a well-defined point, never across users.
- Per-action control over write-back conflicts via a `*Aware` interface.

## Non-goals

- Making the rest of the shared `ActionContext` (request, response, parameters) safe for background
  threads.
- Live, mid-process propagation of background writes. Progress reporting already goes through the
  action (`ExecuteAndWaitInterceptor` pushes `bp.getAction()` on every poll), not through the session.
- A global (application-wide) conflict strategy bean.
- Enforcing the sandbox (throwing on escapes) or isolating the background thread's value stack and
  `ActionContext`. That is a rewrite of the `execAndWait` mechanism to run in a real sandbox, planned
  for 8.0.0 as [WW-5787](https://issues.apache.org/jira/browse/WW-5787).

## Anti-goals

- Catching `IllegalStateException` and carrying on as if the session were alive (PR #1577 swallowed it in
  every method and kept the dead reference). Catching it to *detect* invalidation, un-pin, and answer
  "no session" is the design — including a `put` that discovers the invalidation, un-pins, and pins a
  new session once through the normal `request.getSession(true)` path (no loop).
- Any request-thread map that resolves its session through a request after that request has ended.
- Any path by which background writes land in a session other than the one the process started in.

## Design

### 1. Request-scoped `SessionMap` (default)

- **Pins the session it first sees (decided)**, as today: `request.getSession(false)` at construction,
  `request.getSession(true)` on the first `put` when none is pinned. It does **not** re-resolve through
  the request on every call — code holding the map past its request (async-plugin `Callable`s, stored
  `SessionAware` fields) must never resolve through a recycled request, which may be another user's.
- **Invalidation is detected, not hidden:** an `IllegalStateException` from the pinned session means it
  was invalidated (by another request or directly); the map un-pins it and answers as "no session"
  (`null` / `false` / empty set / no-op). A later `put` obtains a session through
  `request.getSession(true)` — **only while the request is live**.
- **Detached at request end (decided after the final review):** the request cleanup in the dispatcher
  marks the request's `SessionMap` detached. A detached map never calls its request again: with a live
  pinned session it keeps working (async `Callable`s, stored `SessionAware` fields); a `put` with no live
  pinned session throws `IllegalStateException`. Reason: a background thread (sandbox escape, custom
  `BackgroundProcess`) reaching `request.getSession(true)` after the request was recycled would write
  into another user's session. This also closes the same path for a never-pinned map used after its
  request ended, which predates this change. Detach is package-private.
- The pinned reference is read once per call into a local (no check-then-reread), so a concurrent
  `invalidate()` on the same map cannot NPE; how it is published (`volatile`) is the builder's.
- **No `entries` cache:** `entrySet()` is built fresh on every call; an entry's `setValue` writes
  straight through to the pinned session.
- `invalidate()` invalidates the pinned session, if any, and un-pins it.
- `put`/`remove` keep obtaining the old value through the overridable `get(key)` —
  `SessionMapTest.MockSessionMap` pins that subclass contract.
- **No locking at all.** Single attribute calls are thread-safe per the Servlet spec. `put`/`remove`
  return an advisory old value; nothing in the framework relies on cross-request atomicity
  (decided). The `session.getId().intern()` monitor is removed — it was a lock on the JVM-wide string
  pool.
- `protected Set<…> entries` becomes `@Deprecated(since = "7.5.0", forRemoval = true)` and is no
  longer read by `SessionMap`; file a WW Task at 8.0.0 for its removal (do not reference it in source
  before it exists). Whether `protected HttpSession session` stays the pinned field or is deprecated
  behind a private one is the builder's, provided subclass access keeps compiling in 7.5.0. Changed
  subclass-visible behaviour goes in the 7.5.0 migration notes.

### 2. Sandboxed background session (`execAndWait` only)

A `SessionMap` subclass (so existing `((SessionMap) session).invalidate()` casts keep working) that
never touches the request:

- **Taken on the request thread**, when the interceptor creates the `BackgroundProcess` (inside the
  existing `synchronized (httpSession)` block) — never on the background thread, where the request
  may already be recycled. Holds: a shallow copy of the session attributes, the session id at that
  moment, and a log of its own writes (puts, removes, and an invalidate request).
- Reads return the copy with the logged writes applied.
- **Installed for the background thread:** the background thread binds an `ActionContext` that is a
  copy of the invocation context with `withSession(snapshot)`, so the original request thread keeps
  its own request map. If the action is `SessionAware`, it is re-injected with the snapshot before the
  invocation runs.
- **Sandbox model (decided):** the background process works only on its sandboxed session; the
  supported way to change the real session is to write to the sandbox and let write-back reconcile it
  (with the per-action hook deciding conflicts). There is deliberately no callback handing background
  code the real session. Supported entry points: `ActionContext.getContext().getSession()` and
  `SessionAware`.
- **Sandbox escapes (decided: warn only in 7.5.0):** OGNL `#session` and
  `invocation.getInvocationContext().getSession()` still reach the request's pinned map, and writes
  through them go live immediately, as today. A write through a request map from an `execAndWait`
  background thread logs one WARN per background process (action name, never attribute values),
  pointing to the sandbox entry points. Documented as dangerous in the `ExecuteAndWaitInterceptor` /
  `BackgroundProcess` Javadoc, the `execAndWait` docs on struts-site, and the 7.5.0 migration notes. How
  the map recognises a background thread is the builder's.
- *(Builder-check call, refined while planning)* Handed over through **default methods on
  `BackgroundProcess`** — a fluent `withSessionSandbox(…)` (no-op by default) called by the interceptor
  on whatever the overridable `getNewBackgroundProcess(…)` factory returns, and `getSessionSandbox()`
  (null by default). Installing it happens in `StrutsBackgroundProcess`'s private thread body, *after*
  the overridable `beforeInvocation()` (whose default binds the original context), so neither `super`
  nor a subclass that skips it can undo it. A custom `BackgroundProcess` not
  extending `StrutsBackgroundProcess` keeps today's shared-map behaviour — migration-notes line.
- Not serialized: held `transient` by the background process. A deserialized process already has a
  null invocation and is restarted (WW-4900), so the snapshot is lost with it.

### 3. Write-back

- Happens on the **polling request that observes `bp.isDone()`** (the `else if (… bp.isDone())` branch
  in `ExecuteAndWaitInterceptor#doIntercept`), on a request thread, inside the existing
  `synchronized (httpSession)`, before the result is returned.
- **Session-id gate (decided, not overridable):** if the current session id differs from the snapshot's
  start id, or there is no session, all background writes are discarded and logged at debug.
- Otherwise apply **only the logged writes**: keys the background never touched are left alone.
- A logged `invalidate()` is applied after the id gate passes; the conflict hook is not consulted for it.
- After write-back, a `SessionAware` action is re-injected with the request's session map so the
  result rendering writes to the live session.
- If nobody polls after completion, the writes are never applied (the session expires with them).
- *(Builder-check call)* Write-back runs **before** `throw bp.getException()`: a failed background
  action's writes are applied, matching today's behaviour where its writes were already live.
- *(Builder-check call)* The write log is an **ordered operation list** (put / remove / invalidate).
  After a logged `invalidate()`, snapshot reads see an empty session. At write-back the real session is
  invalidated, a fresh one is created on the polling request (`getSession(true)` — the background asked
  for this, e.g. a login flow, so it is not the anti-goal's "absorb a write after *another* request
  invalidated"), and the operations after the invalidate are applied to it without conflict checks.

### 4. Conflicts and the per-action hook

- **Conflict:** a key the background wrote (put or remove) whose value in the real session no longer
  `equals` the snapshot value (absent counts as a value). Known limit: an object mutated in place
  without a new `setAttribute` is the same reference, so it is not detected.
- **Default (decided):** keep the real session's value, drop the background's, log at debug.
- **Hook (decided: per action, `*Aware`):** an action implementing the new interface is consulted once
  per conflicting key with: key, snapshot value, current value, background value. It must be able to
  express: keep current, take background, set a different value, remove the key. Lives in
  `org.apache.struts2.action` next to `SessionAware`/`ParameterNameAware`. Proposed names (builder may
  refine): `BackgroundSessionConflictAware`, method
  `resolveSessionConflict(SessionConflict)` returning a `SessionConflictResolution`.
- An exception thrown by the hook propagates like any other action exception from the polling request.

## Testing

Match the target file's JUnit style: `SessionMapTest` extends `junit.framework.TestCase`,
`ExecuteAndWaitInterceptorTest` extends `StrutsInternalTestCase` (both JUnit 3 — `testXxx()` names, no
`@Test`).

1. **RED first — deterministic NPE reproduction** on current `main`: one `SessionMap`, thread A in
   `invalidate()` held by a latch inside `HttpSession#invalidate`, thread B calling `get()`; on current
   code B blocks on the monitor and then NPEs. The assertion is behavioural ("`get()` concurrent with
   `invalidate()` returns a value or null and never throws"), so it passes on the new code where B does
   not block. No sleep-based stress tests.
2. **Reporter's case:** two maps over the same session; the session is invalidated through the other
   one (or directly); `get`/`containsKey`/`entrySet`/`remove` return empty results, no
   `IllegalStateException`; `put` creates a new session only because the request has none.
3. Staleness: attribute set directly on `HttpSession` is visible through an existing map and its
   `entrySet()`.
4. Snapshot: reads/writes isolated from the live session while running; write-back applies only logged
   keys; id change discards everything; default conflict keeps current; hook outcomes (all four);
   logged invalidate applied; `SessionAware` re-injection in both directions.
5. Sandbox escape: a write through the request map from a background thread goes live and logs exactly
   one WARN per process; writes through the sandbox log nothing.
6. Existing `SessionMapTest`, `ExecuteAndWaitInterceptorTest` and the tests constructing
   `new SessionMap(request)` keep passing.

## Left to the builder

- Class names of the snapshot map and the conflict/resolution types; whether resolution is an enum plus
  value or a small sealed hierarchy.
- How the `ActionContext` copy is made for the background thread.
- Whether `CreateSessionInterceptor` is simplified in this change.
- Debug log wording.
