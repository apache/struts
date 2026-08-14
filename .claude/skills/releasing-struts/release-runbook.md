# Release Runbook

The commands, in order. [`SKILL.md`](SKILL.md) holds the sequence, the gates and the judgement;
this file is what you type.

**Provenance.** Everything marked ✔ was verified against the repository or a completed release
(7.3.0 / 6.11.0, August 2026). Everything marked **⚠ unverified** is carried over from the 2017
cwiki page and has *not* been confirmed against a current run — check it before relying on it,
and correct this file when you do.

**The scripts.** Phases 3 and 5 ship with this skill, in [`scripts/`](scripts):

| Script | Phase | What it does |
|---|---|---|
| [`stage-assemblies.sh`](scripts/stage-assemblies.sh) | 3 | Closed staging repo → `dist/dev`, renamed and re-hashed |
| [`promote-dist.sh`](scripts/promote-dist.sh) | 5 | `dist/dev` → `dist/release` |

Both take `$VERSION` from the environment, refuse to run without it, and refuse a value that is
not a version number — `svn` resolves a `.` path element rather than rejecting it, so a stray
`VERSION` would otherwise move the whole staging tree in one irreversible commit.

Run them from a scratch directory (`cd "$(mktemp -d)"`), never from a repository checkout: the
staging script creates `./$VERSION` and a temporary svn working copy in the current directory.

---

## Phase 1 — Prepare

✔ Two lines, two releases:

| Line | Branch | Build check that must pass |
|---|---|---|
| 7.x | `main` | `Build and Test (JDK 17)` |
| 6.x | `support/struts-6-x-x` | `Build and Test (8)` |

Both branches are protected in `.asf.yaml` and must be green before you start.

**Check the JDK before building anything.** The line dictates it — 7.x builds on **JDK 17**, 6.x
on **JDK 8** — and the whole release is produced by whichever JDK happens to be active in the
shell. Cutting 6.x on 17 produces artifacts that will not run for the users that line exists for.

```bash
mvn -v          # reports the JDK Maven is actually using, not just $JAVA_HOME
```

**If it is the wrong version, stop and ask the release manager how to switch.** Local
environments differ — jenv, SDKMAN, asdf, `JAVA_HOME` by hand, a Homebrew symlink — and guessing
at someone's toolchain is how you end up building against a JDK they did not intend. Ask, do not
infer. (`.java-version` is gitignored in this repo, so it is not a reliable signal either.)

```bash
git checkout main && git pull --ff-only
mvn clean install -DskipAssembly
```

Then:

- Decide the version number from semver impact. Do not read it off the `-SNAPSHOT`.
- ✔ Confirm `struts-master` (currently `15`) and `struts-annotations` are released versions, not
  snapshots. The root pom's `<parent>` must not point at a snapshot.
- ✔ The BOM needs no version sync. `bom/pom.xml` inherits the root version through its
  `<parent>` and declares members as `${project.version}`. The cwiki's
  `struts-version.version` property no longer exists — ignore that step.
- Review JIRA: every issue fixed since the last tag has a fix version; nothing unresolved carries
  this one.
- ⚠ unverified: the cwiki's "omnibus ticket" step. The 7.3.0 and 6.11.0 runs show no such ticket
  — treat it as abandoned unless the PMC says otherwise.

## Phase 2 — Cut

✔ **Cut from a release branch, not from the line branch.** Both August 2026 releases were built
on `release/X.Y.Z-RC1` branched off the line:

```bash
git checkout -b release/7.3.0-RC1 main        # or off support/struts-6-x-x for 6.x
git push -u origin release/7.3.0-RC1
```

The two `[maven-release-plugin]` commits land there and **`main` is never touched** — which is
why the root pom still said `7.2.2-SNAPSHOT` after 7.3.0 shipped, and why the pom is worthless
as a source for the release number.

✔ `maven-release-plugin` 3.3.1, driven interactively, on that branch:

```bash
mvn release:prepare
```

✔ No flags. `autoVersionSubmodules` is configured in the root pom, along with the ASF parent's
`useReleaseProfile=false`, `goals=deploy` and `releaseProfiles=apache-release`. If you find
yourself passing `-D` to the release plugin, the setting belongs in the pom instead — a flag
that has to be remembered is a flag that will be forgotten.

✔ **At the SCM tag prompt, type `STRUTS_X_Y_Z`.** The plugin's default would be
`struts2-project-X.Y.Z` (the root artifactId); every Struts tag in history is the underscore
form, and the GitHub release, the Version Notes and the site all assume it.

This one cannot move into the pom: `tagNameFormat` interpolates `@{project.version}` and has no
string functions, so the best it could produce is `STRUTS_7.3.0`. The prompt stays.

Dry run first if you want one — add `-DdryRun=true`, then `mvn release:clean` before the real
run. On failure, re-run the same command: `-Dresume` defaults to true and it picks up where it
stopped.

✔ The result is two commits on the release branch,
`[maven-release-plugin] prepare release STRUTS_X_Y_Z` and
`[maven-release-plugin] prepare for next development iteration`, plus the tag.

```bash
mvn release:perform
```

✔ `retryFailedDeploymentCount=10` is configured on `maven-deploy-plugin` in the root pom, not
passed here. It has to be in the pom to work at all: `release:perform` forks a fresh Maven
build, and that fork does not inherit `-D` properties from the outer invocation — the flag the
cwiki tells you to pass was doing nothing.

⚠ unverified: the fallback for re-running `perform` elsewhere —
`git checkout STRUTS_X_Y_Z && mvn javadoc:javadoc deploy -DperformRelease=true -Papache-release`.

**Then the staging repository has to be closed — and that is the release manager's action, not
yours.** It happens in the Nexus web UI at <https://repository.apache.org/> (Staging Repositories
→ select → Close), behind an ASF login. Say so, hand over, and **wait for confirmation before
continuing** — phase 3 fetches from the staging *group* URL and gets nothing while the repo is
open.

⚠ unverified in detail, but the gate is checkable and worth checking yourself once you are told
it is done: the artifacts must resolve under

```
https://repository.apache.org/content/groups/staging/org/apache/struts/struts2-core/$VERSION/
```

The staging repo is keyed by user *and* public IP. If your IP changed mid-release you will have
two; drop the stale one, checking the dates.

## Phase 3 — Stage

✔ [`scripts/stage-assemblies.sh`](scripts/stage-assemblies.sh) does this. It runs on your own
machine — the cwiki's "log in to `people.apache.org`" step is dead, that host is gone.

```bash
VERSION=7.3.0 .claude/skills/releasing-struts/scripts/stage-assemblies.sh
```

It fetches `zip`, `md5`, `sha1` and `asc` from the **closed** staging repo, strips the
`2-assembly` infix, drops the `.pom*` files and the legacy `md5`/`sha1` hashes, generates
`.sha256` and `.sha512` locally with `shasum`, prints what it is about to publish, then
`svn add`s the directory to `dist/dev/struts` and commits. It needs your ASF svn credentials.

✔ Gate, verified against `dist/release/struts/7.3.0/`:

```
https://dist.apache.org/repos/dist/dev/struts/$VERSION/
```

holds **six** assemblies — `struts-$VERSION-all.zip`, `-apps.zip`, `-docs.zip`, `-lib.zip`,
`-min-lib.zip` and `-src.zip` — each with `.asc`, `.sha256` and `.sha512` beside it: 24 files.
No `.md5`, no `.sha1`, no `.pom`. `KEYS` lives one level up, in `dist/release/struts/`.

Count them. `set -eu` stops the script on a step that *fails*, not on a crawl that quietly
returns a subset, so a short upload reaches `dist/dev` looking healthy.

**The staging repo must be closed before you run this** — the script pulls from the staging
*group* URL, and an open repo serves nothing there.

Everything else in this phase belongs to **`creating-version-notes`**: the Version Notes page,
its Staging Repository block, the Migration Guide entry, the GitHub release (created as a
**prerelease**), and the `[TEST]` mail to `dev@` and `user@`.

## Phase 4 — Vote

**`creating-release-vote-mail`** owns the mail. The mechanics around it:

- 72 hours minimum, three binding `+1` (PMC members).
- ✔ `To: dev@struts.apache.org`, `Bcc: private@struts.apache.org`. Never `user@`.
- Close with a result mail on the same thread.

## Phase 5 — Promote

✔ [`scripts/promote-dist.sh`](scripts/promote-dist.sh) does this — one server-side `svn mv`:

```bash
VERSION=7.3.0 .claude/skills/releasing-struts/scripts/promote-dist.sh
# svn mv https://dist.apache.org/repos/dist/dev/struts/$VERSION/ \
#        https://dist.apache.org/repos/dist/release/struts/ -m "Release Struts $VERSION"
```

Then **release** the staging repository in Nexus, which replicates to Maven Central.

✔ On pruning old releases: the cwiki says to keep only the latest. Current practice does not —
`dist/release/struts/` held 6.8.0, 6.9.0, 6.10.0, 6.11.0, 7.1.1, 7.2.1, 7.3.0 and `KEYS` in
August 2026. Everything removed stays available at
<https://archive.apache.org/dist/struts/>. Decide deliberately; do not prune on autopilot.

**Then wait 24 hours** for mirrors before anything in phase 6.

## Phase 6 — Publish

### The site — a PR to `apache/struts-site`

✔ Verified against PR #322 (the 7.3.0 / 6.11.0 GA announcement) and #323.

`_config.yml` — all of these move together:

```yaml
current_version: 7.3.0
current_version_short: 730
prev_version: 6.11.0
prev_version_short: 6110
release_date: 1 August 2026
prev_release_date: 1 August 2026
release_date_short: 20260801
prev_release_date_short: 20260801-6110
```

`release_date` is the **tag** date, not the announcement date — 7.2.1 was tagged 15 June and
announced 30 June, and the site says 15 June. The `*_date_short` values are the anchors in
`announce-YYYY.md`; when two releases share a tag date, disambiguate the second
(`20260801-6110`) so the two home-page boxes link to their own entries.

Then:

- `source/announce-YYYY.md` — a new `####` entry at the top, newest first, with its `{#aYYYYMMDD}`
  anchor.
- ✔ `source/releases.md` — the release table (Release / Release Date / Vulnerability / Version
  Notes). **Easy to miss and both August 2026 PRs changed it**; a site PR without it is
  incomplete.
- `source/index.html` — the GA boxes read from `_config.yml`; the security boxes are hand-edited.
- `source/dtds/` — only if a new DTD shipped.

✔ **Not** `source/download.cgi` — that is a six-line wrapper around `mirrors.cgi` with no release
content in it. `source/download.md` interpolates versions from `_config.yml` and its *Prior
releases* section is a static pointer to `archive.apache.org`, so neither needs a per-release
edit.

Publishing is the merge. There is no separate deploy step and no svn.

### GitHub release

✔ Un-flag the prerelease. Title `Struts X.Y.Z`, tag `STRUTS_X_Y_Z`.

### The `[ANN]` mail

✔ Recipients, from the 7.3.0 and 6.11.0 announcements:

```
To: user@struts.apache.org
Cc: announce@apache.org, announcements@struts.apache.org
```

`dev@` is not on it — the list already saw the `[TEST]` mail and the vote.

✔ **Plain text only, and sent from the `@apache.org` identity.** `announce@apache.org` rejects
any message carrying a `text/html` part —

```
ezmlm-reject: fatal: Sorry, a message part has an unacceptable MIME Content-Type: 'text/html' (#5.2.3)
```

— and `announcements@struts.apache.org` answers *"Must be sent from an @apache.org address."*
A draft made with the Gmail tool is an HTML draft whatever you pass it; see *The mail must be
text/plain* in `creating-release-vote-mail` for the full contract. One list accepting the mail
is not evidence the format was right.

Body: the GA boilerplate ("pleased to announce … General Availability … highest quality grade"),
the Version Notes link, the Migration Guide link for a major line, the minimum JDK/spec
requirements for that line, and the download page.

## Phase 7 — Advisories

Only when the release carries a security fix, and the *publication* only after phase 6. The
bulletin itself was almost certainly written when the report was triaged, long before this
release existed.

**`creating-security-bulletins`** owns all of it: unrestricting the bulletin, the CVE record on
<https://cveprocess.apache.org>, and the advisory mails from that record's *OSS/ASF Emails* tab.
Follow that skill from here; it is not a step in this runbook.

The order that matters here: the CVE record goes `RESERVED → DRAFT → READY`, and **READY is the
last state a PMC sets**. ASF Security submits it to the CVE Program and sets `PUBLIC`, so
`cve.org` links 404 until they do. That is expected, and it is not a reason to delay the
bulletin or the mails.

## Post-release

- Add the site announcement entry for any advisory (`announce-YYYY.md`), same form as the GA one.
- Check NVD once the CVE is public — affected ranges have been wrong before, and the fix is an
  email to `nvd@nist.gov` citing the CVE record.
- Answer any coordinator (JPCERT/CC and similar) in their existing thread once the bulletin is
  live; they hold their advisory until you confirm.
- Update the Version Notes page if the vote forced a re-cut.
