# Declarative multi-step flows on ScopedModelDriven

Date: 2026-11-08 · Status: draft · Ticket: [WW-5850](https://issues.apache.org/jira/browse/WW-5850)

## Why

Struts has two ways to keep form state across several requests, for wizard-like flows: the `scope`
interceptor (`ScopeInterceptor`) and the `scopedModelDriven` interceptor.

The 2026-10-09 thread "Is anyone using the scope interceptor?" on user@ asked whether `scope` is still
in use. One user replied: they use it for actions that show more than one form, so the values a user
enters carry across several pages. They have not tried `scopedModelDriven`.

`scope` is a WebWork 2.2 leftover. It copies named properties to and from the session with OGNL
`setValue`/`findValue`, locks the whole session per request with a static lock map, and only works when
the user places it correctly relative to `params`. `scopedModelDriven` is the better base: one model
object stored by reference, already in `defaultStack` before `params`, user input bound through the
normal `params`/ModelDriven path. What it lacks is what makes `scope` usable for wizards: a lifecycle.
Today the model stays in the session until application code removes it by hand.

This ticket adds that lifecycle as **declarative flows**, so `scopedModelDriven` becomes a full
alternative to `scope`. Only then is `scope` deprecated.

## Goals

- A named flow: a set of actions sharing one model stored in the session, with an explicit start, any
  number of intermediate steps, and any number of ends.
- Each action states its role explicitly; the framework enforces the order (no step without a start).
- A broken flow definition fails at startup, not in front of a user.
- Existing `scopedModelDriven` users see no change unless they declare a flow.

## Non-goals

- Deprecating or changing `ScopeInterceptor`. See "Rollout".
- Independent concurrent runs of the same flow in one session (two browser tabs). One running flow
  per user per flow name is enough for now.
- Application-scoped state (`scope` supports it; not carried over).
- Visualising flows in the Config Browser plugin or the IntelliJ IDEA plugin. Possible follow-up; it
  only needs to read the configuration this design produces.
- Enforcing an order *between* steps (step 2 before step 3). Steps only require a running flow.

## Anti-goals (a failure even if it works)

- A client being able to start, reset, or end a flow by sending a request parameter (`scope`'s
  `session.reset`). Only the action's declared role changes flow state.
- Flow state written onto the action outside the `params` authorization path. The model reaches the
  action through `ScopedModelDriven.setModel`, as today; request data reaches the model only through
  `params`.
- Session-wide locking (`scope`'s static lock map).
- A flow role inherited without the author choosing it (e.g. by annotating a base class).

## Design — decided

### Flows and roles

A **flow** is identified by its **namespace plus a name**. Every action in the same namespace that
declares the same flow name belongs to that flow. `/shop` + `checkout` and `/admin` + `checkout` are two
independent flows. One configuration can define many flows, each with its own stored model. The
namespace is part of the flow's identity everywhere: startup grouping, the session key, and the
redirect target. An action reached through namespace fallback belongs to the flow of the namespace it
is configured in, not the one it was requested under.

Each action in a flow has exactly one role:

| Role | Count per flow | Behaviour |
|---|---|---|
| `start` | exactly one | Begins the flow. Creates a new model by default; see "Resuming at the start". |
| `step` | any number | Requires a running flow. Works on the stored model. The flow keeps running. |
| `end` | at least one | Requires a running flow. Works on the stored model. Ends the flow only on a finishing outcome (below). |

**When an end action finishes the flow.** The model is removed from the session only when the end
action's method actually ran and returned a result code other than `input` or `error`. In every other
case the flow keeps running and the user can correct the form and resubmit:

- validation fails and `workflow` returns `input` before the action runs;
- the action itself returns `input` or `error`;
- an exception is thrown (whether or not the `exception` interceptor maps it to a result).

The action still holds the model after it is removed from the session, so the end action's result
(a confirmation page) can render what was submitted.

The action still implements `ScopedModelDriven<T>`. The stored model is set on it with `setModel`
before `params` runs, as `ScopedModelDrivenInterceptor` does today, so `params` binds request data into
the stored model.

### Declaring roles — on each action

The role and flow name are declared on each action, not in a separate flow block:

- **XML:** on the `<action>` element in `struts.xml`, recorded on `ActionConfig`.
- **Convention:** an annotation carrying the flow name and role, meaning the same thing.

### A step or end with no running flow

If a `step` or `end` action is requested and the flow is not running for this user (no stored model, or
no session), the action does not run. The user is redirected to the flow's `start` action.

### Resuming at the start

When the `start` action is requested and a model for that flow is already stored:

- **Default — new:** the stored model is discarded and a new one is built.
- **Opt-in — resume per request:** if the start action implements a new `*Aware` interface, the framework
  calls it with the existing model and the action decides for this request. `true` resumes with the
  existing model; `false` discards it and builds a new one. The method is only called when a stored
  model exists.

Working shape (name to be decided by the builder):

```java
public interface FlowResumeAware<T> {
    boolean resumeFlow(T existingModel);
}
```

Passing the model lets the action resume a half-filled order, start over if it's stale, or decide
based on a request parameter it has validated itself. A marker interface was considered and rejected:
it would only be a static "always resume", and the per-request decision covers that.

### Startup validation

When the configuration is built, each flow is checked:

- exactly one `start` action;
- at least one `end` action;
- every action in the flow implements `ScopedModelDriven`;
- every action in the flow uses the same model type, read from its `getModel()` return type, the same
  way `ScopedModelDrivenInterceptor` already infers the model class.

*(Calls made while writing up — Łukasz to confirm.)*

- every action in the flow has `scopedModelDriven` in its resolved interceptor stack
  (`ActionConfig.getInterceptors()`). `paramsPrepareParamsStack`, `basicStack`, `modelDrivenStack` and
  custom stacks lack it; a flow declared there would otherwise go unenforced.
- the interceptor's parameters must not contradict the flow. For a flow action the flow declaration
  wins: the model is always session-scoped (the interceptor's `scope` defaults to `null`, which means
  the request context map) and stored under the flow's key (`name` is ignored). A `className` that
  differs from the flow's model type fails startup.
- the flow name and role are literals: no `{n}` wildcard substitution in either. Roles are counted per
  `ActionConfig`. A wildcard `ActionConfig` may be a `step` or `end`, but not the `start`: the redirect
  needs one concrete action to target.

A violation fails startup with a message naming the flow and the actions involved.

### Backward compatibility

An action that implements `ScopedModelDriven` but declares no flow behaves exactly as today, including
the interceptor's `scope`, `name` and `className` parameters.

## Rollout

1. **7.6.0 — WW-5850:** flows ship. `ScopeInterceptor` is untouched. Docs (struts-site) describe flows,
   with a worked example of a multi-page form.
2. **7.7.0 — separate ticket:** deprecate `ScopeInterceptor` with `@Deprecated(since, forRemoval = true)`
   and a migration note showing a `scope`-based wizard rewritten as a flow.
3. **8.0.0 — separate ticket:** remove `ScopeInterceptor` and its `struts-default.xml` registration.

## Constraints

- Struts code style: `Struts*` prefix for default implementations, no `XWork*` names, no boolean flag
  arguments, no parameter reassignment.
- Tests are JUnit 3 (`XWorkTestCase`) or JUnit 4; match the file being extended.
  `ScopedModelDrivenInterceptorTest` is JUnit 3. No JUnit 5.
- `scopedModelDriven` already sits in `defaultStack` before `modelDriven` and `params`. The role check
  and the redirect must happen there, before `params` binds anything.
- XML grammar: WW-5849 (`forward` result, also 7.6.0) introduces `struts-7.6.dtd` because
  `struts-7.5.dtd` is frozen once 7.5.0 ships. Whichever ticket lands first creates it; the other adds to it.
- Public text (docs, Jira, PR) describes this as consolidating on one mechanism for multi-page forms.

## Left to the builder

- Names: the `*Aware` interface and its method, the Convention annotation, the XML attribute(s) or
  element, and the role values.
- The exact XML grammar on `<action>`. Note that `<param>` children of `<action>` are set onto the action
  by `staticParams`, so the flow declaration should not be an action `<param>`.
- Whether the Convention annotation goes on the class, the action method, or both (Convention maps
  actions from methods via `@Action`). It must not be `@Inherited`.
- The session key for a flow's model (e.g. derived from the flow name), and how it relates to the
  existing `name` parameter.
- How the redirect to the start action is produced (e.g. building a `redirectAction`-style result from
  the start action's namespace and name).
- Where startup validation hooks in (configuration build / reload).
- Interaction with `execAndWait`, `token`/`tokenSession` and `store` in flow actions.
