---
date: 2025-11-26T20:15:00+01:00
topic: "Complete HTML5 Theme Test Coverage"
ticket: "WW-5444"
tags: [plan, struts, html5-theme, testing, themes, unit-tests]
status: draft
complexity: low
estimated_effort: "3-4 hours (2-3 hours implementation + 1 hour integration tests)"
---

# Implementation Plan: Complete HTML5 Theme Test Coverage

## Overview

- **Goal**: Achieve 100% unit test coverage for HTML5 theme by adding tests to the 5 remaining test classes and implementing integration tests
- **Scope**: Add HTML5-specific test methods to FormTagTest, AnchorTest, ActionErrorTagTest, ActionMessageTagTest, and FieldErrorTagTest, plus create integration tests
- **Success Criteria**:
  - All 18 UI tag test classes have HTML5 theme tests (100% coverage)
  - All tests pass with `mvn test -DskipAssembly`
  - Integration test validates HTML5 theme in showcase application
- **Timeline**: 3-4 hours across 2 phases

## Current State Analysis

### Existing Implementation

From the validation report (`thoughts/shared/validation/2025-11-26-WW-5444-html5-theme-validation.md`):

- ✅ **13 of 18 test classes** have HTML5 theme tests (72% completion)
- ✅ **HTML5 theme** successfully moved to `core/src/main/resources/template/html5/` (42 templates)
- ✅ **All implemented tests pass** (100% pass rate on 82 tests)
- ❌ **5 test classes missing** HTML5 tests
- ❌ **No integration tests** implemented

### Missing Test Classes

1. **FormTagTest** - 58 tests, large complex test class
2. **AnchorTest** - 15 tests, medium complexity
3. **ActionErrorTagTest** - 12 tests, small class
4. **ActionMessageTagTest** - 12 tests, small class
5. **FieldErrorTagTest** - 24 tests, medium complexity

### Critical Finding: Testing Pattern Difference

**Important**: The 5 missing test classes do NOT use the `verifyGenericProperties()` pattern used by the 13 completed test classes. Instead, they use **functional/scenario-based testing** with the `verify()` method against expected output files.

This means we cannot simply copy the 4-method generic pattern. Instead, we need to add HTML5-specific functional tests to each class.

### HTML5 Templates Confirmed

All 5 components have HTML5 templates available:
- ✅ `core/src/main/resources/template/html5/form.ftl` (74 lines, medium complexity)
- ✅ `core/src/main/resources/template/html5/a.ftl` (20 lines, minimal - empty template)
- ✅ `core/src/main/resources/template/html5/actionerror.ftl` (42 lines, low-medium complexity)
- ✅ `core/src/main/resources/template/html5/actionmessage.ftl` (42 lines, low-medium complexity)
- ✅ `core/src/main/resources/template/html5/fielderror.ftl` (75 lines, high complexity)

## Desired End State

### Target Test Coverage

**Unit Tests:**
- 18 of 18 test classes with HTML5 theme tests (100% coverage)
- Estimated 20-30 new test methods across 5 classes
- All tests pass: `mvn test -DskipAssembly`
- 100% pass rate maintained

**Integration Tests:**
- New `Html5TagExampleTest.java` in showcase module
- Validates HTML5 theme rendering end-to-end
- Tests form rendering, error display, and clean HTML5 markup

### Quality Standards

- Follow existing test patterns in each class
- Create expected output files for HTML5 theme
- Test both default and clearTagStateSet variants
- Ensure consistent naming conventions

## Implementation Approach

### Phase Breakdown

#### Phase 1: Unit Test Implementation (2-3 hours)

**Objective**: Add HTML5 theme tests to all 5 missing test classes

**Approach**: Add functional tests for HTML5 theme following the existing pattern in each test class

**Sub-phases:**

##### Phase 1.1: Error/Message Display Tags (1 hour)
**Priority**: High | **Risk**: Low | **Effort**: 1 hour

Start with the simpler error and message display tags:
1. ActionErrorTagTest (12 existing tests)
2. ActionMessageTagTest (12 existing tests)
3. FieldErrorTagTest (24 existing tests)

**Pattern to Follow**: Each class uses `verify()` method with expected output files

##### Phase 1.2: Navigation Tags (30 minutes)
**Priority**: High | **Risk**: Low | **Effort**: 30 minutes

4. AnchorTest (15 existing tests)

**Note**: The `a.ftl` template is empty (only license header), so HTML5 tests may produce identical output to parent theme

##### Phase 1.3: Form Tags (1 hour)
**Priority**: High | **Risk**: Medium | **Effort**: 1 hour

5. FormTagTest (58 existing tests)

**Risk**: Largest and most complex test class with many scenarios

#### Phase 2: Integration Test Implementation (1 hour)

**Objective**: Create end-to-end integration test for HTML5 theme

**Approach**: Create `Html5TagExampleTest.java` using HtmlUnit in showcase module

**Tests to implement:**
- HTML5 form rendering validation
- Error message display with HTML5 theme
- Clean HTML5 markup (no table-based layouts)
- Dynamic attributes functionality

## Detailed Implementation Steps

### Phase 1.1: ActionErrorTagTest, ActionMessageTagTest, FieldErrorTagTest

#### ActionErrorTagTest Implementation

**File**: `core/src/test/java/org/apache/struts2/views/jsp/ui/ActionErrorTagTest.java`

**Current Structure**: 12 tests using functional verification pattern

**Add These Tests**:

1. **testWithActionErrors_html5** - Test error rendering with HTML5 theme
2. **testWithActionErrors_html5_clearTagStateSet** - Test with tag state clearing
3. **testWithoutActionErrors_html5** - Test with no errors
4. **testWithEscape_html5** - Test HTML escaping behavior

**Implementation Pattern** (based on existing tests lines 87-104):

```java
public void testWithActionErrors_html5() throws Exception {
    ActionErrorTag tag = new ActionErrorTag();
    ((InternalActionSupport) action).addActionError("action error 1");
    ((InternalActionSupport) action).addActionError("action error 2");
    tag.setPageContext(pageContext);
    tag.doStartTag();
    tag.doEndTag();

    // Set theme for expected file
    verify(ActionErrorTagTest.class.getResource("ActionErrorTag-3-html5.txt"));
}

public void testWithActionErrors_html5_clearTagStateSet() throws Exception {
    ActionErrorTag tag = new ActionErrorTag();
    tag.setPerformClearTagStateForTagPoolingServers(true);
    ((InternalActionSupport) action).addActionError("action error 1");
    ((InternalActionSupport) action).addActionError("action error 2");
    tag.setPageContext(pageContext);
    tag.doStartTag();
    tag.doEndTag();

    verify(ActionErrorTagTest.class.getResource("ActionErrorTag-3-html5.txt"));
}
```

**Expected Output Files to Create**:
- `ActionErrorTag-3-html5.txt` - Expected HTML5 output for action errors

**Estimated Effort**: 30 minutes (4 tests + 2 expected output files)

---

#### ActionMessageTagTest Implementation

**File**: `core/src/test/java/org/apache/struts2/views/jsp/ui/ActionMessageTagTest.java`

**Current Structure**: 12 tests (mirrors ActionErrorTagTest)

**Add These Tests** (same pattern as ActionErrorTagTest):

1. **testWithActionMessages_html5**
2. **testWithActionMessages_html5_clearTagStateSet**
3. **testWithoutActionMessages_html5**
4. **testWithEscape_html5**

**Implementation Pattern**: Identical to ActionErrorTagTest, replacing error methods with message methods

**Expected Output Files to Create**:
- `ActionMessageTag-3-html5.txt` - Expected HTML5 output for action messages

**Estimated Effort**: 20 minutes (4 tests + 2 expected output files, can copy pattern from ActionErrorTagTest)

---

#### FieldErrorTagTest Implementation

**File**: `core/src/test/java/org/apache/struts2/views/jsp/ui/FieldErrorTagTest.java`

**Current Structure**: 24 tests with field-specific error filtering

**Add These Tests**:

1. **testWithFieldErrors_html5** - Test field error rendering
2. **testWithFieldErrors_html5_clearTagStateSet** - With tag state clearing
3. **testWithFieldErrorsAndParamTag_html5** - Test field filtering with ParamTag
4. **testWithoutFieldErrors_html5** - Test with no errors
5. **testWithSpecificFieldName_html5** - Test fieldName attribute

**Implementation Pattern** (based on existing tests lines 110-129):

```java
public void testWithFieldErrors_html5() throws Exception {
    FieldErrorTag tag = new FieldErrorTag();
    ((InternalAction) action).addFieldError("field1", "field error 1");
    ((InternalAction) action).addFieldError("field2", "field error 2");
    tag.setPageContext(pageContext);
    tag.doStartTag();
    tag.doEndTag();

    verify(FieldErrorTagTest.class.getResource("FieldErrorTag-3-html5.txt"));
}

public void testWithFieldErrorsAndParamTag_html5() throws Exception {
    FieldErrorTag tag = new FieldErrorTag();
    ((InternalAction) action).addFieldError("field1", "field error 1");
    ((InternalAction) action).addFieldError("field2", "field error 2");

    // Add ParamTag to filter specific field
    ParamTag paramTag = new ParamTag();
    paramTag.setPageContext(pageContext);
    paramTag.setValue("field1");
    tag.addParameter("name", paramTag);

    tag.setPageContext(pageContext);
    tag.doStartTag();
    tag.doEndTag();

    verify(FieldErrorTagTest.class.getResource("FieldErrorTag-filtered-html5.txt"));
}
```

**Expected Output Files to Create**:
- `FieldErrorTag-3-html5.txt` - Expected HTML5 output for field errors
- `FieldErrorTag-filtered-html5.txt` - Expected output for filtered field errors

**Estimated Effort**: 40 minutes (5 tests + 3 expected output files)

---

### Phase 1.2: AnchorTest

#### AnchorTest Implementation

**File**: `core/src/test/java/org/apache/struts2/views/jsp/ui/AnchorTest.java`

**Current Structure**: 15 tests for anchor tag functionality

**Add These Tests**:

1. **testSimpleHref_html5** - Basic anchor with href
2. **testSimpleHref_html5_clearTagStateSet** - With tag state clearing
3. **testWithDynamicAttributes_html5** - Test dynamic attributes
4. **testWithBody_html5** - Test body content
5. **testDisabled_html5** - Test disabled anchor

**Implementation Pattern** (based on existing tests lines 76-87):

```java
public void testSimpleHref_html5() throws Exception {
    AnchorTag tag = new AnchorTag();
    StrutsMockHttpServletRequest request = (StrutsMockHttpServletRequest) pageContext.getRequest();

    tag.setPageContext(pageContext);
    tag.setTheme("html5");
    tag.setHref("/some-url");
    tag.setId("myAnchor");
    tag.doStartTag();
    tag.doEndTag();

    verifyResource("AnchorTag-1-html5.txt");
}

public void testWithDynamicAttributes_html5() throws Exception {
    AnchorTag tag = new AnchorTag();

    tag.setPageContext(pageContext);
    tag.setTheme("html5");
    tag.setHref("/some-url");
    tag.setDynamicAttribute(null, "data-toggle", "modal");
    tag.setDynamicAttribute(null, "data-target", "#myModal");
    tag.doStartTag();
    tag.doEndTag();

    verifyResource("AnchorTag-dynamic-html5.txt");
}
```

**Expected Output Files to Create**:
- `AnchorTag-1-html5.txt` - Basic anchor HTML5 output
- `AnchorTag-dynamic-html5.txt` - Anchor with dynamic attributes
- `AnchorTag-disabled-html5.txt` - Disabled anchor output

**Note**: Since `a.ftl` is empty, HTML5 output will likely match parent theme (xhtml)

**Estimated Effort**: 30 minutes (5 tests + 3 expected output files)

---

### Phase 1.3: FormTagTest

#### FormTagTest Implementation

**File**: `core/src/test/java/org/apache/struts2/views/jsp/ui/FormTagTest.java`

**Current Structure**: 58 tests covering extensive form tag scenarios

**Strategy**: Focus on key scenarios rather than duplicating all 58 tests

**Add These Tests** (priority scenarios):

1. **testSimpleForm_html5** - Basic form with action
2. **testSimpleForm_html5_clearTagStateSet** - With tag state clearing
3. **testFormWithValidation_html5** - Form with validation enabled
4. **testFormWithMethod_html5** - Form with method (GET/POST)
5. **testFormWithEnctype_html5** - Form with enctype for file upload
6. **testFormWithDynamicAttributes_html5** - Form with HTML5 data attributes

**Implementation Pattern** (based on existing tests lines 83-98):

```java
public void testSimpleForm_html5() throws Exception {
    FormTag tag = new FormTag();
    tag.setPageContext(pageContext);
    tag.setTheme("html5");
    tag.setAction("testAction");
    tag.setMethod("post");

    tag.doStartTag();
    tag.doEndTag();

    verify(FormTag.class.getResource("Formtag-1-html5.txt"));
}

public void testFormWithValidation_html5() throws Exception {
    FormTag tag = new FormTag();
    tag.setPageContext(pageContext);
    tag.setTheme("html5");
    tag.setAction("testAction");
    tag.setValidate("true");

    tag.doStartTag();
    tag.doEndTag();

    verify(FormTag.class.getResource("Formtag-validate-html5.txt"));
}

public void testFormWithEnctype_html5() throws Exception {
    FormTag tag = new FormTag();
    tag.setPageContext(pageContext);
    tag.setTheme("html5");
    tag.setAction("uploadAction");
    tag.setMethod("post");
    tag.setEnctype("multipart/form-data");

    tag.doStartTag();
    tag.doEndTag();

    verify(FormTag.class.getResource("Formtag-enctype-html5.txt"));
}
```

**Expected Output Files to Create**:
- `Formtag-1-html5.txt` - Basic form output
- `Formtag-validate-html5.txt` - Form with validation
- `Formtag-enctype-html5.txt` - Form with enctype
- `Formtag-method-html5.txt` - Form with GET method
- `Formtag-dynamic-html5.txt` - Form with dynamic attributes

**Estimated Effort**: 1 hour (6 tests + 5 expected output files, most complex class)

---

### Phase 2: Integration Test Implementation

#### Create Html5TagExampleTest.java

**File**: `apps/showcase/src/test/java/it/org/apache/struts2/showcase/Html5TagExampleTest.java`

**Objective**: End-to-end validation of HTML5 theme in showcase application

**Prerequisites**:
- Showcase application must have HTML5 examples page
- HtmlUnit dependency available for testing

**Test Implementation**:

```java
package it.org.apache.struts2.showcase;

import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for HTML5 theme rendering in showcase application
 */
public class Html5TagExampleTest {

    @Test
    public void testHtml5FormRendering() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            final HtmlPage page = webClient.getPage(
                ParameterUtils.getBaseUrl() + "/html5/index.action"
            );

            assertEquals(200, page.getWebResponse().getStatusCode());

            // Verify clean HTML5 markup (no table-based layout)
            String pageContent = page.asXml();
            assertFalse(pageContent.contains("<table"),
                "HTML5 theme should not use table-based layout");

            // Verify form element is present
            assertNotNull(page.querySelector("form"),
                "Form element should be present");
        }
    }

    @Test
    public void testHtml5ErrorDisplay() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

            final HtmlPage page = webClient.getPage(
                ParameterUtils.getBaseUrl() + "/html5/validation.action"
            );

            // Verify error display uses clean HTML5 markup
            String pageContent = page.asXml();

            // Should have error containers
            assertTrue(pageContent.contains("id=\"actionErrors\"") ||
                      pageContent.contains("class=\"errorMessage\""),
                "Error display elements should be present");

            // Should use <ul> for error lists
            assertTrue(pageContent.contains("<ul>"),
                "Errors should be displayed in <ul> lists");
        }
    }

    @Test
    public void testHtml5DynamicAttributes() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setJavaScriptEnabled(false);

            final HtmlPage page = webClient.getPage(
                ParameterUtils.getBaseUrl() + "/html5/dynamicAttributes.action"
            );

            String pageContent = page.asXml();

            // Verify HTML5 data attributes are rendered
            assertTrue(pageContent.contains("data-"),
                "HTML5 data attributes should be present");

            // Verify aria attributes for accessibility
            assertTrue(pageContent.contains("aria-") ||
                      pageContent.contains("role="),
                "Accessibility attributes should be present");
        }
    }

    @Test
    public void testHtml5FieldErrors() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

            final HtmlPage page = webClient.getPage(
                ParameterUtils.getBaseUrl() + "/html5/fieldValidation.action"
            );

            String pageContent = page.asXml();

            // Field errors should be displayed next to fields
            assertTrue(pageContent.contains("fielderror") ||
                      pageContent.contains("field-error"),
                "Field error styling should be present");

            // Should use semantic HTML5
            assertFalse(pageContent.matches(".*<table[^>]*>.*fielderror.*</table>.*"),
                "Field errors should not use table layout");
        }
    }
}
```

**Expected Output Files**: None (uses live showcase application)

**Estimated Effort**: 1 hour (4 integration tests)

---

## Success Criteria

### Automated Criteria (Must Pass)

- [ ] All existing tests pass: `mvn test -DskipAssembly`
- [ ] All new HTML5 tests pass (20-30 new tests across 5 classes)
- [ ] Integration test passes: `mvn test -Dit.test=Html5TagExampleTest -DskipAssembly`
- [ ] Build completes successfully: `mvn clean install`
- [ ] Test coverage: 18 of 18 test classes have HTML5 tests (100%)

### Manual Criteria (Acceptance)

- [ ] FormTagTest has HTML5 theme tests covering key scenarios
- [ ] AnchorTest has HTML5 theme tests
- [ ] ActionErrorTagTest has HTML5 theme tests
- [ ] ActionMessageTagTest has HTML5 theme tests
- [ ] FieldErrorTagTest has HTML5 theme tests
- [ ] Integration test validates HTML5 theme in showcase
- [ ] No table-based layout in HTML5 theme output
- [ ] Clean HTML5 semantic markup verified
- [ ] Dynamic attributes work correctly in HTML5 theme

## Testing & Validation Plan

### Unit Test Execution

```bash
# Run all HTML5 theme tests
mvn test -Dtest="*Test#test*Html5*" -DskipAssembly

# Run specific test class
mvn test -Dtest=FormTagTest#test*Html5* -DskipAssembly
mvn test -Dtest=ActionErrorTagTest#test*Html5* -DskipAssembly

# Run all tests to ensure no regressions
mvn test -DskipAssembly
```

### Integration Test Execution

```bash
# Run integration tests
mvn test -Dit.test=Html5TagExampleTest -DskipAssembly

# Run all showcase integration tests
cd apps/showcase
mvn verify -DskipAssembly
```

### Expected Results

- **Phase 1**: 20-30 new unit tests added, all passing
- **Phase 2**: 4 integration tests added, all passing
- **Overall**: 100% HTML5 test coverage across all UI tag test classes

## Dependencies & Assumptions

### Technical Dependencies

- **Struts version**: 7.x (current development branch)
- **Maven**: 3.x
- **Java**: 17+
- **Testing**: JUnit 5, HtmlUnit (for integration tests)
- **HTML5 templates**: Already present in `core/src/main/resources/template/html5/`

### Assumptions

- HTML5 theme templates are stable and complete
- Showcase application will have HTML5 examples (may need to add)
- Expected output files will match actual HTML5 template output
- No breaking changes to tag rendering during implementation

## Risk Analysis & Mitigation

### Technical Risks

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Expected output files don't match actual output | Medium | Medium | Run tests first to generate actual output, then create expected files |
| Empty `a.ftl` template causes test failures | Low | Low | Document that HTML5 anchor tests will match parent theme |
| FormTagTest complexity causes delays | Medium | Low | Start with simple scenarios, expand gradually |
| Integration tests require showcase changes | Medium | Medium | Create showcase HTML5 examples page if needed |

### Operational Risks

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| Phase 1 takes longer than estimated | Low | Medium | Prioritize error/message tags first (simpler) |
| Integration tests fail in CI | Medium | Low | Ensure showcase is deployed in CI environment |
| New tests expose HTML5 template bugs | High | Low | This is actually beneficial - fix bugs found |

## Implementation Timeline

### Phase 1: Unit Tests (2-3 hours)
- **Phase 1.1**: Error/Message Tags - 1 hour (ActionError, ActionMessage, FieldError)
- **Phase 1.2**: Navigation Tags - 30 minutes (Anchor)
- **Phase 1.3**: Form Tags - 1 hour (Form)

### Phase 2: Integration Tests (1 hour)
- Create Html5TagExampleTest.java - 30 minutes
- Create showcase HTML5 examples (if needed) - 30 minutes

### Total Estimated Time: 3-4 hours

## Code References

### Test Classes to Modify

- `core/src/test/java/org/apache/struts2/views/jsp/ui/FormTagTest.java` - Add 6 HTML5 tests
- `core/src/test/java/org/apache/struts2/views/jsp/ui/AnchorTest.java` - Add 5 HTML5 tests
- `core/src/test/java/org/apache/struts2/views/jsp/ui/ActionErrorTagTest.java` - Add 4 HTML5 tests
- `core/src/test/java/org/apache/struts2/views/jsp/ui/ActionMessageTagTest.java` - Add 4 HTML5 tests
- `core/src/test/java/org/apache/struts2/views/jsp/ui/FieldErrorTagTest.java` - Add 5 HTML5 tests

### Expected Output Files to Create

**ActionErrorTagTest**:
- `core/src/test/resources/org/apache/struts2/views/jsp/ui/ActionErrorTag-3-html5.txt`

**ActionMessageTagTest**:
- `core/src/test/resources/org/apache/struts2/views/jsp/ui/ActionMessageTag-3-html5.txt`

**FieldErrorTagTest**:
- `core/src/test/resources/org/apache/struts2/views/jsp/ui/FieldErrorTag-3-html5.txt`
- `core/src/test/resources/org/apache/struts2/views/jsp/ui/FieldErrorTag-filtered-html5.txt`

**AnchorTest**:
- `core/src/test/resources/org/apache/struts2/views/jsp/ui/AnchorTag-1-html5.txt`
- `core/src/test/resources/org/apache/struts2/views/jsp/ui/AnchorTag-dynamic-html5.txt`
- `core/src/test/resources/org/apache/struts2/views/jsp/ui/AnchorTag-disabled-html5.txt`

**FormTagTest**:
- `core/src/test/resources/org/apache/struts2/views/jsp/ui/Formtag-1-html5.txt`
- `core/src/test/resources/org/apache/struts2/views/jsp/ui/Formtag-validate-html5.txt`
- `core/src/test/resources/org/apache/struts2/views/jsp/ui/Formtag-enctype-html5.txt`
- `core/src/test/resources/org/apache/struts2/views/jsp/ui/Formtag-method-html5.txt`
- `core/src/test/resources/org/apache/struts2/views/jsp/ui/Formtag-dynamic-html5.txt`

### Integration Test to Create

- `apps/showcase/src/test/java/it/org/apache/struts2/showcase/Html5TagExampleTest.java`

### HTML5 Templates (Reference Only)

- `core/src/main/resources/template/html5/form.ftl` - Form template (74 lines)
- `core/src/main/resources/template/html5/a.ftl` - Anchor template (empty)
- `core/src/main/resources/template/html5/actionerror.ftl` - Action error template (42 lines)
- `core/src/main/resources/template/html5/actionmessage.ftl` - Action message template (42 lines)
- `core/src/main/resources/template/html5/fielderror.ftl` - Field error template (75 lines)

## Related Work

### Original Work

- `thoughts/shared/validation/2025-11-26-WW-5444-html5-theme-validation.md` - Validation report showing 72% completion
- `thoughts/shared/research/2025-11-23-WW-5444-html5-theme-testing.md` - Original research document
- Commit: `65f4ad42a` - test(html5-theme): add comprehensive unit tests (13 classes)
- Commit: `26192689f` - feat(themes): move html5 theme from showcase to core

### Related Ticket

- [WW-5444](https://issues.apache.org/jira/browse/WW-5444) - Define new HTML5 theme

## Future Considerations

### Post-Implementation

1. **Documentation**: Update Struts documentation with HTML5 theme usage examples
2. **Showcase Expansion**: Add comprehensive HTML5 examples to showcase application
3. **Performance Testing**: Compare HTML5 theme rendering performance vs xhtml theme
4. **HTML5 Validation**: Add automated HTML5 markup validation (W3C validator)
5. **Accessibility Testing**: Validate ARIA attributes and accessibility compliance
6. **Default Theme Discussion**: Consider making html5 the recommended default theme

### Potential Enhancements

1. **Bootstrap Integration**: Document HTML5 theme + Bootstrap CSS patterns
2. **Custom HTML5 Components**: Add HTML5-specific components (datalist, range, etc.)
3. **Progressive Enhancement**: Add JavaScript enhancements for HTML5 features
4. **Theme Documentation**: Create comprehensive HTML5 theme guide

## Appendices

### A. Test Method Naming Convention

For functional tests in these classes, use this naming pattern:

```
test<Scenario>_html5
test<Scenario>_html5_clearTagStateSet
```

Examples:
- `testSimpleForm_html5`
- `testWithActionErrors_html5`
- `testWithFieldErrorsAndParamTag_html5`

### B. Expected Output File Generation

To generate expected output files:

1. **Run test first** (it will fail)
2. **Capture actual output** from test failure message or writer content
3. **Save to expected file** in `core/src/test/resources/org/apache/struts2/views/jsp/ui/`
4. **Verify output** is correct HTML5 markup
5. **Re-run test** to confirm it passes

### C. Integration Test Setup

If showcase HTML5 examples don't exist, create:

1. **index.jsp**: Basic HTML5 form example
2. **validation.jsp**: Form with validation errors
3. **dynamicAttributes.jsp**: Components with HTML5 data attributes
4. **fieldValidation.jsp**: Form with field-level errors

Place in: `apps/showcase/src/main/webapp/WEB-INF/html5/`

### D. Test Execution Summary

| Phase | Test Class | New Tests | Expected Files | Effort |
|-------|-----------|-----------|----------------|--------|
| 1.1 | ActionErrorTagTest | 4 | 1 | 30 min |
| 1.1 | ActionMessageTagTest | 4 | 1 | 20 min |
| 1.1 | FieldErrorTagTest | 5 | 2 | 40 min |
| 1.2 | AnchorTest | 5 | 3 | 30 min |
| 1.3 | FormTagTest | 6 | 5 | 1 hour |
| 2 | Html5TagExampleTest | 4 | 0 | 1 hour |
| **Total** | **6 classes** | **28 tests** | **12 files** | **3-4 hours** |

---

## Implementation Strategy Notes

### Key Differences from Generic Property Tests

The 13 completed test classes used the `verifyGenericProperties()` method which tests all common UI properties (id, name, cssClass, onclick, etc.) across themes.

The 5 remaining test classes use **functional testing** which tests specific tag behaviors and scenarios. This means:

1. **No generic property pattern** - Cannot use the 4-method template
2. **Scenario-based tests** - Each test validates a specific use case
3. **Expected output files** - Tests compare against expected HTML output files
4. **More test methods needed** - May need 4-6 HTML5 tests per class (not just 2)

### Testing Approach

For each test class:

1. **Identify key scenarios** from existing tests
2. **Add HTML5 variants** of important scenarios
3. **Generate expected output** by running HTML5 templates
4. **Verify clean HTML5 markup** (no tables, semantic elements)
5. **Test clearTagStateSet** variant for pooling support

### Success Indicators

✅ **Phase 1 Complete**: 28 new tests added, all passing
✅ **Phase 2 Complete**: Integration test validates HTML5 theme
✅ **Overall Success**: 100% HTML5 test coverage (18/18 classes)

---

*This plan provides a structured approach to completing HTML5 theme test coverage with realistic effort estimates and clear success criteria.*