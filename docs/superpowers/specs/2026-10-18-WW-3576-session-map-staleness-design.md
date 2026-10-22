# WW-3576 — SessionMap staleness

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
when that session dies.

## Goals

- Request-thread `SessionMap` always reflects the pinned session's live attributes; an invalidated
  session reads as "no session" instead of throwing.
- No map resolves a session through a request that has already ended.

## Non-goals

- Making the shared `ActionContext` safe for `execAndWait` background threads, including isolating
  their session writes. A session sandbox with controlled write-back was designed and built on this
  ticket, then split out: it changes observable `execAndWait` behaviour, so it moves to the 8.0.0
  rewrite, [WW-5787](https://issues.apache.org/jira/browse/WW-5787).

## Anti-goals

- Catching `IllegalStateException` and carrying on as if the session were alive (PR #1577 swallowed it in
  every method and kept the dead reference). Catching it to *detect* invalidation, un-pin, and answer
  "no session" is the design — including a `put` that discovers the invalidation, un-pins, and pins a
  new session once through the normal `request.getSession(true)` path (no loop).
- Any request-thread map that resolves its session through a request after that request has ended.

## Design

### Request-scoped `SessionMap`

- **Pins the session it first sees (decided)**, as today: `request.getSession(false)` at construction,
  `request.getSession(true)` on the first `put` when none is pinned. It does **not** re-resolve through
  the request on every call — code holding the map past its request (async-plugin `Callable`s, stored
  `SessionAware` fields) must never resolve a session through a request that has already ended.
- **Invalidation is detected, not hidden:** an `IllegalStateException` from the pinned session means it
  was invalidated (by another request or directly); the map un-pins it and answers as "no session"
  (`null` / `false` / empty set / no-op). A later `put` obtains a session through
  `request.getSession(true)` — **only while the request is live**.
- **Detached at request end (decided after the final review):** the request cleanup in the dispatcher
  detaches every `SessionMap` built for that request (each registers itself on the request at
  construction), not only the bound context's: this covers the original map left behind when
  `createSession` replaces `ActionContext.SESSION`, and the maps of a nested forward. While the request
  is in async mode, detach (of all request maps, including the bound context's) is deferred to the
  async dispatch's cleanup. A detached map never calls its request again: with a live pinned session it
  keeps working (async `Callable`s, stored `SessionAware` fields); a `put` with no live pinned session
  throws `IllegalStateException`. Reason: a background thread (`execAndWait`, custom
  `BackgroundProcess`) must not resolve a session through a request that has already ended; this applies
  equally to a never-pinned map used after its request ended. Detach is package-private.
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
- `StrutsBackgroundProcess.done` becomes `volatile`: the polling request reads the flag the background
  thread writes.

## Testing

Match the target file's JUnit style: `SessionMapTest` extends `junit.framework.TestCase` (JUnit 3 —
`testXxx()` names, no `@Test`).

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
4. Existing `SessionMapTest`, `ExecuteAndWaitInterceptorTest` and the tests constructing
   `new SessionMap(request)` keep passing.

## Left to the builder

- Whether `CreateSessionInterceptor` is simplified in this change.
- Debug log wording.
