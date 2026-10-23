# Struts Boot Hello

The smallest Struts Boot application: one action, one FreeMarker template, no `web.xml`, no `struts.xml`.

## Three steps

1. Depend on `org.apache.struts:struts2-boot-starter`.
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

## Settings

Settings are read from `struts.properties` on the classpath. A `--key=value` argument overrides the file.

| Key                        | Default | Meaning                                    |
|----------------------------|---------|--------------------------------------------|
| `struts.boot.port`         | `8080`  | HTTP port; `0` picks a free port           |
| `struts.boot.context-path` | `""`    | Context path the application is served at  |

## Defaults worth knowing

- Tomcat listens on all interfaces, not only on localhost.
- `struts.devMode` stays off.
- Each start uses a fresh temporary base directory (`struts-boot-<uuid>`) that is deleted on shutdown.
- Views are FreeMarker only; a result path such as `hello.ftl` resolves to `/templates/hello.ftl` on the classpath.

## Shading and plugins

The jar is built with `maven-shade-plugin`. Every Struts plugin ships its own `META-INF/struts-plugin.xml`, and shading
keeps only one file with that name, so with two or more plugins on the classpath all but one would be silently lost.
This example uses no plugins. If your application needs them, package it differently (for example with
Spring Boot's repackager or a `lib/` directory next to a thin jar) instead of shading.
