---
date: 2026-02-22T12:00:00+01:00
topic: "HttpMethodInterceptor does not work with action names using wildcards"
tags: [research, codebase, HttpMethodInterceptor, DefaultActionProxy, wildcard, isMethodSpecified, security]
status: complete
git_commit: a21c763d8a8592f1056086134414123f6d8d168d
---

# Research: WW-5535 — HttpMethodInterceptor does not work with wildcard action names

**Date**: 2026-02-22

## Research Question

Why does `HttpMethodInterceptor` fail to enforce HTTP method validation when actions use wildcard names like
`example-*`?

## Summary

The root cause is in `DefaultActionProxy.resolveMethod()`. For wildcard-matched actions, the method name is resolved
from the `ActionConfig` (after wildcard substitution) rather than being passed explicitly from the URL. Because
`resolveMethod()` treats any method resolved from config as "not specified", `isMethodSpecified()` returns `false`. This
causes `HttpMethodInterceptor` to check **class-level** annotations instead of **method-level** annotations, potentially
skipping validation entirely.

## Detailed Findings

### 1. The `methodSpecified` Flag Logic

**File**: [
`DefaultActionProxy.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/main/java/org/apache/struts2/DefaultActionProxy.java)

```java
private boolean methodSpecified = true;  // field default

private void resolveMethod() {
    if (StringUtils.isEmpty(this.method)) {
        this.method = config.getMethodName();
        if (StringUtils.isEmpty(this.method)) {
            this.method = ActionConfig.DEFAULT_METHOD;
        }
        methodSpecified = false;  // <-- ONLY path that sets it false
    }
}
```

The flag is set to `false` whenever the method was not passed explicitly to the proxy constructor. This conflates two
different concepts:

- **"Was the method specified by the user via DMI?"** (security concern — user-controlled method invocation)
- **"Is a non-default method being invoked?"** (what `HttpMethodInterceptor` needs to know)

### 2. Wildcard Resolution Flow

For a config like `<action name="example-*" class="ExampleAction" method="do{1}">` and URL `example-create`:

| Step | Component                                    | Result                                                                                                      |
|------|----------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| 1    | `DefaultActionMapper.extractMethodName()`    | `ActionMapping.method = null` (exact map lookup fails for wildcards)                                        |
| 2    | `Dispatcher.serviceAction()`                 | Passes `null` method to `ActionProxyFactory`                                                                |
| 3    | `DefaultActionProxy` constructor             | `this.method = null`                                                                                        |
| 4    | `RuntimeConfigurationImpl.getActionConfig()` | Triggers `ActionConfigMatcher.match()` → `convert()` substitutes `{1}` → `config.methodName = "docreate"`   |
| 5    | `DefaultActionProxy.resolveMethod()`         | `this.method` is empty → takes `config.getMethodName()` = `"docreate"` → **sets `methodSpecified = false`** |

**Key file**: [
`ActionConfigMatcher.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/main/java/org/apache/struts2/config/impl/ActionConfigMatcher.java) —
`convert()` performs `{n}` substitution on method name.

**Key file**: [
`DefaultActionMapper.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/main/java/org/apache/struts2/dispatcher/mapper/DefaultActionMapper.java) —
`extractMethodName()` uses `cfg.getActionConfigs().get(mapping.getName())` which is an exact map lookup that cannot
match wildcard patterns.

### 3. HttpMethodInterceptor Behavior

**File**: [
`HttpMethodInterceptor.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/main/java/org/apache/struts2/interceptor/httpmethod/HttpMethodInterceptor.java)

```java
if (invocation.getProxy().isMethodSpecified()) {
    // Check METHOD-LEVEL annotations (e.g., @HttpGet on the specific method)
    Method method = action.getClass().getMethod(invocation.getProxy().getMethod());
    if (AnnotationUtils.isAnnotatedBy(method, HTTP_METHOD_ANNOTATIONS)) {
        return doIntercept(invocation, method);
    }
} else if (AnnotationUtils.isAnnotatedBy(action.getClass(), HTTP_METHOD_ANNOTATIONS)) {
    // Check CLASS-LEVEL annotations only
    return doIntercept(invocation, action.getClass());
}
// No annotations → allow request through (no validation)
```

**Impact for wildcard actions**: Since `isMethodSpecified()` returns `false`, the interceptor checks class-level
annotations. If the action class has no class-level HTTP method annotations (only method-level ones), validation is *
*skipped entirely**.

### 4. The Semantic Mismatch

The `ActionProxy.isMethodSpecified()` Javadoc says:

> Returns true if the method returned by `getMethod()` is not a default initializer value.

The current implementation interprets "default initializer" as "anything not explicitly passed from the URL/DMI", which
includes wildcard-configured methods. But for `HttpMethodInterceptor`, what matters is whether a *specific* method (not
`execute`) is being invoked, regardless of how it was determined.

## Code References

- [
  `DefaultActionProxy.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/main/java/org/apache/struts2/DefaultActionProxy.java) —
  `resolveMethod()` and `methodSpecified` field
- [
  `ActionProxy.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/main/java/org/apache/struts2/ActionProxy.java) —
  `isMethodSpecified()` interface and Javadoc
- [
  `HttpMethodInterceptor.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/main/java/org/apache/struts2/interceptor/httpmethod/HttpMethodInterceptor.java) —
  `intercept()` branching on `isMethodSpecified()`
- [
  `ActionConfigMatcher.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/main/java/org/apache/struts2/config/impl/ActionConfigMatcher.java) —
  wildcard `{n}` substitution in `convert()`
- [
  `DefaultActionMapper.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/main/java/org/apache/struts2/dispatcher/mapper/DefaultActionMapper.java) —
  `extractMethodName()` exact-match lookup
- [
  `DefaultConfiguration.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java) —
  `findActionConfigInNamespace()` wildcard fallback
- [
  `HttpMethodInterceptorTest.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/test/java/org/apache/struts2/interceptor/httpmethod/HttpMethodInterceptorTest.java) —
  existing tests
- [
  `DefaultActionProxyTest.java`](https://github.com/apache/struts/blob/a21c763d8a8592f1056086134414123f6d8d168d/core/src/test/java/org/apache/struts2/DefaultActionProxyTest.java) —
  only tests disallowed method, not `isMethodSpecified()`

## Architecture Insights

The `isMethodSpecified()` flag was originally introduced (WW-3628) to distinguish user-controlled DMI method invocation
from default `execute()` dispatch. This was a security measure — DMI-specified methods need stricter validation.

However, the flag now serves double duty:

1. **Security gate** in DMI handling — was the method user-specified via URL?
2. **Dispatch hint** in `HttpMethodInterceptor` — should we check method-level or class-level annotations?

These two concerns have different semantics for wildcard actions. A wildcard-configured method like `do{1}` is **not
user-controlled** (it's defined in the config), but it **is a specific method** that should have its annotations
checked.

## Potential Fix Approaches

1. **Fix in `HttpMethodInterceptor`**: Instead of relying on `isMethodSpecified()`, check method-level annotations
   first, then fall back to class-level. This avoids changing the `ActionProxy` contract.

2. **Fix in `DefaultActionProxy.resolveMethod()`**: Set `methodSpecified = true` when the method is resolved from
   config (not just defaulted to `execute`). This changes the semantics of the flag but aligns with the Javadoc ("not a
   default initializer value").

3. **Add a new flag**: Introduce `isMethodFromConfig()` or similar to distinguish "method from wildcard config" from "
   method from DMI" from "default execute". This is the most precise but highest-impact change.

## Test Gaps

- No tests for `DefaultActionProxy` with wildcard-resolved action names
- No tests for `isMethodSpecified()` on a real `DefaultActionProxy` instance (all tests use `MockActionProxy`)
- No tests for `HttpMethodInterceptor` combined with wildcard-resolved methods

## Historical Context (from thoughts/)

No prior research documents found for WW-5535 or WW-3628.

## Open Questions

1. Are there other interceptors or components that depend on `isMethodSpecified()` semantics?
2. Would changing `methodSpecified` behavior for config-resolved methods break DMI security checks?
3. Should `DefaultActionMapper.extractMethodName()` be updated to also resolve wildcard-matched configs?
