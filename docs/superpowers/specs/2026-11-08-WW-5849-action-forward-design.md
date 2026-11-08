# Action forward: a replacement for action chaining

Date: 2026-11-08 · Status: draft, under discussion · Ticket: [WW-5849](https://issues.apache.org/jira/browse/WW-5849)

## Why

On 2026-10-25 the "Is anyone using action chaining?" thread on user@ asked whether chaining is still
in use, ahead of a possible deprecation. Several users replied that they rely on it, one of them with
a large configuration migrated from Struts 1, and James Chaplin gave the reason worth designing for:

> it can be convenient to allow for a sort of "branching flow" where depending on state, the action
> result could chain/flow from one action to another directly (without having to make a round-trip
> back to the user's browser). I don't think any of the alternatives can achieve an equivalent result.

He is right: `redirectAction` costs a round-trip, and "move the logic into a service" is a rewrite,
not a migration. So removing chaining without a replacement is off the table.

But today's chaining does far more than James needs, and the extra reach is what makes it hard to
follow and to reason about. The thought experiment behind this design: *if Struts had no chaining,
what would we add to give James his server-side branch?* The answer is the `forward` result below.
`chain` is then deprecated in 7.x and removed in 8.0.0.

## What chaining does today (the problem)

Verified against `main` at `2f7666e98`.

**Which actions can be chained to**
- Any action in any namespace, by name. `namespace`, `actionName` and `method` on
  `ActionChainResult` go through `TextParseUtil.translateVariables` against the value stack, so the
  target can be computed from data. `method` is still checked against `allowedMethods`
  (`DefaultActionProxy.java:191`).
- The target cannot opt in or out. The only opt-out is source-side (`Unchainable`), implemented only
  by `DefaultTextProvider`.
- Loop detection is an exact `namespace/action!method` history match; `skipActions` disables it per
  action name, ignoring namespace.
- The Convention plugin chains implicitly: action `foo` returning `bar` with no configured result
  chains to `foo-bar` in the same package (`ConventionUnknownHandler.java:310-318`).

**How data passes — three implicit channels**
1. `ChainingInterceptor` copies every readable property of every non-`Unchainable` object in the
   value stack onto the target action (opt-out via `includes`/`excludes`; opt-in annotation gating
   via `struts.chaining.requireAnnotations`, default `false`, which reuses `@StrutsParameter`).
2. The value stack is shared: the source action stays in the stack below the target, so expressions
   evaluated during the target can resolve against it.
3. The original HTTP parameters are passed on and the target's `params` interceptor binds them again,
   after the copy.

**Quirks**
- `ChainingInterceptor.shouldCopyStack` is true whenever `invocation.getResult()` is null, which is
  every fresh invocation — not only chained ones. `<s:action>` builds its stack from the page's
  (`ActionComponent.java:187`), so the copy very likely runs for tag-invoked actions too (not yet
  pinned by a test).
- In `defaultStack`, `prepare` runs before `chain`, so `prepare()` sees pre-copy state.
- Setting `excludes` drops the default `actionErrors`/`actionMessages`/`fieldErrors` excludes.

## Goals

- Server-side transfer from one action to another within the same request, chosen by the source
  action's result code — James's "branching flow".
- Both boundaries explicit: **which actions can be forwarded to** (target opts in) and **what data
  crosses** (named on both sides).
- A **mechanical** migration from `chain`: no action redesign, but each target gets the opt-in and
  each result that needs data gets a hand-off list (see "Migration aid"). Not config-only — the
  explicit boundaries are worth that cost. Many chains need no hand-off at all,
  because request parameters re-bind.
- Ships in a 7.x minor, alongside `chain`, so users migrate before anything is deprecated.

## Non-goals

- Changing `chain`'s behaviour in 7.x. It keeps working exactly as today until removal.
- Fixing the `chain` quirks above. They go away with `chain` in 8.0.0.
- Replacing `redirectAction`, `dispatcher`, or `<s:action>`.

## Anti-goals (would make this a failure even if it works)

- Any implicit data channel: property copying by name, a shared value stack, or values flowing in
  because both sides happen to share a property name.
- A target computed from request data.
- An action becoming a forward target without its author choosing it.
- Reusing `@StrutsParameter` for the hand-off. HTTP input and framework hand-off are different trust
  channels and must stay separately gated.

## Design — decided

### The `forward` result

A new result type registered in `struts-default.xml`. Running it starts a new action invocation for
the target in the same request; the target's own result renders, as with `chain` today.

1. **Static target.** `actionName`, `namespace`, and `method` are literals from configuration — no
   `${...}` evaluation. Branching is done with result codes, one `forward` result per branch.
2. **Target opts in — a target-side allowlist.** An action can only be forwarded to if it is marked
   as a forward target; everything else is denied by default, the same model as `@StrutsParameter`
   for request parameters. Two equivalent forms, both recorded on `ActionConfig`:
   - a class-level annotation (working name `@Forwardable`) that is **not** `@Inherited`, so a
     `BaseAction` cannot opt in every subclass;
   - an attribute on `<action>` in XML, for classes the app does not own or annotate.

   No marker interface (inherited, so one `implements` on a base class reopens every action) and no
   negative marker such as `NotForwardable` (absence already means "not forwardable"; a deny marker
   is the `Unchainable` pattern this design leaves behind). The allowlist names targets, not
   source→target pairs; restricting sources (e.g. `@Forwardable(from = …)`) is a possible later
   extension, out of scope now. Rationale for per-class opt-in even with explicit hand-off: a
   forward bypasses everything mapped to the target's URL (`web.xml` security constraints, servlet
   filters, container auth); only the target's interceptors run.

   Forwarding to an unmarked action throws at run time; see "left to the builder" for config-time
   validation.
3. **Fresh value stack and own context.** The target gets a new value stack (the source action is not
   in it) and its own `ActionContext`: fresh conversion errors, its own `ActionInvocation`, the same
   session, application, locale and request parameters. The target's result renders against the
   target's stack only.
4. **Request parameters are re-bound** like a servlet forward: the target's `params` interceptor binds
   the original HTTP parameters under the target's own `@StrutsParameter` rules, exactly as for a
   direct request.
5. **Explicit hand-off, named on both sides.** The source's `forward` result lists the values it
   passes, each evaluated against the source action. The target declares which properties accept a
   forwarded value, with a dedicated annotation (not `@StrutsParameter`). Only values named on both
   sides are set; a value the source passes but the target does not accept is dropped and logged.
6. **Hand-off is applied after `params`**, by a dedicated interceptor placed directly after `params`
   in every shipped stack (`defaultStack`, `paramsPrepareParamsStack` after the second `params`, and
   the REST and Bean Validation plugin stacks). When both channels set the same property, the
   server-side hand-off wins. If a forward carries hand-off values and the target's stack lacks that
   interceptor, the forward throws rather than silently dropping them.
   - The accepting annotation covers **top-level setters on the action only** in this version: no
     nested paths, no ModelDriven model. A value that fails type conversion is dropped and logged at
     WARN. This keeps the new channel narrower than `@StrutsParameter`; it can be widened
     later if users need it.
7. **Action errors, field errors and messages do not carry over** unless passed explicitly like any
   other value.
8. **Depth limit, not name-history matching.** A constant caps nested forwards per request (counting
   `forward` only, not `<s:action>` or redirects); default 5; exceeding it throws `StrutsException`.
   No `skipActions` equivalent.
9. **Only explicit triggers.** No interceptor copies data, and `<s:action>` is unaffected.
10. **The target runs its full interceptor stack**, including security and HTTP-method interceptors,
    which see the original request.

### Convention plugin

The implicit `foo` → `foo-bar` transfer becomes an **implicit `forward`**, applied only when `foo-bar`
has opted in as a forward target. If `foo-bar` exists but has not opted in, Convention does not
forward (it keeps looking for a result as it would if `foo-bar` didn't exist). Convention reads the
opt-in from `ActionConfig`.

The implicit forward carries **no hand-off list**, so a Convention flow that relied on the chain copy
gets only request parameters. That is an accepted, documented behaviour change; Convention users add
the opt-in and, where they need data, an explicit `forward` result (Convention's `@Result` accepts
`forward` and its params).

### Migration aid

During the deprecation phase (rollout step 2), `chain` logs — once per chain, at a level users can
enable without noise — which properties `ChainingInterceptor` actually copied onto which target.
Users derive each `forward` result's hand-off list from real traffic instead of reading code. The
migration guide walks through: opt in the target, add the hand-off list from the log, drop the
`chain` interceptor reference.

### Rollout

1. **7.6.0** — ship `forward` with a new `struts-7.6.dtd` (the only grammar change is the opt-in
   attribute on `<action>`; `struts-7.5.dtd` is frozen once 7.5.0 is released; apps that opt in via
   XML switch their DOCTYPE), the target opt-in, the hand-off annotation, the depth constant,
   Convention's implicit forward, docs (struts-site), and a `chain` → `forward` migration guide.
   `chain` unchanged.
2. **7.7.0** — deprecate `ActionChainResult`, `ChainingInterceptor`, and `Unchainable` with
   `@Deprecated(since = "…", forRemoval = true)`; deprecate the `struts.chaining.*` constants; file a
   WW Task at fix version 8.0.0 for removal. One release after `forward` ships, so early adopters can
   report problems before everyone sees deprecation warnings.
3. **8.0.0** — remove `chain` and the `chain` interceptor from `struts-default.xml` (`chainStack`,
   `paramsPrepareParamsStack`, `defaultStack`) and from the REST and Bean Validation plugin stacks;
   remove the `ActionChainResult` unwinding loop in `DefaultActionInvocation.getResult()` and its
   contract in `ActionInvocation`; remove the `struts.chaining.*` constants, including
   `AliasInterceptor`'s use of `struts.chaining.requireAnnotations` (decide its replacement then).
   Convention's implicit chain path goes too.

Each step is its own WW ticket; step 1 is WW-5849.

## Constraints

- Struts code style and process: `Struts*` prefix for default implementations, no `XWork*` names,
  no boolean flag arguments, `@Deprecated(since, forRemoval = true)` plus a removal ticket.
- Tests are JUnit 3 (`XWorkTestCase`) or JUnit 4; match the file being extended. No JUnit 5.
- Must not depend on the REST plugin's optional XML dependency or any optional module.

## Left to the builder

- Names: the result type (`forward` is the working name; note the existing `dispatcher` result is a
  servlet forward to a resource, so docs must distinguish them), the target opt-in marker, the
  hand-off annotation, the result param that lists passed values, and the depth constant and its
  default.
- Whether forwarding to a non-opted-in target can also be caught at configuration time.
- How hand-off values are carried between invocations (an `ActionContext` entry is the obvious place)
  and how type conversion applies to them.
- Whether the forward result reuses `ActionProxyFactory.createActionProxy` and how it creates the
  fresh value stack.
- Interaction with `execAndWait`, `token`/`tokenSession`, and `store` interceptors in the target.
