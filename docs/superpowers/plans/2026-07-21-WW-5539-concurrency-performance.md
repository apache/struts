# WW-5539 Concurrency Performance Enhancements Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove three coarse locks from Struts core's type-conversion and validation caches, replacing them with concurrent collections so that read-mostly cache hits no longer serialise.

**Architecture:** Make the backing caches genuinely thread-safe (`ConcurrentHashMap` / `ConcurrentHashMap.newKeySet()`), then delete the `synchronized` blocks that were compensating for them. One new `default` SPI method, `TypeConverterHolder.computeMappingIfAbsent`, provides atomic build-once semantics for the single expensive computation where `computeIfAbsent` is provably safe. Everywhere else, check-then-act is retained and the duplicated work is cheap and idempotent.

**Tech Stack:** Java 17 (`maven.compiler.release=17`), Maven multi-module, JUnit 3-style tests via `junit.framework.TestCase` (through `org.apache.struts2.XWorkTestCase`), AssertJ assertions, Log4j2.

**Spec:** `docs/superpowers/specs/2026-07-21-WW-5539-concurrency-performance-design.md`

## Global Constraints

- **Branch:** all work lands on `WW-5539`. Never commit to `main`.
- **Commit messages:** must be prefixed `WW-5539 `.
- **Target release:** 7.3.0 (minor) — the `TypeConverterHolder` SPI may gain `default` methods but must not break existing third-party implementations.
- **Test style:** core tests extend `org.apache.struts2.XWorkTestCase`, which extends `junit.framework.TestCase`. Test methods are `public void testXxx()` with **no** `@Test` annotation. Setup is `@Override protected void setUp() throws Exception { super.setUp(); ... }`. Do **not** convert these to JUnit 5.
- **Available test fields:** `XWorkTestCase` provides `protected Container container` and `protected ConfigurationManager configurationManager`.
- **`TypeConverter` is a functional interface** (single abstract method `convertValue(Map, Object, Member, String, Object, Class)`), so test stubs may be lambdas.
- **Build command:** `mvn test -DskipAssembly -pl core -Dtest=ClassName#methodName`
- **No binary-compatibility gate** (no japicmp/revapi) and **deprecation warnings do not fail the build** — verified in `pom.xml`.
- **Exactly three methods get `@Deprecated`:** `TypeConverterHolder.getMapping`, `addMapping`, `containsNoMapping`. Do not deprecate `addNoMapping` or any of the five default-mapping methods.

---

### Task 1: Make `StrutsTypeConverterHolder` thread-safe

This is the correctness fix. The holder is a container singleton whose plain `HashMap`s are read without any lock by `XWorkConverter.lookup()` while being written elsewhere.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/conversion/StrutsTypeConverterHolder.java:21-77`
- Test: `core/src/test/java/org/apache/struts2/conversion/StrutsTypeConverterHolderTest.java` (create)

**Interfaces:**
- Consumes: nothing from earlier tasks.
- Produces: `StrutsTypeConverterHolder` with thread-safe internals and a `public StrutsTypeConverterHolder()` no-arg constructor (already implicit). Field `protected final Set<String> unknownMappings`. Behaviour change: `addDefaultMapping(className, null)` is now ignored rather than storing a null value.

- [ ] **Step 1: Write the failing concurrency test**

Create `core/src/test/java/org/apache/struts2/conversion/StrutsTypeConverterHolderTest.java`:

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
package org.apache.struts2.conversion;

import org.apache.struts2.XWorkTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class StrutsTypeConverterHolderTest extends XWorkTestCase {

    private static final int THREADS = 16;
    private static final int PER_THREAD = 200;

    private static TypeConverter stubConverter() {
        return (context, target, member, propertyName, value, toType) -> null;
    }

    public void testConcurrentDefaultMappingRegistrationLosesNothing() throws Exception {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        for (int t = 0; t < THREADS; t++) {
            final int threadId = t;
            futures.add(pool.submit(() -> {
                start.await();
                for (int i = 0; i < PER_THREAD; i++) {
                    String className = "stub.Class" + threadId + "_" + i;
                    holder.addDefaultMapping(className, stubConverter());
                    // interleave reads with writes to provoke the race
                    holder.containsDefaultMapping("stub.Class0_0");
                    holder.getDefaultMapping(className);
                }
                return null;
            }));
        }

        start.countDown();
        for (Future<?> future : futures) {
            future.get(60, TimeUnit.SECONDS);
        }
        pool.shutdown();

        for (int t = 0; t < THREADS; t++) {
            for (int i = 0; i < PER_THREAD; i++) {
                String className = "stub.Class" + t + "_" + i;
                assertThat(holder.getDefaultMapping(className))
                        .as("lost registration for %s", className)
                        .isNotNull();
            }
        }
    }

    public void testConcurrentNoMappingAndUnknownMappingRegistrationLosesNothing() throws Exception {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> futures = new ArrayList<>();

        for (int t = 0; t < THREADS; t++) {
            final int threadId = t;
            futures.add(pool.submit(() -> {
                start.await();
                for (int i = 0; i < PER_THREAD; i++) {
                    holder.addUnknownMapping("stub.Unknown" + threadId + "_" + i);
                    holder.containsUnknownMapping("stub.Unknown0_0");
                }
                return null;
            }));
        }

        start.countDown();
        for (Future<?> future : futures) {
            future.get(60, TimeUnit.SECONDS);
        }
        pool.shutdown();

        for (int t = 0; t < THREADS; t++) {
            for (int i = 0; i < PER_THREAD; i++) {
                String className = "stub.Unknown" + t + "_" + i;
                assertThat(holder.containsUnknownMapping(className))
                        .as("lost unknown mapping for %s", className)
                        .isTrue();
            }
        }
    }

    public void testAddDefaultMappingIgnoresNullConverter() {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();

        holder.addDefaultMapping("stub.NullConverter", null);

        assertThat(holder.containsDefaultMapping("stub.NullConverter")).isFalse();
        assertThat(holder.getDefaultMapping("stub.NullConverter")).isNull();
    }

    public void testAddDefaultMappingClearsUnknownMapping() {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();

        holder.addUnknownMapping("stub.Later");
        assertThat(holder.containsUnknownMapping("stub.Later")).isTrue();

        holder.addDefaultMapping("stub.Later", stubConverter());

        assertThat(holder.containsUnknownMapping("stub.Later")).isFalse();
        assertThat(holder.getDefaultMapping("stub.Later")).isNotNull();
    }
}
```

- [ ] **Step 2: Run the tests to verify they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsTypeConverterHolderTest`

Expected: `testAddDefaultMappingIgnoresNullConverter` FAILS (currently a null value is stored, so `containsDefaultMapping` returns `true`).

The two concurrency tests are **expected to be flaky against the unfixed code** — they may pass on a strongly-ordered x86/ARM machine. Record whatever happens; do not treat a pass as evidence the current code is correct. The null-converter test is the deterministic gate for this step.

- [ ] **Step 3: Make the collections concurrent**

In `StrutsTypeConverterHolder.java`, replace the imports at lines 21-23:

```java
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
```

Replace the four field declarations (lines 37, 58, 63, 71) with:

```java
    private final Map<String, TypeConverter> defaultMappings = new ConcurrentHashMap<>();  // non-action (eg. returned value)
```

```java
    private final Map<Class, Map<String, Object>> mappings = new ConcurrentHashMap<>(); // action
```

```java
    private final Set<Class> noMapping = ConcurrentHashMap.newKeySet(); // action
```

```java
    /**
     * Record classes that doesn't have conversion mapping defined.
     * <pre>
     * - String -&gt; classname as String
     * </pre>
     *
     * @deprecated since 7.3.0, this field is an implementation detail and will be made private.
     */
    @Deprecated
    protected final Set<String> unknownMappings = ConcurrentHashMap.newKeySet();     // non-action (eg. returned value)
```

Keep the existing Javadoc comments above `defaultMappings`, `mappings` and `noMapping` unchanged.

- [ ] **Step 4: Add the null-converter guard**

`ConcurrentHashMap` forbids null values, so `addDefaultMapping` would now throw `NullPointerException` where it previously stored a null. A null converter was never useful — it left `containsDefaultMapping` returning `true` while `getDefaultMapping` returned `null`. `ConverterFactory` is a pluggable SPI, so a third-party implementation returning null is reachable. Skip it explicitly instead.

Add the logger imports to `StrutsTypeConverterHolder.java`:

```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
```

Add the field just inside the class body, before `defaultMappings`:

```java
    private static final Logger LOG = LogManager.getLogger(StrutsTypeConverterHolder.class);
```

Replace `addDefaultMapping` (lines 73-77) with:

```java
    @Override
    public void addDefaultMapping(String className, TypeConverter typeConverter) {
        if (typeConverter == null) {
            LOG.warn("Ignoring null TypeConverter registered for class [{}]", className);
            return;
        }
        defaultMappings.put(className, typeConverter);
        unknownMappings.remove(className);
    }
```

- [ ] **Step 5: Run the tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsTypeConverterHolderTest`

Expected: PASS, all four tests.

- [ ] **Step 6: Run the surrounding regression suites**

Run: `mvn test -DskipAssembly -pl core -Dtest='XWorkConverterTest,AnnotationXWorkConverterTest,StrutsConversionPropertiesProcessorTest,ConfigurationManagerTest'`

Expected: PASS, no edits to those files.

- [ ] **Step 7: Commit**

```bash
git add core/src/main/java/org/apache/struts2/conversion/StrutsTypeConverterHolder.java \
        core/src/test/java/org/apache/struts2/conversion/StrutsTypeConverterHolderTest.java
git commit -m "WW-5539 Make StrutsTypeConverterHolder collections concurrent

The holder is a container singleton whose HashMaps were read without any
lock by XWorkConverter.lookup() while being written elsewhere, risking
lost updates and torn reads during resize.

Null TypeConverters are now ignored with a warning rather than stored,
since ConcurrentHashMap forbids null values and a null converter left the
holder in an inconsistent state."
```

---

### Task 2: Add `computeMappingIfAbsent` to the `TypeConverterHolder` SPI

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/conversion/TypeConverterHolder.java:19-100`
- Modify: `core/src/main/java/org/apache/struts2/conversion/StrutsTypeConverterHolder.java`
- Test: `core/src/test/java/org/apache/struts2/conversion/StrutsTypeConverterHolderTest.java` (modify)

**Interfaces:**
- Consumes: `StrutsTypeConverterHolder` with concurrent internals (Task 1).
- Produces: `Map<String, Object> TypeConverterHolder.computeMappingIfAbsent(Class clazz, Function<Class, Map<String, Object>> builder)` — never returns `null`; returns `Collections.emptyMap()` when the class has no mapping. `getMapping`, `addMapping`, `containsNoMapping` are now `@Deprecated`.

- [ ] **Step 1: Write the failing tests**

Append to `StrutsTypeConverterHolderTest.java` (and add these imports):

```java
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
```

```java
    public void testComputeMappingIfAbsentBuildsOnceAndCaches() {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        AtomicInteger builds = new AtomicInteger();

        Map<String, Object> first = holder.computeMappingIfAbsent(String.class, clazz -> {
            builds.incrementAndGet();
            Map<String, Object> built = new HashMap<>();
            built.put("someProperty", "someConverter");
            return built;
        });
        Map<String, Object> second = holder.computeMappingIfAbsent(String.class, clazz -> {
            builds.incrementAndGet();
            return new HashMap<>();
        });

        assertThat(builds.get()).isEqualTo(1);
        assertThat(first).containsEntry("someProperty", "someConverter");
        assertThat(second).isSameAs(first);
    }

    public void testComputeMappingIfAbsentNegativeCachesEmptyResult() {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        AtomicInteger builds = new AtomicInteger();

        Map<String, Object> first = holder.computeMappingIfAbsent(String.class, clazz -> {
            builds.incrementAndGet();
            return Collections.emptyMap();
        });
        Map<String, Object> second = holder.computeMappingIfAbsent(String.class, clazz -> {
            builds.incrementAndGet();
            return Collections.emptyMap();
        });

        assertThat(first).isEmpty();
        assertThat(second).isEmpty();
        assertThat(builds.get()).as("empty result must be negative cached").isEqualTo(1);
        assertThat(holder.containsNoMapping(String.class)).isTrue();
    }

    public void testComputeMappingIfAbsentNegativeCachesNullResult() {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();

        Map<String, Object> result = holder.computeMappingIfAbsent(String.class, clazz -> null);

        assertThat(result).isNotNull().isEmpty();
        assertThat(holder.containsNoMapping(String.class)).isTrue();
    }

    public void testComputeMappingIfAbsentShortCircuitsOnKnownNoMapping() {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        holder.addNoMapping(String.class);

        Map<String, Object> result = holder.computeMappingIfAbsent(String.class, clazz -> {
            throw new AssertionError("builder must not run for a negative-cached class");
        });

        assertThat(result).isNotNull().isEmpty();
    }

    public void testComputeMappingIfAbsentBuildsOnceUnderConcurrency() throws Exception {
        StrutsTypeConverterHolder holder = new StrutsTypeConverterHolder();
        AtomicInteger builds = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Map<String, Object>>> futures = new ArrayList<>();

        for (int t = 0; t < THREADS; t++) {
            futures.add(pool.submit(() -> {
                start.await();
                return holder.computeMappingIfAbsent(String.class, clazz -> {
                    builds.incrementAndGet();
                    Map<String, Object> built = new HashMap<>();
                    built.put("someProperty", "someConverter");
                    return built;
                });
            }));
        }

        start.countDown();
        Map<String, Object> expected = futures.get(0).get(60, TimeUnit.SECONDS);
        for (Future<Map<String, Object>> future : futures) {
            assertThat(future.get(60, TimeUnit.SECONDS)).isSameAs(expected);
        }
        pool.shutdown();

        assertThat(builds.get()).as("mapping must be built exactly once").isEqualTo(1);
    }
```

- [ ] **Step 2: Run the tests to verify they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsTypeConverterHolderTest`

Expected: COMPILATION FAILURE — `cannot find symbol: method computeMappingIfAbsent`.

- [ ] **Step 3: Add the `default` method to the SPI**

In `TypeConverterHolder.java`, add imports:

```java
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
```

Add `@Deprecated` to exactly three existing methods, keeping their existing Javadoc and adding a `@deprecated` tag to each:

```java
    /**
     * Target class conversion Mappings.
     *
     * @param clazz class to convert to/from
     * @return {@link TypeConverter} for given class
     * @deprecated since 7.3.0, use {@link #computeMappingIfAbsent(Class, Function)} which resolves
     * and caches the mapping atomically instead of requiring a check-then-act at the call site.
     */
    @Deprecated
    Map<String, Object> getMapping(Class clazz);

    /**
     * Assign mapping of converters for given class
     *
     * @param clazz   class to convert to/from
     * @param mapping property converters
     * @deprecated since 7.3.0, use {@link #computeMappingIfAbsent(Class, Function)} which stores
     * the built mapping itself.
     */
    @Deprecated
    void addMapping(Class clazz, Map<String, Object> mapping);

    /**
     * Check if there is no mapping for given class to convert
     *
     * @param clazz class to convert to/from
     * @return true if mapping couldn't be found
     * @deprecated since 7.3.0, use {@link #computeMappingIfAbsent(Class, Function)} which returns
     * an empty map for classes known to have no mapping.
     */
    @Deprecated
    boolean containsNoMapping(Class clazz);
```

Leave `addNoMapping`, `addDefaultMapping`, `containsDefaultMapping`, `getDefaultMapping`, `containsUnknownMapping` and `addUnknownMapping` **undeprecated**.

Add the new method at the end of the interface, before the closing brace:

```java
    /**
     * Returns the property-converter mapping for the given class, building and caching it on first
     * use. Never returns {@code null}: a class known to have no mapping yields
     * {@link Collections#emptyMap()}.
     *
     * <p>If the builder returns {@code null} or an empty map, the class is recorded in the negative
     * cache so the builder is not invoked for it again.</p>
     *
     * <p>Implementations are expected to make this atomic so that the builder runs at most once per
     * class. The default implementation is a non-atomic check-then-act using the deprecated
     * primitives, preserving pre-7.3.0 behaviour for third-party holders that do not override it.</p>
     *
     * @param clazz   class to convert to/from
     * @param builder builds the property-converter mapping for the class when it is not yet cached
     * @return the mapping for the class, or an empty map if it has none
     * @since 7.3.0
     */
    @SuppressWarnings("deprecation")
    default Map<String, Object> computeMappingIfAbsent(Class clazz, Function<Class, Map<String, Object>> builder) {
        if (containsNoMapping(clazz)) {
            return Collections.emptyMap();
        }
        Map<String, Object> mapping = getMapping(clazz);
        if (mapping != null) {
            return mapping;
        }
        mapping = builder.apply(clazz);
        if (mapping == null || mapping.isEmpty()) {
            addNoMapping(clazz);
            return Collections.emptyMap();
        }
        addMapping(clazz, mapping);
        return mapping;
    }
```

The `@SuppressWarnings("deprecation")` is deliberate: the fallback path must keep using the old primitives, because those are the only methods a third-party implementation is guaranteed to provide.

- [ ] **Step 4: Override it in `StrutsTypeConverterHolder`**

Add imports to `StrutsTypeConverterHolder.java`:

```java
import java.util.Collections;
import java.util.function.Function;
```

Add the override after `getMapping`, and mark the three implementing methods `@Deprecated` to match the interface:

```java
    @Override
    public Map<String, Object> computeMappingIfAbsent(Class clazz, Function<Class, Map<String, Object>> builder) {
        if (noMapping.contains(clazz)) {
            return Collections.emptyMap();
        }
        Map<String, Object> mapping = mappings.computeIfAbsent(clazz, c -> {
            Map<String, Object> built = builder.apply(c);
            return (built == null || built.isEmpty()) ? null : built;
        });
        if (mapping == null) {
            noMapping.add(clazz);
            return Collections.emptyMap();
        }
        return mapping;
    }
```

Returning `null` from a `computeIfAbsent` mapping function stores nothing and yields `null`, so the negative case falls out naturally and `getMapping()` keeps its "null when absent" meaning.

Add `@Deprecated` to the `getMapping`, `addMapping` and `containsNoMapping` overrides in this class so they do not emit "overrides deprecated method" noise.

- [ ] **Step 5: Run the tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsTypeConverterHolderTest`

Expected: PASS, all nine tests.

- [ ] **Step 6: Commit**

```bash
git add core/src/main/java/org/apache/struts2/conversion/TypeConverterHolder.java \
        core/src/main/java/org/apache/struts2/conversion/StrutsTypeConverterHolder.java \
        core/src/test/java/org/apache/struts2/conversion/StrutsTypeConverterHolderTest.java
git commit -m "WW-5539 Add TypeConverterHolder#computeMappingIfAbsent

Adds an atomic build-once-and-cache operation so callers no longer need
check-then-act around the class mapping cache, and deprecates the three
primitives it subsumes: getMapping, addMapping and containsNoMapping.

The method is a default method delegating to those primitives, so
third-party TypeConverterHolder implementations keep working unchanged."
```

---

### Task 3: Remove the locks from `XWorkConverter`

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/conversion/impl/XWorkConverter.java:414-443` (`getConverter`), `:466-472` (`registerConverter`, `registerConverterNotFound`), `:551-578` (`buildConverterMapping`)
- Test: `core/src/test/java/org/apache/struts2/conversion/impl/XWorkConverterTest.java` (modify)

**Interfaces:**
- Consumes: `TypeConverterHolder.computeMappingIfAbsent(Class, Function)` (Task 2).
- Produces: `protected Map<String, Object> buildConverterMapping(Class clazz) throws Exception` — **no longer stores** its result in the holder; it only builds and returns. `getConverter`, `registerConverter` and `registerConverterNotFound` are no longer synchronized.

- [ ] **Step 1: Write the failing test**

Append to `XWorkConverterTest.java`. Add these imports:

```java
import org.apache.struts2.conversion.TypeConverterHolder;
import org.apache.struts2.conversion.StrutsTypeConverterHolder;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
```

Add this static nested class inside `XWorkConverterTest` (it is in package `org.apache.struts2.conversion.impl`, so it can call the protected `XWorkConverter()` constructor):

```java
    public static class CountingXWorkConverter extends XWorkConverter {
        final AtomicInteger builds = new AtomicInteger();

        public CountingXWorkConverter() {
            super();
        }

        @Override
        protected Map<String, Object> buildConverterMapping(Class clazz) throws Exception {
            builds.incrementAndGet();
            return super.buildConverterMapping(clazz);
        }
    }
```

And these test methods:

```java
    public void testGetConverterBuildsMappingExactlyOncePerClass() throws Exception {
        CountingXWorkConverter countingConverter = container.inject(CountingXWorkConverter.class);
        // a cold, dedicated holder so other tests' cached mappings cannot mask the behaviour
        countingConverter.setTypeConverterHolder(new StrutsTypeConverterHolder());

        Object first = countingConverter.getConverter(User.class, "Collection_list");
        Object second = countingConverter.getConverter(User.class, "Collection_list");

        assertEquals(String.class, first);
        assertEquals(String.class, second);
        assertEquals(1, countingConverter.builds.get());
    }

    public void testGetConverterBuildsMappingExactlyOnceUnderConcurrency() throws Exception {
        final int threads = 16;
        CountingXWorkConverter countingConverter = container.inject(CountingXWorkConverter.class);
        countingConverter.setTypeConverterHolder(new StrutsTypeConverterHolder());

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Object>> futures = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            futures.add(pool.submit(() -> {
                start.await();
                return countingConverter.getConverter(User.class, "Collection_list");
            }));
        }

        start.countDown();
        for (Future<Object> future : futures) {
            assertEquals(String.class, future.get(60, TimeUnit.SECONDS));
        }
        pool.shutdown();

        assertEquals(1, countingConverter.builds.get());
    }

    public void testGetConverterReturnsNullForUnknownProperty() {
        XWorkConverter freshConverter = container.inject(XWorkConverter.class);
        freshConverter.setTypeConverterHolder(new StrutsTypeConverterHolder());

        assertNull(freshConverter.getConverter(User.class, "noSuchPropertyAnywhere"));
    }

    public void testGetConverterReturnsNullForNullProperty() {
        XWorkConverter freshConverter = container.inject(XWorkConverter.class);
        freshConverter.setTypeConverterHolder(new StrutsTypeConverterHolder());

        assertNull(freshConverter.getConverter(User.class, null));
    }
```

`org.apache.struts2.test.User` is already imported by this test class and has a
`User-conversion.properties` declaring `Collection_list = java.lang.String`, so the built
mapping is non-empty and `getConverter` returns `String.class`.

- [ ] **Step 2: Run the tests and record that they PASS**

Run: `mvn test -DskipAssembly -pl core -Dtest=XWorkConverterTest`

Expected: **PASS — all four new tests pass against the unmodified code.**

This is deliberate and is not a mistake in the plan. Unlike Tasks 1 and 2, there is no failing-test-first cycle available here: the existing `synchronized (clazz)` block already guarantees build-once semantics, and `buildConverterMapping` already caches on the cold path. The old code is *correct*; it is only serialised. These tests exist to pin that correctness down **before** the lock is removed, so that Step 6 proves the refactor preserved it.

If any of them FAIL at this step, stop and investigate — the current behaviour differs from what the design assumed, and the design needs revisiting before any lock comes out.

- [ ] **Step 3: Rewrite `getConverter`**

Replace lines 414-443 of `XWorkConverter.java` with:

```java
    protected Object getConverter(Class clazz, String property) {
        LOG.debug("Retrieving converter for class [{}] and property [{}]", clazz, property);

        if (property == null) {
            return null;
        }
        try {
            Map<String, Object> mapping = converterHolder.computeMappingIfAbsent(clazz, this::buildConverterMappingUnchecked);
            mapping = conditionalReload(clazz, mapping);

            Object converter = mapping.get(property);
            if (converter == null && LOG.isDebugEnabled()) {
                LOG.debug("Converter is null for property [{}]. Mapping size [{}]:", property, mapping.size());
                for (Map.Entry<String, Object> entry : mapping.entrySet()) {
                    LOG.debug("{}:{}", entry.getKey(), entry.getValue());
                }
            }
            return converter;
        } catch (Throwable t) {
            LOG.debug("Got exception trying to resolve converter for class [{}] and property [{}]", clazz, property, t);
            converterHolder.addNoMapping(clazz);
            return null;
        }
    }

    /**
     * Adapts {@link #buildConverterMapping(Class)} to {@link java.util.function.Function} by
     * rethrowing its checked exception unchecked. The caller's {@code catch (Throwable)} still
     * negative-caches the class, so behaviour is unchanged.
     */
    private Map<String, Object> buildConverterMappingUnchecked(Class clazz) {
        try {
            return buildConverterMapping(clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Could not build converter mapping for " + clazz, e);
        }
    }
```

The `containsNoMapping` guard is gone because the holder now owns it: a negative-cached class returns an empty map and `mapping.get(property)` yields `null`, the same outcome as before.

- [ ] **Step 4: Stop `buildConverterMapping` from storing**

Replace lines 571-577 of `XWorkConverter.java` — the tail of `buildConverterMapping` — so it only builds and returns:

```java
        return mapping;
    }
```

That is, delete this block entirely:

```java
        if (!mapping.isEmpty()) {
            converterHolder.addMapping(clazz, mapping);
        } else {
            converterHolder.addNoMapping(clazz);
        }
```

Update the method's Javadoc to record the behaviour change, since it is `protected` and visible to subclasses:

```java
     * @param clazz the class to look for converter mappings for
     * @return the converter mappings
     * @throws Exception in case of any errors
     * @since 7.3.0 this method no longer stores the built mapping in the {@link TypeConverterHolder};
     * storage is owned by {@link TypeConverterHolder#computeMappingIfAbsent(Class, java.util.function.Function)}.
```

- [ ] **Step 5: Drop `synchronized` from the register methods**

Replace lines 466-472 with:

```java
    public void registerConverter(String className, TypeConverter converter) {
        converterHolder.addDefaultMapping(className, converter);
    }

    public void registerConverterNotFound(String className) {
        converterHolder.addUnknownMapping(className);
    }
```

Both are now single delegations to a concurrent map. They are `public`, so the modifier change is externally observable — but any caller relying on them for mutual exclusion was relying on a lock that never covered the readers in `lookup()` anyway.

Leave `lookup(String, boolean)` structurally unchanged. Its resolver `lookupSuper()` reads `getDefaultMapping()` recursively while walking the class hierarchy, and a recursive read inside `ConcurrentHashMap.computeIfAbsent` is forbidden — it deadlocks or throws `IllegalStateException: Recursive update`. It simply becomes lock-free.

- [ ] **Step 6: Run the tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=XWorkConverterTest`

Expected: PASS, including the four new tests and all pre-existing ones with no edits to them.

- [ ] **Step 7: Run the wider conversion regression suite**

Run: `mvn test -DskipAssembly -pl core -Dtest='AnnotationXWorkConverterTest,StrutsConversionPropertiesProcessorTest,StrutsTypeConverterHolderTest,ConfigurationManagerTest'`

Expected: PASS.

- [ ] **Step 8: Commit**

```bash
git add core/src/main/java/org/apache/struts2/conversion/impl/XWorkConverter.java \
        core/src/test/java/org/apache/struts2/conversion/impl/XWorkConverterTest.java
git commit -m "WW-5539 Remove coarse locks from XWorkConverter

getConverter() synchronized on the Class object being converted, which is
a globally visible monitor any other library may contend on, and which
serialised every conversion for a given action class including cache
hits. It now delegates to TypeConverterHolder#computeMappingIfAbsent.

registerConverter and registerConverterNotFound drop their synchronized
modifier; they are single delegations to a concurrent map, and the lock
never covered the readers in lookup() in any case.

buildConverterMapping no longer stores its result - storage is owned by
computeMappingIfAbsent."
```

---

### Task 4: Remove the global lock from `DefaultActionValidatorManager`

The expected headline win: today every validated request in the application serialises on this singleton's monitor, and the lock covers per-request `Validator` construction that never needed it.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/validator/DefaultActionValidatorManager.java:44` (import), `:70-71` (caches), `:139-163` (`getValidators`), `:280-350` (`buildValidatorConfigs`, `loadFile`)
- Test: `core/src/test/java/org/apache/struts2/validator/DefaultActionValidatorManagerTest.java` (modify)

**Interfaces:**
- Consumes: nothing from earlier tasks.
- Produces: `getValidators(Class, String, String)` and `getValidators(Class, String)` are no longer synchronized. `protected List<ValidatorConfig> parseValidatorConfigs(URL fileUrl, String fileName)` is new. Cached `List<ValidatorConfig>` values are unmodifiable.

- [ ] **Step 1: Write the failing test**

Append to `DefaultActionValidatorManagerTest.java`. Add these imports:

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
```

```java
    public void testConcurrentGetValidatorsReturnsConsistentResults() throws Exception {
        final int threads = 16;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Integer>> futures = new ArrayList<>();

        int expectedCount = actionValidatorManager.getValidators(SimpleAction.class, alias).size();

        for (int t = 0; t < threads; t++) {
            futures.add(pool.submit(() -> {
                start.await();
                return actionValidatorManager.getValidators(SimpleAction.class, alias).size();
            }));
        }

        start.countDown();
        for (Future<Integer> future : futures) {
            assertThat(future.get(60, TimeUnit.SECONDS)).isEqualTo(expectedCount);
        }
        pool.shutdown();
    }

    public void testCachedValidatorConfigsAreUnmodifiable() {
        actionValidatorManager.getValidators(SimpleAction.class, alias);

        List<ValidatorConfig> cached = actionValidatorManager.validatorCache.values().iterator().next();

        assertThatThrownBy(() -> cached.add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }
```

`assertThat` and `assertThatThrownBy` are already statically imported by this test class, and `alias`, `actionValidatorManager`, `SimpleAction` and `ArrayList`/`List` are already available.

- [ ] **Step 2: Run the tests to verify they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=DefaultActionValidatorManagerTest`

Expected: `testCachedValidatorConfigsAreUnmodifiable` FAILS — the cached list is a plain `ArrayList`, so `add(null)` succeeds and no exception is thrown.

`testConcurrentGetValidatorsReturnsConsistentResults` is expected to PASS at this step; the current code is correct, just serialised. It is a regression guard for the lock removal, not a demonstration of an existing bug.

- [ ] **Step 3: Make the caches concurrent**

In `DefaultActionValidatorManager.java`, delete the static import at line 44:

```java
import static java.util.Collections.synchronizedMap;
```

Add:

```java
import java.util.concurrent.ConcurrentHashMap;
```

Replace lines 70-71:

```java
    protected final Map<String, List<ValidatorConfig>> validatorCache = new ConcurrentHashMap<>();
    protected final Map<String, List<ValidatorConfig>> validatorFileCache = new ConcurrentHashMap<>();
```

The declared field types stay `Map<...>` and stay `protected`, so `AnnotationActionValidatorManager` and any third-party subclass are unaffected.

- [ ] **Step 4: Rewrite `getValidators` without the lock**

Replace lines 139-163 with:

```java
    @Override
    public List<Validator> getValidators(Class<?> clazz, String context, String method) {
        String validatorKey = buildValidatorKey(clazz, context);

        List<ValidatorConfig> configs = validatorCache.get(validatorKey);
        if (configs == null) {
            configs = validatorCache.computeIfAbsent(validatorKey,
                    key -> buildValidatorConfigs(clazz, context, false, null));
        } else if (reloadingConfigs) {
            configs = buildValidatorConfigs(clazz, context, true, null);
            validatorCache.put(validatorKey, configs);
        }

        ValueStack stack = ActionContext.getContext().getValueStack();
        List<Validator> validators = new ArrayList<>();
        for (ValidatorConfig config : configs) {
            if (method == null || method.equals(config.getParams().get("methodName"))) {
                validators.add(getValidatorFromValidatorConfig(config, stack));
            }
        }
        return validators;
    }

    @Override
    public List<Validator> getValidators(Class<?> clazz, String context) {
        return getValidators(clazz, context, null);
    }
```

This mirrors the previous `containsKey` / `else if (reloadingConfigs)` logic exactly, including that a first-ever call builds with `checkFile=false` even in reload mode. The `Validator` construction loop is now outside any lock — it is per-request work on per-request objects.

- [ ] **Step 5: Make `buildValidatorConfigs` return an unmodifiable list**

Replace the final `return validatorConfigs;` at line 317 with:

```java
        return Collections.unmodifiableList(validatorConfigs);
```

`java.util.Collections` is already imported. With `synchronized` gone, several threads now iterate the same cached list concurrently; that is safe only while no cached list is mutated after publication. Wrapping makes it true by construction rather than by accident.

- [ ] **Step 6: Rewrite `loadFile` and extract `parseValidatorConfigs`**

Replace lines 330-350 with:

```java
    protected List<ValidatorConfig> loadFile(String fileName, Class<?> clazz, boolean checkFile) {
        URL fileUrl = ClassLoaderUtil.getResource(fileName, clazz);

        if (checkFile && fileManager.fileNeedsReloading(fileUrl)) {
            List<ValidatorConfig> reloaded = parseValidatorConfigs(fileUrl, fileName);
            validatorFileCache.put(fileName, reloaded);
            return reloaded;
        }

        return validatorFileCache.computeIfAbsent(fileName, key -> parseValidatorConfigs(fileUrl, fileName));
    }

    /**
     * Parses the validator configs from the given file, returning an unmodifiable list. Returns an
     * empty list when the file does not exist or cannot be read.
     *
     * @param fileUrl  URL of the validation config file, may be null
     * @param fileName name of the validation config file, used for logging and parser context
     * @return an unmodifiable list of validator configs, never null
     */
    protected List<ValidatorConfig> parseValidatorConfigs(URL fileUrl, String fileName) {
        List<ValidatorConfig> retList = Collections.emptyList();

        try (InputStream is = fileManager.loadFile(fileUrl)) {
            if (is != null) {
                retList = Collections.unmodifiableList(
                        new ArrayList<>(validatorFileParser.parseActionValidatorConfigs(validatorFactory, is, fileName)));
            }
        } catch (IOException e) {
            LOG.error("Caught exception while closing file {}", fileName, e);
        }

        return retList;
    }
```

This `computeIfAbsent` runs inside the one on `validatorCache` (via `buildValidatorConfigs`). They are different maps and nothing calls back the other direction, so there is no lock-ordering cycle. Empty results are still cached, preserving today's negative caching.

- [ ] **Step 7: Run the tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=DefaultActionValidatorManagerTest`

Expected: PASS, including both new tests and all pre-existing ones with no edits to them.

- [ ] **Step 8: Run the validator regression suite**

Run: `mvn test -DskipAssembly -pl core -Dtest='ActionValidatorManagerTest,AnnotationActionValidatorManagerTest,DefaultActionValidatorManagerTest'`

Expected: PASS. `AnnotationActionValidatorManagerTest` is the important one — it exercises the wildcard/alias key logic and the subclass that overrides `buildClassValidatorConfigs`.

- [ ] **Step 9: Commit**

```bash
git add core/src/main/java/org/apache/struts2/validator/DefaultActionValidatorManager.java \
        core/src/test/java/org/apache/struts2/validator/DefaultActionValidatorManagerTest.java
git commit -m "WW-5539 Remove global lock from DefaultActionValidatorManager

getValidators() was synchronized on the singleton manager, so every
validated request in the application serialised on it - and the lock
covered the per-request Validator construction loop, which operates on
per-request objects and never needed mutual exclusion.

Both caches become ConcurrentHashMap and cached config lists are wrapped
unmodifiable, since several threads now iterate them concurrently."
```

---

### Task 5: Full build, benchmark, and pull request

**Files:**
- Create: `<scratchpad>/ww5539-benchmark/` — throwaway, **not** committed
- Modify: none

**Interfaces:**
- Consumes: all changes from Tasks 1-4.
- Produces: before/after throughput numbers for the PR description.

- [ ] **Step 1: Run the full core test suite**

Run: `mvn test -DskipAssembly -pl core`

Expected: BUILD SUCCESS. If anything fails, fix it before proceeding — a green core suite is the gate for this change, since the failure mode of a bad lock removal is silent and load-dependent rather than a compile error.

- [ ] **Step 2: Run the full multi-module build**

Run: `mvn test -DskipAssembly`

Expected: BUILD SUCCESS. This covers the plugins, which is where a third-party-style `TypeConverterHolder` or validator subclass would surface.

- [ ] **Step 3: Write the throwaway benchmark**

This runs as a **temporary test class** so it inherits the container bootstrap from `XWorkTestCase` — standing up a Struts container by hand is far more work than the benchmark is worth. It is deleted in Step 5 and never committed.

Create `core/src/test/java/org/apache/struts2/validator/Ww5539BenchmarkTest.java`:

```java
package org.apache.struts2.validator;

import org.apache.struts2.SimpleAction;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.conversion.impl.XWorkConverter;
import org.apache.struts2.test.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Ww5539BenchmarkTest extends XWorkTestCase {

    private static final int[] THREAD_COUNTS = {1, 4, 16, 64};
    private static final int WARMUP_ITERATIONS = 20_000;
    private static final long MEASURE_MILLIS = 3_000;

    public void testBenchmarkValidatorManager() throws Exception {
        DefaultActionValidatorManager manager = container.inject(DefaultActionValidatorManager.class);
        benchmark("getValidators", () -> manager.getValidators(SimpleAction.class, "validationAlias").size());
    }

    public void testBenchmarkConverter() throws Exception {
        XWorkConverter converter = container.getInstance(XWorkConverter.class);
        benchmark("getConverter", () -> converter.convertValue(null, new User(), null, "Collection_list", "1", String.class));
    }

    private void benchmark(String label, Callable<Object> op) throws Exception {
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            op.call();
        }
        System.out.println("=== " + label + " ===");
        for (int threads : THREAD_COUNTS) {
            System.out.printf("%s threads=%d ops/sec=%,d%n", label, threads, measure(op, threads));
        }
    }

    private long measure(Callable<Object> op, int threads) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(MEASURE_MILLIS);
        List<Future<Long>> futures = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            futures.add(pool.submit(() -> {
                start.await();
                long count = 0;
                while (System.nanoTime() < deadline) {
                    for (int i = 0; i < 100; i++) {
                        op.call();
                    }
                    count += 100;
                }
                return count;
            }));
        }

        start.countDown();
        long total = 0;
        for (Future<Long> future : futures) {
            total += future.get(120, TimeUnit.SECONDS);
        }
        pool.shutdown();
        return total * 1000 / MEASURE_MILLIS;
    }
}
```

Note the converter benchmark drives `convertValue` rather than the `protected getConverter` directly, so it exercises the real public path including the lock that Task 3 removed.

- [ ] **Step 4: Capture before/after numbers**

```bash
cd /Users/lukaszlenart/Projects/Apache/struts
git status --porcelain --untracked-files=no    # must be empty

# AFTER numbers, on the branch
mvn test -DskipAssembly -pl core -Dtest=Ww5539BenchmarkTest | tee /tmp/ww5539-after.txt

# BEFORE numbers: stash the benchmark, check out main, reapply just the benchmark
git stash push --include-untracked core/src/test/java/org/apache/struts2/validator/Ww5539BenchmarkTest.java
git switch --detach main
git stash pop
mvn test -DskipAssembly -pl core -Dtest=Ww5539BenchmarkTest | tee /tmp/ww5539-before.txt

# return to the branch
git stash push --include-untracked core/src/test/java/org/apache/struts2/validator/Ww5539BenchmarkTest.java
git switch WW-5539
git stash pop
```

Record both sets in a markdown table: rows = thread counts, columns = before ops/sec, after ops/sec, ratio.

Run each side twice and use the second run — the first is polluted by JIT and filesystem cache effects even with the warm-up loop.

- [ ] **Step 5: Delete the benchmark**

```bash
rm core/src/test/java/org/apache/struts2/validator/Ww5539BenchmarkTest.java
git status --porcelain    # must show nothing to commit
```

Confirm it is gone before opening the PR. It must not appear in the diff.

- [ ] **Step 6: Open the pull request**

Report the benchmark results **faithfully**, including the realistic outcome that the converter changes may not move the needle while the validator lock does — the converter caches warm fast, and the validator lock is the coarser of the two. Do not present a null result as a win.

The PR description must include:

- `Fixes [WW-5539](https://issues.apache.org/jira/browse/WW-5539)`
- The before/after benchmark table with thread counts.
- A **Behaviour changes** section listing:
  - `XWorkConverter.buildConverterMapping` (protected) no longer stores its result in the `TypeConverterHolder`.
  - `StrutsTypeConverterHolder.unknownMappings` (protected) retyped from `HashSet<String>` to `Set<String>` and made `final` — a source-compatibility break for subclasses that assign it or call `HashSet`-specific methods.
  - `addDefaultMapping` now ignores null converters with a warning instead of storing them.
  - `XWorkConverter.registerConverter` / `registerConverterNotFound` are no longer `synchronized`.
  - `TypeConverterHolder.getMapping`, `addMapping`, `containsNoMapping` deprecated.
- A **Known benign race** note: `lookup()` reads `containsUnknownMapping` and `containsDefaultMapping` as two separate atomic calls; the pair is not atomic, so a converter registered between them yields a stale `null` for that one call, self-correcting on the next lookup. No lock was added, because doing so would reintroduce the contention being removed on the hottest read path.
- A **Follow-ups** section: the classloader-pinning issue (`mappings` and `noMapping` hold strong `Class` references in a container singleton, keeping webapp classloaders alive across hot redeploy), and removal of the deprecated methods.

Follow `~/.claude/pr_guideline.md`.

---

## Notes for the implementer

**Why `lookup()` keeps check-then-act.** It is tempting to "finish the job" and wrap the default-mappings cache in `computeIfAbsent` too. Do not. Its resolver, `lookupSuper()`, reads `getDefaultMapping()` recursively while walking the class hierarchy, and a recursive read inside `ConcurrentHashMap.computeIfAbsent` is explicitly forbidden — it deadlocks or throws `IllegalStateException: Recursive update`. The duplicated work on a cold miss is a handful of lock-free map reads.

**Why `computeIfAbsent` on `mappings` is safe.** Its builder, `buildConverterMapping`, reaches `DefaultConversionFileProcessor` (which never touches the holder) and `DefaultConversionAnnotationProcessor` (which writes only to `defaultMappings`, a *different* map). Both were verified during design. If a future change makes the builder write to `mappings`, this becomes a deadlock — worth a comment if you touch that path.

**Concurrency tests prove presence, not absence.** A green run on a strongly-ordered x86 machine is weak evidence. The primary correctness argument is the reasoning about the data structures; the tests are a backstop. Do not add sleeps or retry loops to make a flaky assertion pass — if one is flaky, the design reasoning is wrong and needs revisiting.

**`conditionalReload` stays outside the compute** in `getConverter`. It only does work when `struts.configuration.xml.reload` is enabled and it must run on cache *hits* — that is its entire purpose.
