# WW-5791 — Struts Boot

Ticket: [WW-5791](https://issues.apache.org/jira/browse/WW-5791). Fix version 7.5.0.

## Intent

A newcomer should get a working Struts application with almost no setup. Today the first page
needs a `web.xml`, a `struts.xml` (or the Convention plugin and its naming rules), a WAR build and
an external servlet container. Struts Boot cuts that to three steps:

1. add one dependency, `struts-boot-starter`
2. write an action class and register it in code
3. run `main()` and open the page

The first cut targets newcomers who are starting fresh. Running an existing Struts WAR as a
self-contained jar is a later step.

## Goals

- One dependency, one `main()` call and one action class are enough for a working page.
- Actions, results and views are registered in plain Java through a fluent API. There is no XML
  and no classpath scanning.
- No Spring dependency anywhere in the starter's dependency tree.
- The application builds to a single runnable jar: `java -jar app.jar`.
- Everything a newcomer writes is ordinary Struts. The action classes, value stack, interceptors,
  FreeMarker templates and `struts.properties` behave exactly as they do in a WAR.

## Non-goals (parked in WW-5791's "Parked for later" list)

- Running an existing WAR as a jar (the migration path)
- JSP views
- a Gradle plugin
- containers other than Tomcat
- a fluent API for defining interceptor stacks
- global results, global exception mappings and wildcard actions in the registry
- a bridge to Convention plugin annotations
- dev-mode reload of the registry
- a project generator or archetype
- health and info endpoints

## Anti-goals

- **A second configuration model.** The registry builds the same `PackageConfig` / `ActionConfig` /
  `ResultConfig` objects that `struts.xml` produces. Whatever a registered action does must match
  what the equivalent XML would do, including security defaults: strict method invocation,
  `@StrutsParameter` enforcement and the allowlist. If Boot quietly relaxes any of these to make
  the first page easier, it has failed, even if it works. The invariant: registry config is never
  broader than the equivalent `struts.xml`. Where the two differ, the registry is narrower: a
  registry action allows only its registered method, while an XML action also inherits the
  package's global allowed methods (`execute,input,back,cancel,browse,save,delete,list,index` in
  `struts-default`).
- **A Spring Boot look-alike that pulls in Spring.**
- **Core changes for Boot's convenience.** Boot uses only existing extension points (see
  "Wiring"). If something can't be done that way, stop and raise it. Don't patch `Dispatcher`.
- **Clashing with Spring Boot's files.** Boot never reads `application.properties`.

## The user's view

```java
public class HelloApp implements StrutsBootApplication {

    public static void main(String[] args) {
        StrutsBoot.run(HelloApp.class, args);
    }

    @Override
    public void actions(ActionRegistry actions) {
        actions.action("hello", HelloAction.class)
               .success("hello.ftl");

        actions.namespace("/admin", admin -> {
            admin.action("users", UserAction.class)
                 .method("list")
                 .success("admin/users.ftl");

            admin.action("saveUser", UserAction.class)
                 .method("save")
                 .httpMethods(POST)
                 .result("input", "admin/userForm.ftl")
                 .redirectAction("success", "users");
        });
    }
}
```

The template lives at `src/main/resources/templates/hello.ftl`. Running `HelloApp` serves
`http://localhost:8080/hello` (or `/hello.action`, depending on `struts.action.extension`).

## Registry semantics (decided)

| Call | Builds |
|---|---|
| top-level `actions.action(...)` | an action in a Boot-owned package with namespace `""` |
| `namespace(ns, consumer)` | one `PackageConfig` with that namespace, extending `struts-default` |
| `action(name, Class)` | `ActionConfig` with `className`; method defaults to `execute` |
| `.method(name)` | `methodName`, and adds it to the allowed methods. `ActionConfig#isAllowedMethod` already accepts the configured method, so the explicit entry only makes it visible in `getAllowedMethods()`. No other method is added: a registry action does not inherit the package's global allowed methods |
| `.success(path)` | shorthand for `.result("success", path)` |
| `.result(name, path)` | a `freemarker` result whose location is `/templates/` + `path` |
| `.redirectAction(name, actionName)` | a `redirectAction` result |
| `.httpMethods(HttpMethod...)` | `ActionConfig.Builder#addHttpMethods`, i.e. the WW-3717 restriction (`org.apache.struts2.interceptor.httpmethod.HttpMethod`) |
| `.interceptors(stackName)` | on an action or a namespace: an interceptor-ref to an existing stack. The default is the package default, `defaultStack` |

- **Views:** FreeMarker only in the first cut. A `.result` path that doesn't end in `.ftl` fails at
  startup with a clear message. Paths are made absolute (`/templates/...`) on purpose:
  `FreemarkerResult` resolves relative locations against the action's namespace, which newcomers
  wouldn't expect. `FreemarkerManager` already includes `StrutsClassTemplateLoader`, so
  `/templates/hello.ftl` resolves from the classpath with no extra loader.
- **Action names may not contain `/`** — a `ConfigurationException` at startup. Namespaces are
  the way to nest. Boot does not flip `struts.enable.SlashesInActionNames`, which would route
  differently from the same app as a WAR.
- **Errors fail at startup,** not on first request. Examples: duplicate action names in a
  namespace, a missing template on the classpath, an unknown stack name.
- **`struts.xml` coexists.** `Dispatcher` already loads `struts-default.xml,struts-plugin.xml,struts.xml`,
  and a missing `struts.xml` is tolerated (Convention-only apps already run that way). Registry
  packages are added on top *(my call)*:
  - Boot's packages get Boot-owned names (prefix `struts-boot`), so package names never collide
    with XML.
  - Sharing a namespace with XML is allowed. `struts-default` itself is namespace `""`.
  - **Action clashes throw a `ConfigurationException` at startup**: the same action name in the
    same namespace, registered twice in the registry or defined by both XML and the registry.
    Custom providers load after the XML ones (`Dispatcher#init`), so the check reads the packages
    already in the `Configuration`.
  - Not at compile time: the registry is ordinary Java whose names and namespaces only exist once
    `actions(...)` runs, and `struts.xml` is invisible to the compiler. Startup is the earliest
    point where both sides are known.

## Configuration (decided)

- Framework constants come from the existing `struts.properties` on the classpath, which
  `Dispatcher#init_LegacyStrutsProperties` already loads.
- Boot-only settings sit in the same file under the `struts.boot.*` prefix:
  - `struts.boot.port` (default 8080)
  - `struts.boot.context-path` (default `""`)
- Command-line `--struts.boot.port=9090` overrides the file. Supporting environment variables is
  the builder's call.
- `struts.devMode` keeps its framework default (off). Boot does not switch it on.

## Wiring (my call — the builder may change the mechanics, not the constraint)

The rule is that Boot needs no core changes. It fits the existing extension points like this:

1. `StrutsBoot.run` starts embedded Tomcat (`tomcat-embed-core`). It creates a context and
   registers `StrutsPrepareAndExecuteFilter` on `/*` in code. The Tomcat line follows core's
   Jakarta line:
   - Tomcat 10.1 (Jakarta EE 10) by default
   - Tomcat 11 under `-Pjakartaee11`
   The version moves into parent/BOM management; today it is only a test-scope property in
   `plugins/jasperreports7`. The Boot end-to-end tests run in both profiles.
2. It instantiates the application class and calls `actions(registry)`. It then stores the
   resulting registry as a servlet-context attribute.
3. The filter gets the init param `configProviders=<Boot's provider class>`. That provider
   implements `ServletContextAwareConfigurationProvider`. `Dispatcher#init_CustomConfigurationProviders`
   calls `initWithContext`, which reads the registry, and `loadPackages` builds the packages.
4. Tomcat's base directory is a fresh UUID-named directory under the system temp dir and is
   deleted on shutdown (per the secure temp-file pattern in `CLAUDE.md`).
5. A JVM shutdown hook stops Tomcat cleanly.

## Packaging (my call)

- A new reactor module. I propose a top-level `boot/` next to `plugins/`, because Boot is not a
  plugin and ships no `struts-plugin.xml`. The starter artifact is a jar that depends on
  `struts2-core` and `tomcat-embed-core`.
- **Runnable jar:** the first cut documents a `maven-shade-plugin` setup and uses it in the
  example app, rather than writing a custom Maven plugin. Known limitation: shading merges
  same-named resources, so two plugin jars that each carry `struts-plugin.xml` overwrite each
  other. The minimal scope has only core, so this doesn't bite yet. A nested-jar launcher or a
  dedicated Maven plugin is follow-up work and must be solved before plugins are supported in
  Boot apps.
- An example app goes under `apps/`. It is the newcomer path, and the integration test runs
  against it.

## Testing

- JUnit 4 (`org.junit.Test`); this repo has no JUnit 5. AssertJ and Mockito are available.
- **Registry unit tests:** each fluent call produces the expected config entities, including
  `method` being added to allowed methods, absolute template locations, and the startup errors.
- **End-to-end on embedded Tomcat**, following WW-5734's pattern in `plugins/jasperreports7`:
  - start `StrutsBoot` on a random port
  - GET a registered action and assert the rendered FreeMarker output
  - assert that a POST-only action rejects GET
  - assert that a method not registered on the action can't be invoked via `!method` or
    `method:` syntax. Run this with `struts.enable.DynamicMethodInvocation=true`: DMI is off by
    default, and with it off the test would pass without exercising the allowed-methods check
  - make the POST-only check hit an action that GET can otherwise reach, so the rejection comes
    from the method restriction and not from a 404
- **Coexistence test:** a `struts.xml` action and a registry action in the same namespace both
  answer. A duplicate name across the two fails startup.

## Security

Boot adds a new surface: the embedded container's defaults and the registry's translation into
config. Before the PR, read `THREAT_MODEL.md` and confirm that Boot sits within the framework's
existing trust boundaries. The registry must never produce config that XML couldn't. The anti-goal
above is the test for that.

## Left to the builder

- class and package names below `org.apache.struts2.boot`, apart from `StrutsBoot`,
  `StrutsBootApplication` and `ActionRegistry`
- the exact Tomcat connector setup, logging and the startup banner
- whether environment variables override `struts.boot.*`
- the internal shape of the registry's builder objects
