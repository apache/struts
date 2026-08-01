# WW-5668 — Bounded localized-text caches and consistent request-locale resolution

- **Ticket:** [WW-5668](https://issues.apache.org/jira/browse/WW-5668)
- **Type:** Improvement
- **Fix version:** 7.3.0
- **Date:** 2026-07-30

> Framing note: this is a follow-up to WW-5540. Keep all wording — spec, code,
> commits, tests — in terms of cache bounds, eviction, configurability, and
> resolution consistency. No security/DoS/attacker language.

## Summary

Two related changes to the localized-text subsystem:

1. Give the `AbstractLocalizedTextProvider` caches a configurable maximum size and
   eviction, using the framework's existing cache abstraction, so their footprint
   is bounded like the OGNL expression, BeanInfo, and proxy caches already are.
2. Make request-derived locale resolution consistent between `Dispatcher` and
   `I18nInterceptor`, behind an opt-in flag, so an operator can restrict
   request-derived locales to the JVM's available-locale set.

The two parts are independent. Part 1 is always on. Part 2 defaults off.

## Part 1 — Bounded localized-text caches

### Current state

`AbstractLocalizedTextProvider` holds five internal caches as plain
`ConcurrentHashMap` / `ConcurrentHashMap.newKeySet`:

| Field | Type | Key |
|---|---|---|
| `bundlesMap` | `ConcurrentMap<String, ResourceBundle>` | classloader + bundleName + locale |
| `missingBundles` | `Set<String>` | classloader + bundleName + locale |
| `messageFormats` | `ConcurrentMap<MessageFormatKey, MessageFormat>` | pattern + locale |
| `classHierarchyCache` | `ConcurrentMap<TextCacheKey, String>` | classloader + className + key + locale |
| `packageHierarchyCache` | `ConcurrentMap<TextCacheKey, String>` | classloader + className + key + locale |

None has a configurable upper bound or eviction. Elsewhere the framework
standardises on bounded caches via `OgnlCacheFactory` / `DefaultOgnlCacheFactory`
(`struts.ognl.expressionCacheMaxSize=10000`, `struts.proxy.cacheMaxSize=10000`,
both `wtlfu`). These five are the outlier.

### Approach

Reuse the existing cache abstraction (`OgnlCache` + `DefaultOgnlCacheFactory`)
rather than introducing a new one or calling Caffeine directly.

**Interface change (additive):** add `V remove(K key)` to `OgnlCache<K, V>`.
- `OgnlCaffeineCache`: `return cache.asMap().remove(key);`
- `OgnlDefaultCache`, `OgnlLRUCache`: delegate to the backing map's `remove`.
- A `default` implementation is acceptable if it keeps existing impls compiling,
  but each impl should override with the native map removal so `clearBundle`
  keeps returning the removed value for its debug log.

This is the only change to shared OGNL code, and it is purely additive.

**Provider change:** replace the five fields with `OgnlCache` instances built from a
`DefaultOgnlCacheFactory`, mapping call sites:

| Field | New type | Call-site mapping |
|---|---|---|
| `bundlesMap` | `OgnlCache<String, ResourceBundle>` | `containsKey` → `get(k) != null`; keep `putIfAbsent`; `remove` |
| `missingBundles` | `OgnlCache<String, Boolean>` | `contains` → `get(k) != null`; `add` → `put(k, Boolean.TRUE)` |
| `messageFormats` | `OgnlCache<MessageFormatKey, MessageFormat>` | `get` / `put` |
| `classHierarchyCache` | `OgnlCache<TextCacheKey, String>` | `get` / `putIfAbsent` / `clear` / `size` |
| `packageHierarchyCache` | `OgnlCache<TextCacheKey, String>` | `get` / `putIfAbsent` / `clear` / `size` |

`reloadBundles`, `clearBundle`, and `clearMissingBundlesCache` keep calling
`clear()` / `remove()` on these caches exactly as before. The `NOT_FOUND` identity
sentinel in the hierarchy caches is unchanged.

### Why eviction is correctness-safe

All five are pure caches: every entry is fully reconstructible on a miss (reload the
bundle, re-record a miss, rebuild the `MessageFormat`, re-walk the class/package
hierarchy). WTLFU eviction can therefore only cause an occasional recompute, never a
wrong or stale localized result. This preserves the WW-5540 behaviour while bounding
memory.

### Configuration

Two new constants, applied independently to each of the five caches:

| Constant | Values | Default |
|---|---|---|
| `struts.i18n.cacheType` | `basic` \| `lru` \| `wtlfu` | `wtlfu` |
| `struts.i18n.cacheMaxSize` | integer | `10000` |

- Injected via `@Inject(..., required = false)` setters on
  `AbstractLocalizedTextProvider`, following the existing i18n setters
  (`setReloadBundles`, `setDevMode`, `setSearchDefaultBundlesFirst`).
- New keys in `StrutsConstants`.
- Documented in `default.properties` beside the OGNL/proxy cache settings.
- A single `cacheMaxSize` governs all five caches for now. Per-cache tuning is
  deliberately out of scope — see Out of scope / follow-ups.

### Sizing note (accepted trade-off)

The WW-5540 hierarchy caches are keyed by `(classloader, className, key, locale)`.
A very large application under normal single-locale traffic could have more than
10,000 distinct `(class, key)` pairs, in which case a 10,000 bound causes eviction
churn and partially erodes WW-5540's caching benefit (correctness unaffected, only
recompute cost). WTLFU retains the hot entries, so typical applications are
unaffected, and the bound is configurable. A single default of 10,000 (matching the
OGNL/proxy caches) is accepted for this ticket; per-cache tuning is a follow-up.

## Part 2 — Consistent request-locale resolution

### Current state

Struts resolves a request locale in more than one place, inconsistently:

- `I18nInterceptor.getLocaleFromParam(...)` resolves the `request_locale`
  parameter / cookie / session value, then checks it against the available-locale
  set via `LocaleProvider.isValidLocale(...)` (→ `LocaleUtils.isAvailableLocale`),
  falling back to the default locale when it is not available.
- `Dispatcher.getLocale(HttpServletRequest)` — used when `struts.locale` is unset,
  and as the fallback when a configured `struts.locale` is malformed — returns
  `request.getLocale()` directly, with no availability check.

### Approach

Add an opt-in flag and a single resolution helper in `Dispatcher`.

**New constant:**

| Constant | Values | Default |
|---|---|---|
| `struts.locale.validateRequestLocale` | boolean | `false` |

Default `false` preserves current behaviour byte-for-byte.

**Helper** `resolveRequestLocale(HttpServletRequest request)`, used at both
`request.getLocale()` sites in `Dispatcher.getLocale` (the `struts.locale`-unset
branch and the malformed-`struts.locale` fallback branch):

1. `locale = request.getLocale()`.
2. If `!validateRequestLocale` → return `locale` (current behaviour).
3. Else if `LocaleUtils.isAvailableLocale(locale)` → return `locale`.
4. Else → fall back to the configured `struts.locale` if set and parseable,
   otherwise `Locale.getDefault()`; log at debug.

The existing `RuntimeException` handling around `request.getLocale()` (falling back
to `Locale.getDefault()`) is retained.

**Note on the fallback at the current call sites.** Both call sites live inside
branches that only execute when `struts.locale` is unset (branch 1) or set but
malformed/unparseable (branch 2). In neither case is a configured `struts.locale`
usable as a fallback, so in practice step 4 resolves to `Locale.getDefault()` today.
The "configured `struts.locale` if set and parseable" clause is retained as the
helper's general contract (matching the `I18nInterceptor` spirit and keeping the
helper self-contained), but it is inert at the present call sites — the
implementation must not add dead logic that assumes it fires.

**Reuse vs. duplication.** `Dispatcher` runs before an `ActionContext` /
`LocaleProvider` is necessarily available, so the helper calls
`LocaleUtils.isAvailableLocale(...)` directly — the same underlying check
`I18nInterceptor` reaches through `LocaleProvider.isValidLocale`. The two paths share
the same semantics without sharing a code path. Consolidating them into one shared
locale-resolution component is deliberately out of scope.

**Injection:** `@Inject(value = "struts.locale.validateRequestLocale",
required = false)` setter on `Dispatcher`, parsed to boolean, consistent with how
`struts.locale` (`defaultLocale`) is already injected.

**Docs:** new constant in `default.properties`, commented, near `struts.locale`
("restrict request-derived locales to the JVM's available-locale set").

## Testing

Core tests extend `XWorkTestCase` and are JUnit 3/4 — no JUnit-5 `@Test`
annotations (they would silently not run). Test names stay neutral
(bounds/eviction/resolution).

### Part 1

- **Bound invariant** (the regression test): with a small configured
  `cacheMaxSize` (e.g. 1000), drive `findText` with many distinct locales
  (e.g. 50,000); assert each of the five caches stays on the order of the bound,
  not the number of distinct locales.
- **Correctness under eviction:** normal and repeated locales still return the
  correct localized text; a real bundle key still resolves under eviction
  pressure; the `NOT_FOUND` sentinel path still works.
- **Configurability:** `cacheMaxSize` changes the ceiling; `cacheType` selects the
  implementation.
- **Clear/remove:** `reloadBundles` empties the caches; `clearBundle` removes the
  targeted entry.

### Part 2 (extend the existing `Dispatcher` test)

- Flag **off** (default): any request locale passes through unchanged, including a
  valid-but-unavailable one (proves zero behaviour change).
- Flag **on** + available locale → returned unchanged.
- Flag **on** + valid-but-unavailable locale → falls back to configured
  `struts.locale` when set, otherwise `Locale.getDefault()`.

## Backward compatibility

- Part 1 changes internal cache implementations only; no public API or behavioural
  change for normal localized-text usage. Eviction is correctness-safe. Default
  `cacheMaxSize=10000` matches existing cache conventions. `protected` cache fields
  change type — acceptable for internal framework state; noted for subclasses.
- Part 2 is fully opt-in; default `false` preserves current behaviour exactly.
- The added `OgnlCache.remove` is additive.

## Out of scope / follow-ups (separate tickets)

- Rename `OgnlCache*` → `StrutsCache*` to remove the `Ognl` name leak now that the
  abstraction is used outside OGNL. (Separate ticket.)
- Per-cache size constants (distinct bounds for the hierarchy caches vs.
  bundle/format/missing caches). (Separate ticket.)
- Consolidating `Dispatcher` and `I18nInterceptor` locale resolution into one
  shared component. (Not planned.)
