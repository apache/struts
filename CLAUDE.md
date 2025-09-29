# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

This document outlines essential practices for working with Claude Code on the Apache Struts project. For detailed procedures, use the specialized agents and commands available in `.claude/agents/` and `.claude/commands/`.

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

# Run tests (use /run_tests command for intelligent test execution)
mvn test -DskipAssembly

# Build without running tests
mvn clean install -DskipTests
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

**For comprehensive security analysis, use:** `/security_scan`

### Security Implementation Patterns
```java
// GOOD: Secure temporary file creation
protected File createTemporaryFile(String fileName, Path location) {
    String uid = UUID.randomUUID().toString().replace("-", "_");
    File file = location.resolve("upload_" + uid + ".tmp").toFile();
    LOG.debug("Creating temporary file: {} (originally: {})", file.getName(), fileName);
    return file;
}
```

## Testing Implementation

**For intelligent test execution and analysis, use:** `/run_tests`

### Test Structure & Coverage
- **Unit Tests**: Test individual methods with mocked dependencies
- **Integration Tests**: Test complete workflows with real file I/O
- **Security Tests**: Verify directory traversal prevention, secure naming
- **Error Handling Tests**: Test exception scenarios and error reporting
- **Cleanup Tests**: Verify resource cleanup and tracking

### Basic Testing Command
```bash
# Run all tests (use -DskipAssembly to avoid building docs/examples)
mvn test -DskipAssembly
```

## Documentation Standards

**For comprehensive documentation quality analysis, use:** `/quality_check`

### Documentation Requirements
- **Always document security implications** in methods handling files/user input
- **Include usage examples** for complex methods and classes
- **Document exception conditions** and error handling behavior
- **Reference related methods** using `@see` tags
- **Explain resource management** responsibilities

## Error Handling & Logging

### Logging Best Practices
- Use parameterized logging for performance: `LOG.debug("Processing: {}", value)`
- Log security-relevant operations appropriately
- Use appropriate log levels (debug/info/warn/error)
- Avoid logging sensitive information

## Code Quality Standards

**For comprehensive code quality analysis, use:** `/quality_check`

### Key Principles
- Use `protected` for methods that subclasses might override
- Catch specific exceptions rather than generic `Exception`
- Use clear, descriptive method and variable names
- Follow existing project conventions and patterns

## Available Automated Tools

### Commands
- `/security_scan` - Comprehensive security analysis
- `/run_tests` - Intelligent test execution and analysis
- `/quality_check` - Code quality and documentation analysis
- `/config_analyze` - Configuration validation and optimization
- `/create_plan` - Implementation planning assistance
- `/validate_plan` - Plan validation and verification
- `/commit` - Guided git commit creation
- `/research_codebase` - Comprehensive codebase research

### Specialized Agents
- `security-analyzer` - OGNL injection scanning, CVE detection
- `test-runner` - Maven test execution and coverage analysis
- `code-quality-checker` - JavaDoc compliance, pattern consistency
- `config-validator` - struts.xml validation, interceptor analysis
- `jakarta-migration-helper` - Jakarta EE migration assistance
- `codebase-analyzer` - Project structure and architecture analysis
- `codebase-locator` - Code and file location assistance
- `codebase-pattern-finder` - Pattern examples and usage

## Common Pitfalls to Avoid

1. **File Security**: Never use `File.createTempFile()` without directory control
2. **Resource Leaks**: Always track and clean up temporary files
3. **Test Coverage**: Don't forget to test error conditions and cleanup
4. **Documentation**: Always document security implications
5. **Exception Handling**: Don't let cleanup failures affect main operations
6. **Path Validation**: Always validate and sanitize file paths