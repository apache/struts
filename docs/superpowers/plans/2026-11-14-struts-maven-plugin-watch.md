# `struts:watch` Maven Goal Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** A `struts-maven-plugin` whose `watch` goal pushes changed resource files into
`target/classes` (with the project's own filtering), so Struts' devMode reload picks them up
without a server restart.

**Architecture:** Pure, Maven-free core classes (scan → diff/debounce → push) behind a thin
`WatchMojo`. Pushing reuses `maven-filtering`'s `MavenResourcesFiltering` with settings read
from the project's effective `maven-resources-plugin` configuration. A best-effort inspector
warns when Struts' reload switches are off. One invoker IT drives the real goal.

**Tech Stack:** Java 17, Maven Plugin API + annotations, `maven-filtering`,
`plexus-utils` (`DirectoryScanner`, `Xpp3Dom`), JUnit 4 + AssertJ, `maven-invoker-plugin`.

**Spec:** `docs/superpowers/specs/2026-11-14-struts-maven-plugin-watch-design.md` (WW-5864)

## Global Constraints

- Every commit message starts `WW-5864 `; branch `feat/WW-5864-struts-maven-plugin-watch`.
- Module path `tools/struts-maven-plugin`; `groupId` `org.apache.struts`, `artifactId`
  `struts-maven-plugin`, packaging `maven-plugin` → goal prefix `struts`.
- Java package `org.apache.struts2.maven.watch`.
- No dependency on `struts2-core`. Maven API artifacts are `provided` scope.
- Tests are JUnit 4 (`org.junit.Test`) + AssertJ. No Jupiter.
- Every new file carries the ASF license header (RAT runs in the build), including invoker
  fixture files.
- Polling, not `WatchService`. Default interval 500 ms.
- Single module only: if the goal is about to run and `session.getProjects().size() > 1`,
  fail with: `struts:watch supports a single module; run it in the module that owns the
  resources, e.g. -pl <module>`.
- Background mode does nothing unless `struts.watch=true`. Blocking mode always runs.
- Never fetch an external DTD while reading `struts.xml`.
- Default to no code comments (user's global rule); Javadoc only on Mojo parameters, where it
  becomes the generated goal docs.
- Build with `./mvnw -B ... > log 2>&1; echo $?` and read the log; never judge a build from a
  filtered pipe.

## Review Focus

1. **Editor atomic save** (temp file + rename, or delete-then-create within one poll): one
   push, never a delete of the output file. → Task 2 test
   `deleteFollowedByRecreateBeforeStableIsAPushNotADelete`.
2. **New file in a new sub-directory** (`com/acme/FooAction.properties`): output parent
   directories are created. → Task 3 test `pushCreatesMissingParentDirectories`.
3. **`target/` cleaned while watching** (`mvn clean` in another terminal): the next change
   still pushes. → Task 3 test `pushAfterOutputDirectoryDeletedRecreatesIt`.
4. **Push failure** (filtering error, locked file): logged, watcher keeps running. → Task 2
   test `pusherFailureIsReportedAndWatchingContinues`.
5. **Binary resource in a filtered directory** (an image under a `<filtering>true</filtering>`
   resource): copied byte-for-byte via `nonFilteredFileExtensions`. → Task 3 test
   `binaryFileInFilteredResourceIsCopiedUnchanged`.

---

### Task 1: Module skeleton and resources-plugin settings

**Files:**
- Create: `tools/pom.xml` (aggregator, `artifactId` `struts2-tools`, packaging `pom`, parent
  `struts2-parent` with `relativePath` `../parent/pom.xml`, module `struts-maven-plugin`)
- Create: `tools/struts-maven-plugin/pom.xml`
- Modify: `pom.xml` (root `<modules>`: add `<module>tools</module>` after `boot`)
- Create: `tools/struts-maven-plugin/src/main/java/org/apache/struts2/maven/watch/ResourcesPluginSettings.java`
- Test: `tools/struts-maven-plugin/src/test/java/org/apache/struts2/maven/watch/ResourcesPluginSettingsTest.java`

**Interfaces:**
- Produces: `ResourcesPluginSettings` (immutable) with
  `static ResourcesPluginSettings from(Xpp3Dom configuration, String projectEncoding)` (null
  configuration → defaults) and getters `getEncoding()`, `getPropertiesEncoding()`,
  `getDelimiters(): LinkedHashSet<String>`, `isUseDefaultDelimiters()`, `getEscapeString()`,
  `getNonFilteredFileExtensions(): List<String>`, `isAddDefaultExcludes()`.
- Produces: `static Xpp3Dom effectiveResourcesConfiguration(MavenProject project)` on the same
  class — the `maven-resources-plugin` plugin-level configuration merged with its
  `default-resources` execution configuration (execution wins).

- [ ] **Step 1: Plugin pom.** Parent `struts2-tools`. Plugins: `maven-plugin-plugin` 3.15.1
  (`goalPrefix` `struts`, `helpmojo` execution). Dependencies: `maven-plugin-api` and
  `maven-core` 3.9.11 `provided`; `maven-plugin-annotations` 3.15.1 `provided`;
  `org.apache.maven.shared:maven-filtering` 3.4.0; `junit`, `assertj-core` `test` (versions
  from the parent's dependencyManagement). Check Central for newer releases of the four pinned
  versions; prefer the newest that supports Java 17 and Maven 3.9.
- [ ] **Step 2: Write the failing tests** in `ResourcesPluginSettingsTest`:
  - `defaultsWhenNoConfiguration`: `from(null, "UTF-8")` → encoding `UTF-8`, delimiters
    empty, `isUseDefaultDelimiters()` true, `isAddDefaultExcludes()` true,
    `getNonFilteredFileExtensions()` empty, escape string null.
  - `readsAllFilteringSettings`: an `Xpp3Dom` built from
    `<configuration><encoding>ISO-8859-1</encoding><propertiesEncoding>UTF-8</propertiesEncoding><delimiters><delimiter>@</delimiter></delimiters><useDefaultDelimiters>false</useDefaultDelimiters><escapeString>\</escapeString><nonFilteredFileExtensions><nonFilteredFileExtension>png</nonFilteredFileExtension></nonFilteredFileExtensions><addDefaultExcludes>false</addDefaultExcludes></configuration>`
    (parse with `Xpp3DomBuilder.build(new StringReader(...))`) → each getter returns that value.
  - `executionConfigurationOverridesPluginConfiguration`: a `MavenProject` with a
    `maven-resources-plugin` whose plugin config sets `encoding` `A` and whose
    `default-resources` execution sets `encoding` `B` → `effectiveResourcesConfiguration`
    yields `B`.
- [ ] **Step 3: Run** `./mvnw -B test -DskipAssembly -pl tools/struts-maven-plugin -am > <scratchpad>/watch-build.log 2>&1; echo $?`
  (log outside the repo — a log in the root fails RAT) — expect compile failure / test failures.
- [ ] **Step 4: Implement** `ResourcesPluginSettings`. Merge with
  `Xpp3Dom.mergeXpp3Dom(execution, plugin)`.
- [ ] **Step 5: Run the tests** — expect PASS, and the full module build (`verify`) green
  including RAT.
- [ ] **Step 6: Commit** `WW-5864 build(tools): add struts-maven-plugin module skeleton`.

### Task 2: Scanning and the debounced watcher loop

**Files:**
- Create: `.../watch/WatchedRoot.java`, `.../watch/FileStamp.java`, `.../watch/SourceFile.java`,
  `.../watch/ResourceScanner.java`, `.../watch/ResourcePusher.java`,
  `.../watch/ResourceWatcher.java`
- Test: `.../watch/ResourceScannerTest.java`, `.../watch/ResourceWatcherTest.java`

**Interfaces:**
- Produces: `WatchedRoot` — `Path directory`, `List<String> includes`,
  `List<String> excludes`, `String targetPath` (nullable), `boolean filtering`; built from a
  Maven `Resource` with `static WatchedRoot of(Resource resource, Path basedir)`.
- Produces: `FileStamp(long lastModified, long size)` with value equality.
- Produces: `ResourceScanner` — `ResourceScanner(List<WatchedRoot> roots, boolean
  addDefaultExcludes)`, `Map<SourceFile, FileStamp> scan()`, where
  `SourceFile(WatchedRoot root, String relativePath)` has value equality. Uses
  `DirectoryScanner` with the root's includes (default `**/**`) and excludes; a missing
  root directory yields no entries.
- Produces: `ResourcePusher` interface — `void push(SourceFile file) throws IOException`,
  `void delete(SourceFile file) throws IOException`.
- Produces: `ResourceWatcher` — `ResourceWatcher(ResourceScanner scanner, ResourcePusher
  pusher, List<Path> filterFiles, Consumer<String> info, BiConsumer<String, Exception> error)`;
  `void start()` (takes the baseline scan), `void pollOnce()`, `void run(Duration interval,
  BooleanSupplier keepRunning)`.

Debounce rule (not determined by the signatures): a path is acted on only when two
consecutive scans agree on it after it differed from the last acted-on state — present with
the same stamp in both → `push`; absent in both → `delete`. A filter-file stamp change, once
stable across two scans, pushes every file whose root has `filtering == true`.

- [ ] **Step 1: Write the failing tests.** `ResourceScannerTest` (temp dirs via
  `TemporaryFolder`): `listsFilesHonouringIncludesAndExcludes`,
  `skipsDefaultExcludesWhenEnabled` (`.DS_Store`/`*~` ignored),
  `missingRootDirectoryYieldsNoFiles`. `ResourceWatcherTest` with a recording fake pusher and a
  fake scanner fed a list of scan results:
  - `changeIsPushedOnceAfterTwoStableScans` → after scans `{a:1}`,`{a:2}`,`{a:2}`,`{a:2}`:
    pushes `[a]`.
  - `deletionIsMirroredAfterTwoStableScans` → `{a:1}`,`{}`,`{}`: deletes `[a]`.
  - `deleteFollowedByRecreateBeforeStableIsAPushNotADelete` → `{a:1}`,`{}`,`{a:2}`,`{a:2}`:
    deletes `[]`, pushes `[a]`.
  - `unchangedFilesAreNeverPushed` → `{a:1}`,`{a:1}`,`{a:1}`: pushes `[]`.
  - `filterFileChangeRepushesFilteredResourcesOnly` → filtered root file `f`, unfiltered `u`,
    filter-file stamp changes and stabilises: pushes `[f]`.
  - `pusherFailureIsReportedAndWatchingContinues` → pusher throws on `a`, then `b` changes:
    `error` called once for `a`, `b` still pushed.
  - `runStopsWhenKeepRunningTurnsFalse` → `run(Duration.ofMillis(10), ...)` returns.
- [ ] **Step 2: Run** — expect FAIL (classes missing).
- [ ] **Step 3: Implement** the five types. `ResourceScanner` should be non-final with
  `scan()` overridable, so tests can feed scans.
- [ ] **Step 4: Run** — expect PASS.
- [ ] **Step 5: Commit** `WW-5864 feat(tools): scan resource roots and debounce changes`.

### Task 3: Filtering pusher

**Files:**
- Create: `.../watch/FilteringResourcePusher.java`
- Test: `.../watch/FilteringResourcePusherTest.java`

**Interfaces:**
- Consumes: `ResourcePusher`, `SourceFile`, `WatchedRoot` (Task 2); `ResourcesPluginSettings`
  (Task 1).
- Produces: `FilteringResourcePusher(MavenResourcesFiltering filtering, MavenProject project,
  MavenSession session, ResourcesPluginSettings settings, Path outputDirectory)`, plus
  `Path outputPathOf(SourceFile file)` = `outputDirectory[/targetPath]/relativePath`.

- [ ] **Step 1: Write the failing tests** (use `DefaultMavenResourcesFiltering` wired by hand
  or via a Sisu/`PlexusTestCase`-free construction; temp dirs):
  - `pushCopiesUnfilteredFile` → bytes equal.
  - `pushFiltersPlaceholdersFromProjectProperties` → source `name=${greeting}`, project
    property `greeting=hi` → output `name=hi`.
  - `pushHonoursCustomDelimiter` → settings delimiter `@`, source `name=@greeting@` → `name=hi`.
  - `pushHonoursTargetPath` → root `targetPath` `conf` → output at `conf/a.xml`.
  - `pushCreatesMissingParentDirectories` → `com/acme/FooAction.properties` lands.
  - `pushAfterOutputDirectoryDeletedRecreatesIt`.
  - `binaryFileInFilteredResourceIsCopiedUnchanged` → `nonFilteredFileExtensions` `png`,
    a PNG with `${` bytes stays byte-identical.
  - `deleteRemovesOutputFile` and `deleteOfMissingOutputIsANoOp`.
- [ ] **Step 2: Run** — expect FAIL.
- [ ] **Step 3: Implement.** For `push`, build a one-file `Resource` (directory = root,
  `includes` = the relative path, `targetPath`, `filtering`) and call
  `MavenResourcesFiltering.filterResources(MavenResourcesExecution)` with the settings
  applied (encoding, propertiesEncoding, delimiters, useDefaultDelimiters, escapeString,
  nonFilteredFileExtensions, `build.filters`, overwrite = true).
- [ ] **Step 4: Run** — expect PASS.
- [ ] **Step 5: Commit** `WW-5864 feat(tools): push changed resources through maven-filtering`.

### Task 4: Struts reload-settings inspector

**Files:**
- Create: `.../watch/StrutsReloadInspector.java`
- Test: `.../watch/StrutsReloadInspectorTest.java`

**Interfaces:**
- Produces: `StrutsReloadInspector(Path outputDirectory)`, `List<String> warnings()`.

Rule (mirrors `StrutsBeanSelectionProvider#switchDevMode`): read `<constant name value>` from
`outputDirectory/struts.xml` and keys from `outputDirectory/struts.properties` (properties win
on conflict). Effective `struts.configuration.xml.reload` / `struts.i18n.reload` = explicit
value if present, else the devMode value. Warn per switch that is effectively not `true`.
Missing files, parse errors and absent keys produce no exception; when neither file sets
`struts.devMode`, emit one soft warning that devMode was not found (it may be set elsewhere).

- [ ] **Step 1: Write the failing tests:**
  - `devModeTrueNoWarnings`.
  - `devModeFalseWarnsForBothReloadSwitches` → two warnings naming
    `struts.configuration.xml.reload` and `struts.i18n.reload`.
  - `explicitFalseOverridesDevMode` → devMode `true`, `struts.i18n.reload` `false` → one
    warning naming `struts.i18n.reload`.
  - `propertiesFileOverridesXml`.
  - `devModeNotFoundGivesSoftWarning`.
  - `doesNotFetchExternalDtd` → `struts.xml` with
    `<!DOCTYPE struts PUBLIC "x" "http://127.0.0.1:1/never.dtd">` parses and yields the
    constants (no network).
  - `malformedXmlProducesNoException`.
- [ ] **Step 2: Run** — expect FAIL.
- [ ] **Step 3: Implement** with `DocumentBuilderFactory`: disable
  `http://apache.org/xml/features/nonvalidating/load-external-dtd`, external general and
  parameter entities, and XInclude; set `setExpandEntityReferences(false)`.
- [ ] **Step 4: Run** — expect PASS.
- [ ] **Step 5: Commit** `WW-5864 feat(tools): warn when Struts reload switches are off`.

### Task 5: `WatchMojo`

**Files:**
- Create: `.../watch/WatchMojo.java`
- Test: `.../watch/WatchMojoTest.java`

**Interfaces:**
- Consumes: everything above.
- Produces: `@Mojo(name = "watch", defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
  threadSafe = true)` with parameters:
  - `pollInterval` (`long`, ms, default `500`, property `struts.watch.pollInterval`)
  - `background` (`boolean`, default `false`, property `struts.watch.background`)
  - `enabled` (`boolean`, default `false`, property `struts.watch`) — consulted only when
    `background` is true
  - `timeout` (`long`, seconds, default `0` = until stopped, property `struts.watch.timeout`)
    — blocking mode stops after it; used by scripted runs and the IT
  - injected `MavenProject`, `MavenSession`, `MavenResourcesFiltering`.
- Produces: package-private `boolean shouldRun()` and `void checkSingleModule()` for tests.

Behaviour: `background && !enabled` → log one debug line, return. Otherwise
`checkSingleModule()`; log inspector warnings at WARN; build roots from
`project.getResources()`; at start push every file whose output is missing or older than its
source; then blocking → `watcher.run(...)` on the calling thread until Ctrl+C or `timeout`;
background → start a daemon thread named `struts-watch` running `watcher.run(...)` and
return. Log one INFO line per push: `pushed <relative> -> <output path relative to basedir>`.

- [ ] **Step 1: Write the failing tests:**
  - `backgroundWithoutEnabledDoesNotRun` → `shouldRun()` false.
  - `backgroundWithEnabledRuns` and `blockingAlwaysRuns`.
  - `multiModuleSessionFailsWithGuidance` → `checkSingleModule()` throws
    `MojoFailureException` whose message contains `-pl <module>`.
  - `staleOutputsArePushedAtStart` → source newer than output gets pushed, up-to-date one not.
- [ ] **Step 2: Run** — expect FAIL.
- [ ] **Step 3: Implement.**
- [ ] **Step 4: Run** — expect PASS; `./mvnw -B verify -DskipAssembly -pl tools/struts-maven-plugin -am` green, and the generated `struts:help` lists the four parameters.
- [ ] **Step 5: Commit** `WW-5864 feat(tools): add the struts:watch goal`.

### Task 6: Invoker integration test

**Files:**
- Modify: `tools/struts-maven-plugin/pom.xml` (`maven-invoker-plugin` 3.9.1: `install` +
  `integration-test` + `verify`, `cloneProjectsTo` `target/it`, `postBuildHookScript`
  `verify`, `preBuildHookScript` `prebuild`)
- Create: `tools/struts-maven-plugin/src/it/watch-pushes-change/{pom.xml,invoker.properties,prebuild.groovy,verify.groovy,src/main/resources/struts.xml,src/main/resources/messages.properties}`

- [ ] **Step 1: Fixture.** Pom declares the plugin at `@project.version@`, a filtered resource
  directory, and property `greeting=hi`. `messages.properties` is `msg=${greeting}-v1`.
  `invoker.properties`: `invoker.goals = process-resources ${project.groupId}:${project.artifactId}:${project.version}:watch -Dstruts.watch.timeout=20`.
- [ ] **Step 2: `prebuild.groovy`** starts a daemon thread that sleeps 8 s, then rewrites
  `src/main/resources/messages.properties` to `msg=${greeting}-v2` and deletes nothing.
- [ ] **Step 3: `verify.groovy`** asserts `target/classes/messages.properties` contains
  `msg=hi-v2` and `build.log` contains `pushed messages.properties`.
- [ ] **Step 4: Run** `./mvnw -B verify -DskipAssembly -pl tools/struts-maven-plugin -am` —
  expect the IT to pass. Then temporarily break the push (return early in
  `FilteringResourcePusher.push`) and confirm the IT fails; revert.
- [ ] **Step 5: Commit** `WW-5864 test(tools): invoker IT for struts:watch`.

### Task 7: Showcase reference setup and Jetty scanner

**Files:**
- Modify: `apps/showcase/pom.xml` (Jetty plugin config; add `struts-maven-plugin` with an
  execution `watch` bound to `process-resources`, `<background>true</background>`)
- Modify: `apps/rest-showcase/pom.xml` (Jetty scanner config only)

- [ ] **Step 1: Find the Jetty 11 scanner setting** that keeps scanning compiled classes but
  ignores resource files in `target/classes`: check `jetty-maven-plugin` 11.0.26's
  `scanClassesPattern` (`<excludes>`). Record the outcome in the commit message. If no such
  exclude exists, set `<scan>0</scan>` instead.
- [ ] **Step 2: Apply** it to both showcase poms and add the bound background execution to
  `apps/showcase/pom.xml`.
- [ ] **Step 3: Prove CI stays inert:** `./mvnw -B package -DskipTests -DskipITs -DskipAssembly -pl apps/showcase -am`
  exits 0 and the log has no `pushed` / `struts-watch` lines.
- [ ] **Step 4: Manual end-to-end check** (temporarily set `struts.devMode=true` in the
  showcase `struts.xml`, do not commit it):
  `./mvnw -B -pl apps/showcase -DskipAssembly -Dstruts.watch jetty:run`, then add a probe
  action to `src/main/resources/struts.xml` and change a key in `globalMessages.properties`.
  Expect: a `pushed` line for each, the new action answering on the next request, the new
  message rendering, and no Jetty `Restarting` line. This is the check that the background
  thread survives `jetty:run`'s forked lifecycle.
- [ ] **Step 5: Commit** `WW-5864 build(showcase): reference struts:watch setup and Jetty scanner`.

### Task 8: Docs

**Files:**
- Create: `tools/struts-maven-plugin/README.md` (RAT excludes `**/README.md`)

- [ ] **Step 1:** README: what the goal does and does not do (Java classes out of scope),
  the two ways to run it (`mvn struts:watch -pl <module>` next to the server;
  `mvn jetty:run -Dstruts.watch` with the bound execution), the four parameters, the Jetty
  scanner setting from Task 7, and the single-module limitation.
- [ ] **Step 2: Commit** `WW-5864 docs(tools): struts-maven-plugin usage`. The struts-site
  page is a follow-up PR in that repo.
