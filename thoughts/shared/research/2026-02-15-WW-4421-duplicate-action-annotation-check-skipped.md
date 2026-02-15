---
date: 2026-02-15T12:00:00+01:00
topic: "WW-4421: Duplicate @Action value annotation check skipped"
tags: [research, codebase, convention-plugin, annotations, duplicate-detection]
status: complete
git_commit: fd874258631e999f5bd5ffc3fea8c0e416c61962
---

# Research: WW-4421 - Duplicate @Action value annotation check skipped

**Date**: 2026-02-15
**JIRA**: [WW-4421](https://issues.apache.org/jira/browse/WW-4421)

## Research Question
Investigate the core issue behind duplicate @Action annotation detection being bypassed in the Convention plugin.

## Summary

The duplicate @Action name detection logic in `PackageBasedActionConfigBuilder.buildConfiguration` is **guarded by a conditional that excludes the most common case**: when the `execute()` method is annotated with `@Action`. The duplicate check only runs when `!map.containsKey(DEFAULT_METHOD)` — meaning if the class overrides `execute()` and decorates it with `@Action`, the entire duplicate detection block is skipped.

Additionally, the code uses `Class.getMethods()` and `HashMap` — both with **non-deterministic ordering** — making the behavior inconsistent across JVM versions.

## Detailed Findings

### The Bug: Conditional Guard Excludes Common Case

**File**: [`PackageBasedActionConfigBuilder.java:744-747`](https://github.com/apache/struts/blob/fd874258631e999f5bd5ffc3fea8c0e416c61962/plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java#L744-L747)

```java
if (!map.containsKey(DEFAULT_METHOD)          // <-- THE BUG
        && hasDefaultMethod
        && actionAnnotation == null && actionsAnnotation == null
        && (alwaysMapExecute || map.isEmpty())) {
    // ... duplicate detection code is INSIDE this block ...
    Set<String> actionNames = new HashSet<>();
    for (List<Action> actions : map.values()) {
        for (Action action : actions) {
            String actionName = action.value().equals(Action.DEFAULT_VALUE) ? defaultActionName : action.value();
            if (actionNames.contains(actionName)) {
                throw new ConfigurationException("The action class [" + actionClass +
                    "] contains two methods with an action name annotation whose value " +
                    "is the same (they both might be empty as well).");
            } else {
                actionNames.add(actionName);
            }
        }
    }
}
```

The condition `!map.containsKey(DEFAULT_METHOD)` means:
- **If `execute()` has an `@Action` annotation** → `map` contains key `"execute"` → condition is `false` → **duplicate check is SKIPPED entirely**
- **If `execute()` does NOT have an `@Action` annotation** → condition can be `true` → duplicate check runs

This is backwards from what you'd expect. The most common pattern — overriding `execute()` with `@Action` — is exactly the case that bypasses validation.

### Scenario: When Duplicate Detection Fails

```java
// This class will NOT trigger the duplicate detection error:
public class MethodCheckSkippedOnStartup extends ActionSupport {
    @Action("myAction")        // <-- annotated execute() puts "execute" in the map
    public String execute() {
        return SUCCESS;
    }

    @Action("myAction")        // <-- duplicate name, but no error thrown!
    public String anotherMethod() {
        return SUCCESS;
    }
}
```

```java
// This class WILL trigger the duplicate detection error:
public class MethodCheckFailsOnStartup extends ActionSupport {
    // execute() is NOT annotated — not in the map
    public String execute() {
        return SUCCESS;
    }

    @Action("myAction")
    public String method1() {
        return SUCCESS;
    }

    @Action("myAction")        // <-- duplicate detected, throws ConfigurationException
    public String method2() {
        return SUCCESS;
    }
}
```

### Secondary Issue: Non-Deterministic Method Ordering

**File**: [`PackageBasedActionConfigBuilder.java:943-944`](https://github.com/apache/struts/blob/fd874258631e999f5bd5ffc3fea8c0e416c61962/plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java#L943-L944)

```java
Method[] methods = actionClass.getMethods();        // no guaranteed order
Map<String, List<Action>> map = new HashMap<>();    // no guaranteed iteration order
```

- `Class.getMethods()` returns methods in **no particular order** (JVM spec does not guarantee ordering)
- Results are stored in a `HashMap`, which also has **no guaranteed iteration order**
- This means the second `@Action` annotation that "wins" (overwrites the mapping) is non-deterministic
- Java 7 and Java 8 had different internal `HashMap` implementations, causing different behavior across versions

### The Duplicate Check Should Be Independent

The duplicate detection logic (lines 748-760) is embedded inside a block that also handles default `execute()` method mapping. These are **two separate concerns**:

1. **Should we auto-create an action config for `execute()`?** — This depends on `!map.containsKey(DEFAULT_METHOD)`
2. **Are there duplicate action names across annotated methods?** — This should ALWAYS be checked

The fix should extract the duplicate detection loop so it runs unconditionally (or at least independently of whether `execute()` is in the annotation map).

## Code References

- [`PackageBasedActionConfigBuilder.java:87`](https://github.com/apache/struts/blob/fd874258631e999f5bd5ffc3fea8c0e416c61962/plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java#L87) — `DEFAULT_METHOD = "execute"`
- [`PackageBasedActionConfigBuilder.java:741-773`](https://github.com/apache/struts/blob/fd874258631e999f5bd5ffc3fea8c0e416c61962/plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java#L741-L773) — The buggy block with embedded duplicate check
- [`PackageBasedActionConfigBuilder.java:776-788`](https://github.com/apache/struts/blob/fd874258631e999f5bd5ffc3fea8c0e416c61962/plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java#L776-L788) — Action config creation loop (no duplicate check here)
- [`PackageBasedActionConfigBuilder.java:942-959`](https://github.com/apache/struts/blob/fd874258631e999f5bd5ffc3fea8c0e416c61962/plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java#L942-L959) — `getActionAnnotations()` with non-deterministic ordering
- [`ReflectionTools.java:38-45`](https://github.com/apache/struts/blob/fd874258631e999f5bd5ffc3fea8c0e416c61962/plugins/convention/src/main/java/org/apache/struts2/convention/ReflectionTools.java#L38-L45) — `containsMethod()` helper

## Architecture Insights

1. **Separation of concerns violation**: Duplicate detection is tangled with default-execute-mapping logic inside the same conditional block
2. **Non-deterministic reflection**: Using `getMethods()` + `HashMap` means behavior varies across JVMs
3. **Silent failure mode**: When the check is skipped, one action config silently overwrites another — no error, no warning, just undefined behavior at runtime

## Suggested Fix Direction

1. **Extract duplicate detection** out of the `!map.containsKey(DEFAULT_METHOD)` conditional so it runs for ALL annotated action classes
2. **Also check class-level annotations** (`actionAnnotation`/`actionsAnnotation`) against method-level annotations for name conflicts
3. **Consider using `LinkedHashMap`** or sorting methods for deterministic processing order

## Open Questions

1. Should duplicate detection also consider action names across different classes in the same namespace?
2. Should a warning be issued instead of a `ConfigurationException` to allow intentional overrides?
3. Are there backward-compatibility concerns if we start detecting duplicates that were previously silently ignored?
