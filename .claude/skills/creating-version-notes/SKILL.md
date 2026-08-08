---
name: creating-version-notes
description: Use when preparing, updating, or reviewing the release documentation for a Struts release or release candidate on any maintenance line (6.x, 7.x) - the Version Notes page on the cwiki, its Migration Guide entry, the GitHub release notes, and the test-build announcement mail.
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
| Parent page | Always **Migration Guide** (page id `13981`) — every Version Notes page is a child of it |
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

**Reconcile against what actually merged.** The JIRA query is the starting point, not the answer. Three mismatches to check:

- A ticket resolved `Fixed` whose change did not make the release branch — it must not be listed as delivered.
- A ticket resolved **`Won't Do`** or otherwise not `Fixed` — it belongs under `Rejected requests`, not in a type section and not dropped. Check the resolution, not just the status: both `Closed` and `Resolved` sit in the Done category.
- Work that shipped under a ticket assigned to a different fix version — the notes under-report the release.

A ticket with no commit in the range is not automatically wrong. Check its **component** first: `IDEA Plugin`, `Example Applications` and similar live in other repositories and are still legitimately part of the release.

**Reconcile through the ticket's linked PR, reading the files it changed.** Do not grep commit subjects, and do not go looking for the class named in the ticket title: a title often names the *symptom* while the fix lives elsewhere. WW-5630 reads "Performance Issue SecurityMemberAccess" and was fixed in `ConfigParseUtil`; searching for the former concludes, wrongly, that the backport is missing. Squash-merges also rewrite hashes, so the merge commit id from the PR need not appear on the branch.

**Untick eted patch-level dependency bumps are not a gap.** Dependabot PRs for patch updates are merged directly and deliberately get no ticket, so they get no entry — there is nothing to link. Expect the pom to show a higher patch version than the ticket text says: 6.11.0 shipped jackson 2.22.1 while WW-5648 reads "2.21.4 to 2.22.0". That is correct, not an omission. Minor and major bumps do get a ticket and do get listed.

Where a ticket's summary was written for triage rather than for users, the page may carry a clearer summary — but then it is authored text, and the link must still resolve to that ticket.

## Only released versions belong in the chain

The prior-notes link forms a chain through the series, and it **skips versions that were cut but never released**. Version Notes 7.2.1 links back to 7.1.1, not to the withdrawn 7.2.0.

When a release is superseded before it ships, its content does not disappear — the successor absorbs it. 7.2.1 carries the Breaking changes for the whole 7.2.x cycle. Check what the predecessor covered before assuming your issue list is complete.

This is the same discipline `creating-security-bulletins` applies to Affected Software, for the same reason: naming a version that never reached users misdirects everyone downstream.

## Page section order

Beyond the boilerplate, sections appear in this order, each omitted when empty:

**Breaking changes → Deprecations → Rejected requests → Bug → New Feature → Improvement → Task → Dependency → Issue Detail → Issue List → Other resources**

The first three are authored; the issue-type sections are derived from JIRA.

## Breaking changes

Present only when the release has them — a maintenance release usually does not.

Each item is **one sentence plus its ticket link**:

```
<what changed, in terms of what an application sees> [WW-XXXX].
```

> Annotated wildcard actions are matched most-specific-first, so action selection can differ [WW-3784].

> `JSONInterceptor` uses a fresh reader and writer per request, so custom ones must not hold state between requests [WW-5650].

The sentence exists so a reader can judge **whether to open the ticket**, not so they can avoid opening it. The ticket carries the detail — API signatures, migration steps, the config that changes. Naming the affected type or setting is enough; enumerating what replaces it is the ticket's job.

Derive each item from the fix diff rather than the ticket title, and write only what you confirmed. A change you suspect is breaking but could not pin down is one to raise with the release manager, not to describe vaguely.

## Deprecations

Where a release deprecates public API, list it separately from Breaking changes — nothing stops working yet, so mixing the two overstates the upgrade cost. Same one-line shape, naming the replacement where there is one:

> `ConversionRule.COLLECTION` and the `Collection_` key prefix are deprecated; use `ConversionRule.ELEMENT` and `Element_` instead [WW-5656].

## Rejected requests

A ticket resolved **`Won't Do`** (or otherwise not `Fixed`) against this fix version is still news: someone asked for it and the project decided against it.

- **Do not put it in a type section.** Under Improvement or New Feature it reads as delivered.
- **Do not silently drop it either.** The decision is the value.
- List it under `Rejected requests`, saying it will not be implemented and, where the release manager gave one, the reason.

> [WW-2635] - Flash scope - will not be implemented; the proposed mechanism could introduce a security risk.

Note the JIRA-generated release notes linked from the page *will* still include these tickets under their type. Clearing the fix version in JIRA is the only way to change that, and is the release manager's call.

## Security fixes in a release

A release usually ships before its bulletin publishes and before a CVE exists. The Version Notes then list a **public, neutrally-framed** ticket for a defect whose advisory is still restricted.

- List the ticket as you would any other. It is already public; omitting it under-reports the release.
- **Do not add security framing the bulletin has not published yet** — no severity, no attack description, no S2-XXX or CVE number that has not been assigned and published.
- Once the bulletin is public, the notes may link it.

**Where the ticket's own summary describes the defect, list the neutral part of it.** "List the summary verbatim" assumes a neutrally-worded ticket, and security tickets often are not. WW-5643 reads *"StrutsJSONReader parse state shared across concurrent requests — maxDepth bypass and cross-request data leak"*; the page carried it up to "concurrent requests" and stopped. The trailing clause is the bulletin's job.

Truncate at the clause boundary — never paraphrase into something the ticket does not say, and never alter the ticket link. Then **tell the release manager which summaries you cut and why**: whether an already-public JIRA summary should be reproduced in full is their call, not yours, and it has to be made before the page goes up rather than edited afterwards.

**REQUIRED BACKGROUND:** where the wording of a security-relevant entry is in question, `creating-security-bulletins` governs what may be said and when.

## The Staging Repository block

**Include it.** The block points readers at ASF Nexus staging so they can test the artifacts before the vote closes, and it stays on the page afterwards.

Older 6.x pages (6.9.0, 6.10.0) lack it while the 7.x pages carry it. That is an artefact of cloning within each series, not a difference between the lines — 6.11.0 carries it.

## Link the new page from the Migration Guide

The page is not finished when it is created. **[Migration Guide](https://cwiki.apache.org/confluence/spaces/WW/pages/13981/Migration+Guide) (id `13981`) is both the parent page and the index**, and a Version Notes page that is not listed there is unreachable by anyone browsing.

Add an entry at the **top** of the list under the `<h2>` for the matching line — `Version Notes 7.x`, `Version Notes 6.x`, and so on. The lists are newest-first, and the entry is a page link carrying no body text:

```xml
<li><ac:link><ri:page ri:content-title="Version Notes X.Y.Z"/></ac:link></li>
```

**Update the section, not the whole page.** `confluence_update_page_section` on the exact heading replaces only that section's body; its boundary is the next `<h2>`, so the section body includes the `<h3>` migration-guide link that follows the list. Supply that `<h3>` and its paragraph in the replacement content or they are dropped.

**Verify against raw storage, not the diff.** A version diff of this page renders empty even for a real change, because the markdown view discards `ac:link` bodies. Fetch the new version with `convert_to_markdown=false` and confirm the new entry is present, the prior entries survive in order, and the trailing `<h3>` appears exactly once.

This applies to **every** section update, including ones on the Version Notes page itself — shortening `Breaking changes` carries the same risk of swallowing the `Deprecations` heading that follows it. After any section write, confirm the sections below it are still present exactly once. Where the page has no `ac:link` in it, the cheaper markdown fetch is enough to see the headings.

## Writing pages through the API

`content_file` is rejected for any path outside the repository — a scratchpad path fails as path traversal. Draft wherever you like, but **pass the body as inline `content`** when creating or updating a page.

The response carries the new version number. On a page you have just written, that number is its own check: a create followed by one update should report version 2, so anything higher means someone else wrote in between.

## The GitHub release notes

A release also has a GitHub release at the `STRUTS_X_Y_Z` tag, kept as a **pre-release** while the vote runs. GitHub's generated body is a starting point that needs two corrections before it is fit to publish.

### Name the previous tag yourself

**Never let GitHub choose the range.** It picks the previous tag by reachability, and Struts release branches get renamed and re-imported, so older tags are frequently *not* ancestors of the new one and the heuristic reaches too far back. For 6.11.0 it chose `STRUTS_6_8_0` and produced ~101 entries, 88 of which had already shipped in 6.9.0 and 6.10.0.

Generate the body with the previous release named explicitly, and it comes out right the first time:

```bash
gh api -X POST repos/apache/struts/releases/generate-notes \
  -f tag_name=STRUTS_7_3_0 -f previous_tag_name=STRUTS_7_2_1 -q .body > generated.md
```

Confirm the entry count is plausible against the real change set, which `git log` gives even across unrelated histories:

```bash
git log --format='%h %s' STRUTS_7_2_1..STRUTS_7_3_0
```

**If you inherit a body GitHub generated on its own**, check the `**Full Changelog**: .../compare/<PREVIOUS>...<THIS>` line first, and regenerate as above rather than pruning by hand. When pruning is unavoidable, drop `## New Contributors` too if the contribution it cites falls outside the range — but keep it when the contributors are genuinely new in this range.

### Split the entries

Two sections, `### Dependencies` nested under `## What's Changed`, before any `## New Contributors`:

| Entry | Section |
|---|---|
| Carries a `WW-XXXX` ticket — whoever authored it | `## What's Changed` |
| A human PR that is not a dependency change (ci, chore, release prep) | `## What's Changed` |
| A dependency bump with **no** ticket | `### Dependencies` |

**The discriminator is the ticket, not the author.** A Dependabot PR carrying a ticket stays in What's Changed, because a ticketed bump is release content and appears in the Version Notes Dependency section. A human PR that is purely a dependency change (`Removes unused jaxb-core dependency`) belongs under Dependencies. Both cases occur in the 6.9.0 release.

A PR that mixes a dependency change with something else — CVE-driven library updates *plus* a CI tweak — stays in What's Changed. Dependencies is for entries that are nothing but a bump.

Preserve the generated relative order within each section, and keep the entry lines byte-identical — they carry the author and PR links GitHub rendered. Split with a script rather than by retyping, then **prove nothing was lost**:

```bash
diff <(grep '^\* ' generated.md | sort) <(grep '^\* ' new.md | sort)
```

Empty output means the entry set is unchanged and only the grouping moved.

### Applying it

The release may or may not exist yet — check before assuming which command you need.

```bash
# it exists (release cut earlier, or notes already generated):
gh release view STRUTS_X_Y_Z --json body -q .body > original.md   # keep, so it can be restored
gh release edit STRUTS_X_Y_Z --prerelease --notes-file new.md

# it does not exist yet:
gh release create STRUTS_X_Y_Z --title "Struts X.Y.Z" --prerelease --verify-tag --notes-file new.md
```

Pass `--prerelease` either way, so a release still under vote is not silently promoted, and `--verify-tag` on create so a typo in the tag fails instead of creating one.

## The test-build announcement

Once the Version Notes page and the GitHub release are both up, the release manager announces the test build so people can exercise the staged artifacts during the vote. **Draft it last** — every link in it points at something the earlier steps produced.

Subject is `[TEST] Apache Struts X.Y.Z test build is ready`. Send it to **both** lists, Bcc the private one:

```
To:  dev@struts.apache.org, user@struts.apache.org
Bcc: private@struts.apache.org
```

Both audiences want it — committers to check the staged artifacts, users to test against their own applications — and a build announced to only one of them reaches half the people who could find a problem during the vote.

The body is fixed apart from four substitutions:

```
Hello,

This is a minor release of Struts <LINE> which contains <WHAT>, and it
shouldn't break your code<RISK>. Please take your time and test the bits
- any help is appreciated. Please report any problems you will spot.

Here are the changes from the previous version:
https://github.com/apache/struts/releases/tag/STRUTS_X_Y_Z

Staging Maven repo
https://repository.apache.org/content/groups/staging/

Standalone artifacts
https://dist.apache.org/repos/dist/dev/struts/X.Y.Z/

Release notes
https://cwiki.apache.org/confluence/display/WW/Version+Notes+X.Y.Z

Kind regards
--
Łukasz
```

| Slot | How to fill it |
|---|---|
| `<LINE>` | `6.x` or `7.x` |
| `<WHAT>` | What the issue list actually contains — `mostly bug fixes` for 6.11.0, `a few improvements and bug fixes` for 7.3.0 |
| `<RISK>` | Empty when the release has no Breaking changes; ` but it contains significant changes` when it does. 6.11.0 had none and said nothing; 7.3.0 had seven and said so |
| Tag / paths | Tag underscored (`STRUTS_7_3_0`), dist path and page title dotted (`7.3.0`) |

Do not take the recipients from a previous announcement: 6.11.0 went to `dev@` alone and 7.3.0 to `user@` alone, and both were mistakes. Address every announcement to the two lists above.

Keep the security posture of the pages: the mail links the release notes, it does not summarise what is in them, so no severity, CVE or S2-XXX reaches it either.

**The vote is the next step, and it is a different mail.** Once the test build is announced, `creating-release-vote-mail` composes the `[VOTE] Apache Struts X.Y.Z` call — to `dev@` alone, rendered from the page this skill produced. Do not draft it from here: its recipients, subject and body all differ from the announcement above.

## Re-read the page immediately before you write to it

Confluence has no conflict warning. Fetch the current version immediately before every write and compare the version number against the one you read; if it advanced, re-read, merge onto the newer content, and write that.

After writing, diff against the version you meant to build on. The diff should show only your intended change.

## Red Flags — STOP

- Starting from a copy of the previous release's page
- A version number or JIRA id typed rather than derived
- A link whose label and its id name different releases
- The prior-notes link pointing at a version that was cut but never released
- Publishing the issue list straight from JIRA without reconciling against the release branch
- Concluding a backport is missing from a commit-subject grep, or from the class named in the ticket title
- Treating an untick eted patch dependency bump as a reconciliation gap
- Dropping a `Won't Do` ticket, or listing it under Improvement or New Feature as though it shipped
- A Breaking changes item that runs past one sentence, or restates what the ticket already explains
- Reproducing a security ticket's summary in full when it names the bypass or the leak
- Letting GitHub pick the previous tag instead of passing `previous_tag_name`
- Regrouping release entries by retyping them instead of scripting the split and diffing the result
- A severity, CVE, or S2-XXX reference on the page that has not been published
- Breaking changes assembled by pasting ticket summaries
- Creating the page without adding it to the Migration Guide index
- Trusting an empty version diff on the Migration Guide as proof the edit landed
- Publishing GitHub release notes without checking which tag the Full Changelog compares against
- Splitting the GitHub sections by author instead of by whether the entry carries a ticket
- Editing a GitHub release under vote without `--prerelease`
- Writing from page content read earlier in the session without re-fetching

## Common Mistakes

| Mistake | Reality |
|---|---|
| "Copying last release's page is faster" | It is how "JIRA Release Notes 6.8.0" shipped on the 6.9.0 page. Copy the template. |
| "I updated the link, it's fine" | Check the label too. Every observed defect is a half-updated link. |
| "`version=` takes the version number" | It takes JIRA's numeric version id. Look it up. |
| "The DONE filter can be reused" | A reused filter shows the previous release's issues under this release's heading. |
| "JIRA is the release contents" | JIRA is the claim. The release branch is the fact. Reconcile. |
| "No commit mentions the ticket, so it wasn't backported" | Read the linked PR's changed files. Titles name symptoms, and squash-merges rewrite hashes. |
| "The pom version doesn't match the ticket, that's a gap" | Patch bumps ship untick eted by design. Only ticketed bumps get an entry. |
| "The page is created, so the work is done" | It is invisible until listed on the Migration Guide. |
| "The version diff is empty, so nothing changed" | The diff renders markdown, which drops `ac:link` bodies. Check raw storage. |
| "GitHub generated the changelog, so the range is right" | It guesses the previous tag by reachability. Renamed branches make it reach too far back. Verify with `git log PREV..THIS`. |
| "Dependabot authored it, so it goes under Dependencies" | Ticketed bumps stay in What's Changed. The ticket decides, not the author. |
| "A Won't Do ticket isn't part of the release" | The decision is news. It goes under Rejected requests, not into a type section and not into the bin. |
| "More detail in Breaking changes is safer" | One sentence plus the ticket link. The reader opens the ticket for detail; the page exists to tell them whether to. |
| "The summary is public in JIRA, so I can repeat it" | Not when it names the bypass or the leak and the bulletin is unpublished. Truncate, and say you did. |
| "GitHub will work out the previous tag" | Pass `previous_tag_name` and it is right the first time. |
| "The fix is public, so I can describe the vulnerability" | The ticket being public does not publish the advisory. Neutral framing until the bulletin ships. |
| "Breaking changes are the tickets typed as breaking" | They are the changes that break an application. Author them. |
| "7.x needs different handling from 6.x" | Same structure, same process. Only the data differs. |
