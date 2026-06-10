# Design: Harden commons-fileupload2 against milestone churn

**Date:** 2026-06-10
**Status:** Approved design — pending implementation plan
**Ticket:** [WW-5632](https://issues.apache.org/jira/browse/WW-5632)
**Origin:** [user@struts mailing list thread](https://lists.apache.org/thread/fcdls8xvd9tp9o6dcog65vkqozv4nq5x)
(Tamás Barta, Struts 7.1.1 file upload `NoSuchMethodError`)
**Related (closed):** [WW-5615](https://issues.apache.org/jira/browse/WW-5615) — "Adapt to renamed
methods in Apache Commons FileUpload 2.0.0-M5", fixed in 7.2.0 via PR #1584 / commit `d2810d42f`.

## Context

A user on Struts 7.1.1 reported `java.lang.NoSuchMethodError:
'void org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload.setSizeMax(long)'`
at upload time. Apache Commons FileUpload 2.0.0-M5 renamed several `AbstractFileUpload` methods
(`setSizeMax`→`setMaxSize`, `setFileSizeMax`→`setMaxFileSize`, `setFileCountMax`→`setMaxFileCount`),
breaking binary compatibility with M4. Struts declared M4 but the user's build resolved M5.

WW-5615 (PR #1584) fixed the **symptom** for 7.2.0: it renamed the three call sites in
`AbstractMultiPartRequest.java` and bumped `commons-fileupload2-jakarta-servlet6` M4 → M5 in
`parent/pom.xml`. That commit did **nothing else**.

This design addresses the **class of failure** that WW-5615 left open.

## Root-cause chain (verified on current `main`, 7.2.0-SNAPSHOT)

1. **Milestone dependency.** Struts depends on `-M` builds of commons-fileupload2, which break
   binary compatibility between milestones. Until a 2.0.0 GA exists, Struts is committed to
   milestone artifacts.
2. **The volatile API lives in an unmanaged artifact.** `setMaxSize(long)` / `setMaxFileCount(long)`
   / `setMaxFileSize(long)` are declared on `org.apache.commons.fileupload2.core.AbstractFileUpload`
   in **`commons-fileupload2-core`** (verified via `javap`). `JakartaServletDiskFileUpload` merely
   inherits them. `parent/pom.xml` `<dependencyManagement>` pins only
   `commons-fileupload2-jakarta-servlet6` — **`commons-fileupload2-core` is unmanaged.** A transitive
   dependency pulling a different `-core` milestone reproduces the exact `NoSuchMethodError` even
   when `-jakarta-servlet6` is pinned correctly.
3. **The build guardrail is dormant.** `maven-enforcer-plugin` is configured with a
   `dependencyConvergence` rule, but **only inside `<pluginManagement>`** of the root `pom.xml`; it is
   never bound to an active `<plugins>` section, so it never executes. Struts's own build would not
   catch a fileupload version skew.
4. **The BOM does not help consumers.** `struts2-bom` exports only Struts module versions, not the
   fileupload version. Downstream apps importing the BOM get no convergence assistance.

Net effect: a downstream/transitive dependency can select a mismatched `commons-fileupload2-core`
milestone, and because milestones break binary compatibility, the user gets a runtime
`NoSuchMethodError` deep in request handling, with no build-time warning.

## Goals

- Make a `commons-fileupload2-core` / `-jakarta-servlet6` version skew **impossible within Struts's
  own build**, deterministically.
- Fail the Struts build **early and clearly** if a future transitive dependency wants a
  commons-fileupload2 version other than the tested one.
- For downstream consumer runtimes (where Struts's build guards cannot reach), replace the opaque
  deep-stack `NoSuchMethodError` with a **clear, actionable `StrutsException`**.

## Non-goals

- **Shading/relocating commons-fileupload2.** Rejected: the library is security-sensitive (CVE
  history); shading would force Struts to re-release on every fileupload CVE, against Apache norms,
  and bloats the artifact.
- **Exporting the fileupload version through `struts2-bom`.** Considered and deferred — out of scope
  for this change.
- **Migrating off milestone versions.** Not actionable until a commons-fileupload2 2.0.0 GA exists.

## Design

### Part A — Build-time fail-fast (POM)

**A1. Manage both artifacts at one version.**
Introduce a single `commons-fileupload2.version` property (single source of truth) and add a
`<dependencyManagement>` entry for `org.apache.commons:commons-fileupload2-core` alongside the
existing `commons-fileupload2-jakarta-servlet6` entry in `parent/pom.xml`, both referencing the
property. Because `<dependencyManagement>` wins Maven version mediation, this forces a single,
matched `-core` version across the entire Struts reactor regardless of transitive requests —
closing root-cause #2 deterministically for Struts's own build.

**A2. Activate a narrowly-scoped enforcer (chosen over global `dependencyConvergence`).**
Bind `maven-enforcer-plugin` into an active `<plugins>` section with a `bannedDependencies` rule
scoped **only** to commons-fileupload2: ban all versions of
`org.apache.commons:commons-fileupload2-core` and
`org.apache.commons:commons-fileupload2-jakarta-servlet6` **except** the pinned
`${commons-fileupload2.version}`. This fails the build immediately if any transitive dependency
introduces a different fileupload version, with effectively zero blast radius on unrelated
dependencies.

> **Why not global `dependencyConvergence`?** It has never actually run; activating it may surface
> many pre-existing, unrelated version conflicts across the multi-module build, ballooning scope
> unpredictably. The fileupload-scoped `bannedDependencies` rule targets exactly the failure mode in
> this report. (Global convergence remains a reasonable separate cleanup task, out of scope here.)

The pinned version string lives once in the `commons-fileupload2.version` property and is referenced
by both the `<dependencyManagement>` entries and the enforcer rule — no duplicated literals.

### Part B — Runtime diagnostics guard

Add a one-time, package-private static check in `AbstractMultiPartRequest`, invoked on first use
(e.g. at the top of `prepareServletFileUpload`), guarded so the reflective probe runs **once** per
JVM — no per-request cost.

**Probe (testable, pure):**
`static void verifyFileUploadApi(Class<?> uploadClass)` reflectively confirms that `uploadClass`
declares (inherited included) `setMaxSize(long)`, `setMaxFileCount(long)`, and `setMaxFileSize(long)`.
If any is absent it throws `org.apache.struts2.StrutsException`.

**Self-maintaining message (no hardcoded "expected" version):** the exception reports the
implementation versions read at runtime from both packages —
`org.apache.commons.fileupload2.core.AbstractFileUpload.class.getPackage().getImplementationVersion()`
(the `-core` version) and `JakartaServletDiskFileUpload.class.getPackage().getImplementationVersion()`
(the `-jakarta-servlet6` version) — names the missing method, and instructs the user to align
`commons-fileupload2-core` with `commons-fileupload2-jakarta-servlet6`. Versions fall back to
`"unknown"` when no manifest implementation version is present. Surfacing the **skew** (core vs
jakarta versions) is the actionable signal; no version constant is baked into Struts to drift.

**One-time guard:** the caller wraps `verifyFileUploadApi(JakartaServletDiskFileUpload.class)` with a
JVM-once gate (`static volatile boolean` or a holder). The probe method itself is stateless so tests
can call it repeatedly.

## Testing & verification

**Part A:**
- `mvn validate -DskipAssembly` runs the enforcer clean on the current tree (no fileupload skew
  exists today).
- Manual negative check: temporarily declare a conflicting `commons-fileupload2-core` version and
  confirm the build fails with the banned-dependency message; revert.

**Part B (unit tests in `AbstractMultiPartRequestTest`):**
- `verifyFileUploadApi(JakartaServletDiskFileUpload.class)` does **not** throw (real classpath has the
  M5 API).
- `verifyFileUploadApi(<stub class lacking the setters>)` throws `StrutsException`; assert the message
  names the missing method and the remediation (align `-core` with `-jakarta-servlet6`).

Full suite: `mvn test -DskipAssembly -pl core`.

## Risks

- **Enforcer noise (mitigated).** Scoping `bannedDependencies` to commons-fileupload2 only avoids the
  unbounded scope risk of global `dependencyConvergence`.
- **Reflective probe drift.** If a future commons-fileupload2 release renames these setters again, the
  probe's hardcoded method names become a deliberate tripwire to update alongside the dependency bump
  — acceptable and intended.
- **Null implementation version.** Handled via `"unknown"` fallback so the guard never NPEs while
  building its diagnostic message.

## Out of scope / follow-ups

- Tracked under [WW-5632](https://issues.apache.org/jira/browse/WW-5632).
- Global `dependencyConvergence` cleanup across the reactor.
- Exporting third-party versions through `struts2-bom`.
- Revisiting the dependency once commons-fileupload2 2.0.0 GA ships.

## JIRA ticket

**Summary (title):**

```
Harden commons-fileupload2 dependency against milestone binary-incompatibility
```

**Description (JIRA wiki markup — paste into the description field):**

```
h3. Background

[WW-5615|https://issues.apache.org/jira/browse/WW-5615] fixed the {{NoSuchMethodError}}
caused by Apache Commons FileUpload 2.0.0-M5 renaming {{setSizeMax}} -> {{setMaxSize}}
(and friends), shipped in 7.2.0 via [#1584|https://github.com/apache/struts/pull/1584].
That fix addressed the *symptom* for one milestone bump but not the underlying *class of
failure*.

h3. Problem

Struts depends on _milestone_ ({{-M}}) builds of commons-fileupload2, which break binary
compatibility between milestones. Three gaps remain on {{main}}:

* The renamed setters ({{setMaxSize}}, {{setMaxFileCount}}, {{setMaxFileSize}}) live in
  *{{commons-fileupload2-core}}* ({{AbstractFileUpload}}), but only
  {{commons-fileupload2-jakarta-servlet6}} is pinned in {{dependencyManagement}} —
  {{-core}} is unmanaged, so a transitive dependency can pull a mismatched {{-core}}
  milestone and reproduce the {{NoSuchMethodError}}.
* The {{maven-enforcer-plugin}} {{dependencyConvergence}} rule sits only in
  {{<pluginManagement>}} and is never bound to an active {{<plugins>}} section, so it
  never runs — the build cannot catch a fileupload version skew.
* Downstream consumer runtimes get an opaque, deep-stack {{NoSuchMethodError}} with no
  actionable guidance.

h3. Proposed changes

* *(A1)* Introduce a single {{commons-fileupload2.version}} property and manage *both*
  {{commons-fileupload2-core}} and {{commons-fileupload2-jakarta-servlet6}} at that version
  in {{parent/pom.xml}}, forcing a matched {{-core}} version across the reactor.
* *(A2)* Activate {{maven-enforcer-plugin}} with a fileupload-scoped {{bannedDependencies}}
  rule that fails the build on any commons-fileupload2 version other than the pinned one
  (narrow scope, near-zero blast radius).
* *(B)* Add a once-per-JVM reflective guard in {{AbstractMultiPartRequest}} that throws a
  clear {{StrutsException}} reporting the {{-core}} vs {{-jakarta-servlet6}} version skew
  instead of an opaque {{NoSuchMethodError}}.

Full design: {{docs/superpowers/specs/2026-06-10-fileupload2-milestone-hardening-design.md}}

h3. Affects / Fix version

* Affects: 7.1.1+ (root cause present on 7.2.0-SNAPSHOT {{main}})
* Component: File Upload
```

