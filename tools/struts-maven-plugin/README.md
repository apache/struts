# Struts Maven Plugin

`struts:watch` copies edited resources (`struts.xml`, `*.properties`, FreeMarker templates, validation files) into
`target/classes` while an application runs, so Struts' own reload picks them up without a rebuild or restart.

It pushes resource changes only. It does not compile or reload Java classes: use a restart or debugger hotswap for
those. Webapp files (JSP, FTL under `src/main/webapp`) are already served from source.

## Application setup

The application must have reloading on: `struts.devMode=true` (with the reload switches not explicitly `false`), or
`struts.configuration.xml.reload=true` and `struts.i18n.reload=true`. An explicit `false` wins over `devMode`. The goal
logs a warning at startup when it can see the switches are off, or when `struts.devMode` is not found at all (best effort: it reads only `struts.xml` and
`struts.properties` in the output directory).

Declare the plugin in the application's `pom.xml`; the `struts:` prefix resolves from there, no `pluginGroups` entry
is needed.

```xml
<plugin>
    <groupId>org.apache.struts</groupId>
    <artifactId>struts-maven-plugin</artifactId>
    <version>X.Y.Z</version>
</plugin>
```

`X.Y.Z` is the Struts version; the plugin is released together with Struts.

## Running it

Next to a server started in another terminal, the goal blocks until Ctrl+C:

```sh
mvn struts:watch
```

In a multi-module build, run it from the root with `-pl <module>`. Do not add `-am`: it puts the module's dependencies
into the build too, and the goal then fails because it supports a single module.

Or bound to `process-resources` in the background, switched on from the command line, together with the Jetty plugin:

```xml
<plugin>
    <groupId>org.apache.struts</groupId>
    <artifactId>struts-maven-plugin</artifactId>
    <version>X.Y.Z</version>
    <executions>
        <execution>
            <id>watch</id>
            <phase>process-resources</phase>
            <goals>
                <goal>watch</goal>
            </goals>
            <configuration>
                <background>true</background>
            </configuration>
        </execution>
    </executions>
</plugin>
```

```sh
mvn jetty:run -Dstruts.watch
```

Without `-Dstruts.watch` a background execution does nothing.

Each push is logged, for example `pushed struts.xml -> target/classes/struts.xml`. Files whose output is missing or
older than the source are pushed once at startup. Filtering follows the module's `maven-resources-plugin`
configuration, including its plugin-level `<filters>`. The pom is read once at startup: restart the goal after changing
the resource or filtering configuration.

## Parameters

| Parameter | Property | Default | Meaning |
|---|---|---|---|
| `pollInterval` | `struts.watch.pollInterval` | `500` | Milliseconds between scans of the resource directories |
| `background` | `struts.watch.background` | `false` | Run on a daemon thread and return, so the goal can be bound to a phase |
| `enabled` | `struts.watch` | `false` | Switches background watching on; consulted only when `background` is `true` |
| `timeout` | `struts.watch.timeout` | `0` | Seconds after which a blocking run stops; `0` runs until interrupted |

## Jetty scanner

Pushed resources land in `target/classes`, which the Jetty Maven plugin's scanner would treat as a change and restart
the webapp. Restrict its class scan to compiled classes so recompiles still restart the webapp but resource pushes do
not:

```xml
<scanClassesPattern>
    <includes>
        <include>**/*.class</include>
    </includes>
</scanClassesPattern>
```

`apps/showcase/pom.xml` has both the bound execution and this setting. Its committed `struts.xml` keeps `devMode` and
the reload switches off, so to try it there flip them locally.

## Limitations

- Single module only: in a multi-module build the goal fails with a message to run it with `-pl <module>` (without
  `-am`).
- No Java class reloading, no Gradle support, no Struts Boot support.
