---
date: 2025-12-02T18:33:08+01:00
topic: "WW-5593: Convention plugin fails with NoClassDefFoundError"
tags: [research, codebase, convention-plugin, exception-handling, bug-analysis, WW-5593]
status: complete
jira_ticket: WW-5593
related_tickets: [WW-5594]
---

# Research: WW-5593 - Convention Plugin NoClassDefFoundError Handling

**Date**: 2025-12-02T18:33:08+01:00
**JIRA**: [WW-5593](https://issues.apache.org/jira/browse/WW-5593)
**Related**: [WW-5594](https://issues.apache.org/jira/browse/WW-5594)

## Research Question

Why does the convention plugin fail completely with `NoClassDefFoundError` when scanning classes with missing dependencies, instead of gracefully logging and continuing?

## Summary

The `PackageBasedActionConfigBuilder` class (line 664) only catches `ClassNotFoundException` but not `NoClassDefFoundError` when testing action class candidates during classpath scanning. This causes complete application startup failures instead of graceful degradation when classes have missing optional dependencies.

The issue is triggered when `struts.convention.action.includeJars` is configured, causing the plugin to scan struts2-core.jar which contains `XWorkTestCase` - a test utility class depending on JUnit (an optional dependency).

## Detailed Findings

### 1. The Exception Handling Bug

**Location**: `plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java:662-667`

**Current Code**:
```java
try {
    return inPackage && (nameMatches || (checkImplementsAction && org.apache.struts2.action.Action.class.isAssignableFrom(classInfo.get())));
} catch (ClassNotFoundException ex) {
    LOG.error("Unable to load class [{}]", classInfo.getName(), ex);
    return false;
}
```

**Problem**: Only catches `ClassNotFoundException`, missing `NoClassDefFoundError`.

### 2. Exception Hierarchy

```
java.lang.Throwable
  ├─ java.lang.Exception
  │    └─ java.lang.ClassNotFoundException
  └─ java.lang.Error
       └─ java.lang.LinkageError
            └─ java.lang.NoClassDefFoundError
```

**Key Point**: `NoClassDefFoundError` and `ClassNotFoundException` are siblings under `Throwable`, not parent-child. You cannot catch `NoClassDefFoundError` by catching `ClassNotFoundException`.

**Difference**:
- **ClassNotFoundException**: Thrown when `Class.forName()` or `ClassLoader.loadClass()` cannot find the class definition
- **NoClassDefFoundError**: Thrown when a class was found at compile time but cannot be loaded at runtime due to:
  - Missing dependencies (JAR files)
  - Class initialization failures
  - Static initializer blocks throw exceptions
  - Incompatible class versions
  - Classloader isolation issues

### 3. Where NoClassDefFoundError Originates

**Location**: `core/src/main/java/org/apache/struts2/util/finder/ClassFinder.java:224-235`

```java
public Class<?> get() throws ClassNotFoundException {
    if (clazz != null) return clazz;
    if (notFound != null) throw notFound;
    try {
        this.clazz = classFinder.getClassLoaderInterface().loadClass(name);
        return clazz;
    } catch (ClassNotFoundException notFound) {
        classFinder.getClassesNotLoaded().add(name);
        this.notFound = notFound;
        throw notFound;
    }
}
```

**Analysis**:
- The method signature declares `throws ClassNotFoundException`
- The catch block only handles `ClassNotFoundException`
- However, `ClassLoader.loadClass()` can also throw `NoClassDefFoundError` when class dependencies are missing
- This `NoClassDefFoundError` propagates uncaught through `ClassInfo.get()`

### 4. Call Stack During Failure

From the email thread error log:

```
ERROR [org.apache.struts2.convention.DefaultClassFinder] (default task-1)
Error loading class [org.apache.struts2.XWorkTestCase]:
java.lang.NoClassDefFoundError: Failed to link org/apache/struts2/XWorkTestCase
(Module "deployment.coreweb.war" from Service Module Loader): junit/framework/TestCase
    at java.base/java.lang.ClassLoader.defineClass1(Native Method)
    at java.base/java.lang.ClassLoader.defineClass(ClassLoader.java:1017)
    ...
    at deployment.coreweb.war//org.apache.struts2.util.finder.ClassFinder$ClassInfo.get(ClassFinder.java:228)
    at deployment.coreweb.war//org.apache.struts2.convention.PackageBasedActionConfigBuilder$1.test(PackageBasedActionConfigBuilder.java:6XX)
    at deployment.coreweb.war//org.apache.struts2.convention.DefaultClassFinder.findClasses(DefaultClassFinder.java:280)
```

The error occurs when:
1. Convention plugin scans for action classes
2. Finds `org.apache.struts2.XWorkTestCase` as a candidate
3. Calls `classInfo.get()` to load the class
4. ClassLoader attempts to link the class and load `junit.framework.TestCase`
5. JUnit is not available at runtime (optional dependency)
6. `NoClassDefFoundError` is thrown
7. **Not caught** by `PackageBasedActionConfigBuilder` catch block
8. Application startup fails completely

### 5. Correct Exception Handling Patterns in Struts

The Struts codebase already uses correct patterns in other locations:

#### Pattern A: Catch Throwable (Most Defensive)

**Location**: `plugins/convention/src/main/java/org/apache/struts2/convention/DefaultClassFinder.java:283-286`

```java
} catch (Throwable e) {
    LOG.error("Error loading class [{}]", classInfo.getName(), e);
    classesNotLoaded.add(classInfo.getName());
}
```

**Also used at**: Lines 248-251, 267-270, 297-300

**Advantage**: Catches all exceptions and errors, including:
- `ClassNotFoundException`
- `NoClassDefFoundError`
- `LinkageError`
- `ExceptionInInitializerError`
- Any other unexpected errors

#### Pattern B: Multi-Catch with Specific Exceptions

**Location**: `core/src/main/java/org/apache/struts2/config/providers/XmlDocConfigurationProvider.java:580-582`

```java
} catch (ClassNotFoundException | NoClassDefFoundError e) {
    throw new ConfigurationException("Result class [" + className + "] not found", e, loc);
}
```

**Location**: `core/src/main/java/org/apache/struts2/factory/DefaultInterceptorFactory.java:91-93`

```java
} catch (NoClassDefFoundError e) {
    cause = e;
    message = "Could not load class " + interceptorClassName + ". Perhaps it exists but certain dependencies are not available?";
}
```

**Advantage**: Explicit about what exceptions are expected, provides better error messages

#### Pattern C: Insufficient (Current Bug)

**Location**: `plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java:664`

```java
} catch (ClassNotFoundException ex) {
    LOG.error("Unable to load class [{}]", classInfo.getName(), ex);
    return false;
}
```

**Problem**: Misses `NoClassDefFoundError` and other linkage errors

### 6. When NoClassDefFoundError Occurs

**Common Scenarios**:
1. **Application redeployment** (as mentioned in related research)
2. **Optional dependencies missing** (like JUnit for test classes)
3. **Classloader isolation** in application servers (JBoss, WebSphere, etc.)
4. **Static initializer failures** in class being loaded
5. **Split package issues** in modular systems
6. **Incompatible class versions** between dependencies

### 7. Trigger Condition

**From Email Thread**: Setting this constant triggers the bug:

```xml
<constant name="struts.convention.action.includeJars"
          value=".*?/myjar.*?jar(!/)?" />
```

**Why**:
- By default, convention plugin only scans application's own classes
- Setting `struts.convention.action.includeJars` causes scanning of JAR files
- This includes struts2-core.jar itself
- struts2-core.jar contains `org.apache.struts2.XWorkTestCase`
- `XWorkTestCase` depends on `junit.framework.TestCase`
- JUnit is optional (scope: test)
- At runtime, JUnit not available → `NoClassDefFoundError`

## Code References

- `plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java:664` - Bug location
- `core/src/main/java/org/apache/struts2/util/finder/ClassFinder.java:228` - Where NoClassDefFoundError originates
- `plugins/convention/src/main/java/org/apache/struts2/convention/DefaultClassFinder.java:283-286` - Correct pattern (catches Throwable)
- `core/src/main/java/org/apache/struts2/factory/DefaultInterceptorFactory.java:91-93` - Precedent for NoClassDefFoundError handling
- `core/src/main/java/org/apache/struts2/config/providers/XmlDocConfigurationProvider.java:580-582` - Multi-catch pattern

## Recommended Fix

### Option A: Multi-Catch (Recommended)

**File**: `plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java:664`

```java
} catch (ClassNotFoundException | NoClassDefFoundError ex) {
    LOG.error("Unable to load class [{}]. Perhaps it exists but certain dependencies are not available?",
              classInfo.getName(), ex);
    return false;
}
```

**Advantages**:
- Explicit about expected exceptions
- Follows pattern from `XmlDocConfigurationProvider`
- Provides helpful error message
- Java 7+ multi-catch syntax

### Option B: Catch Throwable (More Defensive)

```java
} catch (Throwable ex) {
    LOG.error("Unable to load class [{}]", classInfo.getName(), ex);
    return false;
}
```

**Advantages**:
- Matches pattern in `DefaultClassFinder`
- Handles any unexpected errors
- Most defensive approach

**Disadvantages**:
- Less specific
- Could mask programming errors

### Recommendation

Use **Option A** (multi-catch) because:
1. It's explicit about the expected error conditions
2. Follows established pattern in `XmlDocConfigurationProvider`
3. Provides better error message for users
4. Still maintains good defensive programming

### Additional Improvements

Consider adding to the log message:
```java
LOG.error("Unable to load class [{}]. Perhaps it exists but certain dependencies are not available? " +
          "If this is a test class or from a library, consider adding it to struts.convention.exclude.packages",
          classInfo.getName(), ex);
```

## Impact Assessment

### Current Risk

**Severity**: HIGH

**Scenarios Affected**:
1. Applications using `struts.convention.action.includeJars`
2. Applications with optional dependencies on scanned classes
3. Redeployment scenarios with classloader issues
4. Application server environments with module isolation (JBoss, WebSphere)

**Current Behavior**:
- ❌ Complete application startup failure
- ❌ No graceful degradation
- ❌ Misleading error (appears critical, not missing optional dependency)
- ❌ Users must add workarounds to proceed

### After Fix

**Expected Behavior**:
- ✅ Graceful degradation - problematic classes logged and skipped
- ✅ Application continues to function
- ✅ Only unavailable actions are affected, not entire app
- ✅ Clear error messages indicating dependency issues
- ✅ Consistent with `DefaultClassFinder` behavior

## Testing Strategy

### Unit Tests to Add

1. **Test NoClassDefFoundError Handling**:
```java
@Test
public void testHandlesNoClassDefFoundError() {
    // Mock a ClassInfo that throws NoClassDefFoundError
    ClassInfo classInfo = mock(ClassInfo.class);
    when(classInfo.get()).thenThrow(new NoClassDefFoundError("junit/framework/TestCase"));

    // Should return false (exclude class) instead of propagating error
    assertFalse(builder.includeClassNameInActionScan("org.apache.struts2.XWorkTestCase"));

    // Should log error
    verify(mockLogger).error(contains("Unable to load class"), any(NoClassDefFoundError.class));
}
```

2. **Test ClassNotFoundException Handling** (existing behavior):
```java
@Test
public void testHandlesClassNotFoundException() {
    ClassInfo classInfo = mock(ClassInfo.class);
    when(classInfo.get()).thenThrow(new ClassNotFoundException("com.example.MissingClass"));

    assertFalse(builder.includeClassNameInActionScan("com.example.MissingClass"));
    verify(mockLogger).error(contains("Unable to load class"), any(ClassNotFoundException.class));
}
```

3. **Test ExceptionInInitializerError** (edge case):
```java
@Test
public void testHandlesExceptionInInitializerError() {
    ClassInfo classInfo = mock(ClassInfo.class);
    when(classInfo.get()).thenThrow(new ExceptionInInitializerError("Static init failed"));

    assertFalse(builder.includeClassNameInActionScan("com.example.BadStaticInit"));
}
```

### Integration Tests

1. Create a test JAR with a class that has missing dependencies
2. Configure `struts.convention.action.includeJars` to scan that JAR
3. Verify application starts successfully despite the problematic class
4. Verify error is logged appropriately

## Workaround for Users

Until the fix is released, users can:

**Option 1**: Exclude problematic packages:
```xml
<constant name="struts.convention.exclude.packages"
          value="org.apache.struts2,org.apache.struts2.*" />
```

**Option 2**: Include JUnit at runtime (not recommended):
```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>runtime</scope>
</dependency>
```

**Option 3**: Don't use `struts.convention.action.includeJars` if not needed

## Related Issues

- **WW-5594**: Wildcard pattern matching issue preventing proper exclusion of org.apache.struts2 package
- Related research: `thoughts/shared/research/2025-12-02-memory-leak-redeployment.md` (redeployment issues)

## Email Thread Reference

**From**: Florian Schlittgen (sc...@liwa.de)
**Date**: December 19, 2024 - January 19, 2025
**Subject**: Struts 7: action class finder
**List**: dev@struts.apache.org

## Next Steps

1. ✅ Create JIRA ticket WW-5593
2. ⏳ Implement fix in `PackageBasedActionConfigBuilder.java:664`
3. ⏳ Add unit tests for NoClassDefFoundError handling
4. ⏳ Add integration tests with missing dependencies
5. ⏳ Update any other catch blocks with same issue
6. ⏳ Review release notes to document the fix