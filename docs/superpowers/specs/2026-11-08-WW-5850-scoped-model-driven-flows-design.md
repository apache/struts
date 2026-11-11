# Declarative multi-step flows

Date: 2026-11-08 (revised 2026-11-11) · Status: draft · Ticket: [WW-5850](https://issues.apache.org/jira/browse/WW-5850)

## Why

Struts has two ways to keep form state across several requests, for wizard-like flows: the `scope`
interceptor (`ScopeInterceptor`) and the `scopedModelDriven` interceptor.

The 2026-10-09 thread "Is anyone using the scope interceptor?" on user@ asked whether `scope` is still
in use. One user replied: they use it for actions that show more than one form, so the values a user
enters carry across several pages. They have not tried `scopedModelDriven`.

`scope` is a WebWork 2.2 leftover. It copies named properties to and from the session with OGNL
`setValue`/`findValue`, locks the whole session per request with a static lock map, and only works when
the user places it correctly relative to `params`. `scopedModelDriven` has the better model: one
object stored by reference, placed before `params`, user input bound through the normal
`params`/ModelDriven path. What neither has is a lifecycle: with `scopedModelDriven` the model stays in
the session until application code removes it by hand.

This ticket adds that lifecycle as **declarative flows**, implemented by a **new `flow` interceptor**
that follows the `scopedModelDriven` model. `scopedModelDriven` itself is not changed, so existing
users run exactly today's code. Once flows ship, `scope` can be deprecated.

## Goals

- A named flow: a set of actions sharing one model stored in the session, with an explicit start, any
  number of intermediate steps, and any number of ends.
- Each action states its role explicitly; the framework enforces the order (no step without a start).
- A broken flow definition fails at startup, not in front of a user.
- No behaviour or code-path change for anyone who does not declare a flow.

## Non-goals

- Changing `ScopedModelDrivenInterceptor` or the `ScopedModelDriven` interface in any way.
- Deprecating or changing `ScopeInterceptor`. See "Rollout".
- Independent concurrent runs of the same flow in one session (two browser tabs). One running flow
  per user per flow is enough for now.
- Application-scoped state (`scope` supports it; not carried over).
- Visualising flows in the Config Browser plugin or the IntelliJ IDEA plugin. Possible follow-up; it
  only needs to read the configuration this design produces.
- Enforcing an order *between* steps (step 2 before step 3). Steps only require a running flow.

## Anti-goals (a failure even if it works)

- Any change in behaviour for an existing app that declares no flow, including `scopedModelDriven`
  users.
- A client being able to start, reset, or end a flow by sending a request parameter (`scope`'s
  `session.reset`). Only the action's declared role changes flow state.
- Flow state written onto the action outside the `params` authorization path. The model reaches the
  action through `setModel`; request data reaches the model only through `params`.
- Session-wide locking (`scope`'s static lock map).
- A flow role inherited without the author choosing it (e.g. by annotating a base class).

## Design — decided

### A separate `flow` interceptor

Flows are implemented by a new interceptor registered as `flow` in `struts-default.xml`.
`ScopedModelDrivenInterceptor` and `ScopedModelDriven` are left byte-for-byte as they are.

- `flow` is added to `defaultStack` before `modelDriven` and `params`, so the stored model is on the
  action before `modelDriven` pushes it and `params` binds into it.
- For an action that declares no flow, `flow` does nothing and calls `invocation.invoke()`.
- For a flow action, `flow` does everything flow-related: role checks, the redirect to start, building
  or resuming the session model, setting it on the action, and ending the flow.

*(Call made while writing up — Łukasz to confirm.)* A flow action implements a **new interface**
(working name `FlowModelDriven<T> extends ModelDriven<T>`, adding `setModel(T)`), not
`ScopedModelDriven`. `ScopedModelDrivenInterceptor` acts only on `instanceof ScopedModelDriven`, so the
two interceptors never touch the same action and their relative order in a stack does not matter.
Reusing `ScopedModelDriven` would work only if `flow` ran first, and would leave `setScopeKey`/
`getScopeKey` meaningless on flow actions.

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

The stored model is always kept in the session.

**When an end action finishes the flow.** The model is removed from the session only when the end
action's method actually ran and returned a result code other than `input` or `error`. In every other
case the flow keeps running and the user can correct the form and resubmit:

- validation fails and `workflow` returns `input` before the action runs;
- the action itself returns `input` or `error`;
- an exception is thrown (whether or not the `exception` interceptor maps it to a result).

The action still holds the model after it is removed from the session, so the end action's result
(a confirmation page) can render what was submitted.

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
- every action in the flow implements the flow model interface;
- every action in the flow uses the same model type, read from its `getModel()` return type (the
  approach `ScopedModelDrivenInterceptor` uses to infer its model class).

*(Calls made while writing up — Łukasz to confirm.)*

- every action in the flow has `flow` in its resolved interceptor stack (`ActionConfig.getInterceptors()`),
  placed before `params`. Only `defaultStack` gets `flow` out of the box; an action on
  `paramsPrepareParamsStack`, `basicStack`, `modelDrivenStack` or a custom stack that declares a flow
  fails startup until `flow` is added, instead of silently going unenforced.
- the flow name and role are literals: no `{n}` wildcard substitution in either. Roles are counted per
  `ActionConfig`. A wildcard `ActionConfig` may be a `step` or `end`, but not the `start`: the redirect
  needs one concrete action to target.

A violation fails startup with a message naming the flow and the actions involved.

### Backward compatibility

- `ScopedModelDrivenInterceptor` and `ScopedModelDriven` are unchanged.
- Adding `flow` to `defaultStack` changes nothing for actions that declare no flow: it only checks the
  action's config and continues. Apps that copy `defaultStack` into their own stack simply lack `flow`
  until they use flows.

## Rollout

1. **7.6.0 — WW-5850:** flows ship. `ScopeInterceptor` and `scopedModelDriven` are untouched. Docs
   (struts-site) describe flows, with a worked example of a multi-page form.
2. **7.7.0 — separate ticket:** deprecate `ScopeInterceptor` with `@Deprecated(since, forRemoval = true)`
   and a migration note showing a `scope`-based wizard rewritten as a flow.
3. **8.0.0 — separate ticket:** remove `ScopeInterceptor` and its `struts-default.xml` registration.

## Constraints

- Struts code style: `Struts*` prefix for default implementations, no `XWork*` names, no boolean flag
  arguments, no parameter reassignment.
- Tests are JUnit 3 (`XWorkTestCase`) or JUnit 4; match the file being extended. No JUnit 5.
  `ScopedModelDrivenInterceptorTest` must pass unchanged.
- The role check and the redirect must happen before `params` binds anything.
- XML grammar: WW-5849 (`forward` result, also 7.6.0) introduces `struts-7.6.dtd` because
  `struts-7.5.dtd` is frozen once 7.5.0 ships. Whichever ticket lands first creates it; the other adds to it.
- Public text (docs, Jira, PR) describes this as consolidating on one mechanism for multi-page forms.

## Left to the builder

- Names: the interceptor class (`Struts*` prefix if it is a default implementation), the flow model
  interface, the `*Aware` interface and its method, the Convention annotation, the XML attribute(s) or
  element, and the role values.
- The exact XML grammar on `<action>`. Note that `<param>` children of `<action>` are set onto the action
  by `staticParams`, so the flow declaration should not be an action `<param>`.
- Whether the Convention annotation goes on the class, the action method, or both (Convention maps
  actions from methods via `@Action`). It must not be `@Inherited`.
- The session key format for a flow's model (it must include the namespace and the flow name).
- How the model is built (`ObjectFactory.buildBean`, as `scopedModelDriven` does). Model-type inference
  lives in the new code; `ScopedModelDrivenInterceptor` is not refactored to share it.
- How the redirect to the start action is produced (e.g. building a `redirectAction`-style result from
  the start action's namespace and name).
- Where startup validation hooks in (configuration build / reload).
- Interaction with `execAndWait`, `token`/`tokenSession` and `store` in flow actions.
