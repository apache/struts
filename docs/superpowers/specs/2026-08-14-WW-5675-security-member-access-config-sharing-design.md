# WW-5675 — Share parsed OGNL security configuration across `SecurityMemberAccess` instances

**Ticket:** [WW-5675](https://issues.apache.org/jira/browse/WW-5675) (sub-task of [WW-5667](https://issues.apache.org/jira/browse/WW-5667))
**Target:** 7.4.0
**Date:** 2026-08-14
**Status:** Design approved, pending implementation plan

## Problem

`SecurityMemberAccess` is a `Scope.PROTOTYPE` bean, registered in two places:

- `DefaultConfiguration.java:416` — `.factory(SecurityMemberAccess.class, Scope.PROTOTYPE)`
- `StrutsBeanSelectionProvider.java:456` — aliased to `STRUTS_MEMBER_ACCESS`, making it user-overridable

Every `container.getInstance(SecurityMemberAccess.class)` therefore constructs a fresh instance and re-runs all
sixteen `@Inject` configuration setters, each of which re-parses a raw comma-delimited string from scratch. With
the stock `struts-excluded-classes.xml` that is roughly 90 configuration entries per instantiation: comma
splitting, `strip`, classloader validation, `Pattern.compile`, and `HashSet` accumulation.

New instances are created on the request path from at least:

- `OgnlValueStackFactory.createValueStack(...)` — once per value stack, and `ParametersInterceptor` creates an
  additional stack per request
- `OgnlUtil.createDefaultContext(Object, ClassResolver)` at `OgnlUtil.java:738` — reached from `setProperties`,
  `copy`, `getBeanMap` and friends. Note `OgnlUtil.copy` calls it **twice** (`OgnlUtil.java:551-552`), so a single
  copy costs two full configuration rebuilds.

This is the dominant half of the parent report. The sibling ticket WW-5674 (merged as `81b34c295`) addressed the
per-*access* allocations; this ticket addresses the per-*instantiation* cost, which is where the reported 9% lives.

The fix proposed on the parent ticket — caching the parsed set in a `SecurityMemberAccess` field — cannot work,
because the instance holding the field is itself discarded and rebuilt each time.

### Also in scope

`ConfigParseUtil.validatePackageNames` (`ConfigParseUtil.java:143`) evaluates `Pattern.compile("\\s")` once per
package name rather than once overall — roughly 58 recompiles of a trivial pattern per instantiation under the
default configuration. Hoist it to a static constant.

## Goals

- Parse the OGNL security configuration once per container instead of once per `SecurityMemberAccess`.
- Preserve OGNL allow/deny semantics exactly. No configuration may become more permissive.
- Keep source compatibility for 7.4.0: existing subclasses and direct setter callers must continue to compile and
  behave identically. The five dev-mode setters are the one signed-off exception — see "`SecurityMemberAccess`
  changes".
- Collapse the two-set allowlist walk introduced by WW-5674 into a single precomputed set.

## Non-goals

- Changing array/primitive package-resolution semantics — that is WW-5676, deliberately separate because it is a
  security-semantics decision rather than a performance fix.
- Removing the residual per-access `getPackage()` lookups — that is WW-5677.
- Removing the deprecated setters. They are scheduled for 8.0.0 (see Follow-ups).
- Adding JMH or any benchmarking infrastructure to the build.

## Approach

Introduce a container-singleton configuration bean that owns all parsing. `SecurityMemberAccess` stays
`Scope.PROTOTYPE` and copies immutable set *references* out of that bean.

Two alternatives were considered and rejected:

**Memoize parsing inside `ConfigParseUtil`** (keyed by raw config string, following the existing Caffeine
precedent in that file). Smallest possible diff and no API change, but it recovers the least: every instantiation
still invokes sixteen setters, still builds the accumulated `HashSet` copies, and still runs the lazy dev-mode
flip. It also does not unblock the allowlist union collapse.

**Revert `SecurityMemberAccess` to `Scope.SINGLETON`**, relocating `acceptProperties`/`excludeProperties` into the
OGNL context. Largest theoretical win, but it reverses a deliberate WW-5343 decision, converts two fields into
shared mutable state requiring thread-safety on the OGNL security gate, and changes the `MemberAccessValueStack`
contract that `ParametersInterceptor` depends on. Under the chosen approach the per-instantiation cost is already
about a dozen reference copies, so this buys very little for substantially more risk.

## Design

### New bean: `SecurityMemberAccessConfig`

Registered in `DefaultConfiguration` beside the existing internal singletons:

```java
.factory(SecurityMemberAccessConfig.class, Scope.SINGLETON)
```

Concrete class, no interface, **not** aliased in `StrutsBeanSelectionProvider`. It is internal plumbing, following
the shape of `ProviderAllowlist` and `ThreadAllowlist` (`DefaultConfiguration.java:418-419`), not a user extension
point.

**The bean must be registered in two places.** An earlier draft of this design claimed `bootstrapFactories` was on
the production path because `ConfigurationManager.addDefaultContainerProviders` (`ConfigurationManager.java:94`)
registers `StrutsDefaultConfigurationProvider`, which calls it at
`StrutsDefaultConfigurationProvider.java:116`. **That claim is wrong**, and it was only caught when the full core
suite failed with 1579 errors during implementation.

`ConfigurationManager.addDefaultContainerProviders()` fires only when `containerProviders.isEmpty()`
(`ConfigurationManager.java:78-80`). `Dispatcher.init()` (`Dispatcher.java:711-719`) installs its own provider
list — including `StrutsBeanSelectionProvider` via `init_AliasStandardObjects` — so the list is never empty and
`StrutsDefaultConfigurationProvider` is never added. The production container is built from
`StrutsBeanSelectionProvider` plus `struts-beans.xml`, and `bootstrapFactories` is not on the path of that *main
Dispatcher* container.

It is, however, on a different, load-bearing path: `DefaultConfiguration.reloadContainer` builds a **bootstrap**
container from `bootstrapFactories` (`DefaultConfiguration.java:283`, via `createBootstrapContainer` at
`DefaultConfiguration.java:348-373`), then calls `setContext(bootstrap)` (`DefaultConfiguration.java:307`), which
calls `bootstrap.getInstance(ValueStackFactory.class).createValueStack()` — and building a value stack instantiates
`SecurityMemberAccess` through `CompoundRootAccessor`/`RootAccessor`. So the bootstrap container's registration
of `SecurityMemberAccessConfig` is not a fallback for some other, unused path: it is exercised on every
`reloadContainer()` call, before the main Dispatcher container even exists.

The registration therefore goes in both places, which is precisely what `ProviderAllowlist` and `ThreadAllowlist`
already do — `DefaultConfiguration.java:418-419` and `struts-beans.xml:175-176`:

```xml
    <bean class="org.apache.struts2.ognl.SecurityMemberAccessConfig"/>
```

The `DefaultConfiguration` registration serves the bootstrap container (`DefaultConfiguration.java:360`) and the
`XWorkTestCase` harness; the `struts-beans.xml` entry serves the real Dispatcher container. **Both registrations
are load-bearing** — production would throw at startup without either, since `useConfig` is a mandatory `@Inject`
on `SecurityMemberAccess`. The bootstrap container carries only `BOOTSTRAP_CONSTANTS`, so most security constants
are absent there, the `required = false` setters do not fire, and the bean falls back to defaults — exactly as a
`SecurityMemberAccess` constructed in that container behaves today.

This failure mode is loud, not silent: `useConfig` is a mandatory `@Inject`, so a container missing the binding
fails closed with a `DependencyException`, not by running with empty exclusions. Because `SecurityMemberAccess` is
`Scope.PROTOTYPE` and `ContainerImpl`'s injector cache is built lazily, that exception fires at the first
`getInstance(SecurityMemberAccess.class)` rather than at `builder.create(...)` — still loud and still fail-closed,
just not at container-build time.

The `TODO: SpringObjectFactoryTest fails when these are SINGLETON` comment at the top of `bootstrapFactories`
applies to the `*Factory` beans in the first block, not to this region, where singletons are already the norm.

It takes over these sixteen `@Inject` setters from `SecurityMemberAccess`:

| Setter | Constant |
|---|---|
| `useAllowStaticFieldAccess` | `STRUTS_ALLOW_STATIC_FIELD_ACCESS` |
| `useExcludedClasses` | `STRUTS_EXCLUDED_CLASSES` |
| `useExcludedPackageNamePatterns` | `STRUTS_EXCLUDED_PACKAGE_NAME_PATTERNS` |
| `useExcludedPackageNames` | `STRUTS_EXCLUDED_PACKAGE_NAMES` |
| `useExcludedPackageExemptClasses` | `STRUTS_EXCLUDED_PACKAGE_EXEMPT_CLASSES` |
| `useEnforceAllowlistEnabled` | `STRUTS_ALLOWLIST_ENABLE` |
| `useAllowlistClasses` | `STRUTS_ALLOWLIST_CLASSES` |
| `useAllowlistPackageNames` | `STRUTS_ALLOWLIST_PACKAGE_NAMES` |
| `useDisallowProxyObjectAccess` | `STRUTS_DISALLOW_PROXY_OBJECT_ACCESS` |
| `useDisallowProxyMemberAccess` | `STRUTS_DISALLOW_PROXY_MEMBER_ACCESS` |
| `useDisallowDefaultPackageAccess` | `STRUTS_DISALLOW_DEFAULT_PACKAGE_ACCESS` |
| `useDevMode` | `STRUTS_DEVMODE` |
| `useDevModeExcludedClasses` | `STRUTS_DEV_MODE_EXCLUDED_CLASSES` |
| `useDevModeExcludedPackageNamePatterns` | `STRUTS_DEV_MODE_EXCLUDED_PACKAGE_NAME_PATTERNS` |
| `useDevModeExcludedPackageNames` | `STRUTS_DEV_MODE_EXCLUDED_PACKAGE_NAMES` |
| `useDevModeExcludedPackageExemptClasses` | `STRUTS_DEV_MODE_EXCLUDED_PACKAGE_EXEMPT_CLASSES` |

`setProxyService` and the `@Inject` constructor stay on `SecurityMemberAccess` — those inject collaborators, not
configuration.

The bean implements `Initializable`. Dev-mode resolution cannot happen inside any individual setter, because
`ContainerImpl.addInjectorsForMembers` iterates `getDeclaredMethods()`, whose order the JDK explicitly leaves
unspecified. `Initializable.init()` runs after the whole dependency graph is built
(`InitializableFactory.wrapIfNeeded`, applied from `Scope` for singleton scope; `DefaultValidatorFactory` is the
existing precedent). `init()` therefore:

1. Selects the effective excluded sets — dev-mode variants when `struts.devMode=true`, otherwise the normal ones.
2. Precomputes the allowlist package union.

The bean exposes only immutable getters, and publishes the **effective** excluded sets with dev-mode already
applied, so nothing downstream needs to know dev-mode exists.

### `SecurityMemberAccess` changes

Gains exactly one injected member:

```java
@Inject
public void useConfig(SecurityMemberAccessConfig config) { … }
```

which seeds its fields by copying immutable set references — no parsing, no `HashSet` construction, no
`Pattern.compile`.

**Fields removed:** `isDevModeInit` (volatile), `isDevMode`, `devModeExcludedClasses`,
`devModeExcludedPackageNamePatterns`, `devModeExcludedPackageNames`, `devModeExcludedPackageExemptClasses`.

**Method removed:** `useDevModeConfiguration()`, along with its call from `checkExclusionList`
(`SecurityMemberAccess.java:264`). The lazy dev-mode flip disappears from the access path entirely.

**Field added:** `allowlistPackageNamesUnion`.

The five dev-mode setters are **deleted outright rather than deprecated** — decided 2026-08-14. This is a
deliberate, signed-off deviation from the "additive and deprecate, no breakage in a minor" policy that governs the
rest of this change.

They are `public`, but only ever container-injected, with no direct caller anywhere in core, plugins, or tests.
Preserving them faithfully would mean keeping `isDevMode` plus the four dev-mode set fields on the instance and
reinstating some form of the lazy flip — that is, keeping precisely the code this change exists to delete, to
serve a caller that does not demonstrably exist. Retention in simplified form was rejected because today's
semantics are subtle enough that any simplification would silently change them: a manual
`useDevModeExcludedClasses` call accumulates into the dev-mode set, which then *replaces* — rather than unions
with — `excludedClasses` on first access.

The accepted risk is that a deployment calling these methods directly breaks at compile time on upgrade to 7.4.0.
This is a loud, immediate failure with an obvious fix, not a silent behavioural change, which is what makes it
acceptable where the constructor break discussed below was not.

The remaining eleven configuration setters stay as `@Deprecated` methods with their `@Inject` annotations removed.
They keep mutating that instance exactly as they do now. Deprecation is by annotation only — no runtime warnings,
which would flood test output given roughly 110 direct call sites across core and plugins.

`useAllowStaticFieldAccess` retains its side effect of calling `useExcludedClasses(Class.class.getName())`, and
the configuration bean must reproduce that accumulation exactly.

### Why setter injection rather than constructor injection

Constructor injection would be the obvious way to guarantee ordering, but it forces a constructor signature
change. A user subclass calling `super(providerAllowlist, threadAllowlist)` — precisely the shape of the existing
`ExternalSecurityMemberAccess` test fixture — would then either fail to compile, or, if a deprecated 2-arg
overload were retained, compile cleanly and silently run with empty exclusions. **That is a fail-open hole**, and
the kind that fails silently rather than loudly.

Setter injection avoids it: `ContainerImpl.addInjectors` recurses into superclasses first
(`ContainerImpl.java:97`), so inherited `@Inject` setters are injected on subclass instances. Existing subclasses
keep compiling *and* receive the configuration.

Injection ordering is safe by construction. Today the setters survive unspecified ordering only because they
*accumulate* rather than assign, making them commutative — a subtlety that is easy to destroy accidentally. After
this change `SecurityMemberAccess` has exactly one injected member touching those fields, so ordering stops
mattering at all.

A null configuration is also safe: the eight direct `new SecurityMemberAccess(null, null)` test sites never have
the setter called, so their fields keep today's hardcoded defaults. Reads only ever touch fields, never the
configuration object, so there is no null path on the access path.

### Allowlist union

With the sets precomputed per container, `isClassAllowlisted` collapses to a single set and a single walk:

```java
|| isClassBelongsToPackages(clazz, allowlistPackageNamesUnion);
```

This deletes the three-argument `isClassBelongsToPackages` overload and the two-set parameters on
`isPackageBelongsToPackages`, resolving WW-5678's first item as a side effect.

The ticket flagged this as a fail-open hazard: if the union were computed in two places — once seeded from
configuration, once when the deprecated `useAllowlistPackageNames` setter fires — the two could drift, silently
dropping `ALLOWLIST_REQUIRED_PACKAGES` from the allowlist with nothing failing loudly. It is also a fail-open
hazard if the union is *re-computed* per instance: that reintroduces exactly the per-instantiation `HashSet`
allocation this ticket exists to remove, and lands on the deployments that configure the allowlist properly,
inverting the ticket's intent.

Both hazards are avoided by moving `ALLOWLIST_REQUIRED_PACKAGES` and the `union(...)` helper onto
`SecurityMemberAccessConfig`, which precomputes `allowlistPackageNamesUnion` once, inside its own
`useAllowlistPackageNames` setter, when the constant fires during container construction:

```java
// SecurityMemberAccessConfig
static final Set<String> ALLOWLIST_REQUIRED_PACKAGES = Set.of(
        "org.apache.struts2.validator.validators",
        "org.apache.struts2.components",
        "org.apache.struts2.views.jsp"
);

public void useAllowlistPackageNames(String commaDelimitedPackageNames) {
    this.allowlistPackageNames = toPackageNamesSet(commaDelimitedPackageNames);
    this.allowlistPackageNamesUnion = union(ALLOWLIST_REQUIRED_PACKAGES, allowlistPackageNames);
}

static Set<String> union(Set<String> required, Set<String> configured) { … }
```

`SecurityMemberAccess.useConfig` copies the precomputed reference (`config.getAllowlistPackageNamesUnion()`) —
no allocation on the hot instantiation path. Its deprecated `useAllowlistPackageNames` setter, which still mutates
a single instance directly and has no `SecurityMemberAccessConfig` to read from, calls the same
`SecurityMemberAccessConfig.union(...)` static method. Both routes therefore funnel through the one method, so
exactly one line in the codebase computes the union, and `ALLOWLIST_REQUIRED_PACKAGES` cannot drift out of it
through a second implementation. The constant and helper live on the config bean — the class that owns computing
and exposing configuration-derived state — rather than being duplicated onto `SecurityMemberAccess`, whose
deprecated setter merely calls back into it.

`isPackageBelongsToPackages` currently early-returns on `first.isEmpty() && second.isEmpty()`. Since
`ALLOWLIST_REQUIRED_PACKAGES` is never empty, that guard simply stops firing on the allowlist path; the exclusion
path, where both sets genuinely can be empty, keeps it. The guard was only ever an optimization, so this is not a
semantic change.

## Data flow

| Tier | Frequency | Work |
|---|---|---|
| `SecurityMemberAccessConfig` construction | Once per container | All parsing, class validation, pattern compilation, dev-mode resolution, union precomputation |
| `useConfig` | Once per `SecurityMemberAccess` | About a dozen immutable reference copies |
| `ParametersInterceptor` | Once per request | Sets `acceptProperties`/`excludeProperties` on the instance (unchanged) |
| `isAccessible` | Per OGNL member access | Field reads only |

## Error handling

Parsing failures — `ConfigurationException` for an unloadable class, an invalid regex, or whitespace in a package
name — move from being thrown on every instantiation to being thrown once, when the singleton is first built.
Still fatal, still loud, just earlier and once. Nothing degrades to a warning.

The `struts.allowlist.enable=false` warning already dedupes via `logWarningForFirstOccurrence`; moving it to the
configuration bean makes it a genuine once-per-container event.

## Behaviour changes

One, accepted during design review: the `"DevMode enabled, using DevMode excluded classes and packages for OGNL
security enforcement!"` warning currently fires on the first OGNL access and will now fire when the configuration
singleton's `init()` runs. The main Dispatcher container is built with `builder.create(false)` (lazy singletons), so
for it that is still triggered by first use — the config bean is constructed the first time something asks for a
`SecurityMemberAccess`, not at container-build/startup time. The bootstrap container does use `create(true)` and so
does log eagerly there. The change is still worth making — it moves the warning from being contingent on OGNL
traffic to being contingent on the config bean's first use, which happens earlier and more predictably — but it is
not a guaranteed startup-time log line for the main container.

No other externally visible behaviour changes. OGNL allow/deny semantics are identical.

## Testing

Core tests are JUnit 4 or extend `XWorkTestCase`. A JUnit 5 `@Test` added to these suites silently never runs.

1. **Sharing proof.** Request several `SecurityMemberAccess` instances from one container and assert their
   configuration-derived sets are reference-identical (`assertSame`, not `assertEquals`). Reference identity is a
   dependency-free proof that no re-parsing occurred, since any re-parse necessarily produces a fresh set; this is
   the sound substitute for a counting probe and is what the implementation actually asserts.
2. **Instance isolation.** Calling a deprecated setter on one instance must not perturb a sibling instance or the
   singleton. The sets are `unmodifiableSet`, so an in-place mutation bug would throw rather than corrupt
   silently, but this invariant deserves an explicit assertion.
3. **Subclass injection.** A subclass declaring the 2-arg constructor and calling
   `super(providerAllowlist, threadAllowlist)` must receive the configuration through the inherited setter. This
   is the test that would catch a future refactor to constructor injection reintroducing the fail-open hole.
4. **Behaviour preservation.** Following WW-5674's differential pattern: for default, dev-mode, and custom
   configurations, the sets the new bean publishes must equal what a legacy-style accumulation produces. This is
   where the `useAllowStaticFieldAccess` → `useExcludedClasses` side effect gets pinned down.
5. **Dev-mode.** With `struts.devMode=true` the effective sets are the dev-mode ones from the start, with no OGNL
   access required to trigger the switch.

The principal safety net is the existing suite. `SecurityMemberAccessTest` and its siblings drive these setters
directly from roughly 110 call sites across core and plugins and must pass untouched. If the deprecated setters
have kept their exact semantics, that suite cannot tell the difference — the strongest available evidence that
OGNL allow/deny semantics are unchanged.

## Risks

| Risk | Mitigation |
|---|---|
| Shared sets mutated in place, poisoning every instance in the container | Sets are already `unmodifiableSet`; test 2 asserts isolation explicitly |
| Allowlist union drifts from `ALLOWLIST_REQUIRED_PACKAGES` (fail-open) | Single computation site; test 4 covers custom allowlist configurations |
| Configuration bean fails to reproduce the accumulate-not-assign semantics | Test 4 is differential against the legacy accumulation, not against hand-written expectations |
| A future refactor moves configuration to constructor injection, reintroducing the silent fail-open | Test 3 encodes the subclass contract; the rationale is recorded above and in the class Javadoc |
| `Initializable` is documented "should be only used internally" | The bean is internal and unaliased; `DefaultValidatorFactory` is the existing precedent |

## Follow-ups

- **8.0.0 — remove the deprecated configuration setters.** The eleven methods left on `SecurityMemberAccess`
  should be removed once the major version allows it. To be filed as its own ticket, cross-referencing WW-5675 and
  WW-5678.
- **WW-5678** — its first item (the package-private overload sharing a name with a public method) is resolved for
  free here by the union collapse. The remaining visibility narrowing stays with that ticket.
- **WW-5667** — the parent should be updated to note that this ticket, not WW-5674, is the one expected to move
  the reported 9%.
- **Migration guide entry for 7.4.0** — the removal of the five dev-mode setters is a source-breaking change in a
  minor release and must be called out in the Version Notes and Migration Guide, however narrow the affected
  audience.
