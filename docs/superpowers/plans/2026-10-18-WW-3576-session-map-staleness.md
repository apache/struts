# WW-3576 SessionMap Staleness and execAndWait Session Sandbox — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Stop `SessionMap` from going stale (NPE race, `IllegalStateException` after invalidation) and give `execAndWait` background processes a sandboxed session reconciled on the polling request.

**Architecture:** The request `SessionMap` pins its session, reads it once per call into a local, treats `IllegalStateException` as "invalidated → no session", and drops the `entries` cache and the interned-id lock. A new `SandboxedSessionMap` (snapshot + ordered operation log) is created by `ExecuteAndWaitInterceptor` on the request thread, installed by `StrutsBackgroundProcess` on the background thread, and reconciled by `SessionSandboxReconciler` on the polling request that sees `isDone()`, gated on the session id, with per-action conflict resolution through `BackgroundSessionConflictAware`.

**Tech Stack:** Java 17, Jakarta Servlet 6, JUnit 3 (`junit.framework.TestCase` / `StrutsInternalTestCase`), Spring `MockHttpServletRequest`/`MockHttpSession`, AssertJ, Log4j2 via `org.apache.struts2.test.LogCapture`.

**Spec:** `docs/superpowers/specs/2026-10-18-WW-3576-session-map-staleness-design.md`

## Global Constraints

- Branch `feature/WW-3576-session-map-staleness`; every commit message starts `WW-3576 `; stage explicit paths only (never `git add -A`), never `git stash`.
- Tests are JUnit 3 in every file touched here: `testXxx()` names, no `@Test`.
- Build with `./mvnw` (no `mvn` on PATH); check Maven's exit code, not a grep of its output.
- Deprecations are `@Deprecated(since = "7.5.0", forRemoval = true)`; no reference in source to a ticket that is not filed.
- No null guards on required beans; no parameter reassignment; no boolean flag arguments; default to no comments.
- Never log session attribute values.
- The session-id gate is not overridable by the hook.

## Review Focus

- **Map used after its request ended** (async-plugin `Callable`, stored `SessionAware` field): with a session pinned, no call may touch the request — Task 2 `testPinnedSessionNeverTouchesTheRequestAgain` (closed `MockHttpServletRequest`).
- **`put(key, null)`** on the sandbox: Servlet semantics treat it as a removal, so it is logged as one — Task 3 `testPutNullIsLoggedAsRemove`.
- **`entrySet()` entry `setValue` on the sandbox**: must be logged like a `put`, not lost — Task 3 `testEntrySetValueIsLogged`.
- **Visibility between the background thread and the polling request**: the operation log written on one thread is read on another — Task 3 makes `SandboxedSessionMap` methods `synchronized`; Task 5 `testBackgroundWritesReachTheRealSession` exercises it across real threads.
- **Hook throws**: the exception propagates from the polling request, and no partial write-back is hidden — Task 4 `testHookExceptionPropagates`.

---

### Task 1: Deterministic NPE reproduction and race-free `SessionMap`

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/SessionMap.java`
- Test: `core/src/test/java/org/apache/struts2/dispatcher/SessionMapTest.java`

**Interfaces:**
- Produces: `SessionMap` with the same public API; `protected volatile HttpSession session` stays the pinned field.

- [ ] **Step 1: Write the failing test** (in `SessionMapTest`)

```java
public void testGetConcurrentWithInvalidateNeverThrows() throws Exception {
    CountDownLatch invalidating = new CountDownLatch(1);
    CountDownLatch release = new CountDownLatch(1);
    MockHttpSession session = new MockHttpSession() {
        @Override
        public void invalidate() {
            invalidating.countDown();
            try { release.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            super.invalidate();
        }
    };
    session.setAttribute("key", "value");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setSession(session);
    SessionMap sessionMap = new SessionMap(request);
    AtomicReference<Throwable> failure = new AtomicReference<>();

    Thread invalidator = new Thread(sessionMap::invalidate);
    invalidator.start();
    invalidating.await();
    Thread reader = new Thread(() -> {
        try { sessionMap.get("key"); } catch (Throwable t) { failure.set(t); }
    });
    reader.start();
    long deadline = System.currentTimeMillis() + 5000;
    while (reader.getState() != Thread.State.BLOCKED && reader.getState() != Thread.State.TERMINATED) {
        assertTrue("reader neither blocked nor finished", System.currentTimeMillis() < deadline);
        Thread.sleep(1);
    }
    release.countDown();
    invalidator.join(5000);
    reader.join(5000);

    assertNull("get() concurrent with invalidate() threw", failure.get());
}
```

- [ ] **Step 2: Run it and watch it fail on current code**

Run: `./mvnw -q test -DskipAssembly -pl core -Dtest=SessionMapTest#testGetConcurrentWithInvalidateNeverThrows -Dsurefire.failIfNoSpecifiedTests=false; echo exit=$?`
Expected: `exit=1`, failure message `get() concurrent with invalidate() threw` caused by `NullPointerException ... because "this.session" is null`. Run it 3 times; it must fail every time.

- [ ] **Step 3: Make every method read `session` once into a local and drop the interned-id lock**

`session` becomes `protected volatile HttpSession session`. Each method: `HttpSession current = session; if (current == null) return <empty>;` then operate on `current` only. Remove every `synchronized (… getId().intern())` block. `put` keeps `synchronized (this)` only around pinning the session from `request.getSession(true)` when none is pinned, and obtains the old value through the overridable `get(key)` (pinned by `MockSessionMap`). `invalidate()` sets `session = null` after `current.invalidate()`.

- [ ] **Step 4: Run `SessionMapTest` and verify it passes**

Run: `./mvnw -q test -DskipAssembly -pl core -Dtest=SessionMapTest -Dsurefire.failIfNoSpecifiedTests=false; echo exit=$?`
Expected: `exit=0`, the new test plus the 12 existing ones pass.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/dispatcher/SessionMap.java core/src/test/java/org/apache/struts2/dispatcher/SessionMapTest.java
git commit -m "WW-3576 fix(core): read SessionMap's session once per call and drop the interned-id lock"
```

### Task 2: Invalidation detection and no `entries` cache

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/SessionMap.java`
- Test: `core/src/test/java/org/apache/struts2/dispatcher/SessionMapTest.java`

**Interfaces:**
- Consumes: Task 1's pinned `session` field.
- Produces: `entries` field deprecated and unread; un-pinning on `IllegalStateException`.

- [ ] **Step 1: Write the failing tests**

```java
public void testSessionInvalidatedElsewhereReadsAsNoSession() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute("key", "value");
    request.setSession(session);
    SessionMap sessionMap = new SessionMap(request);

    session.invalidate();

    assertNull(sessionMap.get("key"));
    assertFalse(sessionMap.containsKey("key"));
    assertTrue(sessionMap.entrySet().isEmpty());
    assertNull(sessionMap.remove("key"));
    sessionMap.clear();
    sessionMap.invalidate();
}

public void testPutAfterInvalidationElsewhereUsesANewSession() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    request.setSession(session);
    SessionMap sessionMap = new SessionMap(request);

    session.invalidate();
    sessionMap.put("key", "value");

    HttpSession current = request.getSession(false);
    assertNotNull(current);
    assertNotSame(session, current);
    assertEquals("value", current.getAttribute("key"));
}

public void testAttributeSetDirectlyIsVisibleThroughEntrySet() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    request.setSession(session);
    SessionMap sessionMap = new SessionMap(request);
    assertTrue(sessionMap.entrySet().isEmpty());

    session.setAttribute("key", "value");

    assertEquals(1, sessionMap.entrySet().size());
}

public void testPinnedSessionNeverTouchesTheRequestAgain() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setSession(new MockHttpSession());
    SessionMap sessionMap = new SessionMap(request);
    request.close();

    sessionMap.put("key", "value");
    assertEquals("value", sessionMap.get("key"));
    assertEquals(1, sessionMap.entrySet().size());
    assertEquals("value", sessionMap.remove("key"));
}
```

- [ ] **Step 2: Run them and verify the first three fail**

Run: `./mvnw -q test -DskipAssembly -pl core -Dtest=SessionMapTest -Dsurefire.failIfNoSpecifiedTests=false; echo exit=$?`
Expected: `exit=1`; `testSessionInvalidatedElsewhereReadsAsNoSession` errors with `IllegalStateException`, `testPutAfterInvalidationElsewhereUsesANewSession` errors with `IllegalStateException`, `testAttributeSetDirectlyIsVisibleThroughEntrySet` fails on the cached empty set. `testPinnedSessionNeverTouchesTheRequestAgain` already passes (it guards Task 1/2 against re-resolving through the request).

- [ ] **Step 3: Implement detection and remove the cache**

Each session call is wrapped so an `IllegalStateException` un-pins (`session = null` only if it still holds the same reference) and returns the "no session" answer (`null` / `false` / `Collections.emptySet()` / no-op). `put` on an un-pinned map goes through the same `request.getSession(true)` path as a first `put`. `entrySet()` builds a fresh `HashSet` on every call; `setValue` writes to the session captured when the set was built. `entries` becomes `@Deprecated(since = "7.5.0", forRemoval = true)` and is never read or written.

- [ ] **Step 4: Run the tests and verify they pass**

Run: `./mvnw -q test -DskipAssembly -pl core -Dtest='SessionMapTest,AttributeMapTest,DispatcherTest' -Dsurefire.failIfNoSpecifiedTests=false; echo exit=$?`
Expected: `exit=0`.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/dispatcher/SessionMap.java core/src/test/java/org/apache/struts2/dispatcher/SessionMapTest.java
git commit -m "WW-3576 fix(core): treat an invalidated session as absent and stop caching SessionMap entries"
```

### Task 3: `SandboxedSessionMap`

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/SessionMap.java` (add `protected SessionMap()` for subclasses that never touch a request)
- Create: `core/src/main/java/org/apache/struts2/interceptor/exec/SandboxedSessionMap.java`
- Create: `core/src/main/java/org/apache/struts2/interceptor/exec/SessionOperation.java`
- Test: `core/src/test/java/org/apache/struts2/interceptor/exec/SandboxedSessionMapTest.java` (extends `junit.framework.TestCase`)

**Interfaces:**
- Produces:
  - `public class SandboxedSessionMap extends SessionMap` — `public SandboxedSessionMap(HttpSession session, String processName)` copies all attributes (shallow) and records `session.getId()`; overrides `get`, `put`, `remove`, `containsKey`, `entrySet`, `clear`, `invalidate`, all `synchronized`.
  - `public String getStartSessionId()`, `public String getProcessName()`
  - `public synchronized List<SessionOperation> getOperations()` — unmodifiable copy, in order.
  - `public synchronized Object getSnapshotValue(String key)` — the value at snapshot time, `null` if absent.
  - `public sealed interface SessionOperation` with records `Put(String key, Object value)`, `Remove(String key)`, `Invalidate()`.

- [ ] **Step 1: Write the failing tests**

```java
public void testReadsSeeSnapshotPlusOwnWritesAndNeverTheLiveSession() {
    MockHttpSession live = new MockHttpSession();
    live.setAttribute("a", "1");
    SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "p");

    live.setAttribute("a", "changed");
    sandbox.put("b", "2");

    assertEquals("1", sandbox.get("a"));
    assertEquals("2", sandbox.get("b"));
    assertNull(live.getAttribute("b"));
    assertEquals(live.getId(), sandbox.getStartSessionId());
    assertEquals(List.of(new SessionOperation.Put("b", "2")), sandbox.getOperations());
}

public void testRemoveClearAndInvalidateAreLoggedInOrder() {
    MockHttpSession live = new MockHttpSession();
    live.setAttribute("a", "1");
    SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "p");

    sandbox.remove("a");
    sandbox.invalidate();
    sandbox.put("user", "u");

    assertEquals(List.of(new SessionOperation.Remove("a"), new SessionOperation.Invalidate(),
        new SessionOperation.Put("user", "u")), sandbox.getOperations());
    assertEquals(Map.of("user", "u"), new HashMap<>(sandbox));
    assertEquals("1", sandbox.getSnapshotValue("a"));
}

public void testPutNullIsLoggedAsRemove() {
    SandboxedSessionMap sandbox = new SandboxedSessionMap(new MockHttpSession(), "p");
    sandbox.put("a", null);
    assertEquals(List.of(new SessionOperation.Remove("a")), sandbox.getOperations());
}

public void testEntrySetValueIsLogged() {
    MockHttpSession live = new MockHttpSession();
    live.setAttribute("a", "1");
    SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "p");

    sandbox.entrySet().iterator().next().setValue("2");

    assertEquals("2", sandbox.get("a"));
    assertEquals(List.of(new SessionOperation.Put("a", "2")), sandbox.getOperations());
}
```

Also: `clear()` logs one `Remove` per key present in the view at that moment.

- [ ] **Step 2: Run them and verify they fail to compile**

Run: `./mvnw -q test -DskipAssembly -pl core -Dtest=SandboxedSessionMapTest -Dsurefire.failIfNoSpecifiedTests=false; echo exit=$?`
Expected: `exit=1`, compilation error `cannot find symbol: class SandboxedSessionMap`.

- [ ] **Step 3: Implement `SessionOperation`, `SandboxedSessionMap` and `protected SessionMap()`**

View is a `LinkedHashMap` copied from the session; `invalidate()` clears the view and logs `Invalidate`. `SandboxedSessionMap` keeps its own fields and never touches `session`, `request` or the deprecated `entries`. It is not `Serializable`-relevant: the process holds it `transient` (Task 5).

- [ ] **Step 4: Run the tests and verify they pass**

Same command as Step 2. Expected: `exit=0`.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/dispatcher/SessionMap.java core/src/main/java/org/apache/struts2/interceptor/exec/SandboxedSessionMap.java core/src/main/java/org/apache/struts2/interceptor/exec/SessionOperation.java core/src/test/java/org/apache/struts2/interceptor/exec/SandboxedSessionMapTest.java
git commit -m "WW-3576 feat(core): add a sandboxed session map recording background writes"
```

### Task 4: Reconciliation and the per-action conflict hook

**Files:**
- Create: `core/src/main/java/org/apache/struts2/action/BackgroundSessionConflictAware.java`
- Create: `core/src/main/java/org/apache/struts2/action/SessionConflict.java`
- Create: `core/src/main/java/org/apache/struts2/action/SessionConflictResolution.java`
- Create: `core/src/main/java/org/apache/struts2/interceptor/exec/SessionSandboxReconciler.java`
- Test: `core/src/test/java/org/apache/struts2/interceptor/exec/SessionSandboxReconcilerTest.java` (extends `junit.framework.TestCase`)

**Interfaces:**
- Consumes: Task 3's `SandboxedSessionMap`, `SessionOperation`.
- Produces:
  - `public record SessionConflict(String key, Object snapshotValue, Object currentValue, Object backgroundValue)` — `null` means absent.
  - `public final class SessionConflictResolution` — `static keepCurrent()`, `static useBackground()`, `static replaceWith(Object value)`, `static remove()`.
  - `public interface BackgroundSessionConflictAware { SessionConflictResolution resolveSessionConflict(SessionConflict conflict); }`
  - `public final class SessionSandboxReconciler` — `public static void reconcile(SandboxedSessionMap sandbox, HttpServletRequest request, Object action)`.

- [ ] **Step 1: Write the failing tests**

Fixture: `MockHttpServletRequest` with a `MockHttpSession live`; sandbox built from `live`.

```java
public void testOnlyLoggedKeysAreApplied()            // live "other" changed after snapshot, untouched by background → kept; background put "b" → applied
public void testSessionIdChangeDiscardsEverything()    // live.invalidate(); request.getSession(true) new; background put "b" → new session has no "b"; one debug line names the process
public void testNoSessionDiscardsEverything()          // live.invalidate(), no new session → reconcile returns, request.getSession(false) still null
public void testConflictKeepsCurrentByDefault()        // snapshot a=1, live a=live, background a=bg → live a stays "live"
public void testHookUseBackground()                    // action returns useBackground() → a=bg
public void testHookReplaceWith()                      // replaceWith("merged") → a=merged
public void testHookRemove()                           // remove() → a absent
public void testHookSeesAllFourValues()                // assert SessionConflict("a", "1", "live", "bg")
public void testRemovalOfAKeyChangedElsewhereIsAConflict() // snapshot a=1, live a=live, background remove a → default keeps "live"
public void testBackgroundInvalidateStartsAFreshSession()  // background invalidate + put user=u → old live invalid, new session has only user=u, hook never called
public void testHookExceptionPropagates()              // hook throws IllegalArgumentException → reconcile throws it
```

Each name above is one test with exactly the assertion in its comment.

- [ ] **Step 2: Run them and verify they fail to compile**

Run: `./mvnw -q test -DskipAssembly -pl core -Dtest=SessionSandboxReconcilerTest -Dsurefire.failIfNoSpecifiedTests=false; echo exit=$?`
Expected: `exit=1`, `cannot find symbol: class SessionSandboxReconciler`.

- [ ] **Step 3: Implement the types and `reconcile`**

Algorithm (not determined by the signature):

```
real = request.getSession(false)
if real == null or !real.getId().equals(sandbox.getStartSessionId()): LOG.debug(process name); return
ops = sandbox.getOperations(); last = index of last Invalidate, or -1
if last >= 0:
    real.invalidate(); target = request.getSession(true)
    apply final value of every key touched after `last` to target (null → removeAttribute); no conflict checks
    return
for each key touched in ops (in first-touch order), final = sandbox.get(key):
    current = real.getAttribute(key); snapshot = sandbox.getSnapshotValue(key)
    if Objects.equals(current, snapshot): write final (null → removeAttribute)
    else if action instanceof BackgroundSessionConflictAware: apply its resolution
    else: LOG.debug(process name, key); keep current
```

- [ ] **Step 4: Run the tests and verify they pass**

Same command as Step 2. Expected: `exit=0`.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/action/BackgroundSessionConflictAware.java core/src/main/java/org/apache/struts2/action/SessionConflict.java core/src/main/java/org/apache/struts2/action/SessionConflictResolution.java core/src/main/java/org/apache/struts2/interceptor/exec/SessionSandboxReconciler.java core/src/test/java/org/apache/struts2/interceptor/exec/SessionSandboxReconcilerTest.java
git commit -m "WW-3576 feat(core): reconcile a session sandbox with per-action conflict resolution"
```

### Task 5: Wire the sandbox into `execAndWait`

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/interceptor/exec/BackgroundProcess.java`
- Modify: `core/src/main/java/org/apache/struts2/interceptor/exec/StrutsBackgroundProcess.java:64-92`
- Modify: `core/src/main/java/org/apache/struts2/interceptor/ExecuteAndWaitInterceptor.java:296-345`
- Test: `core/src/test/java/org/apache/struts2/interceptor/ExecuteAndWaitInterceptorTest.java` (extends `StrutsInternalTestCase`), plus a test action `core/src/test/java/org/apache/struts2/interceptor/SessionWritingWaitAction.java` implementing `SessionAware`, registered in `WaitConfigurationProvider` as `sessionAction`

**Interfaces:**
- Consumes: Tasks 3–4.
- Produces:
  - `BackgroundProcess`: `default BackgroundProcess withSessionSandbox(SandboxedSessionMap sandbox) { return this; }`, `default SandboxedSessionMap getSessionSandbox() { return null; }`
  - `StrutsBackgroundProcess` overrides both; field `private transient SandboxedSessionMap sessionSandbox`.

- [ ] **Step 1: Write the failing tests**

`SessionWritingWaitAction.execute()` writes `"result" → "done"` through `ActionContext.getContext().getSession()` and `"aware" → "yes"` through its injected `SessionAware` map; a field `failWith` makes it throw after writing.

```java
public void testBackgroundWritesReachTheRealSession()      // first proxy → "wait"; wait for done; second proxy → "success"; httpSession has result=done and aware=yes
public void testBackgroundWritesAreInvisibleUntilPolled()  // after done but before the second proxy: httpSession has neither "result" nor "aware"
public void testWritesOfAFailedActionAreApplied()          // failWith set → second proxy throws; httpSession has result=done
public void testSessionAwareGetsTheLiveMapBackAfterWriteBack() // after second proxy, the action's session map is not a SandboxedSessionMap
```

The existing setUp puts a plain `HashMap` as the context session; these tests replace it with `new SessionMap(request)` so the context session is backed by `httpSession`.

- [ ] **Step 2: Run them and verify they fail**

Run: `./mvnw -q test -DskipAssembly -pl core -Dtest=ExecuteAndWaitInterceptorTest -Dsurefire.failIfNoSpecifiedTests=false; echo exit=$?`
Expected: `exit=1`; `testBackgroundWritesAreInvisibleUntilPolled` and `testSessionAwareGetsTheLiveMapBackAfterWriteBack` fail (writes are live today).

- [ ] **Step 3: Implement the wiring**

- Interceptor, creation branch: `new SandboxedSessionMap(httpSession, name)` and `getNewBackgroundProcess(...).withSessionSandbox(sandbox).prepare()`.
- Interceptor, `isDone()` branch, after `session.remove(bp_SessionKey)` and **before** `throw bp.getException()`: if `bp.getSessionSandbox()` is non-null, `SessionSandboxReconciler.reconcile(sandbox, ServletActionContext.getRequest(), bp.getAction())`, then re-inject `context.getSession()` into a `SessionAware` action.
- `StrutsBackgroundProcess` thread body: `beforeInvocation(); installSessionSandbox(); result = invocation.invokeActionOnly();` — `installSessionSandbox()` is private: when a sandbox is set, bind `ActionContext.of(new HashMap<>(invocation.getInvocationContext().getContextMap())).withSession(sandbox)` and call `withSession(sandbox)` on a `SessionAware` action.

- [ ] **Step 4: Run the tests and verify they pass**

Same command as Step 2. Expected: `exit=0`, existing tests (`testFromDeserializedSession`, token tests) still pass.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/interceptor/exec/BackgroundProcess.java core/src/main/java/org/apache/struts2/interceptor/exec/StrutsBackgroundProcess.java core/src/main/java/org/apache/struts2/interceptor/ExecuteAndWaitInterceptor.java core/src/test/java/org/apache/struts2/interceptor/ExecuteAndWaitInterceptorTest.java core/src/test/java/org/apache/struts2/interceptor/SessionWritingWaitAction.java
git commit -m "WW-3576 feat(core): run execAndWait background processes against a session sandbox"
```

### Task 6: Warn on sandbox escapes and document them

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/interceptor/exec/SandboxedSessionMap.java`
- Modify: `core/src/main/java/org/apache/struts2/interceptor/exec/StrutsBackgroundProcess.java`
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/SessionMap.java`
- Modify: Javadoc of `ExecuteAndWaitInterceptor` and `BackgroundProcess`
- Test: `core/src/test/java/org/apache/struts2/interceptor/exec/SandboxedSessionMapTest.java`

**Interfaces:**
- Produces: `public static SandboxedSessionMap current()` (thread-local backed, read by `SessionMap`); `public static void runInSandbox(SandboxedSessionMap sandbox, Runnable work)` sets `current()` for the duration of `work` and clears it in `finally`; `void warnEscapeOnce()`. `StrutsBackgroundProcess`'s thread body runs `invokeActionOnly()` inside `runInSandbox` when a sandbox is set.

- [ ] **Step 1: Write the failing test**

```java
public void testWriteThroughRequestMapFromBackgroundThreadWarnsOnce() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpSession live = new MockHttpSession();
    request.setSession(live);
    SessionMap requestMap = new SessionMap(request);
    SandboxedSessionMap sandbox = new SandboxedSessionMap(live, "longJob");

    try (LogCapture logs = new LogCapture(SandboxedSessionMap.class)) {
        Thread background = new Thread(() -> SandboxedSessionMap.runInSandbox(sandbox, () -> {
            requestMap.put("a", "1");
            requestMap.put("b", "2");
            sandbox.put("c", "3");
        }));
        background.start();
        background.join(5000);

        assertEquals("1", live.getAttribute("a"));
        assertEquals(1, logs.messages().size());
        assertTrue(logs.messages().get(0).contains("longJob"));
        assertFalse(logs.messages().get(0).contains("1"));
    }
}
```

`runInSandbox(SandboxedSessionMap, Runnable)` is the package-level helper `StrutsBackgroundProcess` uses to set and clear `current()`.

- [ ] **Step 2: Run it and verify it fails to compile**

Run: `./mvnw -q test -DskipAssembly -pl core -Dtest=SandboxedSessionMapTest -Dsurefire.failIfNoSpecifiedTests=false; echo exit=$?`
Expected: `exit=1`, `cannot find symbol: method runInSandbox`.

- [ ] **Step 3: Implement the escape warning and Javadoc**

`SessionMap.put/remove/clear/invalidate`: when `SandboxedSessionMap.current()` is non-null and `this` is not a `SandboxedSessionMap`, call `warnEscapeOnce()`; the write still goes live. WARN (once per sandbox, `AtomicBoolean`): `Background process [{}] wrote to the request session, bypassing its session sandbox; use ActionContext.getContext().getSession() or SessionAware`. Javadoc on `ExecuteAndWaitInterceptor` and `BackgroundProcess`: the sandbox model, that `#session` and `invocation.getInvocationContext().getSession()` bypass it, and `BackgroundSessionConflictAware`.

- [ ] **Step 4: Run the full core suite**

Run: `./mvnw -q test -DskipAssembly -pl core > /private/tmp/claude-501/-Users-lukaszlenart-Projects-Apache-struts/9c5c77a5-710a-4c52-9f6b-ec54c8d4b6aa/scratchpad/core-tests.log 2>&1; echo exit=$?`
Expected: `exit=0`.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/interceptor/exec/SandboxedSessionMap.java core/src/main/java/org/apache/struts2/interceptor/exec/StrutsBackgroundProcess.java core/src/main/java/org/apache/struts2/dispatcher/SessionMap.java core/src/main/java/org/apache/struts2/interceptor/ExecuteAndWaitInterceptor.java core/src/main/java/org/apache/struts2/interceptor/exec/BackgroundProcess.java core/src/test/java/org/apache/struts2/interceptor/exec/SandboxedSessionMapTest.java
git commit -m "WW-3576 feat(core): warn when a background process writes around its session sandbox"
```

### Task 7: Reactor build and hand-off items

- [ ] **Step 1: Full reactor test-compile** — `./mvnw -q test-compile -DskipAssembly > …/scratchpad/reactor.log 2>&1; echo exit=$?`, expected `exit=0` (the async plugin and other modules still compile against `SessionMap`).
- [ ] **Step 2: Ask Łukasz** before filing the 8.0.0 Task to remove the deprecated `SessionMap.entries` field.
- [ ] **Step 3: Record owed docs in memory** — 7.5.0 migration notes (subclass-visible `SessionMap` changes, sandbox model, `#session` escape, custom `BackgroundProcess` keeps shared map) and the struts-site `execAndWait` page.
