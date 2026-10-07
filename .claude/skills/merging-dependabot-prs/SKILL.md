---
name: merging-dependabot-prs
description: Use when triaging, classifying or landing Dependabot pull requests in this repo — clearing the open Dependabot queue, deciding whether a bump needs a WW Jira ticket, or checking whether a bump's build actually passed.
---

# Merging Dependabot PRs

## Overview

Dependabot opens bumps against `main` and `support/struts-6-x-x`. Most land as-is.
A bump that **ships to users** needs a WW Jira ticket first, and the ticket ID must
reach the PR title, the PR body and the squash commit subject before it merges.

**Core principle: the decision is driven by the dependency's real Maven scope, not by
what Dependabot calls it.** Dependabot's `build(deps-dev):` prefix and its
`dependency-type:` trailer are guesses about *its own* ecosystem, not this project's
POM. Read the POM.

## The decision

Read the semver class from the Dependabot commit trailer — never parse version strings:

```bash
gh pr view <N> --json commits --jq '.commits[].messageBody' \
  | grep -E 'dependency-name:|update-type:'
# update-type: version-update:semver-{patch|minor|major}
```

Then find the real scope, and cross the two:

| What is being bumped | patch | minor | major |
|---|---|---|---|
| GitHub Action (`.github/workflows/*`) | merge bare | merge bare | merge bare |
| Maven **plugin** or build tooling (`<build><plugins>`, `*-maven-plugin`, `maven-wrapper`) | merge bare | merge bare | merge bare |
| Maven dep, `<scope>test</scope>` | merge bare | merge bare | merge bare |
| Maven dep in `apps/` (showcase, rest-showcase) | merge bare | merge bare | merge bare |
| Maven dep, **compile / runtime / provided** — including `<optional>true</optional>` | merge bare | **TICKET** | **TICKET** |

"Merge bare" always still requires a green build (see below).

### Finding the real scope

```bash
grep -rn '<artifactId>NAME</artifactId>' --include=pom.xml . | grep -v /target/
```

Read the `<scope>` on the surrounding `<dependency>` block in the module that declares it
(`core/pom.xml`, `plugins/*/pom.xml`), not the `<dependencyManagement>` copy in
`parent/pom.xml` — the managed block usually carries no scope.

- No `<scope>` element means **compile** — it ships.
- `<optional>true</optional>` still ships: it is published in the module's POM and users
  who opt in inherit the version. Optional is not exempt.
- For a version property (`jackson.version`, `byte-buddy.version`), resolve the property to
  the artifacts it feeds and take the widest scope among them.

## Is the build actually green?

`.asf.yaml` makes exactly one context required per branch:
`Build and Test (JDK 17)` on `main`, `Build and Test (8)` on `support/struts-6-x-x`.

```bash
gh pr view <N> --json mergeStateStatus,statusCheckRollup --jq \
  '"\(.mergeStateStatus) build=\([.statusCheckRollup[]
    | select((.name // "") | startswith("Build and Test"))
    | (.conclusion // .state)] | unique | join(","))"'
```

- Green = every `Build and Test *` job is `SUCCESS`.
- `continuous-integration/jenkins/pr-merge` is **not** a required context and flakes red
  ("This commit cannot be built"). `mergeStateStatus: UNSTABLE` with all build jobs green
  is mergeable. Ignore Jenkins.
- Any `Build and Test *` failure, or `mergeStateStatus: BLOCKED` — **stop**. Report it and
  move on to the next PR.

## Checkpoint — classify, then stop

Present one row per open PR and **wait for approval** before any Jira write, title edit or
merge. Required columns:

| PR | Base | Bump | Semver | Real scope (+ where declared) | Build | Action |

Only after approval, run the recipes below.

## Landing a bare bump

```bash
gh pr merge <N> --squash --subject "<exact current PR title> (#<N>)"
```

`del_branch_on_merge: true` is set, so no `--delete-branch`.

## Landing a ticketed bump

Five steps, in order. All five are required.

**1. Create the ticket** — `mcp__asf-issues__jira_create_issue`:

```json
{
  "project_key": "WW",
  "issue_type": "Dependency",
  "summary": "Bump <group:artifact or property> from <old> to <new>",
  "description": "Bump <group:artifact or property> from <old> to <new>",
  "components": "<affected module>",
  "additional_fields": "{\"priority\": {\"name\": \"Trivial\"}, \"fixVersions\": [{\"name\": \"<branch release version>\"}]}"
}
```

- `components` is its **own** parameter (comma-separated names), not a member of
  `additional_fields`. `additional_fields` must be a JSON **string**, not an object.
- Issue type is **Dependency** — not Task, not Bug.
- Component is the module that declares the dep: `Core`, `Plugin - REST`, `Plugin - JSON`,
  `Unit Tests`, `Build Management`, …
- Fix version is the one the latest tickets on the same line already carry:
  `project = WW AND fixVersion in unreleasedVersions() ORDER BY created DESC`.
  Not the pom SNAPSHOT: after 6.12.0, `support/struts-6-x-x` read `6.12.1-SNAPSHOT` while
  its next release was 6.13.0. Not the lowest unreleased Jira version either: a shipped
  version can stay unreleased in Jira for days.

**2. Retitle the PR** — insert the ticket after the conventional-commit prefix, leave every
other character alone:

```bash
gh pr edit <N> --title "build(deps): WW-XXXX bump org.htmlunit:htmlunit from 4.21.0 to 5.1.0"
#                                    ^^^^^^^^ inserted; prefix and remainder verbatim
```

**3. Add the Closes line to the body** — its own paragraph, directly after the first line
(`Bumps …`). Some bodies have no release notes and so no `<details>` before the commands
footer; anchor on the `Bumps` line, never on `<details>`. Preserve the rest exactly:

```
Bumps [org.htmlunit:htmlunit](https://github.com/HtmlUnit/htmlunit) from 4.21.0 to 5.1.0.

Closes [WW-XXXX](https://issues.apache.org/jira/browse/WW-XXXX)

<details>
```

**4. Merge with the ticket in the squash subject:**

```bash
gh pr merge <N> --squash --subject "build(deps): WW-XXXX bump org.htmlunit:htmlunit from 4.21.0 to 5.1.0 (#<N>)"
```

Without an explicit `--subject`, GitHub takes the subject from Dependabot's *commit*
headline and the ticket ID is silently lost from git history.

**5. Hand the ticket back, still Open** — list it under the merge report as ready to
resolve once *every* PR on it has merged. The ticket workflow belongs to the release
manager; do not transition it unless asked. When asked:

```json
{"issue_key": "WW-XXXX", "fields": "{\"status\": \"5\", \"resolution\": {\"name\": \"Fixed\"}}", "return_fields": "status,resolution"}
```

`"5"` is the id of the *Resolve Issue* transition. The status name (`"Resolved"`, `"Closed"`)
and the transition name (`"Resolve Issue"`) all fail with "Could not find transition"; only
the id works. Verified on WW-5755.

## One dependency, two branches, one ticket

Dependabot opens the same bump separately against `main` and `support/struts-6-x-x`. Before
creating anything, look for the twin:

```bash
gh pr list --state open --author app/dependabot --json number,title,baseRefName
```

If a twin exists, create **one** ticket, put it in both PR titles/bodies, and list **both**
fix versions on it (WW-5649 carries 6.11.0 and 7.3.0 for PRs #1760 and #1763). It is ready
to resolve once both have merged.

The twin may already have merged in an earlier run, so `--state open` will not show it. Search
Jira too: `project = WW AND issuetype = Dependency AND summary ~ "<artifact>" ORDER BY created DESC`.
If the same bump already has a ticket, reuse it and add this branch's fix version to it.

## Scope of this skill

Triage and land the queue. Do **not**, as part of it, open follow-up PRs — `dependabot.yml`
ignore rules, license-header restorations, test-harness fixes. Report such findings in one
line under the table and let the decision be made separately.

Any bump with a red build is real compatibility work, not a merge. Report the root cause
(first `[ERROR]` lines of the failing `Build and Test` job) and stop. If asked to defer it:
file a `Dependency` ticket at the next major with the root cause, then close the PR with a
comment that gives the cause, links the ticket, and ends in `@dependabot ignore this major
version` — Dependabot replies "OK, I won't notify you…" (Hibernate 7 on #1905, Weld 7 on
#1998). That is the ignore rule; no `dependabot.yml` PR. Never push a fix onto a
Dependabot branch — Dependabot stops rebasing it, and a source change lands under a
ticketless `build(deps):` title.

## Traps

| Trap | Reality |
|---|---|
| "It says `build(deps-dev)`, so it's a dev dependency" | That prefix is Dependabot's guess. `commons-validator` arrives as `deps-dev` and is test scope (true), but the prefix is not evidence — the POM is. |
| "`dependency-type: direct:production`, so it ships" | `maven-wrapper` and `hibernate-core` both say `direct:production`; one is build tooling, the other ships. Read the POM. |
| "`<optional>true</optional>` means users don't get it" | It is in the published POM and pins the version for anyone who opts in. Optional compile deps need a ticket. |
| "git log shows no `WW-` on dependency commits, so titles aren't rewritten" | The squash *subject* historically came from Dependabot's commit headline while the *PR title* carried the ticket. Compare `gh pr view 1746 --json title` against `1f1674411`. Step 4 above exists to close that gap. |
| "A red check means don't merge" | Only `Build and Test *` counts. The ASF Jenkins context is not required and flakes red. |
| "Both branches need their own ticket" | One dependency, one ticket, two fix versions. |
| "The pom says `6.12.1-SNAPSHOT`, so the fix version is 6.12.1" | The pom is a placeholder. Jira's latest tickets on the line carry the real next version. |
| "The build is green, so I can just merge it" | Green is necessary, not sufficient. Classify first, and stop at the checkpoint. |

## Red flags — stop and re-read

- About to run `gh pr merge` before presenting the classification table and getting approval
- About to run `gh pr merge --squash` on a ticketed bump without `--subject`
- Classified a dependency from the PR title alone, without grepping the POM for its scope
- Creating a second ticket for a dependency that already has an open twin PR
- Opening a follow-up PR that nobody asked for
- Creating a Jira issue whose type is anything other than `Dependency`
- Transitioning a ticket nobody asked you to resolve
- Reading the fix version off the pom

## Related

- `creating-version-notes` — ticketed bumps appear in the Version Notes Dependency section;
  bare bumps deliberately do not, and their absence is not a reconciliation gap.
