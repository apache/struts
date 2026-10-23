# Struts Boot Hello

The smallest Struts Boot application: one action, one FreeMarker template, no `web.xml`, no `struts.xml`.

## Three steps

1. Depend on `org.apache.struts:struts2-boot-starter`, at the same version as the rest of Struts. Importing
   `org.apache.struts:struts2-bom` in `<dependencyManagement>` keeps the versions aligned so the starter needs no
   `<version>` of its own.
2. Implement `StrutsBootApplication` and register actions:

   ```java
   public class HelloApp implements StrutsBootApplication {

       public static void main(String[] args) {
           StrutsBoot.run(HelloApp.class, args);
       }

       @Override
       public void actions(ActionRegistry actions) {
           actions.action("hello", HelloAction.class).success("hello.ftl");
       }
   }
   ```

3. Put the view in `src/main/resources/templates/hello.ftl` and run `main()`.

Then open http://localhost:8080/hello.action.

## Running the jar

```
mvn package -pl apps/boot-hello -am -DskipAssembly -DskipTests
java -jar apps/boot-hello/target/struts2-boot-hello-<version>.jar --struts.boot.port=9090
```

`<version>` is the Struts version being built, for example `struts2-boot-hello-7.5.0-SNAPSHOT.jar`. The example is
not deployed to Maven repositories; build it from source.

## Settings

Settings are read from `struts.properties` on the classpath. A `--key=value` argument overrides the file.

| Key                        | Default | Meaning                                    |
|----------------------------|---------|--------------------------------------------|
| `struts.boot.port`         | `8080`  | HTTP port; `0` picks a free port           |
| `struts.boot.context-path` | `""`    | Context path the application is served at  |
| `struts.boot.address`      | all     | Interface to listen on, e.g. `127.0.0.1`   |

## Defaults worth knowing

- Tomcat listens on all interfaces, not only on localhost; set `struts.boot.address` to restrict it.
- Error pages show the status only, without stack traces or the Tomcat version.
- `struts.devMode` stays off.
- Each start uses a fresh temporary base directory (`struts-boot-<uuid>`) that is deleted on shutdown.
- Views are FreeMarker only; a result path such as `hello.ftl` resolves to `/templates/hello.ftl` on the classpath.

## Shading and plugins

The jar is built with `maven-shade-plugin`. Every Struts plugin ships its own `META-INF/struts-plugin.xml`, and shading
keeps only one file with that name, so with two or more plugins on the classpath all but one would be silently lost.
This example uses no plugins. If your application needs them, package it differently instead of shading: a thin jar
with its dependencies in a `lib/` directory (`maven-dependency-plugin` copies them, `maven-jar-plugin` writes the
`Class-Path` manifest entry), or a distribution built with `maven-assembly-plugin`.

## Logging

Struts logs through the Log4j 2 API, and the starter ships no logging backend, so warnings and errors from Struts are
not printed. Add one to your application, for example (version managed by `org.apache.logging.log4j:log4j-bom`):

```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <scope>runtime</scope>
</dependency>
```

with a `log4j2.xml` on the classpath, or `log4j-slf4j2-impl` to route to an SLF4J backend.
