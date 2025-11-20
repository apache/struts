---
date: 2025-11-20T15:58:08+01:00
last_updated: 2025-11-20T16:30:00+01:00
topic: "Why WithLazyParams Interceptor Parameters Were Empty During Action Invocation"
tags: [research, codebase, WW-5586, WW-5587, interceptors, WithLazyParams, configuration, bug]
status: complete
git_commit: 6cfd34c945893ad20c9f115319034469ff868cb7
related_issues: [WW-5586, WW-5587]
---

# Research: Why WithLazyParams Interceptor Parameters Were Empty During Action Invocation

**Date**: 2025-11-20T15:58:08+01:00
**Last Updated**: 2025-11-20T16:30:00+01:00
**Issues**: WW-5586 (partial fix), WW-5587 (InterceptorBuilder line 177 bug)
**Fix Commits**:
- 6cfd34c945893ad20c9f115319034469ff868cb7 (Nov 17, 2025) - WW-5586 (partial)
- Pending commit for WW-5587 fix

## Research Question

Why were interceptor parameters empty when using `WithLazyParams` during action invocation in `DefaultActionInvocation`, even though the test `XmlConfigurationProviderInterceptorParamOverridingTest` showed parameters being configured in XML?

## Summary

**TWO BUGS FOUND** - WW-5586 only fixed the first one:

### Bug #1: DefaultInterceptorFactory (FIXED in WW-5586)
`DefaultInterceptorFactory` **completely skipped property setting** for interceptors implementing `WithLazyParams`:
- Parameters from XML were never injected into the interceptor at factory time
- Interceptor instances had no property values set
- **Fix**: Always set properties for all interceptors at factory time

### Bug #2: InterceptorBuilder Line 177 (STILL BROKEN)
`InterceptorBuilder.constructParameterizedInterceptorReferences()` creates `InterceptorMapping` **without storing the params map**:
- Line 177: `new InterceptorMapping(key, interceptor)` uses 2-arg constructor
- This defaults to an **empty params map**
- `LazyParamInjector.injectParams()` receives empty map and cannot re-evaluate expressions
- **Fix needed**: `new InterceptorMapping(key, interceptor, map)` - add the `map` parameter

**Result**: Even after WW-5586, `WithLazyParams` interceptors still don't work in stack configurations because `InterceptorMapping.getParams()` returns empty.

## Detailed Findings

### The Problem (Before Fix)

**File**: `core/src/main/java/org/apache/struts2/factory/DefaultInterceptorFactory.java`

**Old Code** (BEFORE commit 6cfd34c94):
```java
// Lines 68-75 (approximate - old version)
if (interceptor instanceof WithLazyParams) {
    LOG.debug("Interceptor {} is marked with interface {} and params will be set during action invocation",
            interceptorClassName, WithLazyParams.class.getName());
} else {
    reflectionProvider.setProperties(params, interceptor);  // ← Only non-WithLazyParams got params!
}

interceptor.init();
```

**What was happening:**
1. Factory created interceptor instance
2. **IF** interceptor implements `WithLazyParams` → skip `setProperties()` entirely
3. **ELSE** → set properties normally
4. Call `init()` (with no parameters set for `WithLazyParams` interceptors)
5. Return interceptor with empty/uninitialized properties

**Impact on parameter flow:**
```
XML params → InterceptorBuilder → InterceptorFactory.buildInterceptor()
                                     ↓
                        IF WithLazyParams → SKIP setProperties()
                                     ↓
                        InterceptorMapping created with params map
                                     ↓
                        But interceptor instance has NO values set!
                                     ↓
                        At invocation: LazyParamInjector uses params map
                                     ↓
                        Problem: Params map exists, but were never used initially
```

### The Fix (After commit 6cfd34c94)

**File**: `core/src/main/java/org/apache/struts2/factory/DefaultInterceptorFactory.java:71-75`

**New Code** (AFTER commit 6cfd34c94):
```java
reflectionProvider.setProperties(params, interceptor);  // ← ALWAYS set properties first
if (interceptor instanceof WithLazyParams) {
    LOG.debug("Interceptor {} implements {} - expression parameters will be re-evaluated during action invocation",
            interceptorClassName, WithLazyParams.class.getName());
}

interceptor.init();
```

**Key changes:**
1. `reflectionProvider.setProperties(params, interceptor)` **moved outside** the conditional
2. Now **ALL interceptors** get their properties set at factory time
3. `WithLazyParams` check becomes informational only (just logs)
4. Parameters are available during `init()` for all interceptors

### Bug #2: Missing Params in InterceptorMapping (STILL BROKEN)

**File**: `core/src/main/java/org/apache/struts2/config/providers/InterceptorBuilder.java:177`

**Current Code** (BUGGY):
```java
// Line 175: Interceptor is built WITH params - properties ARE set on instance
Interceptor interceptor = objectFactory.buildInterceptor(cfg, map);

// Line 177: BUG - InterceptorMapping created WITHOUT params map!
InterceptorMapping mapping = new InterceptorMapping(key, interceptor);
```

**What happens:**
1. `objectFactory.buildInterceptor(cfg, map)` correctly builds interceptor with params
2. Thanks to WW-5586 fix, properties ARE set on the interceptor instance
3. BUT `new InterceptorMapping(key, interceptor)` uses 2-arg constructor
4. This constructor creates an **empty HashMap** for params:
   ```java
   public InterceptorMapping(String name, Interceptor interceptor) {
       this(name, interceptor, new HashMap<>());  // ← Empty map!
   }
   ```
5. During action invocation, `interceptorMapping.getParams()` returns empty
6. `LazyParamInjector.injectParams()` receives empty map and has nothing to re-evaluate

**The fix needed:**
```java
// Line 177 - Add the 'map' parameter
InterceptorMapping mapping = new InterceptorMapping(key, interceptor, map);
```

**Why this matters:**
- Interceptor **instance** has properties set (thanks to WW-5586)
- But `InterceptorMapping` metadata doesn't know what those params were
- `WithLazyParams` mechanism relies on `getParams()` to re-evaluate expressions
- Without the params map, there's nothing to re-evaluate

**Comparison with correct code:**
```java
// Line 74: Correct - includes params
result.add(new InterceptorMapping(refName, inter, refParams));  // ✅

// Line 177: Bug - missing params
InterceptorMapping mapping = new InterceptorMapping(key, interceptor);  // ❌
```

### Impact of Both Bugs Together

| Scenario | Bug #1 (Factory) | Bug #2 (Mapping) | Result |
|----------|------------------|------------------|--------|
| Before WW-5586 | Properties NOT set | Params map empty | ❌ Completely broken |
| After WW-5586 | Properties ARE set | Params map STILL empty | ❌ Still broken for WithLazyParams |
| After BOTH fixes | Properties ARE set | Params map populated | ✅ Works correctly |

**Why Bug #2 breaks WithLazyParams:**
1. At factory time: Interceptor gets `foo="${blah}"` set as literal string ✅
2. At invocation time: `LazyParamInjector` needs to re-evaluate `${blah}` ❌
3. But `getParams()` returns empty, so nothing to evaluate ❌
4. Interceptor keeps the literal string `"${blah}"` instead of evaluated value ❌

### Dual Initialization Mechanism

The fix enables a powerful **dual initialization** pattern:

#### 1. Factory Time Initialization
**Location**: `DefaultInterceptorFactory.buildInterceptor()` (core/src/main/java/org/apache/struts2/factory/DefaultInterceptorFactory.java:71)

```java
// Merge config params with ref params
Map<String, String> params = new HashMap<>(interceptorConfig.getParams());
params.putAll(interceptorRefParams);  // XML params from <interceptor-ref>

// Create interceptor instance
Object o = objectFactory.buildBean(interceptorClassName, null);
Interceptor interceptor = (Interceptor) o;

// ✅ SET ALL PARAMETERS (including expression strings)
reflectionProvider.setProperties(params, interceptor);

// Call init() - can now rely on configured values
interceptor.init();

// Return interceptor wrapped in InterceptorMapping with params
return interceptor;
```

**At this point:**
- Static params: `foo = "some value"` → set to `"some value"`
- Expression params: `bar = "${dynamicValue}"` → set to literal string `"${dynamicValue}"`
- Interceptor's `init()` method can access all configured values
- `InterceptorMapping.params` map contains all parameters

#### 2. Invocation Time Re-evaluation
**Location**: `DefaultActionInvocation.invoke()` (core/src/main/java/org/apache/struts2/DefaultActionInvocation.java:261-262)

```java
if (interceptors.hasNext()) {
    final InterceptorMapping interceptorMapping = interceptors.next();
    Interceptor interceptor = interceptorMapping.getInterceptor();

    // ✅ IF WithLazyParams, re-inject parameters with expression evaluation
    if (interceptor instanceof WithLazyParams) {
        interceptor = lazyParamInjector.injectParams(
            interceptor,
            interceptorMapping.getParams(),  // ← Now populated from factory time!
            invocationContext
        );
    }

    // ... continue with intercept()
}
```

**What `LazyParamInjector.injectParams()` does** (core/src/main/java/org/apache/struts2/interceptor/WithLazyParams.java:78-84):
```java
public Interceptor injectParams(Interceptor interceptor, Map<String, String> params, ActionContext invocationContext) {
    for (Map.Entry<String, String> entry : params.entrySet()) {
        // Evaluate expressions against current ValueStack
        Object paramValue = textParser.evaluate(
            new char[]{'$'},
            entry.getValue(),      // e.g., "${dynamicValue}" or "static value"
            valueEvaluator,        // Resolves ${...} from ValueStack
            TextParser.DEFAULT_LOOP_COUNT
        );

        // Set the evaluated value on the interceptor
        ognlUtil.setProperty(entry.getKey(), paramValue, interceptor, invocationContext.getContextMap());
    }
    return interceptor;
}
```

**At this point:**
- Static params: `foo = "some value"` → remains `"some value"` (no expression to evaluate)
- Expression params: `bar = "${dynamicValue}"` → evaluated from ValueStack → becomes actual value

### Complete Parameter Flow

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. XML Configuration                                            │
│    <interceptor-ref name="lazy">                                │
│      <param name="foo">${blah}</param>                          │
│      <param name="bar">static value</param>                     │
│    </interceptor-ref>                                           │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────┐
│ 2. XmlHelper.getParams()                                        │
│    Extracts: {"foo": "${blah}", "bar": "static value"}         │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────┐
│ 3. InterceptorBuilder.constructInterceptorReference()           │
│    Passes params to factory                                     │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────┐
│ 4. DefaultInterceptorFactory.buildInterceptor()                 │
│    ✅ NEW: reflectionProvider.setProperties(params, interceptor)│
│    - Sets foo = "${blah}" (literal string)                      │
│    - Sets bar = "static value"                                  │
│    - Calls interceptor.init()                                   │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────┐
│ 5. InterceptorMapping Created                                   │
│    new InterceptorMapping(name, interceptor, params)            │
│    - interceptor: configured instance                           │
│    - params: {"foo": "${blah}", "bar": "static value"}         │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────┐
│ 6. ActionConfig Built                                           │
│    RuntimeConfiguration stores expanded ActionConfig             │
│    with List<InterceptorMapping>                                │
└────────────────────────────┬────────────────────────────────────┘
                             │
                    [Action invocation]
                             │
┌────────────────────────────▼────────────────────────────────────┐
│ 7. DefaultActionInvocation.invoke()                             │
│    IF interceptor instanceof WithLazyParams:                    │
│      ✅ lazyParamInjector.injectParams(                          │
│           interceptor,                                          │
│           interceptorMapping.getParams()  ← Now has values!     │
│         )                                                       │
│      - Evaluates "${blah}" from ValueStack → "dynamic value"   │
│      - Keeps "static value" as-is                              │
│      - Sets both via OGNL                                      │
└─────────────────────────────────────────────────────────────────┘
```

### Code References

1. **XML Parsing**: `XmlConfigurationProvider.java`, `XmlDocConfigurationProvider.java`
   - `XmlHelper.getParams()` extracts parameters from XML

2. **Parameter Extraction**: `core/src/main/java/org/apache/struts2/config/providers/XmlHelper.java:63-82`
   - Parses `<param>` tags under `<interceptor-ref>`

3. **Interceptor Building**: `core/src/main/java/org/apache/struts2/config/providers/InterceptorBuilder.java:50-91`
   - `constructInterceptorReference()` handles both single interceptors and stacks
   - `constructParameterizedInterceptorReferences()` handles parameter overriding in stacks

4. **Factory (THE FIX)**: `core/src/main/java/org/apache/struts2/factory/DefaultInterceptorFactory.java:71`
   - **Line 71**: `reflectionProvider.setProperties(params, interceptor)` - now ALWAYS called

5. **InterceptorMapping**: `core/src/main/java/org/apache/struts2/config/entities/InterceptorMapping.java`
   - Stores final immutable reference to params map
   - `getParams()` returns the map

6. **Invocation Time Injection**: `core/src/main/java/org/apache/struts2/DefaultActionInvocation.java:261-262`
   - Checks `instanceof WithLazyParams`
   - Calls `lazyParamInjector.injectParams()`

7. **LazyParamInjector**: `core/src/main/java/org/apache/struts2/interceptor/WithLazyParams.java:78-84`
   - Re-evaluates expression parameters
   - Sets properties via OGNL

### Test Examples

#### Test Configuration
**File**: `core/src/test/resources/org/apache/struts2/config/providers/xwork-test-interceptor-param-overriding.xml`

```xml
<interceptors>
    <interceptor name="interceptorOne" class="org.apache.struts2.config.providers.InterceptorForTestPurpose" />
    <interceptor name="interceptorTwo" class="org.apache.struts2.config.providers.InterceptorForTestPurpose" />

    <interceptor-stack name="stackOne">
        <interceptor-ref name="interceptorOne" />
        <interceptor-ref name="interceptorTwo" />
    </interceptor-stack>
</interceptors>

<action name="actionOne">
    <interceptor-ref name="stackOne">
        <param name="interceptorOne.paramOne">i1p1</param>
        <param name="interceptorOne.paramTwo">i1p2</param>
        <param name="interceptorTwo.paramOne">i2p1</param>
    </interceptor-ref>
</action>
```

#### WithLazyParams Test
**File**: `core/src/test/resources/xwork-sample.xml`

```xml
<action name="LazyFooWithStackParams" class="org.apache.struts2.SimpleAction">
    <interceptor-ref name="params"/>
    <interceptor-ref name="lazy">
        <param name="foo">${blah}</param>           <!-- Expression parameter -->
        <param name="bar">static value</param>      <!-- Static parameter -->
    </interceptor-ref>
</action>
```

**Test**: `core/src/test/java/org/apache/struts2/DefaultActionInvocationTest.java:391-412`

```java
public void testInvokeWithLazyParamsStackConfiguration() throws Exception {
    HashMap<String, Object> params = new HashMap<>();
    params.put("blah", "dynamic value");

    ActionContext extraContext = ActionContext.of()
            .withParameters(HttpParameters.create(params).build());

    ActionProxy actionProxy = actionProxyFactory.createActionProxy(
        "", "LazyFooWithStackParams", null, extraContext.getContextMap()
    );

    defaultActionInvocation.init(actionProxy);
    defaultActionInvocation.invoke();

    SimpleAction action = (SimpleAction) defaultActionInvocation.getAction();

    // Expression parameter evaluated at invocation time
    assertEquals("dynamic value", action.getName());

    // Static parameter set at factory time and not re-evaluated
    assertEquals("static value", action.getBlah());
}
```

## Architecture Insights

### Design Pattern: Dual Initialization
The `WithLazyParams` interface enables a powerful pattern:
1. **Early binding** for static configuration (factory time)
2. **Late binding** for dynamic expressions (invocation time)
3. **Interceptor reuse** across multiple actions with different runtime contexts

### Why This Matters
Without the fix, `WithLazyParams` interceptors were unusable in interceptor stacks with parameter overrides because:
- Stack parameter syntax: `<param name="interceptorName.paramName">value</param>`
- These params are passed through `InterceptorBuilder.constructParameterizedInterceptorReferences()`
- Which calls `InterceptorFactory.buildInterceptor(config, params)`
- Which **skipped property setting** for `WithLazyParams`
- Result: Interceptor had no values, `getParams()` returned empty or default values

### Use Cases Enabled by the Fix

1. **Static stack configuration**:
   ```xml
   <interceptor-ref name="fileUpload">
       <param name="allowedTypes">image/png,image/jpeg</param>
   </interceptor-ref>
   ```

2. **Dynamic expression evaluation**:
   ```xml
   <interceptor-ref name="fileUpload">
       <param name="maximumSize">${maxUploadSize}</param>
   </interceptor-ref>
   ```

3. **Mixed static and dynamic** (enabled by dual initialization):
   ```xml
   <interceptor-ref name="fileUpload">
       <param name="allowedTypes">image/png,image/jpeg</param>
       <param name="maximumSize">${maxUploadSize}</param>
   </interceptor-ref>
   ```

## Historical Context

### Related Work
- **Commit 6cfd34c94** (Nov 17, 2025): Main fix for WW-5586
- **Commit b622e5d72**: "WW-3714 Ensure ReflectionExceptionHandler, WithLazyParams, ParamNameAwareResult marker interfaces respected"

### Original Intent of WithLazyParams
The interface was introduced in Struts 2.5.9 to support per-request parameter evaluation. The original JavaDoc (before fix) stated:

> "Interceptors marked with this interface won't be fully initialised during initialisation. Appropriated params will be injected just before usage of the interceptor."

This was **misleading** - it suggested parameters would ONLY be set at invocation time, which broke stack parameter configuration.

### Updated JavaDoc (After Fix)
The new JavaDoc correctly describes the dual initialization:

> "Parameters are set during interceptor creation (factory time), then re-evaluated during each action invocation to resolve expressions like ${someValue}."
>
> "The init() method is called after initial parameter setting, so interceptors can rely on configured values during initialization."

## Open Questions

**Q: Should we also check for other locations where InterceptorMapping is created without params?**
- Only 2 places in InterceptorBuilder.java use `new InterceptorMapping()`
- Line 74: Correctly includes params ✅
- Line 177: Missing params ❌
- No other locations found that could have this bug

**Q: Why didn't the existing test catch this bug?**
- Test: `testInvokeWithLazyParamsStackConfiguration()` in `DefaultActionInvocationTest.java`
- It only verifies final values on the action, not the intermediate `getParams()` call
- Should add assertion: `assertFalse(interceptorMapping.getParams().isEmpty())`

## Related Research

- `thoughts/shared/research/2025-10-17-WW-5579-double-short-range-validators-missing.md` - Validator configuration research
- This research complements understanding of Struts configuration and parameter handling

## Next Steps

1. **Create JIRA issue** for Bug #2 (InterceptorBuilder line 177)
   - Title: "WithLazyParams interceptors lose parameters in stack configurations"
   - Reference WW-5586 as incomplete fix

2. **Implement fix**:
   ```java
   // InterceptorBuilder.java:177
   - InterceptorMapping mapping = new InterceptorMapping(key, interceptor);
   + InterceptorMapping mapping = new InterceptorMapping(key, interceptor, map);
   ```

3. **Enhance test**:
   - Add assertion to verify `getParams()` is not empty
   - Specifically test that params are available for WithLazyParams interceptors in stacks

4. **Run full test suite**:
   ```bash
   mvn test -DskipAssembly
   ```

## Conclusion

The empty parameters issue has **TWO root causes**, not one:

**Bug #1 (Fixed in WW-5586)**: `DefaultInterceptorFactory` skipped property setting for `WithLazyParams` interceptors. The fix moved `reflectionProvider.setProperties()` outside the conditional, enabling dual initialization at factory time.

**Bug #2 (Still broken)**: `InterceptorBuilder.constructParameterizedInterceptorReferences()` at line 177 creates `InterceptorMapping` without the params map, causing `getParams()` to return empty. This breaks the invocation-time re-evaluation mechanism.

**Both fixes are required** for `WithLazyParams` to work correctly:
- Bug #1 fix enables factory-time initialization with parameters
- Bug #2 fix enables invocation-time re-evaluation of expressions

Without Bug #2 fix, `WithLazyParams` interceptors in stack configurations:
- Get configured correctly at factory time ✅
- But cannot re-evaluate expressions at invocation time ❌
- Because `LazyParamInjector` receives empty params map ❌

The complete fix is simple - just add one parameter to the constructor call - but it's critical for the feature to work as designed.
