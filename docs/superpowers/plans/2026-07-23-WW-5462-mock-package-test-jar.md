# WW-5462 Mock Package Test-Jar Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove the test-only `org.apache.struts2.mock` package from the published `struts2-core` jar by moving it to test sources and sharing it with plugin tests via a filtered test-jar.

**Architecture:** Core attaches a `maven-jar-plugin` test-jar containing only `org/apache/struts2/mock/**`. Six plugins consume it as a test-scoped `test-jar` dependency. Tasks are ordered so every commit leaves the full reactor green: pom plumbing first (mocks still in main sources), the move last.

**Tech Stack:** Java 17, Maven multi-module reactor, maven-jar-plugin `test-jar` goal.

**Spec:** `docs/superpowers/specs/2026-07-23-WW-5462-mock-package-test-jar-design.md`

## Global Constraints

- Branch: `WW-5462-mock-package-cleanup` (already exists; work there, never on `main`)
- Every commit message starts with `WW-5462` followed by conventional-commit type (see `~/.claude/commit_guideline.md`)
- Package name `org.apache.struts2.mock` never changes — no import edits anywhere
- The bom (`bom/pom.xml`) must NOT gain a test-jar entry
- Test-jar includes filter is exactly `org/apache/struts2/mock/**`
- No new files other than pom edits; this is a move/delete change

## Known Risk (check in Task 2, Step 3)

When the reactor runs without packaging (`mvn test`), Maven's `ReactorReader` resolves the
test-jar dependency to core's whole `target/test-classes` directory — the includes filter
applies only when the jar is physically packaged. Core's test resources (`struts.xml`,
`struts.properties`, `struts-tests-default.xml`, `log4j2.xml`) then appear on plugin test
classpaths. Plugins `json`, `rest`, `xslt`, `jasperreports` have no own root `struts.xml`
to shadow core's. If Task 2's verification shows plugin test failures caused by leaked core
test resources, STOP and report back — the fallback options (renaming core's clashing test
resources, or copying mocks into plugins instead of the test-jar) need a human decision.

---

### Task 1: Attach filtered test-jar in core

**Files:**
- Modify: `core/pom.xml` (the `<build><plugins>` section starting at line 39)

**Interfaces:**
- Consumes: nothing from other tasks
- Produces: attached artifact `org.apache.struts:struts2-core:test-jar` (classifier `tests`), containing only `org/apache/struts2/mock/**`. Task 2 depends on these exact coordinates.

Background for the implementer: the root `pom.xml` (line ~467) configures `maven-jar-plugin`
with `<archive><manifestFile>` pointing at the OSGi manifest that the felix bundle plugin
generates for the **main** jar. The test-jar must not inherit that manifest, hence the
`<archive combine.self="override"/>` below, which resets the inherited `<archive>` config
so the test-jar gets a default manifest.

- [ ] **Step 1: Add the test-jar execution**

In `core/pom.xml`, inside `<build><plugins>`, add as a new `<plugin>` entry directly after the closing tag of the `maven-surefire-plugin` entry (after line 68):

```xml
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <executions>
                    <execution>
                        <id>mock-test-jar</id>
                        <goals>
                            <goal>test-jar</goal>
                        </goals>
                        <configuration>
                            <archive combine.self="override"/>
                            <includes>
                                <include>org/apache/struts2/mock/**</include>
                            </includes>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```

- [ ] **Step 2: Package core and verify the build passes**

Run:
```bash
mvn package -DskipTests -DskipAssembly -pl core
```
Expected: `BUILD SUCCESS`, and the log contains `maven-jar-plugin:...:test-jar (mock-test-jar) @ struts2-core` followed by `Building jar: .../core/target/struts2-core-7.2.0-SNAPSHOT-tests.jar`.

- [ ] **Step 3: Verify test-jar contents**

Run:
```bash
unzip -l core/target/struts2-core-*-tests.jar
```
Expected: entries only under `META-INF/` and `org/apache/struts2/mock/`. At this point the mock package in test sources holds exactly 3 classes, so the class list is:
`DummyTextProvider.class`, `InjectableAction.class`, `MockLazyInterceptor.class` (plus any inner/anonymous class files of these). NO other packages, NO `struts.xml`, NO `*.properties`.

- [ ] **Step 4: Verify the test-jar manifest is not the OSGi bundle manifest**

Run:
```bash
unzip -p core/target/struts2-core-*-tests.jar META-INF/MANIFEST.MF | head -20
```
Expected: a plain default manifest (`Manifest-Version`, `Created-By`, `Build-Jdk-Spec`). It must NOT contain `Export-Package` or `Bundle-SymbolicName`.

- [ ] **Step 5: Commit**

```bash
git add core/pom.xml
git commit -m "WW-5462 build(core): attach test-jar with the mock package"
```

---

### Task 2: Wire the six consuming plugins

**Files:**
- Modify: `plugins/json/pom.xml`
- Modify: `plugins/rest/pom.xml`
- Modify: `plugins/spring/pom.xml`
- Modify: `plugins/xslt/pom.xml`
- Modify: `plugins/jasperreports/pom.xml`
- Modify: `plugins/jasperreports7/pom.xml`

**Interfaces:**
- Consumes: the `struts2-core` test-jar artifact attached in Task 1
- Produces: plugin test classpaths that resolve `org.apache.struts2.mock` from the test-jar; Task 3 relies on this to move the mocks without breaking plugin builds

- [ ] **Step 1: Add the test-jar dependency to all six plugin poms**

In each of the six poms listed above, inside the `<dependencies>` section, add this block immediately before the first `<scope>test</scope>` dependency (each pom has a `<!-- Test dependencies -->`-style boundary; json's is at line 63 — place it right after that comment where present, otherwise before the junit dependency):

```xml
        <dependency>
            <groupId>org.apache.struts</groupId>
            <artifactId>struts2-core</artifactId>
            <version>${project.version}</version>
            <type>test-jar</type>
            <scope>test</scope>
        </dependency>
```

The block is identical in all six poms. `${project.version}` is correct because all modules share the reactor version; the bom must not be touched.

- [ ] **Step 2: Verify the six plugins build and pass with the new dependency**

Run:
```bash
mvn test -DskipAssembly -am -pl plugins/json,plugins/rest,plugins/spring,plugins/xslt,plugins/jasperreports,plugins/jasperreports7
```
Expected: `BUILD SUCCESS`; all plugin tests pass. The mocks still resolve from core's main classes at this point, so this proves only that the added dependency is well-formed and harmless.

- [ ] **Step 3: Check for the known risk — core test-resource leakage**

The Step 2 run used unpackaged reactor resolution, i.e. plugin test classpaths contained core's whole `target/test-classes` (see "Known Risk" above). Examine the Step 2 output:
- All tests pass → risk is benign, continue.
- Any plugin test failure that does not occur on `main` → STOP. Re-run that plugin's tests on `main` to confirm the regression is caused by this change, then report back with the failing tests and the leaked-resource explanation. Do not improvise a fix.

- [ ] **Step 4: Commit**

```bash
git add plugins/json/pom.xml plugins/rest/pom.xml plugins/spring/pom.xml plugins/xslt/pom.xml plugins/jasperreports/pom.xml plugins/jasperreports7/pom.xml
git commit -m "WW-5462 build(plugins): consume struts2-core test-jar in tests"
```

---

### Task 3: Delete MockContainer, move the five remaining mocks

**Files:**
- Delete: `core/src/main/java/org/apache/struts2/mock/MockContainer.java`
- Move: `core/src/main/java/org/apache/struts2/mock/MockActionInvocation.java` → `core/src/test/java/org/apache/struts2/mock/MockActionInvocation.java`
- Move: `core/src/main/java/org/apache/struts2/mock/MockActionProxy.java` → `core/src/test/java/org/apache/struts2/mock/MockActionProxy.java`
- Move: `core/src/main/java/org/apache/struts2/mock/MockInterceptor.java` → `core/src/test/java/org/apache/struts2/mock/MockInterceptor.java`
- Move: `core/src/main/java/org/apache/struts2/mock/MockObjectTypeDeterminer.java` → `core/src/test/java/org/apache/struts2/mock/MockObjectTypeDeterminer.java`
- Move: `core/src/main/java/org/apache/struts2/mock/MockResult.java` → `core/src/test/java/org/apache/struts2/mock/MockResult.java`

**Interfaces:**
- Consumes: test-jar packaging (Task 1) and plugin wiring (Task 2) — both must be committed first, or this task breaks plugin builds
- Produces: `struts2-core` main jar without `org.apache.struts2.mock`; test-jar now carries all 8 mock classes

- [ ] **Step 1: Delete the dead class and move the five live ones**

```bash
git rm core/src/main/java/org/apache/struts2/mock/MockContainer.java
git mv core/src/main/java/org/apache/struts2/mock/MockActionInvocation.java core/src/test/java/org/apache/struts2/mock/
git mv core/src/main/java/org/apache/struts2/mock/MockActionProxy.java core/src/test/java/org/apache/struts2/mock/
git mv core/src/main/java/org/apache/struts2/mock/MockInterceptor.java core/src/test/java/org/apache/struts2/mock/
git mv core/src/main/java/org/apache/struts2/mock/MockObjectTypeDeterminer.java core/src/test/java/org/apache/struts2/mock/
git mv core/src/main/java/org/apache/struts2/mock/MockResult.java core/src/test/java/org/apache/struts2/mock/
```

Then confirm the main-side package directory is gone:
```bash
ls core/src/main/java/org/apache/struts2/mock/ 2>&1
```
Expected: `No such file or directory`.

No source edits: the package declaration stays `org.apache.struts2.mock`, so no imports change anywhere.

- [ ] **Step 2: Run core tests**

Run:
```bash
mvn test -DskipAssembly -pl core
```
Expected: `BUILD SUCCESS`, same test count as on `main` (the ~50 core test classes using the mocks compile against test sources now).

- [ ] **Step 3: Verify jar contents after the move**

Run:
```bash
mvn package -DskipTests -DskipAssembly -pl core
unzip -l core/target/struts2-core-7.2.0-SNAPSHOT.jar | grep 'struts2/mock' ; echo "main-jar grep exit: $?"
unzip -l core/target/struts2-core-7.2.0-SNAPSHOT-tests.jar | grep -c 'struts2/mock/.*\.class'
```
Expected: main-jar grep exit code `1` (no mock package in the published jar); tests-jar contains `8` classes — the 5 moved + `DummyTextProvider`, `InjectableAction`, `MockLazyInterceptor` (count may exceed 8 only if inner-class files exist; there must be exactly 8 top-level `*.class` names).

- [ ] **Step 4: Verify plugin tests still pass against the moved mocks**

Run:
```bash
mvn test -DskipAssembly -am -pl plugins/json,plugins/rest,plugins/spring,plugins/xslt,plugins/jasperreports,plugins/jasperreports7
```
Expected: `BUILD SUCCESS`, all tests pass — mocks now come from core's test classpath contribution.

- [ ] **Step 5: Commit**

```bash
git add -A core/src
git commit -m "WW-5462 refactor(core): move mock package to test sources, drop unused MockContainer

BREAKING CHANGE: org.apache.struts2.mock is no longer part of the
published struts2-core jar. Downstream tests should use
struts2-junit-plugin, Mockito, or a local copy of the needed mock."
```

---

### Task 4: Remove spring's duplicate mock and run full verification

**Files:**
- Delete: `plugins/spring/src/test/java/org/apache/struts2/mock/DummyTextProvider.java`

**Interfaces:**
- Consumes: test-jar wiring from Task 2 (spring resolves `DummyTextProvider` from core's test-jar instead of its local copy)
- Produces: final verified state of the branch

- [ ] **Step 1: Delete the duplicate**

```bash
git rm plugins/spring/src/test/java/org/apache/struts2/mock/DummyTextProvider.java
```

Same package and class name ship in the core test-jar, so `plugins/spring/src/test/java/org/apache/struts2/spring/SpringObjectFactoryTest.java` (its only consumer, import at line 39) needs no edit.

- [ ] **Step 2: Run spring plugin tests**

Run:
```bash
mvn test -DskipAssembly -am -pl plugins/spring
```
Expected: `BUILD SUCCESS`, all tests pass.

- [ ] **Step 3: Full reactor test run**

Run:
```bash
mvn test -DskipAssembly
```
Expected: `BUILD SUCCESS` — every module compiles and passes.

- [ ] **Step 4: Jakarta EE 11 profile sanity check**

Run:
```bash
mvn clean install -Pjakartaee11 -DskipAssembly
```
Expected: `BUILD SUCCESS`. (This also re-verifies the packaged-jar path: with `install`, plugins resolve the real filtered test-jar, not the test-classes directory.)

- [ ] **Step 5: Commit**

```bash
git add -A plugins/spring
git commit -m "WW-5462 test(spring): use DummyTextProvider from core test-jar"
```

- [ ] **Step 6: Remind the user about the release-notes migration note**

Tell the user (do not attempt to do this yourself — it is a Jira/Confluence action, not a repo change):
> The 7.3.0 release notes need a migration note: `org.apache.struts2.mock` was removed from the struts2-core jar (WW-5462); downstream tests should switch to struts2-junit-plugin, Mockito, or copy the needed mock class.
