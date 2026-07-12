# WW-4858 — Honor parameter filtering during JSON population

- **Jira:** [WW-4858](https://issues.apache.org/jira/browse/WW-4858)
- **Date:** 2026-07-08
- **Status:** Design approved
- **Component:** Plugin - JSON

## Problem

`JSONInterceptor` populates action/model properties directly through `JSONPopulator`
(pure Java reflection over bean setters). This path bypasses most of the name/value
acceptability controls that `ParametersInterceptor` applies to ordinary HTTP
parameters. WW-5624 already retrofitted `@StrutsParameter` authorization onto the JSON
path (via the shared `ParameterAuthorizer` and `filterUnauthorizedKeys()`), but the
remaining consistency gaps persist.

Gaps relative to the normal parameter path (pure-reflection population, ignoring the
OGNL-specific allowlisting which is intentionally *not* shared):

| Control | Normal path | JSON path today |
|---|---|---|
| `@StrutsParameter` authorization | yes | yes (WW-5624) |
| Excluded name patterns (`ExcludedPatternsChecker`) | yes | **no** |
| Accepted name patterns (`AcceptedPatternsChecker`) | yes | **no** |
| Param-name max length (default 100) | yes | **no** |
| `ParameterNameAware` callback | yes | **no** |
| `ParameterValueAware` callback | yes | **no** |
| Excluded/accepted value patterns | yes | **no** |
| Interceptor's own `excludeProperties`/`includeProperties` | output only | not on input |

## Goal & Non-Goals

**Goal:** Make the JSON-object population path enforce the same name/value acceptability
controls as `ParametersInterceptor`, while **keeping pure-reflection population**. OGNL
name evaluation is deliberately *not* introduced — pure reflection is a security feature
(it sidesteps the historical Struts OGNL-injection vector), and this change preserves it.

**Non-goals:**

- The JSON-RPC path (`Content-Type: application/json-rpc`, SMD method invocation) is out
  of scope. It binds deserialized values to *method arguments*, not to stack properties,
  so the parameter-filtering controls do not apply.
- No changes to `ParametersInterceptor` (the framework's most security-sensitive class).
- No conversion of JSON to an HTTP parameter map, and no delegation to
  `ParametersInterceptor` (the ticket's original 2017 proposal). That would re-introduce
  OGNL name evaluation on JSON input and is explicitly rejected.

## Approach

Generalize the existing single tree-walk in `JSONInterceptor`,
`filterUnauthorizedKeysRecursive()` → `filterUnacceptableKeysRecursive()`. That walk
already computes dotted paths (`address.city`, `items[0].name`) using the same naming
semantics `ParametersInterceptor` uses, and already removes rejected keys in place. Every
new check hooks in at that same visit point — one traversal, no second representation of
the JSON tree.

### New injected dependencies

Injected into `JSONInterceptor` — the *same* global singletons `ParametersInterceptor`
uses, so the name denylist/allowlist configuration is shared automatically via the global
Struts constants (no duplicate configuration):

- `ExcludedPatternsChecker excludedPatterns`
- `AcceptedPatternsChecker acceptedPatterns`

### Per-node checks (unified traversal)

For each JSON key at `fullPath`, reject (remove the key) if any of these fail:

**Always-on (security / app-owned):**

- Name length `fullPath.length() <= paramNameMaxLength`
- Not excluded: `!excludedPatterns.isExcluded(fullPath).isExcluded()`
- Accepted: `acceptedPatterns.isAccepted(fullPath).isAccepted()`
- `@StrutsParameter` authorization — **existing behavior, unchanged**
- `ParameterNameAware.acceptableParameterName(fullPath)` when the action implements it

**Opt-in:**

- Interceptor's own `excludeProperties` / `includeProperties` matched against `fullPath`,
  gated by a new `applyPropertyFiltersToInput` flag (default `false`).

For each **leaf scalar value** (including scalar elements inside arrays):

**Always-on:**

- `ParameterValueAware.acceptableParameterValue(value)` when the action implements it

**Opt-in:**

- Excluded/accepted **value** patterns via new `setExcludedValuePatterns` /
  `setAcceptedValuePatterns` (mirroring `ParametersInterceptor`). "No patterns
  configured" means no value filtering, so the opt-in is implicit — configuring the
  patterns turns the filtering on. Values are stringified with `String.valueOf`;
  `null`/empty values are treated as acceptable, matching the normal path.

Maps and Lists recurse as today. List handling is extended to value-check scalar
elements: a scalar element whose value fails a value check is dropped from the list
(individual element removal, not whole-array rejection — approved trade-off). Rejections
log at warn level in devMode and debug level otherwise, mirroring `ParametersInterceptor`.

### New configuration surface (all on `JSONInterceptor`)

| Setting | Default | Effect |
|---|---|---|
| `paramNameMaxLength` | 100 | Max length of a dotted JSON key path |
| `excludedValuePatterns` / `acceptedValuePatterns` | unset | Opt-in value filtering |
| `applyPropertyFiltersToInput` | false | Apply `excludeProperties`/`includeProperties` to input |

Default `struts-plugin.xml` wiring is unchanged: always-on security filters active,
behavioral filters off by default.

## Rollout / Backward Compatibility

Posture: **security-on, behavior opt-in.**

- Always-on additions (name denylist/allowlist, name length, plus the app-owned
  `ParameterNameAware`/`ParameterValueAware` callbacks) only *tighten* acceptance and
  align JSON with the framework's baseline security controls.
- Changes that could break existing permissive JSON apps — value patterns and applying
  `excludeProperties`/`includeProperties` to input — are opt-in and default off.

## Testing

Extend `JSONInterceptorTest` (matching that class's existing framework and conventions)
with one focused test per gap:

- Excluded name pattern rejects a key
- Accepted name pattern gating
- Over-length key rejected
- `ParameterNameAware` rejection honored
- `ParameterValueAware` rejection honored
- Opt-in value patterns (excluded and accepted)
- Opt-in `applyPropertyFiltersToInput` applies `excludeProperties`/`includeProperties`

Plus coverage for nested-object paths and list-element paths. Verify existing population
tests pass unchanged — accepted input must populate identically to today.

## Risk Notes

- Pure-reflection population is unchanged for accepted input; no OGNL surface is added.
- Always-on additions only tighten acceptance; app-breaking behavior changes are opt-in.
- `ParametersInterceptor` is not modified.
- The important shared logic (the pattern checkers) is reused via the existing global
  singletons, so JSON and the normal path share one source of truth for name patterns.
