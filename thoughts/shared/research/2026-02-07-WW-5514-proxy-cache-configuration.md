---
date: 2026-02-07T12:00:00+01:00
topic: "WW-5514: Allow Configuration of ProxyUtil Cache Types"
tags: [research, implementation-plan, proxy, cache, caffeine, WW-5514]
status: complete
---

# WW-5514: Allow Configuration of ProxyUtil Cache Types

**Date**: 2026-02-07
**JIRA**: https://issues.apache.org/jira/browse/WW-5514

## Problem Statement

`ProxyUtil` hardcodes `CacheType.WTLFU` for its internal caches, making the Caffeine library mandatory. Users need the ability to configure cache types (e.g., `BASIC`) to avoid this dependency.

### Current Implementation

**File**: `core/src/main/java/org/apache/struts2/util/ProxyUtil.java`

```java
private static final OgnlCache<Class<?>, Boolean> isProxyCache = 
    new DefaultOgnlCacheFactory<>(CACHE_MAX_SIZE, OgnlCacheFactory.CacheType.WTLFU, CACHE_INITIAL_CAPACITY).buildOgnlCache();
```

Three static caches are hardcoded with WTLFU:
- `isProxyCache` - Caches proxy class detection
- `isProxyMemberCache` - Caches proxy member detection
- `targetClassCache` - Caches ultimate target class resolution

---

## Solution: Option A - Injectable ProxyService

Refactor `ProxyUtil` from a static utility to an injectable service following the `OgnlUtil`/`ExpressionCacheFactory` pattern.

---

## Files to Create

### 1. `core/src/main/java/org/apache/struts2/ognl/ProxyCacheFactory.java`

Marker interface extending `OgnlCacheFactory` for DI.

```java
package org.apache.struts2.ognl;

/**
 * A proxy interface to be used with Struts DI mechanism for proxy detection caching.
 *
 * @since 7.2.0
 */
public interface ProxyCacheFactory<Key, Value> extends OgnlCacheFactory<Key, Value> {
}
```

### 2. `core/src/main/java/org/apache/struts2/ognl/StrutsProxyCacheFactory.java`

Implementation with `@Inject` constructor taking configuration constants.

```java
package org.apache.struts2.ognl;

import org.apache.commons.lang3.EnumUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.Inject;

/**
 * Struts proxy cache factory implementation.
 * Used for creating caches for proxy detection operations.
 *
 * @since 7.2.0
 */
public class StrutsProxyCacheFactory<Key, Value> extends DefaultOgnlCacheFactory<Key, Value>
        implements ProxyCacheFactory<Key, Value> {

    @Inject
    public StrutsProxyCacheFactory(
            @Inject(value = StrutsConstants.STRUTS_PROXY_CACHE_MAXSIZE) String cacheMaxSize,
            @Inject(value = StrutsConstants.STRUTS_PROXY_CACHE_TYPE) String defaultCacheType) {
        super(Integer.parseInt(cacheMaxSize), EnumUtils.getEnumIgnoreCase(CacheType.class, defaultCacheType));
    }
}
```

### 3. `core/src/main/java/org/apache/struts2/util/ProxyService.java`

Service interface with proxy detection methods.

```java
package org.apache.struts2.util;

import java.lang.reflect.Member;

/**
 * Service interface for proxy detection and resolution operations.
 * Replaces static ProxyUtil methods with an injectable service.
 *
 * @since 7.2.0
 */
public interface ProxyService {

    /**
     * Determine the ultimate target class of the given instance.
     */
    Class<?> ultimateTargetClass(Object candidate);

    /**
     * Check whether the given object is a proxy.
     */
    boolean isProxy(Object object);

    /**
     * Check whether the given member is a proxy member.
     */
    boolean isProxyMember(Member member, Object object);

    /**
     * Check whether the given object is a Hibernate proxy.
     */
    boolean isHibernateProxy(Object object);

    /**
     * Check whether the given member is a member of a Hibernate proxy.
     */
    boolean isHibernateProxyMember(Member member);

    /**
     * Get the target instance of a Hibernate proxy.
     */
    Object getHibernateProxyTarget(Object object);

    /**
     * Resolve matching member on target class.
     */
    Member resolveTargetMember(Member proxyMember, Class<?> targetClass);

    /**
     * @deprecated since 7.2, use {@link #resolveTargetMember(Member, Class)} instead.
     */
    @Deprecated
    Member resolveTargetMember(Member proxyMember, Object target);
}
```

### 4. `core/src/main/java/org/apache/struts2/util/StrutsProxyService.java`

Implementation using injected `ProxyCacheFactory`. Move logic from `ProxyUtil`.

```java
package org.apache.struts2.util;

import org.apache.struts2.inject.Inject;
import org.apache.struts2.ognl.OgnlCache;
import org.apache.struts2.ognl.ProxyCacheFactory;
// ... other imports from ProxyUtil

/**
 * Default implementation of {@link ProxyService}.
 *
 * @since 7.2.0
 */
public class StrutsProxyService implements ProxyService {

    private final OgnlCache<Class<?>, Boolean> isProxyCache;
    private final OgnlCache<Member, Boolean> isProxyMemberCache;
    private final OgnlCache<Object, Class<?>> targetClassCache;

    @Inject
    public StrutsProxyService(ProxyCacheFactory<?, ?> proxyCacheFactory) {
        this.isProxyCache = proxyCacheFactory.buildOgnlCache();
        this.isProxyMemberCache = proxyCacheFactory.buildOgnlCache();
        this.targetClassCache = proxyCacheFactory.buildOgnlCache();
    }

    // ... implement all methods from ProxyService interface
    // (move logic from ProxyUtil static methods)
}
```

### 5. `core/src/test/java/org/apache/struts2/ognl/StrutsProxyCacheFactoryTest.java`

Unit tests for cache factory.

### 6. `core/src/test/java/org/apache/struts2/util/StrutsProxyServiceTest.java`

Unit tests for proxy service.

---

## Files to Modify

### 1. `core/src/main/java/org/apache/struts2/StrutsConstants.java`

Add constants:

```java
/**
 * Specifies the type of cache to use for proxy detection. Valid values defined in
 * {@link org.apache.struts2.ognl.OgnlCacheFactory.CacheType}.
 *
 * @since 7.2.0
 */
public static final String STRUTS_PROXY_CACHE_TYPE = "struts.proxy.cacheType";

/**
 * Specifies the maximum cache size for proxy detection caches.
 *
 * @since 7.2.0
 */
public static final String STRUTS_PROXY_CACHE_MAXSIZE = "struts.proxy.cacheMaxSize";
```

### 2. `core/src/main/resources/org/apache/struts2/default.properties`

Add defaults:

```properties
### Proxy detection cache configuration
struts.proxy.cacheType=basic
struts.proxy.cacheMaxSize=10000
```

### 3. `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java`

Add to `BOOTSTRAP_CONSTANTS` static block:

```java
constants.put(StrutsConstants.STRUTS_PROXY_CACHE_TYPE, OgnlCacheFactory.CacheType.BASIC);
constants.put(StrutsConstants.STRUTS_PROXY_CACHE_MAXSIZE, 10000);
```

Add to `bootstrapFactories()` method:

```java
.factory(ProxyCacheFactory.class, StrutsProxyCacheFactory.class, Scope.SINGLETON)
.factory(ProxyService.class, StrutsProxyService.class, Scope.SINGLETON)
```

### 4. `core/src/main/resources/struts-beans.xml`

Add bean registrations:

```xml
<bean type="org.apache.struts2.ognl.ProxyCacheFactory" name="struts"
      class="org.apache.struts2.ognl.StrutsProxyCacheFactory" scope="singleton"/>
<bean type="org.apache.struts2.util.ProxyService" name="struts"
      class="org.apache.struts2.util.StrutsProxyService" scope="singleton"/>
```

### 5. `core/src/main/java/org/apache/struts2/util/ProxyUtil.java`

Deprecate all public static methods:

```java
/**
 * @deprecated since 7.2, inject {@link ProxyService} instead
 */
@Deprecated(since = "7.2")
public static boolean isProxy(Object object) {
    // existing implementation kept for backwards compatibility
}
```

### 6. `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java`

Add ProxyService injection and update calls:

```java
private ProxyService proxyService;

@Inject
public void setProxyService(ProxyService proxyService) {
    this.proxyService = proxyService;
}
```

Replace:
- `ProxyUtil.isProxy()` → `proxyService.isProxy()`
- `ProxyUtil.isProxyMember()` → `proxyService.isProxyMember()`
- `ProxyUtil.ultimateTargetClass()` → `proxyService.ultimateTargetClass()`
- `ProxyUtil.resolveTargetMember()` → `proxyService.resolveTargetMember()`

### 7. `core/src/main/java/org/apache/struts2/interceptor/parameter/ParametersInterceptor.java`

Add ProxyService field and setter, update `ultimateClass()` method.

### 8. `core/src/main/java/org/apache/struts2/interceptor/ChainingInterceptor.java`

Add ProxyService field and setter, update proxy detection calls.

### 9. `core/src/main/java/org/apache/struts2/json/DefaultJSONWriter.java`

Add ProxyService field and setter, update `ultimateTargetClass()` call.

### 10. `plugins/spring/src/test/java/org/apache/struts2/spring/SpringProxyUtilTest.java`

Update to test new `ProxyService` alongside deprecated `ProxyUtil`.

---

## Implementation Order

| Step | File | Action |
|------|------|--------|
| 1 | `StrutsConstants.java` | Add 2 constants |
| 2 | `ProxyCacheFactory.java` | Create marker interface |
| 3 | `StrutsProxyCacheFactory.java` | Create implementation |
| 4 | `ProxyService.java` | Create service interface |
| 5 | `StrutsProxyService.java` | Create implementation with injected factory |
| 6 | `DefaultConfiguration.java` | Register constants + factories |
| 7 | `struts-beans.xml` | Register beans |
| 8 | `default.properties` | Add default values |
| 9 | `ProxyUtil.java` | Add deprecation annotations |
| 10 | `SecurityMemberAccess.java` | Inject and use ProxyService |
| 11 | `ParametersInterceptor.java` | Inject and use ProxyService |
| 12 | `ChainingInterceptor.java` | Inject and use ProxyService |
| 13 | `DefaultJSONWriter.java` | Inject and use ProxyService |
| 14 | Tests | Create unit tests for factory and service |
| 15 | `SpringProxyUtilTest.java` | Update integration tests |

---

## User Configuration

After implementation, users can configure:

```xml
<constant name="struts.proxy.cacheType" value="basic" />
<constant name="struts.proxy.cacheMaxSize" value="5000" />
```

Valid cache types: `basic`, `lru`, `wtlfu`

---

## Verification

1. **Build**: `mvn clean install -DskipTests`
2. **Unit Tests**: `mvn test -DskipAssembly -pl core -Dtest=StrutsProxyCacheFactoryTest,StrutsProxyServiceTest`
3. **Integration Tests**: `mvn test -DskipAssembly -pl plugins/spring -Dtest=SpringProxyUtilTest`
4. **Full Test Suite**: `mvn test -DskipAssembly`
5. **Verify no Caffeine required**: Configure `struts.proxy.cacheType=basic` and confirm app starts without Caffeine

---

## Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| Default `BASIC` cache | Avoids mandatory Caffeine dependency (original issue) |
| Singleton scope | Caches should be shared application-wide |
| Keep deprecated `ProxyUtil` | Backwards compatibility for existing code |
| `ProxyService` in `util` package | Discoverability alongside `ProxyUtil` |
| `StrutsProxyCacheFactory` naming | Follows user preference and Struts conventions |

---

## Code References

- `core/src/main/java/org/apache/struts2/util/ProxyUtil.java:53-58` - Current hardcoded WTLFU caches
- `core/src/main/java/org/apache/struts2/ognl/DefaultOgnlExpressionCacheFactory.java` - Pattern to follow
- `core/src/main/java/org/apache/struts2/ognl/ExpressionCacheFactory.java` - Interface pattern
- `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java:217-225` - Heaviest ProxyUtil consumer
- `core/src/main/resources/struts-beans.xml:239-242` - Bean registration pattern

---

## Related Research

- OgnlUtil injection pattern analysis
- ProxyUtil usage analysis across codebase
