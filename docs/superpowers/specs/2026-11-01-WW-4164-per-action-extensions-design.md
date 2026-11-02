# WW-4164 Per-package / per-action action extensions — design

Ticket: [WW-4164](https://issues.apache.org/jira/browse/WW-4164) (fix version 7.5.0). Closes the
design question moved here from [WW-3193](https://issues.apache.org/jira/browse/WW-3193) (closed as
superseded on 2026-09-12).

## Intent

`struts.action.extension` is one global list (e.g. `action,html,,`). Every action answers on every
extension in it, and every generated URL inherits the current request's extension (or the global
default). Applications that mix extensions cannot say "this action lives at `.html`, that one has no
extension", so they get duplicate URLs for one page and forms / redirects pointing at the wrong
extension.

The goal is to let a package or an action declare **its own extension list**, and have that list
govern both:

1. **Inbound** — which request URLs map to the action (the primary concern), and
2. **Outbound** — which extension the framework uses when it builds a URL to the action
   (`s:url`, `s:a`, `s:form`, `s:submit action=`, `redirectAction`, `postback`). This is what
   resolves WW-3193.

## Goals

- `extension` attribute on `<package>` and `<action>` in `struts-7.5.dtd`, same format as
  `struts.action.extension`: comma-separated, order significant, first entry is the default, an
  empty entry (`""`, `a,,`, trailing comma) means "no extension".
- Resolution order for an action's effective list: action attribute → its package → parent
  packages (same walk as the existing `PackageConfig.getFull*` methods) → global
  `struts.action.extension`.
- A request whose extension is not in the resolved action's list is rejected through an
  interceptor, with a user-definable result, mirroring WW-3717's `http-method`.
- Generated URLs use the target action's list.

## Non-goals

- Convention plugin / annotation support (an `@Action`-level or class-level extension). Follow-up
  ticket, to be filed once this lands.
- `RestActionMapper`, `Restful*ActionMapper`, `PrefixBasedActionMapper`, `CompositeActionMapper`:
  no changes. They keep using the global list.
- Redirecting a wrong-extension request to the canonical URL.
- Resolving ambiguous names by trying several strip candidates (e.g. an action literally named
  `report.json` in a package whose list is empty-only). The mapper strips in one fixed order; the
  edge case is documented, not handled.

## Anti-goals

- Any behaviour change for an application that declares no `extension` attribute. Existing mapper,
  tag and result tests must pass untouched.
- Extension enforcement leaking into security posture claims. This is URL canonicalisation, not
  access control; docs must not present it as a way to hide actions.

## Constraints

- `struts-7.5.dtd` is unreleased — add the attributes there; no new DTD version.
- JUnit 3/4 only; match the style of each test file touched.
- Never `@Inject UnknownHandlerManager` into an interceptor (it breaks Convention config loading —
  see the WW-3717 fix `ced991c4f`). Look it up from the invocation context's container.
- Run the full reactor after each commit (plugins subclass the providers and the mapper).

## Decided design

### Configuration

- **DTD**: `extension CDATA #IMPLIED` on `<!ATTLIST package>` and `<!ATTLIST action>`.
- **Parsing** in `XmlDocConfigurationProvider`: distinguish *absent* from *empty*. DOM
  `getAttribute` returns `""` for an absent attribute, so use `hasAttribute`. `extension=""` is a
  real value meaning "no extension only". Parse with the same rules as
  `DefaultActionMapper.setExtensions` (split on `,`, trailing comma adds `""`); do **not** use
  `commaDelimitedStringToSet` — it loses order and the empty entry.
- **Entities**: `PackageConfig` holds its own list (null = not declared) and exposes a
  `getFull...` accessor that walks parents. `ActionConfig` holds its own list; the *effective*
  list (action → package chain) is resolved when the runtime configuration is built
  (`DefaultConfiguration.buildFullActionConfig`), so the interceptor and the outbound helper read
  one value. An action with nothing declared on itself or its packages keeps a null *declared*
  list; consumers treat null as "the global list". So an undeclared action answers only on the
  global extensions, as today. It never answers on an extension that exists only because some
  other package declared it. `ActionConfigMatcher` copies it onto wildcard-matched
  configs (as it does for `httpMethods`). Field private (Sonar S1948 precedent).
- **Startup warning**: like `warnIfHttpMethodNotEnforced`, warn when an action has an effective
  extension list but its interceptor stack lacks the new interceptor.

### Inbound

- **Mapper** (`DefaultActionMapper.getMapping` / `dropExtension`): strip using the union of the
  global list and every package/action list in the current `Configuration` (reachable through the
  `ConfigurationManager` argument `getMapping` already gets). The union must follow config
  reloads — cache it against the `Configuration` / `RuntimeConfiguration` instance, not the
  injected constant. With no extension attributes anywhere, the union equals the global list and
  `dropExtension` behaves exactly as today.
- **Claiming a request on a non-global extension.** Today `/index.html` under `action,,` makes
  `dropExtension` return null, and `StrutsPrepareAndExecuteFilter` (line ~149) passes the request
  to the container, which serves the static file or JSP. That must keep working once some package
  declares `html`. Rule: an extension found **only** in package/action lists, not in the global
  list, is stripped only if the stripped name and namespace resolve to an existing action
  (exact match or wildcard, via the runtime configuration) **that declares an extension list**
  itself or through its packages. An undeclared action never claims one, because the interceptor
  passes undeclared actions. Otherwise the mapper behaves as today
  for that URL: it returns null, or treats the name literally when the global list has `""`.
  Whether that action *allows* the extension is still the interceptor's decision. That way a
  rejection always goes through the user-definable result.
- **Global list null** (`struts.action.extension` empty, so nothing is stripped today): the rule
  above still applies to declared extensions, so a package declaring `html` gets `.html` stripped
  for its own actions only. Every other name is still taken literally. Needs its own test.
- **Interceptor** (new, e.g. `ActionExtensionInterceptor`, placed next to `httpMethod` in every
  stack in `struts-default.xml` that carries `httpMethod`): if the action's declared list is
  null, pass. That includes extensions produced by mappers other than `DefaultActionMapper`'s own
  stripping. Otherwise compare the request's `ActionMapping.getExtension()` with it.
  **A null mapping extension counts as `""`**, because `dropExtension` leaves the extension null
  when it matches through the empty entry.
- **Enforce only the URL's own action.** The extension describes the request URL, so the
  interceptor checks only the action the URL mapped to. It passes for chained invocations
  (`chain` result), `<s:action>`, and any invocation whose proxy name/namespace differs from the
  request's `ActionMapping`. `action:`-prefix buttons (off by default via
  `struts.mapper.action.prefix.enabled`) are enforced: `handleSpecialParameters` rewrites the
  mapping's name and the proxy is built from that mapping, so the interceptor checks the button's
  target action against the extension of the form's URL.
- **Other mappers.** `RestActionMapper` extends `DefaultActionMapper` but overrides the mapping
  path, and the Restful/Prefix/Composite mappers have their own logic. Declaring `extension`
  under them is the application's responsibility: a doc note, and a startup WARN when an action
  declares an extension list while the configured `ActionMapper` is not exactly
  `DefaultActionMapper`. No marker on `ActionMapping`.
- **Rejection**: return a configurable result name (placeholder `bad-extension`, property like
  `HttpMethodInterceptor.badRequestResultName`) when the action can resolve it (action result,
  package global result, `*`, or an unknown handler — same lookup as `HttpMethodInterceptor`).
  Otherwise send **404** and return `Action.NONE`. 404 (not 405) is my call: the URL does not
  exist for that action.

### Outbound

- Do not give `ActionMapper` implementations config access. Resolve in the callers, before
  `getUriFromActionMapping`, through one shared helper that:
  1. looks up the target `ActionConfig` (namespace + name) in the runtime configuration;
  2. takes the target's effective list (its declared list, or the global list when it declares
     none). It keeps the current request's extension when that list contains it, otherwise uses
     the list's first entry, and sets the result on the `ActionMapping`. For a declared target with
     a current request mapping, a null current extension counts as `""`. With no current request
     mapping, or for an undeclared target whose current extension is null, it uses the first entry,
     which reproduces today's `lookupExtension`. With no attributes anywhere, the current extension was stripped by the global
     list and is kept, which reproduces today's output;
  3. if the action is unknown, the global list is null and nothing is declared, or the caller
     already set an extension: change nothing (the mapper's `lookupExtension` fallback applies as
     today).
- Call sites: `ServletUrlRenderer` (form URL, ~line 179), `Component.determineActionURL`
  (~line 425), `ServletActionRedirectResult` (~line 179), `PostbackResult` (~line 155).
  `FormButton` sets `""` explicitly and keeps doing so.

### Docs

- struts-site: extension section in `core-developers/action-configuration.md` (and the package
  page), next to "HTTP Methods". 7.5.0 Version Notes: new attribute, new interceptor in default
  stacks, the null-equals-empty rule if anyone subclasses the interceptor.

## Left to the builder

- Interceptor / helper class names and package; where the cached union lives.
- Strip order of the union (must keep `""` handling last and reproduce today's order for the
  global list); longest-extension-first among the rest is a reasonable default.
- Exact validation of attribute values (e.g. reject a leading `.` with a `ConfigurationException`,
  as `buildHttpMethods` does for bad methods).
- Test layout, following WW-3717: an XML provider test with fixture files, an interceptor test,
  mapper tests for the union, and tag/result tests for outbound.

## Calls I made (not discussed)

- 404 + `NONE` as the fallback status.
- Rejection result name `bad-extension`.
- Outbound resolved in callers via a helper, not inside `ActionMapper`.
- Stacks receiving the interceptor = those that already carry `httpMethod`.
- Undeclared actions follow the global list only. A non-global extension is claimed only when the
  stripped name resolves to an action; otherwise the request falls through to the container as
  today (keeps static `.html`/JSP serving).
- Enforcement only on the URL's own action. Chain and `<s:action>` are not enforced; `action:`
  buttons are, against the form URL's extension.
- Non-default mappers: WARN + doc note, no API marker.

## Regression tests owed by the anti-goal

"No attributes means no change" is not enough. With **one** package declaring `html`:
- a static `/index.html` (no `index` action) still falls through to the container;
- an undeclared action still answers on `.action`/no extension and does not answer on `.html`;
- a link from a `.html` page to an undeclared action does not inherit `.html`. The outbound
  helper uses the target's effective (global-fallback) list, so a current extension outside it
  is replaced by its first entry.
