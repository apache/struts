# WW-3530 Visitor-Validator Cache-Key Fix Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Stop `AnnotationActionValidatorManager` from collapsing distinct visitor-validator `context` values into one cache key under wildcard actions, so multiple visitor validators on the same field run again.

**Architecture:** Narrow the wildcard config-name substitution in `buildValidatorKey` so it fires only when the class being validated is the action's own class. Visited objects (whose `context` is a stable, explicit visitor param) always key on `context`, exactly like `DefaultActionValidatorManager`.

**Tech Stack:** Java, Maven, JUnit 3-style `XWorkTestCase` (extends `junit.framework.TestCase`), EasyMock.

## Global Constraints

- Target version: **7.3.0** (SNAPSHOT is a placeholder; real version chosen at release).
- Commit messages MUST be prefixed with the Jira ticket: `WW-3530 <type>: ...`.
- Only `core` module is touched. Build/test with `mvn test -DskipAssembly -pl core`.
- `core` tests are JUnit 3/4 — they extend `XWorkTestCase` and use `testXxx` method names with `junit.framework` assertions. An `@Test` annotation would silently never run. Do NOT convert to JUnit 5.
- `ActionConfig.WILDCARD` == `"*"`. Named-pattern detection also matches names containing both `{` and `}`.
- Change is behavior-preserving for: non-wildcard actions, and wildcard actions validating their own class (WW-2996 caching must stay intact).

---

## Task 1: Add failing tests for the cache-key logic

**Files:**
- Test: `core/src/test/java/org/apache/struts2/validator/AnnotationActionValidatorManagerTest.java`

**Interfaces:**
- Consumes: `AnnotationActionValidatorManager.buildValidatorKey(Class clazz, String context)` (protected, same package as the test) — returns the cache key `String`.
- Consumes: existing test classes already imported in the file — `SimpleAnnotationAction` (used as the action class) and `AnnotatedTestBean` (used as a visited/foreign class).
- Produces: a private helper `installInvocation(String configName, Object action)` used by the new tests.

- [ ] **Step 1: Add the mock-installer helper**

Add this private method to `AnnotationActionValidatorManagerTest` (place it just after `tearDown`, before `testBuildValidatorKey`). It installs an `ActionInvocation` whose proxy/config drive `buildValidatorKey`, overriding the one from `setUp`:

```java
private void installInvocation(String configName, Object action) {
    ActionConfig config = new ActionConfig.Builder("packageName", configName, "").build();
    ActionInvocation invocation = EasyMock.createNiceMock(ActionInvocation.class);
    ActionProxy proxy = EasyMock.createNiceMock(ActionProxy.class);

    EasyMock.expect(invocation.getProxy()).andReturn(proxy).anyTimes();
    EasyMock.expect(invocation.getAction()).andReturn(action).anyTimes();
    EasyMock.expect(proxy.getMethod()).andReturn("execute").anyTimes();
    EasyMock.expect(proxy.getConfig()).andReturn(config).anyTimes();

    EasyMock.replay(invocation, proxy);
    ActionContext.getContext().withActionInvocation(invocation);
}
```

- [ ] **Step 2: Add the WW-3530 reproduction test (visited class under a wildcard action)**

Add this test method to the class. Under a wildcard action, a *visited* object's key must include its stable context so `basic` and `additional` don't collide:

```java
public void testBuildValidatorKeyKeepsContextForVisitedClassUnderWildcardAction() {
    // Action is wildcard-mapped; the validated class is a visited model object
    // (not the action class), so its explicit, stable context must be preserved.
    installInvocation("*", new SimpleAnnotationAction());

    String basicKey = annotationActionValidatorManager.buildValidatorKey(AnnotatedTestBean.class, "basic");
    String additionalKey = annotationActionValidatorManager.buildValidatorKey(AnnotatedTestBean.class, "additional");

    assertEquals(AnnotatedTestBean.class.getName() + "/packageName/basic", basicKey);
    assertEquals(AnnotatedTestBean.class.getName() + "/packageName/additional", additionalKey);
    assertFalse("visited-object keys must differ per context", basicKey.equals(additionalKey));
}
```

- [ ] **Step 3: Add the WW-2996 regression test (action's own class under a wildcard action)**

Add this test method. When the validated class IS the action class under a wildcard, the key must ignore context and use the stable config name — preserving WW-2996 behavior:

```java
public void testBuildValidatorKeyIgnoresContextForActionClassUnderWildcardAction() {
    installInvocation("*", new SimpleAnnotationAction());

    String key1 = annotationActionValidatorManager.buildValidatorKey(SimpleAnnotationAction.class, "foo");
    String key2 = annotationActionValidatorManager.buildValidatorKey(SimpleAnnotationAction.class, "bar");

    // WW-2996: a wildcard action's own validators share one cache entry across
    // all resolved names, so the volatile context must NOT be part of the key.
    assertEquals(key1, key2);
    assertEquals(SimpleAnnotationAction.class.getName() + "/packageName/*|execute", key1);
}
```

- [ ] **Step 4: Run the new tests to verify they FAIL**

Run:
```bash
mvn test -DskipAssembly -pl core -Dtest='AnnotationActionValidatorManagerTest#testBuildValidatorKeyKeepsContextForVisitedClassUnderWildcardAction+testBuildValidatorKeyIgnoresContextForActionClassUnderWildcardAction'
```
Expected: `testBuildValidatorKeyKeepsContextForVisitedClassUnderWildcardAction` FAILS — before the fix the visited-class key is `.../packageName/*|execute` for both contexts, so `basicKey` equals `additionalKey` and the `assertEquals(".../basic", ...)` assertion fails. `testBuildValidatorKeyIgnoresContextForActionClassUnderWildcardAction` PASSES already (documents the behavior to preserve).

- [ ] **Step 5: Commit the tests**

```bash
git add core/src/test/java/org/apache/struts2/validator/AnnotationActionValidatorManagerTest.java
git commit -m "WW-3530 test(core): cover visitor-validator cache-key context handling under wildcard actions

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 2: Narrow the wildcard substitution to the action class

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/validator/AnnotationActionValidatorManager.java:40-65`
- Test: `core/src/test/java/org/apache/struts2/validator/AnnotationActionValidatorManagerTest.java` (from Task 1)

**Interfaces:**
- Produces: unchanged signature `protected String buildValidatorKey(Class clazz, String context)`. New behavior: config-name substitution applies only when `clazz.equals(invocation.getAction().getClass())`.

- [ ] **Step 1: Replace the `buildValidatorKey` method body**

Replace the whole method (currently lines 40-65) with:

```java
    @Override
    protected String buildValidatorKey(Class clazz, String context) {
        ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
        ActionProxy proxy = invocation.getProxy();
        ActionConfig config = proxy.getConfig();

        StringBuilder sb = new StringBuilder(clazz.getName());
        sb.append("/");
        if (StringUtils.isNotBlank(config.getPackageName())) {
            sb.append(config.getPackageName());
            sb.append("/");
        }

        Object action = invocation.getAction();
        boolean validatingActionClass = action != null && clazz.equals(action.getClass());
        String configName = config.getName();
        boolean wildcard = configName.contains(ActionConfig.WILDCARD)
                || (configName.contains("{") && configName.contains("}"));

        // WW-2996: key needs to use the name of the action from the config file, instead of the url,
        // so wildcard actions will have the same validator
        // WW-3753: Using the config name instead of the context only for wildcard actions to keep the flexibility
        // provided by the original design (such as mapping different contexts to the same action and method if desired)
        // WW-4536: Using NamedVariablePatternMatcher allows defines actions with patterns enclosed with '{}'
        // WW-3530: the config-name substitution only makes sense for the action's own class; a visited object
        // (visitor validator) carries a stable, explicit context that must remain part of the key
        if (validatingActionClass && wildcard) {
            sb.append(configName);
            sb.append("|");
            sb.append(proxy.getMethod());
        } else {
            sb.append(context);
        }
        return sb.toString();
    }
```

- [ ] **Step 2: Run the new tests to verify they PASS**

Run:
```bash
mvn test -DskipAssembly -pl core -Dtest='AnnotationActionValidatorManagerTest#testBuildValidatorKeyKeepsContextForVisitedClassUnderWildcardAction+testBuildValidatorKeyIgnoresContextForActionClassUnderWildcardAction'
```
Expected: BUILD SUCCESS, both tests PASS.

- [ ] **Step 3: Run the full validator test class for regressions**

Run:
```bash
mvn test -DskipAssembly -pl core -Dtest='AnnotationActionValidatorManagerTest'
```
Expected: BUILD SUCCESS. In particular `testBuildValidatorKey` still passes (non-wildcard config `name`, null action → context branch → `<class>/packageName/name`).

- [ ] **Step 4: Run the visitor-validator regression suites**

Run:
```bash
mvn test -DskipAssembly -pl core -Dtest='VisitorFieldValidatorTest+VisitorFieldValidatorModelTest+DefaultActionValidatorManagerTest'
```
Expected: BUILD SUCCESS. Confirms non-wildcard visitor behavior and the `no-annotations` manager are unaffected.

- [ ] **Step 5: Commit the fix**

```bash
git add core/src/main/java/org/apache/struts2/validator/AnnotationActionValidatorManager.java
git commit -m "WW-3530 fix(core): keep visitor-validator context in cache key under wildcard actions

Apply the wildcard config-name substitution only when validating the action's
own class. Visited objects carry a stable, explicit visitor context that must
remain part of the cache key, otherwise two visitor validators on one field with
different contexts collide and the second is silently dropped.

Fixes https://issues.apache.org/jira/browse/WW-3530

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 3: Full core test run

**Files:** none (verification only).

- [ ] **Step 1: Run the full core module test suite**

Run:
```bash
mvn test -DskipAssembly -pl core
```
Expected: BUILD SUCCESS with no new failures. This guards against any other caller of `buildValidatorKey` / `getValidators` relying on the old wildcard behavior for foreign classes (e.g. the `Form` component's client-side validation path).

- [ ] **Step 2: If green, no commit needed**

Verification-only task; nothing to commit if the suite passes.

---

## Self-Review Notes

- **Test scope note:** Tasks 1-2 verify the fix at the `buildValidatorKey` unit level (distinct cache keys per context), a deterministic proxy for spec test #1. End-to-end "both visitor validators actually execute" is covered by the existing `VisitorFieldValidatorTest` / `VisitorFieldValidatorModelTest` suites (run as regression in Task 2 Step 4), not by a new integration test here.
- **Spec coverage:** Root-cause narrowing → Task 2. Reproduction test (distinct cache entries per context for visited objects, spec test #1) → Task 1 Step 2. WW-2996 regression guard (spec test #2) → Task 1 Step 3. Non-wildcard visitor + `DefaultActionValidatorManager` unchanged (spec test #3) → Task 2 Step 4. Null-action guard (spec edge case) → `action != null` in Task 2 Step 1. Self-visiting wildcard limitation is intentionally not exercised (accepted known limitation per spec).
- **Placeholder scan:** none.
- **Type consistency:** `installInvocation(String, Object)`, `buildValidatorKey(Class, String)`, and `configName`/`wildcard`/`validatingActionClass` locals are consistent across tasks.
