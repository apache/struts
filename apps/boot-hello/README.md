# Struts Boot Hello

The smallest Struts Boot application: one action, one FreeMarker template, no `web.xml`, no `struts.xml`.

Struts Boot is experimental: its API and defaults may change between minor releases.

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

| Key                                   | Default | Meaning                                                    |
|---------------------------------------|---------|------------------------------------------------------------|
| `struts.boot.port`                    | `8080`  | HTTP port; `0` picks a free port                           |
| `struts.boot.context-path`            | `""`    | Context path the application is served at                  |
| `struts.boot.address`                 | all     | Interface to listen on, e.g. `127.0.0.1`                   |
| `struts.boot.session.cookie.secure`   | `false` | `true` marks the session cookie `Secure`                   |
| `struts.boot.session.cookie.sameSite` | `lax`   | `SameSite` attribute: `lax`, `strict`, `none` or `unset`   |

The values of the two session cookie settings ignore case and surrounding spaces. Any other value, an empty one
included, stops the application at startup with an error that names the setting and the value. Keys are matched exactly:
a key that is not in this table, `struts.boot.session.cookie.samesite` for example, is ignored, and so is an argument
without `=`.

## Defaults worth knowing

- Tomcat listens on all interfaces, not only on localhost; set `struts.boot.address` to restrict it.
- Sessions are tracked by cookie only. The session cookie carries `SameSite=Lax`, and `Secure` is not forced on it:
  over the plain HTTP listener the starter opens, it is sent without `Secure`. Set
  `struts.boot.session.cookie.secure=true` when TLS ends in front of the application, at a reverse proxy for example.
- `struts.boot.session.cookie.sameSite` sets the default of Tomcat's cookie processor, so it also applies to every
  other cookie the application adds without a `SameSite` attribute of its own. With `unset` the processor adds no
  `SameSite` attribute; one the application sets on a cookie itself is kept.
- For `SameSite=None`, the cookie reaching the browser must also carry `Secure`. Set
  `struts.boot.session.cookie.secure=true` in Boot, or configure your HTTPS reverse proxy to add the `Secure`
  attribute. The starter does not check this combination. The `secure` setting only reaches the session cookie; a
  cookie the application adds itself needs its own `Secure`.
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
