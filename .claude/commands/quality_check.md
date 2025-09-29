# Quality Check Command

You are tasked with performing comprehensive code quality analysis of the Apache Struts codebase using specialized quality analysis agents.

## Initial Setup

When this command is invoked, respond with:
```
I'm ready to perform a comprehensive code quality analysis of your Apache Struts project. This will evaluate JavaDoc compliance, coding standards, pattern consistency, and overall code maintainability.

What type of quality analysis would you like me to perform?
1. Full quality audit (comprehensive analysis across all dimensions)
2. Documentation review (JavaDoc and code documentation focus)
3. Coding standards check (style, conventions, and patterns)
4. Security-focused quality review (secure coding practices)
5. Maintainability assessment (code complexity and structure)
6. Pre-commit quality validation (recent changes focus)
7. Release readiness quality gate
```

Then wait for the user's selection or specific quality requirements.

## Quality Analysis Process

### 1. Analysis Scope Determination

Based on user selection, determine quality analysis scope:

**Full Quality Audit:**
- Complete codebase documentation analysis
- Comprehensive coding standards validation
- Pattern consistency assessment
- Security-focused quality review
- Maintainability and complexity analysis

**Documentation Review:**
- JavaDoc coverage and completeness
- Security documentation compliance
- API documentation quality
- Code comment adequacy
- Usage example validation

**Coding Standards Check:**
- Naming convention compliance
- Code organization and structure
- Import organization and dependencies
- Method scope and accessibility
- Exception handling patterns

**Security-Focused Quality Review:**
- Secure coding pattern compliance
- Resource management security
- Input validation implementation
- Error handling security
- Security documentation completeness

**Maintainability Assessment:**
- Code complexity analysis
- Method and class size validation
- Dependency analysis
- Code duplication detection
- Refactoring opportunity identification

**Pre-commit Quality Validation:**
- Quality analysis of recent changes
- Style compliance for new code
- Documentation for new features
- Pattern consistency in changes

**Release Readiness Quality Gate:**
- Complete quality compliance check
- Documentation readiness
- Code stability assessment
- Performance quality validation

### 2. Quality Analysis Execution

**Launch the code-quality-checker agent with appropriate scope:**

For comprehensive quality audit:
```
Use the code-quality-checker agent to perform a complete code quality analysis:
- Analyze JavaDoc coverage and documentation quality
- Validate coding standards compliance across all files
- Review pattern consistency (Action, Interceptor, Result patterns)
- Assess resource management and cleanup patterns
- Evaluate security coding practices
- Generate comprehensive quality metrics and recommendations

Focus on identifying quality issues that impact maintainability, security, and developer productivity.
```

For documentation-focused review:
```
Use the code-quality-checker agent to focus on documentation quality:
- Analyze JavaDoc coverage for public classes and methods
- Validate security documentation requirements
- Review API documentation completeness
- Check for proper usage examples in documentation
- Assess code comment quality and usefulness
- Identify missing or inadequate documentation

Prioritize security documentation and public API documentation completeness.
```

For coding standards validation:
```
Use the code-quality-checker agent to validate coding standards:
- Check naming conventions for Actions, Interceptors, Results
- Validate code organization and package structure
- Review import statements and dependency usage
- Assess method scope and accessibility patterns
- Analyze exception handling consistency
- Evaluate code formatting and style compliance

Focus on consistency and adherence to Apache Struts coding conventions.
```

### 3. Supporting Analysis

Based on quality check type, may launch additional agents:

**Configuration Quality (for comprehensive audits):**
```
Use the config-validator agent to assess configuration quality:
- Analyze configuration organization and structure
- Validate configuration documentation
- Check configuration consistency across modules
- Review configuration security practices
```

**Security Quality Integration (for security-focused reviews):**
```
Use the security-analyzer agent to validate security quality aspects:
- Review secure coding pattern implementation
- Analyze security-critical code quality
- Validate security documentation adequacy
- Check security test code quality
```

**Architecture Pattern Analysis (for maintainability assessments):**
```
Use the codebase-pattern-finder agent to analyze architectural quality:
- Identify inconsistent pattern usage
- Find examples of good and bad patterns
- Analyze architectural decision consistency
- Review framework integration patterns
```

### 4. Quality Metrics and Reporting

After analysis completion:

1. **Compile quality metrics** from all analysis dimensions
2. **Calculate quality scores** and compliance percentages
3. **Identify quality trends** and improvement areas
4. **Prioritize quality issues** by impact and effort
5. **Generate actionable improvement recommendations**
6. **Create comprehensive quality report**

## Quality Analysis Report Structure

Generate a detailed quality analysis report:

```markdown
# Code Quality Analysis Report - [Date/Time]

## Executive Summary
- **Overall Quality Score**: [percentage]/100
- **Quality Rating**: [Excellent/Good/Needs Improvement/Poor]
- **Files Analyzed**: [number]
- **Quality Issues Found**: [total number]
- **Analysis Scope**: [description of analysis performed]

## Quality Dimensions Assessment

### Documentation Quality (📝) - [Score]/100
- **JavaDoc Coverage**: [percentage]
- **API Documentation**: [Complete/Incomplete]
- **Security Documentation**: [Compliant/Non-compliant]
- **Usage Examples**: [Adequate/Missing]

#### Documentation Issues
- **Missing JavaDoc**: [number] classes, [number] methods
- **Inadequate Documentation**: [number] security-critical methods
- **Missing Examples**: [number] complex classes without usage examples

#### Critical Documentation Gaps
1. **[ClassName.java]** - Missing class-level JavaDoc with security implications
2. **[MethodName.java:line]** - Missing security documentation for file handling method
3. **[ComponentName.java]** - Missing usage examples for complex API

### Coding Standards (⚡) - [Score]/100
- **Naming Conventions**: [Compliant/Issues Found]
- **Code Organization**: [Well-structured/Needs Improvement]
- **Import Management**: [Clean/Needs Cleanup]
- **Method Scope**: [Appropriate/Needs Review]

#### Standards Violations
- **Naming Issues**: [number] violations
- **Organization Issues**: [number] structural problems
- **Import Problems**: [number] wildcard imports or unused imports
- **Scope Issues**: [number] inappropriate method/field visibility

#### Critical Standards Issues
1. **[File:line]** - Incorrect Action naming pattern
2. **[File:line]** - Inappropriate method scope for extensibility
3. **[File:line]** - Missing proper exception handling

### Pattern Consistency (🎯) - [Score]/100
- **Action Patterns**: [Consistent/Inconsistent]
- **Interceptor Patterns**: [Standard/Non-standard]
- **Result Patterns**: [Uniform/Mixed]
- **Validation Patterns**: [Consistent/Inconsistent]

#### Pattern Inconsistencies
- **Action Inconsistencies**: [number] deviations from standard patterns
- **Interceptor Issues**: [number] non-standard implementations
- **Result Problems**: [number] inconsistent result usage
- **Validation Issues**: [number] mixed validation approaches

### Resource Management (🔧) - [Score]/100
- **File Handling**: [Secure/Insecure patterns found]
- **Stream Management**: [Proper/Improper usage]
- **Cleanup Patterns**: [Implemented/Missing]
- **Memory Management**: [Efficient/Inefficient]

#### Resource Management Issues
- **Insecure File Creation**: [number] instances
- **Missing Resource Cleanup**: [number] violations
- **Stream Leaks**: [number] potential leaks
- **Memory Issues**: [number] inefficient patterns

### Security Code Quality (🔒) - [Score]/100
- **Secure Patterns**: [Percentage implemented]
- **Input Validation**: [Comprehensive/Gaps found]
- **Error Handling**: [Secure/Potential leaks]
- **Resource Security**: [Secure/Vulnerable patterns]

#### Security Quality Issues
- **Insecure Patterns**: [number] security anti-patterns found
- **Missing Validation**: [number] input validation gaps
- **Information Disclosure**: [number] potential disclosure issues
- **Resource Vulnerabilities**: [number] insecure resource handling

### Maintainability (🏗️) - [Score]/100
- **Code Complexity**: [Low/Medium/High]
- **Method Length**: [Appropriate/Too long]
- **Class Size**: [Manageable/Too large]
- **Coupling**: [Loose/Tight]

#### Maintainability Concerns
- **High Complexity**: [number] methods with cyclomatic complexity > 10
- **Long Methods**: [number] methods > 50 lines
- **Large Classes**: [number] classes > 500 lines
- **Tight Coupling**: [number] classes with high coupling

## Quality Metrics Summary

### Coverage Metrics
- **Documentation Coverage**: [percentage]
- **Standards Compliance**: [percentage]
- **Pattern Consistency**: [percentage]
- **Security Quality**: [percentage]

### Complexity Metrics
- **Average Cyclomatic Complexity**: [number]
- **Average Method Length**: [number] lines
- **Average Class Size**: [number] lines
- **Dependency Count**: [number]

### Technical Debt Assessment
- **High-Priority Debt**: [number] items requiring immediate attention
- **Medium-Priority Debt**: [number] items for short-term improvement
- **Low-Priority Debt**: [number] items for long-term enhancement
- **Estimated Effort**: [person-days] to address critical issues

## Quality Improvement Recommendations

### Immediate Actions (🔴) - [Timeline: 1-2 weeks]
1. **Address Critical Documentation Gaps**
   - Add JavaDoc to [number] security-critical classes
   - Document security implications for file handling methods
   - Create usage examples for complex APIs

2. **Fix Standards Violations**
   - Correct [number] naming convention violations
   - Fix [number] inappropriate method scope issues
   - Resolve [number] import organization problems

3. **Implement Missing Security Patterns**
   - Fix [number] insecure file creation patterns
   - Add [number] missing resource cleanup implementations
   - Improve [number] input validation implementations

### Short-term Improvements (🟡) - [Timeline: 1-2 months]
1. **Enhance Pattern Consistency**
   - Standardize [number] inconsistent Action implementations
   - Align [number] Interceptor patterns with framework standards
   - Unify [number] mixed validation approaches

2. **Improve Maintainability**
   - Refactor [number] overly complex methods
   - Split [number] large classes into smaller components
   - Reduce coupling in [number] tightly coupled classes

3. **Documentation Enhancement**
   - Add comprehensive examples to [number] complex classes
   - Improve API documentation for [number] public interfaces
   - Enhance security documentation coverage

### Long-term Strategy (🔵) - [Timeline: 3+ months]
1. **Architectural Quality Improvements**
   - Implement consistent error handling strategy
   - Establish code review quality gates
   - Create automated quality validation tools

2. **Process Improvements**
   - Integrate quality checks into CI/CD pipeline
   - Establish quality metrics tracking
   - Implement automated documentation generation

3. **Team Development**
   - Conduct quality-focused code review training
   - Establish coding standards documentation
   - Create quality improvement guidelines

## Quality Trends Analysis
[If previous analysis available]
- **Quality Score Trend**: [improving/stable/declining]
- **Documentation Trend**: [improvement/degradation in coverage]
- **Standards Compliance**: [trend analysis]
- **Technical Debt**: [accumulation/reduction trends]

## Quality Validation Steps

### Immediate Validation
```bash
# Check documentation generation
mvn javadoc:javadoc

# Validate code formatting
mvn spotless:check

# Run static analysis
mvn spotbugs:check
mvn checkstyle:check
```

### Automated Quality Gates
```bash
# Quality threshold validation
mvn sonar:sonar  # If SonarQube is configured

# Dependency analysis
mvn dependency:analyze

# Test coverage validation
mvn jacoco:check
```

## Integration with Development Workflow

### Pre-commit Quality Checks
- Mandatory JavaDoc for new public methods
- Automated style and standards validation
- Security pattern compliance verification
- Documentation completeness check

### Code Review Quality Focus
- Documentation review for new features
- Pattern consistency validation
- Security quality assessment
- Maintainability impact analysis

### Continuous Quality Monitoring
- Daily quality metric tracking
- Weekly quality trend analysis
- Monthly quality improvement planning
- Quarterly technical debt assessment

## Quality Tools and Automation

### Recommended Tools
- **Checkstyle**: Coding standards enforcement
- **SpotBugs**: Static analysis for bug detection
- **PMD**: Code quality and complexity analysis
- **SonarQube**: Comprehensive quality analysis
- **JaCoCo**: Test coverage analysis

### IDE Integration
- Real-time quality feedback
- Automated code formatting
- Documentation generation
- Quality metric display

## Next Steps
1. Address critical quality issues immediately
2. Implement quality improvement recommendations
3. Establish quality monitoring processes
4. Integrate quality checks into development workflow
5. Schedule regular quality assessments

## Resources and References
- [Apache Struts Coding Standards]
- [Java Code Quality Best Practices]
- [Security Coding Guidelines]
- [Documentation Standards Guide]
- [Refactoring and Maintainability Guidelines]
```

## Quality Analysis Best Practices

### 1. Holistic Quality Assessment
- Evaluate multiple quality dimensions simultaneously
- Consider interdependencies between quality aspects
- Balance immediate fixes with long-term improvements
- Align quality standards with team capabilities

### 2. Actionable Recommendations
- Provide specific, measurable improvement suggestions
- Prioritize recommendations by impact and effort
- Include timeline estimates for improvements
- Offer alternative approaches for complex issues

### 3. Continuous Improvement
- Track quality trends over time
- Establish quality improvement goals
- Regular reassessment of quality standards
- Team training and development planning

### 4. Integration with Development Process
- Embed quality checks in daily development workflow
- Establish quality gates for releases
- Automate quality validation where possible
- Provide real-time quality feedback to developers

Remember: Code quality in Apache Struts applications directly impacts security, maintainability, and team productivity. Consistent quality practices lead to more secure and reliable software.