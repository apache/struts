# Version Notes Template

The canonical skeleton and per-field guidance for a Struts **Version Notes X.Y.Z** page
on the [Apache Struts 2 Wiki](https://cwiki.apache.org/confluence/spaces/WW) (space `WW`).
Companion to [`SKILL.md`](SKILL.md), which covers *how* to establish the values;
this file covers *what the page contains*.

**This file is the source of truth.** Start every page from the skeleton below, never
from a copy of the previous release's page — see the Iron Rule in `SKILL.md`.

## Fields

| Field | What goes in it |
|---|---|
| Version | The release being announced, e.g. `6.11.0`. Appears in the intro sentence, the page title, the Maven snippet, and both JIRA link labels. |
| Parent page | Always `Migration Guide`, page id `13981`. Create the page as its child, and add it to that page's index — see `SKILL.md`. |
| Prior notes page | Title of the previous **released** version's page in the same series, e.g. `Version Notes 6.10.0`. Skip versions that were cut but never released. |
| JIRA version id | The numeric id for `ReleaseNote.jspa?version=`. Obtain from the WW project's versions — it is not the version name. `6.10.0` is `12357065`, `7.2.1` is `12355751`. |
| DONE filter id | Saved-filter id for `issues/?filter=`, labelled `Struts X.Y.Z DONE`. Each release needs its own; a reused id lists the wrong release. |
| TODO filter id | Constant across releases: `12351174`, labelled `Struts x.x.x TODO`. |
| Issue sections | One `<h2>` per issue type present, ordered **Bug → New Feature → Improvement → Task → Dependency**, entries sorted by key ascending. |
| Breaking changes | Optional. Authored, **one sentence plus the ticket link** per item. Omit the section when the release has none. |
| Deprecations | Optional. Same one-line shape, for public API deprecated but still working. |
| Rejected requests | Optional. Tickets resolved `Won't Do` against this fix version — never in a type section. |
| Staging Repository | Always included, on every line — see `SKILL.md`. |

## Corrected storage format

Three defects present in the published pages are fixed here. Keep them fixed:

1. **`ac:name="language"` on the code macros.** The published Maven Dependency and
   Staging Repository macros carry `ac:name=""` with the value `xml`, which is a
   malformed parameter. The Archetype Catalog macro on the same pages has it right.
2. **No `ac:macro-id` attributes.** The published pages share hard-coded macro ids
   across releases and across series because they were cloned. Omit the attribute and
   let Confluence assign one on save.
3. **No trailing empty `<div>`s.** Every published page ends with two empty divs
   carrying inline `font-size: 24.0px` styling. They render as stray whitespace.

```xml
<p><ac:emoticon ac:name="tick"/> These are the notes for the Struts version X.Y.Z distribution.</p>
<p><ac:emoticon ac:name="tick"/> For prior notes in this release series, see <ac:link><ri:page ri:content-title="Version Notes PRIOR"/></ac:link></p>
<p><ac:structured-macro ac:name="toc" ac:schema-version="1"/></p>

<h2>Maven users</h2>
<p>If you are a Maven user, you might want to get started using the <ac:link><ri:page ri:content-title="Struts 2 Maven Archetypes"/><ac:plain-text-link-body><![CDATA[Maven Archetype]]></ac:plain-text-link-body></ac:link>.</p>
<ac:structured-macro ac:name="code" ac:schema-version="1">
  <ac:parameter ac:name="title">Maven Dependency</ac:parameter>
  <ac:parameter ac:name="language">xml</ac:parameter>
  <ac:plain-text-body><![CDATA[<dependency>
  <groupId>org.apache.struts</groupId>
  <artifactId>struts2-core</artifactId>
  <version>X.Y.Z</version>
</dependency>
]]></ac:plain-text-body>
</ac:structured-macro>
<p>You can also use Struts Archetype Catalog like below</p>
<ac:structured-macro ac:name="code" ac:schema-version="1">
  <ac:parameter ac:name="language">text</ac:parameter>
  <ac:parameter ac:name="title">Struts Archetype Catalog</ac:parameter>
  <ac:plain-text-body><![CDATA[mvn archetype:generate -DarchetypeCatalog=http://struts.apache.org/]]></ac:plain-text-body>
</ac:structured-macro>

<ac:structured-macro ac:name="code" ac:schema-version="1">
  <ac:parameter ac:name="title">Staging Repository</ac:parameter>
  <ac:parameter ac:name="language">xml</ac:parameter>
  <ac:plain-text-body><![CDATA[<repositories>
  <repository>
    <id>apache.nexus</id>
    <name>ASF Nexus Staging</name>
    <url>https://repository.apache.org/content/groups/staging/</url>
  </repository>
</repositories>]]></ac:plain-text-body>
</ac:structured-macro>

<!-- OPTIONAL: omit the whole section when the release has no breaking changes -->
<h2>Breaking changes</h2>
<ul style="list-style-type: square;">
  <li>ONE SENTENCE: WHAT AN APPLICATION SEES DIFFERENTLY [<a href="https://issues.apache.org/jira/browse/WW-XXXX">WW-XXXX</a>].</li>
</ul>

<!-- OPTIONAL: public API deprecated but still working -->
<h2>Deprecations</h2>
<ul style="list-style-type: square;">
  <li><code>WHAT</code> is deprecated; use <code>REPLACEMENT</code> instead [<a href="https://issues.apache.org/jira/browse/WW-XXXX">WW-XXXX</a>].</li>
</ul>

<!-- OPTIONAL: tickets resolved Won't Do against this fix version -->
<h2>Rejected requests</h2>
<p>Two long-standing requests were closed as <em>Won't Do</em> in this cycle. They are listed here so the decision is visible rather than silent.</p>
<ul style="list-style-type: square;">
  <li>[<a href="https://issues.apache.org/jira/browse/WW-XXXX">WW-XXXX</a>] - SUMMARY - will not be implemented; REASON WHERE THE RELEASE MANAGER GAVE ONE.</li>
</ul>

<h2>Bug</h2>
<ul><li>[<a href="https://issues.apache.org/jira/browse/WW-XXXX">WW-XXXX</a>] - JIRA SUMMARY</li></ul>

<h2>Issue Detail</h2>
<ul><li><a href="https://issues.apache.org/jira/secure/ReleaseNote.jspa?projectId=12311041&amp;version=JIRA_VERSION_ID">JIRA Release Notes X.Y.Z</a></li></ul>

<h2>Issue List</h2>
<ul>
  <li><a href="https://issues.apache.org/jira/issues/?filter=DONE_FILTER_ID">Struts X.Y.Z DONE</a></li>
  <li><a href="https://issues.apache.org/jira/issues/?filter=12351174">Struts x.x.x TODO</a></li>
</ul>

<h2>Other resources</h2>
<ul>
  <li><a href="http://www.mail-archive.com/commits%40struts.apache.org/">Commit Logs</a></li>
  <li><a href="https://gitbox.apache.org/repos/asf?p=struts.git;a=summary">Source Code Repository</a></li>
</ul>
```

Repeat the issue `<h2>` block per type present, in the order given above.
`projectId=12311041` is the WW project and is constant. Note `&amp;` in the
`ReleaseNote.jspa` URL — a bare `&` is invalid in storage format.

## Before publishing

- [ ] Every placeholder is replaced, and no guidance text survives on the page.
- [ ] Page title is `Version Notes X.Y.Z` and the intro names the same version.
- [ ] Prior-notes link resolves, and names the previous **released** version.
- [ ] Maven snippet version matches the release.
- [ ] `ReleaseNote.jspa` label and its `version=` id are the same release.
- [ ] `DONE` filter label and its `filter=` id are the same release.
- [ ] Issue list reconciled against the release branch via each ticket's linked PR, not taken from JIRA alone.
- [ ] Every ticket's **resolution** checked, not just its status — `Won't Do` goes under Rejected requests.
- [ ] Sections ordered Breaking changes → Deprecations → Rejected requests → Bug → New Feature → Improvement → Task → Dependency; empty ones omitted.
- [ ] Each Breaking changes and Deprecations item is one sentence plus its ticket link.
- [ ] Staging Repository block present.
- [ ] No unpublished severity, CVE, or S2-XXX reference anywhere on the page, and any security summary truncated at a clause boundary was reported to the release manager.
- [ ] Page created as a child of Migration Guide (`13981`).
- [ ] **Listed at the top of the matching `Version Notes N.x` section on the Migration Guide**, and that edit verified against raw storage — the version diff renders empty even when the change landed.
- [ ] Page re-fetched immediately before every write.

## GitHub release notes

- [ ] Body generated with `previous_tag_name` named explicitly, not left to GitHub's guess.
- [ ] Entry count sane against `git log PREV..THIS`.
- [ ] Original body saved first when editing an existing release, so it can be restored.
- [ ] Entries split by **ticket, not author**: ticketed → `## What's Changed`; untick eted dependency bumps → `### Dependencies`; mixed PRs stay in What's Changed.
- [ ] Generated order and entry text preserved within each section.
- [ ] Split verified by diffing the sorted entry lists before and after — empty output.
- [ ] `--prerelease` passed while the vote is open; `--verify-tag` when creating.

## Test-build announcement

- [ ] Drafted **after** the Version Notes page and GitHub release exist — it links both.
- [ ] Subject `[TEST] Apache Struts X.Y.Z test build is ready`.
- [ ] Addressed to **both** `dev@struts.apache.org` and `user@struts.apache.org`, Bcc `private@struts.apache.org`.
- [ ] Risk clause matches reality: silent when there are no Breaking changes, "but it contains significant changes" when there are.
- [ ] Tag underscored in the release link, version dotted in the dist path and page title.
