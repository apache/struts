---
date: 2026-01-31T12:15:00+01:00
topic: "WW-4291: Allow Spring Bean Names for Type Converters"
tags: [research, codebase, spring-plugin, type-converters, initialization]
status: implemented
jira: WW-4291
---

# WW-4291: Allow Spring Bean Names for Type Converters

**Date**: 2026-01-31
**Status**: Implemented

## Problem Summary

Users cannot reference Spring bean names in `struts-conversion.properties` files. When specifying a bean name (e.g., "myConverter") instead of a fully qualified class name, a `ClassNotFoundException` is thrown.

**JIRA:** https://issues.apache.org/jira/browse/WW-4291

## Root Cause

**Timing issue during initialization:**

1. **Bootstrap Phase** (`DefaultConfiguration.createBootstrapContainer()`):
   - `StrutsConversionPropertiesProcessor` implements `EarlyInitializable`
   - `EarlyInitializable` beans are always instantiated when container is created (ContainerBuilder.java:617-620)
   - At this point, `SpringObjectFactory` is not yet available
   - User's `struts-conversion.properties` is processed with basic `ObjectFactory.getClassInstance()` which only does class loading

2. **Full Container Phase** (later):
   - Spring plugin loads `SpringObjectFactory`
   - `SpringObjectFactory.getClassInstance()` already supports bean names via `containsBean()` check
   - But conversion properties were already processed!

**Historical context:** `EarlyInitializable` was introduced in Jan 2018 (fad603c49, d64365770) to ensure converters are registered before first action execution. Removing it entirely could break converter availability.

## Implemented Solution: Two-Phase Processing with Interface

Split conversion property processing into two phases:
1. **Early phase (EarlyInitializable):** Process `struts-default-conversion.properties` only - contains class names
2. **Late phase (Initializable):** Process user properties (`struts-conversion.properties`, `xwork-conversion.properties`) - may contain Spring bean names

### Key Design Decision

Instead of using `instanceof` checks to access the late initialization method, we introduced a clean interface `UserConversionPropertiesProvider` that defines the contract for late-phase processing. This follows the Dependency Inversion Principle.

## Implementation Details

### Files Created

| File | Purpose |
|------|---------|
| `core/src/main/java/org/apache/struts2/conversion/UserConversionPropertiesProvider.java` | Interface defining `initUserConversions()` method |
| `core/src/main/java/org/apache/struts2/conversion/UserConversionPropertiesProcessor.java` | Late initialization processor implementing `Initializable` |
| `core/src/test/java/org/apache/struts2/conversion/StrutsConversionPropertiesProcessorTest.java` | Unit tests |
| `plugins/spring/src/test/java/org/apache/struts2/spring/SpringTypeConverterTest.java` | Spring integration tests |

### Files Modified

| File | Changes |
|------|---------|
| `core/src/main/java/org/apache/struts2/conversion/StrutsConversionPropertiesProcessor.java` | Implements `UserConversionPropertiesProvider`, split `init()` into early/late phases |
| `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java` | Register `UserConversionPropertiesProvider` and `UserConversionPropertiesProcessor`; explicitly trigger late initialization in `reloadContainer()` |
| `core/src/main/java/org/apache/struts2/StrutsConstants.java` | Add `STRUTS_CONVERTER_USER_PROPERTIES_PROVIDER` constant |
| `core/src/main/java/org/apache/struts2/config/StrutsBeanSelectionProvider.java` | Add alias for `UserConversionPropertiesProvider` |
| `core/src/main/resources/struts-beans.xml` | Add bean definitions for new classes |

### Interface Definition

```java
/**
 * Interface for processors that support late initialization of user conversion properties.
 */
public interface UserConversionPropertiesProvider {
    /**
     * Process user conversion properties (struts-conversion.properties, xwork-conversion.properties).
     * Called during late initialization when SpringObjectFactory is available.
     */
    void initUserConversions();
}
```

### Class Diagram

```
                    +----------------------------+
                    | ConversionPropertiesProcessor |
                    +----------------------------+
                                ^
                                |
+---------------------------+   |   +--------------------------------+
| UserConversionPropertiesProvider |<----+ StrutsConversionPropertiesProcessor |
+---------------------------+       | (EarlyInitializable)            |
            ^                       +--------------------------------+
            |
            | @Inject
            |
+--------------------------------+
| UserConversionPropertiesProcessor |
| (Initializable)                |
+--------------------------------+
```

### Initialization Flow

```
Bootstrap Container Created
    |
    v
StrutsConversionPropertiesProcessor.init() [EarlyInitializable]
    |
    +-> processRequired("struts-default-conversion.properties")
    |   (Only class names, no Spring beans needed)
    |
    v
Full Container Created (SpringObjectFactory available)
    |
    v
DefaultConfiguration.reloadContainer() explicitly triggers:
    container.getInstance(UserConversionPropertiesProcessor.class)
    |
    v
UserConversionPropertiesProcessor.init() [Initializable]
    |
    +-> provider.initUserConversions()
        |
        +-> process("struts-conversion.properties")
        +-> process("xwork-conversion.properties")
            (Spring bean names now resolved via SpringObjectFactory)
```

## Verification

### Unit Tests
```bash
mvn test -pl core -DskipAssembly -Dtest=StrutsConversionPropertiesProcessorTest
```

### Spring Plugin Integration Tests
```bash
mvn test -pl plugins/spring -DskipAssembly -Dtest=SpringTypeConverterTest
```

### All Conversion Tests
```bash
mvn test -pl core -DskipAssembly '-Dtest=*Conversion*'
```

## Usage After Implementation

Users can now specify Spring bean names in `struts-conversion.properties`:

```properties
# Using Spring bean name
java.time.LocalDate=localDateConverter

# Using class name (still works - backward compatible)
java.util.UUID=com.example.UUIDConverter
```

With Spring configuration:
```xml
<bean id="localDateConverter" class="com.example.LocalDateConverter"/>
```

## Code References

- `core/src/main/java/org/apache/struts2/conversion/UserConversionPropertiesProvider.java` - New interface
- `core/src/main/java/org/apache/struts2/conversion/StrutsConversionPropertiesProcessor.java:34` - Implements interface
- `core/src/main/java/org/apache/struts2/conversion/StrutsConversionPropertiesProcessor.java:75` - `initUserConversions()`
- `core/src/main/java/org/apache/struts2/conversion/UserConversionPropertiesProcessor.java` - Late processor
- `plugins/spring/src/main/java/org/apache/struts2/spring/SpringObjectFactory.java:246` - Bean name resolution
- `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java:300` - Explicit late initialization trigger
- `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java:384` - Factory registrations
- `core/src/main/resources/struts-beans.xml:117-119` - Bean definitions

## Alternative Approaches Considered

| Approach | Pros | Cons |
|----------|------|------|
| **Remove EarlyInitializable** | Simple, single change | Risk: converters may not be ready when first needed |
| **Spring plugin reprocessing** | Plugin-specific | Duplicates work, potential race conditions |
| **Lazy converter proxies** | Elegant | Complex implementation, overhead |
| **instanceof check** | Simple | Violates Dependency Inversion Principle |
| **Two-phase with interface** | Clean design, testable | Requires new interface (chosen approach) |
