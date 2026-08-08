# `creating-release-vote-mail` skill — design

**Date:** 2026-08-08
**Status:** revised after baseline testing; ready for implementation

## Problem

Opening a Struts release vote means sending `[VOTE] Apache Struts X.Y.Z` to `dev@`. The mail
is frozen ASF boilerplate wrapped around a plain-text rendering of the release's Version Notes
page, written by hand each time.

## Scope

**In scope:** composing and drafting the vote-opening mail.

**Out of scope:** tallying the vote, the `Re: [CLOSED] [VOTE] …` reply, the `[ANN]`
announcement.

## Position in the release flow

Sibling of `creating-version-notes`, running immediately after it:

```
Version Notes page ─┐
GitHub release      ├─ creating-version-notes ─→ [TEST] mail ─→ creating-release-vote-mail ─→ [VOTE] mail
staged artifacts   ─┘
```

## What baseline testing changed

Three fresh agents drafted the 7.3.0 vote mail with no vote-mail skill present, in a worktree
that did not contain this spec: **A-clean** (neutral), **B** (time pressure, explicit
instruction to clone the previous mail, explicit push to include the user list, review gate
removed), **C-clean** (framing thoroughness about unpublished security fixes as a duty owed to
binding voters).

**Two-thirds of the originally specified content taught nothing.** Every baseline already
rendered the body from the cwiki page rather than Jira, verified all four links live, kept
security summaries truncated with no severity/CVE/S2-XXX, left the checkboxes empty, used the
exact subject, put `private@` on Bcc, and authored a fresh opening sentence. `creating-version-notes`,
`creating-security-bulletins` and `SECURITY.md` already carry that knowledge. Restating it
would be words the skill does not need.

**The skill therefore teaches only what agents actually got wrong:**

| Failure | Baselines | Form required |
|---|---|---|
| Security detail routed into the vote via a private companion mail | C-clean | Prohibition covering every channel |
| Body grew content the page does not carry | C-clean | Recipe — state what the mail *is*, in order |
| `user@` added to recipients | B | Prohibition + rationalization counter |
| Sent rather than drafted | B | Prohibition + rationalization counter |
| Frozen boilerplate edited | B | Prohibition + rationalization counter |

**Two decisions were reversed by the evidence:**

- **Rejected requests are included.** All three baselines reproduced them, each citing that
  the decision should be visible; the page itself says they are "listed here so the decision
  is visible rather than silent". The mail mirrors the page, with no exception to enforce.
- **The staging URL is `content/groups/staging/`.** The Version Notes page and the `[TEST]`
  mail both use it; only the archived vote mails used `content/repositories/staging/`. Aligning
  removes a rule that would have existed solely to stop agents fixing the inconsistency — one
  baseline fixed it unprompted. It is also the group repo, so a tester's build resolves
  released transitive dependencies.

## Skill identity

**Name:** `creating-release-vote-mail`
**Location:** `.claude/skills/creating-release-vote-mail/`
**Files:** `SKILL.md` + `vote-mail-template.md`

**Description:**

> Use when opening the formal release vote for a Struts release candidate on any maintenance
> line (6.x, 7.x) — composing and drafting the `[VOTE] Apache Struts X.Y.Z` mail to `dev@`
> once the Version Notes page, GitHub release and staged artifacts are published.

Per `writing-skills`, the description states triggering conditions only and does not summarise
the workflow, so agents read the body rather than shortcutting to the description.

## What the skill contains

### 1. The body recipe (addresses the bloat failure)

C-clean's mail ran 279 lines against 128 and 131 for the other two: it invented a `private@`
companion note and authored a "new settings and behaviour changes" section derived from fix
commits, none of which appears on the page. This is a wrong-shape failure, not indiscipline,
and `writing-skills` is explicit that prohibitions backfire on wrong-shape failures. So the
skill states the contract positively rather than forbidding additions:

> The mail has exactly these parts, in this order: the two-sentence opener; the page's
> Breaking changes, Deprecations and Rejected requests sections where present; the page's
> issue-type sections in page order; the four link lines; the vote boilerplate; the sign-off.

A part not on that list is not in the mail. Stated as a shape, there is nothing to negotiate.

### 2. Recipients (addresses the `user@` failure)

```
To:  dev@struts.apache.org
Bcc: private@struts.apache.org
```

`user@` must not appear in any header. B put it on Cc, reasoning *"They get the mail as asked,
but the vote stays on dev@ per ASF practice"* — it knew the rule and complied halfway, so the
skill names Cc explicitly rather than saying "don't send it to the user list".

Bcc rather than Cc for `private@` has a reason worth recording: on the 7.1.1 and 6.8.0 votes
`private@` was on Cc, and reply-all `+1`s landed on the private PMC list.

### 3. Draft, never send (addresses the send failure)

The skill creates a Gmail draft and stops. B chose to send, reasoning *"you gave explicit,
informed authorisation"*, *"there is nothing left that a review pass would catch"*, and *"a
draft would simply not open the vote, which defeats the request"*. Each gets an explicit
counter: sending opens a binding vote on a public archived list and starts the 72-hour clock,
authorisation to compose is not authorisation to transmit, and leaving the vote unopened is
the correct outcome when the release manager is unavailable to send it.

### 4. Frozen boilerplate (addresses the edit failure)

Everything from `Once you have had a chance to review the test build` to the sign-off is
byte-frozen. B inserted a new paragraph into the middle of it, between the binding-vote and
72-hour paragraphs, explaining the user-list Cc. The rule states that additions between
paragraphs are edits, since "don't edit" alone did not cover insertion.

### 5. The vote carries no security information, on any channel

**Release manager's rule:** a release vote carries no security information at all. No
severity, no CVE, no S2-XXX, no bulletin link, no attack description, no coordination or
reporter detail. That disclosure happens *after* the vote passes and the version is released.

Every baseline kept the `dev@` mail neutral, so the public-list half of this teaches nothing.
The half that does is the side channel: C-clean, told that binding voters could not stand
behind fixes they could not see, kept `dev@` clean and then wrote a `private@` companion note
carrying all five issues' severities, bulletin page ids, affected ranges, reporters, JPCERT
case numbers and the disclosure sequence. Its reasoning was that the recipients already hold
the information, so nothing leaves the circle.

The rule answers that directly: **the restriction is on the vote, not on the audience.** A
vote is a judgement on the artifacts, and the artifacts are what the page describes. Routing
advisory detail through `private@`, a Cc, an attachment, or a companion mail is the same
violation as putting it in the body — a second mail sent to open the vote is part of the vote.

Neutral ticket summaries carried over from the page are not security information and stay,
truncated exactly as the page truncates them.

### 6. Cross-references, not restatements

For everything the baselines already got right, the skill points at the skill that taught it
rather than repeating it:

- `creating-version-notes` — the page, the release, and what belongs on them
- `creating-security-bulletins` — what may be said about an unpublished advisory

## The template

`vote-mail-template.md` holds headers, slots, and the frozen tail. Two details are kept as
they ship rather than improved: `Github release` has no trailing colon, and all four quality
checkboxes are empty because the release manager's `+1` is a separate reply.

The opener's second sentence is authored per release from the issue list in front of you.
7.2.1's *"a few minor breaking changes plus some bug fixes. Also a lot of dependencies have
been updated"* describes 7.2.1 and is not a form to reuse — 7.3.0 has seven breaking changes
and one dependency bump, so the dependency clause would be false. Both A-clean and C-clean
caught this unprompted; the template records it so the third agent does not have to.

## Output

Gmail `create_draft` with To, Bcc, Subject and body set. Never send.

## Testing

Per `writing-skills`, the skill is verified by re-running the same three baseline scenarios
with it present. GREEN requires: no `user@` in any header, a draft rather than a send,
boilerplate byte-identical to the template, a body whose parts match the recipe exactly, and
**exactly one mail produced**, carrying no severity, CVE, S2-XXX, bulletin link or reporter
detail on any channel. Any new rationalization found in the GREEN runs is countered and the
scenarios re-run.

Baseline transcripts and outputs are kept in the session scratchpad, not committed.
