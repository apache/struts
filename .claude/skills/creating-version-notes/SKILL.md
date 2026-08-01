---
name: creating-version-notes
description: Use when preparing, updating, or reviewing a Version Notes page on the Struts cwiki for a release or release candidate, on any maintenance line (6.x, 7.x), including assembling the issue list for a fix version.
---

# Creating Version Notes

## Overview

A Version Notes page answers one question for a user deciding whether to upgrade: **what changed in this release, and what will break.** Almost all of it is a mechanical rendering of a JIRA fix version onto fixed boilerplate.

**Core principle:** the mechanical parts must be *derived*, never retyped; the two judgement parts — Breaking changes, and how a security fix is described — are the only places you author prose.

**One skill covers every maintenance line.** 6.x and 7.x pages share an identical structure. The line changes the data (version, prior page, JIRA ids), never the process.

## The Iron Rule

```
START FROM THE TEMPLATE. NEVER CLONE THE PREVIOUS VERSION NOTES PAGE.
```

Cloning is how the published pages acquired their defects, and it fails differently every time:

| Page | Inherited defect |
|---|---|
| Version Notes 6.9.0 | Issue Detail links **"JIRA Release Notes 6.8.0"** — label and `version=` id both from 6.8.0 |
| Version Notes 6.10.0 | Issue List links **"Struts 6.9.0 DONE"** — label names the previous release, against a `filter=` id different from the one the 6.9.0 page used |
| Both series | Maven Dependency code macro carries `ac:name=""` instead of `ac:name="language"` |

Half-updated links are the signature failure: the number gets fixed and the label doesn't, or the reverse. They survive review because the link still works — it just points at, or claims to be, the wrong release.

**[`version-notes-template.md`](version-notes-template.md) is the source of truth**: field guidance, storage-format skeleton with those defects corrected, and the pre-publication checklist.

## Collect every input before writing

Each row is derived from a named source. A value you cannot source is a visible placeholder, never a guess.

| Input | Where it comes from |
|---|---|
| Version | The release being voted or announced |
| Prior notes page title | The previous **released** version in the same series — see below |
| JIRA version id | Numeric id behind `ReleaseNote.jspa?version=` — from the WW project's versions, **not** the version name |
| DONE filter id | The saved JIRA filter for this release; a new release needs a new filter |
| Issue list | `project = WW AND fixVersion = <version>`, grouped by type |
| Breaking changes | Authored — see below |
| Staging Repository block | An explicit decision — see below |

## The issue list

Group under `<h2>` per issue type, in this order, omitting any type with no issues:

**Bug → New Feature → Improvement → Task → Dependency**

Within a section, order by issue key ascending. Each entry is `[WW-XXXX] - <the JIRA summary verbatim>`.

**Reconcile against what actually merged.** The JIRA query is the starting point, not the answer. Two mismatches to check:

- A ticket marked fixed whose change did not make the release branch — it must not be listed.
- Work that shipped without a ticket, or under a ticket assigned to a different fix version — the notes under-report the release.

Where a ticket's summary was written for triage rather than for users, the page may carry a clearer summary — but then it is authored text, and the link must still resolve to that ticket.

## Only released versions belong in the chain

The prior-notes link forms a chain through the series, and it **skips versions that were cut but never released**. Version Notes 7.2.1 links back to 7.1.1, not to the withdrawn 7.2.0.

When a release is superseded before it ships, its content does not disappear — the successor absorbs it. 7.2.1 carries the Breaking changes for the whole 7.2.x cycle. Check what the predecessor covered before assuming your issue list is complete.

This is the same discipline `creating-security-bulletins` applies to Affected Software, for the same reason: naming a version that never reached users misdirects everyone downstream.

## Breaking changes

Present only when the release has them — a maintenance release usually does not. This section is **authored prose, not a ticket dump**: one item per change, each stating what an application must now do differently, with its ticket(s) linked at the end.

The register is the upgrade decision, not the implementation. From 7.2.1:

> `CookieInterceptor` now applies `@StrutsParameter` authorization to cookie values and deprecates the 4-arg `populateCookieValueIntoStack(...)` in favor of a new 5-arg overload taking the action, so un-annotated setters stop receiving cookies and subclass overrides must migrate.

Name the type or setting a user must act on, say what stops working, and say what replaces it.

## Security fixes in a release

A release usually ships before its bulletin publishes and before a CVE exists. The Version Notes then list a **public, neutrally-framed** ticket for a defect whose advisory is still restricted.

- List the ticket as you would any other. It is already public; omitting it under-reports the release.
- **Do not add security framing the bulletin has not published yet** — no severity, no attack description, no S2-XXX or CVE number that has not been assigned and published.
- Once the bulletin is public, the notes may link it.

**REQUIRED BACKGROUND:** where the wording of a security-relevant entry is in question, `creating-security-bulletins` governs what may be said and when.

## The Staging Repository block

Points readers at ASF Nexus staging so they can test a release candidate before the vote closes.

**Published pages are inconsistent about it** — 7.1.1 and 7.2.1 carry it, 6.9.0 and 6.10.0 do not, which tracks the series rather than the release phase and so most likely propagated by cloning. Treat its presence as a decision to make, not a default to inherit: include it for a page published during the vote, and confirm with the release manager whether it stays once the release is announced.

## Re-read the page immediately before you write to it

Confluence has no conflict warning. Fetch the current version immediately before every write and compare the version number against the one you read; if it advanced, re-read, merge onto the newer content, and write that.

After writing, diff against the version you meant to build on. The diff should show only your intended change.

## Red Flags — STOP

- Starting from a copy of the previous release's page
- A version number or JIRA id typed rather than derived
- A link whose label and its id name different releases
- The prior-notes link pointing at a version that was cut but never released
- Publishing the issue list straight from JIRA without reconciling against the release branch
- A severity, CVE, or S2-XXX reference on the page that has not been published
- Breaking changes assembled by pasting ticket summaries
- Writing from page content read earlier in the session without re-fetching

## Common Mistakes

| Mistake | Reality |
|---|---|
| "Copying last release's page is faster" | It is how "JIRA Release Notes 6.8.0" shipped on the 6.9.0 page. Copy the template. |
| "I updated the link, it's fine" | Check the label too. Every observed defect is a half-updated link. |
| "`version=` takes the version number" | It takes JIRA's numeric version id. Look it up. |
| "The DONE filter can be reused" | A reused filter shows the previous release's issues under this release's heading. |
| "JIRA is the release contents" | JIRA is the claim. The release branch is the fact. Reconcile. |
| "The fix is public, so I can describe the vulnerability" | The ticket being public does not publish the advisory. Neutral framing until the bulletin ships. |
| "Breaking changes are the tickets typed as breaking" | They are the changes that break an application. Author them. |
| "7.x needs different handling from 6.x" | Same structure, same process. Only the data differs. |
