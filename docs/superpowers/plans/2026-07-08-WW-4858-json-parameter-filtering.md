# WW-4858 JSON Parameter Filtering Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make `JSONInterceptor`'s stack-population path enforce the same name/value acceptability controls as `ParametersInterceptor`, while keeping pure-reflection population (no OGNL).

**Architecture:** Generalize the existing single tree-walk `filterUnauthorizedKeysRecursive()` into `filterUnacceptableKeysRecursive()`. Each new check hooks in at the same per-key visit point, using dotted paths (`address.city`, `items[0].name`). The two name-pattern checkers are injected as the same global singletons `ParametersInterceptor` uses; other controls are interceptor-local settings. `ParametersInterceptor` is not modified.

**Tech Stack:** Java, Maven multi-module (`plugins/json` depends on `core`), JSON plugin. Tests are JUnit 4 style via `StrutsTestCase` (methods named `testXxx`, `setUp()` override, `assertEquals`/`assertNull`/`assertNotNull`).

## Global Constraints

- Commit messages MUST be prefixed with `WW-4858` and follow `WW-4858 <type>(json): <desc>`.
- Every commit ends with the footer line: `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`.
- Work happens on branch `WW-4858-json-parameter-filtering` (already created). Never commit to `main`.
- Test class is JUnit 4 style (`extends StrutsTestCase`); do NOT add JUnit 5 `@Test` annotations. Name tests `testXxx`.
- Run a single test with: `mvn test -pl plugins/json -am -DskipAssembly -Dtest=JSONInterceptorTest#<method>`
- Pure-reflection population must remain unchanged for accepted input; no OGNL evaluation may be introduced.
- The two pattern checkers (`excludedPatterns`, `acceptedPatterns`) are null-guarded in production code: a `null` checker means "skip this check". Production wires them via `@Inject`; the guard exists so existing tests that build a bare `new JSONInterceptor()` still pass.

---

## File Structure

- Modify: `plugins/json/src/main/java/org/apache/struts2/json/JSONInterceptor.java` — inject checkers, add settings, generalize the recursive filter to apply all name/value checks.
- Modify: `plugins/json/src/test/java/org/apache/struts2/json/JSONInterceptorTest.java` — wire default checkers into `createInterceptor()`, update one reflective test to the renamed method, add one test per gap.
- Create: `plugins/json/src/test/java/org/apache/struts2/json/ParameterAwareTestAction.java` — test fixture implementing `ParameterNameAware` + `ParameterValueAware`.

---

## Task 1: Inject checkers + generalize filter to enforce excluded/accepted name patterns

**Files:**
- Modify: `plugins/json/src/main/java/org/apache/struts2/json/JSONInterceptor.java`
- Modify: `plugins/json/src/test/java/org/apache/struts2/json/JSONInterceptorTest.java`

**Interfaces:**
- Consumes: `ExcludedPatternsChecker.isExcluded(String).isExcluded()`, `AcceptedPatternsChecker.isAccepted(String).isAccepted()`, existing `ParameterAuthorizer.isAuthorized(String, Object, Object)`.
- Produces: renamed methods `filterUnacceptableKeys(Map, Object, Object)`, `filterUnacceptableKeysRecursive(Map, String, Object, Object)`, `filterUnacceptableList(List, String, Object, Object)`; new method `boolean isAcceptableKey(String fullPath, Object target, Object action)`; injected fields `excludedPatterns`, `acceptedPatterns`.

- [ ] **Step 1: Write the failing tests**

Add to `JSONInterceptorTest.java` (before the final `setUp()` override):

```java
    public void testExcludedNamePatternRejectsKey() throws Exception {
        this.request.setContent("{\"foo\":\"a\", \"bar\":\"b\"}".getBytes());
        this.request.addHeader("Content-Type", "application/json");

        JSONInterceptor interceptor = createInterceptor();
        org.apache.struts2.security.DefaultExcludedPatternsChecker excluded =
                new org.apache.struts2.security.DefaultExcludedPatternsChecker();
        excluded.setExcludedPatterns("bar");
        interceptor.setExcludedPatterns(excluded);
        TestAction action = new TestAction();

        this.invocation.setAction(action);
        this.invocation.getStack().push(action);

        interceptor.intercept(this.invocation);

        assertEquals("a", action.getFoo());
        assertNull(action.getBar());
    }

    public void testAcceptedNamePatternRejectsKey() throws Exception {
        this.request.setContent("{\"foo\":\"a\", \"bar\":\"b\"}".getBytes());
        this.request.addHeader("Content-Type", "application/json");

        JSONInterceptor interceptor = createInterceptor();
        org.apache.struts2.security.DefaultAcceptedPatternsChecker accepted =
                new org.apache.struts2.security.DefaultAcceptedPatternsChecker();
        accepted.setAcceptedPatterns("foo");
        interceptor.setAcceptedPatterns(accepted);
        TestAction action = new TestAction();

        this.invocation.setAction(action);
        this.invocation.getStack().push(action);

        interceptor.intercept(this.invocation);

        assertEquals("a", action.getFoo());
        assertNull(action.getBar());
    }
```

Update `createInterceptor()` to wire default checkers so the happy path exercises real checkers. Replace the existing method body with:

```java
    private JSONInterceptor createInterceptor() {
        JSONInterceptor interceptor = new JSONInterceptor();
        JSONUtil jsonUtil = new JSONUtil();
        jsonUtil.setReader(new StrutsJSONReader());
        jsonUtil.setWriter(new StrutsJSONWriter());
        interceptor.setJsonUtil(jsonUtil);
        // Default: allow all parameters (simulates requireAnnotations=false)
        interceptor.setParameterAuthorizer((parameterName, target, action) -> true);
        interceptor.setExcludedPatterns(new org.apache.struts2.security.DefaultExcludedPatternsChecker());
        interceptor.setAcceptedPatterns(new org.apache.struts2.security.DefaultAcceptedPatternsChecker());
        return interceptor;
    }
```

Update the reflective lookup in `testNonStringKeysAreSkippedByAuthorizationFilter` from the old method name to the new one:

```java
        java.lang.reflect.Method method = JSONInterceptor.class.getDeclaredMethod(
                "filterUnacceptableKeys", java.util.Map.class, Object.class, Object.class);
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -pl plugins/json -am -DskipAssembly -Dtest=JSONInterceptorTest#testExcludedNamePatternRejectsKey+testAcceptedNamePatternRejectsKey`
Expected: FAIL — `setExcludedPatterns`/`setAcceptedPatterns` do not exist on `JSONInterceptor` (compile error).

- [ ] **Step 3: Implement in `JSONInterceptor.java`**

Add imports after the existing `org.apache.struts2.interceptor.parameter.ParameterAuthorizer` import:

```java
import org.apache.struts2.security.AcceptedPatternsChecker;
import org.apache.struts2.security.ExcludedPatternsChecker;
```

Add fields after the existing `private ParameterAuthorizer parameterAuthorizer;` field:

```java
    private ExcludedPatternsChecker excludedPatterns;
    private AcceptedPatternsChecker acceptedPatterns;
```

Add injected setters next to `setParameterAuthorizer`:

```java
    @Inject
    public void setExcludedPatterns(ExcludedPatternsChecker excludedPatterns) {
        this.excludedPatterns = excludedPatterns;
    }

    @Inject
    public void setAcceptedPatterns(AcceptedPatternsChecker acceptedPatterns) {
        this.acceptedPatterns = acceptedPatterns;
    }
```

Rename the call site at the `filterUnauthorizedKeys(json, rootObject, invocation.getAction());` line to:

```java
                filterUnacceptableKeys(json, rootObject, invocation.getAction());
```

Replace the three methods `filterUnauthorizedKeys`, `filterUnauthorizedKeysRecursive`, `filterUnauthorizedList` with the generalized versions below (the leaf-value branch is a no-op placeholder here and is filled in by Task 3/4):

```java
    @SuppressWarnings("rawtypes")
    private void filterUnacceptableKeys(Map json, Object target, Object action) {
        filterUnacceptableKeysRecursive(json, "", target, action);
    }

    @SuppressWarnings("rawtypes")
    private void filterUnacceptableKeysRecursive(Map json, String prefix, Object target, Object action) {
        Iterator<Map.Entry> it = json.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            if (!(entry.getKey() instanceof String key)) {
                // Defensive: a custom JSONReader could produce non-String keys. Skip — we cannot
                // construct a parameter path for filtering, and JSONPopulator wouldn't bind these anyway.
                LOG.debug("Skipping JSON entry with non-String key [{}] of type [{}] under prefix [{}]",
                        entry.getKey(), entry.getKey() == null ? "null" : entry.getKey().getClass().getName(), prefix);
                continue;
            }
            String fullPath = prefix.isEmpty() ? key : prefix + "." + key;

            if (!isAcceptableKey(fullPath, target, action)) {
                it.remove();
                continue;
            }

            Object value = entry.getValue();
            if (value instanceof Map) {
                filterUnacceptableKeysRecursive((Map) value, fullPath, target, action);
            } else if (value instanceof java.util.List) {
                filterUnacceptableList((java.util.List) value, fullPath, target, action);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void filterUnacceptableList(java.util.List list, String prefix, Object target, Object action) {
        // Use prefix+"[0]" so list element properties pick up one extra '[' in their path,
        // matching the indexed-path semantics of ParametersInterceptor (e.g. "items[0].key").
        String elementPrefix = prefix + "[0]";
        for (Object item : list) {
            if (item instanceof Map) {
                filterUnacceptableKeysRecursive((Map) item, elementPrefix, target, action);
            } else if (item instanceof java.util.List) {
                filterUnacceptableList((java.util.List) item, elementPrefix, target, action);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private boolean isAcceptableKey(String fullPath, Object target, Object action) {
        if (excludedPatterns != null && excludedPatterns.isExcluded(fullPath).isExcluded()) {
            LOG.warn("JSON body parameter [{}] matches an excluded pattern; rejected", fullPath);
            return false;
        }
        if (acceptedPatterns != null && !acceptedPatterns.isAccepted(fullPath).isAccepted()) {
            LOG.warn("JSON body parameter [{}] does not match any accepted pattern; rejected", fullPath);
            return false;
        }
        if (!parameterAuthorizer.isAuthorized(fullPath, target, action)) {
            LOG.warn("JSON body parameter [{}] rejected by @StrutsParameter authorization on [{}]",
                    fullPath, target.getClass().getName());
            return false;
        }
        return true;
    }
```

- [ ] **Step 4: Run the full test class to verify pass and no regressions**

Run: `mvn test -pl plugins/json -am -DskipAssembly -Dtest=JSONInterceptorTest`
Expected: PASS (all tests, including the existing authorization tests, green).

- [ ] **Step 5: Commit**

```bash
git add plugins/json/src/main/java/org/apache/struts2/json/JSONInterceptor.java plugins/json/src/test/java/org/apache/struts2/json/JSONInterceptorTest.java
git commit -m "WW-4858 feat(json): enforce excluded/accepted name patterns on JSON population

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 2: Enforce param-name max length

**Files:**
- Modify: `plugins/json/src/main/java/org/apache/struts2/json/JSONInterceptor.java`
- Modify: `plugins/json/src/test/java/org/apache/struts2/json/JSONInterceptorTest.java`

**Interfaces:**
- Consumes: `isAcceptableKey` (Task 1).
- Produces: field `int paramNameMaxLength = 100`; setter `void setParamNameMaxLength(int)`.

- [ ] **Step 1: Write the failing test**

Add to `JSONInterceptorTest.java`:

```java
    public void testParamNameMaxLengthRejectsLongKey() throws Exception {
        this.request.setContent("{\"foo\":\"a\"}".getBytes());
        this.request.addHeader("Content-Type", "application/json");

        JSONInterceptor interceptor = createInterceptor();
        interceptor.setParamNameMaxLength(2); // "foo" is length 3, over the limit
        TestAction action = new TestAction();

        this.invocation.setAction(action);
        this.invocation.getStack().push(action);

        interceptor.intercept(this.invocation);

        assertNull(action.getFoo());
    }
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -pl plugins/json -am -DskipAssembly -Dtest=JSONInterceptorTest#testParamNameMaxLengthRejectsLongKey`
Expected: FAIL — `setParamNameMaxLength` does not exist (compile error).

- [ ] **Step 3: Implement in `JSONInterceptor.java`**

Add field next to `excludedPatterns`/`acceptedPatterns`:

```java
    private int paramNameMaxLength = 100;
```

Add setter next to the checker setters:

```java
    /**
     * If the dotted JSON key path exceeds the configured maximum length it will not be accepted.
     *
     * @param paramNameMaxLength maximum length of a JSON key path
     */
    public void setParamNameMaxLength(int paramNameMaxLength) {
        this.paramNameMaxLength = paramNameMaxLength;
    }
```

Add the length check as the FIRST check inside `isAcceptableKey`, before the excluded-pattern check:

```java
        if (fullPath.length() > paramNameMaxLength) {
            LOG.warn("JSON body parameter [{}] is too long, allowed length is [{}]; rejected", fullPath, paramNameMaxLength);
            return false;
        }
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -pl plugins/json -am -DskipAssembly -Dtest=JSONInterceptorTest#testParamNameMaxLengthRejectsLongKey`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add plugins/json/src/main/java/org/apache/struts2/json/JSONInterceptor.java plugins/json/src/test/java/org/apache/struts2/json/JSONInterceptorTest.java
git commit -m "WW-4858 feat(json): enforce param-name max length on JSON population

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 3: Honor ParameterNameAware and ParameterValueAware

**Files:**
- Create: `plugins/json/src/test/java/org/apache/struts2/json/ParameterAwareTestAction.java`
- Modify: `plugins/json/src/main/java/org/apache/struts2/json/JSONInterceptor.java`
- Modify: `plugins/json/src/test/java/org/apache/struts2/json/JSONInterceptorTest.java`

**Interfaces:**
- Consumes: `isAcceptableKey` (Task 1), `filterUnacceptableKeysRecursive`/`filterUnacceptableList` (Task 1), `org.apache.struts2.action.ParameterNameAware.acceptableParameterName(String)`, `org.apache.struts2.action.ParameterValueAware.acceptableParameterValue(String)`.
- Produces: new method `boolean isAcceptableValue(String fullPath, Object value, Object action)`; leaf-value handling wired into `filterUnacceptableKeysRecursive` and `filterUnacceptableList`.

- [ ] **Step 1: Write the failing tests**

Create `plugins/json/src/test/java/org/apache/struts2/json/ParameterAwareTestAction.java`:

```java
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.json;

import org.apache.struts2.action.ParameterNameAware;
import org.apache.struts2.action.ParameterValueAware;

public class ParameterAwareTestAction implements ParameterNameAware, ParameterValueAware {

    private String foo;
    private String bar;
    private String baz;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public String getBaz() {
        return baz;
    }

    public void setBaz(String baz) {
        this.baz = baz;
    }

    @Override
    public boolean acceptableParameterName(String parameterName) {
        return !"bar".equals(parameterName);
    }

    @Override
    public boolean acceptableParameterValue(String parameterValue) {
        return !"blocked".equals(parameterValue);
    }
}
```

Add to `JSONInterceptorTest.java`:

```java
    public void testParameterNameAwareRejectsKey() throws Exception {
        this.request.setContent("{\"foo\":\"a\", \"bar\":\"b\"}".getBytes());
        this.request.addHeader("Content-Type", "application/json");

        JSONInterceptor interceptor = createInterceptor();
        ParameterAwareTestAction action = new ParameterAwareTestAction();

        this.invocation.setAction(action);
        this.invocation.getStack().push(action);

        interceptor.intercept(this.invocation);

        assertEquals("a", action.getFoo());
        assertNull(action.getBar());
    }

    public void testParameterValueAwareRejectsValue() throws Exception {
        this.request.setContent("{\"foo\":\"blocked\", \"baz\":\"good\"}".getBytes());
        this.request.addHeader("Content-Type", "application/json");

        JSONInterceptor interceptor = createInterceptor();
        ParameterAwareTestAction action = new ParameterAwareTestAction();

        this.invocation.setAction(action);
        this.invocation.getStack().push(action);

        interceptor.intercept(this.invocation);

        assertNull(action.getFoo());
        assertEquals("good", action.getBaz());
    }
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -pl plugins/json -am -DskipAssembly -Dtest=JSONInterceptorTest#testParameterNameAwareRejectsKey+testParameterValueAwareRejectsValue`
Expected: `testParameterNameAwareRejectsKey` FAILS (bar is populated because `ParameterNameAware` is not honored); `testParameterValueAwareRejectsValue` FAILS (foo is populated because `ParameterValueAware` is not honored).

- [ ] **Step 3: Implement in `JSONInterceptor.java`**

Add import next to the security-checker imports:

```java
import org.apache.struts2.action.ParameterNameAware;
import org.apache.struts2.action.ParameterValueAware;
```

Add the `ParameterNameAware` check to `isAcceptableKey`, as the LAST check before `return true;`:

```java
        if (action instanceof ParameterNameAware nameAware && !nameAware.acceptableParameterName(fullPath)) {
            LOG.debug("JSON body parameter [{}] rejected by ParameterNameAware action", fullPath);
            return false;
        }
```

Add the new leaf-value method after `isAcceptableKey`:

```java
    private boolean isAcceptableValue(String fullPath, Object value, Object action) {
        String stringValue = value == null ? null : String.valueOf(value);
        if (action instanceof ParameterValueAware valueAware && !valueAware.acceptableParameterValue(stringValue)) {
            LOG.debug("JSON body value for parameter [{}] rejected by ParameterValueAware action", fullPath);
            return false;
        }
        return true;
    }
```

Wire leaf-value handling into `filterUnacceptableKeysRecursive` by adding an `else` branch to the value dispatch (after the `List` branch):

```java
            if (value instanceof Map) {
                filterUnacceptableKeysRecursive((Map) value, fullPath, target, action);
            } else if (value instanceof java.util.List) {
                filterUnacceptableList((java.util.List) value, fullPath, target, action);
            } else if (!isAcceptableValue(fullPath, value, action)) {
                it.remove();
            }
```

Wire scalar-element value handling into `filterUnacceptableList` by converting the for-loop to an iterator and adding a scalar branch:

```java
    @SuppressWarnings("rawtypes")
    private void filterUnacceptableList(java.util.List list, String prefix, Object target, Object action) {
        String elementPrefix = prefix + "[0]";
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Object item = it.next();
            if (item instanceof Map) {
                filterUnacceptableKeysRecursive((Map) item, elementPrefix, target, action);
            } else if (item instanceof java.util.List) {
                filterUnacceptableList((java.util.List) item, elementPrefix, target, action);
            } else if (!isAcceptableValue(elementPrefix, item, action)) {
                it.remove();
            }
        }
    }
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `mvn test -pl plugins/json -am -DskipAssembly -Dtest=JSONInterceptorTest`
Expected: PASS (whole class green).

- [ ] **Step 5: Commit**

```bash
git add plugins/json/src/main/java/org/apache/struts2/json/JSONInterceptor.java plugins/json/src/test/java/org/apache/struts2/json/JSONInterceptorTest.java plugins/json/src/test/java/org/apache/struts2/json/ParameterAwareTestAction.java
git commit -m "WW-4858 feat(json): honor ParameterNameAware and ParameterValueAware on JSON population

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 4: Opt-in excluded/accepted value patterns

**Files:**
- Modify: `plugins/json/src/main/java/org/apache/struts2/json/JSONInterceptor.java`
- Modify: `plugins/json/src/test/java/org/apache/struts2/json/JSONInterceptorTest.java`

**Interfaces:**
- Consumes: `isAcceptableValue` (Task 3).
- Produces: fields `Set<Pattern> excludedValuePatterns`, `Set<Pattern> acceptedValuePatterns`; setters `void setExcludedValuePatterns(String)`, `void setAcceptedValuePatterns(String)`.

- [ ] **Step 1: Write the failing tests**

Add to `JSONInterceptorTest.java`:

```java
    public void testExcludedValuePatternRejectsValue() throws Exception {
        this.request.setContent("{\"foo\":\"badvalue\", \"bar\":\"okvalue\"}".getBytes());
        this.request.addHeader("Content-Type", "application/json");

        JSONInterceptor interceptor = createInterceptor();
        interceptor.setExcludedValuePatterns("badvalue");
        TestAction action = new TestAction();

        this.invocation.setAction(action);
        this.invocation.getStack().push(action);

        interceptor.intercept(this.invocation);

        assertNull(action.getFoo());
        assertEquals("okvalue", action.getBar());
    }

    public void testAcceptedValuePatternRejectsValue() throws Exception {
        this.request.setContent("{\"foo\":\"allowed\", \"bar\":\"other\"}".getBytes());
        this.request.addHeader("Content-Type", "application/json");

        JSONInterceptor interceptor = createInterceptor();
        interceptor.setAcceptedValuePatterns("allowed");
        TestAction action = new TestAction();

        this.invocation.setAction(action);
        this.invocation.getStack().push(action);

        interceptor.intercept(this.invocation);

        assertEquals("allowed", action.getFoo());
        assertNull(action.getBar());
    }
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -pl plugins/json -am -DskipAssembly -Dtest=JSONInterceptorTest#testExcludedValuePatternRejectsValue+testAcceptedValuePatternRejectsValue`
Expected: FAIL — `setExcludedValuePatterns`/`setAcceptedValuePatterns` do not exist (compile error).

- [ ] **Step 3: Implement in `JSONInterceptor.java`**

Add import next to the other imports:

```java
import org.apache.struts2.util.TextParseUtil;
```

Add fields next to `paramNameMaxLength`:

```java
    private Set<Pattern> excludedValuePatterns;
    private Set<Pattern> acceptedValuePatterns;
```

Add setters + a private compile helper next to `setParamNameMaxLength`:

```java
    /**
     * Sets a comma-delimited list of regular expressions to match JSON leaf values
     * that should be removed. Opt-in: no patterns configured means no value filtering.
     *
     * @param commaDelim comma-delimited regular expressions
     */
    public void setExcludedValuePatterns(String commaDelim) {
        this.excludedValuePatterns = compileValuePatterns(commaDelim);
    }

    /**
     * Sets a comma-delimited list of regular expressions; when set, only JSON leaf values
     * matching one of them are accepted. Opt-in: no patterns configured means no value filtering.
     *
     * @param commaDelim comma-delimited regular expressions
     */
    public void setAcceptedValuePatterns(String commaDelim) {
        this.acceptedValuePatterns = compileValuePatterns(commaDelim);
    }

    private static Set<Pattern> compileValuePatterns(String commaDelim) {
        Set<String> raw = TextParseUtil.commaDelimitedStringToSet(commaDelim);
        Set<Pattern> compiled = new HashSet<>(raw.size());
        for (String pattern : raw) {
            compiled.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
        }
        return Collections.unmodifiableSet(compiled);
    }
```

Extend `isAcceptableValue` — after the `ParameterValueAware` check and before `return true;`, add the value-pattern logic (mirrors `ParametersInterceptor`: null/empty always acceptable):

```java
        if (stringValue == null || stringValue.isEmpty()) {
            return true;
        }
        if (isValueExcluded(stringValue)) {
            LOG.warn("JSON body value [{}] for parameter [{}] matches an excluded value pattern; rejected", stringValue, fullPath);
            return false;
        }
        if (!isValueAccepted(stringValue)) {
            LOG.warn("JSON body value [{}] for parameter [{}] does not match any accepted value pattern; rejected", stringValue, fullPath);
            return false;
        }
```

Add the two predicate helpers after `isAcceptableValue`:

```java
    private boolean isValueExcluded(String value) {
        if (excludedValuePatterns == null || excludedValuePatterns.isEmpty()) {
            return false;
        }
        for (Pattern pattern : excludedValuePatterns) {
            if (pattern.matcher(value).matches()) {
                return true;
            }
        }
        return false;
    }

    private boolean isValueAccepted(String value) {
        if (acceptedValuePatterns == null || acceptedValuePatterns.isEmpty()) {
            return true;
        }
        for (Pattern pattern : acceptedValuePatterns) {
            if (pattern.matcher(value).matches()) {
                return true;
            }
        }
        return false;
    }
```

Note: `Set`, `HashSet`, `Collections`, and `Pattern` are already available (`import java.util.*;` and `import java.util.regex.Pattern;` at the top of the file).

- [ ] **Step 4: Run tests to verify they pass**

Run: `mvn test -pl plugins/json -am -DskipAssembly -Dtest=JSONInterceptorTest`
Expected: PASS (whole class green).

- [ ] **Step 5: Commit**

```bash
git add plugins/json/src/main/java/org/apache/struts2/json/JSONInterceptor.java plugins/json/src/test/java/org/apache/struts2/json/JSONInterceptorTest.java
git commit -m "WW-4858 feat(json): add opt-in excluded/accepted value patterns on JSON population

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 5: Opt-in applying excludeProperties/includeProperties to input

**Files:**
- Modify: `plugins/json/src/main/java/org/apache/struts2/json/JSONInterceptor.java`
- Modify: `plugins/json/src/test/java/org/apache/struts2/json/JSONInterceptorTest.java`

**Interfaces:**
- Consumes: `isAcceptableKey` (Task 1); existing fields `excludeProperties` and `includeProperties` (both `List<Pattern>`).
- Produces: field `boolean applyPropertyFiltersToInput = false`; setter `void setApplyPropertyFiltersToInput(boolean)`; private method `boolean isAcceptedByPropertyFilters(String)`.

- [ ] **Step 1: Write the failing test**

Add to `JSONInterceptorTest.java`:

```java
    public void testExcludePropertiesAppliedToInputWhenEnabled() throws Exception {
        this.request.setContent("{\"foo\":\"a\", \"bar\":\"b\"}".getBytes());
        this.request.addHeader("Content-Type", "application/json");

        JSONInterceptor interceptor = createInterceptor();
        interceptor.setApplyPropertyFiltersToInput(true);
        interceptor.setExcludeProperties("foo");
        TestAction action = new TestAction();

        this.invocation.setAction(action);
        this.invocation.getStack().push(action);

        interceptor.intercept(this.invocation);

        assertNull(action.getFoo());
        assertEquals("b", action.getBar());
    }

    public void testExcludePropertiesNotAppliedToInputByDefault() throws Exception {
        this.request.setContent("{\"foo\":\"a\", \"bar\":\"b\"}".getBytes());
        this.request.addHeader("Content-Type", "application/json");

        JSONInterceptor interceptor = createInterceptor();
        interceptor.setExcludeProperties("foo"); // configured, but flag off
        TestAction action = new TestAction();

        this.invocation.setAction(action);
        this.invocation.getStack().push(action);

        interceptor.intercept(this.invocation);

        assertEquals("a", action.getFoo());
        assertEquals("b", action.getBar());
    }
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -pl plugins/json -am -DskipAssembly -Dtest=JSONInterceptorTest#testExcludePropertiesAppliedToInputWhenEnabled+testExcludePropertiesNotAppliedToInputByDefault`
Expected: `testExcludePropertiesAppliedToInputWhenEnabled` FAILS to compile — `setApplyPropertyFiltersToInput` does not exist.

- [ ] **Step 3: Implement in `JSONInterceptor.java`**

Add field next to `excludedValuePatterns`/`acceptedValuePatterns`:

```java
    private boolean applyPropertyFiltersToInput = false;
```

Add setter next to the value-pattern setters:

```java
    /**
     * When enabled, the interceptor's {@code excludeProperties}/{@code includeProperties} patterns —
     * otherwise used only for serialization output — also gate which JSON keys are populated on input.
     * Opt-in; defaults to {@code false} to preserve existing behavior.
     *
     * @param applyPropertyFiltersToInput true to apply property filters to deserialization
     */
    public void setApplyPropertyFiltersToInput(boolean applyPropertyFiltersToInput) {
        this.applyPropertyFiltersToInput = applyPropertyFiltersToInput;
    }
```

Add the property-filter check to `isAcceptableKey`, immediately before `return true;` (after the `ParameterNameAware` check):

```java
        if (applyPropertyFiltersToInput && !isAcceptedByPropertyFilters(fullPath)) {
            LOG.debug("JSON body parameter [{}] rejected by excludeProperties/includeProperties on input", fullPath);
            return false;
        }
```

Add the helper after `isAcceptableKey`:

```java
    private boolean isAcceptedByPropertyFilters(String fullPath) {
        if (excludeProperties != null) {
            for (Pattern pattern : excludeProperties) {
                if (pattern.matcher(fullPath).matches()) {
                    return false;
                }
            }
        }
        if (includeProperties != null && !includeProperties.isEmpty()) {
            for (Pattern pattern : includeProperties) {
                if (pattern.matcher(fullPath).matches()) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `mvn test -pl plugins/json -am -DskipAssembly -Dtest=JSONInterceptorTest`
Expected: PASS (whole class green).

- [ ] **Step 5: Commit**

```bash
git add plugins/json/src/main/java/org/apache/struts2/json/JSONInterceptor.java plugins/json/src/test/java/org/apache/struts2/json/JSONInterceptorTest.java
git commit -m "WW-4858 feat(json): opt-in applying excludeProperties/includeProperties to JSON input

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 6: Full JSON-plugin regression verification

**Files:** none (verification only).

- [ ] **Step 1: Run the full JSON plugin test suite**

Run: `mvn test -pl plugins/json -am -DskipAssembly`
Expected: BUILD SUCCESS, all JSON plugin tests pass. If any pre-existing population test now fails, treat it as a regression in the traversal changes and fix before proceeding.

- [ ] **Step 2: Confirm no OGNL was introduced**

Run: `git diff main -- plugins/json/src/main/java/org/apache/struts2/json/JSONInterceptor.java`
Expected: the diff shows only reflection/pattern-based filtering; no calls to `stack.setValue`, `stack.findValue` for population, or OGNL evaluation were added to the population path.

- [ ] **Step 3: Verify the branch is clean and ready for PR**

Run: `git status` and `git log --oneline main..HEAD`
Expected: working tree clean; commits for Tasks 1–5 present, each prefixed `WW-4858`.

---

## Self-Review Notes

- **Spec coverage:** excluded/accepted name patterns → Task 1; param-name length → Task 2; `ParameterNameAware`/`ParameterValueAware` → Task 3; opt-in value patterns → Task 4; opt-in `excludeProperties`/`includeProperties` on input → Task 5; JSON-RPC left untouched (only the `Content-Type: application/json` `populateObject` path is filtered) — verified by not modifying the RPC branch; list-element scalar value-checking with individual removal → Task 3 (`filterUnacceptableList` iterator). Full regression → Task 6.
- **Rollout posture:** security/app-owned checks (name patterns, length, authorization, `*Aware`) are always-on; value patterns (Task 4) and property filters on input (Task 5) are opt-in and default off.
- **Type consistency:** `isAcceptableKey(String, Object, Object)`, `isAcceptableValue(String, Object, Object)`, and the renamed `filterUnacceptable*` methods are defined in Task 1/Task 3 and reused with identical signatures in later tasks.
