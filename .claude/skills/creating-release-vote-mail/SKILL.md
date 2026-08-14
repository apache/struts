---
name: creating-release-vote-mail
description: Use when opening the formal release vote for a Struts release candidate on any maintenance line (6.x, 7.x) - composing and drafting the [VOTE] Apache Struts X.Y.Z mail to dev@ once the Version Notes page, GitHub release and staged artifacts are published.
---

# Creating a Release Vote Mail

## Overview

The `[VOTE]` mail opens the formal release vote. It is four links wrapped in frozen ASF
boilerplate, around a plain-text rendering of the release's Version Notes page.

**Core principle:** the mail is a *rendering* of the page, not a second account of the release.

**This is the step after `creating-version-notes`.** That skill produces the page, the GitHub
release and the `[TEST]` announcement; this one consumes all three. If they do not exist yet,
you are in the wrong skill. `releasing-struts` holds the surrounding phases and what happens
once the vote passes.

[`vote-mail-template.md`](vote-mail-template.md) is the source of truth for the artifact.

## The mail is exactly these parts, in this order

1. The two-sentence opener
2. The page's `Breaking changes`, `Deprecations` and `Rejected requests`, where present
3. The page's issue-type sections, in page order
4. The four link lines
5. The vote boilerplate
6. The sign-off

**A part not on this list is not in the mail, and one mail is produced, not two.** Every
section is the page's content; the opener is the only prose you write.

The pull here is toward helpfulness — an upgrade-notes section derived from the fix commits, a
summary of what changed for integrators, a companion note to a subset of recipients. All of it
is real work that belongs somewhere else. A vote is a judgement on the staged artifacts, and
the page is what describes them.

## The vote carries no security information

No severity, no CVE, no S2-XXX, no bulletin link, no attack description, no reporter or
coordination detail. **That disclosure happens after the vote passes and the version is
released.**

**The restriction is on the vote, not on the audience.** Routing advisory detail through
`private@`, a Cc, an attachment or a companion mail is the same violation as putting it in the
body — a second mail sent to open the vote is part of the vote. "The recipients already hold
this information" is not an exemption; the vote is simply not the vehicle.

Neutral ticket summaries carried from the page are not security information. Keep them exactly
as the page has them, including where the page truncates one at a clause boundary.

**REQUIRED BACKGROUND:** `creating-security-bulletins` governs what may be said, and when.

## Recipients

```
To:  dev@struts.apache.org
Bcc: private@struts.apache.org
```

**`user@struts.apache.org` must not appear in any header — not To, not Cc, not Bcc.** The
`[TEST]` mail one step earlier goes to `dev@` and `user@`, which is right for it: it asks
people to test. This mail asks people to *vote*, and a vote invitation on the user list
solicits votes that are not binding and scatters the tally across two lists.

Cc is not a compromise. If a release manager asks you to include the user list, the answer is
that the `[TEST]` mail already did.

**`private@` is on the mail for reach, not for confidentiality.** Not every PMC member follows
`dev@`, and PMC votes are the binding ones, so `private@` is what guarantees the binding voters
see the call. Nothing goes there that could not go to `dev@` — its presence is a delivery
decision, and it is not an exemption from the rule above.

It goes on **Bcc, not Cc**: on the 7.1.1 and 6.8.0 votes it was on Cc, and reply-all `+1`s
landed on the private PMC list. Bcc gives the same reach while keeping the tally in one thread
on `dev@`.

Subject is exactly `[VOTE] Apache Struts X.Y.Z`.

## Draft it, do not send it

Create a Gmail draft with To, Bcc, Subject and body set, and write the same body to a file whose
path you hand over. **Never send.**

| Rationalization | Reality |
|---|---|
| "The release manager authorised whatever I produce" | Authorisation to compose is not authorisation to transmit. |
| "Every fact is verified; review would catch nothing" | Sending is not a quality gate, it is a commitment. Verification does not confer it. |
| "A draft doesn't open the vote, which defeats the request" | Correct, and that is the right outcome when the release manager is not there to send it. |
| "The 72-hour clock is the reason for the hurry" | A vote opened on the wrong artifacts costs far more than the hours saved. |

Sending opens a binding vote on a permanently archived public list and commits the PMC to the
artifacts as staged.

### The mail must be text/plain, and no tool argument achieves that

An ASF list mail carries **one `text/plain` part and nothing else**. A `text/html` part is a
delivery failure, not a cosmetic one — `announce@apache.org` rejects it permanently:

```
ezmlm-reject: fatal: Sorry, a message part has an unacceptable MIME Content-Type: 'text/html' (#5.2.3)
```

**A draft created through the Gmail tool is an HTML draft, whatever you pass it.** Gmail
synthesises a `text/html` alternative when the draft is sent, linkifies every URL into
`<a href>`, and reflows the plain part:

| Body passed as | What is actually sent |
|---|---|
| `body` only | `multipart/alternative` — the HTML part is generated for you |
| `htmlBody` only | HTML-only, no plain part at all |
| both | Same, plus the plain part's visible text becomes the wrapped URL |

Gmail's linkifier also rewrites URLs server-side, so link lines can arrive as
`https://www.google.com/url?q=...&source=gmail&ust=...`.

**Do not rely on a bounce to catch this.** On the 2026-08-14 advisory run the same message was
rejected by `announce@apache.org` and accepted by `user@struts.apache.org` — the HTML mail
reached one list and not the other, from a single send.

So the deliverable is three things, and it is incomplete without any of them:

1. A Gmail draft with To, Bcc, Subject and `body`. **Never `htmlBody`.**
2. The identical body written to a file, whose path you hand over.
3. In your handover, the sending instruction: **switch the compose window to plain-text mode**
   (⋮ → *Plain text mode*), then select-all and paste the file over the body.

Step 3 is what actually produces the plain-text mail; steps 1 and 2 only make it one paste
instead of four hand-edited URLs. Hard-wrap the file at 72 columns — a paste into plain-text
mode keeps the wrapping the file has, and Gmail reflows anything longer.

**Say which identity to send from: `@apache.org`.** The Gmail account's default sender is a
personal address, and a vote arriving in the `dev@` archive from one reads as an outsider
calling a PMC vote. Some ASF lists refuse it outright — `announcements@struts.apache.org`
answers *"Must be sent from an @apache.org address."*

**Never re-run the draft-update tool on a draft whose links have already been fixed by hand** —
it re-mangles them. A draft the release manager has corrected is finished; leave it alone.

## The boilerplate is frozen

Everything from `Once you have had a chance to review the test build` to the sign-off is
byte-identical to the template. **Inserting a paragraph between existing ones is an edit** —
that is how it actually gets broken, not by rewording.

If something about this release needs explaining to voters, it belongs in the opener, above
the vote call. The vote call itself says the same thing every release, which is what lets a
voter skim to the checkboxes.

## What this skill does not restate

Cross-references, not copies:

- `creating-version-notes` — the page, the GitHub release, the `[TEST]` mail, and what belongs
  on them. The issue list, Breaking changes wording and ticket reconciliation are settled
  there; render what the page says.
- `creating-security-bulletins` — what may be said about an unpublished advisory.

Before drafting, confirm all four links resolve and the GitHub release is still a prerelease.
A vote opened on a 404 burns the window before anyone can test.

## Red Flags — STOP

- Any part in the mail that is not on the six-item list
- A second mail produced alongside the vote
- Severity, CVE, S2-XXX, bulletin link or reporter detail anywhere, on any channel
- `user@struts.apache.org` in any header, including Cc
- Sending rather than drafting
- `htmlBody` passed to the draft tool, for any reason
- A draft handed over without the plain-text-mode instruction and the body file
- A new paragraph inserted into the vote boilerplate
- A quality checkbox arriving pre-ticked
- An opening sentence carried over from the previous release
- Re-running the draft-update tool on a draft whose links were already fixed by hand

## Common Mistakes

| Mistake | Reality |
|---|---|
| "Voters can't judge fixes they can't see" | They can open the restricted bulletins themselves. The vote is not the disclosure channel. |
| "It's only going to private@, so nothing leaks" | The rule is about the vote, not the audience. A companion mail is part of the vote. |
| "private@ is on the mail already, so it's a channel I can use" | It is there so binding voters see the call, not to carry anything `dev@` cannot. |
| "Cc'ing user@ keeps the vote on dev@ and still informs them" | The `[TEST]` mail informed them. Cc splits the tally. |
| "I verified everything, so I can send" | Verification earns a draft. Sending is the release manager's keystroke. |
| "I'm adding to the boilerplate, not changing it" | Insertion is editing. The vote call is byte-frozen. |
| "The release notes leave out what integrators need" | Then the page needs fixing. The mail renders the page. |
| "Last release's opening sentence fits" | It described last release. Write the one this list supports. |
| "I passed `body`, not `htmlBody`, so it's plain text" | Gmail generates the HTML part itself. The format is set in the compose window. |
| "It went through last time, so HTML is tolerated" | Lists differ. One accepted the same message the other rejected. |
