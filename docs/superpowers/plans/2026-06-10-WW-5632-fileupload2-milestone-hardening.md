# commons-fileupload2 Milestone Hardening Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make a `commons-fileupload2-core` / `-jakarta-servlet6` version skew impossible in Struts's own build and turn a future runtime `NoSuchMethodError` into a clear, actionable `StrutsException`.

**Architecture:** Three independent changes. (A1) Introduce a single `commons-fileupload2.version` property and manage *both* fileupload artifacts in `parent/pom.xml`. (A2) Activate the dormant `maven-enforcer-plugin` with a fileupload-scoped `bannedDependencies` rule. (B) Add a once-per-JVM reflective API guard in `AbstractMultiPartRequest`.

**Tech Stack:** Maven (multi-module), `maven-enforcer-plugin` 3.6.3, Java 17, JUnit 4 + AssertJ (the `core` module's established test stack), Apache Commons FileUpload 2.0.0-M5.

**Ticket:** [WW-5632](https://issues.apache.org/jira/browse/WW-5632)
**Spec:** `docs/superpowers/specs/2026-06-10-fileupload2-milestone-hardening-design.md`
**Branch:** `WW-5632-fileupload2-milestone-hardening` (already checked out)

---

## File Structure

- `pom.xml` (root) — add the `commons-fileupload2.version` property; change the enforcer rule from `dependencyConvergence` to a scoped `bannedDependencies`; bind the enforcer into the active `<plugins>` section.
- `parent/pom.xml` — reference the new property for `commons-fileupload2-jakarta-servlet6` and add a managed entry for `commons-fileupload2-core`.
- `core/src/main/java/org/apache/struts2/dispatcher/multipart/AbstractMultiPartRequest.java` — add the runtime API guard and call it from `prepareServletFileUpload`.
- `core/src/test/java/org/apache/struts2/dispatcher/multipart/AbstractMultiPartRequestApiCheckTest.java` (new) — unit tests for the guard.

---

## Task 1: Manage both fileupload artifacts via a single version property (A1)

**Files:**
- Modify: `pom.xml:118-119` (properties block)
- Modify: `parent/pom.xml:128-132` (dependencyManagement entry)

- [ ] **Step 1: Add the version property to the root POM**

In `pom.xml`, inside the `<properties>` block, add the property in alphabetical order between `byte-buddy.version` (line 118) and `freemarker.version` (line 119):

```xml
        <byte-buddy.version>1.18.8</byte-buddy.version>
        <commons-fileupload2.version>2.0.0-M5</commons-fileupload2.version>
        <freemarker.version>2.3.34</freemarker.version>
```

- [ ] **Step 2: Reference the property and add the `-core` managed entry**

In `parent/pom.xml`, replace the existing single `commons-fileupload2-jakarta-servlet6` management entry (currently lines 128-132):

```xml
            <dependency>
                <groupId>org.apache.commons</groupId>
                <artifactId>commons-fileupload2-jakarta-servlet6</artifactId>
                <version>2.0.0-M5</version>
            </dependency>
```

with two entries, both referencing the property (the volatile API lives in `-core`, so it must be pinned too):

```xml
            <dependency>
                <groupId>org.apache.commons</groupId>
                <artifactId>commons-fileupload2-core</artifactId>
                <version>${commons-fileupload2.version}</version>
            </dependency>
            <dependency>
                <groupId>org.apache.commons</groupId>
                <artifactId>commons-fileupload2-jakarta-servlet6</artifactId>
                <version>${commons-fileupload2.version}</version>
            </dependency>
```

- [ ] **Step 3: Verify both artifacts resolve to the pinned version**

Run:
```bash
mvn -q -pl core dependency:list -DskipAssembly '-Dincludes=org.apache.commons:commons-fileupload2*'
```
Expected: both `commons-fileupload2-core` and `commons-fileupload2-jakarta-servlet6` listed at `2.0.0-M5`.

- [ ] **Step 4: Verify the reactor still builds**

Run:
```bash
mvn -q validate -DskipAssembly
```
Expected: `BUILD SUCCESS` (no errors from the new property / managed dependency).

- [ ] **Step 5: Commit**

```bash
git add pom.xml parent/pom.xml
git commit -m "WW-5632 build(deps): manage commons-fileupload2-core alongside jakarta-servlet6

Pin both commons-fileupload2 artifacts to a single
commons-fileupload2.version property so the volatile -core API can no
longer skew from -jakarta-servlet6 in the reactor.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 2: Activate a fileupload-scoped enforcer rule (A2)

**Files:**
- Modify: `pom.xml:349-353` (enforcer rule config in `<pluginManagement>`)
- Modify: `pom.xml:373-378` (active `<plugins>` section)

- [ ] **Step 1: Replace the dormant `dependencyConvergence` rule with a scoped `bannedDependencies` rule**

In `pom.xml`, inside the `maven-enforcer-plugin` execution in `<pluginManagement>`, replace the current configuration (lines 349-353):

```xml
                            <configuration>
                                <rules>
                                    <dependencyConvergence />
                                </rules>
                            </configuration>
```

with a rule that bans all commons-fileupload2 versions except the pinned one (`<includes>` are exceptions to the `<excludes>` bans):

```xml
                            <configuration>
                                <rules>
                                    <bannedDependencies>
                                        <excludes>
                                            <exclude>org.apache.commons:commons-fileupload2-core</exclude>
                                            <exclude>org.apache.commons:commons-fileupload2-jakarta-servlet6</exclude>
                                        </excludes>
                                        <includes>
                                            <include>org.apache.commons:commons-fileupload2-core:${commons-fileupload2.version}</include>
                                            <include>org.apache.commons:commons-fileupload2-jakarta-servlet6:${commons-fileupload2.version}</include>
                                        </includes>
                                    </bannedDependencies>
                                </rules>
                            </configuration>
```

- [ ] **Step 2: Bind the enforcer into the active `<plugins>` section**

In `pom.xml`, inside the active `<build><plugins>` block, add the enforcer plugin entry immediately after the `maven-release-plugin` entry (after line 378; version is inherited from `<pluginManagement>`):

```xml
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-release-plugin</artifactId>
                <version>3.3.1</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-enforcer-plugin</artifactId>
            </plugin>
```

- [ ] **Step 3: Verify the enforcer now executes and passes on the clean tree**

Run:
```bash
mvn -q validate -DskipAssembly
```
Expected: `BUILD SUCCESS`. To confirm the rule actually ran (not skipped), run:
```bash
mvn validate -DskipAssembly -pl core | grep -i "enforce"
```
Expected: a line showing `maven-enforcer-plugin:3.6.3:enforce (enforce)` executing.

- [ ] **Step 4: Verify the rule catches a skew (manual negative check, then revert)**

Temporarily edit `parent/pom.xml` to set the `commons-fileupload2-core` managed version to a different value (e.g. `2.0.0-M4` instead of `${commons-fileupload2.version}`), then run:
```bash
mvn validate -DskipAssembly -pl core
```
Expected: `BUILD FAILURE` with a `bannedDependencies` violation naming `commons-fileupload2-core`.
Then revert the edit:
```bash
git checkout -- parent/pom.xml
```

- [ ] **Step 5: Commit**

```bash
git add pom.xml
git commit -m "WW-5632 build: enforce a single commons-fileupload2 version

Activate maven-enforcer-plugin (previously dormant in pluginManagement)
with a fileupload-scoped bannedDependencies rule so any divergent
commons-fileupload2 version fails the build early.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Task 3: Runtime API guard in AbstractMultiPartRequest (B)

**Files:**
- Test: `core/src/test/java/org/apache/struts2/dispatcher/multipart/AbstractMultiPartRequestApiCheckTest.java` (create)
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/multipart/AbstractMultiPartRequest.java` (imports ~line 22 & 34; new method block near `prepareServletFileUpload` at line 213; call site at line 214)

- [ ] **Step 1: Write the failing test**

Create `core/src/test/java/org/apache/struts2/dispatcher/multipart/AbstractMultiPartRequestApiCheckTest.java`:

```java
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.dispatcher.multipart;

import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;
import org.apache.struts2.StrutsException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AbstractMultiPartRequestApiCheckTest {

    @Test
    public void verifyFileUploadApiPassesForCompatibleClass() {
        assertThatCode(() -> AbstractMultiPartRequest.verifyFileUploadApi(JakartaServletDiskFileUpload.class))
                .doesNotThrowAnyException();
    }

    @Test
    public void verifyFileUploadApiThrowsForIncompatibleClass() {
        assertThatThrownBy(() -> AbstractMultiPartRequest.verifyFileUploadApi(IncompatibleFileUpload.class))
                .isInstanceOf(StrutsException.class)
                .hasMessageContaining("setMaxSize")
                .hasMessageContaining("Align commons-fileupload2-core");
    }

    /** Stub lacking the size-limit setters, simulating a binary-incompatible fileupload version. */
    private static class IncompatibleFileUpload {
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run:
```bash
mvn test -DskipAssembly -pl core -Dtest=AbstractMultiPartRequestApiCheckTest
```
Expected: FAIL — compilation error `cannot find symbol: method verifyFileUploadApi(java.lang.Class)` (the guard does not exist yet). This is the red state.

- [ ] **Step 3: Add the two imports**

In `AbstractMultiPartRequest.java`, add the `-core` `AbstractFileUpload` import alongside the existing `fileupload2.core` imports (after line 22, `import org.apache.commons.fileupload2.core.DiskFileItemFactory;` — keep alphabetical, so `AbstractFileUpload` goes *before* it):

```java
import org.apache.commons.fileupload2.core.AbstractFileUpload;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
```

And add the `StrutsException` import after the existing `StrutsConstants` import (line 34):

```java
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
```

- [ ] **Step 4: Implement the guard and wire it into `prepareServletFileUpload`**

In `AbstractMultiPartRequest.java`, add `ensureFileUploadApiVerified();` as the first statement of `prepareServletFileUpload` (currently line 213-214):

```java
    protected JakartaServletDiskFileUpload prepareServletFileUpload(Charset charset, Path saveDir) {
        ensureFileUploadApiVerified();
        JakartaServletDiskFileUpload servletFileUpload = createJakartaFileUpload(charset, saveDir);
```

Then add the following members. Place the field next to the other static members (e.g. directly after the `LOG` field at line 61), and the three methods directly after the `prepareServletFileUpload` method (after its closing brace at line 229):

Field (after line 61):

```java
    /**
     * Verified once per JVM: whether the commons-fileupload2 API on the classpath matches what
     * Struts compiled against. Guards against a mismatched milestone resolving at runtime.
     */
    private static volatile boolean fileUploadApiVerified;
```

Methods (after `prepareServletFileUpload`):

```java
    /**
     * Verifies once per JVM that the commons-fileupload2 API on the classpath matches what Struts
     * compiled against, failing fast with an actionable message instead of a deep-stack
     * {@link NoSuchMethodError} when a mismatched milestone is resolved.
     */
    private void ensureFileUploadApiVerified() {
        if (!fileUploadApiVerified) {
            verifyFileUploadApi(JakartaServletDiskFileUpload.class);
            fileUploadApiVerified = true;
        }
    }

    /**
     * Probes {@code uploadClass} for the size-limit setters Struts invokes in
     * {@link #prepareServletFileUpload}. Package-private for testing.
     *
     * @param uploadClass the file upload class to verify
     * @throws StrutsException if any required method is absent, indicating a binary-incompatible
     *                         commons-fileupload2 version on the classpath
     */
    static void verifyFileUploadApi(Class<?> uploadClass) {
        for (String method : new String[]{"setMaxSize", "setMaxFileCount", "setMaxFileSize"}) {
            try {
                uploadClass.getMethod(method, long.class);
            } catch (NoSuchMethodException e) {
                throw new StrutsException(String.format(
                        "Incompatible Apache Commons FileUpload on the classpath: %s.%s(long) is missing. " +
                                "Detected commons-fileupload2-core version [%s] and commons-fileupload2-jakarta-servlet6 version [%s]. " +
                                "Align commons-fileupload2-core with commons-fileupload2-jakarta-servlet6 (use the same release for both).",
                        uploadClass.getName(), method,
                        implementationVersion(AbstractFileUpload.class),
                        implementationVersion(uploadClass)), e);
            }
        }
    }

    private static String implementationVersion(Class<?> clazz) {
        Package pkg = clazz.getPackage();
        String version = pkg != null ? pkg.getImplementationVersion() : null;
        return version != null ? version : "unknown";
    }
```

- [ ] **Step 5: Run the test to verify it passes**

Run:
```bash
mvn test -DskipAssembly -pl core -Dtest=AbstractMultiPartRequestApiCheckTest
```
Expected: PASS — both tests green.

- [ ] **Step 6: Run the multipart regression tests**

Run:
```bash
mvn test -DskipAssembly -pl core -Dtest='*MultiPartRequest*'
```
Expected: PASS — `JakartaMultiPartRequestTest` and `JakartaStreamMultiPartRequestTest` still green (the guard runs once and does not change upload behavior).

- [ ] **Step 7: Commit**

```bash
git add core/src/main/java/org/apache/struts2/dispatcher/multipart/AbstractMultiPartRequest.java \
        core/src/test/java/org/apache/struts2/dispatcher/multipart/AbstractMultiPartRequestApiCheckTest.java
git commit -m "WW-5632 fix(fileupload): fail fast on incompatible commons-fileupload2 API

Verify once per JVM that the fileupload size-limit setters exist and
throw a clear StrutsException reporting the core/jakarta version skew,
replacing an opaque deep-stack NoSuchMethodError in downstream runtimes.

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Final Verification

- [ ] **Run the full core test suite**

Run:
```bash
mvn test -DskipAssembly -pl core
```
Expected: `BUILD SUCCESS`, all tests pass, enforcer rule executed during `validate`.

- [ ] **Confirm the working tree is clean and the branch holds three commits**

Run:
```bash
git status --short && git log --oneline -3
```
Expected: no uncommitted changes; the three WW-5632 commits on top of the spec commit.
