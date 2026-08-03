# WW-5674 — Make `SecurityMemberAccess.isClassBelongsToPackages` allocation-free

**Date:** 2026-08-03
**Ticket:** [WW-5674](https://issues.apache.org/jira/browse/WW-5674) (sub-task of [WW-5667](https://issues.apache.org/jira/browse/WW-5667))
**Sibling:** [WW-5675](https://issues.apache.org/jira/browse/WW-5675) — config re-parsing on every `SecurityMemberAccess` instantiation (separate spec)

## Background

WW-5667 reports that OGNL security checks consume 9% of RUNNABLE CPU samples in a
2-minute JFR profile of a preprod Payara server under moderate load. The report
contains two stack samples that point at two independent problems.

This spec covers **sample 1** only — the per-OGNL-access cost of
`SecurityMemberAccess.isClassBelongsToPackages`:

```
java.lang.String.split(String)
SecurityMemberAccess.isClassBelongsToPackages(Class, Set) :390
SecurityMemberAccess.isExcludedPackageNames(Class) :386
SecurityMemberAccess.isPackageExcluded(Class) :371
```

Sample 2 — repeated re-parsing of the raw configuration strings, caused by
`SecurityMemberAccess` being a `Scope.PROTOTYPE` bean — is the dominant cost but
is a different change with a different risk profile. It is tracked separately as
WW-5675.

The fix proposed on WW-5667 (cache the parsed `Set` in a `SecurityMemberAccess`
field) addresses neither problem: it does not touch this hot path at all, and it
cannot help sample 2 because the instance holding the field is itself discarded
and rebuilt on each container lookup.

## Problem

```java
public static boolean isClassBelongsToPackages(Class<?> clazz, Set<String> matchingPackages) {
    List<String> packageParts = List.of(toPackageName(clazz).split("\\."));
    return IntStream.range(0, packageParts.size())
            .mapToObj(i -> String.join(".", packageParts.subList(0, i + 1)))
            .anyMatch(matchingPackages::contains);
}
```

For a class whose package has N segments, one call allocates: a `String[]` plus
its N element substrings from `split`, a `List.of` wrapper, an `IntStream`
pipeline, N `subList` views, and N `StringJoiner`-built result strings. For
`org.apache.struts2.ognl` that is roughly a dozen objects.

The method is invoked up to four times per `isAccessible()` call — the
excluded-package check and the allowlist check, each applied to both the
member's declaring class and the target class.

A second, smaller cost sits underneath it:

```java
public static String toPackageName(Class<?> clazz) {
    if (clazz.getPackage() == null) {
        return "";
    }
    return clazz.getPackage().getName();
}
```

`Class.getPackage()` resolves through the defining classloader's package map on
every call. `Class.getPackageName()` (Java 9+) computes the name once and caches
it on the `Class` object.

## Goals

- Remove the per-call allocation overhead on the OGNL member-access hot path.
- **Zero change to allow/deny semantics**, demonstrated by test, not by argument.
- Keep the change small enough to review as a pure optimisation.

## Non-goals

- The `Scope.PROTOTYPE` config re-parsing (WW-5675).
- Changing how arrays and primitives resolve to package names — see
  *Deliberately out of scope* below.
- Any caching layer, memoisation, or new data structure.
- Any change to `struts.excludedPackageNames`, `struts.allowlist.packageNames`,
  or the surrounding configuration.

## Verified current semantics

The rewrite must reproduce the existing prefix set exactly. The following was
established empirically on the target JDK (17, `maven.compiler.release=17`),
not inferred:

| package name | `split("\\.")` | prefixes probed |
|---|---|---|
| `""` | `[""]` (length 1) | `[""]` |
| `"java"` | `["java"]` | `["java"]` |
| `"a.b.c"` | `["a","b","c"]` | `["a", "a.b", "a.b.c"]` |
| `"a..b"` | `["a","","b"]` | `["a", "a.", "a..b"]` |
| `".a"` | `["","a"]` | `["", ".a"]` |
| `"a.b."` | `["a","b"]` | `["a", "a.b"]` |

Two consequences worth stating explicitly, because both are easy to regress:

1. **The default package probes `contains("")`.** `"".split("\\.")` yields a
   one-element array containing the empty string, so a class in the default
   package tests the set for `""`. This is reachable in practice:
   `commaDelimitedStringToSet` filters empty entries *before*
   `ConfigParseUtil.toPackageNamesSet` applies `strip(s, ".")`, so a
   configuration of `struts.excludedPackageNames="."` puts `""` into the set and
   excludes default-package classes. Confirmed live:
   `isClassBelongsToPackages(defaultPkgClass, Set.of("")) == true`.

2. **Trailing-dot inputs are the only divergence.** `split` drops trailing empty
   segments, so `"a.b."` probes `["a", "a.b"]` whereas an index walk would also
   probe `"a.b."`. `Class.getPackage().getName()` cannot produce a trailing dot,
   so this shape is unreachable through every caller. It is recorded here so a
   future reader does not mistake it for a bug.

Every other shape is exactly reproducible by an index walk: probe
`P.substring(0, j)` at each `j` where `P.charAt(j) == '.'`, then probe `P`.

### `toPackageName` guard equivalence

`clazz.getPackage()` returns null for exactly primitives, `void`, and arrays.
Verified across eleven class shapes:

| class | `getPackage()` | current result | `isArray()/isPrimitive()` guard |
|---|---|---|---|
| `String` | non-null | `"java.lang"` | `"java.lang"` |
| default-package class | non-null | `""` | `""` |
| nested (`Map.Entry`) | non-null | `"java.util"` | `"java.util"` |
| lambda (hidden class) | non-null | `"org.apache.struts2.ognl"` | `"org.apache.struts2.ognl"` |
| JDK proxy | non-null | `"jdk.proxy1"` | `"jdk.proxy1"` |
| `int`, `void` | null | `""` | `""` |
| `int[]`, `String[]`, `String[][]` | null | `""` | `""` |

All eleven agree. Note that `void.class.isPrimitive()` is `true`, so `void` is
covered by the guard.

## Design

One file: `core/src/main/java/org/apache/struts2/ognl/SecurityMemberAccess.java`.

### 1. Cheaper package-name lookup, identical result

```java
public static String toPackageName(Class<?> clazz) {
    if (clazz.isArray() || clazz.isPrimitive()) {
        return "";
    }
    return clazz.getPackageName();
}
```

The guard covers precisely the cases where `getPackage()` returns null, so the
result is unchanged for every input while avoiding the classloader package-map
lookup on the common path.

### 2. Extract the walk over a package-name string

Taking a `String` rather than a `Class` makes the prefix logic directly testable
with shapes no real `Class` can produce (`""`, `"a..b"`, `".a"`), which is what
the differential test needs.

```java
static boolean isPackageBelongsToPackages(String packageName, Set<String> first, Set<String> second) {
    if (first.isEmpty() && second.isEmpty()) {
        return false;
    }
    int idx = packageName.indexOf('.');
    while (idx != -1) {
        String prefix = packageName.substring(0, idx);
        if (first.contains(prefix) || second.contains(prefix)) {
            return true;
        }
        idx = packageName.indexOf('.', idx + 1);
    }
    return first.contains(packageName) || second.contains(packageName);
}
```

Package-private: it is an implementation detail, exposed only far enough for the
test in the same package to reach it.

Shortest-prefix-first ordering is preserved. Ordering does not affect the result
(the operation is a disjunction) but it short-circuits earliest on broad
exclusions such as `java.io`, which are the common case.

The `isEmpty()` short-circuit skips the walk — and therefore every substring
allocation — when neither set is configured. That requires both sets to be
empty, so it does not fire on the allowlist path, where
`ALLOWLIST_REQUIRED_PACKAGES` is always non-empty (see §4), nor on the
exclusion path under the shipped configuration, where
`struts.excludedPackageNames` carries roughly thirty entries by default. It
protects deployments that configure both sets empty.

### 3. Both public entry points delegate to it

```java
public static boolean isClassBelongsToPackages(Class<?> clazz, Set<String> matchingPackages) {
    return isClassBelongsToPackages(clazz, matchingPackages, emptySet());
}

public static boolean isClassBelongsToPackages(Class<?> clazz, Set<String> first, Set<String> second) {
    return isPackageBelongsToPackages(toPackageName(clazz), first, second);
}
```

One copy of the prefix logic, reached by every caller.

The existing two-argument signature is retained. It is `public static` on a
public class, so it is nominally API even though a repository-wide search finds
no caller outside `SecurityMemberAccess` itself.

The new three-argument overload is `public static` for consistency with the two
public statics beside it. It has a single caller today; making it
package-private instead would be a defensible alternative and is a trivial
follow-up if the extra surface is unwelcome.

### 4. Single walk on the allowlist path

```java
protected boolean isClassAllowlisted(Class<?> clazz) {
    return allowlistClasses.contains(clazz)
            || ALLOWLIST_REQUIRED_CLASSES.contains(clazz)
            || (providerAllowlist != null && providerAllowlist.getProviderAllowlist().contains(clazz))
            || (threadAllowlist != null && threadAllowlist.getAllowlist().contains(clazz))
            || isClassBelongsToPackages(clazz, ALLOWLIST_REQUIRED_PACKAGES, allowlistPackageNames);
}
```

Two walks over the same package name become one. `ALLOWLIST_REQUIRED_PACKAGES`
is a non-empty constant, so the `isEmpty()` short-circuit does not fire here;
the saving is the second walk.

Semantically identical: probing prefix `p` against `A` then `B` at each step
yields the same disjunction as walking all prefixes against `A` and then all
prefixes against `B`.

`isExcludedPackageNames` continues to call the two-argument form and is
unchanged apart from inheriting the faster implementation.

### 5. Import cleanup

`java.util.List` and `java.util.stream.IntStream` become unused in
`SecurityMemberAccess` once the stream pipeline is gone and must be removed.
`java.util.Collections.emptySet` is already statically imported and is reused by
the two-argument delegate.

## Data flow and error handling

Unchanged. Same inputs, same boolean output, no new exceptions, no new state, no
caching, no new thread-safety considerations. `isPackageBelongsToPackages` is a
pure function of its arguments.

## Deliberately out of scope: array and primitive package semantics

`Class.getPackageName()` resolves arrays to their element type's package
(`java.io.File[]` → `"java.io"`, `String[]` → `"java.lang"`) and primitives to
`"java.lang"`, whereas the current code yields `""` for both. Adopting those
semantics was considered and rejected for this ticket because the change is
**bidirectional**, not a pure hardening:

- **Exclusion path tightens.** `java.io.File[]` currently escapes
  `struts.excludedPackageNames` because its package is `""`; it would become
  excluded.
- **Allowlist path loosens.** An application that allowlists `com.app.actions`
  does not today thereby allowlist `com.app.actions.MyThing[]`. It would. Arrays
  of allowlisted-package types become reachable where they previously required
  an explicit `struts.allowlist.classes` entry.

The allowlist is the primary OGNL defence in Struts 7.x and is enabled by
default, so a change that makes it more permissive needs its own security
reasoning, its own tests, and its own release note. The `isArray()/isPrimitive()`
guard in this spec preserves current behaviour exactly and captures the
`getPackageName()` performance win for ordinary classes, which is all real
traffic.

A follow-up ticket should be filed to decide the array/primitive question on its
own merits. This spec does not prejudge it.

## Testing

The equivalence proof is the deliverable; the speedup is a consequence. Tests go
in `core/src/test/java/org/apache/struts2/ognl/SecurityMemberAccessTest.java`,
which is **JUnit 4** (`org.junit.Test`, `org.junit.Before`, plain class, AssertJ
and Mockito) — not JUnit 5.

1. **Differential test.** Add the current algorithm to the test class as a
   private reference implementation, transcribed so that it takes the package
   name directly rather than a `Class` — the body is otherwise verbatim:

   ```java
   private static boolean legacyPrefixMatch(String packageName, Set<String> matchingPackages) {
       List<String> packageParts = List.of(packageName.split("\\."));
       return IntStream.range(0, packageParts.size())
               .mapToObj(i -> String.join(".", packageParts.subList(0, i + 1)))
               .anyMatch(matchingPackages::contains);
   }
   ```

   Taking a `String` is what lets the matrix cover shapes no real `Class` can
   produce; `toPackageName` equivalence is proven separately by test 4, so the
   two halves of the original method are each covered.

   Assert `legacyPrefixMatch(p, s)` equals
   `isPackageBelongsToPackages(p, s, emptySet())` across a matrix of
   package-name shapes × candidate sets. Shapes: `""`, `"java"`, `"a.b.c"`,
   `"a..b"`, `".a"`, and realistic deep package names — but **not** trailing-dot
   inputs, which are the one known divergence and are unreachable through every
   caller (see *Verified current semantics*). Sets: empty, exact match,
   parent-package match, no match, and the `""` set.

   This is the strongest available evidence that the rewrite is
   behaviour-preserving, and it stays readable in review.

2. **Package-boundary regression.** `org.apache.struts2x` must not match a set
   containing `org.apache.struts2`. This is the classic prefix-matching bug the
   rewrite could plausibly introduce and the single most important assertion in
   the change.

3. **Default-package edge.** A set containing `""` must still match
   default-package classes. Currently untested, obscure, and easy to regress
   silently.

4. **`toPackageName` equivalence** over the eleven class shapes tabulated above,
   asserting the guard agrees with `getPackage()`-based resolution.

5. **Two-set overload equivalence.** `isClassBelongsToPackages(c, A, B)` equals
   `isClassBelongsToPackages(c, A) || isClassBelongsToPackages(c, B)` across the
   matrix, covering empty-`A`, empty-`B`, and both-empty.

6. **Existing suite unchanged.** `SecurityMemberAccessTest` passes without
   modification to any existing assertion, and the full `core` module suite is
   green: `mvn test -DskipAssembly -pl core`.

### Performance verification

The project has no JMH harness and none is added for this change. The win is
established by allocation count — a `String[]` plus N substrings, a list
wrapper, a stream pipeline, N sublist views and N joined strings, reduced to N
substrings — and confirmed with a throwaway benchmark that is **not** committed.
No timing assertion is added to the test suite, since wall-clock assertions are
unreliable in CI.

## Risks

| Risk | Mitigation |
|---|---|
| Prefix matching without package-boundary awareness silently widens exclusion or allowlist matching | Test 2 asserts `org.apache.struts2x` does not match `org.apache.struts2` |
| Default-package `""` edge regresses unnoticed | Test 3 pins it |
| `toPackageName` guard misses a null-`getPackage()` case | Guard verified against eleven class shapes; test 4 pins them |
| Two-set overload changes evaluation semantics | Test 5 asserts equivalence to the disjunction of two single-set calls |
| Reviewer mistakes this for the fix WW-5667 asked for | Ticket descriptions and PR body state that WW-5667's proposed fix addresses neither problem, and that WW-5675 covers the dominant cost |

## Out of scope for this spec

- WW-5675 (config re-parsing / `Scope.PROTOTYPE`) — separate spec and PR.
- Array and primitive package semantics — follow-up ticket, see above.
- `isExcludedPackageNamePatterns`, which walks `excludedPackageNamePatterns` with
  a stream and calls `toPackageName` per pattern. It benefits from the cheaper
  `toPackageName` for free, but its own stream overhead is not addressed here;
  the pattern set is empty by default.
