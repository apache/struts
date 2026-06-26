# Pluggable Tag Escaper Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the hard-coded escaping in `<s:property>` and `<s:text>` with one container bean (`TagEscaper`) that applications can swap, then stop `escapeHtml` + `escapeXml` from double-escaping.

**Architecture:** New package `org.apache.struts2.components.escape` holds `TagEscaper`, `TagOrigin`, the immutable `EscapeConfig` and the default `StrutsTagEscaper`. The bean is selected by the constant `struts.tag.escaper`, wired exactly like `DateFormatter`. `Property` and `Text` build an `EscapeConfig` from their flags and delegate.

**Tech Stack:** Java 17, Struts container (`@Inject`, `struts-beans.xml`, `StrutsBeanSelectionProvider`), commons-text `StringEscapeUtils`, JUnit 3/4 + AssertJ.

**Spec:** `docs/superpowers/specs/2026-11-26-pluggable-tag-escaper-design.md`

## Global Constraints

- Delivered as two code commits in one PR. Commit 1 changes no rendered output; commit 2 changes only html + xml.
- Commit messages: `WW-5639 feat(tags): ...` / `WW-5639 fix(tags): ...`, ending with the `Co-Authored-By: Claude Opus 5.5` trailer. Greg Huber is credited in the PR body only.
- Tests are JUnit 3 (`StrutsInternalTestCase`, `testXxx()`) or JUnit 4 (`org.junit.Test`) — match the file. New files in `components/escape` use JUnit 4 + AssertJ. Never Jupiter.
- Names: `TagEscaper`, `TagOrigin { PROPERTY, TEXT }`, `EscapeConfig`, `StrutsTagEscaper`; constant `StrutsConstants.STRUTS_TAG_ESCAPER = "struts.tag.escaper"`; default bean name `struts`.
- `EscapeConfig` has no public constructor taking the four booleans (WW-5876 will add an ordered chain).
- No `EscapeMode` enum and no `effectiveMode()` in this PR.
- No null guard on the injected `TagEscaper`; tests that build `Property`/`Text` directly inject it.
- Run Maven with `./mvnw` from the worktree root, logging to `$LOGS` (the session scratchpad directory), and check the exit code.
- Expected escaper strings were probed against commons-text 1.9; the build uses 1.15.0, so a mismatch in Task 1 means re-probe, not "fix the code".

## Review Focus

1. A `null` value reaching the escaper (e.g. `Text` with no resolvable message) — expect `null` back, no NPE. Pinned in Task 1.
2. Pooled JSP tags — the config must be built from the current flags on every render, never cached on the component. Covered by the existing `*_clearTagStateSet` tests in `PropertyTagTest`/`TextTagTest`, which must stay green in Task 2.
3. `<s:text escapeHtml="true" escapeXml="true">` — the XML-supersedes-HTML rule applies to `TEXT` as well as `PROPERTY`. Pinned in Task 3.
4. `<s:property escapeJavaScript="true"/>` with default `escapeHtml` and value `x'; alert(1); //\ <b>` — `'` stays escaped after commit 2. Pinned in Task 3.
5. An empty string value — returned as `""` by every combination. Pinned in Task 1.

---

### Task 1: `escape` package with a chaining default

**Files:**
- Create: `core/src/main/java/org/apache/struts2/components/escape/TagEscaper.java`
- Create: `core/src/main/java/org/apache/struts2/components/escape/TagOrigin.java`
- Create: `core/src/main/java/org/apache/struts2/components/escape/EscapeConfig.java`
- Create: `core/src/main/java/org/apache/struts2/components/escape/StrutsTagEscaper.java`
- Test: `core/src/test/java/org/apache/struts2/components/escape/EscapeConfigTest.java`
- Test: `core/src/test/java/org/apache/struts2/components/escape/StrutsTagEscaperTest.java`

**Interfaces:**
- Produces:
  - `interface TagEscaper { String escape(EscapeConfig config, String value); }`
  - `enum TagOrigin { PROPERTY, TEXT }`
  - `final class EscapeConfig` — `static Builder builder(TagOrigin origin)`; `Builder html(boolean)`, `javaScript(boolean)`, `xml(boolean)`, `csv(boolean)`, `EscapeConfig build()`; accessors `TagOrigin origin()`, `boolean html()`, `javaScript()`, `xml()`, `csv()`; `equals`/`hashCode`/`toString`. All flags default to `false` in the builder.
  - `class StrutsTagEscaper implements TagEscaper` with a no-arg constructor and four `protected String` hooks, `escapeHtml(String)`, `escapeJavaScript(String)`, `escapeXml(String)`, `escapeCsv(String)`, delegating to `escapeHtml4`, `escapeEcmaScript`, `escapeXml10`, `escapeCsv`. `escape(EscapeConfig, String)` decides which hooks run and in which order; the hooks only transform a string.

- [ ] **Step 1: Write the failing tests**

`EscapeConfigTest`:
- `builderDefaultsAllFlagsToFalse` — `builder(PROPERTY).build()`: origin `PROPERTY`, all four accessors `false`.
- `builderSetsEachFlag` — each setter reflected by its accessor.
- `equalConfigsAreEqual` — two identical builds are `equals` with equal `hashCode`.

`StrutsTagEscaperTest`, value `V = "\"x' & <b></b>"` (outputs verified with commons-text):
- `noFlagsReturnsValueUnchanged` → `V`
- `htmlOnly` → `&quot;x' &amp; &lt;b&gt;&lt;/b&gt;`
- `javaScriptOnly` → `\"x\' & <b><\/b>`
- `htmlThenJavaScript` → `&quot;x\' &amp; &lt;b&gt;&lt;\/b&gt;`
- `htmlThenXmlDoubleEscapes` → `&amp;quot;x&apos; &amp;amp; &amp;lt;b&amp;gt;&amp;lt;/b&amp;gt;` (today's behaviour; changed in Task 3)
- `htmlThenCsv` → `&quot;x' &amp; &lt;b&gt;&lt;/b&gt;`
- `csvOnly` → `"""x' & <b></b>"`
- `nullValueReturnsNull` — all flags on, `null` → `null`
- `emptyValueReturnsEmpty` — all flags on, `""` → `""`
- `subclassCanReplaceHtmlStep` — an anonymous subclass overriding `escapeHtml(String)` to return `"<html:" + value + ">"`; html + js on `V` → `escapeEcmaScript("<html:" + V + ">")`, proving the override runs in the chain and the other steps are untouched.

- [ ] **Step 2: Run them and see them fail**

Run: `./mvnw test -DskipAssembly -pl core -Dtest='EscapeConfigTest,StrutsTagEscaperTest' > $LOGS/t1.log 2>&1; echo $?`
Expected: non-zero, compilation errors for the missing types.

- [ ] **Step 3: Implement the four types**

`StrutsTagEscaper.escape` calls, in order, `escapeHtml` → `escapeJavaScript` → `escapeXml` → `escapeCsv` hooks, each only when its flag is set — the logic of today's `Property.prepare()`.

- [ ] **Step 4: Run the tests and see them pass**

Same command; expected exit `0`.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/components/escape core/src/test/java/org/apache/struts2/components/escape
git commit -m "WW-5639 feat(tags): pluggable TagEscaper for property and text tags"
```

(Task 2 amends this commit, so the PR has one commit for the whole plug point.)

### Task 2: Wire the bean and delegate from `Property` and `Text`

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/StrutsConstants.java` (next to `STRUTS_DATE_FORMATTER`, ~line 822)
- Modify: `core/src/main/resources/org/apache/struts2/default.properties` (after `struts.date.formatter`, ~line 357)
- Modify: `core/src/main/resources/struts-beans.xml` (after the `DateFormatter` beans, ~line 245)
- Modify: `core/src/main/java/org/apache/struts2/config/StrutsBeanSelectionProvider.java` (after the `DateFormatter` alias, ~line 455)
- Modify: `core/src/main/java/org/apache/struts2/components/Property.java` (`prepare()`, ~lines 164-179)
- Modify: `core/src/main/java/org/apache/struts2/components/Text.java` (`prepare()`, ~lines 214-229)
- Test: `core/src/test/java/org/apache/struts2/components/PropertyTest.java`
- Create test: `core/src/test/java/org/apache/struts2/components/TextTest.java` (JUnit 3, `StrutsInternalTestCase`)

**Interfaces:**
- Consumes: everything Task 1 produces.
- Produces: `Property.setTagEscaper(TagEscaper)` and `Text.setTagEscaper(TagEscaper)`, both `@Inject`.

- [ ] **Step 1: Write the failing tests**

`PropertyTest`:
- In every existing test that builds `new Property(stack)`, call `container.inject(property)` (or move construction into a helper that does).
- `testDefaultTagEscaperIsStrutsTagEscaper` — `container.getInstance(TagEscaper.class)` is a `StrutsTagEscaper`.
- `testCustomTagEscaperReceivesPropertyConfig` — a recording `TagEscaper` that returns `"[escaped]"` and stores the config; with `setEscapeJavaScript(true)`, output is `"[escaped]"` and the recorded config is `origin=PROPERTY, html=true, javaScript=true, xml=false, csv=false`.
- `testDefaultHtmlAndJavaScriptStillChain` — value `x'; alert(1); //\ <b>`, `setEscapeJavaScript(true)`, default `escapeHtml`: output `x\'; alert(1); \/\/\\ &lt;b&gt;`.

`TextTest`:
- `testCustomTagEscaperReceivesTextConfig` — `new Text(stack)`, `container.inject(text)`, then the recording escaper; name an unresolvable key, `setEscapeXml(true)`; recorded config is `origin=TEXT, html=false, javaScript=false, xml=true, csv=false`, and output is `"[escaped]"`.

- [ ] **Step 2: Run them and see them fail**

Run: `./mvnw test -DskipAssembly -pl core -Dtest='PropertyTest,TextTest' > $LOGS/t2.log 2>&1; echo $?`
Expected: non-zero, compilation errors (`setTagEscaper`, `TagEscaper` bean missing).

- [ ] **Step 3: Wire and delegate**

- Constant `STRUTS_TAG_ESCAPER = "struts.tag.escaper"`, with a `See {@link Property#setTagEscaper(TagEscaper)}` JavaDoc line like its `DateFormatter` neighbour.
- `default.properties`: a one-line `###` comment and `struts.tag.escaper=struts`.
- `struts-beans.xml`: `<bean type="org.apache.struts2.components.escape.TagEscaper" name="struts" class="org.apache.struts2.components.escape.StrutsTagEscaper" scope="singleton"/>`.
- `StrutsBeanSelectionProvider`: `alias(TagEscaper.class, StrutsConstants.STRUTS_TAG_ESCAPER, builder, props, Scope.SINGLETON);`.
- `Property`/`Text`: field `tagEscaper`, `@Inject public void setTagEscaper(TagEscaper)`; `prepare()` builds `EscapeConfig.builder(TagOrigin.PROPERTY|TEXT)` from the four fields on each call and returns `tagEscaper.escape(config, value)`. Remove the now-unused `StringEscapeUtils` imports.

- [ ] **Step 4: Run the tag tests and see them pass**

Run: `./mvnw test -DskipAssembly -pl core -Dtest='PropertyTest,TextTest,PropertyTagTest,TextTagTest,StrutsTagEscaperTest,EscapeConfigTest' > $LOGS/t2.log 2>&1; echo $?`
Expected: exit `0`. The existing escape tests in `PropertyTagTest`/`TextTagTest` pass unchanged — that is the "no output change" proof.

- [ ] **Step 5: Run the velocity plugin tests** (its directives build `Property`/`Text` through `container.inject`)

Run: `./mvnw install -DskipAssembly -DskipTests -pl core -q && ./mvnw test -DskipAssembly -pl plugins/velocity > $LOGS/t2v.log 2>&1; echo $?`
Expected: exit `0`.

- [ ] **Step 6: Amend into commit 1**

```bash
git add core/src/main/java/org/apache/struts2/StrutsConstants.java core/src/main/resources/org/apache/struts2/default.properties core/src/main/resources/struts-beans.xml core/src/main/java/org/apache/struts2/config/StrutsBeanSelectionProvider.java core/src/main/java/org/apache/struts2/components/Property.java core/src/main/java/org/apache/struts2/components/Text.java core/src/test/java/org/apache/struts2/components/PropertyTest.java core/src/test/java/org/apache/struts2/components/TextTest.java
git commit --amend --no-edit
```

### Task 3: XML supersedes HTML, devMode warning, tag docs (commit 2)

> Amended after the final review: the devMode warning was dropped (it fired on every plain `<s:property escapeXml="true"/>`); the two devMode tests became `xmlSupersedingHtmlLogsNoWarning`.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/components/escape/StrutsTagEscaper.java`
- Modify: `core/src/main/java/org/apache/struts2/components/Property.java` (class JavaDoc params ~lines 41-48, `@StrutsTagAttribute` on the four setters)
- Modify: `core/src/main/java/org/apache/struts2/components/Text.java` (class JavaDoc params ~lines 66-71, `@StrutsTagAttribute` on the four setters)
- Test: `core/src/test/java/org/apache/struts2/components/escape/StrutsTagEscaperTest.java`
- Test: `core/src/test/java/org/apache/struts2/components/TextTest.java`

**Interfaces:**
- Consumes: Task 1 and 2 types.
- Produces: `StrutsTagEscaper.setDevMode(String)` annotated `@Inject(value = StrutsConstants.STRUTS_DEVMODE, required = false)`, parsed with `BooleanUtils.toBoolean` as in `Component.setDevMode`.

- [ ] **Step 1: Change and add the failing tests**

`StrutsTagEscaperTest`:
- Rename `htmlThenXmlDoubleEscapes` → `xmlSupersedesHtml`; expected becomes `escapeXml10(V)` = `&quot;x&apos; &amp; &lt;b&gt;&lt;/b&gt;`.
- `xmlSupersedesHtmlAndStillChainsCsv` — html + xml + csv → `escapeCsv(escapeXml10(V))`.
- `devModeWarnsWhenXmlSupersedesHtml` — `setDevMode("true")`, html + xml, inside `try (LogCapture log = new LogCapture(StrutsTagEscaper.class))`: exactly one `WARN` message, containing `escapeXml` and `escapeHtml`.
- `noWarningWithoutDevMode` — same config, devMode unset: no `WARN` message.
- `htmlThenJavaScript`, `htmlThenCsv` and the rest stay unchanged.

`TextTest`:
- `testTextXmlSupersedesHtml` — injected default escaper, a message resolving to `1 < 2 & 3`, `setEscapeHtml(true)`, `setEscapeXml(true)` → `1 &lt; 2 &amp; 3` (no `&amp;lt;`).

- [ ] **Step 2: Run them and see them fail**

Run: `./mvnw test -DskipAssembly -pl core -Dtest='StrutsTagEscaperTest,TextTest' > $LOGS/t3.log 2>&1; echo $?`
Expected: non-zero; `xmlSupersedesHtml` and the devMode tests fail.

- [ ] **Step 3: Implement**

`StrutsTagEscaper.escape`: skip the `escapeHtml` hook when `config.xml()` is set (the decision stays in `escape`, so subclasses overriding a hook inherit the rule); in devMode, when both `html()` and `xml()` are set, log one `WARN` naming the tag origin and saying `escapeHtml` was skipped because `escapeXml` replaces it. Everything else unchanged.

Docs, in both tags' class JavaDoc and `@StrutsTagAttribute(description=…)` (wording from the spec):
- `escapeHtml`: `false` emits raw, unescaped HTML — an XSS risk unless the value is trusted.
- `escapeJavaScript`: applied on top of HTML escaping unless `escapeHtml` is `false`; escapes `/` as `\/`, so `</b>` becomes `<\/b>`.
- `escapeXml`: replaces HTML escaping.

- [ ] **Step 4: Run all tag tests and see them pass**

Run: `./mvnw test -DskipAssembly -pl core -Dtest='StrutsTagEscaperTest,EscapeConfigTest,PropertyTest,TextTest,PropertyTagTest,TextTagTest' > $LOGS/t3.log 2>&1; echo $?`
Expected: exit `0`. `PropertyTest#testDefaultHtmlAndJavaScriptStillChain` passes unchanged.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/components/escape/StrutsTagEscaper.java core/src/main/java/org/apache/struts2/components/Property.java core/src/main/java/org/apache/struts2/components/Text.java core/src/test/java/org/apache/struts2/components/escape/StrutsTagEscaperTest.java core/src/test/java/org/apache/struts2/components/TextTest.java
git commit -m "WW-5639 fix(tags): escapeXml replaces escapeHtml instead of double-escaping"
```

### Task 4: Full verification

- [ ] **Step 1: Full core test run**

Run: `./mvnw test -DskipAssembly -pl core > $LOGS/core.log 2>&1; echo $?`
Expected: exit `0`. (`DateTest.testJavaSqlDate` is a known flake; rerun once if it is the only failure.)

- [ ] **Step 2: Confirm the branch shape**

Run: `git log --oneline origin/main..HEAD` and `git status --short`
Expected: three commits (design doc, plug point, XML fix); no `??` probe files.

The PR (review, `/security-review`, Greg's credit, the 7.6.0 improvement line) is handled by the finishing step, not this plan.
