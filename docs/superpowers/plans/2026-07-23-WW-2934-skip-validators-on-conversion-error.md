# WW-2934 Skip Validators on Conversion Error — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** When opted in, skip a field's remaining validators once that field has a type-conversion error, so the user sees only the conversion message instead of a redundant duplicate (e.g. conversion + `required`).

**Architecture:** Add a global constant `struts.validators.skipValidatorsOnConversionError` (default `false`). Inject it into `DefaultActionValidatorManager`. Inside its validator loop, when the flag is on and the current validator is a `FieldValidator` (but not the `conversion` validator itself) whose full field name is present in `ActionContext.getConversionErrors()`, skip it. Action-level validators are untouched.

**Tech Stack:** Java, Struts 2 core, JUnit 3-style `XWorkTestCase` tests, AssertJ available but the surrounding test class uses `junit.framework.TestCase` assertions.

## Global Constraints

- **Commit prefix:** every commit message starts with `WW-2934` (e.g. `WW-2934 feat(core): ...`).
- **Constant name (verbatim):** `struts.validators.skipValidatorsOnConversionError`, default value `false`.
- **Java field naming:** `Struts*` prefix convention is for default implementation *classes*, not relevant here — reuse the existing `DefaultActionValidatorManager`.
- **Test style:** core tests are JUnit 3/4 — test methods are `public void testXxx()` on a class extending `XWorkTestCase`. A `@Test` annotation here silently never runs. Do NOT use `@Test`.
- **License header:** every new `.java` and `.xml` file must begin with the Apache license header (copy from an existing sibling file in the same directory).

---

### Task 1: Skip field validators on conversion error (opt-in)

**Files:**
- Create: `core/src/test/java/org/apache/struts2/validator/ConversionErrorSkipAction.java` (test fixture action)
- Create: `core/src/test/resources/org/apache/struts2/validator/ConversionErrorSkipAction-validation.xml` (test fixture validators)
- Modify: `core/src/main/java/org/apache/struts2/StrutsConstants.java` (add constant, near the other validator constants around line 420)
- Modify: `core/src/main/resources/org/apache/struts2/default.properties` (document the constant)
- Modify: `core/src/main/java/org/apache/struts2/validator/DefaultActionValidatorManager.java` (inject flag + add skip check in `validate(...)`)
- Test: `core/src/test/java/org/apache/struts2/validator/DefaultActionValidatorManagerTest.java` (add four test methods)

**Interfaces:**
- Consumes: `ActionContext.getContext().getConversionErrors()` → `Map<String, ConversionData>` keyed by full field name; `ConversionData(Object value, Class toClass)`; `DefaultActionValidatorManager.validate(Object object, String context)`.
- Produces: `DefaultActionValidatorManager.setSkipValidatorsOnConversionError(String)` (public, `@Inject(required=false)`); `StrutsConstants.STRUTS_VALIDATORS_SKIP_VALIDATORS_ON_CONVERSION_ERROR` = `"struts.validators.skipValidatorsOnConversionError"`.

---

- [ ] **Step 1: Create the test fixture action**

Create `core/src/test/java/org/apache/struts2/validator/ConversionErrorSkipAction.java` (prepend the Apache license header copied from a sibling file such as `DefaultActionValidatorManagerTest.java`):

```java
package org.apache.struts2.validator;

import org.apache.struts2.ActionSupport;

/**
 * Fixture for WW-2934: an Integer field ("age") that carries both a conversion
 * validator and a required validator, plus an unrelated required String field
 * ("name") and an action-level validator (see the matching -validation.xml).
 */
public class ConversionErrorSkipAction extends ActionSupport {

    private Integer age;
    private String name;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

- [ ] **Step 2: Create the test fixture validation XML**

Create `core/src/test/resources/org/apache/struts2/validator/ConversionErrorSkipAction-validation.xml` (prepend the Apache license header as an XML comment, copied from a sibling such as `core/src/test/resources/org/apache/struts2/test/User-validation.xml`):

```xml
<!DOCTYPE validators PUBLIC "-//Apache Struts//XWork Validator 1.0.2//EN" "https://struts.apache.org/dtds/xwork-validator-1.0.2.dtd">
<validators>
    <field name="age">
        <field-validator type="conversion">
            <message>Age must be a valid number</message>
        </field-validator>
        <field-validator type="required">
            <message>Age is required</message>
        </field-validator>
    </field>

    <field name="name">
        <field-validator type="required">
            <message>Name is required</message>
        </field-validator>
    </field>

    <validator type="expression">
        <param name="expression">false</param>
        <message>Action level always fails</message>
    </validator>
</validators>
```

Note: `age` is an `Integer` (defaults to `null`) so the `required` validator actually fires. The `conversion` validator only adds its message when a conversion error exists for the field. The bare `<validator>` (not `<field-validator>`) is action-level.

- [ ] **Step 3: Write the failing tests**

Add these four methods to `DefaultActionValidatorManagerTest` (before the closing brace of the class). Add imports at the top: `import org.apache.struts2.ActionContext;`, `import org.apache.struts2.conversion.impl.ConversionData;` (`java.util.List` and `java.util.Map` are already imported).

```java
public void testConversionError_bothErrorsWhenFlagDisabledByDefault() {
    ConversionErrorSkipAction action = new ConversionErrorSkipAction();
    ActionContext.getContext().getConversionErrors()
            .put("age", new ConversionData(new String[]{"one"}, Integer.class));

    actionValidatorManager.validate(action, null);

    List<String> ageErrors = action.getFieldErrors().get("age");
    assertNotNull(ageErrors);
    assertEquals(2, ageErrors.size()); // conversion + required, current behavior
    assertTrue(ageErrors.contains("Age must be a valid number"));
    assertTrue(ageErrors.contains("Age is required"));
}

public void testConversionError_fieldValidatorsSkippedWhenEnabled() {
    ConversionErrorSkipAction action = new ConversionErrorSkipAction();
    ActionContext.getContext().getConversionErrors()
            .put("age", new ConversionData(new String[]{"one"}, Integer.class));
    actionValidatorManager.setSkipValidatorsOnConversionError("true");

    actionValidatorManager.validate(action, null);

    List<String> ageErrors = action.getFieldErrors().get("age");
    assertNotNull(ageErrors);
    // required is skipped; the conversion validator itself still runs
    assertEquals(1, ageErrors.size());
    assertEquals("Age must be a valid number", ageErrors.get(0));
}

public void testConversionError_unrelatedFieldStillValidatedWhenEnabled() {
    ConversionErrorSkipAction action = new ConversionErrorSkipAction();
    ActionContext.getContext().getConversionErrors()
            .put("age", new ConversionData(new String[]{"one"}, Integer.class));
    actionValidatorManager.setSkipValidatorsOnConversionError("true");

    actionValidatorManager.validate(action, null);

    List<String> nameErrors = action.getFieldErrors().get("name");
    assertNotNull(nameErrors); // "name" has no conversion error, still validated
    assertEquals(1, nameErrors.size());
    assertEquals("Name is required", nameErrors.get(0));
}

public void testConversionError_actionLevelValidatorUnaffectedWhenEnabled() {
    ConversionErrorSkipAction action = new ConversionErrorSkipAction();
    ActionContext.getContext().getConversionErrors()
            .put("age", new ConversionData(new String[]{"one"}, Integer.class));
    actionValidatorManager.setSkipValidatorsOnConversionError("true");

    actionValidatorManager.validate(action, null);

    assertTrue(action.hasActionErrors());
    assertTrue(action.getActionErrors().contains("Action level always fails"));
}
```

- [ ] **Step 4: Run the tests to verify they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=DefaultActionValidatorManagerTest`
Expected: FAIL — compilation error, `cannot find symbol: method setSkipValidatorsOnConversionError(String)` (the production setter does not exist yet).

- [ ] **Step 5: Add the constant**

In `core/src/main/java/org/apache/struts2/StrutsConstants.java`, add near the other validator constants (e.g. just after `STRUTS_ACTIONVALIDATORMANAGER` around line 420):

```java
    /** @see org.apache.struts2.validator.DefaultActionValidatorManager */
    public static final String STRUTS_VALIDATORS_SKIP_VALIDATORS_ON_CONVERSION_ERROR = "struts.validators.skipValidatorsOnConversionError";
```

- [ ] **Step 6: Document the constant in default.properties**

In `core/src/main/resources/org/apache/struts2/default.properties`, add (grouping it with other behavior toggles; place it after an existing active property such as `struts.devMode = false`):

```properties
### When set to true, a field's remaining validators are skipped once that field
### has a type conversion error, avoiding a duplicate error (WW-2934).
### valid values are: true, false (false is the default)
struts.validators.skipValidatorsOnConversionError = false
```

- [ ] **Step 7: Inject the flag into the manager**

In `core/src/main/java/org/apache/struts2/validator/DefaultActionValidatorManager.java`:

Add the import (with the other `org.apache.struts2.validator.validators` usages are not yet imported here — add both needed imports):

```java
import org.apache.struts2.validator.validators.ConversionErrorFieldValidator;
```

Add a field next to `reloadingConfigs` (after line 77, `protected boolean reloadingConfigs;`):

```java
    protected boolean skipValidatorsOnConversionError;
```

Add the injected setter next to `setReloadingConfigs` (after its closing brace, around line 98):

```java
    @Inject(value = StrutsConstants.STRUTS_VALIDATORS_SKIP_VALIDATORS_ON_CONVERSION_ERROR, required = false)
    public void setSkipValidatorsOnConversionError(String skipValidatorsOnConversionError) {
        this.skipValidatorsOnConversionError = Boolean.parseBoolean(skipValidatorsOnConversionError);
    }
```

- [ ] **Step 8: Add the skip check in `validate(...)`**

In the same file, inside `validate(Object, String, ValidatorContext, String)`, extend the existing `if (validator instanceof FieldValidator)` block. It currently reads:

```java
            if (validator instanceof FieldValidator) {
                fValidator = (FieldValidator) validator;
                fullFieldName = validatorContext.getFullFieldName(fValidator.getFieldName());

                if ((shortcircuitedFields != null) && shortcircuitedFields.contains(fullFieldName)) {
                    LOG.debug("Short-circuited, skipping");
                    continue;
                }
            }
```

Add the conversion-error skip as a second guard, immediately before that block's closing brace:

```java
            if (validator instanceof FieldValidator) {
                fValidator = (FieldValidator) validator;
                fullFieldName = validatorContext.getFullFieldName(fValidator.getFieldName());

                if ((shortcircuitedFields != null) && shortcircuitedFields.contains(fullFieldName)) {
                    LOG.debug("Short-circuited, skipping");
                    continue;
                }

                if (skipValidatorsOnConversionError
                        && !(validator instanceof ConversionErrorFieldValidator)
                        && ActionContext.getContext().getConversionErrors().containsKey(fullFieldName)) {
                    LOG.debug("Skipping validator {} for field {} due to a conversion error", validator, fullFieldName);
                    continue;
                }
            }
```

The `!(validator instanceof ConversionErrorFieldValidator)` clause keeps the `conversion` validator itself running so its (possibly custom) message is still reported.

- [ ] **Step 9: Run the tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=DefaultActionValidatorManagerTest`
Expected: PASS — all four new methods plus the pre-existing methods in the class are green.

- [ ] **Step 10: Run the broader validator suite for regressions**

Run: `mvn test -DskipAssembly -pl core -Dtest='org.apache.struts2.validator.*'`
Expected: PASS — no regressions in the validator package (default behavior unchanged because the flag defaults to `false`).

- [ ] **Step 11: Commit**

```bash
git add core/src/main/java/org/apache/struts2/StrutsConstants.java \
        core/src/main/resources/org/apache/struts2/default.properties \
        core/src/main/java/org/apache/struts2/validator/DefaultActionValidatorManager.java \
        core/src/test/java/org/apache/struts2/validator/ConversionErrorSkipAction.java \
        core/src/test/java/org/apache/struts2/validator/DefaultActionValidatorManagerTest.java \
        core/src/test/resources/org/apache/struts2/validator/ConversionErrorSkipAction-validation.xml
git commit -m "WW-2934 feat(core): skip field validators on conversion error behind opt-in flag

Co-Authored-By: Claude Opus 4.8 <noreply@anthropic.com>"
```

---

## Self-Review

**Spec coverage:**
- Rollout — global constant default OFF → Steps 5, 6 (constant + `default.properties` = false).
- Where — `DefaultActionValidatorManager.validate()` → Steps 7, 8.
- Behavior — skip FieldValidator on conversion error, exempt `conversion` validator, action-level untouched → Step 8 + tests in Step 3 (`fieldValidatorsSkippedWhenEnabled`, `actionLevelValidatorUnaffectedWhenEnabled`).
- Use `getConversionErrors()` keyed by full field name → Step 8 (`getFullFieldName` + `getConversionErrors().containsKey`).
- Testing — all five spec test cases: flag off both errors, flag on skip, custom conversion validator still runs (asserted as the sole remaining `age` error), action-level unaffected, field without conversion error still validated → Step 3 four methods (the "custom conversion validator still runs" case is covered by asserting the remaining `age` error equals the conversion message).
- Backward compatibility — default `false`, verified by Step 10 regression run and the disabled-by-default test.

**Placeholder scan:** none — every code and command step is concrete.

**Type consistency:** `setSkipValidatorsOnConversionError(String)` and `STRUTS_VALIDATORS_SKIP_VALIDATORS_ON_CONVERSION_ERROR` are named identically in the Interfaces block, Steps 5/7, and the tests. `ConversionData(Object, Class)` matches the confirmed constructor.
