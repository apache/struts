# WW-5659 Request-Scoped Lazy Interceptor Params Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Stop `WithLazyParams` from writing per-request resolved `${...}` params onto the shared interceptor singleton, so concurrent requests can no longer read one another's upload validation policy.

**Architecture:** Resolved params are written into a per-invocation holder object that the interceptor supplies (`newLazyParams()`) and receives back (`intercept(invocation, params)`). The interceptor singleton becomes immutable after `init()`. A new `InterceptorParams` marker is the general contract; `DisableParams` is opt-in support for the `disabled` param; `UploadPolicy` is the file-upload holder. Unresolvable expressions fail closed instead of silently disabling validation.

**Tech Stack:** Java 17, Maven, JUnit 3-style tests (`junit.framework.TestCase` via `XWorkTestCase`), AssertJ assertions, Log4j2.

**Spec:** `docs/superpowers/specs/2026-07-27-WW-5659-lazy-params-request-scoping-design.md`

## Global Constraints

- **Ticket prefix:** every commit message starts with `WW-5659`.
- **Target version:** 7.3.0. This is a deliberate public API break; no backport to 7.2.x.
- **Test style:** `core` tests are JUnit 3 style — `protected void setUp()`, tests named `public void testXxx()`, no annotations. **A JUnit 5 `@Test` annotation compiles and silently never runs.** Never add one. Extend `StrutsInternalTestCase` (which extends `XWorkTestCase`, which extends `junit.framework.TestCase`) when the test needs the Struts container or `ActionContext`; extend `junit.framework.TestCase` directly for pure value-object tests that need neither.
- **Assertions:** use AssertJ (`import static org.assertj.core.api.Assertions.assertThat;`), matching the existing `ActionFileUploadInterceptorTest`.
- **Package:** all new production types go in `org.apache.struts2.interceptor`.
- **License header:** every new `.java` file starts with the ASF header, copied verbatim from `core/src/main/java/org/apache/struts2/interceptor/WithLazyParams.java:1-18`.
- **Message bundles:** new keys go into `core/src/main/resources/org/apache/struts2/struts-messages.properties` **only**. The five locale bundles (`_da`, `_de`, `_en`, `_pl`, `_pt`) are partial (12–14 keys vs 20 in the base) and fall back to the base bundle. Do not fabricate translations.
- **Build command:** `mvn test -DskipAssembly -pl core -Dtest=ClassName#methodName`

## Deviation from the spec — read before Task 5

The spec's Error Handling section says `unresolved(param)` should mark a dimension unusable **only if** the seeded config-time value still contains `${`. That rule is not implementable deterministically: it relies on introspecting the seed, and for `maximumSize` the seed is a `Long`, so a `${...}` literal cannot survive there — whether config-time OGNL conversion of `"${maxFileSize}"` to `Long` yields `null`, throws, or skips the setter is not knowable without testing, and the rule would silently behave differently per param type.

**This plan implements the simpler, deterministic rule instead: on unresolved, the dimension is marked unusable unconditionally.** A static fallback then applies only when the param is absent from the lazy map entirely (pure static config), which never triggers `unresolved`. This is stricter, type-independent, and consistent with the chosen fail-closed policy.

Task 6 includes a step to update the spec to match. **If the reviewer prefers the spec's original rule, stop and re-plan Task 5** rather than implementing both.

## File Structure

**Created:**
- `core/src/main/java/org/apache/struts2/interceptor/InterceptorParams.java` — general params-holder contract; generic bound for `WithLazyParams`.
- `core/src/main/java/org/apache/struts2/interceptor/DisableParams.java` — opt-in holder state for the `disabled` param.
- `core/src/main/java/org/apache/struts2/interceptor/UploadPolicy.java` — file-upload holder: sizes, types, extensions, unresolved tracking.
- `core/src/test/java/org/apache/struts2/interceptor/DisableParamsTest.java`
- `core/src/test/java/org/apache/struts2/interceptor/LazyParamInjectorTest.java`

**Modified:**
- `core/src/main/java/org/apache/struts2/interceptor/WithLazyParams.java` — new generic contract; `injectParams` → `resolveInto`.
- `core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java` — single config-time `UploadPolicy`; `acceptFile` takes the policy.
- `core/src/main/java/org/apache/struts2/interceptor/ActionFileUploadInterceptor.java` — implements `WithLazyParams<UploadPolicy>`.
- `core/src/main/java/org/apache/struts2/DefaultActionInvocation.java:258-276` — merged lazy/conditional path; non-mutating `mergedParams`.
- `core/src/main/resources/org/apache/struts2/struts-messages.properties` — new fail-closed message key.
- `core/src/test/java/org/apache/struts2/interceptor/ActionFileUploadInterceptorTest.java` — regression + migration.

---

### Task 1: `InterceptorParams` and `DisableParams`

Purely additive — nothing references these yet. Establishes the vocabulary the rest of the plan builds on.

**Files:**
- Create: `core/src/main/java/org/apache/struts2/interceptor/InterceptorParams.java`
- Create: `core/src/main/java/org/apache/struts2/interceptor/DisableParams.java`
- Test: `core/src/test/java/org/apache/struts2/interceptor/DisableParamsTest.java`

**Interfaces:**
- Consumes: nothing.
- Produces: `interface InterceptorParams { default void unresolved(String paramName) {} }`; `class DisableParams implements InterceptorParams` with `void setDisabled(String)`, `boolean isDisabled()`, and `protected DisableParams(DisableParams other)` copy constructor.

- [ ] **Step 1: Write the failing test**

Create `core/src/test/java/org/apache/struts2/interceptor/DisableParamsTest.java` (ASF header first):

```java
package org.apache.struts2.interceptor;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class DisableParamsTest extends TestCase {

    public void testDisabledDefaultsToFalse() {
        assertThat(new DisableParams().isDisabled()).isFalse();
    }

    public void testSetDisabledParsesStringValue() {
        DisableParams params = new DisableParams();
        params.setDisabled("true");
        assertThat(params.isDisabled()).isTrue();

        params.setDisabled("false");
        assertThat(params.isDisabled()).isFalse();
    }

    public void testSetDisabledTreatsNonBooleanTextAsFalse() {
        DisableParams params = new DisableParams();
        params.setDisabled("yes");
        assertThat(params.isDisabled()).isFalse();
    }

    public void testCopyConstructorCarriesDisabledFlag() {
        DisableParams original = new DisableParams();
        original.setDisabled("true");

        DisableParams copy = new DisableParams(original);

        assertThat(copy.isDisabled()).isTrue();
    }

    public void testUnresolvedDefaultsToNoOp() {
        DisableParams params = new DisableParams();
        params.unresolved("someParam");   // must not throw
        assertThat(params.isDisabled()).isFalse();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=DisableParamsTest`
Expected: compilation failure — `DisableParams` does not exist.

- [ ] **Step 3: Write minimal implementation**

Create `core/src/main/java/org/apache/struts2/interceptor/InterceptorParams.java` (ASF header first):

```java
package org.apache.struts2.interceptor;

/**
 * Contract for an object holding the parameters of a single interceptor.
 * <p>
 * Implementations are per-invocation value objects: the framework resolves configured
 * parameters into a fresh instance for each action invocation, so nothing is written back
 * onto the interceptor, which stays immutable after {@link Interceptor#init()}.
 *
 * @since 7.3.0
 */
public interface InterceptorParams {

    /**
     * Called when a {@code ${...}} parameter could not be resolved for the current invocation.
     * The framework skips the write, leaving the seeded configuration value in place, and
     * notifies the holder so it can decide how to degrade.
     * <p>
     * The default implementation does nothing.
     *
     * @param paramName name of the parameter that could not be resolved
     */
    default void unresolved(String paramName) {
    }
}
```

Create `core/src/main/java/org/apache/struts2/interceptor/DisableParams.java` (ASF header first):

```java
package org.apache.struts2.interceptor;

/**
 * Opt-in support for the {@code disabled} interceptor parameter.
 * <p>
 * Interceptors implementing {@link WithLazyParams} must have their params holder extend this
 * class to support {@code <param name="disabled">...</param>}; there is deliberately no
 * fallback to the interceptor instance, which would reintroduce shared mutable state.
 *
 * @since 7.3.0
 */
public class DisableParams implements InterceptorParams {

    private boolean disabled;

    public DisableParams() {
    }

    protected DisableParams(DisableParams other) {
        this.disabled = other.disabled;
    }

    /**
     * @param disable if {@code true}, execution of the interceptor is skipped for this invocation
     */
    public void setDisabled(String disable) {
        this.disabled = Boolean.parseBoolean(disable);
    }

    public boolean isDisabled() {
        return disabled;
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=DisableParamsTest`
Expected: PASS, 5 tests.

- [ ] **Step 5: Commit**

```bash
git add core/src/main/java/org/apache/struts2/interceptor/InterceptorParams.java \
        core/src/main/java/org/apache/struts2/interceptor/DisableParams.java \
        core/src/test/java/org/apache/struts2/interceptor/DisableParamsTest.java
git commit -m "WW-5659 feat(core): add InterceptorParams contract and DisableParams holder"
```

---

### Task 2: `LazyParamInjector.resolveInto`

Adds the new resolution method **alongside** the existing `injectParams`, so the tree keeps compiling. `injectParams` is removed in Task 4 when the call site moves.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/interceptor/WithLazyParams.java:78-84`
- Test: `core/src/test/java/org/apache/struts2/interceptor/LazyParamInjectorTest.java`

**Interfaces:**
- Consumes: `InterceptorParams` from Task 1.
- Produces: `public <P extends InterceptorParams> P resolveInto(P target, Map<String, String> params, ActionContext invocationContext)` on `WithLazyParams.LazyParamInjector`.

- [ ] **Step 1: Write the failing test**

Create `core/src/test/java/org/apache/struts2/interceptor/LazyParamInjectorTest.java` (ASF header first):

```java
package org.apache.struts2.interceptor;

import org.apache.struts2.ActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LazyParamInjectorTest extends StrutsInternalTestCase {

    public static class Holder extends DisableParams {
        private String name;
        private Long size;
        private final List<String> unresolvedCalls = new ArrayList<>();

        public void setName(String name) { this.name = name; }
        public void setSize(Long size) { this.size = size; }
        public String getName() { return name; }
        public Long getSize() { return size; }
        public List<String> getUnresolvedCalls() { return unresolvedCalls; }

        @Override
        public void unresolved(String paramName) { unresolvedCalls.add(paramName); }
    }

    public static class Bean {
        public String getLabel() { return "resolved-label"; }
        public Long getLimit() { return 4096L; }
    }

    private ActionContext context;
    private WithLazyParams.LazyParamInjector injector;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.push(new Bean());
        context = ActionContext.of(stack.getContext()).withContainer(container).withValueStack(stack).bind();
        injector = new WithLazyParams.LazyParamInjector(stack);
        container.inject(injector);
    }

    @Override
    protected void tearDown() throws Exception {
        ActionContext.clear();
        super.tearDown();
    }

    public void testResolvesExpressionsIntoTheHolder() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "${label}");

        Holder holder = injector.resolveInto(new Holder(), params, context);

        assertThat(holder.getName()).isEqualTo("resolved-label");
        assertThat(holder.getUnresolvedCalls()).isEmpty();
    }

    public void testAppliesOgnlTypeConversion() {
        Map<String, String> params = new HashMap<>();
        params.put("size", "${limit}");

        Holder holder = injector.resolveInto(new Holder(), params, context);

        assertThat(holder.getSize()).isEqualTo(4096L);
    }

    public void testPassesLiteralValuesThrough() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "plain-text");

        Holder holder = injector.resolveInto(new Holder(), params, context);

        assertThat(holder.getName()).isEqualTo("plain-text");
        assertThat(holder.getUnresolvedCalls()).isEmpty();
    }

    public void testUnresolvableExpressionSkipsWriteAndNotifiesHolder() {
        Holder seeded = new Holder();
        seeded.setName("seeded-value");

        Map<String, String> params = new HashMap<>();
        params.put("name", "${noSuchProperty}");

        Holder holder = injector.resolveInto(seeded, params, context);

        assertThat(holder.getName()).isEqualTo("seeded-value");
        assertThat(holder.getUnresolvedCalls()).containsExactly("name");
    }

    public void testResolvesDisabledOntoDisableParams() {
        Map<String, String> params = new HashMap<>();
        params.put("disabled", "true");

        Holder holder = injector.resolveInto(new Holder(), params, context);

        assertThat(holder.isDisabled()).isTrue();
    }

    public void testUnknownParamIsIgnoredWithoutFailingTheInvocation() {
        Map<String, String> params = new HashMap<>();
        params.put("noSuchParam", "whatever");

        Holder holder = injector.resolveInto(new Holder(), params, context);

        assertThat(holder.getName()).isNull();
        assertThat(holder.getSize()).isNull();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=LazyParamInjectorTest`
Expected: compilation failure — `resolveInto` does not exist.

- [ ] **Step 3: Write minimal implementation**

In `WithLazyParams.java`, add these imports:

```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.util.reflection.ReflectionException;
```

Add a logger as the first member of `class LazyParamInjector`:

```java
private static final Logger LOG = LogManager.getLogger(LazyParamInjector.class);
```

Add the new method **below** the existing `injectParams` (leave `injectParams` in place for now):

```java
/**
 * Resolves configured params into a per-invocation holder, leaving the interceptor untouched.
 * <p>
 * A {@code ${...}} expression that cannot be resolved is not written: the holder keeps its
 * seeded configuration value and is notified via {@link InterceptorParams#unresolved(String)},
 * so a broken expression cannot silently relax a validation policy.
 *
 * @since 7.3.0
 */
public <P extends InterceptorParams> P resolveInto(P target, Map<String, String> params, ActionContext invocationContext) {
    for (Map.Entry<String, String> entry : params.entrySet()) {
        String paramName = entry.getKey();
        String rawValue = entry.getValue();
        Object paramValue = textParser.evaluate(new char[]{'$'}, rawValue, valueEvaluator, TextParser.DEFAULT_LOOP_COUNT);

        if (isUnresolved(rawValue, paramValue)) {
            LOG.warn("Param [{}] of [{}] could not be resolved from expression [{}]; keeping the configured value",
                    paramName, target.getClass().getName(), rawValue);
            target.unresolved(paramName);
            continue;
        }
        try {
            // throwPropertyExceptions=true so a param with no matching property on the holder is
            // reported rather than silently ignored; OgnlUtil only warns in devMode otherwise
            ognlUtil.setProperty(paramName, paramValue, target, invocationContext.getContextMap(), true);
        } catch (ReflectionException e) {
            LOG.warn("Param [{}] cannot be applied to [{}]; check the interceptor configuration",
                    paramName, target.getClass().getName(), e);
        }
    }
    return target;
}

/**
 * {@link org.apache.struts2.util.OgnlTextParser} yields an empty string for an expression that
 * does not resolve and gives no other signal, so the raw template is needed to tell that apart
 * from a legitimately empty value.
 */
private boolean isUnresolved(String rawValue, Object paramValue) {
    return rawValue != null
            && rawValue.contains("${")
            && (paramValue == null || paramValue.toString().isEmpty());
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -DskipAssembly -pl core -Dtest=LazyParamInjectorTest`
Expected: PASS, 6 tests.

- [ ] **Step 5: Confirm nothing else broke**

Run: `mvn test -DskipAssembly -pl core -Dtest=ActionFileUploadInterceptorTest,DefaultActionInvocationTest`
Expected: PASS — `injectParams` is untouched, so existing behaviour is unchanged.

- [ ] **Step 6: Commit**

```bash
git add core/src/main/java/org/apache/struts2/interceptor/WithLazyParams.java \
        core/src/test/java/org/apache/struts2/interceptor/LazyParamInjectorTest.java
git commit -m "WW-5659 feat(core): resolve lazy params into a holder instead of the interceptor"
```

---

### Task 3: `UploadPolicy` and the `acceptFile` signature

Pure refactor: the interceptor stops holding three loose fields and holds one config-time `UploadPolicy`; `acceptFile` receives the policy explicitly. **No behaviour change** — the old `injectParams` path still mutates the singleton via the setters, which now delegate into `configuredPolicy`. Task 4 removes that.

**Files:**
- Create: `core/src/main/java/org/apache/struts2/interceptor/UploadPolicy.java`
- Modify: `core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java:62-64, 84-104, 116-166`
- Modify: `core/src/main/java/org/apache/struts2/interceptor/ActionFileUploadInterceptor.java:210-264`

**Interfaces:**
- Consumes: `DisableParams` from Task 1.
- Produces: `class UploadPolicy extends DisableParams` with `setMaximumSize(Long)`, `setAllowedTypes(String)`, `setAllowedExtensions(String)`, `getMaximumSize()`, `getAllowedTypes()`, `getAllowedExtensions()`, `copy()`; and `protected boolean acceptFile(UploadPolicy policy, Object action, UploadedFile file, String originalFilename, String contentType, String inputName)`.

- [ ] **Step 1: Write the failing test**

Append to `core/src/test/java/org/apache/struts2/interceptor/ActionFileUploadInterceptorTest.java`, before the closing brace:

```java
    public void testUploadPolicyParsesAndCopies() {
        UploadPolicy policy = new UploadPolicy();
        policy.setAllowedTypes("text/plain, text/html");
        policy.setAllowedExtensions(".txt,.html");
        policy.setMaximumSize(1024L);
        policy.setDisabled("true");

        UploadPolicy copy = policy.copy();

        assertThat(copy.getAllowedTypes()).containsExactlyInAnyOrder("text/plain", "text/html");
        assertThat(copy.getAllowedExtensions()).containsExactlyInAnyOrder(".txt", ".html");
        assertThat(copy.getMaximumSize()).isEqualTo(1024L);
        assertThat(copy.isDisabled()).isTrue();
    }

    public void testUploadPolicyCopyIsIndependentOfTheOriginal() {
        UploadPolicy policy = new UploadPolicy();
        policy.setAllowedTypes("text/plain");

        UploadPolicy copy = policy.copy();
        copy.setAllowedTypes("text/html");

        assertThat(policy.getAllowedTypes()).containsExactly("text/plain");
        assertThat(copy.getAllowedTypes()).containsExactly("text/html");
    }

    public void testUploadPolicyTreatsNullAsNoRestriction() {
        UploadPolicy policy = new UploadPolicy();
        policy.setAllowedTypes(null);
        policy.setAllowedExtensions(null);

        assertThat(policy.getAllowedTypes()).isEmpty();
        assertThat(policy.getAllowedExtensions()).isEmpty();
    }
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -DskipAssembly -pl core -Dtest=ActionFileUploadInterceptorTest#testUploadPolicyParsesAndCopies`
Expected: compilation failure — `UploadPolicy` does not exist.

- [ ] **Step 3: Create `UploadPolicy`**

Create `core/src/main/java/org/apache/struts2/interceptor/UploadPolicy.java` (ASF header first):

```java
package org.apache.struts2.interceptor;

import org.apache.struts2.util.TextParseUtil;

import java.util.Collections;
import java.util.Set;

/**
 * Per-invocation upload validation policy for {@link ActionFileUploadInterceptor}.
 * <p>
 * A configured instance is held by the interceptor and copied for each invocation, so lazily
 * resolved values never reach the shared interceptor.
 *
 * @since 7.3.0
 */
public class UploadPolicy extends DisableParams {

    private Long maximumSize;
    private Set<String> allowedTypes = Collections.emptySet();
    private Set<String> allowedExtensions = Collections.emptySet();

    public UploadPolicy() {
    }

    private UploadPolicy(UploadPolicy other) {
        super(other);
        this.maximumSize = other.maximumSize;
        this.allowedTypes = other.allowedTypes;
        this.allowedExtensions = other.allowedExtensions;
    }

    /**
     * @param allowedTypes a comma-delimited list of content types, or null for no restriction
     */
    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = toSet(allowedTypes);
    }

    /**
     * @param allowedExtensions a comma-delimited list of extensions, or null for no restriction
     */
    public void setAllowedExtensions(String allowedExtensions) {
        this.allowedExtensions = toSet(allowedExtensions);
    }

    /**
     * @param maximumSize the maximum size in bytes, or null for no limit
     */
    public void setMaximumSize(Long maximumSize) {
        this.maximumSize = maximumSize;
    }

    public Long getMaximumSize() {
        return maximumSize;
    }

    public Set<String> getAllowedTypes() {
        return allowedTypes;
    }

    public Set<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    /**
     * @return an independent copy, used to seed a per-invocation policy from the configured one
     */
    public UploadPolicy copy() {
        return new UploadPolicy(this);
    }

    private static Set<String> toSet(String commaDelimited) {
        return commaDelimited == null
                ? Collections.emptySet()
                : TextParseUtil.commaDelimitedStringToSet(commaDelimited);
    }
}
```

Note the null guard: `TextParseUtil.commaDelimitedStringToSet` throws `NullPointerException` on null (`TextParseUtil.java:257` calls `s.split(",")` unguarded), and the old setters had the same defect. Treating null as "no restriction" matches how an empty value already behaves.

- [ ] **Step 4: Rewrite the policy state in `AbstractFileUploadInterceptor`**

Replace the three fields at `:62-64`:

```java
    private Long maximumSize;
    private Set<String> allowedTypesSet = Collections.emptySet();
    private Set<String> allowedExtensionsSet = Collections.emptySet();
```

with:

```java
    private final UploadPolicy configuredPolicy = new UploadPolicy();
```

Replace the three setters at `:84-104` with delegating versions:

```java
    /**
     * Sets the allowed extensions. Applied at configuration time only; the effective policy for
     * an invocation is a copy, see {@link ActionFileUploadInterceptor#newLazyParams()}.
     *
     * @param allowedExtensions A comma-delimited list of extensions
     */
    public void setAllowedExtensions(String allowedExtensions) {
        configuredPolicy.setAllowedExtensions(allowedExtensions);
    }

    /**
     * Sets the allowed mimetypes. Applied at configuration time only; the effective policy for
     * an invocation is a copy, see {@link ActionFileUploadInterceptor#newLazyParams()}.
     *
     * @param allowedTypes A comma-delimited list of types
     */
    public void setAllowedTypes(String allowedTypes) {
        configuredPolicy.setAllowedTypes(allowedTypes);
    }

    /**
     * Sets the maximum size of an uploaded file. Applied at configuration time only; the
     * effective policy for an invocation is a copy, see
     * {@link ActionFileUploadInterceptor#newLazyParams()}.
     *
     * @param maximumSize The maximum size in bytes
     */
    public void setMaximumSize(Long maximumSize) {
        configuredPolicy.setMaximumSize(maximumSize);
    }

    /**
     * @return an independent copy of the configured policy, to be resolved for one invocation
     * @since 7.3.0
     */
    protected UploadPolicy copyConfiguredPolicy() {
        return configuredPolicy.copy();
    }
```

- [ ] **Step 5: Change `acceptFile` to take the policy**

Replace the signature and the three checks at `:116` and `:134-154`:

```java
    protected boolean acceptFile(UploadPolicy policy, Object action, UploadedFile file, String originalFilename, String contentType, String inputName) {
```

and inside it:

```java
        if (policy.getMaximumSize() != null && policy.getMaximumSize() < file.length()) {
            String errMsg = getTextMessage(action, STRUTS_MESSAGES_ERROR_FILE_TOO_LARGE_KEY, new String[]{
                inputName, originalFilename, file.getName(), "" + file.length(), getMaximumSizeStr(action, policy.getMaximumSize())
            });
            errorMessages.add(errMsg);
            LOG.warn(errMsg);
        }
        if ((!policy.getAllowedTypes().isEmpty()) && (!containsItem(policy.getAllowedTypes(), contentType))) {
            String errMsg = getTextMessage(action, STRUTS_MESSAGES_ERROR_CONTENT_TYPE_NOT_ALLOWED_KEY, new String[]{
                inputName, originalFilename, file.getName(), contentType
            });
            errorMessages.add(errMsg);
            LOG.warn(errMsg);
        }
        if ((!policy.getAllowedExtensions().isEmpty()) && (!hasAllowedExtension(policy.getAllowedExtensions(), originalFilename))) {
            String errMsg = getTextMessage(action, STRUTS_MESSAGES_ERROR_FILE_EXTENSION_NOT_ALLOWED_KEY, new String[]{
                inputName, originalFilename, file.getName(), contentType
            });
            errorMessages.add(errMsg);
            LOG.warn(errMsg);
        }
```

Replace `getMaximumSizeStr` at `:164-166`:

```java
    private String getMaximumSizeStr(Object action, Long maximumSize) {
        return NumberFormat.getNumberInstance(getLocaleProvider(action).getLocale()).format(maximumSize);
    }
```

Remove the now-unused `java.util.Collections` import if the compiler flags it.

- [ ] **Step 6: Update the single caller**

In `ActionFileUploadInterceptor.java`, at the top of `intercept` (`:210`), after the `UploadedFilesAware` check at `:229`, add:

```java
        UploadPolicy policy = copyConfiguredPolicy();
```

and change the call at `:248`:

```java
                    if (acceptFile(policy, action, uploadedFile, uploadedFile.getOriginalName(), uploadedFile.getContentType(), inputName)) {
```

- [ ] **Step 7: Run tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=ActionFileUploadInterceptorTest`
Expected: PASS — the three new `UploadPolicy` tests plus every pre-existing test, unchanged. If a pre-existing test fails, the refactor changed behaviour and must be corrected, not the test.

- [ ] **Step 8: Commit**

```bash
git add core/src/main/java/org/apache/struts2/interceptor/UploadPolicy.java \
        core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java \
        core/src/main/java/org/apache/struts2/interceptor/ActionFileUploadInterceptor.java \
        core/src/test/java/org/apache/struts2/interceptor/ActionFileUploadInterceptorTest.java
git commit -m "WW-5659 refactor(core): hold upload policy in one value object"
```

---

### Task 4: New `WithLazyParams` contract and invocation wiring

The behaviour change. After this task the interceptor singleton is never written per request.

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/interceptor/WithLazyParams.java`
- Modify: `core/src/main/java/org/apache/struts2/interceptor/ActionFileUploadInterceptor.java`
- Modify: `core/src/main/java/org/apache/struts2/DefaultActionInvocation.java:258-276`
- Test: `core/src/test/java/org/apache/struts2/interceptor/ActionFileUploadInterceptorTest.java`

**Interfaces:**
- Consumes: `resolveInto` (Task 2), `UploadPolicy` and `copyConfiguredPolicy()` (Task 3).
- Produces: `interface WithLazyParams<P extends InterceptorParams> { P newLazyParams(); String intercept(ActionInvocation, P) throws Exception; }`; `ActionFileUploadInterceptor implements WithLazyParams<UploadPolicy>`.

- [ ] **Step 1: Write the failing tests**

Append to `ActionFileUploadInterceptorTest`, before the closing brace. The first test is carried over from PR #1815 by @deprrous, adapted to the new contract — keep the attribution comment.

```java
    /**
     * Regression for WW-5659: two concurrent invocations resolving different policies must not
     * see each other's values. Scenario contributed by @deprrous in GitHub PR #1815.
     */
    public void testConcurrentDynamicPoliciesStayIsolatedPerRequest() throws Exception {
        CoordinatedActionFileUploadInterceptor sharedInterceptor = new CoordinatedActionFileUploadInterceptor();
        container.inject(sharedInterceptor);

        MyDynamicFileUploadAction plainPolicyAction = new MyDynamicFileUploadAction();
        plainPolicyAction.setAllowedMimeTypes("text/plain");
        container.inject(plainPolicyAction);

        MyDynamicFileUploadAction htmlPolicyAction = new MyDynamicFileUploadAction();
        htmlPolicyAction.setAllowedMimeTypes("text/html");
        container.inject(htmlPolicyAction);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<String> plainResult = executor.submit(() -> runUploadAttempt(
                    sharedInterceptor, plainPolicyAction, createUploadRequest("plain-policy.html", "text/html", htmlContent)));

            assertThat(sharedInterceptor.awaitFirstValidation()).isTrue();

            Future<String> htmlResult = executor.submit(() -> runUploadAttempt(
                    sharedInterceptor, htmlPolicyAction, createUploadRequest("html-policy.html", "text/html", htmlContent)));

            assertThat(htmlResult.get(10, TimeUnit.SECONDS)).isEqualTo("success");
            sharedInterceptor.releaseFirstValidation();
            assertThat(plainResult.get(10, TimeUnit.SECONDS)).isEqualTo("success");
        } finally {
            sharedInterceptor.releaseFirstValidation();
            executor.shutdownNow();
            sharedInterceptor.destroy();
        }

        // the text/plain policy must have rejected the text/html upload despite the concurrent
        // text/html invocation resolving a more permissive policy on the same interceptor
        assertThat(plainPolicyAction.getUploadFiles()).isNull();
        assertThat(plainPolicyAction.getFieldErrors()).containsKey("file");

        assertThat(htmlPolicyAction.hasFieldErrors()).isFalse();
        assertThat(htmlPolicyAction.getUploadFiles()).isNotNull().hasSize(1);
        assertThat(htmlPolicyAction.getUploadFiles().get(0).getOriginalName()).isEqualTo("html-policy.html");
    }

    /**
     * Regression for WW-5659: resolving an invocation's params must leave the interceptor
     * singleton exactly as configured.
     */
    public void testResolutionDoesNotMutateTheInterceptor() throws Exception {
        ActionFileUploadInterceptor interceptor = new ActionFileUploadInterceptor();
        container.inject(interceptor);
        interceptor.setAllowedTypes("text/plain");

        MyDynamicFileUploadAction action = new MyDynamicFileUploadAction();
        action.setAllowedMimeTypes("text/html");
        container.inject(action);

        runUploadAttempt(interceptor, action, createUploadRequest("f.html", "text/html", htmlContent));

        assertThat(interceptor.newLazyParams().getAllowedTypes()).containsExactly("text/plain");
    }

    /**
     * Regression for WW-5659: a lazily resolved {@code disabled} must apply to one invocation only.
     */
    public void testDisabledIsResolvedPerInvocation() throws Exception {
        ActionFileUploadInterceptor interceptor = new ActionFileUploadInterceptor();
        container.inject(interceptor);

        MyDynamicFileUploadAction action = new MyDynamicFileUploadAction();
        action.setAllowedMimeTypes("text/plain");
        container.inject(action);

        UploadPolicy policy = interceptor.newLazyParams();
        policy.setDisabled("true");

        assertThat(policy.isDisabled()).isTrue();
        assertThat(interceptor.newLazyParams().isDisabled()).isFalse();
    }
```

Add these helpers — none of them exist in the test class yet. `runUploadAttempt`, `createUploadRequest`, the `MockHttpServletRequest` overload of `createMultipartRequest` and `CoordinatedActionFileUploadInterceptor` all originate in PR #1815 by @deprrous and are reproduced here, adapted to the new contract, so this task is self-contained:

```java
    private String runUploadAttempt(ActionFileUploadInterceptor actionFileUploadInterceptor,
                                    MyDynamicFileUploadAction action,
                                    MockHttpServletRequest uploadRequest) throws Exception {
        MultiPartRequestWrapper multiPartRequest = createMultipartRequest(uploadRequest, -1, -1, 3, -1);
        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(action);

        ActionContext context = ActionContext.of(valueStack.getContext())
                .withContainer(container)
                .withValueStack(valueStack)
                .withServletRequest(multiPartRequest)
                .bind();
        try {
            MockActionInvocation invocation = new MockActionInvocation();
            invocation.setAction(action);
            invocation.setResultCode("success");
            invocation.setInvocationContext(context);

            Map<String, String> params = new HashMap<>();
            params.put("allowedTypes", "${allowedMimeTypes}");

            WithLazyParams.LazyParamInjector injector = new WithLazyParams.LazyParamInjector(valueStack);
            container.inject(injector);
            UploadPolicy policy = injector.resolveInto(actionFileUploadInterceptor.newLazyParams(), params, context);

            return actionFileUploadInterceptor.intercept(invocation, policy);
        } finally {
            ActionContext.clear();
        }
    }

    private MockHttpServletRequest createUploadRequest(String filename, String contentType, String content) {
        MockHttpServletRequest uploadRequest = new MockHttpServletRequest();
        uploadRequest.setCharacterEncoding(StandardCharsets.UTF_8.name());
        uploadRequest.setMethod("POST");
        uploadRequest.addHeader("Content-type", "multipart/form-data; boundary=\"" + boundary + "\"");
        uploadRequest.setContent((encodeTextFile(filename, contentType, content) + endLine + "--" + boundary + "--")
                .getBytes(StandardCharsets.UTF_8));
        return uploadRequest;
    }

    private MultiPartRequestWrapper createMultipartRequest(MockHttpServletRequest multipartRequest, int maxsize, int maxfilesize, int maxfiles, int maxStringLength) {
        JakartaMultiPartRequest jak = new JakartaMultiPartRequest();
        jak.setMaxSize(String.valueOf(maxsize));
        jak.setMaxFileSize(String.valueOf(maxfilesize));
        jak.setMaxFiles(String.valueOf(maxfiles));
        jak.setMaxStringLength(String.valueOf(maxStringLength));
        jak.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return new MultiPartRequestWrapper(jak, multipartRequest, tempDir.getAbsolutePath(), new DefaultLocaleProvider());
    }

    /** Pauses the first validation so a second invocation can overlap it. From PR #1815 by @deprrous. */
    private static final class CoordinatedActionFileUploadInterceptor extends ActionFileUploadInterceptor {
        private final AtomicBoolean pauseFirstValidation = new AtomicBoolean(true);
        private final CountDownLatch firstValidationEntered = new CountDownLatch(1);
        private final CountDownLatch allowFirstValidationToContinue = new CountDownLatch(1);

        @Override
        protected boolean acceptFile(UploadPolicy policy, Object action, UploadedFile file, String originalFilename, String contentType, String inputName) {
            if (pauseFirstValidation.compareAndSet(true, false)) {
                firstValidationEntered.countDown();
                awaitUnchecked(allowFirstValidationToContinue);
            }
            return super.acceptFile(policy, action, file, originalFilename, contentType, inputName);
        }

        private boolean awaitFirstValidation() throws InterruptedException {
            return firstValidationEntered.await(10, TimeUnit.SECONDS);
        }

        private void releaseFirstValidation() {
            allowFirstValidationToContinue.countDown();
        }

        private void awaitUnchecked(CountDownLatch latch) {
            try {
                if (!latch.await(10, TimeUnit.SECONDS)) {
                    throw new AssertionError("Timed out waiting for concurrent validation release");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError("Interrupted while waiting for concurrent validation release", e);
            }
        }
    }
```

To avoid duplicating the body, change the existing no-request `createMultipartRequest(int, int, int, int)` to delegate to the new overload:

```java
    private MultiPartRequestWrapper createMultipartRequest(int maxsize, int maxfilesize, int maxfiles, int maxStringLength) {
        return createMultipartRequest(request, maxsize, maxfilesize, maxfiles, maxStringLength);
    }
```

Required test imports:

```java
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=ActionFileUploadInterceptorTest#testResolutionDoesNotMutateTheInterceptor`
Expected: compilation failure — `newLazyParams()` and the two-arg `intercept` do not exist.

- [ ] **Step 3: Change the `WithLazyParams` interface**

Replace the interface declaration in `WithLazyParams.java` (keep `LazyParamInjector` nested inside it unchanged apart from the deletion below):

```java
public interface WithLazyParams<P extends InterceptorParams> {

    /**
     * @return a fresh holder for one invocation, seeded from the configured values
     * @since 7.3.0
     */
    P newLazyParams();

    /**
     * Invoked in place of {@link Interceptor#intercept(ActionInvocation)} when lazy params apply.
     *
     * @param lazyParams params resolved for this invocation only
     * @since 7.3.0
     */
    String intercept(ActionInvocation invocation, P lazyParams) throws Exception;
```

Add the import `org.apache.struts2.ActionInvocation` and delete the old `injectParams` method entirely.

- [ ] **Step 4: Implement the contract on the interceptor**

In `ActionFileUploadInterceptor.java`, change the class declaration:

```java
public class ActionFileUploadInterceptor extends AbstractFileUploadInterceptor implements WithLazyParams<UploadPolicy> {
```

Add, above `intercept`:

```java
    @Override
    public UploadPolicy newLazyParams() {
        return copyConfiguredPolicy();
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        return intercept(invocation, newLazyParams());
    }
```

Change the existing `intercept` to the two-arg form, replacing the `UploadPolicy policy = copyConfiguredPolicy();` line added in Task 3:

```java
    @Override
    public String intercept(ActionInvocation invocation, UploadPolicy policy) throws Exception {
```

- [ ] **Step 5: Wire `DefaultActionInvocation`**

Replace `:258-276` with:

```java
            if (interceptors.hasNext()) {
                final InterceptorMapping interceptorMapping = interceptors.next();
                Interceptor interceptor = interceptorMapping.getInterceptor();
                if (interceptor instanceof WithLazyParams<?> lazyInterceptor) {
                    resultCode = invokeWithLazyParams(lazyInterceptor, interceptorMapping);
                } else if (interceptor instanceof ConditionalInterceptor conditionalInterceptor) {
                    resultCode = executeConditional(conditionalInterceptor);
                } else {
                    LOG.debug("Executing normal interceptor: {}", interceptorMapping.getName());
                    resultCode = interceptor.intercept(this);
                }
            } else {
                resultCode = invokeActionOnly();
            }
```

Add these two methods next to `executeConditional`:

```java
    /**
     * Resolves lazy params into a per-invocation holder and dispatches to the interceptor.
     * <p>
     * {@link org.apache.struts2.interceptor.AbstractInterceptor} implements
     * {@link ConditionalInterceptor}, so a lazy interceptor is normally conditional too; both the
     * lazily resolved {@code disabled} flag and any custom {@code shouldIntercept} must be honoured
     * here, because the single-argument {@code intercept} is not the entry point on this path.
     */
    private <P extends org.apache.struts2.interceptor.InterceptorParams> String invokeWithLazyParams(
            WithLazyParams<P> lazyInterceptor, InterceptorMapping interceptorMapping) throws Exception {
        P lazyParams = lazyParamInjector.resolveInto(
                lazyInterceptor.newLazyParams(), mergedParams(interceptorMapping), invocationContext);

        if (lazyParams instanceof org.apache.struts2.interceptor.DisableParams disableParams && disableParams.isDisabled()) {
            LOG.debug("Interceptor: {} is disabled for this invocation, skipping to next", interceptorMapping.getName());
            return this.invoke();
        }
        if (lazyInterceptor instanceof ConditionalInterceptor conditionalInterceptor
                && !conditionalInterceptor.shouldIntercept(this)) {
            LOG.debug("Interceptor: {} is disabled, skipping to next", interceptorMapping.getName());
            return this.invoke();
        }
        LOG.debug("Executing lazy params interceptor: {}", interceptorMapping.getName());
        return lazyInterceptor.intercept(this, lazyParams);
    }

    /**
     * @return a fresh map; the mapping's own param map is shared across requests and must not be mutated
     */
    private Map<String, String> mergedParams(InterceptorMapping interceptorMapping) {
        Map<String, String> merged = new HashMap<>(interceptorMapping.getParams());
        proxy.getConfig().getInterceptors().stream()
                .filter(im -> im.getName().equals(interceptorMapping.getName()))
                .findFirst()
                .ifPresent(im -> merged.putAll(im.getParams()));
        return merged;
    }
```

Add `import java.util.HashMap;` if not present.

- [ ] **Step 6: Run the new tests**

Run: `mvn test -DskipAssembly -pl core -Dtest=ActionFileUploadInterceptorTest`
Expected: PASS, including the three new regressions.

- [ ] **Step 7: Run the wider suite**

Run: `mvn test -DskipAssembly -pl core`
Expected: PASS. `DefaultActionInvocationTest` exercises the rewritten `invoke()` branch and must stay green.

- [ ] **Step 8: Commit**

```bash
git add core/src/main/java/org/apache/struts2/interceptor/WithLazyParams.java \
        core/src/main/java/org/apache/struts2/interceptor/ActionFileUploadInterceptor.java \
        core/src/main/java/org/apache/struts2/DefaultActionInvocation.java \
        core/src/test/java/org/apache/struts2/interceptor/ActionFileUploadInterceptorTest.java
git commit -m "WW-5659 fix(core): resolve lazy interceptor params per invocation

Co-Authored-By: deprrous <sukhbatsuugii2004@gmail.com>"
```

---

### Task 5: Fail closed on unresolvable expressions

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/interceptor/UploadPolicy.java`
- Modify: `core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java`
- Modify: `core/src/main/resources/org/apache/struts2/struts-messages.properties`
- Test: `core/src/test/java/org/apache/struts2/interceptor/ActionFileUploadInterceptorTest.java`

**Interfaces:**
- Consumes: `InterceptorParams.unresolved(String)` (Task 1), `resolveInto` (Task 2), `UploadPolicy` (Task 3).
- Produces: `UploadPolicy.isUnresolved()`, `UploadPolicy.getUnresolvedParams()`; constant `STRUTS_MESSAGES_ERROR_UPLOAD_POLICY_UNRESOLVED_KEY`.

Implements the deterministic rule from the *Deviation from the spec* section: any param reported unresolved marks the policy unusable, regardless of the seeded value.

- [ ] **Step 1: Write the failing test**

Append to `ActionFileUploadInterceptorTest`:

```java
    public void testUnresolvedPolicyRejectsTheUpload() throws Exception {
        ActionFileUploadInterceptor interceptor = new ActionFileUploadInterceptor();
        container.inject(interceptor);

        MyDynamicFileUploadAction action = new MyDynamicFileUploadAction();
        action.setAllowedMimeTypes(null);   // ${allowedMimeTypes} will not resolve
        container.inject(action);

        runUploadAttempt(interceptor, action, createUploadRequest("f.txt", "text/plain", plainContent));

        assertThat(action.getUploadFiles()).isNull();
        assertThat(action.getFieldErrors()).containsKey("file");
    }

    public void testResolvedPolicyStillAcceptsTheUpload() throws Exception {
        ActionFileUploadInterceptor interceptor = new ActionFileUploadInterceptor();
        container.inject(interceptor);

        MyDynamicFileUploadAction action = new MyDynamicFileUploadAction();
        action.setAllowedMimeTypes("text/plain");
        container.inject(action);

        runUploadAttempt(interceptor, action, createUploadRequest("f.txt", "text/plain", plainContent));

        assertThat(action.hasFieldErrors()).isFalse();
        assertThat(action.getUploadFiles()).isNotNull().hasSize(1);
    }

    public void testUploadPolicyTracksUnresolvedParams() {
        UploadPolicy policy = new UploadPolicy();
        assertThat(policy.isUnresolved()).isFalse();

        policy.unresolved("allowedTypes");

        assertThat(policy.isUnresolved()).isTrue();
        assertThat(policy.getUnresolvedParams()).containsExactly("allowedTypes");
    }
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `mvn test -DskipAssembly -pl core -Dtest=ActionFileUploadInterceptorTest#testUploadPolicyTracksUnresolvedParams`
Expected: compilation failure — `isUnresolved()` does not exist.

- [ ] **Step 3: Track unresolved params on the policy**

In `UploadPolicy.java` add the field, copy it, and override `unresolved`:

```java
    private final Set<String> unresolvedParams = new LinkedHashSet<>();
```

In the copy constructor add:

```java
        this.unresolvedParams.addAll(other.unresolvedParams);
```

and add:

```java
    /**
     * A parameter that could not be resolved makes this policy unusable: the upload is rejected
     * rather than validated against a partially-resolved policy, so a broken expression cannot
     * silently relax validation.
     */
    @Override
    public void unresolved(String paramName) {
        unresolvedParams.add(paramName);
    }

    public boolean isUnresolved() {
        return !unresolvedParams.isEmpty();
    }

    public Set<String> getUnresolvedParams() {
        return Collections.unmodifiableSet(unresolvedParams);
    }
```

Add imports `java.util.LinkedHashSet`.

- [ ] **Step 4: Reject in `acceptFile`**

In `AbstractFileUploadInterceptor.java` add the key constant next to the others at `:47-60`:

```java
    public static final String STRUTS_MESSAGES_ERROR_UPLOAD_POLICY_UNRESOLVED_KEY = "struts.messages.error.upload.policy.unresolved";
```

In `acceptFile`, immediately after the existing missing-file check that returns false at `:125-132`, add:

```java
        if (policy.isUnresolved()) {
            String errMsg = getTextMessage(action, STRUTS_MESSAGES_ERROR_UPLOAD_POLICY_UNRESOLVED_KEY, new String[]{
                inputName, originalFilename, String.join(", ", policy.getUnresolvedParams())
            });
            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }
            LOG.warn(errMsg);
            return false;
        }
```

- [ ] **Step 5: Add the message**

Append to `core/src/main/resources/org/apache/struts2/struts-messages.properties`:

```properties
struts.messages.error.upload.policy.unresolved=The upload validation policy could not be resolved, rejecting the file: {0} "{1}"; unresolved parameters: {2}
```

Base bundle only — the locale bundles are partial and fall back to it.

- [ ] **Step 6: Run tests to verify they pass**

Run: `mvn test -DskipAssembly -pl core -Dtest=ActionFileUploadInterceptorTest`
Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add core/src/main/java/org/apache/struts2/interceptor/UploadPolicy.java \
        core/src/main/java/org/apache/struts2/interceptor/AbstractFileUploadInterceptor.java \
        core/src/main/resources/org/apache/struts2/struts-messages.properties \
        core/src/test/java/org/apache/struts2/interceptor/ActionFileUploadInterceptorTest.java
git commit -m "WW-5659 fix(core): reject uploads when the policy cannot be resolved"
```

---

### Task 6: Migrate legacy tests, javadoc, and spec alignment

**Files:**
- Modify: `core/src/test/java/org/apache/struts2/interceptor/ActionFileUploadInterceptorTest.java`
- Modify: `core/src/main/java/org/apache/struts2/interceptor/ActionFileUploadInterceptor.java:70-204` (class javadoc)
- Modify: `docs/superpowers/specs/2026-07-27-WW-5659-lazy-params-request-scoping-design.md`

**Interfaces:**
- Consumes: everything from Tasks 1–5.
- Produces: nothing new.

- [ ] **Step 1: Migrate the setter-shortcut tests**

Five tests simulate injection by calling a setter directly — `testDynamicParameterEvaluation`, `testDynamicParametersChangePerRequest`, `testDynamicExtensionValidation`, `testDynamicMaximumSizeValidation`, `testSecurityValidationWithDynamicParameters`, `testWildcardMatchingWithDynamicParameters`. Each contains a line of the form `interceptor.setAllowedTypes(action.getAllowedMimeTypes());` followed by `interceptor.intercept(mai);`.

Replace each such pair with a real resolution through the injector, e.g.:

```java
        UploadPolicy policy = injectDynamicUploadPolicy(interceptor, ActionContext.getContext(), true, false, false);
        interceptor.intercept(mai, policy);
```

and add the helper:

```java
    private UploadPolicy injectDynamicUploadPolicy(ActionFileUploadInterceptor actionFileUploadInterceptor,
                                                   ActionContext context,
                                                   boolean includeAllowedTypes,
                                                   boolean includeAllowedExtensions,
                                                   boolean includeMaximumSize) {
        Map<String, String> params = new HashMap<>();
        if (includeAllowedTypes) {
            params.put("allowedTypes", "${allowedMimeTypes}");
        }
        if (includeAllowedExtensions) {
            params.put("allowedExtensions", "${allowedExtensions}");
        }
        if (includeMaximumSize) {
            params.put("maximumSize", "${maxFileSize}");
        }

        WithLazyParams.LazyParamInjector injector = new WithLazyParams.LazyParamInjector(context.getValueStack());
        container.inject(injector);
        return injector.resolveInto(actionFileUploadInterceptor.newLazyParams(), params, context);
    }
```

Set the flags per test to match the param that test previously set by hand: `allowedTypes` for the four type/wildcard tests, `allowedExtensions` for `testDynamicExtensionValidation`, `maximumSize` for `testDynamicMaximumSizeValidation`, and both types and extensions for `testSecurityValidationWithDynamicParameters`.

- [ ] **Step 2: Run the suite**

Run: `mvn test -DskipAssembly -pl core -Dtest=ActionFileUploadInterceptorTest`
Expected: PASS. These tests now exercise the real resolution path rather than a hand-called setter.

- [ ] **Step 3: Update the interceptor javadoc**

`ActionFileUploadInterceptor`'s class javadoc at `:70-204` documents the dynamic-parameter feature with a `uploadConfig.setAllowedExtensions(".jpg,.png")` example. Add a paragraph directly above `@see WithLazyParams`:

```java
 * <p>
 * Dynamic parameters are resolved into a fresh {@link UploadPolicy} for each invocation, so the
 * interceptor itself is never modified per request and concurrent uploads cannot observe each
 * other's policy. An expression that cannot be resolved does not relax validation: the policy is
 * marked unresolved and affected uploads are rejected.
```

- [ ] **Step 4: Align the spec with the implemented rule**

In the spec's *Error handling* section, replace the two-case "genuine static fallback / no fallback" rule and the sentence beginning "The rule: `unresolved(param)` marks the dimension unusable **only if**..." with:

```markdown
`UploadPolicy.unresolved(param)` records the parameter and marks the whole policy unusable,
regardless of the seeded value. A static fallback therefore applies only when the param is absent
from the lazy map entirely, i.e. pure static configuration, which never triggers `unresolved`.

The seed-introspection alternative — honouring a seeded value that does not itself contain
`${` — was rejected during planning: it is not implementable deterministically, because
`maximumSize` is seeded as a `Long` and cannot carry a `${...}` literal, so the rule would behave
differently per parameter type.
```

Update the corresponding Testing bullet to drop the static-fallback case.

- [ ] **Step 5: Full build**

Run: `mvn test -DskipAssembly`
Expected: PASS across all modules. Plugins do not implement `WithLazyParams` (verified: `ActionFileUploadInterceptor` is the only implementer), but the interface change is compile-visible, so a full build is the check.

- [ ] **Step 6: Commit**

```bash
git add core/src/test/java/org/apache/struts2/interceptor/ActionFileUploadInterceptorTest.java \
        core/src/main/java/org/apache/struts2/interceptor/ActionFileUploadInterceptor.java \
        docs/superpowers/specs/2026-07-27-WW-5659-lazy-params-request-scoping-design.md
git commit -m "WW-5659 test(core): exercise real lazy param resolution in dynamic upload tests"
```

---

## Done criteria

- `mvn test -DskipAssembly` passes across all modules.
- `ActionFileUploadInterceptor` has no mutable state written after `init()`; `testResolutionDoesNotMutateTheInterceptor` proves it.
- No `ThreadLocal`, no `finally` cleanup, and no per-request write to any shared map remains on the lazy path.
- The spec and the implementation agree on the unresolved-param rule.
