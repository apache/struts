# WW-5790 Two-Phase Copy Baseline Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Stop `ContentTypeInterceptor`'s two-phase copy from writing a fresh instance's constructor defaults over values already on the target.

**Architecture:** `applyTwoPhaseDeserialize` builds a second, untouched fresh instance as a baseline. `copyAuthorizedProperties` copies a property only when the filled value differs from the baseline value (`Objects.deepEquals`), and threads a per-level baseline through the nested-bean recursion. Collections, maps and arrays are compared at the property level only; their element rebuilding is untouched. No public API or configuration change.

**Tech Stack:** Java 17, REST plugin (`plugins/rest`), JUnit 3 style tests (`junit.framework.TestCase`), mockobjects `Mock`, Spring `MockHttpServletRequest`.

**Spec:** [WW-5790](https://issues.apache.org/jira/browse/WW-5790), section "Design" of the ticket description.

## Global Constraints

- Branch `WW-5790-two-phase-copy-baseline` off `main`; commits prefixed `WW-5790`; stage explicit paths, never `git add -A`; draft PR only.
- `ContentTypeInterceptorIntegrationTest` is JUnit 3 style: methods named `testXxx`, no `@Test`.
- No public API change: `ContentTypeHandler`, `AuthorizationAwareContentTypeHandler` and the public surface of `ContentTypeInterceptor` stay as they are. No new `struts.*` constant.
- The copy after the change writes a subset of what it writes today. Nothing new becomes bindable.
- The existing null-skip comment in `copyAuthorizedProperties` is the only place that explains skip rules; extend it, do not add a second comment.
- Run `/security-review` and a code review before `gh pr create`.

## Review Focus

- A nested type without a no-arg constructor, pre-existing on the target: the baseline cannot be built for that level, so the subtree falls back to today's copy-non-null rule and the set leaf must still be applied. Pinned in Task 1 (`testTwoPhaseCopyWithoutNestedNoArgConstructorStillAppliesTheSetLeaf`).
- A body value equal to the constructor default while the target holds a different value: not applied, by design. Pinned in Task 1 (`testTwoPhaseCopyDoesNotApplyABodyValueEqualToTheConstructorDefault`).
- A nested type with its own `equals`: two all-default nested instances compare equal and the whole subtree is skipped, which keeps the target's nested object untouched. Same outcome as the identity path that Task 1 tests, reached one branch earlier. Not tested separately; `DefaultedAddress` deliberately has no `equals` so the recursion path is the one exercised.
- Target with no no-arg constructor: unchanged, body dropped with a WARN. Existing behaviour, not re-pinned here.
- Non-deterministic constructor defaults (`UUID.randomUUID()`): still copied, as today. Documented in Task 2, not tested.

---

### Task 1: Baseline comparison in the two-phase copy

**Files:**
- Modify: `plugins/rest/src/main/java/org/apache/struts2/rest/ContentTypeInterceptor.java:189-283` (`applyTwoPhaseDeserialize`, `copyAuthorizedProperties`)
- Test: `plugins/rest/src/test/java/org/apache/struts2/rest/ContentTypeInterceptorIntegrationTest.java`

**Interfaces:**
- Consumes: `setupInterceptorWithHandler(Object action, ContentTypeHandler handler)`, `runWithBody(String body, String contentType)`, fixtures `DefaultedModel` and `ModelDrivenRestAction` already in the test (added by WW-5789).
- Produces: `copyAuthorizedProperties(Object source, Object baseline, Object target, Object action, Object authTarget, String prefix)`; `baseline` may be null, meaning "no baseline, copy every non-null value" (today's rule).

- [ ] **Step 1: Create the branch**

```bash
git switch -c WW-5790-two-phase-copy-baseline main
```

- [ ] **Step 2: Extend the fixtures in `ContentTypeInterceptorIntegrationTest`**

Extend `DefaultedModel` (keep `status = "new"`) with:

```java
private String name;
private List<String> tags = new ArrayList<>();
private DefaultedAddress address = new DefaultedAddress();
private PinnedAddress pinned;   // null by default, class has no no-arg constructor
```

plus plain getters and setters. Add two nested fixtures beside it:

```java
public static class DefaultedAddress {
    private String country = "PL";
    private String city;
    // getters and setters
}

public static class PinnedAddress {
    private String country = "PL";
    private String city;
    public PinnedAddress(String city) { this.city = city; }
    // getters and setters
}
```

Add a non-aware handler that scripts what "the body" sets:

```java
public static class ScriptedHandler implements ContentTypeHandler {
    private final Consumer<DefaultedModel> body;
    ScriptedHandler(Consumer<DefaultedModel> body) { this.body = body; }
    @Override public void toObject(ActionInvocation invocation, Reader in, Object target) { body.accept((DefaultedModel) target); }
    @Override public String fromObject(ActionInvocation invocation, Object obj, String resultCode, Writer stream) { return null; }
    @Override public String getContentType() { return "application/x-scripted"; }
    @Override public String getExtension() { return null; }
}
```

Imports to add: `java.util.ArrayList`, `java.util.List`, `java.util.function.Consumer`.

- [ ] **Step 3: Write the failing tests**

Each test builds `ModelDrivenRestAction modelDriven = new ModelDrivenRestAction()`, mutates `modelDriven.getModel()` to the pre-existing state, calls `setupInterceptorWithHandler(modelDriven, new ScriptedHandler(...))`, then `runWithBody("ignored", "application/x-scripted")`. Section comment above them: `// --- WW-5790: the two-phase copy copies only what the handler changed ---`.

```java
public void testTwoPhaseCopyKeepsAnOmittedScalarDefault()
    // model.setStatus("approved"); body sets name "alice"
    // assertEquals("approved", model.getStatus()); assertEquals("alice", model.getName());

public void testTwoPhaseCopyAppliesAScalarTheBodySets()
    // model.setStatus("approved"); body sets status "rejected"
    // assertEquals("rejected", model.getStatus());

public void testTwoPhaseCopyKeepsAnOmittedCollectionDefault()
    // model.setTags(new ArrayList<>(List.of("kept"))); body sets name "alice"
    // assertEquals(List.of("kept"), model.getTags());

public void testTwoPhaseCopyKeepsAnOmittedNestedDefault()
    // model.getAddress().setCountry("DE"); body sets name "alice"
    // assertEquals("DE", model.getAddress().getCountry());

public void testTwoPhaseCopyAppliesOnlyTheNestedLeafTheBodySets()
    // model.getAddress().setCountry("DE"); body sets address.city "Warsaw"
    // assertEquals("Warsaw", model.getAddress().getCity()); assertEquals("DE", model.getAddress().getCountry());

public void testTwoPhaseCopyWithoutNestedNoArgConstructorStillAppliesTheSetLeaf()
    // model.setPinned(new PinnedAddress("Gdansk")); model.getPinned().setCountry("DE");
    // body: m -> { m.setPinned(new PinnedAddress("Warsaw")); }
    // assertEquals("Warsaw", model.getPinned().getCity());
    // assertEquals("fallback keeps today's copy-non-null rule for this subtree", "PL", model.getPinned().getCountry());

public void testTwoPhaseCopyDoesNotApplyABodyValueEqualToTheConstructorDefault()
    // model.setStatus("approved"); body sets status "new"
    // assertEquals("documented limit of the baseline comparison", "approved", model.getStatus());
```

- [ ] **Step 4: Run the class and confirm which tests fail**

Run:
```bash
./mvnw -q test -DskipAssembly -pl plugins/rest -am -Dtest=ContentTypeInterceptorIntegrationTest -Dsurefire.failIfNoSpecifiedTests=false > /tmp/ww5790-red.log 2>&1; echo exit=$?; grep -E 'Tests run:|<<< FAILURE' /tmp/ww5790-red.log
```
Expected: exit=1. Failing: `KeepsAnOmittedScalarDefault` (status is `new`), `KeepsAnOmittedCollectionDefault` (tags empty), `KeepsAnOmittedNestedDefault` (country `PL`), `AppliesOnlyTheNestedLeafTheBodySets` (country `PL`), `DoesNotApplyABodyValueEqualToTheConstructorDefault` (status `new`). Passing already: `AppliesAScalarTheBodySets`, `WithoutNestedNoArgConstructorStillAppliesTheSetLeaf`. Every WW-5789 and Jackson test still green.

- [ ] **Step 5: Implement the baseline in `ContentTypeInterceptor`**

In `applyTwoPhaseDeserialize`, after `freshInstance` is created and filled:

```java
Object baseline = createFreshInstance(target.getClass());
copyAuthorizedProperties(freshInstance, baseline, target, invocation.getAction(), target, "");
```

Change the signature to `private void copyAuthorizedProperties(Object source, Object baseline, Object target, Object action, Object authTarget, String prefix)`. Inside the loop, after the null-skip `continue` and before the nested-bean branch:

```java
Object baselineValue = baseline == null ? null : readMethod.invoke(baseline);
if (baseline != null && Objects.deepEquals(sourceValue, baselineValue)) {
    continue;
}
```

In the nested-bean branch, the recursive call becomes `copyAuthorizedProperties(sourceValue, nestedBaseline, targetValue, action, authTarget, fullPath)` where `nestedBaseline` is `baselineValue` when non-null, otherwise `createFreshInstance(sourceValue.getClass())` (null when that class has no no-arg constructor, which hands the subtree today's rule). The three element-level recursive calls in `deepCopyAuthorizedCollection`, `deepCopyAuthorizedMap` and `deepCopyAuthorizedArray` pass `null` as the baseline: their elements are new objects, so defaults inside them are the element's own.

Add `import java.util.Objects;`.

- [ ] **Step 6: Extend the skip comment**

Replace the null-skip comment block at the `if (sourceValue == null)` branch so that it covers both rules in at most four lines: null in the fresh instance and a value equal to the baseline are both read as "not in the body"; copying either would overwrite what `params` or `prepare()` put on the target; a body value equal to the constructor default is therefore not applied.

- [ ] **Step 7: Run the REST plugin suite**

Run:
```bash
./mvnw test -DskipAssembly -pl plugins/rest -am '-Dtest=org/apache/struts2/rest/**/*Test' -Dsurefire.failIfNoSpecifiedTests=false > /tmp/ww5790-green.log 2>&1; echo exit=$?; grep -E '^\[INFO\] Tests run: [0-9]+, Failures: [0-9]+, Errors: [0-9]+, Skipped: [0-9]+$|BUILD' /tmp/ww5790-green.log
```
Expected: exit=0, `BUILD SUCCESS`, 0 failures, test count 233 (226 before plus 7).

- [ ] **Step 8: Commit**

```bash
git add plugins/rest/src/main/java/org/apache/struts2/rest/ContentTypeInterceptor.java plugins/rest/src/test/java/org/apache/struts2/rest/ContentTypeInterceptorIntegrationTest.java
git diff --cached --name-only
git commit -m "WW-5790 fix(rest): copy only what the body handler changed in the two-phase copy"
```

---

### Task 2: Document the skip rules on the REST plugin page

**Files:**
- Modify: `~/Projects/Apache/struts-site/source/plugins/rest/index.md:323-329` (the "As of Struts 7.2.0 the built-in content type handlers enforce" paragraph)

**Interfaces:**
- Consumes: the behaviour pinned in Task 1.
- Produces: a struts-site PR titled `WW-5790 docs: two-phase copy skips constructor defaults`.

- [ ] **Step 1: Branch in the site repo**

```bash
cd ~/Projects/Apache/struts-site && git switch -c WW-5790-two-phase-copy-docs main
```

- [ ] **Step 2: Add one paragraph after the 7.2.0 enforcement paragraph**

Content to convey, in the page's voice: a custom handler that implements only `ContentTypeHandler` is deserialized into a fresh instance and only authorized properties are copied onto the action or model; since 7.5.0 that copy skips a property whose value is null or equal to a fresh instance's constructor default, so values bound by `params` or loaded in `prepare()` survive a body that omits them; the two consequences a reader must know: a body value equal to the constructor default is not applied over a differing existing value, and the constructor of the target runs twice per request on this path. Close with the recommendation that already exists on the page: implement `AuthorizationAwareContentTypeHandler` to get exact semantics.

- [ ] **Step 3: Check the page renders**

Run: `cd ~/Projects/Apache/struts-site && grep -n -A12 'As of Struts 7.2.0 the built-in content type handlers' source/plugins/rest/index.md`
Expected: the new paragraph sits before the `Since Struts 7.3.0` warning block, no broken Markdown.

- [ ] **Step 4: Commit and open the draft PR**

```bash
cd ~/Projects/Apache/struts-site
git add source/plugins/rest/index.md
git commit -m "WW-5790 docs: two-phase copy skips constructor defaults"
git push -u origin WW-5790-two-phase-copy-docs
gh pr create --draft --title "WW-5790 docs: two-phase copy skips constructor defaults" --body "Documents the skip rules of the REST two-phase copy after [WW-5790](https://issues.apache.org/jira/browse/WW-5790)."
```

---

### Task 3: Review gate and the code PR

**Files:**
- None new. Operates on the Task 1 branch.

- [ ] **Step 1: Security review and code review**

Run `/security-review` on the branch diff and `/code-review medium`. Apply what survives verification; re-run Task 1 Step 7 after any change.

- [ ] **Step 2: Push and open the draft PR**

```bash
git push -u origin WW-5790-two-phase-copy-baseline
gh pr create --draft --title "WW-5790 fix(rest): copy only what the body handler changed in the two-phase copy" --body-file <body>
```

PR body: `Fixes [WW-5790](https://issues.apache.org/jira/browse/WW-5790)`, the three-line mechanism, the two documented limits, the affected-population statement (`requireAnnotations=true`, non-aware handler, non-null constructor default), the test list, the suite count, the review outcome, the link to the site PR, and the `🤖 Generated with [Claude Code](https://claude.com/claude-code)` line. No session link.

- [ ] **Step 3: Note the Version Notes line**

The 7.5.0 Version Notes owe one line: "REST plugin: with `struts.parameters.requireAnnotations=true`, a custom `ContentTypeHandler` no longer resets properties the body omits to their constructor defaults (WW-5790)." Record it where the other 7.5.0 migration notes are collected; it is written at release time, not in this PR.
