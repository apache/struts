# Security Policy

## Supported Versions

Please visit the [Releases](https://struts.apache.org/releases.html#prior-releases) page to see full information about each version 
and what potential vulnerability it can have:

| Version | Supported          |
|---------|--------------------|
| 7.x     | :white_check_mark: |
| 6.7.x   | :white_check_mark: |
| 2.5.x   | ❌ |

## Reporting New Security Issues with the Apache Struts

([original](https://struts.apache.org/security.html))

The Apache Struts project takes a very active stance in eliminating security problems
and denial of service attacks against applications using the Apache Struts framework.

**We strongly encourage folks to report such security problems to our private security mailing list first,
before disclosing them in a public forum**.

We cannot accept regular bug reports or other queries at this address, we ask that you use our
[issue tracker (JIRA)](https://issues.apache.org/jira/browse/WW) for those.

```
All mail sent to this address that does not relate to security problems in the Apache Struts source code will be ignored
```

Note that all networked servers are subject to denial of service attacks, and we cannot promise magic
workarounds to generic problems (such as a client streaming lots of data to your server or requesting
the same URL repeatedly). In general, our philosophy is to avoid any attacks that can cause the server
to consume resources in a non-linear relationship to the size of inputs.

The mailing address is: [security@struts.apache.org](mailto:security@struts.apache.org)

[General network server security tips](http://httpd.apache.org/docs/trunk/misc/security_tips.html)

[The Apache Security Team](http://www.apache.org/security/)

## Before Reporting

Before sending a vulnerability report, run through the following checks. They exist to prevent duplicate reports, public disclosure of untriaged issues,
and reports for behavior that is already documented as insecure configuration.

### 1. Read this policy

Confirm:

- which Struts versions are currently supported (see [Supported Versions](#supported-versions)),
- where reports must be sent (see [Reporting New Security Issues](#reporting-new-security-issues-with-the-apache-struts)),
- which reports do not belong on the private security list.

### 2. Read the Struts security guidelines

Review the [Struts security guidance](https://struts.apache.org/security/) and determine whether the finding is already covered by documented secure
configuration or application guidance, including but not limited to:

- Config Browser Plugin exposure,
- direct JSP access,
- `devMode` is required to exploit the vulnerability,
- `@StrutsParameter` usage and parameter annotation requirements,
- unsafe setters or getters exposed to request parameters,
- use of incoming values in localization or forced OGNL evaluation,
- raw JSP EL expressions,
- custom error pages,
- Dynamic Method Invocation and Strict Method Invocation,
- accepted and excluded parameter patterns,
- Fetch Metadata, COOP, and COEP protections,
- OGNL sandboxing, allowlists, excluded classes/packages, and OGNL Guard settings.

If the behavior is caused by an application ignoring documented security guidance, that is not an Apache Struts framework vulnerability.

### 3. Check previously disclosed vulnerabilities

Compare the finding against already disclosed Struts vulnerabilities — affected versions, impact ratings, mitigations, and fixed versions:

- [Struts security information](https://struts.apache.org/security/)
- [Prior releases and vulnerability notes](https://struts.apache.org/releases.html#prior-releases)
- [Security Bulletins (S2 series)](https://cwiki.apache.org/confluence/display/WW/Security+Bulletins)

If the finding overlaps with a known vulnerability, link to the existing bulletin, advisory, CVE, or release notes instead of drafting a new report.

## Assessment

Before drafting a report, confirm:

1. Is the affected version supported?
2. Is the behavior in Apache Struts framework code, rather than only in an application using Struts?
3. Is it already documented as insecure configuration or unsupported usage?
4. Is it a duplicate of a previously disclosed vulnerability or Security Bulletin?
5. Can the impact be demonstrated with a minimal, self-contained reproduction?

Only proceed with a private report when these answers still point to a likely new vulnerability in the framework.

## Private Report Requirements

A useful private report includes:

- affected Struts version or version range,
- affected component or module,
- required application configuration, if any,
- minimal reproduction steps,
- expected behavior,
- actual behavior,
- demonstrated security impact,
- whether authentication or special privileges are required,
- proposed fix or mitigation, if known.

Do not speculate beyond what can be demonstrated. If severity is uncertain, say so explicitly.

## Report Quality Rules

- One vulnerability per report.
- Keep reproduction steps minimal and self-contained.
- Do not include unrelated findings.
- Do not publish exploit details publicly before the Struts project has triaged the issue.
- Do not send ordinary bugs, usage questions, or generic denial-of-service concerns to the private security list.
- If the issue is not a vulnerability in Apache Struts source code, use the appropriate public support or issue channel instead.
