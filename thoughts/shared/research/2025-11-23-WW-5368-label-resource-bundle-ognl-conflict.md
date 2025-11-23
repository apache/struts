---
date: 2025-11-23T11:45:30+0000
topic: "WW-5368: Access warning when resource bundle key starts with 'label'"
tags: [research, codebase, WW-5368, OGNL, security, UIBean, resource-bundles, critical-bug]
status: complete
jira: https://issues.apache.org/jira/browse/WW-5368
---

# Research: WW-5368 - OGNL Security Warning with Resource Bundle Keys Starting with "label"

**Date**: 2025-11-23T11:45:30+0000

## Research Question

Why does using `getText()` with resource bundle keys starting with "label" generate OGNL security warnings: "Access to non-public [protected java.lang.String org.apache.struts2.components.UIBean.label] is blocked!"?

## Summary

The issue occurs when OGNL evaluates expressions like `getText('label.reasonOfTransaction.'+top)` in JSP tags. OGNL's expression parser treats `label` as a potential property access on objects in the ValueStack before evaluating it as part of a string literal. When a UIBean component is on the stack, OGNL attempts to access its `protected String label` field, triggering SecurityMemberAccess warnings even though the intent is to use "label" as part of a resource bundle key name.

**Key Finding**: This is fundamentally an OGNL expression parsing ambiguity issue, not a security vulnerability, but the warnings indicate OGNL is attempting field access during expression evaluation.

## Detailed Findings

### Component 1: UIBean.label Field

**File:** `core/src/main/java/org/apache/struts2/components/UIBean.java`

The UIBean class has a `protected String label` field that is the source of the conflict:

```java
// Line 484
protected String label;

// Line 1142-1145
@StrutsTagAttribute(description="Label expression used for rendering an element specific label")
public void setLabel(String label) {
    this.label = label;
}
```

**Critical Details:**
- Field visibility: `protected` (not private)
- Has public setter: `setLabel(String)`
- **No public getter method** - this is significant for OGNL access patterns
- Part of UIBean base class hierarchy: `Object → Component → UIBean`

### Component 2: OGNL SecurityMemberAccess Blocking Mechanism

**File:** `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java`

SecurityMemberAccess enforces strict access control for OGNL expressions:

```java
// Lines 359-361: The critical check
protected boolean checkPublicMemberAccess(Member member) {
    return Modifier.isPublic(member.getModifiers());
}

// Lines 140-206: Security check flow in isAccessible()
// 1. Proxy object access check
// 2. Proxy member access check
// 3. Public member access check ← BLOCKS protected fields
// 4. Static field access check
// 5. Static method access check
// 6. Default package access check
// 7. Exclusion list check
// 8. Allowlist check
// 9. Property name check
```

**Why Protected Fields Are Blocked:**

The `checkPublicMemberAccess()` method only returns `true` for members with `public` modifier. Any `protected`, `private`, or package-private members fail this check and generate a warning:

```java
// Line 173
if (!checkPublicMemberAccess(member)) {
    LOG.warn("Access to non-public [{}] is blocked!", member);
    return false;
}
```

**Components Package Allowlist (Lines 61-66):**

```java
private static final Set<String> ALLOWLIST_REQUIRED_PACKAGES = Set.of(
    "org.apache.struts2.validator.validators",
    "org.apache.struts2.components",  // ← UIBean is in this package
    "org.apache.struts2.views.jsp"
);
```

This allowlist was added in **WW-5364** (commit `39c3e332d`, November 2023) to permit OGNL access to component classes, but it doesn't bypass the public-only member access restriction.

### Component 3: getText() and Resource Bundle Resolution

**File:** `core/src/main/java/org/apache/struts2/ActionSupport.java`

The `getText()` method delegates to TextProvider:

```java
// Lines 112-113
@Override
public String getText(String aTextName) {
    return getTextProvider().getText(aTextName);
}
```

**Resolution Chain:**
1. `ActionSupport.getText()` → delegates to `TextProvider`
2. `DefaultTextProvider` → searches resource bundles via `LocalizedTextProvider`
3. `LocalizedTextProvider` → cascades through:
   - Action class hierarchy
   - Package-level bundles
   - Global resource bundles
   - Locale-specific variations

**File:** `core/src/main/java/org/apache/struts2/text/DefaultTextProvider.java`

```java
@Override
public String getText(String key) {
    return localizedTextProvider.findDefaultText(key, ActionContext.getContext().getLocale());
}
```

### Component 4: OGNL Expression Evaluation Ambiguity

**File:** `core/src/main/java/org/apache/struts2/ognl/accessor/CompoundRootAccessor.java`

The root cause: OGNL's property resolution happens **before** string concatenation:

```java
// Lines 153-183: getProperty method
@Override
public Object getProperty(Map context, Object target, Object name) throws OgnlException {
    CompoundRoot root = (CompoundRoot) target;
    OgnlContext ognlContext = (OgnlContext) context;

    // Iterate through ValueStack from top to bottom
    for (Object o : root) {
        if (o == null) {
            continue;
        }

        try {
            // Check for getter/setter FIRST
            if ((OgnlRuntime.hasGetProperty(ognlContext, o, name)) ||
                ((o instanceof Map) && ((Map) o).containsKey(name))) {
                return OgnlRuntime.getProperty(ognlContext, o, name);
            }
        } catch (OgnlException e) {
            // ... error handling ...
        } catch (IntrospectionException e) {
            // Continue to next object in stack
        }
    }
    // ... property not found handling ...
}
```

**Why the Confusion Occurs:**

When OGNL evaluates `getText('label.reasonOfTransaction.'+top)`:

1. OGNL parser encounters the token `label` (even within string context)
2. Before evaluating string concatenation, OGNL checks if `label` is a property on the stack
3. UIBean components are often on the ValueStack during tag rendering
4. OGNL finds `UIBean.label` property via introspection (`hasGetProperty`)
5. SecurityMemberAccess attempts to check if accessing `label` field is allowed
6. Since the field is `protected`, the warning is logged
7. Access is denied, OGNL continues evaluation and eventually calls `getText()` correctly

**The evaluation order:**
- Property/field access is checked via `CompoundRootAccessor.getProperty()`
- Method calls are handled via `CompoundRootAccessor.callMethod()`
- String literals and concatenation happen at parse time
- But property resolution is **attempted first** during expression traversal

### Component 5: Select Tag listValue Evaluation

**File:** `core/src/main/java/org/apache/struts2/components/ListUIBean.java`

The select tag's `listValue` attribute is processed in `evaluateExtraParams()`:

```java
// Lines 120-125
if (listValue != null) {
    listValue = stripExpression(listValue);  // Removes %{} wrapper
    addParameter("listValue", listValue);
} else if (value instanceof Map) {
    addParameter("listValue", "value");
} else {
    addParameter("listValue", "top");
}
```

**File:** `core/src/main/resources/template/simple/select.ftl`

FreeMarker template evaluates listValue for each item:

```freemarker
<@s.iterator value="attributes.list">
    <#elseif attributes.listValue??>
        <#if stack.findString(attributes.listValue)??>
          <#assign itemValue = stack.findString(attributes.listValue)/>
        <#else>
          <#assign itemValue = ''/>
        </#if>
    <#else>
        <#assign itemValue = stack.findString('top')/>
    </#if>
    <option value="${itemKeyStr}">${itemValue}</option>
</@s.iterator>
```

**File:** `core/src/main/java/org/apache/struts2/components/IteratorComponent.java`

During iteration, each list item is pushed onto the ValueStack:

```java
// Lines 270-284
if ((iterator != null) && iterator.hasNext()) {
    Object currentValue = iterator.next();
    stack.push(currentValue);  // ← Item pushed to top of stack

    if (currentValue != null) {
        threadAllowlist.allowClassHierarchy(currentValue.getClass());
    }
    // ...
}
```

**The Problem in Context:**

For the problematic expression `%{getText('label.reasonOfTransaction.'+top)}`:

1. Template strips `%{}` → `getText('label.reasonOfTransaction.'+top)`
2. For each item, iterator pushes it onto ValueStack
3. `stack.findString()` calls OGNL to evaluate the expression
4. OGNL's parser encounters `label` and checks stack for property access
5. If UIBean is on the stack, SecurityMemberAccess warning is triggered
6. Eventually `getText()` method is called correctly and returns localized text

## Code References

- `core/src/main/java/org/apache/struts2/components/UIBean.java:484` - Protected label field
- `core/src/main/java/org/apache/struts2/components/UIBean.java:1142-1145` - setLabel() method
- `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java:359-361` - Public member check
- `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java:173` - Warning log
- `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java:61-66` - Components allowlist
- `core/src/main/java/org/apache/struts2/ognl/accessor/CompoundRootAccessor.java:153-183` - Property resolution
- `core/src/main/java/org/apache/struts2/components/ListUIBean.java:120-125` - listValue processing
- `core/src/main/resources/template/simple/select.ftl:57-81` - Template evaluation
- `core/src/main/java/org/apache/struts2/ActionSupport.java:112-113` - getText() delegation

## Architecture Insights

### Design Pattern: ValueStack and CompoundRoot

Struts uses a **CompoundRoot** pattern where objects are stacked and searched top-to-bottom for property/method resolution. This allows flexible context access but creates ambiguity when property names conflict with string literals in expressions.

### OGNL Evaluation Order

1. **Parse expression** - tokenize and build AST
2. **Resolve properties** - check stack for matching properties (triggers introspection)
3. **Invoke methods** - call methods with evaluated parameters
4. **Apply operators** - string concatenation, arithmetic, etc.

The issue arises because step 2 (property resolution) happens during AST traversal, even for tokens that are intended as string literals.

### Security vs. Usability Trade-off

SecurityMemberAccess enforces strict public-only access to prevent OGNL injection attacks, but this creates false-positive warnings when:
- Property names match common words ("label", "name", "value", "text")
- Expressions use string literals that match property names
- Components on ValueStack have protected fields

## Historical Context

### Related Issue: WW-5364 (Components Allowlist)

**Commit:** `39c3e332d7a0ea4fa51bb6e62f5ac170b0cc5072`
**Date:** November 24, 2023
**Author:** Kusal Kithul-Godage
**PR:** [#800](https://github.com/apache/struts/pull/800)
**Title:** "WW-5364 Automatically populate OGNL allowlist"

This commit added `org.apache.struts2.components` to `ALLOWLIST_REQUIRED_PACKAGES`, which allows OGNL to access component classes but doesn't bypass the public-only member restriction.

### PR #1059 - Not Found

The Jira ticket mentions PR #1059 with title "[WW-5368] Fixes checking nonce of invalidated session", but:
- No PR #1059 exists in the git repository
- No commits reference WW-5368
- The PR title doesn't match the label field access issue
- Likely a documentation error or refers to a different tracking system

### Similar Protected Fields in Components

Multiple components have similar patterns that could cause OGNL warnings:

**Bean.java** - `protected String name;` (line 98)
**Param.java** - `protected String name;` (line 114), `protected String value;`
**Text.java** - `protected String name;` (line 130)
**I18n.java** - `protected String name;` (line 88)

**Contrast: Date.java uses `private`:**
```java
private String name;  // Line 204 - Avoids OGNL accessibility
public String getName() { return name; }  // Line 417 - Provides getter
```

This suggests the Date component was designed to avoid OGNL field access conflicts by using `private` visibility.

## Potential Solutions

### Option 1: Change Field Visibility to Private

**Change in UIBean.java:**
```java
// From:
protected String label;

// To:
private String label;

// Add public getter:
public String getLabel() {
    return label;
}
```

**Pros:**
- Eliminates OGNL field access warnings
- Better encapsulation
- Follows JavaBean conventions

**Cons:**
- Breaks subclass direct field access (if any subclasses rely on it)
- Requires thorough testing of all UIBean subclasses

### Option 2: OGNL Expression Escaping/Quoting

Users could modify expressions to avoid ambiguity:

```jsp
<!-- Problematic: -->
<s:select listValue="%{getText('label.reasonOfTransaction.'+top)}" />

<!-- Alternative 1: Use different concatenation -->
<s:select listValue="%{getText('label' + '.reasonOfTransaction.' + top)}" />

<!-- Alternative 2: Use string formatting -->
<s:select listValue="%{getText(#sprintf('label.reasonOfTransaction.%s', top))}" />
```

**Pros:**
- No code changes required
- Immediate workaround

**Cons:**
- Requires user education
- Less readable
- Doesn't solve root cause

### Option 3: OGNL Parser Enhancement

Modify OGNL to better distinguish string literals from property access in compound expressions.

**Pros:**
- Solves root cause
- Benefits all users

**Cons:**
- Requires changes to OGNL library (external dependency)
- Complex implementation
- Potential compatibility issues

### Option 4: SecurityMemberAccess Exception for Component Fields

Add special handling in SecurityMemberAccess to suppress warnings for component package protected fields when accessed during expression evaluation (not direct assignment):

```java
protected boolean checkPublicMemberAccess(Member member) {
    if (!Modifier.isPublic(member.getModifiers())) {
        // Suppress warning for component fields during read access
        if (member instanceof Field &&
            member.getDeclaringClass().getPackage().getName().equals("org.apache.struts2.components") &&
            isReadContext()) {  // Would need to track context
            return true;  // Allow without warning
        }
        return false;
    }
    return true;
}
```

**Pros:**
- Suppresses false-positive warnings
- Maintains security for actual violations

**Cons:**
- Adds complexity to security checks
- Requires tracking read vs. write context
- May hide legitimate security issues

## Recommendation

**Recommended Solution: Option 1 (Change to Private Fields)**

The cleanest solution is to change UIBean's `label` field (and similar fields in other components) from `protected` to `private` and add public getter methods. This:

1. Follows JavaBean conventions
2. Eliminates OGNL warnings at the source
3. Improves encapsulation
4. Matches the pattern already used in Date component

**Implementation Steps:**

1. Audit all UIBean fields and similar components for `protected` fields
2. Change to `private` and add public getters where missing
3. Review all subclasses for direct field access
4. Add comprehensive tests for tag rendering with resource bundle keys
5. Document the change in migration guide

## Open Questions

1. **Why no getter for label field?** - UIBean has `setLabel()` but no `getLabel()`. Is this intentional or an oversight?

2. **Subclass field access** - Do any UIBean subclasses directly access the `label` field? Changing to `private` would break such access.

3. **OGNL version compatibility** - Would upgrading OGNL library version provide better expression parsing?

4. **Performance impact** - How many warnings are being generated in production applications? Is this causing log spam or performance issues?

5. **Other affected fields** - Should we audit all component fields systematically for similar issues?

## Related Research

- `thoughts/lukaszlenart/notes/2025-10-17-struts2-iterator-validation-visitor-pattern.md` - Validation patterns and OGNL security considerations

## Testing Recommendations

To verify the fix, create tests that:

1. Use `getText()` with keys starting with common field names: "label", "name", "value", "id"
2. Verify no SecurityMemberAccess warnings are logged
3. Test select tag with `listValue="%{getText('label.key.'+top)}"`
4. Verify correct i18n text is rendered
5. Test all UIBean subclasses for field access patterns
6. Performance test with high-frequency tag rendering

## External References

- **Jira Ticket**: https://issues.apache.org/jira/browse/WW-5368
- **Related Commit (WW-5364)**: `39c3e332d7a0ea4fa51bb6e62f5ac170b0cc5072`
- **Related PR**: [#800](https://github.com/apache/struts/pull/800)