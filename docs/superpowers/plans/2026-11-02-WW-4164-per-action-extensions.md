# WW-4164 Per-package / per-action extensions Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let `<package>` and `<action>` declare their own `extension` list. That list governs which request URLs map to the action and which extension generated URLs carry.

**Architecture:** The list is parsed into `PackageConfig` / `ActionConfig`, and the effective list is resolved when the runtime configuration is built. Inbound, `DefaultActionMapper` strips a declared-only extension when the stripped name resolves to an action. A new `ActionExtensionInterceptor` rejects disallowed extensions through a user-definable result, or falls back to 404. Outbound, an `ActionExtensionResolver` bean sets the extension on the `ActionMapping` in the four URL-building call sites before `getUriFromActionMapping`.

**Tech Stack:** Java 17, Struts core, JUnit 3 (`StrutsInternalTestCase`) + AssertJ, Maven.

**Spec:** `docs/superpowers/specs/2026-11-01-WW-4164-per-action-extensions-design.md`

## Global Constraints

- No behaviour change for an application that declares no `extension` attribute. Existing mapper, tag and result tests must pass untouched.
- Attribute format is identical to `struts.action.extension`: comma-separated, order significant, first entry is the default, an empty entry / trailing comma means "no extension".
- Attributes are added to `core/src/main/resources/struts-7.5.dtd`; no new DTD version.
- Tests are JUnit 3 style (`extends StrutsInternalTestCase`, `testXxx()` names) in every file touched here. New test files follow the same style. No Jupiter.
- Never `@Inject UnknownHandlerManager` into an interceptor; look it up from `invocation.getInvocationContext().getContainer()`.
- New public/protected members carry `@since 7.5.0` Javadoc. No inline comments beyond load-bearing ones.
- Run `./mvnw -q package -DskipTests -DskipAssembly` (full reactor), and core tests via `./mvnw test -DskipAssembly -pl core`, after every task. Log to a file and check the exit code.
- Commit messages: `WW-4164 <type>(<scope>): ...` with the `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>` trailer. Stage explicit paths only (never `git add -A`); check `git status` for stray `??` probe files.

## Review Focus

1. **DMI names with a declared extension** (`/foo!bar.html` with `html` declared only on `foo`). Expected: it is stripped and resolves to `foo`, because the bang part is ignored for the existence check. Test in Task 4.
2. **Namespace fallback** (`/unknown-ns/foo.html` where `foo` lives in the default `""` namespace). Expected: it is claimed exactly when `RuntimeConfiguration.getActionConfig` resolves it today, because the lookup uses the same fallback. Test in Task 4.
3. **Wildcard actions** (`<action name="edit_*" extension="html">`). Expected: `/edit_user.html` is claimed and allowed, and `/edit_user.action` is rejected. Tests in Task 2 (matcher copies the list) and Task 4.
4. **Configuration reload in devMode.** Expected: the declared-extension cache refreshes when `RuntimeConfiguration` is replaced. Test in Task 4.
5. **Outbound to an undeclared target from a declared-only-extension page** (current request `.html`, `html` declared elsewhere, target undeclared, global `action,,`). Expected: the URL ends `.action`, not `.html`. Test in Task 5.

---

### Task 1: `ActionExtensions` list utilities

**Files:**
- Create: `core/src/main/java/org/apache/struts2/dispatcher/mapper/ActionExtensions.java`
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/mapper/DefaultActionMapper.java:247-260` (`setExtensions` delegates to `ActionExtensions.parse`)
- Test: `core/src/test/java/org/apache/struts2/dispatcher/mapper/ActionExtensionsTest.java`

**Interfaces:**
- Produces (`public final class ActionExtensions`, private constructor):
  - `static List<String> parse(String value)` returns an unmodifiable list in declared order, with a trailing comma adding `""`. Blank entries in the middle (`a,,b`) are kept as `""`. `parse("")` returns `List.of("")`. `parse(null)` returns `null`.
  - `static boolean allows(List<String> allowed, String extension)`: a `null` extension is treated as `""`.
  - `static String select(List<String> allowed, String current)`: returns `current` (null counts as `""`) when `allows`, otherwise `allowed.get(0)`.

- [ ] **Step 1: Write the failing tests** `ActionExtensionsTest extends TestCase`:
  - `testParseKeepsOrder`: `parse("html,action")` equals `List.of("html","action")`.
  - `testParseTrailingCommaAddsEmpty`: `parse("action,,")` equals `List.of("action","")` (today's `DefaultActionMapper.setExtensions` result; `DefaultActionMapperTest` keeps guarding the mapper side).
  - `testParseEmptyMeansNoExtensionOnly`: `parse("")` equals `List.of("")`.
  - `testParseNull`: `parse(null)` is null.
  - `testAllowsNullAsEmpty`: `allows(List.of("html",""), null)` is true, and `allows(List.of("html"), null)` is false.
  - `testSelectKeepsAllowedCurrent`: `select(List.of("action","html"), "html")` is `"html"`.
  - `testSelectFallsBackToFirst`: `select(List.of("action",""), "html")` is `"action"`.
- [ ] **Step 2: Run** `./mvnw test -DskipAssembly -pl core -Dtest=ActionExtensionsTest`. Expected: compilation FAIL (class missing).
- [ ] **Step 3: Implement `ActionExtensions`.** Make `DefaultActionMapper.setExtensions(String)` call `parse` for non-empty input. Keep its existing `null` / empty handling (`StringUtils.isNotEmpty` false → `extensions = null`) unchanged.
- [ ] **Step 4: Run** `ActionExtensionsTest` and `DefaultActionMapperTest`. Expected: PASS.
- [ ] **Step 5: Commit** `WW-4164 feat(core): add ActionExtensions list parsing`.

### Task 2: `extension` attribute in XML config

**Files:**
- Modify: `core/src/main/resources/struts-7.5.dtd`: `extension CDATA #IMPLIED` in `<!ATTLIST package>` (line ~39) and `<!ATTLIST action>` (line ~103)
- Modify: `core/src/main/java/org/apache/struts2/config/entities/PackageConfig.java`
- Modify: `core/src/main/java/org/apache/struts2/config/entities/ActionConfig.java`
- Modify: `core/src/main/java/org/apache/struts2/config/providers/XmlDocConfigurationProvider.java` (`buildPackageContext` ~line 619; `buildActionConfig` ~line 497)
- Modify: `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java:538-580` (`buildFullActionConfig`)
- Modify: `core/src/main/java/org/apache/struts2/config/impl/ActionConfigMatcher.java:~147`
- Create: `core/src/test/resources/org/apache/struts2/config/providers/struts-extension.xml`, `struts-extension-invalid.xml`
- Test: `core/src/test/java/org/apache/struts2/config/providers/XmlConfigurationProviderExtensionTest.java`, modelled on `XmlConfigurationProviderHttpMethodTest`

**Interfaces:**
- Consumes: `ActionExtensions.parse`.
- Produces:
  - `PackageConfig`: `private List<String> extensions` (null = undeclared); `List<String> getExtensions()`; `List<String> getFullExtensions()` returns its own list if non-null, otherwise the first non-null `getFullExtensions()` among `parents` in order, otherwise null. `Builder extensions(List<String>)`. Copy the list in the copy constructor; include it in `equals`/`hashCode`.
  - `ActionConfig`: `private List<String> extensions`; `List<String> getExtensions()` (null = undeclared at action and package level); `Builder extensions(List<String>)`. Copy it in the copy constructor; include it in `equals`/`hashCode`/`toString` (like `httpMethods`).
  - After `buildFullActionConfig`, `ActionConfig.getExtensions()` holds the **effective declared** list: the action's own, else `packageContext.getFullExtensions()`, else null.
  - `XmlDocConfigurationProvider`: `protected List<String> buildExtensions(Element element, Location location)` returns null when `!element.hasAttribute("extension")`, otherwise the `parse` result. Throws `ConfigurationException` for an entry that starts with `.` or contains `/`, naming the element's `name` attribute and the bad value.

- [ ] **Step 1: Write the fixture** `struts-extension.xml` (DOCTYPE `-//Apache Software Foundation//DTD Struts Configuration 7.5//EN`). It contains:
  - package `base` (abstract, `extension="html"`);
  - package `child` (extends `base`, namespace `/child`) with actions `inherits` (no attribute) and `own` (`extension="json,"`);
  - package `plain` (namespace `/plain`) with action `undeclared`, and action `none` with `extension=""`;
  - package `wild` (namespace `/wild`) with action `edit_*` (`extension="html"`).

  `struts-extension-invalid.xml` contains one action with `extension=".html"`.
- [ ] **Step 2: Write the failing tests**:
  - `testActionInheritsPackageChainExtension`: runtime config `/child`/`inherits` `getExtensions()` equals `List.of("html")`.
  - `testActionOverridesPackage`: `/child`/`own` equals `List.of("json","")`.
  - `testUndeclaredIsNull`: `/plain`/`undeclared` is null.
  - `testEmptyAttributeMeansNoExtensionOnly`: `/plain`/`none` equals `List.of("")`.
  - `testWildcardMatchCopiesExtensions`: `getActionConfig("/wild","edit_user").getExtensions()` equals `List.of("html")`.
  - `testLeadingDotRejected`: loading the invalid fixture throws `ConfigurationException` whose message contains `.html`.
- [ ] **Step 3: Run** `-Dtest=XmlConfigurationProviderExtensionTest`. Expected: FAIL (DTD validation rejects the attribute / methods missing).
- [ ] **Step 4: Implement** the DTD attributes, entity fields, `buildExtensions` wiring, effective resolution in `buildFullActionConfig` (on the `new ActionConfig.Builder(baseConfig)` at ~line 572), and `.extensions(orig.getExtensions())` in `ActionConfigMatcher`.
- [ ] **Step 5: Run** the new test plus `XmlConfigurationProviderHttpMethodTest` and the whole `org.apache.struts2.config` test package. Expected: PASS.
- [ ] **Step 6: Commit** `WW-4164 feat(core): parse extension attribute on package and action`.

### Task 3: `ActionExtensionInterceptor` and startup warnings

**Files:**
- Create: `core/src/main/java/org/apache/struts2/interceptor/ActionExtensionInterceptor.java`
- Modify: `core/src/main/java/org/apache/struts2/interceptor/httpmethod/HttpMethodInterceptor.java:173-182`: move `canResolveResult` to a shared helper
- Create: `core/src/main/java/org/apache/struts2/interceptor/ResultResolution.java` (package-public final class, static method)
- Modify: `core/src/main/resources/struts-default.xml`: declare `<interceptor name="actionExtension" class="org.apache.struts2.interceptor.ActionExtensionInterceptor"/>` after `httpMethod` (line 88), and add `<interceptor-ref name="actionExtension"/>` immediately after every `<interceptor-ref name="httpMethod"/>` (lines ~99, ~161, ~193)
- Modify: `core/src/main/java/org/apache/struts2/config/impl/DefaultConfiguration.java`: `warnIfExtensionNotEnforced`, next to `warnIfHttpMethodNotEnforced` (~line 563)
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/Dispatcher.java:719-745`: mapper warning at the end of `init()`
- Test: `core/src/test/java/org/apache/struts2/interceptor/ActionExtensionInterceptorTest.java`

**Interfaces:**
- Consumes: `ActionConfig.getExtensions()`, `ActionExtensions.allows`.
- Produces:
  - `public final class ResultResolution { static boolean canResolve(ActionInvocation invocation, String resultName) }`: the body moved verbatim from `HttpMethodInterceptor.canResolveResult`, which now calls it.
  - `public class ActionExtensionInterceptor extends AbstractInterceptor`, with property `badExtensionResultName` (default `"bad-extension"`) and `setBadExtensionResultName(String)`. `intercept` passes (`invocation.invoke()`) when any of these holds:
    - `config.getExtensions() == null`;
    - `ActionContext.getActionMapping() == null`;
    - the mapping's name/namespace differ from `proxy.getActionName()` / `proxy.getNamespace()` (`Objects.equals`);
    - `ActionExtensions.allows(...)`.

    Otherwise it calls `protected String rejectRequest(ActionInvocation)`, which returns `badExtensionResultName` when `ResultResolution.canResolve`, else `response.sendError(404)` and returns `Action.NONE`.

- [ ] **Step 1: Write the failing tests** `ActionExtensionInterceptorTest extends StrutsInternalTestCase`. Build the proxy with an `ActionConfig` the way `HttpMethodInterceptorTest` does, and put an `ActionMapping` on the context:
  - `testUndeclaredPasses`: config without extensions, mapping extension `"html"` → `invoke` called, result `"success"`.
  - `testAllowedPasses`: extensions `["html"]`, mapping ext `"html"` → passes.
  - `testNullMappingExtensionCountsAsEmpty`: extensions `["action",""]`, mapping ext `null` → passes.
  - `testRejectedWithoutResultSends404`: extensions `["html"]`, mapping ext `"action"` → returns `Action.NONE`, `MockHttpServletResponse.getStatus()` is 404, `invoke` not called.
  - `testRejectedUsesBadExtensionResult`: same, with config result `bad-extension` → returns `"bad-extension"`.
  - `testCustomResultName`: `setBadExtensionResultName("wrong-ext")` with that result present → returns `"wrong-ext"`.
  - `testChainedInvocationNotEnforced`: mapping name `"first"`, proxy action name `"second"`, disallowed ext → passes.
  - `testNoMappingPasses`: no `ActionMapping` in context → passes.
- [ ] **Step 2: Run** `-Dtest=ActionExtensionInterceptorTest`. Expected: compilation FAIL.
- [ ] **Step 3: Implement** `ResultResolution`, refactor `HttpMethodInterceptor`, and implement the interceptor and the `struts-default.xml` entries.
- [ ] **Step 4: Implement the warnings.**
  - `DefaultConfiguration.warnIfExtensionNotEnforced(ActionConfig fullConfig, List<InterceptorMapping> interceptors)`: WARN `Action [{}] in package [{}] declares extension {} but its interceptors do not include ActionExtensionInterceptor, the restriction will not be enforced` when the effective list is non-null and no interceptor is an `ActionExtensionInterceptor`.
  - `Dispatcher`: `private void warnIfExtensionsIgnoredByMapper(Container container)`, called after the configuration is loaded in `init()`. WARN once when `container.getInstance(ActionMapper.class).getClass() != DefaultActionMapper.class` and any runtime `ActionConfig` has non-null `getExtensions()`.
  - Tests: one test in `XmlConfigurationProviderExtensionTest` for the stack warning (an action in a package whose default stack lacks the interceptor), using the log-capture approach `XmlConfigurationProviderHttpMethodTest` uses for its warning if it has one. Otherwise assert via a Log4j2 `ListAppender`.
- [ ] **Step 5: Run** the new test, `HttpMethodInterceptorTest`, and `./mvnw test -DskipAssembly -pl core`. Expected: PASS. Every action in existing fixtures is undeclared, so the interceptor passes.
- [ ] **Step 6: Commit** `WW-4164 feat(core): reject disallowed extensions with ActionExtensionInterceptor`.

### Task 4: Inbound claiming in `DefaultActionMapper`

**Files:**
- Modify: `core/src/main/java/org/apache/struts2/dispatcher/mapper/DefaultActionMapper.java:279-295` (`getMapping`)
- Test: `core/src/test/java/org/apache/struts2/dispatcher/mapper/DefaultActionMapperTest.java` (append `testDeclared*` methods; load `struts-extension.xml` from Task 2 plus a `/` namespace action `index-less` setup as needed)

**Interfaces:**
- Consumes: `ActionConfig.getExtensions()`, `RuntimeConfiguration.getActionConfig(String, String)`, `ConfigurationManager.getConfiguration().getRuntimeConfiguration()`.
- Produces: `protected String stripDeclaredExtension(String uri, ActionMapping mapping, ConfigurationManager configManager)` returns the stripped URI with `mapping.setExtension(ext)` set, or null when no declared-only extension claims the URI.

Algorithm (not determined by the signature):

```
declaredOnly = cached set of every non-empty entry of any runtime ActionConfig.getExtensions(),
               minus the global `extensions` list; sorted longest first.
               Cache key: identity of the RuntimeConfiguration instance (volatile holder).
for ext in declaredOnly:
    if uri ends with "." + ext:
        candidate = uri without it
        probe = new ActionMapping(); parseNameAndNamespace(candidate, probe, configManager)
        name = probe.getName() up to the first '!' (DMI)
        config = runtime.getActionConfig(probe.getNamespace(), name)
        if config != null && config.getExtensions() != null:
            mapping.setExtension(ext); return candidate
return null
```

`getMapping` calls `stripDeclaredExtension` first and falls back to today's `dropExtension` only when it returns null. When `declaredOnly` is empty the method returns null immediately, which is what preserves today's behaviour. Only an action that *declares* extensions can claim a declared-only extension. The interceptor passes undeclared actions, so claiming for them would let `/home.html` execute an undeclared `home`.

- [ ] **Step 1: Write the failing tests** (global `action,,` unless stated). Each one builds a request with `setRequestURI` and calls `mapper.getMapping(req, configManager)`:
  - `testDeclaredExtensionClaimedForResolvingAction`: `/child/inherits.html` → name `inherits`, namespace `/child`, extension `html`.
  - `testDeclaredExtensionNotClaimedForStaticFile`: `/index.html` (no `index` action anywhere) → `null`, i.e. it falls through to the container.
  - `testDeclaredOnlyExtensionNotClaimedForUndeclaredAction`: `/plain/undeclared.html` → `null` (falls through, as today).
  - `testDeclaredActionClaimsOtherDeclaredExtension`: `/child/inherits.json` (`json` declared only by `own`) → claimed with extension `json`. The interceptor rejects it later.
  - `testGlobalExtensionUnchanged`: `/plain/undeclared.action` → extension `action`.
  - `testDmiNameWithDeclaredExtension`: DMI enabled, `/child/inherits!foo.html` → name `inherits`, method `foo`, extension `html`.
  - `testNamespaceFallbackWithDeclaredExtension`: an action in the `""` namespace with `extension="html"`; `/other/thing.html` behaves as `getActionConfig("/other","thing")` resolves.
  - `testWildcardWithDeclaredExtension`: `/wild/edit_user.html` → name `edit_user`, extension `html`.
  - `testGlobalNullStillStripsDeclared`: `setExtensions("")`; `/child/inherits.html` → name `inherits`, ext `html`; `/plain/foo.bar` → name `foo.bar` (literal, as today).
  - `testCacheFollowsReload`: reload configuration (`configurationManager.reload()` or re-`loadConfigurationProviders` with a fixture lacking `html`); `/child/inherits.html` is no longer claimed.
- [ ] **Step 2: Run** `-Dtest=DefaultActionMapperTest`. Expected: the new tests FAIL (`html` is not stripped / mapping null). Existing tests pass.
- [ ] **Step 3: Implement** `stripDeclaredExtension`, the cache holder, and the `getMapping` call order.
- [ ] **Step 4: Run** `DefaultActionMapperTest`, `CompositeActionMapperTest`, `Restful*ActionMapperTest`, then `./mvnw test -DskipAssembly -pl core,plugins/rest,plugins/convention`. Expected: PASS.
- [ ] **Step 5: Commit** `WW-4164 feat(core): map declared extensions to their actions`.

### Task 5: Outbound URL extension

**Files:**
- Create: `core/src/main/java/org/apache/struts2/dispatcher/mapper/ActionExtensionResolver.java`
- Modify: `core/src/main/resources/struts-beans.xml`: `<bean class="org.apache.struts2.dispatcher.mapper.ActionExtensionResolver" scope="singleton"/>` (same form as line 121)
- Modify: `core/src/main/java/org/apache/struts2/components/Component.java:~419-427` (`determineActionURL`) + `@Inject setActionExtensionResolver`
- Modify: `core/src/main/java/org/apache/struts2/components/ServletUrlRenderer.java:~178` (form URL; use the form component's resolver)
- Modify: `core/src/main/java/org/apache/struts2/result/ServletActionRedirectResult.java:~179`, `core/src/main/java/org/apache/struts2/result/PostbackResult.java:~155` + `@Inject setActionExtensionResolver` on each
- Test: `core/src/test/java/org/apache/struts2/dispatcher/mapper/ActionExtensionResolverTest.java` (new), plus additions to `views/jsp/URLTagTest.java`, `views/jsp/ui/FormTagTest.java`, `result/ServletActionRedirectResultTest.java`, `result/PostbackResultTest.java`

**Interfaces:**
- Consumes: `ActionExtensions.parse/select`, `ActionConfig.getExtensions()`.
- Produces:
  - `public class ActionExtensionResolver`, with `@Inject setConfiguration(Configuration)` and `@Inject(StrutsConstants.STRUTS_ACTION_EXTENSION) setExtensions(String)` (parsed via `ActionExtensions.parse`; empty → null).
  - `public void applyTo(ActionMapping target)`, which is a no-op when any of these holds:
    - `target.getExtension() != null`;
    - `getActionConfig(target.getNamespace(), name without "!…")` is null;
    - the effective list (declared, else global) is null.

    Otherwise it calls `target.setExtension(ActionExtensions.select(effective, currentExtension))`, where `currentExtension` comes from `ActionContext.getContext().getActionMapping()` (null when absent).
  - Each call site calls `actionExtensionResolver.applyTo(mapping)` immediately before `getUriFromActionMapping(mapping)`. `FormButton` is untouched.
  - Hand-built components/results in existing tests need `setActionExtensionResolver(container.inject(ActionExtensionResolver.class))` or `container.inject(result)`. No null guard in production code: fix tests that NPE by injecting.

- [ ] **Step 1: Write the failing tests**:
  - `ActionExtensionResolverTest` (load `struts-extension.xml`, global `action,,`):
    - `testDeclaredTargetUsesFirstWhenCurrentNotAllowed`: current `action`, target `/child`/`inherits` → `html`.
    - `testDeclaredTargetKeepsAllowedCurrent`: current `json`, target `/child`/`own` → `json`.
    - `testUndeclaredTargetFromDeclaredOnlyPage`: current `html`, target `/plain`/`undeclared` → `action` (Review Focus 5).
    - `testUndeclaredTargetKeepsGlobalCurrent`: current `""`, target `/plain`/`undeclared` → `""`.
    - `testExplicitExtensionUntouched`: target extension preset `""` → still `""`.
    - `testUnknownActionUntouched`: target `/nowhere`/`x` → extension stays null.
  - `URLTagTest.testActionUrlUsesDeclaredExtension`: `<s:url action="inherits" namespace="/child"/>` on a request mapped with extension `action` → `/child/inherits.html`.
  - `FormTagTest.testFormActionUsesDeclaredExtension`: form `action="inherits" namespace="/child"` → rendered action `/child/inherits.html` (the WW-3193 scenario).
  - `ServletActionRedirectResultTest.testRedirectUsesDeclaredExtension`: `actionName=inherits`, `namespace=/child` → `Location` ends `/child/inherits.html`.
  - `PostbackResultTest.testPostbackUsesDeclaredExtension`: same target → form action ends `/child/inherits.html`.
- [ ] **Step 2: Run** these tests. Expected: FAIL (class missing / URLs end `.action`).
- [ ] **Step 3: Implement** the resolver, the bean registration, the four call sites and the injection setters.
- [ ] **Step 4: Run** `./mvnw test -DskipAssembly -pl core`, then the full reactor `./mvnw -q package -DskipAssembly` (plugins render URLs). Fix any NPE from hand-built components by injecting in the test. Expected: PASS.
- [ ] **Step 5: Commit** `WW-4164 feat(core): build action URLs with the target action's extension`.

### Task 6: Anti-goal regression suite

**Files:**
- Create: `core/src/test/java/org/apache/struts2/dispatcher/ActionExtensionEndToEndTest.java` (`StrutsInternalTestCase`; drive through `Dispatcher.serviceAction` / `StrutsPrepareAndExecuteFilter` with a `MockFilterChain`, as the existing filter tests do)
- Create: `core/src/test/resources/org/apache/struts2/dispatcher/struts-extension-one-package.xml`: extends `struts-default`; one package `/shop` with `extension="html"` and action `cart`; one undeclared package `/` with action `home`

**Interfaces:**
- Consumes: everything above. Produces nothing.

- [ ] **Step 1: Write the tests** (global `action,,`):
  - `testStaticHtmlFallsThrough`: `/index.html` → filter chain invoked, Struts does not handle it.
  - `testDeclaredActionServedOnItsExtension`: `/shop/cart.html` → action executes.
  - `testDeclaredActionRejectedOnGlobalExtension`: `/shop/cart.action` → 404.
  - `testUndeclaredActionUnchanged`: `/home.action` and `/home` → action executes.
  - `testUndeclaredActionNotServedOnDeclaredOnlyExtension`: `/home.html` → filter chain invoked and action not executed. `home` declares nothing, so the mapper does not claim `html` for it (Task 4).
  - `testLinkFromHtmlPageToUndeclaredAction`: render `<s:url action="home" namespace="/"/>` while the current mapping extension is `html` → ends `/home.action`.
- [ ] **Step 2: Run** `-Dtest=ActionExtensionEndToEndTest`. Expected: PASS. If a test fails, the earlier task that owns the behaviour is wrong: fix it there.
- [ ] **Step 3: Verify the regression guard bites.** Temporarily make `stripDeclaredExtension` return null for every URI, using Edit, and confirm with `git diff --stat`. Run the test and see `testDeclaredActionServedOnItsExtension` FAIL. Restore with Edit and confirm `git diff --stat` is clean for that file.
- [ ] **Step 4: Run** the full reactor: `./mvnw -DskipAssembly test > /tmp/claude-ww4164-full.log 2>&1; echo $?`. Expected: `0`.
- [ ] **Step 5: Commit** `WW-4164 test(core): pin extension behaviour with one declaring package`.

### Task 7: Documentation hand-off

**Files:**
- No code in this repo beyond the Javadoc already written.
- struts-site (separate repo, separate PR): extend `core-developers/action-configuration.md` with an "Extensions" section next to "HTTP Methods", covering the attribute, the resolution order, the `bad-extension` result / 404, the not-enforced cases (chain, `<s:action>`, `action:` buttons, non-default mappers) and the "URL canonicalisation, not access control" note.

- [ ] **Step 1:** Draft the struts-site section text and include it in the struts PR body for Łukasz to review. Open the struts-site PR only when asked.
- [ ] **Step 2:** List the 7.5.0 Version Notes lines in the PR body:
  - the new attribute;
  - the `actionExtension` interceptor added to `basicStack`, `paramsPrepareParamsStack` and `defaultStack`;
  - hand-built components/results in user tests now need `ActionExtensionResolver` injected;
  - the Convention follow-up ticket.
