---
date: 2025-10-17T06:33:12+0000
topic: "Struts 2: Validating Fields in Iterators/Collections"
tags: [validation, iterator, visitor-validator, collections, conversion-errors]
status: complete
branch: main
---

# Research: Struts 2 Iterator Field Validation

**Date**: 2025-10-17T06:33:12+0000

## User Question

User is migrating from Struts 1 to Struts 2 (actually Struts 7) and struggling with validation syntax for fields within iterators:

```jsp
<s:iterator value="mother.child" status="status">
   <s:textfield name="mother.child[%{#status.index}].name"/>
   <s:textfield name="mother.child[%{#status.index}].pocketmoney" />
</s:iterator>
```

**Issues encountered:**
1. Tried XML field validators with `<field name="mother.child[].name">` - gets errors for "mother.child[].name" (required even when all children have names)
2. Conversion errors not repopulating fields with bad values despite `<param name="repopulateField">true</param>`
3. Missing equivalent to Struts 1's `indexedListProperty` approach
4. Missing `@Repeatable` for `@DoubleRangeFieldValidator`
5. Confusion about double validator locale formatting (German: 9.999,99 vs Java float format)

## Summary

**Key Finding**: In Struts 2, you CANNOT directly validate indexed collection properties with `<field name="collection[].property">`. Instead, you must use the **VisitorFieldValidator pattern**, which delegates validation to the child object's own validation file.

This is fundamentally different from Struts 1's approach but provides better separation of concerns and reusability.

## Detailed Findings

### The VisitorFieldValidator Pattern

#### Core Implementation

Found in `core/src/main/java/org/apache/struts2/validator/validators/VisitorFieldValidator.java:158-166`:

```java
private void validateArrayElements(Object[] array, String fieldName, String visitorContext) {
    if (array == null) return;

    for (int i = 0; i < array.length; i++) {
        Object o = array[i];
        if (o != null) {
            validateObject(fieldName + "[" + i + "]", o, visitorContext);
        }
    }
}
```

The validator automatically:
1. Iterates through collections/arrays
2. Appends index notation `[0]`, `[1]`, etc. to field names
3. Validates each object using its own validation file
4. Creates proper field error keys like `mother.child[0].name`, `mother.child[1].pocketmoney`

#### Supported Data Types

From `VisitorFieldValidator.java:127-138`:
- Simple Object properties
- Collections of Objects (via `Collection` interface)
- Arrays of Objects

### Solution: Two-File Validation Pattern

#### File 1: Action/Parent Validation (`YourAction-validation.xml`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE validators PUBLIC "-//Apache Struts//XWork Validator 1.0//EN"
    "https://struts.apache.org/dtds/xwork-validator-1.0.dtd">
<validators>
    <field name="mother.child">
        <field-validator type="visitor">
            <param name="appendPrefix">true</param>
            <message></message>
        </field-validator>
    </field>
</validators>
```

**Key Parameters**:
- `appendPrefix` (default: true) - Prepends parent field name to child field names
- `context` (optional) - Specifies validation context for targeted validation

#### File 2: Child Object Validation (`Child-validation.xml`)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE validators PUBLIC "-//Apache Struts//XWork Validator 1.0//EN"
    "https://struts.apache.org/dtds/xwork-validator-1.0.dtd">
<validators>
    <field name="name">
        <field-validator type="requiredstring">
            <message>Child name is required</message>
        </field-validator>
        <field-validator type="stringlength">
            <param name="minLength">2</param>
            <param name="maxLength">50</param>
            <message>Name must be between 2 and 50 characters</message>
        </field-validator>
    </field>

    <field name="pocketmoney">
        <field-validator type="required">
            <message>Pocket money is required</message>
        </field-validator>
        <field-validator type="double">
            <param name="minInclusive">0.00</param>
            <param name="maxInclusive">999.99</param>
            <message>Pocket money must be between 0.00 and 999.99</message>
        </field-validator>
        <field-validator type="conversion">
            <param name="repopulateField">true</param>
            <message>Invalid number format for pocket money</message>
        </field-validator>
    </field>
</validators>
```

### Working Examples from Codebase

#### Example 1: TestBean List Validation

**Action**: `core/src/test/java/org/apache/struts2/validator/VisitorValidatorTestAction.java:37-50`
```java
private List<TestBean> testBeanList = new ArrayList<>();

@StrutsParameter(depth = 3)
public List<TestBean> getTestBeanList() {
    return testBeanList;
}
```

**Validation**: `core/src/test/resources/org/apache/struts2/validator/VisitorValidatorTestAction-validateList-validation.xml`
```xml
<validators>
    <field name="testBeanList">
        <field-validator type="visitor">
            <message>testBeanList: </message>
        </field-validator>
    </field>
</validators>
```

**Child Validation**: `core/src/test/resources/org/apache/struts2/TestBean-validation.xml`
```xml
<validators>
    <field name="name">
        <field-validator type="requiredstring">
            <message>You must enter a name.</message>
        </field-validator>
    </field>
</validators>
```

#### Example 2: Person Object with Visitor

**Action**: `apps/showcase/src/main/java/org/apache/struts2/showcase/person/NewPersonAction.java:37-44`
```java
private Person person;

@StrutsParameter(depth = 1)
public Person getPerson() {
    return person;
}
```

**Validation**: `apps/showcase/src/main/resources/org/apache/struts2/showcase/person/NewPersonAction-validation.xml`
```xml
<validators>
    <field name="person">
        <field-validator type="visitor">
            <message></message>
        </field-validator>
    </field>
</validators>
```

**Child Validation**: `apps/showcase/src/main/resources/org/apache/struts2/showcase/person/Person-validation.xml`
```xml
<validators>
    <field name="name">
        <field-validator type="requiredstring">
            <message>You must enter a first name.</message>
        </field-validator>
    </field>
    <field name="lastName">
        <field-validator type="requiredstring">
            <message>You must enter a last name</message>
        </field-validator>
    </field>
</validators>
```

## Code References

- `core/src/main/java/org/apache/struts2/validator/validators/VisitorFieldValidator.java:89-203` - Main visitor validator implementation
- `core/src/main/java/org/apache/struts2/validator/validators/VisitorFieldValidator.java:158-166` - Array iteration logic
- `core/src/main/java/org/apache/struts2/validator/validators/VisitorFieldValidator.java:168-184` - Individual object validation with field name prefixing
- `core/src/main/java/org/apache/struts2/validator/validators/RepopulateConversionErrorFieldValidatorSupport.java:98-145` - Conversion error repopulation implementation
- `core/src/main/java/org/apache/struts2/validator/validators/ConversionErrorFieldValidator.java:43-63` - Conversion error detection

## Additional Issues Addressed

### 1. Conversion Error Repopulation

**Implementation**: `RepopulateConversionErrorFieldValidatorSupport.java:98-145`

The `repopulateField` parameter should work, but there's complexity with indexed properties:

```java
public void repopulateField(Object object) throws ValidationException {
    Map<String, ConversionData> conversionErrors = ActionContext.getContext().getConversionErrors();
    String fieldName = getFieldName();
    String fullFieldName = getValidatorContext().getFullFieldName(fieldName);

    if (conversionErrors.containsKey(fullFieldName)) {
        Object value = conversionErrors.get(fullFieldName).getValue();
        // ... repopulation logic
    }
}
```

For indexed properties, the `fullFieldName` should be `mother.child[0].pocketmoney`. The visitor validator's `AppendingValidatorContext` (lines 186-222) handles this field name construction.

**Proper usage in child validation**:
```xml
<field name="pocketmoney">
    <field-validator type="conversion">
        <param name="repopulateField">true</param>
        <message>Please enter a valid number</message>
    </field-validator>
</field>
```

### 2. Double Validator and Locale Formatting

**Issue**: User confused about `minInclusive`/`maxInclusive` format vs locale-specific input.

**Clarification**:
- **Type Conversion Layer**: Handles locale-specific formats (e.g., German `9.999,99` → `9999.99`)
- **Validation Layer**: Works with Java numeric values using `.` as decimal separator
- Parameters like `minInclusive="999.99"` use Java format, NOT locale format

For German locale with input `9.999,99`:
1. Type converter parses `9.999,99` → double value `9999.99`
2. Validator checks: `0.00 <= 9999.99 <= 9999.99` ✓

### 3. Decimal Place Validation

The double validator does NOT enforce decimal places. For format-specific validation:

```xml
<field name="pocketmoney">
    <!-- First validate it's a number -->
    <field-validator type="conversion">
        <param name="repopulateField">true</param>
        <message>Invalid number format</message>
    </field-validator>

    <!-- Then validate format (as string before conversion) -->
    <field-validator type="regex">
        <param name="regexExpression"><![CDATA[^\d+,\d{2}$]]></param>
        <message>Please enter amount with exactly 2 decimal places (e.g., 12,34)</message>
    </field-validator>

    <!-- Finally validate range -->
    <field-validator type="double">
        <param name="minInclusive">0.00</param>
        <param name="maxInclusive">999.99</param>
        <message>Amount must be between 0,00 and 999,99</message>
    </field-validator>
</field>
```

### 4. Missing @Repeatable for @DoubleRangeFieldValidator

**Status**: Confirmed missing from codebase inspection.

**Workaround**: Use XML validation instead of annotations for multiple range checks on the same field, or use `@CustomValidator` with expression validation.

**Recommendation**: File JIRA issue for enhancement.

## Architecture Insights

### Why Visitor Pattern vs Direct Field Validation?

**Design Benefits**:
1. **Separation of Concerns**: Child object owns its validation rules
2. **Reusability**: Same Child validation works in different contexts
3. **ModelDriven Pattern**: Aligns with Struts 2's ModelDriven approach
4. **Type Safety**: Each object validates according to its class definition

**Trade-off**: More verbose (requires separate validation file) but more maintainable for complex object graphs.

### Field Name Resolution

The `AppendingValidatorContext` class (`VisitorFieldValidator.java:186-222`) ensures proper field name construction:

```java
public String getFullFieldName(String fieldName) {
    if (parent instanceof VisitorFieldValidator.AppendingValidatorContext) {
        return parent.getFullFieldName(field + "." + fieldName);
    }
    return field + "." + fieldName;
}
```

This recursive construction handles nested visitors (e.g., `grandmother.mother.child[0].name`).

## Important Action Configuration

Don't forget the `@StrutsParameter` annotation with proper depth:

```java
public class MotherAction extends ActionSupport {
    private Mother mother;

    @StrutsParameter(depth = 3)  // Allows mother.child[0].name depth access
    public Mother getMother() { return mother; }

    public void setMother(Mother mother) { this.mother = mother; }
}
```

The `depth` parameter controls how deep OGNL can traverse the object graph for security reasons.

## Comparison with Struts 1

| Struts 1 | Struts 2 |
|----------|----------|
| `<field property="pocketmoney" indexedListProperty="child" depends="mask">` | Two-file pattern: Parent uses visitor, Child defines field rules |
| Single file validation | Distributed validation by object |
| Index-aware validators | Visitor automatically handles indexing |

**Philosophy Change**: Struts 1 focused on form-centric validation; Struts 2 focuses on object-centric validation.

## Related Documentation

- Apache Struts Visitor Validator: https://struts.apache.org/core-developers/visitor-validator
- VisitorFieldValidator API: https://struts.apache.org/maven/struts2-core/apidocs/com/opensymphony/xwork2/validator/validators/VisitorFieldValidator.html

## Open Questions

1. **Repopulation Edge Case**: Does `repopulateField` work correctly for all indexed property scenarios, or are there known limitations?
2. **Performance**: What's the performance impact of visitor validation on large collections (100+ elements)?
3. **Custom Validators**: Can custom validators be easily integrated into the visitor pattern?
4. **Conditional Validation**: How to apply conditional validation (OGNL expressions) within visited objects?

## Recommendations

1. **File JIRA**: Request `@Repeatable` support for `@DoubleRangeFieldValidator`
2. **Documentation**: Clarify in official docs that double validator params use Java format, not locale format
3. **Example**: Add showcase example demonstrating iterator validation with conversion errors
4. **Testing**: Test repopulation behavior specifically with indexed properties to confirm it works as expected