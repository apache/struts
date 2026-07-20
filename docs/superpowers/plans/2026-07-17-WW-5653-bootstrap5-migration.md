# Bootstrap 5.3.x Sample-App Migration — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate `apps/showcase` and `apps/rest-showcase` off vendored Bootstrap 3.3.4 onto Bootstrap 5.3.7 served via WebJars, keeping the pages' look and behavior (minimal port).

**Architecture:** Bootstrap CSS/JS, Bootstrap Icons, and (showcase-only) jQuery are pulled as WebJar dependencies and referenced with the framework's `<s:webjar>` tag. Vendored static assets are deleted. Bootstrap-3-only markup (glyphicons, panels, navbar, `data-*` hooks, grid classes, alert dismiss) is rewritten to its Bootstrap 5 equivalent.

**Tech Stack:** Maven, JSP + Struts UI tags, Bootstrap 5.3.7, Bootstrap Icons 1.13.1, jQuery 3.7.1, `<s:webjar>` (webjars-locator-lite).

## Global Constraints

- Pinned versions: **bootstrap `5.3.7`** (`org.webjars`), **bootstrap-icons `1.13.1`** (`org.webjars.npm`), **jquery `3.7.1`** (`org.webjars`, showcase only).
- The shared `webjars-jquery.version` property is **`4.0.0`** (drives core test scope, PR #1789). The showcase MUST use a **separate** `webjars-jquery-showcase.version=3.7.1` property — never reuse `webjars-jquery.version`.
- Exact `<s:webjar>` (version-less) paths:
  - `bootstrap/css/bootstrap.min.css`
  - `bootstrap/js/bootstrap.bundle.min.js` (bundle includes Popper — required for dropdowns/tooltips)
  - `bootstrap-icons/font/bootstrap-icons.min.css`
  - `jquery/jquery.min.js`
- WebJar allowlist per app: `struts.webjars.allowlist=jquery,bootstrap,bootstrap-icons` (rest-showcase: `bootstrap,bootstrap-icons`). `struts.webjars.enabled` already defaults `true`.
- Commit convention: prefix every commit `WW-5653 <type>...` and end with `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`.
- Work on branch `WW-5653-bootstrap5-sample-apps`. Finish with a **draft** PR.
- No automated UI tests exist for these apps. Per-task verification is **build success + grep assertions**; final verification is **manual browser smoke testing**. (This plan intentionally deviates from unit-test TDD — the deliverables are JSP/asset changes with no unit-test surface.)

### Icon mapping (glyphicon → Bootstrap Icons)

| Bootstrap 3 | Bootstrap 5 (`bi bi-*`) |
|-------------|-------------------------|
| `glyphicon-home` | `bi-house` |
| `glyphicon-cog` | `bi-gear` |
| `glyphicon-file` | `bi-file-earmark` |
| `glyphicon-question-sign` | `bi-question-circle` |
| `glyphicon-share` | `bi-share` |
| `glyphicon-upload` | `bi-upload` |
| `glyphicon-arrow-left` | `bi-arrow-left` |
| `glyphicon-eye-open` | `bi-eye` |
| `glyphicon-edit` | `bi-pencil` |
| `glyphicon-trash` | `bi-trash` |

Markup form: `<span class="glyphicon glyphicon-home"></span>` → `<i class="bi bi-house"></i>`.

### Class / attribute mapping (Bootstrap 3 → 5)

| Bootstrap 3 | Bootstrap 5 |
|-------------|-------------|
| `panel panel-default` | `card` |
| `panel-heading` | `card-header` |
| `panel-title` | `card-title` |
| `panel-body` | `card-body` |
| `panel-footer` | `card-footer` |
| `btn-default` | `btn-secondary` |
| `col-xs-N` | `col-N` |
| `data-toggle=` | `data-bs-toggle=` |
| `data-target=` | `data-bs-target=` |
| `data-dismiss=` | `data-bs-dismiss=` |
| `<a class="close">&times;</a>` | `<button type="button" class="btn-close" aria-label="Close"></button>` |
| `pull-right` / `pull-left` | `float-end` / `float-start` |
| `navbar-default` | `navbar-light bg-light` |
| `hidden` | `d-none` |
| `text-right` | `text-end` |

---

### Task 1: POM / dependency wiring

**Files:**
- Modify: `pom.xml` (properties block, ~line 131)
- Modify: `parent/pom.xml` (dependencyManagement, ~line 60)
- Modify: `apps/showcase/pom.xml` (`<dependencies>`, before line 175 `</dependencies>`)
- Modify: `apps/rest-showcase/pom.xml` (`<dependencies>`)

**Interfaces:**
- Produces: managed webjar dependencies + version properties consumed by Tasks 2 and 4.

- [ ] **Step 1: Add version properties to root `pom.xml`**

In the properties block (next to `webjars-jquery.version`), add:

```xml
        <webjars-bootstrap.version>5.3.7</webjars-bootstrap.version>
        <webjars-bootstrap-icons.version>1.13.1</webjars-bootstrap-icons.version>
        <webjars-jquery-showcase.version>3.7.1</webjars-jquery-showcase.version>
```

Leave the existing `<webjars-jquery.version>4.0.0</webjars-jquery.version>` untouched (it drives core test scope).

- [ ] **Step 2: Add managed dependencies to `parent/pom.xml`**

After the existing `org.webjars:jquery` managed dependency block, add:

```xml
            <dependency>
                <groupId>org.webjars</groupId>
                <artifactId>bootstrap</artifactId>
                <version>${webjars-bootstrap.version}</version>
            </dependency>

            <dependency>
                <groupId>org.webjars.npm</groupId>
                <artifactId>bootstrap-icons</artifactId>
                <version>${webjars-bootstrap-icons.version}</version>
            </dependency>
```

Note: `bootstrap-icons` is under group `org.webjars.npm`. Do NOT add a managed entry for the showcase jQuery — it is declared directly in the showcase pom (Step 3) to keep its version explicit and separate from the shared `jquery` managed entry.

- [ ] **Step 3: Add dependencies to `apps/showcase/pom.xml`**

Immediately before `</dependencies>` (line ~175), add:

```xml
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>bootstrap</artifactId>
        </dependency>
        <dependency>
            <groupId>org.webjars.npm</groupId>
            <artifactId>bootstrap-icons</artifactId>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>jquery</artifactId>
            <version>${webjars-jquery-showcase.version}</version>
        </dependency>
```

The explicit `<version>` overrides the `4.0.0` managed default for the showcase only.

- [ ] **Step 4: Add dependencies to `apps/rest-showcase/pom.xml`**

Inside `<dependencies>`, add (no jQuery — rest-showcase uses none):

```xml
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>bootstrap</artifactId>
        </dependency>
        <dependency>
            <groupId>org.webjars.npm</groupId>
            <artifactId>bootstrap-icons</artifactId>
        </dependency>
```

- [ ] **Step 5: Verify dependency resolution**

Run: `mvn -q -pl apps/showcase,apps/rest-showcase -am dependency:resolve -DskipAssembly 2>&1 | tail -20`
Expected: BUILD SUCCESS; `org.webjars:bootstrap:jar:5.3.7`, `org.webjars.npm:bootstrap-icons:jar:1.13.1`, and `org.webjars:jquery:jar:3.7.1` (for showcase) appear in the tree.

Confirm the showcase jQuery is 3.7.1, not 4.0.0:
Run: `mvn -q -pl apps/showcase dependency:tree -Dincludes=org.webjars:jquery -DskipAssembly 2>&1 | grep jquery`
Expected: `org.webjars:jquery:jar:3.7.1`.

- [ ] **Step 6: Commit**

```bash
git add pom.xml parent/pom.xml apps/showcase/pom.xml apps/rest-showcase/pom.xml
git commit -m "WW-5653 build: add Bootstrap 5, Bootstrap Icons, showcase jQuery webjars

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 2: Showcase asset delivery + alert dismiss

**Files:**
- Modify: `apps/showcase/src/main/webapp/WEB-INF/decorators/main.jsp:64-79`
- Modify: `apps/showcase/src/main/resources/struts.xml` (constants block, ~line 37)
- Delete: `apps/showcase/src/main/webapp/styles/bootstrap.css`, `bootstrap.min.css`, `bootstrap.css.map`, `bootstrap-theme.css`, `bootstrap-theme.min.css`, `bootstrap-theme.css.map`
- Delete: `apps/showcase/src/main/webapp/js/bootstrap.min.js`, `apps/showcase/src/main/webapp/js/jquery-2.1.4.min.js`

**Interfaces:**
- Consumes: webjar deps from Task 1.
- Produces: `<s:webjar>`-served assets used by all showcase pages (Task 3).

- [ ] **Step 1: Replace the CSS/JS head references in `main.jsp`**

Replace lines 64-79 (from the `bootstrapCss` block through the closing `</s:script>` of the alert-init block) with exactly this:

```jsp
    <link rel="stylesheet" type="text/css" media="all" href="<s:webjar path='bootstrap/css/bootstrap.min.css'/>"/>
    <link rel="stylesheet" type="text/css" href="<s:webjar path='bootstrap-icons/font/bootstrap-icons.min.css'/>"/>
    <s:url var="mainCss" value='/styles/main.css' encode='false' includeParams='none'/>
    <s:link href="%{mainCss}" rel="stylesheet" type="text/css" media="all"/>

    <script src="<s:webjar path='jquery/jquery.min.js'/>"></script>
    <script src="<s:webjar path='bootstrap/js/bootstrap.bundle.min.js'/>"></script>
    <s:script>
        $(function () {
            $('ul.alert').each(function () {
                var wrapper = $('<div class="alert alert-dismissible" />');
                $(this).before(wrapper);
                wrapper.append('<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>').append(this);
            });
        });
    </s:script>
```

Guard note: `<s:webjar>` writes the resolved URL inline, so it goes inside a plain `<link>`/`<script>` `href`/`src` attribute — not via `<s:link href>` / `<s:script src>`. Keep the existing `mainCss` `<s:url>`/`<s:link>` pair as shown.

- [ ] **Step 2: Verify the prettyPrint ready-handler is intact**

Confirm the later block (around old line 91) still reads:

```jsp
    <s:script>
        jQuery(document).ready(function() { prettyPrint(); } );
    </s:script>
```

Leave it unchanged — jQuery is still served.

- [ ] **Step 3: Add the webjar allowlist constant to showcase `struts.xml`**

After the `struts.allowlist.packageNames` constant (~line 39), add:

```xml
    <constant name="struts.webjars.allowlist" value="jquery,bootstrap,bootstrap-icons"/>
```

- [ ] **Step 4: Delete vendored static assets**

```bash
git rm apps/showcase/src/main/webapp/styles/bootstrap.css \
       apps/showcase/src/main/webapp/styles/bootstrap.min.css \
       apps/showcase/src/main/webapp/styles/bootstrap.css.map \
       apps/showcase/src/main/webapp/styles/bootstrap-theme.css \
       apps/showcase/src/main/webapp/styles/bootstrap-theme.min.css \
       apps/showcase/src/main/webapp/styles/bootstrap-theme.css.map \
       apps/showcase/src/main/webapp/js/bootstrap.min.js \
       apps/showcase/src/main/webapp/js/jquery-2.1.4.min.js
```

- [ ] **Step 5: Verify no lingering references to deleted assets**

Run: `grep -rn "bootstrap.css\|bootstrap.min.js\|bootstrap-theme\|jquery-2.1.4" apps/showcase/src/main/webapp`
Expected: no matches (empty output).

- [ ] **Step 6: Build the showcase WAR**

Run: `mvn -q -pl apps/showcase -am package -DskipAssembly -DskipTests 2>&1 | tail -5`
Expected: BUILD SUCCESS.

- [ ] **Step 7: Commit**

```bash
git add apps/showcase/src/main/webapp/WEB-INF/decorators/main.jsp apps/showcase/src/main/resources/struts.xml
git commit -m "WW-5653 feat(showcase): serve Bootstrap 5 and jQuery via webjars

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 3: Showcase markup migration

**Files:**
- Modify: showcase JSPs under `apps/showcase/src/main/webapp/` (~78 files) wherever the mapped classes/attributes appear, including `WEB-INF/decorators/main.jsp` (navbar, glyphicons).

**Interfaces:**
- Consumes: Bootstrap 5 assets from Task 2.
- Produces: fully BS5-compatible markup (final app in Task 6).

- [ ] **Step 1: Inventory every occurrence to migrate**

Run and save the list:

```bash
grep -rInE "glyphicon|panel(-default|-heading|-title|-body|-footer)?\b|btn-default|col-xs-|data-toggle=|data-target=|data-dismiss=|class=\"close\"|pull-(right|left)|navbar-default|\btext-right\b" apps/showcase/src/main/webapp --include="*.jsp"
```

Expected: a non-empty list. Work through it file by file.

- [ ] **Step 2: Migrate glyphicons**

For every `<span class="glyphicon glyphicon-X">...</span>`, replace with `<i class="bi bi-Y"></i>` using the icon mapping table in Global Constraints. Example — in `main.jsp`:

```jsp
<li><s:a value="%{home}"><i class="bi bi-house"></i> Home</s:a></li>
```

- [ ] **Step 3: Migrate panels → cards**

For each panel block, apply: `panel panel-default`→`card`, `panel-heading`→`card-header`, `panel-title`→`card-title`, `panel-body`→`card-body`, `panel-footer`→`card-footer`. Example:

```html
<div class="card">
  <div class="card-header"><h3 class="card-title">Title</h3></div>
  <div class="card-body">...</div>
</div>
```

- [ ] **Step 4: Migrate buttons, grid, utilities, data hooks**

Apply the class/attribute mapping table: `btn-default`→`btn-secondary`, `col-xs-N`→`col-N`, `pull-right`→`float-end`, `pull-left`→`float-start`, `text-right`→`text-end`, `hidden`→`d-none`, and `data-toggle`/`data-target`/`data-dismiss` → `data-bs-*`.

- [ ] **Step 5: Migrate the navbar in `main.jsp`**

Update the BS3 navbar to BS5 structure. Replace `navbar navbar-default` with `navbar navbar-expand-lg navbar-light bg-light`; convert the toggle button to:

```html
<button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbar-main" aria-controls="navbar-main" aria-expanded="false" aria-label="Toggle navigation">
  <span class="navbar-toggler-icon"></span>
</button>
```

Convert dropdown triggers to `class="nav-link dropdown-toggle" data-bs-toggle="dropdown"` and dropdown menus to `class="dropdown-menu"` with `class="dropdown-item"` links. Keep the existing menu entries and links; only the wrapper classes/attributes change.

- [ ] **Step 6: Verify no Bootstrap 3 markup remains**

Run: `grep -rInE "glyphicon|panel-(default|heading|title|body|footer)|btn-default|col-xs-|data-toggle=|data-target=|data-dismiss=|class=\"close\"|pull-(right|left)|navbar-default" apps/showcase/src/main/webapp --include="*.jsp"`
Expected: no matches (empty output).

- [ ] **Step 7: Rebuild showcase**

Run: `mvn -q -pl apps/showcase -am package -DskipAssembly -DskipTests 2>&1 | tail -5`
Expected: BUILD SUCCESS.

- [ ] **Step 8: Commit**

```bash
git add apps/showcase/src/main/webapp
git commit -m "WW-5653 feat(showcase): migrate JSP markup to Bootstrap 5

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 4: rest-showcase asset delivery

**Files:**
- Modify: the 5 order JSPs under `apps/rest-showcase/src/main/webapp/WEB-INF/content/` (`orders-index.jsp`, `orders-show.jsp`, `orders-edit.jsp`, `orders-editNew.jsp`, `orders-deleteConfirm.jsp`) — each references `css/bootstrap.min.css` at line ~32
- Modify: `apps/rest-showcase/src/main/resources/struts.xml`
- Delete: `apps/rest-showcase/src/main/webapp/css/bootstrap.css`, `bootstrap.min.css`, `bootstrap.css.map`, `bootstrap-theme.css`, `bootstrap-theme.min.css`, `bootstrap-theme.css.map`

**Interfaces:**
- Consumes: webjar deps from Task 1.
- Produces: `<s:webjar>`-served CSS used by rest-showcase pages (Task 5).

- [ ] **Step 1: Replace the bootstrap CSS reference in each of the 5 JSPs**

Change the line `<s:link href="%{#pageContextPath}/css/bootstrap.min.css" rel="stylesheet"></s:link>` to:

```jsp
    <link rel="stylesheet" href="<s:webjar path='bootstrap/css/bootstrap.min.css'/>"/>
    <link rel="stylesheet" href="<s:webjar path='bootstrap-icons/font/bootstrap-icons.min.css'/>"/>
```

Leave the adjacent `app.css` `<s:link>` untouched.

- [ ] **Step 2: Add the webjar allowlist constant to rest-showcase `struts.xml`**

Inside `<struts>`, alongside the other constants (~line 31), add:

```xml
    <constant name="struts.webjars.allowlist" value="bootstrap,bootstrap-icons"/>
```

- [ ] **Step 3: Delete vendored static CSS**

```bash
git rm apps/rest-showcase/src/main/webapp/css/bootstrap.css \
       apps/rest-showcase/src/main/webapp/css/bootstrap.min.css \
       apps/rest-showcase/src/main/webapp/css/bootstrap.css.map \
       apps/rest-showcase/src/main/webapp/css/bootstrap-theme.css \
       apps/rest-showcase/src/main/webapp/css/bootstrap-theme.min.css \
       apps/rest-showcase/src/main/webapp/css/bootstrap-theme.css.map
```

- [ ] **Step 4: Verify no lingering references**

Run: `grep -rn "css/bootstrap\|bootstrap-theme" apps/rest-showcase/src/main/webapp`
Expected: no matches.

- [ ] **Step 5: Commit**

```bash
git add apps/rest-showcase/src/main/webapp apps/rest-showcase/src/main/resources/struts.xml
git commit -m "WW-5653 feat(rest-showcase): serve Bootstrap 5 CSS via webjars

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 5: rest-showcase markup migration

**Files:**
- Modify: the 5 order JSPs under `apps/rest-showcase/src/main/webapp/WEB-INF/content/`

**Interfaces:**
- Consumes: Bootstrap 5 CSS from Task 4.
- Produces: BS5-compatible rest-showcase markup (final app in Task 6).

- [ ] **Step 1: Inventory occurrences**

Run: `grep -rInE "glyphicon|panel-(default|heading|title|body|footer)|btn-default|col-xs-|pull-(right|left)|</apan>" apps/rest-showcase/src/main/webapp --include="*.jsp"`
Expected: non-empty (glyphicons + the `</apan>` typo).

- [ ] **Step 2: Migrate glyphicons and classes**

Apply the icon and class mapping tables. Example — `orders-index.jsp`:

```jsp
<a href="orders/${id}" class="btn btn-secondary"><i class="bi bi-eye"></i> View</a>
<a href="orders/${id}/edit" class="btn btn-secondary"><i class="bi bi-pencil"></i> Edit</a>
<a href="orders/${id}/deleteConfirm" class="btn btn-danger"><i class="bi bi-trash"></i> Delete</a>
```

And the "Back to Orders" links: `<i class="bi bi-arrow-left"></i> Back to Orders`.

- [ ] **Step 3: Fix the malformed closing tag**

In `orders-editNew.jsp:79`, change `</apan>` to `</span>` — but since that `<span class="glyphicon ...">` becomes `<i class="bi bi-arrow-left">`, the corrected line is:

```jsp
        <i class="bi bi-arrow-left"></i> Back to Orders
```

- [ ] **Step 4: Verify no Bootstrap 3 markup or typo remains**

Run: `grep -rInE "glyphicon|panel-(default|heading|title|body|footer)|btn-default|col-xs-|</apan>" apps/rest-showcase/src/main/webapp --include="*.jsp"`
Expected: no matches.

- [ ] **Step 5: Build rest-showcase**

Run: `mvn -q -pl apps/rest-showcase -am package -DskipAssembly -DskipTests 2>&1 | tail -5`
Expected: BUILD SUCCESS.

- [ ] **Step 6: Commit**

```bash
git add apps/rest-showcase/src/main/webapp
git commit -m "WW-5653 feat(rest-showcase): migrate JSP markup to Bootstrap 5

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 6: Full build, manual verification, draft PR

**Files:** none (verification + PR).

**Interfaces:**
- Consumes: all prior tasks.

- [ ] **Step 1: Full build**

Run: `mvn clean install -DskipAssembly 2>&1 | tail -15`
Expected: BUILD SUCCESS across all modules (existing core webjar tests still pass — they use the `4.0.0` shared jQuery, unaffected by the showcase's 3.7.1).

- [ ] **Step 2: Run the showcase and smoke-test in a browser**

Run: `mvn -pl apps/showcase jetty:run -DskipAssembly` (then open http://localhost:8080/). Verify:
- Navbar renders; the collapse toggle and dropdown menus (Configuration, File, Help) open.
- Icons render (home, gear, file, question-circle).
- Alerts show a working close (×) button.
- Prettify code blocks are highlighted (jQuery ready-handler works).
- The AJAX validation form (`/validation/ajaxFormSubmit`) submits and shows feedback.
- No 404s for `/static/webjars/bootstrap/...`, `.../bootstrap-icons/...`, `.../jquery/...` in the browser network tab.

- [ ] **Step 3: Run the rest-showcase and smoke-test**

Run: `mvn -pl apps/rest-showcase jetty:run -DskipAssembly`. Verify order list/show/edit/new/delete pages render with correct styling and `bi` icons; no webjar 404s.

- [ ] **Step 4: Push branch and open a DRAFT PR**

```bash
git push -u origin WW-5653-bootstrap5-sample-apps
gh pr create --draft \
  --title "WW-5653 Upgrade Bootstrap to 5.3.x in sample apps" \
  --body "$(cat <<'EOF'
Fixes [WW-5653](https://issues.apache.org/jira/browse/WW-5653)

Migrates `apps/showcase` and `apps/rest-showcase` from vendored Bootstrap 3.3.4 to Bootstrap 5.3.7 served via `<s:webjar>`. Minimal port — pages keep their look and behavior.

- Bootstrap 5.3.7 + Bootstrap Icons 1.13.1 via WebJars; vendored static assets removed
- Showcase jQuery kept (3.7.1) via a dedicated `webjars-jquery-showcase.version`, decoupled from the shared `webjars-jquery.version` (4.0.0, core test scope)
- Glyphicons → Bootstrap Icons; panels → cards; `data-*` → `data-bs-*`; navbar/alert markup updated
- WebJar allowlist configured per app
- Verified via manual browser smoke testing (no automated UI tests for these apps)

🤖 Generated with [Claude Code](https://claude.com/claude-code)
EOF
)"
```

Expected: PR created in **draft** state. Report the PR URL.
