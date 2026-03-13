---
date: 2026-03-06T12:00:00+01:00
topic: "default-action-ref fails to find wildcard named actions"
tags: [research, codebase, default-action-ref, wildcard, action-mapping, DefaultConfiguration]
status: complete
---

# Research: WW-2963 — default-action-ref fails to find wildcard named actions

**Date**: 2026-03-06
**Ticket**: [WW-2963](https://issues.apache.org/jira/browse/WW-2963)

## Research Question

When `<default-action-ref name="movie-list"/>` is configured and the only matching action uses a wildcard pattern `<action name="movie-*" ...>`, Struts returns a 404 instead of resolving the default action through wildcard matching.

## Summary

The bug is in `DefaultConfiguration.RuntimeConfigurationImpl.findActionConfigInNamespace()`. When the default-action-ref fallback triggers (step 3 of the resolution chain), it only performs an **exact map lookup** (`actions.get(defaultActionRef)`) for the default action name. It does NOT attempt wildcard matching. This means if the default action name (e.g., "movie-list") is only matchable via a wildcard pattern (e.g., "movie-*"), the lookup returns `null` and the request fails with a 404.

The fix is to also try the wildcard matcher when the exact lookup for the default action ref fails.

## Detailed Findings

### Action Resolution Order (within a namespace)

In [`DefaultConfiguration.java:611-632`](https://github.com/apache/struts/blob/4c94c4f89a15b3102c3822dfc64dca15ee42a731/core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java#L611-L632):

```java
private ActionConfig findActionConfigInNamespace(String namespace, String name) {
    ActionConfig config = null;
    if (namespace == null) {
        namespace = "";
    }
    Map<String, ActionConfig> actions = namespaceActionConfigs.get(namespace);
    if (actions != null) {
        config = actions.get(name);                                    // 1. Exact match
        if (config == null) {
            config = namespaceActionConfigMatchers.get(namespace).match(name);  // 2. Wildcard match
            if (config == null) {
                String defaultActionRef = namespaceConfigs.get(namespace);
                if (defaultActionRef != null) {
                    config = actions.get(defaultActionRef);            // 3. Default (EXACT ONLY — BUG)
                }
            }
        }
    }
    return config;
}
```

Steps 1 and 2 correctly try exact then wildcard matching for the **incoming request name**. But step 3 (the default-action-ref fallback) only does `actions.get(defaultActionRef)` — an exact map lookup. It never tries wildcard matching for the default action name.

### The Bug Scenario

Configuration:
```xml
<package name="default" namespace="/" extends="struts-default">
    <default-action-ref name="movie-list"/>
    <action name="movie-*" class="MovieAction" method="{1}"/>
</package>
```

When a request hits an unknown action in namespace `/`:
1. `actions.get("unknownAction")` → `null` (no exact match)
2. `namespaceActionConfigMatchers.get("/").match("unknownAction")` → `null` (doesn't match `movie-*`)
3. `defaultActionRef` = `"movie-list"` from `namespaceConfigs`
4. `actions.get("movie-list")` → `null` ← **BUG**: "movie-list" is not a key in the actions map; it's only matchable via the wildcard pattern "movie-*"
5. Result: `null` → 404

### The Fix

After the exact lookup fails for the default action ref, also try the wildcard matcher:

```java
if (config == null) {
    String defaultActionRef = namespaceConfigs.get(namespace);
    if (defaultActionRef != null) {
        config = actions.get(defaultActionRef);
        if (config == null) {
            config = namespaceActionConfigMatchers.get(namespace).match(defaultActionRef);
        }
    }
}
```

This adds one line: try `namespaceActionConfigMatchers.get(namespace).match(defaultActionRef)` when `actions.get(defaultActionRef)` returns null, mirroring the same exact→wildcard fallback pattern already used for the request name.

### How `actions` Map Is Built

In [`DefaultConfiguration.java:440-478`](https://github.com/apache/struts/blob/4c94c4f89a15b3102c3822dfc64dca15ee42a731/core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java#L440-L478), `buildRuntimeConfiguration()` iterates all `PackageConfig` instances and populates:

- `namespaceActionConfigs`: `Map<String, Map<String, ActionConfig>>` — namespace → (action pattern name → ActionConfig). Keys are the literal declared names (e.g., `"movie-*"`), NOT expanded names.
- `namespaceConfigs`: `Map<String, String>` — namespace → default action ref name (e.g., `"movie-list"`).

The `ActionConfigMatcher` is then built from the action names in each namespace, compiling only non-literal (wildcard-containing) names into patterns.

### How Wildcard Matching Works

The matching chain is:
1. `ActionConfigMatcher.match(name)` → inherited from `AbstractMatcher.match()` ([`AbstractMatcher.java:131-148`](https://github.com/apache/struts/blob/4c94c4f89a15b3102c3822dfc64dca15ee42a731/core/src/main/java/org/apache/struts2/config/impl/AbstractMatcher.java#L131-L148))
2. Uses `WildcardHelper.match()` (default `PatternMatcher` impl) to match the input against compiled patterns
3. On match, `ActionConfigMatcher.convert()` clones the `ActionConfig` substituting `{1}`, `{2}`, etc. with captured groups

### Key Classes

| Concern | Class | File |
|---|---|---|
| Resolution order (exact→wildcard→default) | `RuntimeConfigurationImpl` | [`DefaultConfiguration.java:611-632`](https://github.com/apache/struts/blob/4c94c4f89a15b3102c3822dfc64dca15ee42a731/core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java#L611-L632) |
| Runtime config build | `DefaultConfiguration` | [`DefaultConfiguration.java:440-478`](https://github.com/apache/struts/blob/4c94c4f89a15b3102c3822dfc64dca15ee42a731/core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java#L440-L478) |
| Wildcard pattern storage/matching | `AbstractMatcher` | [`AbstractMatcher.java:95-148`](https://github.com/apache/struts/blob/4c94c4f89a15b3102c3822dfc64dca15ee42a731/core/src/main/java/org/apache/struts2/config/impl/AbstractMatcher.java#L95-L148) |
| ActionConfig wildcard cloning | `ActionConfigMatcher` | [`ActionConfigMatcher.java:58-152`](https://github.com/apache/struts/blob/4c94c4f89a15b3102c3822dfc64dca15ee42a731/core/src/main/java/org/apache/struts2/config/impl/ActionConfigMatcher.java#L58-L152) |
| Default `*`/`**` matcher | `WildcardHelper` | [`WildcardHelper.java`](https://github.com/apache/struts/blob/4c94c4f89a15b3102c3822dfc64dca15ee42a731/core/src/main/java/org/apache/struts2/util/WildcardHelper.java) |
| PackageConfig default-action-ref storage | `PackageConfig` | [`PackageConfig.java:52, 262-273`](https://github.com/apache/struts/blob/4c94c4f89a15b3102c3822dfc64dca15ee42a731/core/src/main/java/org/apache/struts2/config/entities/PackageConfig.java#L262-L273) |
| XML parsing of default-action-ref | `XmlDocConfigurationProvider` | [`XmlDocConfigurationProvider.java:833-840`](https://github.com/apache/struts/blob/4c94c4f89a15b3102c3822dfc64dca15ee42a731/core/src/main/java/org/apache/struts2/config/providers/XmlDocConfigurationProvider.java#L833-L840) |

## Code References

- `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java:626` — The buggy line: `config = actions.get(defaultActionRef)` without wildcard fallback
- `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java:621` — How wildcard matching IS correctly done for the request name (just above the buggy code)
- `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java:440-478` — `buildRuntimeConfiguration()` where action maps and default refs are built

## Existing Test Coverage

- **No tests exist** for WW-2963 or the combination of default-action-ref with wildcard actions
- Wildcard tests exist in `ConfigurationTest.java:91-116` (testWildcardName, testWildcardNamespace)
- Default-action-ref is used in production XML (showcase, tiles, etc.) but never tested with wildcards
- Best place to add a test: `ConfigurationTest.java` or a new test using a dedicated test XML config

## Architecture Insights

The resolution chain (exact → wildcard → default) was designed so that default-action-ref is a last resort. However, the implementation assumed the default action ref name would always correspond to a literally-declared action. The fix is minimal and consistent with the existing pattern — just add a wildcard match attempt for the default action ref name, the same way it's already done for the request name.

## Open Questions

1. Should there be a validation at configuration load time that warns if `default-action-ref` doesn't resolve to any action (neither exact nor wildcard)?
2. Should the wildcard-matched default action also support `{1}` substitution (e.g., "movie-list" matching "movie-*" with `{1}` = "list")? The current `ActionConfigMatcher.convert()` already handles this.
3. Does the same issue exist in the namespace-level wildcard fallback at `getActionConfig()` lines 581-605? (Likely no — namespace matching uses `NamespaceMatcher` which already does wildcard matching.)
