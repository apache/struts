# Security Scan Command

You are tasked with performing a comprehensive security analysis of the Apache Struts codebase using specialized security scanning agents.

## Initial Setup

When this command is invoked, respond with:
```
I'm ready to perform a comprehensive security scan of your Apache Struts project. This will analyze the codebase for OGNL injection vulnerabilities, CVE patterns, parameter filtering issues, and other security concerns.

What type of security scan would you like me to perform?
1. Full security audit (comprehensive analysis)
2. Quick security check (focus on critical vulnerabilities)
3. Specific component scan (target specific files/features)
4. Pre-release security validation
```

Then wait for the user's selection.

## Security Scanning Process

### 1. Scan Type Determination
Based on user selection, determine scan scope:

**Full Security Audit:**
- Complete codebase OGNL injection analysis
- Comprehensive parameter filtering review
- File upload security assessment
- Configuration security validation
- Plugin security analysis

**Quick Security Check:**
- Critical CVE pattern detection
- OGNL injection hotspots
- Parameter security quick scan
- Development mode detection

**Specific Component Scan:**
- Ask user for specific files, packages, or features
- Focused analysis on specified components
- Related security dependency analysis

**Pre-release Security Validation:**
- Security regression detection
- New code security analysis
- Configuration security compliance
- Security test validation

### 2. Security Analysis Execution

**Launch the security-analyzer agent with appropriate scope:**

For comprehensive scans:
```
Use the security-analyzer agent to perform a complete security analysis of the Apache Struts codebase, including:
- OGNL injection vulnerability detection
- Parameter filtering and validation analysis
- File upload security assessment
- Interceptor security configuration review
- CVE pattern identification
- Configuration security validation

Focus on identifying critical security issues that could lead to RCE or data exposure.
```

For quick scans:
```
Use the security-analyzer agent to perform a rapid security assessment focusing on:
- Critical OGNL injection patterns
- Missing parameter exclusion configurations
- Development mode detection
- High-risk file upload configurations
- Known CVE patterns (CVE-2017-5638, CVE-2018-11776)

Prioritize findings by risk level and provide immediate remediation guidance.
```

### 3. Configuration Security Validation

**Launch the config-validator agent for configuration analysis:**
```
Use the config-validator agent to analyze security configurations including:
- struts.xml security settings
- Interceptor stack security validation
- Parameter exclusion pattern analysis
- Plugin security configurations
- Development vs production setting validation

Focus on configuration vulnerabilities and security misconfigurations.
```

### 4. Code Quality Security Review

**Launch the code-quality-checker agent for secure coding analysis:**
```
Use the code-quality-checker agent to review code quality from a security perspective:
- Secure coding pattern compliance
- Resource cleanup security (file handling)
- Input validation implementation
- Exception handling security
- Security documentation completeness

Identify areas where poor code quality could lead to security vulnerabilities.
```

### 5. Results Synthesis and Reporting

After all agents complete their analysis:

1. **Compile security findings** from all agents
2. **Prioritize by risk level** (Critical, High, Medium, Low)
3. **Group related findings** to avoid duplication
4. **Provide specific remediation guidance** for each finding
5. **Generate security compliance report**

## Security Report Structure

Generate a comprehensive security report:

```markdown
# Security Scan Report - [Date/Time]

## Executive Summary
- **Overall Security Rating**: [Critical/High/Medium/Low Risk]
- **Critical Vulnerabilities**: [number]
- **High-Risk Issues**: [number]
- **Medium-Risk Issues**: [number]
- **Scan Scope**: [description of what was scanned]

## Critical Security Findings (🔴)

### 1. [Vulnerability Type] - [Severity Score]
- **Location**: `file.java:line`
- **Description**: [Detailed vulnerability description]
- **Risk**: [Potential impact - RCE, data exposure, etc.]
- **CVE Reference**: [If applicable]
- **Remediation**:
  ```java
  // Secure implementation example
  ```
- **Verification**: [How to test the fix]

## High-Risk Security Issues (🟠)
[Similar format for high-risk findings]

## Medium-Risk Security Issues (🟡)
[Similar format for medium-risk findings]

## Configuration Security Assessment

### Parameter Security
- **Parameter Exclusion**: [Status - Secure/Vulnerable]
- **Parameter Validation**: [Implementation quality]
- **Recommendations**: [Specific configuration changes]

### Interceptor Security
- **Security Interceptor Usage**: [Analysis]
- **Stack Ordering**: [Validation results]
- **Missing Security Controls**: [Identified gaps]

### File Upload Security
- **Upload Restrictions**: [Current configuration analysis]
- **Security Controls**: [Validation of restrictions]
- **Recommendations**: [Security improvements needed]

## Development Environment Security
- **Development Mode**: [Production ready/Development detected]
- **Debug Settings**: [Secure/Insecure configurations found]
- **Logging Security**: [Sensitive data exposure analysis]

## Plugin Security Analysis
- **Plugin Configurations**: [Security assessment]
- **Plugin Vulnerabilities**: [Known issues in used plugins]
- **Plugin Updates**: [Security-related updates available]

## Code Quality Security Impact
- **Secure Coding Patterns**: [Compliance assessment]
- **Resource Management**: [Security of file/stream handling]
- **Error Handling**: [Information disclosure prevention]

## Security Testing Coverage
- **Security Test Presence**: [Analysis of security-specific tests]
- **Test Coverage**: [Security-critical code coverage]
- **Recommendations**: [Additional security tests needed]

## Compliance and Standards
- **OWASP Top 10**: [Compliance assessment]
- **Framework Security Guidelines**: [Adherence to Struts security best practices]
- **Industry Standards**: [Compliance with relevant security standards]

## Immediate Actions Required
1. **[Critical Action 1]** - [Timeline: Immediate]
2. **[Critical Action 2]** - [Timeline: Within 24 hours]
3. **[High Priority Action]** - [Timeline: Within 1 week]

## Security Improvement Roadmap
### Short Term (1-2 weeks)
- [List of immediate security improvements]

### Medium Term (1-2 months)
- [Strategic security enhancements]

### Long Term (3+ months)
- [Architectural security improvements]

## Security Validation Steps
```bash
# Commands to verify security fixes
mvn test -Dtest=*Security*Test -DskipAssembly
mvn test -Dtest=*Ognl*Test -DskipAssembly
# Additional validation commands
```

## Resources and References
- [OWASP Struts Security Guidelines]
- [Apache Struts Security Bulletins]
- [CVE References and patches]
- [Security testing resources]

## Next Steps
1. Address critical vulnerabilities immediately
2. Implement recommended configuration changes
3. Enhance security testing coverage
4. Schedule regular security assessments
5. Update security documentation and training
```

## Security Scanning Best Practices

### 1. Regular Scanning Schedule
- Pre-commit security checks for critical changes
- Weekly comprehensive security scans
- Pre-release security validation
- Post-deployment security verification

### 2. Scan Scope Optimization
- Focus on high-risk components (OGNL, file upload, parameter processing)
- Include all configuration files in scope
- Analyze third-party dependencies for known vulnerabilities
- Review custom interceptors and actions thoroughly

### 3. Remediation Prioritization
- **Critical**: RCE vulnerabilities, OGNL injection
- **High**: Parameter pollution, file upload issues
- **Medium**: Configuration weaknesses, information disclosure
- **Low**: Security hardening opportunities

### 4. Validation and Testing
- Verify all security fixes with appropriate tests
- Ensure security changes don't break functionality
- Document security decisions and trade-offs
- Maintain security regression test suite

## Integration with Development Workflow

### 1. Pre-commit Integration
```bash
# Quick security check before commit
/security_scan quick

# Validate specific files
/security_scan specific src/main/java/com/example/NewAction.java
```

### 2. CI/CD Integration
- Automated security scanning in build pipeline
- Security gate criteria for deployment
- Security report generation and storage
- Security trend tracking and alerting

### 3. Security Review Process
- Mandatory security review for security-sensitive changes
- Security expert involvement in major feature reviews
- Security impact assessment for architectural changes
- Regular security training and awareness programs

## Emergency Security Response

If critical vulnerabilities are found:

1. **Immediate Assessment**: Determine if vulnerability is actively exploitable
2. **Risk Mitigation**: Implement temporary mitigations if possible
3. **Fix Development**: Prioritize fix development and testing
4. **Deployment Planning**: Plan emergency deployment if needed
5. **Communication**: Notify stakeholders of security issues and remediation
6. **Post-incident Review**: Analyze how vulnerability was introduced and improve processes

Remember: Security scanning is only effective if findings are acted upon promptly. Always prioritize critical vulnerabilities and maintain a proactive security posture.