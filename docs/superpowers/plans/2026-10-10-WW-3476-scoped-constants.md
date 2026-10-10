# WW-3476 Package-Scoped Constants Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let a `<package>` override selected Struts constants with `<scoped-constant name value/>`, inherited through `extends`, read at request time through a `ScopedConstantProvider` that falls back to the global constant.

**Architecture:** `PackageConfig` stores each package's own overrides and, at `build()`, an eagerly merged view (parents first, own entries win). Components opt a constant in by registering a `ScopableConstants` bean; `DefaultConfiguration` rejects any `<scoped-constant>` whose name no bean registered. `StrutsScopedConstantProvider.getValue(name)` reads the current `ActionInvocation`'s package and falls back to `container.getInstance(String.class, name)` — the singleton's injected value is never touched.

**Tech Stack:** Java 17, Struts core config/DI (`org.apache.struts2.inject`), XML DTD, JUnit 3 (`XWorkTestCase`) and JUnit 4, AssertJ 3.27.

**Spec:** `docs/superpowers/specs/2026-10-10-WW-3476-scoped-constants-design.md`

## Global Constraints

- Branch: `WW-3476-scoped-constants` (already exists, holds the spec commit). Never push to `main`.
- Commit messages start with `WW-3476 ` and end with `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- Stage explicit paths only — never `git add -A` / `git add .`; check `git diff --cached --name-only` before every commit.
- Never `git stash` in this repo.
- Tests are JUnit 3 or 4 — never Jupiter. In a class extending `XWorkTestCase`, methods are `public void testXxx()` with no `@Test`.
- Maven: the Bash tool has no `mvn`; use `./mvnw` from the repo root, log to `core/target/ww3476.log`, and read Maven's exit code — never trust a grep over the output alone.
- Every new file starts with the ASF license header, copied verbatim from `core/src/main/java/org/apache/struts2/config/entities/PackageConfig.java` (Java) or `core/src/test/resources/org/apache/struts2/config/providers/xwork-test-package-final.xml` (XML).
- New public API carries `@since 7.5.0`. Comments: default to none; short Javadoc on new public types/methods only.
- Purely additive: do not edit `struts-6.5.dtd` or any older DTD; no existing constructor/interface signature changes.
- No constant is registered as scopable in `src/main` — `ScopableConstants` beans exist only under `src/test`.
- Default implementation name prefix is `Struts*`.
- Watch-it-fail mutations are applied with the Edit tool and confirmed with `git diff --stat` (BSD `sed -i` silently no-ops here), then reverted with Edit.

## Review Focus

1. **An abstract package declares an unregistered scoped constant** — must still fail at startup, even though `buildRuntimeConfiguration` skips abstract packages when collecting actions. Test: Task 3 `abstractPackageIsValidatedToo`.
2. **`<scoped-constant value=""/>`** — an empty override is a real value; the provider must return `""`, not fall back to the global constant. Test: Task 4 `testEmptyOverrideIsReturnedAsIs`.
3. **Invocation without a resolvable package** (proxy null, `ActionConfig` null, package name not in `Configuration`) — must return the global value, never throw. Tests: Task 4 `testGlobalValueWhenInvocationHasNoProxy`, `testGlobalValueWhenProxyHasNoConfig`, `testGlobalValueWhenPackageIsUnknown`.
4. **Scopable constant with no global value** — outside an override the provider returns `null` (as `container.getInstance` does), it must not throw. Test: Task 4 `testUnsetConstantWithoutOverrideIsNull`.
5. **Several `ScopableConstants` beans** — the scopable set is the union; a name from the second bean must validate. Test: Task 3 `namesFromAllBeansAreScopable`.

---

## File Structure

| File | Responsibility |
|---|---|
| `core/src/main/java/org/apache/struts2/config/entities/PackageConfig.java` (modify) | Store own + merged scoped constants |
| `core/src/main/resources/struts-7.5.dtd` (create) | DTD with `<scoped-constant>` |
| `core/src/main/java/org/apache/struts2/config/StrutsXmlConfigurationProvider.java` (modify) | Map the 7.5 public id to the DTD |
| `core/src/main/java/org/apache/struts2/config/providers/XmlDocConfigurationProvider.java` (modify) | Parse `<scoped-constant>` |
| `core/src/main/java/org/apache/struts2/config/ScopableConstants.java` (create) | Registry contract + union helper |
| `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java` (modify) | Startup validation |
| `core/src/main/java/org/apache/struts2/config/ScopedConstantProvider.java` (create) | Lookup contract |
| `core/src/main/java/org/apache/struts2/config/StrutsScopedConstantProvider.java` (create) | Default lookup |
| `core/src/main/java/org/apache/struts2/StrutsConstants.java` (modify) | Bean-selection constant |
| `core/src/main/java/org/apache/struts2/config/StrutsBeanSelectionProvider.java` (modify) | Alias the default bean |
| `core/src/main/resources/struts-beans.xml` (modify) | Runtime binding |
| `core/src/main/java/org/apache/struts2/config/providers/StrutsDefaultConfigurationProvider.java` (modify) | Test-harness binding |
| `core/src/test/java/org/apache/struts2/config/SampleScopableConstants.java`, `SecondScopableConstants.java` (create) | Test-only registry beans |
| Tests + fixtures listed per task | |

---

### Task 1: `PackageConfig` stores and merges scoped constants

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/config/entities/PackageConfig.java`
- Test: `core/src/test/java/org/apache/struts2/config/entities/PackageConfigTest.java` (JUnit 3, extends `XWorkTestCase`)

**Interfaces:**
- Produces: `PackageConfig.Builder addScopedConstant(String name, String value)`; `Map<String, String> PackageConfig.getScopedConstants()` (own only, unmodifiable after `build()`); `Map<String, String> PackageConfig.getAllScopedConstants()` (merged, unmodifiable, empty before `build()`).

- [ ] **Step 1: Write the failing tests** — append to `PackageConfigTest` (add `import java.util.Map;`):

```java
    public void testScopedConstantsChildOverridesParent() {
        PackageConfig parent = new PackageConfig.Builder("parent")
                .addScopedConstant("first", "parent-first")
                .addScopedConstant("second", "parent-second")
                .build();

        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(parent)
                .addScopedConstant("second", "child-second")
                .build();

        assertEquals(Map.of("second", "child-second"), child.getScopedConstants());
        assertEquals(Map.of("first", "parent-first", "second", "child-second"), child.getAllScopedConstants());
    }

    public void testScopedConstantsInheritedThroughTwoLevels() {
        PackageConfig grandparent = new PackageConfig.Builder("grandparent")
                .addScopedConstant("first", "grandparent-first")
                .build();
        PackageConfig parent = new PackageConfig.Builder("parent")
                .addParent(grandparent)
                .build();

        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(parent)
                .build();

        assertEquals(Map.of(), child.getScopedConstants());
        assertEquals(Map.of("first", "grandparent-first"), child.getAllScopedConstants());
    }

    public void testScopedConstantsFirstAddedParentWins() {
        PackageConfig first = new PackageConfig.Builder("first")
                .addScopedConstant("shared", "from-first")
                .build();
        PackageConfig second = new PackageConfig.Builder("second")
                .addScopedConstant("shared", "from-second")
                .build();

        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(first)
                .addParent(second)
                .build();

        assertEquals("from-first", child.getAllScopedConstants().get("shared"));
    }

    public void testScopedConstantsSurviveBuilderCopy() {
        PackageConfig parent = new PackageConfig.Builder("parent")
                .addScopedConstant("first", "parent-first")
                .build();
        PackageConfig original = new PackageConfig.Builder("child")
                .addParent(parent)
                .addScopedConstant("second", "child-second")
                .build();

        PackageConfig copy = new PackageConfig.Builder(original).build();

        assertEquals(original.getScopedConstants(), copy.getScopedConstants());
        assertEquals(original.getAllScopedConstants(), copy.getAllScopedConstants());
    }

    public void testScopedConstantsAreUnmodifiableAfterBuild() {
        PackageConfig config = new PackageConfig.Builder("pkg")
                .addScopedConstant("first", "value")
                .build();

        assertThrows(UnsupportedOperationException.class, () -> config.getScopedConstants().put("x", "y"));
        assertThrows(UnsupportedOperationException.class, () -> config.getAllScopedConstants().put("x", "y"));
    }

    public void testScopedConstantsTakePartInEquality() {
        PackageConfig plain = new PackageConfig.Builder("pkg").build();
        PackageConfig scoped = new PackageConfig.Builder("pkg").addScopedConstant("first", "value").build();

        assertFalse(plain.equals(scoped));
    }
```

Also add `import static org.junit.Assert.assertThrows;` — JUnit 4.13 provides it, and it works inside this JUnit 3 class.

- [ ] **Step 2: Run to verify they fail**

Run: `./mvnw test -DskipAssembly -pl core -Dtest=PackageConfigTest -Dsurefire.failIfNoSpecifiedTests=false > core/target/ww3476.log 2>&1; echo "exit=$?"; grep -E "ERROR|Tests run|cannot find symbol" core/target/ww3476.log | head -20`
Expected: `exit=1`, compilation error `cannot find symbol ... addScopedConstant`.

- [ ] **Step 3: Implement** in `PackageConfig.java`.

Fields, after `protected boolean strictMethodInvocation = true;`:

```java
    protected Map<String, String> scopedConstants;
    protected Map<String, String> allScopedConstants;
```

In `PackageConfig(String name)`, after `parents = new ArrayList<>();`:

```java
        scopedConstants = new LinkedHashMap<>();
        allScopedConstants = Collections.emptyMap();
```

In `PackageConfig(PackageConfig orig)`, after `this.strictMethodInvocation = orig.strictMethodInvocation;`:

```java
        this.scopedConstants = new LinkedHashMap<>(orig.scopedConstants);
        this.allScopedConstants = orig.allScopedConstants;
```

Getters, directly after `getAllResultTypeConfigs()`:

```java
    /**
     * @return scoped constants declared by this package itself, without inherited ones
     * @since 7.5.0
     */
    public Map<String, String> getScopedConstants() {
        return scopedConstants;
    }

    /**
     * @return scoped constants in effect for this package: inherited ones, overridden by its own
     * @since 7.5.0
     */
    public Map<String, String> getAllScopedConstants() {
        return allScopedConstants;
    }
```

In `equals`, after the `parents` comparison line:

```java
        if (!Objects.equals(scopedConstants, that.scopedConstants)) return false;
```

In `hashCode`, after the `parents` line:

```java
        result = 31 * result + (scopedConstants != null ? scopedConstants.hashCode() : 0);
```

In `Builder`, after `addResultTypeConfig(...)`:

```java
        /**
         * @since 7.5.0
         */
        public Builder addScopedConstant(String name, String value) {
            target.scopedConstants.put(name, value);
            return this;
        }
```

In `Builder.build()`, before `PackageConfig result = target;`:

```java
            target.scopedConstants = Collections.unmodifiableMap(target.scopedConstants);
            target.allScopedConstants = mergeScopedConstants(target);
```

And in `Builder`, after `build()`:

```java
        private static Map<String, String> mergeScopedConstants(PackageConfig config) {
            Map<String, String> merged = new LinkedHashMap<>();
            for (PackageConfig parent : config.parents) {
                merged.putAll(parent.getAllScopedConstants());
            }
            merged.putAll(config.scopedConstants);
            return Collections.unmodifiableMap(merged);
        }
```

(`addParent` inserts at index 0, so iterating `parents` and `putAll`-ing lets the first-added parent win — the same order as `getAllResultTypeConfigs()`; `testScopedConstantsFirstAddedParentWins` pins it.)

- [ ] **Step 4: Run to verify they pass** — same command as Step 2. Expected: `exit=0`, `Tests run: N, Failures: 0, Errors: 0`.

- [ ] **Step 5: Watch it fail** — with Edit, swap the merge order in `mergeScopedConstants` (move `merged.putAll(config.scopedConstants);` above the `for` loop). `git diff --stat` must show `PackageConfig.java` changed. Re-run Step 2's command: expect `testScopedConstantsChildOverridesParent` to FAIL. Revert with Edit; re-run; expect `exit=0`.

- [ ] **Step 6: Commit**

```bash
git add core/src/main/java/org/apache/struts2/config/entities/PackageConfig.java core/src/test/java/org/apache/struts2/config/entities/PackageConfigTest.java
git diff --cached --name-only
git commit -m "WW-3476 feat(config): store scoped constants on PackageConfig

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 2: `struts-7.5.dtd` and parsing `<scoped-constant>`

**Files:**
- Create: `core/src/main/resources/struts-7.5.dtd`
- Modify: `core/src/main/java/org/apache/struts2/config/StrutsXmlConfigurationProvider.java:47-54`
- Modify: `core/src/main/java/org/apache/struts2/config/providers/XmlDocConfigurationProvider.java` (`addPackage`, ~line 411)
- Create fixtures in `core/src/test/resources/org/apache/struts2/config/providers/`: `xwork-test-scoped-constants.xml`, `xwork-test-scoped-constants-duplicate.xml`, `xwork-test-scoped-constants-old-dtd.xml`
- Test: `core/src/test/java/org/apache/struts2/config/providers/XmlConfigurationProviderPackagesTest.java` (JUnit 3, extends `ConfigurationTestBase`)

**Interfaces:**
- Consumes: `PackageConfig.Builder.addScopedConstant`, `getScopedConstants`, `getAllScopedConstants` (Task 1).
- Produces: public id `-//Apache Software Foundation//DTD Struts Configuration 7.5//EN` → `struts-7.5.dtd`; `protected void XmlDocConfigurationProvider.loadScopedConstants(PackageConfig.Builder, Element)`.

- [ ] **Step 1: Create the fixtures** (license header first in each, per Global Constraints).

`xwork-test-scoped-constants.xml`:

```xml
<!DOCTYPE struts PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 7.5//EN"
        "struts-7.5.dtd">
<struts>
    <package name="parent" namespace="/parent">
        <scoped-constant name="first" value="parent-first"/>
        <scoped-constant name="second" value="parent-second"/>
    </package>

    <package name="child" namespace="/child" extends="parent">
        <scoped-constant name="second" value="child-second"/>
    </package>
</struts>
```

`xwork-test-scoped-constants-duplicate.xml`:

```xml
<!DOCTYPE struts PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 7.5//EN"
        "struts-7.5.dtd">
<struts>
    <package name="dup" namespace="/dup">
        <scoped-constant name="first" value="one"/>
        <scoped-constant name="first" value="two"/>
    </package>
</struts>
```

`xwork-test-scoped-constants-old-dtd.xml`:

```xml
<!DOCTYPE struts PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 6.5//EN"
        "struts-6.5.dtd">
<struts>
    <package name="old" namespace="/old">
        <scoped-constant name="first" value="one"/>
    </package>
</struts>
```

- [ ] **Step 2: Write the failing tests** — append to `XmlConfigurationProviderPackagesTest` (add `import java.util.Map;` and `import static org.assertj.core.api.Assertions.assertThat;`):

```java
    public void testScopedConstantsLoadAndInherit() throws ConfigurationException {
        buildConfigurationProvider(getXmlConfigFilePath("xwork-test-scoped-constants.xml"));

        PackageConfig parent = configuration.getPackageConfig("parent");
        PackageConfig child = configuration.getPackageConfig("child");

        assertEquals(Map.of("first", "parent-first", "second", "parent-second"), parent.getScopedConstants());
        assertEquals(Map.of("second", "child-second"), child.getScopedConstants());
        assertEquals(Map.of("first", "parent-first", "second", "child-second"), child.getAllScopedConstants());
    }

    public void testDuplicateScopedConstantInOnePackageFails() {
        try {
            buildConfigurationProvider(getXmlConfigFilePath("xwork-test-scoped-constants-duplicate.xml"));
            fail("Should have thrown a ConfigurationException");
        } catch (ConfigurationException e) {
            assertThat(e).hasStackTraceContaining("Package [dup] declares scoped constant [first] more than once");
        }
    }

    public void testScopedConstantIsRejectedUnderOlderDtd() {
        try {
            buildConfigurationProvider(getXmlConfigFilePath("xwork-test-scoped-constants-old-dtd.xml"));
            fail("Should have thrown a ConfigurationException");
        } catch (ConfigurationException e) {
            assertThat(e).hasStackTraceContaining("\"scoped-constant\" must be declared");
        }
    }
```

(The old-DTD assertion matches the parser's `Element type "scoped-constant" must be declared.` — not the fixture file name, which also contains `scoped-constant`.)

- [ ] **Step 3: Run to verify they fail**

Run: `./mvnw test -DskipAssembly -pl core -Dtest=XmlConfigurationProviderPackagesTest -Dsurefire.failIfNoSpecifiedTests=false > core/target/ww3476.log 2>&1; echo "exit=$?"; grep -E "Tests run|FAIL|testScoped|testDuplicate" core/target/ww3476.log | head -20`
Expected: `exit=1`; `testScopedConstantsLoadAndInherit` and `testDuplicateScopedConstantInOnePackageFails` fail/error (7.5 public id is unmapped). `testScopedConstantIsRejectedUnderOlderDtd` already passes — that is fine, it guards that the 6.5 DTD stays unchanged.

- [ ] **Step 4: Create the DTD**

```bash
cp core/src/main/resources/struts-6.5.dtd core/src/main/resources/struts-7.5.dtd
```

Then, with Edit, in `struts-7.5.dtd`:
- In the header comment, replace `"-//Apache Software Foundation//DTD Struts Configuration 6.5//EN"` with `"-//Apache Software Foundation//DTD Struts Configuration 7.5//EN"` and `"https://struts.apache.org/dtds/struts-6.5.dtd"` with `"https://struts.apache.org/dtds/struts-7.5.dtd"`.
- Replace

```
<!ELEMENT package (result-types?, interceptors?, default-interceptor-ref?, default-action-ref?, default-class-ref?, global-results?, global-allowed-methods?, global-exception-mappings?, action*)>
```

with

```
<!ELEMENT package (scoped-constant*, result-types?, interceptors?, default-interceptor-ref?, default-action-ref?, default-class-ref?, global-results?, global-allowed-methods?, global-exception-mappings?, action*)>
```

- Directly after the `<!ATTLIST package ... >` block, add:

```
<!ELEMENT scoped-constant EMPTY>
<!ATTLIST scoped-constant
    name CDATA #REQUIRED
    value CDATA #REQUIRED
>
```

Check: `diff core/src/main/resources/struts-6.5.dtd core/src/main/resources/struts-7.5.dtd` shows only those changes.

- [ ] **Step 5: Register the DTD** — in `StrutsXmlConfigurationProvider.STRUTS_DTD_MAPPINGS`, change the last entry to:

```java
            "-//Apache Software Foundation//DTD Struts Configuration 6.5//EN", "struts-6.5.dtd",
            "-//Apache Software Foundation//DTD Struts Configuration 7.5//EN", "struts-7.5.dtd");
```

- [ ] **Step 6: Parse the element** — in `XmlDocConfigurationProvider.addPackage`, directly after `LOG.debug("Loaded {}", newPackage);`:

```java
        loadScopedConstants(newPackage, packageElement);
```

And add, directly before `loadGlobalResults(...)`'s Javadoc:

```java
    /**
     * @since 7.5.0
     */
    protected void loadScopedConstants(PackageConfig.Builder packageContext, Element packageElement) {
        Set<String> declared = new HashSet<>();
        iterateChildrenByTagName(packageElement, "scoped-constant", constantElement -> {
            String name = constantElement.getAttribute("name");
            if (!declared.add(name)) {
                throw new ConfigurationException(format("Package [%s] declares scoped constant [%s] more than once",
                        packageContext.getName(), name), constantElement);
            }
            packageContext.addScopedConstant(name, constantElement.getAttribute("value"));
        });
    }
```

(The static `format` import, `Set` and `HashSet` are already present in this file.)

- [ ] **Step 7: Run to verify they pass** — Step 3's command. Expected: `exit=0`, all tests in the class pass (including the pre-existing 6.5/2.x fixtures).

- [ ] **Step 8: Watch it fail** — with Edit, change `if (!declared.add(name)) {` to `if (false && !declared.add(name)) {`; `git diff --stat` shows the file; re-run: `testDuplicateScopedConstantInOnePackageFails` FAILS. Revert. Then, with Edit, add `scoped-constant*, ` to the `package` content model in **`struts-6.5.dtd`** plus the same `<!ELEMENT scoped-constant ...>` block; re-run: `testScopedConstantIsRejectedUnderOlderDtd` FAILS. Revert with Edit; `git diff --stat core/src/main/resources/struts-6.5.dtd` must be empty. Re-run: `exit=0`.

- [ ] **Step 9: Commit**

```bash
git add core/src/main/resources/struts-7.5.dtd \
  core/src/main/java/org/apache/struts2/config/StrutsXmlConfigurationProvider.java \
  core/src/main/java/org/apache/struts2/config/providers/XmlDocConfigurationProvider.java \
  core/src/test/java/org/apache/struts2/config/providers/XmlConfigurationProviderPackagesTest.java \
  core/src/test/resources/org/apache/struts2/config/providers/xwork-test-scoped-constants.xml \
  core/src/test/resources/org/apache/struts2/config/providers/xwork-test-scoped-constants-duplicate.xml \
  core/src/test/resources/org/apache/struts2/config/providers/xwork-test-scoped-constants-old-dtd.xml
git diff --cached --name-only
git commit -m "WW-3476 feat(config): add struts-7.5.dtd with <scoped-constant>

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 3: `ScopableConstants` registry and startup validation

**Files:**
- Create: `core/src/main/java/org/apache/struts2/config/ScopableConstants.java`
- Modify: `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java` (`buildRuntimeConfiguration`, ~line 451)
- Create (test): `core/src/test/java/org/apache/struts2/config/SampleScopableConstants.java`, `core/src/test/java/org/apache/struts2/config/SecondScopableConstants.java`
- Create fixtures in `core/src/test/resources/org/apache/struts2/config/`: `scoped-constants-registered.xml`, `scoped-constants-unregistered.xml`, `scoped-constants-abstract-unregistered.xml`, `scoped-constants-no-registry.xml`, `scoped-constants-two-beans.xml`
- Test: `core/src/test/java/org/apache/struts2/config/ScopedConstantsValidationTest.java` (new, JUnit 4)

**Interfaces:**
- Consumes: `PackageConfig.getScopedConstants()` (Task 1); `struts-7.5.dtd` mapping (Task 2).
- Produces: `interface ScopableConstants { Set<String> getNames(); static Set<String> collectNames(Container container); }`; test beans `SampleScopableConstants` → `{"sample.scopable", "sample.unset"}`, `SecondScopableConstants` → `{"second.scopable"}`.

- [ ] **Step 1: Create the registry contract** — `ScopableConstants.java`:

```java
package org.apache.struts2.config;

import org.apache.struts2.inject.Container;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Declares constants a package may override with {@code <scoped-constant>}. Register implementations as named beans;
 * the scopable set is the union over all of them.
 *
 * @since 7.5.0
 */
public interface ScopableConstants {

    Set<String> getNames();

    static Set<String> collectNames(Container container) {
        Set<String> names = new HashSet<>();
        for (String beanName : container.getInstanceNames(ScopableConstants.class)) {
            names.addAll(container.getInstance(ScopableConstants.class, beanName).getNames());
        }
        return Collections.unmodifiableSet(names);
    }
}
```

- [ ] **Step 2: Create the test beans**

`SampleScopableConstants.java`:

```java
package org.apache.struts2.config;

import java.util.Set;

public class SampleScopableConstants implements ScopableConstants {

    @Override
    public Set<String> getNames() {
        return Set.of("sample.scopable", "sample.unset");
    }
}
```

`SecondScopableConstants.java`:

```java
package org.apache.struts2.config;

import java.util.Set;

public class SecondScopableConstants implements ScopableConstants {

    @Override
    public Set<String> getNames() {
        return Set.of("second.scopable");
    }
}
```

- [ ] **Step 3: Create the fixtures.** Each file is: the XML license header, then this DOCTYPE, then the `<struts>` body shown below.

```xml
<!DOCTYPE struts PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 7.5//EN"
        "struts-7.5.dtd">
```

`scoped-constants-registered.xml`:

```xml
<struts>
    <bean type="org.apache.struts2.config.ScopableConstants" name="sample"
          class="org.apache.struts2.config.SampleScopableConstants"/>

    <package name="registered" namespace="/registered">
        <scoped-constant name="sample.scopable" value="x"/>
    </package>
</struts>
```

`scoped-constants-unregistered.xml`:

```xml
<struts>
    <bean type="org.apache.struts2.config.ScopableConstants" name="sample"
          class="org.apache.struts2.config.SampleScopableConstants"/>

    <package name="unregistered" namespace="/unregistered">
        <scoped-constant name="sample.unknown" value="x"/>
    </package>
</struts>
```

`scoped-constants-abstract-unregistered.xml`:

```xml
<struts>
    <bean type="org.apache.struts2.config.ScopableConstants" name="sample"
          class="org.apache.struts2.config.SampleScopableConstants"/>

    <package name="abstractBase" abstract="true">
        <scoped-constant name="sample.unknown" value="x"/>
    </package>
</struts>
```

`scoped-constants-no-registry.xml`:

```xml
<struts>
    <package name="unregistered" namespace="/unregistered">
        <scoped-constant name="sample.scopable" value="x"/>
    </package>
</struts>
```

`scoped-constants-two-beans.xml`:

```xml
<struts>
    <bean type="org.apache.struts2.config.ScopableConstants" name="sample"
          class="org.apache.struts2.config.SampleScopableConstants"/>
    <bean type="org.apache.struts2.config.ScopableConstants" name="second"
          class="org.apache.struts2.config.SecondScopableConstants"/>

    <package name="both" namespace="/both">
        <scoped-constant name="sample.scopable" value="x"/>
        <scoped-constant name="second.scopable" value="y"/>
    </package>
</struts>
```

- [ ] **Step 4: Write the failing tests** — `ScopedConstantsValidationTest.java`:

```java
package org.apache.struts2.config;

import org.apache.struts2.ActionContext;
import org.apache.struts2.config.providers.StrutsDefaultConfigurationProvider;
import org.apache.struts2.inject.Container;
import org.junit.After;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ScopedConstantsValidationTest {

    private ConfigurationManager configurationManager;

    @After
    public void tearDown() {
        if (configurationManager != null) {
            configurationManager.destroyConfiguration();
        }
        ActionContext.clear();
    }

    @Test
    public void registeredNameLoads() {
        Configuration configuration = load("scoped-constants-registered.xml");

        assertThat(configuration.getPackageConfig("registered").getScopedConstants())
                .isEqualTo(Map.of("sample.scopable", "x"));
    }

    @Test
    public void unregisteredNameFails() {
        assertThatThrownBy(() -> load("scoped-constants-unregistered.xml"))
                .isInstanceOf(ConfigurationException.class)
                .hasStackTraceContaining("Package [unregistered] declares scoped constant [sample.unknown], which is not scopable. "
                        + "Scopable constants: [sample.scopable, sample.unset].");
    }

    @Test
    public void abstractPackageIsValidatedToo() {
        assertThatThrownBy(() -> load("scoped-constants-abstract-unregistered.xml"))
                .isInstanceOf(ConfigurationException.class)
                .hasStackTraceContaining("Package [abstractBase] declares scoped constant [sample.unknown], which is not scopable.");
    }

    @Test
    public void anyScopedConstantFailsWhenNothingIsScopable() {
        assertThatThrownBy(() -> load("scoped-constants-no-registry.xml"))
                .isInstanceOf(ConfigurationException.class)
                .hasStackTraceContaining("Package [unregistered] declares scoped constant [sample.scopable], which is not scopable. "
                        + "No constants are scopable in this configuration.");
    }

    @Test
    public void namesFromAllBeansAreScopable() {
        Configuration configuration = load("scoped-constants-two-beans.xml");

        assertThat(configuration.getPackageConfig("both").getScopedConstants())
                .isEqualTo(Map.of("sample.scopable", "x", "second.scopable", "y"));
    }

    private Configuration load(String fixture) {
        configurationManager = new ConfigurationManager(Container.DEFAULT_NAME);
        configurationManager.addContainerProvider(new StrutsDefaultConfigurationProvider());
        configurationManager.addContainerProvider(new StrutsXmlConfigurationProvider("org/apache/struts2/config/" + fixture));
        return configurationManager.getConfiguration();
    }
}
```

- [ ] **Step 5: Run to verify they fail**

Run: `./mvnw test -DskipAssembly -pl core -Dtest=ScopedConstantsValidationTest -Dsurefire.failIfNoSpecifiedTests=false > core/target/ww3476.log 2>&1; echo "exit=$?"; grep -E "Tests run|FAIL|Expecting" core/target/ww3476.log | head -20`
Expected: `exit=1`; the three `...Fails`/`...Too` tests fail (no exception thrown); `registeredNameLoads` and `namesFromAllBeansAreScopable` pass.

- [ ] **Step 6: Implement validation** — in `DefaultConfiguration.java`, add the import `org.apache.struts2.config.ScopableConstants` (`java.util.Set` and `java.util.TreeSet` are already imported). As the first statement of `buildRuntimeConfiguration()`:

```java
        validateScopedConstants();
```

And add, directly after `buildRuntimeConfiguration()`:

```java
    /**
     * @since 7.5.0
     */
    protected void validateScopedConstants() throws ConfigurationException {
        Set<String> scopableNames = ScopableConstants.collectNames(container);
        for (PackageConfig packageConfig : packageContexts.values()) {
            for (String name : packageConfig.getScopedConstants().keySet()) {
                if (!scopableNames.contains(name)) {
                    throw new ConfigurationException(String.format("Package [%s] declares scoped constant [%s], which is not scopable. %s",
                            packageConfig.getName(), name, describeScopable(scopableNames)), packageConfig);
                }
            }
        }
    }

    private static String describeScopable(Set<String> scopableNames) {
        if (scopableNames.isEmpty()) {
            return "No constants are scopable in this configuration.";
        }
        return "Scopable constants: " + new TreeSet<>(scopableNames) + ".";
    }
```

- [ ] **Step 7: Run to verify they pass** — Step 5's command. Expected: `exit=0`, 5 tests, 0 failures.

- [ ] **Step 8: Watch it fail** — with Edit, comment out the `validateScopedConstants();` call; `git diff --stat` shows `DefaultConfiguration.java`; re-run: the three failure-path tests FAIL. Revert; re-run: `exit=0`.

- [ ] **Step 9: Regression check on config loading** — `./mvnw test -DskipAssembly -pl core -Dtest='*Configuration*Test,XmlConfigurationProvider*Test' -Dsurefire.failIfNoSpecifiedTests=false > core/target/ww3476.log 2>&1; echo "exit=$?"; grep -E "Tests run:.*Fail" core/target/ww3476.log | tail -3`. Expected `exit=0`.

- [ ] **Step 10: Commit**

```bash
git add core/src/main/java/org/apache/struts2/config/ScopableConstants.java \
  core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java \
  core/src/test/java/org/apache/struts2/config/SampleScopableConstants.java \
  core/src/test/java/org/apache/struts2/config/SecondScopableConstants.java \
  core/src/test/java/org/apache/struts2/config/ScopedConstantsValidationTest.java \
  core/src/test/resources/org/apache/struts2/config/scoped-constants-registered.xml \
  core/src/test/resources/org/apache/struts2/config/scoped-constants-unregistered.xml \
  core/src/test/resources/org/apache/struts2/config/scoped-constants-abstract-unregistered.xml \
  core/src/test/resources/org/apache/struts2/config/scoped-constants-no-registry.xml \
  core/src/test/resources/org/apache/struts2/config/scoped-constants-two-beans.xml
git diff --cached --name-only
git commit -m "WW-3476 feat(config): reject unregistered scoped constants at startup

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 4: `ScopedConstantProvider` and its wiring

**Files:**
- Create: `core/src/main/java/org/apache/struts2/config/ScopedConstantProvider.java`
- Create: `core/src/main/java/org/apache/struts2/config/StrutsScopedConstantProvider.java`
- Modify: `core/src/main/java/org/apache/struts2/StrutsConstants.java` (after `STRUTS_PARAMETER_ALLOWLISTER`, ~line 629)
- Modify: `core/src/main/java/org/apache/struts2/config/StrutsBeanSelectionProvider.java` (~line 456)
- Modify: `core/src/main/resources/struts-beans.xml` (after the `ParameterAllowlister` bean, ~line 258)
- Modify: `core/src/main/java/org/apache/struts2/config/providers/StrutsDefaultConfigurationProvider.java` (`register` chain)
- Create fixture: `core/src/test/resources/org/apache/struts2/config/scoped-constants-provider.xml`
- Test: `core/src/test/java/org/apache/struts2/config/StrutsScopedConstantProviderTest.java` (new, JUnit 3, extends `XWorkTestCase`)

**Interfaces:**
- Consumes: `ScopableConstants.collectNames(Container)`, `SampleScopableConstants` (Task 3); `PackageConfig.getAllScopedConstants()` (Task 1); 7.5 DTD (Task 2).
- Produces: `interface ScopedConstantProvider { String getValue(String name); }`; `StrutsScopedConstantProvider`; `StrutsConstants.STRUTS_SCOPED_CONSTANT_PROVIDER = "struts.scopedConstantProvider"`.

- [ ] **Step 1: Create the contract** — `ScopedConstantProvider.java`:

```java
package org.apache.struts2.config;

/**
 * Resolves a scopable constant for the current action invocation: the package's {@code <scoped-constant>}, inherited
 * through {@code extends}, or else the global constant.
 *
 * @since 7.5.0
 */
public interface ScopedConstantProvider {

    /**
     * @param name a constant registered through a {@link ScopableConstants} bean
     * @return the scoped value, or the global value ({@code null} when there is none)
     * @throws IllegalArgumentException when {@code name} is not scopable
     */
    String getValue(String name);
}
```

- [ ] **Step 2: Create the fixture** — `scoped-constants-provider.xml`: the XML license header, then this DOCTYPE, then the body below.

```xml
<!DOCTYPE struts PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 7.5//EN"
        "struts-7.5.dtd">
```

```xml
<struts>
    <bean type="org.apache.struts2.config.ScopableConstants" name="sample"
          class="org.apache.struts2.config.SampleScopableConstants"/>

    <constant name="sample.scopable" value="global"/>

    <package name="base" abstract="true">
        <scoped-constant name="sample.scopable" value="base"/>
    </package>

    <package name="child" namespace="/child" extends="base"/>

    <package name="override" namespace="/override" extends="base">
        <scoped-constant name="sample.scopable" value="override"/>
    </package>

    <package name="empty" namespace="/empty" extends="base">
        <scoped-constant name="sample.scopable" value=""/>
    </package>

    <package name="plain" namespace="/plain"/>
</struts>
```

- [ ] **Step 3: Write the failing tests** — `StrutsScopedConstantProviderTest.java`:

```java
package org.apache.struts2.config;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionSupport;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.mock.MockActionProxy;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StrutsScopedConstantProviderTest extends XWorkTestCase {

    private ScopedConstantProvider provider;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new StrutsXmlConfigurationProvider("org/apache/struts2/config/scoped-constants-provider.xml"));
        provider = container.getInstance(ScopedConstantProvider.class);
    }

    public void testDefaultProviderIsStrutsImplementation() {
        assertTrue(provider instanceof StrutsScopedConstantProvider);
    }

    public void testPackageValueWinsInsideInvocation() {
        enterPackage("override");

        assertEquals("override", provider.getValue("sample.scopable"));
    }

    public void testInheritedValueResolvesForChildPackage() {
        enterPackage("child");

        assertEquals("base", provider.getValue("sample.scopable"));
    }

    public void testEmptyOverrideIsReturnedAsIs() {
        enterPackage("empty");

        assertEquals("", provider.getValue("sample.scopable"));
    }

    public void testGlobalValueWithoutOverride() {
        enterPackage("plain");

        assertEquals("global", provider.getValue("sample.scopable"));
    }

    public void testGlobalValueWithoutInvocation() {
        assertEquals("global", provider.getValue("sample.scopable"));
    }

    public void testGlobalValueWithoutActionContext() {
        ActionContext context = ActionContext.getContext();
        ActionContext.clear();
        try {
            assertEquals("global", provider.getValue("sample.scopable"));
        } finally {
            context.bind();
        }
    }

    public void testGlobalValueWhenInvocationHasNoProxy() {
        ActionContext.getContext().withActionInvocation(new MockActionInvocation());

        assertEquals("global", provider.getValue("sample.scopable"));
    }

    public void testGlobalValueWhenProxyHasNoConfig() {
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setProxy(new MockActionProxy());
        ActionContext.getContext().withActionInvocation(invocation);

        assertEquals("global", provider.getValue("sample.scopable"));
    }

    public void testGlobalValueWhenPackageIsUnknown() {
        enterPackage("missing");

        assertEquals("global", provider.getValue("sample.scopable"));
    }

    public void testUnsetConstantWithoutOverrideIsNull() {
        enterPackage("plain");

        assertNull(provider.getValue("sample.unset"));
    }

    public void testUnregisteredNameThrows() {
        assertThatThrownBy(() -> provider.getValue("sample.unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[sample.unknown]");
    }

    public void testGlobalConstantUntouchedByScopedRead() {
        enterPackage("override");
        provider.getValue("sample.scopable");

        assertEquals("global", container.getInstance(String.class, "sample.scopable"));
    }

    private void enterPackage(String packageName) {
        MockActionProxy proxy = new MockActionProxy();
        proxy.setConfig(new ActionConfig.Builder(packageName, "test", ActionSupport.class.getName()).build());
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setProxy(proxy);
        ActionContext.getContext().withActionInvocation(invocation);
    }
}
```

- [ ] **Step 4: Run to verify they fail**

Run: `./mvnw test -DskipAssembly -pl core -Dtest=StrutsScopedConstantProviderTest -Dsurefire.failIfNoSpecifiedTests=false > core/target/ww3476.log 2>&1; echo "exit=$?"; grep -E "ERROR|Tests run|cannot find symbol" core/target/ww3476.log | head -20`
Expected: `exit=1`, `cannot find symbol ... StrutsScopedConstantProvider`.

- [ ] **Step 5: Implement the default provider** — `StrutsScopedConstantProvider.java`:

```java
package org.apache.struts2.config;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.Inject;

import java.util.Optional;
import java.util.Set;

/**
 * @since 7.5.0
 */
public class StrutsScopedConstantProvider implements ScopedConstantProvider {

    private Container container;
    private Configuration configuration;
    private volatile Set<String> scopableNames;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getValue(String name) {
        if (!getScopableNames().contains(name)) {
            throw new IllegalArgumentException(String.format(
                    "Constant [%s] is not scopable; register it through a %s bean", name, ScopableConstants.class.getName()));
        }
        return currentPackage()
                .map(packageConfig -> packageConfig.getAllScopedConstants().get(name))
                .orElseGet(() -> container.getInstance(String.class, name));
    }

    private Optional<PackageConfig> currentPackage() {
        ActionContext context = ActionContext.getContext();
        ActionInvocation invocation = context != null ? context.getActionInvocation() : null;
        ActionProxy proxy = invocation != null ? invocation.getProxy() : null;
        if (proxy == null || proxy.getConfig() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(configuration.getPackageConfig(proxy.getConfig().getPackageName()));
    }

    private Set<String> getScopableNames() {
        Set<String> names = scopableNames;
        if (names == null) {
            names = ScopableConstants.collectNames(container);
            scopableNames = names;
        }
        return names;
    }
}
```

Note: `Optional.map` treats a `null` mapping result as empty, so a package without the override falls through to `orElseGet`; an empty-string override is non-null and is returned as is.

- [ ] **Step 6: Wire it**

`StrutsConstants.java`, directly after `STRUTS_PARAMETER_ALLOWLISTER`:

```java

    /**
     * The {@link org.apache.struts2.config.ScopedConstantProvider} implementation class.
     *
     * @since 7.5.0
     */
    public static final String STRUTS_SCOPED_CONSTANT_PROVIDER = "struts.scopedConstantProvider";
```

`StrutsBeanSelectionProvider.java`, directly after the `ParameterAllowlister` alias (same package — no import):

```java
        alias(ScopedConstantProvider.class, StrutsConstants.STRUTS_SCOPED_CONSTANT_PROVIDER, builder, props, Scope.SINGLETON);
```

`struts-beans.xml`, directly after the `ParameterAllowlister` bean:

```xml

    <bean type="org.apache.struts2.config.ScopedConstantProvider" name="struts"
          class="org.apache.struts2.config.StrutsScopedConstantProvider" scope="singleton"/>
```

`StrutsDefaultConfigurationProvider.register`, directly after `.factory(UnknownHandlerManager.class, DefaultUnknownHandlerManager.class, Scope.SINGLETON)`:

```java

                .factory(ScopedConstantProvider.class, StrutsScopedConstantProvider.class, Scope.SINGLETON)
```

with imports `org.apache.struts2.config.ScopedConstantProvider` and `org.apache.struts2.config.StrutsScopedConstantProvider`.

- [ ] **Step 7: Run to verify they pass** — Step 4's command. Expected: `exit=0`, 13 tests, 0 failures.

- [ ] **Step 8: Watch it fail** — with Edit, replace `.map(packageConfig -> packageConfig.getAllScopedConstants().get(name))` with `.map(packageConfig -> (String) null)`; `git diff --stat` shows the file; re-run: `testPackageValueWinsInsideInvocation`, `testInheritedValueResolvesForChildPackage`, `testEmptyOverrideIsReturnedAsIs` FAIL. Revert. Then replace `.orElseGet(() -> container.getInstance(String.class, name))` with `.orElse(null)`; re-run: the `testGlobalValue*` tests FAIL. Revert; re-run: `exit=0`.

- [ ] **Step 9: Commit**

```bash
git add core/src/main/java/org/apache/struts2/config/ScopedConstantProvider.java \
  core/src/main/java/org/apache/struts2/config/StrutsScopedConstantProvider.java \
  core/src/main/java/org/apache/struts2/StrutsConstants.java \
  core/src/main/java/org/apache/struts2/config/StrutsBeanSelectionProvider.java \
  core/src/main/resources/struts-beans.xml \
  core/src/main/java/org/apache/struts2/config/providers/StrutsDefaultConfigurationProvider.java \
  core/src/test/java/org/apache/struts2/config/StrutsScopedConstantProviderTest.java \
  core/src/test/resources/org/apache/struts2/config/scoped-constants-provider.xml
git diff --cached --name-only
git commit -m "WW-3476 feat(config): add ScopedConstantProvider resolving package overrides

Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>"
```

---

### Task 5: Full verification

**Files:** none changed unless a regression appears.

- [ ] **Step 1: Check for stray files** — `git status --short` must show no `??` entries from this work (review agents may leave probe tests; delete those, never commit them).

- [ ] **Step 2: Run core and the Convention plugin** (Convention compares `PackageConfig`s with `equals`, which Task 1 changed):

Run: `./mvnw test -DskipAssembly -pl core,plugins/convention > core/target/ww3476-full.log 2>&1; echo "exit=$?"; grep -E "Tests run:.*Fail|BUILD|ERROR\]" core/target/ww3476-full.log | tail -10`
Expected: `exit=0`, `BUILD SUCCESS`. If a failure is in `DateTest.testJavaSqlDate` or another test unrelated to config, re-run that single test on `main` to confirm it is pre-existing before reporting it.

- [ ] **Step 3: Confirm no adopter slipped in** — `grep -rn "ScopableConstants" core/src/main plugins/*/src/main` must list only `ScopableConstants.java`, `StrutsScopedConstantProvider.java`, `ScopedConstantProvider.java`, `DefaultConfiguration.java` — no `<bean type="...ScopableConstants"` in any `src/main` XML.

- [ ] **Step 4: Hand back** — report the commits and test results. Not part of this plan (each needs the user's go-ahead): the `/security-review` pass and PR (`WW-3476 feat(config): add package-scoped constants`), and the struts-site PR publishing `dtds/struts-7.5.dtd` with docs for `<scoped-constant>`.
