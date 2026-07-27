# WW-5659: Request-scoped resolution of lazy interceptor params

**Jira:** [WW-5659](https://issues.apache.org/jira/browse/WW-5659)
**Type:** Bug
**Affects:** 7.2.0, 7.2.1
**Fix version:** 7.3.0
**Date:** 2026-07-27
**Reported via:** GitHub PR [#1815](https://github.com/apache/struts/pull/1815) (approach not adopted)

## Problem

`WithLazyParams.LazyParamInjector#injectParams` resolves `${...}` interceptor params
once per request and writes the resolved values straight onto the interceptor:

```java
// WithLazyParams.java:78-84
public Interceptor injectParams(Interceptor interceptor, Map<String, String> params, ActionContext invocationContext) {
    for (Map.Entry<String, String> entry : params.entrySet()) {
        Object paramValue = textParser.evaluate(new char[]{'$'}, entry.getValue(), valueEvaluator, TextParser.DEFAULT_LOOP_COUNT);
        ognlUtil.setProperty(entry.getKey(), paramValue, interceptor, invocationContext.getContextMap());
    }
    return interceptor;
}
```

That interceptor is a singleton, built once at configuration-parse time
(`InterceptorBuilder.java:73-74`, `:175-177`) and reused for every request. When an
action references a stack without overriding params, `InterceptorBuilder.java:79`
(`result.addAll(stackConfig.getInterceptors())`) hands the same `InterceptorMapping`
objects to every such action, so the instance is shared across actions too.

Request-scoped state is therefore written to process-wide state with no synchronisation.

### Consequence for file upload validation

`ActionFileUploadInterceptor` is the only `WithLazyParams` implementer today. Its
resolved policy lands in plain instance fields (`AbstractFileUploadInterceptor.java:62-64`,
written at `:85`, `:94`, `:103`) and is read back by `acceptFile` (`:134`, `:141`, `:148`).

Per request:

- `DefaultActionInvocation.java:269` — resolve, write the shared fields
- `DefaultActionInvocation.java:275` — `intercept()` → `acceptFile()` reads the shared fields

Nothing guards the interval. Two concurrent requests resolving different policies can have
their `allowedTypes`, `allowedExtensions` and `maximumSize` cross over, so a request can be
validated against another request's upload policy.

### `disabled` is affected too

`AbstractInterceptor.java:28` holds `disabled` as a shared `boolean`, written by
`setDisabled` (`:56-58`) and read by `shouldIntercept` (`:61-63`). It travels through
`injectParams` like any other param, so `<param name="disabled">${...}</param>` has the
same cross-request bleed — and it decides whether the interceptor runs at all.

Scope note: for interceptors that are *not* `WithLazyParams`, `setDisabled` is only ever
called at config time by `buildInterceptor`, never per request. `disabled` is racy only on
the lazy path.

### Secondary defect

`DefaultActionInvocation.java:262-267` calls `params.putAll(...)` on the map returned by
`InterceptorMapping#getParams` (`:60-62`), which is the live shared map — an unsynchronised
write to a shared `HashMap` on every request.

### Not affected

`struts-default.xml:60` declares `actionFileUpload` with no params, so a default
configuration resolves nothing and writes nothing. Static (non-expression) params resolve
to the same value on every request. Only applications opting into the dynamic `${...}`
form added in WW-5585 are affected.

`actionFileUpload` sits at position 15 of `defaultStack`, ahead of `staticParams` (19),
`actionMappingParams` (20) and `params` (21), so no request parameter has been bound to the
action at resolution time — the expression's *value* is not attacker-supplied. Which policy
is selected can still depend on request-derived state populated by `prepare` (11),
`scopedModelDriven` (13) or `modelDriven` (14); the shipped showcase does exactly that in
`DynamicFileUploadAction.java:136-139`.

This was assessed as a thread-safety defect rather than a framework vulnerability. Released
7.2.0 and 7.2.1 are not being backported.

## Decision

Fix the `WithLazyParams` contract rather than patching its one implementer, so no future
implementer can reintroduce the bug. Resolved params are written into a per-invocation
object the interceptor supplies and receives back; the interceptor singleton stays
immutable after `init()`.

Rejected alternatives:

- **Per-invocation copy of the interceptor** (`copyForInvocation()`): smallest diff, but a
  copy constructor rots silently when a field is added, it copies injected container
  dependencies, and it creates transient instances that never receive `destroy()`. It hides
  the mutable snapshot rather than separating config-time from request-time state.
- **Interceptor-owned resolution** (framework stops injecting, interceptor resolves raw
  templates itself): loses OGNL type conversion, duplicates resolution in every
  implementer, and silently changes what the setters mean.
- **`ThreadLocal` on the interceptor** (PR #1815): makes setter behaviour depend on hidden
  thread state, requires a cleanup call that any implementer can forget, and leaves the
  unsafe write path in place, merely bypassed.

This is a clean interface break in the next minor. `WithLazyParams` is public API since
2.5.9, but `ActionFileUploadInterceptor` is its only implementer in the repo; third-party
implementers get a compile error rather than silent breakage, noted in the migration guide.

## Components

### `InterceptorParams` (new)

`org.apache.struts2.interceptor.InterceptorParams` — the general contract for a params
holder, and the generic bound for `WithLazyParams`.

```java
public interface InterceptorParams {
    /** Notified when a {@code ${...}} param could not be resolved for this invocation. */
    default void unresolved(String paramName) { }
}
```

The default no-op keeps the interface free for implementers that do not care. `UploadPolicy`
overrides it to support fail-closed validation.

### `DisableParams` (new)

`org.apache.struts2.interceptor.DisableParams` — opt-in support for the `disabled` param.
A class, not an interface, because it holds state and needs a setter for OGNL.

```java
public class DisableParams implements InterceptorParams {
    private boolean disabled;
    public void setDisabled(String disable) { this.disabled = Boolean.parseBoolean(disable); }
    public boolean isDisabled() { return disabled; }
}
```

`disabled` is universal across interceptors while lazy resolution is rare, so `DisableParams`
is *not* a subtype of any lazy-specific type; both sit under `InterceptorParams`.

A holder that does not extend `DisableParams` has no `disabled` property, so a `disabled`
param on such an interceptor resolves to nothing and is reported by the unknown-param
warning below. There is deliberately no fallback to the singleton — that is the racy path
being removed. Interceptors supporting `disabled` must extend `DisableParams`.

### `WithLazyParams` (changed)

```java
public interface WithLazyParams<P extends InterceptorParams> {
    P newLazyParams();
    String intercept(ActionInvocation invocation, P lazyParams) throws Exception;
}
```

`AbstractInterceptor.java:48` declares `intercept(ActionInvocation)` abstract, and a
class-declared abstract method takes precedence over an interface default, so a `default`
single-arg implementation on this interface would not satisfy it. Implementers write the
one-line delegation themselves.

### `LazyParamInjector` (changed)

`injectParams(Interceptor, ...)` becomes `resolveInto(InterceptorParams target, ...)`. It
still uses `textParser.evaluate` followed by `ognlUtil.setProperty`, so OGNL type conversion
against the holder's typed setters is preserved (`maximumSize` still converts `String` →
`Long`).

Two behaviours are added:

- **Unresolved detection.** `OgnlTextParser.java:85-88` yields `""` for an expression that
  does not resolve and gives no signal distinguishing that from a legitimate empty value.
  The injector holds the raw template, so `raw.contains("${") && resolved.isEmpty()`
  recovers it. On unresolved: skip the write (the holder keeps its seeded config-time
  value), call `target.unresolved(paramName)`, and log a WARN naming interceptor, param and
  expression.
- **Unknown-param warning.** A param with no matching property on the holder currently
  no-ops silently, because `ognlUtil.setProperty` swallows the `OgnlException`
  (`OgnlUtil.java:297-299`). Log a WARN. Not a regression — a typo no-ops today too — but
  this design makes it more likely to matter.

### `DefaultActionInvocation` (changed)

`AbstractInterceptor.java:26` implements `ConditionalInterceptor`, so every interceptor
extending it — `ActionFileUploadInterceptor` included — takes the `instanceof
ConditionalInterceptor` branch at `:271` and is invoked through `executeConditional` →
`conditionalInterceptor.intercept(this)` at `:318`. That works today only because
`injectParams` had already mutated the singleton. Under the new contract the lazy and
conditional paths must be merged, or the single-arg `intercept` would run with unresolved
values and the dynamic policy would silently vanish.

```java
private <P extends InterceptorParams> String invokeWithLazyParams(WithLazyParams<P> lazy,
                                                                  InterceptorMapping mapping) throws Exception {
    P params = lazy.newLazyParams();
    lazyParamInjector.resolveInto(params, mergedParams(mapping), invocationContext);
    if (params instanceof DisableParams dp && dp.isDisabled()) {
        return invoke();
    }
    if (lazy instanceof ConditionalInterceptor ci && !ci.shouldIntercept(this)) {
        return invoke();
    }
    return lazy.intercept(this, params);
}
```

Checking both `isDisabled()` and `shouldIntercept` keeps custom `shouldIntercept` overrides
working while making the lazily-resolved `disabled` request-scoped. The singleton's
`disabled` field is never written on this path, so `AbstractInterceptor.shouldIntercept`
returns `true` and the holder is authoritative.

`mergedParams(mapping)` returns a fresh map instead of mutating the shared one, retiring the
`putAll` at `:262-267`.

### `UploadPolicy` (new)

`org.apache.struts2.interceptor.UploadPolicy` — top-level, not nested, because it appears in
the interceptor's public signature.

```java
public class UploadPolicy extends DisableParams {
    private Long maximumSize;
    private Set<String> allowedTypes = Collections.emptySet();
    private Set<String> allowedExtensions = Collections.emptySet();
    // typed setters (OGNL converts String -> Long for maximumSize), getters, copy()
    // overrides unresolved(String) to record dimensions that could not be resolved
}
```

### `AbstractFileUploadInterceptor` / `ActionFileUploadInterceptor` (changed)

The interceptor holds a single config-time `UploadPolicy` rather than keeping the three
fields at `:62-64` *and* adding a holder:

```java
private final UploadPolicy configuredPolicy = new UploadPolicy();

public void setAllowedTypes(String csv) { configuredPolicy.setAllowedTypes(csv); }   // config-time only
// ...
@Override public UploadPolicy newLazyParams() { return configuredPolicy.copy(); }
```

Public setter signatures are unchanged, so static `struts.xml` config and `buildInterceptor`
reflection are untouched. They are documented as config-time only, which is what they
already are — no framework code calls them outside OGNL injection.

`acceptFile` takes the policy explicitly, and `getMaximumSizeStr` (`:164-166`) takes the
value rather than reading a field:

```java
protected boolean acceptFile(UploadPolicy policy, Object action, UploadedFile file,
                             String originalFilename, String contentType, String inputName)
```

This is a `protected` break for any subclass overriding `acceptFile`.

`ActionFileUploadInterceptor implements WithLazyParams<UploadPolicy>`; its single-arg
`intercept` delegates to `intercept(invocation, newLazyParams())` so direct use outside the
lazy path still works.

## Data flow

**Config time (once at startup)** — unchanged. `InterceptorBuilder` →
`objectFactory.buildInterceptor(config, params)` reflects params onto the singleton,
populating `configuredPolicy` with literal values including any raw `${...}` text.
`InterceptorMapping` stores the raw param map.

**Request time (per invocation)**

1. `DefaultActionInvocation.invoke()` takes the next `InterceptorMapping`;
   `instanceof WithLazyParams<?>` routes to `invokeWithLazyParams`.
2. `mergedParams(mapping)` builds a fresh map.
3. `lazy.newLazyParams()` → `configuredPolicy.copy()`.
4. `lazyParamInjector.resolveInto(holder, merged, invocationContext)`.
5. `disabled` check, then `shouldIntercept`.
6. `lazy.intercept(invocation, holder)`.
7. The holder becomes garbage when the invocation ends.

Step 7 is the design's own check: **there is no cleanup step.** No `finally`, nothing to
clear, no `ThreadLocal` to leak. If a future change makes cleanup necessary, the separation
has regressed.

## Error handling

Unresolvable `${...}` is **fail-closed**. Today it produces `""`
(`OgnlTextParser.java:85-88`) → empty set (`TextParseUtil.java:257`) → treated as "no
restriction" by `acceptFile` (`:141`, `:148` both guard on `!isEmpty()`), so a typo or a
null intermediate silently switches off an upload restriction.

Under this design:

- The write is skipped, so the holder keeps its seeded config-time value, and
  `unresolved(paramName)` is called.
- A WARN is logged naming interceptor, param and expression.

`UploadPolicy.unresolved(param)` then has to decide whether the seeded value is usable. Two
cases exist, and they are distinguishable:

- **A genuine static fallback.** The interceptor's own `<interceptor>` definition carried a
  literal value for that param and the `<interceptor-ref>` overrode it with an expression,
  so the seed is a real value (e.g. `image/png`). That value applies and validation
  proceeds normally.
- **No fallback.** The only configuration for that param is the expression itself, so
  `buildInterceptor` seeded the holder with the literal `${...}` text — as a set containing
  the string `"${uploadConfig.allowedMimeTypes}"`, which matches no content type.

The rule: `unresolved(param)` marks the dimension unusable **only if** the seeded value for
that dimension still contains `${`. Otherwise a genuine static fallback exists and is used.
A dimension marked unusable causes `acceptFile` to reject the file with a dedicated message
rather than the opaque one produced by matching content types against literal `${...}` text.
This needs a new bundle key — `struts.messages.error.upload.policy.unresolved` — added to
the shipped properties.

This is a behaviour change for 7.2.x applications with a broken expression. Those
applications are currently running with that validation silently disabled, which is the
reason to surface it.

## Testing

`ActionFileUploadInterceptorTest` is JUnit 3 style — `protected void setUp()`, plain
`public void testX()` methods, no annotations. A JUnit 5 `@Test` added there compiles and
silently never runs. New tests must follow the existing style.

- Carry over PR #1815's concurrency regression, adapted to the new signature, retaining the
  contributor's attribution. Its scenario is the acceptance criterion.
- Direct guard for the defect: after an invocation resolves a policy, assert
  `newLazyParams()` still returns the configured values — the singleton was never written.
- `disabled` request-scoping: concurrent invocations resolving different `disabled` values;
  assert only the intended one is skipped.
- Fail-closed: unresolved expression with no static fallback → rejected with the new
  message; unresolved *with* a static fallback (literal on the `<interceptor>` definition,
  expression on the `<interceptor-ref>`) → the static value applies and validation proceeds.
- `ConditionalInterceptor` interaction: a `WithLazyParams` interceptor that is also
  conditional still honours a custom `shouldIntercept`.
- Migrate existing dynamic tests (`testDynamicParameterEvaluation` and friends) off the
  "simulate injection by calling the setter directly" shortcut onto real `LazyParamInjector`
  calls. PR #1815 already started this.

## Out of scope

Defining *all* interceptor params via dedicated params classes, rather than loose setters on
each interceptor, is the natural extension of `InterceptorParams`. It would touch 44
interceptor implementations across core and plugins plus every third-party interceptor, so
it is an ecosystem-wide breaking change belonging to 8.0.0 alongside the struts2-api
extraction (WW-4759). It is not required for correctness here: `disabled` is racy only on
the lazy path, which this change already covers. A separate ticket will be filed.
