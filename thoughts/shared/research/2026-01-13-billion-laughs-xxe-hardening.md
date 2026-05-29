---
date: 2026-01-13T00:00:00Z
topic: "XML Entity Expansion (Billion Laughs) Hardening Analysis"
tags: [research, security, xxe, billion-laughs, DomHelper, xml-parsing, hardening]
jira: WW-5621
status: complete
severity: Low (hardening only - not an exploitable vulnerability)
---

# Research: XML Entity Expansion (Billion Laughs) Hardening Analysis

**Date**: 2026-01-13
**Jira**: [WW-5621](https://issues.apache.org/jira/browse/WW-5621)

## Research Question
Assess the scope of a reported Billion Laughs DoS concern in Apache Struts XML parsers and determine appropriate hardening measures.

## Summary

**NOT A VULNERABILITY**: While the SAX parser in `DomHelper.java` does not explicitly block DOCTYPE declarations with internal entity expansion, modern JDKs (7u45+) enforce a built-in 64,000 entity expansion limit that already prevents Billion Laughs attacks. Additionally, none of the `DomHelper.parse()` call sites accept user-supplied input — all XML sources come from the classpath.

This analysis led to **defense-in-depth hardening** and removal of an unnecessary feature that could theoretically become an attack surface if misused by custom application code.

## Why This Is Not a Vulnerability

1. **JDK protection is already in place**: Since JDK 7u45, the XML parser enforces a hard limit of 64,000 entity expansions. A Billion Laughs payload is rejected with a `SAXParseException` before causing meaningful resource consumption. This is verified by a unit test.

2. **No user-controlled input reaches the parsers**: All callers of `DomHelper.parse()` load XML exclusively from the classpath via `ClassLoader.getResources()`:
   - `XmlConfigurationProvider` — parses `struts.xml` and `<include>` files
   - `DefaultValidatorFileParser` — parses `*-validation.xml` and `*-validators.xml`

3. **The StringAdapter path was disabled by default**: `parseStringAsXML` defaulted to `false`, no subclasses of `StringAdapter` exist in the codebase, and enabling it required custom Java code across three layers (custom adapter, custom result, struts.xml registration).

## Detailed Findings

### XML Parser Configuration in DomHelper

**File:** `core/src/main/java/org/apache/struts2/util/DomHelper.java`

**Current protection:**
```java
factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
```

External entities are properly blocked. Internal entity expansion (used by Billion Laughs) is handled by the JDK's built-in limit.

### DomHelper.parse() Call Sites

| Location | What is parsed | User input? |
|----------|---------------|-------------|
| `XmlConfigurationProvider.java:173` | struts.xml + includes from classpath | No |
| `DefaultValidatorFileParser.java:94` | Action validation files from classpath | No |
| `DefaultValidatorFileParser.java:131` | Validator definitions from classpath | No |

All sources are classpath resources. An attacker would need write access to the application's classpath (WEB-INF/classes or deployed JARs) to inject a malicious XML file — at which point they already have full control of the application.

### StringAdapter.parseStringAsXML (removed)

**File:** `plugins/xslt/src/main/java/org/apache/struts2/result/xslt/StringAdapter.java`

This feature allowed a `StringAdapter` subclass to parse its string value as XML via `DomHelper.parse()`. While disabled by default, it represented unnecessary attack surface:
- No subclasses of `StringAdapter` exist anywhere in the codebase
- Enabling it required custom Java code, not just configuration
- The XSLT plugin itself is niche

**Decision**: Remove the feature entirely and deprecate the API methods for future removal.

### Tiles Plugin - DigesterDefinitionsReader

**File:** `plugins/tiles/src/main/java/org/apache/tiles/core/definition/digester/DigesterDefinitionsReader.java`

Already had good protection (external entities and DTD loading disabled). Added `FEATURE_SECURE_PROCESSING` as defense-in-depth. Only parses internal Tiles definition files from the classpath.

### XSLTResult TransformerFactory

**File:** `plugins/xslt/src/main/java/org/apache/struts2/result/xslt/XSLTResult.java`

Already properly secured:
```java
factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
```

No changes needed.

## Hardening Measures Applied

1. **Removed `parseStringAsXML` from StringAdapter** — eliminates a theoretical attack surface that could be misused by custom application code
2. **Deprecated `getParseStringAsXML()` and `setParseStringAsXML()`** — marked for removal in a future version
3. **Enabled `FEATURE_SECURE_PROCESSING` in DigesterDefinitionsReader** — defense-in-depth
4. **Added unit test** — verifies the JDK's 64K entity expansion limit rejects Billion Laughs payloads, serving as a regression guard

## Entity Expansion Impact (for reference)

Without the JDK limit, a Billion Laughs payload would cause:

| Level | Payload | Memory | Time |
|-------|---------|--------|------|
| 3 | ~500 bytes | 3 KB | 35 ms |
| 5 | ~500 bytes | 300 KB | 91 ms |
| 7 | ~500 bytes | 30 MB | 3408 ms, 1837 MB memory |

The JDK limit stops expansion at 64,000 entities, well before these levels become dangerous.

## Code References

- `core/src/main/java/org/apache/struts2/util/DomHelper.java` — XML parser with external entity protection
- `plugins/xslt/src/main/java/org/apache/struts2/result/xslt/StringAdapter.java` — removed parseStringAsXML feature
- `plugins/tiles/src/main/java/org/apache/tiles/core/definition/digester/DigesterDefinitionsReader.java` — added SECURE_PROCESSING
- `core/src/test/java/org/apache/struts2/util/DomHelperTest.java` — Billion Laughs regression test

## References

- OWASP XXE Prevention Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html
- Billion Laughs Attack: https://en.wikipedia.org/wiki/Billion_laughs_attack