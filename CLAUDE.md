# CLAUDE.md

## Build Commands

```bash
# Build without tests (fastest)
./mvnw clean install -DskipTests -DskipAssembly

# Test a single module
./mvnw -pl core clean test
./mvnw -pl plugins/spring clean test

# Full build with tests
./mvnw clean install

# Integration tests
./mvnw clean verify -DskipAssembly

# Coverage report
./mvnw clean verify -Pcoverage -DskipAssembly
```

## Project-Specific Rules

- This is the **6.x.x** branch (`release/struts-6-8-x`).
- Uses **javax.servlet** (Java EE), not Jakarta EE. Verify imports use `javax.servlet` namespace.
- Test pattern: `**/*Test.java`. Test classes use JUnit 4 with `@Test` annotations.
- OGNL expressions have strict security via `SecurityMemberAccess` — test any new OGNL usage against the security sandbox.
- Each plugin has its own `struts-plugin.xml` descriptor — register new beans there, not in core config.
- Run `./mvnw clean prepare-package` before committing to verify Apache RAT license headers pass.

## Module Layout

- `core/` — framework core
- `plugins/` — 20+ plugin modules (spring, json, tiles, velocity, etc.)
- `apps/showcase/` — feature demo app
- `apps/rest-showcase/` — REST examples