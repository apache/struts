# WW-3476 — Package-scoped constants

**Ticket:** [WW-3476](https://issues.apache.org/jira/browse/WW-3476) — *Allow overwriting of constants for each package*
**Target:** 7.5.0 (ticket fix version)
**Date:** 2026-10-10
**Status:** Design approved, pending implementation plan

## Problem

The ticket (2010, 2.2.1) asks to override constants per package — its examples are the ActionMapper, the locale
and the JSP location. Struts constants cannot do this today:

- Constants are global. They are injected once, at bootstrap, into container singletons
  (`@Inject(StrutsConstants.X)` in ~30 core classes) or read via `container.getInstance(String.class, name)`.
- `<package>` has no constant-like child (`struts-6.5.dtd`), and `PackageConfig` has nowhere to store one.

A scoped value therefore cannot transparently replace what a singleton already holds. Making every constant
package-aware would need a container per package — an 8.0-sized change. This design instead adds an opt-in
mechanism that fits 7.x.

## Goals

- A package can declare an override for a constant; child packages inherit it through `extends`.
- A component that wants a scoped value reads it at request time against the current invocation's package,
  falling back to the global constant.
- The shared singleton is never mutated — the same rule `WithLazyParams` follows since 7.3.0.
- A misconfigured override fails at startup, never silently.
- Purely additive: no published DTD, constructor or interface changes.

## Non-goals

- **No adopters in this change.** No existing constant becomes scopable. The mechanism ships alone; each adopter
  (UI theme, i18n, Convention result path, …) is a follow-up ticket.
- No `${...}` expressions — values are static strings.
- No action-level scope (`<scoped-constant>` inside `<action>`).
- No Convention-plugin annotation. Convention-built packages extend an XML parent package and inherit its
  overrides.
- No typed conversion — values are `String`, as with `container.getInstance(String.class, name)`.
- No user-overridable provider (no `StrutsBeanSelectionProvider` alias / new constant) until there is a need.

## Inherently non-scopable constants

Anything consumed before the action is resolved can never be scoped, because the package is not yet known:
the ActionMapper (it is what determines the package), multipart parsing limits (`struts.multipart.*`, applied
in `Dispatcher` before mapping), and similar bootstrap/dispatch settings. The design does not special-case
these — they are simply never registered as scopable. The per-URL-space ActionMapper need from the ticket is
already served by `PrefixBasedActionMapper` (`struts.mapper.prefixMapping`, WW-3260).

Security switches consumed at request time (`struts.parameters.requireAnnotations`, CSP nonce source,
`struts.enable.DynamicMethodInvocation`, `devMode`) are technically scopable but should not be registered
without a deliberate decision: a package-level override could quietly relax an application-wide hardening flag.

## Design

### 1. Configuration syntax — `struts-7.5.dtd`

A new element, distinct from the global `<constant>` so it is never mistaken for one:

```xml
<!DOCTYPE struts PUBLIC
    "-//Apache Software Foundation//DTD Struts Configuration 7.5//EN"
    "https://struts.apache.org/dtds/struts-7.5.dtd">
<struts>
    <package name="admin" extends="struts-default" namespace="/admin">
        <scoped-constant name="some.scopable.constant" value="x"/>
        <action name="..." class="..."/>
    </package>
</struts>
```

- New `core/src/main/resources/struts-7.5.dtd`: a copy of `struts-6.5.dtd` with
  - `scoped-constant*` added first in the `package` content model;
  - `<!ELEMENT scoped-constant EMPTY>` with `name CDATA #REQUIRED` and `value CDATA #REQUIRED`;
  - the header comment's public id / URL updated to 7.5.
- Registered in `StrutsXmlConfigurationProvider` next to the 6.5 mapping (`StrutsXmlConfigurationProvider.java:54`).
- Published DTDs are unchanged. Precedent: WW-5409 added `struts-6.5.dtd` for the single `final` attribute.
- The DTD name follows the ticket's fix version; if the release ships under another number, rename it with it.

### 2. Storage — `PackageConfig`

- New field: the package's own scoped constants, `Map<String, String>`, insertion-ordered.
- `Builder.addScopedConstant(String name, String value)`.
- `getScopedConstants()` — own declarations only (unmodifiable).
- `getAllScopedConstants()` — merged map: parents first, then own entries, so the nearest declaration wins.
  Multiple parents merge in the same `putAll` order as `getAllResultTypeConfigs()`.
- The merged map is computed eagerly in `Builder.build()` and stored unmodifiable. Parents are always built
  before their children, so the request-time lookup does not walk the hierarchy.
- The copy constructor `PackageConfig(PackageConfig orig)` copies both maps.

### 3. Parsing — `XmlDocConfigurationProvider`

- `addPackage(Element)` reads `scoped-constant` children and calls `addScopedConstant`.
- A duplicate `name` within one package is a `ConfigurationException` carrying the element's location.
- Under an older DOCTYPE, the validating parser already rejects the element — no extra code.

### 4. Registry — `ScopableConstants`

```java
package org.apache.struts2.config;

public interface ScopableConstants {
    Set<String> getNames();
}
```

- Adopters register a named bean, in `struts-beans.xml` (core) or a plugin's `struts-plugin.xml`:
  `<bean type="org.apache.struts2.config.ScopableConstants" name="ui" class="...UiScopableConstants"/>`
- The registry is the union of all registered beans' names, collected via
  `container.getInstanceNames(ScopableConstants.class)` — the existing pattern for `UnknownHandler`,
  `FileManager` and `PackageProvider`.
- This change registers no beans; only tests do.

### 5. Resolution — `ScopedConstantProvider`

```java
package org.apache.struts2.config;

public interface ScopedConstantProvider {
    String getValue(String name);
}
```

Default implementation `StrutsScopedConstantProvider`, bound in `struts-beans.xml`. It injects `Container` and
`Configuration` (the latter is a container factory, `DefaultConfiguration.java:291`). `getValue(name)`:

1. `name` not in the registry → `IllegalArgumentException`. Only an adopter's coding error reaches this; user
   configuration cannot.
2. No `ActionContext`, or no `ActionInvocation` on it (bootstrap, a JSP rendered without an action) → the global
   value, `container.getInstance(String.class, name)`.
3. Otherwise: the invocation's `ActionConfig.getPackageName()` → `configuration.getPackageConfig(...)` →
   `getAllScopedConstants().get(name)`; when absent, the global value.

Config reload needs no extra code: the container and `Configuration` are rebuilt, and lookups read the new
`PackageConfig`s.

### 6. Validation — `DefaultConfiguration.rebuildRuntimeConfiguration()`

After all package providers have loaded, for every package, each name in `getScopedConstants()` (own
declarations — inherited ones were validated on the parent) must be in the registry. Otherwise a
`ConfigurationException`:

> Package [admin] declares scoped constant [some.constant], which is not scopable. Scopable constants: [a, b].

or, when the registry is empty, "No constants are scopable in this configuration." The exception carries the
package's location (`PackageConfig` is `Located`); per-element locations are not stored.

## Testing

JUnit 3/4 only — match each target file's existing style; no Jupiter.

- **Parsing** — `XmlConfigurationProviderPackagesTest` (extends `ConfigurationTestBase`), with new fixtures next to
  `xwork-test-package-final.xml`:
  - a 7.5 fixture with `<scoped-constant>` loads, and the values land on `PackageConfig`;
  - a duplicate name in one package fails with `ConfigurationException`;
  - `<scoped-constant>` under the 6.5 DOCTYPE fails validation;
  - existing 6.5/6.0/2.x fixtures still load (covered by the current suite).
- **Merging** — `PackageConfigTest`: child overrides parent; grandchild inherits through two levels; a package
  with no declarations inherits everything; the multiple-parents winner is pinned.
- **Validation** — a test-only `ScopableConstants` bean: a registered name loads; an unregistered name throws with
  package and constant in the message; with no beans registered, any `<scoped-constant>` throws.
- **Provider** — `StrutsScopedConstantProviderTest`: the package value wins inside an invocation; the global value
  is returned with no context, no invocation, or no override; an inherited value resolves for a child package;
  an unregistered name throws; the global `container.getInstance(String.class, name)` is unchanged after a scoped
  read.
- **Watch it fail** — mutate each guarded behaviour (merge order, the validation call, the fallback) with Edit,
  confirm via `git diff --stat`, see the test go red, restore.

## Compatibility

Additive only: a new DTD, a new element, two new interfaces, one new default bean, new `PackageConfig` methods.
Existing constructors, builders, interfaces and published DTDs are untouched — suitable for the 7.x line.

## Delivery

1. Core PR: `WW-3476 feat(config): add package-scoped constants`.
2. struts-site PR: publish `dtds/struts-7.5.dtd`, and document `<scoped-constant>` and the adopter API
   (`WW-3476 docs: ...`). The public DTD URL must resolve before the release ships.
3. Version Notes entry at release time.
4. Follow-up tickets, one per adopter, filed when wanted.
