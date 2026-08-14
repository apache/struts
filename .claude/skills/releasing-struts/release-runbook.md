# Release Runbook

**The process is published, not kept here.** Every phase, every command, every gate is at
[Release Guidelines](https://struts.apache.org/release-guidelines.html), maintained in
`apache/struts-site` as `source/release-guidelines.md`. Read it there and follow it.

This file holds only what that page cannot: the points where a step is a human's to take rather
than yours, and the scripts this skill ships. [`SKILL.md`](SKILL.md) holds the sequence, the
gates and the judgement.

**When you learn something new during a release, it goes in the site page.** A correction that
lands only here is a correction the next release manager will never see.

## The scripts

Phases 3 and 5 ship with this skill, in [`scripts/`](scripts). The Release Guidelines link to
them by GitHub URL, so they are part of the published process — changing their behaviour means
updating that page too.

| Script | Phase | What it does |
|---|---|---|
| [`stage-assemblies.sh`](scripts/stage-assemblies.sh) | 3 | Closed staging repo → `dist/dev`, renamed and re-hashed |
| [`promote-dist.sh`](scripts/promote-dist.sh) | 5 | `dist/dev` → `dist/release` |

Both take `$VERSION` from the environment, refuse to run without it, and refuse a value that is
not a version number — `svn` resolves a `.` path element rather than rejecting it, so a stray
`VERSION` would otherwise move the whole staging tree in one irreversible commit.

Run them from a scratch directory, never from a repository checkout: `stage-assemblies.sh`
creates `./$VERSION` and a temporary svn working copy in the current directory. That means
calling them by absolute path, since the scratch directory is not the checkout:

```bash
cd "$(mktemp -d)"
VERSION=7.3.0 ~/Projects/Apache/struts/.claude/skills/releasing-struts/scripts/stage-assemblies.sh
```

## Phase 1 — Prepare

**If the JDK is wrong, ask — do not infer.** `mvn -v` reports what Maven is actually using, and
the line dictates what that must be (7.x on 17, 6.x on 8). Local environments differ — jenv,
SDKMAN, asdf, `JAVA_HOME` by hand, a Homebrew symlink — and guessing at someone's toolchain is
how you end up building against a JDK they did not intend. `.java-version` is gitignored in this
repo, so it is not a signal either.

**State the version number and get agreement before phase 2 begins.** The tag is the first
irreversible act of the release, and the pom cannot tell you the number.

## Phase 2 — Cut

At the SCM tag prompt, `STRUTS_X_Y_Z` is typed by hand every time. **This one cannot move into
the pom**, so do not "fix" it: `tagNameFormat` interpolates `@{project.version}` and has no
string functions, so the best it could produce is `STRUTS_7.3.0`. The prompt stays.

**Closing the staging repository is the release manager's action, not yours.** It happens in the
Nexus web UI at <https://repository.apache.org/> (Staging Repositories → select → Close), behind
an ASF login. Say so, hand over, and **wait for confirmation before continuing** — phase 3
fetches from the staging *group* URL and gets nothing while the repository is open.

The gate is worth checking yourself once you are told it is done:

```
https://repository.apache.org/content/groups/staging/org/apache/struts/struts2-core/$VERSION/
```

## Phase 3 — Stage

Run [`stage-assemblies.sh`](scripts/stage-assemblies.sh) as above, then **count the files** at
`https://dist.apache.org/repos/dist/dev/struts/$VERSION/`: six assemblies, each with `.asc`,
`.sha256` and `.sha512`, 24 in total. `set -eu` stops the script on a step that *fails*, not on a
crawl that quietly returns a subset, so a short upload reaches `dist/dev` looking healthy.

Everything else in this phase belongs to **`creating-version-notes`**: the Version Notes page,
its Staging Repository block, the Migration Guide entry, the GitHub release (created as a
**prerelease**), and the `[TEST]` mail.

## Phase 4 — Vote

**`creating-release-vote-mail`** owns the mail. Nothing here.

## Phase 5 — Promote

Run [`promote-dist.sh`](scripts/promote-dist.sh). **Releasing the staging repository in Nexus is
again the release manager's action** in the web UI — hand over and wait, as in phase 2.

Pruning old releases from `dist/release/struts/` is a deliberate decision, never an autopilot
step: several supported versions from both lines are normally kept.

## Phase 6 — Publish

**The `[ANN]` mail must be `text/plain`, and a draft made with the Gmail tool is an HTML draft
whatever you pass it.** `announce@apache.org` rejects any message carrying a `text/html` part —

```
ezmlm-reject: fatal: Sorry, a message part has an unacceptable MIME Content-Type: 'text/html' (#5.2.3)
```

— and `announcements@struts.apache.org` answers *"Must be sent from an @apache.org address."*
See *The mail must be text/plain* in `creating-release-vote-mail` for the full contract. One list
accepting the mail is not evidence the format was right.

## Phase 7 — Advisories

**`creating-security-bulletins`** owns all of it: unrestricting the bulletin, the CVE record on
<https://cveprocess.apache.org>, and the advisory mails from that record's *OSS/ASF Emails* tab.
Follow that skill from here; it is not a step in this runbook.
