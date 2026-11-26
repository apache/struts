# Pluggable tag escaper

**Date:** 2026-11-26 (supersedes the 2026-06-26 property-only design)
**Jira:** [WW-5639](https://issues.apache.org/jira/browse/WW-5639)
**Target release:** 7.6.0
**Credit:** port of Greg Huber's patch attached to WW-5639 (`ww-5639-struts.patch`, Struts 6)

## Intent

`<s:property>` and `<s:text>` hard-code their escaping in a private `prepare()` that chains
the selected escapers in a fixed order (`escapeHtml4` → `escapeEcmaScript` → `escapeXml10` →
`escapeCsv`).

1. **Pluggability** (Greg Huber): applications want to replace the escaping, e.g. let a
   safelist of markup (`<b>`, `<br>`) through via jsoup instead of escaping everything, and
   treat `property` and `text` differently. The application owns the risk of its replacement.
2. **One broken combination**: `escapeHtml` + `escapeXml` double-escapes
   (`&` → `&amp;amp;`). Every other combination is either a single escaper or a useful chain.

## Why the escapers stay chained

The 2026-06-26 design made the options mutually exclusive with HTML first. Probing
commons-text showed that is wrong:

| Value `"x' & <b></b>` | Output | Verdict |
|---|---|---|
| js only | `\"x\' & <b><\/b>` | `<\/b>` is correct JS escaping, not chaining damage |
| html + js | `&quot;x\' &amp; &lt;b&gt;&lt;\/b&gt;` | useful: safe in `onclick="…"` and in a JS string |
| html + xml | `&amp;quot;x&apos; &amp;amp; …` | broken: double-escaped |
| html + csv | `&quot;x' &amp; &lt;b&gt;&lt;/b&gt;` | fine |

`escapeHtml` defaults to `true` on `<s:property>` and is disabled only by an explicit
`escapeHtml="false"`, so `<s:property escapeJavaScript="true"/>` means html + js. HTML-wins
exclusivity would drop JS escaping there (`'` and `\` pass `escapeHtml4` untouched — a JS
string breakout); JS-wins exclusivity would drop HTML escaping (`\"` closes an HTML attribute).
Both are rejected.

## Goals / non-goals

- Goal: one container bean, replaceable via `struts.xml` + a constant, used by both tags.
- Goal: fix the html + xml double escaping without changing any other output.
- Non-goal: changing `<s:text>`'s `escapeHtml=false` default.
- Non-goal: other tags (`actionmessage`, `actionerror` — [WW-5877](https://issues.apache.org/jira/browse/WW-5877); `fielderror`, `escapeHtmlBody`).
- Non-goal: an ordered `escapeMode="js,html"` attribute — follow-up ticket (see below).
- Non-goal: `struts-site` docs (updated separately).
- Anti-goal: any change in rendered output in the first commit.

## Approaches considered

| Approach | Trade-off | Decision |
|---|---|---|
| Greg's patch as is: `PropertyEscapeProvider` + `TextEscapeProvider`, `escape(boolean, boolean, boolean, boolean, String)` | Two duplicate interfaces (one default bean per type); flag arguments lock the signature to today's four flags | Rejected |
| One interface, config carries a single resolved mode | Can't express html + js, which is the useful default chain | Rejected |
| **One interface, `escape(EscapeConfig, String)`; config carries origin and raw flags** | Custom escapers have full freedom; config can grow (e.g. an ordered chain) without breaking them | **Chosen** |

## Design

Package `org.apache.struts2.components.escape`:

```java
public interface TagEscaper {
    String escape(EscapeConfig config, String value);
}

public enum TagOrigin { PROPERTY, TEXT }

public final class EscapeConfig {            // immutable, accessors only
    TagOrigin origin();
    boolean html(); boolean javaScript(); boolean xml(); boolean csv();
}

public class StrutsTagEscaper implements TagEscaper { ... }   // default
```

`StrutsTagEscaper` exposes each step as a `protected` hook (`escapeHtml`, `escapeJavaScript`,
`escapeXml`, `escapeCsv`), so an application that only wants different HTML handling (e.g. a
jsoup safelist) extends it and overrides one method, keeping the default ordering and the
XML-supersedes-HTML rule.

`EscapeConfig` exposes no public four-boolean constructor, so a later ordered chain can be
added without breaking custom escapers.

Wiring follows `DateFormatter`:

- `StrutsConstants.STRUTS_TAG_ESCAPER = "struts.tag.escaper"`
- `default.properties`: `struts.tag.escaper=struts`
- `struts-beans.xml`: `<bean type="...TagEscaper" name="struts" class="...StrutsTagEscaper" scope="singleton"/>`
- `StrutsBeanSelectionProvider`: `alias(TagEscaper.class, STRUTS_TAG_ESCAPER, ...)`
- `Property` and `Text`: `@Inject setTagEscaper(TagEscaper)`; `prepare()` delegates to
  `tagEscaper.escape(config, value)`.

User override:

```xml
<bean type="org.apache.struts2.components.escape.TagEscaper" name="myEscaper" class="com.acme.MyEscaper"/>
<constant name="struts.tag.escaper" value="myEscaper"/>
```

## Delivery: one PR, two commits

**Commit 1 — plug point, no output change.** Everything above; `StrutsTagEscaper` chains
exactly as `prepare()` does today. Tests pin today's output for both tags, including html + js
and html + xml, and show a custom `TagEscaper` bean replacing the default.

**Commit 2 — XML supersedes HTML.** When `escapeXml` is set, `StrutsTagEscaper` skips HTML
escaping (`escapeXml10` already covers `< > & " '`, so nothing becomes less safe). All other
chaining is unchanged. No warning is logged: on `<s:property>` `escapeHtml` defaults to `true`, so html + xml
is the normal result of a plain `escapeXml="true"` and a warning would fire on every render.
The html + xml test assertion from commit 1 changes — that diff is the whole behaviour change.
Tests also pin `x'; alert(1); //\ <b>` with `escapeJavaScript="true"` and default `escapeHtml`
keeping `'` escaped.

Tag attribute descriptions and class JavaDoc for both tags:

- `escapeHtml=false` emits raw HTML — an XSS risk unless the value is trusted.
- `escapeJavaScript` is applied on top of HTML escaping unless `escapeHtml="false"`; it escapes
  `/` as `\/`, so `</b>` becomes `<\/b>`.
- `escapeXml` replaces HTML escaping.

Release notes: an improvement line on the 7.6.0 Version Notes draft page, not Breaking changes —
only already double-escaped output changes.

| Usage | Today / commit 1 | Commit 2 |
|---|---|---|
| property default | html | html |
| text default | none | none |
| property `escapeJavaScript=true` | html + js | html + js |
| `escapeHtml=false escapeJavaScript=true` | js | js |
| `escapeHtml=false escapeXml=true` | xml | xml |
| property `escapeXml=true` | html + xml (double-escaped) | xml |
| property `escapeCsv=true` | html + csv | html + csv |

## Follow-up ([WW-5876](https://issues.apache.org/jira/browse/WW-5876))

An ordered `escapeMode` attribute (e.g. `escapeMode="js,html"`) on tags with escape options,
introducing an `EscapeMode` enum and an ordered chain on `EscapeConfig`. JS-then-HTML is the
correct order for JS in an HTML attribute and today's flags can't express it.

## Left to the builder

- `EscapeConfig` construction (package-private constructor or builder), `equals`/`toString`.
- Test placement: `PropertyTest` is JUnit 3 style (`StrutsInternalTestCase`); match each file.
