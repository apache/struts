---
date: 2025-01-27T00:00:00Z
topic: "Security Enhancements Implementation Plan for Compress Component"
tags: [implementation, security, compress, dos-prevention, logging-security, redos-mitigation]
status: planning
component: core/src/main/java/org/apache/struts2/components/Compress.java
related_issues: [security-review-compress-component]
---

# Implementation Plan: Security Enhancements for Compress Component

**Date**: 2025-01-27

## Objective

Implement security enhancements to address three identified security issues in the `Compress` component:
1. **Logging Sensitive Data** - Truncate body content in log messages
2. **DoS via Large Input** - Add configurable size limit
3. **ReDoS Mitigation** - Add performance safeguards for regex operations

## Security Issues Summary

### Issue 1: Logging Sensitive Data (Medium Risk)
- **Location**: `Compress.java:101, 105, 108, 111`
- **Problem**: Full body content logged at debug/trace level
- **Risk**: Sensitive data (passwords, tokens, PII) may be exposed in logs
- **Solution**: Implement truncation with length indicator

### Issue 2: DoS via Large Input (Medium-High Risk)
- **Location**: `Compress.java:95-112` (no size check)
- **Problem**: No limit on body size, potential memory exhaustion
- **Risk**: OutOfMemoryError, CPU exhaustion, service disruption
- **Solution**: Add configurable maximum size check

### Issue 3: ReDoS Potential (Low-Medium Risk)
- **Location**: `Compress.java:141-161` (regex operations)
- **Problem**: Regex patterns could be slow on malicious input
- **Risk**: CPU exhaustion from regex backtracking
- **Solution**: Add hard limit and optimize regex patterns

## Implementation Phases

### Phase 1: Configuration Constants

**File**: `core/src/main/java/org/apache/struts2/StrutsConstants.java`
**Location**: After line 344 (after `STRUTS_COMPRESS_ENABLED`)

**Changes**:
```java
/**
 * Maximum size (in bytes) of body content that can be compressed.
 * Content exceeding this limit will be skipped without compression.
 * Set to 0 or negative to disable size checking (not recommended).
 * Default: 10MB (10485760 bytes)
 *
 * @since 7.2.0
 */
public static final String STRUTS_COMPRESS_MAX_SIZE = "struts.compress.maxSize";

/**
 * Maximum length of body content to include in log messages.
 * Content longer than this will be truncated with length indicator.
 * Default: 200 characters
 *
 * @since 7.2.0
 */
public static final String STRUTS_COMPRESS_LOG_MAX_LENGTH = "struts.compress.log.maxLength";
```

**Validation**:
- [ ] Constants added with proper JavaDoc
- [ ] Follows existing naming conventions
- [ ] Proper `@since` annotation

---

### Phase 2: Default Properties

**File**: `core/src/main/resources/org/apache/struts2/default.properties`
**Location**: After line 215 (after `struts.compress.enabled=true`)

**Changes**:
```properties
### Maximum size (in bytes) of body content that can be compressed.
### Content exceeding this limit will be skipped without compression.
### Default: 10MB (10485760 bytes)
struts.compress.maxSize=10485760

### Maximum length of body content to include in log messages.
### Content longer than this will be truncated with length indicator.
### Default: 200 characters
struts.compress.log.maxLength=200
```

**Validation**:
- [ ] Properties added with descriptive comments
- [ ] Default values are reasonable (10MB, 200 chars)
- [ ] Follows existing property file format

---

### Phase 3: Compress Component Updates

**File**: `core/src/main/java/org/apache/struts2/components/Compress.java`

#### 3.1: Add New Fields

**Location**: After line 83 (after `private boolean compressionEnabled = true;`)

**Changes**:
```java
private Long maxSize = null;
private int logMaxLength = 200;
```

#### 3.2: Add Injection Methods

**Location**: After `setCompressionEnabled` method (after line 92)

**Changes**:
```java
@Inject(value = StrutsConstants.STRUTS_COMPRESS_MAX_SIZE, required = false)
public void setMaxSize(String maxSize) {
    if (maxSize != null && !maxSize.trim().isEmpty()) {
        try {
            this.maxSize = Long.parseLong(maxSize.trim());
            if (this.maxSize < 0) {
                LOG.warn("Invalid maxSize value: {}, must be >= 0. Disabling size limit.", maxSize);
                this.maxSize = null;
            }
        } catch (NumberFormatException e) {
            LOG.warn("Invalid maxSize value: {}, must be a valid number. Disabling size limit.", maxSize, e);
            this.maxSize = null;
        }
    }
}

@Inject(value = StrutsConstants.STRUTS_COMPRESS_LOG_MAX_LENGTH, required = false)
public void setLogMaxLength(String logMaxLength) {
    if (logMaxLength != null && !logMaxLength.trim().isEmpty()) {
        try {
            int length = Integer.parseInt(logMaxLength.trim());
            if (length < 0) {
                LOG.warn("Invalid logMaxLength value: {}, must be >= 0. Using default: 200.", logMaxLength);
                this.logMaxLength = 200;
            } else {
                this.logMaxLength = length;
            }
        } catch (NumberFormatException e) {
            LOG.warn("Invalid logMaxLength value: {}, must be a valid number. Using default: 200.", logMaxLength, e);
            this.logMaxLength = 200;
        }
    }
}
```

#### 3.3: Add Helper Methods

**Location**: After `setSingleLine` method (after line 128)

**Changes**:
```java
/**
 * Truncates content for safe logging to prevent sensitive data exposure
 * and excessive log file growth.
 *
 * @param content the content to truncate
 * @return truncated content with length indicator if truncated, original content otherwise
 */
private String truncateForLogging(String content) {
    if (content == null) {
        return null;
    }
    if (content.length() <= logMaxLength) {
        return content;
    }
    return content.substring(0, logMaxLength) + "... (truncated, length: " + content.length() + ")";
}

/**
 * Checks if the body content exceeds the maximum allowed size.
 *
 * @param body the body content to check
 * @return true if body exceeds maximum size, false otherwise
 */
private boolean exceedsMaxSize(String body) {
    if (maxSize == null || body == null) {
        return false;
    }
    return body.length() > maxSize;
}
```

#### 3.4: Update end() Method

**Location**: Replace lines 95-112

**Changes**:
```java
@Override
public boolean end(Writer writer, String body) {
    // Check size limit before processing
    if (exceedsMaxSize(body) && compressionEnabled) {
        LOG.warn("Body size: {} exceeds maximum allowed size: {}, skipping compression", 
                 body.length(), maxSize);
        return super.end(writer, body, true);
    }

    Object forceValue = findValue(force, Boolean.class);
    Object singleLineValue = findValue(singleLine, Boolean.class);

    boolean forced = forceValue != null && Boolean.parseBoolean(forceValue.toString());
    if (!compressionEnabled && !forced) {
        LOG.debug("Compression disabled globally, skipping: {}", truncateForLogging(body));
        return super.end(writer, body, true);
    }
    if (devMode && !forced) {
        LOG.debug("Avoids compressing output: {} in DevMode", truncateForLogging(body));
        return super.end(writer, body, true);
    }
    LOG.trace("Compresses: {}", truncateForLogging(body));
    boolean useSingleLine = singleLineValue instanceof Boolean single && single;
    String compressedBody = compressWhitespace(body, useSingleLine);
    LOG.trace("Compressed: {}", truncateForLogging(compressedBody));
    return super.end(writer, compressedBody, true);
}
```

#### 3.5: Update compressWhitespace() Method

**Location**: Replace lines 130-161

**Changes**:
```java
/**
 * Compresses whitespace in the input string.
 *
 * <p>This method normalizes line breaks (CR, LF, CRLF) to LF and collapses
 * consecutive whitespace characters according to the specified mode.</p>
 *
 * <p>Security note: This method includes safeguards against ReDoS attacks
 * by using simple, bounded regex patterns and early exit for very large inputs.</p>
 *
 * @param input      the input string to compress
 * @param singleLine if true, removes all line breaks and collapses to single spaces;
 *                   if false, preserves line structure with single line breaks
 * @return the compressed string with normalized whitespace
 */
private String compressWhitespace(String input, boolean singleLine) {
    if (input == null || input.isEmpty()) {
        return input;
    }

    // Early exit for very large inputs to prevent ReDoS and excessive processing
    // This is a secondary check; primary size check happens in end() method
    if (input.length() > 50_000_000) { // 50MB hard limit for regex operations
        LOG.warn("Input size {} exceeds safe processing limit (50MB), returning original content", 
                 input.length());
        return input;
    }

    // Normalize all line breaks to \n (handles \r\n, \r, \n)
    // This pattern is safe: simple alternation with no quantifiers
    String normalized = input.replaceAll("\\r\\n|\\r", "\n");

    if (singleLine) {
        // Remove all line breaks and collapse whitespace to single space
        // Pattern is safe: simple character class with quantifier
        String compressed = normalized.replaceAll("\\s+", " ").strip();
        // Simple string replace (not regex) - safe
        return compressed.replace("> <", "><");
    } else {
        // Preserve line breaks but collapse other whitespace
        // Patterns are safe: bounded character classes with simple quantifiers
        return normalized
                .replaceAll("[ \\t]+", " ")      // Collapse spaces/tabs to single space
                .replaceAll("\\n+", "\n")       // Collapse multiple newlines to single
                .replaceAll(" *\\n *", "\n")    // Remove spaces around newlines
                .strip();                        // Remove leading/trailing whitespace
    }
}
```

#### 3.6: Update Class JavaDoc

**Location**: Update lines 34-46

**Changes**: Add security considerations section:
```java
/**
 * <p>
 * Used to compress HTML output. Just wrap a given section with the tag.
 * </p>
 *
 * <p>
 * <b>Security considerations:</b>
 * </p>
 * <ul>
 *   <li>Body content is truncated in log messages to prevent sensitive data exposure</li>
 *   <li>Maximum size limit prevents DoS attacks via large inputs (configurable via struts.compress.maxSize)</li>
 *   <li>Regex operations include safeguards against ReDoS attacks</li>
 * </ul>
 *
 * <p>
 * Configurable attributes are:
 * </p>
 * ...
```

**Validation Checklist**:
- [ ] New fields added with appropriate types
- [ ] Injection methods handle null/empty/invalid values gracefully
- [ ] Helper methods are private and well-documented
- [ ] `end()` method includes size check before processing
- [ ] All log statements use `truncateForLogging()`
- [ ] `compressWhitespace()` includes hard limit check
- [ ] JavaDoc updated with security notes
- [ ] Code follows existing patterns and conventions

---

### Phase 4: Test Implementation

**File**: `core/src/test/java/org/apache/struts2/components/CompressTest.java`
**Location**: Add new test methods before `setUp()` method (before line 345)

#### Test 1: Max Size Limit Enforcement

```java
public void testMaxSizeLimit() {
    Compress compress = new Compress(stack);
    
    // Create body larger than default maxSize (10MB)
    StringBuilder largeBody = new StringBuilder();
    largeBody.append("<html><body>");
    for (int i = 0; i < 11_000_000; i++) { // ~11MB
        largeBody.append("x");
    }
    largeBody.append("</body></html>");
    
    StringWriter writer = new StringWriter();
    compress.setDevMode("false");
    compress.setMaxSize("10485760"); // 10MB
    
    compress.end(writer, largeBody.toString());
    
    // Should return original content without compression
    assertEquals(largeBody.toString(), writer.toString());
}
```

#### Test 2: Max Size Disabled

```java
public void testMaxSizeDisabled() {
    Compress compress = new Compress(stack);
    
    StringBuilder largeBody = new StringBuilder();
    largeBody.append("<html><body>");
    for (int i = 0; i < 11_000_000; i++) {
        largeBody.append("x");
    }
    largeBody.append("</body></html>");
    
    StringWriter writer = new StringWriter();
    compress.setDevMode("false");
    compress.setMaxSize(null); // No limit
    
    compress.end(writer, largeBody.toString());
    
    // Should compress even large content when limit is disabled
    assertNotEquals(largeBody.toString(), writer.toString());
}
```

#### Test 3: Log Truncation

```java
public void testLogTruncation() {
    Compress compress = new Compress(stack);
    
    StringBuilder longBody = new StringBuilder();
    for (int i = 0; i < 500; i++) {
        longBody.append("x");
    }
    
    compress.setLogMaxLength("200");
    
    // Test that processing doesn't throw exceptions with long content
    StringWriter writer = new StringWriter();
    compress.setDevMode("false");
    compress.end(writer, longBody.toString());
    
    // Should process without errors
    assertNotNull(writer.toString());
}
```

#### Test 4: Very Large Input Safety

```java
public void testVeryLargeInputSafety() {
    Compress compress = new Compress(stack);
    
    // Create input larger than 50MB hard limit
    StringBuilder hugeBody = new StringBuilder();
    hugeBody.append("<html><body>");
    for (int i = 0; i < 60_000_000; i++) { // ~60MB
        hugeBody.append("x");
    }
    hugeBody.append("</body></html>");
    
    StringWriter writer = new StringWriter();
    compress.setDevMode("false");
    compress.setMaxSize(null); // No config limit
    
    compress.end(writer, hugeBody.toString());
    
    // Should return original content due to hard limit in compressWhitespace
    assertEquals(hugeBody.toString(), writer.toString());
}
```

#### Test 5: Invalid Configuration Values

```java
public void testInvalidMaxSizeConfiguration() {
    Compress compress = new Compress(stack);
    
    // Test negative value
    compress.setMaxSize("-1");
    assertNull(compress.maxSize); // Should be null after invalid input
    
    // Test non-numeric value
    compress.setMaxSize("invalid");
    assertNull(compress.maxSize); // Should be null after invalid input
    
    // Test valid value
    compress.setMaxSize("5242880"); // 5MB
    assertEquals(Long.valueOf(5242880L), compress.maxSize);
}

public void testInvalidLogMaxLengthConfiguration() {
    Compress compress = new Compress(stack);
    
    // Test negative value
    compress.setLogMaxLength("-1");
    assertEquals(200, compress.logMaxLength); // Should use default
    
    // Test non-numeric value
    compress.setLogMaxLength("invalid");
    assertEquals(200, compress.logMaxLength); // Should use default
    
    // Test valid value
    compress.setLogMaxLength("500");
    assertEquals(500, compress.logMaxLength);
}
```

**Note**: Tests 5 require access to private fields. Use reflection or make fields package-private for testing, or test via behavior rather than direct field access.

**Validation Checklist**:
- [ ] All new test methods added
- [ ] Tests cover size limit enforcement
- [ ] Tests cover disabled size limit
- [ ] Tests cover log truncation behavior
- [ ] Tests cover very large input safety
- [ ] Tests cover invalid configuration handling
- [ ] All existing tests still pass
- [ ] Run: `mvn test -Dtest=CompressTest -DskipAssembly`

---

### Phase 5: Documentation Updates

#### 5.1: Site Documentation - Description

**File**: `core/src/site/resources/tags/compress-description.html`

**Changes**: Add security section:
```html
<p><b>Security:</b> The compress tag includes built-in protections against DoS attacks 
and sensitive data exposure. Large content exceeding the configured maximum size 
(default 10MB) will be skipped without compression. Log messages are automatically 
truncated to prevent sensitive data from appearing in logs.</p>
```

#### 5.2: Site Documentation - Attributes

**File**: `core/src/site/resources/tags/compress-attributes.html`

**Changes**: Add note about security features in existing attribute descriptions if needed.

**Validation Checklist**:
- [ ] Security section added to description
- [ ] Documentation is clear and accurate
- [ ] Follows existing documentation style

---

## Implementation Checklist

### Pre-Implementation
- [ ] Review existing code patterns in similar components
- [ ] Verify configuration injection patterns
- [ ] Check test structure and conventions

### Phase 1: Configuration Constants
- [ ] Add `STRUTS_COMPRESS_MAX_SIZE` constant
- [ ] Add `STRUTS_COMPRESS_LOG_MAX_LENGTH` constant
- [ ] Add proper JavaDoc documentation
- [ ] Verify naming conventions

### Phase 2: Default Properties
- [ ] Add `struts.compress.maxSize=10485760`
- [ ] Add `struts.compress.log.maxLength=200`
- [ ] Add descriptive comments
- [ ] Verify property file format

### Phase 3: Component Updates
- [ ] Add `maxSize` and `logMaxLength` fields
- [ ] Add `setMaxSize()` injection method
- [ ] Add `setLogMaxLength()` injection method
- [ ] Add `truncateForLogging()` helper method
- [ ] Add `exceedsMaxSize()` helper method
- [ ] Update `end()` method with size check
- [ ] Update all log statements to use truncation
- [ ] Update `compressWhitespace()` with hard limit
- [ ] Update class JavaDoc with security notes

### Phase 4: Tests
- [ ] Add `testMaxSizeLimit()` test
- [ ] Add `testMaxSizeDisabled()` test
- [ ] Add `testLogTruncation()` test
- [ ] Add `testVeryLargeInputSafety()` test
- [ ] Add `testInvalidMaxSizeConfiguration()` test
- [ ] Add `testInvalidLogMaxLengthConfiguration()` test
- [ ] Run all CompressTest tests
- [ ] Run CompressTagTest tests
- [ ] Verify all tests pass

### Phase 5: Documentation
- [ ] Update compress-description.html
- [ ] Update compress-attributes.html if needed
- [ ] Verify documentation accuracy

### Post-Implementation
- [ ] Code review
- [ ] Security review
- [ ] Performance testing
- [ ] Integration testing
- [ ] Update CHANGELOG if applicable

---

## Configuration Examples

### Default Configuration (Recommended)
```properties
struts.compress.maxSize=10485760
struts.compress.log.maxLength=200
```

### Custom Size Limit (5MB)
```properties
struts.compress.maxSize=5242880
```

### Disable Size Limit (Not Recommended)
```properties
struts.compress.maxSize=0
```

### Increase Log Truncation Length
```properties
struts.compress.log.maxLength=500
```

---

## Security Considerations

### Default Values Rationale
- **10MB max size**: Reasonable for HTML content, prevents DoS while allowing legitimate use
- **200 char log truncation**: Prevents sensitive data exposure while maintaining useful debugging info
- **50MB hard limit**: Absolute safety limit for regex operations, prevents ReDoS

### Backward Compatibility
- All changes are backward compatible
- Default behavior preserved when limits are disabled
- Existing code continues to work without changes

### Error Handling
- Invalid configuration values log warnings and use safe defaults
- Size limit violations log warnings and skip compression (fail-safe)
- Hard limit violations return original content (fail-safe)

---

## Code References

### Files to Modify
- `core/src/main/java/org/apache/struts2/StrutsConstants.java:344-365` - Add constants
- `core/src/main/resources/org/apache/struts2/default.properties:215-220` - Add defaults
- `core/src/main/java/org/apache/struts2/components/Compress.java:83-161` - Main implementation
- `core/src/test/java/org/apache/struts2/components/CompressTest.java:345+` - Add tests
- `core/src/site/resources/tags/compress-description.html` - Update docs

### Related Patterns
- Size limit pattern: `AbstractMultiPartRequest.java:234-249` - `exceedsMaxStringLength()`
- Configuration injection: `AbstractMultiPartRequest.java:133-168` - Size limit injection
- Logging patterns: Standard Log4j2 parameterized logging

---

## Testing Strategy

### Unit Tests
- Test size limit enforcement
- Test size limit disabled
- Test log truncation
- Test very large input safety
- Test invalid configuration values

### Integration Tests
- Verify existing CompressTagTest still passes
- Test with various configuration combinations
- Test with real HTML content

### Performance Tests
- Verify no significant performance regression
- Test with typical HTML sizes (1KB - 1MB)
- Test with edge cases (empty, very small, at limits)

### Security Tests
- Test with very large inputs (>10MB, >50MB)
- Test with inputs containing sensitive data
- Verify log truncation works correctly
- Verify size limits are enforced

---

## Risk Assessment

### Implementation Risks
- **Low**: Changes are isolated to Compress component
- **Low**: Backward compatible by default
- **Low**: Follows existing Struts patterns

### Security Risks Addressed
- **Medium-High → Low**: DoS via large input (mitigated)
- **Medium → Low**: Sensitive data in logs (mitigated)
- **Low-Medium → Low**: ReDoS potential (mitigated)

---

## Open Questions

1. Should the hard limit (50MB) be configurable or remain fixed?
   - **Decision**: Fixed for security, configurable limit is separate
2. Should size limit violations throw exceptions or just skip compression?
   - **Decision**: Skip compression (fail-safe approach)
3. Should log truncation be configurable per log level?
   - **Decision**: Single configuration for simplicity

---

## Notes

- All changes follow existing Struts patterns and conventions
- Default values chosen to balance security and usability
- Implementation is fail-safe (errors don't break functionality)
- Comprehensive test coverage ensures reliability
- Documentation updated to inform users of security features

