---
date: 2025-12-02T18:33:08+01:00
topic: "WW-5594: Convention plugin exclusion pattern wildcard matching issue"
tags: [research, codebase, convention-plugin, wildcard-matching, configuration, bug-analysis, WW-5594]
status: complete
jira_ticket: WW-5594
related_tickets: [WW-5593]
---

# Research: WW-5594 - Convention Plugin Wildcard Exclusion Pattern Issue

**Date**: 2025-12-02T18:33:08+01:00
**JIRA**: [WW-5594](https://issues.apache.org/jira/browse/WW-5594)
**Related**: [WW-5593](https://issues.apache.org/jira/browse/WW-5593)

## Research Question

Why doesn't the default exclusion pattern `org.apache.struts2.*` properly exclude classes directly in the `org.apache.struts2` package (like `XWorkTestCase`)?

## Summary

The convention plugin's `PackageBasedActionConfigBuilder` extracts package names from class names using `StringUtils.substringBeforeLast(className, ".")`, then matches these package names against exclusion patterns. For class `org.apache.struts2.XWorkTestCase`, this produces package name `org.apache.struts2` (no trailing dot). The pattern `org.apache.struts2.*` expects something after the dot for the `*` to match, causing a mismatch for classes directly in the root package.

This is a conceptual mismatch between patterns designed for full class names vs. matching against extracted package names.

## Detailed Findings

### 1. Package Exclusion Logic

**Location**: `plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java:558-584`

#### Package Name Extraction (Line 558-560)

```java
protected boolean includeClassNameInActionScan(String className) {
    String classPackageName = StringUtils.substringBeforeLast(className, ".");
    return (checkActionPackages(classPackageName) || checkPackageLocators(classPackageName)) && checkExcludePackages(classPackageName);
}
```

**Examples**:
- `"org.apache.struts2.core.ActionSupport"` → `"org.apache.struts2.core"`
- `"com.example.actions.UserAction"` → `"com.example.actions"`
- `"org.apache.struts2.XWorkTestCase"` → `"org.apache.struts2"` ⚠️

#### Exclusion Pattern Matching (Lines 569-584)

```java
protected boolean checkExcludePackages(String classPackageName) {
    if (excludePackages != null && excludePackages.length > 0) {
        WildcardHelper wildcardHelper = new WildcardHelper();
        Map<String, String> matchMap = new HashMap<>();

        for (String packageExclude : excludePackages) {
            int[] packagePattern = wildcardHelper.compilePattern(packageExclude);
            if (wildcardHelper.match(matchMap, classPackageName, packagePattern)) {
                return false;
            }
        }
    }
    return true;
}
```

### 2. The Conceptual Mismatch

**The Problem**:

When checking if `org.apache.struts2.XWorkTestCase` should be excluded:

1. **Step 1**: Extract package name
   - Input: `"org.apache.struts2.XWorkTestCase"`
   - `substringBeforeLast(className, ".")` → `"org.apache.struts2"`
   - No trailing dot

2. **Step 2**: Match against pattern `org.apache.struts2.*`
   - Pattern expects: `org.apache.struts2.` + `*` (something)
   - Actual input: `org.apache.struts2` (nothing after last segment)
   - **Mismatch**: Pattern requires a literal `.` that doesn't exist in the package name

**Root Cause**:
- Exclusion patterns are written for **fully qualified class names** (e.g., `org.apache.struts2.*` should match `org.apache.struts2.XWorkTestCase`)
- But matching is performed against **package names only** (e.g., `org.apache.struts2` after stripping class name)
- This creates edge cases for classes directly in a package

### 3. WildcardHelper Pattern Matching

**Location**: `core/src/main/java/org/apache/struts2/util/WildcardHelper.java`

#### Pattern Compilation Constants

From `WildcardHelper.java:42-48`:
```java
public static final int MATCH_FILE = -1;      // Single * - matches zero or more chars (excluding /)
public static final int MATCH_PATH = -2;      // Double ** - matches zero or more chars (including /)
public static final int MATCH_BEGIN = -4;     // Pattern must match from beginning
public static final int MATCH_THEEND = -5;    // Pattern must match to the end
```

#### How Pattern `org.apache.struts2.*` Compiles

The pattern compiles to:
```
[MATCH_BEGIN, 'o', 'r', 'g', '.', 'a', 'p', 'a', 'c', 'h', 'e', '.', 's', 't', 'r', 'u', 't', 's', '2', '.', MATCH_FILE, MATCH_THEEND]
```

**Matching Requirements**:
1. Must start with `org.apache.struts2.` (literal characters)
2. Must have `MATCH_FILE` (zero or more non-slash characters)
3. Must end exactly (`MATCH_THEEND`)

#### Test Evidence

From `core/src/test/java/org/apache/struts2/util/WildcardHelperTest.java:54-76`:

```java
public void testMatchStrutsPackages() {
    // given
    HashMap<String, String> matchedPatterns = new HashMap<>();
    int[] pattern = wildcardHelper.compilePattern("org.apache.struts2.*");

    // when & then
    assertTrue(wildcardHelper.match(matchedPatterns, "org.apache.struts2.XWorkTestCase", pattern));
    assertEquals("org.apache.struts2.XWorkTestCase", matchedPatterns.get("0"));
    assertEquals("XWorkTestCase", matchedPatterns.get("1"));

    assertTrue(wildcardHelper.match(matchedPatterns, "org.apache.struts2.core.SomeClass", pattern));
    assertEquals("org.apache.struts2.core.SomeClass", matchedPatterns.get("0"));
    assertEquals("core.SomeClass", matchedPatterns.get("1"));

    // IMPORTANT: Pattern matches even with nothing after dot
    assertTrue(wildcardHelper.match(matchedPatterns, "org.apache.struts2.", pattern));
    assertEquals("org.apache.struts2.", matchedPatterns.get("0"));
    assertEquals("", matchedPatterns.get("1"));
}
```

**Key Insight**: The pattern `org.apache.struts2.*` **WILL match** `org.apache.struts2.` (with trailing dot) because `MATCH_FILE` can match zero characters. However, it **WON'T match** `org.apache.struts2` (without trailing dot) because the literal `.` character before `MATCH_FILE` is required.

### 4. Why This Matters

**Scenario**: Class `org.apache.struts2.XWorkTestCase` should be excluded

**What Happens**:
1. Pattern in config: `org.apache.struts2.*`
2. Class name: `org.apache.struts2.XWorkTestCase`
3. Extracted package: `org.apache.struts2` (no trailing dot)
4. Pattern match: `"org.apache.struts2"` vs pattern `"org.apache.struts2.*"`
5. **Result**: NO MATCH (pattern expects literal `.` before the `*`)
6. Class is NOT excluded
7. Convention plugin tries to scan it
8. Triggers WW-5593 (NoClassDefFoundError)

**Contrast with subpackage classes**:
1. Class name: `org.apache.struts2.core.ActionSupport`
2. Extracted package: `org.apache.struts2.core`
3. Pattern match: `"org.apache.struts2.core"` vs pattern `"org.apache.struts2.*"`
4. **Result**: NO MATCH (pattern expects `org.apache.struts2.` + something, but we have `org.apache.struts2.core` which is a different string)

**Wait, that's also wrong!** Let me reconsider...

Actually, looking at the test more carefully:

The test shows that `"org.apache.struts2.*"` matches:
- `"org.apache.struts2.XWorkTestCase"` (full class name)
- `"org.apache.struts2.core.SomeClass"` (subpackage class name)

But in the actual code, we're matching:
- `"org.apache.struts2"` (extracted package, no class name)

So the pattern `"org.apache.struts2.*"` is designed to match **class names** like `"org.apache.struts2.XWorkTestCase"`, but it's being used to match **package names** like `"org.apache.struts2"`.

### 5. Default Configuration

**Location**: `plugins/convention/src/main/resources/struts-plugin.xml:57`

```xml
<constant name="struts.convention.exclude.packages"
          value="org.apache.struts.*,org.apache.struts2.*,org.springframework.web.struts.*,org.springframework.web.struts2.*,org.hibernate.*"/>
```

**Packages That Should Be Excluded**:
- `org.apache.struts.*` - Struts 1.x packages
- `org.apache.struts2.*` - Struts 2.x packages
- `org.springframework.web.struts.*` - Spring Struts integration
- `org.springframework.web.struts2.*` - Spring Struts 2 integration
- `org.hibernate.*` - Hibernate packages

**Problem**: Classes directly in these root packages (without subpackages) may not be properly excluded:
- `org.apache.struts2.XWorkTestCase` ❌ Not excluded
- `org.apache.struts2.StrutsConstants` ❌ Not excluded
- But: `org.apache.struts2.core.ActionSupport` - needs investigation

### 6. Email Thread Evidence

From Florian Schlittgen's debugging (2024-12-19 21:41):

```java
public static void main(String[] args) {
    String packageExclude = "org.apache.struts2.*";
    String classPackageName = "org.apache.struts2";
    WildcardHelper wildcardHelper = new WildcardHelper();
    int[] packagePattern = wildcardHelper.compilePattern(packageExclude);
    System.out.println(wildcardHelper.match(new HashMap<>(), classPackageName, packagePattern));
}
```

**Output**: `false`

The pattern `"org.apache.struts2.*"` does NOT match `"org.apache.struts2"` (without trailing dot).

From Lukasz Lenart's response (2024-12-23 19:12):

```java
// This WORKS (with trailing dot)
String classPackageName = "org.apache.struts2.";
// Output: true
```

So the pattern requires a trailing dot to match.

## Code References

- `plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java:558-584` - Package exclusion logic
- `core/src/main/java/org/apache/struts2/util/WildcardHelper.java:71-254` - Wildcard pattern matching implementation
- `core/src/test/java/org/apache/struts2/util/WildcardHelperTest.java:54-76` - Pattern matching tests
- `plugins/convention/src/main/resources/struts-plugin.xml:57` - Default exclusion configuration

## Recommended Fixes

### Option A: Update Default Configuration (Simplest)

**File**: `plugins/convention/src/main/resources/struts-plugin.xml:57`

Add both root packages and wildcard patterns to the configuration.

**Disadvantages**:
- ❌ Users with custom exclusion patterns still need to know about this
- ❌ Makes the configuration string longer
- ❌ Requires pattern duplication

### Option B: Match Against Full Class Name

Match exclusion patterns against the full class name instead of just the package name.

**Disadvantages**:
- ❌ Changes method logic significantly
- ❌ May affect performance (two wildcard checks instead of one)
- ❌ Could have unintended side effects with existing configurations

### Option C: Enhance WildcardHelper for Package Matching

Create a new method `matchPackagePattern()` that understands package-style matching.

**Disadvantages**:
- ❌ Most complex solution
- ❌ Changes core utility class
- ❌ Requires extensive testing
- ❌ Overkill for the problem

### Option D: Enhance checkExcludePackages to Handle Root Packages (IMPLEMENTED ✅)

**File**: `plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java`

Modify `checkExcludePackages()` to automatically handle patterns ending with `.*` by also checking if the package name equals the base pattern (without `.*`).

**Implementation**:
```java
protected boolean checkExcludePackages(String classPackageName) {
    if (excludePackages != null && excludePackages.length > 0) {
        WildcardHelper wildcardHelper = new WildcardHelper();
        Map<String, String> matchMap = new HashMap<>();

        for (String packageExclude : excludePackages) {
            // WW-5594: For patterns ending with ".*", also check if package equals the base
            if (packageExclude.endsWith(".*")) {
                String basePackage = packageExclude.substring(0, packageExclude.length() - 2);
                if (classPackageName.equals(basePackage)) {
                    return false;
                }
            }

            int[] packagePattern = wildcardHelper.compilePattern(packageExclude);
            if (wildcardHelper.match(matchMap, classPackageName, packagePattern)) {
                return false;
            }
        }
    }
    return true;
}
```

**Advantages**:
- ✅ No configuration duplication needed
- ✅ Backward compatible - existing configurations with `.*` patterns now work correctly
- ✅ Single point of fix in `checkExcludePackages()` method
- ✅ Intuitive behavior - `org.apache.struts2.*` now excludes both root and subpackages
- ✅ Minimal code change
- ✅ Users don't need to update their configurations

**Disadvantages**:
- ❌ Slightly more processing per pattern (trivial performance impact)

### Recommendation

**Option D** was chosen and implemented because:
1. Most elegant solution - no configuration duplication
2. Backward compatible - existing configurations work better
3. Single point of fix
4. Intuitive behavior for users
5. Minimal code change with maximum benefit

## Impact Assessment

### Current Risk

**Severity**: MEDIUM

**Scenarios Affected**:
1. Classes directly in root Struts packages (like `XWorkTestCase`)
2. Users setting `struts.convention.action.includeJars`
3. Custom exclusion patterns not including root packages

**Current Behavior**:
- ❌ Test classes in root packages not excluded
- ❌ Can trigger WW-5593 (NoClassDefFoundError)
- ❌ Users must manually add root packages to exclusions
- ❌ Non-intuitive pattern matching behavior

### After Fix

**Expected Behavior**:
- ✅ Root package classes properly excluded by default
- ✅ Reduces occurrence of WW-5593
- ✅ More intuitive default configuration
- ✅ Better documentation for custom patterns

## Testing Strategy

### Unit Tests to Add

1. **Test Root Package Exclusion**:
```java
@Test
public void testExcludeRootPackageClasses() {
    String[] excludePackages = {"org.apache.struts2", "org.apache.struts2.*"};
    builder.setExcludePackages(excludePackages);

    // Should exclude classes directly in org.apache.struts2
    assertFalse(builder.includeClassNameInActionScan("org.apache.struts2.XWorkTestCase"));
    assertFalse(builder.includeClassNameInActionScan("org.apache.struts2.StrutsConstants"));

    // Should also exclude subpackages
    assertFalse(builder.includeClassNameInActionScan("org.apache.struts2.core.ActionSupport"));
    assertFalse(builder.includeClassNameInActionScan("org.apache.struts2.dispatcher.Dispatcher"));
}
```

2. **Test Wildcard Pattern Behavior**:
```java
@Test
public void testWildcardPatternMatchingWithPackageNames() {
    WildcardHelper helper = new WildcardHelper();
    int[] pattern = helper.compilePattern("org.apache.struts2.*");

    // Package name without trailing dot should NOT match
    assertFalse(helper.match(new HashMap<>(), "org.apache.struts2", pattern));

    // Package name with trailing dot SHOULD match
    assertTrue(helper.match(new HashMap<>(), "org.apache.struts2.", pattern));

    // Full class name SHOULD match
    assertTrue(helper.match(new HashMap<>(), "org.apache.struts2.XWorkTestCase", pattern));
}
```

3. **Test Default Configuration**:
```java
@Test
public void testDefaultExclusionPatterns() {
    // Load default patterns from struts-plugin.xml
    builder.setExcludePackages(getDefaultExclusionPatterns());

    // Should exclude Struts 2 root package classes
    assertFalse(builder.includeClassNameInActionScan("org.apache.struts2.XWorkTestCase"));

    // Should exclude Struts 1 root package classes
    assertFalse(builder.includeClassNameInActionScan("org.apache.struts.Action"));

    // Should exclude Hibernate root package classes
    assertFalse(builder.includeClassNameInActionScan("org.hibernate.Session"));
}
```

### Integration Tests

1. Test scanning with `struts.convention.action.includeJars` configured
2. Verify classes in root excluded packages are not scanned
3. Verify classes in subpackages are properly excluded
4. Verify user action classes are still included

## Workaround for Users

Until the fix is released, users should update their exclusion patterns:

```xml
<constant name="struts.convention.exclude.packages"
          value="org.apache.struts2,org.apache.struts2.*,com.opensymphony.xwork2,com.opensymphony.xwork2.*" />
```

**Pattern**: For each package to exclude, include both:
- Root package without wildcard: `org.apache.struts2`
- Subpackages with wildcard: `org.apache.struts2.*`

## Documentation Updates Needed

1. **Convention Plugin Documentation**:
   - Explain wildcard pattern matching behavior
   - Provide examples of correct exclusion patterns
   - Document the difference between `org.example` and `org.example.*`

2. **Migration Guide**:
   - Note the improved default exclusions in release notes
   - Explain that custom exclusion patterns should include root packages

3. **Configuration Reference**:
   - Update `struts.convention.exclude.packages` documentation
   - Add examples showing both patterns

## Related Issues

- **WW-5593**: NoClassDefFoundError bug that this exclusion issue can trigger
- Related research: `thoughts/shared/research/2025-12-02-WW-5593-convention-plugin-noclass-exception.md`

## Email Thread Reference

**From**: Florian Schlittgen (sc...@liwa.de)
**Date**: December 19, 2024 - January 19, 2025
**Subject**: Struts 7: action class finder
**List**: dev@struts.apache.org

**Key Insight from Thread** (Florian, 2024-12-23 19:41):
> "Thanks for looking into it. Your examples/tests are alright, but I think this is not the way it is being called. Please take a look at org.apache.struts2.convention.PackageBasedActionConfigBuilder.includeClassNameInActionScan(String). While debugging I can see that this method is being called with parameter "className" = "org.apache.struts2.XWorkTestCase". In the method's first line the package name is derived from the className by using "StringUtils.substringBeforeLast(className, ".")" which gives you "org.apache.struts2", no trailing dot. And without trailing dot it is not working."

## Implementation Status

1. ✅ Create JIRA ticket WW-5594
2. ✅ Modify `checkExcludePackages()` to handle `.*` patterns for root packages (Option D)
3. ✅ Default exclusion patterns in `struts-plugin.xml` unchanged (no duplication needed)
4. ✅ Add unit tests for root package exclusion (`PackageBasedActionConfigBuilderTest.testWW5594_RootPackageExclusion`)
5. ✅ Add unit tests documenting WildcardHelper behavior (`WildcardHelperTest.testWW5594_*`)
6. ⏳ Add integration tests with includeJars configured
7. ⏳ Update documentation for pattern matching behavior
8. ⏳ Update migration guide with new default patterns

## Files Changed

- `plugins/convention/src/main/java/org/apache/struts2/convention/PackageBasedActionConfigBuilder.java` - Enhanced `checkExcludePackages()` method
- `plugins/convention/src/test/java/org/apache/struts2/convention/PackageBasedActionConfigBuilderTest.java` - Added `testWW5594_RootPackageExclusion()`
- `core/src/test/java/org/apache/struts2/util/WildcardHelperTest.java` - Added `testWW5594_WildcardPatternRequiresTrailingDot()` and `testWW5594_ExactPackagePatternMatchesPackageName()`