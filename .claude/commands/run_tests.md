# Run Tests Command

You are tasked with intelligently executing and analyzing Apache Struts tests using the specialized test-runner agent and other supporting agents as needed.

## Initial Setup

When this command is invoked, respond with:
```
I'm ready to execute and analyze tests for your Apache Struts project. I can run various types of tests and provide detailed analysis of results.

What type of test execution would you like?
1. Full test suite (all modules with coverage analysis)
2. Quick smoke tests (essential functionality only)
3. Module-specific tests (choose specific Maven modules)
4. Pattern-based tests (run tests matching specific patterns)
5. Security-focused tests (security and vulnerability tests)
6. Performance validation tests (performance regression detection)
7. Pre-commit validation (tests for recent changes)
```

Then wait for the user's selection or specific test requirements.

## Test Execution Process

### 1. Test Strategy Determination

Based on user selection, determine test execution strategy:

**Full Test Suite:**
- Execute complete test suite across all modules
- Generate comprehensive coverage reports
- Analyze performance trends
- Validate security test coverage

**Quick Smoke Tests:**
- Focus on critical functionality tests
- Fast execution for rapid feedback
- Essential security tests included
- Core module validation

**Module-Specific Tests:**
- Ask user to specify modules (core, plugins/*, apps/*)
- Execute tests for selected modules only
- Module-specific coverage analysis
- Inter-module dependency validation

**Pattern-Based Tests:**
- Ask user for test patterns or component types
- Execute tests matching patterns
- Focused analysis on specific functionality
- Related component testing

**Security-Focused Tests:**
- All security-related test execution
- OGNL injection prevention tests
- Parameter filtering validation
- File upload security tests

**Performance Validation:**
- Performance regression detection
- Load testing execution
- Memory usage analysis
- Response time validation

**Pre-commit Validation:**
- Tests affected by recent changes
- Critical path validation
- Security regression prevention
- Quick feedback cycle

### 2. Test Execution Strategy

**Launch the test-runner agent with appropriate parameters:**

For full test suite:
```
Use the test-runner agent to execute the complete Apache Struts test suite:
- Run all tests across core, plugins, and apps modules
- Generate comprehensive coverage reports using JaCoCo
- Analyze test execution performance and identify slow tests
- Validate security test coverage for critical components
- Provide detailed failure analysis with remediation guidance
- Include performance regression detection

Execute with: mvn clean test jacoco:report -DskipAssembly
```

For quick smoke tests:
```
Use the test-runner agent to execute essential smoke tests:
- Focus on critical functionality in core module
- Run basic security tests (OGNL, parameter handling)
- Execute key integration tests
- Validate basic plugin functionality
- Provide rapid feedback on test status

Execute with: mvn test -Dtest=*Smoke*Test,*Security*Test -DskipAssembly
```

For module-specific testing:
```
Use the test-runner agent to execute tests for [specified modules]:
- Run comprehensive tests for selected modules
- Analyze module-specific test coverage
- Validate inter-module dependencies
- Check for module-specific performance issues
- Provide module-focused remediation guidance

Execute with: mvn test -pl [modules] -DskipAssembly
```

### 3. Supporting Analysis

Based on test type, may launch additional agents:

**Security Test Validation (for security-focused testing):**
```
Use the security-analyzer agent to validate security test coverage:
- Analyze security test completeness
- Identify missing security test scenarios
- Validate security test effectiveness
- Check for security regression test gaps
```

**Code Quality Impact (for comprehensive testing):**
```
Use the code-quality-checker agent to assess test quality:
- Analyze test code quality and patterns
- Validate test coverage adequacy
- Review test documentation and maintainability
- Identify test code improvements needed
```

**Configuration Testing (when relevant):**
```
Use the config-validator agent to validate configuration-related tests:
- Check configuration parsing test coverage
- Validate configuration validation tests
- Ensure configuration security tests are comprehensive
```

### 4. Results Analysis and Reporting

After test execution completes:

1. **Compile test results** from all modules and test types
2. **Analyze failure patterns** and categorize by severity
3. **Generate coverage reports** and identify gaps
4. **Assess performance trends** and regression indicators
5. **Provide specific remediation guidance** for failures
6. **Generate comprehensive test report**

## Test Report Structure

Generate a detailed test execution report:

```markdown
# Test Execution Report - [Date/Time]

## Executive Summary
- **Test Suite**: [description of tests executed]
- **Overall Status**: [PASSED/FAILED]
- **Total Tests**: [number] ([passed]/[failed]/[skipped])
- **Execution Time**: [duration]
- **Coverage**: [overall percentage]

## Test Results by Module

### Core Module
- **Tests Executed**: [number]
- **Status**: [passed]/[total] ✅❌
- **Coverage**: [percentage]
- **Execution Time**: [duration]
- **Key Failures**: [summary of critical failures]

### Plugins
#### JSON Plugin
- **Tests**: [passed]/[total]
- **Status**: [overall status]
- **Notable Issues**: [any plugin-specific problems]

#### [Other Plugins]
[Similar breakdown for each plugin]

### Applications
#### Showcase App
- **Integration Tests**: [passed]/[total]
- **Status**: [overall status]
- **Performance**: [any performance issues noted]

## Detailed Failure Analysis

### Critical Failures (🔴)
1. **[TestClass.testMethod]**
   - **Module**: [module name]
   - **Error**: [detailed error message]
   - **Root Cause**: [analysis of why test failed]
   - **Impact**: [functional impact of failure]
   - **Remediation**: [specific steps to fix]
   - **Related Tests**: [other tests that might be affected]

### High-Priority Failures (🟠)
[Similar format for high-priority failures]

### Medium-Priority Failures (🟡)
[Similar format for medium-priority failures]

## Test Coverage Analysis

### Overall Coverage
- **Line Coverage**: [percentage]
- **Branch Coverage**: [percentage]
- **Method Coverage**: [percentage]

### Critical Component Coverage
- **Security Components**: [percentage]
  - OGNL handling: [percentage]
  - Parameter processing: [percentage]
  - File upload: [percentage]
- **Core Framework**: [percentage]
  - Actions: [percentage]
  - Interceptors: [percentage]
  - Results: [percentage]

### Coverage Gaps
- **Uncovered Critical Paths**: [list with file:line references]
- **Missing Security Tests**: [identified gaps]
- **Insufficient Integration Coverage**: [areas needing more tests]

## Performance Analysis

### Test Execution Performance
- **Slowest Tests**:
  1. [TestClass.testMethod] - [duration]
  2. [TestClass.testMethod] - [duration]
  3. [TestClass.testMethod] - [duration]

### Module Performance
- **Core Module**: [execution time] ([change from baseline])
- **Plugin Tests**: [execution time] ([change from baseline])
- **Integration Tests**: [execution time] ([change from baseline])

### Performance Trends
- **Overall Trend**: [improving/degrading/stable]
- **Regression Indicators**: [any concerning performance changes]
- **Resource Usage**: [memory/CPU usage analysis]

## Security Test Validation

### Security Test Coverage
- **OGNL Injection Tests**: [status] ([passed]/[total])
- **Parameter Security Tests**: [status] ([passed]/[total])
- **File Upload Security**: [status] ([passed]/[total])
- **Authentication Tests**: [status] ([passed]/[total])
- **Configuration Security**: [status] ([passed]/[total])

### Security Test Quality
- **Test Completeness**: [assessment of security test coverage]
- **Attack Vector Coverage**: [analysis of tested attack scenarios]
- **Missing Security Tests**: [identified gaps in security testing]

## Quality Metrics

### Test Code Quality
- **Test Maintainability**: [assessment]
- **Test Documentation**: [quality of test documentation]
- **Test Patterns**: [consistency of testing patterns]
- **Test Isolation**: [degree of test independence]

### Technical Debt
- **Flaky Tests**: [list of unstable tests]
- **Skipped Tests**: [analysis of why tests are skipped]
- **Outdated Tests**: [tests that may need updating]

## Environment and Configuration

### Test Environment
- **Java Version**: [version used for testing]
- **Maven Version**: [version]
- **Test Configuration**: [key test settings]
- **Parallel Execution**: [whether parallel execution was used]

### Build Information
- **Build Command**: [exact Maven command executed]
- **Build Profiles**: [profiles used during testing]
- **System Properties**: [relevant system properties]

## Recommendations

### Immediate Actions (🔴)
1. **Fix Critical Test Failures**: [specific actions needed]
2. **Address Security Test Gaps**: [security testing improvements]
3. **Resolve Performance Regressions**: [performance fixes needed]

### Short-term Improvements (🟡)
1. **Improve Test Coverage**: [areas needing more tests]
2. **Optimize Slow Tests**: [test performance improvements]
3. **Fix Flaky Tests**: [stability improvements needed]

### Long-term Strategy (🔵)
1. **Test Architecture**: [improvements to test structure]
2. **Automation Enhancement**: [CI/CD testing improvements]
3. **Performance Monitoring**: [ongoing performance tracking]

## Next Steps
1. Address critical test failures immediately
2. Review and implement coverage improvements
3. Optimize test execution performance
4. Enhance security test coverage
5. Update test documentation and procedures

## Verification Commands
```bash
# Re-run failed tests
mvn test -Dtest=[FailedTestClass] -DskipAssembly

# Generate fresh coverage report
mvn clean test jacoco:report -DskipAssembly

# Run specific test categories
mvn test -Dtest=*Security*Test -DskipAssembly
mvn test -Dtest=*Integration*Test -DskipAssembly
```

## Resources
- [Maven Surefire Documentation]
- [JaCoCo Coverage Analysis]
- [Struts Testing Best Practices]
- [Test Performance Optimization Guide]
```

## Test Execution Best Practices

### 1. Efficient Test Execution
- Always use `-DskipAssembly` to avoid building documentation
- Use parallel execution (`-T 1C`) for large test suites when safe
- Leverage test patterns to run relevant tests only
- Cache dependencies to reduce setup time

### 2. Test Quality Assurance
- Ensure test isolation and repeatability
- Validate test coverage meets minimum thresholds
- Monitor test execution trends for performance regressions
- Maintain comprehensive security test coverage

### 3. Failure Handling and Analysis
- Categorize failures by severity and functional impact
- Provide clear remediation guidance for each failure
- Track failure patterns across different environments
- Implement automatic retry for known flaky tests

### 4. Performance Monitoring
- Track test execution times and identify trends
- Monitor resource usage during test execution
- Identify and optimize slow tests
- Set performance thresholds for regression detection

## Integration with Development Workflow

### 1. Pre-commit Testing
```bash
# Quick validation before commit
/run_tests quick

# Security-focused validation
/run_tests security

# Module-specific testing
/run_tests module core
```

### 2. CI/CD Integration
- Automated test execution in build pipeline
- Test result analysis and reporting
- Performance regression detection
- Coverage trend monitoring

### 3. Release Validation
- Comprehensive test suite execution
- Performance benchmark validation
- Security test compliance verification
- Integration test coverage validation

## Emergency Test Response

If critical test failures are detected:

1. **Immediate Assessment**: Determine if failures indicate functional regression
2. **Impact Analysis**: Assess business impact of failing functionality
3. **Root Cause Analysis**: Investigate underlying cause of failures
4. **Fix Prioritization**: Prioritize fixes based on severity and impact
5. **Validation**: Ensure fixes don't introduce new regressions
6. **Process Improvement**: Analyze how to prevent similar failures

Remember: Testing is crucial for maintaining Struts application quality and security. Always prioritize security tests and ensure comprehensive coverage of critical functionality.