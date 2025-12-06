---
date: 2025-09-24T09:18:27+02:00
topic: "WW-5256 FreeMarker Whitespace Compression Implementation Analysis"
tags: [research, codebase, freemarker, compression, whitespace, WW-5256, configuration]
status: complete
---

# Research: WW-5256 FreeMarker Whitespace Compression Implementation Analysis

**Date**: 2025-09-24T09:18:27+02:00

## Research Question
Analyze JIRA issue WW-5256 and FreeMarker whitespace documentation to determine what changes are needed to fulfill the requirements, ensuring compression is optional and can be disabled in devMode or via configuration flag.

## Summary
WW-5256 requests reducing HTML output size from FreeMarker templates. A comprehensive implementation already exists in a feature branch but hasn't been merged. The current main branch has basic FreeMarker whitespace stripping hardcoded as enabled. To fulfill the requirements, we need to:

1. **Merge the existing compress tag implementation** from feature branch `feature/WW-5256-compress`
2. **Add configuration option** for FreeMarker whitespace stripping (currently hardcoded)
3. **Integrate DevMode awareness** for both features (partially exists in compress tag)

## Detailed Findings

### Current Implementation Status

#### Main Branch (Production)
- **FreeMarker whitespace stripping**: Hardcoded as enabled in `FreemarkerManager.java:345`
- **No configuration control**: Cannot be disabled through Struts properties
- **No compress tag**: Dedicated compression functionality doesn't exist

#### Feature Branch (`feature/WW-5256-compress`)
- **Dedicated compress tag**: Complete implementation with `<s:compress>` tag
- **DevMode integration**: Automatically disables compression in development mode
- **HTML compression algorithm**: Uses regex `body.trim().replaceAll(">\\s+<", "><")`
- **Force override**: `force` attribute allows overriding DevMode behavior

### FreeMarker Documentation Analysis

#### Available Compression Techniques
1. **Whitespace Stripping**: Removes indentation and trailing whitespace from lines with only FTL tags
2. **Compress Directive**: `<#compress>` removes excess whitespace from generated output
3. **Manual Control**: `<#t>`, `<#rt>`, `<#lt>`, `<#nt>` directives for fine-grained control

#### Best Practices
- Compression should be configurable based on environment (development vs production)
- Multiple compression levels available for different use cases
- Template-level control allows fine-tuning specific outputs

### Required Changes for Full Implementation

#### 1. Configuration System Integration

**New Configuration Constants** (add to `StrutsConstants.java`):
```java
public static final String STRUTS_FREEMARKER_WHITESPACE_STRIPPING = "struts.freemarker.whitespaceStripping";
public static final String STRUTS_FREEMARKER_COMPRESSION_ENABLED = "struts.freemarker.compression.enabled";
```

**Default Properties** (add to `default.properties`):
```properties
# FreeMarker whitespace stripping (currently hardcoded as true)
struts.freemarker.whitespaceStripping = true

# FreeMarker HTML compression tag (new feature)
struts.freemarker.compression.enabled = true
```

#### 2. FreemarkerManager Modifications

**Current Code** (`FreemarkerManager.java:345`):
```java
LOG.debug("Enabled whitespace stripping");
configuration.setWhitespaceStripping(true);  // Hardcoded!
```

**Required Change**:
```java
@Inject(value = StrutsConstants.STRUTS_FREEMARKER_WHITESPACE_STRIPPING, required = false)
public void setWhitespaceStripping(String whitespaceStripping) {
    this.whitespaceStripping = BooleanUtils.toBoolean(whitespaceStripping);
}

// In createConfiguration() method:
if (whitespaceStripping && !devMode) {
    LOG.debug("Enabled whitespace stripping");
    configuration.setWhitespaceStripping(true);
} else {
    LOG.debug("Disabled whitespace stripping (devMode: {})", devMode);
    configuration.setWhitespaceStripping(false);
}
```

#### 3. Compress Component Enhancement

**Merge from Feature Branch**:
- `core/src/main/java/org/apache/struts2/components/Compress.java`
- `core/src/main/java/org/apache/struts2/views/jsp/CompressTag.java`
- Test files: `CompressTest.java`, `CompressTagTest.java`
- Documentation files

**Add Global Configuration Support**:
```java
@Inject(value = StrutsConstants.STRUTS_FREEMARKER_COMPRESSION_ENABLED, required = false)
public void setCompressionEnabled(String compressionEnabled) {
    this.compressionEnabled = BooleanUtils.toBoolean(compressionEnabled);
}

@Inject(StrutsConstants.STRUTS_DEVMODE)
public void setDevMode(String devMode) {
    this.devMode = BooleanUtils.toBoolean(devMode);
}
```

## Code References

### Current Implementation
- `core/src/main/java/org/apache/struts2/views/freemarker/FreemarkerManager.java:345` - Hardcoded whitespace stripping
- `core/src/main/java/org/apache/struts2/StrutsConstants.java` - Configuration constants
- `core/src/main/resources/org/apache/struts2/default.properties` - Default configuration values

### Feature Branch Implementation
- Commit: `a98ba7717d391e643b578501086d1b40f82d9ca4` - "WW-5256 Implements dedicated tag to compress output"
- `feature/WW-5256-compress` branch contains complete compress tag implementation

### Configuration Patterns
- `core/src/main/java/org/apache/struts2/components/Component.java` - DevMode injection pattern
- `core/src/main/java/org/apache/struts2/config/StrutsBeanSelectionProvider.java` - DevMode auto-configuration

## Architecture Insights

### Established Configuration Patterns
1. **Dependency Injection**: Use `@Inject` with `StrutsConstants` for configuration
2. **Boolean Conversion**: Use `BooleanUtils.toBoolean()` for string-to-boolean conversion
3. **DevMode Integration**: Check `devMode` flag to automatically adjust behavior
4. **Optional Parameters**: Use `required = false` for optional configuration

### Security Considerations
- DevMode awareness prevents unexpected behavior in development
- Simple regex-based compression algorithm avoids complex parsing vulnerabilities
- Configuration allows administrators to control feature availability

### Performance Considerations
- Whitespace stripping happens during template compilation (minimal runtime cost)
- HTML compression happens during output generation (measurable runtime cost)
- Configuration allows disabling features when not needed

## Implementation Strategy

### Phase 1: Make Current Whitespace Stripping Configurable
1. Add configuration constant and default value
2. Modify `FreemarkerManager` to inject and use the configuration
3. Add DevMode awareness to disable stripping in development

### Phase 2: Merge Compress Tag Implementation
1. Cherry-pick or merge the feature branch implementation
2. Add global configuration support to the compress component
3. Ensure proper integration with existing DevMode patterns

### Phase 3: Testing and Documentation
1. Test both features with various configuration combinations
2. Update documentation for new configuration options
3. Verify DevMode behavior works as expected

## Open Questions
1. Should whitespace stripping be automatically disabled in DevMode (like the compress tag) or require explicit configuration?
2. Should there be different compression levels or is the current binary on/off sufficient?
3. Should the compress tag respect the global whitespace stripping setting or remain independent?

## Related Research
- WW-5256: Reduce size of generate html out of freemarker tag templates
- FreeMarker Documentation: Whitespace handling best practices
- Struts Configuration Patterns: DevMode and optional feature implementation