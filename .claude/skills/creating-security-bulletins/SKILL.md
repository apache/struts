---
name: creating-security-bulletins
description: Use when drafting, updating, or reviewing an S2-XXX security bulletin on the Struts cwiki, when preparing bulletin text ahead of a CVE request, when publishing a bulletin and announcing it to the ASF lists, or when deciding how much detail about a fixed vulnerability is safe to publish.
---

# Creating Security Bulletins

## Overview

An S2-XXX bulletin exists to tell an operator **what to upgrade and why** — not to explain the defect. Every sentence that helps a defender must be weighed against how much it helps someone building an exploit.

**Core principle:** every field is either traced to source you read this session, or a visible placeholder. Never a plausible guess.

**Process authority:** [`SECURITY.md`](../../../SECURITY.md) governs disclosure. This skill governs *what the page says and how it is written*.

**REQUIRED BACKGROUND:** the claims you put in a bulletin come from triage. Use `triaging-security-reports` to establish them before writing.

## The Iron Rule

```
NO FIELD IN A BULLETIN WITHOUT A SOURCE YOU READ THIS SESSION,
OR A VISIBLE PLACEHOLDER.
```

Applies to the severity rating, the affected versions, and above all the Workaround. "There is no workaround" is a factual claim about absence — the hardest kind to get right, and the most common thing to assert without checking.

## Page structure

Sections in order, matching the existing published bulletins:

`Summary` (in an `excerpt` macro) → field table → `Problem` → `Solution` → `Backward compatibility` → `Workaround`

Field table rows, in order:

| Row | Content |
|---|---|
| Who should read this | Usually `All Struts 2 developers and users`; narrow it only when exposure is genuinely conditional |
| Impact of vulnerability | A short impact phrase, not a paragraph — *Remote Code Execution*, *Denial of service*, *Disclosure of Data, Denial of Service, Server Side Request Forgery*. Hedging is accepted where warranted (*Possible Remote Code Execution vulnerability*) |
| Maximum security rating | Low / Moderate / Important / Critical — see the rating scale below |
| Recommendation | `Upgrade to Struts X.Y.Z at least`. Name **every** maintenance line that carries the fix (`Upgrade to Struts 6.8.0 or 7.1.1 at least`), and add the required action where upgrading alone is not enough (`… and use Action File Upload Interceptor`) |
| Affected Software | Officially released versions only (see below); bullet one range per maintenance line, linking the EOL announcement for end-of-life ranges |
| Reporters | Credit the reporter — they earned it, and it costs nothing. Include their organisation where they gave one (`Steven Seeley of Source Incite`); obfuscate any email (`pwntester at github dot com`) |
| CVE Identifier | Placeholder until assigned (see below) |

**Match the house voice — from the *recent* bulletins only.** Read the two or three most recently published ones before writing. They are far terser than a triage write-up: `Problem` is one to three sentences, and every affected feature is **linked to its page on struts.apache.org** so an operator can go straight to the documentation. Where a bulletin resembles an earlier one, the Summary says so and links it.

**Do not take the older bulletins as a precedent for how much to disclose.** Earlier advisories explained causes and mitigations in far more detail, and that detail was used to build working exploits. The project deliberately stopped. An old bulletin naming the exact construct that triggers the flaw is evidence of the practice this skill exists to prevent, not licence to repeat it — mine them for structure and tone, never for depth.

## Affected Software: released versions only

**List only versions that passed a PMC release vote.** A build that was cut, failed its test period, and was superseded never reached users as a release — listing it implies an official artifact was vulnerable and drags a phantom version into every downstream CVE record and scanner database.

Verify before writing. Do not infer the range from the tags in git: a tag exists for builds that were never voted through. Ask, or check the release announcements.

**Deriving the lower bound** — one method, both bounds:

1. Find when the vulnerable code entered, with `git log -S'<the vulnerable construct>' -- <path>`. Do not assume it arrived with the feature that made it reachable; a defect often predates the control that was supposed to bound it.
2. Map that commit to the first *release* containing it.
3. If step 2 can't be settled from what you have, write a visible placeholder naming what must be confirmed — never a guessed version number.

## The rating scale is published — apply it, don't invent one

The definitions live on **[Security Bulletins](https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=61758)** (page `61758`), and they answer one question: *how worried should I be about this vulnerability?*

**That page is the only authority.** The four-level naming was introduced comparatively recently, so bulletins published before it use other wording and inconsistent capitalisation. Never infer the vocabulary or calibrate a rating from an older bulletin — match a definition on page `61758`, and take comparisons only from advisories published since the scale existed.

| Rating | Applies when |
|---|---|
| **Critical** | A remote attacker can get Struts to execute arbitrary code — exploitable automatically, regardless of whether the developer followed the Security Guide |
| **Important** | Compromise of the application's **data or availability**; also easy RCE that depends on the developer having mistreated user input |
| **Moderate** | There is **significant mitigation**: the flaw does not affect likely configurations, or the configuration is not widely used, or the attacker must be authenticated |
| **Low** | Everything else — believed **extremely hard to exploit**, or the exploit yields minimal consequences |

Two traps in applying it:

- **Low is not "narrow".** A flaw that is trivial to trigger and causes real damage is not Low merely because a setting gates it. Reserve Low for hard-to-exploit *or* minimal-consequence.
- **The Moderate clause is "not widely used", not "opt-in".** A gate only mitigates if few deployments pass through it. S2-068 needed file upload enabled and was still rated **Important**, because file upload is ordinary. Ask how many real deployments the precondition actually excludes.
- **Availability counts as Important.** Denial of service is not automatically a lesser class — S2-068 was disk exhaustion, rated Important. It drops to Moderate only where a mitigation clause genuinely applies.

**Exploitation status belongs on the page, not in the rating.** The scale measures the flaw itself, so it has no slot for "a public reproduction already exists." When a defect was disclosed publicly before the fix shipped, or a working reproduction is already public, say so in plain words — downstream consumers are told by their own regulators to prioritise on real risk and active exploitation, not on a severity class alone. It costs nothing: the reproduction is already out.

## CVE placeholder

CVEs are requested **after** the fixed release is out and accepted. Until then the row carries a placeholder that cannot be mistaken for a real identifier:

```
CVE-YYYY-NNNNN (to be assigned before publication)
```

Never leave a cloned page's real CVE in place. Never invent a well-formed-looking number.

One CVE per independently fixable issue — separate fixes get separate bulletins and separate CVEs, per [CNA rules 4.1.10](https://www.cve.org/ResourcesSupport/AllResources/CNARules).

## The disclosure budget

**The budget covers every prose section — `Problem`, `Backward compatibility`, and `Workaround` alike.** `Problem` is the section authors guard; `Backward compatibility` is the one that leaks, because describing what changed about the fixed behaviour describes the defect. A note saying which inputs are handled differently now points straight at the code path that was rewritten. Apply the table below to all three sections, and write the BC note in terms of what an application might *observe*, never what the fix altered internally.

Write the shortest true description that lets an operator judge whether they are exposed. One to three sentences, as in the published bulletins.

| Safe to publish | Never publish before the fix is out |
|---|---|
| Impact categories and consequence | Class, method, or field names |
| The component in plain words, linked to its documentation | `file:line` references |
| Whether a configured control fails to apply | Commit hashes, PR or Jira numbers |
| That state is shared / input is unvalidated | The triggering request shape or payload |
| Which released versions are affected | Reproduction steps, PoC, timing conditions |

**Write for an operator, not a reviewer.** S2-068 describes an exploited disk-exhaustion bug in one sentence — *"If support for file upload is enabled, file leak in multipart request processing causes disk exhaustion."* That is the register: the feature, the failure, the consequence. Naming the class turns a bulletin into a starting point.

## State who is *not* affected

An operator's first question is "does this reach me?" Answer it on the page, or every reader has to assume it does.

The house form is **one sentence, linked to the feature's documentation** — S2-067 does it in a single line:

> **Note**: applications not using [FileUploadInterceptor](https://struts.apache.org/core-developers/file-upload-interceptor) are safe.

or folded into the opening clause, as S2-068 does with *"If support for file upload is enabled, …"*. Say it whenever exposure is conditional — an optional plugin the application chooses to ship, a setting that must be switched on, an endpoint that must be mapped, or an unaffected sibling path that lets a reader stop reading. Add "earlier releases are not affected" when there is a clean prior baseline.

Keep it at the level of a deployment decision ("uses the plugin", "exposes such an endpoint"), not a code path. Scoping *reduces* net disclosure: it shrinks the population that has to care, and it costs an attacker nothing they could not learn from the dependency list.

## Fix provenance

A bulletin promises a fixed release and describes post-fix behaviour as settled fact. Both claims rest on a specific change.

**Record which commit or PR each behavioural claim rests on**, in the version comment or your notes — not on the page.

**Confirm that change is merged into the release branch before publishing.** A patch under private review may be revised or dropped; a bulletin describing behaviour that never shipped is worse than a late bulletin. Bulletins are routinely drafted while the fix is still embargoed and unmerged — that is normal, and it is exactly why the merge state must be re-checked at publication time rather than at drafting time.

**Derive BC notes from the fix diff, not from its commit message.** A commit summary that calls the behaviour unchanged can still carry an observable difference its author did not think worth mentioning. Read the diff.

**`Backward compatibility` is also where a breaking upgrade is announced**, and the announcement has to be blunt. S2-067 told users the fix was *not* backward compatible, that they had to rewrite their actions onto a new mechanism, and that staying on the old one left them vulnerable. Where the fix is transparent, the house sentence is simply *"This change is backward compatible."*

## Workaround: verify or say nothing

Three valid outcomes, in order of preference:

1. **A verified configuration or operational change.** Trace it in source and confirm it actually removes reachability. Give the change, not the mechanism. It need not be a Struts setting — S2-068 offers a sized or dedicated temp volume, and pointing at the relevant section of the Security Guide is a legitimate workaround in itself.
2. **Upgrade only** — when you checked and found nothing.
3. **Verified absence.** The house value is a bare `n/a` (S2-066, S2-067); spell it out when the reason is worth stating.

Never ship a workaround you reasoned about but did not confirm. A wrong workaround leaves operators believing they are protected and discredits every other field on the page.

**The tension to decide deliberately:** a workaround usually reveals which path is affected. That is often the right trade — it is why the bulletin exists — but it is a decision to make and surface, not one to make silently. Say which way you went and why.

## Re-read the page immediately before you write to it

Bulletins are drafted by more than one person, often within the same hour. Content you read earlier may have moved on — a backport range added, a placeholder resolved, a section rewritten.

**Fetch the current version immediately before every write, and compare the returned version number against the one you read.** If it advanced, re-read, merge your change onto the newer content, and write that. Writing from a stale copy silently discards someone else's work with no warning and no conflict error.

After writing, diff your new version against the one you meant to build on. The diff should show only your intended change. If it shows deletions you did not intend, restore from history and redo the edit on top.

## Restrictions

Bulletins stay restricted until the coordinated publication date.

**Check restrictions before the edit and again after.** An API update should not disturb them, but "should not" is not verification, and an accidentally public pre-release bulletin is an unrecoverable disclosure.

Expected on the Struts wiki: read and update limited to the author plus `struts-committers`.

Publication is clearing them **completely** — read *and* update, both empty, matching every
already-published bulletin. Verify with an unauthenticated fetch of the public URL, not with the
API's response: the tool reporting success is not the page being readable.

## Announcing it: the mail is text/plain, or it does not arrive

Once the page is public the advisory goes to the lists. **The mail carries one `text/plain`
part and nothing else.** A `text/html` part is a delivery failure — `announce@apache.org`
rejects it permanently:

```
ezmlm-reject: fatal: Sorry, a message part has an unacceptable MIME Content-Type: 'text/html' (#5.2.3)
```

**Do not rely on a bounce to catch it.** On the S2-070 run, 2026-08-14, one send was rejected
by `announce@apache.org` and *accepted* by `user@struts.apache.org`. The HTML advisory reached
the user list. A partial failure looks like success in the Sent folder.

Two unrelated defects bounced that morning, each from a different list:

| Defect | What the list says |
|---|---|
| A `text/html` part | `unacceptable MIME Content-Type: 'text/html' (#5.2.3)` |
| Wrong sender identity | `Must be sent from an @apache.org address.` |

**The CVE tool generates both mails — use them.** Each record on `cveprocess.apache.org` has an
*OSS/ASF Emails* tab holding a finished `oss-security` mail and a finished ASF-lists mail, built
from the record's own affected ranges, description, credit and references, with send buttons
that go through ASF infrastructure rather than a personal mailbox. Copying that text is how the
mail stays consistent with the CVE record; composing a fresh one is how the two drift.

If you draft in Gmail instead, the deliverable is three things and is incomplete without any:

1. A draft with To, Bcc, Subject and `body`. **Never `htmlBody`** — and passing `body` alone
   does not make the mail plain text; Gmail generates the HTML part itself on send.
2. The identical body in a file, whose path you hand over, hard-wrapped at 72 columns.
3. The sending instruction in your handover: **plain-text mode on** (⋮ → *Plain text mode*),
   paste the file over the body, send from the `@apache.org` identity.

The `oss-security` copy is a separate mail with no Cc and no Bcc — not the ASF mail with an
extra recipient.

**Recipients are not interchangeable.** The tool's ASF mail addresses `announce@apache.org` and
`dev@`; Struts practice adds `user@struts.apache.org`, which is the list operators actually
read. `announcements@struts.apache.org` takes only `@apache.org` senders.

## Start from the template, never from a previous bulletin

**[`bulletin-template.md`](bulletin-template.md)** — the field reference, per-section guidance, pre-publication checklist, and a storage-format skeleton ready to POST to the Confluence API. **It is the source of truth.**

A rendered copy exists on the wiki as a restricted child of *Security Bulletins* for authors who prefer to copy a page; when the two disagree, fix the wiki page from the file. Whichever route you take, confirm the new page carries the same restrictions before typing anything into it, and give the `excerpt` macro a fresh `ac:macro-id` — a copied page inherits the template's, and two bulletins must not share one.

**If you inherit a page cloned from a previous bulletin instead**, assume every field is inherited and wrong until you have replaced it. The residue that survives a careless edit:

- The previous bulletin's real CVE identifier
- Its affected versions, rating, and reporter credit
- Its workaround — describing a mitigation for an unrelated defect
- The `excerpt` macro's `ac:macro-id`, now **duplicated across two pages** — generate a fresh UUID

Read the whole page and rewrite it; do not patch the fields you happen to notice.

## Red Flags — STOP

- About to write a Workaround you have not traced in source
- About to write "no workaround exists" without having looked
- Affected Software copied from a git tag list rather than confirmed releases
- A CVE number on the page that you did not receive from the CVE assignment process
- Naming a class, method, or file in `Problem` "because it's already public in the PR"
- Copying the disclosure depth of an older bulletin — that depth is the reason this budget exists
- Calibrating a rating against a bulletin published before the four-level scale existed
- Guarding `Problem` carefully and then describing the fix's internals in `Backward compatibility`
- Writing a BC note from the fix's commit message without reading the diff
- Publishing while the fix is still unmerged, or without re-checking that it landed
- No statement of who is *not* affected, when exposure depends on a plugin or an opt-in setting
- Writing a page from content you read earlier in the session without re-fetching it first
- Publishing without re-checking restrictions
- Treating an API success as proof the page is publicly readable
- `htmlBody` passed to the draft tool, for any reason
- An announcement composed from scratch when the CVE record's *OSS/ASF Emails* tab holds one
- A draft handed over without the plain-text-mode instruction and the body file
- A severity rating chosen by feel, or by reachability alone, without checking it against the published scale
- Rating something Low because the feature is opt-in — opt-in is the definition of Moderate

## Common Mistakes

| Mistake | Reality |
|---|---|
| "The PR is public, so detail costs nothing" | A bulletin is indexed, permanent, and read by people who never see the PR. Aggregation is the harm. |
| "An older bulletin explained the cause in detail" | Those explanations were used to build exploits. The practice was stopped deliberately — don't restore it. |
| "An older bulletin rated something like this X" | The rating scale postdates it. Match a definition on page 61758 instead. |
| "Listing the failed build is more honest" | It is less accurate. That build was never a release; listing it misdirects every downstream consumer. |
| "Disabling the feature is an obvious workaround" | Obvious ≠ verified. Confirm the feature is genuinely on the only reachable path. |
| "The rating is roughly right" | Ratings drive upgrade urgency. Read the published definitions and match one, don't approximate. |
| "It needs an opt-in feature, so it's Low" | That is the Moderate mitigation clause. Low means hard to exploit or minimal consequence. |
| "Restrictions were set when the page was created" | Verify after every edit. The cost of being wrong once is total. |
| "I'll fill in the CVE later" | Only if the placeholder is unmistakable. A blank or a stale number ships as fact. |
| "The BC note is just a compatibility courtesy" | It describes what the fix changed, which describes the defect. Same budget as `Problem`. |
| "The commit message says behaviour is unchanged" | Commit summaries understate. Read the diff and decide for yourself. |
| "Naming the plugin narrows it for an attacker too" | They can read your dependency list. Scoping spares every operator who isn't exposed. |
| "The patch is reviewed, so the release will contain it" | Reviewed is not merged. Re-check at publication, not at drafting. |
| "Copying the last bulletin is quicker than the template" | It is how another advisory's CVE ships on your page. Copy the template. |
| "I read the page a few minutes ago" | Someone else may have written to it since. Re-fetch, then write. There is no conflict warning. |
| "I passed `body`, not `htmlBody`, so it's plain text" | Gmail generates the HTML part itself on send. The format is decided in the compose window. |
| "It reached the lists, so the format was fine" | One list accepted the same message another rejected. Check every recipient, not the Sent folder. |
| "Writing the mail myself is quicker than opening the CVE tool" | The tool's text is generated from the record. Hand-written text is how the mail and the CVE drift apart. |
