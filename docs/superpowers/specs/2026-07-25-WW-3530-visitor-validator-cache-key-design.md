# WW-3530: Fix visitor-validator cache-key collision under wildcard actions

- **Jira:** [WW-3530](https://issues.apache.org/jira/browse/WW-3530)
- **Component:** XML Validators
- **Target version:** 7.3.0
- **Date:** 2026-07-25

## Problem

When two visitor field validators are declared on the same field with different
`context` params (e.g. `basic` and `additional`) and they visit an object of the
same class, the second validator is silently ignored: both produce the same
validator-cache key, so the first context's validators are returned for both and
run twice.

Originally reported in 2010 (v2.2.1) when the cache key omitted `context`
entirely. Intervening changes (WW-2996, WW-3753, WW-4536) reworked the key so
`context` is now included for normal actions — which fixed the report for
non-wildcard actions. The defect still reproduces for **wildcard / named-pattern
actions**, where the key intentionally drops `context`.

### Root cause

`AnnotationActionValidatorManager.buildValidatorKey(Class clazz, String context)`
(`core/src/main/java/org/apache/struts2/validator/AnnotationActionValidatorManager.java`)
substitutes the action's config name + method for `context` whenever the current
action is a wildcard/named-pattern action:

```java
String configName = config.getName();
if (configName.contains(ActionConfig.WILDCARD)
        || (configName.contains("{") && configName.contains("}"))) {
    sb.append(configName).append("|").append(proxy.getMethod());
} else {
    sb.append(context);
}
```

The word "context" has two meanings:

- **Action-level validation** (`ValidationInterceptor`): `clazz` is the action
  class and `context` is the action name — for a wildcard action this is derived
  from the URL and varies per request. Keying on it caused the WW-2996 memory
  leak (one cache entry per resolved URL). WW-3753 correctly substitutes the
  stable config name here.
- **Visitor validation** (`VisitorFieldValidator`): `clazz` is the *visited
  object's* class (not the action class) and `context` is the visitor's explicit,
  hand-written `context` param — stable, and the only thing distinguishing
  `basic` from `additional`.

The bug is that the config-name substitution — designed for the action's own
class under a wildcard — is applied **too broadly**, firing for visited objects
too and wrongly discarding their stable `context`.

## Fix

Narrow the substitution to the case it was designed for: only swap in the config
name when `clazz` is the action's own class. For any other class (a visited
object), always key on `context`.

```java
protected String buildValidatorKey(Class clazz, String context) {
    ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
    ActionProxy proxy = invocation.getProxy();
    ActionConfig config = proxy.getConfig();

    StringBuilder sb = new StringBuilder(clazz.getName());
    sb.append("/");
    if (StringUtils.isNotBlank(config.getPackageName())) {
        sb.append(config.getPackageName()).append("/");
    }

    Object action = invocation.getAction();
    boolean validatingActionClass = action != null && clazz.equals(action.getClass());
    String configName = config.getName();
    boolean wildcard = configName.contains(ActionConfig.WILDCARD)
            || (configName.contains("{") && configName.contains("}"));

    if (validatingActionClass && wildcard) {
        // WW-2996/WW-3753/WW-4536: wildcard actions share validators across
        // resolved names; key on the stable config name, not the volatile context.
        sb.append(configName).append("|").append(proxy.getMethod());
    } else {
        // Normal actions AND all visited objects (WW-3530): context is stable.
        sb.append(context);
    }
    return sb.toString();
}
```

### Behavioral change

The only difference: when `clazz` is **not** the action class (a visited object
under a visitor validator), the key now includes `context` even if the current
action is a wildcard. `basic` and `additional` therefore get distinct cache
entries and both run. The wildcard-action caching path (WW-2996) is untouched,
because it now fires only when `clazz == action.getClass()`.

## Scope, edge cases, non-goals

- **`DefaultActionValidatorManager`** (the `no-annotations` bean) already keys on
  `clazz + "/" + context` unconditionally — no bug, no change.
- **Null action guard:** if `invocation.getAction()` is null, fall through to the
  `context` branch (safe default).
- **Self-visiting wildcard action** (an action that visits an object of its *own*
  class under a wildcard mapping, with two different contexts): still collides,
  because `clazz == action.getClass()` takes the wildcard branch. Accepted known
  limitation — extremely contrived, and resolving it would require reintroducing
  the volatile-vs-stable-context ambiguity this fix avoids. Documented, not fixed.
- **Non-goal:** no change to the `ActionValidatorManager` interface, to
  `VisitorFieldValidator`, or to `DefaultActionValidatorManager`.

## Testing

Tests live in
`core/src/test/java/org/apache/struts2/validator/AnnotationActionValidatorManagerTest.java`
(plus existing `VisitorFieldValidatorTest` / `VisitorFieldValidatorModelTest`
for regression).

1. **Bug reproduction (fails before, passes after):** a wildcard action with two
   visitor validators on the same field, visiting the same class with different
   contexts → assert **both** contexts' validators execute.
2. **WW-2996 regression guard:** a wildcard action's own validators still resolve
   to a single cache entry across two different resolved action names.
3. **Existing coverage stays green:** non-wildcard visitor case and current
   wildcard action-level validation behavior unchanged.

## References

- WW-3530 — this issue
- WW-2996 — memory leak from keying on volatile wildcard action names
- WW-3753 — introduced config-name substitution for wildcard actions
- WW-4536 — extended it to `NamedVariablePatternMatcher` (`{...}`) actions
