---
date: 2026-02-27T12:00:00+01:00
topic: "WW-4428: Add java.time (LocalDate, LocalDateTime) support to JSON plugin"
tags: [research, codebase, json-plugin, java-time, localdate, localdatetime, serialization, deserialization]
status: complete
git_commit: 4d2eb938351b0e84a393979045248e21b75766e9
---

# Research: WW-4428 — Java 8 Date/Time Support in JSON Plugin

**Date**: 2026-02-27

## Research Question

What is the current state of Java 8 `java.time` support (LocalDate, LocalDateTime, etc.) in the Struts JSON plugin, and what changes are needed to implement WW-4428?

## Summary

The JSON plugin has **zero java.time support**. Only `java.util.Date` and `java.util.Calendar` are handled. Java 8 date types like `LocalDate` and `LocalDateTime` fall through to JavaBean introspection during serialization (producing garbage like `{"dayOfMonth":23,"month":"DECEMBER",...}`) and throw exceptions during deserialization. The core module already has comprehensive java.time support via `DateConverter`, but none of it is wired into the JSON plugin.

## Detailed Findings

### 1. Serialization — DefaultJSONWriter

**File**: [`plugins/json/src/main/java/org/apache/struts2/json/DefaultJSONWriter.java`](https://github.com/apache/struts/blob/4d2eb938351b0e84a393979045248e21b75766e9/plugins/json/src/main/java/org/apache/struts2/json/DefaultJSONWriter.java)

The `process()` method (line ~163) dispatches on type:

```java
} else if (object instanceof Date) {
    this.date((Date) object, method);
} else if (object instanceof Calendar) {
    this.date(((Calendar) object).getTime(), method);
}
```

There is no branch for `java.time.temporal.TemporalAccessor` or any specific java.time type. These objects fall through to `processCustom()` → `bean()`, which introspects them as JavaBeans.

The `date()` method (line ~335) only accepts `java.util.Date` and uses `SimpleDateFormat`:

```java
protected void date(Date date, Method method) {
    // uses SimpleDateFormat with JSONUtil.RFC3339_FORMAT default
}
```

The `setDateFormatter()` method (line ~487) only creates a `SimpleDateFormat`.

### 2. Deserialization — JSONPopulator

**File**: [`plugins/json/src/main/java/org/apache/struts2/json/JSONPopulator.java`](https://github.com/apache/struts/blob/4d2eb938351b0e84a393979045248e21b75766e9/plugins/json/src/main/java/org/apache/struts2/json/JSONPopulator.java)

`isJSONPrimitive()` (line ~92) only recognizes `Date.class`:

```java
return clazz.isPrimitive() || clazz.equals(String.class) || clazz.equals(Date.class) ...
```

`convertPrimitive()` (line ~255) only handles `Date.class` via `SimpleDateFormat.parse()`. Java.time types will throw `JSONException("Incompatible types for property ...")`.

### 3. Format Configuration

**File**: [`plugins/json/src/main/java/org/apache/struts2/json/JSONUtil.java`](https://github.com/apache/struts/blob/4d2eb938351b0e84a393979045248e21b75766e9/plugins/json/src/main/java/org/apache/struts2/json/JSONUtil.java)

- `RFC3339_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"` (line 53) — the default format
- The java.time equivalent is `DateTimeFormatter.ISO_LOCAL_DATE_TIME`

### 4. @JSON Annotation

**File**: [`plugins/json/src/main/java/org/apache/struts2/json/annotations/JSON.java`](https://github.com/apache/struts/blob/4d2eb938351b0e84a393979045248e21b75766e9/plugins/json/src/main/java/org/apache/struts2/json/annotations/JSON.java)

Has `format()` attribute for per-property date format overrides — currently only used with `SimpleDateFormat`. Should be extended to work with `DateTimeFormatter` for java.time types.

### 5. @JSONFieldBridge Workaround

**Directory**: `plugins/json/src/main/java/org/apache/struts2/json/bridge/`

The `FieldBridge` interface provides a manual escape hatch (`objectToString`) but only supports serialization, not deserialization.

### 6. Core Module Already Has java.time Support

**File**: [`core/src/main/java/org/apache/struts2/conversion/impl/DateConverter.java`](https://github.com/apache/struts/blob/4d2eb938351b0e84a393979045248e21b75766e9/core/src/main/java/org/apache/struts2/conversion/impl/DateConverter.java)

Handles `LocalDate`, `LocalDateTime`, `LocalTime`, `OffsetDateTime` using `DateTimeFormatter.parseBest()`. This is not wired into the JSON plugin.

### 7. Test Coverage

- `DefaultJSONWriterTest.java` — only tests `java.util.Date` serialization (lines 115-142)
- `SingleDateBean.java` — test fixture with only a `java.util.Date` field
- `JSONPopulatorTest.java` — no dedicated date deserialization test
- Zero tests for any java.time type

## Gap Analysis

| Type | Serialization | Deserialization |
|---|---|---|
| `java.util.Date` | Supported | Supported |
| `java.util.Calendar` | Supported (→ Date) | Not supported |
| `java.time.LocalDate` | **Not supported** | **Not supported** |
| `java.time.LocalDateTime` | **Not supported** | **Not supported** |
| `java.time.LocalTime` | **Not supported** | **Not supported** |
| `java.time.ZonedDateTime` | **Not supported** | **Not supported** |
| `java.time.Instant` | **Not supported** | **Not supported** |
| `java.time.OffsetDateTime` | **Not supported** | **Not supported** |

## Implementation Points

To implement WW-4428, changes are needed in:

### DefaultJSONWriter.java
1. Add `instanceof` checks in `process()` for `LocalDate`, `LocalDateTime`, `LocalTime`, `ZonedDateTime`, `Instant`, `OffsetDateTime` (or a blanket `TemporalAccessor` check)
2. Add a new `temporal(TemporalAccessor, Method)` method using `DateTimeFormatter`
3. Use sensible defaults: `ISO_LOCAL_DATE` for `LocalDate`, `ISO_LOCAL_DATE_TIME` for `LocalDateTime`, etc.
4. Respect `@JSON(format=...)` annotation via `DateTimeFormatter.ofPattern()`

### JSONPopulator.java
1. Extend `isJSONPrimitive()` to recognize java.time classes
2. Extend `convertPrimitive()` to parse java.time types from strings using `DateTimeFormatter`
3. Respect `@JSON(format=...)` for custom formats

### JSONWriter.java (interface)
1. Consider adding `setDateTimeFormatter(String)` or reusing `setDateFormatter()` for both legacy and java.time

### Tests
1. Add test beans with java.time fields
2. Add serialization tests for each supported java.time type
3. Add deserialization tests for each type
4. Test `@JSON(format=...)` with java.time types
5. Test default format behavior

## Architecture Insights

- The JSON plugin was designed when Java 6 was the target, hence `SimpleDateFormat` throughout
- The `@JSON(format=...)` annotation is the natural extension point for per-field formatting
- The core module's `DateConverter` shows the established pattern for handling java.time in Struts
- Since Struts now requires Java 17+, there are no compatibility concerns with using java.time directly

## Historical Context

- WW-4428 was filed in December 2014 (Struts 2.3.20 era, targeting Java 6/7)
- Original constraint: couldn't add java.time directly due to Java 6/7 compatibility
- Related ticket: WW-5016 — Support java 8 date time in the date tag (already implemented in `components/Date.java`)
- No prior research documents in thoughts/ for this topic

## Related Research

None found in thoughts/shared/research/.

## Open Questions

1. Should `Instant` be serialized as epoch millis (number) or ISO-8601 string?
2. Should `ZonedDateTime` include the zone info in the default format?
3. Should the implementation use a blanket `TemporalAccessor` check or individual type checks?
4. Should `Calendar` deserialization also be added while we're at it?