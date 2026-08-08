# `creating-release-vote-mail` skill — design

**Date:** 2026-08-08
**Status:** approved, ready for implementation

## Problem

Opening a Struts release vote means sending `[VOTE] Apache Struts X.Y.Z` to `dev@`.
The mail is mostly frozen ASF boilerplate wrapped around a plain-text rendering of the
release's Version Notes page, but it is currently written by hand each time. Hand-writing
it produces drift — the 7.2.1 and 6.10.0 mails disagree on their sign-off — and it re-states,
in a public archived list, content whose canonical form already exists on the cwiki.

## Scope

**In scope:** composing and drafting the vote-opening mail.

**Out of scope:** tallying the vote, the `Re: [CLOSED] [VOTE] …` reply, and the `[ANN]`
announcement. Those are later stages and may become their own skill if wanted.

## Position in the release flow

The skill is a sibling of `creating-version-notes` and runs immediately after it:

```
Version Notes page ─┐
GitHub release      ├─ creating-version-notes ─→ [TEST] mail ─→ creating-release-vote-mail ─→ [VOTE] mail
staged artifacts   ─┘
```

`creating-version-notes` ends at the `[TEST]` announcement; this skill begins there. Their
frontmatter descriptions therefore do not compete for the same trigger.

## Skill identity

**Name:** `creating-release-vote-mail`
**Location:** `.claude/skills/creating-release-vote-mail/`
**Files:** `SKILL.md` + `vote-mail-template.md`

**Description:**

> Use when opening the formal release vote for a Struts release candidate on any maintenance
> line (6.x, 7.x) — composing and drafting the `[VOTE] Apache Struts X.Y.Z` mail to `dev@`
> once the Version Notes page, GitHub release and staged artifacts are published.

## Core principle

**The mail is a rendering of the Version Notes page, not a second account of the release.**

Everything below the opening sentence is a plain-text transform of a published cwiki section.
Nothing is re-authored, so the mail cannot assert something the page does not.

### The Iron Rule

```
THE VERSION NOTES PAGE IS THE ONLY SOURCE FOR THE BODY.
NEVER RETYPE THE ISSUE LIST, AND NEVER CLONE THE PREVIOUS VOTE MAIL.
```

Cloning is the same failure `creating-version-notes` bans for the same reason: the number
gets updated and the surrounding text does not.

## Preconditions

The mail is four links wrapped in boilerplate. All four must resolve *before* drafting —
a vote opened on a 404 burns the 72-hour window before anyone can test.

| Link | Produced by | Check |
|---|---|---|
| `Version+Notes+X.Y.Z` on cwiki | `creating-version-notes` | fetch it — it is also the body source |
| `releases/tag/STRUTS_X_Y_Z` | `creating-version-notes` | `gh release view`, must still be `--prerelease` |
| `dist/dev/struts/X.Y.Z/` | release build | HTTP check; artifacts and signatures present |
| Nexus `content/repositories/staging/` | `mvn release` | staging repo open, not dropped |

## Recipients

```
To:  dev@struts.apache.org
Bcc: private@struts.apache.org
```

**`user@` must not appear.** The `[TEST]` mail one step earlier goes to both `dev@` and
`user@`; the `[VOTE]` mail goes to `dev@` alone, because a vote invitation on the user list
solicits votes from people whose votes are not binding. Both sampled mails got this right;
the skill records *why* so it stays right.

Subject is exactly `[VOTE] Apache Struts X.Y.Z` — no "test build", no RC suffix.

## The opening sentence — the only authored prose

Two sentences: the fixed `The Apache Struts X.Y.Z test build is available.` plus one
describing the *shape* of the issue list, never its individual contents.

| Page has | Second sentence |
|---|---|
| no Breaking changes | `With this release the following issues were addressed:` (as in 6.10.0) |
| Breaking changes | `This release contains <what>. Also a lot of dependencies have been updated:` (as in 7.2.1) |

## Transform rules, per section

- **Breaking changes** — the page's items verbatim, `- ` prefixed, ticket references as bare
  `[WW-XXXX]` text (plain-text mail carries no links). Verbatim copying is what holds them at
  the page's one-sentence form; re-authoring is how the 7.2.1 items grew to three clauses.
- **Deprecations** — included when the page has them. A deprecation tells a voter what to
  check in their own application, so it belongs in front of the people testing.
- **Rejected requests** — **not** included. A `Won't Do` ticket has nothing to test; it is
  release documentation, and the Release notes link carries it.
- **Issue-type sections** — page order (Bug → New Feature → Improvement → Task → Dependency),
  heading bare on its own line, entries `[WW-XXXX] - <summary>`, blank line between sections.
  Omit any type the page omits.
- **Security-truncated summaries carry through exactly as truncated.** Where the page stopped
  a summary at a clause boundary because its bulletin is unpublished, the mail stops there too.
  Re-expanding it publishes to `dev@` — a public archived list — what the page deliberately
  withheld. Cross-references `creating-security-bulletins`.
- **Hard-wrap at 72 columns**, continuation lines unindented, matching both sampled mails.
  This keeps the list legible in the ASF archives and in quoted replies.

### A ticket/page mismatch is not automatically an error

A Dependency entry may name a lower version than the Breaking changes prose: 7.2.1 lists
`[WW-5536] - Bump ognl:ognl from 3.3.5 to 3.4.8` while its Breaking changes says OGNL went to
3.4.11. Both are correct — the entry reproduces the ticket summary verbatim, the prose states
what shipped. `creating-version-notes` mandates exactly this. Do not "fix" it in the mail.

## The template

`vote-mail-template.md` holds the headers, the slots, and the frozen tail:

```
Subject: [VOTE] Apache Struts <X.Y.Z>
To:      dev@struts.apache.org
Bcc:     private@struts.apache.org

The Apache Struts <X.Y.Z> test build is available. <SHAPE SENTENCE>

<Breaking changes block — omit when the page has none>
<Deprecations block — omit when the page has none>
<Issue list, page order>

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

Two deliberate choices frozen here:

- **Sign-off** is `On behalf of the Apache Struts project` (7.2.1's form, not 6.10.0's
  `Kind regards`) — it reads as the PMC opening a formal vote rather than a personal note.
- **Staging URL** is `content/repositories/staging/`, as both sampled vote mails used. It
  differs from the `[TEST]` mail's `content/groups/staging/`; that difference is accepted,
  not a defect to reconcile.

`Github release` keeps its missing colon and the boilerplate keeps its exact wording. The
template records what ships; it does not improve it.

### All four checkboxes ship empty

The release manager's own vote is a separate reply (`+1 (binding)`), as both sampled threads
show. A call that arrives with a quality level already ticked reads as a decision announced
rather than a vote opened.

## Verification before creating the draft

1. All four links resolve; the GitHub release is still flagged pre-release.
2. Ticket sets match — `diff` the mail's `WW-` ids against the page's, excluding the page's
   `Rejected requests` section, which the mail deliberately omits. Empty output, or the mail
   is not a rendering.
3. Boilerplate byte-identical to the template.
4. `To`/`Bcc` correct, `user@` absent, subject exactly `[VOTE] Apache Struts X.Y.Z`.
5. All four checkboxes empty.

## Output

The skill composes the mail and calls Gmail `create_draft` with To, Bcc, Subject and body set.

**The skill drafts; it never sends.** Sending opens a binding project vote, which stays the
release manager's keystroke.

## Failure modes recorded in the skill

`SKILL.md` closes with the two tables the sibling skills use.

**Red Flags — STOP:**

- Cloning the previous release's vote mail
- Retyping the issue list instead of rendering the page
- Re-expanding a summary the page truncated
- Adding `user@struts.apache.org` to the recipients
- Pre-ticking a quality level
- Drafting before the page, tag or dist path exist
- Putting a severity, CVE or S2-XXX reference in the mail
- Editing the boilerplate wording
- "Correcting" a Dependency entry to match the Breaking changes prose

**Common Mistakes** pairs each with its reality, e.g.:

| Mistake | Reality |
|---|---|
| "Last release's vote mail is the fastest start" | It is how the sign-off drifted between 6.10.0 and 7.2.1. Start from the template. |
| "The [TEST] mail went to user@, so this should too" | A vote invitation on the user list solicits non-binding votes. `dev@` only. |
| "The ticket says 3.4.8 but we shipped 3.4.11" | Both are right. The entry is the ticket summary verbatim; the prose is what shipped. |
| "The fix is public, so I can describe it" | A public ticket does not publish the advisory. `dev@` is archived. Neutral framing until the bulletin ships. |
| "The page is up, so I can draft" | Check the tag, the dist path and the staging repo too. A vote on a 404 wastes 72 hours. |

## Tests

Ships without tests, matching `creating-version-notes`. A failing baseline under
`writing-skills` requires subagents, which are not spawned unprompted. Tests can be added on
request.
