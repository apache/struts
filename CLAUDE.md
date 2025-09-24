# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

This document outlines essential practices for working with Claude Code on the Apache Struts project, based on security improvements and testing implementations.

## Project Overview

Apache Struts is a mature MVC web application framework for Java, originally based on WebWork 2. The project follows a modular architecture with clear separation between core framework, plugins, and applications.

### Build System & Environment
- **Build Tool**: Maven with multi-module structure
- **Java Version**: Java 17+
- **Testing**: JUnit 5 with AssertJ assertions
- **IDE Support**: IntelliJ IDEA with project-specific configurations

### Key Build Commands
```bash
# Full build with tests
mvn clean install

# Run tests only (skip assembly to avoid docs/examples ZIP creation)
mvn test -DskipAssembly

# Run specific test class
mvn test -Dtest=ClassName

# Run tests matching pattern
mvn test -Dtest=*Pattern*Test

# Build without running tests
mvn clean install -DskipTests

# Generate site documentation
mvn site
```

### Project Structure
```
struts/
├── core/           # Core framework (struts2-core)
├── plugins/        # Plugin modules (tiles, json, etc.)
├── apps/          # Sample applications (showcase, rest-showcase)
├── assembly/      # Distribution packaging
├── bom/           # Bill of Materials for dependency management
├── parent/        # Parent POM with shared configuration
└── jakarta/       # Jakarta EE compatibility modules
```

### Core Architecture Components

#### MVC Framework Components
- **ActionSupport**: Base class for actions with validation and internationalization
- **ActionContext**: Thread-local context holding request/response data
- **ActionProxy/ActionInvocation**: Handles action execution lifecycle
- **Dispatcher**: Core request dispatcher and framework initialization
- **Interceptors**: Cross-cutting concerns (validation, file upload, security)

#### Key Packages
- `org.apache.struts2.dispatcher`: Request handling and context management
- `org.apache.struts2.interceptor`: Interceptor implementations
- `org.apache.struts2.components`: UI component system
- `org.apache.struts2.views`: View technologies (JSP, FreeMarker, Velocity)
- `org.apache.struts2.security`: Security-related utilities

### Technology Stack
- **Jakarta EE**: Servlet API, JSP, JSTL
- **Core Libraries**: OGNL (expression language), Commons FileUpload2, Log4j2
- **Template Engines**: FreeMarker, Velocity (via plugins)
- **Build Dependencies**: Maven, various plugins for assembly and site generation

## Security-First Development

### Critical Security Principles
1. **Never create files in system temp directories** - always use controlled application directories
2. **Use UUID-based naming** for temporary files to prevent collisions and path traversal
3. **Implement proper resource cleanup** with try-with-resources and finally blocks
4. **Track all temporary resources** for explicit cleanup (security critical)
5. **Validate all user inputs** and sanitize filenames before processing

### Security Implementation Patterns
```java
// GOOD: Secure temporary file creation
protected File createTemporaryFile(String fileName, Path location) {
    String uid = UUID.randomUUID().toString().replace("-", "_");
    File file = location.resolve("upload_" + uid + ".tmp").toFile();
    LOG.debug("Creating temporary file: {} (originally: {})", file.getName(), fileName);
    return file;
}

// BAD: Insecure system temp usage
File tempFile = File.createTempFile("struts_upload_", "_" + item.getName());
```

### Resource Management
- Always use tracking collections for cleanup: `List<File> temporaryFiles`, `List<DiskFileItem> diskFileItems`
- Implement protected cleanup methods for extensibility
- Make cleanup idempotent and exception-safe
- Use try-with-resources for streams and I/O operations

## Testing Implementation

### Test Structure & Coverage
- **Unit Tests**: Test individual methods with mocked dependencies
- **Integration Tests**: Test complete workflows with real file I/O
- **Security Tests**: Verify directory traversal prevention, secure naming
- **Error Handling Tests**: Test exception scenarios and error reporting
- **Cleanup Tests**: Verify resource cleanup and tracking

### Testing Commands
```bash
# Run all tests
mvn test -DskipAssembly

# Run specific test class
mvn test -Dtest=JakartaMultiPartRequestTest

# Run tests with specific method pattern
mvn test -Dtest=*MultiPartRequestTest#temporal*
```

use `-DskipAssembly` to avoid building zip files with docs, examples, etc.

### Test Implementation Patterns
```java
@Test
public void securityTestExample() throws Exception {
    // given - malicious input
    String maliciousFilename = "malicious../../../etc/passwd";
    
    // when - process input
    processFile(maliciousFilename);
    
    // then - verify security measures
    assertThat(tempFile.getParent()).isEqualTo(saveDir);
    assertThat(tempFile.getName()).doesNotContain("..");
}
```

### Reflection-Based Testing for Private Members
```java
Field privateField = ClassName.class.getDeclaredField("fieldName");
privateField.setAccessible(true);
@SuppressWarnings("unchecked")
List<Type> values = (List<Type>) privateField.get(instance);
```

## JavaDoc Documentation Standards

### Class-Level Documentation
```java
/**
 * Brief description of the class purpose and functionality.
 * 
 * <p>Detailed description with multiple paragraphs explaining:</p>
 * <ul>
 *   <li>Key features and capabilities</li>
 *   <li>Security considerations</li>
 *   <li>Resource management approach</li>
 *   <li>Usage patterns and examples</li>
 * </ul>
 * 
 * <p>Usage example:</p>
 * <pre>
 * ClassName instance = new ClassName();
 * try {
 *     instance.process(data);
 * } finally {
 *     instance.cleanUp(); // Always clean up resources
 * }
 * </pre>
 * 
 * @see RelatedClass
 * @see org.apache.package.ImportantInterface
 */
```

### Method-Level Documentation
```java
/**
 * Brief description of what the method does.
 * 
 * <p>Detailed description explaining:</p>
 * <ol>
 *   <li>Step-by-step process</li>
 *   <li>Security considerations</li>
 *   <li>Error handling behavior</li>
 *   <li>Resource management</li>
 * </ol>
 * 
 * <p>Security note: This method creates files in controlled directory
 * to prevent security vulnerabilities.</p>
 * 
 * @param paramName description of parameter and constraints
 * @param saveDir the directory where files will be created (must exist)
 * @return description of return value
 * @throws IOException if file creation fails or I/O error occurs
 * @see #relatedMethod(Type)
 * @see #cleanUpMethod()
 */
```

### Documentation Best Practices
- **Always document security implications** in methods handling files/user input
- **Include usage examples** for complex methods and classes
- **Document exception conditions** and error handling behavior
- **Reference related methods** using `@see` tags
- **Explain resource management** responsibilities
- **Use `<p>`, `<ol>`, `<ul>`, `<li>` for structured content
- **Include `<pre>` blocks** for code examples

## Error Handling & Logging

### Error Message Patterns
```java
// Localized error messages
LocalizedMessage errorMessage = buildErrorMessage(
    e.getClass(), 
    e.getMessage(), 
    new Object[]{fileName}
);
if (!errors.contains(errorMessage)) {
    errors.add(errorMessage);
}
```

### Logging Best Practices
```java
// Use parameterized logging for performance
LOG.debug("Processing file: {} in directory: {}", 
          normalizeSpace(fileName), saveDir);

// Log security-relevant operations
LOG.warn("Failed to delete temporary file: {}", tempFile.getAbsolutePath());

// Use appropriate log levels
LOG.debug() // Development details
LOG.info()  // General information
LOG.warn()  // Potential issues
LOG.error() // Serious problems
```

## Code Quality Standards

### Method Scope & Extensibility
- Use `protected` for methods that subclasses might override
- Implement cleanup methods as separate `protected` methods
- Make core functionality extensible while maintaining security

### Exception Handling
- Catch specific exceptions rather than generic `Exception`
- Log exceptions with context but continue cleanup operations
- Use try-finally blocks to ensure cleanup always occurs

### Code Organization
- Group related methods together (processing, cleanup, utilities)
- Keep security-critical code in dedicated methods
- Use clear, descriptive method and variable names
- Follow existing project conventions and patterns

## Common Pitfalls to Avoid

1. **File Security**: Never use `File.createTempFile()` without directory control
2. **Resource Leaks**: Always track and clean up temporary files
3. **Test Coverage**: Don't forget to test error conditions and cleanup
4. **Documentation**: Always document security implications
5. **Exception Handling**: Don't let cleanup failures affect main operations
6. **Path Validation**: Always validate and sanitize file paths
7. **Reflection Testing**: Use `@SuppressWarnings("unchecked")` appropriately

This document should be updated as new patterns and practices emerge during development.