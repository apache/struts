# Validate Implementation Plan

You are tasked with systematically verifying the successful implementation of a software development plan for Apache Struts development. This command helps ensure that implementation plans were executed correctly, success criteria were met, and all expected changes were implemented according to specifications.

## Initial Setup

When this command is invoked, respond with:
```
I'm ready to validate your implementation plan. Please provide:
1. The path to the implementation plan (e.g., thoughts/shared/plans/YYYY-MM-DD-WW-XXXX-feature.md)
2. Or describe what was implemented so I can locate the relevant plan

I'll systematically verify that the implementation matches the plan's requirements and success criteria.
```

Then wait for the user's validation request.

## Validation Methodology

### Phase 1: Context Discovery & Setup

1. **Locate Implementation Plan:**
   - If user provides a specific plan path, read it fully using Read tool
   - If no path provided, use **thoughts-locator** agent to find relevant implementation plans
   - Search for recent plans in `thoughts/shared/plans/` matching the user's description
   - Look for WW-XXXX ticket patterns if mentioned

2. **Plan Analysis:**
   - Use **thoughts-analyzer** agent to extract key details from the implementation plan:
     - Expected file changes and new components
     - Success criteria (automated and manual)
     - Security requirements and CVE mitigations
     - Performance expectations
     - Testing requirements

3. **Setup Validation Tracking:**
   - Use TodoWrite to create validation checklist based on plan requirements
   - Mark validation phases as pending initially

### Phase 2: Parallel Implementation Verification

**Launch multiple Task agents concurrently to verify different aspects:**

4. **Codebase Structure Verification:**
   - Use **codebase-locator** agent to find all files mentioned in the implementation plan
   - Verify that expected new files were created
   - Check that expected modifications were made to existing files
   - Confirm Maven module structure changes (`/core/`, `/plugins/`, `/apps/`, `/jakarta/`)

5. **Implementation Quality Analysis:**
   - Use **codebase-analyzer** agent to analyze the implemented components:
     - Maven dependencies and build configuration changes
     - Security implementations (OGNL patterns, CVE mitigations)
     - Interceptor stack integration and ordering
     - Plugin architecture compliance
   - Verify adherence to Apache Struts architectural patterns

6. **Pattern Compliance Verification:**
   - Use **codebase-pattern-finder** agent to check if implementations follow established patterns:
     - Action class patterns (ActionSupport, ModelDriven)
     - Interceptor implementations and configurations
     - Result type usage and custom implementations
     - Validation approach consistency (XML vs annotations)
     - Security patterns for OGNL injection prevention

### Phase 3: Automated Verification

7. **Build and Test Verification:**
   - Execute `mvn clean install` to verify successful build
   - Run `mvn test -DskipAssembly` to execute test suite
   - Check that all tests pass as expected in the plan
   - Verify no new build errors or warnings introduced

8. **Security Validation:**
   - Search for OGNL expressions that might introduce vulnerabilities
   - Verify parameter filtering and validation implementations
   - Check for proper security interceptor configurations
   - Validate CVE mitigation patterns (CVE-2017-5638, CVE-2018-11776, etc.)

9. **Performance Assessment:**
   - Analyze impact on request processing pipeline
   - Check interceptor stack execution overhead
   - Verify memory usage patterns align with expectations
   - Confirm no performance regressions introduced

### Phase 4: Historical Verification

10. **Git History Analysis:**
    - Run `git log --oneline --since="[plan-date]"` to see commits since plan creation
    - Verify expected commits were made
    - Check commit messages align with plan requirements
    - Confirm no unexpected changes were introduced

11. **Documentation and Configuration:**
    - Verify configuration files were updated as planned (struts.xml, struts-plugin.xml)
    - Check that JavaDoc documentation was added as specified
    - Confirm example applications were updated if required
    - Validate that migration documentation was created if needed

### Phase 5: Comprehensive Assessment

12. **Success Criteria Evaluation:**
    - Check each automated criterion from the plan (build passes, tests pass, etc.)
    - Evaluate manual criteria based on available evidence
    - Assess security requirements compliance
    - Verify performance benchmarks if specified

13. **Gap Analysis:**
    - Identify any plan requirements that weren't implemented
    - Document deviations from the original plan
    - Note any additional work done beyond the plan scope
    - Highlight potential issues or concerns

### Phase 6: Validation Report Generation

14. **Generate Validation Report:**
    - Create comprehensive report at `thoughts/shared/validation/YYYY-MM-DD-WW-XXXX-validation.md`
    - Use consistent naming with date and ticket number
    - Include YAML frontmatter with validation metadata

## Validation Report Structure

```markdown
---
date: [ISO format date and time with timezone]
plan_validated: "[path to original implementation plan]"
validation_status: "[complete|partial|failed]"
ticket: "[WW-XXXX if applicable]"
tags: [validation, struts, relevant-components]
issues_found: [number of issues]
success_rate: "[percentage of criteria met]"
---

# Validation Report: [Implementation Topic]

**Date**: [Current date and time with timezone]
**Original Plan**: [`thoughts/shared/plans/plan-file.md`](link)
**Validation Status**: [Complete/Partial/Failed]

## Executive Summary
[High-level assessment: Was the plan successfully implemented?]

## Implementation Plan Analysis
### Original Requirements
- [Requirement 1 from plan]
- [Requirement 2 from plan]
- [etc.]

### Success Criteria from Plan
#### Automated Criteria
- [ ] All existing tests pass: `mvn test -DskipAssembly`
- [ ] Build completes successfully: `mvn clean install`
- [ ] [Other automated criteria from plan]

#### Manual Criteria
- [ ] [Manual criterion 1]
- [ ] [Manual criterion 2]
- [ ] [etc.]

## Verification Results

### Codebase Structure ✅/❌
**Expected Changes**: [From plan]
**Actual Implementation**: [What was found]
**Status**: [Complete/Partial/Missing]

#### Files Created/Modified
- `path/to/file.java:123` - ✅ Implemented as planned
- `another/file.xml:45-67` - ❌ Missing expected configuration
- `new/component.java` - ✅ Created with proper patterns

### Security Implementation ✅/❌
**Security Requirements**: [From plan]
**Verification Results**:
- OGNL injection prevention: [Status and details]
- Parameter filtering: [Implementation found/missing]
- CVE mitigations: [Specific patterns verified]
- Interceptor security: [Configuration validation]

### Testing Verification ✅/❌
**Build Results**:
```
mvn clean install
[Build output summary]

mvn test -DskipAssembly
[Test results summary]
```

**Test Coverage**: [New tests created vs planned]
**Integration Tests**: [End-to-end validation results]

### Performance Analysis ✅/❌
**Expected Impact**: [From plan]
**Measured Impact**: [Actual findings]
- Request processing overhead: [Assessment]
- Memory usage: [Analysis]
- Interceptor stack performance: [Evaluation]

### Architecture Compliance ✅/❌
**Pattern Adherence**:
- Action patterns: [Compliance assessment]
- Interceptor patterns: [Implementation quality]
- Result types: [Usage validation]
- Maven structure: [Module organization]

### Configuration Validation ✅/❌
**struts.xml Changes**: [Verification results]
**Plugin Configurations**: [struts-plugin.xml validation]
**Default Settings**: [Property changes verification]

## Git History Analysis
**Commits Since Plan**: [Number and summary]
**Expected Commits**: [From plan vs actual]
**Commit Quality**: [Message quality and atomicity]

## Issue Analysis

### Critical Issues (🔴)
[Issues that break functionality or security]

### Minor Issues (🟡)
[Issues that deviate from plan but don't break functionality]

### Suggestions (🔵)
[Improvements and optimizations identified]

## Compliance Assessment

### Requirements Compliance
- **Fully Implemented**: [X of Y requirements]
- **Partially Implemented**: [X of Y requirements]
- **Not Implemented**: [X of Y requirements]
- **Additional Work**: [Items done beyond plan scope]

### Success Criteria Met
- **Automated Criteria**: [X of Y passed]
- **Manual Criteria**: [X of Y verified]
- **Overall Success Rate**: [Percentage]%

## Recommendations

### Immediate Actions Required
[Critical items that must be addressed]

### Suggested Improvements
[Nice-to-have enhancements]

### Future Considerations
[Items for next iteration or follow-up work]

## Code References
- `file.java:123` - [Description of implementation]
- `config.xml:45-67` - [Configuration details]
- `test.java:89` - [Test coverage gaps]

## Related Documentation
- Original Plan: [`thoughts/shared/plans/plan-file.md`](link)
- Implementation commits: [Git references]
- Related tickets: [WW-XXXX references]

## Appendices

### A. Test Output Details
[Detailed test results if significant issues found]

### B. Security Scan Results
[Detailed security verification results]

### C. Performance Benchmarks
[Performance measurement details if applicable]
```

## Apache Struts Specific Validations

### Framework Integration Checks
- **Action Layer**: Verify ActionSupport patterns, ModelDriven implementations
- **Interceptor Stack**: Validate ordering dependencies, security interceptor placement
- **Result Types**: Confirm proper result type usage and custom implementations
- **Plugin Architecture**: Check extension points and configuration compliance
- **OGNL Security**: Validate expression evaluation safety and parameter exclusion

### Security-First Validation
- Always verify OGNL injection prevention in new features
- Check parameter pollution and manipulation attack mitigations
- Validate input sanitization and validation implementations
- Review interceptor security configurations thoroughly
- Confirm CVE mitigation strategies are properly implemented

### Maven Module Validation
- Verify changes to `/core/`, `/plugins/`, `/apps/`, `/jakarta/` modules
- Check build profile implications and compatibility
- Validate dependency management across modules
- Confirm test execution works with `mvn test -DskipAssembly`

### Performance Validation
- Assess request processing pipeline impact
- Measure interceptor stack execution overhead
- Check memory usage patterns and potential leaks
- Validate caching strategies and their effectiveness

## Success Metrics

A successful validation includes:
- ✅ All planned requirements implemented and verified
- ✅ Automated tests pass without regressions
- ✅ Security requirements met with proper CVE mitigations
- ✅ Performance impact within acceptable bounds
- ✅ Code follows established Struts patterns and conventions
- ✅ Configuration changes properly implemented
- ✅ Documentation updated as planned
- ✅ Git history reflects planned development approach

## Important Notes

- **Thorough Verification**: Use all available agents in parallel for comprehensive analysis
- **Security Focus**: Always prioritize security validation for OGNL and CVE patterns
- **Evidence-Based**: Provide concrete file references and line numbers for all findings
- **Actionable Results**: Include specific recommendations for any issues found
- **Historical Context**: Consider the plan's context and decision rationale
- **Complete Coverage**: Verify both planned requirements AND quality of implementation
- **Maven Integration**: Leverage build system for automated verification
- **Documentation**: Generate detailed validation reports for team reference

Remember: The goal is to ensure implementation plans were not just completed, but completed correctly with high quality, security, and adherence to Apache Struts best practices.