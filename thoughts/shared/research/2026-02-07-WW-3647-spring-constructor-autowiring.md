---
date: 2026-02-07T12:00:00+01:00
topic: "WW-3647: ServletActionRedirectResult Spring Constructor Autowiring Issue"
tags: [ research, codebase, spring-plugin, autowiring, constructor-injection ]
status: complete
---

# Research: WW-3647 ServletActionRedirectResult Spring Constructor Autowiring Issue

**Date**: 2026-02-07

## Research Question

Is WW-3647 (ServletActionRedirectResult injection problem with Spring JNDI lookup beans) still an issue, and what is the
root cause?

## Summary

**YES, this issue is still present in the current codebase.** The root cause is the default "mixed injection strategy"
in `SpringObjectFactory` that uses `AUTOWIRE_CONSTRUCTOR`, which causes Spring to inject any available String bean into
all String parameters of result class constructors.

## Detailed Findings

### The Problem

When a Spring JNDI lookup bean with a String default value exists:

```xml

<jee:jndi-lookup jndi-name="someName" id="currentEnvironment" default-value="XXXX"/>
```

`ServletActionRedirectResult` redirect URLs become malformed:

```
http://localhost:8080/XXXX/index!XXXX.action#XXXX
```

### Root Cause

The issue is in `SpringObjectFactory.buildBean(Class, Map)` at line 199:

```java
// When alwaysRespectAutowireStrategy is false (DEFAULT)
bean =autoWiringFactory.

autowire(clazz, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
```

Spring's `AUTOWIRE_CONSTRUCTOR` strategy:

1. Examines all constructors of `ServletActionRedirectResult`
2. Finds constructors with String parameters
3. Looks for Spring beans of type `String` to inject
4. Injects the JNDI bean's String value into **ALL** String parameters

### ServletActionRedirectResult Constructors

The class has 5 constructors, 4 of which take String parameters (
`core/src/main/java/org/apache/struts2/result/ServletActionRedirectResult.java:135-156`):

```java
public ServletActionRedirectResult()                                    // no-arg

public ServletActionRedirectResult(String actionName)                   // 1 String

public ServletActionRedirectResult(String actionName, String method)    // 2 Strings

public ServletActionRedirectResult(String namespace, String actionName, String method)  // 3 Strings

public ServletActionRedirectResult(String namespace, String actionName, String method, String anchor)  // 4 Strings
```

When Spring's constructor autowiring finds a String bean, it matches that bean to **every** String parameter, resulting
in:

- `namespace = "XXXX"`
- `actionName = "XXXX"`
- `method = "XXXX"`
- `anchor = "XXXX"`

### Default Configuration

In `core/src/main/resources/org/apache/struts2/default.properties`:

```properties
struts.objectFactory.spring.autoWire.alwaysRespect=false
```

This default value enables the legacy mixed injection strategy that causes the issue.

### Code Flow

1. Struts needs to create a `ServletActionRedirectResult` instance
2. `SpringObjectFactory.buildBean()` is called
3. Since `alwaysRespectAutowireStrategy = false`, it uses constructor autowiring:
   ```java
   bean = autoWiringFactory.autowire(clazz, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
   ```
4. Spring finds a String bean (the JNDI lookup) and injects it into constructor String parameters
5. The result is instantiated with incorrect values

### Why the Workaround Works

Setting `struts.objectFactory.spring.autoWire.alwaysRespect = true` causes the code to take a different path (
`SpringObjectFactory.java:188-192`):

```java
if(alwaysRespectAutowireStrategy){
// Leave the creation up to Spring
bean =autoWiringFactory.

createBean(clazz, autowireStrategy, false);

injectApplicationContext(bean);
    return

injectInternalBeans(bean);
}
```

The default `autowireStrategy` is `AUTOWIRE_BY_NAME`, which only injects beans when property names match bean names.
Since `ServletActionRedirectResult` doesn't have properties named after JNDI beans, no incorrect injection occurs.

## Code References

- `plugins/spring/src/main/java/org/apache/struts2/spring/SpringObjectFactory.java:183-208` - buildBean method with
  mixed injection strategy
- `plugins/spring/src/main/java/org/apache/struts2/spring/SpringObjectFactory.java:58` - alwaysRespectAutowireStrategy
  field (default: false)
- `core/src/main/java/org/apache/struts2/result/ServletActionRedirectResult.java:135-156` - Constructors with String
  parameters
- `core/src/main/java/org/apache/struts2/StrutsConstants.java:138` - STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE_ALWAYS_RESPECT
  constant
- `core/src/main/resources/org/apache/struts2/default.properties` - Default configuration

## Test Coverage Gap

There are **no tests** in the Spring plugin specifically testing `ServletActionRedirectResult` autowiring behavior.
Existing tests cover:

- Constructor injection with custom beans (`testShouldUseConstructorBasedInjectionWhenCreatingABeanFromAClassName`)
- alwaysRespect configuration switching
- Fallback behavior for ambiguous constructors

But none test the scenario of a String bean being incorrectly injected into result class constructors.

## Potential Fixes

1. **Change the default**: Set `struts.objectFactory.spring.autoWire.alwaysRespect = true` as the default
    - Pros: Fixes the issue for everyone
    - Cons: Breaking change for existing applications relying on constructor injection

2. **Remove String-parameter constructors**: Use only the no-arg constructor with setters
    - Pros: Avoids the problem entirely
    - Cons: Less convenient API, breaks existing code using these constructors

3. **Add @Autowired(required=false) annotations**: Explicitly mark constructors
    - Pros: Gives control over which constructors Spring considers
    - Cons: Adds Spring-specific annotations to core classes

4. **Use primary constructor designation**: Mark the no-arg constructor as preferred
    - Pros: Spring would prefer the no-arg constructor
    - Cons: Requires additional Spring configuration

## Workaround (Still Valid)

Configure Struts to respect the autowire strategy:

**struts.xml:**

```xml

<constant name="struts.objectFactory.spring.autoWire.alwaysRespect" value="true"/>
```

**struts.properties:**

```properties
struts.objectFactory.spring.autoWire.alwaysRespect=true
```

## Architecture Insights

The "mixed injection strategy" was designed to provide flexibility by:

1. First using constructor autowiring to create beans
2. Then applying property-based autowiring for additional dependencies

However, this approach has an inherent flaw: constructor autowiring is too aggressive with simple types like String,
matching any available String bean to any String constructor parameter. This design predates the modern understanding
that constructor injection should primarily be used for required dependencies with specific types.

## Open Questions

1. Should the default for `alwaysRespectAutowireStrategy` be changed to `true`?
2. Are there other result classes or Struts components with similar String-parameter constructors that might be
   affected?
3. Is the "mixed injection strategy" still needed, or should it be deprecated?
