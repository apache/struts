# WW-5641: Restore `struts.json.writer` / `struts.json.reader` Override — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Restore the documented ability to override the JSON serializer/deserializer via the `struts.json.writer` / `struts.json.reader` constants, which regressed in the 7.2.x JSON hardening rework.

**Architecture:** The JSON plugin's `<bean-selection>` runs at plugin-parse time (inside `struts-plugin.xml`), before the app config is folded in, so it freezes the default `JSONWriter`/`JSONReader` binding to the shipped defaults. The framework already provides the order-safe home for order-sensitive bean-selection: `struts-deferred.xml`, which `Dispatcher.init()` loads **last**. The fix moves the JSON `<bean-selection>` from `struts-plugin.xml` into a new `struts-deferred.xml` (mirroring the velocity plugin), so `alias()` runs after the app override and binds the default to the override. `JSONUtil` reverts to its clean 7.2.x state.

**Tech Stack:** Java, Maven, Struts internal IoC container, JUnit (`junit.framework.TestCase` via `StrutsTestCase`).

**Spec:** `docs/superpowers/specs/2026-07-06-WW-5641-json-writer-reader-override-regression-design.md`
**Ticket:** [WW-5641](https://issues.apache.org/jira/browse/WW-5641)

## Global Constraints

- Commit messages MUST be prefixed with the ticket id: `WW-5641 <type>: <desc>`.
- Work on the feature branch `WW-5641-json-writer-reader-override`. Never commit to `main`.
- Do NOT change `StrutsJSONWriter`, `StrutsJSONReader`, the DoS-limit constants (`struts.json.maxDepth`, `maxElements`, `maxLength`, `maxStringLength`, `maxKeyLength`), `JSONBeanSelectionProvider`, `AbstractBeanSelectionProvider`, or any core class.
- `JSONUtil.java` must end up byte-identical to its state on `main` (commit `789dbf3cd`): `@Inject` on `setWriter(JSONWriter)` and `setReader(JSONReader)`, no `setContainer`, no `org.apache.struts2.inject.Container` import.
- The `<bean name="struts">` writer/reader definitions, the `JSONUtil`/`JSONCacheDestroyable` beans, all constants, and the `json-default` package stay in `struts-plugin.xml` — mirror the velocity plugin, which keeps `<bean>` in `struts-plugin.xml` and only `<bean-selection>` in `struts-deferred.xml`.
- Test idiom for this module: extend `StrutsTestCase`, name tests `testXxx()`, use inherited `assertEquals` (JUnit 3/4 style). No `@Test` annotations.
- The test `config` init param must be `struts-default.xml,struts-plugin.xml,struts-json-override.xml` — do NOT append `struts-deferred.xml` (the framework loads it unconditionally after the chain; listing it twice emits a spurious warning).

---

### Task 1: Move the JSON `<bean-selection>` to `struts-deferred.xml`, revert `JSONUtil`, prove the override wins (RED → GREEN)

One task: revert the earlier (rejected) `JSONUtil` change, relocate the `<bean-selection>` element, add the regression test, and verify RED→GREEN plus the full module suite. Single green commit.

**Files:**
- Modify: `plugins/json/src/main/java/org/apache/struts2/json/JSONUtil.java` (revert to `main` state — remove `setContainer` + `Container` import, restore `@Inject` on the two setters)
- Modify: `plugins/json/src/main/resources/struts-plugin.xml` (delete the `<bean-selection>` line)
- Create: `plugins/json/src/main/resources/struts-deferred.xml` (the moved `<bean-selection>`)
- Create: `plugins/json/src/test/java/org/apache/struts2/json/CustomTestJSONWriter.java`
- Create: `plugins/json/src/test/java/org/apache/struts2/json/CustomTestJSONReader.java`
- Create: `plugins/json/src/test/resources/struts-json-override.xml`
- Create: `plugins/json/src/test/java/org/apache/struts2/json/JSONWriterOverrideTest.java`

**Interfaces:**
- Consumes: `JSONWriter` (methods: `String write(Object)`; `String write(Object, Collection<Pattern>, Collection<Pattern>, boolean)`; `setIgnoreHierarchy(boolean)`; `setEnumAsBean(boolean)`; `setDateFormatter(String)`; `setCacheBeanInfo(boolean)`; `setExcludeProxyProperties(boolean)`). `JSONReader` (methods: `Object read(String)`; `setMaxElements(int)`; `setMaxDepth(int)`; `setMaxStringLength(int)`; `setMaxKeyLength(int)`). `JSONUtil.serialize(Object, boolean)` and `JSONUtil.getReader()`. `container.getInstance(Class)`.
- Produces: no new API. The behavioral contract: `container.getInstance(JSONWriter.class)` / `getInstance(JSONReader.class)` resolve to the app override when configured.

- [ ] **Step 1: Revert the `JSONUtil` import**

In `plugins/json/src/main/java/org/apache/struts2/json/JSONUtil.java`, remove the `Container` import line so only the `Inject` import remains:

```java
import org.apache.struts2.inject.Inject;
```

(Delete the `import org.apache.struts2.inject.Container;` line directly above it.)

- [ ] **Step 2: Revert the `JSONUtil` setters to the `main` state**

Replace the current block (the `setContainer` method plus the two plain setters):

```java
    @Inject
    public void setContainer(Container container) {
        setWriter(container.getInstance(JSONWriter.class,
                container.getInstance(String.class, JSONConstants.JSON_WRITER)));
        setReader(container.getInstance(JSONReader.class,
                container.getInstance(String.class, JSONConstants.JSON_READER)));
    }

    public void setReader(JSONReader reader) {
        this.reader = reader;
    }

    public JSONReader getReader() {
        return reader;
    }

    public void setWriter(JSONWriter writer) {
        this.writer = writer;
    }
```

with the original `main` version (restore `@Inject` on both setters, no `setContainer`):

```java
    @Inject
    public void setReader(JSONReader reader) {
        this.reader = reader;
    }

    public JSONReader getReader() {
        return reader;
    }

    @Inject
    public void setWriter(JSONWriter writer) {
        this.writer = writer;
    }
```

Verify with `git diff 789dbf3cd -- plugins/json/src/main/java/org/apache/struts2/json/JSONUtil.java` — it MUST print nothing (file identical to `main`).

- [ ] **Step 3: Remove the `<bean-selection>` from `struts-plugin.xml`**

In `plugins/json/src/main/resources/struts-plugin.xml`, delete this line (line 68):

```xml
    <bean-selection name="jsonBeans" class="org.apache.struts2.json.JSONBeanSelectionProvider"/>
```

Leave everything else (the `<bean>` definitions, constants, `json-default` package) unchanged. The closing `</struts>` remains.

- [ ] **Step 4: Create `struts-deferred.xml` with the moved element**

Create `plugins/json/src/main/resources/struts-deferred.xml` (mirrors `plugins/velocity/src/main/resources/struts-deferred.xml`):

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!--
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
-->
<!DOCTYPE struts PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 6.0//EN"
        "https://struts.apache.org/dtds/struts-6.0.dtd">

<struts>

    <bean-selection name="jsonBeans" class="org.apache.struts2.json.JSONBeanSelectionProvider"/>

</struts>
```

- [ ] **Step 5: Create the marker writer fixture**

Create `plugins/json/src/test/java/org/apache/struts2/json/CustomTestJSONWriter.java`:

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

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Marker writer proving that a user-configured {@code struts.json.writer} override
 * wins over the default {@link StrutsJSONWriter}. Emits a sentinel so output-level
 * assertions are unambiguous.
 */
public class CustomTestJSONWriter implements JSONWriter {

    public static final String SENTINEL = "{\"__customWriter__\":true}";

    @Override
    public String write(Object object) throws JSONException {
        return SENTINEL;
    }

    @Override
    public String write(Object object, Collection<Pattern> excludeProperties,
                        Collection<Pattern> includeProperties,
                        boolean excludeNullProperties) throws JSONException {
        return SENTINEL;
    }

    // Configuration setters are intentionally ignored: this marker always emits SENTINEL.
    // (in-body comment required by Sonar java:S1186 — empty methods must be explained)
    @Override public void setIgnoreHierarchy(boolean ignoreHierarchy) { /* no-op marker */ }
    @Override public void setEnumAsBean(boolean enumAsBean) { /* no-op marker */ }
    @Override public void setDateFormatter(String defaultDateFormat) { /* no-op marker */ }
    @Override public void setCacheBeanInfo(boolean cacheBeanInfo) { /* no-op marker */ }
    @Override public void setExcludeProxyProperties(boolean excludeProxyProperties) { /* no-op marker */ }
}
```

- [ ] **Step 6: Create the marker reader fixture**

Create `plugins/json/src/test/java/org/apache/struts2/json/CustomTestJSONReader.java`:

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

/**
 * Marker reader proving that a user-configured {@code struts.json.reader} override
 * wins over the default {@link StrutsJSONReader}.
 */
public class CustomTestJSONReader implements JSONReader {

    public static final String SENTINEL = "__customReader__";

    @Override
    public Object read(String string) throws JSONException {
        return SENTINEL;
    }

    // Limit setters are intentionally ignored: this marker always returns SENTINEL.
    // (in-body comment required by Sonar java:S1186 — empty methods must be explained)
    @Override public void setMaxElements(int maxElements) { /* no-op marker */ }
    @Override public void setMaxDepth(int maxDepth) { /* no-op marker */ }
    @Override public void setMaxStringLength(int maxStringLength) { /* no-op marker */ }
    @Override public void setMaxKeyLength(int maxKeyLength) { /* no-op marker */ }
}
```

- [ ] **Step 7: Create the application-style override config**

Create `plugins/json/src/test/resources/struts-json-override.xml`. The ASF license header is
required — Apache RAT (bound to `prepare-package`) flags any `src/test/resources/**/*.xml`
without it and fails the build:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!--
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
-->
<!DOCTYPE struts PUBLIC
    "-//Apache Software Foundation//DTD Struts Configuration 6.0//EN"
    "https://struts.apache.org/dtds/struts-6.0.dtd">
<struts>
    <bean type="org.apache.struts2.json.JSONWriter" name="customTestWriter"
          class="org.apache.struts2.json.CustomTestJSONWriter" scope="prototype"/>
    <bean type="org.apache.struts2.json.JSONReader" name="customTestReader"
          class="org.apache.struts2.json.CustomTestJSONReader" scope="prototype"/>
    <constant name="struts.json.writer" value="customTestWriter"/>
    <constant name="struts.json.reader" value="customTestReader"/>
</struts>
```

- [ ] **Step 8: Create the regression test**

Create `plugins/json/src/test/java/org/apache/struts2/json/JSONWriterOverrideTest.java`. It boots the real `Dispatcher` chain; `struts-deferred.xml` is auto-loaded last by `Dispatcher.init()` (do NOT list it in `config`). Asserts both the default container binding and the effective use:

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

import org.apache.struts2.junit.StrutsTestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Regression test for WW-5641: {@code struts.json.writer} / {@code struts.json.reader}
 * overrides from an application config were ignored on the 7.2.x line because the JSON
 * plugin's {@code <bean-selection>} ran at plugin-parse time, before the app config.
 * Moving it to {@code struts-deferred.xml} makes it run last, so the override wins.
 *
 * <p>Boots the real Dispatcher chain. {@code struts-deferred.xml} is loaded unconditionally
 * by {@code Dispatcher.init()} after the {@code config} chain, so it is intentionally omitted
 * from the config init param below.</p>
 */
public class JSONWriterOverrideTest extends StrutsTestCase {

    @Override
    protected void setupBeforeInitDispatcher() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("config", "struts-default.xml,struts-plugin.xml,struts-json-override.xml");
        dispatcherInitParams = params;
    }

    /**
     * The default JSONWriter binding must resolve to the app override, not StrutsJSONWriter.
     */
    public void testCustomWriterWinsAsDefaultBinding() {
        JSONWriter writer = container.getInstance(JSONWriter.class);
        assertEquals("struts.json.writer override was ignored; default StrutsJSONWriter was used",
                CustomTestJSONWriter.class, writer.getClass());
    }

    /**
     * The default JSONReader binding must resolve to the app override, not StrutsJSONReader.
     */
    public void testCustomReaderWinsAsDefaultBinding() {
        JSONReader reader = container.getInstance(JSONReader.class);
        assertEquals("struts.json.reader override was ignored; default StrutsJSONReader was used",
                CustomTestJSONReader.class, reader.getClass());
    }

    /**
     * End-to-end: the writer JSONUtil actually serializes with must be the override.
     */
    public void testJSONUtilUsesCustomWriter() throws Exception {
        JSONUtil jsonUtil = container.getInstance(JSONUtil.class);
        assertEquals(CustomTestJSONWriter.SENTINEL, jsonUtil.serialize(new Object(), false));
    }

    /**
     * End-to-end: the reader JSONUtil exposes must be the override.
     */
    public void testJSONUtilUsesCustomReader() {
        JSONUtil jsonUtil = container.getInstance(JSONUtil.class);
        assertEquals(CustomTestJSONReader.class, jsonUtil.getReader().getClass());
    }
}
```

- [ ] **Step 9: Verify the test reproduces the bug BEFORE the resource move**

To confirm the test is a real reproduction, temporarily stash only the two resource changes and run the test against the old wiring:

```bash
git stash push -- plugins/json/src/main/resources/struts-plugin.xml plugins/json/src/main/resources/struts-deferred.xml
mvn test -DskipAssembly -pl plugins/json -Dtest=JSONWriterOverrideTest
git stash pop
```

Expected with the resources stashed (old wiring, `<bean-selection>` still in `struts-plugin.xml`, no deferred file): all four tests FAIL — default bindings resolve to `StrutsJSONWriter`/`StrutsJSONReader` and `serialize` returns `{}`. If any test passes here, the bootstrap is not reproducing the ordering — stop and re-check the `config` param before proceeding. (The `JSONUtil` revert from Steps 1-2 stays in place during this check; it is not what causes the bug.)

- [ ] **Step 10: Run the test to verify it PASSES with the fix in place**

Run: `mvn test -DskipAssembly -pl plugins/json -Dtest=JSONWriterOverrideTest`
Expected: all four tests PASS. Output pristine — no "default mapping already assigned" warning for `JSONWriter`/`JSONReader`.

- [ ] **Step 11: Run the full JSON plugin suite AND the license check**

Run: `mvn test -DskipAssembly -pl plugins/json`
Expected: BUILD SUCCESS, all tests pass (DoS-limit, bean-info caching, and the `new JSONUtil()` + `setWriter`/`setReader` tests are all unaffected).

Then run the Apache RAT license check — it binds to `prepare-package`, so `mvn test` does NOT
exercise it, and a new resource file missing the ASF header will pass tests but fail CI/`install`:

Run: `mvn apache-rat:check -pl plugins/json`
Expected: BUILD SUCCESS (exit 0), no `UNAPPROVED` files. If it flags `struts-json-override.xml`,
the ASF header from Step 7 is missing — add it and re-run.

- [ ] **Step 12: Commit**

```bash
git add plugins/json/src/main/java/org/apache/struts2/json/JSONUtil.java \
        plugins/json/src/main/resources/struts-plugin.xml \
        plugins/json/src/main/resources/struts-deferred.xml \
        plugins/json/src/test/java/org/apache/struts2/json/CustomTestJSONWriter.java \
        plugins/json/src/test/java/org/apache/struts2/json/CustomTestJSONReader.java \
        plugins/json/src/test/java/org/apache/struts2/json/JSONWriterOverrideTest.java \
        plugins/json/src/test/resources/struts-json-override.xml
git commit -m "WW-5641 fix: run JSON bean-selection from struts-deferred.xml

The JSON plugin declared <bean-selection> in struts-plugin.xml, which runs
at plugin-parse time, before the application struts.xml is folded in. That
froze the JSONWriter/JSONReader default binding to StrutsJSONWriter/Reader,
so struts.json.writer / struts.json.reader overrides were ignored.

Move the element to struts-deferred.xml, which Dispatcher loads last (after
the app config and core's StrutsBeanSelectionProvider), so the alias honors
the override. Mirrors the velocity plugin. JSONUtil is unchanged from main.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Self-Review

**Spec coverage:**
- Move `<bean-selection>` to `struts-deferred.xml` → Steps 3-4. ✓
- Revert `JSONUtil` to `main` state → Steps 1-2 + git-diff verification. ✓
- Keep `<bean>` defs/constants in `struts-plugin.xml` (velocity mirror) → Step 3 leaves them; Global Constraints. ✓
- No core / `JSONBeanSelectionProvider` / DoS / `StrutsJSONWriter` changes → no task touches them; Global Constraints forbids it. ✓
- Regression test asserts BOTH default binding AND effective use, for writer AND reader → Step 8 (4 tests). ✓
- `StrutsTestCase` real Dispatcher; `struts-deferred.xml` not in `config` param → Step 8 + Global Constraints + Step 10 pristine-output check. ✓
- FAIL-before / PASS-after → Steps 9 and 10. ✓
- Full module suite green → Step 11. ✓

**Placeholder scan:** No TBD/TODO; every code/resource step shows complete content. ✓

**Type consistency:** Fixture method signatures match the `JSONWriter`/`JSONReader` interfaces. `serialize(Object, boolean)` and `getReader()` exist on `JSONUtil`. `container.getInstance(Class)` used for default-binding assertions. Bean names (`customTestWriter`/`customTestReader`) match between the override XML and the constant values. The deferred element's `name`/`class` match the line removed from `struts-plugin.xml`. ✓
