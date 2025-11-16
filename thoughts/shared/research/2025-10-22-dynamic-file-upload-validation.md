---
date: 2025-10-22T00:00:00Z
topic: "Dynamic File Upload Validation Without Custom Interceptors"
tags: [research, codebase, file-upload, validation, interceptor, UploadedFilesAware]
status: complete
git_commit: 06f9f9303387edf0557d128bbd7123bded4f24f5
---

# Research: Dynamic File Upload Validation Without Custom Interceptors

**Date**: 2025-10-22

## Research Question

User asked: How can I dynamically set `allowedTypes` and `allowedExtensions` for file upload validation from my action class instead of static configuration in struts.xml?

Specific issues:
1. Static configuration works: `<param name="actionFileUpload.allowedTypes">application/pdf</param>`
2. Dynamic expression doesn't work: `<param name="actionFileUpload.allowedTypes">${acceptedFileTypes}</param>` (due to TextParseUtil.commaDelimitedStringToSet)
3. Documentation mentions `setXContentType(String contentType)` method but cannot find it in Struts 7 core
4. Needs to match validation logic with parallel batch import function (without Struts)
5. Wants to avoid writing a custom interceptor

## Summary

**Key Findings:**

1. **`${...}` expressions don't work in interceptor parameters** because `TextParseUtil.commaDelimitedStringToSet()` is a pure string splitter with no OGNL evaluation, and interceptor parameters are set at startup when no ValueStack exists.

2. **`setXContentType` is a deprecated pattern** from pre-Struts 6.4.0 using naming conventions (`setUploadContentType`). Modern approach uses `UploadedFilesAware` interface with `UploadedFile` API.

3. **Recommended solution: Programmatic validation in action** by implementing:
   - `UploadedFilesAware` interface to receive files
   - `validate()` method for custom validation logic
   - Shared configuration class for both Struts and batch import
   - No custom interceptor needed

4. **Alternative if needed:** Implement `WithLazyParams` interface for runtime parameter evaluation (but with per-request overhead).

## Detailed Findings

### 1. Why `${acceptedFileTypes}` Doesn't Work

**File**: [core/src/main/java/org/apache/struts2/util/TextParseUtil.java#L256](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/core/src/main/java/org/apache/struts2/util/TextParseUtil.java#L256)

```java
public static Set<String> commaDelimitedStringToSet(String s) {
    return Arrays.stream(s.split(","))
        .map(String::trim)
        .filter(s1 -> !s1.isEmpty())
        .collect(Collectors.toSet());
}
```

**The Problem:**
- This is a **pure string splitter** with zero OGNL evaluation
- If you pass `"${acceptedFileTypes}"`, it creates a Set with the literal string `"${acceptedFileTypes}"`
- No expression evaluation happens

**Root Cause - Parameter Processing Lifecycle:**

**Phase 1: XML Parsing (Startup)**
- File: [core/src/main/java/org/apache/struts2/config/providers/XmlHelper.java#L73-95](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/core/src/main/java/org/apache/struts2/config/providers/XmlHelper.java#L73-L95)
- Parameters extracted as **literal strings** from XML
- `${foo}` remains as the string `"${foo}"`

**Phase 2: Interceptor Instantiation (Startup)**
- File: [core/src/main/java/org/apache/struts2/factory/DefaultInterceptorFactory.java#L54-81](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/core/src/main/java/org/apache/struts2/factory/DefaultInterceptorFactory.java#L54-L81)
- Properties set via `reflectionProvider.setProperties(params, interceptor)`
- OGNL is used to **set** properties, not **evaluate** the value strings
- No `ActionContext` or `ValueStack` exists yet (happens at startup)

**Phase 3: Setter Execution**
- File: [core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java#L78](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java#L78)

```java
public void setAllowedExtensions(String allowedExtensions) {
    allowedExtensionsSet = TextParseUtil.commaDelimitedStringToSet(allowedExtensions);
}
```

- Receives literal string `"${acceptedFileTypes}"`
- No evaluation mechanism available
- Creates Set with that literal value

### 2. About `setXContentType` Method

**The Old Pattern (Deprecated - Pre-Struts 6.4.0):**

Used automatic property binding with naming conventions:
- `setUpload(File file)` - receives the uploaded file
- `setUploadContentType(String contentType)` - receives the content type
- `setUploadFileName(String fileName)` - receives the original filename

**Example**: [apps/showcase/src/main/java/org/apache/struts2/showcase/UITagExample.java#L245-246](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/apps/showcase/src/main/java/org/apache/struts2/showcase/UITagExample.java#L245-L246)

```java
public class UITagExample extends ActionSupport {
    File picture;
    String pictureContentType;  // Notice the naming pattern
    String pictureFileName;

    @StrutsParameter
    public void setPicture(File picture) {
        this.picture = picture;
    }

    @StrutsParameter
    public void setPictureContentType(String pictureContentType) {
        this.pictureContentType = pictureContentType;
    }

    @StrutsParameter
    public void setPictureFileName(String pictureFileName) {
        this.pictureFileName = pictureFileName;
    }
}
```

**The Modern Pattern (Struts 6.4.0+):**

Uses `UploadedFilesAware` interface with `UploadedFile` API:

**Interface**: [core/src/main/java/org/apache/struts2/action/UploadedFilesAware.java](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/core/src/main/java/org/apache/struts2/action/UploadedFilesAware.java)

**Example**: [apps/showcase/src/main/java/org/apache/struts2/showcase/fileupload/FileUploadAction.java](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/apps/showcase/src/main/java/org/apache/struts2/showcase/fileupload/FileUploadAction.java)

```java
public class FileUploadAction extends ActionSupport implements UploadedFilesAware {
    private UploadedFile uploadedFile;
    private String contentType;
    private String fileName;
    private String originalName;

    @Override
    public void withUploadedFiles(List<UploadedFile> uploadedFiles) {
        this.uploadedFile = uploadedFiles.get(0);
        this.contentType = uploadedFile.getContentType();
        this.fileName = uploadedFile.getName();
        this.originalName = uploadedFile.getOriginalName();
    }

    public String execute() {
        // Programmatic validation possible here
        if (contentType != null && !contentType.equals("application/pdf")) {
            addFieldError("upload", "Only PDF files are allowed");
            return ERROR;
        }
        return SUCCESS;
    }
}
```

### 3. File Upload Validation Architecture

**Core Interceptor**: [core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java#L105-167](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java#L105-L167)

The `acceptFile()` method provides validation:

```java
protected boolean acceptFile(Object action, UploadedFile file, String originalFilename,
                             String contentType, String inputName) {
    Set<String> errorMessages = new HashSet<>();
    ValidationAware validation = null;

    if (action instanceof ValidationAware) {
        validation = (ValidationAware) action;
    }

    // Validation checks:
    // 1. Null file check
    if (file == null || file.getContent() == null) {
        String errMsg = getTextMessage(action, STRUTS_MESSAGES_ERROR_UPLOADING_KEY,
                                       new String[]{inputName});
        if (validation != null) {
            validation.addFieldError(inputName, errMsg);
        }
        return false;
    }

    // 2. File size validation (line 139-143)
    if (maximumSize != null && maximumSize < file.length()) {
        String errMsg = getTextMessage(action, STRUTS_MESSAGES_ERROR_FILE_TOO_LARGE_KEY,
                                       new String[]{inputName, originalFilename, file.getName(),
                                                   "" + file.length(), getMaximumSizeStr(action)});
        errorMessages.add(errMsg);
    }

    // 3. Content type validation (line 144-150)
    if ((!allowedTypesSet.isEmpty()) && (!containsItem(allowedTypesSet, contentType))) {
        String errMsg = getTextMessage(action, STRUTS_MESSAGES_ERROR_CONTENT_TYPE_NOT_ALLOWED_KEY,
                                       new String[]{inputName, originalFilename, file.getName(), contentType});
        errorMessages.add(errMsg);
    }

    // 4. File extension validation (line 151-157)
    if ((!allowedExtensionsSet.isEmpty()) && (!hasAllowedExtension(allowedExtensionsSet, originalFilename))) {
        String errMsg = getTextMessage(action, STRUTS_MESSAGES_ERROR_FILE_EXTENSION_NOT_ALLOWED_KEY,
                                       new String[]{inputName, originalFilename, file.getName(), contentType});
        errorMessages.add(errMsg);
    }

    if (validation != null) {
        for (String errorMsg : errorMessages) {
            validation.addFieldError(inputName, errorMsg);
        }
    }

    return errorMessages.isEmpty();
}
```

**Extension Matching**: [AbstractFileUploadInterceptor.java#L169-181](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java#L169-L181)

```java
private boolean hasAllowedExtension(Collection<String> extensionCollection, String filename) {
    if (filename == null) {
        return false;
    }

    String lowercaseFilename = filename.toLowerCase();
    for (String extension : extensionCollection) {
        if (lowercaseFilename.endsWith(extension)) {
            return true;
        }
    }

    return false;
}
```

**Content Type Matching with Wildcards**: [AbstractFileUploadInterceptor.java#L183-196](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java#L183-L196)

```java
private boolean containsItem(Collection<String> itemCollection, String item) {
    for (String pattern : itemCollection)
        if (matchesWildcard(pattern, item))
            return true;
    return false;
}

private boolean matchesWildcard(String pattern, String text) {
    Object o = matcher.compilePattern(pattern);
    return matcher.match(new HashMap<>(), text, o);
}
```

Supports patterns like `text/*` matching `text/plain`, `text/html`, etc.

### 4. Programmatic Validation Examples

**Multiple File Upload**: [apps/showcase/src/main/java/org/apache/struts2/showcase/fileupload/MultipleFileUploadUsingArrayAction.java](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/apps/showcase/src/main/java/org/apache/struts2/showcase/fileupload/MultipleFileUploadUsingArrayAction.java)

**XML-Based Validation**: [apps/showcase/src/main/resources/org/apache/struts2/showcase/fileupload/FileUploadAction-validation.xml](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/apps/showcase/src/main/resources/org/apache/struts2/showcase/fileupload/FileUploadAction-validation.xml)

```xml
<validators>
    <field name="upload">
        <field-validator type="fieldexpression">
            <param name="expression"><![CDATA[getUploadSize() > 0]]></param>
            <message>File cannot be empty</message>
        </field-validator>
    </field>
</validators>
```

**Test Example**: [core/src/test/java/org/apache/struts2/interceptor/ActionFileUploadInterceptorTest.java](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/core/src/test/java/org/apache/struts2/interceptor/ActionFileUploadInterceptorTest.java)

Shows how interceptor and ValidationAware integration works.

### 5. Alternative: WithLazyParams Interface (Advanced)

**File**: [core/src/main/java/org/apache/struts2/interceptor/WithLazyParams.java#L39-76](https://github.com/apache/struts/blob/06f9f9303387edf0557d128bbd7123bded4f24f5/core/src/main/java/org/apache/struts2/interceptor/WithLazyParams.java#L39-L76)

```java
public interface WithLazyParams {
    class LazyParamInjector {
        public Interceptor injectParams(Interceptor interceptor,
                                       Map<String, String> params,
                                       ActionContext invocationContext) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                // CRITICAL: This DOES evaluate ${...} expressions
                Object paramValue = textParser.evaluate(
                    new char[]{ '$' },
                    entry.getValue(),
                    valueEvaluator,  // Uses ValueStack.findValue()
                    TextParser.DEFAULT_LOOP_COUNT
                );
                ognlUtil.setProperty(entry.getKey(), paramValue, interceptor,
                                   invocationContext.getContextMap());
            }
            return interceptor;
        }
    }
}
```

**How it works:**
- Interceptors implementing `WithLazyParams` skip parameter setting during initialization
- Parameters are injected **per-request** during action invocation
- `textParser.evaluate()` resolves `${...}` expressions against ValueStack
- Happens in `DefaultActionInvocation.invoke()`

**Limitations:**
- Per-request evaluation overhead
- Requires custom interceptor implementation
- More complex than programmatic validation

## Code References

### Core Files
- `core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java:78` - `setAllowedExtensions()` setter
- `core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java:85` - `setAllowedTypes()` setter
- `core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java:105-167` - `acceptFile()` validation logic
- `core/src/main/java/org/apache/struts2/interceptor/ActionFileUploadInterceptor.java` - Concrete implementation
- `core/src/main/java/org/apache/struts2/action/UploadedFilesAware.java` - Modern interface for file handling
- `core/src/main/java/org/apache/struts2/util/TextParseUtil.java:256` - `commaDelimitedStringToSet()` method
- `core/src/main/java/org/apache/struts2/config/providers/XmlHelper.java:73-95` - XML parameter parsing
- `core/src/main/java/org/apache/struts2/factory/DefaultInterceptorFactory.java:54-81` - Interceptor instantiation
- `core/src/main/java/org/apache/struts2/interceptor/WithLazyParams.java:39-76` - Lazy parameter evaluation

### Example Files
- `apps/showcase/src/main/java/org/apache/struts2/showcase/fileupload/FileUploadAction.java` - Modern UploadedFilesAware example
- `apps/showcase/src/main/java/org/apache/struts2/showcase/fileupload/MultipleFileUploadUsingArrayAction.java` - Multiple file upload
- `apps/showcase/src/main/java/org/apache/struts2/showcase/fileupload/MultipleFileUploadUsingListAction.java` - List-based upload
- `apps/showcase/src/main/java/org/apache/struts2/showcase/UITagExample.java:245-246` - Old setXContentType pattern
- `apps/showcase/src/main/resources/org/apache/struts2/showcase/fileupload/FileUploadAction-validation.xml` - XML validation example

### Test Files
- `core/src/test/java/org/apache/struts2/interceptor/ActionFileUploadInterceptorTest.java` - Validation integration tests

## Architecture Insights

### 1. Parameter Processing Timeline

| Phase | When | OGNL Evaluation | ValueStack Available | Can Use ${...} |
|-------|------|-----------------|---------------------|----------------|
| **XML Parsing** | Startup | ❌ No | ❌ No | ❌ No |
| **Interceptor Init** | Startup | ⚠️ Property names only | ❌ No | ❌ No |
| **Setter Methods** | Startup | ❌ No | ❌ No | ❌ No |
| **WithLazyParams** | Per-request | ✅ Yes | ✅ Yes | ✅ Yes |
| **Request Processing** | Per-request | ✅ Yes | ✅ Yes | ✅ Yes |
| **Action validate()** | Per-request | ✅ Yes | ✅ Yes | ✅ Yes |

### 2. Validation Layers

**Layer 1: Interceptor (Pre-Action)**
- Configured via struts.xml parameters
- Executes before action
- Adds field errors to ValidationAware
- Cannot access action properties

**Layer 2: Action validate() Method**
- Executes after interceptor
- Full access to UploadedFile objects
- Can implement dynamic business logic
- Can load configuration from database/properties

**Layer 3: XML Validation**
- Declarative validation rules
- Can reference action methods via expressions
- Executes after validate() method

### 3. Security Features

1. **Case-insensitive extension matching** - Prevents bypassing via uppercase extensions
2. **Wildcard support for content types** - Allows flexible type matching (e.g., `text/*`)
3. **Multiple validation layers** - Defense in depth
4. **Integration with ValidationAware** - Consistent error handling
5. **Null safety** - Handles null files and content appropriately

## Recommended Solution

### Complete Implementation Example

```java
package com.example;

import org.apache.struts2.ActionSupport;
import org.apache.struts2.action.UploadedFilesAware;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DynamicFileUploadAction extends ActionSupport implements UploadedFilesAware {

    private UploadedFile uploadedFile;
    private String contentType;
    private String originalName;

    // Dynamic configuration - shared with batch import
    private Set<String> acceptedFileTypes;
    private Set<String> acceptedFileExtensions;

    public DynamicFileUploadAction() {
        // Load dynamic configuration
        // This matches your batch import function's configuration
        acceptedFileTypes = FileUploadConfig.getAcceptedTypes();
        acceptedFileExtensions = FileUploadConfig.getAcceptedExtensions();
    }

    @Override
    public void withUploadedFiles(List<UploadedFile> uploadedFiles) {
        if (!uploadedFiles.isEmpty()) {
            this.uploadedFile = uploadedFiles.get(0);
            this.contentType = uploadedFile.getContentType();
            this.originalName = uploadedFile.getOriginalName();
        }
    }

    @Override
    public void validate() {
        if (uploadedFile == null) {
            addFieldError("upload", "Please select a file to upload");
            return;
        }

        // Validate content type
        if (!isValidContentType(contentType)) {
            addFieldError("upload",
                "File type not allowed: " + contentType +
                ". Allowed types: " + acceptedFileTypes);
        }

        // Validate file extension
        if (!hasValidExtension(originalName)) {
            addFieldError("upload",
                "File extension not allowed. Allowed extensions: " + acceptedFileExtensions);
        }

        // Additional validation
        if (uploadedFile.length() == 0) {
            addFieldError("upload", "File cannot be empty");
        }
    }

    private boolean isValidContentType(String contentType) {
        if (contentType == null || acceptedFileTypes.isEmpty()) {
            return false;
        }

        // Support wildcard matching (e.g., "image/*")
        for (String allowedType : acceptedFileTypes) {
            if (matchesWildcard(allowedType, contentType)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasValidExtension(String filename) {
        if (filename == null || acceptedFileExtensions.isEmpty()) {
            return false;
        }

        String lowerFilename = filename.toLowerCase();
        for (String extension : acceptedFileExtensions) {
            if (lowerFilename.endsWith(extension.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesWildcard(String pattern, String text) {
        if (pattern.contains("*")) {
            String prefix = pattern.substring(0, pattern.indexOf("*"));
            return text.startsWith(prefix);
        }
        return pattern.equals(text);
    }

    public String execute() {
        // Process the uploaded file
        // uploadedFile.getContent() gives you InputStream
        return SUCCESS;
    }

    // Getters for JSP access
    public String getContentType() { return contentType; }
    public String getOriginalName() { return originalName; }
}
```

### Shared Configuration Class

```java
public class FileUploadConfig {
    private static final Set<String> ACCEPTED_TYPES;
    private static final Set<String> ACCEPTED_EXTENSIONS;

    static {
        // Load from properties file or database
        Properties props = loadProperties("file-upload-config.properties");
        ACCEPTED_TYPES = parseCommaSeparated(props.getProperty("accepted.types"));
        ACCEPTED_EXTENSIONS = parseCommaSeparated(props.getProperty("accepted.extensions"));
    }

    public static Set<String> getAcceptedTypes() {
        return new HashSet<>(ACCEPTED_TYPES);
    }

    public static Set<String> getAcceptedExtensions() {
        return new HashSet<>(ACCEPTED_EXTENSIONS);
    }

    private static Set<String> parseCommaSeparated(String value) {
        return Arrays.stream(value.split(","))
            .map(String::trim)
            .collect(Collectors.toSet());
    }

    private static Properties loadProperties(String filename) {
        Properties props = new Properties();
        try (InputStream is = FileUploadConfig.class.getClassLoader()
                .getResourceAsStream(filename)) {
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
        return props;
    }
}
```

### struts.xml Configuration

```xml
<action name="dynamicUpload" class="com.example.DynamicFileUploadAction">
    <interceptor-ref name="actionFileUpload">
        <!-- Basic size limit only - types/extensions validated in action -->
        <param name="maximumSize">5242880</param> <!-- 5MB -->
    </interceptor-ref>
    <interceptor-ref name="basicStack"/>
    <result name="success">upload-success.jsp</result>
    <result name="input">upload-form.jsp</result>
</action>
```

### Configuration Properties File

```properties
# file-upload-config.properties
# Shared between Struts upload and batch import
accepted.types=application/pdf,image/jpeg,image/png
accepted.extensions=.pdf,.jpg,.jpeg,.png
```

## Benefits of Recommended Solution

✅ **No custom interceptor needed** - Uses standard Struts patterns
✅ **Dynamic configuration loading** - Can change without recompilation
✅ **Shared config between Struts and batch import** - Single source of truth
✅ **Full control over validation logic** - Can add business-specific rules
✅ **Standard validation error handling** - Integrates with Struts validation
✅ **Wildcard support** - Same pattern matching as interceptor
✅ **Case-insensitive extension matching** - Consistent with Struts security practices

## Related Research

- File upload security patterns in Apache Struts
- Interceptor lifecycle and parameter injection
- OGNL expression evaluation contexts
- Struts validation framework integration

## Open Questions

1. **Performance consideration**: Should configuration be cached vs loaded per-action instance?
2. **Configuration source**: Database vs properties file vs external service?
3. **Validation error messages**: Should they be internationalized (i18n)?
4. **Batch import integration**: Should validation logic be extracted to a shared service class?
5. **Multiple file handling**: Should validation apply to all files or per-file basis?

## Alternative Approaches Considered

### Option A: WithLazyParams Custom Interceptor
**Pros:** Enables `${...}` evaluation
**Cons:** Requires custom interceptor, per-request overhead, more complex

### Option B: Struts Constants
**Pros:** Simple, no code changes
**Cons:** Not truly dynamic, requires restart to change

### Option C: Custom Validator
**Pros:** Reusable across actions
**Cons:** More complex setup, still requires configuration loading

**Conclusion:** Programmatic validation in action provides the best balance of flexibility, simplicity, and maintainability for this use case.
