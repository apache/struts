# HTML5 constraint validation, and retiring the JavaScript client-side validator

**Tickets:** not yet filed — three proposed, see "Ticket structure"
**Supersedes:** [WW-2975](https://issues.apache.org/jira/browse/WW-2975) (to be closed Won't Fix)
**Target:** 7.4.0 (deprecate + new feature), 8.0.0 (remove)
**Date:** 2026-08-24
**Status:** Design approved, pending implementation plan

**Plan scope:** the implementation plan drawn from this spec covers the **7.4.0 work only** — the deprecation
and the new constraint feature. The 8.0.0 removal is specified here so the deprecation is written against a
known endpoint, but it is a separate ticket, a separate release and a separate plan.

## Problem

`xhtml/form-close-validate.ftl` generates a `validateForm_<id>()` function by iterating
`attributes.tagNames`. That list is seeded by `Form.evaluateExtraParams` (`Form.java:207`) and appended to
by `UIBean.evaluateParams` (`UIBean.java:834`) — but **only** when `findAncestor(Form.class)` finds the form
on the component stack.

Any input that reaches the form's markup by another route is therefore invisible to client-side validation:
raw HTML, a custom tag, an included fragment, or a component handed pre-rendered field markup. The generated
function is then an empty shell:

```js
function validateForm_doubleValidationAction() {
    ...
    var errors = false;
    var continueValidation = true;

    return !errors;
}
```

This is WW-2975, reported 2009-01-30 against 2.1.6 and reproduced unchanged on `main` (7.4.0-SNAPSHOT) on
2026-08-24. The reproduction renders a form whose action declares a `double` validator for
`myUpDownSelectTag`: a nested `<s:updownselect>` produces the `if (form.elements['myUpDownSelectTag'])`
block, while an identical raw `<input name="myUpDownSelectTag">` produces nothing at all.

The ticket's second complaint is also live, at LOW confidence (the repo has **no** JavaScript test
infrastructure — no `package.json`, nothing). `validation.js` `addErrorXHTML` walks `row.parentNode` up
until it finds a `TR`. A field with no `TR` ancestor sends that walk off the top of the document, and the
handler is `catch (err) { alert(err) }` — so the user gets a raw JavaScript error instead of a validation
message.

### Why this is not being fixed in place

The feature is legacy on every axis. It exists only in `xhtml` (the default theme) and `css_xhtml`
(`parent = xhtml`); the `html5` theme has `parent = simple` and never had it. It supports eight validators.
It reports errors by inserting `<tr>` elements, so it only works with the table layout. It has zero test
coverage beyond four golden files.

The successor is native HTML5 constraint validation, emitted per field. That dissolves the root cause rather
than patching it: with constraints riding on each `<input>`, there is no central field list for a foreign
field to be missing from.

## Goals

- Emit HTML5 constraint attributes from the `html5` theme, derived from the action's validators.
- Never emit a constraint the browser would enforce more strictly than the server does.
- Deprecate the JavaScript client-side validator in 7.4.0, remove it in 8.0.0.
- Change nothing about existing renderings on upgrade to 7.4.0.

## Non-goals

- Fixing the `tagNames` scope or `addError` fragility in the deprecated path. It is documented as a known
  limitation and deleted in 8.0.0.
- Shipping any JavaScript. Struts emits messages as `data-*` attributes and stops there.
- Changing an input's `type` attribute. See "The never-change-the-type rule".
- Constraint support in `xhtml`, `css_xhtml` or `simple`. This is an `html5` theme feature.
- Touching `struts.ui.theme`'s default. `xhtml` remains the default theme in 7.4.0.

## Approach

Derive constraints in Java from the action's validators, behind a swappable container bean, and render them
from a single new `html5` template include.

### The never-change-the-type rule

`min`/`max` are inert on `type="text"`; they only apply to `number`, `range` and the temporal types. Emitting
them therefore means switching the input to `type="number"` — and that **is** a false rejection: a browser
`type="number"` refuses `1234,50`, which Struts' locale-aware conversion accepts in a comma-decimal locale.
The same argument rules out `type="email"` and `type="url"`, whose browser regexes differ from
`EmailValidator` and `URLValidator`.

So: **Struts never sets or changes `type`. It only adds constraints that are safe for whatever type is
already there.** A developer who writes `type="number"` has accepted that widget's semantics, and `min`/`max`
become pure additions.

### Mapping table

| Validator | Emits | Condition |
|---|---|---|
| `required` | `required` | always |
| `requiredstring` | `required` | always — server is stricter on whitespace-only input, which is safe |
| `stringlength` | `minlength` / `maxlength` | only when `trim="false"` |
| `regex` | `pattern` | only when `caseSensitive="true"` **and** the regex is ECMAScript-safe |
| `int`, `short`, `long` | `min` / `max` | only when the control is already numeric |
| `double` | `min` / `max` | only when the control is already numeric |
| `date` | `min` / `max` | only when the control is already temporal |
| `email`, `url` | — | never; browser regexes diverge from Struts' |
| `creditcard`, `fieldexpression`, `expression`, `conversion`, visitor | — | no safe mapping |

`stringlength` with `trim="true"` is excluded because the server measures the *trimmed* value: a
`maxlength` derived from it would stop the user typing input the server would have accepted.

`RegexFieldValidator` uses `matcher.matches()`, so it is fully anchored and matches HTML5 `pattern`
semantics. The divergence is syntactic, not positional.

### ECMAScript-safe regex detection

This is the least-solved part of the design and the strongest argument for the provider being swappable.

A denylist of Java-only constructs (`\p{Alpha}`, possessive quantifiers, `\A`/`\z`, lookbehind) violates the
never-false-reject rule the first time it misses one: a missed construct becomes a `pattern` the browser
interprets differently. So detection is an **allowlist** — literals, `\d \w \s` and their negations,
character classes without POSIX or Unicode property syntax, grouping, alternation, anchors, and bounded
quantifiers. Anything outside it emits no `pattern`.

This is conservative to the point that some legitimate regexes will silently get no client-side check. That
is the correct failure direction under the agreed rule, and it is the piece most likely to need tuning after
real use.

## Components

### `HtmlControlType` (new enum)

The provider's real question is not "what string is in `type`" but "which constraint attributes are legal on
this control". `textarea` and `select` have no `type` attribute at all yet do accept `required`, so the enum
models the *control*, not the attribute — hence `HtmlControlType`, not `HtmlInputType`.

```java
public enum HtmlControlType {
    TEXT, SEARCH, TEL, PASSWORD, EMAIL, URL,
    NUMBER, RANGE,
    DATE, MONTH, WEEK, TIME, DATETIME_LOCAL,
    CHECKBOX, RADIO, FILE, HIDDEN, SELECT,
    TEXTAREA,
    OTHER;

    public static HtmlControlType from(String type);
    public boolean supportsPattern();  // text-entry only
    public boolean supportsLength();   // text-entry + TEXTAREA
    public boolean supportsRange();    // numeric + temporal
}
```

`from()` must never throw. `type` is an OGNL-evaluated tag attribute, so at runtime it can be any string — a
typo, or an input type newer than this enum. Unknown and `null` normalise to `OTHER`, which supports nothing,
so an unrecognised type degrades to emitting no constraints. The conservative default falls out for free.

The enum appears in the public signature of an extension point. Adding members later stays binary- and
source-compatible for callers, but can make an exhaustive `switch` in a custom provider non-exhaustive. That
belongs in the release notes.

### `HtmlConstraintProvider` (new interface) and `StrutsHtmlConstraintProvider`

```java
public interface HtmlConstraintProvider {
    Map<String, String> constraintsFor(List<Validator> validators, HtmlControlType control);
}
```

Named per the project convention of `Struts*` for default implementations rather than `Default*`. Registered
**once** in `struts-beans.xml` as `type="...HtmlConstraintProvider" name="struts"`, following the
`UrlRenderer` model — a bean registered under two types builds two instances, which is not wanted here.

The default implementation encodes the mapping table. Because the agreed policy is deliberately restrictive,
the swappable bean is how applications that want `type="email"` or best-effort `pattern` get served.

### `StrutsConstants.STRUTS_UI_HTML5_CONSTRAINTS`

`"struts.ui.html5.constraints"`, following the `struts.ui.checkbox.submitUnchecked` naming precedent.
Defaults to `false` in `default.properties` for 7.4.0 and `true` for 8.0.0.

Off by default matters: the `html5` theme shipped in 7.2.x, so emitting `required` on upgrade would start
blocking submits on forms that render unchanged today. The 8.0.0 flip lands in a major with a migration entry.

### Resolving the control type

`attributes.type` is set by `TextField.evaluateExtraParams` (`TextField.java:91`) and by nothing else on the
input path — `TextArea`, `Select`, `Checkbox`, `File`, `Hidden` and `Radio` have no `type` attribute at all.
So the control type cannot be read from the attribute map alone; it needs a component-level hook:

```java
protected HtmlControlType getControlType();   // UIBean, returns OTHER
```

Overridden in four places, which is all that is needed:

| Component | Returns |
|---|---|
| `UIBean` (base) | `OTHER` — supports nothing, so unknown controls emit no constraints |
| `TextField` | `HtmlControlType.from(getAttributes().get("type"))`, defaulting to `TEXT` when absent, matching `text.ftl`'s `attributes.type!"text"` |
| `Password` | `PASSWORD` — it extends `TextField` but its template hardcodes the type |
| `TextArea` | `TEXTAREA` |
| `Select` | `SELECT` |

`Checkbox`, `Radio`, `File` and `Hidden` deliberately get no override. They fall through to `OTHER`, which
emits nothing — the correct answer for all four. `ComboBox` extends `TextField` and correctly inherits `TEXT`.

### `UIBean` hook

`UIBean.evaluateParams` resolves `final Form form = (Form) findAncestor(Form.class)` at `UIBean.java:824`
and appends to `tagNames` just below it. **The hook cannot go there.** `evaluateExtraParams()` is the *last*
statement of `evaluateParams()` (`UIBean.java:905`), and that is where `TextField` sets `attributes.type` —
so at the `tagNames` block the control type is not yet resolved and every text field would look like `OTHER`.

The hook therefore goes at the very end of `evaluateParams()`, after the `evaluateExtraParams()` call. The
`form` local is declared at method scope and is still in scope there (the tooltip block below it already
uses it). When the constant is on and a form was found:

```
form.getFieldValidators(translatedName)
  → provider.constraintsFor(validators, getControlType())
  → addParameter("constraints", map)
```

Gating the *computation* on the constant keeps the cost at zero when off. Themes that do not render
`attributes.constraints` simply ignore it.

### `Form.getFieldValidators(String)` (new)

`Form.getValidators(String)` re-runs the action-mapping lookup and
`actionValidatorManager.getValidators(actionClass, actionName, methodName)` on every call, so a 20-field form
would do 20 full lookups. `getFieldValidators` resolves the action's validator list **once**, memoises it on
the form's attributes, and filters by field name per call.

The existing `getValidators(String)` stays untouched for the deprecated `form-close-validate.ftl` and is
deleted with it in 8.0.0.

### `html5/constraints.ftl` (new), included from `common-attributes.ftl`

```freemarker
<#if attributes.constraints??><#list attributes.constraints as k, v> ${k}="${v?html}"<#rt/></#list></#if>
```

Including it from `common-attributes.ftl` means every `html5` input picks it up without per-template edits.

`constraintsFor` returns the full set of attributes to render, not only constraints — messages ride the same
map as `data-msg-<validatorType>` entries: `data-msg-required`, `data-msg-stringlength`, `data-msg-regex`.
The text comes from `validator.getMessage(action)`, which resolves through `DelegatingValidatorContext` and
`textProviderFactory`, so it is properly i18n'd.

A `data-msg-*` entry is emitted for **every** validator carrying a message, including those that produce no
constraint. An `email` validator therefore contributes `data-msg-email` and nothing else — which is exactly
the case where an application most needs the message, since Struts could not express the rule natively.
Struts ships nothing that consumes these attributes.

### Deliberately unchanged

`requiredLabel` keeps meaning "draw a `*` next to the label". It never produces a `required` attribute — only
a `required` *validator* does. Conflating the two is the most likely regression in this work.

## Deprecation and removal

`validate="true"` is not one feature but four edits to the rendered form:

- `xhtml/form-validate.ftl` injects the `validation.js` `<script>` tag
- the same template rewrites `onsubmit` to `return validateForm_<id>();`
- `xhtml/form.ftl` overwrites `onreset` with `clearErrorMessages(this);clearErrorLabels(this);`
- `xhtml/form-close.ftl` includes `form-close-validate.ftl`

### 7.4.0 — deprecate, change nothing

Following the WW-5510 tooltip precedent (`c2a5bfe3c`):

- `@Deprecated(since = "7.4.0", forRemoval = true)` on `Form.setValidate`, `Form.getValidators(String)` and
  `Form.evaluateClientSideJsEnablement`, **and** on the mirrored `FormTag.setValidate`. WW-5510 annotated
  both the component and the JSP tag layer; both surfaces are public API.
- `<strong>Deprecated since 7.4.0</strong>` banners inside the relevant `<!-- START SNIPPET: ... -->` blocks
  in the `Form` Javadoc. Those snippets are pulled into the website by the `remote_file_content` plugin, so
  omitting this leaves the site advertising the feature as current.
- A deprecation banner in the `form-close-validate.ftl` header comment, since a `.ftl` carries no annotation
  and anyone who overrode that template needs to see it there.
- **No runtime warning.** WW-5510 shipped annotations and Javadoc only. `validate="true"` is opt-in, so every
  affected user made a deliberate choice and will be reading release notes.
- `struts.ui.html5.constraints` ships `false`.

Two traps: `Dispatcher` and `ValidationInterceptor` also declare unrelated `setValidate` methods that must
not be annotated. And WW-5510's annotations say `since = "7.0.1"` while its fix version was 7.0.3 — use the
real target version here.

### 8.0.0 — remove

Delete `xhtml/form-close-validate.ftl`, `xhtml/form-validate.ftl`, `xhtml/validation.js` and
`css_xhtml/validation.js`. Strip the `validate` branches from `xhtml/form.ftl` and `xhtml/form-close.ftl`.
Drop `validate`, `performValidation` and `tagNames` from `Form`, `FormTag` (including its
`clearTagStateForTagPoolingServers` reset) and the `tagNames` append in `UIBean`. Flip the constant's default
to `true`.

`html5/form.ftl` also reads `attributes.validate` in its `onsubmit` guard and must be updated in the same
change.

Retire the `Formtag-2.txt`, `Formtag-11.txt`, `Formtag-22.txt` and `Formtag-24.txt` fixtures and their
`FormTagTest` methods.

`tagNames` dies here, which is what finally closes WW-2975's root cause rather than patching it.

## Testing

`HtmlControlType` and `StrutsHtmlConstraintProvider` are plain objects — JUnit 4 (`org.junit.Test`). Anything
that renders a tag must extend `AbstractUITagTest`, which is JUnit 3 style: methods named `testXxx()`, and a
Jupiter `@Test` there silently never runs.

The negative cases carry the weight, because they are what protects the never-false-reject rule:

- `stringlength trim="true"` → no `minlength`/`maxlength`
- `regex caseSensitive="false"` → no `pattern`
- a regex using Java-only syntax → no `pattern`
- `int`/`double` on `TEXT` → no `min`/`max`; on `NUMBER` → both
- `email`/`url` validators → never set or change `type`
- `creditcard`, `fieldexpression`, visitor → empty map
- `HtmlControlType.from(null)`, `from("NuMbEr")`, `from("supercolor")` → `OTHER`/`NUMBER`/`OTHER`, never throws

Tag-rendering tests use the existing `verify(resource)` golden-file pattern with new `.txt` fixtures,
covering constraints on and off via the constant, and `requiredLabel="true"` **not** producing a `required`
attribute.

**Harness trap, encountered during triage:** a form-validation tag test needs
`initDispatcher(configProviders = TestConfigurationProvider)` *and* `createMocks()` in `setUp`, plus the
`prepareMockInvocation()` EasyMock helper from `FormTagTest`. Without them `evaluateClientSideJsEnablement`
finds no `ValidationInterceptor`, `performValidation` stays `false`, and no validation function is emitted at
all — while `onsubmit` still calls the missing function. A test written without that setup passes or fails
for entirely the wrong reason.

The WW-2975 reproduction test is **not** committed. It asserts behaviour scheduled for deletion, so it stays
attached to the triage comment as evidence rather than becoming a permanent red or an inverted test pinning a
known-bad behaviour.

## Ticket structure

WW-2975 is a 2009 Improvement carrying six fix-version bumps and is the wrong vehicle. Close it **Won't Fix**,
superseded, with a triage comment recording the reproduction and the empty `validateForm_` output.

The repo convention is a matched deprecate/remove pair — WW-5682/WW-5683, WW-5673, WW-5654 all follow it.

| Summary | Type | Fix version |
|---|---|---|
| Deprecate JavaScript client-side validation in the xhtml and css_xhtml themes | Improvement | 7.4.0 |
| Derive HTML5 constraint attributes from validators in the html5 theme | New Feature | 7.4.0 |
| Remove deprecated JavaScript client-side validation | Improvement | 8.0.0 |

Cross-link [WW-4395](https://issues.apache.org/jira/browse/WW-4395) ("Make email validator regex comply with
RFC 6531", Open, 7.4.0) from the feature ticket: that regex's divergence from browsers is precisely why
`type="email"` is never emitted.

## Documentation

In `struts-site`:

- Delete `core-developers/pure-java-script-client-side-validation.md`.
- Rewrite `core-developers/client-side-validation.md` around the `html5` theme: the mapping table, the
  `trim` and `caseSensitive` conditions, the never-change-the-type rule, and the `data-msg-*` attributes.
- Mark `core-developers/client-validation-example.md` as deprecated.
- Fix the stale claim in the pure-JavaScript page that messages are "not the internationalized version" —
  `ValidatorSupport.getMessage` resolves through `DelegatingValidatorContext` and `textProviderFactory`.
- Leave the "Available since Struts 7.2.0" claim in `tag-developers/html5-theme.md` alone — it is correct.
  `git tag --contains e24d2f2d3` returns `STRUTS_7_2_0`, so the theme shipped in 7.2.0 and **WW-5444's fix
  version of 7.2.1 is the wrong record**. Correct the Jira ticket, not the docs.
- Update `tag-developers/form-tag.md` for the deprecated `validate` attribute.

The Migration Guide entry lives on the cwiki as part of the Version Notes process, not in `struts-site`.

## Risks

- **The allowlist is too strict.** Real applications will have regexes that get no `pattern`. Mitigated by
  the swappable provider; expect tuning after the first release.
- **`requiredLabel` conflation.** Explicitly tested against.
- **8.0.0 removal is user-visible.** Forms relying on `validate="true"` lose client-side checking. Server-side
  validation is unaffected, so this is a UX regression rather than a correctness or security one, but it
  needs a prominent migration entry.
- **Constraint derivation runs per field.** Mitigated by memoising the validator list on the form; worth a
  sanity check on a wide form before release.
