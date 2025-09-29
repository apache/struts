# Configuration Analysis Command

You are tasked with performing comprehensive configuration analysis of Apache Struts projects using specialized configuration validation agents.

## Initial Setup

When this command is invoked, respond with:
```
I'm ready to analyze your Apache Struts configuration files for correctness, security, and optimization opportunities. I can examine XML configurations, plugin settings, and framework properties.

What type of configuration analysis would you like me to perform?
1. Complete configuration audit (all struts.xml, plugins, properties)
2. Security configuration review (security settings and vulnerabilities)
3. Performance configuration analysis (optimization opportunities)
4. Plugin configuration validation (plugin-specific configurations)
5. Configuration consistency check (consistency across modules)
6. Migration configuration assessment (Jakarta EE or version upgrade)
7. Specific configuration troubleshooting (target specific config issues)
```

Then wait for the user's selection or specific configuration requirements.

## Configuration Analysis Process

### 1. Analysis Scope Determination

Based on user selection, determine configuration analysis scope:

**Complete Configuration Audit:**
- All struts.xml files across modules
- Plugin configuration validation
- Framework properties analysis
- Security configuration assessment
- Performance configuration review

**Security Configuration Review:**
- Parameter exclusion patterns
- Security interceptor configurations
- Development mode settings
- File upload security settings
- OGNL security configurations

**Performance Configuration Analysis:**
- Interceptor stack optimization
- Caching configuration review
- Resource loading optimization
- Plugin overhead assessment
- Action configuration efficiency

**Plugin Configuration Validation:**
- Plugin-specific struts-plugin.xml files
- Plugin compatibility analysis
- Plugin configuration consistency
- Plugin dependency validation

**Configuration Consistency Check:**
- Cross-module configuration consistency
- Package inheritance validation
- Namespace organization analysis
- Common configuration patterns

**Migration Configuration Assessment:**
- Jakarta EE compatibility analysis
- Version upgrade requirements
- Deprecated configuration detection
- Migration path validation

**Specific Configuration Troubleshooting:**
- Ask user for specific configuration issues
- Targeted analysis of problem areas
- Root cause identification
- Solution recommendations

### 2. Configuration Analysis Execution

**Launch the config-validator agent with appropriate scope:**

For complete configuration audit:
```
Use the config-validator agent to perform comprehensive configuration analysis:
- Validate all struts.xml files for syntax and semantic correctness
- Analyze package inheritance and namespace organization
- Review action mappings and result configurations
- Validate interceptor stack configurations and ordering
- Check plugin configurations and compatibility
- Assess security configuration compliance
- Identify performance optimization opportunities

Focus on configuration correctness, security compliance, and best practices adherence.
```

For security-focused configuration review:
```
Use the config-validator agent to perform security configuration analysis:
- Analyze parameter exclusion patterns and security
- Review security interceptor configurations and ordering
- Check development mode and debug settings
- Validate file upload security configurations
- Assess OGNL security settings and restrictions
- Identify potential security configuration vulnerabilities

Prioritize security misconfigurations that could lead to vulnerabilities.
```

For performance configuration analysis:
```
Use the config-validator agent to analyze performance configuration:
- Review interceptor stack efficiency and ordering
- Analyze action configuration for performance impact
- Check caching configuration and optimization opportunities
- Assess plugin configuration overhead
- Identify configuration bottlenecks and inefficiencies
- Recommend performance optimization changes

Focus on configuration changes that can improve application performance.
```

### 3. Supporting Analysis

Based on configuration analysis type, may launch additional agents:

**Security Integration (for security-focused reviews):**
```
Use the security-analyzer agent to validate security configuration effectiveness:
- Analyze if security configurations actually prevent known attacks
- Validate parameter filtering effectiveness
- Check if security interceptors are properly implemented
- Assess overall security configuration completeness
```

**Jakarta Migration Analysis (for migration assessments):**
```
Use the jakarta-migration-helper agent to analyze configuration migration requirements:
- Identify Jakarta EE compatibility issues in configurations
- Analyze configuration namespace changes needed
- Assess plugin configuration migration requirements
- Provide migration strategy for configurations
```

**Code Quality Integration (for comprehensive audits):**
```
Use the code-quality-checker agent to analyze configuration quality:
- Review configuration organization and maintainability
- Check configuration documentation adequacy
- Analyze configuration complexity and clarity
- Assess configuration testing coverage
```

### 4. Configuration Optimization and Reporting

After analysis completion:

1. **Compile configuration findings** from all analysis areas
2. **Categorize issues** by type and severity
3. **Identify optimization opportunities** for performance and security
4. **Validate configuration best practices** compliance
5. **Generate actionable recommendations** with examples
6. **Create comprehensive configuration report**

## Configuration Analysis Report Structure

Generate a detailed configuration analysis report:

```markdown
# Configuration Analysis Report - [Date/Time]

## Executive Summary
- **Configuration Files Analyzed**: [number]
- **Overall Configuration Health**: [Excellent/Good/Needs Improvement/Critical Issues]
- **Security Compliance**: [Compliant/Non-compliant]
- **Performance Rating**: [Optimized/Good/Needs Optimization]
- **Issues Found**: [total number] ([critical]/[high]/[medium]/[low])

## Configuration Inventory

### Core Configuration Files
- **Main struts.xml**: [path] - [status]
- **Module configurations**: [list of discovered struts.xml files]
- **Properties files**: [list of struts.properties files]
- **Plugin configurations**: [number] struts-plugin.xml files

### Configuration Structure Overview
- **Packages Defined**: [number]
- **Actions Configured**: [number]
- **Interceptor Stacks**: [number]
- **Results Defined**: [number]
- **Plugins Integrated**: [number]

## XML Structure and Syntax Analysis

### Schema Validation
- **DTD Compliance**: [Valid/Invalid]
- **Schema Version**: [detected version]
- **Syntax Errors**: [none/list of errors]
- **Structural Issues**: [none/list of issues]

### Configuration Parsing
- **Parsing Status**: [Successful/Failed]
- **Loading Errors**: [none/list of errors]
- **Validation Warnings**: [none/list of warnings]

## Package and Namespace Analysis

### Package Configuration
- **Package Hierarchy**: [well-organized/needs improvement]
- **Inheritance Structure**: [valid/broken chains]
- **Namespace Organization**: [logical/chaotic]

#### Package Structure Issues
1. **[package-name]** - Invalid inheritance chain
2. **[package-name]** - Namespace conflict with [other-package]
3. **[package-name]** - Missing required parent package

### Namespace Management
- **Namespace Conflicts**: [none/number found]
- **Namespace Coverage**: [complete/gaps identified]
- **URL Mapping**: [consistent/inconsistent]

## Action Configuration Analysis

### Action Mappings
- **Total Actions**: [number]
- **Complete Actions**: [number] (with class, method, results)
- **Incomplete Actions**: [number] (missing components)
- **Dynamic Actions**: [number] (wildcard/DMI usage)

### Action Configuration Quality
- **Proper Result Mapping**: [percentage]%
- **Security Compliance**: [secure/insecure patterns found]
- **Performance Impact**: [optimized/needs improvement]

#### Critical Action Issues
1. **[action-name]** - Missing error result mapping
2. **[action-name]** - Insecure wildcard method mapping
3. **[action-name]** - No class definition specified

## Interceptor Configuration Analysis

### Interceptor Stack Validation
- **Default Stacks**: [number] configured
- **Custom Stacks**: [number] configured
- **Stack Inheritance**: [proper/issues found]

### Security Interceptor Assessment
- **Security Interceptor Usage**: [comprehensive/gaps found]
- **Parameter Filtering**: [properly configured/insufficient]
- **Security Ordering**: [correct/incorrect]

#### Critical Interceptor Issues
1. **[stack-name]** - Security interceptors in wrong order
2. **[stack-name]** - Missing parameter exclusion patterns
3. **[stack-name]** - Vulnerable to parameter pollution

### Interceptor Performance Analysis
- **Stack Efficiency**: [optimized/redundant interceptors found]
- **Execution Order**: [optimal/suboptimal]
- **Performance Impact**: [minimal/concerning overhead]

## Security Configuration Assessment

### Critical Security Settings
- **Development Mode**: [production-ready/development mode enabled]
- **Dynamic Method Invocation**: [disabled/enabled - security risk]
- **OGNL Restrictions**: [properly configured/unrestricted]
- **Debug Settings**: [secure/debug enabled in production]

### Parameter Security Configuration
```xml
<!-- Current parameter exclusion configuration -->
<interceptor-ref name="params">
    <param name="excludeParams">[current patterns]</param>
</interceptor-ref>
```

**Security Assessment**: [secure/vulnerable]
**Recommended Improvements**: [specific pattern additions needed]

### File Upload Security
- **Upload Restrictions**: [properly configured/insufficient]
- **Size Limits**: [appropriate/missing or excessive]
- **Type Restrictions**: [comprehensive/gaps found]
- **Path Security**: [secure/vulnerable to traversal]

#### Security Configuration Issues
1. **Parameter Filtering** - Missing exclusion for [dangerous patterns]
2. **File Upload** - No size restrictions configured
3. **Development Mode** - Enabled in production configuration

## Plugin Configuration Analysis

### Plugin Inventory
- **Active Plugins**: [list with versions]
- **Plugin Compatibility**: [compatible/version conflicts]
- **Configuration Consistency**: [consistent/conflicts found]

### Plugin-Specific Analysis
#### JSON Plugin
- **Configuration Status**: [properly configured/issues found]
- **Security Settings**: [secure/needs review]
- **Performance Impact**: [optimized/overhead concerns]

#### [Other Plugins]
[Similar analysis for each detected plugin]

### Plugin Configuration Issues
1. **[plugin-name]** - Version compatibility issue
2. **[plugin-name]** - Missing required configuration
3. **[plugin-name]** - Security configuration gap

## Performance Configuration Assessment

### Interceptor Performance
- **Stack Optimization**: [optimized/redundancy found]
- **Heavy Interceptors**: [efficient/performance concerns]
- **Execution Overhead**: [minimal/significant]

### Caching Configuration
- **Configuration Caching**: [enabled/disabled]
- **Static Content**: [optimized/unoptimized]
- **Resource Loading**: [efficient/inefficient]

### Performance Optimization Opportunities
1. **Interceptor Stack Reduction** - Remove [specific redundant interceptors]
2. **Caching Enhancement** - Enable [specific caching options]
3. **Resource Optimization** - Optimize [specific resource settings]

## Configuration Best Practices Compliance

### Structural Best Practices
- **Package Organization**: [follows standards/needs improvement]
- **Naming Conventions**: [consistent/inconsistent]
- **Configuration Modularity**: [well-modularized/monolithic]

### Security Best Practices
- **Defense in Depth**: [implemented/gaps found]
- **Least Privilege**: [followed/violations found]
- **Security by Default**: [configured/insecure defaults]

### Performance Best Practices
- **Minimal Configuration**: [optimized/excessive configuration]
- **Efficient Patterns**: [used/inefficient patterns found]
- **Resource Management**: [optimized/wasteful]

## Configuration Issues by Severity

### Critical Issues (🔴) - Immediate Action Required
1. **[file:location]** - Security vulnerability in parameter filtering
2. **[file:location]** - Development mode enabled in production
3. **[file:location]** - Missing security interceptor configuration

### High-Priority Issues (🟠) - Address Soon
1. **[file:location]** - Suboptimal interceptor ordering
2. **[file:location]** - Missing error handling configuration
3. **[file:location]** - Performance bottleneck in stack configuration

### Medium-Priority Issues (🟡) - Plan for Resolution
1. **[file:location]** - Configuration inconsistency across modules
2. **[file:location]** - Missing optimization opportunity
3. **[file:location]** - Documentation gap in configuration

### Low-Priority Issues (🔵) - Future Improvements
1. **[file:location]** - Minor naming convention deviation
2. **[file:location]** - Optional performance enhancement
3. **[file:location]** - Cosmetic configuration cleanup

## Recommendations and Solutions

### Immediate Configuration Changes
```xml
<!-- Security: Update parameter exclusion patterns -->
<interceptor-ref name="params">
    <param name="excludeParams">
        dojo\..*,struts\..*,session\..*,request\..*,
        application\..*,servlet.*,parameters\..*,#.*
    </param>
</interceptor-ref>

<!-- Performance: Optimize interceptor stack -->
<interceptor-stack name="optimizedStack">
    <interceptor-ref name="exception"/>
    <interceptor-ref name="params"/>
    <interceptor-ref name="validation"/>
    <interceptor-ref name="workflow"/>
</interceptor-stack>
```

### Properties Configuration Updates
```properties
# Security: Production settings
struts.devMode=false
struts.enable.DynamicMethodInvocation=false

# Performance: Optimization settings
struts.configuration.xml.reload=false
struts.i18n.reload=false
```

### Plugin Configuration Improvements
[Specific plugin configuration recommendations]

## Migration Considerations

### Jakarta EE Compatibility
- **Current Compatibility**: [compatible/requires changes]
- **Migration Requirements**: [list of changes needed]
- **Plugin Compatibility**: [assessment of plugin Jakarta support]

### Version Upgrade Path
- **Current Framework Version**: [version]
- **Recommended Target**: [version]
- **Configuration Changes**: [list of required updates]

## Configuration Testing and Validation

### Validation Commands
```bash
# Validate XML syntax
xmllint --noout struts.xml

# Test configuration loading
mvn compile

# Security configuration test
mvn test -Dtest=*Security*Test -DskipAssembly
```

### Configuration Quality Checks
```bash
# Check for development mode
grep -r "struts.devMode=true" --include="*.properties" .

# Validate parameter exclusions
grep -r "excludeParams" --include="*.xml" .

# Check plugin configurations
find . -name "struts-plugin.xml" -exec xmllint --noout {} \;
```

## Next Steps

### Immediate Actions (Next 24 hours)
1. Fix critical security configuration issues
2. Disable development mode in production configurations
3. Update parameter exclusion patterns

### Short-term Actions (Next week)
1. Optimize interceptor stack configurations
2. Resolve plugin configuration inconsistencies
3. Implement performance optimization recommendations

### Long-term Improvements (Next month)
1. Standardize configuration patterns across modules
2. Implement configuration validation automation
3. Create configuration documentation and guidelines

## Configuration Maintenance Strategy

### Regular Configuration Review
- Monthly configuration security audit
- Quarterly performance configuration review
- Semi-annual configuration optimization assessment
- Annual configuration architecture review

### Automation and Monitoring
- Automated configuration validation in CI/CD
- Configuration change impact analysis
- Performance monitoring of configuration changes
- Security configuration compliance checking

## Resources and Documentation

### Configuration References
- [Apache Struts Configuration Reference]
- [Struts Security Configuration Guide]
- [Performance Optimization Documentation]
- [Plugin Configuration Examples]

### Validation Tools
- [XML Schema Validation Tools]
- [Configuration Testing Frameworks]
- [Security Configuration Scanners]
- [Performance Analysis Tools]
```

## Configuration Analysis Best Practices

### 1. Comprehensive Scope
- Analyze all configuration files, not just main struts.xml
- Include plugin configurations and properties files
- Consider configuration interactions and dependencies
- Evaluate configuration impact on runtime behavior

### 2. Security-First Approach
- Prioritize security configuration issues
- Validate against known attack patterns
- Ensure defense-in-depth configuration
- Regular security configuration updates

### 3. Performance Optimization
- Identify configuration bottlenecks
- Optimize interceptor stack efficiency
- Enable appropriate caching mechanisms
- Monitor configuration performance impact

### 4. Maintainability Focus
- Ensure configuration clarity and documentation
- Standardize configuration patterns
- Implement configuration validation automation
- Plan for configuration evolution and migration

## Integration with Development Workflow

### Development Phase
- Configuration validation during development
- Real-time configuration syntax checking
- Configuration best practices guidance
- Automated configuration formatting

### Testing Phase
- Configuration-specific testing strategies
- Security configuration validation
- Performance configuration testing
- Integration testing with various configurations

### Deployment Phase
- Environment-specific configuration validation
- Production configuration security review
- Configuration deployment automation
- Post-deployment configuration verification

Remember: Apache Struts configuration directly impacts application security, performance, and maintainability. Regular configuration analysis and optimization are essential for a robust application.