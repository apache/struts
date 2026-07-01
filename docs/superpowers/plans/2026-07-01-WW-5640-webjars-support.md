# WebJars Support in Struts Core — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let Struts core resolve and serve WebJar assets by a version-less logical path (`bootstrap/css/bootstrap.min.css` → `META-INF/resources/webjars/bootstrap/5.3.8/css/bootstrap.min.css`) through the existing static-content pipeline, exposing a `WebJarUrlProvider` seam plus a `<s:webjar>` tag / `<@s.webjar>` macro.

**Architecture:** One resolution seam — `WebJarUrlProvider` (default impl wraps a singleton `org.webjars.WebJarVersionLocator` from `webjars-locator-lite`) — consumed by two callers: a new `/webjars/` branch in `DefaultStaticContentLoader` (serving) and a thin `WebJar` component (URL string for tags/macros). Resolution is hard-constrained to the `META-INF/resources/webjars/` root, honours an optional allowlist and a master enable switch, and fails closed (404 / empty output).

**Tech Stack:** Java 17, Maven (multi-module), `org.webjars:webjars-locator-lite:1.1.3`, JUnit 5 + AssertJ + Mockito, FreeMarker UI templates, Struts container DI (`@Inject`), `struts-annotations` (TLD generation).

**Spec:** `docs/superpowers/specs/2026-07-01-webjars-support-design.md` — JIRA [WW-5640](https://issues.apache.org/jira/browse/WW-5640).

## Global Constraints

- **Commit prefix:** every commit message starts with `WW-5640 ` and ends with the trailer `Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>`.
- **Module:** all changes are in the `core` module unless a path says otherwise.
- **Java level:** `maven.compiler.release=17`; no APIs beyond Java 17.
- **Build/test command:** `mvn test -DskipAssembly -pl core` (add `-Dtest=...#...` for a single test). Full-module build for dependency changes: `mvn -q -pl core -am install -DskipAssembly -DskipTests`.
- **Dependency footprint:** `webjars-locator-lite` must add exactly one transitive jar (`org.jspecify:jspecify`, Apache-2.0). Verify — do not let it pull Jackson or a classpath scanner.
- **Fail closed:** unresolved / disabled / allowlist-blocked / traversal → 404 when serving, empty output when building URLs. Never fall through to arbitrary classpath serving.
- **Package for new provider classes:** `org.apache.struts2.webjars`. Component/tag/model packages are fixed by convention (`components`, `views.jsp`, `views.freemarker.tags`).
- **Config defaults:** `struts.webjars.enabled=true`, `struts.webjars.allowlist=` (empty = all). Reuse existing `struts.ui.staticContentPath`, `struts.serve.static`, `struts.serve.static.browserCache`.
- **Test framework (CORRECTION, 2026-07-01):** the `core` module uses **JUnit 4** (`org.junit.Test`, `@Before`) with **AssertJ** and **Mockito** — it has **no** JUnit 5 Jupiter engine on the classpath. The test code blocks in Tasks 3/5/6/7 are written in Jupiter (`org.junit.jupiter.api.*`, `@BeforeEach`, `@Nested`) and MUST be translated to JUnit 4 idioms when implemented: `org.junit.Test` + `@Before`; no `@BeforeEach`/`@Nested`/`@DisplayName`. Keep AssertJ assertions and Mockito verbatim. Task 6's `@Nested Serving` class becomes a separate top-level test class (e.g. `DefaultStaticContentLoaderWebJarServingTest`) or plain methods in the same class. Do NOT add JUnit 5 dependencies — introducing Jupiter to Struts core is a separate ticket, out of scope here.

## File Structure

**New files:**
- `core/src/main/java/org/apache/struts2/webjars/WebJarUrlProvider.java` — public interface (the cooperation seam).
- `core/src/main/java/org/apache/struts2/webjars/DefaultWebJarUrlProvider.java` — default impl; wraps `WebJarVersionLocator`; normalization + allowlist + URL composition.
- `core/src/main/java/org/apache/struts2/components/WebJar.java` — UI component emitting the resolved URL (extends `ContextBean`).
- `core/src/main/java/org/apache/struts2/views/jsp/WebJarTag.java` — JSP tag wrapper.
- `core/src/main/java/org/apache/struts2/views/freemarker/tags/WebJarModel.java` — FreeMarker model wrapper.
- `core/src/test/java/org/apache/struts2/webjars/DefaultWebJarUrlProviderTest.java` — provider unit tests.
- `core/src/test/java/org/apache/struts2/dispatcher/DefaultStaticContentLoaderWebJarTest.java` — serving + content-type tests.
- `core/src/test/java/org/apache/struts2/components/WebJarTest.java` — component/URL-emission test.

**Modified files:**
- `pom.xml` — version properties for `webjars-locator-lite` and the test webjar.
- `parent/pom.xml` — `<dependencyManagement>` entries for both.
- `core/pom.xml` — declare the two dependencies (main + test).
- `core/src/main/java/org/apache/struts2/StrutsConstants.java` — 3 new constants.
- `core/src/main/resources/org/apache/struts2/default.properties` — 2 new defaults.
- `core/src/main/resources/struts-beans.xml` — register the provider bean.
- `core/src/main/java/org/apache/struts2/config/StrutsBeanSelectionProvider.java` — bean-selection alias.
- `core/src/main/java/org/apache/struts2/dispatcher/DefaultStaticContentLoader.java` — `/webjars/` branch + extended content-type map + `WebJarUrlProvider` injection.
- `core/src/main/java/org/apache/struts2/views/freemarker/tags/StrutsModels.java` — register `webjar` model.

---

### Task 1: Add `webjars-locator-lite` dependency (+ test webjar)

**Files:**
- Modify: `pom.xml` (properties block, ~line 116–132)
- Modify: `parent/pom.xml` (`<dependencyManagement><dependencies>`, ~line 38+)
- Modify: `core/pom.xml` (`<dependencies>`, ~line 145+)

**Interfaces:**
- Produces: `org.webjars.WebJarVersionLocator` on the `core` compile classpath; a resolvable test webjar (`jquery`) on the `core` test classpath.

- [ ] **Step 1: Add version properties to root `pom.xml`**

In `pom.xml`, inside `<properties>`, in the "dependency versions in alphanumeric order" list (after the `<velocity-tools.version>`/`<weld.version>` neighbours — keep alphabetical), add:

```xml
        <webjars-jquery.version>3.7.1</webjars-jquery.version>
        <webjars-locator-lite.version>1.1.3</webjars-locator-lite.version>
```

- [ ] **Step 2: Add managed dependencies to `parent/pom.xml`**

In `parent/pom.xml`, inside `<dependencyManagement><dependencies>` (alongside the freemarker/caffeine entries around line 45–55), add:

```xml
            <dependency>
                <groupId>org.webjars</groupId>
                <artifactId>webjars-locator-lite</artifactId>
                <version>${webjars-locator-lite.version}</version>
            </dependency>

            <dependency>
                <groupId>org.webjars</groupId>
                <artifactId>jquery</artifactId>
                <version>${webjars-jquery.version}</version>
            </dependency>
```

- [ ] **Step 3: Declare the dependencies in `core/pom.xml`**

In `core/pom.xml`, inside `<dependencies>` (after the freemarker/caffeine block, versionless — versions come from `parent`), add:

```xml
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>webjars-locator-lite</artifactId>
        </dependency>

        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>jquery</artifactId>
            <scope>test</scope>
        </dependency>
```

- [ ] **Step 4: Build and verify the dependency footprint**

Run: `mvn -q -pl core -am install -DskipAssembly -DskipTests`
Expected: BUILD SUCCESS.

Run: `mvn -q -pl core dependency:tree -Dincludes=org.webjars:webjars-locator-lite,org.jspecify:jspecify,com.fasterxml.jackson.core:jackson-databind`
Expected: `webjars-locator-lite` present with `org.jspecify:jspecify` as its only transitive; **no `jackson` line**. If Jackson appears, stop — wrong artifact was added.

- [ ] **Step 5: Commit**

```bash
git add pom.xml parent/pom.xml core/pom.xml
git commit -m "WW-5640 build: add webjars-locator-lite dependency

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 2: Add configuration constants and defaults

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/StrutsConstants.java` (after line 318 and after line 435)
- Modify: `core/src/main/resources/org/apache/struts2/default.properties` (after line 107)

**Interfaces:**
- Produces: `StrutsConstants.STRUTS_WEBJARS_ENABLED` (`"struts.webjars.enabled"`), `STRUTS_WEBJARS_ALLOWLIST` (`"struts.webjars.allowlist"`), `STRUTS_WEBJARS_URL_PROVIDER` (`"struts.webjars.urlProvider"`); default values in `default.properties`.

- [ ] **Step 1: Add the settings constants**

In `StrutsConstants.java`, immediately after `STRUTS_UI_STATIC_CONTENT_PATH` (line 318):

```java
    /**
     * Whether WebJars support is enabled (serving and URL building)
     */
    public static final String STRUTS_WEBJARS_ENABLED = "struts.webjars.enabled";

    /**
     * Optional comma-separated allowlist of WebJar names permitted to be served (empty = all)
     */
    public static final String STRUTS_WEBJARS_ALLOWLIST = "struts.webjars.allowlist";
```

- [ ] **Step 2: Add the bean-selection constant**

In `StrutsConstants.java`, immediately after `STRUTS_STATIC_CONTENT_LOADER` (line 435):

```java
    /**
     * The {@link org.apache.struts2.webjars.WebJarUrlProvider} implementation class
     */
    public static final String STRUTS_WEBJARS_URL_PROVIDER = "struts.webjars.urlProvider";
```

- [ ] **Step 3: Add defaults to `default.properties`**

In `default.properties`, after line 107 (`struts.serve.static.browserCache=true`):

```properties

### WebJars support
### Master switch for resolving/serving WebJar assets under <staticContentPath>/webjars/**
struts.webjars.enabled=true
### Optional comma-separated allowlist of WebJar names (empty = all WebJars on the classpath)
struts.webjars.allowlist=
```

- [ ] **Step 4: Compile to verify**

Run: `mvn -q -pl core compile -DskipAssembly`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/StrutsConstants.java core/src/main/resources/org/apache/struts2/default.properties
git commit -m "WW-5640 feat: add webjars config constants and defaults

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 3: `WebJarUrlProvider` interface + `DefaultWebJarUrlProvider` (TDD)

**Files:**
- Create: `core/src/main/java/org/apache/struts2/webjars/WebJarUrlProvider.java`
- Create: `core/src/main/java/org/apache/struts2/webjars/DefaultWebJarUrlProvider.java`
- Test: `core/src/test/java/org/apache/struts2/webjars/DefaultWebJarUrlProviderTest.java`

**Interfaces:**
- Consumes: `org.webjars.WebJarVersionLocator` (methods `String path(String name, String filePath)`, `String fullPath(String name, String filePath)`, both return `null` when unresolved; constant `WebJarVersionLocator.WEBJARS_PATH_PREFIX == "META-INF/resources/webjars"`); `StrutsConstants` from Task 2; `StaticContentLoader.Validator.validateStaticContentPath(String)`.
- Produces:
  - `WebJarUrlProvider.resolveResourcePath(String logicalPath) -> Optional<String>` (classpath resource under `META-INF/resources/webjars/…`, for serving).
  - `WebJarUrlProvider.resolveUrl(String logicalPath, HttpServletRequest request) -> Optional<String>` (servable URL: contextPath + staticContentPath + `/webjars/` + versioned path).
  - `WebJarUrlProvider.isEnabled() -> boolean`.

- [ ] **Step 1: Write the interface**

Create `WebJarUrlProvider.java`:

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
package org.apache.struts2.webjars;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * Resolves version-less WebJar logical paths (e.g. {@code bootstrap/css/bootstrap.min.css}) to either a
 * concrete classpath resource under {@code META-INF/resources/webjars/} (for serving) or a servable URL
 * (for tags/macros). Resolution is constrained to the WebJars root, honours an optional allowlist and the
 * {@code struts.webjars.enabled} switch, and fails closed (empty result) when unresolved or blocked.
 */
public interface WebJarUrlProvider {

    /**
     * @param logicalPath version-less path such as {@code bootstrap/css/bootstrap.min.css}
     * @return the concrete classpath resource path (e.g.
     *         {@code META-INF/resources/webjars/bootstrap/5.3.8/css/bootstrap.min.css}), or empty
     */
    Optional<String> resolveResourcePath(String logicalPath);

    /**
     * @param logicalPath version-less path such as {@code bootstrap/css/bootstrap.min.css}
     * @param request     the current request (used for the servlet context path)
     * @return a servable URL, or empty
     */
    Optional<String> resolveUrl(String logicalPath, HttpServletRequest request);

    /**
     * @return whether WebJars support is enabled
     */
    boolean isEnabled();
}
```

- [ ] **Step 2: Write the failing tests**

Create `DefaultWebJarUrlProviderTest.java` (the `jquery` test webjar from Task 1 provides a real, resolvable WebJar):

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
package org.apache.struts2.webjars;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultWebJarUrlProviderTest {

    private DefaultWebJarUrlProvider provider;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        provider = new DefaultWebJarUrlProvider();
        provider.setEnabled("true");
        provider.setAllowlist("");
        provider.setStaticContentPath("/static");
        request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("/myapp");
    }

    @Test
    void resolvesKnownResourceToVersionedClasspathPath() {
        assertThat(provider.resolveResourcePath("jquery/jquery.min.js"))
            .hasValueSatisfying(p -> assertThat(p)
                .startsWith("META-INF/resources/webjars/jquery/")
                .endsWith("/jquery.min.js"));
    }

    @Test
    void resolvesKnownResourceToServableUrl() {
        assertThat(provider.resolveUrl("jquery/jquery.min.js", request))
            .hasValueSatisfying(u -> assertThat(u)
                .startsWith("/myapp/static/webjars/jquery/")
                .endsWith("/jquery.min.js"));
    }

    @Test
    void unknownWebjarResolvesEmpty() {
        assertThat(provider.resolveResourcePath("no-such-lib/x.js")).isEmpty();
        assertThat(provider.resolveUrl("no-such-lib/x.js", request)).isEmpty();
    }

    @Test
    void traversalIsRejected() {
        assertThat(provider.resolveResourcePath("jquery/../../../etc/passwd")).isEmpty();
        assertThat(provider.resolveResourcePath("../jquery/jquery.min.js")).isEmpty();
    }

    @Test
    void allowlistBlocksNonListedWebjar() {
        provider.setAllowlist("bootstrap");
        assertThat(provider.resolveResourcePath("jquery/jquery.min.js")).isEmpty();
    }

    @Test
    void allowlistPermitsListedWebjar() {
        provider.setAllowlist("jquery, bootstrap");
        assertThat(provider.resolveResourcePath("jquery/jquery.min.js")).isPresent();
    }

    @Test
    void disabledResolvesEmpty() {
        provider.setEnabled("false");
        assertThat(provider.isEnabled()).isFalse();
        assertThat(provider.resolveResourcePath("jquery/jquery.min.js")).isEmpty();
        assertThat(provider.resolveUrl("jquery/jquery.min.js", request)).isEmpty();
    }

    @Test
    void rootContextPathIsNotDuplicated() {
        when(request.getContextPath()).thenReturn("/");
        assertThat(provider.resolveUrl("jquery/jquery.min.js", request))
            .hasValueSatisfying(u -> assertThat(u).startsWith("/static/webjars/jquery/"));
    }

    @Test
    void blankOrSingleSegmentPathResolvesEmpty() {
        assertThat(provider.resolveResourcePath("")).isEmpty();
        assertThat(provider.resolveResourcePath("jquery")).isEmpty();
    }
}
```

- [ ] **Step 3: Run tests to verify they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=DefaultWebJarUrlProviderTest`
Expected: FAIL — `DefaultWebJarUrlProvider` does not exist / does not compile.

- [ ] **Step 4: Write the implementation**

Create `DefaultWebJarUrlProvider.java`:

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
package org.apache.struts2.webjars;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.StaticContentLoader;
import org.apache.struts2.inject.Inject;
import org.webjars.WebJarVersionLocator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Default {@link WebJarUrlProvider} backed by a singleton {@link WebJarVersionLocator}
 * (from {@code webjars-locator-lite}). Thread-safe.
 */
public class DefaultWebJarUrlProvider implements WebJarUrlProvider {

    private static final Logger LOG = LogManager.getLogger(DefaultWebJarUrlProvider.class);

    private static final String WEBJARS_URL_SEGMENT = "/webjars/";

    private final WebJarVersionLocator locator = new WebJarVersionLocator();

    private boolean enabled = true;
    private Set<String> allowlist = Collections.emptySet();
    private String uiStaticContentPath = StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH;

    @Inject(value = StrutsConstants.STRUTS_WEBJARS_ENABLED, required = false)
    public void setEnabled(String enabled) {
        this.enabled = BooleanUtils.toBoolean(enabled);
    }

    @Inject(value = StrutsConstants.STRUTS_WEBJARS_ALLOWLIST, required = false)
    public void setAllowlist(String allowlist) {
        Set<String> names = new HashSet<>();
        if (StringUtils.isNotBlank(allowlist)) {
            for (String name : allowlist.split(",")) {
                String trimmed = name.trim();
                if (!trimmed.isEmpty()) {
                    names.add(trimmed);
                }
            }
        }
        this.allowlist = Collections.unmodifiableSet(names);
    }

    @Inject(StrutsConstants.STRUTS_UI_STATIC_CONTENT_PATH)
    public void setStaticContentPath(String uiStaticContentPath) {
        this.uiStaticContentPath = StaticContentLoader.Validator.validateStaticContentPath(uiStaticContentPath);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Optional<String> resolveResourcePath(String logicalPath) {
        String[] parts = split(logicalPath);
        if (parts == null) {
            return Optional.empty();
        }
        String full = locator.fullPath(parts[0], parts[1]);
        if (full == null || !full.startsWith(WebJarVersionLocator.WEBJARS_PATH_PREFIX + "/")) {
            return Optional.empty();
        }
        return Optional.of(full);
    }

    @Override
    public Optional<String> resolveUrl(String logicalPath, HttpServletRequest request) {
        String[] parts = split(logicalPath);
        if (parts == null) {
            return Optional.empty();
        }
        String versioned = locator.path(parts[0], parts[1]);
        if (versioned == null) {
            return Optional.empty();
        }
        StringBuilder url = new StringBuilder();
        String contextPath = request.getContextPath();
        if (StringUtils.isNotEmpty(contextPath) && !"/".equals(contextPath)) {
            url.append(contextPath);
        }
        url.append(uiStaticContentPath).append(WEBJARS_URL_SEGMENT).append(versioned);
        return Optional.of(url.toString());
    }

    /**
     * Normalize, validate and split a logical path into {webJarName, filePath}.
     *
     * @return a two-element array, or {@code null} if disabled, blank, single-segment,
     *         traversal-tainted, or allowlist-blocked
     */
    private String[] split(String logicalPath) {
        if (!enabled || StringUtils.isBlank(logicalPath)) {
            return null;
        }
        String normalized = StringUtils.stripStart(logicalPath, "/");
        if (normalized.contains("\\")) {
            return null;
        }
        for (String segment : normalized.split("/")) {
            if (segment.equals("..") || segment.equals(".")) {
                LOG.debug("Rejecting WebJar path with traversal segment: {}", logicalPath);
                return null;
            }
        }
        int slash = normalized.indexOf('/');
        if (slash < 1 || slash == normalized.length() - 1) {
            return null;
        }
        String webJarName = normalized.substring(0, slash);
        String filePath = normalized.substring(slash + 1);
        if (!isAllowed(webJarName)) {
            LOG.debug("WebJar '{}' is not on the allowlist", webJarName);
            return null;
        }
        return new String[]{webJarName, filePath};
    }

    private boolean isAllowed(String webJarName) {
        return allowlist.isEmpty() || allowlist.contains(webJarName);
    }
}
```

Note: `Arrays` import is used by no code above — remove it if your IDE flags it; it is listed only so the import block compiles cleanly if you extend allowlist parsing. (Prefer removing unused imports before commit.)

- [ ] **Step 5: Run tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=DefaultWebJarUrlProviderTest`
Expected: PASS (all 9 tests).

- [ ] **Step 6: Commit**

```bash
git add core/src/main/java/org/apache/struts2/webjars/ core/src/test/java/org/apache/struts2/webjars/
git commit -m "WW-5640 feat: add WebJarUrlProvider resolution seam

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 4: Register the provider bean in the container

**Files:**
- Modify: `core/src/main/resources/struts-beans.xml` (after line 209)
- Modify: `core/src/main/java/org/apache/struts2/config/StrutsBeanSelectionProvider.java` (import + after the `StaticContentLoader` alias, ~line 432)

**Interfaces:**
- Consumes: `WebJarUrlProvider` (Task 3), `StrutsConstants.STRUTS_WEBJARS_URL_PROVIDER` (Task 2).
- Produces: a container-resolvable `WebJarUrlProvider` bean (`name="struts"`), injectable via `@Inject`/`Container.getInstance(WebJarUrlProvider.class)`.

- [ ] **Step 1: Register the default bean**

In `struts-beans.xml`, immediately after the `StaticContentLoader` bean (line 208–209):

```xml
    <bean type="org.apache.struts2.webjars.WebJarUrlProvider"
          class="org.apache.struts2.webjars.DefaultWebJarUrlProvider" name="struts"/>
```

- [ ] **Step 2: Add the bean-selection alias**

In `StrutsBeanSelectionProvider.java`, add the import (with the other `org.apache.struts2.*` imports):

```java
import org.apache.struts2.webjars.WebJarUrlProvider;
```

Then, in `register(...)`, immediately after the `StaticContentLoader` alias line (`alias(StaticContentLoader.class, StrutsConstants.STRUTS_STATIC_CONTENT_LOADER, builder, props);`):

```java
        alias(WebJarUrlProvider.class, StrutsConstants.STRUTS_WEBJARS_URL_PROVIDER, builder, props);
```

- [ ] **Step 3: Build to verify wiring**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsBeanSelectionProviderTest`
Expected: PASS (if this test does not exist in the module, run `mvn -q -pl core compile -DskipAssembly` and expect BUILD SUCCESS instead).

- [ ] **Step 4: Commit**

```bash
git add core/src/main/resources/struts-beans.xml core/src/main/java/org/apache/struts2/config/StrutsBeanSelectionProvider.java
git commit -m "WW-5640 feat: register WebJarUrlProvider bean

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 5: Extend static content-type map for WebJar asset types (TDD)

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/DefaultStaticContentLoader.java` (`getContentType`, lines 330–350)
- Test: `core/src/test/java/org/apache/struts2/dispatcher/DefaultStaticContentLoaderWebJarTest.java` (create; content-type cases first, serving cases added in Task 6)

**Interfaces:**
- Produces: `DefaultStaticContentLoader.getContentType(String)` returns correct MIME types for `.woff`, `.woff2`, `.ttf`, `.eot`, `.otf`, `.svg`, `.map`, `.json`, `.ico`, `.mjs` (plus existing types).

- [ ] **Step 1: Write the failing test**

Create `DefaultStaticContentLoaderWebJarTest.java`:

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
package org.apache.struts2.dispatcher;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultStaticContentLoaderWebJarTest {

    private final ContentTypeProbe loader = new ContentTypeProbe();

    /** Exposes the protected getContentType for assertion. */
    static class ContentTypeProbe extends DefaultStaticContentLoader {
        String type(String name) {
            return getContentType(name);
        }
    }

    @Test
    void mapsWebJarAssetTypes() {
        assertThat(loader.type("x.woff2")).isEqualTo("font/woff2");
        assertThat(loader.type("x.woff")).isEqualTo("font/woff");
        assertThat(loader.type("x.ttf")).isEqualTo("font/ttf");
        assertThat(loader.type("x.otf")).isEqualTo("font/otf");
        assertThat(loader.type("x.eot")).isEqualTo("application/vnd.ms-fontobject");
        assertThat(loader.type("x.svg")).isEqualTo("image/svg+xml");
        assertThat(loader.type("x.map")).isEqualTo("application/json");
        assertThat(loader.type("x.json")).isEqualTo("application/json");
        assertThat(loader.type("x.ico")).isEqualTo("image/x-icon");
        assertThat(loader.type("x.mjs")).isEqualTo("text/javascript");
    }

    @Test
    void preservesExistingTypes() {
        assertThat(loader.type("x.js")).isEqualTo("text/javascript");
        assertThat(loader.type("x.css")).isEqualTo("text/css");
        assertThat(loader.type("x.png")).isEqualTo("image/png");
        assertThat(loader.type("x.unknown")).isNull();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=DefaultStaticContentLoaderWebJarTest#mapsWebJarAssetTypes`
Expected: FAIL — e.g. `.woff2` returns `null`.

- [ ] **Step 3: Extend `getContentType`**

In `DefaultStaticContentLoader.getContentType`, replace the existing `if/else` chain body (lines 333–349) with the extended version (keeps all existing entries, adds new ones):

```java
        if (name.endsWith(".js") || name.endsWith(".mjs")) {
            return "text/javascript";
        } else if (name.endsWith(".css")) {
            return "text/css";
        } else if (name.endsWith(".html")) {
            return "text/html";
        } else if (name.endsWith(".txt")) {
            return "text/plain";
        } else if (name.endsWith(".gif")) {
            return "image/gif";
        } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (name.endsWith(".png")) {
            return "image/png";
        } else if (name.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (name.endsWith(".ico")) {
            return "image/x-icon";
        } else if (name.endsWith(".woff2")) {
            return "font/woff2";
        } else if (name.endsWith(".woff")) {
            return "font/woff";
        } else if (name.endsWith(".ttf")) {
            return "font/ttf";
        } else if (name.endsWith(".otf")) {
            return "font/otf";
        } else if (name.endsWith(".eot")) {
            return "application/vnd.ms-fontobject";
        } else if (name.endsWith(".json") || name.endsWith(".map")) {
            return "application/json";
        } else {
            return null;
        }
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=DefaultStaticContentLoaderWebJarTest`
Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/dispatcher/DefaultStaticContentLoader.java core/src/test/java/org/apache/struts2/dispatcher/DefaultStaticContentLoaderWebJarTest.java
git commit -m "WW-5640 feat: extend static content-type map for webjar assets

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 6: Serve `/webjars/**` through `DefaultStaticContentLoader` (TDD)

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/DefaultStaticContentLoader.java` (imports; new field + `@Inject` setter; refactor 404 block; `findStaticResource` branch; new `findWebJarResource`)
- Test: `core/src/test/java/org/apache/struts2/dispatcher/DefaultStaticContentLoaderWebJarTest.java` (add serving cases)

**Interfaces:**
- Consumes: `WebJarUrlProvider.resolveResourcePath(String)` (Task 3); existing `findResource(String)`, `process(InputStream, String, ...)`, `cleanupPath(String)`.
- Produces: serving of `<staticContentPath>/webjars/<name>/<path>` → 200 with the versioned classpath resource; unresolved → 404.

- [ ] **Step 1: Add the failing serving tests**

Append to `DefaultStaticContentLoaderWebJarTest.java` (inside the class):

```java
    @org.junit.jupiter.api.Nested
    class Serving {

        private DefaultStaticContentLoader newLoader(boolean enabled) {
            DefaultStaticContentLoader loader = new DefaultStaticContentLoader();
            loader.setServeStaticContent("true");
            loader.setStaticContentPath("/static");
            loader.setServeStaticBrowserCache("true");
            loader.setEncoding("UTF-8");
            org.apache.struts2.webjars.DefaultWebJarUrlProvider provider =
                new org.apache.struts2.webjars.DefaultWebJarUrlProvider();
            provider.setEnabled(String.valueOf(enabled));
            provider.setAllowlist("");
            provider.setStaticContentPath("/static");
            loader.setWebJarUrlProvider(provider);
            return loader;
        }

        @Test
        void servesKnownWebJarAssetWithContentType() throws Exception {
            DefaultStaticContentLoader loader = newLoader(true);
            jakarta.servlet.http.HttpServletRequest request =
                org.mockito.Mockito.mock(jakarta.servlet.http.HttpServletRequest.class);
            jakarta.servlet.http.HttpServletResponse response =
                org.mockito.Mockito.mock(jakarta.servlet.http.HttpServletResponse.class);
            java.io.ByteArrayOutputStream captured = new java.io.ByteArrayOutputStream();
            org.mockito.Mockito.when(response.getOutputStream())
                .thenReturn(new org.apache.struts2.dispatcher.WebJarTestServletOutputStream(captured));

            loader.findStaticResource("/static/webjars/jquery/jquery.min.js", request, response);

            org.mockito.Mockito.verify(response).setContentType("text/javascript");
            org.mockito.Mockito.verify(response, org.mockito.Mockito.never())
                .sendError(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND);
            assertThat(captured.size()).isGreaterThan(0);
        }

        @Test
        void unknownWebJarAssetReturns404() throws Exception {
            DefaultStaticContentLoader loader = newLoader(true);
            jakarta.servlet.http.HttpServletRequest request =
                org.mockito.Mockito.mock(jakarta.servlet.http.HttpServletRequest.class);
            jakarta.servlet.http.HttpServletResponse response =
                org.mockito.Mockito.mock(jakarta.servlet.http.HttpServletResponse.class);

            loader.findStaticResource("/static/webjars/nope/nope.js", request, response);

            org.mockito.Mockito.verify(response)
                .sendError(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND);
        }

        @Test
        void disabledWebJarsReturns404() throws Exception {
            DefaultStaticContentLoader loader = newLoader(false);
            jakarta.servlet.http.HttpServletRequest request =
                org.mockito.Mockito.mock(jakarta.servlet.http.HttpServletRequest.class);
            jakarta.servlet.http.HttpServletResponse response =
                org.mockito.Mockito.mock(jakarta.servlet.http.HttpServletResponse.class);

            loader.findStaticResource("/static/webjars/jquery/jquery.min.js", request, response);

            org.mockito.Mockito.verify(response)
                .sendError(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND);
        }
    }
```

Create the minimal `ServletOutputStream` test helper `core/src/test/java/org/apache/struts2/dispatcher/WebJarTestServletOutputStream.java`:

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
package org.apache.struts2.dispatcher;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import java.io.IOException;
import java.io.OutputStream;

/** Minimal ServletOutputStream backed by an OutputStream, for tests. */
public class WebJarTestServletOutputStream extends ServletOutputStream {

    private final OutputStream delegate;

    public WebJarTestServletOutputStream(OutputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public void write(int b) throws IOException {
        delegate.write(b);
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        // no-op
    }
}
```

- [ ] **Step 2: Run the serving tests to verify they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=DefaultStaticContentLoaderWebJarTest`
Expected: FAIL — `setWebJarUrlProvider` does not exist / `/webjars/` not handled.

- [ ] **Step 3: Wire the provider and add the branch**

In `DefaultStaticContentLoader.java`, add imports (with the existing imports):

```java
import java.util.Optional;
import org.apache.struts2.webjars.WebJarUrlProvider;
```

Add a field (near `encoding`/`devMode`, ~line 108) and its injection setter:

```java
    protected WebJarUrlProvider webJarUrlProvider;

    @Inject
    public void setWebJarUrlProvider(WebJarUrlProvider webJarUrlProvider) {
        this.webJarUrlProvider = webJarUrlProvider;
    }
```

Add a class constant (near the top of the class body):

```java
    protected static final String WEBJARS_REQUEST_PREFIX = "/webjars/";
```

Refactor `findStaticResource` (lines 209–243). Replace the whole method with:

```java
    public void findStaticResource(String path, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        String name = cleanupPath(path);

        if (name.startsWith(WEBJARS_REQUEST_PREFIX)) {
            if (!findWebJarResource(name, path, request, response)) {
                sendNotFound(response);
            }
            return;
        }

        for (String pathPrefix : pathPrefixes) {
            URL resourceUrl = findResource(buildPath(name, pathPrefix));
            if (resourceUrl != null) {
                InputStream is = null;
                try {
                    //check that the resource path is under the pathPrefix path
                    String pathEnding = buildPath(name, pathPrefix);
                    if (resourceUrl.getFile().endsWith(pathEnding))
                        is = resourceUrl.openStream();
                } catch (IOException ex) {
                    // just ignore it
                    continue;
                }

                //not inside the try block, as this could throw IOExceptions also
                if (is != null) {
                    process(is, path, request, response);
                    return;
                }
            }
        }

        sendNotFound(response);
    }

    /**
     * Resolve and serve a WebJar asset requested under {@code <staticContentPath>/webjars/**}.
     *
     * @param name    the request path with the static-content prefix stripped, e.g. {@code /webjars/jquery/jquery.min.js}
     * @param path    the original request path (used for content-type detection)
     * @return true if the asset was resolved and streamed; false otherwise (caller sends 404)
     */
    protected boolean findWebJarResource(String name, String path, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        String logicalPath = name.substring(WEBJARS_REQUEST_PREFIX.length());
        Optional<String> resource = webJarUrlProvider.resolveResourcePath(logicalPath);
        if (resource.isEmpty()) {
            return false;
        }
        URL resourceUrl = findResource(resource.get());
        if (resourceUrl == null) {
            return false;
        }
        process(resourceUrl.openStream(), path, request, response);
        return true;
    }

    protected void sendNotFound(HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e1) {
            // we're already sending an error, not much else we can do if more stuff breaks
            LOG.warn("Unable to send error response, code: {};", HttpServletResponse.SC_NOT_FOUND, e1);
        } catch (IllegalStateException ise) {
            // Log illegalstate instead of passing unrecoverable exception to calling thread
            LOG.warn("Unable to send error response, code: {}; isCommitted: {};", HttpServletResponse.SC_NOT_FOUND, response.isCommitted(), ise);
        }
    }
```

(The `sendNotFound` body is lifted verbatim from the old inline 404 block so behaviour is unchanged for non-WebJar paths.)

- [ ] **Step 4: Run all loader tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=DefaultStaticContentLoaderWebJarTest`
Expected: PASS (content-type + all three serving tests).

- [ ] **Step 5: Run the full dispatcher test package for regressions**

Run: `mvn test -DskipAssembly -pl core -Dtest=org.apache.struts2.dispatcher.*`
Expected: PASS (no regression in existing static-content behaviour).

- [ ] **Step 6: Commit**

```bash
git add core/src/main/java/org/apache/struts2/dispatcher/DefaultStaticContentLoader.java core/src/test/java/org/apache/struts2/dispatcher/
git commit -m "WW-5640 feat: serve webjar assets via static content loader

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 7: `<s:webjar>` component + JSP tag + FreeMarker macro (TDD)

**Files:**
- Create: `core/src/main/java/org/apache/struts2/components/WebJar.java`
- Create: `core/src/main/java/org/apache/struts2/views/jsp/WebJarTag.java`
- Create: `core/src/main/java/org/apache/struts2/views/freemarker/tags/WebJarModel.java`
- Modify: `core/src/main/java/org/apache/struts2/views/freemarker/tags/StrutsModels.java` (field + getter)
- Test: `core/src/test/java/org/apache/struts2/components/WebJarTest.java`

**Interfaces:**
- Consumes: `WebJarUrlProvider.resolveUrl(String, HttpServletRequest)` (Task 3); `ContextBean` (`var`, `putInContext`, `findString`); `Component` (`@Inject` processed by `container.inject`).
- Produces: `<s:webjar path="…"/>` and `<@s.webjar path="…"/>` emitting the resolved URL string, or storing it in `var`; empty output when unresolved.

- [ ] **Step 1: Write the failing component test**

Create `WebJarTest.java`:

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
package org.apache.struts2.components;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import org.apache.struts2.config.ConfigurationManager;
import org.apache.struts2.config.providers.XmlConfigurationProvider;
import org.apache.struts2.inject.Container;
import org.apache.struts2.webjars.WebJarUrlProvider;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebJarTest {

    private WebJar newComponent(ValueStack stack, HttpServletRequest request, WebJarUrlProvider provider) {
        WebJar webJar = new WebJar(stack, request);
        webJar.setWebJarUrlProvider(provider);
        return webJar;
    }

    @Test
    void writesResolvedUrl() {
        ValueStack stack = mock(ValueStack.class);
        when(stack.findString("jquery/jquery.min.js")).thenReturn("jquery/jquery.min.js");
        HttpServletRequest request = mock(HttpServletRequest.class);
        WebJarUrlProvider provider = mock(WebJarUrlProvider.class);
        when(provider.resolveUrl("jquery/jquery.min.js", request))
            .thenReturn(Optional.of("/myapp/static/webjars/jquery/3.7.1/jquery.min.js"));

        WebJar webJar = newComponent(stack, request, provider);
        webJar.setPath("jquery/jquery.min.js");
        StringWriter writer = new StringWriter();

        webJar.start(writer);
        webJar.end(writer, "");

        assertThat(writer.toString()).isEqualTo("/myapp/static/webjars/jquery/3.7.1/jquery.min.js");
    }

    @Test
    void unresolvedPathWritesNothing() {
        ValueStack stack = mock(ValueStack.class);
        when(stack.findString("nope/x.js")).thenReturn("nope/x.js");
        HttpServletRequest request = mock(HttpServletRequest.class);
        WebJarUrlProvider provider = mock(WebJarUrlProvider.class);
        when(provider.resolveUrl("nope/x.js", request)).thenReturn(Optional.empty());

        WebJar webJar = newComponent(stack, request, provider);
        webJar.setPath("nope/x.js");
        StringWriter writer = new StringWriter();

        webJar.start(writer);
        webJar.end(writer, "");

        assertThat(writer.toString()).isEmpty();
    }
}
```

> If `stack.getContext()` is dereferenced by `ContextBean`/`Component` during `end`, stub it: `when(stack.getContext()).thenReturn(new org.apache.struts2.util.StrutsContext());` — add only if a NullPointerException surfaces at run time.

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=WebJarTest`
Expected: FAIL — `WebJar` does not exist.

- [ ] **Step 3: Write the component**

Create `WebJar.java`:

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
package org.apache.struts2.components;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.webjars.WebJarUrlProvider;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

/**
 * <p>Resolves a version-less WebJar resource path to a servable URL and writes it to the output
 * (or stores it in a variable when {@code var} is set). Compose it with {@code <s:script>}/{@code <s:link>}
 * or a raw {@code <link>}/{@code <script>} element.</p>
 *
 * <b>Examples</b>
 * <pre>
 *   &lt;link rel="stylesheet" href="&lt;s:webjar path="bootstrap/css/bootstrap.min.css"/&gt;"&gt;
 *   &lt;@s.webjar path="jquery/jquery.min.js"/&gt;
 * </pre>
 */
@StrutsTag(
    name = "webjar",
    tldTagClass = "org.apache.struts2.views.jsp.WebJarTag",
    description = "Resolve a version-less WebJar resource path to a servable URL")
public class WebJar extends ContextBean {

    private static final Logger LOG = LogManager.getLogger(WebJar.class);

    protected String path;

    private final HttpServletRequest request;
    private WebJarUrlProvider webJarUrlProvider;

    public WebJar(ValueStack stack, HttpServletRequest request) {
        super(stack);
        this.request = request;
    }

    @Inject
    public void setWebJarUrlProvider(WebJarUrlProvider webJarUrlProvider) {
        this.webJarUrlProvider = webJarUrlProvider;
    }

    @Override
    public boolean end(Writer writer, String body) {
        String logicalPath = findString(path);
        Optional<String> url = (logicalPath == null)
            ? Optional.empty()
            : webJarUrlProvider.resolveUrl(logicalPath, request);

        if (url.isPresent()) {
            if (StringUtils.isNotBlank(getVar())) {
                putInContext(url.get());
            } else {
                try {
                    writer.write(url.get());
                } catch (IOException e) {
                    LOG.info("Could not write WebJar URL for path '{}'", path, e);
                }
            }
        }
        return super.end(writer, body);
    }

    @StrutsTagAttribute(required = true,
        description = "The version-less WebJar resource path, e.g. bootstrap/css/bootstrap.min.css")
    public void setPath(String path) {
        this.path = path;
    }
}
```

- [ ] **Step 4: Write the JSP tag**

Create `WebJarTag.java`:

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
package org.apache.struts2.views.jsp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.WebJar;
import org.apache.struts2.util.ValueStack;

public class WebJarTag extends ContextBeanTag {

    private static final long serialVersionUID = 1L;

    protected String path;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new WebJar(stack, req);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        WebJar webJar = (WebJar) component;
        webJar.setPath(path);
    }

    public void setPath(String path) {
        this.path = path;
    }
}
```

- [ ] **Step 5: Write the FreeMarker model**

Create `WebJarModel.java`:

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
package org.apache.struts2.views.freemarker.tags;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.WebJar;
import org.apache.struts2.util.ValueStack;

public class WebJarModel extends TagModel {

    public WebJarModel(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    @Override
    protected Component getBean() {
        return new WebJar(stack, req);
    }
}
```

- [ ] **Step 6: Register the model in `StrutsModels`**

In `StrutsModels.java`, add a field (with the other `protected *Model` fields, e.g. near `set`):

```java
    protected WebJarModel webjar;
```

And a getter (with the other getters, e.g. near `getSet()`):

```java
    public WebJarModel getWebjar() {
        if (webjar == null) {
            webjar = new WebJarModel(stack, req, res);
        }
        return webjar;
    }
```

- [ ] **Step 7: Run tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=WebJarTest`
Expected: PASS.

Run: `mvn -q -pl core compile -DskipAssembly`
Expected: BUILD SUCCESS (annotation processor regenerates the TLD with the new `webjar` tag).

- [ ] **Step 8: Commit**

```bash
git add core/src/main/java/org/apache/struts2/components/WebJar.java core/src/main/java/org/apache/struts2/views/jsp/WebJarTag.java core/src/main/java/org/apache/struts2/views/freemarker/tags/WebJarModel.java core/src/main/java/org/apache/struts2/views/freemarker/tags/StrutsModels.java core/src/test/java/org/apache/struts2/components/WebJarTest.java
git commit -m "WW-5640 feat: add <s:webjar> tag and <@s.webjar> macro

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

### Task 8: Full-module verification

**Files:** none (verification only).

**Interfaces:** none.

- [ ] **Step 1: Run the full `core` test suite**

Run: `mvn test -DskipAssembly -pl core`
Expected: BUILD SUCCESS, no failures. If any pre-existing unrelated failures appear, confirm they also fail on a clean `main` checkout before attributing them here.

- [ ] **Step 2: Confirm the generated TLD contains the tag**

Run: `grep -c "<name>webjar</name>" core/target/classes/META-INF/struts-tags.tld`
Expected: `1` (the annotation processor emitted the `<s:webjar>` tag entry).

- [ ] **Step 3: Re-confirm dependency footprint**

Run: `mvn -q -pl core dependency:tree -Dincludes=com.fasterxml.jackson.core:*`
Expected: no `webjars`-sourced Jackson (Jackson may appear from other modules/plugins, but must not be a child of `webjars-locator-lite`).

- [ ] **Step 4: Commit (only if verification produced fixes)**

If steps surfaced fixes, commit them:

```bash
git add -A
git commit -m "WW-5640 test: full-module verification for webjars support

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Self-Review

**Spec coverage:**
- R1 (resolution + cache + fail-closed): Task 3 (`DefaultWebJarUrlProvider`, locator caching, empty-on-miss). ✓
- R2 (serving, reuse content-type/caching, hook point): Task 6 (`/webjars/` branch reuses `process()`) + Task 5 (content-type). ✓
- R3 (`WebJarUrlProvider` + tag + macro, URL string, optional var): Task 3 (interface) + Task 4 (container) + Task 7 (tag/macro/var). ✓
- R4 (`struts.webjars.enabled`, `struts.webjars.allowlist`, reuse existing): Task 2. ✓
- R5 (normalize/reject traversal, locator-backed only, allowlist, disabled): Task 3 (`split` normalization + allowlist + enabled) + tests. ✓
- R6 (add `webjars-locator-lite`, verify footprint): Task 1. ✓
- Testing section (unit resolution, integration serving, tag/macro): Tasks 3, 5, 6, 7, 8. ✓
- Decision 6 (branch in loader), 7 (extend MIME), 8 (URL string + var), 9 (allowlist property): Tasks 6, 5, 7, 2. ✓

**Placeholder scan:** No TBD/TODO. The one "confirm during implementation" note (whether `ContextBean.end` dereferences `stack.getContext()`) is a defensive test-stub hint with the exact stub given, not a gap. The `Arrays` import note in Task 3 is an explicit cleanup instruction. Resolved.

**Type consistency:** `WebJarUrlProvider` methods (`resolveResourcePath(String)`, `resolveUrl(String, HttpServletRequest)`, `isEnabled()`) are identical across Tasks 3, 6, 7. `DefaultWebJarUrlProvider` setters (`setEnabled`, `setAllowlist`, `setStaticContentPath`) match between impl (Task 3) and test constructions (Tasks 3, 6). `DefaultStaticContentLoader.setWebJarUrlProvider` name matches between Task 6 impl and Task 6 test. `WebJar(ValueStack, HttpServletRequest)` constructor + `setPath`/`setWebJarUrlProvider` match across Task 7 component, tag, model, and test. `StrutsConstants.STRUTS_WEBJARS_*` names match Tasks 2, 3, 4. ✓

**Risk to re-verify at execution time (per spec):** exact line numbers drift; re-locate insertion points by symbol, not line. Confirm `WebJarVersionLocator.path`/`fullPath` semantics against the pinned `1.1.3` (the impl assumes `path` returns `name/version/file` and `fullPath` returns the `META-INF/resources/webjars`-prefixed path).
