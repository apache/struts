# WW-5540: LocalizedTextProvider Traversal Caching — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Cache the class- and package-hierarchy traversal in `StrutsLocalizedTextProvider.findText` so repeated lookups for the same `(classloader, class, key, locale)` collapse to a single map lookup, without changing observable behavior.

**Architecture:** Split raw-pattern resolution (cacheable) from translation/formatting (per-call). Add two `ConcurrentHashMap` caches in `AbstractLocalizedTextProvider` — one for the class/interface/superclass walk, one for the `*.package` walk — each storing a raw pattern or a shared `NOT_FOUND` sentinel. `findText` reads the caches, then formats per call, and falls through to the next tier when a cached pattern formats to `null` (preserving `formatWithNullDetection` semantics).

**Tech Stack:** Java, Maven, JUnit 3/4 via `XWorkTestCase` (junit.framework), Mockito/mockobjects (existing test deps).

## Global Constraints

- Commit messages MUST be prefixed with the Jira ticket: `WW-5540 <type>[scope]: <desc>`.
- Cache key stores the class **name (String)**, never a `Class` object — no classloader pinning.
- Caches are unbounded `ConcurrentHashMap` (consistent with existing `bundlesMap`/`missingBundles`).
- `NOT_FOUND` is a unique `String` instance compared by `==` (identity), never by content.
- Use `get` + `putIfAbsent`, never `computeIfAbsent`, on the new caches (the child-property path recurses back into `findText`).
- Only the raw pattern is cached; `TextParseUtil.translateVariables` + `MessageFormat` always run per call.
- Core tests here extend `XWorkTestCase` — use `testXxx()` methods and `junit.framework` `assert*`; **no `@Test`, no AssertJ** (an `@Test` here silently never runs).
- Build/test: `mvn test -DskipAssembly -pl core -Dtest=<Class>#<method>` (single) or `-Dtest=<Class>` (whole class).
- Work on branch `WW-5540-localized-text-provider-caching` (already created off `main`).

---

### Task 1: Raw / format split (behavior-preserving refactor)

Extract the "find the raw pattern" and "render a pattern" steps from `getMessage`, and add a raw twin of `findMessage`. No caching yet. Behavior must be byte-identical; the existing test suite is the safety net.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/text/AbstractLocalizedTextProvider.java`
- Test (regression only): `core/src/test/java/org/apache/struts2/text/StrutsLocalizedTextProviderTest.java`

**Interfaces:**
- Consumes: existing `findResourceBundle`, `buildMessageFormat`, `formatWithNullDetection`, `reloadBundles`.
- Produces (used by Task 2 & 3):
  - `private String getRawMessage(String bundleName, Locale locale, String key)` → raw pattern or `null`.
  - `protected String formatMessage(String rawPattern, Locale locale, ValueStack valueStack, Object[] args)` → translated+formatted string (or `null` via null-detection).
  - `private String findMessageRaw(Class<?> clazz, String key, String indexedKey, Locale locale, Set<String> checked)` → first raw pattern found walking class/interface/superclass, or `null`.

- [ ] **Step 1: Add `getRawMessage` and `formatMessage`, and re-express `getMessage` via them**

In `AbstractLocalizedTextProvider`, add these two methods (place them just above the existing `getMessage`):

```java
/**
 * Resolves the raw (untranslated, unformatted) message pattern for a key within a single bundle.
 * Returns {@code null} when the bundle or key is absent. This is the cacheable unit relied upon by
 * the hierarchy-resolution caches; translation and formatting are applied separately by
 * {@link #formatMessage(String, Locale, ValueStack, Object[])}.
 */
private String getRawMessage(String bundleName, Locale locale, String key) {
    ResourceBundle bundle = findResourceBundle(bundleName, locale);
    if (bundle == null) {
        return null;
    }
    try {
        return bundle.getString(key);
    } catch (MissingResourceException e) {
        LOG.debug("Missing key [{}] in bundle [{}]!", key, bundleName);
        return null;
    }
}

/**
 * Applies value stack variable translation (when a stack is available) and {@link MessageFormat}
 * argument substitution to a raw message pattern. Mirrors the rendering previously performed inline
 * by {@link #getMessage(String, Locale, String, ValueStack, Object[])}.
 */
protected String formatMessage(String rawPattern, Locale locale, ValueStack valueStack, Object[] args) {
    String message = (valueStack != null)
            ? TextParseUtil.translateVariables(rawPattern, valueStack)
            : rawPattern;
    MessageFormat mf = buildMessageFormat(message, locale);
    return formatWithNullDetection(mf, args);
}
```

Then replace the body of the existing `getMessage` (currently at `AbstractLocalizedTextProvider.java:513-532`) with:

```java
protected String getMessage(String bundleName, Locale locale, String key, ValueStack valueStack, Object[] args) {
    ResourceBundle bundle = findResourceBundle(bundleName, locale);
    if (bundle == null) {
        return null;
    }
    if (valueStack != null) {
        reloadBundles(valueStack.getContext());
    }
    try {
        String rawPattern = bundle.getString(key);
        return formatMessage(rawPattern, locale, valueStack, args);
    } catch (MissingResourceException e) {
        LOG.debug("Missing key [{}] in bundle [{}]!", key, bundleName);
        return null;
    }
}
```

(This keeps `getMessage`'s order — `findResourceBundle` → `reloadBundles` → `getString` — identical; only the trailing translate/format is now delegated to `formatMessage`.)

- [ ] **Step 2: Add `findMessageRaw`**

Add next to the existing `findMessage`. It mirrors `findMessage` exactly (including the pre-existing, never-populated `checked` cycle guard) but returns the raw pattern via `getRawMessage` with no translation/formatting/args:

```java
/**
 * Raw-pattern twin of {@link #findMessage}. Walks class, implemented interfaces, then up the
 * hierarchy, returning the first raw message pattern found (via {@link #getRawMessage}) without
 * translation or formatting. Used by the cached class-hierarchy resolver.
 */
private String findMessageRaw(Class<?> clazz, String key, String indexedKey, Locale locale, Set<String> checked) {
    if (checked == null) {
        checked = new TreeSet<>();
    } else if (checked.contains(clazz.getName())) {
        return null;
    }

    // look in properties of this class
    String msg = getRawMessage(clazz.getName(), locale, key);
    if (msg != null) {
        return msg;
    }
    if (indexedKey != null) {
        msg = getRawMessage(clazz.getName(), locale, indexedKey);
        if (msg != null) {
            return msg;
        }
    }

    // look in properties of implemented interfaces
    Class<?>[] interfaces = clazz.getInterfaces();
    for (Class<?> anInterface : interfaces) {
        msg = getRawMessage(anInterface.getName(), locale, key);
        if (msg != null) {
            return msg;
        }
        if (indexedKey != null) {
            msg = getRawMessage(anInterface.getName(), locale, indexedKey);
            if (msg != null) {
                return msg;
            }
        }
    }

    // traverse up hierarchy
    if (clazz.isInterface()) {
        interfaces = clazz.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            msg = findMessageRaw(anInterface, key, indexedKey, locale, checked);
            if (msg != null) {
                return msg;
            }
        }
    } else {
        if (!clazz.equals(Object.class) && !clazz.isPrimitive()) {
            return findMessageRaw(clazz.getSuperclass(), key, indexedKey, locale, checked);
        }
    }

    return null;
}
```

Leave the existing `findMessage` and the existing `getMessage` callers untouched otherwise.

- [ ] **Step 3: Compile**

Run: `mvn -q test-compile -DskipAssembly -pl core`
Expected: BUILD SUCCESS (new methods compile; `getRawMessage`/`findMessageRaw` may be flagged unused by the IDE but not by the compiler).

- [ ] **Step 4: Run the full regression suite for this class**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsLocalizedTextProviderTest`
Expected: PASS — all existing tests green (proves the `getMessage`/`formatMessage` refactor is behavior-preserving).

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/text/AbstractLocalizedTextProvider.java
git commit -m "WW-5540 refactor(core): split raw message resolution from formatting

Add getRawMessage/formatMessage and a raw twin findMessageRaw, and
re-express getMessage in terms of them. Pure refactor, no behavior
change; groundwork for the hierarchy-traversal caches.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 2: Class-hierarchy cache (with invalidation, reload hoist, and correctness tests)

Introduce the first cache end-to-end: key type, sentinel, map, resolver, `findText` wiring for the class + ModelDriven tiers, invalidation at all three clear sites, and the reload hoist. Add a test fixture and correctness tests first.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/text/AbstractLocalizedTextProvider.java`
- Modify: `core/src/main/java/org/apache/struts2/text/StrutsLocalizedTextProvider.java`
- Create: `core/src/test/java/org/apache/struts2/text/CacheFixture.java`
- Create: `core/src/test/resources/org/apache/struts2/text/CacheFixture.properties`
- Modify: `core/src/test/java/org/apache/struts2/text/StrutsLocalizedTextProviderTest.java`

**Interfaces:**
- Consumes (from Task 1): `getRawMessage`, `formatMessage`, `findMessageRaw`.
- Produces (used by Task 3 and tests):
  - `static class TextCacheKey` with fields `int classLoaderHash, String className, String textKey, Locale locale` and `equals`/`hashCode`.
  - `private static final String NOT_FOUND` — identity sentinel.
  - `private final ConcurrentMap<TextCacheKey, String> classHierarchyCache`.
  - `private int currentLoaderHashCode()` → `getCurrentThreadContextClassLoader().hashCode()`.
  - `protected String resolveClassHierarchyRaw(Class<?> clazz, String textKey, String indexedKey, Locale locale)` → raw pattern or `NOT_FOUND`.
  - `protected int classHierarchyCacheSize()` → entry count (test support).

- [ ] **Step 1: Create the test fixture class**

Create `core/src/test/java/org/apache/struts2/text/CacheFixture.java`:

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
package org.apache.struts2.text;

/**
 * Simple fixture whose class-associated bundle ({@code CacheFixture.properties}) backs the
 * localized-text caching tests. The {@code name} property is exposed so OGNL expressions such as
 * {@code ${name}} can be resolved against a value stack.
 */
public class CacheFixture {

    private final String name;

    public CacheFixture(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

- [ ] **Step 2: Create the fixture bundle**

Create `core/src/test/resources/org/apache/struts2/text/CacheFixture.properties`:

```properties
cache.static=Static cached value
cache.withparam=Value with param {0}
cache.withognl=Hello ${name}
cache.nullformat={0}
```

- [ ] **Step 3: Add public cache-size accessor to the test helper**

In `StrutsLocalizedTextProviderTest.java`, inside the nested `TestStrutsLocalizedTextProvider` class (after `getBundlesReloadedIndicatorValue`, before its closing brace), add:

```java
public int classHierarchyCacheSize() {
    return super.classHierarchyCacheSize();
}
```

- [ ] **Step 4: Write the failing tests**

Add these methods to `StrutsLocalizedTextProviderTest` (anywhere among the other `testXxx` methods). They reference `resolveClassHierarchyRaw`/`classHierarchyCacheSize`/`NOT_FOUND` behavior that does not exist yet, so they fail to compile/pass until Steps 5–9:

```java
public void testClassHierarchyCacheReusesFoundPattern() {
    TestStrutsLocalizedTextProvider provider = new TestStrutsLocalizedTextProvider();
    ValueStack valueStack = ActionContext.getContext().getValueStack();

    assertEquals("Cache not empty before first lookup ?", 0, provider.classHierarchyCacheSize());
    String first = provider.findText(CacheFixture.class, "cache.static", Locale.ENGLISH, null, null, valueStack);
    assertEquals("Static cached value", first);
    assertEquals("Cache not populated after found lookup ?", 1, provider.classHierarchyCacheSize());

    String second = provider.findText(CacheFixture.class, "cache.static", Locale.ENGLISH, null, null, valueStack);
    assertEquals("Second lookup differs from first ?", first, second);
    assertEquals("Cache grew on repeated lookup ?", 1, provider.classHierarchyCacheSize());
}

public void testClassHierarchyCacheStoresMisses() {
    TestStrutsLocalizedTextProvider provider = new TestStrutsLocalizedTextProvider();
    ValueStack valueStack = ActionContext.getContext().getValueStack();

    String first = provider.findText(CacheFixture.class, "cache.missing", Locale.ENGLISH, "Fallback", null, valueStack);
    assertEquals("Fallback", first);
    assertEquals("Miss not cached ?", 1, provider.classHierarchyCacheSize());

    String second = provider.findText(CacheFixture.class, "cache.missing", Locale.ENGLISH, "Fallback", null, valueStack);
    assertEquals("Fallback", second);
    assertEquals("Miss cache grew on repeat ?", 1, provider.classHierarchyCacheSize());
}

public void testFormattingIsPerCallNotCached() {
    TestStrutsLocalizedTextProvider provider = new TestStrutsLocalizedTextProvider();
    ValueStack valueStack = ActionContext.getContext().getValueStack();

    String x = provider.findText(CacheFixture.class, "cache.withparam", Locale.ENGLISH, null, new Object[]{"X"}, valueStack);
    String y = provider.findText(CacheFixture.class, "cache.withparam", Locale.ENGLISH, null, new Object[]{"Y"}, valueStack);
    assertEquals("Value with param X", x);
    assertEquals("Value with param Y", y);
}

public void testOgnlTranslationIsPerCall() {
    TestStrutsLocalizedTextProvider provider = new TestStrutsLocalizedTextProvider();
    ValueStack valueStack = ActionContext.getContext().getValueStack();

    valueStack.push(new CacheFixture("World"));
    String world = provider.findText(CacheFixture.class, "cache.withognl", Locale.ENGLISH, null, null, valueStack);
    valueStack.pop();
    valueStack.push(new CacheFixture("Mars"));
    String mars = provider.findText(CacheFixture.class, "cache.withognl", Locale.ENGLISH, null, null, valueStack);
    valueStack.pop();

    assertEquals("Hello World", world);
    assertEquals("Hello Mars", mars);
}

public void testNullFormattingFallsThroughToDefault() {
    TestStrutsLocalizedTextProvider provider = new TestStrutsLocalizedTextProvider();
    ValueStack valueStack = ActionContext.getContext().getValueStack();

    // "{0}" with a null arg formats to the literal "null"; findText must fall through to the default.
    String first = provider.findText(CacheFixture.class, "cache.nullformat", Locale.ENGLISH, "Fallback", new Object[]{null}, valueStack);
    assertEquals("Fallback", first);
    // Repeat after the pattern is cached — still falls through.
    String second = provider.findText(CacheFixture.class, "cache.nullformat", Locale.ENGLISH, "Fallback", new Object[]{null}, valueStack);
    assertEquals("Fallback", second);
}

public void testReloadClearsClassHierarchyCache() {
    TestStrutsLocalizedTextProvider provider = new TestStrutsLocalizedTextProvider();
    ValueStack valueStack = ActionContext.getContext().getValueStack();

    provider.findText(CacheFixture.class, "cache.static", Locale.ENGLISH, null, null, valueStack);
    assertEquals("Cache not populated ?", 1, provider.classHierarchyCacheSize());

    provider.callReloadBundlesForceReload();
    assertEquals("Reload did not clear class hierarchy cache ?", 0, provider.classHierarchyCacheSize());
}

public void testClearBundleAndClearMissingCacheEmptyClassHierarchyCache() {
    TestStrutsLocalizedTextProvider provider = new TestStrutsLocalizedTextProvider();
    ValueStack valueStack = ActionContext.getContext().getValueStack();

    provider.findText(CacheFixture.class, "cache.static", Locale.ENGLISH, null, null, valueStack);
    assertEquals("Cache not populated ?", 1, provider.classHierarchyCacheSize());
    provider.callClearBundleWithLocale("org/apache/struts2/text/CacheFixture", Locale.ENGLISH);
    assertEquals("clearBundle did not empty class hierarchy cache ?", 0, provider.classHierarchyCacheSize());

    provider.findText(CacheFixture.class, "cache.static", Locale.ENGLISH, null, null, valueStack);
    assertEquals("Cache not repopulated ?", 1, provider.classHierarchyCacheSize());
    provider.callClearMissingBundlesCache();
    assertEquals("clearMissingBundlesCache did not empty class hierarchy cache ?", 0, provider.classHierarchyCacheSize());
}
```

- [ ] **Step 5: Run the new tests to confirm they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsLocalizedTextProviderTest#testClassHierarchyCacheReusesFoundPattern+testReloadClearsClassHierarchyCache`
Expected: compilation failure (`classHierarchyCacheSize()` undefined) — this is the red state.

- [ ] **Step 6: Add the cache field, sentinel, key type, loader-hash helper, and size accessor**

In `AbstractLocalizedTextProvider`, add the sentinel near the other constants (after `RELOADED` at line ~58):

```java
private static final String NOT_FOUND = new String("__STRUTS_TEXT_NOT_FOUND__"); // unique identity sentinel; compared with ==
```

Add the field next to the other caches (after `delegatedClassLoaderMap` at line ~68):

```java
private final ConcurrentMap<TextCacheKey, String> classHierarchyCache = new ConcurrentHashMap<>();
```

Add the helper and the size accessor (place near `getCurrentThreadContextClassLoader`):

```java
private int currentLoaderHashCode() {
    return getCurrentThreadContextClassLoader().hashCode();
}

/** Test-support accessor: current number of cached class-hierarchy resolutions. */
protected int classHierarchyCacheSize() {
    return classHierarchyCache.size();
}
```

Add the key class next to the existing `MessageFormatKey` static class:

```java
static class TextCacheKey {
    private final int classLoaderHash;
    private final String className;
    private final String textKey;
    private final Locale locale;

    TextCacheKey(int classLoaderHash, String className, String textKey, Locale locale) {
        this.classLoaderHash = classLoaderHash;
        this.className = className;
        this.textKey = textKey;
        this.locale = locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextCacheKey that = (TextCacheKey) o;
        return classLoaderHash == that.classLoaderHash
                && Objects.equals(className, that.className)
                && Objects.equals(textKey, that.textKey)
                && Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        int result = classLoaderHash;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (textKey != null ? textKey.hashCode() : 0);
        result = 31 * result + (locale != null ? locale.hashCode() : 0);
        return result;
    }
}
```

(`Objects`, `ConcurrentMap`, `ConcurrentHashMap`, `Locale` are already imported.)

- [ ] **Step 7: Add the cached class-hierarchy resolver**

Add to `AbstractLocalizedTextProvider` (near `findMessageRaw`):

```java
/**
 * Cached resolution of the class/interface/superclass hierarchy for a key. Returns the raw pattern
 * found, or {@link #NOT_FOUND} when the key is absent from the entire hierarchy. Keyed on the
 * context classloader hash + class name + key + locale, so no {@link Class} reference is retained.
 * Uses get + putIfAbsent (never computeIfAbsent) because the child-property path recurses into findText.
 */
protected String resolveClassHierarchyRaw(Class<?> clazz, String textKey, String indexedKey, Locale locale) {
    TextCacheKey cacheKey = new TextCacheKey(currentLoaderHashCode(), clazz.getName(), textKey, locale);
    String cached = classHierarchyCache.get(cacheKey);
    if (cached != null) {
        return cached;
    }
    String raw = findMessageRaw(clazz, textKey, indexedKey, locale, null);
    String toStore = (raw != null) ? raw : NOT_FOUND;
    classHierarchyCache.putIfAbsent(cacheKey, toStore);
    return toStore;
}

/** @return true when a cached raw-resolution result represents "not found". */
protected boolean isNotFound(String cachedRawResult) {
    return cachedRawResult == NOT_FOUND;
}
```

(`NOT_FOUND` is `private` and not visible to the `StrutsLocalizedTextProvider` subclass, so the subclass tests "found?" via `isNotFound(...)` rather than referencing the sentinel directly.)

- [ ] **Step 8: Wire invalidation into the three clear sites**

In `reloadBundles(Map<String, Object> context)`, inside the `if (!reloaded)` block, add the clear immediately after `bundlesMap.clear();` (line ~224):

```java
bundlesMap.clear();
classHierarchyCache.clear();
```

In `clearBundle(String bundleName, Locale locale)` (line ~187), add after the `bundlesMap.remove(key)` line:

```java
final ResourceBundle removedBundle = bundlesMap.remove(key);
classHierarchyCache.clear();
```

In `clearMissingBundlesCache()` (line ~205), add after `missingBundles.clear();`:

```java
missingBundles.clear();
classHierarchyCache.clear();
```

- [ ] **Step 9: Rewire `findText` (class + ModelDriven tiers) and hoist the reload**

In `StrutsLocalizedTextProvider.findText(Class<?>, String, Locale, String, Object[], ValueStack)` (lines 62-195), make these edits.

Immediately after the `textKey == null` guard (after line 67), add the reload hoist:

```java
        // Trigger bundle reload (and cache invalidation) once, before any cached hierarchy lookup,
        // so that in reload/devMode the hierarchy caches are cleared before they are read.
        reloadBundles(valueStack != null ? valueStack.getContext() : null);
```

Replace the class-hierarchy block (current lines 84-89):

```java
        // search up class hierarchy
        String msg = findMessage(startClazz, textKey, indexedTextName, locale, args, null, valueStack);

        if (msg != null) {
            return msg;
        }
```

with:

```java
        // search up class hierarchy (cached raw resolution; format per call)
        String classHierarchyRaw = resolveClassHierarchyRaw(startClazz, textKey, indexedTextName, locale);
        String msg = null;
        if (!isNotFound(classHierarchyRaw)) {
            msg = formatMessage(classHierarchyRaw, locale, valueStack, args);
            if (msg != null) {
                return msg;
            }
        }
```

Replace the ModelDriven inner lookup (current lines 102-105):

```java
                        msg = findMessage(model.getClass(), textKey, indexedTextName, locale, args, null, valueStack);
                        if (msg != null) {
                            return msg;
                        }
```

with:

```java
                        String modelRaw = resolveClassHierarchyRaw(model.getClass(), textKey, indexedTextName, locale);
                        if (!isNotFound(modelRaw)) {
                            msg = formatMessage(modelRaw, locale, valueStack, args);
                            if (msg != null) {
                                return msg;
                            }
                        }
```

(`isNotFound` was added in Step 7; the sentinel itself stays private to `AbstractLocalizedTextProvider`.)

Leave the package loop (lines 111-134), child-property block, and default-message block unchanged in this task.

- [ ] **Step 10: Compile and run the new tests**

Run: `mvn -q test-compile -DskipAssembly -pl core`
Expected: BUILD SUCCESS.

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsLocalizedTextProviderTest#testClassHierarchyCacheReusesFoundPattern+testClassHierarchyCacheStoresMisses+testFormattingIsPerCallNotCached+testOgnlTranslationIsPerCall+testNullFormattingFallsThroughToDefault+testReloadClearsClassHierarchyCache+testClearBundleAndClearMissingCacheEmptyClassHierarchyCache`
Expected: PASS (7 tests green).

- [ ] **Step 11: Run the full class to confirm no regression**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsLocalizedTextProviderTest`
Expected: PASS — all tests (existing + 6 new) green.

- [ ] **Step 12: Commit**

```bash
git add core/src/main/java/org/apache/struts2/text/AbstractLocalizedTextProvider.java \
        core/src/main/java/org/apache/struts2/text/StrutsLocalizedTextProvider.java \
        core/src/test/java/org/apache/struts2/text/CacheFixture.java \
        core/src/test/resources/org/apache/struts2/text/CacheFixture.properties \
        core/src/test/java/org/apache/struts2/text/StrutsLocalizedTextProviderTest.java
git commit -m "WW-5540 perf(core): cache class-hierarchy text resolution

Cache the class/interface/superclass traversal in findText keyed on
(classloader, class name, key, locale), storing the raw pattern or a
NOT_FOUND marker. Formatting stays per call and falls through to the
next tier when a cached pattern formats to null. Invalidated on
reloadBundles/clearBundle/clearMissingBundlesCache; reload is hoisted
to the top of findText so caches are cleared before they are read.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 3: Package-hierarchy cache

Cache the `*.package` traversal the same way, wire it into `findText`, and invalidate it at the same three sites.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/text/AbstractLocalizedTextProvider.java`
- Modify: `core/src/main/java/org/apache/struts2/text/StrutsLocalizedTextProvider.java`
- Modify: `core/src/test/java/org/apache/struts2/text/StrutsLocalizedTextProviderTest.java`

**Interfaces:**
- Consumes (from Task 1/2): `getRawMessage`, `formatMessage`, `isNotFound`, `TextCacheKey`, `NOT_FOUND`, `currentLoaderHashCode`.
- Produces:
  - `private final ConcurrentMap<TextCacheKey, String> packageHierarchyCache`.
  - `private String findPackageMessageRaw(Class<?> startClazz, String textKey, String indexedTextName, Locale locale)` → first raw `*.package` match or `null`.
  - `protected String resolvePackageHierarchyRaw(Class<?> startClazz, String textKey, String indexedTextName, Locale locale)` → raw pattern or `NOT_FOUND`.
  - `protected int packageHierarchyCacheSize()` → entry count (test support).

- [ ] **Step 1: Add public accessor to the test helper**

In `StrutsLocalizedTextProviderTest.TestStrutsLocalizedTextProvider`, add:

```java
public int packageHierarchyCacheSize() {
    return super.packageHierarchyCacheSize();
}
```

- [ ] **Step 2: Write the failing tests**

Add to `StrutsLocalizedTextProviderTest`:

```java
public void testPackageHierarchyCacheReusesFoundPattern() {
    TestStrutsLocalizedTextProvider provider = new TestStrutsLocalizedTextProvider();
    ValueStack valueStack = ActionContext.getContext().getValueStack();

    // ModelDrivenAction2 lives in a package that provides "package.properties" = "It works!".
    assertEquals("Package cache not empty before lookup ?", 0, provider.packageHierarchyCacheSize());
    String first = provider.findText(org.apache.struts2.test.ModelDrivenAction2.class, "package.properties", Locale.getDefault(), null, null, valueStack);
    assertEquals("It works!", first);
    assertEquals("Package cache not populated after found lookup ?", 1, provider.packageHierarchyCacheSize());

    String second = provider.findText(org.apache.struts2.test.ModelDrivenAction2.class, "package.properties", Locale.getDefault(), null, null, valueStack);
    assertEquals("Second package lookup differs ?", first, second);
    assertEquals("Package cache grew on repeat ?", 1, provider.packageHierarchyCacheSize());
}

public void testReloadClearsPackageHierarchyCache() {
    TestStrutsLocalizedTextProvider provider = new TestStrutsLocalizedTextProvider();
    ValueStack valueStack = ActionContext.getContext().getValueStack();

    provider.findText(org.apache.struts2.test.ModelDrivenAction2.class, "package.properties", Locale.getDefault(), null, null, valueStack);
    assertEquals("Package cache not populated ?", 1, provider.packageHierarchyCacheSize());

    provider.callReloadBundlesForceReload();
    assertEquals("Reload did not clear package hierarchy cache ?", 0, provider.packageHierarchyCacheSize());
}
```

- [ ] **Step 3: Run the new tests to confirm they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsLocalizedTextProviderTest#testPackageHierarchyCacheReusesFoundPattern+testReloadClearsPackageHierarchyCache`
Expected: compilation failure (`packageHierarchyCacheSize()` undefined) — red state.

- [ ] **Step 4: Add the package cache field, raw walk, resolver, and size accessor**

In `AbstractLocalizedTextProvider`, add the field next to `classHierarchyCache`:

```java
private final ConcurrentMap<TextCacheKey, String> packageHierarchyCache = new ConcurrentHashMap<>();
```

Add the size accessor next to `classHierarchyCacheSize`:

```java
/** Test-support accessor: current number of cached package-hierarchy resolutions. */
protected int packageHierarchyCacheSize() {
    return packageHierarchyCache.size();
}
```

Add the raw package walk (mirrors the current package loop in `StrutsLocalizedTextProvider.findText`, using `getRawMessage`) and its cached resolver, next to `resolveClassHierarchyRaw`:

```java
/**
 * Raw-pattern walk of the {@code *.package} bundles up the class hierarchy of {@code startClazz}.
 * Returns the first raw pattern found (via {@link #getRawMessage}) for the key or its indexed form,
 * or {@code null} when none match.
 */
private String findPackageMessageRaw(Class<?> startClazz, String textKey, String indexedTextName, Locale locale) {
    for (Class<?> clazz = startClazz;
         (clazz != null) && !clazz.equals(Object.class);
         clazz = clazz.getSuperclass()) {

        String basePackageName = clazz.getName();
        while (basePackageName.lastIndexOf('.') != -1) {
            basePackageName = basePackageName.substring(0, basePackageName.lastIndexOf('.'));
            String packageName = basePackageName + ".package";
            String msg = getRawMessage(packageName, locale, textKey);
            if (msg != null) {
                return msg;
            }
            if (indexedTextName != null) {
                msg = getRawMessage(packageName, locale, indexedTextName);
                if (msg != null) {
                    return msg;
                }
            }
        }
    }
    return null;
}

/**
 * Cached resolution of the {@code *.package} hierarchy for a key. Returns the raw pattern found, or
 * {@link #NOT_FOUND} when absent. Same keying and get + putIfAbsent discipline as
 * {@link #resolveClassHierarchyRaw}.
 */
protected String resolvePackageHierarchyRaw(Class<?> startClazz, String textKey, String indexedTextName, Locale locale) {
    TextCacheKey cacheKey = new TextCacheKey(currentLoaderHashCode(), startClazz.getName(), textKey, locale);
    String cached = packageHierarchyCache.get(cacheKey);
    if (cached != null) {
        return cached;
    }
    String raw = findPackageMessageRaw(startClazz, textKey, indexedTextName, locale);
    String toStore = (raw != null) ? raw : NOT_FOUND;
    packageHierarchyCache.putIfAbsent(cacheKey, toStore);
    return toStore;
}
```

- [ ] **Step 5: Invalidate the package cache at the three clear sites**

Add `packageHierarchyCache.clear();` immediately after each `classHierarchyCache.clear();` added in Task 2 — in `reloadBundles` (the `if (!reloaded)` block), `clearBundle`, and `clearMissingBundlesCache`.

- [ ] **Step 6: Replace the package loop in `findText` with the cached resolver**

In `StrutsLocalizedTextProvider.findText`, replace the entire package-hierarchy loop (current lines 111-134):

```java
        // nothing still? alright, search the package hierarchy now
        for (Class<?> clazz = startClazz;
             (clazz != null) && !clazz.equals(Object.class);
             clazz = clazz.getSuperclass()) {

            String basePackageName = clazz.getName();
            while (basePackageName.lastIndexOf('.') != -1) {
                basePackageName = basePackageName.substring(0, basePackageName.lastIndexOf('.'));
                String packageName = basePackageName + ".package";
                msg = getMessage(packageName, locale, textKey, valueStack, args);

                if (msg != null) {
                    return msg;
                }

                if (indexedTextName != null) {
                    msg = getMessage(packageName, locale, indexedTextName, valueStack, args);

                    if (msg != null) {
                        return msg;
                    }
                }
            }
        }
```

with:

```java
        // search the package hierarchy (cached raw resolution; format per call)
        String packageRaw = resolvePackageHierarchyRaw(startClazz, textKey, indexedTextName, locale);
        if (!isNotFound(packageRaw)) {
            msg = formatMessage(packageRaw, locale, valueStack, args);
            if (msg != null) {
                return msg;
            }
        }
```

- [ ] **Step 7: Compile and run the new tests**

Run: `mvn -q test-compile -DskipAssembly -pl core`
Expected: BUILD SUCCESS.

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsLocalizedTextProviderTest#testPackageHierarchyCacheReusesFoundPattern+testReloadClearsPackageHierarchyCache`
Expected: PASS (2 tests green). In particular `testFindTextInPackage` (existing) must still pass.

- [ ] **Step 8: Run the full class and the sibling suite**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsLocalizedTextProviderTest`
Expected: PASS — all tests green.

Run: `mvn test -DskipAssembly -pl core -Dtest=GlobalLocalizedTextProviderTest,LocalizedTextUtilTest`
Expected: PASS (or "No tests matching" for any class that doesn't exist — confirm the ones that do exist pass). This guards the other `AbstractLocalizedTextProvider` subclass and legacy util.

- [ ] **Step 9: Commit**

```bash
git add core/src/main/java/org/apache/struts2/text/AbstractLocalizedTextProvider.java \
        core/src/main/java/org/apache/struts2/text/StrutsLocalizedTextProvider.java \
        core/src/test/java/org/apache/struts2/text/StrutsLocalizedTextProviderTest.java
git commit -m "WW-5540 perf(core): cache package-hierarchy text resolution

Cache the *.package traversal in findText the same way as the class
hierarchy, with the same keying, fall-through, and invalidation.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Final verification

- [ ] Run the whole core text package and a broad i18n-touching slice:
  `mvn test -DskipAssembly -pl core -Dtest='*LocalizedText*,*TextProvider*,TextProviderSupportTest'`
  Expected: PASS.
- [ ] Confirm no `computeIfAbsent` was used on the new caches and no `Class` object is stored in any key.
- [ ] Confirm `NOT_FOUND` is only ever compared via `isNotFound(...)` / `==`, never `.equals`.

## Notes / residual behavior (documented in the spec)

- A message that formats to the literal `"null"` and is redefined deeper in the *same* class hierarchy may resolve differently than before (shallow `null` short-circuits the cached path). Accepted as pathological — see the spec's "formatWithNullDetection fall-through" section.
- Caches are unbounded, consistent with `bundlesMap`/`missingBundles`; see the spec's "Known limitation".
