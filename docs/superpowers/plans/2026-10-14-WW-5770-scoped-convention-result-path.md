# WW-5770 Scopable Convention Result Path Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let `<scoped-constant name="struts.convention.result.path">` on a parent package set the Convention
result path for every action under it.

**Architecture:** A `default` method `ScopedConstantProvider.getValue(name, PackageConfig)` resolves a constant
against an explicit package. `ConventionsService` gains package-aware `default` overloads; `ConventionsServiceImpl`
implements them with precedence `@ResultPath` → package scoped value → global. `DefaultResultMapBuilder` (startup)
and `ConventionUnknownHandler` (runtime) pass the package they already hold.

**Tech Stack:** Java 17, Struts container (`@Inject`), JUnit 4 + JUnit 3-style `TestCase`, EasyMock, AssertJ
(core only — the Convention module has no AssertJ), `StrutsJUnit4TestCase` from `struts2-junit-plugin`.

**Spec:** `docs/superpowers/specs/2026-10-14-WW-5770-scoped-convention-result-path-design.md`

## Global Constraints

- Fix version 7.5.0; new public API carries `@since 7.5.0`.
- Tests are JUnit 3 or 4 — never Jupiter. Match the target file's style; an `@Test` in a `TestCase` subclass never runs.
- `ScopedConstantProvider` must stay a functional interface (23 test sites stub it as `name -> ...`).
- `ConventionsServiceImpl`'s constructor signature does not change.
- No null guard on the injected `ScopedConstantProvider`; tests inject one.
- No new comments beyond Javadoc on new public methods; match the file's density.
- Commit messages: `WW-5770 <type>(<scope>): ...`, ending with `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`;
  no session links.
- Stage explicit paths only — never `git add -A`, never `git stash`.
- Use `./mvnw` (there is no `mvn` on the PATH); log to a file and read Maven's exit code, not a grep of the output.

## Review Focus

1. **Override without a trailing slash** (`/WEB-INF/admin`) — must behave like the global constant does: a slash is
   appended. Pinned in Task 3 (startup) and Task 4 (runtime).
2. **Empty override** (`value=""`) — must be used as-is, not fall back to the global path. Pinned in Task 2.
3. **Package-level `@ResultPath` (`package-info.java`)** — must beat the scoped value just as the class-level one
   does. Pinned in Task 2.
4. **Action-less request when the default parent package has no override** — global path, unchanged from today.
   Pinned in Task 4.
5. **A custom `ConventionsService` that implements only the one-argument methods** — keeps its old behaviour.
   Pinned in Task 2.

---

### Task 1: Package-aware lookup on `ScopedConstantProvider`

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/config/ScopedConstantProvider.java`
- Test: `core/src/test/java/org/apache/struts2/config/providers/StrutsScopedConstantProviderTest.java`

**Interfaces:**
- Produces: `default String getValue(String name, PackageConfig packageConfig)` on
  `org.apache.struts2.config.ScopedConstantProvider` — the package's `getAllScopedConstants().get(name)` when
  non-null, else `getValue(name)`; a `null` package goes straight to `getValue(name)`.

- [ ] **Step 1: Write the failing tests**

Append to `StrutsScopedConstantProviderTest` (JUnit 3 style, before `enterPackage`), and add imports
`org.apache.struts2.config.entities.PackageConfig`:

```java
    public void testExplicitPackageResolvesInheritedValue() {
        assertEquals("base", provider.getValue("sample.scopable", configuration.getPackageConfig("child")));
    }

    public void testExplicitPackageWinsOverInvocationPackage() {
        enterPackage("override");

        assertEquals("base", provider.getValue("sample.scopable", configuration.getPackageConfig("child")));
    }

    public void testExplicitPackageWithoutOverrideFallsBackToInvocation() {
        enterPackage("override");

        assertEquals("override", provider.getValue("sample.scopable", configuration.getPackageConfig("plain")));
    }

    public void testExplicitPackageWithoutOverrideGivesGlobalOutsideInvocation() {
        assertEquals("global", provider.getValue("sample.scopable", configuration.getPackageConfig("plain")));
    }

    public void testNullPackageFallsBackToInvocation() {
        enterPackage("override");

        assertEquals("override", provider.getValue("sample.scopable", null));
    }

    public void testExplicitPackageEmptyOverrideIsReturnedAsIs() {
        assertEquals("", provider.getValue("sample.scopable", configuration.getPackageConfig("empty")));
    }

    public void testExplicitPackageRejectsUnregisteredName() {
        PackageConfig plain = configuration.getPackageConfig("plain");

        assertThatThrownBy(() -> provider.getValue("sample.unknown", plain))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[sample.unknown]");
    }

    public void testLambdaProviderGetsPackageLookup() {
        ScopedConstantProvider lambda = name -> "fallback";

        assertEquals("base", lambda.getValue("sample.scopable", configuration.getPackageConfig("child")));
        assertEquals("fallback", lambda.getValue("sample.scopable", configuration.getPackageConfig("plain")));
        assertEquals("fallback", lambda.getValue("sample.scopable", null));
    }
```

- [ ] **Step 2: Run the tests to verify they fail**

```bash
./mvnw test -DskipAssembly -pl core -Dtest=StrutsScopedConstantProviderTest > /tmp/ww5770-t1.log 2>&1; echo "exit=$?"; grep -E "ERROR|Tests run" /tmp/ww5770-t1.log | head -20
```
Expected: non-zero exit, compilation error `method getValue in interface ScopedConstantProvider cannot be applied`.

- [ ] **Step 3: Add the default method**

In `ScopedConstantProvider.java` add the import `org.apache.struts2.config.entities.PackageConfig`, change the
class Javadoc's first sentence to "Resolves a scopable constant for the current action invocation or a given
package:" and add after `getValue(String name)`:

```java
    /**
     * @param name          a constant registered through a {@link ScopableConstants} bean
     * @param packageConfig the package whose scoped constants, inherited ones included, take precedence; may be
     *                      {@code null}
     * @return the package's scoped value, or else {@link #getValue(String)}
     * @throws IllegalArgumentException when {@code name} is not scopable
     * @since 7.5.0
     */
    default String getValue(String name, PackageConfig packageConfig) {
        String value = packageConfig != null ? packageConfig.getAllScopedConstants().get(name) : null;
        return value != null ? value : getValue(name);
    }
```

- [ ] **Step 4: Run the tests to verify they pass**

```bash
./mvnw test -DskipAssembly -pl core -Dtest='StrutsScopedConstantProviderTest,ScopedConstantProviderWiringTest,StrutsJavaConfigurationProviderTest,ConstantConfigTest' > /tmp/ww5770-t1.log 2>&1; echo "exit=$?"; grep -E "Tests run:|FAIL" /tmp/ww5770-t1.log | tail -5
```
Expected: `exit=0`, no failures.

- [ ] **Step 5: Install core so the Convention module compiles against it**

```bash
./mvnw install -DskipAssembly -DskipTests -pl core > /tmp/ww5770-install.log 2>&1; echo "exit=$?"
```
Expected: `exit=0`. Repeat this step whenever core changes again.

- [ ] **Step 6: Commit**

```bash
git add core/src/main/java/org/apache/struts2/config/ScopedConstantProvider.java core/src/test/java/org/apache/struts2/config/providers/StrutsScopedConstantProviderTest.java
git diff --cached --name-only
git commit -m "WW-5770 feat(core): resolve a scoped constant against an explicit package

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 2: Package-aware result path in `ConventionsService`

**Files:**
- Create: `plugins/convention/src/main/java/org/apache/struts2/convention/ConventionScopableConstants.java`
- Modify: `plugins/convention/src/main/resources/struts-plugin.xml` (after the `ConventionsService` bean, line ~40)
- Modify: `plugins/convention/src/main/java/org/apache/struts2/convention/ConventionsService.java`
- Modify: `plugins/convention/src/main/java/org/apache/struts2/convention/ConventionsServiceImpl.java`
- Create: `plugins/convention/src/test/java/org/apache/struts2/convention/ConventionsServiceImplTest.java`

**Interfaces:**
- Consumes: `ScopedConstantProvider.getValue(String, PackageConfig)` (Task 1).
- Produces:
  - `ConventionsService#determineResultPath(Class<?> actionClass, PackageConfig packageConfig)` — `default`, delegates to `determineResultPath(actionClass)`.
  - `ConventionsService#determineResultPath(ActionConfig actionConfig, PackageConfig packageConfig)` — `default`, delegates to `determineResultPath(actionConfig)`.
  - `ConventionsServiceImpl#setScopedConstantProvider(ScopedConstantProvider)` — `@Inject` setter.
  - `ConventionScopableConstants` — `ScopableConstants` returning `Set.of(ConventionConstants.CONVENTION_RESULT_PATH)`.

- [ ] **Step 1: Write the failing tests**

Create `ConventionsServiceImplTest.java` with the ASF license header copied from `ConventionsServiceImpl.java`:

```java
package org.apache.struts2.convention;

import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.config.entities.ResultTypeConfig;
import org.apache.struts2.convention.actions.NoAnnotationAction;
import org.apache.struts2.convention.actions.resultpath.ClassLevelResultPathAction;
import org.apache.struts2.convention.actions.resultpath.PackageLevelResultPathAction;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class ConventionsServiceImplTest {

    private static final String GLOBAL = "/WEB-INF/content/";
    private static final String SCOPED = "/WEB-INF/admin/";

    private ConventionsServiceImpl service;
    private PackageConfig scopedPackage;
    private PackageConfig plainPackage;

    @Before
    public void setUp() {
        service = new ConventionsServiceImpl(GLOBAL);
        service.setScopedConstantProvider(name -> GLOBAL);
        scopedPackage = childOf(SCOPED);
        plainPackage = new PackageConfig.Builder("plain").build();
    }

    @Test
    public void scopedValueOfParentPackageWinsOverGlobal() {
        assertEquals(SCOPED, service.determineResultPath(NoAnnotationAction.class, scopedPackage));
    }

    @Test
    public void classLevelResultPathWinsOverScopedValue() {
        assertEquals("/class-level", service.determineResultPath(ClassLevelResultPathAction.class, scopedPackage));
    }

    @Test
    public void packageLevelResultPathWinsOverScopedValue() {
        assertEquals("/package-level", service.determineResultPath(PackageLevelResultPathAction.class, scopedPackage));
    }

    @Test
    public void packageWithoutScopedValueGivesGlobal() {
        assertEquals(GLOBAL, service.determineResultPath(NoAnnotationAction.class, plainPackage));
    }

    @Test
    public void emptyScopedValueIsUsedAsIs() {
        assertEquals("", service.determineResultPath(NoAnnotationAction.class, childOf("")));
    }

    @Test
    public void nullPackageGivesGlobalWithoutConsultingProvider() {
        ConventionsServiceImpl withoutProvider = new ConventionsServiceImpl(GLOBAL);

        assertEquals(GLOBAL, withoutProvider.determineResultPath(NoAnnotationAction.class, null));
        assertEquals(GLOBAL, withoutProvider.determineResultPath(NoAnnotationAction.class));
        assertEquals(GLOBAL, withoutProvider.determineResultPath((ActionConfig) null, null));
        assertEquals(GLOBAL, withoutProvider.determineResultPath((ActionConfig) null));
    }

    @Test
    public void actionlessRequestUsesScopedValueOfPackage() {
        assertEquals(SCOPED, service.determineResultPath((ActionConfig) null, scopedPackage));
    }

    @Test
    public void actionConfigResolvesThroughItsClass() {
        ActionConfig annotated = actionConfig(ClassLevelResultPathAction.class.getName());
        ActionConfig plain = actionConfig(NoAnnotationAction.class.getName());

        assertEquals("/class-level", service.determineResultPath(annotated, scopedPackage));
        assertEquals(SCOPED, service.determineResultPath(plain, scopedPackage));
    }

    @Test
    public void unknownActionClassStillFails() {
        ActionConfig missing = actionConfig("com.example.MissingAction");

        RuntimeException e = assertThrows(RuntimeException.class,
                () -> service.determineResultPath(missing, scopedPackage));
        assertTrue(e.getMessage().contains("[com.example.MissingAction]"));
    }

    @Test
    public void customServiceWithOnlyOneArgumentMethodsKeepsItsBehaviour() {
        ConventionsService custom = new ConventionsService() {
            @Override
            public String determineResultPath(Class<?> actionClass) {
                return "/custom/";
            }

            @Override
            public String determineResultPath(ActionConfig actionConfig) {
                return "/custom/";
            }

            @Override
            public Map<String, ResultTypeConfig> getResultTypesByExtension(PackageConfig packageConfig) {
                return Collections.emptyMap();
            }
        };

        assertEquals("/custom/", custom.determineResultPath(NoAnnotationAction.class, scopedPackage));
        assertEquals("/custom/", custom.determineResultPath((ActionConfig) null, scopedPackage));
    }

    @Test
    public void resultPathIsScopable() {
        assertEquals(Set.of(ConventionConstants.CONVENTION_RESULT_PATH), new ConventionScopableConstants().getNames());
    }

    private static PackageConfig childOf(String scopedResultPath) {
        PackageConfig parent = new PackageConfig.Builder("admin-base")
                .addScopedConstant(ConventionConstants.CONVENTION_RESULT_PATH, scopedResultPath)
                .build();
        return new PackageConfig.Builder("admin-base#/admin").namespace("/admin").addParent(parent).build();
    }

    private static ActionConfig actionConfig(String className) {
        return new ActionConfig.Builder("admin-base#/admin", "foo", className).build();
    }
}
```

- [ ] **Step 2: Run the tests to verify they fail**

```bash
./mvnw test -DskipAssembly -pl plugins/convention -Dtest=ConventionsServiceImplTest > /tmp/ww5770-t2.log 2>&1; echo "exit=$?"; grep -E "ERROR|Tests run" /tmp/ww5770-t2.log | head -20
```
Expected: non-zero exit, compilation errors for `setScopedConstantProvider`, `ConventionScopableConstants` and the
two-argument `determineResultPath`.

- [ ] **Step 3: Add `ConventionScopableConstants` and register it**

Create `ConventionScopableConstants.java` (ASF header, same shape as
`core/src/main/java/org/apache/struts2/components/StrutsUiScopableConstants.java`):

```java
package org.apache.struts2.convention;

import org.apache.struts2.config.ScopableConstants;

import java.util.Set;

/**
 * Lets a package override the Convention result path with {@code <scoped-constant>}.
 *
 * @since 7.5.0
 */
public class ConventionScopableConstants implements ScopableConstants {

    @Override
    public Set<String> getNames() {
        return Set.of(ConventionConstants.CONVENTION_RESULT_PATH);
    }
}
```

In `struts-plugin.xml`, directly after the `ConventionsService` bean:

```xml
    <bean type="org.apache.struts2.config.ScopableConstants" name="convention"
          class="org.apache.struts2.convention.ConventionScopableConstants"/>
```

- [ ] **Step 4: Add the default methods to `ConventionsService`**

After `String determineResultPath(ActionConfig actionConfig);`:

```java
    /**
     * Locates the result location like {@link #determineResultPath(Class)}, using the package's scoped
     * {@code struts.convention.result.path} when the action carries no {@code @ResultPath}.
     *
     * @param   actionClass The action class.
     * @param   packageConfig The package the action belongs to, or null for the global default.
     * @return  The result location.
     * @since 7.5.0
     */
    default String determineResultPath(Class<?> actionClass, PackageConfig packageConfig) {
        return determineResultPath(actionClass);
    }

    /**
     * Locates the result location like {@link #determineResultPath(ActionConfig)}, using the package's scoped
     * {@code struts.convention.result.path} when the action carries no {@code @ResultPath}.
     *
     * @param   actionConfig (Optional) The configuration for the action, or null when there is no action.
     * @param   packageConfig The package whose scoped value applies, or null for the global default.
     * @return  The result location.
     * @since 7.5.0
     */
    default String determineResultPath(ActionConfig actionConfig, PackageConfig packageConfig) {
        return determineResultPath(actionConfig);
    }
```

- [ ] **Step 5: Implement them in `ConventionsServiceImpl`**

Add imports `org.apache.struts2.config.ScopedConstantProvider`. Add the field and setter after the constructor:

```java
    private ScopedConstantProvider scopedConstantProvider;
```
(declare next to `private String resultPath;`)

```java
    @Inject
    public void setScopedConstantProvider(ScopedConstantProvider scopedConstantProvider) {
        this.scopedConstantProvider = scopedConstantProvider;
    }
```

Replace both existing `determineResultPath` methods with:

```java
    /**
     * {@inheritDoc}
     */
    public String determineResultPath(Class<?> actionClass) {
        return determineResultPath(actionClass, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String determineResultPath(Class<?> actionClass, PackageConfig packageConfig) {
        String annotatedResultPath = findAnnotatedResultPath(actionClass);
        return annotatedResultPath != null ? annotatedResultPath : scopedResultPath(packageConfig);
    }

    /**
     * {@inheritDoc}
     */
    public String determineResultPath(ActionConfig actionConfig) {
        return determineResultPath(actionConfig, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String determineResultPath(ActionConfig actionConfig, PackageConfig packageConfig) {
        if (actionConfig == null) {
            return scopedResultPath(packageConfig);
        }

        try {
            return determineResultPath(ClassLoaderUtil.loadClass(actionConfig.getClassName(), this.getClass()), packageConfig);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid action class configuration that references an unknown " +
                "class named [" + actionConfig.getClassName() + "]", e);
        }
    }

    private String scopedResultPath(PackageConfig packageConfig) {
        if (packageConfig == null) {
            return resultPath;
        }
        return scopedConstantProvider.getValue(ConventionConstants.CONVENTION_RESULT_PATH, packageConfig);
    }

    private String findAnnotatedResultPath(Class<?> actionClass) {
        ResultPath resultPathAnnotation = AnnotationUtils.findAnnotation(actionClass, ResultPath.class);
        if (resultPathAnnotation == null) {
            return null;
        }
        if (resultPathAnnotation.value().equals("") && resultPathAnnotation.property().equals("")) {
            throw new ConfigurationException("The ResultPath annotation must have either" +
                " a value or property specified.");
        }

        String property = resultPathAnnotation.property();
        if (property.equals("")) {
            return resultPathAnnotation.value();
        }
        try {
            ResourceBundle strutsBundle = ResourceBundle.getBundle("struts");
            return strutsBundle.getString(property);
        } catch (Exception e) {
            throw new ConfigurationException("The action class [" + actionClass + "] defines" +
                " a @ResultPath annotation and a property definition however the" +
                " struts.properties could not be found in the classpath using ResourceBundle" +
                " OR the bundle exists but the property [" + property + "] is not defined" +
                " in the file.", e);
        }
    }
```

The exception messages are copied verbatim from the current implementation.

- [ ] **Step 6: Run the tests to verify they pass, plus the module**

```bash
./mvnw test -DskipAssembly -pl plugins/convention > /tmp/ww5770-t2.log 2>&1; echo "exit=$?"; grep -E "Tests run:|FAIL" /tmp/ww5770-t2.log | tail -5
```
Expected: `exit=0`. `DefaultResultMapBuilderTest` is still green because `DefaultResultMapBuilder` still calls the
one-argument method.

- [ ] **Step 7: Commit**

```bash
git add plugins/convention/src/main/java/org/apache/struts2/convention/ConventionScopableConstants.java plugins/convention/src/main/resources/struts-plugin.xml plugins/convention/src/main/java/org/apache/struts2/convention/ConventionsService.java plugins/convention/src/main/java/org/apache/struts2/convention/ConventionsServiceImpl.java plugins/convention/src/test/java/org/apache/struts2/convention/ConventionsServiceImplTest.java
git diff --cached --name-only
git commit -m "WW-5770 feat(convention): resolve the result path against the action's package

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 3: Startup — `DefaultResultMapBuilder` uses the package's result path

**Files:**
- Modify: `plugins/convention/src/main/java/org/apache/struts2/convention/DefaultResultMapBuilder.java:159`
- Modify: `plugins/convention/src/test/java/org/apache/struts2/convention/DefaultResultMapBuilderTest.java`
- Create: `plugins/convention/src/test/java/org/apache/struts2/convention/ConventionScopedResultPathTest.java`
- Create: `plugins/convention/src/test/resources/struts-convention-scoped-result-path.xml`
- Create: `plugins/convention/src/test/resources/scoped/message-success.ftl`

**Interfaces:**
- Consumes: `ConventionsService#determineResultPath(Class<?>, PackageConfig)`,
  `ConventionsServiceImpl#setScopedConstantProvider` (Task 2).

- [ ] **Step 1: Route the existing test sites through a helper**

In `DefaultResultMapBuilderTest`, use Edit with `replace_all` to replace `new ConventionsServiceImpl(` with
`newConventionsService(` (21 sites). Then add, next to `createPackageConfigBuilder`:

```java
    private ConventionsService newConventionsService(String resultPath) {
        ConventionsServiceImpl service = new ConventionsServiceImpl(resultPath);
        service.setScopedConstantProvider(name -> resultPath);
        return service;
    }
```

Check with `git diff --stat` that only this file changed and `grep -c "newConventionsService(" ` reports 22.

- [ ] **Step 2: Write the failing unit tests**

Replace the existing `createPackageConfigBuilder(String namespace)` with these two methods (the result types are
unchanged; the body only moves into a `builder` local so a parent can be added):

```java
    private PackageConfig createPackageConfigBuilder(String namespace) {
        return createPackageConfigBuilder(namespace, null);
    }

    private PackageConfig createPackageConfigBuilder(String namespace, String scopedResultPath) {
        ResultTypeConfig resultType = new ResultTypeConfig.Builder("dispatcher", "org.apache.struts2.result.ServletDispatcherResult").
                addParam("key", "value").addParam("key1", "value1").defaultResultParam("location").build();

        ResultTypeConfig redirect = new ResultTypeConfig.Builder("redirectAction",
                "org.apache.struts2.result.ServletActionRedirectResult").defaultResultParam("actionName").build();

        ResultTypeConfig ftlResultType = new ResultTypeConfig.Builder("freemarker",
                "org.apache.struts2.views.freemarker.FreemarkerResult").defaultResultParam("location").build();

        PackageConfig.Builder builder = new PackageConfig.Builder("package").
                namespace(namespace).
                defaultResultType("dispatcher").
                addResultTypeConfig(resultType).
                addResultTypeConfig(redirect).
                addResultTypeConfig(ftlResultType);
        if (scopedResultPath != null) {
            builder.addParent(new PackageConfig.Builder("scoped-parent")
                    .addScopedConstant(ConventionConstants.CONVENTION_RESULT_PATH, scopedResultPath)
                    .build());
        }
        return builder.build();
    }
```

Then add the tests:

```java
    @Test
    public void testScopedResultPathFromParentPackage() {
        ServletContext context = mockServletContext("/WEB-INF/admin");

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace", "/WEB-INF/admin/");
        this.conventionsService = newConventionsService("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, null, "action", packageConfig);
        verify(context, "/WEB-INF/admin", results, false);
    }

    @Test
    public void testScopedResultPathWithoutTrailingSlash() {
        ServletContext context = mockServletContext("/WEB-INF/admin");

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace", "/WEB-INF/admin");
        this.conventionsService = newConventionsService("/WEB-INF/location");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(NoAnnotationAction.class, null, "action", packageConfig);
        verify(context, "/WEB-INF/admin", results, false);
    }

    @Test
    public void testResultPathAnnotationWinsOverScopedResultPath() {
        ServletContext context = mockServletContext("/class-level");

        PackageConfig packageConfig = createPackageConfigBuilder("/namespace", "/WEB-INF/admin/");
        this.conventionsService = newConventionsService("/not-used");
        DefaultResultMapBuilder builder = new DefaultResultMapBuilder(context, container, "dispatcher,velocity,freemarker");
        Map<String, ResultConfig> results = builder.build(ClassLevelResultPathAction.class, null, "action", packageConfig);
        verify(context, "/class-level", results, false);
    }
```

- [ ] **Step 3: Write the failing integration test**

`struts-convention-scoped-result-path.xml` (copy the license comment block from
`struts-convention-configuration.xml`):

```xml
<!DOCTYPE struts PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 7.5//EN"
        "https://struts.apache.org/dtds/struts-7.5.dtd">

<struts>
    <constant name="struts.convention.result.path" value="/"/>

    <package name="convention-default" extends="struts-default">
        <scoped-constant name="struts.convention.result.path" value="/scoped/"/>
    </package>
</struts>
```

`scoped/message-success.ftl`:

```
<#--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
-->
<html>
<body>
This is the scoped view ${message}
</body>
</html>
```

`ConventionScopedResultPathTest.java` (ASF header; same shape as `ConventionJUnit4Test`):

```java
package org.apache.struts2.convention;

import actions.MessageAction;
import org.apache.struts2.junit.StrutsJUnit4TestCase;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ConventionScopedResultPathTest extends StrutsJUnit4TestCase<MessageAction> {

    @Test
    public void testParentPackageScopedResultPath() throws Exception {
        String output = executeAction("/message.action");

        assertTrue(output, output.contains("This is the scoped view Hello World"));
    }

    @Override
    protected String getConfigPath() {
        return "struts-plugin.xml,struts-convention-scoped-result-path.xml";
    }
}
```

The root `message-success.ftl` ("This is the view …") stays where it is: if the override is ignored, that file
is picked and the assertion fails.

- [ ] **Step 4: Run the tests to verify they fail**

```bash
./mvnw test -DskipAssembly -pl plugins/convention -Dtest='DefaultResultMapBuilderTest,ConventionScopedResultPathTest' > /tmp/ww5770-t3.log 2>&1; echo "exit=$?"; grep -E "Tests run:|FAIL|testScoped|testResultPathAnnotationWins|testParentPackage" /tmp/ww5770-t3.log | head -20
```
Expected: non-zero exit; `testScopedResultPathFromParentPackage`, `testScopedResultPathWithoutTrailingSlash`
and `testParentPackageScopedResultPath` fail (the EasyMock servlet context sees `/WEB-INF/location/namespace/`;
the integration output is "This is the view Hello World"). `testResultPathAnnotationWinsOverScopedResultPath`
already passes — that is expected, it guards precedence. If `ConventionScopedResultPathTest` fails at startup
instead of on the assertion, read the cause before going on — it is the spec's startup-ordering check.

- [ ] **Step 5: Pass the package at the call site**

`DefaultResultMapBuilder.java:159`:

```java
        String defaultResultPath = conventionsService.determineResultPath(actionClass, packageConfig);
```

- [ ] **Step 6: Run the module to verify everything passes**

```bash
./mvnw test -DskipAssembly -pl plugins/convention > /tmp/ww5770-t3.log 2>&1; echo "exit=$?"; grep -E "Tests run:|FAIL" /tmp/ww5770-t3.log | tail -5
```
Expected: `exit=0`.

- [ ] **Step 7: Commit**

```bash
git add plugins/convention/src/main/java/org/apache/struts2/convention/DefaultResultMapBuilder.java plugins/convention/src/test/java/org/apache/struts2/convention/DefaultResultMapBuilderTest.java plugins/convention/src/test/java/org/apache/struts2/convention/ConventionScopedResultPathTest.java plugins/convention/src/test/resources/struts-convention-scoped-result-path.xml plugins/convention/src/test/resources/scoped/message-success.ftl
git diff --cached --name-only
git commit -m "WW-5770 feat(convention): honour the scoped result path when building results

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 4: Runtime — `ConventionUnknownHandler` uses the package's result path

**Files:**
- Modify: `plugins/convention/src/main/java/org/apache/struts2/convention/ConventionUnknownHandler.java` (lines 134, 282, 385-386)
- Modify: `plugins/convention/src/test/java/org/apache/struts2/convention/ConventionUnknownHandlerTest.java`

**Interfaces:**
- Consumes: `ConventionsService#determineResultPath(ActionConfig, PackageConfig)` (Task 2).
- Produces: `protected String determinePath(ActionConfig actionConfig, PackageConfig packageConfig, String namespace)`
  replacing `determinePath(ActionConfig, String)`.

- [ ] **Step 1: Write the failing tests**

In `ConventionUnknownHandlerTest` (JUnit 3 `TestCase`, EasyMock static imports already present), add imports
`java.util.ArrayList`, `java.util.List`, `org.apache.struts2.convention.actions.NoAnnotationAction`, and these
tests plus helpers:

```java
    private static final String DEFAULT_PARENT = "convention-default";

    public void testUnknownActionUsesScopedResultPathOfDefaultParentPackage() throws Exception {
        List<String> requested = new ArrayList<>();
        ConventionUnknownHandler handler = scopedHandler(recordingServletContext(requested),
                packageWithResultPath(DEFAULT_PARENT, "", "/WEB-INF/scoped/"), null);

        handler.handleUnknownAction("", "does-not-exist");

        assertFalse(requested.isEmpty());
        for (String path : requested) {
            assertTrue(path, path.startsWith("/WEB-INF/scoped/does-not-exist"));
        }
    }

    public void testUnknownActionWithoutOverrideUsesGlobalResultPath() throws Exception {
        List<String> requested = new ArrayList<>();
        ConventionUnknownHandler handler = scopedHandler(recordingServletContext(requested),
                new PackageConfig.Builder(DEFAULT_PARENT).build(), null);

        handler.handleUnknownAction("", "does-not-exist");

        assertFalse(requested.isEmpty());
        for (String path : requested) {
            assertTrue(path, path.startsWith("/WEB-INF/content/does-not-exist"));
        }
    }

    public void testUnknownResultUsesScopedResultPathOfActionPackage() throws Exception {
        List<String> requested = new ArrayList<>();
        PackageConfig actionPackage = packageWithResultPath("admin", "/admin", "/WEB-INF/scoped/");
        ConventionUnknownHandler handler = scopedHandler(recordingServletContext(requested),
                new PackageConfig.Builder(DEFAULT_PARENT).build(), actionPackage);
        ActionConfig actionConfig = new ActionConfig.Builder("admin", "foo", NoAnnotationAction.class.getName()).build();

        handler.handleUnknownResult(null, "foo", actionConfig, "success");

        assertFalse(requested.isEmpty());
        for (String path : requested) {
            assertTrue(path, path.startsWith("/WEB-INF/scoped/admin/"));
        }
    }

    public void testDeterminePathAppendsSlashToScopedResultPath() {
        PackageConfig parent = packageWithResultPath(DEFAULT_PARENT, "", "/WEB-INF/scoped");
        ConventionUnknownHandler handler = scopedHandler(null, parent, null);

        assertEquals("/WEB-INF/scoped/admin/", handler.determinePath(null, parent, "/admin"));
    }

    private ServletContext recordingServletContext(List<String> requested) throws Exception {
        ServletContext servletContext = createNiceMock(ServletContext.class);
        expect(servletContext.getResource(anyString())).andAnswer(() -> {
            requested.add((String) getCurrentArguments()[0]);
            return null;
        }).anyTimes();
        replay(servletContext);
        return servletContext;
    }

    private static PackageConfig packageWithResultPath(String name, String namespace, String resultPath) {
        PackageConfig parent = new PackageConfig.Builder(name + "-base")
                .addScopedConstant(ConventionConstants.CONVENTION_RESULT_PATH, resultPath)
                .build();
        return new PackageConfig.Builder(name).namespace(namespace).addParent(parent).build();
    }

    private ConventionUnknownHandler scopedHandler(ServletContext servletContext, PackageConfig defaultParent,
                                                   PackageConfig actionPackage) {
        Configuration configuration = createNiceMock(Configuration.class);
        expect(configuration.getPackageConfig(DEFAULT_PARENT)).andStubReturn(defaultParent);
        if (actionPackage != null) {
            expect(configuration.getPackageConfig(actionPackage.getName())).andStubReturn(actionPackage);
        }
        RuntimeConfiguration runtime = createNiceMock(RuntimeConfiguration.class);
        expect(configuration.getRuntimeConfiguration()).andStubReturn(runtime);

        ConventionsServiceImpl service = new ConventionsServiceImpl("/WEB-INF/content/");
        service.setScopedConstantProvider(name -> "/WEB-INF/content/");
        Container container = createNiceMock(Container.class);
        expect(container.getInstance(String.class, ConventionConstants.CONVENTION_CONVENTIONS_SERVICE)).andStubReturn("test");
        expect(container.getInstance(ConventionsService.class, "test")).andStubReturn(service);

        replay(configuration, runtime, container);

        return new ConventionUnknownHandler(configuration, null, servletContext, container, DEFAULT_PARENT,
                "false", "-");
    }
```

In the existing `container()` helper, replace

```java
        ActionConfig actionConfig = null;
        expect(service.determineResultPath(actionConfig)).andReturn("");
```

with

```java
        expect(service.determineResultPath((ActionConfig) null, packageConfiguration)).andReturn("");
```

- [ ] **Step 2: Run the tests to verify they fail**

```bash
./mvnw test -DskipAssembly -pl plugins/convention -Dtest=ConventionUnknownHandlerTest > /tmp/ww5770-t4.log 2>&1; echo "exit=$?"; grep -E "ERROR|Tests run|FAIL" /tmp/ww5770-t4.log | head -20
```
Expected: non-zero exit — compilation error on the three-argument `determinePath`. (Without that test the two
`ScopedResultPath` tests fail on paths starting `/WEB-INF/content/`.)

- [ ] **Step 3: Change `determinePath` and its two callers**

```java
    protected String determinePath(ActionConfig actionConfig, PackageConfig packageConfig, String namespace) {
        String finalPrefix = conventionsService.determineResultPath(actionConfig, packageConfig);
```

Add to its Javadoc, after `@param actionConfig`:

```java
     * @param packageConfig The package whose scoped result path applies: the action's package, or the default
     *                      parent package when there is no action.
```

Line 134 in `handleUnknownAction`:

```java
        String pathPrefix = determinePath(null, parentPackage, namespace);
```

Line 282 in `handleUnknownResult`:

```java
        String pathPrefix = determinePath(actionConfig, pkg, ns);
```

- [ ] **Step 4: Run the module to verify everything passes**

```bash
./mvnw test -DskipAssembly -pl plugins/convention > /tmp/ww5770-t4.log 2>&1; echo "exit=$?"; grep -E "Tests run:|FAIL" /tmp/ww5770-t4.log | tail -5
```
Expected: `exit=0`.

- [ ] **Step 5: Commit**

```bash
git add plugins/convention/src/main/java/org/apache/struts2/convention/ConventionUnknownHandler.java plugins/convention/src/test/java/org/apache/struts2/convention/ConventionUnknownHandlerTest.java
git diff --cached --name-only
git commit -m "WW-5770 feat(convention): honour the scoped result path for unknown actions and results

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 5: Full verification

- [ ] **Step 1: Compile the whole reactor and run core + Convention tests**

```bash
./mvnw install -DskipAssembly -DskipTests > /tmp/ww5770-reactor.log 2>&1; echo "exit=$?"
./mvnw test -DskipAssembly -pl core,plugins/convention,plugins/javatemplates > /tmp/ww5770-tests.log 2>&1; echo "exit=$?"; grep -E "Tests run:.*Fail|BUILD" /tmp/ww5770-tests.log | tail -8
```
Expected: both `exit=0`. The javatemplates module proves its `name -> null` lambdas still compile.

- [ ] **Step 2: Check for stray files**

```bash
git status --short
```
Expected: no `??` entries from this work (review agents leave probe tests behind).

---

### Task 6: struts-site documentation

Separate repository: `~/Projects/Apache/struts-site`, branch `WW-5770-scoped-convention-result-path` from `main`.

**Files:**
- Modify: `source/core-developers/package-configuration.md` (the list after "Since Struts 7.5.0 these core constants are scopable:", line ~120)
- Modify: `source/plugins/convention/index.md` (end of "### ResultPath annotation", line ~641)

- [ ] **Step 1: List the constant on the package-configuration page**

After the core-constants bullet list, add:

```markdown
The [Convention plugin](../plugins/convention/) makes `struts.convention.result.path` scopable, see
[Result path per package](../plugins/convention/#result-path-per-package).
```

- [ ] **Step 2: Document it on the Convention page**

After the last paragraph of "### ResultPath annotation":

~~~markdown
#### Result path per package

Since Struts 7.5.0 a parent package can set the result path for all actions placed under it with a
[scoped constant](../../core-developers/package-configuration#scoped-constants):

```xml
<package name="admin-base" extends="convention-default">
    <scoped-constant name="struts.convention.result.path" value="/WEB-INF/admin/"/>
</package>
```

The result path of an action is taken from, in order:

1. `@ResultPath` on the action class or its Java package
2. the `struts.convention.result.path` scoped constant of the action's package, including inherited ones
3. the global `struts.convention.result.path` constant

A request without an action class, served straight from a template, uses the scoped constant of the default
parent package (`struts.convention.default.parent.package`).
~~~

- [ ] **Step 3: Check both anchors exist**

```bash
grep -n "^## Scoped constants" ~/Projects/Apache/struts-site/source/core-developers/package-configuration.md
grep -n "^#### Result path per package" ~/Projects/Apache/struts-site/source/plugins/convention/index.md
```
Expected: one hit each, matching the `#scoped-constants` and `#result-path-per-package` link targets.

- [ ] **Step 4: Commit**

```bash
git -C ~/Projects/Apache/struts-site add source/core-developers/package-configuration.md source/plugins/convention/index.md
git -C ~/Projects/Apache/struts-site commit -m "WW-5770 docs: document the scopable Convention result path

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

## After the tasks

- Run `/security-review` on the struts branch diff before `gh pr create` (CLAUDE.md), reading `SECURITY.md` and
  `THREAT_MODEL.md` first.
- PR titles: `WW-5770 Make struts.convention.result.path scopable per package` (struts) and
  `WW-5770 docs: document the scopable Convention result path` (struts-site); body links
  `Fixes [WW-5770](https://issues.apache.org/jira/browse/WW-5770)`.
- 7.5.0 migration notes (Version Notes, at release time): new default method
  `ScopedConstantProvider.getValue(String, PackageConfig)`; `ConventionUnknownHandler.determinePath` signature change.
