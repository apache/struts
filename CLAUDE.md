# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

For detailed procedures, use the specialized agents, commands and skills in `.claude/agents/`, `.claude/commands/` and `.claude/skills/`.

## Project Overview

Apache Struts is a mature MVC web application framework for Java (originally WebWork 2). Uses OGNL for value stack expressions and FreeMarker for UI tag templates.

**Version**: read it from the root `pom.xml` — it is `7.4.0-SNAPSHOT` as of 2026-08-23. Do not treat the `-SNAPSHOT` value as the next release number: the release version is chosen at release time from the semver impact of the accumulated changes, so `7.4.0-SNAPSHOT` may well ship as something else. Released versions are git tags like `STRUTS_7_2_1`.

### Build Commands

```bash
# Run tests (skip assembly for speed)
mvn test -DskipAssembly

# Single test in specific module
mvn test -DskipAssembly -pl core -Dtest=MyClassTest#testMethodName

# Jakarta EE 11 / Spring 7 profile
mvn clean install -Pjakartaee11
```

### Project Structure

```
struts/
├── core/           # struts2-core - main framework
├── plugins/        # Plugin modules (json, rest, spring, tiles, velocity, etc.)
├── apps/           # Sample applications (showcase, rest-showcase)
├── assembly/       # Distribution packaging
├── bom/            # Bill of Materials for dependency management
├── parent/         # Parent POM with shared configuration
└── jakarta/        # Jakarta EE compatibility modules
```

### Core Architecture

**Request Lifecycle**: `Dispatcher` → `ActionProxy` → `ActionInvocation` → Interceptor stack → `Action` → Result

Key packages in `org.apache.struts2`:

- `dispatcher` - Request handling, `Dispatcher`, servlet integration
- `interceptor` - Built-in interceptors (params, validation, fileUpload)
- `components` - UI tag components (form, textfield, submit)
- `action` - Action interfaces (`UploadedFilesAware`, `SessionAware`, etc.)
- `security` - Security utilities and OGNL member access policies

## Security-Critical Patterns

Apache Struts has a history of security vulnerabilities (OGNL injection, temp file exploits). Apply these Struts-specific patterns:

1. **Temporary files**: Use UUID-based names in controlled locations (see example below)
2. **OGNL expressions**: Evaluate only framework-generated OGNL; use allowlist member access
3. **File uploads**: Validate content types, sanitize filenames, enforce size limits
4. **Parameter filtering**: Use `ParameterNameAware` to restrict accepted parameter names

```java
// Secure temporary file pattern
protected File createTemporaryFile(String fileName, Path location) {
    String uid = UUID.randomUUID().toString().replace("-", "_");
    return location.resolve("upload_" + uid + ".tmp").toFile();
}
```

## Security Reports & Scans

For any security-related activity — vulnerability scans, security analysis, drafting security reports — **[`SECURITY.md`](SECURITY.md) is the source of truth**.
Read it first and follow its pre-reporting checks, assessment checklist, and reporting requirements. Reports must be sent privately to
`security@struts.apache.org`; do not open a public GitHub issue, Jira issue, pull request, or mailing list thread for a suspected vulnerability before private
triage. [`AGENTS.md`](AGENTS.md) is a shorter LLM-facing wrapper around the same process.

## Testing

Run with `mvn test -DskipAssembly`.

**Tests are JUnit 4 — there is no JUnit 5 anywhere in this repo.** `parent/pom.xml` declares
`junit:junit:4.13.2`; there are zero `org.junit.jupiter` imports. Two styles coexist:

- **JUnit 3 style** — ~114 classes extend `XWorkTestCase` (which extends `junit.framework.TestCase`).
  Methods must be named `testXxx()`. A Jupiter `@Test` annotation added to one of these **silently
  never runs** — it does not fail, it is simply not collected.
- **JUnit 4 style** — ~210 classes use `import org.junit.Test`.

Before adding a test, open the target file and match the style already there. AssertJ assertions and
Mockito mocks are both available and widely used. Introducing Jupiter is a build-infrastructure change
that needs its own `WW-` ticket, never a side effect of a feature.

## Pull Requests

- **Title format**: `WW-XXXX Description` — a Jira ticket ID is required for any code change.
  Pure documentation and build/CI changes (`SECURITY.md`, `AGENTS.md`, `CLAUDE.md`, `.claude/`,
  workflows) take no ticket and use conventional-commit form instead: `docs: ...`, `build(ci): ...`,
  `chore: ...`
- **Link ticket in description**: `Fixes [WW-XXXX](https://issues.apache.org/jira/browse/WW-XXXX)`
- **Issue tracker**: https://issues.apache.org/jira/projects/WW
- **Never submit a PR that fixes a suspected vulnerability.** Before opening a PR, verify the change is not a security patch (OGNL injection, parameter
  filtering bypass, file upload exploit, auth bypass, RCE, SSRF, path traversal, deserialization, XSS in framework components, etc.). If it is, stop and report
  it privately to `security@struts.apache.org` — see [`SECURITY.md`](SECURITY.md).