# WW-5668 — Bounded i18n caches and consistent request-locale resolution — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Bound the five `AbstractLocalizedTextProvider` caches with configurable size/eviction via the existing `OgnlCache` abstraction, and add an opt-in consistency check that resolves request-derived locales against the JVM available-locale set in `Dispatcher`.

**Architecture:** Reuse `OgnlCache` + `DefaultOgnlCacheFactory` (add an additive `remove`) for the i18n caches; drive them from two new `@Inject` constants. Add an opt-in `Dispatcher` helper mirroring `I18nInterceptor`'s availability check. The two parts are independent; Part 1 is always on, Part 2 defaults off.

**Tech Stack:** Java 17, Struts core, `org.apache.struts2.ognl` cache classes (Caffeine/WTLFU), commons-lang3 `LocaleUtils`/`EnumUtils`, JUnit 4 / `XWorkTestCase`, Mockito.

**Spec:** `docs/superpowers/specs/2026-07-30-WW-5668-i18n-cache-bounds-locale-resolution-design.md`

## Global Constraints

- Ticket prefix on every commit: `WW-5668`. Neutral framing only — no security/DoS/attacker/exhaustion language in code, comments, tests, or commit messages.
- Core tests are JUnit 3/4. Tests extending `XWorkTestCase` use `public void testXxx()` methods (JUnit 3 style); standalone tests use JUnit 4 (`org.junit.Test`, `org.junit.Assert`). Never JUnit 5 `@Test` — it silently does not run.
- Build/test command: `mvn test -DskipAssembly -pl core -Dtest=<ClassName>` (append `#<method>` for a single method).
- New config constant defaults must match existing cache conventions: `wtlfu` / `10000`.
- `struts.locale.validateRequestLocale` default `false` — current behaviour must be preserved byte-for-byte when unset.

---

### Task 1: Add `remove` to the `OgnlCache` abstraction

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/ognl/OgnlCache.java`
- Modify: `core/src/main/java/org/apache/struts2/ognl/OgnlCaffeineCache.java`
- Modify: `core/src/main/java/org/apache/struts2/ognl/OgnlDefaultCache.java`
- Modify: `core/src/main/java/org/apache/struts2/ognl/OgnlLRUCache.java`
- Test (create): `core/src/test/java/org/apache/struts2/ognl/OgnlCacheRemoveTest.java`

**Interfaces:**
- Produces: `V OgnlCache.remove(K key)` — removes the mapping for `key`, returning the previous value or `null`. Implemented by all three cache classes.

- [ ] **Step 1: Write the failing test**

Create `core/src/test/java/org/apache/struts2/ognl/OgnlCacheRemoveTest.java`:

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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OgnlCacheRemoveTest {

    private void assertRemoveContract(OgnlCache<String, String> cache) {
        cache.put("k", "v");
        assertEquals("v", cache.get("k"));
        assertEquals("remove returns previous value", "v", cache.remove("k"));
        assertNull("entry gone after remove", cache.get("k"));
        assertNull("remove of absent key returns null", cache.remove("absent"));
    }

    @Test
    public void caffeineCacheRemove() {
        assertRemoveContract(new OgnlCaffeineCache<>(10, 16));
    }

    @Test
    public void defaultCacheRemove() {
        assertRemoveContract(new OgnlDefaultCache<>(10, 16, 0.75f));
    }

    @Test
    public void lruCacheRemove() {
        assertRemoveContract(new OgnlLRUCache<>(10, 16, 0.75f));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=OgnlCacheRemoveTest`
Expected: COMPILE FAILURE — `OgnlCache` has no `remove` method.

- [ ] **Step 3: Add `remove` to the interface**

In `OgnlCache.java`, after the `void put(K key, V value);` / `void putIfAbsent(...)` declarations, add:

```java
    /**
     * Removes the mapping for the given key, if present.
     *
     * @param key the key to remove
     * @return the previous value associated with the key, or {@code null} if none
     * @since 7.3.0
     */
    V remove(K key);
```

- [ ] **Step 4: Implement in all three cache classes**

`OgnlCaffeineCache.java` — add:

```java
    @Override
    public V remove(K key) {
        return cache.asMap().remove(key);
    }
```

`OgnlDefaultCache.java` — add:

```java
    @Override
    public V remove(K key) {
        return ognlCache.remove(key);
    }
```

`OgnlLRUCache.java` — add:

```java
    @Override
    public V remove(K key) {
        return ognlLRUCache.remove(key);
    }
```

- [ ] **Step 5: Run test to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=OgnlCacheRemoveTest`
Expected: PASS (3 tests).

- [ ] **Step 6: Verify no other implementors broke**

Run: `mvn test -DskipAssembly -pl core -Dtest=OgnlUtilTest`
Expected: PASS — the only `OgnlCache` implementors are the three modified classes (confirmed by design), so nothing else needed the new method.

- [ ] **Step 7: Commit**

```bash
git add core/src/main/java/org/apache/struts2/ognl/OgnlCache.java \
        core/src/main/java/org/apache/struts2/ognl/OgnlCaffeineCache.java \
        core/src/main/java/org/apache/struts2/ognl/OgnlDefaultCache.java \
        core/src/main/java/org/apache/struts2/ognl/OgnlLRUCache.java \
        core/src/test/java/org/apache/struts2/ognl/OgnlCacheRemoveTest.java
git commit -m "WW-5668 Add remove(key) to the OgnlCache abstraction

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 2: Bound the localized-text provider caches (configurable)

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/StrutsConstants.java` (add two constants)
- Modify: `core/src/main/resources/org/apache/struts2/default.properties` (document them)
- Modify: `core/src/main/java/org/apache/struts2/text/AbstractLocalizedTextProvider.java` (cache fields, setters, call sites, size accessors)
- Test: `core/src/test/java/org/apache/struts2/text/StrutsLocalizedTextProviderTest.java` (add tests; existing 33 must stay green)

**Interfaces:**
- Consumes: `OgnlCache<K,V>` with `get`/`put`/`putIfAbsent`/`remove`/`clear`/`size` (Task 1); `DefaultOgnlCacheFactory<K,V>(int cacheMaxSize, CacheType type)` with `buildOgnlCache()`; `OgnlCacheFactory.CacheType` enum (`BASIC`/`LRU`/`WTLFU`).
- Produces: constants `STRUTS_I18N_CACHE_TYPE = "struts.i18n.cacheType"`, `STRUTS_I18N_CACHE_MAXSIZE = "struts.i18n.cacheMaxSize"`; provider setters `setI18nCacheType(String)`, `setI18nCacheMaxSize(String)`; `protected int` size accessors `bundlesMapSize()`, `missingBundlesSize()`, `messageFormatsSize()` (alongside the existing `classHierarchyCacheSize()`/`packageHierarchyCacheSize()`).

- [ ] **Step 1: Add the constants**

In `StrutsConstants.java`, after `STRUTS_OGNL_EXPRESSION_CACHE_MAXSIZE` (line ~545), add:

```java
    /**
     * Specifies the type of cache to use for the localized-text provider caches. Valid values defined in
     * {@link org.apache.struts2.ognl.OgnlCacheFactory.CacheType}.
     *
     * @since 7.3.0
     */
    public static final String STRUTS_I18N_CACHE_TYPE = "struts.i18n.cacheType";

    /**
     * Specifies the maximum size of each localized-text provider cache. Configure based on the cache type
     * chosen and application-specific needs.
     *
     * @since 7.3.0
     */
    public static final String STRUTS_I18N_CACHE_MAXSIZE = "struts.i18n.cacheMaxSize";
```

- [ ] **Step 2: Document them in default.properties**

In `core/src/main/resources/org/apache/struts2/default.properties`, after the `struts.ognl.expressionCacheMaxSize=10000` block (line ~297), add:

```properties
### Specifies the type of cache to use for the localized-text provider caches. See StrutsConstants for details.
struts.i18n.cacheType=wtlfu

### Specifies the maximum size of each localized-text provider cache. This should be configured based on the
### cache type chosen and application-specific needs.
struts.i18n.cacheMaxSize=10000
```

- [ ] **Step 3: Write the failing tests**

In `StrutsLocalizedTextProviderTest.java`, add these methods (JUnit 3 style, matching the file). They use the existing `TestStrutsLocalizedTextProvider` subclass and `CacheFixture` fixture already present in this test class:

```java
    public void testCachesAreBoundedByConfiguredMaxSize() {
        TestStrutsLocalizedTextProvider provider = new TestStrutsLocalizedTextProvider();
        provider.setI18nCacheMaxSize("100");
        ValueStack valueStack = ActionContext.getContext().getValueStack();

        for (int i = 0; i < 20000; i++) {
            Locale locale = Locale.forLanguageTag("en-US-x" + String.format("%05d", i));
            provider.findText(CacheFixture.class, "cache.missing", locale, "Fallback", null, valueStack);
        }

        assertTrue("classHierarchyCache not bounded ?", provider.classHierarchyCacheSize() <= 2000);
        assertTrue("packageHierarchyCache not bounded ?", provider.packageHierarchyCacheSize() <= 2000);
        assertTrue("bundlesMap not bounded ?", provider.bundlesMapSize() <= 2000);
        assertTrue("missingBundles not bounded ?", provider.missingBundlesSize() <= 2000);
        assertTrue("messageFormats not bounded ?", provider.messageFormatsSize() <= 2000);
    }

    public void testCorrectTextStillReturnedUnderEviction() {
        TestStrutsLocalizedTextProvider provider = new TestStrutsLocalizedTextProvider();
        provider.setI18nCacheMaxSize("50");
        ValueStack valueStack = ActionContext.getContext().getValueStack();

        // Force heavy eviction with many distinct locales.
        for (int i = 0; i < 5000; i++) {
            Locale locale = Locale.forLanguageTag("en-US-x" + String.format("%05d", i));
            provider.findText(CacheFixture.class, "cache.missing", locale, "Fallback", null, valueStack);
        }

        // A real key in a real locale still resolves correctly after eviction pressure.
        String result = provider.findText(CacheFixture.class, "cache.static", Locale.ENGLISH, null, null, valueStack);
        assertEquals("Static cached value", result);
    }

    public void testReloadClearsBoundedCaches() {
        TestStrutsLocalizedTextProvider provider = new TestStrutsLocalizedTextProvider();
        ValueStack valueStack = ActionContext.getContext().getValueStack();

        provider.findText(CacheFixture.class, "cache.missing", Locale.ENGLISH, "Fallback", null, valueStack);
        assertTrue("missingBundles not populated ?", provider.missingBundlesSize() > 0);

        provider.reloadBundles(ActionContext.getContext().getContextMap());
        assertEquals("reload did not clear bundlesMap ?", 0, provider.bundlesMapSize());
    }
```

- [ ] **Step 4: Run the new tests to verify they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsLocalizedTextProviderTest#testCachesAreBoundedByConfiguredMaxSize+testCorrectTextStillReturnedUnderEviction+testReloadClearsBoundedCaches`
Expected: COMPILE FAILURE — `setI18nCacheMaxSize`, `bundlesMapSize`, `missingBundlesSize`, `messageFormatsSize` do not exist yet.

- [ ] **Step 5: Add imports and replace the cache fields**

In `AbstractLocalizedTextProvider.java`, add imports:

```java
import org.apache.commons.lang3.EnumUtils;
import org.apache.struts2.ognl.DefaultOgnlCacheFactory;
import org.apache.struts2.ognl.OgnlCache;
import org.apache.struts2.ognl.OgnlCacheFactory.CacheType;
```

Replace the five cache field declarations (currently lines ~62, 67, 69, 71, 72):

```java
    protected final ConcurrentMap<String, ResourceBundle> bundlesMap = new ConcurrentHashMap<>();
    ...
    private final ConcurrentMap<MessageFormatKey, MessageFormat> messageFormats = new ConcurrentHashMap<>();
    ...
    private final Set<String> missingBundles = ConcurrentHashMap.newKeySet();
    ...
    private final ConcurrentMap<TextCacheKey, String> classHierarchyCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<TextCacheKey, String> packageHierarchyCache = new ConcurrentHashMap<>();
```

with (keeping `classLoaderMap` and `delegatedClassLoaderMap` as they are — they still use `ConcurrentMap`):

```java
    private volatile CacheType i18nCacheType = CacheType.WTLFU;
    private volatile int i18nCacheMaxSize = 10000;

    private <K, V> OgnlCache<K, V> buildI18nCache() {
        return new DefaultOgnlCacheFactory<K, V>(i18nCacheMaxSize, i18nCacheType).buildOgnlCache();
    }

    protected OgnlCache<String, ResourceBundle> bundlesMap = buildI18nCache();
    private OgnlCache<MessageFormatKey, MessageFormat> messageFormats = buildI18nCache();
    private OgnlCache<String, Boolean> missingBundles = buildI18nCache();
    private OgnlCache<TextCacheKey, String> classHierarchyCache = buildI18nCache();
    private OgnlCache<TextCacheKey, String> packageHierarchyCache = buildI18nCache();
```

Keep the field ordering so `i18nCacheType`/`i18nCacheMaxSize` and `buildI18nCache()` are declared before the five cache fields (field initializers run top-to-bottom). Leave `boolean devMode`/`reloadBundles`/`searchDefaultBundlesFirst` where they are.

- [ ] **Step 6: Add the injectable setters and rebuild helper**

Near the other `@Inject` setters (e.g. after `setSearchDefaultBundlesFirst`, line ~405), add:

```java
    @Inject(value = StrutsConstants.STRUTS_I18N_CACHE_TYPE, required = false)
    public void setI18nCacheType(String cacheType) {
        this.i18nCacheType = EnumUtils.getEnumIgnoreCase(CacheType.class, cacheType, CacheType.WTLFU);
        rebuildI18nCaches();
    }

    @Inject(value = StrutsConstants.STRUTS_I18N_CACHE_MAXSIZE, required = false)
    public void setI18nCacheMaxSize(String cacheMaxSize) {
        this.i18nCacheMaxSize = Integer.parseInt(cacheMaxSize);
        rebuildI18nCaches();
    }

    /**
     * Rebuilds the localized-text caches from the current type/size. Called during dependency injection
     * (single-threaded startup, before the provider serves lookups); discards any warm-up entries.
     */
    private void rebuildI18nCaches() {
        bundlesMap = buildI18nCache();
        messageFormats = buildI18nCache();
        missingBundles = buildI18nCache();
        classHierarchyCache = buildI18nCache();
        packageHierarchyCache = buildI18nCache();
    }
```

- [ ] **Step 7: Update the `bundlesMap` / `missingBundles` call sites in `findResourceBundle`**

Replace the body of `findResourceBundle` (lines ~408-442) that uses `missingBundles.contains` / `bundlesMap.containsKey` / `bundlesMap.get` / `putIfAbsent` / `missingBundles.add`. New body:

```java
    @Override
    public ResourceBundle findResourceBundle(String bundleName, Locale locale) {
        ClassLoader classLoader = getCurrentThreadContextClassLoader();
        String key = createMissesKey(String.valueOf(classLoader.hashCode()), bundleName, locale);

        if (missingBundles.get(key) != null) {
            return null;
        }

        ResourceBundle bundle = null;
        try {
            bundle = bundlesMap.get(key);
            if (bundle == null) {
                bundle = ResourceBundle.getBundle(bundleName, locale, classLoader);
                bundlesMap.putIfAbsent(key, bundle);
            }
        } catch (MissingResourceException ex) {
            if (delegatedClassLoaderMap.containsKey(classLoader.hashCode())) {
                try {
                    bundle = bundlesMap.get(key);
                    if (bundle == null) {
                        bundle = ResourceBundle.getBundle(bundleName, locale, delegatedClassLoaderMap.get(classLoader.hashCode()));
                        bundlesMap.putIfAbsent(key, bundle);
                    }
                } catch (MissingResourceException e) {
                    LOG.debug("Missing resource bundle [{}]!", bundleName, e);
                    missingBundles.put(key, Boolean.TRUE);
                }
            } else {
                LOG.debug("Missing resource bundle [{}]!", bundleName);
                missingBundles.put(key, Boolean.TRUE);
            }
        }
        return bundle;
    }
```

`clearBundle` (line ~209) needs no change: `bundlesMap.remove(key)` now calls `OgnlCache.remove` and still returns the removed `ResourceBundle`. `clearMissingBundlesCache` (`missingBundles.clear()`), and `reloadBundles` (`bundlesMap.clear()` etc.) also need no change — `clear()` is unchanged on `OgnlCache`. `buildMessageFormat` (`messageFormats.get`/`put`), `resolveClassHierarchyRaw`/`resolvePackageHierarchyRaw` (`get`/`putIfAbsent`) are signature-compatible and need no change.

- [ ] **Step 8: Add the three size accessors**

Next to the existing `classHierarchyCacheSize()`/`packageHierarchyCacheSize()` (lines ~103-111), add:

```java
    /** Test-support accessor: current number of cached resource bundles. */
    protected int bundlesMapSize() {
        return bundlesMap.size();
    }

    /** Test-support accessor: current number of cached missing-bundle markers. */
    protected int missingBundlesSize() {
        return missingBundles.size();
    }

    /** Test-support accessor: current number of cached message formats. */
    protected int messageFormatsSize() {
        return messageFormats.size();
    }
```

- [ ] **Step 9: Run the new tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsLocalizedTextProviderTest#testCachesAreBoundedByConfiguredMaxSize+testCorrectTextStillReturnedUnderEviction+testReloadClearsBoundedCaches`
Expected: PASS (3 tests).

- [ ] **Step 10: Run the full provider suite to confirm no regression**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsLocalizedTextProviderTest`
Expected: PASS (all existing tests + 3 new).

- [ ] **Step 11: Commit**

```bash
git add core/src/main/java/org/apache/struts2/StrutsConstants.java \
        core/src/main/resources/org/apache/struts2/default.properties \
        core/src/main/java/org/apache/struts2/text/AbstractLocalizedTextProvider.java \
        core/src/test/java/org/apache/struts2/text/StrutsLocalizedTextProviderTest.java
git commit -m "WW-5668 Bound the localized-text provider caches with configurable size

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 3: Opt-in request-locale resolution consistency in Dispatcher

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/StrutsConstants.java` (add one constant)
- Modify: `core/src/main/resources/org/apache/struts2/default.properties` (document it)
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/Dispatcher.java` (flag field, setter, helper, `getLocale` call sites)
- Test: `core/src/test/java/org/apache/struts2/dispatcher/DispatcherTest.java` (add tests)

**Interfaces:**
- Consumes: `org.apache.commons.lang3.LocaleUtils.isAvailableLocale(Locale)` (LocaleUtils already imported in `Dispatcher`).
- Produces: constant `STRUTS_LOCALE_VALIDATE_REQUEST = "struts.locale.validateRequestLocale"`; `Dispatcher.setValidateRequestLocale(String)`; private `Locale resolveRequestLocale(HttpServletRequest)`.

- [ ] **Step 1: Add the constant**

In `StrutsConstants.java`, after `STRUTS_LOCALE` (line 136), add:

```java
    /**
     * When enabled, request-derived locales (from {@code Accept-Language}, used when {@code struts.locale} is
     * unset) are restricted to the JVM's available-locale set; unavailable values fall back to the default.
     *
     * @since 7.3.0
     */
    public static final String STRUTS_LOCALE_VALIDATE_REQUEST = "struts.locale.validateRequestLocale";
```

- [ ] **Step 2: Document it in default.properties**

In `default.properties`, after the `# struts.locale=en_US` line (line ~26), add:

```properties
### When true, restrict request-derived locales (Accept-Language, used when struts.locale is unset) to the
### JVM's available-locale set; unavailable values fall back to the default locale. Defaults to false.
struts.locale.validateRequestLocale=false
```

- [ ] **Step 3: Write the failing tests**

In `DispatcherTest.java` (JUnit 4, uses Mockito), add — modelled on the existing `getLocale` tests around lines 481-532:

```java
    @Test
    public void testValidateRequestLocaleOffPassesThrough() throws Exception {
        Dispatcher du = initDispatcher(new HashMap<>());
        HttpServletRequest request = mock(HttpServletRequest.class);
        // A syntactically valid but not JVM-available locale.
        Locale exotic = new Locale("en", "US", "xzz99");
        when(request.getLocale()).thenReturn(exotic);

        assertEquals("Default off must pass the request locale through unchanged",
                exotic, du.getLocale(request));
    }

    @Test
    public void testValidateRequestLocaleOnKeepsAvailableLocale() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(StrutsConstants.STRUTS_LOCALE_VALIDATE_REQUEST, "true");
        Dispatcher du = initDispatcher(params);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getLocale()).thenReturn(Locale.UK);

        assertEquals("Available request locale must be kept", Locale.UK, du.getLocale(request));
    }

    @Test
    public void testValidateRequestLocaleOnFallsBackForUnavailableLocale() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(StrutsConstants.STRUTS_LOCALE_VALIDATE_REQUEST, "true");
        Dispatcher du = initDispatcher(params);
        HttpServletRequest request = mock(HttpServletRequest.class);
        Locale exotic = new Locale("en", "US", "xzz99");
        when(request.getLocale()).thenReturn(exotic);

        // struts.locale unset in this dispatcher -> fall back to the JVM default.
        assertEquals("Unavailable request locale must fall back to system default",
                Locale.getDefault(), du.getLocale(request));
    }
```

Note: reuse the test's existing helper for building a `Dispatcher` with init params. If the existing tests use a different constructor pattern than `initDispatcher(Map)`, match whatever those `getLocale` tests already use to build `du` (check the top of the nearest existing `getLocale` test and copy its setup verbatim).

- [ ] **Step 4: Run the new tests to verify they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=DispatcherTest#testValidateRequestLocaleOffPassesThrough+testValidateRequestLocaleOnKeepsAvailableLocale+testValidateRequestLocaleOnFallsBackForUnavailableLocale`
Expected: COMPILE FAILURE — `STRUTS_LOCALE_VALIDATE_REQUEST` / setter not present.

- [ ] **Step 5: Add the flag field and setter**

In `Dispatcher.java`, near `defaultLocale` (line ~152) add a field:

```java
    private boolean validateRequestLocale = false;
```

Near `setDefaultLocale` (line ~308) add:

```java
    @Inject(value = StrutsConstants.STRUTS_LOCALE_VALIDATE_REQUEST, required = false)
    public void setValidateRequestLocale(String val) {
        validateRequestLocale = Boolean.parseBoolean(val);
    }
```

- [ ] **Step 6: Add the resolution helper and route both call sites through it**

In `Dispatcher.getLocale(HttpServletRequest)` (lines ~925-950), replace the two `locale = request.getLocale();` calls (lines ~932 and ~943) with `locale = resolveRequestLocale(request);`. Then add the helper directly below `getLocale`:

```java
    /**
     * Resolves the request locale. When {@code struts.locale.validateRequestLocale} is enabled and the
     * request locale is not part of the JVM's available-locale set, falls back to the configured
     * {@code struts.locale} when set and parseable, otherwise the JVM default. When disabled (default),
     * returns the request locale unchanged.
     */
    protected Locale resolveRequestLocale(HttpServletRequest request) {
        Locale locale = request.getLocale();
        if (!validateRequestLocale || LocaleUtils.isAvailableLocale(locale)) {
            return locale;
        }
        if (defaultLocale != null) {
            try {
                return LocaleUtils.toLocale(defaultLocale);
            } catch (IllegalArgumentException e) {
                LOG.debug("Configured 'struts.locale' = [{}] is not parseable; falling back to system default", defaultLocale);
            }
        }
        LOG.debug("Request locale [{}] is not available; falling back to system default locale", locale);
        return Locale.getDefault();
    }
```

(The `defaultLocale`-set-and-parseable branch is inert at the two current call sites — see the spec's fallback note — but is retained as the helper's general contract. Do not add logic assuming it fires.)

- [ ] **Step 7: Run the new tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=DispatcherTest#testValidateRequestLocaleOffPassesThrough+testValidateRequestLocaleOnKeepsAvailableLocale+testValidateRequestLocaleOnFallsBackForUnavailableLocale`
Expected: PASS (3 tests).

- [ ] **Step 8: Run the full Dispatcher suite to confirm no regression**

Run: `mvn test -DskipAssembly -pl core -Dtest=DispatcherTest`
Expected: PASS — existing `getLocale` tests unaffected (flag defaults off).

- [ ] **Step 9: Commit**

```bash
git add core/src/main/java/org/apache/struts2/StrutsConstants.java \
        core/src/main/resources/org/apache/struts2/default.properties \
        core/src/main/java/org/apache/struts2/dispatcher/Dispatcher.java \
        core/src/test/java/org/apache/struts2/dispatcher/DispatcherTest.java
git commit -m "WW-5668 Add opt-in request-locale resolution consistency to Dispatcher

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 4: Whole-change verification

- [ ] **Step 1: Run both affected suites together**

Run: `mvn test -DskipAssembly -pl core -Dtest=OgnlCacheRemoveTest,StrutsLocalizedTextProviderTest,DispatcherTest,OgnlUtilTest`
Expected: all PASS.

- [ ] **Step 2: Run the broader i18n/dispatcher regression set**

Run: `mvn test -DskipAssembly -pl core -Dtest="*LocalizedText*,*Dispatcher*,*Ognl*"`
Expected: all PASS. If any pre-existing unrelated flake appears (e.g. jasperreports temp-file), note it and re-run the specific class.

- [ ] **Step 3: Confirm the working tree is clean and the branch is ready**

Run: `git status --short && git log --oneline main..HEAD`
Expected: no uncommitted changes; three `WW-5668` commits (Task 1-3).

---

## Self-Review

**Spec coverage:**
- Part 1 bounded caches (reuse OgnlCache, add `remove`, five caches, config constants, default.properties, correctness-safe eviction) → Tasks 1 + 2. ✓
- Part 1 sizing default `wtlfu`/`10000` single shared size → Task 2 Steps 1-2, 5. ✓
- Part 2 opt-in `struts.locale.validateRequestLocale` default false, helper mirroring `I18nInterceptor`, fallback to configured `struts.locale` else `Locale.getDefault()`, inertness caveat → Task 3. ✓
- Testing: bound invariant, correctness under eviction, clear/remove, flag off/on/fallback → Task 2 Step 3, Task 3 Step 3. ✓
- Out-of-scope (rename, per-cache sizes, path consolidation) → not implemented, correctly deferred. ✓

**Placeholder scan:** No TBD/TODO; all steps contain concrete code or exact commands. The one soft spot — Task 3 Step 3's note to match the existing `getLocale` test's dispatcher-construction helper — is a real instruction to copy verified local code, not a placeholder, because the surrounding `getLocale` tests already build `du` that way.

**Type consistency:** `OgnlCache.remove(K)→V` used identically in Task 1 (definition) and Task 2 (clearBundle relies on it). `CacheType`, `DefaultOgnlCacheFactory(int, CacheType)`, `buildOgnlCache()` match the real signatures. Size accessors `bundlesMapSize`/`missingBundlesSize`/`messageFormatsSize` defined in Task 2 Step 8, used in Task 2 Step 3. `setI18nCacheMaxSize(String)` defined Step 6, used Step 3. `STRUTS_I18N_CACHE_*` and `STRUTS_LOCALE_VALIDATE_REQUEST` defined and consumed consistently.
