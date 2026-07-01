# Spec: WebJars support in Struts core

**Date:** 2026-07-01
**JIRA:** [WW-5640](https://issues.apache.org/jira/browse/WW-5640)
**Status:** Design approved — grounded against 7.2.x source; ready for implementation plan
**Scope:** Framework (`core`) only. The consuming plugin work (struts2-bootstrap) is separate, in a separate repo, and starts only after this ships in a Struts release.

> ASF process note: commit messages must be prefixed with the ticket (`WW-5640 ...`). Before opening a PR, confirm the change is not a security patch (it is a feature) per `SECURITY.md`.

## Goal

Add first-class **WebJars** support to Struts core so client-side libraries packaged as WebJars (`org.webjars:*`, shipped under `META-INF/resources/webjars/<name>/<version>/…`) can be referenced from templates and tags by a **version-less logical path** and served through Struts' existing static-content pipeline.

Example: a template references `bootstrap/css/bootstrap.min.css`; Struts resolves and serves `META-INF/resources/webjars/bootstrap/5.3.8/css/bootstrap.min.css`, and the emitted URL is `<ctx>/static/webjars/bootstrap/5.3.8/css/bootstrap.min.css`.

## Motivation

Plugins and applications currently vendor client-side assets directly on the classpath and re-download/re-commit them on every upgrade. The struts2-bootstrap plugin, for example, commits ~2000 Bootstrap + bootstrap-icons files and re-vendors them by hand each release. WebJars replace this with a dependency bump (auto-updatable via Renovate/Dependabot). Struts should let plugins reference WebJar assets by a stable, version-less path and handle resolution + serving centrally.

**First consumer:** the struts2-bootstrap plugin (separate repo, separate work).

## Fixed decisions

| # | Decision | Choice | Rationale |
|---|----------|--------|-----------|
| Q0 | Serving model | **Struts serves** webjar assets through its static-content pipeline (`DefaultStaticContentLoader`), reusing content-type / caching handling. | Framework owns caching + security; works even where the servlet container does not auto-serve `META-INF/resources/`. Consistent with how Struts serves its bundled assets. |
| 1 | Version resolution | **`org.webjars:webjars-locator-lite`** maps version-less path → versioned classpath resource. | Purpose-built for frameworks; MIT; single Apache-2.0 transitive dep; no Jackson/scanner; adopted by Spring Framework 6.2 for the same reasons. |
| 2 | Public contract | **`WebJarUrlProvider` interface** (container-resolvable) **+ thin `<s:webjar>` tag and `<@s.webjar>` macro** on top. | Clean injectable Java seam for plugins + template ergonomics. |
| 3 | URL prefix | Serve under the **existing static content path**: `<staticContentPath>/webjars/…`. | Reuses the already-wired static dispatcher; no new servlet mapping; single enable/disable switch. |
| 4 | Security / allowlist | Hard-constrain resolution to the **`META-INF/resources/webjars/` root** with path normalization (block `..`). **Optional allowlist** of webjar names via a Struts config property; default = all webjars on classpath. | Struts streams classpath bytes, so traversal protection is mandatory; allowlist is opt-in defense-in-depth. |
| 5 | Cache-busting | No query-param cache-buster; the resolved **version lives in the URL path**. | Versioned URLs are inherently cache-stable; no ETag needed beyond current behavior. |
| 6 | Serving hook | **Add a `/webjars/` branch inside `DefaultStaticContentLoader`**, delegating resolution to `WebJarUrlProvider`, then reusing existing `process()`/caching. | Smallest change; single injected loader bean; no new multi-loader dispatch wiring. |
| 7 | MIME coverage | **Extend `getContentType()`** with common webjar asset types (fonts, svg, source maps, json, ico). | Current map returns `null` for `.woff2/.ttf/.svg/.map/.eot/.ico/.json`, breaking web-font loading. Benefits existing static content too. |
| 8 | Tag output | `<s:webjar>` / `<@s.webjar>` **emit the resolved URL string** (with optional `var` to store in the value stack). | Composes with the existing `<s:script>`/`<s:link>` tags; caller controls the element. |
| 9 | Allowlist config | Struts config property **`struts.webjars.allowlist`** (comma-separated; empty = all). | Consistent with `struts.webjars.enabled` and the rest of the `struts.*` config surface. |
| 10 | Target release | Land in the **next Struts 7.x minor**. | — |

## Architecture

One resolution seam, consumed by two callers (serving + URL-building):

```
                    ┌─────────────────────────────┐
   <s:webjar> ─────▶│                             │
   <@s.webjar> ────▶│      WebJarUrlProvider       │───▶ WebJarVersionLocator
                    │   (DefaultWebJarUrlProvider) │      (webjars-locator-lite,
   DefaultStatic ──▶│                             │       singleton, cached)
   ContentLoader    └─────────────────────────────┘
   (/webjars/ branch)
```

### Grounding (verified against 7.2.x source)

- **Dispatch chain:** `StrutsPrepareAndExecuteFilter.tryHandleRequest` → `ExecuteOperations.executeStaticResourceRequest` → `StaticContentLoader.canHandle(path)` → `findStaticResource(path, req, res)`.
- **Loader bean:** single container bean in `core/src/main/resources/struts-beans.xml`:
  `<bean type="org.apache.struts2.dispatcher.StaticContentLoader" class="org.apache.struts2.dispatcher.DefaultStaticContentLoader" name="struts"/>`.
- **`canHandle`:** `serveStatic && resourcePath.startsWith(uiStaticContentPath + "/")` (`DefaultStaticContentLoader`).
- **Fixed serving roots today:** `getAdditionalPackages()` → `org.apache.struts2.static`, `template`, `static` (+ debugging in devMode). Webjars need a distinct branch because resolution injects a **version**, so it is not just another package prefix.
- **Caching:** `process()` sets caching headers gated on `serveStaticBrowserCache` (`struts.serve.static.browserCache`); uses a fixed `Last-Modified`, no ETag. Versioned URLs make this sufficient.
- **Config pattern:** `StrutsConstants` (`public static final String struts.*`) + `core/src/main/resources/org/apache/struts2/default.properties` defaults + `@Inject(StrutsConstants.XXX)` setters.
- **DI pattern:** interface + default impl registered in `struts-beans.xml` as `name="struts"`; obtained via `Container.getInstance(Type.class)` or `@Inject`.
- **Tag pattern:** `Component` (or `ContextBean`) subclass + JSP `*Tag` (`getBean`/`populateParams`) + FreeMarker `*Model extends TagModel` (`getBean`) + registration in `StrutsModels` (field + getter). `<s:url>` builds context-aware URLs via `DefaultUrlHelper.buildUrl` / `request.getContextPath()`. `<s:script>`/`<s:link>` already render full elements pointing at static content.
- **Locator API:** `org.webjars.WebJarVersionLocator` (webjars-locator-lite 1.1.3, MIT; one transitive dep `org.jspecify:jspecify:1.0.0`, Apache-2.0). `WEBJARS_PATH_PREFIX = "META-INF/resources/webjars"`. `fullPath(name, filePath)` → versioned classpath path or **`null`** when unresolved. Thread-safe; recommended as a singleton.

> Class/method names above are verified against the current branch but must be re-confirmed at implementation time (the tree moves).

## Components

### `WebJarUrlProvider` (public contract — R3)
Public, stable interface in an appropriate `org.apache.struts2` package (e.g. `org.apache.struts2.views.webjars` — final package TBD during implementation, following neighbours). Container-registered so plugins depend on the interface, not internals.

Responsibilities:
- **Resolve to classpath resource** (for serving): logical `<webjar>/<path>` → `META-INF/resources/webjars/<webjar>/<version>/<path>`, or empty/absent when unresolved or blocked.
- **Resolve to servable URL** (for tags): `<contextPath> + <staticContentPath> + "/webjars/" + <webjar>/<version>/<path>`.
- Apply the **allowlist** and the **enabled** switch.
- Enforce **path normalization** and reject anything escaping `META-INF/resources/webjars/`.

`DefaultWebJarUrlProvider`:
- Holds a singleton `WebJarVersionLocator` (constructed once; thread-safe).
- `@Inject`s `struts.webjars.enabled`, `struts.webjars.allowlist`, `struts.ui.staticContentPath`.
- Fail-closed: locator `null` → no resource / no URL emitted.

### Serving branch in `DefaultStaticContentLoader` (R2, Decision 6)
- `canHandle` already matches `<staticContentPath>/...`; add recognition of the `/webjars/` sub-prefix and gate it additionally on `struts.webjars.enabled`.
- In `findStaticResource`, when the cleaned path starts with `/webjars/`, strip the prefix, hand the logical `<webjar>/<path>` to `WebJarUrlProvider` for classpath resolution, obtain the resource URL, and stream it through the existing `process()` (reusing caching + the extended content-type map). Unresolved → **404** (fail closed); never fall through to arbitrary classpath serving.

### Content-type extension (Decision 7)
Extend `getContentType()` to cover at least: `.woff`, `.woff2`, `.ttf`, `.eot`, `.otf`, `.svg`, `.map` (→ `application/json`), `.json`, `.ico`, `.mjs` (→ `text/javascript`). Applies to all static content, not just webjars.

### `<s:webjar>` tag + `<@s.webjar>` macro (R3, Decision 8)
- `WebJar` component (extends `ContextBean` to inherit `var` support) with a `path` attribute; `start`/`end` resolves via `WebJarUrlProvider` and emits the URL string, or stores it in `var` when set.
- JSP `WebJarTag` (`getBean` + `populateParams`); FreeMarker `WebJarModel extends TagModel`; register field + getter in `StrutsModels`. TLD entry is generated from `@StrutsTag`/`@StrutsTagAttribute` annotations.
- Unresolved path → emit nothing (fail closed).

### Configuration (R4, Decision 9)
New `StrutsConstants` + `default.properties`:
- `struts.webjars.enabled=true` — master switch.
- `struts.webjars.allowlist=` — optional comma-separated webjar names; empty = all.

Reuse existing `struts.serve.static`, `struts.serve.static.browserCache`, `struts.ui.staticContentPath`. No new prefix constant.

### Dependency (R6)
Add `org.webjars:webjars-locator-lite` (1.1.x, currently 1.1.3) to `core`. Manage the version in `bom`/`parent` per project convention. Transitive footprint: `org.jspecify:jspecify` only (Apache-2.0). Both Category A — no ASF licensing issue.

## Security (R5)

- Normalize the requested path and **reject any resolved path escaping `META-INF/resources/webjars/`** (block `..` traversal and encoded variants; the loader already `URLDecoder.decode`s in `buildPath`, so normalize post-decode).
- Only serve resources whose resolved path comes from the locator (a known webjar); never raw classpath lookups.
- Honour the optional allowlist.
- Honour `struts.webjars.enabled=false` (both serving and URL emission become inert).

## Data flow

**Serving:** request `<ctx>/static/webjars/bootstrap/5.3.8/css/bootstrap.min.css` → filter → `canHandle` (static + `/webjars/` + enabled) → `findStaticResource` strips `/static`, sees `/webjars/…`, asks `WebJarUrlProvider` to resolve to `META-INF/resources/webjars/bootstrap/5.3.8/css/bootstrap.min.css`, validates containment, `findResource`, `process()` → 200 + content-type + caching headers. Unresolved/blocked → 404.

**URL building:** `<s:webjar path="bootstrap/css/bootstrap.min.css"/>` → component → `WebJarUrlProvider` resolves version and composes `<ctx>/static/webjars/bootstrap/5.3.8/css/bootstrap.min.css` → emitted string (or stored in `var`). Typically wrapped by the caller: `<s:link href="%{webjarUrl}"/>` or `<link rel="stylesheet" href="...">`.

## Error handling

- **Unresolved webjar/path:** serving → 404; URL building → empty output / absent `var`. Never throw to the user; log at debug.
- **Disabled:** `canHandle` returns false for `/webjars/`; tag emits nothing.
- **Traversal / out-of-root:** treated as unresolved → 404 / empty.
- **Locator construction failure:** provider degrades to fail-closed; logged.

## Testing

- **Unit — resolution (`DefaultWebJarUrlProvider`):** known path → expected versioned URL; unknown webjar/path → empty; traversal (`bootstrap/../../../etc/passwd`, encoded `..`) → rejected; allowlist blocks a non-listed webjar; `enabled=false` → inert.
- **Unit — content-type:** each new extension maps to the expected MIME type.
- **Integration — serving:** request a served webjar asset → 200 + correct content-type + caching headers governed by `struts.serve.static.browserCache`; `struts.webjars.enabled=false` → 404/not served; unknown asset → 404.
- **Tag/macro:** `<s:webjar>` and `<@s.webjar>` render the resolved URL; `var` stores it; unresolved → empty.

Use JUnit 5 + AssertJ + Mockito, per project convention. A test webjar (e.g. a small `org.webjars` artifact) is added as a `test` dependency to `core` for integration coverage.

## Out of scope

- Container-served model (relying on Servlet `META-INF/resources` auto-serving) — rejected in favour of Q0.
- ETag support / per-resource `Last-Modified` — versioned URLs make it unnecessary.
- The consuming plugin's changes (separate work, separate repo).
- Any `<s:script>`/`<s:link>` convenience overload that auto-emits an element from a webjar path — possible follow-up; this spec emits URL strings only.

## Confirm during implementation

- Final package for `WebJarUrlProvider` and the tag classes (follow existing neighbours).
- Exact `canHandle`/`findStaticResource` edit points against the then-current `DefaultStaticContentLoader`.
- `webjars-locator-lite` version pinned via `bom`/`parent`; re-check latest 1.1.x at implementation time.
- Whether `struts.webjars.allowlist` matches on webjar name only (assumed) vs. name+path prefix.
