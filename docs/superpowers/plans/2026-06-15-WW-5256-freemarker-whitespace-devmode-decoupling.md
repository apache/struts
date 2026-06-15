# FreeMarker Whitespace Stripping / devMode Decoupling Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make FreeMarker whitespace stripping governed solely by `struts.freemarker.whitespaceStripping` (default `true`), removing the `devMode` auto-disable that broke `s:textarea` rendering and bloated HTML output in development.

**Architecture:** Remove the `&& !devMode` term in `FreemarkerManager.buildConfiguration()` and delete the now-unused `devMode` field/setter/injection. Update the Javadoc and the unit tests accordingly. `TextareaTest` serves as the rendering-level regression guard.

**Tech Stack:** Java, FreeMarker 2.3.34, JUnit 3-style tests (`testXxx` methods extending `StrutsInternalTestCase`), Maven.

**Spec:** `docs/superpowers/specs/2026-06-15-WW-5256-freemarker-whitespace-devmode-decoupling-design.md`

**Ticket:** [WW-5256](https://issues.apache.org/jira/browse/WW-5256)

---

### Task 1: Prove the bug with a failing test (RED)

This task documents the current broken behavior: with `devMode=true` and whitespace stripping
configured `true`, the manager wrongly reports stripping as disabled. This test is temporary —
it uses the `setDevMode` API that Task 2 removes, so Task 2 deletes it.

**Files:**
- Test: `core/src/test/java/org/apache/struts2/views/freemarker/FreemarkerManagerTest.java`

- [ ] **Step 1: Add the temporary failing test**

Insert this method after `testWhitespaceStrippingEnabledWhenNotInDevMode` (currently ends at line 169, before the closing `}` of the class):

```java
    // TEMP (WW-5256): documents the pre-fix bug; removed in the same change that removes the devMode coupling.
    public void testWhitespaceStrippingNotDisabledInDevMode() throws Exception {
        // given
        FreemarkerManager manager = new FreemarkerManager();
        container.inject(manager);
        manager.setWhitespaceStripping("true");
        manager.setDevMode("true");

        // when
        manager.init(servletContext);

        // then
        assertTrue(manager.config.getWhitespaceStripping());
    }
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=FreemarkerManagerTest#testWhitespaceStrippingNotDisabledInDevMode`
Expected: FAIL — assertion error, `getWhitespaceStripping()` returns `false` because the current code computes `whitespaceStripping && !devMode`.

- [ ] **Step 3: Commit the failing test**

```bash
git add core/src/test/java/org/apache/struts2/views/freemarker/FreemarkerManagerTest.java
git commit -m "WW-5256 test: prove whitespace stripping wrongly disabled in devMode"
```

---

### Task 2: Decouple stripping from devMode (GREEN)

Apply the production fix and align the test suite. Because the `setDevMode` setter is removed,
the temporary test from Task 1 and the two original devMode-coupling tests are deleted, and the
config test drops its `setDevMode` call.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/views/freemarker/FreemarkerManager.java` (field at line 180; setter at 216-219; coupling at 355-357)
- Modify: `core/src/test/java/org/apache/struts2/views/freemarker/FreemarkerManagerTest.java` (lines 129-169)

- [ ] **Step 1: Replace the coupling in `FreemarkerManager`**

Find (lines 355-357):

```java
        boolean enableWhitespaceStripping = whitespaceStripping && !devMode;
        LOG.debug("Whitespace stripping: {} (configured: {}, devMode: {})", enableWhitespaceStripping, whitespaceStripping, devMode);
        configuration.setWhitespaceStripping(enableWhitespaceStripping);
```

Replace with:

```java
        LOG.debug("Whitespace stripping: {}", whitespaceStripping);
        configuration.setWhitespaceStripping(whitespaceStripping);
```

- [ ] **Step 2: Remove the unused `devMode` field**

Find (line 180):

```java
    protected boolean devMode;
```

Delete this line.

- [ ] **Step 3: Remove the unused `setDevMode` setter and its injection**

Find (lines 216-219):

```java

    @Inject(value = StrutsConstants.STRUTS_DEVMODE, required = false)
    public void setDevMode(String devMode) {
        this.devMode = BooleanUtils.toBoolean(devMode);
    }
```

Delete this block (the setter plus the blank line preceding it).

- [ ] **Step 4: Delete the temporary test from Task 1 and the two devMode-coupling tests**

In `FreemarkerManagerTest.java`, delete these three methods entirely:
- `testWhitespaceStrippingNotDisabledInDevMode` (added in Task 1)
- `testWhitespaceStrippingDisabledInDevMode` (lines 143-155)
- `testWhitespaceStrippingEnabledWhenNotInDevMode` (lines 157-169)

- [ ] **Step 5: Drop the `setDevMode` call from the config test**

Find (lines 129-141):

```java
    public void testWhitespaceStrippingDisabledViaConfiguration() throws Exception {
        // given
        FreemarkerManager manager = new FreemarkerManager();
        container.inject(manager);
        manager.setWhitespaceStripping("false");
        manager.setDevMode("false");

        // when
        manager.init(servletContext);

        // then
        assertFalse(manager.config.getWhitespaceStripping());
    }
```

Replace with (remove the `manager.setDevMode("false");` line):

```java
    public void testWhitespaceStrippingDisabledViaConfiguration() throws Exception {
        // given
        FreemarkerManager manager = new FreemarkerManager();
        container.inject(manager);
        manager.setWhitespaceStripping("false");

        // when
        manager.init(servletContext);

        // then
        assertFalse(manager.config.getWhitespaceStripping());
    }
```

- [ ] **Step 6: Verify no remaining references to the removed API**

Run: `grep -rn "setDevMode\|\.devMode" core/src/main/java/org/apache/struts2/views/freemarker/ core/src/test/java/org/apache/struts2/views/freemarker/`
Expected: no matches.

- [ ] **Step 7: Run the FreemarkerManager tests**

Run: `mvn test -DskipAssembly -pl core -Dtest=FreemarkerManagerTest`
Expected: PASS — `testWhitespaceStrippingEnabledByDefault` and `testWhitespaceStrippingDisabledViaConfiguration` both green; class compiles with no reference to `devMode`/`setDevMode`.

- [ ] **Step 8: Commit**

```bash
git add core/src/main/java/org/apache/struts2/views/freemarker/FreemarkerManager.java core/src/test/java/org/apache/struts2/views/freemarker/FreemarkerManagerTest.java
git commit -m "WW-5256 fix(freemarker): honor whitespaceStripping regardless of devMode"
```

---

### Task 3: Update the configuration Javadoc

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/StrutsConstants.java` (lines 335-342)

- [ ] **Step 1: Remove the stale devMode sentence**

Find (lines 335-342):

```java
    /**
     * Controls FreeMarker whitespace stripping during template compilation.
     * When enabled (default), removes indentation and trailing whitespace from lines containing only FTL tags.
     * Automatically disabled when devMode is enabled.
     *
     * @since 7.2.0
     */
    public static final String STRUTS_FREEMARKER_WHITESPACE_STRIPPING = "struts.freemarker.whitespaceStripping";
```

Replace with:

```java
    /**
     * Controls FreeMarker whitespace stripping during template compilation.
     * When enabled (default), removes indentation and trailing whitespace from lines containing only FTL tags.
     *
     * @since 7.2.0
     */
    public static final String STRUTS_FREEMARKER_WHITESPACE_STRIPPING = "struts.freemarker.whitespaceStripping";
```

- [ ] **Step 2: Commit**

```bash
git add core/src/main/java/org/apache/struts2/StrutsConstants.java
git commit -m "WW-5256 docs: drop devMode note from whitespaceStripping constant"
```

---

### Task 4: Confirm rendering regression guard and full module build

`TextareaTest` renders the `s:textarea` tag against expected output fixtures. With stripping now
unconditionally on by default, it must stay green — this is the user-visible guarantee that the
textarea no longer emits blank lines.

**Files:**
- Verify only: `core/src/test/java/org/apache/struts2/views/jsp/ui/TextareaTest.java`

- [ ] **Step 1: Run the textarea rendering tests**

Run: `mvn test -DskipAssembly -pl core -Dtest=TextareaTest`
Expected: PASS — rendered output matches `Textarea-1.txt` / `Textarea-2.txt` fixtures (no internal blank lines).

- [ ] **Step 2: Run the full core test module**

Run: `mvn test -DskipAssembly -pl core`
Expected: BUILD SUCCESS — no compilation errors from the removed `devMode` API, all tests pass.

- [ ] **Step 3: No commit needed**

This task is verification only; no source changes.

---

## Self-Review

**Spec coverage:**
- Decouple stripping from devMode → Task 2 Step 1. ✓
- Remove `devMode` field/setter/`@Inject` → Task 2 Steps 2-3. ✓
- `default.properties` unchanged → no task needed (intentional). ✓
- Javadoc cleanup → Task 3. ✓
- Test updates (remove 2 devMode tests, drop stray `setDevMode` call, keep default/explicit tests) → Task 2 Steps 4-5. ✓
- Verification (`FreemarkerManagerTest`, textarea rendering, no internal blank lines) → Task 2 Step 7, Task 4. ✓

**Placeholder scan:** No TBD/TODO; every code step shows exact before/after content. The one `TEMP` marker is an intentional, scoped throwaway test removed within the same plan (Task 2 Step 4). ✓

**Type/signature consistency:** `whitespaceStripping` (existing `protected boolean`, default `true`) and `config.getWhitespaceStripping()` used consistently across tasks; `setDevMode`/`devMode` only referenced in the temporary Task 1 test and the deletions in Task 2. ✓
