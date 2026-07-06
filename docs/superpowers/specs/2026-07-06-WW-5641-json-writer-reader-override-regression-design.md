# WW-5641: Restore `struts.json.writer` / `struts.json.reader` override

**Ticket:** [WW-5641](https://issues.apache.org/jira/browse/WW-5641)
**Type:** Bug (regression)
**Component:** Plugin - JSON
**Date:** 2026-07-06

## Problem

A custom JSON writer/reader configured the documented way is silently ignored on the 7.2.x
line. The framework always uses the default `StrutsJSONWriter` / `StrutsJSONReader` regardless
of the application's override:

```xml
<bean type="org.apache.struts2.json.JSONWriter" name="flexJSONWriter"
      class="org.demo.FlexJSONWriter" scope="prototype"/>
<constant name="struts.json.writer" value="flexJSONWriter"/>
```

This worked on 7.1.x and regressed in 7.2.x. The extension point is still documented at
<https://struts.apache.org/plugins/json/>, so this is a regression, not an intended API change.
Confirmed by the `json-customize` module reproduction in apache/struts-examples PR #535
(`ProduceActionTest`): passes on 7.1.x, fails on 7.2.x.

This is a **functional regression** restoring a documented extension point — not a security fix
(no OGNL injection, parameter-filtering bypass, auth bypass, RCE, SSRF, path traversal, etc.),
so it follows the normal PR path.

## Root cause (verified against current `main`)

The 7.2.x JSON hardening rework added a `<bean-selection name="jsonBeans" .../>` element to the
plugin's **`struts-plugin.xml`** to select the `JSONWriter` / `JSONReader` default binding via
`JSONBeanSelectionProvider` → `AbstractBeanSelectionProvider.alias()`. The defect is *where*
that element lives, which determines *when* it runs.

- `XmlDocConfigurationProvider.registerBeanSelection()` (`:215-222`) invokes the bean-selection
  provider **inline, the moment the `<bean-selection>` element is parsed** inside the JSON
  plugin's `struts-plugin.xml`.
- In `Dispatcher.init()` the config sources are registered in this order (see
  `Dispatcher.java:696-704`): `struts-default.xml` → all `struts-plugin.xml` (plugin discovery)
  → the app's `struts.xml` (`init_TraditionalXmlConfigurations`) → core's
  `StrutsBeanSelectionProvider` (`init_AliasStandardObjects`) → `struts-deferred.xml`
  (`init_DeferredXmlConfigurations`).
- So the JSON `<bean-selection>` fires while parsing `struts-plugin.xml`, **before** the app's
  `struts.xml` folds its `flexJSONWriter` bean and its `struts.json.writer` constant into the
  shared `props`/builder. `alias()` reads `props.getProperty("struts.json.writer", "struts")` =
  `"struts"` (the plugin default) and locks `JSONWriter/DEFAULT_NAME → StrutsJSONWriter`.
  `alias()` binds the default name only once (guarded by `!builder.contains(type, DEFAULT_NAME)`),
  so nothing re-selects when the app config loads afterward. `JSONUtil` injects that frozen
  default binding and never sees the override.

The 7.1.x code was immune because it resolved the writer/reader by the constant value at
container-use time — after the full container (including the app `struts.xml`) was built.

## Chosen approach: run the JSON `<bean-selection>` late, via `struts-deferred.xml`

The framework already provides the idiomatic, order-safe mechanism for exactly this. From
`Dispatcher.java`:

> `struts-deferred.xml` can be used to load configuration which is sensitive to loading order
> such as 'bean-selection' elements

`init_DeferredXmlConfigurations()` loads `struts-deferred.xml` **last** — after the app's
`struts.xml` and after core's `StrutsBeanSelectionProvider`. A `<bean-selection>` placed there
runs once the app's beans and constants are already in `props`/builder, so `alias()` reads the
*effective* `struts.json.writer` value and aliases `JSONWriter/DEFAULT_NAME` to the application's
override.

This is the established convention in-tree: the **velocity plugin** keeps its `<bean>`
definitions in `struts-plugin.xml` and places its `<bean-selection name="velocityBeans" .../>`
in `plugins/velocity/src/main/resources/struts-deferred.xml`. The JSON plugin is the outlier
that put `<bean-selection>` in `struts-plugin.xml`. Core's own `StrutsBeanSelectionProvider` uses
the same late-running principle (registered by `init_AliasStandardObjects`, after all XML),
which is why application overrides of core beans (`struts.objectFactory`, converters, …) work.

### Changes

1. **Remove** the `<bean-selection name="jsonBeans" .../>` element from
   `plugins/json/src/main/resources/struts-plugin.xml` (line 68). Leave the `<bean name="struts">`
   writer/reader definitions, the `JSONUtil`/`JSONCacheDestroyable` beans, the constants, and the
   `json-default` package exactly as they are — mirroring velocity.
2. **Create** `plugins/json/src/main/resources/struts-deferred.xml` containing the moved
   `<bean-selection name="jsonBeans" class="org.apache.struts2.json.JSONBeanSelectionProvider"/>`.
3. **Revert `JSONUtil` to the 7.2.x state**: restore `@Inject` on `setWriter(JSONWriter)` and
   `setReader(JSONReader)`, and remove the `setContainer(Container)` method and the
   `org.apache.struts2.inject.Container` import introduced by the earlier (rejected) attempt.
   No Java source change remains — `JSONUtil` again injects the (now correct) default binding.

### Why this over resolving by-name inside `JSONUtil`

An alternative (rejected) was to have `JSONUtil` inject the `Container` and resolve the
writer/reader by the constant value at runtime. That works but sidesteps the framework's standard
bean-override mechanism, leaves the default `JSONWriter`/`JSONReader` container binding pointing
at the wrong implementation, and only fixes `JSONUtil`'s own consumption. The deferred
`<bean-selection>` fixes the binding itself: `container.getInstance(JSONWriter.class)` returns the
override for *any* consumer, `JSONUtil` needs no change, and the plugin now matches velocity and
core convention.

### What is intentionally *not* changed

- `StrutsJSONWriter` / `StrutsJSONReader` remain the shipped defaults (they are the `name="struts"`
  beans, aliased to the default only when no app override is present).
- The JSON DoS limits (`struts.json.maxDepth`, `maxElements`, `maxLength`, `maxStringLength`,
  `maxKeyLength`) and their `@Inject` setters are untouched.
- No core class changes. `JSONBeanSelectionProvider` and `AbstractBeanSelectionProvider` are
  unchanged — only *when* the provider runs changes, driven by which config file carries the
  element.

### Thread-safety

`JSONUtil` remains `prototype`; the writer/reader remain `prototype` (aliased with
`Scope.PROTOTYPE`). Lifecycle is identical to current `main` — no thread-safety change for the
stateful bean-info caching (`StrutsJSONWriter.clearBeanInfoCaches()`, `JSONCacheDestroyable`).

## Test plan

New files under `plugins/json/src/test/...`:

1. **`CustomTestJSONWriter`** — implements the current `JSONWriter` interface; `write(...)`
   returns a sentinel (`{"__customWriter__":true}`) for unambiguous serialization assertions.
   Implements: `write(Object)`, `write(Object, Collection<Pattern>, Collection<Pattern>, boolean)`,
   `setIgnoreHierarchy`, `setEnumAsBean`, `setDateFormatter`, `setCacheBeanInfo`,
   `setExcludeProxyProperties`.
2. **`CustomTestJSONReader`** — implements the current `JSONReader` interface as a marker:
   `read(String)` returns a sentinel, `setMaxElements`/`setMaxDepth`/`setMaxStringLength`/
   `setMaxKeyLength` are no-ops.
3. **Override config** `src/test/resources/struts-json-override.xml` — registers both custom
   beans (`scope="prototype"`) and the `struts.json.writer` / `struts.json.reader` constants
   pointing at them.
4. **`JSONWriterOverrideTest extends StrutsTestCase`** — boots the real `Dispatcher` config
   chain so the deferred `struts-deferred.xml` runs after the override. The config init param is
   `struts-default.xml,struts-plugin.xml,struts-json-override.xml` (defaults → JSON plugin beans
   → app override). `struts-deferred.xml` is **not** listed: `Dispatcher.init()` always runs
   `init_DeferredXmlConfigurations()` after the `config` chain, so the JSON plugin's
   `struts-deferred.xml` (on the classpath) loads last on its own — listing it explicitly would
   load it twice and emit a spurious "default mapping already assigned" warning. Because the fix
   now corrects the binding itself, the test asserts at **both** levels:
   - **Default container binding:** `container.getInstance(JSONWriter.class)` is a
     `CustomTestJSONWriter`; `container.getInstance(JSONReader.class)` is a `CustomTestJSONReader`.
   - **Effective use:** `jsonUtil.serialize(someBean, false)` returns the writer sentinel and
     `jsonUtil.getReader().getClass()` is `CustomTestJSONReader`.

   > Note: `XWorkTestCase` alone uses only `StrutsDefaultConfigurationProvider` and loads neither
   > the plugin `struts-plugin.xml` nor `struts-deferred.xml`, so it cannot reproduce the ordering.
   > `StrutsTestCase` (full `Dispatcher` init) is required.

Both assertions must **FAIL on current `main`** (before moving the element) and **PASS after**.

## Verification

- New `JSONWriterOverrideTest` fails before the fix, passes after.
- Full `struts2-json-plugin` module suite green: `mvn test -DskipAssembly -pl plugins/json` —
  DoS-limit and caching tests unaffected.

## Scope guardrails

- No change to `StrutsJSONWriter` / `StrutsJSONReader`, the DoS constants, `JSONUtil` (net-zero
  after revert), `JSONBeanSelectionProvider`, or any core class.
- Change is confined to two plugin resource files (`struts-plugin.xml`, new `struts-deferred.xml`)
  plus new test fixtures.
- Delivered via a PR from a feature branch referencing WW-5641 (never pushed to `main`).
