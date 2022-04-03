# Security Policy

## Supported Versions

Please vist the [Releases](https://struts.apache.org/releases.html#prior-releases) page to see full information about each version 
and what potential vulnerability it can have:

| Version | Supported          |
| ------- | ------------------ |
| 2.5.20  | :white_check_mark: |
| 2.3.37  | :white_check_mark: |

## Reporting New Security Issues with thr Apache Struts

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
workarounds to generic problems (such as a client streaming lots of data to your server, or re-requesting
the same URL repeatedly). In general our philosophy is to avoid any attacks which can cause the server
to consume resources in a non-linear relationship to the size of inputs.

The mailing address is: [security@struts.apache.org](mailto:security@struts.apache.org)

[General network server security tips](http://httpd.apache.org/docs/trunk/misc/security_tips.html)

[The Apache Security Team](http://www.apache.org/security/)
