---
date: 2026-02-06T12:00:00+01:00
topic: "WW-3429 - Configurable Checkbox Hidden Field Prefix"
tags: [research, codebase, checkbox, interceptor, freemarker, constants, backward-compatibility]
status: complete
jira_ticket: WW-3429
---

# Research: WW-3429 - Configurable Checkbox Hidden Field Prefix

**Date**: 2026-02-06

## Research Question

How to add a Struts constant to steer backward compatibility with the prefix used in checkbox.ftl template and CheckboxInterceptor.java, addressing the HTML validation issue with `__checkbox_` prefix.

## Summary

The JIRA ticket WW-3429 reports that the `__checkbox_` prefix violates HTML standards (double underscores in attribute names). The solution requires:

1. Adding a new constant `STRUTS_UI_CHECKBOX_HIDDEN_PREFIX` to `StrutsConstants.java`
2. Setting default value in `default.properties` (use `__checkbox_` for backward compatibility)
3. Injecting the constant into `Checkbox.java` component and `CheckboxInterceptor.java`
4. Passing the prefix to FreeMarker templates via component parameters
5. Updating all checkbox templates to use the configurable prefix

## Detailed Findings

### Current Hardcoded Prefix Locations

The prefix `__checkbox_` is hardcoded in multiple files:

| File | Line | Usage |
|------|------|-------|
| `core/src/main/resources/template/simple/checkbox.ftl` | 43 | Hidden field generation |
| `core/src/main/resources/template/html5/checkbox.ftl` | 44 | Hidden field generation |
| `core/src/main/resources/template/css_xhtml/checkbox.ftl` | - | Hidden field generation |
| `core/src/main/resources/template/xhtml/checkbox.ftl` | - | Hidden field generation |
| `core/src/main/java/org/apache/struts2/interceptor/CheckboxInterceptor.java` | 71-72 | Prefix matching |
| `plugins/javatemplates/.../CheckboxHandler.java` | - | Java template rendering |
| `core/src/test/java/.../CheckboxInterceptorTest.java` | - | Test assertions |

### Pattern for Adding Configuration Constant

Based on existing patterns (e.g., `STRUTS_UI_CHECKBOX_SUBMIT_UNCHECKED`):

#### Step 1: Add to StrutsConstants.java

```java
// core/src/main/java/org/apache/struts2/StrutsConstants.java
/**
 * The prefix used for hidden checkbox fields to track unchecked values.
 * Default is "__checkbox_" for backward compatibility.
 * Set to "struts_checkbox_" to avoid HTML validation warnings about double underscores.
 * @since 7.2.0
 */
public static final String STRUTS_UI_CHECKBOX_HIDDEN_PREFIX = "struts.ui.checkbox.hiddenPrefix";
```

#### Step 2: Add default value in default.properties

```properties
# core/src/main/resources/org/apache/struts2/default.properties
### Checkbox hidden field prefix (WW-3429)
# Default prefix for backward compatibility. Change to "struts_checkbox_" for HTML5 validation.
struts.ui.checkbox.hiddenPrefix = __checkbox_
```

#### Step 3: Inject into Checkbox.java component

```java
// core/src/main/java/org/apache/struts2/components/Checkbox.java
public static final String ATTR_HIDDEN_PREFIX = "hiddenPrefix";
private String hiddenPrefixGlobal = "__checkbox_";

@Inject(value = StrutsConstants.STRUTS_UI_CHECKBOX_HIDDEN_PREFIX, required = false)
public void setHiddenPrefixGlobal(String hiddenPrefixGlobal) {
    this.hiddenPrefixGlobal = hiddenPrefixGlobal;
}

@Override
protected void evaluateExtraParams() {
    super.evaluateExtraParams();
    // ... existing code ...
    addParameter(ATTR_HIDDEN_PREFIX, hiddenPrefixGlobal);
}
```

#### Step 4: Inject into CheckboxInterceptor.java

```java
// core/src/main/java/org/apache/struts2/interceptor/CheckboxInterceptor.java
private String hiddenPrefix = "__checkbox_";

@Inject(value = StrutsConstants.STRUTS_UI_CHECKBOX_HIDDEN_PREFIX, required = false)
public void setHiddenPrefix(String hiddenPrefix) {
    this.hiddenPrefix = hiddenPrefix;
}

@Override
public String intercept(ActionInvocation ai) throws Exception {
    // Replace hardcoded "__checkbox_" with this.hiddenPrefix
    if (name.startsWith(hiddenPrefix)) {
        String checkboxName = name.substring(hiddenPrefix.length());
        // ...
    }
}
```

#### Step 5: Update checkbox.ftl templates

```freemarker
<#-- core/src/main/resources/template/simple/checkbox.ftl -->
<#if attributes.submitUnchecked!false>
<input type="hidden" id="${attributes.hiddenPrefix}${attributes.id}" name="${attributes.hiddenPrefix}${attributes.name}" value="${attributes.fieldValue}"<#rt/>
<#if attributes.disabled!false>
 disabled="disabled"<#rt/>
</#if>
 /><#rt/>
</#if>
```

### Existing Example: STRUTS_UI_CHECKBOX_SUBMIT_UNCHECKED

This constant shows the pattern already in use:

**StrutsConstants.java** (line 702):
```java
public static final String STRUTS_UI_CHECKBOX_SUBMIT_UNCHECKED = "struts.ui.checkbox.submitUnchecked";
```

**Checkbox.java** - Injection:
```java
@Inject(value = StrutsConstants.STRUTS_UI_CHECKBOX_SUBMIT_UNCHECKED, required = false)
public void setSubmitUncheckedGlobal(String submitUncheckedGlobal) {
    this.submitUncheckedGlobal = submitUncheckedGlobal;
}
```

**Checkbox.java** - Usage in evaluateExtraParams():
```java
if (submitUnchecked != null) {
    Object parsedValue = findValue(submitUnchecked, Boolean.class);
    addParameter(ATTR_SUBMIT_UNCHECKED, parsedValue == null ? Boolean.valueOf(submitUnchecked) : parsedValue);
} else if (submitUncheckedGlobal != null) {
    addParameter(ATTR_SUBMIT_UNCHECKED, Boolean.parseBoolean(submitUncheckedGlobal));
} else {
    addParameter(ATTR_SUBMIT_UNCHECKED, false);
}
```

## Code References

- `core/src/main/java/org/apache/struts2/StrutsConstants.java` - Constant definitions
- `core/src/main/resources/org/apache/struts2/default.properties` - Default values
- `core/src/main/java/org/apache/struts2/components/Checkbox.java` - UI component
- `core/src/main/java/org/apache/struts2/interceptor/CheckboxInterceptor.java:71-72` - Prefix matching
- `core/src/main/resources/template/simple/checkbox.ftl:43` - Template hidden field

## Architecture Insights

1. **Constant injection pattern**: Use `@Inject(value = CONSTANT, required = false)` with setter method
2. **Template access**: Components pass values via `addParameter()` method, templates access via `${attributes.paramName}`
3. **Backward compatibility**: Default value should preserve existing behavior (`__checkbox_`)
4. **Multiple templates**: All 4 theme templates (simple, html5, css_xhtml, xhtml) need updating

## Files to Modify

1. **StrutsConstants.java** - Add new constant
2. **default.properties** - Add default value
3. **Checkbox.java** - Inject constant, add parameter
4. **CheckboxInterceptor.java** - Inject constant, use in prefix matching
5. **checkbox.ftl** (simple) - Use `${attributes.hiddenPrefix}`
6. **checkbox.ftl** (html5) - Use `${attributes.hiddenPrefix}`
7. **checkbox.ftl** (css_xhtml) - Use `${attributes.hiddenPrefix}`
8. **checkbox.ftl** (xhtml) - Use `${attributes.hiddenPrefix}`
9. **CheckboxHandler.java** (javatemplates plugin) - Update if applicable
10. **CheckboxInterceptorTest.java** - Add tests for configurable prefix

## Suggested Configuration Values

| Value | Description |
|-------|-------------|
| `__checkbox_` | Default, backward compatible (current behavior) |
| `struts_checkbox_` | HTML5 compliant (recommended for new projects) |
| `sc_` | Minimal prefix (short form) |

## Open Questions

1. Should there be a per-tag `hiddenPrefix` attribute in addition to the global constant?
2. Should the interceptor support multiple prefixes simultaneously during migration?
3. Need to verify the javatemplates plugin `CheckboxHandler.java` implementation

## Related JIRA Issues

- [WW-3429](https://issues.apache.org/jira/browse/WW-3429) - Original issue about HTML validation warnings