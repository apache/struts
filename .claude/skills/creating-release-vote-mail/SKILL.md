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
you are in the wrong skill.

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

`private@` goes on **Bcc, not Cc**: on the 7.1.1 and 6.8.0 votes it was on Cc, and reply-all
`+1`s landed on the private PMC list.

Subject is exactly `[VOTE] Apache Struts X.Y.Z`.

## Draft it, do not send it

Create a Gmail draft with To, Bcc, Subject and body set. **Never send.**

| Rationalization | Reality |
|---|---|
| "The release manager authorised whatever I produce" | Authorisation to compose is not authorisation to transmit. |
| "Every fact is verified; review would catch nothing" | Sending is not a quality gate, it is a commitment. Verification does not confer it. |
| "A draft doesn't open the vote, which defeats the request" | Correct, and that is the right outcome when the release manager is not there to send it. |
| "The 72-hour clock is the reason for the hurry" | A vote opened on the wrong artifacts costs far more than the hours saved. |

Sending opens a binding vote on a permanently archived public list and commits the PMC to the
artifacts as staged.

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
- A new paragraph inserted into the vote boilerplate
- A quality checkbox arriving pre-ticked
- An opening sentence carried over from the previous release

## Common Mistakes

| Mistake | Reality |
|---|---|
| "Voters can't judge fixes they can't see" | They can open the restricted bulletins themselves. The vote is not the disclosure channel. |
| "It's only going to private@, so nothing leaks" | The rule is about the vote, not the audience. A companion mail is part of the vote. |
| "Cc'ing user@ keeps the vote on dev@ and still informs them" | The `[TEST]` mail informed them. Cc splits the tally. |
| "I verified everything, so I can send" | Verification earns a draft. Sending is the release manager's keystroke. |
| "I'm adding to the boilerplate, not changing it" | Insertion is editing. The vote call is byte-frozen. |
| "The release notes leave out what integrators need" | Then the page needs fixing. The mail renders the page. |
| "Last release's opening sentence fits" | It described last release. Write the one this list supports. |
