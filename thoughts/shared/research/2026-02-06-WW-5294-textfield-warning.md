---
date: 2026-02-06T10:00:00-08:00
topic: "WW-5294: TextField tag not showing warning when JSP exposed directly"
tags: [research, security, components, templates, direct-jsp-access]
status: complete
jira: WW-5294
---

# Research: WW-5294 - TextField Warning for Direct JSP Access

**Date**: 2026-02-06

## Research Question

Investigate why `<s:textfield/>` tag does not show the security warning when JSP pages are accessed directly (bypassing Struts action flow), while tags like `<s:url>` and `<s:a>` do show warnings.

## Summary

The warning mechanism for "direct JSP access" is **inconsistently implemented** across Struts components. The warning exists in some places but not others, and the detection conditions vary. The bug is that `<s:textfield>` (and potentially other UIBean components) should trigger the same warning as other tags but doesn't in certain scenarios.

## Detailed Findings

### Warning Mechanism Locations

There are **two locations** where warnings about direct JSP access are implemented:

#### 1. TagUtils.getStack() - Exception when ValueStack is null

**File**: `core/src/main/java/org/apache/struts2/views/jsp/TagUtils.java:44-47`

```java
if (stack == null) {
    LOG.warn("No ValueStack in ActionContext!");
    throw new ConfigurationException("Rendering tag out of Action scope, accessing directly JSPs is not recommended! " +
        "Please read https://struts.apache.org/security/#never-expose-jsp-files-directly");
}
```

- **Behavior**: Logs warning AND throws exception (stops rendering)
- **Trigger**: ValueStack is null
- **Affects**: ALL tags that use `getStack()`

#### 2. FreemarkerTemplateEngine.renderTemplate() - Warning when action is null

**File**: `core/src/main/java/org/apache/struts2/components/template/FreemarkerTemplateEngine.java:119-125`

```java
ActionInvocation ai = ActionContext.getContext().getActionInvocation();
Object action = (ai == null) ? null : ai.getAction();
if (action == null) {
    LOG.warn("Rendering tag {} out of Action scope, accessing directly JSPs is not recommended! " +
            "Please read https://struts.apache.org/security/#never-expose-jsp-files-directly", templateName);
}
```

- **Behavior**: Logs warning only (rendering continues)
- **Trigger**: ActionInvocation is null OR action is null
- **Affects**: Only UIBean components using FreeMarker templates

### Missing Warning Locations

#### 1. JspTemplateEngine - NO warning check

**File**: `core/src/main/java/org/apache/struts2/components/template/JspTemplateEngine.java`

The JSP template engine does NOT have any warning for direct JSP access. If someone uses JSP templates instead of FreeMarker, they get no warning.

#### 2. ServletUrlRenderer - NO warning check

**File**: `core/src/main/java/org/apache/struts2/components/ServletUrlRenderer.java`

The URL renderer used by `<s:url>` tag does NOT have any warning mechanism. It uses `ActionContext.getContext().getActionInvocation()` but only for URL generation logic, not for warning.

### Component Class Hierarchy

```
Component (base)
├── ContextBean
│   └── URL (extends ContextBean) → Uses ServletUrlRenderer, NO template
└── UIBean (extends Component)
    ├── TextField → Uses FreeMarker template "text.ftl"
    ├── ClosingUIBean
    │   └── Anchor → Uses FreeMarker template "a.ftl"
    └── (other UI components)
```

### Tag Processing Flow

1. **JSP tag doStartTag()** calls `ComponentTagSupport.doStartTag()`
2. `getStack()` is called → delegates to `TagUtils.getStack()`
3. If stack is null → **ConfigurationException thrown** (all tags fail)
4. Component is created and rendered

For UIBean components (TextField, Anchor, etc.):
5. `UIBean.end()` calls `mergeTemplate()`
6. `TemplateEngineManager.getTemplateEngine()` returns appropriate engine
7. `FreemarkerTemplateEngine.renderTemplate()` logs warning if action is null

For URL component:
5. `URL.end()` calls `urlRenderer.renderUrl()`
6. No warning check exists in this path

### Root Cause Analysis

**Scenario 1: No ValueStack at all**
- First tag to render triggers `ConfigurationException` in TagUtils
- ALL tags fail with visible error
- This is NOT the bug scenario

**Scenario 2: ValueStack exists but no ActionInvocation**
This is the likely bug scenario:
- TagUtils check passes (stack is not null)
- `<s:url>` renders via ServletUrlRenderer → **NO warning**
- `<s:a>` renders via FreemarkerTemplateEngine → **Logs warning**
- `<s:textfield>` renders via FreemarkerTemplateEngine → **Should log warning**

The issue is that:
1. The FreemarkerTemplateEngine warning only goes to the **log**, not the user
2. The warning condition `action == null` might not be triggered if there's a "stub" ActionInvocation
3. The warning is inconsistent between template engines (FreeMarker has it, JSP doesn't)

### Key Inconsistencies Found

| Component | Template Engine | Warning Location | Warning Type |
|-----------|----------------|------------------|--------------|
| `<s:url>` | None (direct render) | None | **No warning** |
| `<s:a>` | FreeMarker | FreemarkerTemplateEngine | Log warning |
| `<s:textfield>` | FreeMarker | FreemarkerTemplateEngine | Log warning |
| Any UIBean with JSP template | JSP | None | **No warning** |

## Code References

- `core/src/main/java/org/apache/struts2/views/jsp/TagUtils.java:38-56` - getStack() with ConfigurationException
- `core/src/main/java/org/apache/struts2/components/template/FreemarkerTemplateEngine.java:119-125` - Warning for FreeMarker
- `core/src/main/java/org/apache/struts2/components/template/JspTemplateEngine.java:48-83` - renderTemplate() without warning
- `core/src/main/java/org/apache/struts2/components/ServletUrlRenderer.java:75-134` - renderUrl() without warning
- `core/src/main/java/org/apache/struts2/components/UIBean.java:565-578` - end() calls mergeTemplate()
- `core/src/main/java/org/apache/struts2/components/URL.java:141-144` - end() uses urlRenderer

## Recommendations

### Option 1: Centralize Warning in TagUtils (Recommended)

Add the ActionInvocation check to `TagUtils.getStack()` so ALL tags get the warning consistently:

```java
public static ValueStack getStack(PageContext pageContext) {
    // ... existing stack check ...

    // Add warning for missing ActionInvocation
    ActionInvocation ai = ActionContext.getContext().getActionInvocation();
    if (ai == null || ai.getAction() == null) {
        LOG.warn("Rendering tag out of Action scope, accessing directly JSPs is not recommended! " +
                "Please read https://struts.apache.org/security/#never-expose-jsp-files-directly");
    }

    return stack;
}
```

### Option 2: Add Warning to JspTemplateEngine

Add the same warning check that exists in FreemarkerTemplateEngine to JspTemplateEngine for consistency.

### Option 3: Add Warning to Component.start()

Add the warning check to the base `Component.start()` method so ALL components (not just template-based ones) get the warning.

## Security Implications

This is a **security issue** because:
1. Directly accessing JSPs bypasses Struts interceptors (validation, security, etc.)
2. Form input tags like `<s:textfield>` pose potentially greater risks than link tags
3. The inconsistent warning behavior may give developers false confidence

## Related Issues

- The JIRA ticket was reopened because the fix was incomplete
- Title updated to clarify: "not showing the warning" for `<s:textfield>`
- Fix version: 7.2.0

## Open Questions

1. Should the warning also be added to `Component.start()` for non-template components?
2. Should the warning be elevated to a thrown exception (like TagUtils does for null stack)?
3. Are there other components/paths that also need the warning?