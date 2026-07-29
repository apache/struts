# WW-3784 Specificity-ordered wildcard matching for annotated actions — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make annotation-based (Convention plugin) wildcard action patterns match most-specific-first, so a specific pattern like `some/usefull/*` is never shadowed by a general one like `some/*`, regardless of class-scan order.

**Architecture:** The fix lives entirely in the Convention plugin plus one neutral core helper. A new `Comparator<String>` ranks action-name patterns by specificity; a new `PackageConfig.Builder.reorderActionConfigs(Comparator)` re-sorts a package's `LinkedHashMap` of action configs; and `PackageBasedActionConfigBuilder` applies the sort to every package after building it. Core matchers and XML configuration providers are untouched, so XML's explicit file-order semantics are preserved.

**Tech Stack:** Java 17, Maven multi-module (`core`, `plugins/convention`), JUnit 4 (convention tests) and JUnit 3 / `junit.framework.TestCase` (core tests), AssertJ/Mockito available but not required here.

## Global Constraints

- **Commit prefix:** every commit message MUST start with `WW-3784` followed by a Conventional-Commits type (`feat`/`test`/`refactor`/`docs`). End each commit body with `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`.
- **Branch:** work on `WW-3784-annotated-wildcard-specificity-ordering` (already created and checked out). Never commit to `main`.
- **Fix version target:** 7.3.0. Use `@since 7.3.0 (WW-3784)` on new public/protected API.
- **No XML/core-matcher behavior changes:** do not modify `AbstractMatcher`, `ActionConfigMatcher`, or any `config/providers/*Xml*` class.
- **Core cannot depend on Convention:** the specificity `Comparator` lives in the convention plugin; the core `reorderActionConfigs` helper must accept a generic `Comparator<String>` and must not reference the comparator class.
- **Core test trap:** core tests that `extends XWorkTestCase` silently ignore JUnit 4 `@Test`. New core tests here use `extends junit.framework.TestCase` with `testXxx()` methods.
- **Build/test commands:** `mvn test -DskipAssembly -pl core -Dtest=...` and `mvn test -DskipAssembly -pl plugins/convention -Dtest=...` (add `-am` on the first plugin run so the freshly-built core is available).

---

### Task 1: `ActionNameSpecificityComparator` (Convention plugin)

A pure `Comparator<String>` that orders wildcard action-name patterns most-specific-first.

**Files:**
- Create: `plugins/convention/src/main/java/org/apache/struts2/convention/ActionNameSpecificityComparator.java`
- Test: `plugins/convention/src/test/java/org/apache/struts2/convention/ActionNameSpecificityComparatorTest.java`

**Interfaces:**
- Consumes: nothing.
- Produces: `public class ActionNameSpecificityComparator implements java.util.Comparator<String>` with `public int compare(String a, String b)`. Ordering keys, most-specific first: (1) fewer wildcard tokens; (2) more literal characters; (3) fewer path-spanning `**` tokens; (4) natural `String` order (deterministic tiebreak). A wildcard token is one `*`/`**`/`***…` run **or** one `{var}` group.

- [ ] **Step 1: Write the failing test**

```java
package org.apache.struts2.convention;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ActionNameSpecificityComparatorTest {

    private final ActionNameSpecificityComparator comparator = new ActionNameSpecificityComparator();

    @Test
    public void moreLiteralPrefixIsMoreSpecific_ticketCase() {
        // equal wildcard count (1 each); "some/usefull/*" has more literal chars -> more specific
        assertTrue(comparator.compare("some/usefull/*", "some/*") < 0);
    }

    @Test
    public void fewerWildcardsIsMoreSpecific() {
        assertTrue(comparator.compare("a/*", "a/*/*") < 0);
    }

    @Test
    public void singleStarBeatsPathStarAtEqualLiterals() {
        // both "a/" literal (2 chars), one wildcard each; "a/*" (file) beats "a/**" (path)
        assertTrue(comparator.compare("a/*", "a/**") < 0);
    }

    @Test
    public void namedVariablesCountAsWildcards() {
        assertTrue(comparator.compare("some/usefull/{id}", "some/{id}") < 0);
    }

    @Test
    public void literalRanksBeforeAnyWildcard() {
        assertTrue(comparator.compare("some/list", "some/*") < 0);
    }

    @Test
    public void sortIsDeterministicRegardlessOfInputOrder() {
        List<String> expected = Arrays.asList("some/usefull/*", "some/*", "*");
        List<String> shuffled = new ArrayList<>(expected);
        Collections.shuffle(shuffled, new Random(42));
        shuffled.sort(comparator);
        assertEquals(expected, shuffled);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -DskipAssembly -pl plugins/convention -am -Dtest=ActionNameSpecificityComparatorTest`
Expected: FAIL to compile — `ActionNameSpecificityComparator` does not exist.

- [ ] **Step 3: Write the implementation**

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
package org.apache.struts2.convention;

import java.util.Comparator;

/**
 * Orders wildcard action-name patterns most-specific-first so that, under the framework's
 * first-match-wins matching, a specific pattern (e.g. {@code some/usefull/*}) is evaluated
 * before a general one (e.g. {@code some/*}).
 *
 * <p>Ordering keys, applied in order:</p>
 * <ol>
 *   <li>fewer wildcard tokens first (a {@code *}/{@code **} run, or a <code>{var}</code> group);</li>
 *   <li>more literal characters first;</li>
 *   <li>fewer path-spanning {@code **} tokens first;</li>
 *   <li>natural (alphabetical) order of the pattern, for deterministic tie-breaking.</li>
 * </ol>
 *
 * <p>Matcher-agnostic: it recognises both {@code *}/{@code **} (WildcardHelper) and
 * <code>{var}</code> (NamedVariablePatternMatcher) wildcards.</p>
 *
 * @since 7.3.0 (WW-3784)
 */
public class ActionNameSpecificityComparator implements Comparator<String> {

    @Override
    public int compare(String a, String b) {
        Counts ca = count(a);
        Counts cb = count(b);

        int byWildcards = Integer.compare(ca.wildcards, cb.wildcards);
        if (byWildcards != 0) {
            return byWildcards;
        }
        int byLiterals = Integer.compare(cb.literals, ca.literals); // more literals first
        if (byLiterals != 0) {
            return byLiterals;
        }
        int byPathWildcards = Integer.compare(ca.pathWildcards, cb.pathWildcards);
        if (byPathWildcards != 0) {
            return byPathWildcards;
        }
        return a.compareTo(b);
    }

    private Counts count(String pattern) {
        int wildcards = 0;
        int pathWildcards = 0;
        int literals = 0;
        int i = 0;
        int len = pattern.length();
        while (i < len) {
            char c = pattern.charAt(i);
            if (c == '*') {
                int start = i;
                while (i < len && pattern.charAt(i) == '*') {
                    i++;
                }
                wildcards++;
                if (i - start >= 2) {
                    pathWildcards++;
                }
            } else if (c == '{') {
                int close = pattern.indexOf('}', i);
                if (close < 0) {
                    literals += len - i; // malformed: treat the remainder as literal
                    break;
                }
                wildcards++;
                i = close + 1;
            } else {
                literals++;
                i++;
            }
        }
        return new Counts(wildcards, pathWildcards, literals);
    }

    private record Counts(int wildcards, int pathWildcards, int literals) {
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -DskipAssembly -pl plugins/convention -am -Dtest=ActionNameSpecificityComparatorTest`
Expected: PASS (6 tests).

- [ ] **Step 5: Commit**

```bash
git add plugins/convention/src/main/java/org/apache/struts2/convention/ActionNameSpecificityComparator.java \
        plugins/convention/src/test/java/org/apache/struts2/convention/ActionNameSpecificityComparatorTest.java
git commit -m "WW-3784 feat(convention): add action-name specificity comparator

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 2: `PackageConfig.Builder.reorderActionConfigs` (core helper)

A neutral core helper that re-sorts a package's action-config map by a caller-supplied comparator over action names. Generic on purpose — core never references the convention comparator.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/config/entities/PackageConfig.java` (add `import java.util.Comparator;` near the other `java.util` imports at lines 25-33; add the `reorderActionConfigs` method inside the `Builder` class, next to `addActionConfig` at line 515)
- Test: `core/src/test/java/org/apache/struts2/config/entities/PackageConfigBuilderReorderTest.java`

**Interfaces:**
- Consumes: nothing from other tasks.
- Produces: `public PackageConfig.Builder reorderActionConfigs(java.util.Comparator<String> byActionName)` — re-inserts the builder's action configs into a fresh `LinkedHashMap` ordered by `byActionName` over the action-name keys; returns `this`. Must be called before `build()`.

- [ ] **Step 1: Write the failing test**

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
package org.apache.struts2.config.entities;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PackageConfigBuilderReorderTest extends TestCase {

    public void testReorderActionConfigsAppliesComparator() {
        PackageConfig.Builder builder = new PackageConfig.Builder("test");
        builder.addActionConfig("some/*", action("some/*"));
        builder.addActionConfig("some/usefull/*", action("some/usefull/*"));

        // reverse-alphabetical proves the map is genuinely reordered, not left as-inserted
        builder.reorderActionConfigs(Comparator.reverseOrder());

        List<String> keys = new ArrayList<>(builder.build().getActionConfigs().keySet());
        assertEquals(List.of("some/usefull/*", "some/*"), keys);
    }

    private ActionConfig action(String name) {
        return new ActionConfig.Builder("test", name, "com.example.Action").build();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=PackageConfigBuilderReorderTest`
Expected: FAIL to compile — `reorderActionConfigs` does not exist.

- [ ] **Step 3: Add the import and the method**

Add near the other `java.util` imports (lines 25-33):

```java
import java.util.Comparator;
```

Add inside the `Builder` class, immediately after `addActionConfig` (line 515-518):

```java
public Builder reorderActionConfigs(Comparator<String> byActionName) {
    List<Map.Entry<String, ActionConfig>> entries = new ArrayList<>(target.actionConfigs.entrySet());
    entries.sort(Map.Entry.comparingByKey(byActionName));
    Map<String, ActionConfig> reordered = new LinkedHashMap<>();
    for (Map.Entry<String, ActionConfig> entry : entries) {
        reordered.put(entry.getKey(), entry.getValue());
    }
    target.actionConfigs = reordered;
    return this;
}
```

(`ArrayList`, `List`, `Map`, and `LinkedHashMap` are already imported.)

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=PackageConfigBuilderReorderTest`
Expected: PASS (1 test).

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/config/entities/PackageConfig.java \
        core/src/test/java/org/apache/struts2/config/entities/PackageConfigBuilderReorderTest.java
git commit -m "WW-3784 feat(core): add PackageConfig.Builder.reorderActionConfigs

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 3: Wire specificity ordering into `PackageBasedActionConfigBuilder`

Apply the comparator to every package the Convention plugin builds, right after index actions are added and before packages are registered with the configuration.

**Files:**
- Modify: `plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java` (add `import java.util.Comparator;` near line 75; call the new method in `buildConfiguration` after `buildIndexActions(packageConfigs);` at line 796; add the `reorderActionConfigsBySpecificity` method)
- Test: `plugins/convention/src/test/java/org/apache/struts2/convention/PackageBasedActionConfigBuilderReorderTest.java`

**Interfaces:**
- Consumes: `ActionNameSpecificityComparator` (Task 1); `PackageConfig.Builder.reorderActionConfigs(Comparator<String>)` (Task 2).
- Produces: `static void reorderActionConfigsBySpecificity(Map<String, PackageConfig.Builder> packageConfigs)` — package-private, `static` so it is unit-testable without constructing a builder; applies `new ActionNameSpecificityComparator()` to every builder in the map.

- [ ] **Step 1: Write the failing test**

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
package org.apache.struts2.convention;

import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.PackageConfig;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PackageBasedActionConfigBuilderReorderTest {

    @Test
    public void reordersEveryPackageSpecificFirst() {
        // input insertion order is general-before-specific (the bug scenario)
        PackageConfig.Builder pkg = new PackageConfig.Builder("test");
        pkg.addActionConfig("some/*", action("some/*"));
        pkg.addActionConfig("some/usefull/*", action("some/usefull/*"));

        Map<String, PackageConfig.Builder> packageConfigs = new HashMap<>();
        packageConfigs.put("test", pkg);

        PackageBasedActionConfigBuilder.reorderActionConfigsBySpecificity(packageConfigs);

        List<String> keys = new ArrayList<>(pkg.build().getActionConfigs().keySet());
        assertEquals(List.of("some/usefull/*", "some/*"), keys);
    }

    private ActionConfig action(String name) {
        return new ActionConfig.Builder("test", name, "com.example.Action").build();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -DskipAssembly -pl plugins/convention -am -Dtest=PackageBasedActionConfigBuilderReorderTest`
Expected: FAIL to compile — `reorderActionConfigsBySpecificity` does not exist.

- [ ] **Step 3: Add the import, the call site, and the method**

Add near line 75 (with the other `java.util` imports):

```java
import java.util.Comparator;
```

In `buildConfiguration`, change the block at lines 796-802 so the reorder runs after index actions and before registration:

```java
        buildIndexActions(packageConfigs);

        reorderActionConfigsBySpecificity(packageConfigs);

        // Add the new actions to the configuration
        Set<String> packageNames = packageConfigs.keySet();
        for (String packageName : packageNames) {
            configuration.addPackageConfig(packageName, packageConfigs.get(packageName).build());
        }
```

Add the method (e.g. immediately after `buildConfiguration`, before `getAllowedMethods` at line 805):

```java
    /**
     * Reorders each package's action configs most-specific-first so that annotated wildcard
     * patterns follow specific-before-general precedence under first-match-wins matching.
     * XML-defined packages are untouched: only packages built by this convention builder pass
     * through here.
     *
     * @param packageConfigs the packages built during {@link #buildConfiguration(Set)}
     * @since 7.3.0 (WW-3784)
     */
    static void reorderActionConfigsBySpecificity(Map<String, PackageConfig.Builder> packageConfigs) {
        Comparator<String> bySpecificity = new ActionNameSpecificityComparator();
        for (PackageConfig.Builder packageConfig : packageConfigs.values()) {
            packageConfig.reorderActionConfigs(bySpecificity);
        }
    }
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -DskipAssembly -pl plugins/convention -am -Dtest=PackageBasedActionConfigBuilderReorderTest`
Expected: PASS (1 test).

- [ ] **Step 5: Run the full convention + core suites for regressions**

Run: `mvn test -DskipAssembly -pl core,plugins/convention -am`
Expected: PASS. Pay attention to `PackageBasedActionConfigBuilderTest` — if any assertion depended on the old (arbitrary) action-config ordering, update that expectation to the new specificity order and note it in the commit body.

- [ ] **Step 6: Commit**

```bash
git add plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java \
        plugins/convention/src/test/java/org/apache/struts2/convention/PackageBasedActionConfigBuilderReorderTest.java
git commit -m "WW-3784 feat(convention): order annotated wildcard actions most-specific-first

Sorts each convention-built package's action configs by pattern specificity so a
specific pattern (some/usefull/*) is matched before a general one (some/*),
regardless of class-scan order. Also makes convention action ordering deterministic.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Notes for the implementer

- **Known limitation (documented in the spec):** ordering is per-package. Convention places all actions of one namespace into the same package, so the common case is covered; competing wildcards spread across *different* convention packages sharing a namespace remain in package-registration order. Do not try to expand scope to namespace-wide ordering — that would require touching core `DefaultConfiguration` and risk affecting XML.
- **Do not add a config flag.** The behavior is always on for Convention by design.
- **If `PackageBasedActionConfigBuilderTest` fails only on ordering:** that is expected fallout of making order deterministic; re-baseline the affected assertions to specificity order. Any *non-ordering* failure is a real regression — stop and investigate.

## Self-Review

- **Spec coverage:**
  - Scalar multi-key comparator (spec §"The specificity comparator") → Task 1.
  - `PackageConfig.Builder` reorder helper (spec §"Architecture & placement") → Task 2.
  - Convention integration point after `buildIndexActions` (spec §"Integration point") → Task 3.
  - Testing — comparator unit tests incl. shuffle-invariance (spec §Testing) → Task 1; wiring/order test → Task 3; core helper test → Task 2.
  - XML/core-matcher untouched (spec §Scope, §Out of scope) → enforced by Global Constraints; no such files modified.
- **Placeholder scan:** none — all steps contain concrete code and exact commands.
- **Type consistency:** `reorderActionConfigs(Comparator<String>)` is defined in Task 2 and consumed with the identical signature in Task 3; `ActionNameSpecificityComparator` is defined in Task 1 and instantiated in Task 3; `reorderActionConfigsBySpecificity(Map<String, PackageConfig.Builder>)` is defined and tested with the same signature in Task 3.
