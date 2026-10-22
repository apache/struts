# WW-3576 SessionMap Staleness — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Stop `SessionMap` from going stale (NPE race, `IllegalStateException` after invalidation).

**Architecture:** The request `SessionMap` pins its session, reads it once per call into a local, treats `IllegalStateException` as "invalidated → no session", and drops the `entries` cache and the interned-id lock.

**Tech Stack:** Java 17, Jakarta Servlet 6, JUnit 3 (`junit.framework.TestCase` / `StrutsInternalTestCase`), Spring `MockHttpServletRequest`/`MockHttpSession`, AssertJ, Log4j2 via `org.apache.struts2.test.LogCapture`.

**Spec:** `docs/superpowers/specs/2026-10-18-WW-3576-session-map-staleness-design.md`

## Global Constraints

- Branch `feature/WW-3576-session-map-staleness`; every commit message starts `WW-3576 `; stage explicit paths only (never `git add -A`), never `git stash`.
- Tests are JUnit 3 in every file touched here: `testXxx()` names, no `@Test`.
- Build with `./mvnw` (no `mvn` on PATH); check Maven's exit code, not a grep of its output.
- Deprecations are `@Deprecated(since = "7.5.0", forRemoval = true)`; no reference in source to a ticket that is not filed.
- No null guards on required beans; no parameter reassignment; no boolean flag arguments; default to no comments.
- Never log session attribute values.

## Review Focus

- **Map used after its request ended** (async-plugin `Callable`, stored `SessionAware` field): with a session pinned, no call may touch the request — Task 2 `testPinnedSessionNeverTouchesTheRequestAgain` (closed `MockHttpServletRequest`).

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

The `execAndWait` session sandbox (former Tasks 3–6) was split out to [WW-5787](https://issues.apache.org/jira/browse/WW-5787) (8.0.0) after review.

### Task 7: Reactor build and hand-off items

- [ ] **Step 1: Full reactor test-compile** — `./mvnw -q test-compile -DskipAssembly > …/scratchpad/reactor.log 2>&1; echo exit=$?`, expected `exit=0` (the async plugin and other modules still compile against `SessionMap`).
- [ ] **Step 2: Ask Łukasz** before filing the 8.0.0 Task to remove the deprecated `SessionMap.entries` field.
- [ ] **Step 3: Record owed docs in memory** — 7.5.0 migration notes (subclass-visible `SessionMap` changes) and the struts-site `execAndWait` page.
