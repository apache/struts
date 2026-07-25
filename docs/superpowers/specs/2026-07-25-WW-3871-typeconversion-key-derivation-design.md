# WW-3871 — `@TypeConversion` key derivation

**Ticket:** [WW-3871](https://issues.apache.org/jira/browse/WW-3871) — TypeConversion annotation support improvement
**Target version:** 7.3.0
**Date:** 2026-07-25

## Problem

The reporter asked that `@TypeConversion` build its own key from the property name once a
`ConversionRule` is given, instead of forcing:

```java
@TypeConversion(key = "CreateIfNull_users", rule = ConversionRule.CreateIfNull, value = "true")
```

Half of this already works. Commit `77cbafb74` (2018) taught `XWorkConverter` to derive the key for
**method-level** annotations: with no `key`, the property name is resolved from the method and the
rule's prefix is prepended (`XWorkConverter.java:526-545`).

Three gaps remain:

1. **Class-level `@Conversion(conversions = {...})` has no derivation at all.** The `key` is used
   verbatim (`XWorkConverter.java:504-519`), so callers still spell out prefixes — see
   `core/src/test/java/org/apache/struts2/util/MyBeanAction.java:38-41`.
2. **`@TypeConversion` is `@Target({METHOD})`**, while its own Javadoc claims it "can be applied at
   property and method level" (`TypeConversion.java:51`).
3. **Explicit keys are never normalised.** `@TypeConversion(key = "foo", rule = CREATE_IF_NULL)` on a
   setter registers `foo`, a mapping nothing reads — `DefaultObjectTypeDeterminer` looks up
   `CreateIfNull_foo`.

Two defects live in the same code block and are fixed here rather than left behind:

- `break` where `continue` is meant (`XWorkConverter.java:508` and `:542`). One already-mapped key
  aborts the **remaining** `@TypeConversion` entries in a `@Conversion` array.
- An empty class-level `key` registers a mapping under `""`. `DefaultConversionAnnotationProcessor`
  guards only `null` (`:58`).

## Goals

- A bare property name in `key` works at class, method and field level, for every `ConversionRule`.
- Existing annotations that spell out the prefix keep working byte-for-byte.
- `@TypeConversion` becomes usable on fields, matching its documentation.
- The rule-to-prefix table lives in one place.

## Non-goals

- `ConversionRule.COLLECTION` / the `Collection_` prefix stays deprecated-as-is.
- `ConversionRule.MAP` keeps having no prefix of its own.
- No changes to `struts-conversion.properties` or `<Class>-conversion.properties` parsing. This
  ticket is annotations only.

## Design

### 1. Rule-to-prefix mapping moves onto `ConversionRule`

`org.apache.struts2.conversion.annotations.ConversionRule` gains:

```java
public String prefix() {
    return switch (this) {
        case COLLECTION     -> DefaultObjectTypeDeterminer.DEPRECATED_ELEMENT_PREFIX; // "Collection_"
        case CREATE_IF_NULL -> DefaultObjectTypeDeterminer.CREATE_IF_NULL_PREFIX;     // "CreateIfNull_"
        case ELEMENT        -> DefaultObjectTypeDeterminer.ELEMENT_PREFIX;            // "Element_"
        case KEY            -> DefaultObjectTypeDeterminer.KEY_PREFIX;                // "Key_"
        case KEY_PROPERTY   -> DefaultObjectTypeDeterminer.KEY_PROPERTY_PREFIX;       // "KeyProperty_"
        case PROPERTY, MAP  -> "";
    };
}
```

`PROPERTY` and `MAP` returning `""` preserves today's behaviour: `DefaultObjectTypeDeterminer` reads
map and collection metadata through the `Key_` and `Element_` keys, never through a `Map_` key.

The `annotations` package already depends on `conversion.impl` (`TypeConversion.converterClass()`
defaults to `XWorkBasicConverter.class`), so referencing the prefix constants adds no new coupling.
An exhaustive `switch` over the enum means a future rule cannot silently miss a prefix.

### 2. One key resolver in `XWorkConverter`

```java
static String resolveKey(TypeConversion tc, String name) {
    if (name == null || name.isEmpty()) {
        return null;                        // caller skips the entry and logs WARN
    }
    if (tc.type() == ConversionType.APPLICATION) {
        return name;                        // key is a class name, never prefixed
    }
    String prefix = tc.rule().prefix();
    return name.startsWith(prefix) ? name : prefix + name;
}
```

All three annotation passes route both explicit keys and derived property names through this
function, so the three call sites cannot drift apart.

**The `APPLICATION` carve-out** is belt-and-braces: application-scope entries use the default
`PROPERTY` rule in practice, where the prefix is `""` anyway. Without it,
`@TypeConversion(type = APPLICATION, key = "java.util.Date", rule = ELEMENT)` would register
`Element_java.util.Date` into the global converter map, which nothing reads.

**`name.startsWith(prefix)`** is the backward-compatibility guarantee: an already-prefixed key is
returned untouched, so `key = "KeyProperty_annotatedBeanMap"` and `key = "annotatedBeanMap"` both
resolve to `KeyProperty_annotatedBeanMap` under `rule = KEY_PROPERTY`. It misfires only for a
property literally named `KeyProperty_foo` (or another prefix), which is legal Java but effectively
nonexistent; such a property gets exactly today's behaviour.

### 3. Field-level support

`@TypeConversion` becomes `@Target({ElementType.METHOD, ElementType.FIELD})`.

`addConverterMapping` splits into four ordered passes, keeping today's first-writer-wins rule:

```java
protected void addConverterMapping(Map<String, Object> mapping, Class clazz) {
    fileProcessor.process(mapping, clazz, buildConverterFilename(clazz));   // 1
    processClassLevelAnnotations(mapping, clazz);                           // 2
    processMethodAnnotations(mapping, clazz);                               // 3
    processFieldAnnotations(mapping, clazz);                                // 4  new
}
```

This yields the precedence **class > method > field**, which preserves current behaviour exactly:
class-level `@Conversion` already outranks methods, and field annotations — none of which exist
today — only fill gaps.

Extracting the three passes into named private methods is the targeted cleanup this ticket earns.
The current single method is roughly 45 lines of nested loops concealing the `break` defect, and a
fourth pass would push it past readable.

The field pass iterates `clazz.getDeclaredFields()` — declared, not inherited, because
`buildConverterMapping` already walks the class hierarchy and calls `addConverterMapping` per class.
It skips `static` and synthetic fields, which also makes the interface case a no-op
(`getDeclaredFields()` on an interface returns its constants). A field's own name is the property
name; no getter/setter parsing is involved.

### 4. Error handling

| Situation | Today | After |
|---|---|---|
| Class-level entry with `key = ""` | registers a `""` mapping | skipped, `WARN` naming class and annotation |
| `@TypeConversion` on a non-property method (`execute()`) with no key | silently dropped at DEBUG | skipped, `WARN` naming class and method |
| Field annotation whose key a method already claimed | n/a | skipped, `DEBUG` — the documented precedence, made visible |
| Second entry in a `@Conversion` array after a key collision | **dropped** (`break`) | processed (`continue`) |

The class-level `mapping.containsKey(...)` check moves **after** key resolution; it currently tests
the raw key, which post-change would be the wrong string. `DefaultConversionAnnotationProcessor.process`
keeps its `key == null` guard as defence in depth even though callers no longer pass null.

## Testing

Both affected test classes extend `XWorkTestCase`, i.e. JUnit 3 style — new tests use
`public void testXxx()` with no `@Test` annotation, which would silently never run there.

**`XWorkConverterTest` / `AnnotationXWorkConverterTest`:**

- bare key + rule resolves to the prefixed key
- already-prefixed key + same rule is unchanged (idempotence)
- `PROPERTY` and `MAP` rules leave the key untouched
- `type = APPLICATION` leaves the key untouched regardless of rule
- empty key and non-property method produce no mapping entry, and specifically no `""` key

**`MyBeanActionTest` — end-to-end:** `MyBeanAction` keeps its four spelled-out class-level prefixes
untouched, which is what proves existing applications don't break. A second fixture action declares
the same four conversions with **bare** keys; the test asserts both produce identical converter
mappings and identical bound results.

**Field support:** a fixture with `@TypeConversion` on a private field, asserting its derived key
matches the setter form's, plus a test that a method annotation wins when both a field and its
setter are annotated for the same key.

**`continue` regression:** a fixture whose `@Conversion` array has an early entry colliding with a
key already in the mapping, asserting the later entries still register. This regression is currently
invisible.

## Documentation

- `TypeConversion` Javadoc: the parameter table's `key` row gains the derivation rule (it currently
  says only "Defaults to the property name", which understates it); the class-level example drops its
  now-redundant prefixes; an `@since 7.3.0` note records that bare keys are accepted at class and
  field level, and that fields are a supported target.
- `ConversionRule` Javadoc: document `prefix()` and which rules have none.

## Compatibility

Source- and binary-compatible. The only behavioural change to existing code is that an explicit
method-level key carrying a non-`PROPERTY` rule without its prefix now resolves to the prefixed key.
That mapping is unreachable today, so the change turns a silent no-op into the behaviour the author
intended.
