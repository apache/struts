---
name: code-review
description: Apache Struts pull request review guide. Use when reviewing pull requests in this repository to check test conventions, security-sensitive framework code, PR and commit hygiene, and Struts-specific implementation patterns.
license: Apache-2.0
---

# Reviewing Apache Struts pull requests

Apache Struts is a mature MVC framework for Java. It uses OGNL for value stack
expressions and FreeMarker for UI tag templates, and it has a long history of
security vulnerabilities in exactly those areas. Review accordingly: the
conventions below are not style preferences, they are the places where changes
tend to go wrong in this codebase.

Deeper references, when a review needs them:

- [`CLAUDE.md`](../../../CLAUDE.md) — build commands, module layout, request lifecycle
- [`SECURITY.md`](../../../SECURITY.md) — the vulnerability reporting process (source of truth)
- [`THREAT_MODEL.md`](../../../THREAT_MODEL.md) — scope, trust boundaries, known non-findings
- [`AGENTS.md`](../../../AGENTS.md) — rules for AI agents working on security findings

## 1. Tests

**This repository is JUnit 4. There is no JUnit 5 anywhere in it.**
`parent/pom.xml` declares `junit:junit:4.13.2`; there are zero
`org.junit.jupiter` imports. Two styles coexist and a new test must match the
style already in the file it joins:

- **JUnit 3 style** — classes extending `XWorkTestCase`, which extends
  `junit.framework.TestCase`. Test methods must be named `testXxx()`. A Jupiter
  `@Test` annotation added to one of these **silently never runs** — it does not
  fail, it is simply not collected. Flag this as blocking whenever you see
  `org.junit.jupiter` in a diff.
- **JUnit 4 style** — classes using `import org.junit.Test`.

Both styles are widespread and neither is being migrated away from.

AssertJ assertions and Mockito mocks are both available and widely used.
Introducing JUnit 5 is a build-infrastructure change that needs its own `WW-`
ticket; it is never a side effect of a feature PR.

### Tests that pass without testing anything

Three traps in this codebase produce green tests that assert nothing. Check for
them whenever a PR adds a test in these areas:

- **Unpushed action.** An action object that was never pushed onto the value
  stack binds no parameters at all, so an assertion that "the parameter was not
  bound" passes for the wrong reason. Confirm the fixture pushes the action.
- **`requireAnnotations` is off by default in a bare harness.**
  `ParametersInterceptor.requireAnnotations` is a Java field initialised to
  `false`; production turns it on through
  `struts.parameters.requireAnnotations=true` in `default.properties`. A test
  that constructs the interceptor directly and expects an unannotated parameter
  to be rejected will pass with the check disabled. The test must set the flag.
- **Interned string literals defeat identity checks.** Where the framework
  compares against a marker constant with `==`, a String *literal* in a test is
  interned to the same instance as the constant, so the test passes vacuously
  even against unfixed code. Such fixtures must build the value at runtime, with
  an `assertNotSame` guard proving they did.

Run tests with `mvn test -DskipAssembly`; a single test with
`mvn test -DskipAssembly -pl core -Dtest=MyClassTest#testMethodName`.

## 2. Security-sensitive changes

Watch for diffs touching OGNL expression evaluation, the OGNL allowlist and
member access policies, parameter filtering and `@StrutsParameter` gating,
file upload handling, action mapping and name cleanup, deserialization, path
handling, or escaping in framework components and UI tag templates.

**When a change looks like it fixes a vulnerability rather than an ordinary
bug, say so in neutral terms and stop there.** A pull request is public, so a
review comment that names the weakness, explains how it is reached, or
estimates its impact is itself a disclosure — precisely what the project's
private process exists to prevent.

Use wording of this shape, and no more than this:

> This change touches security-sensitive framework code. Please confirm it is
> not a fix for a suspected vulnerability before merging — see `SECURITY.md`.
> Vulnerability fixes go through the private process at
> `security@struts.apache.org`, not a public pull request.

Do **not**, in a review comment: describe the suspected weakness or its class,
sketch an exploit or a triggering input, assess exploitability or severity,
speculate about affected versions, or link the change to a specific CVE or
security bulletin. If a reviewer needs to raise any of that, it belongs in
private mail to `security@struts.apache.org`.

## 3. Pull request and commit hygiene

- **Title** — `WW-XXXX Description`. A Jira ticket ID is required for any code
  change; the tracker is <https://issues.apache.org/jira/projects/WW>.
- **Description** — links the ticket:
  `Fixes [WW-XXXX](https://issues.apache.org/jira/browse/WW-XXXX)`.
- **Exception** — pure documentation and build/CI changes (`SECURITY.md`,
  `AGENTS.md`, `CLAUDE.md`, `.github/`, workflows) take no ticket and use
  conventional-commit form instead: `docs: ...`, `build(ci): ...`, `chore: ...`.
- Commit messages follow the same rule as the title.

Flag a missing or malformed ticket reference as a non-blocking comment, not as
a code defect.

## 4. Struts implementation patterns

- **Temporary files** get UUID-based names in a controlled location, never a
  name derived from user input:

  ```java
  protected File createTemporaryFile(String fileName, Path location) {
      String uid = UUID.randomUUID().toString().replace("-", "_");
      return location.resolve("upload_" + uid + ".tmp").toFile();
  }
  ```

- **OGNL** — evaluate only framework-generated expressions, and keep member
  access on an allowlist. Treat any new path that evaluates a
  request-derived string as OGNL as a blocking finding under section 2.
- **Parameters** — request-settable action properties need `@StrutsParameter`;
  use `ParameterNameAware` to restrict accepted parameter names. Note that a
  `ModelDriven` action's own setters are a known exemption, so review changes
  there with care.
- **Uploads** — validate content types, sanitise file names, enforce size
  limits.
- **Naming** — framework default implementations are prefixed `Struts`
  (`StrutsBeanSelectionProvider`), not `Default`, for new classes.
- **No placeholder TODOs** — a comment must not reference a Jira ticket that
  has not been filed.

## Reviewing the review

Keep findings proportionate. Blocking comments are for correctness, the JUnit 5
trap, vacuous tests, and security-sensitive code paths. Everything else —
naming, ticket references, style — is a suggestion or a nitpick, and should be
labelled as such.
