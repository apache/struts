---
date: 2025-11-23T17:48:30+01:00
topic: "HTML5 Theme Implementation and Testing Approach"
tags: [research, html5-theme, testing, ui-tags, themes, WW-5444]
status: complete
git_commit: 461c06d0c5afde260fdb0f32038a32f2af5bb42e
git_branch: feature/WW-5444-html5
---

# Research: HTML5 Theme Implementation and Testing Approach

**Date**: 2025-11-23T17:48:30+01:00
**Branch**: feature/WW-5444-html5
**Jira**: [WW-5444](https://issues.apache.org/jira/browse/WW-5444)

## Research Question

What testing is needed for the HTML5 theme implementation in Apache Struts, and how should it be approached based on existing theme testing patterns?

## Summary

The HTML5 theme has been implemented with 45 FreeMarker templates in the showcase application for easier manual testing. Analysis of existing theme testing patterns reveals a well-established infrastructure in `AbstractUITagTest` that should be replicated for html5 theme validation. The theme currently lives in `apps/showcase` but should eventually move to `core/` for production distribution.

**Key Findings:**
- HTML5 theme has 45 templates but NO unit tests yet
- Existing themes (simple, xhtml, css_xhtml) use `verifyGenericProperties()` for testing
- Test pattern is straightforward: add `testGenericHtml5()` to each UI tag test class
- Production themes belong in `core/src/main/resources/template/`, not `apps/showcase`
- No historical documentation exists in thoughts/ directory about theme testing

## Detailed Findings

### Current HTML5 Theme Implementation

**Location**: `apps/showcase/src/main/resources/template/html5/`

**45 FreeMarker Templates:**
- Form elements: `form.ftl`, `form-close.ftl`, `text.ftl`, `textarea.ftl`, `password.ftl`, `checkbox.ftl`, `checkboxlist.ftl`, `radiomap.ftl`, `select.ftl`, `file.ftl`, `hidden.ftl`, `token.ftl`
- Complex components: `combobox.ftl`, `doubleselect.ftl`, `inputtransferselect.ftl`, `optiontransferselect.ftl`, `updownselect.ftl`, `optgroup.ftl`
- Display components: `actionerror.ftl`, `actionmessage.ftl`, `fielderror.ftl`, `label.ftl`
- Navigation: `a.ftl`, `a-close.ftl`, `link.ftl`, `submit.ftl`, `submit-close.ftl`, `reset.ftl`
- Utilities: `head.ftl`, `script.ftl`, `script-close.ftl`, `debug.ftl`, `css.ftl`, `nonce.ftl`, `datetextfield.ftl`
- Structural: `controlheader.ftl`, `controlfooter.ftl`, `empty.ftl`
- Attribute handling: `common-attributes.ftl`, `dynamic-attributes.ftl`, `prefixed-dynamic-attributes.ftl`, `scripting-events.ftl`

**Supporting Files:**
- Java: `apps/showcase/src/main/java/org/apache/struts2/showcase/action/Html5Action.java`
- JSP: `apps/showcase/src/main/webapp/WEB-INF/html5/index.jsp`
- Config: `apps/showcase/src/main/resources/struts.xml` (lines 163-167)

**Key Features:**
- Clean HTML5 markup without table-based layouts
- Empty control headers/footers (no wrapping divs)
- Support for HTML5 input types via TextField component
- Bootstrap CSS integration
- CSP nonce attribute support

### Theme Testing Infrastructure

**Core Testing Class**: `core/src/test/java/org/apache/struts2/views/jsp/AbstractUITagTest.java`

This class provides the foundation for all UI tag testing with two primary testing approaches:

#### 1. Generic Property Testing

**Method**: `verifyGenericProperties(AbstractUITag tag, String theme, String[] exclude)`

Tests that all standard HTML attributes render correctly for a given theme:
- Sets 15+ generic properties (id, name, cssClass, cssStyle, title, disabled, tabindex, value, onclick, ondblclick, etc.)
- Adds dynamic attribute test (`data-id="id-random"`)
- Renders tag with specified theme
- Verifies each property appears correctly in HTML output
- Checks for FreeMarker template errors

**Property Test Coverage** (Lines 110-133):
```java
protected Map<String, PropertyHolder> initializedGenericTagTestProperties() {
    Map<String, PropertyHolder> result = new HashMap<>();
    new PropertyHolder("name", "someName").addToMap(result);
    new PropertyHolder("id", "someId").addToMap(result);
    new PropertyHolder("cssClass", "cssClass1", "class=\"cssClass1\"").addToMap(result);
    new PropertyHolder("cssStyle", "cssStyle1", "style=\"cssStyle1\"").addToMap(result);
    new PropertyHolder("title", "someTitle").addToMap(result);
    new PropertyHolder("disabled", "true", "disabled=\"disabled\"").addToMap(result);
    new PropertyHolder("tabindex", "99").addToMap(result);
    new PropertyHolder("value", "someValue").addToMap(result);
    // ... event handlers (onclick, ondblclick, onmousedown, etc.)
    return result;
}
```

#### 2. Specific Output Testing

**Method**: `verify(URL url)`

Compares rendered output against expected `.txt` files:
- Loads expected HTML from resource file
- Normalizes both actual and expected output (removes whitespace differences)
- Performs exact string comparison
- Provides detailed error messages on mismatch

**Expected File Location**: `core/src/test/resources/org/apache/struts2/views/jsp/ui/{TagName}-{N}.txt`

### Existing Theme Test Patterns

**Simple Theme Tests** (from TextfieldTest.java):
```java
public void testGenericSimple() throws Exception {
    TextFieldTag tag = new TextFieldTag();
    verifyGenericProperties(tag, "simple", null);
}
```

**XHTML Theme Tests**:
```java
public void testGenericXhtml() throws Exception {
    TextFieldTag tag = new TextFieldTag();
    verifyGenericProperties(tag, "xhtml", null);
}
```

**With Property Exclusions** (RadioTest.java):
```java
public void testGenericSimple() throws Exception {
    RadioTag tag = new RadioTag();
    verifyGenericProperties(tag, "simple", new String[]{"id", "value"});
    // Excludes "id" and "value" as radio buttons handle them differently
}
```

### Theme Template Location Patterns

**Production Themes** → `core/src/main/resources/template/`
- **simple**: 47 templates (base theme, no parent)
- **xhtml**: 30 templates (parent = simple)
- **css_xhtml**: 13 templates (parent = xhtml)

**Demo/Showcase Themes** → `apps/showcase/src/main/resources/template/`
- **html5**: 45 templates (currently here)
- **ajaxErrorContainers**: 4 templates

**Theme Inheritance Pattern**:
```
simple (root)
  ↓
xhtml (parent = simple)
  ↓
css_xhtml (parent = xhtml)
```

Defined via `theme.properties` with `parent = <parent-theme>` property.

**Key Pattern Rule**: Production-ready themes MUST be in `core/` to be packaged with struts2-core.jar and available to all applications.

### Integration Testing Pattern

**Example from Showcase**: `apps/showcase/src/test/java/it/org/apache/struts2/showcase/UITagExampleTest.java`

Uses HtmlUnit to test complete form workflows:
```java
@Test
public void testInputForm() throws Exception {
    try (final WebClient webClient = new WebClient()) {
        final HtmlPage page = webClient.getPage(
            ParameterUtils.getBaseUrl() + "/tags/ui/example!input.action"
        );

        final HtmlForm form = page.getFormByName("exampleSubmit");
        final HtmlTextInput textField = form.getInputByName("name");
        // ... interact with form elements

        final HtmlSubmitInput button = form.getInputByValue("Submit");
        final HtmlPage page2 = button.click();

        // Verify results
        Assert.assertEquals("name", page2.getElementById("name").asNormalizedText());
    }
}
```

## Code References

### HTML5 Theme Implementation
- `apps/showcase/src/main/resources/template/html5/*.ftl` - All 45 FreeMarker templates
- `apps/showcase/src/main/java/org/apache/struts2/showcase/action/Html5Action.java` - Demo action
- `apps/showcase/src/main/webapp/WEB-INF/html5/index.jsp` - Demo page
- `apps/showcase/src/main/resources/struts.xml:163-167` - Configuration

### Testing Infrastructure
- `core/src/test/java/org/apache/struts2/views/jsp/AbstractUITagTest.java:155-174` - verifyGenericProperties() method
- `core/src/test/java/org/apache/struts2/views/jsp/AbstractUITagTest.java:110-133` - Generic property definitions
- `core/src/test/java/org/apache/struts2/views/jsp/AbstractUITagTest.java:201-230` - verify() method for output comparison
- `core/src/test/resources/org/apache/struts2/views/jsp/ui/` - Expected output files

### Example Test Classes
- `core/src/test/java/org/apache/struts2/views/jsp/ui/TextfieldTest.java` - Comprehensive test examples
- `core/src/test/java/org/apache/struts2/views/jsp/ui/PasswordTest.java:78-86` - Theme test pattern
- `core/src/test/java/org/apache/struts2/views/jsp/ui/CheckboxTest.java` - Checkbox-specific tests
- `core/src/test/java/org/apache/struts2/views/jsp/ui/SelectTest.java` - Select/dropdown tests

### Integration Tests
- `apps/showcase/src/test/java/it/org/apache/struts2/showcase/UITagExampleTest.java` - HtmlUnit integration test example

## Testing Recommendations

### Priority 1: Unit Tests for Generic Properties (High Priority)

Add `testGenericHtml5()` method to each UI tag test class:

**Files to modify** (in `core/src/test/java/org/apache/struts2/views/jsp/ui/`):
1. `TextfieldTest.java`
2. `TextareaTest.java`
3. `PasswordTest.java`
4. `CheckboxTest.java`
5. `CheckboxListTest.java`
6. `RadioTest.java`
7. `SelectTest.java`
8. `FileTest.java`
9. `HiddenTest.java`
10. `LabelTest.java`
11. `FormTagTest.java`
12. `SubmitTest.java`
13. `ResetTest.java`
14. `AnchorTagTest.java`
15. `ActionErrorTagTest.java`
16. `ActionMessageTagTest.java`
17. `FieldErrorTagTest.java`
18. `DoubleSelectTest.java`
19. `ComboBoxTest.java`
20. `UpDownSelectTagTest.java`
21. `InputTransferSelectTagTest.java`
22. `OptionTransferSelectTagTest.java`

**Pattern to add**:
```java
public void testGenericHtml5() throws Exception {
    <TagName>Tag tag = new <TagName>Tag();
    verifyGenericProperties(tag, "html5", null);
}

public void testGenericHtml5_clearTagStateSet() throws Exception {
    <TagName>Tag tag = new <TagName>Tag();
    tag.setPerformClearTagStateForTagPoolingServers(true);
    verifyGenericProperties(tag, "html5", null);
}
```

**Exclusions to consider**:
- Radio/Checkbox: Exclude "id" and "value" (handled differently)
- Hidden: Exclude properties that don't apply to hidden fields
- Token: Minimal properties apply

**Effort**: ~2-3 lines per test class × 22 classes = **minimal implementation effort**

**Benefit**: Ensures all 45 templates render correctly with standard HTML attributes

### Priority 2: Integration Tests in Showcase (Medium Priority)

**Create**: `apps/showcase/src/test/java/it/org/apache/struts2/showcase/Html5TagExampleTest.java`

**Test Coverage**:
1. Basic form rendering with html5 theme
2. All input types render correctly
3. Error messages display properly (actionerror, actionmessage, fielderror)
4. Dynamic attributes work (data-*, aria-*)
5. Submit workflow completes successfully
6. No table-based layout in output

**Example structure**:
```java
public class Html5TagExampleTest {
    @Test
    public void testHtml5FormRendering() throws Exception {
        try (final WebClient webClient = new WebClient()) {
            final HtmlPage page = webClient.getPage(
                ParameterUtils.getBaseUrl() + "/html5/index.action"
            );

            // Verify clean HTML5 markup
            assertFalse(page.asXml().contains("<table"));

            // Test error display
            assertNotNull(page.getElementById("actionErrors"));

            // Verify Bootstrap compatibility
            assertTrue(page.asXml().contains("class=\""));
        }
    }
}
```

**Effort**: 1-2 integration test methods

**Benefit**: End-to-end validation that theme works in real application context

### Priority 3: Expand Showcase Demo (Medium Priority)

**Enhance**: `apps/showcase/src/main/webapp/WEB-INF/html5/index.jsp`

**Add demonstrations for**:
1. All form input types (text, password, checkbox, radio, select, textarea, file, hidden)
2. Complex components (doubleselect, combobox, updownselect, etc.)
3. Error handling scenarios (fielderror, actionerror, actionmessage)
4. Dynamic attributes (data-*, aria-*)
5. HTML5 input types (email, url, number, date, etc.)
6. Submit/reset buttons
7. Links and anchors
8. Label positioning

**Benefit**: Visual validation during development, documentation for users

### Priority 4: Template Relocation Decision (High Priority)

**Question**: Should html5 theme move from `apps/showcase` to `core`?

**Options**:

**Option A: Move to core/** (Recommended for production theme)
- **Location**: `core/src/main/resources/template/html5/`
- **Pros**:
  - Available to all Struts applications
  - Shipped with struts2-core.jar
  - Consistent with simple/xhtml/css_xhtml
- **Cons**:
  - Commits to maintaining it
  - Increases core artifact size slightly

**Option B: Keep in showcase** (Recommended for experimental theme)
- **Location**: `apps/showcase/src/main/resources/template/html5/`
- **Pros**:
  - Easy to experiment and iterate
  - Doesn't affect core framework
  - Users can copy templates to their apps
- **Cons**:
  - Not available in struts2-core.jar
  - Users must manually add templates
  - Inconsistent with other themes

**Recommendation**: If html5 theme is meant to be production-ready and recommended for new applications, move to `core/`. If it's experimental or an example, keep in showcase.

**Implementation**: If moving to core, also move:
- Templates: `apps/showcase/src/main/resources/template/html5/` → `core/src/main/resources/template/html5/`
- Tests: Create unit tests in `core/src/test/java/org/apache/struts2/views/jsp/ui/*Test.java`
- Update showcase to use core's html5 theme instead of local copy

### Priority 5: HTML5 Validation Testing (Low-Medium Priority)

**Tools to use**:
- W3C HTML5 Validator (via API or library)
- HtmlUnit for semantic validation
- Accessibility checkers (ARIA attributes, semantic tags)

**What to validate**:
1. Valid HTML5 markup (no deprecated elements)
2. Proper attribute handling (data-*, aria-*, etc.)
3. Semantic HTML structure where appropriate
4. No table-based layouts (unless data tables)
5. Accessibility compliance (labels, ARIA)

**Example test**:
```java
@Test
public void testHtml5ValidMarkup() throws Exception {
    // Render form with html5 theme
    FormTag form = new FormTag();
    form.setTheme("html5");
    // ... configure
    form.doStartTag();
    form.doEndTag();

    String output = writer.toString();

    // Verify no table layout
    assertFalse(output.contains("<table"));
    assertFalse(output.contains("class=\"tdLabel\""));

    // Verify HTML5 elements
    assertTrue(output.matches(".*<form[^>]*>.*"));

    // No deprecated attributes
    assertFalse(output.contains("align="));
    assertFalse(output.contains("border="));
}
```

### Priority 6: Documentation (Medium Priority)

**Create**:
1. **User Guide**: How to use html5 theme in applications
   - Migration guide from xhtml/simple to html5
   - Bootstrap CSS integration examples
   - HTML5 input type usage

2. **Developer Guide**: How html5 theme works
   - Template structure
   - Differences from xhtml theme
   - Customization points

3. **Testing Guide**: How to test themes
   - Unit test pattern
   - Integration test approach
   - Expected output file creation

**Location**: Consider adding to `core/src/site/` for Maven site generation

## Architecture Insights

### Theme Testing Architecture

The Struts theme testing architecture demonstrates several key design patterns:

1. **Template Method Pattern**: `AbstractUITagTest` provides template methods like `verifyGenericProperties()` that subclasses call with theme-specific parameters

2. **Resource-Based Verification**: Expected output files as resources allow platform-independent testing without hardcoded strings in test code

3. **Normalization Strategy**: Whitespace normalization makes tests resilient to formatting differences while still catching real HTML changes

4. **Property Holder Pattern**: Encapsulates test data (name, value, expectation) for declarative property testing

5. **Theme Inheritance**: FreeMarker template resolution with fallback supports theme hierarchies (css_xhtml → xhtml → simple)

### HTML5 Theme Design Decisions

1. **No Control Wrappers**: Empty `controlheader.ftl` and `controlfooter.ftl` provide clean HTML without extra divs, unlike xhtml theme's table-based layout

2. **Bootstrap Compatibility**: Clean markup designed to work with Bootstrap CSS classes without framework-specific markup

3. **HTML5 Input Types**: TextField component supports HTML5 input types (email, url, number, etc.) via type attribute

4. **CSP Support**: Includes `nonce.ftl` for Content Security Policy nonce attribute handling

5. **Dynamic Attributes**: Full support for data-* and aria-* attributes via `dynamic-attributes.ftl`

### Testing Infrastructure Design

1. **Separation of Concerns**: Generic property tests separate from specific behavior tests

2. **Reusable Test Infrastructure**: `AbstractUITagTest` provides shared testing utilities for all UI tag tests

3. **Theme Parameterization**: Same test can validate multiple themes by changing theme parameter

4. **Exclusion Mechanism**: Allows tags to exclude properties that don't apply (e.g., hidden field doesn't render visible properties)

5. **Integration with Maven**: Test resources use classpath resolution for portability

## Open Questions

1. **Production Readiness**: Is html5 theme ready for production (core/) or still experimental (showcase/)?

2. **Theme Properties**: Should html5 theme have a `theme.properties` file defining inheritance (e.g., `parent = simple`)?

3. **Default Theme**: Should html5 become the default theme for new Struts applications (replacing xhtml)?

4. **Backward Compatibility**: How to handle applications migrating from xhtml to html5 theme?

5. **CSS Framework Integration**: Should html5 theme include optional CSS for Bootstrap, Tailwind, or remain unopinionated?

6. **Component Coverage**: Are all 45 templates necessary, or should some inherit from simple theme?

7. **Form Layout**: Should html5 theme include layout options (inline, horizontal, vertical) like Bootstrap forms?

8. **Error Positioning**: How should field errors be positioned in html5 theme compared to xhtml theme's table-based approach?

## Next Steps

### Immediate Actions (Before PR)

1. **Add unit tests**: Implement `testGenericHtml5()` in all UI tag test classes
2. **Run test suite**: Execute `mvn test -DskipAssembly` to verify all tests pass
3. **Decide location**: Determine if html5 theme should move to core/ or stay in showcase/
4. **Create integration test**: Add `Html5TagExampleTest.java` for end-to-end validation

### Short-term Actions (Before Release)

1. **Expand showcase demo**: Add comprehensive examples to index.jsp
2. **HTML5 validation**: Verify all templates produce valid HTML5 markup
3. **Documentation**: Create user guide for html5 theme usage
4. **Performance testing**: Compare rendering performance vs xhtml theme

### Long-term Considerations

1. **Default theme discussion**: Consider making html5 the recommended/default theme
2. **Migration tooling**: Create utilities to help migrate from xhtml to html5
3. **CSS framework integration**: Evaluate official integrations with popular CSS frameworks
4. **Accessibility audit**: Ensure WCAG compliance for all components

## Testing Effort Estimation

**Unit Tests**:
- 22 test classes × 2 methods each = 44 test methods
- ~2-3 lines per method
- **Effort**: 2-3 hours

**Integration Tests**:
- 1 test class with 3-5 test methods
- **Effort**: 2-4 hours

**Expected Output Files** (if needed for specific tests):
- 0 files needed for generic property tests
- Optional for specific behavior tests
- **Effort**: Variable, only if specific behavior needs validation

**Total Estimated Effort**: **4-7 hours** for comprehensive test coverage

## References

### Jira Issues
- [WW-5444](https://issues.apache.org/jira/browse/WW-5444) - Implement a new html5 theme

### Related Code
- Theme rendering: `core/src/main/java/org/apache/struts2/components/UIBean.java`
- Template engine: `core/src/main/java/org/apache/struts2/components/template/`
- FreeMarker templates: `core/src/main/resources/template/`

### Maven Commands
- Run tests: `mvn test -DskipAssembly`
- Build showcase: `mvn clean install` (from apps/showcase/)
- Full build: `mvn clean install` (from project root)
