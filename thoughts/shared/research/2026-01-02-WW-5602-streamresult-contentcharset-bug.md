---
date: 2026-01-02T15:55:38+01:00
last_updated: 2026-01-02T16:15:00+01:00
topic: "WW-5602: StreamResult contentCharSet Handling Issues"
tags: [research, codebase, streamresult, charset, bug, WW-5602]
status: complete
jira_ticket: WW-5602
---

# Research: WW-5602 - StreamResult contentCharSet Handling Issues

**Date**: 2026-01-02 15:55:38 +0100
**JIRA**: [WW-5602](https://issues.apache.org/jira/browse/WW-5602)

## Research Question

Two issues related to StreamResult's contentCharSet handling:

1. Why does UTF-8 appear in content-type when contentCharSet is not specified?
2. When contentCharSet is an expression that evaluates to null, shouldn't the charset be omitted instead of appending
   `;charset=`?

## Summary

**Question 1 (UTF-8 appearing automatically):**
The UTF-8 encoding is set by `Dispatcher.prepare()` which calls `response.setCharacterEncoding(defaultEncoding)` before
the action executes. The `defaultEncoding` comes from `struts.i18n.encoding` property (defaults to UTF-8). When
StreamResult later sets the content type, the servlet container may include this pre-set charset.

**Question 2 (Expression evaluating to null):**
This IS a bug. The code checks the raw `contentCharSet` string before parsing, not the evaluated result. When an
expression like `${myMethod}` evaluates to null, `conditionalParse()` returns an empty string, resulting in `;charset=`
being appended with no value.

## Detailed Findings

### Source of Default UTF-8

The UTF-8 encoding comes from `Dispatcher.prepare()`:

```java
// Dispatcher.java:937-952
public void prepare(HttpServletRequest request, HttpServletResponse response) {
    String encoding = null;
    if (defaultEncoding != null) {
        encoding = defaultEncoding;  // From struts.i18n.encoding, defaults to UTF-8
    }
    // ...
    if (encoding != null) {
        applyEncoding(request, encoding);
        applyEncoding(response, encoding);  // <-- Sets UTF-8 on response
    }
}
```

The `applyEncoding()` method calls `response.setCharacterEncoding(encoding)`:

```java
// Dispatcher.java:976-984
private void applyEncoding(HttpServletResponse response, String encoding) {
    try {
        if (!encoding.equals(response.getCharacterEncoding())) {
            response.setCharacterEncoding(encoding);
        }
    } catch (Exception e) {
        // ...
    }
}
```

The default value is set in `default.properties`:

```properties
struts.i18n.encoding=UTF-8
```

### The contentCharSet Bug

The bug is in `StreamResult.doExecute()`:

```java
// StreamResult.java:237-241
if (contentCharSet != null && !contentCharSet.isEmpty()) {
    oResponse.setContentType(conditionalParse(contentType, invocation) + ";charset=" + conditionalParse(contentCharSet, invocation));
} else {
    oResponse.setContentType(conditionalParse(contentType, invocation));
}
```

The problem: The check evaluates the **raw string** (`${myMethod}`) rather than the **parsed result**.

When `contentCharSet = "${myMethod}"` and `myMethod` returns `null`:

1. Check passes: `"${myMethod}"` is not null or empty
2. `conditionalParse()` evaluates the expression
3. `OgnlTextParser.evaluate()` handles null by returning empty string (lines 85-88)
4. Result: `;charset=` appended with empty value

### OgnlTextParser Null Handling

```java
// OgnlTextParser.java:85-89
} else {
    // the variable doesn't exist, so don't display anything
    expression = left.concat(right);
    result = expression;
}
```

When an expression `${foo}` evaluates to null, it returns the concatenation of left+right (empty strings in this case).

## Code References

- `core/src/main/java/org/apache/struts2/result/StreamResult.java:237-241` - The buggy charset check
- `core/src/main/java/org/apache/struts2/dispatcher/Dispatcher.java:937-984` - Source of default UTF-8
- `core/src/main/java/org/apache/struts2/util/OgnlTextParser.java:85-89` - Null expression handling
- `core/src/main/resources/org/apache/struts2/default.properties:27` - Default UTF-8 setting
- `core/src/main/java/org/apache/struts2/result/StrutsResultSupport.java:216-225` - conditionalParse implementation

## Proposed Fix

The fix should address both issues:

1. Evaluate the expression BEFORE checking for emptiness
2. Call `response.setCharacterEncoding(null)` to clear Dispatcher's default UTF-8

```java
// Proposed fix for StreamResult.java
String parsedContentCharSet = contentCharSet != null ? conditionalParse(contentCharSet, invocation) : null;
if (parsedContentCharSet != null && !parsedContentCharSet.isEmpty()) {
    oResponse.setContentType(conditionalParse(contentType, invocation) + ";charset=" + parsedContentCharSet);
} else {
    oResponse.setContentType(conditionalParse(contentType, invocation));
    oResponse.setCharacterEncoding(null);  // Clear Dispatcher's default encoding
}
```

This ensures:
- If the expression evaluates to null or empty, the charset is omitted entirely
- The `setCharacterEncoding(null)` call resets the response encoding that was set by `Dispatcher.prepare()`

## How Other Result Types Handle Encoding

Research into other Struts Result types revealed two patterns:

### Pattern 1: Content-Type Header Only (PlainTextResult, StreamResult)
- Add charset via Content-Type header: `text/plain; charset=UTF-8`
- Does NOT override response encoding set by Dispatcher
- Example: `response.setContentType("text/plain; charset=" + charSet)`

### Pattern 2: Direct Response Encoding (XSLTResult, JSONValidationInterceptor)
- Calls `response.setCharacterEncoding(encoding)` directly
- Explicitly overrides what Dispatcher set
- More forceful approach

StreamResult currently uses Pattern 1, but the proposed fix adds Pattern 2's approach to clear the encoding when no charset is specified.

## Servlet API: setCharacterEncoding(null)

According to Servlet API specification:
- Calling `response.setCharacterEncoding(null)` resets encoding to the servlet container default
- This is a valid way to "clear" the Dispatcher's default encoding
- Not commonly used in Struts codebase, but follows the API specification

## Potential Breaking Changes

This fix could affect applications that:
- Rely on Dispatcher's default UTF-8 encoding for StreamResult responses
- Serve text-based streams without explicitly setting contentCharSet

**Mitigation**: Users who want UTF-8 can explicitly set `contentCharSet="UTF-8"` in their result configuration:
```xml
<result name="success" type="stream">
    <param name="contentType">text/plain</param>
    <param name="contentCharSet">UTF-8</param>
    <param name="inputName">myStream</param>
</result>
```

## Alternative Approaches Considered

### Option A: Minimal Fix (Bug Only)
Only fix the expression evaluation bug, leave UTF-8 behavior unchanged.
- Pros: Minimal change
- Cons: Doesn't address the unexpected UTF-8 appearing

### Option B: Auto-reset Encoding (Recommended)
Fix bug + call `setCharacterEncoding(null)` when no charset specified.
- Pros: Addresses both issues, clean content-type for binary streams
- Cons: Could break apps relying on default UTF-8

### Option C: Add Parameter
Add a new `resetEncoding` parameter for explicit control.
- Pros: Fully backward compatible, flexible
- Cons: More complex, adds configuration option

## Test Coverage

Existing tests in `StreamResultTest.java`:

- `testStreamResultDefault()` - Tests default behavior (no charset)
- `testStreamResultWithCharSet()` - Tests explicit charset "ISO-8859-1"
- `testStreamResultWithCharSet2()` - Tests expression-based charset returning UTF-8

**Missing test case:** Expression that evaluates to null (the bug scenario)

## Open Questions

1. Should there be a way to explicitly prevent charset from being added even when `struts.i18n.encoding` is set?
2. Should the framework provide a more explicit "no charset" option for binary streams?
