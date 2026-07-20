# WW-5620 Log4j2 Logging Standardization Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Migrate the three remaining legacy-logging source files to Log4j2 and remove the now-dead first-party SLF4J dependency declarations.

**Architecture:** Three independent, mostly mechanical source edits (two class-own logger swaps, one dead-DI-feature removal), followed by a pom cleanup gated on a `mvn dependency:tree` check. No functional or security behavior changes.

**Tech Stack:** Java, Log4j2 (`org.apache.logging.log4j`), Maven. Core tests are JUnit 4 + AssertJ + Mockito in this repo.

## Global Constraints

- Fix version: 7.3.0. Target ticket: WW-5620. Every commit message MUST be prefixed `WW-5620`.
- Logger convention: `private static final Logger LOG = LogManager.getLogger(<Class>.class);` for static class loggers (see `ActionSupport`, `ObjectFactory`). Preserve the existing field style where a file already uses an instance logger.
- No new pom dependency may be added. `log4j-api` is already available (directly in core, transitively in tiles via `struts2-core`).
- Do NOT touch the runtime bridges `log4j-jcl`, `log4j-slf4j-impl`, or `commons-logging` — they route third-party logging and are out of scope.
- Build/test commands: `mvn test -DskipAssembly -pl core` and `mvn test -DskipAssembly -pl plugins/tiles`.
- Reference spec: `docs/superpowers/specs/2026-07-20-WW-5620-log4j2-logging-standardization-design.md`.

---

## File Structure

- Modify: `core/src/main/java/org/apache/struts2/inject/util/FinalizableReferenceQueue.java` — swap `java.util.logging` → Log4j2.
- Modify: `plugins/tiles/src/main/java/org/apache/tiles/velocity/template/AbstractDefaultToStringRenderable.java` — swap SLF4J → Log4j2.
- Modify: `core/src/main/java/org/apache/struts2/inject/ContainerBuilder.java` — remove the injectable `java.util.logging.Logger` DI factory.
- Modify: `core/pom.xml`, `parent/pom.xml`, `pom.xml` — remove dead first-party SLF4J declarations.

No test files are created: files 1–2 are behavior-preserving; file 3 removes dead, untested code. Verification is via existing suites, `dependency:tree`, and grep.

---

### Task 1: Migrate `FinalizableReferenceQueue` to Log4j2

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/inject/util/FinalizableReferenceQueue.java`

**Interfaces:**
- Consumes: nothing from other tasks.
- Produces: nothing consumed by other tasks (internal logger only).

- [ ] **Step 1: Confirm current state**

Run: `grep -n "logging\|logger\|Level" core/src/main/java/org/apache/struts2/inject/util/FinalizableReferenceQueue.java`
Expected: lines showing `import java.util.logging.Level;`, `import java.util.logging.Logger;`, the `logger` field (`Logger.getLogger(...getName())`), and `logger.log(Level.SEVERE, "Error cleaning up after reference.", t);`.

- [ ] **Step 2: Replace the imports**

Replace:
```java
import java.util.logging.Level;
import java.util.logging.Logger;
```
with:
```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
```
(Keep these in import-order position consistent with the file; the surrounding `java.lang.ref.*` / `java.util.concurrent.*` imports stay.)

- [ ] **Step 3: Replace the logger field**

Replace:
```java
  private static final Logger logger =
      Logger.getLogger(FinalizableReferenceQueue.class.getName());
```
with:
```java
  private static final Logger LOG = LogManager.getLogger(FinalizableReferenceQueue.class);
```

- [ ] **Step 4: Replace the log call**

Replace:
```java
    logger.log(Level.SEVERE, "Error cleaning up after reference.", t);
```
with:
```java
    LOG.error("Error cleaning up after reference.", t);
```

- [ ] **Step 5: Verify no leftover legacy references**

Run: `grep -n "java.util.logging\|Level\.\|logger\b" core/src/main/java/org/apache/struts2/inject/util/FinalizableReferenceQueue.java`
Expected: no matches.

- [ ] **Step 6: Compile core**

Run: `mvn -q -o compile -pl core -DskipAssembly` (drop `-o` if the local repo needs downloads)
Expected: BUILD SUCCESS.

- [ ] **Step 7: Commit**

```bash
git add core/src/main/java/org/apache/struts2/inject/util/FinalizableReferenceQueue.java
git commit -m "WW-5620 Migrate FinalizableReferenceQueue to Log4j2

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 2: Migrate `AbstractDefaultToStringRenderable` to Log4j2

**Files:**
- Modify: `plugins/tiles/src/main/java/org/apache/tiles/velocity/template/AbstractDefaultToStringRenderable.java`

**Interfaces:**
- Consumes: nothing from other tasks.
- Produces: nothing consumed by other tasks (internal instance logger only).

- [ ] **Step 1: Confirm current state**

Run: `grep -n "slf4j\|Logger\|log\." plugins/tiles/src/main/java/org/apache/tiles/velocity/template/AbstractDefaultToStringRenderable.java`
Expected: `import org.slf4j.Logger;`, `import org.slf4j.LoggerFactory;`, `private final Logger log = LoggerFactory.getLogger(getClass());`, and `log.error("Error when closing a StringWriter, the impossible happened!", e);`.

- [ ] **Step 2: Replace the imports**

Replace:
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
```
with:
```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
```
(Keep import ordering consistent with the file; `org.apache.logging...` sorts near the other `org.apache.*` imports.)

- [ ] **Step 3: Replace the logger field factory call**

Replace:
```java
    private final Logger log = LoggerFactory.getLogger(getClass());
```
with:
```java
    private final Logger log = LogManager.getLogger(getClass());
```
(The `log.error(...)` call is unchanged — the Log4j2 `Logger.error(String, Throwable)` signature is call-compatible.)

- [ ] **Step 4: Verify no leftover SLF4J references**

Run: `grep -n "slf4j\|LoggerFactory" plugins/tiles/src/main/java/org/apache/tiles/velocity/template/AbstractDefaultToStringRenderable.java`
Expected: no matches.

- [ ] **Step 5: Compile the tiles plugin (confirms log4j-api resolves transitively)**

Run: `mvn -q -o compile -pl plugins/tiles -am -DskipAssembly` (drop `-o` if downloads are needed)
Expected: BUILD SUCCESS. If it fails with `package org.apache.logging.log4j does not exist`, STOP — the transitive assumption is wrong; report back before adding any dependency.

- [ ] **Step 6: Commit**

```bash
git add plugins/tiles/src/main/java/org/apache/tiles/velocity/template/AbstractDefaultToStringRenderable.java
git commit -m "WW-5620 Migrate AbstractDefaultToStringRenderable to Log4j2

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 3: Remove the injectable Logger factory from `ContainerBuilder`

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/inject/ContainerBuilder.java`

**Interfaces:**
- Consumes: nothing from other tasks.
- Produces: removes the DI binding for `java.util.logging.Logger` (verified unused repo-wide).

- [ ] **Step 1: Re-confirm the injectable Logger is unused**

Run: `grep -rn --include="*.java" "java.util.logging.Logger" . | grep -v /target/ | grep -v "ContainerBuilder.java\|FinalizableReferenceQueue.java"`
Expected: no matches (after Task 1, `FinalizableReferenceQueue` no longer appears either). If any consumer appears, STOP and report.

- [ ] **Step 2: Remove the `LOGGER_FACTORY` constant**

Delete this block:
```java
    private static final InternalFactory<Logger> LOGGER_FACTORY =
            new InternalFactory<>() {
                public Logger create(InternalContext context) {
                    Member member = context.getExternalContext().getMember();
                    return member == null ? Logger.getAnonymousLogger()
                            : Logger.getLogger(member.getDeclaringClass().getName());
                }

                @Override
                public Class<? extends Logger> type() {
                    return Logger.class;
                }
            };
```

- [ ] **Step 3: Remove the factory registration in the constructor**

Delete these lines (comment + registration):
```java
        // Inject the logger for the injected member's declaring class.
        factories.put(Key.newInstance(Logger.class, Container.DEFAULT_NAME), LOGGER_FACTORY);
```
Leave the preceding `CONTAINER_FACTORY` registration intact.

- [ ] **Step 4: Remove the now-unused imports**

Delete:
```java
import java.lang.reflect.Member;
```
and
```java
import java.util.logging.Logger;
```

- [ ] **Step 5: Update the class Javadoc**

In the class Javadoc `<ul>`, delete the line:
```java
 *   <li>Injects the {@link Logger} for the injected member's declaring class.
```
Leave the `<li>Injects the current {@link Container}.` bullet.

- [ ] **Step 6: Verify no leftover references**

Run: `grep -n "Logger\|Member\|LOGGER_FACTORY" core/src/main/java/org/apache/struts2/inject/ContainerBuilder.java`
Expected: no matches.

- [ ] **Step 7: Compile core**

Run: `mvn -q -o compile -pl core -DskipAssembly` (drop `-o` if downloads are needed)
Expected: BUILD SUCCESS.

- [ ] **Step 8: Commit**

```bash
git add core/src/main/java/org/apache/struts2/inject/ContainerBuilder.java
git commit -m "WW-5620 Remove unused injectable j.u.l.Logger DI factory from ContainerBuilder

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 4: Remove dead first-party SLF4J dependency declarations

**Files:**
- Modify: `core/pom.xml`
- Modify: `parent/pom.xml`
- Modify: `pom.xml`

**Interfaces:**
- Consumes: Tasks 1–3 must be done first (no first-party source uses SLF4J anymore).
- Produces: nothing consumed by other tasks.

- [ ] **Step 1: Dependency-tree gate (before deleting anything)**

Run: `grep -rln --include="*.java" "org.slf4j" . | grep -v /target/`
Expected: no matches (Task 2 removed the last usage).

Run: `mvn -q -o dependency:tree -pl core,plugins/tiles -Dincludes=org.slf4j 2>&1 | grep -i slf4j` (drop `-o` if downloads are needed)
Expected: any `org.slf4j:*` shown is transitive (an indented child of another artifact), not a direct first-party declaration. If a first-party module *requires* Struts to expose SLF4J for compilation, STOP, keep the minimum needed, and note it in the spec's Verification section before continuing.

- [ ] **Step 2: Remove the SLF4J block from `core/pom.xml`**

Delete:
```xml
        <!-- SLF4J support -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <scope>test</scope>
        </dependency>
```

- [ ] **Step 3: Remove the SLF4J `dependencyManagement` entries from `parent/pom.xml`**

Delete:
```xml
            <dependency>
                <groupId>org.slf4j</groupId>
                <artifactId>slf4j-api</artifactId>
                <version>${slf4j.version}</version>
            </dependency>
            <dependency>
                <groupId>org.slf4j</groupId>
                <artifactId>slf4j-simple</artifactId>
                <version>${slf4j.version}</version>
            </dependency>
```

- [ ] **Step 4: Remove the `slf4j.version` property from root `pom.xml`**

Delete:
```xml
        <slf4j.version>2.0.18</slf4j.version>
```

- [ ] **Step 5: Verify no SLF4J declarations remain**

Run: `grep -rn "slf4j" --include="pom.xml" . | grep -v /target/`
Expected: only bridge artifacts remain — `apps/showcase` `log4j-slf4j-impl` (and its `<name>slf4j</name>` profile entry). No `org.slf4j:slf4j-api`, no `slf4j-simple`, no `slf4j.version`.

- [ ] **Step 6: Build and test core + tiles**

Run: `mvn -o test -DskipAssembly -pl core -am` (drop `-o` if downloads are needed)
Expected: BUILD SUCCESS, tests green.

Run: `mvn -o test -DskipAssembly -pl plugins/tiles -am`
Expected: BUILD SUCCESS, tests green.

If any test fails because it silently relied on `slf4j-simple` as a test logging backend, STOP and report — do not add unrelated logging config to force a pass.

- [ ] **Step 7: Commit**

```bash
git add core/pom.xml parent/pom.xml pom.xml
git commit -m "WW-5620 Remove dead first-party SLF4J dependency declarations

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Self-Review

**Spec coverage:**
- Spec §1 FinalizableReferenceQueue → Task 1. ✓
- Spec §2 AbstractDefaultToStringRenderable → Task 2. ✓
- Spec §3 ContainerBuilder factory removal → Task 3. ✓
- Spec §4 dependency cleanup (core/parent/root poms) → Task 4. ✓
- Spec Verification (dependency-tree gate, compile+test, grep) → Task 4 Step 1, per-task compile steps, grep steps. ✓
- Out-of-scope bridges left untouched → asserted in Global Constraints and Task 4 Step 5. ✓

**Placeholder scan:** No TBD/TODO/"handle edge cases"/vague steps. Every code step shows exact before/after. ✓

**Type consistency:** Logger field named `LOG` (static, Task 1) vs `log` (instance, Task 2) — intentional, matches each file's existing style and the codebase convention; no cross-task symbol sharing. ✓
