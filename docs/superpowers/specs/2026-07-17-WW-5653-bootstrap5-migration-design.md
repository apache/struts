# WW-5653 — Upgrade Bootstrap to 5.3.x in sample apps (design)

- **Ticket:** [WW-5653](https://issues.apache.org/jira/browse/WW-5653)
- **Date:** 2026-07-17
- **Status:** Approved design, pending implementation plan

## Summary

Migrate both sample applications — `apps/showcase` and `apps/rest-showcase` — off the
vendored Bootstrap 3.3.4 assets onto Bootstrap 5.3.x, delivered through the framework's
WebJars support (`<s:webjar>`). This is a **minimal port**: pages keep their existing
structure and appearance, and we change only what Bootstrap 5 breaks. The end state has
zero checked-in Bootstrap 3 assets and zero Bootstrap 3 markup remaining in the repo.

## Motivation

- Bootstrap 3.3.4 (2015) is long unsupported and ships known-vulnerable assets.
- Bootstrap 5 removes the jQuery dependency, modernizing the demo apps' front-end.
- Serving the assets via `<s:webjar>` dogfoods a framework feature in its own public
  demo app and lets Dependabot keep the assets current instead of manual file swaps.

## Decisions

These were settled during brainstorming:

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Migration depth | **Minimal port** | Showcase is a functional demo, not a design showcase; lowest risk. |
| Asset delivery | **WebJars via `<s:webjar>`** | Dogfoods the feature; no vendored assets; Dependabot-managed. |
| jQuery | **Keep, served via jQuery WebJar, pinned 3.7.1** | Two direct usages remain (not just Bootstrap); avoids a demo rewrite and jQuery 4 surprises. |
| Icons | **Bootstrap Icons via WebJar** | Official BS5 companion set; consistent with the delivery approach. |
| App scope | **Both apps** | rest-showcase is small; leaves zero Bootstrap 3 in the repo. |

## Scope

### In scope
- `apps/showcase` — full migration (Bootstrap CSS + JS, jQuery, icons, markup).
- `apps/rest-showcase` — CSS-only Bootstrap + icons + markup (no Bootstrap JS or jQuery
  is used there today).

### Out of scope
- Any idiomatic Bootstrap 5 rewrite (utilities API, cards-everywhere, redesigned layouts).
- Rewriting the jQuery usages in vanilla JS.
- Changes to `core` or any plugin — only the two sample apps and shared POM version
  properties are touched.

## Design

### 1. Dependency / asset delivery

- Root `pom.xml`: add version properties
  - `webjars-bootstrap.version` → 5.3.x
  - `webjars-bootstrap-icons.version` → latest
- **jQuery version coupling (important):** the existing `webjars-jquery.version` property
  drives the **test-scope** jQuery in `core` and is a candidate to move to 4.0.0
  (PR #1789). The showcase decision is to pin jQuery **3.7.1**. To keep the showcase
  decoupled from the core test dependency, introduce a dedicated
  `webjars-jquery-showcase.version` (= 3.7.1) property for the showcase, rather than
  reusing `webjars-jquery.version`. This prevents a future bump of the shared property
  from silently upgrading the showcase to jQuery 4.
- `parent/pom.xml`: add managed `<dependency>` entries for `org.webjars:bootstrap` and
  `org.webjars:bootstrap-icons`. The showcase `jquery` dependency is declared with the
  dedicated showcase version property.
- `apps/showcase/pom.xml`: add compile-scope deps on `bootstrap`, `bootstrap-icons`,
  `jquery`.
- `apps/rest-showcase/pom.xml`: add compile-scope deps on `bootstrap`, `bootstrap-icons`.
- Delete vendored static assets:
  - `apps/showcase/src/main/webapp/styles/bootstrap*.{css,map}`
  - `apps/showcase/src/main/webapp/js/bootstrap.min.js`
  - `apps/showcase/src/main/webapp/js/jquery-2.1.4.min.js`
  - `apps/rest-showcase/src/main/webapp/css/bootstrap*.{css,map}`
- Replace static references in decorators/JSPs with `<s:webjar>`:
  - `bootstrap/css/bootstrap.min.css`
  - `bootstrap/js/bootstrap.bundle.min.js` (the **bundle** includes Popper, required for
    dropdowns/tooltips/popovers) — showcase only
  - `bootstrap-icons/font/bootstrap-icons.css`
  - `jquery/jquery.min.js` — showcase only
- Configure the allowlist in each app's `struts.xml`:
  `struts.webjars.allowlist=jquery,bootstrap,bootstrap-icons`.
  (`struts.webjars.enabled` already defaults to `true`; the allowlist defaults to empty,
  which allows all — setting it explicitly demonstrates the security control.)

### 2. Markup migration (minimal port)

Applied wherever the classes/attributes appear (~78 showcase JSPs, 5 rest-showcase JSPs):

- **Icons:** `glyphicon glyphicon-X` → Bootstrap Icons `bi bi-Y`. Mapping:
  home→house, cog→gear, file→file-earmark, trash→trash, edit→pencil, eye-open→eye,
  arrow-left→arrow-left, upload→upload, share→share, question-sign→question-circle.
- **Panels:** `panel`, `panel-default`, `panel-heading`, `panel-body` → `card`,
  `card-header`, `card-body`.
- **Navbar:** update BS3 navbar markup to BS5 structure; `data-toggle`/`data-target` →
  `data-bs-toggle`/`data-bs-target`.
- **Buttons:** `btn-default` → `btn-secondary`.
- **Grid:** `col-xs-*` → `col-*`; verify other breakpoint classes.
- **JS data hooks:** any remaining `data-*` Bootstrap hooks → `data-bs-*`.
- **jQuery:** unchanged in source; `main.jsp` prettyPrint ready-handler and
  `ajaxFormSubmit.jsp` AJAX demo continue to work, now served from the jQuery webjar.
- **Incidental fix:** correct the malformed `</apan>` closing tag in
  `apps/rest-showcase/src/main/webapp/WEB-INF/content/orders-editNew.jsp:79`.

### 3. Components / units of work

The migration decomposes into independent, separately verifiable units:

1. **POM/dependency wiring** — version properties, managed deps, app deps.
2. **Showcase asset delivery** — decorator `main.jsp` webjar references + allowlist +
   static-file deletion.
3. **Showcase markup** — icons, panels, navbar, buttons, grid, data-bs-* across JSPs.
4. **rest-showcase asset delivery** — per-page webjar CSS references + allowlist +
   static-file deletion.
5. **rest-showcase markup** — icons + BS5 classes across the 5 order JSPs + typo fix.

## Verification

No automated UI tests exist for these apps, so the acceptance gate is **manual browser
testing** after a successful build:

- `mvn clean install -DskipAssembly` succeeds.
- Both apps deploy and start.
- Showcase: navbar + dropdowns, modals/collapses, tooltips/popovers, file-upload pages,
  the AJAX validation form (`ajaxFormSubmit.jsp`), and prettyPrint code blocks render and
  behave correctly.
- rest-showcase: order list/show/edit/new/delete pages render with correct styling and
  icons.
- Confirm no 404s for webjar-served assets (correct paths + allowlist entries).

## Risks / Notes

- **Breaking UI migration, not a version bump.** BS3→BS5 has no drop-in compatibility;
  expect changes across most showcase JSPs. Mitigated by the minimal-port scope.
- **Manual verification only.** No UI test coverage means visual/interaction regressions
  must be caught by hand.
- **Popper dependency.** Dropdowns/tooltips need `bootstrap.bundle.min.js`, not the plain
  `bootstrap.min.js`.
- **Bootstrap Icons markup.** `bi` icons render via a webfont CSS; verify the font files
  resolve under the webjar static path.
- **Exact versions pinned at implementation.** "5.3.x" / "latest" here denote the newest
  stable release available when the implementation plan is written; the plan will record
  the exact pinned versions (e.g. bootstrap 5.3.n, bootstrap-icons x.y.z).
