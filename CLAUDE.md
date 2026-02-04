# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

For detailed procedures, use the specialized agents and commands in `.claude/agents/` and `.claude/commands/`.

## Project Overview

Apache Struts is a mature MVC web application framework for Java (originally WebWork 2). Current version: *
*7.2.0-SNAPSHOT**.

### Build Commands

```bash
# Full build with tests
mvn clean install

# Run all tests (faster, skips assembly)
mvn test -DskipAssembly

# Run single test class
mvn test -DskipAssembly -Dtest=MyClassTest

# Run single test method
mvn test -DskipAssembly -Dtest=MyClassTest#testMethodName

# Run tests in a specific module
mvn test -DskipAssembly -pl core

# Build without tests
mvn clean install -DskipTests

# Build with code coverage (JaCoCo)
mvn clean install -Pcoverage

# Build with Jakarta EE 11 (Spring 7)
mvn clean install -Pjakartaee11

# Run OWASP dependency vulnerability check
mvn verify -Pdependency-check
```

### Project Structure

```
struts/
├── core/           # struts2-core - main framework
├── plugins/        # Plugin modules (json, rest, spring, tiles, velocity, etc.)
├── apps/           # Sample applications (showcase, rest-showcase)
├── assembly/       # Distribution packaging
├── bom/            # Bill of Materials for dependency management
├── parent/         # Parent POM with shared configuration
└── jakarta/        # Jakarta EE compatibility modules
```

### Core Architecture

**Request Lifecycle**: `Dispatcher` → `ActionProxy` → `ActionInvocation` → Interceptor stack → `Action` → Result

Key components:

- **ActionSupport**: Base class for actions (validation, i18n, messages)
- **ActionContext**: Thread-local context with request/response/session data
- **Interceptors**: Cross-cutting concerns (validation, file upload, security, params)
- **Results**: Response handlers (dispatcher, redirect, json, stream)

Key packages in `org.apache.struts2`:

- `dispatcher` - Request handling, `Dispatcher`, servlet integration
- `interceptor` - Built-in interceptors (params, validation, fileUpload)
- `components` - UI tag components (form, textfield, submit)
- `action` - Action interfaces (`UploadedFilesAware`, `SessionAware`, etc.)
- `security` - Security utilities and OGNL member access policies

### Technology Stack

- **Java 17+** with Jakarta EE 10 (Servlet 6.0, JSP 3.1)
- **OGNL** - Expression language for value stack access
- **FreeMarker** - Default template engine for UI tags
- **Commons FileUpload2** - File upload handling
- **Log4j2/SLF4J** - Logging

## Security-Critical Patterns

Apache Struts has a history of security vulnerabilities. Follow these strictly:

1. **Temporary files**: Never use system temp directory; use UUID-based names in controlled locations
2. **OGNL expressions**: Never evaluate user-controlled OGNL; use allowlist member access
3. **File uploads**: Validate content types, sanitize filenames, enforce size limits
4. **Parameter injection**: Use `ParameterNameAware` to filter dangerous parameter names

```java
// Secure temporary file pattern
protected File createTemporaryFile(String fileName, Path location) {
    String uid = UUID.randomUUID().toString().replace("-", "_");
    return location.resolve("upload_" + uid + ".tmp").toFile();
}
```

Run `/security_scan` for comprehensive security analysis.

## Testing

**Priority order for running tests:**

1. **JetBrains MCP** (in IntelliJ): `mcp__jetbrains__execute_run_configuration`
2. **test-runner agent**: `Task` tool with `subagent_type="test-runner"`
3. **Direct Maven**: `mvn test -DskipAssembly -Dtest=TestClassName`

Tests use JUnit 5 with AssertJ assertions and Mockito for mocking.

## Available Tools

### Commands

- `/security_scan` - OGNL injection, CVE detection, security analysis
- `/quality_check` - JavaDoc compliance, coding standards
- `/config_analyze` - struts.xml validation, interceptor analysis
- `/create_plan` / `/validate_plan` - Implementation planning
- `/research_codebase` - Codebase exploration

### Specialized Agents

- `test-runner` - Maven test execution (use this to RUN tests)
- `security-analyzer` - Security vulnerability scanning
- `codebase-locator` - Find files, classes, implementations
- `codebase-pattern-finder` - Find similar code patterns
- `config-validator` - Validate Struts configuration files

## Pull Requests

- **Title format**: `WW-XXXX Description` (Jira ticket ID required)
- **Link ticket in description**: `Fixes [WW-XXXX](https://issues.apache.org/jira/browse/WW-XXXX)`
- **Issue tracker**: https://issues.apache.org/jira/projects/WW

## Common Pitfalls

1. Never use `File.createTempFile()` without controlling the directory
2. Always clean up temporary files (track and delete in finally blocks)
3. Test error paths and cleanup behavior, not just happy paths
4. Don't catch generic `Exception` - catch specific types
5. Use `protected` visibility for methods subclasses may override