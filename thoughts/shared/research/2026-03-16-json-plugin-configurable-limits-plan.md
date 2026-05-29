---
date: 2026-03-16T12:00:00+01:00
topic: "JSON Plugin Configurable Limits - Implementation Plan"
tags: [plan, json-plugin, configuration, hardening, WW-5618]
status: ready
---

# Implementation Plan: JSON Plugin Configurable Limits

## JIRA Issue

**Ticket:** [WW-5618](https://issues.apache.org/jira/browse/WW-5618)

---

## Key Design Decisions

1. **Instance methods over static:** `JSONUtil.deserialize()` becomes instance methods. `JSONInterceptor` gets `JSONUtil` injected via Container (it's already a registered prototype bean). `JSONUtil` gets `JSONReader` and `JSONWriter` injected via simple `@Inject` (no manual Container lookup).

2. **Naming convention:** Both reader and writer follow the `Struts*` naming convention for framework implementations. `JSONReader` becomes an interface with `StrutsJSONReader` as the implementation. `DefaultJSONWriter` is renamed to `StrutsJSONWriter`. Both registered in `struts-plugin.xml` as default beans.

3. **Bean selection via `BeanSelectionProvider`:** A new `JSONBeanSelectionProvider` (extending `AbstractBeanSelectionProvider`) uses the `alias()` mechanism to resolve `JSONReader` and `JSONWriter` beans by constant-configured name. This replaces the manual two-step `container.getInstance()` lookup in `JSONUtil.setContainer()` and follows the same pattern as `VelocityBeanSelectionProvider`. Users can swap implementations by name, class name, or Spring bean ID.

4. **Backward compatibility:** The current `JSONReader` class is used only internally — `JSONUtil.deserialize()` calls `new JSONReader()` in a static method. No external code should be extending it. The static `deserialize()` methods on `JSONUtil` will be deprecated but kept (delegating to instance methods) to avoid breaking any direct callers.

5. **`@Inject` pattern:** All injected constants use `String` parameter type with conversion in the setter body, following the established Struts convention (e.g., `setDefaultEncoding(String val)` in `JSONInterceptor`).

6. **Limits flow:** `JSONInterceptor` → sets limits on `JSONUtil` instance → `JSONUtil` passes limits to `JSONReader` before each `deserialize()` call. Per-action `<param>` overrides on the interceptor take precedence over global constants.

---

## Implementation Steps

### Step 1: Add constants to `JSONConstants`

**File:** `plugins/json/src/main/java/org/apache/struts2/json/JSONConstants.java`

Add new constants:

```java
public static final String JSON_READER = "struts.json.reader";
public static final String JSON_MAX_ELEMENTS = "struts.json.maxElements";
public static final String JSON_MAX_DEPTH = "struts.json.maxDepth";
public static final String JSON_MAX_LENGTH = "struts.json.maxLength";
public static final String JSON_MAX_STRING_LENGTH = "struts.json.maxStringLength";
public static final String JSON_MAX_KEY_LENGTH = "struts.json.maxKeyLength";
```

**Expected outcome:** Constants available for injection and XML configuration.

---

### Step 2: Update `JSONConstantConfig`

**File:** `plugins/json/src/main/java/org/apache/struts2/json/config/entities/JSONConstantConfig.java`

Add fields, getters, setters, and `getAllAsStringsMap()` entries for each new constant. Follow the existing pattern (`jsonWriter`, `jsonDateFormat`):

```java
private BeanConfig jsonReader;
private Integer jsonMaxElements;
private Integer jsonMaxDepth;
private Integer jsonMaxLength;
private Integer jsonMaxStringLength;
private Integer jsonMaxKeyLength;
```

**Expected outcome:** New constants wired into the ConstantConfig system.

---

### Step 3: Extract `JSONReader` interface and create `StrutsJSONReader`

**Current file:** `plugins/json/src/main/java/org/apache/struts2/json/JSONReader.java`
**New file:** `plugins/json/src/main/java/org/apache/struts2/json/StrutsJSONReader.java`

#### 3a: Create `JSONReader` interface

Replace the current class with an interface:

```java
public interface JSONReader {

    int DEFAULT_MAX_ELEMENTS = 10_000;
    int DEFAULT_MAX_DEPTH = 64;
    int DEFAULT_MAX_STRING_LENGTH = 262_144;    // 256KB
    int DEFAULT_MAX_KEY_LENGTH = 512;

    Object read(String string) throws JSONException;

    void setMaxElements(int maxElements);
    void setMaxDepth(int maxDepth);
    void setMaxStringLength(int maxStringLength);
    void setMaxKeyLength(int maxKeyLength);
}
```

#### 3b: Create `StrutsJSONReader` implementation

Rename current `JSONReader` class to `StrutsJSONReader implements JSONReader`. Add:

- Limit fields with defaults from the interface constants
- A `depth` counter field, incremented on entry to `array()`/`object()`, decremented on exit (use try/finally)
- In `array()`: check `ret.size() >= maxElements` before `ret.add()`, throw `JSONException` if exceeded
- In `object()`: check `ret.size() >= maxElements` before `ret.put()`, throw `JSONException` if exceeded
- In `read()`: check `depth >= maxDepth` before calling `array()`/`object()`, throw `JSONException` if exceeded
- In `string()`: check `buf.length() >= maxStringLength` in the character loop, throw `JSONException` if exceeded
- In `object()`: check key length against `maxKeyLength` after reading key string

Error messages should be clear and include the limit value, e.g.:
`"JSON array exceeds maximum allowed elements (10000). Use struts.json.maxElements to increase the limit."`

**Expected outcome:** `JSONReader` is an interface; `StrutsJSONReader` enforces configurable bounds.

---

### Step 4: Rename `DefaultJSONWriter` to `StrutsJSONWriter`

**Current file:** `plugins/json/src/main/java/org/apache/struts2/json/DefaultJSONWriter.java`
**New file:** `plugins/json/src/main/java/org/apache/struts2/json/StrutsJSONWriter.java`

Rename the class from `DefaultJSONWriter` to `StrutsJSONWriter`. This is a mechanical rename — the `JSONWriter` interface stays unchanged.

Changes required:
- Rename class file and class declaration
- Update `struts-plugin.xml` bean registration: `class="org.apache.struts2.json.StrutsJSONWriter"`
- Update all test references (~40 occurrences across `JSONResultTest.java`, `DefaultJSONWriterTest.java`, `JSONUtilTest.java`, `JSONEnumTest.java`)
- Rename `DefaultJSONWriterTest.java` to `StrutsJSONWriterTest.java`
- Update resource references (e.g., `DefaultJSONWriter.class.getResource(...)` → `StrutsJSONWriter.class.getResource(...)`)

**Expected outcome:** Writer follows the same `Struts*` naming convention as the reader. `JSONWriter` interface is unchanged — no impact on custom implementations.

---

### Step 5: Create `JSONBeanSelectionProvider`

**New file:** `plugins/json/src/main/java/org/apache/struts2/json/JSONBeanSelectionProvider.java`

Create a bean selection provider following the `VelocityBeanSelectionProvider` pattern:

```java
package org.apache.struts2.json;

import org.apache.struts2.config.AbstractBeanSelectionProvider;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.inject.Scope;
import org.apache.struts2.util.location.LocatableProperties;

public class JSONBeanSelectionProvider extends AbstractBeanSelectionProvider {

    @Override
    public void register(ContainerBuilder builder, LocatableProperties props)
            throws ConfigurationException {
        alias(JSONReader.class, JSONConstants.JSON_READER, builder, props, Scope.PROTOTYPE);
        alias(JSONWriter.class, JSONConstants.JSON_WRITER, builder, props, Scope.PROTOTYPE);
    }
}
```

This uses the standard `alias()` mechanism from `AbstractBeanSelectionProvider` which:
1. Reads the constant value (e.g., `struts.json.reader` → `"struts"`)
2. Finds the bean registered under that name
3. Aliases it to `Container.DEFAULT_NAME` so plain `@Inject` resolves it
4. Falls back to class name loading or Spring bean ID delegation if the name isn't a registered bean

**Expected outcome:** `JSONReader` and `JSONWriter` beans are selectable via constants using the standard Struts bean aliasing mechanism. Users can swap implementations by bean name, fully qualified class name, or Spring bean ID.

---

### Step 6: Update `JSONUtil` to use instance methods and simple `@Inject`

**File:** `plugins/json/src/main/java/org/apache/struts2/json/JSONUtil.java`

#### 6a: Replace manual Container lookup with simple `@Inject`

Remove the existing `setContainer()` method with its manual two-step resolution. Replace with direct injection (the `BeanSelectionProvider` aliases handle the name resolution):

```java
private JSONReader reader;
private JSONWriter writer;

@Inject
public void setReader(JSONReader reader) {
    this.reader = reader;
}

@Inject
public void setWriter(JSONWriter writer) {
    this.writer = writer;
}

public JSONReader getReader() {
    return reader;
}
```

#### 6b: Add instance `deserialize()` methods with `maxLength` check

```java
public Object deserialize(Reader reader, int maxLength) throws JSONException {
    BufferedReader bufferReader = new BufferedReader(reader);
    String line;
    StringBuilder buffer = new StringBuilder();
    try {
        while ((line = bufferReader.readLine()) != null) {
            buffer.append(line);
            if (buffer.length() > maxLength) {
                throw new JSONException("JSON input exceeds maximum allowed length ("
                    + maxLength + "). Use struts.json.maxLength to increase the limit.");
            }
        }
    } catch (IOException e) {
        throw new JSONException(e);
    }
    return this.reader.read(buffer.toString());
}
```

#### 6c: Deprecate static `deserialize()` methods

Keep existing static methods but mark `@Deprecated` and delegate internally (create a default `StrutsJSONReader` for backward compatibility):

```java
@Deprecated
public static Object deserialize(String json) throws JSONException {
    StrutsJSONReader reader = new StrutsJSONReader();
    return reader.read(json);
}
```

**Expected outcome:** `JSONUtil` uses simple `@Inject` for both reader and writer; limits flow through instance methods; static API preserved but deprecated.

---

### Step 7: Wire limits into `JSONInterceptor`

**File:** `plugins/json/src/main/java/org/apache/struts2/json/JSONInterceptor.java`

#### 7a: Inject `JSONUtil` instance instead of static access

```java
private JSONUtil jsonUtil;

@Inject
public void setJsonUtil(JSONUtil jsonUtil) {
    this.jsonUtil = jsonUtil;
}
```

#### 7b: Add limit fields with `@Inject` from constants

```java
private int maxElements = JSONReader.DEFAULT_MAX_ELEMENTS;
private int maxDepth = JSONReader.DEFAULT_MAX_DEPTH;
private int maxLength = 2_097_152;  // 2MB
private int maxStringLength = JSONReader.DEFAULT_MAX_STRING_LENGTH;
private int maxKeyLength = JSONReader.DEFAULT_MAX_KEY_LENGTH;

@Inject(value = JSONConstants.JSON_MAX_ELEMENTS, required = false)
public void setMaxElements(String maxElements) {
    this.maxElements = Integer.parseInt(maxElements);
}

@Inject(value = JSONConstants.JSON_MAX_DEPTH, required = false)
public void setMaxDepth(String maxDepth) {
    this.maxDepth = Integer.parseInt(maxDepth);
}

// ... same pattern for maxLength, maxStringLength, maxKeyLength
```

#### 7c: Update `intercept()` to use instance `jsonUtil` and pass limits

Replace the static call:
```java
// Before:
Object obj = JSONUtil.deserialize(request.getReader());

// After:
jsonUtil.getReader().setMaxElements(maxElements);
jsonUtil.getReader().setMaxDepth(maxDepth);
jsonUtil.getReader().setMaxStringLength(maxStringLength);
jsonUtil.getReader().setMaxKeyLength(maxKeyLength);
Object obj = jsonUtil.deserialize(request.getReader(), maxLength);
```

Do the same for the SMD deserialization path (line 136).

Note: Since `JSONUtil` is prototype-scoped, the reader instance is per-interceptor invocation when injected properly. But to be safe, limits should be set before each deserialization call.

#### 7d: Also update `JSONResult` and `JSONValidationInterceptor` if they use static `JSONUtil.deserialize()`

Check these classes and update them to use injected `JSONUtil` if they call the static deserialize methods.

**Expected outcome:** Limits flow from interceptor → JSONUtil → JSONReader per request. Configurable globally and per-action.

---

### Step 8: Register beans and defaults in `struts-plugin.xml`

**File:** `plugins/json/src/main/resources/struts-plugin.xml`

```xml
<!-- Bean selection provider for JSONReader/JSONWriter aliasing -->
<bean-selection name="jsonBeans"
      class="org.apache.struts2.json.JSONBeanSelectionProvider"/>

<!-- JSONWriter bean (renamed from DefaultJSONWriter) -->
<bean type="org.apache.struts2.json.JSONWriter" name="struts"
      class="org.apache.struts2.json.StrutsJSONWriter" scope="prototype"/>
<constant name="struts.json.writer" value="struts"/>

<!-- JSONReader bean -->
<bean type="org.apache.struts2.json.JSONReader" name="struts"
      class="org.apache.struts2.json.StrutsJSONReader" scope="prototype"/>
<constant name="struts.json.reader" value="struts"/>

<!-- JSONUtil (prototype — not thread-safe) -->
<bean class="org.apache.struts2.json.JSONUtil" scope="prototype"/>

<!-- Default limits -->
<constant name="struts.json.maxElements" value="10000"/>
<constant name="struts.json.maxDepth" value="64"/>
<constant name="struts.json.maxLength" value="2097152"/>
<constant name="struts.json.maxStringLength" value="262144"/>
<constant name="struts.json.maxKeyLength" value="512"/>
```

**Expected outcome:** Sensible defaults applied out-of-the-box; all values overridable. Users can swap implementations via constants.

---

### Step 9: Write tests

**File:** `plugins/json/src/test/java/org/apache/struts2/json/StrutsJSONReaderTest.java` (new, for the implementation)
**File:** `plugins/json/src/test/java/org/apache/struts2/json/JSONReaderTest.java` (existing, keep for backward compat of deprecated static API)

#### Unit tests for `StrutsJSONReader`:
- [ ] Array with elements under limit parses successfully
- [ ] Array exceeding `maxElements` throws `JSONException` with descriptive message
- [ ] Object exceeding `maxElements` throws `JSONException`
- [ ] Nesting within `maxDepth` parses successfully
- [ ] Nesting exceeding `maxDepth` throws `JSONException`
- [ ] String within `maxStringLength` parses successfully
- [ ] String exceeding `maxStringLength` throws `JSONException`
- [ ] Object key exceeding `maxKeyLength` throws `JSONException`
- [ ] Default limits allow typical JSON payloads (regression)
- [ ] Custom limits via setters work correctly
- [ ] Depth counter resets correctly after parsing (no state leakage)

#### Unit tests for `JSONUtil` instance methods:
- [ ] Input exceeding `maxLength` throws `JSONException` before parsing begins
- [ ] Input within `maxLength` parses successfully
- [ ] Deprecated static methods still work (backward compat)

#### Integration test (if feasible):
- [ ] Per-action `<param>` override applies different limits than global default

**Expected outcome:** All limit boundaries tested; existing tests continue to pass.

---

### Step 10: Update documentation

- Update JSON plugin documentation page to describe new configuration options
- Add migration notes: mention that new defaults may reject unusually large payloads
- Document per-action override pattern with XML example
- Document custom `JSONReader`/`JSONWriter` implementation pattern

---

## Files Modified (Summary)

| File | Change |
|------|--------|
| `JSONConstants.java` | Add 6 new constants |
| `JSONConstantConfig.java` | Add fields, getters, setters for new constants |
| `JSONReader.java` | **Rewrite** — becomes an interface with limit setters and defaults |
| `StrutsJSONReader.java` | **New** — implementation with depth tracking and bounds checks |
| `DefaultJSONWriter.java` | **Rename** → `StrutsJSONWriter.java` (class name + file) |
| `JSONBeanSelectionProvider.java` | **New** — bean aliasing for reader/writer via constants |
| `JSONUtil.java` | Replace manual Container lookup with `@Inject`, add instance `deserialize()`, deprecate static ones |
| `JSONInterceptor.java` | Inject `JSONUtil`, add `@Inject` + setter methods for limits |
| `struts-plugin.xml` | Add `bean-selection`, register `StrutsJSONReader`, rename writer bean, add defaults |
| `StrutsJSONReaderTest.java` | **New** — test cases for all limits |
| `DefaultJSONWriterTest.java` | **Rename** → `StrutsJSONWriterTest.java`, update all references |
| `JSONResultTest.java` | Update `DefaultJSONWriter` → `StrutsJSONWriter` (~25 occurrences) |
| `JSONUtilTest.java` | Update `DefaultJSONWriter` → `StrutsJSONWriter` references |
| `JSONEnumTest.java` | Update `DefaultJSONWriter` → `StrutsJSONWriter` references |
| `JSONReaderTest.java` | Keep existing tests, verify backward compat |

## Configuration Examples

### Global defaults (struts.xml)
```xml
<constant name="struts.json.maxElements" value="50000"/>
<constant name="struts.json.maxDepth" value="32"/>
<constant name="struts.json.maxLength" value="5242880"/> <!-- 5MB -->
```

### Per-action override
```xml
<action name="importData" class="com.example.ImportAction">
    <interceptor-ref name="json">
        <param name="maxElements">100000</param>
        <param name="maxDepth">10</param>
        <param name="maxLength">10485760</param> <!-- 10MB -->
    </interceptor-ref>
    <result type="json"/>
</action>
```

### Custom JSONReader implementation
```xml
<bean type="org.apache.struts2.json.JSONReader" name="custom"
      class="com.example.MyJSONReader" scope="prototype"/>
<constant name="struts.json.reader" value="custom"/>
```

### Custom JSONWriter implementation
```xml
<bean type="org.apache.struts2.json.JSONWriter" name="custom"
      class="com.example.MyJSONWriter" scope="prototype"/>
<constant name="struts.json.writer" value="custom"/>
```

### Custom implementation via Spring bean ID
```xml
<!-- Works because BeanSelectionProvider falls back to ObjectFactory/Spring -->
<constant name="struts.json.reader" value="mySpringJsonReaderBean"/>
```

## Risks and Considerations

1. **Breaking change — `JSONReader` becomes an interface:** Code that directly instantiated `new JSONReader()` will break. However, `JSONReader` was only used internally by `JSONUtil.deserialize()` static methods. External code calling the static API will still work via the deprecated path. Risk: **low**.

2. **Breaking change — static `deserialize()` deprecated:** Callers using `JSONUtil.deserialize(String)` or `JSONUtil.deserialize(Reader)` statically will get deprecation warnings but the methods still work. They just won't have limit enforcement. Risk: **none** (functional).

3. **New defaults may reject large payloads:** A payload with >10,000 array elements or >64 nesting depth will now be rejected. Mitigation: defaults are generous for typical use; clear error messages include the constant name to override. Risk: **low**.

4. **Performance:** Bounds checks add negligible overhead (integer comparison per element/depth increment).

5. **Breaking change — `DefaultJSONWriter` renamed to `StrutsJSONWriter`:** Code that directly references `DefaultJSONWriter` (e.g., `new DefaultJSONWriter()` in tests or custom code) will break. However, `DefaultJSONWriter` was never part of the public API — users should reference the `JSONWriter` interface. Bean registration in `struts-plugin.xml` is internal. Risk: **low** — affects custom code that bypasses the Container.

6. **Thread safety:** `StrutsJSONReader` is not thread-safe (instance fields for parser state). Registered as `scope="prototype"` — same as `StrutsJSONWriter`. Each deserialization gets a fresh instance.

7. **Prototype scope and limit setting:** Since `JSONUtil` is prototype-scoped and holds a prototype `JSONReader`, the interceptor sets limits on the reader before each deserialization. This is safe because each interceptor invocation gets its own `JSONUtil` instance from the Container.

8. **`BeanSelectionProvider` ordering:** The `<bean-selection>` element in `struts-plugin.xml` must appear so that the beans it references (`StrutsJSONReader`, `StrutsJSONWriter`) are already registered. Since `<bean>` elements are processed before `<bean-selection>`, this works naturally.
