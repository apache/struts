# WW-5770 — Scopable `struts.convention.result.path`

**Ticket:** [WW-5770](https://issues.apache.org/jira/browse/WW-5770) — *Make struts.convention.result.path scopable per package*
**Target:** 7.5.0 (ticket fix version)
**Date:** 2026-10-14
**Status:** Implemented — [PR #2009](https://github.com/apache/struts/pull/2009)

## Problem

WW-3476 added `<scoped-constant>`; WW-5768 made the UI theme its first adopter. Convention needs more than a
`ScopableConstants` bean, because `ScopedConstantProvider.getValue(name)` resolves the package from the current
`ActionInvocation`, and Convention reads the result path where there is none:

1. **Startup** — `DefaultResultMapBuilder.build(...)` → `ConventionsService.determineResultPath(Class)`. No
   invocation exists, so `getValue(name)` always returns the global value. `build()` does receive the action's
   `PackageConfig` (`PackageBasedActionConfigBuilder` passes `pkgCfg.build()`, created with `addParent(parentPkg)`),
   and its `getAllScopedConstants()` already includes the parent's overrides.
2. **Runtime** — `ConventionUnknownHandler.determinePath(...)` → `determineResultPath(ActionConfig)`, where the
   `ActionConfig` is `null` for an action-less request.

## Goals

- A parent package sets the Convention result path for every action under it:

  ```xml
  <package name="admin-base" extends="convention-default">
      <scoped-constant name="struts.convention.result.path" value="/WEB-INF/admin/"/>
  </package>
  ```

- Precedence: `@ResultPath` on the action class or package → the package's scoped constant (inherited ones
  included) → the global `struts.convention.result.path`.
- Custom `ConventionsService` implementations keep compiling and behaving as before.
- A swapped-in `struts.scopedConstantProvider` governs the result path too, as it does the UI theme.

## Non-goals

- No deprecation of the one-argument `determineResultPath` methods: they still mean "the path without package
  scope".
- No namespace-based package lookup for action-less requests (see Decisions).

## Decisions

- **Action-less requests use the default parent package.** `handleUnknownAction` has no `ActionConfig`; it resolves
  the scoped value against `struts.convention.default.parent.package`, the package it already uses for result
  types. An override on another package does not apply to action-less JSPs under its namespace. Namespace lookup
  was rejected: several packages can share a namespace and Convention generates one per parent + namespace.
- **Resolve through `ScopedConstantProvider`, not `PackageConfig` directly.** Reading
  `getAllScopedConstants()` in Convention would bypass a custom provider and duplicate the lookup.
- **`ConventionsServiceImpl` keeps its constructor.** The injected global `resultPath` stays the fallback for a
  `null` package; the provider is called only when there is a package. Test sites that pass a package
  (all of `DefaultResultMapBuilderTest`) inject a lambda provider through a helper.
- **The new provider method is `default`.** `ScopedConstantProvider` is stubbed as a lambda in 23 test sites
  (`name -> null`); an abstract method would break them and any lambda-based user provider. The default returns
  the package's value when it declares one, else `getValue(name)`. `StrutsScopedConstantProvider` overrides it:
  a non-null package without the constant gives the global value, never the current invocation's package — a
  nested request (`<s:action>`, chain) reaches `handleUnknownAction` with the caller's invocation still bound.
  A `null` package still means `getValue(name)`.

## Design

### Core — `org.apache.struts2.config`

- `ScopedConstantProvider` gains `default String getValue(String name, PackageConfig packageConfig)`,
  `@since 7.5.0`: the package's `getAllScopedConstants()` value when present, else `getValue(name)`. A `null`
  package goes straight to `getValue(name)`. The interface stays functional.
- `StrutsScopedConstantProvider` overrides it: name check, then the package's value or the global value; a `null`
  package delegates to `getValue(name)`.

### Convention plugin

- `ConventionScopableConstants implements ScopableConstants` returns `Set.of(CONVENTION_RESULT_PATH)`, registered
  in `struts-plugin.xml` as a named `ScopableConstants` bean (pattern: `StrutsUiScopableConstants`).
- `ConventionsService` gains two `default` methods:
  - `determineResultPath(Class<?> actionClass, PackageConfig packageConfig)` → `determineResultPath(actionClass)`
  - `determineResultPath(ActionConfig actionConfig, PackageConfig packageConfig)` → `determineResultPath(actionConfig)`
- `ConventionsServiceImpl`:
  - `@Inject setScopedConstantProvider(ScopedConstantProvider)` — setter, so the constructor is unchanged.
  - `@ResultPath` handling extracted to a private `findAnnotatedResultPath(Class)`.
  - Two-argument `Class` form: annotation if present; else, with a package,
    `scopedConstantProvider.getValue(CONVENTION_RESULT_PATH, packageConfig)`; with a `null` package, `resultPath`.
  - Two-argument `ActionConfig` form: `null` action → the package's scoped value (or `resultPath` for a `null`
    package); otherwise load the class and delegate, keeping the `ClassNotFoundException` handling.
  - One-argument forms are unchanged in behaviour.
- `DefaultResultMapBuilder.build(...)` calls `determineResultPath(actionClass, packageConfig)`.
- `ConventionUnknownHandler.determinePath(ActionConfig, PackageConfig, String namespace)` — signature change
  (protected): `handleUnknownAction` passes `parentPackage`; `handleUnknownResult` passes the `pkg` it already
  resolves from `actionConfig.getPackageName()`.

### Error handling

Nothing new. A misspelled name, or the constant used without the Convention plugin, already fails at startup in
`DefaultConfiguration.validateScopedConstants`. `${...}` substitution applies via WW-5767. A scoped value is
treated exactly like the global one, including trailing-slash handling.

### Startup ordering — verify, don't assume

`PackageBasedActionConfigBuilder` runs inside `loadPackages`; `StrutsScopedConstantProvider.setContainer` fetches
`Configuration` and collects scopable names when injected. The integration test below proves both are available at
that point, with the Convention `ScopableConstants` bean already registered.

## Testing

Each test goes into the existing file, in that file's style.

| File (style) | Cases |
|---|---|
| `StrutsScopedConstantProviderTest` (`XWorkTestCase`) | `getValue(name, pkg)` returns an inherited override; an explicit package wins over the invocation's; a package without the constant and a `null` package fall back to `getValue(name)`; non-scopable name throws; a lambda provider gets the package lookup |
| `ConventionsServiceImplTest` — new (JUnit 4) | `@ResultPath` beats the override; override beats global; `null` package → global |
| `DefaultResultMapBuilderTest` (JUnit 4) | parent-package override moves results located at startup; no override → global path |
| `ConventionUnknownHandlerTest` (`TestCase`, EasyMock) | action-less request uses the default parent package's override; `handleUnknownResult` uses the action package's override; existing `determineResultPath(actionConfig)` expectations move to the two-argument form |
| Startup integration (harness located during planning) | the real convention `struts-plugin.xml` plus an XML parent package declaring the constant boots; a Convention action under it gets the overridden locations |

## Docs and migration

- **struts-site:** list the constant in the scoped-constants section of `core-developers/package-configuration.md`;
  add a section to the Convention plugin page showing the precedence.
- **7.5.0 migration notes:** new default method `ScopedConstantProvider.getValue(String, PackageConfig)` (custom
  providers may override it); `ConventionUnknownHandler.determinePath` signature change (an override without
  `@Override` silently stops being called); subclasses of `ConventionsServiceImpl` overriding the one-argument
  `determineResultPath` methods must override the two-argument forms, which the framework now calls.
