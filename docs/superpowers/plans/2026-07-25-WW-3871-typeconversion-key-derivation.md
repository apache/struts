# WW-3871 `@TypeConversion` Key Derivation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let `@TypeConversion` accept a bare property name as its `key` at class, method and field level, deriving the `ConversionRule` prefix automatically, without breaking annotations that already spell the prefix out.

**Architecture:** `ConversionRule` gains a `prefix()` method owning the rule-to-prefix table. `XWorkConverter` gains one static `resolveKey(type, rule, name)` used by all three annotation passes, and `addConverterMapping` splits into four named passes (properties file, class-level, method, field) with first-writer-wins precedence. `@TypeConversion` gains `ElementType.FIELD`.

**Tech Stack:** Java 17, Maven, JUnit (see constraints), Log4j2, `org.apache.commons.lang3.StringUtils`.

**Spec:** `docs/superpowers/specs/2026-07-25-WW-3871-typeconversion-key-derivation-design.md`

## Global Constraints

- **Branch:** work on `WW-3871-typeconversion-key-derivation` (already created, based on `main` @ `8a5323fbd`). Never commit to `main`.
- **Commit messages:** must be prefixed with the ticket: `WW-3871 <type>(<scope>): <description>`. Types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`.
- **Test framework:** core tests are JUnit 3/4, **never** JUnit 5. Classes extending `XWorkTestCase` (which extends `junit.framework.TestCase`) use `public void testXxx()` with **no** `@Test` annotation — an `@Test` there silently never runs. Standalone JUnit 4 classes (no superclass, `org.junit.Test` + `org.junit.Assert`) are also fine and are used in this module, e.g. `core/src/test/java/org/apache/struts2/LocaleProviderTest.java`.
- **Build command:** `mvn test -DskipAssembly -pl core -Dtest=ClassName#methodName` for a single test; `mvn test -DskipAssembly -pl core` for the module.
- **Licence header:** every new `.java` and `.properties` file must start with the Apache licence header — copy it verbatim from a neighbouring file in the same directory.
- **Target version for `@since` tags:** `7.3.0`.
- **No new dependencies.**

---

### Task 1: `ConversionRule.prefix()`

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/conversion/annotations/ConversionRule.java`
- Test (create): `core/src/test/java/org/apache/struts2/conversion/annotations/ConversionRuleTest.java`

**Interfaces:**
- Consumes: `DefaultObjectTypeDeterminer.KEY_PREFIX`, `ELEMENT_PREFIX`, `KEY_PROPERTY_PREFIX`, `CREATE_IF_NULL_PREFIX`, `DEPRECATED_ELEMENT_PREFIX` — public String constants in `org.apache.struts2.conversion.impl.DefaultObjectTypeDeterminer` (`"Key_"`, `"Element_"`, `"KeyProperty_"`, `"CreateIfNull_"`, `"Collection_"`).
- Produces: `public String ConversionRule.prefix()` — returns the mapping-key prefix for the rule, `""` for `PROPERTY` and `MAP`. Never null.

- [ ] **Step 1: Write the failing test**

Create `core/src/test/java/org/apache/struts2/conversion/annotations/ConversionRuleTest.java` (Apache licence header first, copied from `ConversionRule.java`):

```java
package org.apache.struts2.conversion.annotations;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConversionRuleTest {

    @Test
    public void prefixIsDefinedForEveryRule() {
        assertEquals("", ConversionRule.PROPERTY.prefix());
        assertEquals("", ConversionRule.MAP.prefix());
        assertEquals("Collection_", ConversionRule.COLLECTION.prefix());
        assertEquals("CreateIfNull_", ConversionRule.CREATE_IF_NULL.prefix());
        assertEquals("Element_", ConversionRule.ELEMENT.prefix());
        assertEquals("Key_", ConversionRule.KEY.prefix());
        assertEquals("KeyProperty_", ConversionRule.KEY_PROPERTY.prefix());
    }
}
```

- [ ] **Step 2: Run it to make sure it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=ConversionRuleTest`
Expected: compilation failure — `cannot find symbol: method prefix()`.

- [ ] **Step 3: Implement `prefix()`**

In `ConversionRule.java`, add the import and the method. The enum body becomes:

```java
package org.apache.struts2.conversion.annotations;

import org.apache.struts2.conversion.impl.DefaultObjectTypeDeterminer;

/**
 * <code>ConversionRule</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
 */
public enum ConversionRule {

    PROPERTY, COLLECTION, MAP, KEY, KEY_PROPERTY, ELEMENT, CREATE_IF_NULL;

    /**
     * The prefix a conversion mapping key carries for this rule, as read back by
     * {@link DefaultObjectTypeDeterminer}. {@code PROPERTY} and {@code MAP} have no prefix of their
     * own: map and collection metadata is read through the {@code Key_} and {@code Element_} keys.
     *
     * @return the mapping key prefix, never null; an empty string when the rule has none
     * @since 7.3.0
     */
    public String prefix() {
        return switch (this) {
            case COLLECTION -> DefaultObjectTypeDeterminer.DEPRECATED_ELEMENT_PREFIX;
            case CREATE_IF_NULL -> DefaultObjectTypeDeterminer.CREATE_IF_NULL_PREFIX;
            case ELEMENT -> DefaultObjectTypeDeterminer.ELEMENT_PREFIX;
            case KEY -> DefaultObjectTypeDeterminer.KEY_PREFIX;
            case KEY_PROPERTY -> DefaultObjectTypeDeterminer.KEY_PROPERTY_PREFIX;
            case PROPERTY, MAP -> "";
        };
    }

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
}
```

The `switch` is deliberately exhaustive with no `default` branch: adding a rule then fails to compile until its prefix is decided.

- [ ] **Step 4: Run the test and make sure it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=ConversionRuleTest`
Expected: PASS, 1 test.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/conversion/annotations/ConversionRule.java \
        core/src/test/java/org/apache/struts2/conversion/annotations/ConversionRuleTest.java
git commit -m "WW-3871 feat(core): add ConversionRule#prefix() owning the rule-to-prefix table"
```

---

### Task 2: Split `addConverterMapping` into named passes (pure refactor)

No behaviour changes in this task — including the `break` statements, which stay wrong until Task 4. This exists so the later tasks touch small, readable methods.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/conversion/impl/XWorkConverter.java:496-548`

**Interfaces:**
- Produces: `private void processClassLevelAnnotations(Map<String, Object> mapping, Class clazz)` and `private void processMethodAnnotations(Map<String, Object> mapping, Class clazz)`, both called from `addConverterMapping`.

- [ ] **Step 1: Run the existing tests to establish the baseline**

Run: `mvn test -DskipAssembly -pl core -Dtest='XWorkConverterTest+AnnotationXWorkConverterTest+MyBeanActionTest'`
Expected: PASS. Record the test counts — the same tests must still pass at Step 3.

- [ ] **Step 2: Extract the two passes**

Replace the whole body of `addConverterMapping` (currently lines 496-548) with:

```java
    protected void addConverterMapping(Map<String, Object> mapping, Class clazz) {
        // Process <clazz>-conversion.properties file
        String converterFilename = buildConverterFilename(clazz);
        fileProcessor.process(mapping, clazz, converterFilename);

        processClassLevelAnnotations(mapping, clazz);
        processMethodAnnotations(mapping, clazz);
    }

    /**
     * Registers the {@link TypeConversion} entries declared by a class level {@link Conversion}
     * annotation.
     */
    private void processClassLevelAnnotations(Map<String, Object> mapping, Class clazz) {
        for (Annotation annotation : clazz.getAnnotations()) {
            if (annotation instanceof Conversion conversion) {
                for (TypeConversion tc : conversion.conversions()) {
                    if (mapping.containsKey(tc.key())) {
                        break;
                    }
                    if (LOG.isDebugEnabled()) {
                        if (StringUtils.isEmpty(tc.key())) {
                            LOG.debug("WARNING! key of @TypeConversion [{}/{}] applied to [{}] is empty!", tc.converter(), tc.converterClass(), clazz.getName());
                        } else {
                            LOG.debug("TypeConversion [{}/{}] with key: [{}]", tc.converter(), tc.converterClass(), tc.key());
                        }
                    }
                    annotationProcessor.process(mapping, tc, tc.key());
                }
            }
        }
    }

    /**
     * Registers {@link TypeConversion} annotations found on the class' methods.
     */
    private void processMethodAnnotations(Map<String, Object> mapping, Class clazz) {
        for (Method method : clazz.getMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                if (annotation instanceof TypeConversion tc) {
                    String key = tc.key();
                    // Default to the property name with prefix
                    if (StringUtils.isEmpty(key)) {
                        key = AnnotationUtils.resolvePropertyName(method);
                        key = switch (tc.rule()) {
                            case COLLECTION -> DefaultObjectTypeDeterminer.DEPRECATED_ELEMENT_PREFIX + key;
                            case CREATE_IF_NULL -> DefaultObjectTypeDeterminer.CREATE_IF_NULL_PREFIX + key;
                            case ELEMENT -> DefaultObjectTypeDeterminer.ELEMENT_PREFIX + key;
                            case KEY -> DefaultObjectTypeDeterminer.KEY_PREFIX + key;
                            case KEY_PROPERTY -> DefaultObjectTypeDeterminer.KEY_PROPERTY_PREFIX + key;
                            default -> key;
                        };
                        LOG.debug("Retrieved key [{}] from method name [{}]", key, method.getName());
                    }
                    if (mapping.containsKey(key)) {
                        break;
                    }
                    annotationProcessor.process(mapping, tc, key);
                }
            }
        }
    }
```

Note: the original code reused a local `Annotation[] annotations` variable across both loops; the extracted methods each iterate directly, so that variable disappears. Check whether `Annotation` and `Method` imports are still needed — they are, both extracted methods use them.

- [ ] **Step 3: Run the same tests and confirm identical results**

Run: `mvn test -DskipAssembly -pl core -Dtest='XWorkConverterTest+AnnotationXWorkConverterTest+MyBeanActionTest'`
Expected: PASS, same counts as Step 1.

- [ ] **Step 4: Commit**

```bash
git add core/src/main/java/org/apache/struts2/conversion/impl/XWorkConverter.java
git commit -m "WW-3871 refactor(core): split addConverterMapping into per-source passes"
```

---

### Task 3: `resolveKey` helper, wired into the method pass

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/conversion/impl/XWorkConverter.java`
- Test (modify): `core/src/test/java/org/apache/struts2/conversion/impl/XWorkConverterTest.java`
- Test fixture (create): `core/src/test/java/org/apache/struts2/util/ExplicitKeyConversionAction.java`

**Interfaces:**
- Consumes: `ConversionRule.prefix()` from Task 1; `processMethodAnnotations` from Task 2.
- Produces: `static String resolveKey(ConversionType type, ConversionRule rule, String name)` on `XWorkConverter` — package-private static, returns the prefixed mapping key, or `null` when `name` is null or empty. Callers in Tasks 4 and 5 rely on this exact signature and on `null` meaning "skip this entry".

- [ ] **Step 1: Write the failing unit tests for `resolveKey`**

Append to `core/src/test/java/org/apache/struts2/conversion/impl/XWorkConverterTest.java` (a `XWorkTestCase` subclass — plain `testXxx` methods, no `@Test`):

```java
    public void testResolveKeyPrependsTheRulePrefix() {
        assertEquals("KeyProperty_annotatedBeanMap",
                XWorkConverter.resolveKey(ConversionType.CLASS, ConversionRule.KEY_PROPERTY, "annotatedBeanMap"));
        assertEquals("Element_annotatedBeanList",
                XWorkConverter.resolveKey(ConversionType.CLASS, ConversionRule.ELEMENT, "annotatedBeanList"));
        assertEquals("CreateIfNull_users",
                XWorkConverter.resolveKey(ConversionType.CLASS, ConversionRule.CREATE_IF_NULL, "users"));
    }

    public void testResolveKeyLeavesAnAlreadyPrefixedKeyAlone() {
        assertEquals("KeyProperty_annotatedBeanMap",
                XWorkConverter.resolveKey(ConversionType.CLASS, ConversionRule.KEY_PROPERTY, "KeyProperty_annotatedBeanMap"));
        assertEquals("Key_beanMap",
                XWorkConverter.resolveKey(ConversionType.CLASS, ConversionRule.KEY, "Key_beanMap"));
    }

    public void testResolveKeyDoesNotPrefixPropertyOrMapRules() {
        assertEquals("someProperty",
                XWorkConverter.resolveKey(ConversionType.CLASS, ConversionRule.PROPERTY, "someProperty"));
        assertEquals("keyValues",
                XWorkConverter.resolveKey(ConversionType.CLASS, ConversionRule.MAP, "keyValues"));
    }

    public void testResolveKeyNeverPrefixesApplicationScopedKeys() {
        assertEquals("java.util.Date",
                XWorkConverter.resolveKey(ConversionType.APPLICATION, ConversionRule.PROPERTY, "java.util.Date"));
        assertEquals("java.util.Date",
                XWorkConverter.resolveKey(ConversionType.APPLICATION, ConversionRule.ELEMENT, "java.util.Date"));
    }

    public void testResolveKeyReturnsNullWhenNoNameIsAvailable() {
        assertNull(XWorkConverter.resolveKey(ConversionType.CLASS, ConversionRule.PROPERTY, null));
        assertNull(XWorkConverter.resolveKey(ConversionType.CLASS, ConversionRule.KEY, ""));
    }
```

Add these imports to that test class if not already present:

```java
import org.apache.struts2.conversion.annotations.ConversionRule;
import org.apache.struts2.conversion.annotations.ConversionType;
```

- [ ] **Step 2: Run them to make sure they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=XWorkConverterTest`
Expected: compilation failure — `cannot find symbol: method resolveKey(...)`.

- [ ] **Step 3: Implement `resolveKey` and use it in the method pass**

Add to `XWorkConverter` (place it just above `addConverterMapping`):

```java
    /**
     * Resolves the conversion mapping key for an annotation: the given name carrying the
     * {@link ConversionRule}'s prefix. A name that already starts with that prefix is returned
     * unchanged, so annotations that spell the prefix out keep working.
     *
     * @param type the annotation's {@link ConversionType}; APPLICATION keys are class names and are never prefixed
     * @param rule the annotation's {@link ConversionRule}
     * @param name an explicit key or a property name derived from a method or field
     * @return the mapping key, or null when no name is available and the entry must be skipped
     * @since 7.3.0
     */
    static String resolveKey(ConversionType type, ConversionRule rule, String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        if (type == ConversionType.APPLICATION) {
            return name;
        }
        String prefix = rule.prefix();
        return name.startsWith(prefix) ? name : prefix + name;
    }
```

Add the imports `org.apache.struts2.conversion.annotations.ConversionRule` and `org.apache.struts2.conversion.annotations.ConversionType`.

Then replace the body of `processMethodAnnotations` (from Task 2) with:

```java
    private void processMethodAnnotations(Map<String, Object> mapping, Class clazz) {
        for (Method method : clazz.getMethods()) {
            for (Annotation annotation : method.getAnnotations()) {
                if (!(annotation instanceof TypeConversion tc)) {
                    continue;
                }
                String name = StringUtils.isEmpty(tc.key()) ? AnnotationUtils.resolvePropertyName(method) : tc.key();
                String key = resolveKey(tc.type(), tc.rule(), name);
                if (key == null) {
                    LOG.warn("Ignoring @TypeConversion on [{}#{}]: no key was given and no property name could be derived from the method",
                            clazz.getName(), method.getName());
                    continue;
                }
                if (mapping.containsKey(key)) {
                    continue;
                }
                LOG.debug("TypeConversion [{}/{}] on method [{}] resolved to key [{}]",
                        tc.converter(), tc.converterClass(), method.getName(), key);
                annotationProcessor.process(mapping, tc, key);
            }
        }
    }
```

Two behaviour changes land here: an explicit method-level key is now normalised through `resolveKey`, and the `break` becomes `continue` so one already-mapped key no longer aborts the method's remaining annotations. `DefaultObjectTypeDeterminer` may no longer be referenced by this class — remove the import only if the compiler says it is unused.

- [ ] **Step 4: Run the unit tests and make sure they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=XWorkConverterTest`
Expected: PASS.

- [ ] **Step 5: Write the failing fixture test for explicit-key normalisation**

Create `core/src/test/java/org/apache/struts2/util/ExplicitKeyConversionAction.java` (Apache licence header copied from `MyBeanAction.java`):

```java
package org.apache.struts2.util;

import org.apache.struts2.conversion.annotations.ConversionRule;
import org.apache.struts2.conversion.annotations.TypeConversion;

import java.util.ArrayList;
import java.util.List;

/**
 * Declares a method level {@link TypeConversion} with an explicit, unprefixed key. Before WW-3871
 * this registered a bare {@code bareList} mapping that nothing ever read.
 */
public class ExplicitKeyConversionAction {

    private List bareList = new ArrayList();

    public List getBareList() {
        return bareList;
    }

    @TypeConversion(key = "bareList", rule = ConversionRule.CREATE_IF_NULL, value = "true")
    public void setBareList(List bareList) {
        this.bareList = bareList;
    }
}
```

Append to `XWorkConverterTest`:

```java
    public void testExplicitMethodKeyGetsTheRulePrefix() {
        XWorkConverter freshConverter = container.inject(XWorkConverter.class);
        freshConverter.setTypeConverterHolder(new StrutsTypeConverterHolder());

        assertEquals("true", freshConverter.getConverter(ExplicitKeyConversionAction.class, "CreateIfNull_bareList"));
        assertNull(freshConverter.getConverter(ExplicitKeyConversionAction.class, "bareList"));
    }
```

Add the import `org.apache.struts2.util.ExplicitKeyConversionAction` if the test class does not already import that package wholesale.

- [ ] **Step 6: Run it and confirm it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=XWorkConverterTest#testExplicitMethodKeyGetsTheRulePrefix`
Expected: PASS.

- [ ] **Step 7: Run the wider conversion suite for regressions**

Run: `mvn test -DskipAssembly -pl core -Dtest='XWorkConverterTest+AnnotationXWorkConverterTest+MyBeanActionTest+XWorkBasicConverterTest'`
Expected: PASS.

- [ ] **Step 8: Commit**

```bash
git add core/src/main/java/org/apache/struts2/conversion/impl/XWorkConverter.java \
        core/src/test/java/org/apache/struts2/conversion/impl/XWorkConverterTest.java \
        core/src/test/java/org/apache/struts2/util/ExplicitKeyConversionAction.java
git commit -m "WW-3871 feat(core): derive conversion mapping keys through a single resolver"
```

---

### Task 4: Class-level bare keys, `continue` fix, empty-key warning

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/conversion/impl/XWorkConverter.java` (`processClassLevelAnnotations`)
- Test fixture (create): `core/src/test/java/org/apache/struts2/util/BareKeyConversionAction.java`
- Test fixture (create): `core/src/test/java/org/apache/struts2/util/CollidingKeyConversionAction.java`
- Test resource (create): `core/src/test/resources/org/apache/struts2/util/CollidingKeyConversionAction-conversion.properties`
- Test (modify): `core/src/test/java/org/apache/struts2/conversion/impl/XWorkConverterTest.java`

**Interfaces:**
- Consumes: `XWorkConverter.resolveKey(ConversionType, ConversionRule, String)` from Task 3.
- Produces: nothing new; changes `processClassLevelAnnotations` behaviour only.

- [ ] **Step 1: Write the failing tests**

Create `core/src/test/java/org/apache/struts2/util/BareKeyConversionAction.java` (licence header first):

```java
package org.apache.struts2.util;

import org.apache.struts2.conversion.annotations.Conversion;
import org.apache.struts2.conversion.annotations.ConversionRule;
import org.apache.struts2.conversion.annotations.TypeConversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class level counterpart of {@link MyBeanAction}, declaring the same four conversions with
 * bare property names instead of spelled-out prefixes.
 */
@Conversion(
        conversions = {
                @TypeConversion(key = "annotatedBeanMap", rule = ConversionRule.KEY_PROPERTY, value = "id"),
                @TypeConversion(key = "annotatedBeanMap", rule = ConversionRule.ELEMENT, converterClass = MyBean.class),
                @TypeConversion(key = "annotatedBeanList", rule = ConversionRule.KEY_PROPERTY, value = "id"),
                @TypeConversion(key = "annotatedBeanList", rule = ConversionRule.ELEMENT, converterClass = MyBean.class)
        })
public class BareKeyConversionAction {

    private Map annotatedBeanMap = new HashMap();
    private List annotatedBeanList = new ArrayList();

    public Map getAnnotatedBeanMap() {
        return annotatedBeanMap;
    }

    public void setAnnotatedBeanMap(Map annotatedBeanMap) {
        this.annotatedBeanMap = annotatedBeanMap;
    }

    public List getAnnotatedBeanList() {
        return annotatedBeanList;
    }

    public void setAnnotatedBeanList(List annotatedBeanList) {
        this.annotatedBeanList = annotatedBeanList;
    }
}
```

Note the same bare key appears twice with different rules — that is the point: the rule, not the key, disambiguates them.

Create `core/src/test/java/org/apache/struts2/util/CollidingKeyConversionAction.java` (licence header first):

```java
package org.apache.struts2.util;

import org.apache.struts2.conversion.annotations.Conversion;
import org.apache.struts2.conversion.annotations.ConversionRule;
import org.apache.struts2.conversion.annotations.TypeConversion;

import java.util.ArrayList;
import java.util.List;

/**
 * The first conversion entry collides with a key already supplied by
 * {@code CollidingKeyConversionAction-conversion.properties}; the second must still be registered.
 */
@Conversion(
        conversions = {
                @TypeConversion(key = "fromProperties", rule = ConversionRule.CREATE_IF_NULL, value = "false"),
                @TypeConversion(key = "afterTheCollision", rule = ConversionRule.CREATE_IF_NULL, value = "true")
        })
public class CollidingKeyConversionAction {

    private List afterTheCollision = new ArrayList();

    public List getAfterTheCollision() {
        return afterTheCollision;
    }

    public void setAfterTheCollision(List afterTheCollision) {
        this.afterTheCollision = afterTheCollision;
    }
}
```

Create `core/src/test/resources/org/apache/struts2/util/CollidingKeyConversionAction-conversion.properties` (comment-style Apache licence header copied verbatim from `core/src/test/resources/org/apache/struts2/test/DataAware-conversion.properties`, then):

```properties
CreateIfNull_fromProperties=true
```

Append to `XWorkConverterTest`:

```java
    public void testClassLevelBareKeysGetTheRulePrefix() {
        XWorkConverter freshConverter = container.inject(XWorkConverter.class);
        freshConverter.setTypeConverterHolder(new StrutsTypeConverterHolder());

        assertEquals("id", freshConverter.getConverter(BareKeyConversionAction.class, "KeyProperty_annotatedBeanMap"));
        assertEquals(MyBean.class, freshConverter.getConverter(BareKeyConversionAction.class, "Element_annotatedBeanMap"));
        assertEquals("id", freshConverter.getConverter(BareKeyConversionAction.class, "KeyProperty_annotatedBeanList"));
        assertEquals(MyBean.class, freshConverter.getConverter(BareKeyConversionAction.class, "Element_annotatedBeanList"));
    }

    public void testClassLevelBareKeysMatchTheSpelledOutForm() {
        XWorkConverter freshConverter = container.inject(XWorkConverter.class);
        freshConverter.setTypeConverterHolder(new StrutsTypeConverterHolder());

        for (String key : new String[]{"KeyProperty_annotatedBeanMap", "Element_annotatedBeanMap",
                "KeyProperty_annotatedBeanList", "Element_annotatedBeanList"}) {
            assertEquals("mismatch for " + key,
                    freshConverter.getConverter(MyBeanAction.class, key),
                    freshConverter.getConverter(BareKeyConversionAction.class, key));
        }
    }

    public void testClassLevelEntriesAfterAKeyCollisionAreStillRegistered() {
        XWorkConverter freshConverter = container.inject(XWorkConverter.class);
        freshConverter.setTypeConverterHolder(new StrutsTypeConverterHolder());

        // supplied by the -conversion.properties file, so the annotation must not overwrite it
        assertEquals("true", freshConverter.getConverter(CollidingKeyConversionAction.class, "CreateIfNull_fromProperties"));
        // the entry after the collision used to be dropped by `break`
        assertEquals("true", freshConverter.getConverter(CollidingKeyConversionAction.class, "CreateIfNull_afterTheCollision"));
    }
```

Add imports for `org.apache.struts2.util.BareKeyConversionAction`, `org.apache.struts2.util.CollidingKeyConversionAction`, `org.apache.struts2.util.MyBean` and `org.apache.struts2.util.MyBeanAction` as needed.

- [ ] **Step 2: Run them to make sure they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest='XWorkConverterTest#testClassLevelBareKeysGetTheRulePrefix+XWorkConverterTest#testClassLevelEntriesAfterAKeyCollisionAreStillRegistered'`
Expected: FAIL — bare keys resolve to null (no prefix applied), and the post-collision entry is missing.

- [ ] **Step 3: Rewrite `processClassLevelAnnotations`**

```java
    private void processClassLevelAnnotations(Map<String, Object> mapping, Class clazz) {
        for (Annotation annotation : clazz.getAnnotations()) {
            if (!(annotation instanceof Conversion conversion)) {
                continue;
            }
            for (TypeConversion tc : conversion.conversions()) {
                String key = resolveKey(tc.type(), tc.rule(), tc.key());
                if (key == null) {
                    LOG.warn("Ignoring @TypeConversion [{}/{}] declared on [{}]: no key was given and a class level annotation has no property name to derive one from",
                            tc.converter(), tc.converterClass(), clazz.getName());
                    continue;
                }
                if (mapping.containsKey(key)) {
                    continue;
                }
                LOG.debug("TypeConversion [{}/{}] declared on [{}] resolved to key [{}]",
                        tc.converter(), tc.converterClass(), clazz.getName(), key);
                annotationProcessor.process(mapping, tc, key);
            }
        }
    }
```

Three changes: `resolveKey` is applied, the `containsKey` guard now tests the *resolved* key and `continue`s instead of `break`ing, and an unresolvable key is skipped with a WARN rather than registering a `""` mapping.

- [ ] **Step 4: Run the new tests and make sure they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=XWorkConverterTest`
Expected: PASS.

- [ ] **Step 5: Run the conversion suite for regressions**

Run: `mvn test -DskipAssembly -pl core -Dtest='XWorkConverterTest+AnnotationXWorkConverterTest+MyBeanActionTest+XWorkBasicConverterTest+StringConverterTest'`
Expected: PASS. `MyBeanActionTest` passing here is the backward-compatibility proof: its fixture still spells out every prefix.

- [ ] **Step 6: Commit**

```bash
git add core/src/main/java/org/apache/struts2/conversion/impl/XWorkConverter.java \
        core/src/test/java/org/apache/struts2/conversion/impl/XWorkConverterTest.java \
        core/src/test/java/org/apache/struts2/util/BareKeyConversionAction.java \
        core/src/test/java/org/apache/struts2/util/CollidingKeyConversionAction.java \
        core/src/test/resources/org/apache/struts2/util/CollidingKeyConversionAction-conversion.properties
git commit -m "WW-3871 fix(core): derive class level conversion keys and stop dropping later entries"
```

---

### Task 5: Field-level `@TypeConversion`

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/conversion/annotations/TypeConversion.java:153` (`@Target`)
- Modify: `core/src/main/java/org/apache/struts2/conversion/impl/XWorkConverter.java` (`addConverterMapping`, new `processFieldAnnotations`)
- Test fixture (create): `core/src/test/java/org/apache/struts2/util/FieldConversionAction.java`
- Test (modify): `core/src/test/java/org/apache/struts2/conversion/impl/XWorkConverterTest.java`

**Interfaces:**
- Consumes: `XWorkConverter.resolveKey(ConversionType, ConversionRule, String)` from Task 3.
- Produces: `private void processFieldAnnotations(Map<String, Object> mapping, Class clazz)`, called last from `addConverterMapping`.

- [ ] **Step 1: Write the failing test**

Create `core/src/test/java/org/apache/struts2/util/FieldConversionAction.java` (licence header first):

```java
package org.apache.struts2.util;

import org.apache.struts2.conversion.annotations.ConversionRule;
import org.apache.struts2.conversion.annotations.TypeConversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exercises field level {@link TypeConversion}: {@code fieldOnlyList} is annotated on the field
 * alone, while {@code contestedMap} is annotated on both the field and its setter so the
 * class &gt; method &gt; field precedence can be asserted.
 */
public class FieldConversionAction {

    @TypeConversion(rule = ConversionRule.CREATE_IF_NULL, value = "true")
    private List fieldOnlyList = new ArrayList();

    @TypeConversion(rule = ConversionRule.KEY, converterClass = String.class)
    private Map contestedMap = new HashMap();

    public List getFieldOnlyList() {
        return fieldOnlyList;
    }

    public void setFieldOnlyList(List fieldOnlyList) {
        this.fieldOnlyList = fieldOnlyList;
    }

    public Map getContestedMap() {
        return contestedMap;
    }

    @TypeConversion(rule = ConversionRule.KEY, converterClass = Long.class)
    public void setContestedMap(Map contestedMap) {
        this.contestedMap = contestedMap;
    }
}
```

Append to `XWorkConverterTest`:

```java
    public void testFieldLevelAnnotationDerivesKeyFromTheFieldName() {
        XWorkConverter freshConverter = container.inject(XWorkConverter.class);
        freshConverter.setTypeConverterHolder(new StrutsTypeConverterHolder());

        assertEquals("true", freshConverter.getConverter(FieldConversionAction.class, "CreateIfNull_fieldOnlyList"));
    }

    public void testMethodAnnotationWinsOverFieldAnnotation() {
        XWorkConverter freshConverter = container.inject(XWorkConverter.class);
        freshConverter.setTypeConverterHolder(new StrutsTypeConverterHolder());

        assertEquals(Long.class, freshConverter.getConverter(FieldConversionAction.class, "Key_contestedMap"));
    }
```

Add the import for `org.apache.struts2.util.FieldConversionAction`.

- [ ] **Step 2: Run it to make sure it fails**

Run: `mvn test-compile -DskipAssembly -pl core`
Expected: compilation failure — `annotation type org.apache.struts2.conversion.annotations.TypeConversion is not applicable to this kind of declaration` on the two annotated fields.

- [ ] **Step 3: Widen the annotation target**

In `TypeConversion.java`, change line 153:

```java
@Target({ElementType.METHOD, ElementType.FIELD})
```

- [ ] **Step 4: Run the test again — it must now fail on the assertion, not on compilation**

Run: `mvn test -DskipAssembly -pl core -Dtest=XWorkConverterTest#testFieldLevelAnnotationDerivesKeyFromTheFieldName`
Expected: FAIL — `expected:<true> but was:<null>`; the annotation compiles but nothing reads it yet.

- [ ] **Step 5: Add the field pass**

In `XWorkConverter`, add the call at the end of `addConverterMapping`:

```java
    protected void addConverterMapping(Map<String, Object> mapping, Class clazz) {
        // Process <clazz>-conversion.properties file
        String converterFilename = buildConverterFilename(clazz);
        fileProcessor.process(mapping, clazz, converterFilename);

        processClassLevelAnnotations(mapping, clazz);
        processMethodAnnotations(mapping, clazz);
        processFieldAnnotations(mapping, clazz);
    }
```

and the new method after `processMethodAnnotations`:

```java
    /**
     * Registers {@link TypeConversion} annotations found on the class' own fields. Only declared
     * fields are read: {@link #buildConverterMapping(Class)} already walks the class hierarchy and
     * calls this method once per class. Static and synthetic fields are skipped, which also makes
     * this a no-op for interfaces.
     */
    private void processFieldAnnotations(Map<String, Object> mapping, Class clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }
            for (Annotation annotation : field.getAnnotations()) {
                if (!(annotation instanceof TypeConversion tc)) {
                    continue;
                }
                String name = StringUtils.isEmpty(tc.key()) ? field.getName() : tc.key();
                String key = resolveKey(tc.type(), tc.rule(), name);
                if (key == null) {
                    // defensive: a field always has a name, so this is unreachable in practice
                    LOG.warn("Ignoring @TypeConversion on field [{}#{}]: the key could not be resolved",
                            clazz.getName(), field.getName());
                    continue;
                }
                if (mapping.containsKey(key)) {
                    LOG.debug("Skipping @TypeConversion on field [{}#{}]: key [{}] is already mapped by a higher precedence source",
                            clazz.getName(), field.getName(), key);
                    continue;
                }
                LOG.debug("TypeConversion [{}/{}] on field [{}] resolved to key [{}]",
                        tc.converter(), tc.converterClass(), field.getName(), key);
                annotationProcessor.process(mapping, tc, key);
            }
        }
    }
```

Add the imports `java.lang.reflect.Field` and `java.lang.reflect.Modifier`.

- [ ] **Step 6: Run the new tests and make sure they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=XWorkConverterTest`
Expected: PASS, including `testMethodAnnotationWinsOverFieldAnnotation` — the method pass runs first and claims `Key_contestedMap` with `Long.class`.

- [ ] **Step 7: Run the full core module**

Run: `mvn test -DskipAssembly -pl core`
Expected: PASS. This is the first full run; field scanning now touches every class the converter maps, so a broad run is warranted here rather than at the end.

- [ ] **Step 8: Commit**

```bash
git add core/src/main/java/org/apache/struts2/conversion/annotations/TypeConversion.java \
        core/src/main/java/org/apache/struts2/conversion/impl/XWorkConverter.java \
        core/src/test/java/org/apache/struts2/conversion/impl/XWorkConverterTest.java \
        core/src/test/java/org/apache/struts2/util/FieldConversionAction.java
git commit -m "WW-3871 feat(core): support @TypeConversion on fields"
```

---

### Task 6: End-to-end proof through the action lifecycle

The unit tests assert mapping contents; this asserts that bare keys actually bind request parameters, through the same path `MyBeanActionTest` exercises.

**Files:**
- Test fixture (create): `core/src/test/java/org/apache/struts2/util/MyBeanBareKeyAction.java`
- Test resource (modify): `core/src/test/resources/xwork-sample.xml:128-132` (add a sibling action)
- Test (modify): `core/src/test/java/org/apache/struts2/util/MyBeanActionTest.java`

**Interfaces:**
- Consumes: everything from Tasks 3-5.
- Produces: action name `MyBeanBareKey` in `xwork-sample.xml`.

- [ ] **Step 1: Write the failing test**

Create `core/src/test/java/org/apache/struts2/util/MyBeanBareKeyAction.java` (licence header first) — a copy of `MyBeanAction` with bare class-level keys and no `beanList`/`beanMap` properties:

```java
package org.apache.struts2.util;

import org.apache.struts2.action.Action;
import org.apache.struts2.conversion.annotations.Conversion;
import org.apache.struts2.conversion.annotations.ConversionRule;
import org.apache.struts2.conversion.annotations.TypeConversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link MyBeanAction} restated with bare property names as conversion keys. Both must bind
 * identically; {@code MyBeanAction} keeps the spelled-out prefixes so the old form stays covered.
 */
@Conversion(
        conversions = {
                @TypeConversion(key = "annotatedBeanMap", rule = ConversionRule.KEY_PROPERTY, value = "id"),
                @TypeConversion(key = "annotatedBeanMap", rule = ConversionRule.ELEMENT, converterClass = MyBean.class),
                @TypeConversion(key = "annotatedBeanList", rule = ConversionRule.KEY_PROPERTY, value = "id"),
                @TypeConversion(key = "annotatedBeanList", rule = ConversionRule.ELEMENT, converterClass = MyBean.class)
        })
public class MyBeanBareKeyAction implements Action {

    private Map annotatedBeanMap = new HashMap();
    private List annotatedBeanList = new ArrayList();

    public Map getAnnotatedBeanMap() {
        return annotatedBeanMap;
    }

    @TypeConversion(rule = ConversionRule.KEY, converterClass = Long.class)
    public void setAnnotatedBeanMap(Map annotatedBeanMap) {
        this.annotatedBeanMap = annotatedBeanMap;
    }

    public List getAnnotatedBeanList() {
        return annotatedBeanList;
    }

    @TypeConversion(rule = ConversionRule.CREATE_IF_NULL, value = "true")
    public void setAnnotatedBeanList(List annotatedBeanList) {
        this.annotatedBeanList = annotatedBeanList;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }
}
```

Register it in `core/src/test/resources/xwork-sample.xml`, immediately after the existing `MyBean` action (line 132):

```xml
        <action name="MyBeanBareKey" class="org.apache.struts2.util.MyBeanBareKeyAction">
            <interceptor-ref name="debugStack"/>
            <interceptor-ref name="defaultStack"/>
            <result name="success" type="mock"/>
        </action>
```

Append to `MyBeanActionTest`:

```java
    public void testBareConversionKeysBindTheSameWayAsPrefixedOnes() throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("annotatedBeanList(1234567890).name", "This is the bla bean by annotation");
        params.put("annotatedBeanMap[1234567891].id", "1234567891");
        params.put("annotatedBeanMap[1234567891].name", "This is the 2nd bla bean by annotation");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "MyBeanBareKey", null, extraContext.getContextMap());
        proxy.execute();
        MyBeanBareKeyAction action = (MyBeanBareKeyAction) proxy.getInvocation().getAction();

        // CreateIfNull_annotatedBeanList + Element_annotatedBeanList
        assertEquals(1, action.getAnnotatedBeanList().size());
        assertEquals(MyBean.class, action.getAnnotatedBeanList().get(0).getClass());
        assertEquals("This is the bla bean by annotation",
                proxy.getInvocation().getStack().findValue("annotatedBeanList.get(0).name"));

        // Key_annotatedBeanMap makes the key a Long, Element_annotatedBeanMap makes the value a MyBean
        assertTrue(action.getAnnotatedBeanMap().containsKey(1234567891L));
        assertEquals(MyBean.class, action.getAnnotatedBeanMap().get(1234567891L).getClass());
        assertEquals("This is the 2nd bla bean by annotation",
                proxy.getInvocation().getStack().findValue("annotatedBeanMap.get(1234567891L).name"));
    }
```

Unlike the existing tests in this class, this one lets exceptions propagate rather than catching and calling `fail()` — a stack trace from the runner is more useful than a bare failure.

- [ ] **Step 2: Run it — it must pass on the first try**

This task adds no production code, so there is no red phase: Tasks 3-5 already implement the behaviour and this test only proves it reaches the action. A failure here is a real defect in those tasks, not a missing feature — debug it there rather than adjusting this test.

Run: `mvn test -DskipAssembly -pl core -Dtest=MyBeanActionTest#testBareConversionKeysBindTheSameWayAsPrefixedOnes`
Expected: PASS.

- [ ] **Step 3: Confirm the old form still binds**

Run: `mvn test -DskipAssembly -pl core -Dtest=MyBeanActionTest`
Expected: PASS — all three tests, including the two pre-existing ones against the spelled-out fixture.

- [ ] **Step 4: Commit**

```bash
git add core/src/test/java/org/apache/struts2/util/MyBeanBareKeyAction.java \
        core/src/test/java/org/apache/struts2/util/MyBeanActionTest.java \
        core/src/test/resources/xwork-sample.xml
git commit -m "WW-3871 test(core): assert bare conversion keys bind through the action lifecycle"
```

---

### Task 7: Documentation

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/conversion/annotations/TypeConversion.java` (class Javadoc: usage snippet, parameter table, example)

**Interfaces:**
- Consumes: the behaviour built in Tasks 1-5.
- Produces: nothing code-facing.

- [ ] **Step 1: Update the usage snippet**

Replace the `usage` snippet body (line 51) with:

```java
 * <p>The TypeConversion annotation can be applied at field and method level.</p>
```

- [ ] **Step 2: Update the `key` row of the parameter table**

Replace the `key` row (lines 67-72) with:

```java
 * <tr>
 * <td>key</td>
 * <td>no</td>
 * <td>The annotated property/field name</td>
 * <td>The property name the rule applies to. The matching prefix for the given rule
 * (<code>Key_</code>, <code>Element_</code>, <code>KeyProperty_</code>, <code>CreateIfNull_</code>)
 * is prepended automatically unless the key already carries it. Required on TYPE level annotations,
 * where there is no member name to derive it from.</td>
 * </tr>
```

- [ ] **Step 3: Update the `key()` attribute Javadoc**

Replace the Javadoc on `key()` (lines 157-163) with:

```java
    /**
     * The property name this conversion applies to. Optional on fields and methods, where it
     * defaults to the property name; required on TYPE level annotations.
     *
     * <p>The prefix matching the declared {@link ConversionRule} is prepended automatically, so
     * {@code @TypeConversion(key = "users", rule = ConversionRule.CREATE_IF_NULL, value = "true")}
     * and {@code @TypeConversion(key = "CreateIfNull_users", ...)} are equivalent.</p>
     *
     * @return key
     * @since 7.3.0 the rule prefix is derived; previously the full key had to be spelled out
     */
```

- [ ] **Step 4: Simplify the example**

In the example snippet, add a field-level case and drop a redundant prefix. Replace the `setUsers` example (lines 132-135) with:

```java
 *   &#64;TypeConversion(rule = ConversionRule.CREATE_IF_NULL, value = "true")
 *   private List users = null;
 *
 *   &#64;TypeConversion(rule = ConversionRule.COLLECTION, converterClass = String.class)
 *   public void setUsers( List users ) {
 *       this.users = users;
 *   }
```

- [ ] **Step 5: Verify the Javadoc builds**

Run: `mvn javadoc:javadoc -DskipAssembly -pl core -q`
Expected: no errors on `TypeConversion.java` or `ConversionRule.java`. Pre-existing warnings elsewhere in the module are not this task's concern.

- [ ] **Step 6: Run the full core suite one last time**

Run: `mvn test -DskipAssembly -pl core`
Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add core/src/main/java/org/apache/struts2/conversion/annotations/TypeConversion.java
git commit -m "WW-3871 docs(core): document conversion key derivation and field level support"
```

---

## Verification Checklist

Before opening the PR:

- [ ] `mvn test -DskipAssembly -pl core` passes in full
- [ ] `MyBeanActionTest` still passes with its original spelled-out prefixes untouched — the backward-compatibility guarantee
- [ ] `git log --oneline main..HEAD` shows every commit prefixed `WW-3871`
- [ ] PR title: `WW-3871 Derive ConversionRule prefixes for @TypeConversion keys`
- [ ] PR body links the ticket: `Fixes [WW-3871](https://issues.apache.org/jira/browse/WW-3871)`
- [ ] This is not a security patch (no OGNL evaluation, parameter filtering, upload or auth path is touched), so a public PR is appropriate
