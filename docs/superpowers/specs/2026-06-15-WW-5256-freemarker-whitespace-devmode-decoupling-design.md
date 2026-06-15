---
date: 2026-06-15
ticket: WW-5256
url: https://issues.apache.org/jira/browse/WW-5256
status: design
---

# WW-5256 — Decouple FreeMarker whitespace stripping from devMode

## Problem

In `release/7.2.0-RC1`, `s:textarea` renders blank lines inside an empty textarea, and
the overall HTML output contains noticeably more whitespace than in 6.x. Reported symptom
on CRUD entry/add pages: an empty `bean.data` (null default) textarea shows up as two blank
lines on screen, and the raw HTML source has far more whitespace throughout than the 6.x
equivalent.

## Root cause

The FreeMarker templates are **not** the cause. `textarea.ftl` and all of its included
sub-templates (`css.ftl`, `scripting-events.ftl`, `common-attributes.ftl`,
`dynamic-attributes.ftl`) are byte-for-byte identical between `support/struts-6-x-x` and
`release/7.2.0-RC1` apart from the `parameters` → `attributes` rename. Their whitespace
structure is unchanged.

The regression is in `FreemarkerManager.java:355`, introduced by WW-5256
(commit `9305a5812`, `@since 7.2.0`):

```java
// 6.x — whitespace stripping unconditionally on:
configuration.setWhitespaceStripping(true);

// 7.2.0-RC1 — stripping turned off whenever devMode is on:
boolean enableWhitespaceStripping = whitespaceStripping && !devMode;
configuration.setWhitespaceStripping(enableWhitespaceStripping);
```

When `struts.devMode=true` (the normal development setting), whitespace stripping is
forced off. FreeMarker then stops collapsing directive-only lines (e.g. `<#if …>` /
`</#if>`), which produces two distinct effects:

1. **General whitespace bloat** — every directive-only line in every UI template now emits
   its newline and indentation.
2. **Visible `s:textarea` breakage** — the directive lines around `nameValue` sit *inside*
   `<textarea>…</textarea>`, where whitespace is **significant content**. The collapsed
   newlines that 6.x removed now render as blank lines in the browser.

The `&& !devMode` term also **overrides** the `struts.freemarker.whitespaceStripping`
config flag: a developer running in devMode has no way to turn stripping back on.

### Why the coupling was introduced (and why it was wrong)

Per the WW-5256 research note
(`thoughts/shared/research/2025-09-24-WW-5256-freemarker-whitespace-compression.md`), the
devMode auto-disable was copied by analogy from the new `<s:compress>` tag, which
intentionally disables compression in devMode with a `force` override. The stated goal was
"readable output while debugging." The note itself raised this as an *open question*
("Should whitespace stripping be automatically disabled in DevMode … or require explicit
configuration?") rather than a settled decision.

The analogy does not hold: for most tags the extra dev whitespace is harmless cosmetic
noise, but for `<textarea>` (and `<pre>`) the stripped newlines are semantically
significant page content. "Make dev output readable" therefore silently broke `s:textarea`
rendering, with no escape hatch. The configurable flag from WW-5256 is the useful part and
is kept; the devMode auto-disable is the unvalidated part and is removed.

## Design

Decouple whitespace stripping from devMode. Honor `struts.freemarker.whitespaceStripping`
(default `true`) unconditionally. This restores 6.x behavior by default while keeping an
explicit opt-out for anyone who genuinely wants raw, un-collapsed template output.

### Changes

1. **`core/src/main/java/org/apache/struts2/views/freemarker/FreemarkerManager.java`**
   - Replace the coupling with a direct call:
     ```java
     configuration.setWhitespaceStripping(whitespaceStripping);
     ```
   - Simplify the debug log to drop the devMode reference.
   - Remove the now-unused `devMode` field, the `setDevMode(String)` setter, and its
     `@Inject(value = StrutsConstants.STRUTS_DEVMODE, required = false)` annotation. The
     field was added in 7.2.0 solely for this coupling and is referenced nowhere else in
     the class, so removing it before final 7.2.0 release breaks no released API.

2. **`core/src/main/java/org/apache/struts2/StrutsConstants.java`**
   - Remove the line "*Automatically disabled when devMode is enabled.*" from the
     `STRUTS_FREEMARKER_WHITESPACE_STRIPPING` Javadoc. Keep `@since 7.2.0`.

3. **`core/src/main/resources/org/apache/struts2/default.properties`**
   - No change. `struts.freemarker.whitespaceStripping=true` remains the default.

4. **`core/src/test/java/org/apache/struts2/views/freemarker/FreemarkerManagerTest.java`**
   - Remove `testWhitespaceStrippingDisabledInDevMode` and
     `testWhitespaceStrippingEnabledWhenNotInDevMode` — they assert the removed coupling and
     call the removed `setDevMode` setter.
   - Remove the stray `manager.setDevMode("false")` call from
     `testWhitespaceStrippingDisabledViaConfiguration`.
   - Keep `testWhitespaceStrippingEnabledByDefault` and the explicit-disable test; together
     they fully cover the remaining behavior (default on; honored when set false).

### Behavior after the change

| devMode | `struts.freemarker.whitespaceStripping` | Stripping enabled? |
|---------|-----------------------------------------|--------------------|
| any     | unset (default)                         | yes (matches 6.x)  |
| any     | `true`                                  | yes                |
| any     | `false`                                 | no                 |

## Scope and risk

- Contained, single-purpose fix landing in `release/7.2.0-RC1` before the final 7.2.0
  release. No migration or backward-compatibility concern, since the removed `devMode`
  wiring was never in a released version.
- **Not** a security change — this is purely rendering/whitespace behavior. The normal
  PR flow applies (no private security-triage path needed).
- The `<s:compress>` tag's own devMode/`force` behavior is unrelated and unchanged.

## Verification

- `mvn test -DskipAssembly -pl core -Dtest=FreemarkerManagerTest` passes with the updated
  test set.
- Manual check (or an `AbstractUITagTest`-style assertion): an `s:textarea` bound to a null
  value renders as `<textarea …></textarea>` with no internal blank lines when
  `struts.devMode=true`.
