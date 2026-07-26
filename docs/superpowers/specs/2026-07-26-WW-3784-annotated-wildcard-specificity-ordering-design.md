# WW-3784 — Specificity-ordered wildcard matching for annotated actions

- **Jira:** [WW-3784](https://issues.apache.org/jira/browse/WW-3784) — *Greedy and non-greedy matching behaviour should work in action methods using annotated wildcards*
- **Type:** Bug (Core Actions)
- **Fix version:** 7.3.0
- **Date:** 2026-07-26

## Problem

Struts matches wildcard action patterns on a **first-match-wins, insertion-order** basis.
`AbstractMatcher.match()` iterates its `compiledPatterns` list and `break`s on the first hit:

```java
for (Mapping<E> m : compiledPatterns) {
    if (wildcard.match(vars, potentialMatch, m.pattern())) {
        config = convert(potentialMatch, m.target(), vars);
        break;
    }
}
```

In **XML** configuration, precedence is controlled by *physically ordering* mappings — specific
patterns are placed before general ones, so first-match-wins does the right thing.

In **annotation-based** configuration (Convention plugin `@Action` wildcards), there is **no ordering
guarantee**. The registration order is derived from `Set<Class<?>>` class-scan order in
`PackageBasedActionConfigBuilder.buildConfiguration(...)`, which is effectively arbitrary and
non-deterministic across JVMs/classloaders. Consequently, when two annotated patterns genuinely
overlap — most clearly when a broad `**` pattern (the only token that crosses `/`) is registered
before a narrower, more specific pattern it can also match, or when two patterns share the same
matchable shape — the general pattern can win first-match-wins and shadow the specific one, leaving
the specific action unreachable.

### Concrete example (from the ticket)

| Pattern (annotated) | Intent |
|---|---|
| `some/usefull/*` | specific — should handle `/some/usefull/sleeping` |
| `some/*` | general — should handle `/some/eating` |

**Clarification (post-ticket):** with the default `WildcardHelper` matcher, single `*` (`MATCH_FILE`)
matches only within one path segment and does **not** cross `/` — only `**` (`MATCH_PATH`) does.
Under that rule, `some/*` matches exactly one segment after `some/` and `some/usefull/*` matches
exactly one segment after `some/usefull/` — different segment counts, so the two patterns are
**disjoint** and never compete for the same incoming URL; `some/*` alone cannot shadow
`some/usefull/*`. The ticket's original example predates this clarification. The comparator still
orders this pair deterministically (see the worked ranking below), which is a harmless improvement
over non-deterministic scan order, but the *shadowing* failure mode described above requires
genuinely overlapping patterns — which arises most clearly via `**` (e.g. a `**` catch-all registered
ahead of a narrower sibling it can also match) or via patterns that share the same matchable shape.

### Matchers in play

- `WildcardHelper` (default bean `struts`): `*` (`MATCH_FILE`) matches within a single path segment
  and does **not** cross `/`; `**` (`MATCH_PATH`) is the only token that crosses `/`.
- `NamedVariablePatternMatcher` (bean `namedVariable`): `{var}` → `([^/]+)`, also does **not** cross
  `/`.

The defect is fundamentally about **match precedence / ordering**, not the regex semantics
themselves.

## Scope decision

**Automatic specificity-ordering, annotation-sourced configs only.** XML keeps its explicit
file-order semantics untouched. No configuration flag — always on for Convention. This is safe
because the prior Convention order was non-deterministic, so no application could reliably depend on
it.

Rejected alternatives:

- *Specificity-ordering for all configs incl. XML* — would change long-standing XML first-match-wins
  behavior and risk breaking configs that rely on order.
- *Opt-in flag* — pushes the burden onto users who would need to discover it; the bug should just be
  fixed for annotations.

## Architecture & placement

The entire change lives in the **Convention plugin**. Core matchers
(`AbstractMatcher` / `ActionConfigMatcher`) and the XML configuration providers are **not modified**,
so XML behavior is fully preserved.

We change *the order in which Convention registers wildcard action patterns into each
`PackageConfig`*. That `LinkedHashMap` insertion order is exactly what flows through
`DefaultConfiguration` into the per-namespace `ActionConfigMatcher` and drives the runtime
first-match-wins loop.

```
@Action wildcards
      │  (Set<Class> scan order — arbitrary)
      ▼
PackageBasedActionConfigBuilder.buildConfiguration()
      │  ── NEW: reorder each PackageConfig.Builder's actions by specificity
      ▼
PackageConfig.actionConfigs (LinkedHashMap, now specific-first)
      │
      ▼
DefaultConfiguration → ActionConfigMatcher (first-match-wins over specific-first list)
```

Two pieces:

1. **`ActionNameSpecificityComparator`** — new pure `Comparator<String>` over action-name patterns,
   in `org.apache.struts2.convention`.
2. **A sort pass** in `PackageBasedActionConfigBuilder`, applied to each `PackageConfig.Builder`
   after all actions are collected and before packages are handed to the configuration.

One small, generic helper is added to core so the plugin can reorder a builder's map:
`PackageConfig.Builder.reorderActionConfigs(Comparator<String> byActionName)`, which clears and
re-inserts `actionConfigs` in sorted key order. It is a neutral utility that XML code never calls.

## The specificity comparator

`ActionNameSpecificityComparator implements Comparator<String>` orders action-name patterns
**most-specific first** using these keys, in order:

1. **Fewer wildcard tokens** first. A token is a `*` / `**` run (WildcardHelper) or a `{var}` group
   (NamedVariable).
2. **More literal characters** first — total pattern length minus the characters consumed by wildcard
   tokens.
3. **`*` before `**`** — fewer path-spanning (`**`) tokens is more specific.
4. **Alphabetical** on the raw pattern string — deterministic tiebreak.

Notes:

- The comparator is **matcher-agnostic**: it counts both `*`/`**` runs and `{var}` groups as
  wildcards, so it behaves correctly whether the application uses `WildcardHelper` or
  `NamedVariablePatternMatcher`.
- Literal (wildcard-free) names naturally sort first (0 wildcards, all-literal). This is harmless:
  literal action names are resolved by exact-map lookup (`actions.get(name)`) **before** the wildcard
  loop runs, so their relative order never affects matching.
- **Secondary benefit:** ordering becomes **deterministic** across JVMs/classloaders, which it is not
  today.

### Worked ranking — ticket case

| Pattern | wildcards | literal chars | `**` count | order |
|---|---|---|---|---|
| `some/usefull/*` | 1 | 13 | 0 | **1st (specific)** |
| `some/*` | 1 | 5 | 0 | 2nd (general) |

Tie on key 1 (both 1 wildcard) → key 2 decides: `some/usefull/*` has more literal characters, so it
is tried first. Result: `/some/usefull/sleeping` → specific action; `/some/eating` → general action —
regardless of scan order.

## Integration point

In `PackageBasedActionConfigBuilder.buildConfiguration(Set<Class<?>> classes)`, after the `classes`
loop finishes populating the `packageConfigs` map, iterate each `PackageConfig.Builder` and reorder
its action configs via `reorderActionConfigs(new ActionNameSpecificityComparator())`. Existing
downstream steps (index actions, adding packages to the configuration) run unchanged on the reordered
builders.

## Edge cases & limitations

- **Per-package scope.** Sorting is applied within each `PackageConfig`. Convention places all actions
  of a given namespace into the same package builder, so the common "several action classes, one
  namespace" case is fully covered. Competing wildcards spread across *different* convention packages
  that share a namespace remain in package-registration order. This is documented as a known limit and
  is out of scope; addressing it would require moving ordering into core `DefaultConfiguration`, which
  would risk affecting XML.
- **Genuine ties.** Patterns of identical specificity that truly overlap resolve alphabetically —
  deterministic, if arbitrary. Documented behavior.
- **No config flag.** Always on for Convention (per scope decision). The prior order was
  non-deterministic, so nothing could reliably depend on it.
- **Known limitation — primary key can misrank `**` vs. multi-token patterns.** The comparator's
  primary key is *raw* wildcard-token count, not `**`-awareness. A single-token `**` catch-all (1
  wildcard) therefore ranks ahead of a narrower two-token pattern such as `*/*` (2 wildcards) even
  though `*/*` matches a strictly smaller set of paths, so a `**` catch-all can shadow a more-specific
  sibling within the same package. This is an accepted known limitation — it is still a net
  improvement over the prior non-deterministic order, since the outcome is at least stable across
  JVMs/classloaders. A future refinement would lift the `**`-count key above the raw
  wildcard-token-count key so path-spanning patterns are always penalized first; that is out of scope
  for this fix.
- **Known limitation — parent-package actions are not resorted.**
  `PackageConfig.getAllActionConfigs()` inserts parent-package actions into the result map *before*
  the (sorted) own-package actions, so a parent package's wildcard action would still be matched ahead
  of the sorted actions of a child package regardless of specificity. In practice this has no
  observable impact: convention parent packages (e.g. `struts-default`) declare no wildcard action
  mappings, so there is nothing there to shadow child-package actions.

## Testing

- **Unit — `ActionNameSpecificityComparator`:**
  - Ticket case: `some/usefull/*` ranks before `some/*`.
  - `*` vs `**`: single-star ranks before double-star at equal literal length.
  - `{var}` patterns ranked consistently with `*` patterns.
  - Literals rank before any wildcard pattern.
  - **Shuffle-invariance:** a randomized input list produces an identical sorted output.
- **Integration — Convention plugin:** register competing annotated wildcards on action classes and
  assert that `/some/usefull/sleeping` resolves to the specific action and `/some/eating` to the
  general action, independent of class-registration order.

## Out of scope

- Changes to XML wildcard precedence or core matcher semantics.
- Namespace-wide (cross-package) ordering.
- Any change to greedy vs. non-greedy regex behavior of `WildcardHelper` / `NamedVariablePatternMatcher`.
