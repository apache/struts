---
date: 2025-11-21T00:00:00Z
topic: "Lazy Multipart Parsing for Dynamic File Upload Limits"
tags: [research, codebase, file-upload, multipart, lazy-parsing, dynamic-configuration]
status: complete
related_ticket: WW-5585
---

# Research: Lazy Multipart Parsing for Dynamic File Upload Limits

**Date**: 2025-11-21

## Research Question

The current WW-5585 implementation allows dynamic configuration of file upload validation parameters (allowedTypes, allowedExtensions, maximumSize) at the interceptor level via `WithLazyParams`. However, the `MultiPartRequest` parsing happens **before** the action is instantiated, enforcing global limits (`struts.multipart.maxSize`) as hard caps.

**Problem**: Even if an action specifies a higher `maximumSize` dynamically, files exceeding the global `struts.multipart.maxSize` are rejected during parsing before the interceptor can apply dynamic limits.

**Goal**: Investigate implementing "lazy parsing" to defer multipart parsing until after `Preparable.prepare()` runs, allowing dynamic limits to be applied before parsing.

## Summary

### Architecture Constraint
The current request lifecycle is:
1. **Request arrives** → `Dispatcher.wrapRequest()` creates `MultiPartRequestWrapper`
2. **Constructor** → `multi.parse(request, saveDir)` with global limits
3. **Action instantiation** → `Preparable.prepare()` sets dynamic config
4. **Interceptor runs** → `ActionFileUploadInterceptor` validates files (too late for size limits)

### Proposed Solution: Lazy Parsing
Defer `parse()` execution until after `Preparable.prepare()` runs, allowing the interceptor to inject dynamic limits before parsing.

**Changes Required:**
| Component | Change |
|-----------|--------|
| `AbstractMultiPartRequest` | Add lazy parsing state, limit setters, `triggerParsing()` |
| `MultiPartRequestWrapper` | Defer `parse()` call, add `triggerParsing()` delegation |
| `ActionFileUploadInterceptor` | Trigger parsing after reading dynamic params |
| `StrutsConstants` | Add `struts.multipart.lazyParsing` flag |

### Complexity Assessment
- **Medium complexity** - Changes to 4 classes
- **Backward compatible** - Flag defaults to `false`
- **Risk**: Breaking existing code that accesses files before interceptor

## Detailed Findings

### 1. Current Multipart Request Lifecycle

**Entry Point**: `Dispatcher.wrapRequest()`

```java
// Dispatcher.java - creates wrapper with immediate parsing
MultiPartRequestWrapper multiWrapper = new MultiPartRequestWrapper(
    multiPartRequest, request, saveDir, provider,
    disableRequestAttributeValueStackLookup
);
```

**MultiPartRequestWrapper Constructor** (line 72-89):
```java
public MultiPartRequestWrapper(MultiPartRequest multiPartRequest, HttpServletRequest request,
                               String saveDir, LocaleProvider provider,
                               boolean disableRequestAttributeValueStackLookup) {
    super(request, disableRequestAttributeValueStackLookup);
    errors = new ArrayList<>();
    multi = multiPartRequest;
    defaultLocale = provider.getLocale();
    setLocale(request);
    try {
        multi.parse(request, saveDir);  // <-- PARSING HAPPENS HERE
        for (LocalizedMessage error : multi.getErrors()) {
            addError(error);
        }
    } catch (IOException e) {
        LOG.warn(e.getMessage(), e);
        addError(buildErrorMessage(e, new Object[] {e.getMessage()}));
    }
}
```

**Key Insight**: Parsing happens in constructor, before any action/interceptor code runs.

### 2. Size Limit Enforcement in Commons FileUpload2

**AbstractMultiPartRequest.prepareServletFileUpload()** (line 213-229):
```java
protected JakartaServletDiskFileUpload prepareServletFileUpload(Charset charset, Path saveDir) {
    JakartaServletDiskFileUpload servletFileUpload = createJakartaFileUpload(charset, saveDir);

    if (maxSize != null) {
        servletFileUpload.setSizeMax(maxSize);         // Total request size
    }
    if (maxFiles != null) {
        servletFileUpload.setFileCountMax(maxFiles);   // Max file count
    }
    if (maxFileSize != null) {
        servletFileUpload.setFileSizeMax(maxFileSize); // Per-file size
    }
    return servletFileUpload;
}
```

These limits are set on Commons FileUpload2 and enforced during `parseRequest()`. Once set, they cannot be changed retroactively.

### 3. JakartaMultiPartRequest vs JakartaStreamMultiPartRequest

**JakartaMultiPartRequest** (Traditional API):
- Uses `servletFileUpload.parseRequest()` - parses entire request at once
- All files loaded into memory or disk immediately
- Size validation during parsing

**JakartaStreamMultiPartRequest** (Streaming API):
- Uses `servletFileUpload.getItemIterator()` - processes items one at a time
- Files streamed to disk as processed
- Still validates size during streaming

Both implementations call `prepareServletFileUpload()` which sets the limits from injected constants.

### 4. Interceptor Parameter Evaluation Timeline

With `WithLazyParams` implementation:

| Phase | When | Description |
|-------|------|-------------|
| XML Parsing | Startup | Parameters stored as literal strings |
| Interceptor Init | Startup | Singleton created, `@Inject` setters called |
| Request Arrives | Per-request | `MultiPartRequestWrapper` created with parsing |
| Action Created | Per-request | `Preparable.prepare()` can set dynamic values |
| LazyParams Injection | Per-request | `${...}` expressions evaluated against ValueStack |
| Interceptor.intercept() | Per-request | Validates already-parsed files |

**Gap**: Parsing happens before action exists, so dynamic limits can't be applied.

## Implementation Plan

### Phase 1: AbstractMultiPartRequest Changes

Add fields for deferred parsing state:
```java
private HttpServletRequest deferredRequest;
private String deferredSaveDir;
private boolean parsed = false;
private boolean lazyParsingEnabled = false;
```

Add setter methods for dynamic limits:
```java
public void setMaxSizeLimit(Long maxSize) {
    if (maxSize != null) {
        this.maxSize = maxSize;
    }
}

public void setMaxFileSizeLimit(Long maxFileSize) { ... }
public void setMaxFilesLimit(Long maxFiles) { ... }
public void setMaxSizeOfFilesLimit(Long maxSizeOfFiles) { ... }
```

Add lazy parsing control:
```java
public void setLazyParsingEnabled(boolean enabled) {
    this.lazyParsingEnabled = enabled;
}

public void triggerParsing() throws IOException {
    if (!lazyParsingEnabled) {
        throw new IllegalStateException("Lazy parsing is not enabled");
    }
    if (parsed) {
        return;  // Already parsed
    }
    if (deferredRequest == null) {
        throw new IllegalStateException("No deferred request to parse");
    }
    doParse(deferredRequest, deferredSaveDir);
}
```

Modify `parse()` to support deferred mode:
```java
public void parse(HttpServletRequest request, String saveDir) throws IOException {
    if (lazyParsingEnabled) {
        this.deferredRequest = request;
        this.deferredSaveDir = saveDir;
        return;  // Defer actual parsing
    }
    doParse(request, saveDir);
}

private void doParse(HttpServletRequest request, String saveDir) throws IOException {
    // Move existing parse() logic here
    try {
        processUpload(request, saveDir);
    } catch (FileUploadException e) {
        // ... error handling
    } finally {
        parsed = true;
        deferredRequest = null;
        deferredSaveDir = null;
    }
}
```

### Phase 2: MultiPartRequestWrapper Changes

Add delegation methods:
```java
public void triggerParsing() throws IOException {
    if (multi instanceof AbstractMultiPartRequest abstractMulti) {
        abstractMulti.triggerParsing();
        // Re-collect errors after parsing
        for (LocalizedMessage error : multi.getErrors()) {
            addError(error);
        }
    }
}

public void setMaxSizeLimit(Long maxSize) {
    if (multi instanceof AbstractMultiPartRequest abstractMulti) {
        abstractMulti.setMaxSizeLimit(maxSize);
    }
}

public void setMaxFileSizeLimit(Long maxFileSize) { ... }
public void setMaxFilesLimit(Long maxFiles) { ... }

public boolean isLazyParsingEnabled() {
    if (multi instanceof AbstractMultiPartRequest abstractMulti) {
        return abstractMulti.isLazyParsingEnabled();
    }
    return false;
}
```

Modify constructor to support lazy mode:
```java
public MultiPartRequestWrapper(MultiPartRequest multiPartRequest, HttpServletRequest request,
                               String saveDir, LocaleProvider provider,
                               boolean disableRequestAttributeValueStackLookup) {
    super(request, disableRequestAttributeValueStackLookup);
    errors = new ArrayList<>();
    multi = multiPartRequest;
    defaultLocale = provider.getLocale();
    setLocale(request);

    // Check if lazy parsing is enabled
    boolean lazyParsing = (multi instanceof AbstractMultiPartRequest abstractMulti)
        && abstractMulti.isLazyParsingEnabled();

    if (!lazyParsing) {
        try {
            multi.parse(request, saveDir);
            collectErrors();
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
            addError(buildErrorMessage(e, new Object[] {e.getMessage()}));
        }
    } else {
        // Store for later parsing
        try {
            multi.parse(request, saveDir);  // This just stores the request
        } catch (IOException e) {
            // Should not happen in lazy mode
            LOG.warn("Unexpected error during lazy parse setup", e);
        }
    }
}

private void collectErrors() {
    for (LocalizedMessage error : multi.getErrors()) {
        addError(error);
    }
}
```

### Phase 3: ActionFileUploadInterceptor Changes

Modify `intercept()` to trigger lazy parsing:
```java
@Override
public String intercept(ActionInvocation invocation) throws Exception {
    HttpServletRequest request = invocation.getInvocationContext().getServletRequest();
    MultiPartRequestWrapper multiWrapper = findMultipartRequestWrapper(request);

    if (multiWrapper == null) {
        // ... existing bypass logic
        return invocation.invoke();
    }

    // Trigger lazy parsing with dynamic limits if enabled
    if (multiWrapper.isLazyParsingEnabled()) {
        // Apply dynamic limits before parsing
        applyDynamicLimits(multiWrapper, invocation);

        try {
            multiWrapper.triggerParsing();
        } catch (IOException e) {
            LOG.warn("Error during lazy multipart parsing", e);
        }
    }

    if (!(invocation.getAction() instanceof UploadedFilesAware action)) {
        // ... existing logic
        return invocation.invoke();
    }

    // ... rest of existing validation logic
}

private void applyDynamicLimits(MultiPartRequestWrapper multiWrapper, ActionInvocation invocation) {
    // Apply interceptor's maximumSize to wrapper before parsing
    Long maxSize = getMaximumSize();  // From interceptor config (possibly dynamic)
    if (maxSize != null) {
        multiWrapper.setMaxFileSizeLimit(maxSize);
    }

    // Could also read from action if it implements a config interface
    Object action = invocation.getAction();
    if (action instanceof FileUploadConfigurable configurable) {
        Long actionMaxSize = configurable.getMaxFileSize();
        if (actionMaxSize != null) {
            multiWrapper.setMaxFileSizeLimit(actionMaxSize);
        }
    }
}
```

### Phase 4: Configuration Flag

Add to `StrutsConstants`:
```java
String STRUTS_MULTIPART_LAZY_PARSING = "struts.multipart.lazyParsing";
```

Add to `default.properties`:
```properties
struts.multipart.lazyParsing = false
```

Inject in `AbstractMultiPartRequest`:
```java
@Inject(value = StrutsConstants.STRUTS_MULTIPART_LAZY_PARSING, required = false)
public void setLazyParsingEnabled(String enabled) {
    this.lazyParsingEnabled = Boolean.parseBoolean(enabled);
}
```

### Phase 5: Optional FileUploadConfigurable Interface

Create interface for actions that provide dynamic upload config:
```java
public interface FileUploadConfigurable {
    Long getMaxFileSize();
    Long getMaxRequestSize();
    Long getMaxFiles();
    Set<String> getAllowedTypes();
    Set<String> getAllowedExtensions();
}
```

This allows actions to provide upload limits that are applied before parsing.

## Code References

### Files to Modify
- `core/src/main/java/org/apache/struts2/dispatcher/multipart/AbstractMultiPartRequest.java` - Add lazy parsing support
- `core/src/main/java/org/apache/struts2/dispatcher/multipart/MultiPartRequestWrapper.java` - Add delegation methods
- `core/src/main/java/org/apache/struts2/interceptor/ActionFileUploadInterceptor.java` - Trigger lazy parsing
- `core/src/main/java/org/apache/struts2/StrutsConstants.java` - Add flag constant
- `core/src/main/resources/org/apache/struts2/default.properties` - Add default value

### Files to Create
- `core/src/main/java/org/apache/struts2/action/FileUploadConfigurable.java` - Optional interface

### Test Files
- `core/src/test/java/org/apache/struts2/interceptor/ActionFileUploadInterceptorTest.java` - Add lazy parsing tests
- `apps/showcase/src/test/java/it/org/apache/struts2/showcase/DynamicFileUploadTest.java` - Integration tests

## Architecture Insights

### Request Flow with Lazy Parsing

```
Request Arrives
    │
    ▼
Dispatcher.wrapRequest()
    │
    ▼
MultiPartRequestWrapper(lazyParsing=true)
    │
    ├─► Stores request, saveDir
    │   Does NOT call processUpload()
    │
    ▼
Action Instantiation
    │
    ▼
Preparable.prepare()
    │
    ├─► Action sets dynamic config
    │
    ▼
ActionFileUploadInterceptor.intercept()
    │
    ├─► Reads dynamic limits from action/config
    ├─► Calls multiWrapper.setMaxFileSizeLimit(...)
    ├─► Calls multiWrapper.triggerParsing()
    │       │
    │       ▼
    │   processUpload() with dynamic limits
    │
    ▼
File Validation (existing logic)
    │
    ▼
Action.execute()
```

### Backward Compatibility

1. **Default behavior unchanged**: `struts.multipart.lazyParsing=false` by default
2. **Opt-in feature**: Users must explicitly enable lazy parsing
3. **Graceful fallback**: If lazy parsing enabled but not triggered, files still accessible (empty)
4. **No API breaking changes**: All existing code continues to work

### Security Considerations

1. **Memory usage**: With lazy parsing, request body is buffered until parsing
2. **Timeout handling**: Long-running prepare() could delay parsing
3. **Error handling**: Parsing errors must be properly propagated to action
4. **Resource cleanup**: Ensure deferred request references are cleared

## Open Questions

1. ~~**Buffer management**: How is the request body buffered during deferral? Servlet container may not support re-reading.~~ **RESOLVED**: HTTP input stream cannot be re-read. Lazy parsing as originally designed is not feasible.
2. **Streaming impact**: Does lazy parsing break streaming implementations?
3. **Error timing**: Should parse errors be reported as action errors or interceptor errors?
4. **Multiple interceptors**: What if multiple interceptors try to trigger parsing?

## Follow-up Research: Request Body Re-readability (2025-11-21)

### Finding: HTTP Input Stream Cannot Be Re-read

The HTTP servlet input stream is a **one-time read stream**:
- Once consumed by `parseRequest()` or `getItemIterator()`, it's exhausted
- No built-in buffering or rewinding capability
- This is a fundamental limitation of the Servlet API

### Solutions Considered

| Solution | Feasibility | Issue |
|----------|-------------|-------|
| Defer parsing, read later | ❌ Not possible | Stream consumed |
| Cache body with `ContentCachingRequestWrapper` | ⚠️ Risky | Large files → OOM |
| Custom `HttpServletRequestWrapper` with buffering | ⚠️ Risky | Same memory issue |

### Alternative: Early Action Resolution (Option 2 Revised)

**Key Discovery**: Action mapping can be resolved from URL path BEFORE parsing!

In `DefaultActionMapper.getMapping()`:
```java
String uri = RequestUtils.getUri(request);  // URL path only
parseNameAndNamespace(uri, mapping, configManager);  // No body needed
handleSpecialParameters(request, mapping);  // Uses getParameterMap() - won't have multipart params
```

The core action/namespace is determined from URL. `handleSpecialParameters()` handles button prefixes which are rarely used with file uploads.

### Revised Implementation Plan

**Instead of lazy parsing, use early action config lookup:**

1. **In `StrutsPrepareFilter.doFilter()` - change order:**
   ```java
   // BEFORE: wrapRequest() then findActionMapping()
   // AFTER:  findActionMapping() then wrapRequest()

   ActionMapping mapping = prepare.findActionMapping(request, response, true);
   // Look up action config, get interceptor params
   Long maxSize = getMaxSizeFromActionConfig(mapping);
   // Apply to multipart request before parsing
   request = prepare.wrapRequest(request, maxSize);
   ```

2. **Add method to look up interceptor params from ActionConfig:**
   ```java
   private Long getMaxSizeFromActionConfig(ActionMapping mapping) {
       if (mapping == null) return null;
       ActionConfig actionConfig = configuration.getActionConfig(
           mapping.getNamespace(), mapping.getName());
       if (actionConfig == null) return null;

       // Find actionFileUpload interceptor params
       for (InterceptorMapping im : actionConfig.getInterceptors()) {
           if ("actionFileUpload".equals(im.getName())) {
               String maxSize = im.getParams().get("maximumSize");
               if (maxSize != null) {
                   // Handle ${...} expressions by evaluating later
                   return parseSize(maxSize);
               }
           }
       }
       return null;
   }
   ```

3. **Modify `Dispatcher.wrapRequest()` to accept optional maxSize:**
   ```java
   public HttpServletRequest wrapRequest(HttpServletRequest request, Long maxSize) {
       if (maxSize != null) {
           multiPartRequest.setMaxSizeLimit(maxSize);
       }
       // ... existing wrapping logic
   }
   ```

### Limitations of Early Action Resolution

1. **Dynamic `${...}` expressions won't work** - No ValueStack available yet
2. **`handleSpecialParameters()` won't have multipart form fields** - But rarely needed for file uploads
3. **Action not instantiated** - Can't call `Preparable.prepare()`

### Recommendation

For **static interceptor params** (e.g., `<param name="maximumSize">10485760</param>`), early action resolution works.

For **dynamic params** using `${...}` expressions:
- Current `WithLazyParams` implementation handles post-parsing validation
- Pre-parsing dynamic limits require `Preparable.prepare()` which needs action instantiation
- **True dynamic pre-parsing limits may not be achievable** without significant architectural changes

## Final Conclusion

**Decision: Keep current approach - no lazy parsing implementation.**

The current architecture is sufficient:

| Layer | Limit | When | Purpose |
|-------|-------|------|---------|
| `struts.multipart.maxSize` | Global hard ceiling | During parsing | Prevent oversized requests |
| Interceptor `maximumSize` | Per-action (via `WithLazyParams`) | After parsing | Fine-grained validation |

**Rationale:**
1. Early action resolution adds complexity users won't understand
2. Difference between "pre-parsing" and "post-parsing" limits is confusing
3. Current `WithLazyParams` provides adequate dynamic control
4. Users simply set global limit high enough, then use interceptor params

**Recommendation:** Document current behavior clearly - set `struts.multipart.maxSize` as ceiling, use dynamic interceptor params for per-action limits

## Risk Assessment

| Risk | Impact | Mitigation |
|------|--------|------------|
| Request body not re-readable | High | May need to cache input stream |
| Breaking existing behavior | Medium | Default to disabled |
| Performance overhead | Low | Only affects lazy-enabled requests |
| Complexity increase | Medium | Clear documentation, tests |

## Next Steps

1. **Prototype Phase 1**: Implement AbstractMultiPartRequest changes
2. **Test re-readability**: Verify request body can be read after deferral
3. **Implement Phase 2-4**: Complete wrapper and interceptor changes
4. **Add comprehensive tests**: Unit and integration tests
5. **Documentation**: Update file upload documentation
6. **Performance testing**: Measure overhead of lazy parsing

## Related Research

- `thoughts/shared/research/2025-10-22-dynamic-file-upload-validation.md` - WithLazyParams implementation
- WW-5585 ticket - Dynamic file upload parameters feature
