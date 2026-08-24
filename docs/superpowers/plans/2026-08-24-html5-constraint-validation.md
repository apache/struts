# HTML5 Constraint Validation (7.4.0) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deprecate the generated JavaScript client-side validator, and let the `html5` theme emit native HTML5 constraint attributes derived from the action's validators.

**Architecture:** A container-registered `HtmlConstraintProvider` maps a field's validators plus its resolved `HtmlControlType` to a map of HTML attributes. `UIBean.evaluateParams` calls it at the end of the method and stashes the result as the `constraints` attribute; a new `html5/constraints.ftl`, included from `common-attributes.ftl`, renders it. Everything is gated behind a new constant defaulting to `false`, so no existing rendering changes.

**Tech Stack:** Java 17, Maven, FreeMarker templates, JUnit 4 + JUnit 3 (`XWorkTestCase`), AssertJ, EasyMock.

**Spec:** `docs/superpowers/specs/2026-08-24-html5-constraint-validation-design.md`

**Tickets:** WW-5694 (deprecation, Task 1), WW-5695 (constraints, Tasks 2–9). WW-5696 is the 8.0.0 removal and is **out of scope for this plan**.

## Global Constraints

- Target version string in every `@Deprecated` annotation: `since = "7.4.0", forRemoval = true`.
- **Every new file — main source AND test source — must carry the Apache licence header**, copied verbatim
  from a sibling file in the same directory. The code blocks in this plan omit it for brevity; that omission
  is not permission to skip it. `apache-rat-plugin:check` is bound to the `prepare-package` phase
  (`pom.xml:549-555`), so `mvn test -DskipAssembly` — the command every task below uses — **never runs the
  licence check**. A missing header therefore passes every task-level verification in this plan and fails
  the real build. Add it as you create each file.
- **Tests are JUnit 4 or JUnit 3. There is no JUnit 5 in this repo.** New pure-unit test classes use `org.junit.Test`. Any test that renders a tag must extend `AbstractUITagTest` (JUnit 3 style: methods named `testXxx()`, no annotation). A Jupiter `@Test` on an `XWorkTestCase` subclass silently never runs.
- Commit message format: `WW-XXXX <type>(<scope>): <description>`. Ticket prefix is mandatory.
- Never commit to `main`. Work happens on `feature/WW-5695-html5-constraint-validation`.
- Never `git add -A` in this repo — the tree holds ~20 long-lived untracked files. Stage explicit paths and verify with `git diff --cached --name-only`.
- Build command: `mvn test -DskipAssembly -pl core -Dtest=<TestClass>` from the repo root.
- `DateTest.testJavaSqlDate` is a known flake (WW-5686). A red build on that one alone is the clock, not your change.
- **The governing rule for every mapping decision: never false-reject.** Emit a constraint only when the browser cannot reject input the server would accept. When unsure, emit nothing.
- Struts ships **no JavaScript** as part of this work.

---

### Task 1: Deprecate the JavaScript client-side validator (WW-5694)

Independent of every other task and separately mergeable. Annotations and documentation only — no behaviour changes.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/components/Form.java`
- Modify: `core/src/main/java/org/apache/struts2/views/jsp/ui/FormTag.java`
- Modify: `core/src/main/resources/template/xhtml/form-close-validate.ftl`

**Interfaces:**
- Consumes: nothing.
- Produces: nothing. Later tasks do not depend on this one.

- [ ] **Step 1: Annotate the `Form` component**

In `Form.java`, add `@Deprecated(since = "7.4.0", forRemoval = true)` to three members. `setValidate` is around line 512, `getValidators` around line 270, `evaluateClientSideJsEnablement` around line 242 — confirm by search, not by line number.

```java
    @Deprecated(since = "7.4.0", forRemoval = true)
    protected void evaluateClientSideJsEnablement(String actionName, String namespace, String actionMethod) {

    @Deprecated(since = "7.4.0", forRemoval = true)
    public List getValidators(String name) {

    @StrutsTagAttribute(description = "Whether client side/js validation should be performed. Only useful with theme xhtml/ajax", type = "Boolean", defaultValue = "false")
    @Deprecated(since = "7.4.0", forRemoval = true)
    public void setValidate(String validate) {
```

Keep the existing `@StrutsTagAttribute` annotation and its exact text; only add the `@Deprecated` line beneath it.

**Do not touch `Dispatcher.setValidate` or `ValidationInterceptor.setValidate`.** They are unrelated methods that happen to share the name.

- [ ] **Step 2: Annotate the JSP tag layer**

WW-5510 (`c2a5bfe3c`) annotated both the component and the tag. In `FormTag.java` (setter around line 96):

```java
    @Deprecated(since = "7.4.0", forRemoval = true)
    public void setValidate(String validate) {
        this.validate = validate;
    }
```

Leave `FormTag.clearTagStateForTagPoolingServers` alone — the field reset stays until removal.

- [ ] **Step 3: Add the Javadoc banner**

Find the `<!-- START SNIPPET: ... -->` block in `Form.java`'s class Javadoc that documents `validate`. Add, as the first line inside the snippet:

```java
 * <strong>Deprecated since 7.4.0 — use the html5 theme's constraint attributes instead. Removed in 8.0.0.</strong>
```

This matters because the website pulls these snippets in via the `remote_file_content` Jekyll plugin. Omitting it leaves struts.apache.org advertising the feature as current.

- [ ] **Step 4: Add the template banner**

A `.ftl` carries no annotation, and anyone who overrode this template needs to see the notice there. In `core/src/main/resources/template/xhtml/form-close-validate.ftl`, immediately after the Apache licence header comment:

```
<#--
DEPRECATED since Struts 7.4.0, removed in 8.0.0 (WW-5694 / WW-5696).

JavaScript client-side validation is superseded by native HTML5 constraint
attributes in the html5 theme (WW-5695). This template, form-validate.ftl and
validation.js are all removed in 8.0.0.
-->
```

- [ ] **Step 5: Verify the build still compiles and existing tests pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=FormTagTest`
Expected: PASS. Deprecation annotations change no behaviour, so all four `validateForm_` golden files must still match byte-for-byte.

- [ ] **Step 6: Commit**

```bash
git add core/src/main/java/org/apache/struts2/components/Form.java \
        core/src/main/java/org/apache/struts2/views/jsp/ui/FormTag.java \
        core/src/main/resources/template/xhtml/form-close-validate.ftl
git diff --cached --name-only
git commit -m "WW-5694 refactor(validation): deprecate JavaScript client-side validation

Marks the form tag's validate attribute and the machinery behind it for
removal in 8.0.0. Annotations and documentation only; no behaviour change.

Follows the WW-5510 precedent: annotate both the component and the JSP tag,
and put the notice inside the START SNIPPET block so the website picks it up.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 2: `HtmlControlType` enum

**Files:**
- Create: `core/src/main/java/org/apache/struts2/components/HtmlControlType.java`
- Test: `core/src/test/java/org/apache/struts2/components/HtmlControlTypeTest.java`

**Interfaces:**
- Consumes: nothing.
- Produces: `HtmlControlType` with `static HtmlControlType from(String type)`, and instance predicates `boolean supportsPattern()`, `boolean supportsLength()`, `boolean supportsRange()`. Used by Tasks 4, 6, 7.

Package note: `org.apache.struts2.components` is where `UrlRenderer` lives, so a new view-layer extension point belongs there by convention. WW-5689 eventually moves this whole package into a plugin; that is expected and does not change the right answer for 7.4.0.

- [ ] **Step 1: Write the failing test**

Create `core/src/test/java/org/apache/struts2/components/HtmlControlTypeTest.java` (JUnit 4 — this is a plain object with no container):

```java
package org.apache.struts2.components;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmlControlTypeTest {

    @Test
    public void resolvesKnownTypes() {
        assertThat(HtmlControlType.from("text")).isEqualTo(HtmlControlType.TEXT);
        assertThat(HtmlControlType.from("number")).isEqualTo(HtmlControlType.NUMBER);
        assertThat(HtmlControlType.from("datetime-local")).isEqualTo(HtmlControlType.DATETIME_LOCAL);
    }

    @Test
    public void isLenientAboutCaseAndWhitespace() {
        assertThat(HtmlControlType.from("  NuMbEr ")).isEqualTo(HtmlControlType.NUMBER);
    }

    @Test
    public void neverThrowsOnUnusableInput() {
        assertThat(HtmlControlType.from(null)).isEqualTo(HtmlControlType.OTHER);
        assertThat(HtmlControlType.from("")).isEqualTo(HtmlControlType.OTHER);
        assertThat(HtmlControlType.from("   ")).isEqualTo(HtmlControlType.OTHER);
        assertThat(HtmlControlType.from("supercolor")).isEqualTo(HtmlControlType.OTHER);
    }

    @Test
    public void otherSupportsNothing() {
        assertThat(HtmlControlType.OTHER.supportsPattern()).isFalse();
        assertThat(HtmlControlType.OTHER.supportsLength()).isFalse();
        assertThat(HtmlControlType.OTHER.supportsRange()).isFalse();
    }

    @Test
    public void patternIsTextEntryOnly() {
        assertThat(HtmlControlType.TEXT.supportsPattern()).isTrue();
        assertThat(HtmlControlType.PASSWORD.supportsPattern()).isTrue();
        assertThat(HtmlControlType.NUMBER.supportsPattern()).isFalse();
        assertThat(HtmlControlType.TEXTAREA.supportsPattern()).isFalse();
        assertThat(HtmlControlType.SELECT.supportsPattern()).isFalse();
    }

    @Test
    public void lengthIsTextEntryPlusTextarea() {
        assertThat(HtmlControlType.TEXT.supportsLength()).isTrue();
        assertThat(HtmlControlType.TEXTAREA.supportsLength()).isTrue();
        assertThat(HtmlControlType.NUMBER.supportsLength()).isFalse();
        assertThat(HtmlControlType.CHECKBOX.supportsLength()).isFalse();
    }

    @Test
    public void rangeIsNumericAndTemporalOnly() {
        assertThat(HtmlControlType.NUMBER.supportsRange()).isTrue();
        assertThat(HtmlControlType.RANGE.supportsRange()).isTrue();
        assertThat(HtmlControlType.DATE.supportsRange()).isTrue();
        assertThat(HtmlControlType.TEXT.supportsRange()).isFalse();
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=HtmlControlTypeTest`
Expected: FAIL — compilation error, `HtmlControlType` does not exist.

- [ ] **Step 3: Write the implementation**

Create `core/src/main/java/org/apache/struts2/components/HtmlControlType.java` with the standard Apache licence header (copy it verbatim from any neighbouring file in the same package), then:

```java
package org.apache.struts2.components;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * The kind of HTML form control a {@link UIBean} renders, used to decide which HTML5 constraint
 * attributes are legal on it.
 * <p>
 * This models the <em>control</em> rather than the {@code type} attribute, because {@code textarea}
 * and {@code select} have no {@code type} attribute yet still accept {@code required}.
 *
 * @since 7.4.0
 */
public enum HtmlControlType {

    TEXT, SEARCH, TEL, PASSWORD, EMAIL, URL,
    NUMBER, RANGE,
    DATE, MONTH, WEEK, TIME, DATETIME_LOCAL,
    CHECKBOX, RADIO, FILE, HIDDEN, SELECT,
    TEXTAREA,
    OTHER;

    private static final Set<HtmlControlType> TEXT_ENTRY = EnumSet.of(TEXT, SEARCH, TEL, PASSWORD, EMAIL, URL);
    private static final Set<HtmlControlType> NUMERIC = EnumSet.of(NUMBER, RANGE);
    private static final Set<HtmlControlType> TEMPORAL = EnumSet.of(DATE, MONTH, WEEK, TIME, DATETIME_LOCAL);

    /**
     * Resolves a raw {@code type} attribute value. Never throws: the attribute is OGNL-evaluated, so at
     * runtime it can be any string. Anything unrecognised becomes {@link #OTHER}, which supports no
     * constraints at all — so an unknown control degrades to emitting nothing.
     */
    public static HtmlControlType from(String type) {
        if (type == null) {
            return OTHER;
        }
        String normalised = type.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        if (normalised.isEmpty()) {
            return OTHER;
        }
        try {
            return valueOf(normalised);
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }

    public boolean supportsPattern() {
        return TEXT_ENTRY.contains(this);
    }

    public boolean supportsLength() {
        return TEXT_ENTRY.contains(this) || this == TEXTAREA;
    }

    public boolean supportsRange() {
        return NUMERIC.contains(this) || TEMPORAL.contains(this);
    }
}
```

- [ ] **Step 4: Run the test to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=HtmlControlTypeTest`
Expected: PASS, 7 tests.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/components/HtmlControlType.java \
        core/src/test/java/org/apache/struts2/components/HtmlControlTypeTest.java
git diff --cached --name-only
git commit -m "WW-5695 feat(components): add HtmlControlType

Models the kind of form control a UIBean renders, so constraint derivation can
ask which HTML5 attributes are legal rather than string-matching a type
attribute. Models the control, not the attribute, because textarea and select
have no type yet still accept required.

from() never throws; unknown input becomes OTHER, which supports nothing.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 3: ECMAScript-safe regex detection

The highest-risk logic in this work. A regex that Java and the browser interpret differently becomes a false rejection the user cannot get past, so detection is an **allowlist**: anything not provably common to both engines is rejected.

**Files:**
- Create: `core/src/main/java/org/apache/struts2/components/EcmaScriptSafeRegex.java`
- Test: `core/src/test/java/org/apache/struts2/components/EcmaScriptSafeRegexTest.java`

**Interfaces:**
- Consumes: nothing.
- Produces: `EcmaScriptSafeRegex.isSafe(String regex)` returning `boolean`. Used by Task 4.

- [ ] **Step 1: Write the failing test**

```java
package org.apache.struts2.components;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EcmaScriptSafeRegexTest {

    @Test
    public void acceptsPortableConstructs() {
        assertThat(EcmaScriptSafeRegex.isSafe("[a-z]+")).isTrue();
        assertThat(EcmaScriptSafeRegex.isSafe("\\d{3}-\\d{4}")).isTrue();
        assertThat(EcmaScriptSafeRegex.isSafe("(foo|bar)?baz")).isTrue();
        assertThat(EcmaScriptSafeRegex.isSafe("^\\w+@\\w+\\.\\w{2,6}$")).isTrue();
        assertThat(EcmaScriptSafeRegex.isSafe("(?:ab)+")).isTrue();
        assertThat(EcmaScriptSafeRegex.isSafe("a(?=b)")).isTrue();
        assertThat(EcmaScriptSafeRegex.isSafe("a(?!b)")).isTrue();
    }

    @Test
    public void rejectsJavaOnlyEscapes() {
        assertThat(EcmaScriptSafeRegex.isSafe("\\p{Alpha}+")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("\\A\\d+\\z")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("\\Qliteral\\E")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("\\h+")).isFalse();
    }

    @Test
    public void rejectsWhitespaceClassesWhoseMeaningDiffersBetweenEngines() {
        // Java's \s is ASCII-only by default; ECMAScript's includes NBSP and friends, so
        // ^\S+$ accepts a value containing NBSP on the server and rejects it in the browser
        assertThat(EcmaScriptSafeRegex.isSafe("^\\S+$")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("\\s*")).isFalse();
    }

    @Test
    public void rejectsPossessiveQuantifiers() {
        assertThat(EcmaScriptSafeRegex.isSafe("\\d++")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("a*+")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("a?+")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("a{2,3}+")).isFalse();
    }

    @Test
    public void rejectsNonPortableGroups() {
        assertThat(EcmaScriptSafeRegex.isSafe("(?<name>a)")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("(?<=a)b")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("(?>a)")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("(?i)abc")).isFalse();
    }

    @Test
    public void rejectsJavaCharacterClassFeatures() {
        assertThat(EcmaScriptSafeRegex.isSafe("[[:alpha:]]")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("[a-z&&[^aeiou]]")).isFalse();
    }

    @Test
    public void rejectsUnusableInput() {
        assertThat(EcmaScriptSafeRegex.isSafe(null)).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("abc\\")).isFalse();
        assertThat(EcmaScriptSafeRegex.isSafe("[abc")).isFalse();
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=EcmaScriptSafeRegexTest`
Expected: FAIL — compilation error, `EcmaScriptSafeRegex` does not exist.

- [ ] **Step 3: Write the implementation**

Create `core/src/main/java/org/apache/struts2/components/EcmaScriptSafeRegex.java` with the Apache licence header, then:

```java
package org.apache.struts2.components;

/**
 * Decides whether a Java regular expression can be handed to a browser as an HTML5 {@code pattern}
 * attribute without changing meaning.
 * <p>
 * This is an allowlist by design. A denylist of Java-only constructs would violate the
 * never-false-reject rule the first time it missed one, because a missed construct becomes a pattern
 * the browser interprets differently and the user cannot get past. Anything not provably common to
 * both engines is rejected, and the field simply gets no client-side check.
 *
 * @since 7.4.0
 */
public final class EcmaScriptSafeRegex {

    /**
     * Escapes with identical meaning in both engines.
     * <p>
     * {@code \s} and {@code \S} are deliberately absent. Java's {@code \s} is ASCII-only by default
     * while ECMAScript's is the wider Unicode set, so {@code ^\S+$} accepts a value containing NBSP
     * on the server and rejects it in the browser. {@code \d} and {@code \w} are safe — both engines
     * are ASCII-only for those, and JavaScript never widens them.
     */
    private static final String ALLOWED_ESCAPES = "dDwWbBnrtf\\.*+?()[]{}|^$/-";

    private EcmaScriptSafeRegex() {
    }

    public static boolean isSafe(String regex) {
        if (regex == null || regex.isEmpty()) {
            return false;
        }
        boolean inCharClass = false;
        for (int i = 0; i < regex.length(); i++) {
            char current = regex.charAt(i);
            switch (current) {
                case '\\':
                    if (i + 1 >= regex.length() || ALLOWED_ESCAPES.indexOf(regex.charAt(++i)) < 0) {
                        return false;
                    }
                    break;
                case '[':
                    // Java allows nested classes and POSIX names; ECMAScript allows neither
                    if (inCharClass || regex.startsWith("[:", i)) {
                        return false;
                    }
                    inCharClass = true;
                    break;
                case ']':
                    inCharClass = false;
                    break;
                case '&':
                    // Java character-class intersection
                    if (inCharClass && i + 1 < regex.length() && regex.charAt(i + 1) == '&') {
                        return false;
                    }
                    break;
                case '(':
                    // only non-capturing groups and lookahead are portable; named groups,
                    // lookbehind, atomic groups and inline flags are not
                    if (i + 1 < regex.length() && regex.charAt(i + 1) == '?') {
                        if (i + 2 >= regex.length()) {
                            return false;
                        }
                        char kind = regex.charAt(i + 2);
                        if (kind != ':' && kind != '=' && kind != '!') {
                            return false;
                        }
                    }
                    break;
                case '*':
                case '+':
                case '?':
                case '}':
                    // possessive quantifier
                    if (i + 1 < regex.length() && regex.charAt(i + 1) == '+') {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return !inCharClass;
    }
}
```

- [ ] **Step 4: Run the test to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=EcmaScriptSafeRegexTest`
Expected: PASS, 7 tests.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/components/EcmaScriptSafeRegex.java \
        core/src/test/java/org/apache/struts2/components/EcmaScriptSafeRegexTest.java
git diff --cached --name-only
git commit -m "WW-5695 feat(components): add ECMAScript-safe regex detection

Decides whether a Java regex can become an HTML5 pattern attribute without
changing meaning. Allowlist by design: a denylist would violate the
never-false-reject rule the first time it missed a construct, and a regex the
browser reads differently is a rejection the user cannot get past.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 4: `HtmlConstraintProvider` and the default implementation

**Files:**
- Create: `core/src/main/java/org/apache/struts2/components/HtmlConstraintProvider.java`
- Create: `core/src/main/java/org/apache/struts2/components/StrutsHtmlConstraintProvider.java`
- Modify: `core/src/main/resources/struts-beans.xml:146`
- Test: `core/src/test/java/org/apache/struts2/components/StrutsHtmlConstraintProviderTest.java`

**Interfaces:**
- Consumes: `HtmlControlType` (Task 2), `EcmaScriptSafeRegex.isSafe(String)` (Task 3).
- Produces: `HtmlConstraintProvider.constraintsFor(List<Validator> validators, HtmlControlType control, Object action)` returning `Map<String, String>`. Used by Task 7.

**Signature note:** the spec's sketch showed two parameters. The third, `action`, is required — `Validator.getMessage(Object)` needs the action instance to resolve i18n text, and the `data-msg-*` attributes come from it.

- [ ] **Step 1: Write the failing test**

The negative cases carry the weight here; they are what protects the never-false-reject rule.

```java
package org.apache.struts2.components;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.validator.Validator;
import org.apache.struts2.validator.validators.DoubleRangeFieldValidator;
import org.apache.struts2.validator.validators.EmailValidator;
import org.apache.struts2.validator.validators.IntRangeFieldValidator;
import org.apache.struts2.validator.validators.RegexFieldValidator;
import org.apache.struts2.validator.validators.RequiredFieldValidator;
import org.apache.struts2.validator.validators.RequiredStringValidator;
import org.apache.struts2.validator.validators.StringLengthFieldValidator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class StrutsHtmlConstraintProviderTest {

    private StrutsHtmlConstraintProvider provider;
    private Object action;

    @Before
    public void setUp() {
        provider = new StrutsHtmlConstraintProvider();
        action = new ActionSupport();
    }

    private Map<String, String> constraints(Validator validator, HtmlControlType control) {
        return provider.constraintsFor(singletonList(validator), control, null);
    }

    @Test
    public void requiredValidatorEmitsRequired() {
        assertThat(constraints(new RequiredFieldValidator(), HtmlControlType.TEXT))
            .containsEntry("required", "required");
    }

    @Test
    public void requiredStringEmitsRequiredEvenThoughServerIsStricter() {
        assertThat(constraints(new RequiredStringValidator(), HtmlControlType.TEXT))
            .containsEntry("required", "required");
    }

    @Test
    public void stringLengthEmitsLengthsWhenNotTrimming() {
        StringLengthFieldValidator validator = new StringLengthFieldValidator();
        validator.setTrim(false);
        validator.setMinLength(3);
        validator.setMaxLength(10);

        assertThat(constraints(validator, HtmlControlType.TEXT))
            .containsEntry("minlength", "3")
            .containsEntry("maxlength", "10");
    }

    @Test
    public void stringLengthEmitsNothingWhenTrimming() {
        StringLengthFieldValidator validator = new StringLengthFieldValidator();
        validator.setTrim(true);
        validator.setMinLength(3);
        validator.setMaxLength(10);

        // the server measures the trimmed value, so maxlength here would stop the user
        // typing input the server would have accepted
        assertThat(constraints(validator, HtmlControlType.TEXT)).isEmpty();
    }

    @Test
    public void stringLengthEmitsNothingOnAControlWithoutLength() {
        StringLengthFieldValidator validator = new StringLengthFieldValidator();
        validator.setTrim(false);
        validator.setMaxLength(10);

        assertThat(constraints(validator, HtmlControlType.NUMBER)).isEmpty();
    }

    @Test
    public void regexEmitsPatternWhenPortableAndCaseSensitive() {
        RegexFieldValidator validator = new RegexFieldValidator();
        validator.setRegex("[a-z]+");
        validator.setCaseSensitive(true);

        assertThat(constraints(validator, HtmlControlType.TEXT))
            .containsEntry("pattern", "[a-z]+");
    }

    @Test
    public void regexEmitsNothingWhenCaseInsensitive() {
        RegexFieldValidator validator = new RegexFieldValidator();
        validator.setRegex("[a-z]+");
        validator.setCaseSensitive(false);

        // HTML pattern accepts no flags, so a case-insensitive rule cannot be expressed
        assertThat(constraints(validator, HtmlControlType.TEXT)).isEmpty();
    }

    @Test
    public void regexEmitsNothingWhenNotPortable() {
        RegexFieldValidator validator = new RegexFieldValidator();
        validator.setRegex("\\p{Alpha}+");
        validator.setCaseSensitive(true);

        assertThat(constraints(validator, HtmlControlType.TEXT)).isEmpty();
    }

    @Test
    public void intRangeEmitsBoundsOnlyOnANumericControl() {
        IntRangeFieldValidator validator = new IntRangeFieldValidator();
        validator.setMin(5);
        validator.setMax(50);

        assertThat(constraints(validator, HtmlControlType.NUMBER))
            .containsEntry("min", "5")
            .containsEntry("max", "50");
        assertThat(constraints(validator, HtmlControlType.TEXT)).isEmpty();
    }

    @Test
    public void doubleRangeEmitsInclusiveBoundsOnlyOnANumericControl() {
        DoubleRangeFieldValidator validator = new DoubleRangeFieldValidator();
        validator.setMinInclusive(6000.1);
        validator.setMaxInclusive(10000.1);

        assertThat(constraints(validator, HtmlControlType.NUMBER))
            .containsEntry("min", "6000.1")
            .containsEntry("max", "10000.1");
        assertThat(constraints(validator, HtmlControlType.TEXT)).isEmpty();
    }

    @Test
    public void emailValidatorNeverContributesAConstraint() {
        // the browser's email grammar differs from EmailValidator's, so honouring it
        // could reject an address the server accepts
        assertThat(constraints(new EmailValidator(), HtmlControlType.TEXT)).isEmpty();
        assertThat(constraints(new EmailValidator(), HtmlControlType.EMAIL)).isEmpty();
    }

    @Test
    public void unknownControlGetsNothing() {
        assertThat(constraints(new RequiredFieldValidator(), HtmlControlType.OTHER)).isEmpty();
    }

    @Test
    public void emptyInputIsHandled() {
        assertThat(provider.constraintsFor(null, HtmlControlType.TEXT, null)).isEmpty();
        assertThat(provider.constraintsFor(List.of(), HtmlControlType.TEXT, null)).isEmpty();
    }

    @Test
    public void messageIsEmittedEvenForAValidatorThatContributesNoConstraint() {
        EmailValidator validator = new EmailValidator();
        validator.setDefaultMessage("not an email");

        Map<String, String> result =
            provider.constraintsFor(singletonList(validator), HtmlControlType.TEXT, action);

        assertThat(result).containsEntry("data-msg-email", "not an email");
    }
}
```

**Note on `required` and `OTHER`:** the test above asserts `OTHER` gets nothing at all, including `required`. That is deliberate — `OTHER` means "we do not know what this control is", and guessing is how false rejections happen.

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsHtmlConstraintProviderTest`
Expected: FAIL — compilation error, `StrutsHtmlConstraintProvider` does not exist.

- [ ] **Step 3: Write the interface**

Create `core/src/main/java/org/apache/struts2/components/HtmlConstraintProvider.java` with the licence header, then:

```java
package org.apache.struts2.components;

import org.apache.struts2.validator.Validator;

import java.util.List;
import java.util.Map;

/**
 * Maps a field's validators onto the HTML attributes a theme should render for it.
 * <p>
 * The default implementation is deliberately conservative — see {@link StrutsHtmlConstraintProvider}.
 * Applications wanting a best-effort mapping (an {@code email} validator becoming
 * {@code type="email"}, say) should register their own implementation instead.
 *
 * @since 7.4.0
 */
public interface HtmlConstraintProvider {

    /**
     * @param validators the field's validators; may be null or empty
     * @param control    the kind of control being rendered
     * @param action     the action instance, used to resolve i18n validator messages; may be null
     * @return attribute name to value; never null, possibly empty
     */
    Map<String, String> constraintsFor(List<Validator> validators, HtmlControlType control, Object action);
}
```

- [ ] **Step 4: Write the default implementation**

Create `core/src/main/java/org/apache/struts2/components/StrutsHtmlConstraintProvider.java` with the licence header, then:

```java
package org.apache.struts2.components;

import org.apache.struts2.validator.Validator;
import org.apache.struts2.validator.validators.DoubleRangeFieldValidator;
import org.apache.struts2.validator.validators.RangeValidatorSupport;
import org.apache.struts2.validator.validators.RegexFieldValidator;
import org.apache.struts2.validator.validators.RequiredFieldValidator;
import org.apache.struts2.validator.validators.RequiredStringValidator;
import org.apache.struts2.validator.validators.StringLengthFieldValidator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Default {@link HtmlConstraintProvider}.
 * <p>
 * Governed by one rule: never false-reject. A constraint is emitted only when the browser cannot
 * reject input the server would accept. In particular this implementation <em>never sets or changes
 * an input's {@code type}</em> — switching a field to {@code type="number"} would reject
 * {@code 1234,50}, which the framework's locale-aware conversion accepts in a comma-decimal locale,
 * and the browsers' {@code email}/{@code url} grammars differ from the framework's validators.
 * Range constraints are therefore emitted only on a control the developer already made numeric.
 *
 * @since 7.4.0
 */
public class StrutsHtmlConstraintProvider implements HtmlConstraintProvider {

    @Override
    public Map<String, String> constraintsFor(List<Validator> validators, HtmlControlType control, Object action) {
        Map<String, String> attributes = new LinkedHashMap<>();
        if (validators == null || validators.isEmpty() || control == null) {
            return attributes;
        }
        for (Validator validator : validators) {
            addConstraints(attributes, validator, control);
            addMessage(attributes, validator, action);
        }
        return attributes;
    }

    protected void addConstraints(Map<String, String> attributes, Validator validator, HtmlControlType control) {
        if (validator instanceof RequiredFieldValidator || validator instanceof RequiredStringValidator) {
            addRequired(attributes, control);
        } else if (validator instanceof StringLengthFieldValidator lengthValidator) {
            addLength(attributes, lengthValidator, control);
        } else if (validator instanceof RegexFieldValidator regexValidator) {
            addPattern(attributes, regexValidator, control);
        } else if (validator instanceof DoubleRangeFieldValidator doubleValidator) {
            addDoubleRange(attributes, doubleValidator, control);
        } else if (validator instanceof RangeValidatorSupport<?> rangeValidator) {
            addRange(attributes, rangeValidator, control);
        }
    }

    protected void addRequired(Map<String, String> attributes, HtmlControlType control) {
        if (control == HtmlControlType.OTHER) {
            return;
        }
        attributes.put("required", "required");
    }

    protected void addLength(Map<String, String> attributes, StringLengthFieldValidator validator, HtmlControlType control) {
        // with trim=true the server measures the trimmed value, so a maxlength taken from it would
        // stop the user typing input the server would have accepted
        if (!control.supportsLength() || validator.isTrim()) {
            return;
        }
        if (validator.getMinLength() > -1) {
            attributes.put("minlength", String.valueOf(validator.getMinLength()));
        }
        if (validator.getMaxLength() > -1) {
            attributes.put("maxlength", String.valueOf(validator.getMaxLength()));
        }
    }

    protected void addPattern(Map<String, String> attributes, RegexFieldValidator validator, HtmlControlType control) {
        // HTML pattern accepts no flags, so a case-insensitive rule cannot be expressed at all
        if (!control.supportsPattern() || !validator.isCaseSensitive()) {
            return;
        }
        String regex = validator.getRegex();
        if (EcmaScriptSafeRegex.isSafe(regex)) {
            attributes.put("pattern", regex);
        }
    }

    protected void addRange(Map<String, String> attributes, RangeValidatorSupport<?> validator, HtmlControlType control) {
        if (!isNumeric(control)) {
            return;
        }
        putIfPresent(attributes, "min", validator.getMin());
        putIfPresent(attributes, "max", validator.getMax());
    }

    protected void addDoubleRange(Map<String, String> attributes, DoubleRangeFieldValidator validator, HtmlControlType control) {
        if (!isNumeric(control)) {
            return;
        }
        // exclusive bounds have no HTML equivalent; omitting them leaves the browser more
        // permissive than the server, which is the safe direction
        putIfPresent(attributes, "min", validator.getMinInclusive());
        putIfPresent(attributes, "max", validator.getMaxInclusive());
    }

    protected void addMessage(Map<String, String> attributes, Validator validator, Object action) {
        if (action == null) {
            return;
        }
        String message = validator.getMessage(action);
        if (message != null && !message.isEmpty()) {
            attributes.put("data-msg-" + validator.getValidatorType(), message);
        }
    }

    private boolean isNumeric(HtmlControlType control) {
        return control == HtmlControlType.NUMBER || control == HtmlControlType.RANGE;
    }

    private void putIfPresent(Map<String, String> attributes, String name, Object value) {
        if (value != null) {
            attributes.put(name, String.valueOf(value));
        }
    }
}
```

**Scope decision, made explicit:** `DateRangeFieldValidator` extends `RangeValidatorSupport<Date>`, so it reaches `addRange`, where `isNumeric` rejects it and it emits nothing. Temporal `min`/`max` need per-control ISO formatting (`date` wants `yyyy-MM-dd`, `week` wants `2026-W12`, and so on) and are **deliberately not implemented here**. `HtmlControlType.supportsRange()` already covers the temporal types so the enum needs no change when this lands; file it as a follow-up rather than guessing at the formats now.

- [ ] **Step 5: Register the bean**

In `core/src/main/resources/struts-beans.xml`, immediately after the `UrlRenderer` bean (around line 146):

```xml
    <bean type="org.apache.struts2.components.HtmlConstraintProvider" name="struts"
          class="org.apache.struts2.components.StrutsHtmlConstraintProvider"/>
```

Register it **once**, under a single type. Registering a bean under two types builds two instances.

- [ ] **Step 6: Run the tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsHtmlConstraintProviderTest`
Expected: PASS, 14 tests.

- [ ] **Step 7: Commit**

```bash
git add core/src/main/java/org/apache/struts2/components/HtmlConstraintProvider.java \
        core/src/main/java/org/apache/struts2/components/StrutsHtmlConstraintProvider.java \
        core/src/main/resources/struts-beans.xml \
        core/src/test/java/org/apache/struts2/components/StrutsHtmlConstraintProviderTest.java
git diff --cached --name-only
git commit -m "WW-5695 feat(components): map validators onto HTML5 constraint attributes

Adds HtmlConstraintProvider and its conservative default implementation, which
never sets or changes an input's type: type=number would reject 1234,50 that
locale-aware conversion accepts, and browser email/url grammars differ from the
framework's validators. Range constraints therefore land only on a control the
developer already made numeric.

Registered as a swappable bean so applications wanting a best-effort mapping
can replace the policy.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 5: `Form.getFieldValidators` with per-render memoisation

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/components/Form.java`
- Test: `core/src/test/java/org/apache/struts2/components/FormFieldValidatorsTest.java`

**Interfaces:**
- Consumes: nothing new.
- Produces: `Form.getFieldValidators(String name)` returning `List<Validator>`. Used by Task 7.

`Form.getValidators(String)` re-runs the action-mapping lookup and `actionValidatorManager.getValidators(...)` on **every** call, so a twenty-field form would do twenty full resolutions. The new method resolves once per form render.

**Memoisation location:** on private fields of the `Form` component, **not** on the attributes map as the spec sketch said. A `Form` component is constructed per render by `ComponentTagSupport`, so a field is naturally request-scoped, and the attributes map is exposed to templates and should not carry private bookkeeping.

Leave `getValidators(String)` exactly as it is. It is deprecated and dies with the JavaScript validator in 8.0.0; the small duplication dies with it.

- [ ] **Step 1: Write the failing test**

```java
package org.apache.struts2.components;

import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.validator.Validator;
import org.apache.struts2.views.jsp.AbstractUITagTest;
import org.apache.struts2.views.jsp.ui.FormTag;

import java.util.HashMap;
import java.util.List;

public class FormFieldValidatorsTest extends AbstractUITagTest {

    public void testFindsTheFieldsValidators() throws Exception {
        Form form = formForDoubleValidationAction();

        List<Validator> validators = form.getFieldValidators("myUpDownSelectTag");

        assertEquals(1, validators.size());
        assertEquals("double", validators.get(0).getValidatorType());
    }

    public void testReturnsEmptyForAnUnvalidatedField() throws Exception {
        Form form = formForDoubleValidationAction();

        assertTrue(form.getFieldValidators("noSuchField").isEmpty());
    }

    public void testResolvesTheActionsValidatorsOnlyOnceAcrossFields() throws Exception {
        Form form = formForDoubleValidationAction();

        ActionValidatorManager manager = mock(ActionValidatorManager.class);
        when(manager.getValidators(any(), any(), any())).thenReturn(Collections.emptyList());
        form.setActionValidatorManager(manager);

        form.getFieldValidators("myUpDownSelectTag");
        form.getFieldValidators("someOtherField");

        verify(manager, times(1)).getValidators(any(), any(), any());
    }

    private Form formForDoubleValidationAction() throws Exception {
        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setAction("doubleValidationAction");
        tag.setNamespace("");
        tag.doStartTag();
        return (Form) tag.getComponent();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initDispatcher(new HashMap<String, String>() {{
            put("configProviders", TestConfigurationProvider.class.getName());
        }});
        createMocks();
    }
}
```

**Harness trap, part two:** `createMocks()` never calls `setConfig` on the `MockActionProxy` it builds, so
`AnnotationActionValidatorManager.buildValidatorKey` dereferences a null `ActionConfig` and NPEs. The test's
`setUp` also needs `((MockActionProxy) actionProxy).setConfig(configuration.getRuntimeConfiguration()
.getActionConfig("", "doubleValidationAction"))`. This is why `FormTagTest` carries its own
`prepareMockInvocation()` helper.

**Do not write a memoisation test that only compares result sizes** — resolution is deterministic, so such a
test passes identically against an implementation with no cache at all. Assert the *number of resolutions*
with a mocked `ActionValidatorManager`, across two different field names.

**Harness trap:** without `initDispatcher(configProviders = TestConfigurationProvider)` *and* `createMocks()`, the action config is not present and validator resolution silently returns nothing — the test would pass or fail for entirely the wrong reason. `DoubleValidationAction-validation.xml` already exists at `core/src/test/resources/org/apache/struts2/views/jsp/ui/` and declares a `double` validator for `myUpDownSelectTag`.

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=FormFieldValidatorsTest`
Expected: FAIL — compilation error, `getFieldValidators` does not exist.

- [ ] **Step 3: Write the implementation**

In `Form.java`, add two fields beside the existing ones (near `actionValidatorManager`, around line 120):

```java
    private List<Validator> cachedActionValidators;
    private String cachedActionName;
    private boolean actionValidatorsResolved;
```

Then add the method next to `getValidators`:

```java
    /**
     * Returns the validators declared for a single field, resolving the action's validator list at
     * most once per form render.
     *
     * @since 7.4.0
     */
    public List<Validator> getFieldValidators(String name) {
        resolveActionValidators();
        if (cachedActionValidators.isEmpty()) {
            return Collections.emptyList();
        }
        Class actionClass = (Class) getAttributes().get("actionClass");
        List<Validator> validators = new ArrayList<>();
        findFieldValidators(name, actionClass, cachedActionName, cachedActionValidators, validators, "");
        return validators;
    }

    private void resolveActionValidators() {
        if (actionValidatorsResolved) {
            return;
        }
        actionValidatorsResolved = true;
        cachedActionValidators = Collections.emptyList();

        Class actionClass = (Class) getAttributes().get("actionClass");
        if (actionClass == null) {
            return;
        }
        ActionMapping mapping = actionMapper.getMappingFromActionName(findString(action));
        if (mapping == null) {
            mapping = actionMapper.getMappingFromActionName((String) getAttributes().get("actionName"));
        }
        if (mapping == null) {
            return;
        }
        cachedActionName = mapping.getName();
        String methodName = isValidateAnnotatedMethodOnly(cachedActionName) ? mapping.getMethod() : null;
        cachedActionValidators =
            actionValidatorManager.getValidators(actionClass, cachedActionName, methodName);
    }
```

- [ ] **Step 4: Run the test to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=FormFieldValidatorsTest`
Expected: PASS, 3 tests.

- [ ] **Step 5: Verify nothing regressed**

Run: `mvn test -DskipAssembly -pl core -Dtest=FormTagTest`
Expected: PASS. `getValidators` was not touched, so all four `validateForm_` golden files must still match.

- [ ] **Step 6: Commit**

```bash
git add core/src/main/java/org/apache/struts2/components/Form.java \
        core/src/test/java/org/apache/struts2/components/FormFieldValidatorsTest.java
git diff --cached --name-only
git commit -m "WW-5695 feat(components): add Form.getFieldValidators with per-render caching

getValidators re-runs the action-mapping lookup and the validator-manager
resolution on every call, so a twenty-field form would do twenty of them. The
new method resolves once per form render and filters per field.

Memoised on component fields rather than the attributes map: a Form component
is built per render, and the attributes map is exposed to templates.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 6: `getControlType()` on the component hierarchy

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/components/UIBean.java`
- Modify: `core/src/main/java/org/apache/struts2/components/TextField.java`
- Modify: `core/src/main/java/org/apache/struts2/components/Password.java`
- Modify: `core/src/main/java/org/apache/struts2/components/TextArea.java`
- Modify: `core/src/main/java/org/apache/struts2/components/Select.java`
- Test: `core/src/test/java/org/apache/struts2/components/ControlTypeTest.java`

**Interfaces:**
- Consumes: `HtmlControlType` (Task 2).
- Produces: `protected HtmlControlType UIBean.getControlType()`. Used by Task 7.

`attributes.type` is set by `TextField.evaluateExtraParams` (`TextField.java:91`) and by nothing else on the input path — `TextArea`, `Select`, `Checkbox`, `File`, `Hidden` and `Radio` have no `type` attribute at all. So the control type needs a component hook.

`Checkbox`, `Radio`, `File` and `Hidden` deliberately get **no** override: they fall through to `OTHER`, which emits nothing, and that is the correct answer for all four. `ComboBox` extends `TextField` and correctly inherits `TEXT`.

- [ ] **Step 1: Write the failing test**

```java
package org.apache.struts2.components;

import org.apache.struts2.views.jsp.AbstractUITagTest;

public class ControlTypeTest extends AbstractUITagTest {

    public void testTextFieldDefaultsToText() {
        TextField textField = new TextField(stack, request, response);
        assertEquals(HtmlControlType.TEXT, textField.getControlType());
    }

    public void testTextFieldHonoursAnExplicitType() {
        TextField textField = new TextField(stack, request, response);
        textField.addParameter("type", "number");
        assertEquals(HtmlControlType.NUMBER, textField.getControlType());
    }

    public void testTextFieldFallsBackForAnUnknownType() {
        TextField textField = new TextField(stack, request, response);
        textField.addParameter("type", "supercolor");
        assertEquals(HtmlControlType.OTHER, textField.getControlType());
    }

    public void testPasswordIsAlwaysPassword() {
        Password password = new Password(stack, request, response);
        assertEquals(HtmlControlType.PASSWORD, password.getControlType());
    }

    public void testTextAreaIsTextarea() {
        TextArea textArea = new TextArea(stack, request, response);
        assertEquals(HtmlControlType.TEXTAREA, textArea.getControlType());
    }

    public void testSelectIsSelect() {
        Select select = new Select(stack, request, response);
        assertEquals(HtmlControlType.SELECT, select.getControlType());
    }

    public void testControlsWithoutAnOverrideAreUnknown() {
        assertEquals(HtmlControlType.OTHER, new Checkbox(stack, request, response).getControlType());
        assertEquals(HtmlControlType.OTHER, new Hidden(stack, request, response).getControlType());
        assertEquals(HtmlControlType.OTHER, new File(stack, request, response).getControlType());
    }
}
```

`getControlType()` is `protected`, and this test lives in the same package, so it is directly callable.

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=ControlTypeTest`
Expected: FAIL — compilation error, `getControlType` does not exist.

- [ ] **Step 3: Add the base method to `UIBean`**

Place it next to the other `protected` helpers, above `evaluateExtraParams()`:

```java
    /**
     * The kind of HTML control this component renders, used to decide which HTML5 constraint
     * attributes are legal on it. Defaults to {@link HtmlControlType#OTHER}, which supports no
     * constraints — so a component that does not override this emits none.
     *
     * @since 7.4.0
     */
    protected HtmlControlType getControlType() {
        return HtmlControlType.OTHER;
    }
```

- [ ] **Step 4: Add the four overrides**

`TextField.java` — reads the attribute set by its own `evaluateExtraParams`, defaulting to `TEXT` to match `text.ftl`'s `attributes.type!"text"`:

```java
    @Override
    protected HtmlControlType getControlType() {
        Object type = getAttributes().get("type");
        return type == null ? HtmlControlType.TEXT : HtmlControlType.from(String.valueOf(type));
    }
```

`Password.java` — it extends `TextField`, but its template hardcodes the type:

```java
    @Override
    protected HtmlControlType getControlType() {
        return HtmlControlType.PASSWORD;
    }
```

`TextArea.java`:

```java
    @Override
    protected HtmlControlType getControlType() {
        return HtmlControlType.TEXTAREA;
    }
```

`Select.java`:

```java
    @Override
    protected HtmlControlType getControlType() {
        return HtmlControlType.SELECT;
    }
```

- [ ] **Step 5: Run the test to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=ControlTypeTest`
Expected: PASS, 7 tests.

- [ ] **Step 6: Commit**

```bash
git add core/src/main/java/org/apache/struts2/components/UIBean.java \
        core/src/main/java/org/apache/struts2/components/TextField.java \
        core/src/main/java/org/apache/struts2/components/Password.java \
        core/src/main/java/org/apache/struts2/components/TextArea.java \
        core/src/main/java/org/apache/struts2/components/Select.java \
        core/src/test/java/org/apache/struts2/components/ControlTypeTest.java
git diff --cached --name-only
git commit -m "WW-5695 feat(components): resolve the HTML control type per component

attributes.type is set by TextField and nothing else on the input path, so the
control type cannot come from the attribute map alone. Adds getControlType()
with four overrides; Checkbox, Radio, File and Hidden fall through to OTHER,
which emits nothing and is correct for all four.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 7: The constant and the `UIBean` wiring

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/StrutsConstants.java:204`
- Modify: `core/src/main/resources/org/apache/struts2/default.properties:167`
- Modify: `core/src/main/java/org/apache/struts2/components/UIBean.java`
- Test: `core/src/test/java/org/apache/struts2/components/ConstraintAttributesTest.java`

**Interfaces:**
- Consumes: `HtmlConstraintProvider` (Task 4), `Form.getFieldValidators` (Task 5), `getControlType()` (Task 6).
- Produces: the `constraints` attribute on the component's attribute map, a `Map<String, String>`. Consumed by Task 8's template.

**Hook placement — this is the part that is easy to get wrong.** `evaluateParams()` resolves `final Form form = (Form) findAncestor(Form.class)` at `UIBean.java:824` and appends to `tagNames` just below. **The hook must not go there.** `evaluateExtraParams()` is the *last* statement of `evaluateParams()` (`UIBean.java:905`), and that is where `TextField` sets `attributes.type` — so at the `tagNames` block no text field has a resolved type and every one of them would look like `OTHER`. The hook goes at the very end of the method. The `form` local is method-scoped and still in scope there.

- [ ] **Step 1: Write the failing test**

```java
package org.apache.struts2.components;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.views.jsp.AbstractUITagTest;
import org.apache.struts2.views.jsp.ui.FormTag;
import org.apache.struts2.views.jsp.ui.TextFieldTag;

import java.util.HashMap;
import java.util.Map;

public class ConstraintAttributesTest extends AbstractUITagTest {

    public void testNoConstraintsWhenTheConstantIsOff() throws Exception {
        initDispatcherWith("false");

        assertNull(renderFieldAndReturnConstraints());
    }

    public void testConstraintsWhenTheConstantIsOn() throws Exception {
        initDispatcherWith("true");

        Map<String, String> constraints = renderFieldAndReturnConstraints();
        assertNotNull("expected constraints to be populated", constraints);
        assertEquals("3", constraints.get("minlength"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> renderFieldAndReturnConstraints() throws Exception {
        FormTag form = new FormTag();
        form.setPageContext(pageContext);
        form.setAction("constraintAction");
        form.setNamespace("");
        form.doStartTag();

        TextFieldTag field = new TextFieldTag();
        field.setPageContext(pageContext);
        field.setName("username");
        field.doStartTag();

        Map<String, Object> attributes =
            ((UIBean) field.getComponent()).getAttributes();

        field.doEndTag();
        form.doEndTag();

        return (Map<String, String>) attributes.get("constraints");
    }

    private void initDispatcherWith(String constraintsEnabled) throws Exception {
        initDispatcher(new HashMap<String, String>() {{
            put("configProviders", TestConfigurationProvider.class.getName());
            put(StrutsConstants.STRUTS_UI_HTML5_CONSTRAINTS, constraintsEnabled);
        }});
        createMocks();
    }
}
```

This test needs an action named `constraintAction` with a `stringlength` validator carrying `trim="false"` and `minLength=3` on a `username` field. Create both:

`core/src/test/java/org/apache/struts2/components/ConstraintAction.java`:

```java
package org.apache.struts2.components;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

public class ConstraintAction extends ActionSupport {

    private String username;

    public String getUsername() {
        return username;
    }

    @StrutsParameter
    public void setUsername(String username) {
        this.username = username;
    }
}
```

`core/src/test/resources/org/apache/struts2/components/ConstraintAction-validation.xml` (copy the licence header and DOCTYPE verbatim from `core/src/test/resources/org/apache/struts2/views/jsp/ui/DoubleValidationAction-validation.xml`):

```xml
<validators>
    <field name="username">
        <field-validator type="stringlength">
            <param name="trim">false</param>
            <param name="minLength">3</param>
            <message>username must be at least ${minLength} characters</message>
        </field-validator>
    </field>
</validators>
```

Then register `constraintAction` in `core/src/test/java/org/apache/struts2/TestConfigurationProvider.java`. Add the config beside the existing `doubleValidationActionConfig` (around line 90) — it must carry the `ValidationInterceptor` mapping, or `evaluateClientSideJsEnablement` finds no interceptor and the action looks unvalidated:

```java
        ActionConfig constraintActionConfig = new ActionConfig.Builder("", "constraintAction", ConstraintAction.class.getName())
            .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, ServletDispatcherResult.class.getName())
                    .addParam("location", "success.jsp")
                    .build())
            .addInterceptor(new InterceptorMapping("validation", validationInterceptor))
            .build();
```

and register it in the `defaultPackageConfig` builder (around line 121), next to the `doubleValidationAction` line:

```java
            .addActionConfig("constraintAction", constraintActionConfig)
```

Add `import org.apache.struts2.components.ConstraintAction;` at the top.

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=ConstraintAttributesTest`
Expected: FAIL — compilation error, `STRUTS_UI_HTML5_CONSTRAINTS` does not exist.

- [ ] **Step 3: Add the constant**

In `StrutsConstants.java`, after `STRUTS_UI_STATIC_CONTENT_PATH` (around line 204):

```java
    /**
     * Whether the html5 theme emits HTML5 constraint attributes derived from the action's validators.
     * Defaults to false in 7.4.0; the default becomes true in 8.0.0.
     *
     * @since 7.4.0
     */
    public static final String STRUTS_UI_HTML5_CONSTRAINTS = "struts.ui.html5.constraints";
```

In `default.properties`, in the "Standard UI theme" block after `struts.ui.templateSuffix=ftl`:

```properties
### Whether the html5 theme emits HTML5 constraint attributes (required, minlength,
### maxlength, pattern, min, max) derived from the action's validators.
### Defaults to false so existing html5-theme forms render unchanged; becomes true in 8.0.0.
struts.ui.html5.constraints=false
```

- [ ] **Step 4: Wire `UIBean`**

Add the field and injection setters alongside the existing ones (near `setCspNonceReader`, around line 559):

```java
    protected HtmlConstraintProvider htmlConstraintProvider;
    protected boolean html5ConstraintsEnabled;

    @Inject
    public void setHtmlConstraintProvider(HtmlConstraintProvider htmlConstraintProvider) {
        this.htmlConstraintProvider = htmlConstraintProvider;
    }

    @Inject(value = StrutsConstants.STRUTS_UI_HTML5_CONSTRAINTS, required = false)
    public void setHtml5ConstraintsEnabled(String html5ConstraintsEnabled) {
        this.html5ConstraintsEnabled = BooleanUtils.toBoolean(html5ConstraintsEnabled);
    }
```

Then, at the **end** of `evaluateParams()`, after the existing `evaluateExtraParams();` call at line 905:

```java
        evaluateExtraParams();

        // must run after evaluateExtraParams(): that is where TextField resolves attributes.type,
        // and the control type decides which constraints are legal
        addConstraintAttributes(form);
    }

    /**
     * Derives HTML5 constraint attributes for this field from the action's validators.
     *
     * @since 7.4.0
     */
    protected void addConstraintAttributes(Form form) {
        if (!html5ConstraintsEnabled || form == null || htmlConstraintProvider == null) {
            return;
        }
        String fieldName = (String) getAttributes().get("name");
        if (fieldName == null) {
            return;
        }
        Map<String, String> constraints = htmlConstraintProvider.constraintsFor(
            form.getFieldValidators(fieldName), getControlType(), stack.peek());
        if (!constraints.isEmpty()) {
            addParameter("constraints", constraints);
        }
    }
```

Add `import org.apache.commons.lang3.BooleanUtils;` if it is not already present.

Gating on `html5ConstraintsEnabled` first means the cost is exactly zero when the feature is off.

- [ ] **Step 5: Run the test to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=ConstraintAttributesTest`
Expected: PASS, 2 tests.

- [ ] **Step 6: Verify nothing regressed**

Run: `mvn test -DskipAssembly -pl core`
Expected: PASS. The constant defaults to `false`, so every existing golden file must be unchanged. If `DateTest.testJavaSqlDate` fails alone, that is the known WW-5686 flake — rerun it on its own to confirm.

- [ ] **Step 7: Commit**

```bash
git add core/src/main/java/org/apache/struts2/StrutsConstants.java \
        core/src/main/resources/org/apache/struts2/default.properties \
        core/src/main/java/org/apache/struts2/components/UIBean.java \
        core/src/test/java/org/apache/struts2/components/ConstraintAttributesTest.java \
        core/src/test/java/org/apache/struts2/components/ConstraintAction.java \
        core/src/test/resources/org/apache/struts2/components/ConstraintAction-validation.xml \
        core/src/test/java/org/apache/struts2/TestConfigurationProvider.java
git diff --cached --name-only
git commit -m "WW-5695 feat(components): derive constraint attributes during tag evaluation

Adds struts.ui.html5.constraints, default false, and wires UIBean to the
constraint provider behind it.

The hook sits at the end of evaluateParams rather than beside the tagNames
block, because evaluateExtraParams is where TextField resolves attributes.type
and it runs last; hooking earlier would make every text field look like OTHER.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 8: Render the attributes from the html5 theme

**Files:**
- Create: `core/src/main/resources/template/html5/constraints.ftl`
- Modify: `core/src/main/resources/template/html5/common-attributes.ftl`
- Test: `core/src/test/java/org/apache/struts2/views/jsp/ui/Html5ConstraintRenderingTest.java`

**Interfaces:**
- Consumes: the `constraints` attribute produced by Task 7.
- Produces: rendered HTML. Nothing downstream depends on it.

Including from `common-attributes.ftl` means every html5 input picks the attributes up without per-template edits.

- [ ] **Step 1: Write the failing test**

```java
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.views.jsp.AbstractUITagTest;

import java.util.HashMap;

public class Html5ConstraintRenderingTest extends AbstractUITagTest {

    public void testRendersConstraintAttributes() throws Exception {
        String output = render("true");

        assertTrue("expected minlength in: " + output, output.contains("minlength=\"3\""));
    }

    public void testRendersNothingWhenTheConstantIsOff() throws Exception {
        String output = render("false");

        assertFalse("expected no minlength in: " + output, output.contains("minlength="));
    }

    public void testRequiredLabelDoesNotBecomeARequiredAttribute() throws Exception {
        FormTag form = new FormTag();
        form.setPageContext(pageContext);
        form.setTheme("html5");
        form.setAction("constraintAction");
        form.setNamespace("");
        form.doStartTag();

        TextFieldTag field = new TextFieldTag();
        field.setPageContext(pageContext);
        field.setTheme("html5");
        field.setName("noValidatorHere");
        field.setRequiredLabel("true");
        field.doStartTag();
        field.doEndTag();
        form.doEndTag();

        String output = writer.toString();
        assertFalse("requiredLabel draws an asterisk; it must never emit a required attribute: " + output,
            output.contains("required=\"required\""));
    }

    private String render(String constraintsEnabled) throws Exception {
        initDispatcher(new HashMap<String, String>() {{
            put("configProviders", TestConfigurationProvider.class.getName());
            put(StrutsConstants.STRUTS_UI_HTML5_CONSTRAINTS, constraintsEnabled);
        }});
        createMocks();

        FormTag form = new FormTag();
        form.setPageContext(pageContext);
        form.setTheme("html5");
        form.setAction("constraintAction");
        form.setNamespace("");
        form.doStartTag();

        TextFieldTag field = new TextFieldTag();
        field.setPageContext(pageContext);
        field.setTheme("html5");
        field.setName("username");
        field.doStartTag();
        field.doEndTag();
        form.doEndTag();

        return writer.toString();
    }
}
```

The third test is the one that matters most: `requiredLabel` is the visual asterisk and must never become a `required` attribute. Conflating them is the most likely regression in this whole change.

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=Html5ConstraintRenderingTest`
Expected: FAIL — `testRendersConstraintAttributes` finds no `minlength`, because nothing renders the map yet.

- [ ] **Step 3: Create the template**

`core/src/main/resources/template/html5/constraints.ftl`, with the same `<#--` licence header used by the neighbouring html5 templates, then:

```
<#if attributes.constraints??><#list attributes.constraints as attributeName, attributeValue> ${attributeName}="${attributeValue?html}"<#rt/></#list></#if>
```

The `?html` escape matters: `pattern` and the `data-msg-*` values are author-controlled strings that land inside an HTML attribute.

- [ ] **Step 4: Include it**

Append to `core/src/main/resources/template/html5/common-attributes.ftl`, after the existing `accesskey` block:

```
<#include "/${attributes.templateDir}/${attributes.expandTheme}/constraints.ftl" /><#rt/>
```

- [ ] **Step 5: Run the test to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=Html5ConstraintRenderingTest`
Expected: PASS, 3 tests.

- [ ] **Step 6: Verify the whole suite**

Run: `mvn test -DskipAssembly -pl core`
Expected: PASS, including every existing html5 golden file — the constant is off by default, so `Formtag-1-html5.txt` and friends must be byte-identical.

- [ ] **Step 7: Commit**

```bash
git add core/src/main/resources/template/html5/constraints.ftl \
        core/src/main/resources/template/html5/common-attributes.ftl \
        core/src/test/java/org/apache/struts2/views/jsp/ui/Html5ConstraintRenderingTest.java
git diff --cached --name-only
git commit -m "WW-5695 feat(html5): render derived constraint attributes

Included from common-attributes.ftl so every html5 input picks the attributes
up without per-template edits. Values are HTML-escaped: pattern and the
data-msg-* text are author-controlled and land inside an attribute.

Covers the regression that matters most - requiredLabel draws an asterisk and
must never emit a required attribute.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 9: Documentation (`struts-site` repository)

**This task is in a different repository:** `~/Projects/Apache/struts-site`. It needs its own branch, its own commit, and its own PR. Docs changes there take conventional-commit form with no ticket prefix.

**Files:**
- Modify: `source/core-developers/client-side-validation.md`
- Delete: `source/core-developers/pure-java-script-client-side-validation.md`
- Modify: `source/core-developers/client-validation-example.md`
- Modify: `source/tag-developers/form-tag.md`

- [ ] **Step 1: Rewrite the client-side validation page**

Rewrite `client-side-validation.md` around the html5 theme. It must carry:

- the `struts.ui.html5.constraints` constant, its `false` default in 7.4.0 and the 8.0.0 flip;
- the full mapping table from the spec, including the `trim="false"` and `caseSensitive="true"` conditions;
- the never-change-the-type rule, stated as the reason `type="email"`, `type="url"` and `type="number"` are never set by Struts, with the `1234,50` comma-decimal example;
- the `data-msg-*` attributes, and that Struts ships nothing that consumes them;
- that `requiredLabel` is unrelated to the `required` attribute;
- a deprecation notice for the JavaScript validator pointing at WW-5694 and WW-5696.

Its existing "Client Side Validation Types" table links to the pure-JavaScript page; replace that section.

- [ ] **Step 2: Delete the pure-JavaScript page and fix the stale i18n claim**

Delete `pure-java-script-client-side-validation.md`. Before deleting, note that it claims errors are reported "not the internationalized version that the server-side might be aware of" — **that is wrong**, `ValidatorSupport.getMessage` resolves through `DelegatingValidatorContext` and `textProviderFactory`. Do not carry the claim into the rewritten page.

Grep for inbound links and repoint them:

```bash
cd ~/Projects/Apache/struts-site
grep -rn "pure-java-script-client-side-validation" source/
```

- [ ] **Step 3: Mark the example page deprecated**

Add a deprecation banner at the top of `client-validation-example.md` pointing at the html5 theme.

- [ ] **Step 4: Note the deprecated attribute**

In `tag-developers/form-tag.md`, mark `validate` deprecated since 7.4.0, removed in 8.0.0.

- [ ] **Step 5: Leave the html5 theme's "since" version alone**

`tag-developers/html5-theme.md` says "Available since Struts 7.2.0". **That is correct** — `git tag --contains e24d2f2d3` returns `STRUTS_7_2_0`. WW-5444's fix version of 7.2.1 is the wrong record. Do not "fix" the docs to match Jira; the Jira ticket is what needs correcting.

- [ ] **Step 6: Commit**

```bash
cd ~/Projects/Apache/struts-site
git checkout -b docs/html5-constraint-validation
git add source/core-developers/client-side-validation.md \
        source/core-developers/client-validation-example.md \
        source/tag-developers/form-tag.md
git rm source/core-developers/pure-java-script-client-side-validation.md
git diff --cached --name-only
git commit -m "docs: document html5 constraint validation, deprecate the JS validator

Rewrites the client-side validation page around the html5 theme's constraint
attributes and removes the pure-JavaScript page.

Drops that page's claim that client-side messages are not internationalized:
ValidatorSupport.getMessage resolves through DelegatingValidatorContext and
textProviderFactory, so they always were.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

## Follow-ups (file after this plan lands, do not do them here)

- **Temporal `min`/`max`.** `DateRangeFieldValidator` currently emits nothing. Needs per-control ISO formatting (`date` → `yyyy-MM-dd`, `month` → `yyyy-MM`, `week` → `yyyy-'W'ww`, `time` → `HH:mm`). `HtmlControlType.supportsRange()` already covers the temporal types, so no enum change is needed.
- **WW-5444's fix version** says 7.2.1; the html5 theme actually shipped in 7.2.0. Correct the ticket.
- **The regex allowlist will be too strict for some real applications.** Expect tuning after the first release; the swappable provider is the escape hatch in the meantime.
- **WW-5696** — the 8.0.0 removal. Already filed, out of scope here.
