---
name: triaging-security-reports
description: Use when a vulnerability or security report arrives for triage, when assessing a CVE/RCE/OGNL/injection claim against the code, or when drafting a reply to a security researcher — to research the claim from source without trusting the reporter and without fabricating your own facts.
---

# Triaging Security Reports

## Overview

A security report is a **claim to be tested, not a finding to be confirmed or rebutted**. The reporter may be right, wrong, partially right, or right about the symptom and wrong about the cause. Your job is to independently re-derive the truth from current source.

**Core principle:** Every factual statement that ends up in your assessment or reply — the reporter's claims *and your own* — must be traced to current source code before you write it down. The most common failure is not believing the reporter; it is **inventing supporting facts to justify a verdict you already reached.**

**Process authority:** [`SECURITY.md`](../../../SECURITY.md) is the source of truth for the disclosure process (private handling, assessment checklist, reporting rules). Read it. This skill governs *how you research and respond*, not the process itself.

## The Iron Rule

```
NO CLAIM IN A SECURITY RESPONSE WITHOUT A FILE:LINE YOU READ THIS SESSION.
```

Applies to the verdict, every mitigation you cite, and every "default" you state. If you can't point to the line, you can't write the sentence.

## Research: report-blind, not report-led

Read the report once to know what to investigate. Then **research as if you were auditing that area cold** — do not let the report's framing drive your search.

For each claim, independently verify:

| Reporter asserts | You must verify from source |
|---|---|
| A line number ("bug is at X:392") | Read that line **and its call path** — is it even reachable as described? |
| A severity / CVSS | Re-derive from actual exploitability, not their number |
| "No mitigation / no gate exists" | Search for gates, filters, allowlists, authorizers *yourself* — absence claims are the most often wrong |
| "Default configuration" | Check the **effective runtime default**, not one source (see trap below) |
| "Same as CVE-XXXX" | Confirm the mechanism actually matches; analogy ≠ equivalence |
| A working PoC | Trace whether the payload survives every filter on the path |

If the report has **no reproducible PoC against a default config**, that is itself a triage outcome — say so per `SECURITY.md`.

## The effective-default trap

A Java field initializer and the shipped config can disagree. Reading only one produces a confident, wrong claim.

```java
private boolean requireAnnotations = false;   // field initializer
```
```properties
struts.parameters.requireAnnotations=true     # default.properties OVERRIDES it
```

**The effective default is `true`.** Always trace the full chain: field initializer → `@Inject` setter → `default.properties` → any struts.xml override. State the *effective runtime* value, and cite the file that actually wins.

## Vulnerability vs. operator responsibility

"In the default configuration" is a crutch — drop it. Decide the real question:

- **Is it a vulnerability?** Then it's a vulnerability whether or not it's the default. Handle it privately per `SECURITY.md`.
- **Does it require an operator to opt into an insecure configuration?** A documented, opt-in setting (e.g. `cookiesName=*`, `devMode=true`) that works as advertised is the operator's responsibility, provided the docs carry the warning. Say "X works as documented; the operator owns the security implications of enabling it" — not "not a vuln *in the default config*."
- **Is the RCE/escalation only reachable via application code the framework can't constrain?** (e.g. an action that moves an uploaded file to a web root.) Then it's an application concern, not a framework vulnerability — state that boundary explicitly.

## Drafting the reply

- Lead with the verdict and the *reason*, both grounded in file:line.
- Cite a source for every mitigation you mention. If you didn't verify it this session, delete the sentence.
- Prefer "works as documented / operator responsibility" framing over "default configuration."
- **Don't over-promise.** Before pledging a hardening change, check it doesn't already exist (it often does) and that you intend to actually do it.
- Acknowledge anything the reporter got right (e.g. correct CVE-fix verification) — it builds the relationship and signals you actually read it.
- Keep it private: no public issue, PR, Jira, or list thread before triage. Never open a PR that is itself the security fix (see [`CLAUDE.md`](../../../CLAUDE.md)).

## Red Flags — STOP

- About to write "this is mitigated by X" — did you read X's line *this session*?
- About to state a "default" from a field initializer — did you check `default.properties`?
- Citing the reporter's line number without having traced its call path.
- Asserting "no gate / no check exists" without having grepped for it.
- Two of your own claims contradict each other → at least one is unverified. Stop and verify both.
- Promising a fix/warning "we'll add" without checking it isn't already there.
- Writing "not a vulnerability in the default configuration" → reframe as vuln-or-not + operator responsibility.

## Common Mistakes

| Mistake | Reality |
|---|---|
| "Reporter cited line 392, so that's the bug site" | A line is only a bug if it's *reachable* as described. Trace callers. |
| "The field defaults to false, so the gate is off by default" | `default.properties` may override it to true. Check the effective value. |
| "I'll add a mitigation to strengthen the rejection" | An unverified mitigation that's wrong discredits the whole response. Verify or omit. |
| "It rejects the payload, obviously" | Confirm the specific PoC string fails the specific filter (e.g. full-match regex `ACCEPTED_PATTERN`). |
| "We should add a startup warning" | Grep first — the warning frequently already exists. |
| "Not a vuln in default config" | Either it's a vuln or it's operator-owned opt-in. The default-config hedge muddies both. |
