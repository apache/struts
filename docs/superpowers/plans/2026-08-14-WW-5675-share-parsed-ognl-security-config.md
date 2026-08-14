# WW-5675 Share Parsed OGNL Security Configuration — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Parse the OGNL security configuration once per container instead of once per `SecurityMemberAccess` instantiation, without changing OGNL allow/deny semantics.

**Architecture:** A new `Scope.SINGLETON` bean, `SecurityMemberAccessConfig`, takes over all sixteen `@Inject` configuration setters and does all parsing once per container, resolving dev-mode in `Initializable.init()`. `SecurityMemberAccess` stays `Scope.PROTOTYPE` and receives that bean through a single `@Inject` setter, copying immutable set references. The dev-mode lazy flip is deleted from the access path, and the allowlist two-set walk collapses into one precomputed union.

**Tech Stack:** Java 17 (`maven.compiler.release=17`), Maven, JUnit 4 (`org.junit.Test`), AssertJ, Mockito, Log4j2, Caffeine.

**Spec:** `docs/superpowers/specs/2026-08-14-WW-5675-security-member-access-config-sharing-design.md`

## Global Constraints

- **Branch:** `WW-5675-share-parsed-ognl-security-config`. Never push to `main`; finish via a PR.
- **Commit format:** `WW-5675 <type>(<scope>): <description>`, e.g. `WW-5675 perf(ognl): share parsed config across instances`. Every commit ends with the `Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>` trailer.
- **Never `git add -A` or `git add .`** in this repo — the tree carries roughly twenty long-lived untracked files. Stage explicit paths and verify with `git diff --cached --name-only` before every commit.
- **Core tests are JUnit 4** (`org.junit.Test`, `org.junit.Before`) or extend `XWorkTestCase`. A JUnit 5 `@Test` added to these suites silently never runs.
- **OGNL allow/deny semantics must not change.** No configuration may become more permissive. This is a security gate.
- **Test command:** `mvn test -DskipAssembly -pl core -Dtest=ClassName#methodName`
- **Full module suite:** `mvn test -DskipAssembly -pl core`
- Target version 7.4.0.

---

## File Structure

| File | Responsibility |
|---|---|
| `core/src/main/java/org/apache/struts2/util/ConfigParseUtil.java` | Modify: hoist the whitespace `Pattern` to a constant |
| `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccessConfig.java` | **Create**: owns all config parsing and dev-mode resolution, one instance per container |
| `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java` | Modify: register the new bean as `Scope.SINGLETON` |
| `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java` | Modify: receive config by setter, deprecate eleven setters, delete dev-mode state, collapse the allowlist union |
| `core/src/test/java/org/apache/struts2/util/ConfigParseUtilTest.java` | Test: whitespace validation behaviour preserved |
| `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessConfigTest.java` | **Create**: differential parsing + dev-mode resolution |
| `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessConfigSharingTest.java` | **Create**: sharing proof, instance isolation, subclass injection |

---

### Task 1: Hoist the whitespace pattern in `ConfigParseUtil`

`validatePackageNames` currently calls `Pattern.compile("\\s")` once per package name — roughly 58 recompiles of a trivial pattern per `SecurityMemberAccess` instantiation under the default configuration.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/util/ConfigParseUtil.java:142-146`
- Test: `core/src/test/java/org/apache/struts2/util/ConfigParseUtilTest.java`

**Interfaces:**
- Consumes: nothing.
- Produces: no signature change. `public static void validatePackageNames(Collection<String> packageNames)` keeps its exact behaviour and throws `ConfigurationException` on any whitespace.

- [ ] **Step 1: Write the failing test**

Create `core/src/test/java/org/apache/struts2/util/ConfigParseUtilTest.java` if it does not exist; otherwise append these methods to the existing class. If creating it, use the standard ASF license header copied verbatim from `ConfigParseUtil.java` lines 1-18.

```java
package org.apache.struts2.util;

import org.apache.struts2.config.ConfigurationException;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertThrows;

public class ConfigParseUtilTest {

    @Test
    public void validatePackageNamesAcceptsNamesWithoutWhitespace() {
        ConfigParseUtil.validatePackageNames(Set.of("java.lang", "org.apache.struts2", ""));
    }

    @Test
    public void validatePackageNamesRejectsSpace() {
        assertThrows(ConfigurationException.class,
                () -> ConfigParseUtil.validatePackageNames(Set.of("java.lang", "org.apache struts2")));
    }

    @Test
    public void validatePackageNamesRejectsTab() {
        assertThrows(ConfigurationException.class,
                () -> ConfigParseUtil.validatePackageNames(Set.of("java\tlang")));
    }

    @Test
    public void validatePackageNamesRejectsNewline() {
        assertThrows(ConfigurationException.class,
                () -> ConfigParseUtil.validatePackageNames(Set.of("java\nlang")));
    }

    @Test
    public void validatePackageNamesAcceptsEmptyCollection() {
        ConfigParseUtil.validatePackageNames(List.of());
    }
}
```

- [ ] **Step 2: Run the tests to verify they pass against the current implementation**

Run: `mvn test -DskipAssembly -pl core -Dtest=ConfigParseUtilTest`
Expected: PASS. These tests characterise existing behaviour before the refactor — they are the guard, not a red test. Do not proceed if any fails; that would mean the characterisation is wrong.

- [ ] **Step 3: Hoist the pattern**

In `core/src/main/java/org/apache/struts2/util/ConfigParseUtil.java`, add the constant next to the existing cache constants near line 45:

```java
    private static final Pattern WHITESPACE = Pattern.compile("\\s");
```

Then replace the body of `validatePackageNames`:

```java
    public static void validatePackageNames(Collection<String> packageNames) {
        if (packageNames.stream().anyMatch(s -> WHITESPACE.matcher(s).find())) {
            throw new ConfigurationException("Excluded package names could not be parsed due to erroneous whitespace characters: " + packageNames);
        }
    }
```

- [ ] **Step 4: Run the tests again**

Run: `mvn test -DskipAssembly -pl core -Dtest=ConfigParseUtilTest`
Expected: PASS, identical results to Step 2.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/util/ConfigParseUtil.java core/src/test/java/org/apache/struts2/util/ConfigParseUtilTest.java
git diff --cached --name-only
git commit -m "WW-5675 perf(config): hoist the whitespace pattern in validatePackageNames

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 2: Create the `SecurityMemberAccessConfig` bean

A standalone bean that owns all parsing. It does not touch `SecurityMemberAccess` yet, so it is independently testable.

**Files:**
- Create: `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccessConfig.java`
- Test: `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessConfigTest.java`

**Interfaces:**
- Consumes: `ConfigParseUtil.toClassesSet`, `toClassObjectsSet`, `toNewClassesSet`, `toNewPatternsSet`, `toNewPackageNamesSet`, `toPackageNamesSet` (all `public static`, unchanged).
- Produces, relied on by Task 3:
  - `boolean isAllowStaticFieldAccess()`
  - `Set<String> getExcludedClasses()`
  - `Set<Pattern> getExcludedPackageNamePatterns()`
  - `Set<String> getExcludedPackageNames()`
  - `Set<String> getExcludedPackageExemptClasses()`
  - `boolean isEnforceAllowlistEnabled()`
  - `Set<Class<?>> getAllowlistClasses()`
  - `Set<String> getAllowlistPackageNames()`
  - `boolean isDisallowProxyObjectAccess()`
  - `boolean isDisallowProxyMemberAccess()`
  - `boolean isDisallowDefaultPackageAccess()`

  The four excluded-* getters return the **effective** sets, with dev-mode already applied.

- [ ] **Step 1: Write the failing test**

Create `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessConfigTest.java` with the ASF license header copied verbatim from `SecurityMemberAccess.java` lines 1-18.

The `legacy*` methods below are **frozen oracles**: verbatim copies of the accumulation logic they replace. They must never be deleted, nor rewritten to delegate to production code — that would make the differential vacuous. This mirrors the approach used in `SecurityMemberAccessPackageMatchingTest` for WW-5674.

```java
package org.apache.struts2.ognl;

import org.junit.Test;

import java.util.Set;
import java.util.regex.Pattern;

import static org.apache.struts2.util.ConfigParseUtil.toNewClassesSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SecurityMemberAccessConfigTest {

    /**
     * Frozen oracle: the accumulation SecurityMemberAccess performed before WW-5675.
     * Never delete this, and never make it delegate to production code.
     */
    private static Set<String> legacyExcludedClassAccumulation(boolean allowStaticFieldAccess, String configured) {
        Set<String> excludedClasses = Set.of(Object.class.getName());
        if (!allowStaticFieldAccess) {
            excludedClasses = toNewClassesSet(excludedClasses, Class.class.getName());
        }
        return toNewClassesSet(excludedClasses, configured);
    }

    private SecurityMemberAccessConfig configWith(boolean devMode, String excludedClasses, String devModeExcludedClasses) {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();
        config.useDevMode(String.valueOf(devMode));
        config.useExcludedClasses(excludedClasses);
        config.useDevModeExcludedClasses(devModeExcludedClasses);
        config.init();
        return config;
    }

    @Test
    public void excludedClassesMatchLegacyAccumulation() {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();
        config.useExcludedClasses("java.lang.Runtime,java.lang.ProcessBuilder");
        config.init();

        assertEquals(legacyExcludedClassAccumulation(true, "java.lang.Runtime,java.lang.ProcessBuilder"),
                config.getExcludedClasses());
    }

    @Test
    public void disallowingStaticFieldAccessAddsClassToExclusions() {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();
        config.useAllowStaticFieldAccess("false");
        config.useExcludedClasses("java.lang.Runtime");
        config.init();

        assertFalse(config.isAllowStaticFieldAccess());
        assertEquals(legacyExcludedClassAccumulation(false, "java.lang.Runtime"), config.getExcludedClasses());
    }

    /**
     * The container iterates getDeclaredMethods(), whose order the JDK leaves unspecified.
     * The accumulation must therefore be commutative, as it was before WW-5675.
     */
    @Test
    public void setterOrderDoesNotAffectExcludedClasses() {
        SecurityMemberAccessConfig forward = new SecurityMemberAccessConfig();
        forward.useAllowStaticFieldAccess("false");
        forward.useExcludedClasses("java.lang.Runtime");
        forward.init();

        SecurityMemberAccessConfig reverse = new SecurityMemberAccessConfig();
        reverse.useExcludedClasses("java.lang.Runtime");
        reverse.useAllowStaticFieldAccess("false");
        reverse.init();

        assertEquals(forward.getExcludedClasses(), reverse.getExcludedClasses());
    }

    @Test
    public void devModeDisabledPublishesNormalExclusions() {
        SecurityMemberAccessConfig config = configWith(false, "java.lang.Runtime", "java.lang.ProcessBuilder");

        assertTrue(config.getExcludedClasses().contains("java.lang.Runtime"));
        assertFalse(config.getExcludedClasses().contains("java.lang.ProcessBuilder"));
    }

    @Test
    public void devModeEnabledPublishesDevModeExclusions() {
        SecurityMemberAccessConfig config = configWith(true, "java.lang.Runtime", "java.lang.ProcessBuilder");

        assertTrue(config.getExcludedClasses().contains("java.lang.ProcessBuilder"));
        assertFalse(config.getExcludedClasses().contains("java.lang.Runtime"));
    }

    @Test
    public void packageNamesAreStrippedOfDots() {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();
        config.useExcludedPackageNames("java.io.,.java.net");
        config.init();

        assertTrue(config.getExcludedPackageNames().contains("java.io"));
        assertTrue(config.getExcludedPackageNames().contains("java.net"));
    }

    @Test
    public void patternsAreCompiledOnce() {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();
        config.useExcludedPackageNamePatterns("^java\\.lang\\..*");
        config.init();

        Set<Pattern> patterns = config.getExcludedPackageNamePatterns();
        assertEquals(1, patterns.size());
        assertTrue(patterns.iterator().next().matcher("java.lang.Runtime").matches());
    }

    /**
     * A missing init() must fail closed: production exclusions, never the dev-mode ones.
     */
    @Test
    public void withoutInitTheNormalExclusionsApply() {
        SecurityMemberAccessConfig config = new SecurityMemberAccessConfig();
        config.useDevMode("true");
        config.useExcludedClasses("java.lang.Runtime");
        config.useDevModeExcludedClasses("java.lang.ProcessBuilder");

        assertTrue(config.getExcludedClasses().contains("java.lang.Runtime"));
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessConfigTest`
Expected: FAIL — compilation error, `SecurityMemberAccessConfig` does not exist.

- [ ] **Step 3: Create the bean**

Create `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccessConfig.java` with the ASF license header copied verbatim from `SecurityMemberAccess.java` lines 1-18.

Note that `init()` overwrites the normal fields with the dev-mode ones, exactly mirroring the `useDevModeConfiguration()` method it replaces. This is deliberate: if `init()` never runs, the normal production exclusions remain in force, which fails closed.

```java
package org.apache.struts2.ognl;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.inject.Initializable;

import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Collections.emptySet;
import static org.apache.struts2.StrutsConstants.STRUTS_ALLOWLIST_CLASSES;
import static org.apache.struts2.StrutsConstants.STRUTS_ALLOWLIST_PACKAGE_NAMES;
import static org.apache.struts2.util.ConfigParseUtil.toClassObjectsSet;
import static org.apache.struts2.util.ConfigParseUtil.toClassesSet;
import static org.apache.struts2.util.ConfigParseUtil.toNewClassesSet;
import static org.apache.struts2.util.ConfigParseUtil.toNewPackageNamesSet;
import static org.apache.struts2.util.ConfigParseUtil.toNewPatternsSet;
import static org.apache.struts2.util.ConfigParseUtil.toPackageNamesSet;
import static org.apache.struts2.util.DebugUtils.logWarningForFirstOccurrence;

/**
 * Holds the parsed OGNL security configuration for one container.
 * <p>
 * {@link SecurityMemberAccess} is a {@code Scope.PROTOTYPE} bean, constructed once per value stack and
 * again for each OGNL context. Parsing the roughly ninety configuration entries on every one of those
 * was the dominant cost identified by WW-5667. This bean is a {@code Scope.SINGLETON}, so the parsing
 * happens once per container and each {@code SecurityMemberAccess} merely copies immutable references.
 * <p>
 * Dev-mode is resolved in {@link #init()} rather than in a setter, because the container iterates
 * {@code getDeclaredMethods()}, whose order the JDK leaves unspecified. If {@code init()} never runs,
 * the normal production exclusions stay in force, which fails closed.
 *
 * @since Struts 7.4.0
 */
public class SecurityMemberAccessConfig implements Initializable {

    private static final Logger LOG = LogManager.getLogger(SecurityMemberAccessConfig.class);

    private boolean allowStaticFieldAccess = true;

    private Set<String> excludedClasses = Set.of(Object.class.getName());
    private Set<Pattern> excludedPackageNamePatterns = emptySet();
    private Set<String> excludedPackageNames = emptySet();
    private Set<String> excludedPackageExemptClasses = emptySet();

    private boolean isDevMode;
    private Set<String> devModeExcludedClasses = Set.of(Object.class.getName());
    private Set<Pattern> devModeExcludedPackageNamePatterns = emptySet();
    private Set<String> devModeExcludedPackageNames = emptySet();
    private Set<String> devModeExcludedPackageExemptClasses = emptySet();

    private boolean enforceAllowlistEnabled = false;
    private Set<Class<?>> allowlistClasses = emptySet();
    private Set<String> allowlistPackageNames = emptySet();

    private boolean disallowProxyObjectAccess = false;
    private boolean disallowProxyMemberAccess = false;
    private boolean disallowDefaultPackageAccess = false;

    @Override
    public void init() {
        if (!isDevMode) {
            return;
        }
        logWarningForFirstOccurrence("devMode", LOG,
                "DevMode enabled, using DevMode excluded classes and packages for OGNL security enforcement!");
        excludedClasses = devModeExcludedClasses;
        excludedPackageNamePatterns = devModeExcludedPackageNamePatterns;
        excludedPackageNames = devModeExcludedPackageNames;
        excludedPackageExemptClasses = devModeExcludedPackageExemptClasses;
    }

    @Inject(value = StrutsConstants.STRUTS_ALLOW_STATIC_FIELD_ACCESS, required = false)
    public void useAllowStaticFieldAccess(String allowStaticFieldAccess) {
        this.allowStaticFieldAccess = BooleanUtils.toBoolean(allowStaticFieldAccess);
        if (!this.allowStaticFieldAccess) {
            useExcludedClasses(Class.class.getName());
        }
    }

    @Inject(value = StrutsConstants.STRUTS_EXCLUDED_CLASSES, required = false)
    public void useExcludedClasses(String commaDelimitedClasses) {
        this.excludedClasses = toNewClassesSet(excludedClasses, commaDelimitedClasses);
    }

    @Inject(value = StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAME_PATTERNS, required = false)
    public void useExcludedPackageNamePatterns(String commaDelimitedPackagePatterns) {
        this.excludedPackageNamePatterns = toNewPatternsSet(excludedPackageNamePatterns, commaDelimitedPackagePatterns);
    }

    @Inject(value = StrutsConstants.STRUTS_EXCLUDED_PACKAGE_NAMES, required = false)
    public void useExcludedPackageNames(String commaDelimitedPackageNames) {
        this.excludedPackageNames = toNewPackageNamesSet(excludedPackageNames, commaDelimitedPackageNames);
    }

    @Inject(value = StrutsConstants.STRUTS_EXCLUDED_PACKAGE_EXEMPT_CLASSES, required = false)
    public void useExcludedPackageExemptClasses(String commaDelimitedClasses) {
        this.excludedPackageExemptClasses = toClassesSet(commaDelimitedClasses);
    }

    @Inject(value = StrutsConstants.STRUTS_ALLOWLIST_ENABLE, required = false)
    public void useEnforceAllowlistEnabled(String enforceAllowlistEnabled) {
        this.enforceAllowlistEnabled = BooleanUtils.toBoolean(enforceAllowlistEnabled);
        if (!this.enforceAllowlistEnabled) {
            String msg = "OGNL allowlist is disabled!" +
                    " We strongly recommend keeping it enabled to protect against critical vulnerabilities." +
                    " Set the configuration `{}=true` to enable it." +
                    " Please refer to the Struts 7.0 migration guide and security documentation for further information.";
            logWarningForFirstOccurrence("allowlist", LOG, msg, StrutsConstants.STRUTS_ALLOWLIST_ENABLE);
        }
    }

    @Inject(value = STRUTS_ALLOWLIST_CLASSES, required = false)
    public void useAllowlistClasses(String commaDelimitedClasses) {
        this.allowlistClasses = toClassObjectsSet(commaDelimitedClasses);
    }

    @Inject(value = STRUTS_ALLOWLIST_PACKAGE_NAMES, required = false)
    public void useAllowlistPackageNames(String commaDelimitedPackageNames) {
        this.allowlistPackageNames = toPackageNamesSet(commaDelimitedPackageNames);
    }

    @Inject(value = StrutsConstants.STRUTS_DISALLOW_PROXY_OBJECT_ACCESS, required = false)
    public void useDisallowProxyObjectAccess(String disallowProxyObjectAccess) {
        this.disallowProxyObjectAccess = BooleanUtils.toBoolean(disallowProxyObjectAccess);
    }

    @Inject(value = StrutsConstants.STRUTS_DISALLOW_PROXY_MEMBER_ACCESS, required = false)
    public void useDisallowProxyMemberAccess(String disallowProxyMemberAccess) {
        this.disallowProxyMemberAccess = BooleanUtils.toBoolean(disallowProxyMemberAccess);
    }

    @Inject(value = StrutsConstants.STRUTS_DISALLOW_DEFAULT_PACKAGE_ACCESS, required = false)
    public void useDisallowDefaultPackageAccess(String disallowDefaultPackageAccess) {
        this.disallowDefaultPackageAccess = BooleanUtils.toBoolean(disallowDefaultPackageAccess);
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void useDevMode(String devMode) {
        this.isDevMode = BooleanUtils.toBoolean(devMode);
    }

    @Inject(value = StrutsConstants.STRUTS_DEV_MODE_EXCLUDED_CLASSES, required = false)
    public void useDevModeExcludedClasses(String commaDelimitedClasses) {
        this.devModeExcludedClasses = toNewClassesSet(devModeExcludedClasses, commaDelimitedClasses);
    }

    @Inject(value = StrutsConstants.STRUTS_DEV_MODE_EXCLUDED_PACKAGE_NAME_PATTERNS, required = false)
    public void useDevModeExcludedPackageNamePatterns(String commaDelimitedPackagePatterns) {
        this.devModeExcludedPackageNamePatterns = toNewPatternsSet(devModeExcludedPackageNamePatterns, commaDelimitedPackagePatterns);
    }

    @Inject(value = StrutsConstants.STRUTS_DEV_MODE_EXCLUDED_PACKAGE_NAMES, required = false)
    public void useDevModeExcludedPackageNames(String commaDelimitedPackageNames) {
        this.devModeExcludedPackageNames = toNewPackageNamesSet(devModeExcludedPackageNames, commaDelimitedPackageNames);
    }

    @Inject(value = StrutsConstants.STRUTS_DEV_MODE_EXCLUDED_PACKAGE_EXEMPT_CLASSES, required = false)
    public void useDevModeExcludedPackageExemptClasses(String commaDelimitedClasses) {
        this.devModeExcludedPackageExemptClasses = toClassesSet(commaDelimitedClasses);
    }

    public boolean isAllowStaticFieldAccess() {
        return allowStaticFieldAccess;
    }

    public Set<String> getExcludedClasses() {
        return excludedClasses;
    }

    public Set<Pattern> getExcludedPackageNamePatterns() {
        return excludedPackageNamePatterns;
    }

    public Set<String> getExcludedPackageNames() {
        return excludedPackageNames;
    }

    public Set<String> getExcludedPackageExemptClasses() {
        return excludedPackageExemptClasses;
    }

    public boolean isEnforceAllowlistEnabled() {
        return enforceAllowlistEnabled;
    }

    public Set<Class<?>> getAllowlistClasses() {
        return allowlistClasses;
    }

    public Set<String> getAllowlistPackageNames() {
        return allowlistPackageNames;
    }

    public boolean isDisallowProxyObjectAccess() {
        return disallowProxyObjectAccess;
    }

    public boolean isDisallowProxyMemberAccess() {
        return disallowProxyMemberAccess;
    }

    public boolean isDisallowDefaultPackageAccess() {
        return disallowDefaultPackageAccess;
    }
}
```

- [ ] **Step 4: Run the test to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessConfigTest`
Expected: PASS, 9 tests.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccessConfig.java core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessConfigTest.java
git diff --cached --name-only
git commit -m "WW-5675 feat(ognl): add a container-singleton OGNL security config bean

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 3: Register the bean and wire it into `SecurityMemberAccess`

`SecurityMemberAccess` starts reading the shared configuration. Its own setters lose `@Inject` and become deprecated, but keep mutating the instance so the roughly 110 existing direct call sites behave identically.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java:416-420`
- Modify: `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java:473-536`
- Test: `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessConfigSharingTest.java`

**Interfaces:**
- Consumes: every getter from Task 2.
- Produces: `public void useConfig(SecurityMemberAccessConfig config)` on `SecurityMemberAccess`, annotated `@Inject`. Task 4 removes the dev-mode setters; Task 5 changes the allowlist walk.

- [ ] **Step 1: Write the failing test**

Create `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessConfigSharingTest.java` with the ASF license header copied verbatim from `SecurityMemberAccess.java` lines 1-18.

`XWorkTestCase` lives in `core/src/main/java/org/apache/struts2/XWorkTestCase.java` and exposes `protected Container container`. It is a JUnit 3 style `TestCase`, so test methods must be named `testXxx` — an `@Test` annotation alone will not run them.

```java
package org.apache.struts2.ognl;

import org.apache.struts2.XWorkTestCase;

import java.util.Set;

public class SecurityMemberAccessConfigSharingTest extends XWorkTestCase {

    /**
     * Reference identity proves no re-parsing occurred: any re-parse necessarily
     * allocates a fresh set.
     */
    public void testConfigDerivedSetsAreSharedAcrossInstances() throws Exception {
        SecurityMemberAccess first = container.getInstance(SecurityMemberAccess.class);
        SecurityMemberAccess second = container.getInstance(SecurityMemberAccess.class);

        assertNotSame("expected a prototype bean", first, second);

        Set<String> firstExcluded = SecurityMemberAccessTest.reflectField(first, "excludedClasses");
        Set<String> secondExcluded = SecurityMemberAccessTest.reflectField(second, "excludedClasses");
        assertSame("excluded classes were re-parsed per instance", firstExcluded, secondExcluded);

        Set<String> firstPackages = SecurityMemberAccessTest.reflectField(first, "excludedPackageNames");
        Set<String> secondPackages = SecurityMemberAccessTest.reflectField(second, "excludedPackageNames");
        assertSame("excluded package names were re-parsed per instance", firstPackages, secondPackages);
    }

    public void testConfigBeanIsASingleton() {
        assertSame(container.getInstance(SecurityMemberAccessConfig.class),
                container.getInstance(SecurityMemberAccessConfig.class));
    }

    /**
     * The shared sets must not be perturbed by a deprecated setter call on one instance.
     */
    public void testDeprecatedSetterDoesNotLeakToSiblings() throws Exception {
        SecurityMemberAccess mutated = container.getInstance(SecurityMemberAccess.class);
        SecurityMemberAccess untouched = container.getInstance(SecurityMemberAccess.class);
        SecurityMemberAccessConfig config = container.getInstance(SecurityMemberAccessConfig.class);

        Set<String> before = SecurityMemberAccessTest.reflectField(untouched, "excludedClasses");
        mutated.useExcludedClasses("java.lang.Runtime");
        Set<String> after = SecurityMemberAccessTest.reflectField(untouched, "excludedClasses");

        assertSame("a sibling instance was affected", before, after);
        assertFalse("the shared config was mutated", config.getExcludedClasses().contains("java.lang.Runtime"));

        Set<String> mutatedSet = SecurityMemberAccessTest.reflectField(mutated, "excludedClasses");
        assertTrue("the setter did not affect its own instance", mutatedSet.contains("java.lang.Runtime"));
    }

    /**
     * Guards the fail-open hole avoided by using setter rather than constructor injection:
     * a subclass calling the two-argument super constructor must still receive the config.
     */
    public void testSubclassReceivesConfigThroughInheritedSetter() throws Exception {
        SubclassedSecurityMemberAccess subclassed = new SubclassedSecurityMemberAccess(
                container.getInstance(ProviderAllowlist.class),
                container.getInstance(ThreadAllowlist.class));

        container.inject(subclassed);

        Set<String> excluded = SecurityMemberAccessTest.reflectField(subclassed, "excludedClasses");
        assertSame("subclass did not receive the shared config",
                container.getInstance(SecurityMemberAccessConfig.class).getExcludedClasses(), excluded);
    }

    static class SubclassedSecurityMemberAccess extends SecurityMemberAccess {
        SubclassedSecurityMemberAccess(ProviderAllowlist providerAllowlist, ThreadAllowlist threadAllowlist) {
            super(providerAllowlist, threadAllowlist);
        }
    }
}
```

`Container.inject(Object)` is declared at `core/src/main/java/org/apache/struts2/inject/Container.java:83`, so the call above drives the real injection path — including `ContainerImpl.addInjectors`, which recurses into superclasses at `ContainerImpl.java:97`. That recursion is exactly what this test exists to protect.

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessConfigSharingTest`
Expected: FAIL — `SecurityMemberAccessConfig` is not registered in the container, and the sets are still parsed per instance so `assertSame` fails.

- [ ] **Step 3: Register the bean**

In `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java`, inside `bootstrapFactories`, add the registration immediately after the `ThreadAllowlist` line:

```java
                .factory(ProviderAllowlist.class, Scope.SINGLETON)
                .factory(ThreadAllowlist.class, Scope.SINGLETON)
                .factory(SecurityMemberAccessConfig.class, Scope.SINGLETON)
```

Add the import alongside the existing OGNL imports near line 123:

```java
import org.apache.struts2.ognl.SecurityMemberAccessConfig;
```

- [ ] **Step 4: Add the config setter to `SecurityMemberAccess`**

In `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java`, add this method immediately after `setProxyService` (around line 113):

```java
    /**
     * Copies the shared, already-parsed configuration into this instance. This is the only injected
     * member that touches the configuration fields, so the unspecified order in which the container
     * iterates {@code getDeclaredMethods()} cannot affect the result.
     *
     * @since Struts 7.4.0
     */
    @Inject
    public void useConfig(SecurityMemberAccessConfig config) {
        this.allowStaticFieldAccess = config.isAllowStaticFieldAccess();
        this.excludedClasses = config.getExcludedClasses();
        this.excludedPackageNamePatterns = config.getExcludedPackageNamePatterns();
        this.excludedPackageNames = config.getExcludedPackageNames();
        this.excludedPackageExemptClasses = config.getExcludedPackageExemptClasses();
        this.enforceAllowlistEnabled = config.isEnforceAllowlistEnabled();
        this.allowlistClasses = config.getAllowlistClasses();
        this.allowlistPackageNames = config.getAllowlistPackageNames();
        this.disallowProxyObjectAccess = config.isDisallowProxyObjectAccess();
        this.disallowProxyMemberAccess = config.isDisallowProxyMemberAccess();
        this.disallowDefaultPackageAccess = config.isDisallowDefaultPackageAccess();
    }
```

- [ ] **Step 5: Deprecate the eleven remaining setters**

Still in `SecurityMemberAccess.java`, for each of these methods remove the `@Inject(...)` annotation and add `@Deprecated` plus a Javadoc `@deprecated` tag. Leave every method body exactly as it is.

Apply to: `useAllowStaticFieldAccess`, `useExcludedClasses`, `useExcludedPackageNamePatterns`, `useExcludedPackageNames`, `useExcludedPackageExemptClasses`, `useEnforceAllowlistEnabled`, `useAllowlistClasses`, `useAllowlistPackageNames`, `useDisallowProxyObjectAccess`, `useDisallowProxyMemberAccess`, `useDisallowDefaultPackageAccess`.

The pattern for each, shown for `useExcludedClasses`:

```java
    /**
     * @deprecated since 7.4.0, configuration is parsed once per container by
     * {@link SecurityMemberAccessConfig}. This method still mutates this instance and is retained for
     * tests and existing callers; it will be removed in Struts 8.0.0.
     */
    @Deprecated
    public void useExcludedClasses(String commaDelimitedClasses) {
        this.excludedClasses = toNewClassesSet(excludedClasses, commaDelimitedClasses);
    }
```

Do **not** annotate `useDevMode` or the four `useDevModeExcluded*` methods — Task 4 deletes those. Do **not** touch `useAcceptProperties` or `useExcludeProperties`; they carry per-request state, not configuration, and are not deprecated.

Deprecation is by annotation only. Do not add runtime warnings — roughly 110 test call sites would flood the build output.

- [ ] **Step 6: Run the new test**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessConfigSharingTest`
Expected: PASS, 4 tests.

- [ ] **Step 7: Run the existing security suites**

Run: `mvn test -DskipAssembly -pl core -Dtest='SecurityMemberAccessTest,ExternalSecurityMemberAccessTest,OgnlUtilTest,OgnlValueStackTest'`
Expected: PASS. Surefire needs comma separation; `+` is not valid.

If `SecurityMemberAccessTest` fails, the deprecated setters have not kept their exact semantics — fix the setter, not the test.

- [ ] **Step 8: Commit**

```bash
git add core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessConfigSharingTest.java
git diff --cached --name-only
git commit -m "WW-5675 perf(ognl): share parsed config across SecurityMemberAccess instances

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 4: Delete the dev-mode state from `SecurityMemberAccess`

The lazy dev-mode flip runs on the access path today. With dev-mode resolved once by the config bean, all of it goes.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java:89-94, 264, 538-574`
- Test: `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessConfigSharingTest.java`

**Interfaces:**
- Consumes: `SecurityMemberAccessConfig` getters, which already publish dev-mode-resolved sets.
- Produces: removal only. `useDevMode`, `useDevModeExcludedClasses`, `useDevModeExcludedPackageNamePatterns`, `useDevModeExcludedPackageNames`, `useDevModeExcludedPackageExemptClasses` and `useDevModeConfiguration` no longer exist on `SecurityMemberAccess`.

- [ ] **Step 1: Write the failing test**

Append to `SecurityMemberAccessConfigSharingTest`:

```java
    /**
     * Dev-mode exclusions must be in force from the first access, with no lazy flip.
     */
    public void testDevModeExclusionsApplyWithoutAnAccess() throws Exception {
        loadConfigurationProviders(new StubConfigurationProvider() {
            @Override
            public void register(ContainerBuilder builder, LocatableProperties props) {
                props.setProperty(StrutsConstants.STRUTS_DEVMODE, "true");
                props.setProperty(StrutsConstants.STRUTS_DEV_MODE_EXCLUDED_CLASSES, "java.lang.ProcessBuilder");
            }
        });

        SecurityMemberAccess sma = container.getInstance(SecurityMemberAccess.class);
        Set<String> excluded = SecurityMemberAccessTest.reflectField(sma, "excludedClasses");

        assertTrue("dev-mode exclusions were not applied at startup",
                excluded.contains("java.lang.ProcessBuilder"));
    }

    public void testDevModeMethodsAreGone() throws Exception {
        for (String name : new String[]{"useDevMode", "useDevModeExcludedClasses",
                "useDevModeExcludedPackageNamePatterns", "useDevModeExcludedPackageNames",
                "useDevModeExcludedPackageExemptClasses", "useDevModeConfiguration"}) {
            for (java.lang.reflect.Method method : SecurityMemberAccess.class.getDeclaredMethods()) {
                assertFalse("SecurityMemberAccess still declares " + name, method.getName().equals(name));
            }
        }
    }
```

Add these imports to the test file:

```java
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.test.StubConfigurationProvider;
import org.apache.struts2.util.location.LocatableProperties;
```

Note `LocatableProperties` is in `org.apache.struts2.util.location`, not `org.apache.struts2.config.entities`.

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessConfigSharingTest#testDevModeMethodsAreGone`
Expected: FAIL — the methods still exist.

- [ ] **Step 3: Delete the dev-mode state**

In `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java`:

Delete these fields (lines 89-94):

```java
    private volatile boolean isDevModeInit;
    private boolean isDevMode;
    private Set<String> devModeExcludedClasses = Set.of(Object.class.getName());
    private Set<Pattern> devModeExcludedPackageNamePatterns = emptySet();
    private Set<String> devModeExcludedPackageNames = emptySet();
    private Set<String> devModeExcludedPackageExemptClasses = emptySet();
```

Delete the `useDevModeConfiguration()` method entirely, and its call at the top of `checkExclusionList` so that the method begins:

```java
    protected boolean checkExclusionList(Object target, Member member) {
        Class<?> memberClass = member.getDeclaringClass();
```

Delete the five dev-mode setters: `useDevMode`, `useDevModeExcludedClasses`, `useDevModeExcludedPackageNamePatterns`, `useDevModeExcludedPackageNames`, `useDevModeExcludedPackageExemptClasses`.

- [ ] **Step 4: Run the tests**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessConfigSharingTest`
Expected: PASS, 6 tests.

- [ ] **Step 5: Run the existing security suites**

Run: `mvn test -DskipAssembly -pl core -Dtest='SecurityMemberAccessTest,ExternalSecurityMemberAccessTest'`
Expected: PASS. If a test called a dev-mode setter directly, the earlier survey was wrong — stop and report rather than deleting the test.

- [ ] **Step 6: Commit**

```bash
git add core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessConfigSharingTest.java
git diff --cached --name-only
git commit -m "WW-5675 refactor(ognl): drop the lazy dev-mode flip from the access path

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 5: Collapse the allowlist two-set walk into one precomputed union

WW-5674 merged the two allowlist walks with a three-argument helper. With the configuration shared, the union can be precomputed, so the helper reverts to a single set.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java:257-263, 392-441`
- Test: `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessPackageMatchingTest.java`

**Interfaces:**
- Consumes: `SecurityMemberAccessConfig.getAllowlistPackageNames()`.
- Produces: `static boolean isPackageBelongsToPackages(String packageName, Set<String> matchingPackages)` — two arguments, replacing the three-argument form. The three-argument `isClassBelongsToPackages(Class, Set, Set)` overload is deleted.

- [ ] **Step 1: Write the failing test**

Append to `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessPackageMatchingTest.java`. Do not modify or delete the frozen oracles already in that file.

```java
    /**
     * The union must never lose ALLOWLIST_REQUIRED_PACKAGES. Dropping them would be a silent
     * fail-open: Struts' own components would stop being allowlisted with nothing failing loudly.
     */
    @Test
    public void allowlistUnionRetainsRequiredPackagesAfterSetterCall() throws Exception {
        SecurityMemberAccess sma = new SecurityMemberAccess(null, null);
        sma.useAllowlistPackageNames("com.example.app");

        Set<String> union = SecurityMemberAccessTest.reflectField(sma, "allowlistPackageNamesUnion");

        assertTrue("configured package missing", union.contains("com.example.app"));
        assertTrue("required Struts package dropped from the allowlist",
                union.contains("org.apache.struts2.components"));
    }

    @Test
    public void allowlistUnionContainsRequiredPackagesByDefault() throws Exception {
        SecurityMemberAccess sma = new SecurityMemberAccess(null, null);

        Set<String> union = SecurityMemberAccessTest.reflectField(sma, "allowlistPackageNamesUnion");

        assertTrue(union.contains("org.apache.struts2.components"));
        assertTrue(union.contains("org.apache.struts2.views.jsp"));
        assertTrue(union.contains("org.apache.struts2.validator.validators"));
    }

    @Test
    public void singleSetWalkMatchesTheFrozenOracle() {
        for (String packageName : PACKAGE_NAMES) {
            for (Set<String> candidates : CANDIDATE_SETS) {
                assertEquals("mismatch for " + packageName + " against " + candidates,
                        legacyPrefixMatch(packageName, candidates),
                        SecurityMemberAccess.isPackageBelongsToPackages(packageName, candidates));
            }
        }
    }
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessPackageMatchingTest`
Expected: FAIL — `allowlistPackageNamesUnion` does not exist and `isPackageBelongsToPackages` still takes three arguments.

- [ ] **Step 3: Add the union field and its single computation site**

In `SecurityMemberAccess.java`, replace the allowlist field declarations (around line 97-98):

```java
    private Set<Class<?>> allowlistClasses = emptySet();
    private Set<String> allowlistPackageNames = emptySet();
    private Set<String> allowlistPackageNamesUnion = ALLOWLIST_REQUIRED_PACKAGES;
```

The union defaults to `ALLOWLIST_REQUIRED_PACKAGES` so that an instance which never receives configuration — the eight direct `new SecurityMemberAccess(null, null)` test constructions — still allowlists Struts' own packages, exactly as before.

Add the single computation site and its helper:

```java
    /**
     * The only place the allowlist union is computed. Both the injected configuration and the
     * deprecated setter route through here; splitting this in two would risk silently dropping
     * {@code ALLOWLIST_REQUIRED_PACKAGES}, which fails open.
     */
    private void applyAllowlistPackageNames(Set<String> packageNames) {
        this.allowlistPackageNames = packageNames;
        this.allowlistPackageNamesUnion = union(ALLOWLIST_REQUIRED_PACKAGES, packageNames);
    }

    private static Set<String> union(Set<String> required, Set<String> configured) {
        if (configured.isEmpty()) {
            return required;
        }
        Set<String> union = new HashSet<>(required);
        union.addAll(configured);
        return unmodifiableSet(union);
    }
```

Add these imports:

```java
import java.util.HashSet;

import static java.util.Collections.unmodifiableSet;
```

- [ ] **Step 4: Route both callers through it**

In `useConfig`, replace the direct assignment:

```java
        this.allowlistPackageNames = config.getAllowlistPackageNames();
```

with:

```java
        applyAllowlistPackageNames(config.getAllowlistPackageNames());
```

In the deprecated `useAllowlistPackageNames`, replace the body:

```java
    @Deprecated
    public void useAllowlistPackageNames(String commaDelimitedPackageNames) {
        applyAllowlistPackageNames(toPackageNamesSet(commaDelimitedPackageNames));
    }
```

- [ ] **Step 5: Collapse the walk**

Replace the last clause of `isClassAllowlisted`:

```java
                || isClassBelongsToPackages(clazz, allowlistPackageNamesUnion);
```

Delete the three-argument `isClassBelongsToPackages(Class, Set, Set)` overload entirely, and rewrite the remaining pair:

```java
    public static boolean isClassBelongsToPackages(Class<?> clazz, Set<String> matchingPackages) {
        return isPackageBelongsToPackages(toPackageName(clazz), matchingPackages);
    }

    /**
     * Tests whether the given package name, or any of its parent packages, is present in the set.
     * Walks the name in place rather than building the full prefix list, since this runs on the OGNL
     * member-access path. Shortest prefix first, so broad entries such as {@code java.io}
     * short-circuit earliest.
     *
     * <p>
     * The package name must not end in {@code '.'}. Such a name is probed one prefix more than by the
     * implementation this replaced, which matches more broadly — tightening exclusion but
     * <em>loosening</em> the allowlist. {@link Class#getPackageName()} cannot produce a trailing dot,
     * and {@code ConfigParseUtil.toPackageNamesSet} strips them from configured names, so every
     * current caller is safe; route any other string through here only after confirming the same.
     *
     * @param packageName      the package name to test, empty for the default package, never ending in {@code '.'}
     * @param matchingPackages the package names to match against
     * @return {@code true} if the package or any parent package is in the set
     */
    static boolean isPackageBelongsToPackages(String packageName, Set<String> matchingPackages) {
        if (matchingPackages.isEmpty()) {
            return false;
        }
        int idx = packageName.indexOf('.');
        while (idx != -1) {
            if (matchingPackages.contains(packageName.substring(0, idx))) {
                return true;
            }
            idx = packageName.indexOf('.', idx + 1);
        }
        return matchingPackages.contains(packageName);
    }
```

Remove the now-unused `emptySet` static import only if no other usage remains — check with `grep -n "emptySet" core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java`.

- [ ] **Step 6: Run the package-matching tests**

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessPackageMatchingTest`
Expected: PASS, 12 tests.

- [ ] **Step 7: Run the security suites**

Run: `mvn test -DskipAssembly -pl core -Dtest='SecurityMemberAccessTest,ExternalSecurityMemberAccessTest,SecurityMemberAccessConfigSharingTest,SecurityMemberAccessConfigTest'`
Expected: PASS.

- [ ] **Step 8: Commit**

```bash
git add core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessPackageMatchingTest.java
git diff --cached --name-only
git commit -m "WW-5675 perf(ognl): precompute the allowlist package union

Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>"
```

---

### Task 6: Verify the whole module and the plugins

**Files:** none modified unless a failure is found.

**Interfaces:** none.

- [ ] **Step 1: Run the full core suite**

Run: `mvn test -DskipAssembly -pl core`
Expected: PASS with zero failures and zero errors. For reference, the suite stood at 3158 tests when WW-5674 merged; this plan adds roughly 15.

Record the actual counts. Do not describe the work as complete without this output in hand.

- [ ] **Step 2: Run the plugin suites that touch `SecurityMemberAccess`**

Run: `mvn test -DskipAssembly -pl plugins/spring,plugins/cdi`
Expected: PASS. Both modules construct `new SecurityMemberAccess(null, null)` directly in proxy tests, which exercises the un-injected path.

- [ ] **Step 3: Confirm no stray `@Inject` remains on the deprecated setters**

Run:

```bash
grep -n -B2 "public void use" core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java | grep -A2 "@Inject"
```

Expected: only `useConfig` and `setProxyService` appear. Any other hit means a setter kept its annotation and will still be injected per instance, silently defeating the change.

- [ ] **Step 4: Confirm the dev-mode state is gone**

Run:

```bash
grep -n "devMode\|DevMode" core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java
```

Expected: no matches.

- [ ] **Step 5: Commit any fixes, then push and open a draft PR**

Only if Steps 1-4 are clean:

```bash
git push -u origin WW-5675-share-parsed-ognl-security-config
```

Open a draft PR titled `WW-5675 Share parsed OGNL security configuration across SecurityMemberAccess instances`, with `Fixes [WW-5675](https://issues.apache.org/jira/browse/WW-5675)` in the description.

The PR description must state plainly that removing the five dev-mode setters is a source-breaking change in a minor release, why it was accepted, and that a Migration Guide entry is owed for 7.4.0.

---

## Follow-ups (not part of this plan)

- File a Jira Improvement for 8.0.0 to remove the eleven deprecated setters, cross-referencing WW-5675 and WW-5678.
- Add the Version Notes and Migration Guide entry for the dev-mode setter removal.
- Update WW-5667 to record that this ticket, not WW-5674, is the one expected to move the reported 9%.
- WW-5678's first item is resolved here by Task 5; the remaining visibility narrowing stays with that ticket.
