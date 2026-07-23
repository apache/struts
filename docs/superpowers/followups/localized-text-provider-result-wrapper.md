# Follow-up ticket draft — replace null-overloaded control flow in LocalizedTextProvider with an explicit result type

> Ready to file at https://issues.apache.org/jira/projects/WW. Not yet filed — do **not** reference a
> `WW-XXXX` id in source until this exists (per the no-placeholder-TODO convention).

## Type / Component
Improvement (code quality / maintainability) — Core.

## Summary
Replace the `null`-overloaded control flow in `AbstractLocalizedTextProvider` /
`StrutsLocalizedTextProvider` message resolution with an explicit result type, so that
"not found" and "found but the value degraded to the literal `null`" are represented
distinctly instead of both collapsing to a Java `null` that callers must branch on.

## Background
Message lookup uses `null` as an overloaded signal:

- `formatWithNullDetection` returns `null` when a formatted message equals the literal string
  `"null"` (e.g. a `{0}` pattern rendered with a `null` argument).
- Callers (`findText`'s tiers, the deprecated `findMessage`) treat that `null` identically to a
  genuine "key not found" and fall through to the next source: `if (msg != null) return msg;`.

Both cases legitimately mean "keep searching", so the overloading is not a bug — but it is a
readability/robustness wart. The codebase already has a wrapper for the analogous default-message
path: `GetDefaultMessageReturnArg { String message; boolean foundInBundle; }`. Extending a similar
explicit result to the hierarchy path would make the intent self-documenting and consistent.

Context: this was surfaced while implementing WW-5540 (hierarchy-traversal caching). WW-5540
deliberately kept the existing `null` convention and only added a cache-boundary marker
(`NOT_FOUND` sentinel + `isNotFound(...)`) plus a fall-through mitigation, to stay behavior-preserving
and focused. This ticket is the orthogonal control-flow cleanup that WW-5540 deferred.

## Proposed change
Introduce an internal result type (e.g. `sealed`/enum-tagged: `Found(String value)` vs
`ContinueSearch`) used by the raw-resolution + formatting path, unwrapped to `String`/`null` at the
public `findText` boundary (the public method signatures return `String` and must not change).
Consider whether `formatWithNullDetection`, `getMessage`, `findDefaultText`, and `getDefaultMessage`
should adopt the same type for consistency, or whether the change should be scoped to the hierarchy
path only.

## Scope / risk notes
- Behavior must remain identical (this is a refactor, not a behavior change).
- Touches long-standing framework internals many call sites branch on, plus the `@Deprecated`
  `getMessage`/`findMessage` retained for descendant classes — review the ripple carefully.
- Purely a readability/maintainability gain; the branch itself does not disappear, it becomes
  explicit (`result.isFound()` instead of `msg != null`).

## Acceptance
- No public API signature changes.
- Existing `StrutsLocalizedTextProviderTest` (and related i18n tests) stay green with no behavior change.
- `null` is no longer used to mean "found but continue searching" anywhere in the resolution path.
