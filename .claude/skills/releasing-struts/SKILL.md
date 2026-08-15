---
name: releasing-struts
description: Use when running or planning an Apache Struts release on any maintenance line (6.x, 7.x) - cutting the tag, staging artifacts, opening the vote, promoting, updating the site and announcing - or when asked what the next step in a release is.
---

# Releasing Struts

## Overview

A release is seven phases with a gate between each.

**The process itself is published**, at
[Release Guidelines](https://struts.apache.org/release-guidelines.html) — every phase, every
command, the release policy and the one-time setup a new release manager needs. It is the source
of truth, it is maintained in `apache/struts-site` (`source/release-guidelines.md`), and it is
what you follow.

This skill is the agent's half of it: the judgement about *ordering* and *when to stop*, which
sibling skill owns which document, and the points where a step is the release manager's to take
rather than yours. [`release-runbook.md`](release-runbook.md) holds that last part.

**Core principle:** a phase is finished when its gate is verifiable by someone other than you.
"I ran the command" is not a gate; "the URL resolves" is.

**Corrections go to the site page.** If a release teaches you something the guidelines get wrong,
fix them in a PR to `apache/struts-site`. Only what is genuinely agent-specific belongs here.

## The phases

| # | Phase | Gate before moving on |
|---|---|---|
| 1 | Prepare | Branch green, version decided, parent poms released |
| 2 | Cut | Tag pushed, artifacts in a **closed** Nexus staging repo |
| 3 | Stage | Assemblies in `dist/dev`, Version Notes page live, `[TEST]` mail sent |
| 4 | Vote | 72 h elapsed, three binding `+1`, result mail sent |
| 5 | Promote | Nexus repo released, `dist/dev` → `dist/release`, 24 h rsync waited |
| 6 | Publish | Site PR merged, GitHub release un-flagged, `[ANN]` mail delivered |
| 7 | Advisories | Bulletins public, CVE records filled, advisory mails sent from the CVE tool |

Phase 7 only exists when the release carries a security fix, and *publishing* the advisory is
**strictly after** phase 6 — see *Security work is a separate clock* below. Writing the bulletin
is not: it is usually drafted long before the release exists, and often on its own timetable
entirely.

## Which skill owns which artifact

Cross-references, not copies. Do not restate what these settle:

- **`creating-version-notes`** — the Version Notes page, its Staging Repository block, the
  Migration Guide entry, the GitHub release notes, and the `[TEST]` mail. All of phase 3's
  paperwork.
- **`creating-release-vote-mail`** — the `[VOTE]` mail. All of phase 4's paperwork.
- **`creating-security-bulletins`** — the S2-XXX page, what may be disclosed and when,
  publication, and the advisory mails.

**That last one is not a phase of this process.** A bulletin gets written when the report is
triaged, which may be months before a release carries the fix, and plenty of bulletins are
handled with no release in flight at all. It is a skill in its own right, invoked whenever it is
needed. Phase 7 is the reverse direction: *if* this release carries a security fix, then once
phase 6 is done, go and follow that skill.

Phases 1, 2, 5 and 6 have no sibling skill — the guidelines carry those steps, and this skill
carries the ordering that binds them.

## Two lines, two releases

`main` is the 7.x line; `support/struts-6-x-x` is 6.x. Both are protected and both require their
build to pass. A change that lands on both is **two releases**, each with its own tag, vote,
site entry and announcement — not one release mentioned twice.

They can be cut in parallel and voted in parallel, and usually are. Keep the version numbers
independent: 6.11.0 and 7.3.0 shipped together and share nothing but a date.

**Neither line branch is where the release is cut.** Both August 2026 releases were built on a
`release/X.Y.Z-RC1` branch off the line, so the `[maven-release-plugin]` commits never reach
`main`. A failed vote is then a deleted branch, not a revert.

## The version number is chosen at release time

The `-SNAPSHOT` in the pom is a placeholder, not a decision. Pick the number from the semver
impact of what actually landed since the last tag, and say so out loud before cutting — the tag
is the first irreversible act of the release.

The pom cannot tell you: because releases are cut on a side branch, `main` still read
`7.2.2-SNAPSHOT` after 7.3.0 had shipped.

## Security work is a separate clock

**Nothing about an unpublished advisory goes into the release paperwork.** Not the Version
Notes, not the `[TEST]` mail, not the `[VOTE]`, not the commit messages, not the site entry.
The tickets are neutral; that is deliberate and it is what makes the embargo survive a public
release process.

The advisory follows the release, and the ordering is not negotiable:

```
release GA  →  bulletin unrestricted  →  advisory mails  →  CVE pushed to MITRE
```

A bulletin published before the fixed artifact is downloadable tells attackers what to look for
and gives operators nothing to do about it.

**A 6.x release containing only embargoed fixes is self-disclosing** — the diff between the two
tags is the vulnerability whatever the commit messages say. That is a reason to bundle it with
unrelated work, or to publish the bulletins with the release, not a reason to pretend otherwise.

## The cwiki release pages are retired

The wiki pages a release manager used to land on — *Building Struts 2 — Normal release*,
*Fast track release*, *Creating and Signing a Distribution*, *One time steps*,
*Sample announcements* — were retired in August 2026 and now carry nothing but a pointer to
[Release Guidelines](https://struts.apache.org/release-guidelines.html). Their old content
survives only in page history, where it describes a process last revised between 2013 and 2017:
`develop`/`master` branches, `people.apache.org`, an svn checkout of the production site.

**Never restore a step from that history.** If something in the guidelines looks incomplete, the
answer is the last release, not the last wiki revision.

## Gates that are actually load-bearing

- **A closed Nexus staging repo, not just a successful `release:perform`.** Until it is closed
  the URL in the Version Notes resolves to nothing and every tester is blocked.
- **72 hours, and three binding `+1`.** PMC votes are the binding ones; `private@` is on the
  vote mail so binding voters see it.
- **24 hours after the `dist` move, before announcing.** ASF mirroring guidance. Announcing into
  an unmirrored release sends everyone to a 404.
- **The GitHub release stops being a prerelease at phase 6, not at phase 3.** During the vote it
  must still be flagged, or the vote is on an artifact the world already treats as final.

## Red Flags — STOP

- Cutting a tag before the version number has been stated and agreed
- A `[VOTE]` opened on a staging repo that is not closed, or on a link that 404s
- Announcing before the 24-hour mirror wait
- Any severity, CVE, S2-XXX or bulletin link in release paperwork
- A bulletin unrestricted before the fixed release is downloadable
- Reviving a step from the history of a retired cwiki page
- One release "covering" both maintenance lines
- Inferring the release version from the `-SNAPSHOT` in the pom
- Closing or releasing a Nexus staging repository yourself — that is the release manager's login

## Common Mistakes

| Mistake | Reality |
|---|---|
| "`release:perform` succeeded, so the artifacts are staged" | They are staged and *open*. Close the repo or nobody can fetch them. |
| "The vote passed, so it's released" | Nexus release, dist move and the mirror wait all come after. |
| "I'll announce now and fix the site after" | The announcement links the site. Merge the site PR first. |
| "The 6.x fix is the same change, so one announcement covers both" | Two artifacts, two downloads, two sets of affected users. |
| "The advisory is an announcement, so it gets `[ANN]` too" | `[ANN]` is for releases. CVE reports take the CVE tool's subject unedited. |
| "The pom says 7.3.1-SNAPSHOT, so this is 7.3.1" | The placeholder is not a decision. Semver impact decides. |
| "The process is documented in this skill" | It is documented on the site. This skill adds ordering, ownership and hand-offs. |
| "I found the release steps on the wiki" | Those pages are stubs now. Their history is the 2013–2017 process. |
