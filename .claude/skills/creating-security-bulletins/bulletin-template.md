# Security Bulletin Template

The canonical skeleton and per-field guidance for an S2-XXX security bulletin.
Companion to [`SKILL.md`](SKILL.md), which covers *how* to establish the facts that
go in these fields; this file covers *what the page contains*.

A rendered copy lives on the Struts wiki as a restricted child of
[Security Bulletins](https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=61758)
for authors who prefer to copy a page. **This file is the source of truth** — when the
two disagree, fix the wiki page from here.

**Draft bulletins stay restricted** (read and update limited to the author plus
`struts-committers`) until the coordinated publication date. Check restrictions before
an edit and again after it: an accidentally public pre-release bulletin is an
unrecoverable disclosure.

## Fields

| Row | What goes in it |
|---|---|
| Who should read this | Usually `All Struts 2 developers and users`. Narrow it only when exposure is genuinely conditional. |
| Impact of vulnerability | A short impact phrase, not a paragraph — `Remote Code Execution`, `Denial of service`. Hedge where warranted (`Possible Remote Code Execution vulnerability`). |
| Maximum security rating | `Low` / `Moderate` / `Important` / `Critical`, matching a definition on the [Security Bulletins](https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=61758) page. That page is the only authority — the four-level naming postdates many older bulletins, so never calibrate against one. |
| Recommendation | `Upgrade to Struts X.Y.Z at least`. Name **every** maintenance line carrying the fix, and add the required action where upgrading alone is not enough. |
| Affected Software | Officially released versions only. One bullet per maintenance line; link the EOL announcement for end-of-life ranges. |
| Reporters | Credit the reporter. Include their organisation where they gave one; obfuscate any email address. |
| CVE Identifier | `CVE-YYYY-NNNNN (to be assigned before publication)` until the real identifier arrives. One CVE per independently fixable issue. |

### Affected Software

List only versions that passed a PMC release vote. A build that was cut, failed its
test period and was superseded never reached users — listing it implies an official
artifact was vulnerable and drags a phantom version into every downstream CVE record
and scanner database. Do not read the range off git tags; tags exist for builds that
were never voted through.

To find the lower bound: locate when the vulnerable construct entered with
`git log -S`, then map that commit to the first release containing it. A defect often
predates the control that was supposed to bound it, so do not assume it arrived with
the feature that made it reachable. If the mapping cannot be settled, write a visible
placeholder naming what must be confirmed — never a guessed version number.

## Problem

One to three sentences. Write for an operator, not a reviewer: the feature, the
failure, the consequence.

| Safe to publish | Never publish before the fix is out |
|---|---|
| Impact categories and consequence | Class, method, or field names |
| The component in plain words, linked to its documentation | `file:line` references |
| Whether a configured control fails to apply | Commit hashes, PR or Jira numbers |
| That state is shared / input is unvalidated | The triggering request shape or payload |
| Which released versions are affected | Reproduction steps, PoC, timing conditions |

Older bulletins explained causes and mitigations in far more detail, and that detail
was used to build working exploits. The project deliberately stopped. **Mine the
archive for structure and tone, never for depth.**

**Then say who is not affected.** An operator's first question is "does this reach
me?" — answer it, or every reader must assume it does. One sentence, linked to the
feature's documentation, either as a trailing note or folded into the opening clause.
Name the optional plugin, the setting that must be switched on, the endpoint that must
be mapped, or the unaffected sibling path. Add "earlier releases are not affected"
where there is a clean prior baseline.

Keep it at the level of a deployment decision, not a code path. Scoping *reduces* net
disclosure: it shrinks the population that has to care, and costs an attacker nothing
they could not read off a dependency list.

## Solution

`Upgrade to Struts X.Y.Z at least.` Repeat for each maintenance line, and link the
migration guide where the fix requires one.

## Backward compatibility

**Subject to the same disclosure budget as Problem.** This is the section that leaks:
describing what changed about the fixed behaviour describes the defect. Write it in
terms of what an application might *observe*, never what the fix altered internally,
and derive it from the fix diff rather than the commit message — a summary calling the
behaviour unchanged can still carry an observable difference.

It is also where a **breaking** upgrade is announced, and that announcement has to be
blunt: what must be rewritten, and what staying put costs. Where the fix is
transparent, the house sentence is simply `This change is backward compatible.`

## Workaround

Three valid outcomes, in order of preference:

1. **A verified configuration or operational change** — traced in source and confirmed
   to remove reachability. Give the change, not the mechanism. It need not be a Struts
   setting; container and reverse-proxy limits count, as does pointing at the relevant
   section of the Security Guide.
2. **Upgrade only**, when you checked and found nothing.
3. **Verified absence.** The house value is a bare `n/a`; spell it out when the reason
   is worth stating.

Never ship a workaround you reasoned about but did not confirm — it leaves operators
believing they are protected and discredits every other field on the page. "No
workaround exists" is a claim about absence and needs checking too.

A workaround usually reveals which path is affected. That is often the right trade,
but make it deliberately and record which way you went.

## Before publishing

- [ ] Every placeholder is replaced, and no guidance text survives on the page.
- [ ] The CVE identifier is real, not the placeholder.
- [ ] Affected Software lists voted releases only, and covers every maintenance line.
- [ ] The rating matches a published definition rather than an approximation.
- [ ] The workaround was verified in source, or its absence was.
- [ ] Problem, Backward compatibility and Workaround name no class, file, commit, PR
      or payload.
- [ ] The fix is **merged** into the release branch — reviewed is not merged; re-check
      now, not at drafting time.
- [ ] The fixed release is out and accepted.
- [ ] Restrictions are lifted only at the coordinated publication moment.

## Storage-format skeleton

Ready to POST to the Confluence API. Give the `excerpt` macro a **fresh**
`ac:macro-id` each time — two bulletins must not share one.

```xml
<h2>Summary</h2>
<ac:structured-macro ac:name="excerpt" ac:schema-version="1">
  <ac:parameter ac:name="atlassian-macro-output-type">BLOCK</ac:parameter>
  <ac:rich-text-body><p>ONE-LINE DESCRIPTION OF THE DEFECT</p></ac:rich-text-body>
</ac:structured-macro>
<p class="auto-cursor-target"><br/></p>
<table class="wrapped"><colgroup><col/><col/></colgroup><tbody>
  <tr><th><p>Who should read this</p></th><td><p>All Struts 2 developers and users</p></td></tr>
  <tr><th><p>Impact of vulnerability</p></th><td><p>IMPACT PHRASE</p></td></tr>
  <tr><th><p>Maximum security rating</p></th><td><p>Low | Moderate | Important | Critical</p></td></tr>
  <tr><th><p>Recommendation</p></th><td><p>Upgrade to Struts X.Y.Z at least</p></td></tr>
  <tr><th><p>Affected Software</p></th><td><ul style="list-style-type: square;">
    <li>Struts A.B.C through Struts D.E.F</li></ul></td></tr>
  <tr><th><p>Reporters</p></th><td><p>REPORTER</p></td></tr>
  <tr><th><p>CVE Identifier</p></th><td><p>CVE-YYYY-NNNNN (to be assigned before publication)</p></td></tr>
</tbody></table>
<h2>Problem</h2>
<p>WHAT THE DEFECT ALLOWS, IN OPERATOR TERMS.</p>
<p>WHO IS NOT AFFECTED, AND WHY.</p>
<h2>Solution</h2>
<p>Upgrade to Struts X.Y.Z at least.</p>
<h2>Backward compatibility</h2>
<p>This change is backward compatible.</p>
<h2>Workaround</h2>
<p>WORKAROUND, OR A STATEMENT THAT NONE EXISTS.</p>
```
