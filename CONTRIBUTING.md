<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
# Contributing to Apache Struts

Thanks for your interest in contributing! Apache Struts is maintained by a
community of volunteers under the [Apache Software Foundation](https://www.apache.org/).
This guide walks a first-time contributor from a fresh clone to a merged pull
request. You do not need to be a committer to contribute — anyone can open a PR.

## Getting help

- **Mailing lists:** Subscribe and ask on the developer or user list — see
  <https://struts.apache.org/mail.html>. The developer list is the best place
  to discuss a change before you start larger work.
- **Issue tracker:** [JIRA WW project](https://issues.apache.org/jira/projects/WW).
- **Homepage & docs:** <https://struts.apache.org/>.

If you are unsure whether a change is wanted, ask on the developer list or
comment on the relevant JIRA issue first.

## Project overview

Apache Struts is a mature MVC web framework for Java (originally WebWork 2). It
uses OGNL for value-stack expressions and FreeMarker for UI tag templates. The
repository is a multi-module Maven build:

| Module     | Responsibility                                              |
|------------|-------------------------------------------------------------|
| `core`     | `struts2-core` — the main framework                         |
| `plugins`  | Plugin modules (json, rest, spring, tiles, velocity, …)     |
| `apps`     | Sample applications (showcase, rest-showcase)               |
| `assembly` | Distribution packaging                                      |
| `bom`      | Bill of Materials for dependency management                 |
| `parent`   | Parent POM with shared configuration                        |
| `jakarta`  | Jakarta EE compatibility modules                            |

The request lifecycle is `Dispatcher` → `ActionProxy` → `ActionInvocation` →
interceptor stack → `Action` → `Result`.

## Prerequisites & building

- **JDK 17** and **Maven**.
- Run the tests (skipping assembly for speed):

  ```bash
  mvn test -DskipAssembly
  ```

- Run a single test in a specific module:

  ```bash
  mvn test -DskipAssembly -pl core -Dtest=MyClassTest#testMethodName
  ```

- Build against the Jakarta EE 11 / Spring 7 profile:

  ```bash
  mvn clean install -Pjakartaee11
  ```

Tests use JUnit 5 with AssertJ assertions and Mockito for mocking.

## Finding something to work on

Browse the [JIRA WW project](https://issues.apache.org/jira/projects/WW) for
open issues. Comment on an issue to let others know you are working on it. If
no ticket exists for your change, **file one first** — every commit and pull
request must reference a `WW-XXXX` ticket ID.

## Development workflow

1. Fork the repository and clone your fork.
2. Create a branch off `main` named after the ticket, e.g. `WW-1234-short-description`.
3. Implement your change **with tests**. Keep commits focused.
4. Prefix every commit message with the ticket ID: `WW-1234 Describe the change`.
5. Run `mvn test -DskipAssembly` and make sure it passes before opening a PR.

## Submitting a pull request

- **Title format:** `WW-XXXX Description` (the JIRA ticket ID is required).
- **Link the ticket** in the description:
  `Fixes [WW-XXXX](https://issues.apache.org/jira/browse/WW-XXXX)`.
- Continuous integration must pass, and reviewers expect code changes to come
  with tests.

## Reporting security issues

**Do not** open a public GitHub issue, JIRA issue, pull request, or
mailing-list thread for a suspected vulnerability. Report it privately to
**security@struts.apache.org**. See [`SECURITY.md`](SECURITY.md) for the full
process. This includes OGNL injection, parameter-filtering bypasses, file
upload exploits, authentication bypass, RCE, SSRF, path traversal,
deserialization, and XSS in framework components.

## Licensing & Code of Conduct

- Apache Struts is licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
- Every new source file must include the standard ASF license header (see any
  existing source file or this file's header for the exact text).
- By submitting a pull request you agree to license your contribution under the
  Apache License 2.0. The ASF does not require a separate signed CLA for typical
  contributions.
- All participation is governed by the
  [ASF Code of Conduct](https://www.apache.org/foundation/policies/conduct.html).
