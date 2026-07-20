# WW-5620 — Standardize logging on Log4j2

- **Jira:** [WW-5620](https://issues.apache.org/jira/browse/WW-5620)
- **Type:** Improvement (refactor / dependency cleanup)
- **Fix version:** 7.3.0
- **Components:** Core, Plugin - Tiles
- **Date:** 2026-07-20

## Goal

Log4j2 is the project's standard logging layer. Three source files still use
legacy logging frameworks. Migrate them to Log4j2 and remove the now-dead
first-party SLF4J dependency declarations that only existed to support them.

This is a pure logging-standardization refactor: **no functional or security
behavior changes.** All three files are vendored (files 1 and 3 from Google
Guice, file 2 from Apache Tiles), so there is no upstream-sync concern.

## Scope

### In scope
1. Migrate `FinalizableReferenceQueue.java` (core) off `java.util.logging`.
2. Migrate `AbstractDefaultToStringRenderable.java` (tiles) off SLF4J.
3. Remove the injectable `java.util.logging.Logger` DI factory from
   `ContainerBuilder.java` (core).
4. Remove the first-party SLF4J dependency declarations that become dead after
   step 2, gated on a build/dependency-tree verification.

### Out of scope
- The commons-logging → Log4j2 and SLF4J → Log4j2 **runtime bridges**
  (`log4j-jcl`, `log4j-slf4j-impl`, `commons-logging`). These route
  *transitive third-party* library logging into Log4j2 and are unrelated to
  first-party code. They stay.
- Any broader logging refactor beyond these three files.

## File-level changes

### 1. `core/src/main/java/org/apache/struts2/inject/util/FinalizableReferenceQueue.java`

Faithful class-own logger swap, adopting the codebase convention (`private
static final Logger LOG = LogManager.getLogger(<Class>.class);` — see
`ActionSupport`, `ObjectFactory`, `DefaultActionInvocation`).

- Remove imports `java.util.logging.Level`, `java.util.logging.Logger`.
- Add imports `org.apache.logging.log4j.LogManager`,
  `org.apache.logging.log4j.Logger`.
- Replace
  `private static final Logger logger = Logger.getLogger(FinalizableReferenceQueue.class.getName());`
  with
  `private static final Logger LOG = LogManager.getLogger(FinalizableReferenceQueue.class);`
- Replace
  `logger.log(Level.SEVERE, "Error cleaning up after reference.", t);`
  with
  `LOG.error("Error cleaning up after reference.", t);`

### 2. `plugins/tiles/src/main/java/org/apache/tiles/velocity/template/AbstractDefaultToStringRenderable.java`

Faithful SLF4J swap. The Log4j2 `Logger` error API is call-compatible, so only
imports and the factory call change.

- Remove imports `org.slf4j.Logger`, `org.slf4j.LoggerFactory`.
- Add imports `org.apache.logging.log4j.LogManager`,
  `org.apache.logging.log4j.Logger`.
- Replace `private final Logger log = LoggerFactory.getLogger(getClass());`
  with `private final Logger log = LogManager.getLogger(getClass());`
- `log.error("Error when closing a StringWriter, the impossible happened!", e);`
  is unchanged.
- No new pom dependency: `log4j-api` reaches the tiles plugin transitively via
  `struts2-core`. Confirm at build time.

### 3. `core/src/main/java/org/apache/struts2/inject/ContainerBuilder.java`

Here `java.util.logging.Logger` is **not** a class-own logger — it is
registered as an injectable DI type (a legacy Guice feature: consumers could
`@Inject` a `Logger` scoped to their declaring class). A repo-wide search found
**no code that injects it**. Per decision, remove the dead feature rather than
port it to Log4j2 (which has no `getAnonymousLogger()` equivalent for the
`member == null` fallback and would change the DI contract).

- Remove the `LOGGER_FACTORY` `InternalFactory<Logger>` constant.
- Remove its registration:
  `factories.put(Key.newInstance(Logger.class, Container.DEFAULT_NAME), LOGGER_FACTORY);`
  and the preceding comment.
- Remove now-unused imports `java.util.logging.Logger` and
  `java.lang.reflect.Member` (`Member` is used only inside the removed factory).
- Update the class Javadoc: drop the
  "Injects the `{@link Logger}` for the injected member's declaring class"
  bullet, leaving the `Container` bullet.

### 4. Dependency cleanup (gated on verification)

Evidence for removal:
- The tiles file (step 2) is the only main-source SLF4J user in the repo.
- No test source uses SLF4J; there is no `simplelogger.properties`.
- core's `slf4j-api` is `optional` (never part of the transitive API contract)
  and dates to the original xwork merge.

Remove, after the verification gate below:
- `core/pom.xml` — the optional `slf4j-api` dependency and the test-scoped
  `slf4j-simple` dependency (the `<!-- SLF4J support -->` block).
- `parent/pom.xml` — the `dependencyManagement` entries for `slf4j-api` and
  `slf4j-simple`.
- root `pom.xml` — the `<slf4j.version>` property.

`java.util.logging` is JDK built-in — nothing to remove for the two core files.

## Verification

1. **Dependency-tree gate (do before deleting pom entries):**
   run `mvn dependency:tree` for `core` and `plugins/tiles`. Confirm no
   first-party module *requires* Struts to expose SLF4J. If something genuinely
   needs it, keep that minimum and record the reason here.
2. **Compile + test:**
   - `mvn test -DskipAssembly -pl core` (core tests are JUnit 4 in this repo)
   - `mvn test -DskipAssembly -pl plugins/tiles`
3. **Grep confirmation:** no source references the injectable `Logger` type,
   the removed `logger` field, or `org.slf4j` after the change.

## Testing strategy

No new tests are required:
- Files 1 and 2 are behavior-preserving logger swaps.
- File 3 removes dead, untested code.

Existing core and tiles test suites must stay green.

## Risks

- **Low.** The only non-mechanical change is removing the injectable `Logger`
  factory in `ContainerBuilder`, which is unused internally. A third-party
  extension that injected `java.util.logging.Logger` from the Struts container
  would lose that binding; this is acceptable for the 7.3.0 minor and called out
  here.
- The dependency-tree gate protects against an unexpected transitive reliance on
  the SLF4J declarations.
