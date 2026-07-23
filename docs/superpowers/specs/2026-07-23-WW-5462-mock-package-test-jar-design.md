# WW-5462: Move `org.apache.struts2.mock` out of the published struts2-core jar

- **Ticket**: [WW-5462](https://issues.apache.org/jira/browse/WW-5462)
- **Date**: 2026-07-23
- **Status**: Approved design
- **Fix version**: 7.3.0

## Problem

Six mock classes live in `core/src/main/java/org/apache/struts2/mock/` and ship in the
published `struts2-core` jar, yet nothing in production code references them — they are
used only by tests (core's own tests plus the json, rest, spring, xslt, jasperreports,
and jasperreports7 plugin tests). One of them is referenced by nothing at all. Test-only
code in the public jar inflates the supported API surface for no benefit.

The `com.opensymphony.xwork2.mock` package named in the ticket no longer exists; it was
migrated to `org.apache.struts2.mock` in 7.0.

## Decision summary

| Decision | Choice |
|---|---|
| Compatibility | Remove from the main jar in 7.3.0; note in release notes (no deprecation cycle) |
| Sharing mechanism | Filtered `test-jar` attached by core, consumed test-scope by plugins |
| Shared `mocks` module | Rejected — mocks implement core interfaces, so a module would create a reactor dependency cycle (`mocks → core` compile, `core → mocks` test); Maven rejects cycles regardless of scope |
| `MockContainer` | Delete — zero references anywhere in the codebase |

## Design

### 1. Delete dead code

Delete `core/src/main/java/org/apache/struts2/mock/MockContainer.java`. No references
exist in core, plugins, or apps.

### 2. Move the remaining five mocks

Move from `core/src/main/java/org/apache/struts2/mock/` to
`core/src/test/java/org/apache/struts2/mock/`:

- `MockActionInvocation`
- `MockActionProxy`
- `MockInterceptor`
- `MockObjectTypeDeterminer`
- `MockResult`

They join the three mocks already in test sources (`DummyTextProvider`,
`InjectableAction`, `MockLazyInterceptor`). The package name is unchanged, so no core
test file needs an import change.

### 3. Attach a filtered test-jar from core

In `core/pom.xml`, add a `maven-jar-plugin` execution:

```xml
<execution>
    <goals>
        <goal>test-jar</goal>
    </goals>
    <configuration>
        <archive combine.self="override"/>
        <includes>
            <include>org/apache/struts2/mock/**</include>
        </includes>
    </configuration>
</execution>
```

The `<archive combine.self="override"/>` resets the `<archive><manifestFile>` configuration
inherited from the root pom, which points at the OSGi manifest generated for the main jar;
without it the test-jar would carry the main bundle's manifest.

**Dev-build caveat:** when the reactor runs without packaging (`mvn test -DskipAssembly`),
Maven resolves the test-jar dependency to core's whole `target/test-classes` directory —
the includes filter applies only to the packaged jar. Plugin test classpaths then also see
core's other test classes and test resources (`struts.xml`, `struts.properties`). This is
verified empirically during implementation; packaged builds (`mvn package`/`install`) always
use the filtered jar.

The includes filter is essential: it keeps core's other test classes and test resources
(`struts.xml` variants, test properties) off plugin test classpaths, where they would
conflict with plugin test configuration. The resulting
`struts2-core-<version>-tests.jar` contains only the mock package (all eight classes).

This artifact is internal build plumbing. It is deployed alongside the main artifact as
Maven attaches it by default, but it is not added to the bom and not documented as
supported API.

### 4. Wire the six consuming plugins

Add to the poms of `json`, `rest`, `spring`, `xslt`, `jasperreports`, and
`jasperreports7`:

```xml
<dependency>
    <groupId>org.apache.struts</groupId>
    <artifactId>struts2-core</artifactId>
    <version>${project.version}</version>
    <type>test-jar</type>
    <scope>test</scope>
</dependency>
```

`<type>test-jar</type>` (not `<classifier>tests</classifier>`) is Maven's recommended
form for reactor builds. The version is declared inline with `${project.version}`
because the bom stays free of internal test artifacts.

### 5. Remove the spring plugin's duplicate

Delete `plugins/spring/src/test/java/org/apache/struts2/mock/DummyTextProvider.java`.
The test-jar provides the canonical copy from core's test sources. No import changes
are needed — same package, same class name.

## Compatibility

Downstream projects that use these mocks in their own tests will fail to compile
against 7.3.0. This is accepted (Trivial-priority cleanup; the classes were never
intended as public API). Release notes must include a migration note: use
`struts2-junit-plugin`, Mockito, or copy the needed class into your own test sources.

## Out of scope

- Extracting a core `api` module to enable a supported, standalone mocks artifact
- Any change to the three mocks already in core test sources beyond packaging
- Renaming or refactoring the mock classes themselves

## Verification

1. `mvn test -DskipAssembly` — full reactor: core and all six plugins compile and pass.
2. `mvn clean install -Pjakartaee11` — Jakarta EE 11 profile sanity check.
3. Inspect `core/target/struts2-core-*-tests.jar` — contains only
   `org/apache/struts2/mock/**` (eight classes), no test resources.
4. Inspect `core/target/struts2-core-*.jar` — contains no `org/apache/struts2/mock`
   package.
