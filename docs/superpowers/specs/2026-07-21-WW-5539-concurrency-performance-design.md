# WW-5539 — Concurrency performance enhancements

**Jira**: [WW-5539](https://issues.apache.org/jira/browse/WW-5539)
**Fix version**: 7.3.0
**Component**: Core
**Date**: 2026-07-21

## Problem

WW-5539 names three classes as candidates for improved locking, without further detail.
Inspection confirms a distinct problem in each.

### `StrutsTypeConverterHolder` — unsynchronised shared mutable state

All four collections are plain `HashMap`/`HashSet` (`StrutsTypeConverterHolder.java:37-71`).
The holder is a container singleton mutated at runtime through `addDefaultMapping`,
`addMapping`, `addNoMapping` and `addUnknownMapping`. Meanwhile `XWorkConverter.lookup()`
(`XWorkConverter.java:364-368`) reads it under no lock at all, while `registerConverter()`
writes under the `XWorkConverter` monitor. Readers therefore race writers on a `HashMap`:
lost updates, and torn reads during resize.

This is a correctness defect, not only a contention problem.

### `XWorkConverter` — two coarse locks

1. `getConverter()` wraps its body in `synchronized (clazz)` (`XWorkConverter.java:417`).
   Locking on a `Class` object is a well-known anti-pattern — the monitor is globally
   visible and any other library may contend on it. It also serialises every conversion
   for a given action class, including pure cache hits.
2. `registerConverter` and `registerConverterNotFound` are `synchronized` methods
   (`XWorkConverter.java:466,470`) on the singleton, so every cache miss takes a
   process-wide lock.

### `DefaultActionValidatorManager` — global lock on the request path

`getValidators(...)` is `synchronized` on the singleton manager
(`DefaultActionValidatorManager.java:140`). Every validated request in the application
serialises on it. The lock covers not just the cache lookup but the per-request
`Validator` instantiation loop (lines 149-157), which operates on per-request objects
and never needed mutual exclusion. The caches are `synchronizedMap` wrappers with
non-atomic `containsKey`/`get`/`put` sequences layered on top (lines 143-150, 335-347).

## Approach

Make the caches genuinely concurrent, then delete the coarse locks. Where a computation
is expensive and provably safe to guard, use `computeIfAbsent`. Where it is not safe,
accept that two threads may occasionally redo cheap idempotent work and converge on the
same answer.

These caches are read-mostly with a small warm-up burst, which is the workload
`ConcurrentHashMap` is built for. The resulting diff mostly removes code, which matters
for a change whose entire risk profile is the soundness of its concurrency reasoning.

Two alternatives were considered and rejected:

- **Striped per-key locking** (interning a lock object per class). Preserves
  exactly-once computation everywhere, but adds a lock-object cache that itself needs
  eviction reasoning, still blocks readers behind writers, and duplicates the
  classloader-pinning problem in a second map. `computeIfAbsent` provides the same
  guarantee where it matters, for free.
- **Copy-on-write immutable snapshots.** Fastest possible reads, but `mappings` is keyed
  by every action class in the application, so each cold miss copies the whole map:
  O(n) per write, O(n²) to warm. Under `struts.configuration.xml.reload` writes never
  stop. Wrong shape for this data.

## Compatibility constraints

`TypeConverterHolder` is an SPI: aliased to `struts.converter.holder`
(`StrutsBeanSelectionProvider.java:413`) and bound in `struts-beans.xml:115`. Third
parties can supply their own implementations. 7.3.0 is a minor release, so the interface
may gain `default` methods but must not break existing implementations.

## Design

### 1. `TypeConverterHolder` SPI

One new `default` method:

```java
default Map<String, Object> computeMappingIfAbsent(Class clazz,
                                                   Function<Class, Map<String, Object>> builder)
```

Contract: return the class's property-converter mapping, building and caching it on first
use. Returns `Collections.emptyMap()` when the class is known to have none — never
`null`. A builder returning `null` or an empty map means "no mapping", and the holder
records that in the negative cache itself.

The `default` body implements this with the existing
`containsNoMapping`/`getMapping`/`addMapping`/`addNoMapping` primitives — check-then-act,
matching today's semantics. Third-party holders inherit it unchanged and keep working,
without the atomicity benefit.

Because the `default` body calls methods this same change deprecates, it carries
`@SuppressWarnings("deprecation")`. That is intentional and not an oversight: the fallback
path must keep using the old primitives, since those are the only methods a third-party
implementation is guaranteed to provide.

`StrutsTypeConverterHolder` overrides it:

```java
if (noMapping.contains(clazz)) {
    return Collections.emptyMap();
}
Map<String, Object> mapping = mappings.computeIfAbsent(clazz, c -> {
    Map<String, Object> built = builder.apply(c);
    return (built == null || built.isEmpty()) ? null : built;
});
if (mapping == null) {
    noMapping.add(clazz);
    return Collections.emptyMap();
}
return mapping;
```

A builder returning `null` inside `computeIfAbsent` stores nothing and yields `null`, so
the negative case falls out naturally and `getMapping()` retains its current
"null when absent" meaning.

**Fields** become `ConcurrentHashMap` and `ConcurrentHashMap.newKeySet()`. This is the
part that fixes the data race.

**Deprecations.** `getMapping`, `addMapping` and `containsNoMapping` are marked
`@Deprecated`: all three are strictly subsumed by `computeMappingIfAbsent`, and each
invites check-then-act at call sites.

`addNoMapping` is **not** deprecated. `XWorkConverter.getConverter` catches `Throwable`
and negative-caches the failure; that is a distinct operation ("this class failed to
build, stop retrying"), and folding it into the compute method would mean swallowing
`Throwable` inside the SPI, hiding real failures from implementers.

The default-mapping methods (`addDefaultMapping`, `containsDefaultMapping`,
`getDefaultMapping`, `containsUnknownMapping`, `addUnknownMapping`) are **not**
deprecated. They back the cache that cannot use `computeIfAbsent` (see below) and have
live external callers in `StrutsConversionPropertiesProcessor` and
`DefaultConversionAnnotationProcessor`.

Removal of the deprecated methods is tracked separately by the project lead.

**The `protected HashSet<String> unknownMappings` field** is retyped to `Set<String>`,
made `final`, backed by `ConcurrentHashMap.newKeySet()`, and marked `@Deprecated`.
Retyping is a source-compatibility break for any subclass that assigns it or calls a
`HashSet`-specific method. The risk is judged negligible, but it is a break and is
recorded here deliberately rather than left implicit.

### 2. `XWorkConverter`

**`getConverter(Class, String)` — `synchronized (clazz)` removed:**

```java
protected Object getConverter(Class clazz, String property) {
    if (property == null) {
        return null;
    }
    try {
        Map<String, Object> mapping = converterHolder.computeMappingIfAbsent(clazz, this::buildConverterMapping);
        mapping = conditionalReload(clazz, mapping);
        return mapping.get(property);
    } catch (Throwable t) {
        LOG.debug("Got exception trying to resolve converter for class [{}] and property [{}]", clazz, property, t);
        converterHolder.addNoMapping(clazz);
        return null;
    }
}
```

The `containsNoMapping` guard folds into the holder: a negative-cached class returns an
empty map and `mapping.get(property)` yields `null`, the same outcome. The
`catch (Throwable)` behaviour is preserved exactly.

Two consequences:

- **`buildConverterMapping` stops storing.** It currently calls `addMapping`/`addNoMapping`
  itself (`XWorkConverter.java:571-575`); with the holder owning storage that becomes a
  double write. It reduces to "build and return the map". The method is `protected`, so
  this is a behaviour change visible to subclasses and belongs in the release notes.
- **`buildConverterMapping` declares `throws Exception`**, which does not fit `Function`.
  It is wrapped in a private lambda that rethrows as unchecked; the outer
  `catch (Throwable)` still catches it, so negative-caching semantics are unchanged.

**`conditionalReload` stays outside the compute.** It only does work when
`struts.configuration.xml.reload` is enabled, and it must run on cache *hits* — that is
its purpose. Under reload it may rebuild concurrently with last-write-wins; that is a
dev-mode-only path where the current code is no better, and rebuilding is idempotent.

**`registerConverter` / `registerConverterNotFound` — `synchronized` removed.** Both are
now single delegations to a concurrent map. They are `public`, so the modifier is
externally observable; any caller relying on them for mutual exclusion was relying on a
lock that never covered the readers in `lookup()`.

**`lookup(String, boolean)` keeps check-then-act.** Its resolver is `lookupSuper()`
(`XWorkConverter.java:600-626`), which *reads* `getDefaultMapping()` recursively while
walking the class hierarchy. A recursive read inside `ConcurrentHashMap.computeIfAbsent`
is forbidden — it deadlocks or throws `IllegalStateException: Recursive update`. This
cache therefore simply becomes lock-free. Two threads racing on a cold miss may both walk
the hierarchy and both call `registerConverter` with an equal result; the work is a few
lock-free map reads and the outcome is identical.

**Known benign race, deliberately not closed.** `lookup` (line 364) reads
`containsUnknownMapping` and `containsDefaultMapping` as two separate calls. Each is
atomic against the concurrent map, but the *pair* is not — a converter registered between
them yields a stale `null` for that one call. Today, against an unsynchronised `HashMap`,
this is outright unsafe; afterwards it is a benign race that self-corrects on the next
lookup. No lock is added: doing so would reintroduce the contention being removed, on the
hottest read path, to close a window that resolves itself. This is recorded so a future
reader does not mistake it for an oversight.

### 3. `DefaultActionValidatorManager`

**Both `getValidators` overloads lose `synchronized`.** The three-argument one becomes:

```java
String validatorKey = buildValidatorKey(clazz, context);
List<ValidatorConfig> configs = validatorCache.get(validatorKey);
if (configs == null) {
    configs = validatorCache.computeIfAbsent(validatorKey,
            k -> buildValidatorConfigs(clazz, context, false, null));
} else if (reloadingConfigs) {
    configs = buildValidatorConfigs(clazz, context, true, null);
    validatorCache.put(validatorKey, configs);
}
```

This mirrors the existing `containsKey` / `else if (reloadingConfigs)` logic exactly,
including that a first-ever call builds with `checkFile=false` even in reload mode.

**The substantive win is what now sits outside any lock:** the loop at lines 149-157 that
reads the `ValueStack` and calls `validatorFactory.getValidator(config)` per config. That
is per-request work on per-request objects. Setting caching aside entirely, removing it
from the critical path takes the manager off the serialisation path of every validated
request.

**`loadFile` gets the same treatment**, with the file-reload branch kept explicit:

```java
URL fileUrl = ClassLoaderUtil.getResource(fileName, clazz);
if (checkFile && fileManager.fileNeedsReloading(fileUrl)) {
    List<ValidatorConfig> reloaded = parseValidatorConfigs(fileUrl, fileName);
    validatorFileCache.put(fileName, reloaded);
    return reloaded;
}
return validatorFileCache.computeIfAbsent(fileName, k -> parseValidatorConfigs(fileUrl, fileName));
```

`parseValidatorConfigs(URL, String)` is a new private helper extracted from the existing
body of `loadFile` — the `fileManager.loadFile` / `validatorFileParser` /
`catch (IOException)` block at lines 336-342, unchanged in behaviour. It is extracted only
so the same logic can serve both branches above without duplication.

This `computeIfAbsent` runs inside the one on `validatorCache`. They are different maps
and nothing calls back the other direction, so there is no lock-ordering cycle. Empty
results are still cached, preserving today's negative caching.

**Both caches become `ConcurrentHashMap`.** The declared field types stay
`Map<String, List<ValidatorConfig>>` and stay `protected`, so `AnnotationActionValidatorManager`
and any third-party subclass are unaffected.

**Cached lists are wrapped unmodifiable before publication.** With `synchronized` gone,
several threads iterate the same cached `List<ValidatorConfig>` concurrently. That is safe
only while no cached list is mutated after publication — true today, but only incidentally,
and `buildValidatorConfigs` does `addAll` into lists that flow around freely. Wrapping makes
it true by construction: a future mutation fails loudly at the mistake instead of becoming
an intermittent production heisenbug. Callers were checked —
`buildClassValidatorConfigs`/`buildAliasValidatorConfigs` results are only read or
`addAll`-ed into a fresh accumulator, and `AnnotationActionValidatorManager` already copies
defensively.

**Noted limitation, untouched:** `AnnotationActionValidatorManager.buildValidatorKey` calls
`ActionContext.getContext().getActionInvocation()`, so the cache key depends on per-request
state. It works, but the class cannot be reasoned about purely from its own source.

## Testing

**Regression gate.** The existing `XWorkConverterTest`, `AnnotationXWorkConverterTest`,
`DefaultActionValidatorManagerTest` and `AnnotationActionValidatorManagerTest`
(`XWorkTestCase`-based, JUnit 4 as core actually uses) must pass untouched. Any need to
*edit* them signals a semantic change that this design says will not happen.

**New concurrency tests**, added to the existing test classes. Each uses 16 threads
released simultaneously via `CountDownLatch`, and asserts on collected results rather than
on timing, so there is no sleep-based flakiness:

- `StrutsTypeConverterHolder`: 16 threads registering distinct default mappings while
  others read; assert every registration is visible at the end and no read returns a torn
  result. This test would plausibly fail against today's code.
- `XWorkConverter.getConverter`: 16 threads resolving converters for the same class
  concurrently; assert equal results and no exception. Additionally assert
  `buildConverterMapping` runs exactly once per class via a counting subclass — the
  concrete payoff of `computeMappingIfAbsent`.
- `DefaultActionValidatorManager.getValidators`: 16 threads against the same class and
  context; assert consistent validator lists and a single build.

**Caveat.** These tests can demonstrate a race but never its absence; a green run on a
strongly-ordered x86 machine is weak evidence. The primary correctness argument is the
reasoning about the data structures — reads on concurrent collections, both
`computeIfAbsent` call sites provably not re-entering their own maps, and the remaining
races benign and self-correcting. The tests are a backstop for that argument, not a
substitute.

**Benchmark, not committed.** A throwaway harness measuring `getValidators` and
`getConverter` throughput at 1/4/16/64 threads, before and after, warmed. Numbers go in
the PR description. The result will be reported faithfully, including the realistic
outcome that the converter changes do not move the needle while the validator lock does —
the converter caches warm fast, and the validator lock is the coarser of the two.

## Out of scope

1. **Classloader pinning.** `mappings` and `noMapping` hold strong `Class` references in a
   container singleton, keeping webapp classloaders alive across hot redeploy. A separate
   Jira issue: it is a lifetime/correctness issue rather than a locking one, and fixing it
   changes cache semantics.
2. **The `FIXME lukaszlenart` in `TypeConverterHolder`** about merging `unknownMappings`
   into `noMapping` — a semantic consolidation, unrelated to locking.
3. **Removal of the deprecated methods** — tracked by the project lead for a later release.

## Risk

The change is mostly deletion, but it deletes locks, so the failure mode is silent and
load-dependent rather than a failing build. Two things warrant the closest review:

- No `computeIfAbsent` builder can re-enter its own map. Verified for both call sites;
  the `lookupSuper` case is precisely why the default-mapping cache keeps check-then-act.
  Confirmed that `DefaultConversionFileProcessor` never touches the holder and
  `DefaultConversionAnnotationProcessor` writes only to `defaultMappings`, a different map.
- No cached collection is mutated after publication — enforced by unmodifiable wrapping in
  `DefaultActionValidatorManager`.
