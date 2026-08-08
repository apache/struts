# `creating-release-vote-mail` Skill Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a `creating-release-vote-mail` skill that drafts the `[VOTE] Apache Struts X.Y.Z` mail as a plain-text rendering of the already-published Version Notes page.

**Architecture:** Two files under `.claude/skills/creating-release-vote-mail/`, matching the shape of the three sibling skills in the same directory — `SKILL.md` carries judgement (preconditions, recipients, transform rules, failure modes) and `vote-mail-template.md` carries the frozen artifact (headers, slots, ASF vote boilerplate). A one-line handoff is added to `creating-version-notes` so the two chain.

**Tech Stack:** Markdown skill files with YAML frontmatter; Gmail MCP (`create_draft`); `gh` CLI and `curl` for precondition checks.

**Spec:** [`docs/superpowers/specs/2026-08-08-release-vote-mail-skill-design.md`](../specs/2026-08-08-release-vote-mail-skill-design.md)

## Global Constraints

- Skill directory: `.claude/skills/creating-release-vote-mail/`, sibling to `creating-version-notes`, `creating-security-bulletins`, `triaging-security-reports`.
- Skill name is gerund-form kebab-case, matching every existing skill in that directory.
- Frontmatter is exactly two keys, `name` and `description`, matching the sibling skills.
- The skill **drafts, never sends**. Gmail `create_draft` only.
- Recipients are `To: dev@struts.apache.org`, `Bcc: private@struts.apache.org`. `user@struts.apache.org` must appear nowhere in either file except as a prohibition.
- Sign-off is exactly `On behalf of the Apache Struts project`.
- Staging URL is exactly `https://repository.apache.org/content/repositories/staging/`.
- The ASF vote boilerplate is reproduced **byte-identical** to the archived mails, including `Github release` without its colon.
- No tests ship with this skill, matching `creating-version-notes`.
- Commits use the `docs:` prefix with no Jira ticket — this is process documentation, not framework code.
- All work lands on branch `vote-mail-skill` (already created) and finishes as a PR. Never push to `main`.
- Never `git add -A` in this repo; stage explicit paths and check `git diff --cached --name-only`.

## File Structure

| File | Responsibility |
|---|---|
| `.claude/skills/creating-release-vote-mail/vote-mail-template.md` | The artifact: field table, mail skeleton with slots, frozen boilerplate, pre-send checklist. Changes when the mail's wording changes. |
| `.claude/skills/creating-release-vote-mail/SKILL.md` | The judgement: when to use, preconditions, recipient rule, transform rules, verification, Red Flags, Common Mistakes. Changes when a new failure mode is learned. |
| `.claude/skills/creating-version-notes/SKILL.md` | Modified: one handoff line at the end of "The test-build announcement". |

The split follows the standing decision recorded for `creating-version-notes`: mechanical reference material goes in the template file, every rule requiring judgement stays in `SKILL.md`, because a rule in an unloaded file is a rule that will not be followed.

---

### Task 1: The template file

**Files:**
- Create: `.claude/skills/creating-release-vote-mail/vote-mail-template.md`
- Reference (read only, do not modify): `.claude/skills/creating-version-notes/version-notes-template.md`, `.claude/skills/creating-security-bulletins/bulletin-template.md`

**Interfaces:**
- Produces: the slot names `<X.Y.Z>`, `<X_Y_Z>`, `<SHAPE SENTENCE>` and the section-block names `Breaking changes`, `Deprecations`, `Issue list`. Task 2's `SKILL.md` refers to these by exactly these names.

- [ ] **Step 1: Read the two sibling templates for house shape**

Read `.claude/skills/creating-version-notes/version-notes-template.md` and
`.claude/skills/creating-security-bulletins/bulletin-template.md`. Both open with a
"Companion to `SKILL.md`" preamble, then a `## Fields` table, then the skeleton. Match that
order — do not invent a new structure.

- [ ] **Step 2: Write the template file**

Create `.claude/skills/creating-release-vote-mail/vote-mail-template.md` with exactly this content:

````markdown
# Release Vote Mail Template

The canonical skeleton for the `[VOTE] Apache Struts X.Y.Z` mail that opens a release vote
on `dev@struts.apache.org`. Companion to [`SKILL.md`](SKILL.md), which covers *how* to
establish what goes in the slots; this file covers *what the mail contains*.

**This file is the source of truth.** Start every vote mail from the skeleton below, never
from a copy of the previous release's mail — see the Iron Rule in `SKILL.md`.

## Fields

| Slot | What goes in it |
|---|---|
| `<X.Y.Z>` | The release being voted on, dotted, e.g. `7.3.0`. Appears in the subject, the opening sentence, the Version Notes URL and the dist path. |
| `<X_Y_Z>` | The same version underscored, e.g. `7_3_0`, for the `STRUTS_` git tag only. |
| `<SHAPE SENTENCE>` | One sentence describing the *shape* of the issue list, never its individual tickets. See `SKILL.md`. |
| Breaking changes | Optional block. The Version Notes page's items verbatim, `- ` prefixed. Omit the block when the page has none. |
| Deprecations | Optional block. Same shape. Omit when the page has none. |
| Issue list | The page's issue-type sections in page order, rendered as plain text. Never retyped from JIRA. |

`Rejected requests` is **not** a slot. The page may carry that section; the mail never does.

## Skeleton

```
Subject: [VOTE] Apache Struts <X.Y.Z>
To:      dev@struts.apache.org
Bcc:     private@struts.apache.org

The Apache Struts <X.Y.Z> test build is available. <SHAPE SENTENCE>

Breaking changes

- <page item, verbatim, ending [WW-XXXX].>
- <page item, verbatim, ending [WW-XXXX].>

Deprecations

- <page item, verbatim, ending [WW-XXXX].>

Bug
[WW-XXXX] - <summary>
[WW-XXXX] - <summary>

New Feature
[WW-XXXX] - <summary>

Improvement
[WW-XXXX] - <summary>

Task
[WW-XXXX] - <summary>

Dependency
[WW-XXXX] - <summary>

Release notes:
* https://cwiki.apache.org/confluence/display/WW/Version+Notes+<X.Y.Z>

Github release
* https://github.com/apache/struts/releases/tag/STRUTS_<X_Y_Z>

Distribution:
* https://dist.apache.org/repos/dist/dev/struts/<X.Y.Z>/

Maven 2 staging repository:
* https://repository.apache.org/content/repositories/staging/

Once you have had a chance to review the test build, please respond
with a vote on its quality:

[ ] Leave at test build
[ ] Alpha
[ ] Beta
[ ] General Availability (GA)

Everyone who has tested the build is invited to vote. Votes by PMC
members are considered binding. A vote passes if there are at least
three binding +1s and more +1s than -1s.

The vote will remain open for at least 72 hours, longer upon request.
A vote can be amended at any time to upgrade or downgrade the quality
of the release based on future experience. If an initial vote
designates the build as "Beta", the release will be submitted for
mirroring and announced to the user list. Once released as a public
beta, subsequent quality votes on a build may be held on the user
list.

As always, the act of voting carries certain obligations. A binding
vote not only states an opinion, but means that the voter is agreeing
to help do the work.

On behalf of the Apache Struts project
Łukasz
```

## Frozen text — do not edit

Everything from `Once you have had a chance to review the test build` to the sign-off is
**byte-frozen**. It is the ASF vote call: the quality options voters tick, the binding-vote
threshold, the 72-hour minimum, and the obligation a binding vote carries. Rewording any of
it changes what the project is asking for.

Three details that look like typos and are kept deliberately:

| Detail | Why it stays |
|---|---|
| `Github release` has no trailing colon, unlike the other three link labels | Every archived Struts vote mail reads this way. The template records what ships. |
| The staging URL is `content/repositories/staging/`, while the `[TEST]` mail uses `content/groups/staging/` | Both are valid Nexus endpoints. The vote mail's form is what past votes used; the difference is accepted, not a defect to reconcile. |
| All four quality checkboxes are empty | The release manager's own `+1 (binding)` is a separate reply. A call arriving with GA pre-ticked reads as a decision announced, not a vote opened. |

## Pre-send checklist

- [ ] All four links resolve, and the GitHub release is still flagged pre-release
- [ ] Ticket ids in the mail match the page's, excluding the page's `Rejected requests`
- [ ] Boilerplate byte-identical to the frozen text above
- [ ] `To: dev@` only — `user@` absent — and `Bcc: private@` present
- [ ] Subject is exactly `[VOTE] Apache Struts X.Y.Z`
- [ ] All four checkboxes empty
- [ ] Body hard-wrapped at 72 columns, continuation lines unindented
````

- [ ] **Step 3: Verify the boilerplate is byte-identical to the archived mail**

Write the archived 6.10.0 boilerplate to a scratchpad file and diff the template's copy
against it. Both mails carry identical boilerplate, so either is a valid reference.

```bash
SCRATCH=/private/tmp/claude-501/-Users-lukaszlenart-Projects-Apache-struts/322a9b9b-d830-4ab7-a7e8-2e30a7682260/scratchpad
cat > "$SCRATCH/boilerplate-reference.txt" <<'EOF'
Once you have had a chance to review the test build, please respond
with a vote on its quality:

[ ] Leave at test build
[ ] Alpha
[ ] Beta
[ ] General Availability (GA)

Everyone who has tested the build is invited to vote. Votes by PMC
members are considered binding. A vote passes if there are at least
three binding +1s and more +1s than -1s.

The vote will remain open for at least 72 hours, longer upon request.
A vote can be amended at any time to upgrade or downgrade the quality
of the release based on future experience. If an initial vote
designates the build as "Beta", the release will be submitted for
mirroring and announced to the user list. Once released as a public
beta, subsequent quality votes on a build may be held on the user
list.

As always, the act of voting carries certain obligations. A binding
vote not only states an opinion, but means that the voter is agreeing
to help do the work.
EOF

sed -n '/^Once you have had a chance/,/^to help do the work\.$/p' \
  .claude/skills/creating-release-vote-mail/vote-mail-template.md \
  > "$SCRATCH/boilerplate-template.txt"

diff "$SCRATCH/boilerplate-reference.txt" "$SCRATCH/boilerplate-template.txt"
```

Expected: no output. Any diff means the boilerplate was retyped rather than copied — fix the
template, do not adjust the reference.

- [ ] **Step 4: Verify the constrained strings**

```bash
grep -n 'user@struts.apache.org' .claude/skills/creating-release-vote-mail/vote-mail-template.md
grep -c 'content/repositories/staging/' .claude/skills/creating-release-vote-mail/vote-mail-template.md
grep -n 'On behalf of the Apache Struts project' .claude/skills/creating-release-vote-mail/vote-mail-template.md
```

Expected: the first prints nothing (exit 1); the second prints `2` (skeleton plus the
frozen-text table row); the third prints one line.

- [ ] **Step 5: Commit**

```bash
git add .claude/skills/creating-release-vote-mail/vote-mail-template.md
git diff --cached --name-only
git commit -m "docs: add the release vote mail template

Freezes the [VOTE] Apache Struts X.Y.Z skeleton: slots, link block, and
the ASF vote boilerplate reproduced byte-identical to the archived 6.10.0
and 7.2.1 mails.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 2: The skill file

**Files:**
- Create: `.claude/skills/creating-release-vote-mail/SKILL.md`
- Reference (read only): `.claude/skills/creating-version-notes/SKILL.md`

**Interfaces:**
- Consumes: from Task 1, the slot names `<X.Y.Z>`, `<X_Y_Z>`, `<SHAPE SENTENCE>` and the block names `Breaking changes`, `Deprecations`, `Issue list`; the pre-send checklist, which `SKILL.md` points at rather than duplicating.
- Produces: the skill `name: creating-release-vote-mail`, which Task 3's handoff line names.

- [ ] **Step 1: Confirm the description does not collide with the sibling**

```bash
grep -n '^description:' .claude/skills/*/SKILL.md
```

Expected: `creating-version-notes` ends its description at "the test-build announcement mail".
The new description must begin where that ends and must not contain the words "Version Notes
page", "Migration Guide" or "GitHub release notes" as things it *produces* — those are its
inputs. Two skills claiming the same trigger is the failure this step prevents.

- [ ] **Step 2: Write `SKILL.md`**

Create `.claude/skills/creating-release-vote-mail/SKILL.md` with this content:

````markdown
---
name: creating-release-vote-mail
description: Use when opening the formal release vote for a Struts release candidate on any maintenance line (6.x, 7.x) - composing and drafting the [VOTE] Apache Struts X.Y.Z mail to dev@ once the Version Notes page, GitHub release and staged artifacts are published.
---

# Creating a Release Vote Mail

## Overview

The `[VOTE]` mail opens the formal release vote. It is four links wrapped in frozen ASF
boilerplate, around a plain-text rendering of the release's Version Notes page.

**Core principle:** the mail is a *rendering* of the page, not a second account of the
release. Everything below the opening sentence is a transform of a published cwiki section,
so the mail cannot assert something the page does not.

**This is the step after [`creating-version-notes`](../creating-version-notes/SKILL.md).**
That skill produces the page, the GitHub release and the `[TEST]` announcement; this one
consumes all three. If they do not exist yet, you are in the wrong skill.

## The Iron Rule

```
THE VERSION NOTES PAGE IS THE ONLY SOURCE FOR THE BODY.
NEVER RETYPE THE ISSUE LIST, AND NEVER CLONE THE PREVIOUS VOTE MAIL.
```

Cloning fails the same way it fails for Version Notes pages: the version number gets updated
and the surrounding text does not. The 7.2.1 and 6.10.0 mails already disagree on their
sign-off, which is what cloning drift looks like before anyone notices it.

[`vote-mail-template.md`](vote-mail-template.md) is the source of truth for the artifact —
slots, link block, frozen boilerplate, and the pre-send checklist.

## Check every precondition before drafting

The mail is four links. A vote opened on a 404 burns the 72-hour window before anyone can
test, and the mail cannot be recalled from a public archive.

| Link | Produced by | How to check |
|---|---|---|
| `Version+Notes+X.Y.Z` on cwiki | `creating-version-notes` | Fetch it — it is also the body source |
| `releases/tag/STRUTS_X_Y_Z` | `creating-version-notes` | `gh release view STRUTS_X_Y_Z --json isPrerelease,url` — must still be a prerelease |
| `dist/dev/struts/X.Y.Z/` | The release build | `curl -sI` the directory; artifacts and their `.asc`/`.sha512` present |
| Nexus staging | `mvn release` | The staging repository is open, not dropped or already released |

A GitHub release that is no longer a prerelease means someone promoted it before the vote
closed. Stop and resolve that before drafting.

## Recipients

```
To:  dev@struts.apache.org
Bcc: private@struts.apache.org
```

**`user@` must not appear.** The `[TEST]` mail one step earlier goes to `dev@` *and* `user@`,
which is correct for it — it asks people to test. This mail asks people to *vote*, and a vote
invitation on the user list solicits votes from people whose votes are not binding.

The subject is exactly `[VOTE] Apache Struts X.Y.Z`. No "test build", no RC suffix, no
release-quality word.

## The opening sentence is the only prose you author

Two sentences. The first is fixed: `The Apache Struts X.Y.Z test build is available.` The
second describes the *shape* of the issue list.

| The page has | Second sentence |
|---|---|
| No Breaking changes | `With this release the following issues were addressed:` |
| Breaking changes | `This release contains <what>. Also a lot of dependencies have been updated:` |

Fill `<what>` from what the list actually holds — `a few minor breaking changes plus some bug
fixes` for 7.2.1. **Never preview individual tickets here.** The list below it is the preview;
a ticket named in the opener is a ticket you have decided matters more than its neighbours,
which is an editorial judgement the vote mail has no business making.

## Transform rules, per section

| Page section | In the mail |
|---|---|
| Breaking changes | Verbatim, `- ` prefixed, ticket refs as bare `[WW-XXXX]` text |
| Deprecations | Verbatim, same shape |
| Rejected requests | **Omitted** |
| Bug / New Feature / Improvement / Task / Dependency | Page order, heading bare on its own line, entries `[WW-XXXX] - <summary>` |

**Copy Breaking changes verbatim.** Verbatim is what holds each item at the page's
one-sentence form. Re-authoring them from the tickets is how the 7.2.1 items grew to three
clauses each, which is longer than the rule the page itself follows.

**Deprecations are in; Rejected requests are out.** A deprecation tells a voter what to check
in their own application, so it belongs in front of the people testing. A `Won't Do` ticket
has nothing to test — it is release documentation, and the Release notes link carries it.

**Plain text carries no links.** A ticket reference is the bare string `[WW-XXXX]`, and the
only URLs in the mail are the four link lines.

**Hard-wrap at 72 columns**, continuation lines unindented. This is what keeps the issue list
legible in the ASF archives and in the quoted replies voters send back.

## A security summary the page truncated stays truncated

Where the Version Notes page stopped a ticket summary at a clause boundary because its
bulletin is unpublished, the mail stops at the same boundary.

`dev@struts.apache.org` is a **public, permanently archived** list. Re-expanding a truncated
summary there publishes what the page deliberately withheld, to a wider audience than the
page, and no correction removes it from the archive.

Nothing else about the release's security posture reaches this mail either: no severity, no
attack description, no S2-XXX or CVE that has not been published.

**REQUIRED BACKGROUND:** where the wording of a security-relevant entry is in question,
`creating-security-bulletins` governs what may be said and when.

## A ticket/prose version mismatch is not an error

A Dependency entry may name a lower version than the Breaking changes prose. 7.2.1 lists
`[WW-5536] - Bump ognl:ognl from 3.3.5 to 3.4.8` while its Breaking changes says OGNL went to
3.4.11. Both are correct: the entry reproduces the ticket summary verbatim, the prose states
what shipped, and patch bumps land after a ticket is titled.

**Do not reconcile it in the mail.** Changing either one makes the mail disagree with the
page, which is the one thing a rendering may never do.

## Verify, then draft

Run the pre-send checklist in [`vote-mail-template.md`](vote-mail-template.md). The one worth
scripting is the ticket-set comparison — sort the `WW-` ids in your draft against the page's,
excluding the page's `Rejected requests` section. Empty output, or the mail is not a rendering
and you should find out which direction the difference runs before sending.

## Draft it, do not send it

Create a Gmail draft with `To`, `Bcc`, `Subject` and body set. **Never send.**

Sending opens a binding project vote on a public list, starts the 72-hour clock, and commits
the PMC to the artifacts as staged. That is the release manager's keystroke, not yours.

## Red Flags — STOP

- Starting from a copy of the previous release's vote mail
- Retyping the issue list from JIRA instead of rendering the page
- Re-expanding a summary the page truncated
- Adding `user@struts.apache.org` to the recipients
- Pre-ticking a quality level in the checkbox block
- Drafting before the page, tag, dist path or staging repo exist
- A severity, CVE or S2-XXX reference anywhere in the mail
- Editing the boilerplate wording
- "Correcting" a Dependency entry so it matches the Breaking changes prose
- Naming individual tickets in the opening sentence
- Sending, rather than drafting

## Common Mistakes

| Mistake | Reality |
|---|---|
| "Last release's vote mail is the fastest start" | It is how the sign-off drifted between 6.10.0 and 7.2.1. Start from the template. |
| "The `[TEST]` mail went to `user@`, so this should too" | That mail asks people to test. This one asks people to vote, and user-list votes are not binding. `dev@` only. |
| "The ticket says 3.4.8 but we shipped 3.4.11" | Both are right. The entry is the ticket summary verbatim; the prose is what shipped. Leave it. |
| "The ticket is public, so I can describe the vulnerability" | A public ticket does not publish the advisory, and `dev@` is archived forever. Neutral framing until the bulletin ships. |
| "The page is up, so I can draft" | Check the tag, the dist path and the staging repo too. A vote on a 404 wastes 72 hours. |
| "I'll tick GA since that's what we want" | The release manager's `+1` is a separate reply. A pre-ticked call announces a decision instead of opening a vote. |
| "Rejected requests are part of the release, so list them" | Voters test artifacts. A `Won't Do` ticket has nothing to test; the page link carries it. |
| "It only needs the ticket numbers, I'll type them out" | Typing is the failure mode. Render the page. |
````

- [ ] **Step 3: Verify frontmatter and cross-references**

```bash
head -4 .claude/skills/creating-release-vote-mail/SKILL.md
grep -c 'vote-mail-template.md' .claude/skills/creating-release-vote-mail/SKILL.md
grep -n 'user@struts.apache.org' .claude/skills/creating-release-vote-mail/SKILL.md
```

Expected: frontmatter is `---`, `name:`, `description:`, `---` and nothing else; the template
is referenced at least 3 times; `user@` appears only in the prohibition and the two mistake
rows that explain it — read each hit and confirm none of them is a recipient instruction.

- [ ] **Step 4: Commit**

```bash
git add .claude/skills/creating-release-vote-mail/SKILL.md
git diff --cached --name-only
git commit -m "docs: add creating-release-vote-mail skill

Drafts the [VOTE] Apache Struts X.Y.Z mail as a rendering of the
published Version Notes page. Records the dev@-only recipient rule, the
truncated-security-summary carry-through, and the draft-never-send
boundary.

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 3: Chain it to `creating-version-notes`

**Files:**
- Modify: `.claude/skills/creating-version-notes/SKILL.md` — end of the `## The test-build announcement` section, immediately before `## Re-read the page immediately before you write to it`

**Interfaces:**
- Consumes: the skill name `creating-release-vote-mail` from Task 2.

- [ ] **Step 1: Locate the insertion point**

```bash
grep -n '^## ' .claude/skills/creating-version-notes/SKILL.md
```

The handoff goes at the **end** of `## The test-build announcement`, after the paragraph
beginning `Keep the security posture of the pages`.

- [ ] **Step 2: Add the handoff line**

Append this paragraph to that section:

```markdown
**The vote is the next step, and it is a different mail.** Once the `[TEST]` build has been
announced, `creating-release-vote-mail` composes the `[VOTE] Apache Struts X.Y.Z` call — to
`dev@` alone, rendered from the page this skill produced. Do not draft it from here: its
recipients, subject and body differ from the announcement above.
```

- [ ] **Step 3: Verify nothing else moved**

```bash
git diff --stat .claude/skills/creating-version-notes/SKILL.md
git diff .claude/skills/creating-version-notes/SKILL.md
```

Expected: `1 file changed, N insertions(+)` with **no deletions**. Any deletion means a
neighbouring section was disturbed — this skill's own rules warn about swallowing an adjacent
heading, and the same care applies to editing it.

- [ ] **Step 4: Commit**

```bash
git add .claude/skills/creating-version-notes/SKILL.md
git diff --cached --name-only
git commit -m "docs: point creating-version-notes at the vote mail step

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 4: Dry run against the archived 6.10.0 vote

This is the closest thing the skill has to a test: render a mail the project already sent, and
compare. 6.10.0 is the better subject than 7.2.1 — its issue list is three tickets, so a diff
is readable, and it has no Breaking changes block, which exercises the omission path.

**Files:**
- No repository files change. Working files go in the scratchpad.

**Interfaces:**
- Consumes: `SKILL.md` and `vote-mail-template.md` from Tasks 1 and 2.

- [ ] **Step 1: Capture the archived mail as the expected output**

Fetch the sent `[VOTE] Apache Struts 6.10.0` mail (Gmail thread `19e5fc8e0a444f92`, message
`19e5fcd6bf35b65b`) and save its plaintext body to `$SCRATCH/expected-6.10.0.txt`.

- [ ] **Step 2: Render a mail from the published page, following the skill**

Follow `SKILL.md` end to end for version `6.10.0`, sourcing the body from
`https://cwiki.apache.org/confluence/display/WW/Version+Notes+6.10.0`. Write the result to
`$SCRATCH/rendered-6.10.0.txt` instead of creating a draft.

Skip the precondition checks that cannot pass for a shipped release — the 6.10.0 GitHub
release is no longer a prerelease and its staging repo is long closed. Note which checks you
skipped; that list is itself a finding if it is longer than those two.

- [ ] **Step 3: Diff and classify every difference**

```bash
diff -u "$SCRATCH/expected-6.10.0.txt" "$SCRATCH/rendered-6.10.0.txt"
```

Expected: differences only in the sign-off (`Kind regards` → `On behalf of the Apache Struts
project`, a deliberate normalisation from the spec) and in wrapping of the authored opener.

Classify each remaining difference into exactly one bucket:

| Bucket | Action |
|---|---|
| Deliberate normalisation named in the spec | None — expected |
| The skill produced something wrong | Fix `SKILL.md` or the template, re-run from Step 2 |
| The archived mail was wrong | Add a Common Mistakes row so the skill prevents it next time |

- [ ] **Step 4: Verify the ticket-set check actually works**

```bash
grep -o 'WW-[0-9]*' "$SCRATCH/rendered-6.10.0.txt" | sort -u
```

Expected: exactly `WW-5623`, `WW-5628`, `WW-5629` — matching the page's three tickets. If the
rendering dropped or invented one, the transform rules in Task 2 need fixing.

- [ ] **Step 5: Commit any corrections**

If Steps 3 or 4 required changes:

```bash
git add .claude/skills/creating-release-vote-mail/
git diff --cached --name-only
git commit -m "docs: correct the vote mail skill from the 6.10.0 dry run

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

If nothing changed, record the dry-run outcome in the PR description instead and move on.

---

### Task 5: Open the PR

**Files:**
- No file changes.

- [ ] **Step 1: Review the whole branch before pushing**

```bash
git log --oneline main..vote-mail-skill
git diff main...vote-mail-skill
```

Read the full diff. Every change on this branch gets the same review gate, including the
corrections Task 4 may have added.

- [ ] **Step 2: Confirm this is not a security patch**

This branch adds process documentation and touches no framework code. Confirm with:

```bash
git diff --name-only main...vote-mail-skill
```

Expected: only paths under `.claude/skills/` and `docs/superpowers/`. Anything under `core/`
or `plugins/` means something unintended was staged — stop and investigate before pushing.

- [ ] **Step 3: Push and open the PR**

```bash
git push -u origin vote-mail-skill
gh pr create --title "docs: add creating-release-vote-mail skill" --body "$(cat <<'EOF'
Adds a `creating-release-vote-mail` skill that drafts the `[VOTE] Apache Struts X.Y.Z` mail
as a plain-text rendering of the already-published Version Notes page.

It is the step after `creating-version-notes`: that skill ends at the `[TEST]` announcement,
this one opens the vote.

- `vote-mail-template.md` freezes the skeleton and the ASF vote boilerplate, reproduced
  byte-identical to the archived 6.10.0 and 7.2.1 mails.
- `SKILL.md` records the judgement: `dev@`-only recipients, Deprecations in and Rejected
  requests out, truncated security summaries carrying through unchanged, and drafting rather
  than sending.
- Validated by re-rendering the archived 6.10.0 vote mail from its published page.

Design: `docs/superpowers/specs/2026-08-08-release-vote-mail-skill-design.md`

No Jira ticket — process documentation, consistent with the `creating-version-notes` and
`creating-security-bulletins` skill commits.

🤖 Generated with [Claude Code](https://claude.com/claude-code)
EOF
)"
```

---

## Self-Review

**Spec coverage.** Every spec section maps to a task: identity and frontmatter → Task 2 Step 2;
preconditions table → Task 2; recipients → Tasks 1 and 2, verified in Task 1 Step 4 and Task 2
Step 3; opening sentence → Task 2; transform rules including Deprecations-in/Rejected-out →
Task 2; security truncation → Task 2; template with frozen sign-off and staging URL → Task 1;
empty checkboxes → Task 1; verification checklist → Task 1 Step 2, exercised in Task 4;
Gmail-draft-never-send → Task 2; Red Flags and Common Mistakes → Task 2; "no tests" → Global
Constraints, with Task 4 standing in as a dry run. The spec's position-in-flow diagram is what
Task 3 implements.

**Placeholders.** None. Every file's full content is given inline; every command is runnable;
every "Expected:" states a concrete result.

**Name consistency.** `creating-release-vote-mail` (skill name, directory, frontmatter, Task 3
handoff, PR title), `vote-mail-template.md` (Task 1 creates it, Task 2 links it three times),
and the slot names `<X.Y.Z>` / `<X_Y_Z>` / `<SHAPE SENTENCE>` are used identically in Tasks 1
and 2. Branch `vote-mail-skill` is the same in Global Constraints and Task 5.
