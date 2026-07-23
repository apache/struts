# WW-2934 — Skip field validators when a field has a conversion error

- **Jira:** [WW-2934](https://issues.apache.org/jira/browse/WW-2934)
- **Type:** Improvement
- **Target version:** 7.3.0
- **Date:** 2026-07-23

## Problem

When a field fails type conversion (for example, a user types `one` into an
`Integer age` field), the user receives two errors for the same field:

1. A conversion error, added by `ConversionErrorInterceptor` (which runs before
   validation in the default stack).
2. A redundant field-validator error, e.g. from a `requiredstring` or `int`
   range validator, because binding failed and the field holds its default value.

The two messages are confusing and, per the reporter, a long-standing pain point
for real-world projects. Once a field's value could not be converted, its
remaining field validators are operating on a value the user never actually
entered, so they should be skipped.

## Current mechanics (confirmed)

- Default interceptor stack order is `conversionError` → `validation` →
  `workflow`. Conversion errors are therefore already recorded **before**
  validators run — both as field errors and in
  `ActionContext.getConversionErrors()`, a `Map<String, ConversionData>` keyed by
  **full field name**.
- `DefaultActionValidatorManager.validate(...)` runs each validator in order and
  already has a per-field short-circuit mechanism (`shortcircuitedFields`) that
  skips later validators for a field once an earlier short-circuit validator
  fails.
- The `conversion` field validator (`ConversionErrorFieldValidator`, extending
  `RepopulateConversionErrorFieldValidatorSupport`) exists specifically to
  *report* the conversion error (and optionally repopulate the field). It must
  never be skipped.
- `DefaultActionValidatorManager` already injects a Struts constant
  (`struts.configuration.xml.reload`) via `@Inject(..., required = false)`, so a
  new constant follows an established pattern.

## Goals

- When enabled, skip a field's validators once that field has a conversion error.
- Preserve the `conversion` validator so custom conversion messages and
  `repopulateField` still work.
- Leave action-level (non-field) validators untouched.
- Change nothing for existing applications unless they opt in.

## Non-goals

- No change to interceptor stack ordering.
- No per-field or per-validator configuration syntax.
- No change to how conversion errors themselves are produced or reported.

## Design

### Rollout: global constant, default OFF

Add a new global constant:

```
struts.validators.skipValidatorsOnConversionError = false   (default)
```

Default `false` preserves today's behavior (both errors shown). Applications
opt in by setting the constant to `true`. This is the safest rollout for a
15+-year-old default that some applications may rely on.

### Where: `DefaultActionValidatorManager.validate(...)`

This method is the natural home — it owns the validator loop and the existing
`shortcircuitedFields` skip logic, and it is the single path for both XML and
annotation-driven validation. Alternatives were rejected:

- `ValidationInterceptor` does not run individual validators, so it could only
  strip errors after the fact — fragile, and unable to distinguish a
  conversion-driven error from a legitimately-added one.
- `ConversionErrorInterceptor` knows the conversion errors but has no handle on
  the validator list, so it would still need to hand state to the manager.

### Behavior

Inside the validator loop, when the flag is enabled and the current validator is
a `FieldValidator` that is **not** a `ConversionErrorFieldValidator`, check
whether the field's full name is present in
`ActionContext.getContext().getConversionErrors()`. If so, skip that validator
(`continue`).

`fullFieldName` is already computed in the loop as
`validatorContext.getFullFieldName(fValidator.getFieldName())` — the same key the
conversion errors map and the repopulation logic use — so nested and indexed
fields compare correctly.

**Why the conversion errors map, not "any pre-existing field error":** the map
is the precise, authoritative source, populated during parameter binding before
validation runs. Checking generic field errors would risk skipping a validator
because of an error added by an earlier validator in the same pass.

### Implementation sketch

1. `StrutsConstants`: add
   `STRUTS_VALIDATORS_SKIP_VALIDATORS_ON_CONVERSION_ERROR =
   "struts.validators.skipValidatorsOnConversionError"`.
2. `default.properties`: document the constant (commented, default `false`).
3. `DefaultActionValidatorManager`: add a `boolean
   skipValidatorsOnConversionError` field (default `false`) with
   `@Inject(value = STRUTS_VALIDATORS_SKIP_VALIDATORS_ON_CONVERSION_ERROR,
   required = false)` setter parsing the boolean.
4. In `validate(...)`, after resolving `fValidator`/`fullFieldName` and before
   running the validator, add the skip check described above.

## Testing

Unit tests (in `DefaultActionValidatorManagerTest` or a focused test class),
each asserting resulting field errors:

- **Flag off (default):** field with a conversion error still gets both the
  conversion error and its other field-validator errors (current behavior
  unchanged).
- **Flag on:** field with a conversion error gets only the conversion error;
  `required`/range validators for that field are skipped.
- **Flag on + custom `conversion` validator:** the `conversion` validator still
  runs (custom message present).
- **Flag on, action-level validator:** action-level (non-field) validators are
  unaffected.
- **Flag on, field without a conversion error:** its validators run normally.

## Backward compatibility

Default `false` means zero behavior change for existing applications. Opting in
is a single constant. No configuration files, action code, or validator
definitions need to change.
