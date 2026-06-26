<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
# Apache Struts — Threat Model (v0 draft)

## §1 Header

- **Project:** Apache Struts (`apache/struts`), `main` @ HEAD (2026-06). Scope: the
  Struts framework in `apache/struts` only (the core MVC framework, its
  interceptors, tags, and the plugins shipped in this repo).
- **Date:** 2026-06-24. **Drafted for PMC review** via the threat-model-producer
  rubric (Scovetta). This is an unratified proposal, not an ASF Security team or
  PMC position; authorship and sponsorship are settled only once the PMC adopts it
  (see Status below and §14).
- **Status:** DRAFT — not yet reviewed by the Struts PMC. Built as a strict
  superset of the existing [`SECURITY.md`](SECURITY.md) and the published
  [Struts security guidance](https://struts.apache.org/security/); every
  load-bearing claim is tagged for provenance (see §14 for open questions).
- **Version binding:** versioned with the project; a report against version *N*
  is triaged against the model as it stood at *N*. The security envelope changed
  materially at **7.0** (several hardening knobs flipped to secure-by-default —
  §5a), so the version is itself load-bearing.
- **Reporting cross-reference:** §8-property violations → report privately per
  [`SECURITY.md`](SECURITY.md) (`security@struts.apache.org`); §3/§9/§11a findings
  are closed citing this document and the existing `SECURITY.md` "Before
  Reporting" checks.
- **Provenance legend:** *(documented)* = Struts' own docs/`SECURITY.md`/security
  site; *(maintainer)* = confirmed by a Struts PMC member through this process;
  *(inferred)* = reasoned from architecture/docs, not yet PMC-ratified — each has
  a matching §14 open question.
- **Draft confidence:** the bulk is *(documented)* — Struts has an unusually rich
  published security policy — with a handful of *(inferred)* scoping calls for the
  PMC to ratify.

**What Struts is.** Apache Struts 2 is a **Java MVC web framework** for building
server-side web applications. A request flows: servlet filter → action mapping →
**interceptor stack** (parameter population, validation, etc.) → **Action** →
**result** (typically a JSP/FreeMarker view). Request parameters are bound onto
action properties via setters, and view/configuration expressions are evaluated
through **OGNL (Object-Graph Navigation Language)** against the **ValueStack**.
*(documented — struts.apache.org)*

**The framework's own security philosophy (load-bearing).** Struts
**"doesn't provide any security mechanism — it is just a pure web framework."**
*(documented — [security guidance](https://struts.apache.org/security/))* It is
not an authentication, authorization, session-security, or input-sanitisation
layer; those are the embedding application's responsibility (§3/§10). What Struts
*does* take an active stance on is **not letting its own machinery — chiefly OGNL
expression evaluation and request-parameter binding — become an injection vector**.
That single sentence shapes the whole model: most "Struts is insecure" reports are
either OGNL-injection-class (in model, §8) or application-responsibility (out of
model, §3/§11a).

## §2 Scope and intended use

Intended deployment: the Struts JARs are a **dependency embedded inside a web
application** (a WAR) that the application developer writes, configures, and
deploys into a servlet container (Tomcat, Jetty, …) behind the operator's
perimeter. Struts is **in-process** with the application; it has no daemon, no
listening socket of its own, and no trust boundary against the application code
it runs inside. *(documented — it is a framework, not a server.)*

**Caller roles.**

- **Untrusted HTTP client** — sends requests (parameters, headers, cookies,
  multipart uploads) to a Struts-backed endpoint. **The primary untrusted boundary.**
  Struts must treat all request-derived values as hostile. *(documented — the
  parameter/OGNL hardening exists precisely for this actor.)*
- **Application developer** — writes the actions, JSPs, struts.xml/annotations,
  and chooses the hardening settings (§5a). **Trusted by the framework** — their
  code and configuration run with the application's privileges. A finding that
  requires the developer to write unsafe code or disable a default protection is
  the application's bug, not Struts' (§3). *(documented — the developer-responsibility
  section of the security guidance.)*
- **Operator** — deploys the WAR, sets `devMode` off, restricts dev-only plugins,
  configures the container and JVM. **Trusted.** *(documented.)*

**Component families.**

| Family | Entry point | Touches | In model? |
| --- | --- | --- | --- |
| OGNL evaluation + ValueStack | expression eval for params, tags, results | in-JVM code paths | **In — the central attack surface** *(documented)* |
| Parameter binding (`ParametersInterceptor`, `@StrutsParameter`) | request params → action setters | reflection into app objects | **In — primary boundary** *(documented)* |
| Interceptor stack (cookie, fileupload, fetch-metadata, COOP/COEP, …) | per-request processing | request data | **In** *(documented)* |
| Tag library / JSP & FreeMarker integration | view rendering, expression output | template eval | **In — output-side OGNL/EL** *(documented)* |
| File upload (Jakarta multipart) | multipart request parsing | temp files | **In — historical CVE surface** *(documented — S2 bulletins)* |
| Bundled plugins (REST, JSON, Convention, …) in this repo | extra mappers/result types | request data | **In — same request-trust surface** *(inferred — §14 Q-plugins)* |
| Config Browser Plugin | exposes internal config | dev-only diagnostic | **In as dev-only** — exposure in prod is operator misconfig (§3/§11a) *(documented)* |
| Embedding application's own actions/JSPs/config | the developer's code | as the app | **Out — application responsibility (§3)** *(documented)* |
| Examples / showcase / test apps | demo code | n/a | **Out** *(see §3)* |

## §3 Out of scope (explicit non-goals)

The detailed lists of developer anti-patterns and insecure configurations are
maintained in the project's own docs and are **not duplicated here** — this model
links to them and assigns each a triage disposition (§13):

- **Anything the application developer is responsible for.** Struts provides no
  security mechanism of its own *(documented)*. The full enumeration —
  developer-exposed unsafe setters, request parameters used in localization or
  forced OGNL evaluation, raw `${...}` JSP-EL over untrusted values, direct JSP
  access, mixing security levels in one namespace — is in the
  [security guidance](https://struts.apache.org/security/) and
  [`SECURITY.md`](SECURITY.md). All are `OUT-OF-MODEL: application-responsibility`.
- **Findings that only manifest with a documented-insecure / non-default setting**
  (`devMode=true`, Config Browser Plugin exposed in production, DMI enabled, or a
  §5a hardening knob turned off) → `OUT-OF-MODEL: non-default-config`. *(documented.)*
- **The servlet container, JVM, JDK, and OS**, and the application's own
  authentication, authorization, session management, CSRF token storage, and
  transport (TLS). Struts is "a pure web framework," not a security framework.
  *(documented / inferred — §14 Q-env.)*
- **Generic denial of service.** Per [`SECURITY.md`](SECURITY.md), generic flooding
  or large-body streaming is not accepted; only *super-linear* amplification inside
  framework code may be in model (§8 / §14 Q-dos). *(documented.)*
- **Already-disclosed S2-series vulnerabilities** — a duplicate of an existing
  Security Bulletin/CVE is closed by reference (the
  [`SECURITY.md` "Before Reporting"](SECURITY.md) checks), not re-triaged.
- **Examples, showcase, and test applications** shipped in the repo. *(inferred — §14 Q-scope.)*

## §4 Trust boundaries and data flow

```
Untrusted HTTP request
  │  params, headers, cookies, multipart
  ▼
Servlet filter ─► action mapping ─► Interceptor stack ─► Action ─► Result (JSP/FreeMarker)
                                        │                              │
                            ParametersInterceptor              tag/result OGNL eval
                            binds params to setters            against ValueStack
                                        │                              │
                                        ▼                              ▼
                              OGNL evaluation against the ValueStack  ◄── the trust boundary
                              (allowlist / excluded classes+packages /
                               expression length / @StrutsParameter)
```

- **HTTP client → framework** is the one boundary Struts owns. Every request-derived
  string (parameter *names* as well as *values*, cookie names/values, header values,
  multipart filenames) is untrusted and may carry an OGNL payload. The framework's
  job at this boundary is to bind parameters and evaluate expressions **without
  letting attacker input reach an OGNL evaluation that creates or changes executable
  code**. *(documented.)*
- **Framework → application code** is *not* a trust boundary — Struts runs the
  developer's actions and templates in-process, fully trusted. *(documented.)*

**Reachability precondition (triager's test).** A finding is in-model only if it is
reachable by an **untrusted HTTP client against a Struts application that follows the
documented secure configuration** (current-version defaults, `devMode` off, dev-only
plugins restricted, no developer anti-patterns from §3). A finding that needs
`devMode`, a disabled default protection, a developer-introduced unsafe setter, or a
documented anti-pattern is `OUT-OF-MODEL`. *(documented/inferred — §14 Q-default.)*

## §5 Assumptions about the environment

- A servlet container and a JVM the operator maintains; Struts does not patch or
  harden them. *(inferred — §14 Q-env.)*
- The application is deployed with the **current supported version** (7.x or 6.x per
  `SECURITY.md`); 2.x is end-of-life and out of support. *(documented — Supported
  Versions table.)*
- The operator runs production with `devMode=false` and dev-only diagnostics (Config
  Browser Plugin) disabled or access-controlled. *(documented.)*
- Struts opens no sockets and makes no outbound connections of its own; any network
  egress is the application's. *(inferred — §14 Q-egress.)*

## §5a Build-time and configuration variants — **the central knob set**

Struts' security envelope is set almost entirely by **runtime configuration**. The
**authoritative, current list of every hardening setting (purpose + secure default)
lives in the [security guidance](https://struts.apache.org/security/) and is not
reproduced here.** Only the triage-load-bearing facts:

- The security posture **changed materially at 7.0**, where a cluster of
  OGNL-injection and parameter-binding defences became **secure-by-default** —
  notably the OGNL allowlist (`struts.allowlist.enable`), the `@StrutsParameter`
  annotation requirement (`struts.parameters.requireAnnotations`), excluded
  classes/packages, the expression-length cap (`struts.ognl.expressionMaxLength`,
  default 256), and the static-field/proxy/default-package/custom-map disallows.
- `struts.devMode` (must be `false` in production) and Dynamic Method Invocation
  (gated by Strict Method Invocation since 2.5) are the two settings whose *insecure*
  value most often turns a non-finding into an apparent finding.
- The **FetchMetadata / COOP / COEP** interceptors (6.0+) are opt-in cross-origin
  defences (§8.5).

**Insecure-default question (wave 1).** Because the secure posture is the **7.0
default set**, the triage rule needs ratifying: is "a finding that only works with a
pre-7.0 default, or with a 7.0 hardening knob turned off" `OUT-OF-MODEL:
non-default-config`, with §10 carrying "deploy current version with defaults"? — §14
Q-default. The OGNL **Java Security Manager sandbox** (`-Dognl.security.manager`) is a
separate, opt-in defence built on the JDK `SecurityManager`, which has been
**deprecated for removal since JDK 17 (JEP 411), disabled by default since JDK 18,
and permanently disabled in JDK 24 (JEP 486)** *(documented — JDK release notes)* —
so on modern JDKs the model cannot treat it as a relied-upon control (§14 Q-jsm).

## §6 Assumptions about inputs

| Surface | Input | Attacker-controllable? | Concern |
| --- | --- | --- | --- |
| Parameter binding | request parameter **names and values** | **yes** | OGNL injection via crafted names; binding to unsafe setters |
| Cookies | cookie names/values (Cookie Interceptor) | **yes** | same OGNL/parameter concerns; checked by accepted/excluded patterns |
| Headers | request headers | **yes** | header-driven expression/log paths |
| Multipart upload | file content, filename, content-type | **yes** | parser robustness, temp-file handling (S2 history) |
| Expression context | values that reach an OGNL eval (tags, results, forced eval) | **yes if developer feeds untrusted input in** | the core RCE channel |
| struts.xml / annotations / action code | framework + app configuration | **no — developer-trusted** | not an attacker surface (§3) |

The accepted/excluded pattern checkers (`AcceptedPatternsChecker` /
`ExcludedPatternsChecker`, since 2.3.20) validate parameter names/values for the
Parameters and Cookie interceptors; a custom override that drops below the framework
defaults is a developer error, not a framework flaw. *(documented.)*

## §7 Adversary model

- **In scope:** an **untrusted remote HTTP client** with no credentials, able to send
  arbitrary parameters, headers, cookies, and multipart uploads to any
  Struts-handled endpoint. Capabilities: craft parameter names/values carrying OGNL,
  attempt to reach executable-code creation through the ValueStack, pollute
  parameter binding, exploit a file-upload or multipart parsing bug, or trigger a
  super-linear resource path in framework code. Goal: **remote code execution via
  OGNL** (the dominant Struts threat), and secondarily data disclosure, SSRF through
  framework features, or DoS amplification. *(documented — the OGNL lineage is the
  framework's stated central concern.)*
- **On-path network attacker** — only where the application/operator has not deployed
  TLS; transport security is the app's, so this is largely out of model (§3). *(inferred — §14 Q-env.)*
- **Out of scope:** the application developer (writes trusted code/config); the
  operator (deploys, sets devMode/plugins); anyone with container/host/JVM control;
  and a developer who disables a default protection or follows a documented
  anti-pattern (§3). *(documented.)*

## §8 Security properties the framework provides

*(In the current-version, default-hardening posture; each lists violation symptom +
severity. Most are documented controls — the OGNL-injection defences are the core of
Struts' security work.)*

1. **OGNL injection containment.** Attacker-supplied request data (parameter names/
   values, cookies, headers) must not reach an OGNL evaluation that creates or alters
   executable code. Enforced in depth by the default controls listed in §5a / the
   [security guidance](https://struts.apache.org/security/) (allowlist, excluded
   classes/packages, expression-length cap, static-field/proxy/default-package/
   custom-map disallows, excluded node types). *Violation:* a crafted request
   achieving OGNL-driven code execution (or class-loader/member access beyond the
   allowlist) on a default-configured current-version app. *Severity:*
   security-critical (the S2-RCE class). *(documented.)*
2. **Parameter-binding safety (7.0).** Request parameters bind only to setters the
   developer marked `@StrutsParameter` (to the declared depth); arbitrary deep/nested
   property traversal is not reachable by default. *Violation:* parameters reaching
   an unannotated setter, or nesting beyond the declared depth, on a default 7.0 app.
   *Severity:* critical. *(documented.)*
3. **Method-invocation control.** Dynamic Method Invocation is gated by Strict Method
   Invocation; a client cannot invoke arbitrary action methods by name when DMI is at
   its recommended (off/strict) setting. *Violation:* arbitrary method invocation on a
   default app. *Severity:* high–critical. *(documented.)*
4. **Expression-length and node-type bounds.** OGNL expressions over the configured
   length (default 256) and forbidden node types are rejected before evaluation.
   *Violation:* bypass of these bounds. *Severity:* high. *(documented.)*
5. **Cross-origin / fetch-metadata defences (opt-in).** When the FetchMetadata, COOP,
   and COEP interceptors are enabled, the framework emits/enforces the corresponding
   `Sec-Fetch-*` and cross-origin isolation behaviour. *Violation:* the interceptor
   failing to enforce its documented behaviour when enabled. *Severity:* medium–high.
   *(documented — opt-in since 6.0.)*

## §9 Security properties the framework does *not* provide

- **No security mechanism in the general sense.** Struts provides no authentication,
  authorization, session security, CSRF token store, input sanitisation, or output
  encoding *for the application's own data* — "it is just a pure web framework."
  *(documented.)*
  - *False friend:* "Struts has no built-in login/access control" is **by design**,
    not a vulnerability.
- **No protection against developer anti-patterns or non-default config** — unsafe
  setters, raw `${}` on user input, request params in localization/forced eval,
  direct JSP access, `devMode` on, disabled hardening (§3/§5a).
- **No defence once OGNL evaluation is fed untrusted input by the application
  itself** (forced expression evaluation on a request value) — that is the developer
  handing OGNL the attacker's string. *(documented.)*
- **No hard anti-DoS guarantee** beyond the "avoid super-linear in input size"
  philosophy; generic flooding/streaming DoS is the operator's to absorb. *(documented.)*
- **The OGNL Java Security Manager sandbox is not a relied-upon control on modern
  JDKs** (the underlying `SecurityManager` is deprecated for removal since JDK 17 and
  permanently disabled in JDK 24; see §5a). *(documented.)*
- **Auto-generated error pages do not escape action names** (historical S2-006) — the
  app must define custom error pages; XSS in the default error page is a documented
  hardening item, not a defended property. *(documented.)*
- **Well-known classes (framework):** OGNL/expression injection, multipart/file-upload
  parsing bugs, and parameter-pollution are the framework's recurring risk classes;
  reflected XSS, CSRF token management, and transport security are the application's.

## §10 Downstream (developer + operator) responsibilities

The full, authoritative how-to is the [security guidance](https://struts.apache.org/security/)
and [`SECURITY.md`](SECURITY.md); in one line: **deploy a current supported version
with the default hardening left on, `devMode` off, dev-only plugins restricted,
parameter setters annotated, JSPs hidden behind actions, and the application's own
authn/authz/CSRF/TLS supplied** (Struts provides none of those). The threat-model
value is only that a finding requiring the developer to *violate* one of these is
`OUT-OF-MODEL` (§3/§13), not that this list is novel.

## §11 Known misuse patterns

These are the §3 application-responsibility / non-default-config items viewed as
"things integrators get wrong" — running `devMode=true` in production or exposing the
Config Browser Plugin; disabling a default OGNL/binding protection "to make something
work"; exposing unsafe setters to binding; feeding request parameters into forced
OGNL evaluation or localization; allowing direct `*.jsp` access or raw `${}` EL on
untrusted values; relying on the OGNL Java Security Manager sandbox on modern JDKs. Each
is documented in the [security guidance](https://struts.apache.org/security/); the
disposition mapping is §11a/§13.

## §11a Known non-findings (recurring false positives)

*(Seeded directly from `SECURITY.md` "Before Reporting" — the PMC owns the
authoritative list; §14 Q12.)*

- **"OGNL/RCE that only works with `devMode=true`."** `OUT-OF-MODEL: non-default-config`
  — devMode is a development-only setting documented as unsafe for production.
- **"An action setter lets me inject a value / reach a dangerous method."** When the
  setter is developer-exposed without `@StrutsParameter` (7.0), or performs an unsafe
  side effect, this is `OUT-OF-MODEL: application-responsibility`. In-model only if it
  bypasses the framework's *default* binding/OGNL protections.
- **"Direct JSP access discloses X / executes Y."** App-deployment misconfiguration —
  JSPs must be hidden behind actions. `OUT-OF-MODEL: application-responsibility`.
- **"Raw `${}` EL / forced OGNL eval on my request parameter is exploitable."** The
  application fed untrusted input to expression evaluation — documented anti-pattern,
  not a framework flaw.
- **"Config Browser Plugin exposes internal configuration."** Dev-only diagnostic;
  exposing it in production is operator misconfiguration. `OUT-OF-MODEL: non-default-config`.
- **"I can enumerate / pass arbitrary parameters."** Parameter binding is the point of
  the framework; in-model only when it crosses the default annotation/allowlist
  protections.
- **"Generic DoS: I streamed a huge body / hammered a URL."** Not accepted per
  `SECURITY.md`; only super-linear amplification inside framework code is considered.
- **Duplicate of a disclosed S2-series bulletin/CVE** — closed by reference.
- **Dependency-tail CVEs** (a transitive jar, e.g. a logging or XML library) from an
  SCA scan — triage upstream unless Struts' own code reaches the vulnerable path with
  untrusted input.

## §12 Conditions that would change this model

- A change to the default-hardening set (e.g. a new secure-by-default knob, or a
  default flipped) — re-baseline §5a/§8/§11a.
- A new request-facing surface, a new bundled plugin, or a new expression/templating
  integration with its own trust surface.
- A change to how OGNL evaluation, the allowlist, or parameter binding works.
- A report that cannot be routed to a §13 disposition → revise §8/§9.

## §13 Triage dispositions

| Disposition | Meaning | Licensed by |
| --- | --- | --- |
| `VALID` | A §8 property breaks via an untrusted HTTP client on a current-version, default-hardened app. | §8, §6, §7 |
| `VALID-HARDENING` | A §11 misuse is too easy, or a default could be tightened. | §11/§5a |
| `OUT-OF-MODEL: application-responsibility` | Requires a developer anti-pattern (unsafe setter, raw EL, forced eval, direct JSP) or the app's own authn/authz. | §3/§10 |
| `OUT-OF-MODEL: non-default-config` | Only manifests with `devMode`, a dev-only plugin, DMI, or a disabled default protection. | §5a |
| `OUT-OF-MODEL: adversary-not-in-scope` | Requires container/host/JVM/developer control. | §7 |
| `OUT-OF-MODEL: unsupported-version` | Only affects an end-of-life (2.x) version. | §5 |
| `BY-DESIGN: property-disclaimed` | Concerns a property §9 disclaims (no built-in authn/authz/encoding; generic DoS; JSM on JDK21+). | §9 |
| `KNOWN-NON-FINDING` | Matches §11a. | §11a |
| `DUPLICATE` | Matches a disclosed S2-series bulletin/CVE. | §3 |
| `MODEL-GAP` | Unroutable. | triggers §12 |

## §14 Open questions for the maintainers

**Wave 1 — scope, defaults, intended use**

- **Q-default.** Confirm the triage baseline is "current supported version (7.x/6.x)
  with the documented default hardening on, `devMode` off, dev-only plugins
  restricted" — and that a finding requiring a pre-7.0 default or a disabled hardening
  knob is `OUT-OF-MODEL: non-default-config`. (§5a/§13.)
- **Q-scope.** Confirm the in-scope surface is the framework in `apache/struts`
  (core + interceptors + tags + bundled plugins), with the embedding application's own
  actions/JSPs/config, and examples/showcase, out of scope. (§2/§3.)
- **Q-philosophy.** Confirm the framing that Struts provides **no security mechanism
  of its own** beyond OGNL/parameter-binding injection containment — i.e. authn,
  authz, session security, CSRF token storage, output encoding, and transport are the
  application's. (§9.)
- **Q-env.** Confirm the servlet container, JVM, JDK, and OS are out of scope — Struts
  does not patch or harden them, and the operator maintains them. (§3/§5.)
- **Q-egress.** Confirm Struts opens no sockets and makes no outbound connections of
  its own, so any network egress (and the SSRF surface it implies) is the
  application's. (§5/§7.)

**Wave 2 — mechanism confirmations**

- **Q-ognl.** Confirm the §8.1 list is the authoritative set of default OGNL-injection
  defences (allowlist, excluded classes/packages/patterns, expression length,
  static-field/proxy/default-package/custom-map disallows, excluded node types) and
  that a bypass of any on a default app is `VALID`. (§8.)
- **Q-jsm.** Confirm the OGNL Java Security Manager sandbox is **not** a relied-upon
  control (opt-in, and non-functional on modern JDKs — see §5a), so a report premised
  on its absence is not a finding. (§5a/§9.)
- **Q-dos.** Where is the line between "generic DoS we don't accept" and "super-linear
  amplification inside framework code we do"? Confirm the §3/§8 wording. (§3.)

**Wave 3 — surfaces & false-friends**

- **Q-plugins.** Which bundled plugins (REST, JSON, Convention, …) are in scope at the
  same request-trust level, and are any (e.g. REST/XML) historically higher-risk and
  worth their own §8 note? (§2.)
- **Q-upload.** Confirm the multipart/file-upload surface (Jakarta) and what the
  framework guarantees vs. leaves to the container/app. (§2/§6.)
- **Q12.** Beyond the `SECURITY.md` "Before Reporting" list already folded into §11a,
  what do scanners/researchers most often report against Struts that you consider a
  non-finding? (Feeds §11a.)

## §15 Appendix — existing-policy back-map

This `THREAT_MODEL.md` is **additive** — it does not replace
[`SECURITY.md`](SECURITY.md) (reporting process, supported versions, "Before
Reporting" checks) or the published [security guidance](https://struts.apache.org/security/);
both are preserved and remain canonical for the reporting workflow. The discoverability
chain is `AGENTS.md` → `SECURITY.md` → this model. Mapping of existing-policy claims to
sections:

| Existing-policy statement | Threat-model § |
| --- | --- |
| "Struts doesn't provide any security mechanism — pure web framework" | §1, §9, §13 (`BY-DESIGN`) |
| OGNL is the central historical vuln class | §1, §7, §8.1 |
| devMode / Config Browser Plugin are dev-only | §3, §5a, §11a |
| `@StrutsParameter` / unsafe setters | §6, §8.2, §10, §11a |
| Direct JSP access / raw `${}` EL / forced eval / localization | §3, §10, §11a |
| Allowlist / excluded classes/packages / expression length (7.0 defaults) | §5a, §8.1 |
| DMI / Strict Method Invocation | §5a, §8.3 |
| FetchMetadata / COOP / COEP | §5a, §8.5 |
| OGNL JSM sandbox (modern-JDK limitation) | §5a, §9 |
| Generic DoS not accepted; non-linear-in-input philosophy | §3, §8, §9 |
| "Before Reporting" duplicate/known-config checks | §3, §11a, §13 (`DUPLICATE`) |
| Supported versions (2.x EOL) | §5, §13 (`OUT-OF-MODEL: unsupported-version`) |
