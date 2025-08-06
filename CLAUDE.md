# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Apache Struts 2 Framework (Version 6.7.x)

This is the Apache Struts 2 web framework, a free open-source solution for creating Java web applications. The codebase is a multi-module Maven project with comprehensive plugin architecture.

## Build System & Common Commands

### Maven Commands
```bash
# Build entire project
./mvnw clean install

# Build without running tests
./mvnw clean install -DskipTests

# Build without assembly
./mvnw clean install -DskipAssembly

# Run all tests
./mvnw clean test

# Run tests with coverage
./mvnw clean verify -Pcoverage -DskipAssembly

# Run integration tests
./mvnw clean verify -DskipAssembly

# Test specific module
./mvnw -pl core clean test
./mvnw -pl plugins/spring clean test

# Check for security vulnerabilities
./mvnw clean verify -Pdependency-check

# Run Apache RAT license check
./mvnw clean prepare-package
```

### Test Framework
- **Primary**: JUnit 4.13.2 with Maven Surefire Plugin 3.5.1
- **Pattern**: `**/*Test.java` (excludes `**/TestBean.java`)
- **Coverage**: JaCoCo 0.8.12
- **Additional**: Mockito, EasyMock, AssertJ, Spring Test
- **Integration**: Maven Failsafe Plugin with Jetty on port 8090

## Project Architecture

### Core Structure
```
struts6/
├── core/                    # Core Struts 2 framework (main dependency)
├── plugins/                 # Plugin modules
│   ├── spring/             # Spring integration
│   ├── json/               # JSON support
│   ├── tiles/              # Apache Tiles integration
│   ├── velocity/           # Velocity template engine
│   └── [20+ other plugins]
├── apps/                   # Sample applications
│   ├── showcase/           # Feature demonstration app
│   └── rest-showcase/      # REST API examples
├── bundles/                # OSGi bundles
├── bom/                    # Bill of Materials
└── assembly/               # Distribution packaging
```

### Key Technologies
- **Java**: Minimum JDK 8, supports up to JDK 21
- **Servlet API**: 3.1+ required
- **Expression Language**: OGNL 3.3.5
- **Dependency Injection**: Custom container (`com.opensymphony.xwork2.inject`)
- **Templating**: FreeMarker 2.3.33 (default), Velocity, JSP
- **Logging**: SLF4J 2.0.16 with Log4j2 2.24.1
- **Build**: Maven with wrapper (3.9.6)

### Core Components Architecture

#### Action Framework (MVC Pattern)
- **Actions**: Located in `core/src/main/java/org/apache/struts2/action/`
- **Action Support**: `ActionSupport` base class with validation and i18n
- **Action Context**: `ActionContext` provides access to servlet objects
- **Action Invocation**: `DefaultActionInvocation` handles action execution

#### Configuration System
- **XML-based**: Primary configuration via `struts.xml` files
- **Annotation-based**: Convention plugin for zero-config approach  
- **Java-based**: `StrutsJavaConfiguration` for programmatic setup
- **Property files**: `struts.properties` for framework settings

#### Interceptor Chain
- **Framework Core**: All requests processed through interceptor chain
- **Built-in Interceptors**: 20+ interceptors in `org.apache.struts2.interceptor`
- **Validation**: `ValidationInterceptor` with annotation support
- **File Upload**: `FileUploadInterceptor` with security controls
- **Parameters**: `ParametersInterceptor` with OGNL expression handling

#### Result Framework  
- **Result Types**: JSP, FreeMarker, Redirect, Stream, JSON, etc.
- **Chaining**: `ActionChainResult` for action-to-action calls
- **Templates**: Pluggable result renderers

#### Value Stack (OGNL Integration)
- **Expression Language**: OGNL for property access and method calls
- **Security**: `SecurityMemberAccess` prevents dangerous operations
- **Performance**: Caffeine-based expression caching
- **Context**: CompoundRoot provides hierarchical value resolution

### Plugin Architecture
Each plugin is a separate Maven module with:
- **Plugin Descriptor**: `struts-plugin.xml` defines beans and configuration
- **Dependency Isolation**: Separate classloaders for plugin resources
- **Extension Points**: Configurable via dependency injection
- **Popular Plugins**: Spring (DI), JSON (REST), Tiles (Layout), Bean Validation (JSR-303)

### Security Architecture
- **OGNL Security**: Restricted method access and class loading
- **CSRF Protection**: Token-based with `TokenInterceptor`
- **File Upload Security**: Type and size restrictions  
- **Content Security Policy**: Built-in CSP support
- **Input Validation**: Server-side validation framework
- **Pattern Matching**: Configurable allowed/excluded patterns

## Development Guidelines

### Code Organization
- **Package Structure**: Follow existing `org.apache.struts2.*` hierarchy
- **Naming Conventions**: Use Struts conventions (Actions end with `Action`)
- **Configuration**: Prefer XML configuration in `struts.xml` for complex setups
- **Testing**: Each module has comprehensive unit and integration tests

### Plugin Development
```java
// Plugin descriptor example (struts-plugin.xml)
<bean type="com.opensymphony.xwork2.ObjectFactory" 
      name="myObjectFactory" 
      class="com.example.MyObjectFactory" />
```

### Common Patterns
- **Action Implementation**: Extend `ActionSupport` or implement `Action`
- **Result Mapping**: Use result configuration in `struts.xml`
- **Interceptor Development**: Extend `AbstractInterceptor`
- **Type Conversion**: Implement `TypeConverter` for custom types
- **Validation**: Use validation XML or annotations

### Important Notes
- **Version**: Currently 6.7.5-SNAPSHOT (release branch: `release/struts-6-7-x`)
- **Java Compatibility**: Compiled for Java 8, tested through Java 21
- **Security**: Always validate inputs and follow OWASP guidelines
- **Performance**: Leverage built-in caching (OGNL expressions, templates)
- **Deprecation**: Some legacy XWork components marked for removal

### Build Profiles
- **coverage**: Enables JaCoCo coverage reporting
- **dependency-check**: OWASP dependency vulnerability scanning  
- **jdk17**: Special configuration for Java 17+ module system

This is a mature, enterprise-grade framework with extensive documentation at https://struts.apache.org/ and active community support through Apache mailing lists and JIRA (project WW).