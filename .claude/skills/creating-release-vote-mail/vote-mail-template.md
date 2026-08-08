# Release Vote Mail Template

The canonical skeleton for the `[VOTE] Apache Struts X.Y.Z` mail that opens a release vote.
Companion to [`SKILL.md`](SKILL.md), which covers *how* to fill the slots; this file covers
*what the mail contains*.

**This file is the source of truth.** Start every vote mail from the skeleton below.

## Slots

| Slot | What goes in it |
|---|---|
| `<X.Y.Z>` | The release being voted on, dotted — subject, opening sentence, Version Notes URL, dist path |
| `<X_Y_Z>` | The same version underscored, for the `STRUTS_` git tag only |
| `<SHAPE SENTENCE>` | See below — authored per release |
| Page sections | Breaking changes, Deprecations, Rejected requests, and the issue-type sections, copied from the Version Notes page. Omit any the page omits. |

## The shape sentence is authored per release

The opener is two sentences. The first is fixed. The second describes the *shape* of the issue
list — how the release is composed — and is written from the list in front of you.

| The page has | Second sentence |
|---|---|
| No Breaking changes | `With this release the following issues were addressed:` |
| Breaking changes | `This release contains <what>:` |

**Do not reuse a previous release's wording.** 7.2.1's *"a few minor breaking changes plus some
bug fixes. Also a lot of dependencies have been updated"* describes 7.2.1. Applied to 7.3.0 —
seven breaking changes, one dependency bump — both halves are false.

Name no individual ticket here. The list below is the detail.

## Skeleton

```
Subject: [VOTE] Apache Struts <X.Y.Z>
To:      dev@struts.apache.org
Bcc:     private@struts.apache.org

The Apache Struts <X.Y.Z> test build is available. <SHAPE SENTENCE>

Breaking changes

- <page item, verbatim, ending [WW-XXXX].>

Deprecations

- <page item, verbatim, ending [WW-XXXX].>

Rejected requests

[WW-XXXX] - <page item, verbatim>

Bug
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
* https://repository.apache.org/content/groups/staging/

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

Hard-wrap the body at 72 columns, continuation lines unindented, so the list stays legible in
the ASF archives and in quoted replies.

## Frozen text

Everything from `Once you have had a chance to review the test build` to the sign-off is
byte-frozen. It is the vote call itself: the options voters tick, the binding threshold, the
72-hour minimum, and what a binding vote commits the voter to.

Details that look like defects and are kept:

| Detail | Why |
|---|---|
| `Github release` has no trailing colon | Every archived Struts vote mail reads this way |
| All four checkboxes empty | The release manager's `+1 (binding)` is a separate reply |

The staging URL is `content/groups/staging/`, matching the Version Notes page and the `[TEST]`
mail. The group repo also resolves released transitive dependencies, which the bare staging
repository does not.

## Pre-draft checklist

- [ ] All four links resolve; the GitHub release is still flagged pre-release
- [ ] Ticket ids in the mail match the page's exactly, both directions
- [ ] Boilerplate byte-identical to the frozen text above
- [ ] `To: dev@` only; `user@` absent from every header; `Bcc: private@` present
- [ ] Subject is exactly `[VOTE] Apache Struts X.Y.Z`
- [ ] All four checkboxes empty
- [ ] No severity, CVE, S2-XXX, bulletin link or reporter detail anywhere
- [ ] Exactly one mail
