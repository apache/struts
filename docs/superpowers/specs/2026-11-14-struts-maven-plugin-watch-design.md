# Struts Maven Plugin: `struts:watch` — Design

Date: 2026-11-14 · Ticket: [WW-5864](https://issues.apache.org/jira/browse/WW-5864) · Status: approved

## Intent

Developers working on a Struts application under `mvn jetty:run` (or any server that runs
from `target/classes`) with `struts.devMode=true` expect edits to `struts.xml` and message
bundles to show up on the next request. Today they have to restart the server.

A spike (2026-11-14, showcase app, devMode on, Jetty 11 plugin) showed that Struts' own
reloading works — the gap is in the build layout:

| Change | Edited in `src/main/resources` | Same edit in `target/classes` |
|---|---|---|
| New action in `struts.xml` | not picked up | picked up on the next request |
| Message in `globalMessages.properties` | not picked up | picked up on the next request |
| Action class (recompiled) | — | old class keeps running |

Struts watches the URL it loaded each file from (`DefaultFileManager` / `FileRevision`,
checked per request by `ConfigurationManager.conditionalReload()`). Under `jetty:run` that is
`target/classes/...`, and nothing copies edits there while the server runs. When something
does, Jetty's own scanner (`<scan>` in the showcase pom) additionally restarts the whole
webapp, duplicating the work Struts already did.

`struts:watch` closes the copy gap: it watches the project's resource directories and pushes
changed files into the output directory through Maven's own resource processing, so Struts'
existing reload picks them up.

## Goals

- Save a resource file in `src/main/resources`, refresh the browser, see the change — no
  server restart, no IDE build step, works from the command line.
- Honour the project's Maven resource configuration: the declared `<resources>` directories,
  includes/excludes, `targetPath`, and filtering (`${...}` placeholders), so the file in
  `target/classes` is exactly what `process-resources` would have produced.
- Run alongside whatever server the developer already uses (`jetty:run`, Cargo, Tomcat
  plugin, an IDE-managed server) with no dependency on any of them.
- Help the developer see the loop working: log each file pushed, warn when Struts' reload
  switches look off.

## Non-goals

- **Java class reloading.** Out of scope (decided). Developers keep using a restart or JVM
  hotswap under a debugger.
- **Starting or embedding a server.** The plugin never owns Jetty/Tomcat (decided: alongside,
  not a wrapper).
- **Webapp files (JSP/FTL in `src/main/webapp`).** Already served from source by `jetty:run`.
- **Gradle, and Struts Boot apps.** A later, separate question.
- **Multi-module applications** (resources in a jar module, `jetty:run` in the war module).
  Single module only for now (decided). The goal watches the module it runs in; run from a
  reactor with more than one project, it fails fast with a message to run it in the module
  that owns the resources (`-pl <module>`) instead of silently watching only the first one.
- **Any change to Struts core's reload mechanism.** The spike showed it works.

## Anti-goals

- A file in `target/classes` that differs from what `process-resources` would produce
  (unfiltered placeholders, ignored excludes). Developers would chase phantom bugs.
- Anything that leaks into the deployed application — this is a build-time tool only.
- A save that triggers a full webapp restart in the documented setup.

## Constraints

- Lives in the Struts reactor, released with Struts. First `maven-plugin` packaging module in
  the repo: needs `maven-plugin-plugin` and the Maven plugin API (`provided` scope).
- Java 17 (`maven.compiler.release` in the root pom).
- ASF naming: `maven-*-plugin` is reserved for Apache Maven; the artifact is
  `struts-maven-plugin`, giving the prefix `struts` → `mvn struts:watch`. Prefix resolution
  works when the plugin is declared in the application's pom (no `pluginGroups` needed).
- macOS: the JDK `WatchService` is polling-based with a ~10 s default interval there, which
  is too slow for save-and-refresh.

## Approaches considered

1. **IDE build + Jetty scan config (docs only).** IntelliJ ⌘F9 / Eclipse auto-build already
   copy resources. No code, but needs the IDE and a habit; no help from the CLI.
2. **Point Jetty's classloader at `src/main/resources` (pom recipe).** No code, no build step,
   but loses resource filtering and it is unverified whether `extraClasspath` can precede
   `target/classes`.
3. **Struts core watches source folders in devMode.** Server-agnostic, but bakes the Maven
   layout into core, ignores filtering, and adds devMode code that reads outside the
   classpath — new core surface and security review.
4. **Struts Maven plugin** — chosen. Two variants:
   - **Alongside (`struts:watch`)** — chosen: small, no server dependency, survives server
     version changes; costs a second command (or a background mode, see below).
   - Wrapper (`struts:run` embedding a server) — rejected: Struts would own a server version,
     its Jakarta line and every config option developers expect from `jetty:run`; effectively
     a second Struts Boot.

## Design

### Module

- Location: `tools/struts-maven-plugin` (new top-level `tools` aggregator; `plugins/` is
  reserved for runtime Struts plugins). _My call._
- `groupId` `org.apache.struts`, `artifactId` `struts-maven-plugin`, packaging `maven-plugin`.
- No dependency on `struts2-core` — it never loads Struts classes.

### Goal `watch`

- Default: blocking. Developer runs `mvn struts:watch` in a second terminal next to
  `mvn jetty:run`. Ctrl+C stops it.
- Background mode (`<background>true</background>` / `-Dstruts.watch.background=true`):
  the goal starts a daemon watcher thread and returns, so binding it to `process-resources`
  in the app's pom makes `mvn jetty:run` alone enough (jetty:run forks the lifecycle, then
  blocks; the watcher lives as long as the Maven JVM). _My call; the builder must verify the
  thread survives `jetty:run`'s forked lifecycle._
- A bound background execution is a no-op unless explicitly switched on
  (`-Dstruts.watch=true`, typically `mvn jetty:run -Dstruts.watch`), so committing the binding
  to an application pom never starts a watcher in `package`/`install`/CI/release builds.
  _My call._
- Watched roots: every `<resource>` directory from the project model (`project.getResources()`),
  not a hard-coded `src/main/resources`.
- On change: re-process **only the changed file** with the project's resource settings via
  `maven-filtering` (`MavenResourcesFiltering`, the library `maven-resources-plugin` uses),
  honouring includes/excludes, `targetPath`, filtering, encoding and delimiters. Write to
  `project.getBuild().getOutputDirectory()`.
- Filtering settings that are not in the project model (delimiters, `escapeString`,
  `nonFilteredFileExtensions`, `propertiesEncoding`, `useDefaultDelimiters`,
  `addDefaultExcludes`) are read from the effective `maven-resources-plugin` configuration of
  the `default-resources` execution in the model — no mirror parameters on `struts:watch`, so
  the two cannot drift. _My call._
- A change to a filter file (`build.filters`) re-pushes every filtered resource. _My call._
- Extra `copy-resources` executions writing into the output directory are out of scope. _My call._
- On delete: remove the corresponding output file.
- Change detection: a last-modified/size polling scan of the watched roots at a configurable
  interval (default 500 ms), not `WatchService`, because of the macOS constraint. Debounce so
  an editor's save-via-rename produces one push.
- Logging: one INFO line per push (`pushed struts.xml → target/classes/struts.xml`).

### Struts-specific help

- At start, best-effort check of `struts.devMode`, `struts.configuration.xml.reload` and
  `struts.i18n.reload` in the output directory's `struts.xml` / `struts.properties`; WARN when
  devMode is explicitly `false` or a reload switch is explicitly `false`. Best-effort only —
  constants set from Spring, Java config or system properties are invisible, so never fail.
- Docs show the Jetty plugin config that stops Jetty's scanner restarting on resource
  changes. _Unverified whether Jetty 11/12's `scanClassesPattern` excludes cover this; the
  builder must confirm, or document `<scan>0</scan>` as the fallback._

### Showcase and docs

- `apps/showcase` declares the plugin with a bound background execution, inert without
  `-Dstruts.watch` (so CI and the `jetty:start` ITs never run a watcher), and its Jetty config
  is switched to the documented "no restart on resource change" setup — the showcase is the
  reference setup. Showcase keeps `devMode=false` (it also runs the ITs). The plugin's own
  invoker IT is what actually exercises the watcher. _My call._
- The plugin's generated goal docs ship with the module; the struts-site usage page is a
  follow-up PR in that repo. _My call._
- Fix version comes from the ticket.

## Testing

- The watcher/copier is a plain class tested without Maven (JUnit 4, matching the repo):
  change, add, delete, excluded file ignored, filtered placeholder resolved, `targetPath`
  respected, debounce.
- One integration test through `maven-invoker-plugin` (or the plugin testing harness) on a
  tiny fixture project running the goal in background mode. _Builder's choice of harness._

## Left to the builder

- Exact `maven-plugin-plugin` / `maven-filtering` versions and the Maven API baseline.
- Thread/lifecycle handling for background mode and clean shutdown.
- Test harness for the integration test.
- Whether the `tools` aggregator joins the default reactor or a profile (default preferred, so
  it is released with every Struts release).
