---
date: 2025-11-23T17:55:00+01:00
topic: "Move HTML5 Theme from Showcase to Core"
tags: [plan, html5-theme, WW-5444, theme-migration]
status: pending
git_commit: 461c06d0c5afde260fdb0f32038a32f2af5bb42e
git_branch: feature/WW-5444-html5
related_research: thoughts/shared/research/2025-11-23-WW-5444-html5-theme-testing.md
---

# Implementation Plan: Move HTML5 Theme to Core

**Date**: 2025-11-23T17:55:00+01:00
**Branch**: feature/WW-5444-html5
**Jira**: [WW-5444](https://issues.apache.org/jira/browse/WW-5444)
**Related Research**: [HTML5 Theme Testing Approach](../research/2025-11-23-WW-5444-html5-theme-testing.md)

## Objective

Move the html5 theme from `apps/showcase/src/main/resources/template/html5/` to `core/src/main/resources/template/html5/` to make it a production-ready theme shipped with struts2-core.jar, enabling all Struts applications to use it and allowing unit tests to be written in the core module.

## Background

### Current State
- **Location**: `apps/showcase/src/main/resources/template/html5/`
- **Templates**: 45 FreeMarker templates (.ftl files)
- **Status**: Fully functional in showcase but not accessible to other applications
- **Testing**: No unit tests (can't add them in core while templates are in showcase)

### Why Move to Core?
1. **Distribution**: Make html5 theme available to all Struts applications via struts2-core.jar
2. **Testing**: Enable unit tests in `core/src/test/` to validate theme rendering
3. **Consistency**: Align with other production themes (simple, xhtml, css_xhtml)
4. **Official Support**: Signal that html5 is a supported, production-ready theme

### Design Decisions
- **Intentional omissions confirmed**: Missing templates (form-validate.ftl, tooltip.ftl, etc.) are deliberate - html5 uses simpler, modern approach
- **No theme.properties needed initially**: Can be added later if inheritance is desired
- **Bootstrap compatible**: Clean markup designed for CSS framework integration

## Implementation Steps

### Phase 1: Create HTML5 Theme Directory in Core

**Action**: Create target directory structure

```bash
mkdir -p core/src/main/resources/template/html5/
```

**Verification**: Directory exists and is empty

---

### Phase 2: Move FreeMarker Templates to Core

**Action**: Move all 45 templates from showcase to core

**Source**: `apps/showcase/src/main/resources/template/html5/`
**Target**: `core/src/main/resources/template/html5/`

**Templates to move** (45 files):

**Form Elements** (12 files):
- `form.ftl` - Form opening tag with HTML5 attributes
- `form-close.ftl` - Form closing tag
- `text.ftl` - Text input field (supports HTML5 types)
- `textarea.ftl` - Textarea element
- `password.ftl` - Password input field
- `checkbox.ftl` - Checkbox input element
- `checkboxlist.ftl` - List of checkboxes
- `radiomap.ftl` - Radio button map
- `select.ftl` - Select/dropdown element
- `file.ftl` - File upload input
- `hidden.ftl` - Hidden input field
- `token.ftl` - CSRF token

**Complex Components** (8 files):
- `combobox.ftl` - Combo box
- `doubleselect.ftl` - Double select component
- `inputtransferselect.ftl` - Input transfer select
- `optiontransferselect.ftl` - Option transfer select
- `updownselect.ftl` - Up/down select component
- `optgroup.ftl` - Option group element
- `datetextfield.ftl` - Date input field

**Display Components** (4 files):
- `actionerror.ftl` - Action errors display
- `actionmessage.ftl` - Action messages display
- `fielderror.ftl` - Field error display
- `label.ftl` - Label element

**Navigation** (7 files):
- `a.ftl` - Anchor element opening
- `a-close.ftl` - Anchor element closing
- `link.ftl` - Link element (CSS/stylesheet)
- `submit.ftl` - Submit button
- `submit-close.ftl` - Submit button closing
- `reset.ftl` - Reset button

**Utilities** (6 files):
- `head.ftl` - HTML head section
- `script.ftl` - Script opening tag
- `script-close.ftl` - Script closing tag
- `debug.ftl` - Debug component
- `css.ftl` - CSS class/style handling
- `nonce.ftl` - Nonce attribute for CSP

**Structural** (3 files):
- `controlheader.ftl` - Control header wrapper (empty by design)
- `controlfooter.ftl` - Control footer wrapper (empty by design)
- `empty.ftl` - Empty template

**Attribute Handling** (4 files):
- `common-attributes.ftl` - Common HTML5 attributes
- `dynamic-attributes.ftl` - Dynamic attribute handling
- `prefixed-dynamic-attributes.ftl` - Prefixed dynamic attributes
- `scripting-events.ftl` - JavaScript event handlers

**Method**:
```bash
# Option 1: Git mv (preserves history)
git mv apps/showcase/src/main/resources/template/html5/*.ftl \
       core/src/main/resources/template/html5/

# Option 2: Copy then delete (simpler)
cp -r apps/showcase/src/main/resources/template/html5/*.ftl \
      core/src/main/resources/template/html5/
```

**Verification**:
- All 45 .ftl files exist in `core/src/main/resources/template/html5/`
- File contents are identical to originals

---

### Phase 3: Create theme.properties (Optional)

**Decision Point**: Does html5 theme need a theme.properties file?

**Option A: No theme.properties** (Recommended initially)
- html5 is standalone theme like simple
- No parent theme inheritance needed
- Simpler to maintain

**Option B: Add theme.properties with parent**
```properties
# core/src/main/resources/template/html5/theme.properties
parent = simple
```
- Inherit any missing templates from simple
- Fallback for future template additions

**Recommendation**: Start without theme.properties, add later if inheritance needed

**Action**: Skip for now (can add in future commit if needed)

---

### Phase 4: Delete HTML5 Templates from Showcase

**Action**: Remove html5 template directory from showcase

```bash
rm -rf apps/showcase/src/main/resources/template/html5/
```

**Rationale**:
- Templates now in core, accessible via classpath
- Avoid duplication and maintenance issues
- Showcase will load html5 theme from core

**Verification**:
- Directory `apps/showcase/src/main/resources/template/html5/` does not exist
- Showcase can still access html5 theme (from core's classpath)

---

### Phase 5: Verify Showcase Still Works

**Action**: Build and test showcase application

**Build**:
```bash
cd apps/showcase
mvn clean install
```

**Test**:
1. Start showcase application
2. Navigate to: `http://localhost:8080/struts2-showcase/html5/index.action`
3. Verify page renders correctly with html5 theme
4. Check that all tags display properly
5. Verify actionerror/actionmessage display correctly

**Expected Behavior**:
- Page renders identically to before
- Templates loaded from core's classpath, not showcase
- No errors in console
- All html5 theme tags work

**Verification Checklist**:
- [ ] Showcase builds successfully
- [ ] /html5/index.action accessible
- [ ] Form elements render correctly
- [ ] Action errors/messages display
- [ ] Links work (theme="html5" attribute)
- [ ] No template not found errors
- [ ] Browser console shows no errors

---

### Phase 6: Run Core Tests

**Action**: Verify existing core tests still pass

```bash
cd core
mvn test -DskipAssembly
```

**Expected Outcome**:
- All existing tests pass
- No test failures introduced
- Build succeeds

**Note**: No new tests added yet (that's Phase 7, separate commit)

**Verification**:
- [ ] `mvn test` completes successfully
- [ ] No test failures
- [ ] Build shows SUCCESS

---

### Phase 7: Update Documentation (Optional)

**Consider updating**:
- `core/README.md` or docs - mention html5 theme availability
- Showcase welcome page - explain html5 theme is now in core
- Migration guide for users

**Action**: Can be done in follow-up commit

---

## Rollback Plan

If issues are encountered:

1. **Revert file moves**:
   ```bash
   git checkout apps/showcase/src/main/resources/template/html5/
   rm -rf core/src/main/resources/template/html5/
   ```

2. **Rebuild**:
   ```bash
   mvn clean install
   ```

3. **Verify showcase works** with original templates

## Expected Outcomes

### After Successful Move

**Core Module**:
- ✅ New directory: `core/src/main/resources/template/html5/` with 45 templates
- ✅ html5 theme packaged in struts2-core.jar
- ✅ Available to all Struts applications
- ✅ Ready for unit tests

**Showcase Module**:
- ✅ No more duplicate templates in `apps/showcase/src/main/resources/template/html5/`
- ✅ Still works correctly by loading html5 theme from core's classpath
- ✅ Html5Action and index.jsp unchanged

**Applications Using Struts**:
- ✅ Can now use html5 theme: `<s:form theme="html5">`
- ✅ No manual template copying needed
- ✅ html5 theme available out-of-the-box

## Next Steps (Future Work)

After successful move:

1. **Add Unit Tests** (separate commit)
   - Add `testGenericHtml5()` to 22 UI tag test classes
   - See: [Testing Implementation Plan](../research/2025-11-23-WW-5444-html5-theme-testing.md#priority-1-unit-tests-for-generic-properties-high-priority)

2. **Add Integration Tests** (separate commit)
   - Create `Html5TagExampleTest.java` in showcase

3. **Expand Showcase Demo** (optional)
   - Add more examples to `/html5/index.jsp`
   - Demonstrate all html5 theme features

4. **Documentation** (optional)
   - User guide for html5 theme
   - Migration guide from xhtml to html5

## Files Changed Summary

### Created
- `core/src/main/resources/template/html5/*.ftl` (45 files)
- Optional: `core/src/main/resources/template/html5/theme.properties`

### Deleted
- `apps/showcase/src/main/resources/template/html5/*.ftl` (45 files)

### Modified
- None (no code changes needed)

### Unchanged
- `apps/showcase/src/main/java/org/apache/struts2/showcase/action/Html5Action.java`
- `apps/showcase/src/main/webapp/WEB-INF/html5/index.jsp`
- `apps/showcase/src/main/resources/struts.xml`

## Git Commit Plan

**Commit Message**:
```
feat(themes): move html5 theme from showcase to core

- Move 45 FreeMarker templates to core/src/main/resources/template/html5/
- Remove duplicate templates from showcase
- Makes html5 theme available to all applications via struts2-core.jar
- Enables unit testing in core module

Resolves WW-5444

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

**Branch**: `feature/WW-5444-html5` (current)

**Conventional Commits Type**: `feat` (new feature - production theme availability)

## Risk Assessment

**Low Risk**:
- No code changes, only file moves
- Templates already working in showcase
- Easy to rollback (git revert)
- No breaking changes to existing code

**Potential Issues**:
1. **Classpath ordering** - Shouldn't be an issue (core loaded before showcase)
2. **Build time** - Minimal impact (same number of files, different location)
3. **Caching** - Maven/IDE may need clean build

**Mitigation**:
- Test showcase thoroughly after move
- Run full test suite
- Clean build if caching issues occur

## Acceptance Criteria

- [ ] All 45 templates exist in `core/src/main/resources/template/html5/`
- [ ] No templates remain in `apps/showcase/src/main/resources/template/html5/`
- [ ] Showcase builds successfully
- [ ] Showcase /html5/index.action renders correctly
- [ ] Core tests pass (`mvn test -DskipAssembly`)
- [ ] Full build succeeds (`mvn clean install`)
- [ ] Git history shows file moves (if using `git mv`)

## Timeline Estimate

**Effort**: 30-60 minutes

- Phase 1: Create directory (1 min)
- Phase 2: Move templates (5 min)
- Phase 3: theme.properties decision (skip)
- Phase 4: Delete from showcase (1 min)
- Phase 5: Verify showcase (15-20 min)
- Phase 6: Run core tests (10-15 min)
- Phase 7: Documentation (optional, later)

**Total**: ~30 minutes of actual work + ~20 minutes of verification/testing

## Dependencies

**None** - This is a standalone change that doesn't depend on other work

## References

- **Research Document**: [HTML5 Theme Testing Approach](../research/2025-11-23-WW-5444-html5-theme-testing.md)
- **Jira Issue**: [WW-5444 - Implement a new html5 theme](https://issues.apache.org/jira/browse/WW-5444)
- **Git Branch**: `feature/WW-5444-html5`
- **Current Commit**: `461c06d0c5afde260fdb0f32038a32f2af5bb42e`

## Status

**Current**: Pending - Awaiting execution
**Last Updated**: 2025-11-23T17:55:00+01:00