# WW-5674 — Allocation-free `isClassBelongsToPackages` Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove the per-OGNL-access allocation overhead in `SecurityMemberAccess.isClassBelongsToPackages` and `toPackageName`, with zero change to allow/deny semantics proven by test.

**Architecture:** Replace a `split` + `IntStream` + `String.join` prefix construction with an index walk over the package-name string, extracted into a package-private pure function so it can be tested against shapes no real `Class` can produce. Swap `Class.getPackage().getName()` for the cached `Class.getPackageName()` behind an `isArray()/isPrimitive()` guard that reproduces the old result exactly. Collapse the allowlist path's two walks into one via a two-set overload.

**Tech Stack:** Java 17, Maven, JUnit 4, AssertJ, Mockito.

**Spec:** `docs/superpowers/specs/2026-08-03-WW-5674-isclassbelongstopackages-allocation-design.md`

**Ticket:** [WW-5674](https://issues.apache.org/jira/browse/WW-5674), sub-task of [WW-5667](https://issues.apache.org/jira/browse/WW-5667)

## Global Constraints

- **Java release target is 17** (`maven.compiler.release=17` in root `pom.xml`). `Class.getPackageName()` is Java 9+, so it is available.
- **Core tests are JUnit 4, never JUnit 5.** Use `org.junit.Test` and `org.junit.Before`. An `@org.junit.jupiter.api.Test` added here silently never runs.
- **AssertJ** (`org.assertj.core.api.Assertions.assertThat`) and **Mockito** are already on the core test classpath.
- **Zero behavior change is the acceptance criterion.** Any test that changes an existing assertion means the change is wrong, not the test.
- **Do not change array or primitive package semantics.** `toPackageName` must keep returning `""` for arrays, primitives and `void`. This is deliberate — see the spec's *Deliberately out of scope* section. Adopting `getPackageName()` semantics there loosens the allowlist.
- **Branch is `WW-5674-isclassbelongstopackages-allocation`, already checked out.** Never commit to `main`.
- **Commit message format:** `WW-5674 <type>(<scope>): <description>`, e.g. `WW-5674 test(ognl): ...`. Ticket prefix is mandatory.
- **No JMH, no timing assertions.** The project has no benchmark harness and none is added. Wall-clock assertions are unreliable in CI.
- Single test run: `mvn test -DskipAssembly -pl core -Dtest=<ClassName>`
- Full core suite: `mvn test -DskipAssembly -pl core`

## File Structure

| File | Responsibility |
|---|---|
| `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java` | Modified. Lines 374–379 (`toPackageName`), 389–394 (`isClassBelongsToPackages`), 254–261 (`isClassAllowlisted`), imports at 35 and 39. |
| `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessPackageMatchingTest.java` | Created. All new tests for the static package-matching utilities. |
| `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessTest.java` | Untouched. Must stay green without edits. |
| `core/src/test/java/PackagelessAction.java` | Existing default-package class, reused via `Class.forName("PackagelessAction")`. Do not modify. |

**Note on test file placement.** The spec named `SecurityMemberAccessTest.java` as the test home. That file is 1136 lines, and the new tests are a self-contained block of pure-static table-driven checks that need `List` and `IntStream` imports the existing file does not have. They go in a new focused test class in the same package instead — `isPackageBelongsToPackages` is package-private, so the test must live in `org.apache.struts2.ognl`. This is a deliberate, flagged deviation and it strengthens the spec's requirement that the existing suite pass unmodified.

---

### Task 1: Characterization tests that lock in current behavior

Create the test file and pin the *existing* behavior before touching any production code. These tests exercise only the current public API (`isClassBelongsToPackages(Class, Set)` and `toPackageName(Class)`), so they **pass immediately against unmodified code**.

This is intentional and is the gate for the whole plan: if any assertion here fails, the semantic claims in the spec are wrong and you must stop and re-derive them rather than "fixing" the test.

**Files:**
- Create: `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessPackageMatchingTest.java`

**Interfaces:**
- Consumes: existing `SecurityMemberAccess.isClassBelongsToPackages(Class<?>, Set<String>)`, `SecurityMemberAccess.toPackageName(Class<?>)`, `ConfigParseUtil.toPackageNamesSet(String)`.
- Produces: the constants `PACKAGE_NAMES` and `CANDIDATE_SETS`, and the helpers `legacyPrefixMatch(String, Set<String>)` and `legacyToPackageName(Class<?>)`, all reused by Tasks 3 and 4.

- [ ] **Step 1: Create the test file**

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
package org.apache.struts2.ognl;

import org.apache.struts2.util.ConfigParseUtil;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Collections.emptySet;
import static org.apache.struts2.ognl.SecurityMemberAccess.isClassBelongsToPackages;
import static org.apache.struts2.ognl.SecurityMemberAccess.toPackageName;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Characterisation and equivalence tests for the static package-matching helpers in
 * {@link SecurityMemberAccess}, covering WW-5674.
 * <p>
 * These helpers gate OGNL member access, so the rewrite in WW-5674 must be exactly
 * behaviour-preserving. That is proven here by running the replaced implementation
 * side by side with the new one over a matrix of inputs.
 */
public class SecurityMemberAccessPackageMatchingTest {

    /**
     * The implementation replaced by WW-5674, retained verbatim apart from taking the package
     * name directly instead of a {@link Class}. Used as the reference oracle for the rewrite.
     */
    private static boolean legacyPrefixMatch(String packageName, Set<String> matchingPackages) {
        List<String> packageParts = List.of(packageName.split("\\."));
        return IntStream.range(0, packageParts.size())
                .mapToObj(i -> String.join(".", packageParts.subList(0, i + 1)))
                .anyMatch(matchingPackages::contains);
    }

    /**
     * The {@code toPackageName} implementation replaced by WW-5674, retained as the reference oracle.
     */
    private static String legacyToPackageName(Class<?> clazz) {
        if (clazz.getPackage() == null) {
            return "";
        }
        return clazz.getPackage().getName();
    }

    /**
     * Package-name shapes. Deliberately excludes trailing-dot inputs such as {@code "a.b."}:
     * {@code split} drops trailing empty segments where an index walk would not, and
     * {@code Class.getPackage().getName()} cannot produce a trailing dot, so the shape is
     * unreachable through every caller. See the spec's "Verified current semantics" section.
     */
    private static final List<String> PACKAGE_NAMES = List.of(
            "",
            "java",
            "a.b.c",
            "a..b",
            ".a",
            "org.apache.struts2",
            "org.apache.struts2.ognl",
            "org.apache.struts2x",
            "java.io",
            "java.io.tmp",
            "javax.servlet.http");

    private static final List<Set<String>> CANDIDATE_SETS = List.of(
            emptySet(),
            Set.of(""),
            Set.of("java"),
            Set.of("java.io"),
            Set.of("org.apache.struts2"),
            Set.of("a"),
            Set.of("a.b"),
            Set.of("zzz.not.matching"),
            Set.of("java.io", "org.apache.struts2", "javax"));

    private static List<Class<?>> classShapes() throws Exception {
        return List.of(
                String.class,
                Map.Entry.class,
                SecurityMemberAccess.class,
                Class.forName("PackagelessAction"),
                int.class,
                void.class,
                int[].class,
                String[].class,
                String[][].class,
                ((Runnable) () -> {
                }).getClass(),
                Proxy.newProxyInstance(
                        SecurityMemberAccessPackageMatchingTest.class.getClassLoader(),
                        new Class<?>[]{Runnable.class},
                        (proxy, method, args) -> null).getClass());
    }

    @Test
    public void siblingPackageWithSharedCharacterPrefixDoesNotMatch() {
        Set<String> excluded = Set.of("org.apache.struts2");

        assertThat(legacyPrefixMatch("org.apache.struts2x", excluded))
                .as("a sibling package sharing a character prefix must not match")
                .isFalse();
        assertThat(legacyPrefixMatch("org.apache.struts2", excluded))
                .as("an exact match must match")
                .isTrue();
        assertThat(legacyPrefixMatch("org.apache.struts2.ognl", excluded))
                .as("a sub-package must match")
                .isTrue();
    }

    @Test
    public void dotOnlyConfigurationYieldsEmptyStringPackageName() {
        assertThat(ConfigParseUtil.toPackageNamesSet("."))
                .as("struts.excludedPackageNames=\".\" strips to the empty string")
                .containsExactly("");
    }

    @Test
    public void defaultPackageMatchesOnlyWhenEmptyStringConfigured() throws Exception {
        Class<?> packageless = Class.forName("PackagelessAction");

        assertThat(toPackageName(packageless)).isEmpty();
        assertThat(isClassBelongsToPackages(packageless, Set.of("")))
                .as("a default-package class is matched by the empty-string entry")
                .isTrue();
        assertThat(isClassBelongsToPackages(packageless, Set.of("java")))
                .as("a default-package class is not matched by an unrelated entry")
                .isFalse();
    }

    @Test
    public void toPackageNameMatchesLegacyAcrossClassShapes() throws Exception {
        for (Class<?> clazz : classShapes()) {
            assertThat(toPackageName(clazz))
                    .as("toPackageName(%s)", clazz.getName())
                    .isEqualTo(legacyToPackageName(clazz));
        }
    }

    @Test
    public void arraysAndPrimitivesResolveToTheEmptyPackage() {
        assertThat(toPackageName(int.class)).isEmpty();
        assertThat(toPackageName(void.class)).isEmpty();
        assertThat(toPackageName(int[].class)).isEmpty();
        assertThat(toPackageName(String[].class)).isEmpty();
        assertThat(toPackageName(String[][].class)).isEmpty();
    }

    @Test
    public void classEntryPointMatchesLegacyAcrossCandidateSets() throws Exception {
        for (Class<?> clazz : classShapes()) {
            for (Set<String> candidates : CANDIDATE_SETS) {
                assertThat(isClassBelongsToPackages(clazz, candidates))
                        .as("clazz=[%s] candidates=%s", clazz.getName(), candidates)
                        .isEqualTo(legacyPrefixMatch(legacyToPackageName(clazz), candidates));
            }
        }
    }
}
```

- [ ] **Step 2: Run the tests — they must all PASS against unmodified production code**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessPackageMatchingTest`

Expected: BUILD SUCCESS, 6 tests run, 0 failures.

This is a characterization suite, so passing immediately is correct. **If anything fails, stop.** It means the spec's description of current behavior is wrong — re-derive the semantics before changing production code. Do not edit the assertions to make them green.

- [ ] **Step 3: Commit**

```bash
git add core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessPackageMatchingTest.java
git commit -m "WW-5674 test(ognl): characterise SecurityMemberAccess package matching

Pins the current behaviour of isClassBelongsToPackages and toPackageName
before the WW-5674 rewrite, including the default-package empty-string edge
reachable via struts.excludedPackageNames=\".\" and the package-boundary case
where org.apache.struts2x must not match org.apache.struts2."
```

---

### Task 2: Make `toPackageName` use the cached `getPackageName()`

Swap the classloader package-map lookup for the value cached on the `Class`, behind a guard covering exactly the cases where `getPackage()` returns null.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java:374-379`
- Test: `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessPackageMatchingTest.java` (no changes — Task 1's `toPackageNameMatchesLegacyAcrossClassShapes` and `arraysAndPrimitivesResolveToTheEmptyPackage` are the gate)

**Interfaces:**
- Consumes: `legacyToPackageName(Class<?>)` and `classShapes()` from Task 1.
- Produces: `SecurityMemberAccess.toPackageName(Class<?>)` — unchanged signature `public static String`, unchanged results.

- [ ] **Step 1: Replace the method body**

Replace lines 374–379 of `SecurityMemberAccess.java`:

```java
    public static String toPackageName(Class<?> clazz) {
        if (clazz.getPackage() == null) {
            return "";
        }
        return clazz.getPackage().getName();
    }
```

with:

```java
    public static String toPackageName(Class<?> clazz) {
        // Class.getPackage() resolves through the defining classloader's package map on every
        // call, whereas getPackageName() is computed once and cached on the Class. getPackage()
        // returns null for exactly arrays, primitives and void, so the guard reproduces the
        // previous result for every input. Note that void.class.isPrimitive() is true.
        // Arrays deliberately keep the empty package here: getPackageName() would resolve them
        // to the element type's package, which would loosen the allowlist. See WW-5674.
        if (clazz.isArray() || clazz.isPrimitive()) {
            return "";
        }
        return clazz.getPackageName();
    }
```

- [ ] **Step 2: Run the tests to verify behavior is unchanged**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessPackageMatchingTest`

Expected: PASS, 6 tests, 0 failures. `toPackageNameMatchesLegacyAcrossClassShapes` compares the new implementation against the retained legacy oracle across all eleven class shapes, so a regression here fails loudly.

- [ ] **Step 3: Run the existing SecurityMemberAccess suite**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessTest`

Expected: PASS, 0 failures, with no edits to that file.

- [ ] **Step 4: Commit**

```bash
git add core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java
git commit -m "WW-5674 perf(ognl): resolve package names via cached Class.getPackageName

getPackage() performs a classloader package-map lookup on every call; the name
returned by getPackageName() is computed once and cached on the Class. The
isArray()/isPrimitive() guard covers exactly the inputs for which getPackage()
returns null, so results are unchanged for every class shape."
```

---

### Task 3: Replace the prefix construction with an index walk

Extract the walk into a package-private pure function over the package-name string, and delegate the existing public method to it.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java:389-394` (method body), `:35` and `:39` (imports)
- Modify: `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessPackageMatchingTest.java` (add one test)

**Interfaces:**
- Consumes: `toPackageName(Class<?>)` from Task 2; `legacyPrefixMatch(String, Set<String>)`, `PACKAGE_NAMES`, `CANDIDATE_SETS` from Task 1.
- Produces: `static boolean SecurityMemberAccess.isPackageBelongsToPackages(String packageName, Set<String> first, Set<String> second)` — package-private, pure, no allocation beyond one substring per package level. Consumed by Task 4.

- [ ] **Step 1: Write the failing test**

Add to `SecurityMemberAccessPackageMatchingTest`:

```java
    @Test
    public void indexWalkMatchesLegacyAcrossPackageNameShapes() {
        for (String packageName : PACKAGE_NAMES) {
            for (Set<String> candidates : CANDIDATE_SETS) {
                assertThat(SecurityMemberAccess.isPackageBelongsToPackages(packageName, candidates, emptySet()))
                        .as("packageName=[%s] candidates=%s", packageName, candidates)
                        .isEqualTo(legacyPrefixMatch(packageName, candidates));
            }
        }
    }

    @Test
    public void bothSetsEmptyShortCircuitsToFalse() {
        for (String packageName : PACKAGE_NAMES) {
            assertThat(SecurityMemberAccess.isPackageBelongsToPackages(packageName, emptySet(), emptySet()))
                    .as("packageName=[%s] with no configured packages", packageName)
                    .isFalse();
        }
    }
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessPackageMatchingTest`

Expected: COMPILATION FAILURE — `cannot find symbol: method isPackageBelongsToPackages(String,Set<String>,Set<String>)`. That is the red state for this task.

- [ ] **Step 3: Write the implementation**

Replace lines 389–394 of `SecurityMemberAccess.java`:

```java
    public static boolean isClassBelongsToPackages(Class<?> clazz, Set<String> matchingPackages) {
        List<String> packageParts = List.of(toPackageName(clazz).split("\\."));
        return IntStream.range(0, packageParts.size())
                .mapToObj(i -> String.join(".", packageParts.subList(0, i + 1)))
                .anyMatch(matchingPackages::contains);
    }
```

with:

```java
    public static boolean isClassBelongsToPackages(Class<?> clazz, Set<String> matchingPackages) {
        return isPackageBelongsToPackages(toPackageName(clazz), matchingPackages, emptySet());
    }

    /**
     * Tests whether the given package name, or any of its parent packages, is present in either
     * set. Walks the name in place rather than building the full prefix list, since this runs on
     * the OGNL member-access path. Shortest prefix first, so broad entries such as {@code java.io}
     * short-circuit earliest.
     *
     * @param packageName the package name to test, empty for the default package
     * @param first       the first set of package names to match against
     * @param second      the second set of package names to match against
     * @return {@code true} if the package or any parent package is in either set
     */
    static boolean isPackageBelongsToPackages(String packageName, Set<String> first, Set<String> second) {
        if (first.isEmpty() && second.isEmpty()) {
            return false;
        }
        int idx = packageName.indexOf('.');
        while (idx != -1) {
            String prefix = packageName.substring(0, idx);
            if (first.contains(prefix) || second.contains(prefix)) {
                return true;
            }
            idx = packageName.indexOf('.', idx + 1);
        }
        return first.contains(packageName) || second.contains(packageName);
    }
```

- [ ] **Step 4: Remove the now-unused imports**

Delete line 35 (`import java.util.List;`) and line 39 (`import java.util.stream.IntStream;`) from `SecurityMemberAccess.java`. Both are used only by the code just replaced — verify with:

```bash
grep -n '\bList\b\|\bIntStream\b' core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java
```

Expected after deletion: no output. `emptySet` is already statically imported at line 42 and is now used by the delegate; leave it.

- [ ] **Step 5: Run tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessPackageMatchingTest`

Expected: PASS, 8 tests, 0 failures.

- [ ] **Step 6: Run the existing SecurityMemberAccess suite**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessTest`

Expected: PASS, 0 failures, still with no edits to that file.

- [ ] **Step 7: Commit**

```bash
git add core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java \
        core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessPackageMatchingTest.java
git commit -m "WW-5674 perf(ognl): walk package names in place instead of building prefixes

Replaces the split/IntStream/String.join prefix construction with an index walk,
extracted into a pure package-private helper so it can be tested against package
name shapes no real Class can produce. Per call this drops a String[], a list
wrapper, a stream pipeline, N sublist views and N joined strings, leaving one
substring per package level.

Equivalence with the replaced implementation is asserted over a matrix of
package name shapes and candidate sets."
```

---

### Task 4: Collapse the allowlist path to a single walk

`isClassAllowlisted` walks the same package name twice, once per allowlist set. Add a two-set overload and use it.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java:254-261` (`isClassAllowlisted`) and the `isClassBelongsToPackages` block from Task 3
- Modify: `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessPackageMatchingTest.java` (add one test)

**Interfaces:**
- Consumes: `isPackageBelongsToPackages(String, Set<String>, Set<String>)` from Task 3.
- Produces: `public static boolean SecurityMemberAccess.isClassBelongsToPackages(Class<?> clazz, Set<String> first, Set<String> second)`.

- [ ] **Step 1: Write the failing test**

Add to `SecurityMemberAccessPackageMatchingTest`:

```java
    @Test
    public void twoSetOverloadEqualsDisjunctionOfSingleSetCalls() throws Exception {
        for (Class<?> clazz : classShapes()) {
            for (Set<String> first : CANDIDATE_SETS) {
                for (Set<String> second : CANDIDATE_SETS) {
                    assertThat(isClassBelongsToPackages(clazz, first, second))
                            .as("clazz=[%s] first=%s second=%s", clazz.getName(), first, second)
                            .isEqualTo(isClassBelongsToPackages(clazz, first)
                                    || isClassBelongsToPackages(clazz, second));
                }
            }
        }
    }
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessPackageMatchingTest`

Expected: COMPILATION FAILURE — `cannot find symbol: method isClassBelongsToPackages(Class<CAP#1>,Set<String>,Set<String>)`. That is the red state for this task.

- [ ] **Step 3: Add the overload**

In `SecurityMemberAccess.java`, replace the two-argument method written in Task 3:

```java
    public static boolean isClassBelongsToPackages(Class<?> clazz, Set<String> matchingPackages) {
        return isPackageBelongsToPackages(toPackageName(clazz), matchingPackages, emptySet());
    }
```

with the delegating pair:

```java
    public static boolean isClassBelongsToPackages(Class<?> clazz, Set<String> matchingPackages) {
        return isClassBelongsToPackages(clazz, matchingPackages, emptySet());
    }

    /**
     * Tests the class's package against two sets in a single walk. Equivalent to calling
     * {@link #isClassBelongsToPackages(Class, Set)} once per set and OR-ing the results, but
     * walks the package name only once.
     *
     * @param clazz  the class whose package is tested
     * @param first  the first set of package names to match against
     * @param second the second set of package names to match against
     * @return {@code true} if the class's package or any parent package is in either set
     */
    public static boolean isClassBelongsToPackages(Class<?> clazz, Set<String> first, Set<String> second) {
        return isPackageBelongsToPackages(toPackageName(clazz), first, second);
    }
```

- [ ] **Step 4: Use the overload in `isClassAllowlisted`**

In `SecurityMemberAccess.java`, replace the final two clauses of `isClassAllowlisted` (lines 259–260):

```java
                || isClassBelongsToPackages(clazz, ALLOWLIST_REQUIRED_PACKAGES)
                || isClassBelongsToPackages(clazz, allowlistPackageNames);
```

with a single clause:

```java
                || isClassBelongsToPackages(clazz, ALLOWLIST_REQUIRED_PACKAGES, allowlistPackageNames);
```

The full method then reads:

```java
    protected boolean isClassAllowlisted(Class<?> clazz) {
        return allowlistClasses.contains(clazz)
                || ALLOWLIST_REQUIRED_CLASSES.contains(clazz)
                || (providerAllowlist != null && providerAllowlist.getProviderAllowlist().contains(clazz))
                || (threadAllowlist != null && threadAllowlist.getAllowlist().contains(clazz))
                || isClassBelongsToPackages(clazz, ALLOWLIST_REQUIRED_PACKAGES, allowlistPackageNames);
    }
```

- [ ] **Step 5: Run tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessPackageMatchingTest`

Expected: PASS, 9 tests, 0 failures.

- [ ] **Step 6: Run the full core suite**

Run: `mvn test -DskipAssembly -pl core`

Expected: BUILD SUCCESS, 0 failures, 0 errors. This is the real gate — `SecurityMemberAccessTest`, `OgnlValueStackTest`, `OgnlUtilTest` and the allowlist tests all exercise these paths end to end. Confirm `SecurityMemberAccessTest.java` is still unmodified:

```bash
git status --porcelain core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessTest.java
```

Expected: no output.

- [ ] **Step 7: Commit**

```bash
git add core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java \
        core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessPackageMatchingTest.java
git commit -m "WW-5674 perf(ognl): match both allowlist package sets in one walk

isClassAllowlisted walked the class's package name twice, once for
ALLOWLIST_REQUIRED_PACKAGES and once for the configured allowlist. A two-set
overload probes both sets at each prefix, halving the work on a path that runs
for every OGNL member access.

Asserted equivalent to OR-ing the two single-set calls across a matrix of class
shapes and candidate sets."
```

---

## Self-Review

**Spec coverage.** Every requirement maps to a task:

| Spec requirement | Task |
|---|---|
| `toPackageName` guard + `getPackageName()` | Task 2 |
| Extract `isPackageBelongsToPackages` (package-private) | Task 3 |
| Index walk, shortest-prefix-first, `isEmpty()` short-circuit | Task 3 |
| Two public entry points delegate to one walk | Tasks 3, 4 |
| Single walk in `isClassAllowlisted` | Task 4 |
| Import cleanup (`List`, `IntStream`) | Task 3, Step 4 |
| Test 1 — differential vs legacy over shapes | Task 3, `indexWalkMatchesLegacyAcrossPackageNameShapes` |
| Test 2 — package-boundary regression | Task 1, `siblingPackageWithSharedCharacterPrefixDoesNotMatch` |
| Test 3 — default-package `""` edge | Task 1, `defaultPackageMatchesOnlyWhenEmptyStringConfigured` + `dotOnlyConfigurationYieldsEmptyStringPackageName` |
| Test 4 — `toPackageName` over 11 class shapes | Task 1, `toPackageNameMatchesLegacyAcrossClassShapes` |
| Test 5 — two-set overload equivalence | Task 4, `twoSetOverloadEqualsDisjunctionOfSingleSetCalls` |
| Test 6 — existing suite unchanged, full core green | Tasks 2, 3, 4 (Step 6) |
| No JMH, no timing assertions | Global Constraints |
| Array/primitive semantics unchanged | Task 2 comment + `arraysAndPrimitivesResolveToTheEmptyPackage` |

One spec deviation, flagged in *File Structure*: tests live in a new `SecurityMemberAccessPackageMatchingTest` rather than the 1136-line `SecurityMemberAccessTest`.

**Placeholder scan.** No TBD/TODO, no "handle edge cases", no "similar to Task N". Every code step carries complete, compilable content.

**Type consistency.** `isPackageBelongsToPackages(String, Set<String>, Set<String>)` is package-private and named identically in Tasks 3 and 4. `isClassBelongsToPackages` keeps its two-argument signature throughout and gains a three-argument overload in Task 4 only. `legacyPrefixMatch(String, Set<String>)`, `legacyToPackageName(Class<?>)`, `classShapes()`, `PACKAGE_NAMES` and `CANDIDATE_SETS` are defined once in Task 1 and referenced under those exact names in Tasks 3 and 4. `classShapes()` throws `Exception` (via `Class.forName`), so every test using it declares `throws Exception` — checked in Tasks 1 and 4.

## Follow-ups not in scope

Neither is filed; file before referencing either from code, per the project's no-placeholder-TODO rule.

- **WW-5675** is already filed and covers the dominant cost (config re-parsing driven by the `Scope.PROTOTYPE` bean). WW-5674 alone will not move the 9% figure much.
- **Array/primitive package semantics** — whether `getPackageName()` semantics should be adopted for arrays. Tightens the exclusion list, loosens the allowlist. Needs its own security reasoning.
- **`ConfigParseUtil.validatePackageNames`** (`ConfigParseUtil.java:143`) evaluates `Pattern.compile("\\s")` once per package name rather than once overall. One-line fix, belongs with WW-5675.
