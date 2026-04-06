# Hibernate Proxy Detection Optimization

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Eliminate `LinkageError` exceptions thrown when Hibernate is not on the classpath by detecting availability once at class-load time.

**Architecture:** Add a static availability check in `StrutsProxyService` that probes for `org.hibernate.proxy.HibernateProxy` once during class initialization. All Hibernate-related methods short-circuit immediately when Hibernate is absent. Same pattern applied to deprecated `ProxyUtil`.

**Tech Stack:** Java 17, JUnit 5, AssertJ, Mockito

---

### Task 1: Add Hibernate Availability Check to StrutsProxyService

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/util/StrutsProxyService.java`
- Test: `core/src/test/java/org/apache/struts2/util/StrutsProxyServiceTest.java`

- [ ] **Step 1: Write the failing test — verify no LinkageError is thrown when Hibernate classes are used**

The existing tests already call `isHibernateProxy()` and `isHibernateProxyMember()` with non-Hibernate objects. We need a test that verifies the short-circuit behavior works correctly. Add this test to `StrutsProxyServiceTest.java`:

```java
@Test
public void isHibernateProxyDoesNotThrowWhenCalledRepeatedly() {
    // Verify that calling isHibernateProxy many times for different objects
    // does not cause performance issues (no exceptions thrown internally)
    for (int i = 0; i < 1000; i++) {
        assertThat(proxyService.isHibernateProxy(new Object())).isFalse();
    }
}

@Test
public void isHibernateProxyMemberDoesNotThrowWhenCalledRepeatedly() throws NoSuchMethodException {
    Method method = Object.class.getMethod("toString");
    for (int i = 0; i < 1000; i++) {
        assertThat(proxyService.isHibernateProxyMember(method)).isFalse();
    }
}
```

- [ ] **Step 2: Run tests to verify they pass (baseline — these pass even without the fix because Hibernate IS on the test classpath)**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsProxyServiceTest#isHibernateProxyDoesNotThrowWhenCalledRepeatedly+isHibernateProxyMemberDoesNotThrowWhenCalledRepeatedly`
Expected: PASS

- [ ] **Step 3: Add static Hibernate availability flag to StrutsProxyService**

In `core/src/main/java/org/apache/struts2/util/StrutsProxyService.java`, add a static availability check at the top of the class and modify the three Hibernate methods to short-circuit:

```java
// Add this field near the top of the class, after the class declaration:
private static final boolean HIBERNATE_AVAILABLE = isHibernateAvailable();

private static boolean isHibernateAvailable() {
    try {
        Class.forName("org.hibernate.proxy.HibernateProxy");
        return true;
    } catch (ClassNotFoundException e) {
        return false;
    }
}
```

Then modify the three Hibernate methods to short-circuit:

**`isHibernateProxy`** — change from:
```java
@Override
public boolean isHibernateProxy(Object object) {
    try {
        return object != null && HibernateProxy.class.isAssignableFrom(object.getClass());
    } catch (LinkageError ignored) {
        return false;
    }
}
```
to:
```java
@Override
public boolean isHibernateProxy(Object object) {
    if (!HIBERNATE_AVAILABLE || object == null) {
        return false;
    }
    try {
        return HibernateProxy.class.isAssignableFrom(object.getClass());
    } catch (LinkageError ignored) {
        return false;
    }
}
```

**`isHibernateProxyMember`** — change from:
```java
@Override
public boolean isHibernateProxyMember(Member member) {
    try {
        return hasMember(HibernateProxy.class, member);
    } catch (LinkageError ignored) {
        return false;
    }
}
```
to:
```java
@Override
public boolean isHibernateProxyMember(Member member) {
    if (!HIBERNATE_AVAILABLE) {
        return false;
    }
    try {
        return hasMember(HibernateProxy.class, member);
    } catch (LinkageError ignored) {
        return false;
    }
}
```

**`getHibernateProxyTarget`** — change from:
```java
@Override
public Object getHibernateProxyTarget(Object object) {
    try {
        return Hibernate.unproxy(object);
    } catch (LinkageError ignored) {
        return object;
    }
}
```
to:
```java
@Override
public Object getHibernateProxyTarget(Object object) {
    if (!HIBERNATE_AVAILABLE) {
        return object;
    }
    try {
        return Hibernate.unproxy(object);
    } catch (LinkageError ignored) {
        return object;
    }
}
```

- [ ] **Step 4: Run the full StrutsProxyService test suite**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsProxyServiceTest`
Expected: All tests PASS

- [ ] **Step 5: Run the Spring integration test suite**

Run: `mvn test -DskipAssembly -pl core -Dtest=StrutsProxyServiceSpringIntegrationTest`
Expected: All tests PASS

- [ ] **Step 6: Commit**

```bash
git add core/src/main/java/org/apache/struts2/util/StrutsProxyService.java core/src/test/java/org/apache/struts2/util/StrutsProxyServiceTest.java
git commit -m "WW-5622 Optimize Hibernate proxy detection to avoid LinkageError exceptions

Add static availability check for Hibernate classes in StrutsProxyService.
When Hibernate is not on the classpath, all Hibernate-related methods
short-circuit immediately without throwing/catching LinkageError.
This eliminates a significant performance penalty for applications
that don't use Hibernate."
```

---

### Task 2: Apply Same Fix to Deprecated ProxyUtil

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/util/ProxyUtil.java`

- [ ] **Step 1: Add the same static availability check to ProxyUtil**

In `core/src/main/java/org/apache/struts2/util/ProxyUtil.java`, add the same pattern:

```java
// Add after the isProxyMemberCache field:
private static final boolean HIBERNATE_AVAILABLE = isHibernateAvailable();

private static boolean isHibernateAvailable() {
    try {
        Class.forName("org.hibernate.proxy.HibernateProxy");
        return true;
    } catch (ClassNotFoundException e) {
        return false;
    }
}
```

Then modify the three Hibernate methods in ProxyUtil identically to Task 1:

**`isHibernateProxy`**:
```java
@Deprecated(since = "7.2")
public static boolean isHibernateProxy(Object object) {
    if (!HIBERNATE_AVAILABLE || object == null) {
        return false;
    }
    try {
        return HibernateProxy.class.isAssignableFrom(object.getClass());
    } catch (LinkageError ignored) {
        return false;
    }
}
```

**`isHibernateProxyMember`**:
```java
@Deprecated(since = "7.2")
public static boolean isHibernateProxyMember(Member member) {
    if (!HIBERNATE_AVAILABLE) {
        return false;
    }
    try {
        return hasMember(HibernateProxy.class, member);
    } catch (LinkageError ignored) {
        return false;
    }
}
```

**`getHibernateProxyTarget`**:
```java
@Deprecated(since = "7.2")
public static Object getHibernateProxyTarget(Object object) {
    if (!HIBERNATE_AVAILABLE) {
        return object;
    }
    try {
        return Hibernate.unproxy(object);
    } catch (LinkageError ignored) {
        return object;
    }
}
```

- [ ] **Step 2: Run existing ProxyUtil tests**

Run: `mvn test -DskipAssembly -pl core -Dtest=ProxyUtilTest`
Expected: PASS (or if no dedicated test exists, run the SecurityMemberAccess tests which exercise ProxyUtil indirectly)

Run: `mvn test -DskipAssembly -pl core -Dtest=SecurityMemberAccessTest`
Expected: PASS

- [ ] **Step 3: Commit**

```bash
git add core/src/main/java/org/apache/struts2/util/ProxyUtil.java
git commit -m "WW-5622 Apply same Hibernate availability optimization to deprecated ProxyUtil"
```

---

### Task 3: Run Full Test Suite

- [ ] **Step 1: Run all core tests**

Run: `mvn test -DskipAssembly -pl core`
Expected: All tests PASS

- [ ] **Step 2: Run spring plugin tests (exercises proxy detection heavily)**

Run: `mvn test -DskipAssembly -pl plugins/spring`
Expected: All tests PASS

- [ ] **Step 3: Run json plugin tests (StrutsJSONWriter has Hibernate-related class name checks)**

Run: `mvn test -DskipAssembly -pl plugins/json`
Expected: All tests PASS
