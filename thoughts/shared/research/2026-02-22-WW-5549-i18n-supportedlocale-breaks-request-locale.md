---
date: 2026-02-22T12:00:00+01:00
topic: "I18nInterceptor supportedLocale disables request_locale parameter"
tags: [research, codebase, i18n, interceptor, locale, WW-5549]
status: complete
git_commit: a21c763d8a8592f1056086134414123f6d8d168d
---

# Research: WW-5549 - I18nInterceptor supportedLocale disables request_locale

**Date**: 2026-02-22

## Research Question

When `supportedLocale` is configured on the i18n interceptor, the `request_locale` parameter stops working if the browser's Accept-Language header matches a supported locale.

## Summary

The bug is in the class hierarchy of `LocaleHandler` implementations. `AcceptLanguageLocaleHandler.find()` returns early when it finds a match between the browser's Accept-Language header and the `supportedLocale` set. Since `SessionLocaleHandler` and `CookieLocaleHandler` both extend `AcceptLanguageLocaleHandler` and call `super.find()` first, the explicit `request_locale` parameter is never checked when the Accept-Language header matches a supported locale.

## Detailed Findings

### Class Hierarchy

```
LocaleHandler (interface)
  └── RequestLocaleHandler          (storage=REQUEST, checks request_only_locale)
        └── AcceptLanguageLocaleHandler  (storage=ACCEPT_LANGUAGE, checks Accept-Language header)
              ├── SessionLocaleHandler   (storage=SESSION, checks request_locale + session)
              └── CookieLocaleHandler    (storage=COOKIE, checks request_cookie_locale + cookie)
```

### The Bug: AcceptLanguageLocaleHandler.find() — Line 279

```java
// I18nInterceptor.java:279-290
@Override
public Locale find() {
    if (!supportedLocale.isEmpty()) {
        Enumeration locales = actionInvocation.getInvocationContext().getServletRequest().getLocales();
        while (locales.hasMoreElements()) {
            Locale locale = (Locale) locales.nextElement();
            if (supportedLocale.contains(locale)) {
                return locale;  // ← RETURNS HERE, never calls super.find()
            }
        }
    }
    return super.find();  // ← Only reached if supportedLocale is empty or no match
}
```

### SessionLocaleHandler.find() — Line 301

```java
// I18nInterceptor.java:300-317
@Override
public Locale find() {
    Locale requestOnlyLocale = super.find();  // ← calls AcceptLanguageLocaleHandler.find()

    if (requestOnlyLocale != null) {
        LOG.debug("Found locale under request only param, it won't be stored in session!");
        shouldStore = false;          // ← prevents session storage
        return requestOnlyLocale;     // ← returns WITHOUT checking request_locale
    }

    // request_locale is only checked here, which is never reached when super.find() returns non-null
    Parameter requestedLocale = findLocaleParameter(actionInvocation, parameterName);
    if (requestedLocale.isDefined()) {
        return getLocaleFromParam(requestedLocale.getValue());
    }
    return null;
}
```

### Concrete Bug Scenario

Configuration: `supportedLocale="fr,en"`, storage=SESSION (default)

1. User has French browser (`Accept-Language: fr,en`)
2. App defaults to French — correct
3. User clicks "English" link with `?request_locale=en`
4. `SessionLocaleHandler.find()` calls `super.find()` → `AcceptLanguageLocaleHandler.find()`
5. Accept-Language header yields `fr`, which IS in `supportedLocale`
6. Returns `fr` immediately — `request_locale=en` is **never checked**
7. `shouldStore = false` — so even if it were checked, it wouldn't persist
8. Locale stays French despite explicit user request to switch to English

### intercept() Flow

```java
// I18nInterceptor.java:117-144
LocaleHandler localeHandler = getLocaleHandler(invocation);  // SessionLocaleHandler for default
Locale locale = localeHandler.find();       // BUG: returns Accept-Language match, skips request_locale
if (locale == null) {
    locale = localeHandler.read(invocation); // never reached when find() returns non-null
}
if (localeHandler.shouldStore()) {
    locale = localeHandler.store(invocation, locale); // shouldStore=false, skipped
}
useLocale(invocation, locale);  // sets the wrong locale
```

### Root Cause

`SessionLocaleHandler.find()` was designed to call `super.find()` to check `request_only_locale` (a non-persistent locale override). It interprets any non-null result from `super.find()` as "a request-only locale was found." But `AcceptLanguageLocaleHandler.find()` conflates two different things:

1. A locale from `request_only_locale` parameter (legitimate non-persistent override)
2. A locale from Accept-Language header matching `supportedLocale` (ambient browser preference)

Both return non-null from `super.find()`, and `SessionLocaleHandler` cannot distinguish between them.

### The Same Bug Affects CookieLocaleHandler

`CookieLocaleHandler.find()` (line 369) has the identical pattern — calls `super.find()` and returns early if non-null, skipping `request_cookie_locale`.

## Code References

- [`I18nInterceptor.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/main/java/org/apache/struts2/interceptor/I18nInterceptor.java) — Full interceptor
  - Line 65: `supportedLocale` field declaration
  - Line 103-109: `setSupportedLocale()` — parses comma-delimited string to `Set<Locale>`
  - Line 117-144: `intercept()` — main flow
  - Line 152-167: `getLocaleHandler()` — factory for handler selection
  - Line 229-269: `RequestLocaleHandler` — base handler, checks `request_only_locale`
  - Line 271-292: `AcceptLanguageLocaleHandler` — **bug location** at line 279-290
  - Line 294-361: `SessionLocaleHandler` — **affected** at line 300-317
  - Line 363-419: `CookieLocaleHandler` — **also affected** at line 369-384
- [`I18nInterceptorTest.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/test/java/org/apache/struts2/interceptor/I18nInterceptorTest.java) — Test class
  - Line 266: `testAcceptLanguageBasedLocale` — only tests ACCEPT_LANGUAGE storage mode
  - Line 281: `testAcceptLanguageBasedLocaleWithFallbackToDefault` — fallback test

## Test Coverage Gaps

1. **No test** for `supportedLocale` + `request_locale` used simultaneously
2. **No test** for `supportedLocale` with SESSION storage mode (the default!)
3. **No test** for `supportedLocale` with COOKIE storage mode
4. Existing `supportedLocale` tests only use `ACCEPT_LANGUAGE` storage mode where the bug doesn't manifest (because `AcceptLanguageLocaleHandler` is used directly, not through `SessionLocaleHandler`)

## Fix Direction

The `request_locale` / `request_cookie_locale` parameter (explicit user choice) should always take precedence over the Accept-Language header (ambient browser preference). Options:

1. **Reorder in SessionLocaleHandler/CookieLocaleHandler**: Check `request_locale` **before** calling `super.find()`, so the explicit parameter always wins
2. **Reorder in AcceptLanguageLocaleHandler**: Check `super.find()` (request_only_locale) first, then fall back to Accept-Language matching — this would fix it for `AcceptLanguageLocaleHandler` itself but not for `SessionLocaleHandler`/`CookieLocaleHandler` which have their own `find()` override
3. **Restructure the hierarchy**: Separate Accept-Language matching from the `find()` chain so it doesn't interfere with explicit parameter checks

Option 1 is the most targeted fix with minimal risk.

## Open Questions

1. Should `supportedLocale` also validate/filter `request_locale` values? (e.g., reject `request_locale=es` if `supportedLocale="en,fr"`)
2. Should the session-stored locale also be validated against `supportedLocale` on subsequent requests?
3. The `Locale::new` constructor (line 107) is deprecated — should this be updated to use `Locale.forLanguageTag()` or `Locale.Builder`?