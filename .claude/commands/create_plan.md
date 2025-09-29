# Create Implementation Plan

You are tasked with creating detailed, actionable implementation plans for Apache Struts development through an interactive, iterative process. You help developers plan complex features, refactoring efforts, security improvements, and architectural changes with thorough research and structured deliverables.

## Initial Setup

When this command is invoked, respond with:
```
I'm ready to help you create a comprehensive implementation plan for Apache Struts. Please describe what you want to implement, improve, or refactor, and I'll work with you to develop a thorough plan.

What would you like to plan?
```

Then wait for the user's planning request.

## Planning Methodology

### 1. Context Gathering & Initial Analysis

After receiving the planning request:

1. **Read any directly mentioned files first:**
   - If the user mentions specific tickets, files, or documentation, read them FULLY first
   - Use the Read tool WITHOUT limit/offset parameters to read entire files
   - Read these files yourself in the main context before spawning any sub-tasks
   - This ensures you have complete context before decomposing the planning task

2. **Analyze and clarify requirements:**
   - Ask clarifying questions about unclear requirements
   - Be skeptical - probe deeper into assumptions and constraints
   - Understand the business/technical context and goals
   - Identify stakeholders and success criteria
   - Clarify scope boundaries and non-goals

3. **Create initial planning structure:**
   - Use TodoWrite to track all planning phases and subtasks
   - Break down the planning work into parallel research areas

### 2. Research & Discovery Phase

**Use parallel Task agents for comprehensive research:**

**For current state analysis:**
- Use **codebase-locator** to find existing related components and implementations
- Use **codebase-analyzer** to understand current architecture and identify integration points
- Use **codebase-pattern-finder** to find similar existing patterns to model after or replace

**For historical context:**
- Use **thoughts-locator** to discover existing documentation about the topic (WW-XXXX tickets, research, plans)
- Use **thoughts-analyzer** to extract insights from the most relevant historical documents

**For external research (if needed):**
- Use **web-search-researcher** for modern Apache Struts best practices, security updates, or external documentation
- Include links from web research in the final plan

**Key research areas for Apache Struts:**
- Security implications (OGNL injection, CVE patterns, parameter filtering)
- Maven module dependencies and build considerations
- Interceptor stack integration and ordering
- Plugin architecture and extension points
- Testing strategies (unit, integration, `mvn test -DskipAssembly`)
- Performance impact on request processing pipeline
- Configuration approaches (XML, annotations, convention)

### 3. Plan Structure Development

After research completion, develop a structured plan with these sections:

#### Plan Document Structure:
```markdown
---
date: [ISO format date and time with timezone]
topic: "[Implementation Topic]"
ticket: "[WW-XXXX if applicable]"
tags: [plan, struts, relevant-components]
status: draft
complexity: [low|medium|high]
estimated_effort: [brief estimate]
---

# Implementation Plan: [Topic]

## Overview
- **Goal**: [Clear statement of what will be implemented]
- **Scope**: [What's included and excluded]
- **Success Criteria**: [Measurable outcomes]
- **Timeline**: [Estimated phases and duration]

## Current State Analysis
### Existing Architecture
- Current implementation details with file references
- Integration points and dependencies
- Limitations and pain points

### Maven Module Structure
- Affected modules (`/core/`, `/plugins/`, `/apps/`, `/jakarta/`)
- Build dependencies and profiles
- Testing module considerations

## Desired End State
### Target Architecture
- Detailed description of final implementation
- New components and their responsibilities
- Integration approach with existing Struts components

### Security Considerations
- OGNL expression safety analysis
- Input validation and parameter filtering
- CVE mitigation strategies (CVE-2017-5638, CVE-2018-11776, etc.)
- Interceptor security configuration

## Implementation Approach
### Phase Breakdown
#### Phase 1: [Foundation/Setup]
- Specific tasks with file paths and line numbers
- Prerequisites and dependencies
- Risk mitigation strategies

#### Phase 2: [Core Implementation]
- Development tasks in logical order
- Testing approach for each component
- Integration steps

#### Phase 3: [Integration & Testing]
- End-to-end testing strategy
- Performance validation
- Security testing approach

### Development Strategy
- **Configuration Approach**: XML vs annotations vs convention
- **Interceptor Integration**: Stack placement and ordering
- **Plugin Considerations**: Extension points and backwards compatibility
- **Maven Build Integration**: Test commands and profiles

## Detailed Implementation Steps

### File-Level Changes
- `path/to/file.java:123` - Specific change description
- `another/file.xml:45-67` - Configuration modifications
- New files to create with their purpose

### Testing Strategy
#### Unit Tests
- Test classes to create/modify
- Mock strategies for Struts components
- Coverage expectations

#### Integration Tests
- End-to-end scenarios to test
- Maven test execution: `mvn test -DskipAssembly`
- Performance test considerations

#### Security Tests
- OGNL injection prevention tests
- Parameter filtering validation
- Interceptor security configuration tests

## Success Criteria

### Automated Criteria (Must Pass)
- [ ] All existing tests pass: `mvn test -DskipAssembly`
- [ ] New tests achieve X% coverage
- [ ] Performance benchmarks within Y% of baseline
- [ ] Security scan passes with no new vulnerabilities
- [ ] Build completes successfully: `mvn clean install`

### Manual Criteria (Acceptance)
- [ ] Feature works as specified in [environment]
- [ ] Documentation updated and reviewed
- [ ] Code review completed
- [ ] Security review approved
- [ ] Integration with [specific components] validated

## Performance Considerations
- Impact on request processing pipeline
- Memory usage implications
- Interceptor stack execution overhead
- Database/external service impact

## Security Analysis
### Threat Model
- Attack vectors and mitigation strategies
- OGNL expression evaluation points
- Input validation requirements

### Security Controls
- Parameter filtering and validation
- Authentication/authorization integration
- Audit logging requirements

## Migration Strategy
- Backwards compatibility approach
- Deprecation timeline for old features
- Migration scripts or tools needed
- Documentation for users

## Testing & Validation Plan
### Development Testing
- Unit test strategy and tools
- Integration test scenarios
- Local development validation steps

### Staging Validation
- End-to-end test scenarios
- Performance testing approach
- Security testing checklist

### Production Readiness
- Rollout strategy (feature flags, gradual rollout)
- Monitoring and alerting setup
- Rollback procedures

## Dependencies & Assumptions
### Technical Dependencies
- Required Struts version compatibility
- Maven dependencies and plugins
- External service requirements

### Assumptions
- Development environment setup
- Team expertise and training needs
- Timeline assumptions and constraints

## Risk Analysis & Mitigation
### Technical Risks
- [Risk]: [Impact] - [Mitigation Strategy]
- [Risk]: [Impact] - [Mitigation Strategy]

### Operational Risks
- Deployment complexity
- Performance impact
- Security implications

## Code References
- `file.java:123` - Existing implementation to modify
- `another.xml:45-67` - Configuration to update
- `third.java:89` - Pattern to follow

## Related Work
### Historical Context (from thoughts/)
- `thoughts/shared/research/related-topic.md` - Previous analysis
- `thoughts/shared/tickets/WW-1234.md` - Related ticket work
- `thoughts/shared/plans/similar-feature.md` - Similar implementation

### External References
- [Apache Struts Documentation](link) - Relevant section
- [Security Advisory](link) - CVE information
- [Performance Study](link) - Benchmarking data

## Future Considerations
- Planned follow-up work
- Potential enhancements
- Architectural evolution path

## Appendices
### A. Configuration Examples
[Detailed configuration snippets]

### B. Code Samples
[Key implementation examples]

### C. Test Data
[Sample test cases and data]
```

### 4. Interactive Refinement

**Collaborate with the user to refine the plan:**
- Present initial plan structure and gather feedback
- Ask specific questions about unclear areas
- Iterate on implementation approach based on user expertise
- Refine success criteria and acceptance criteria
- Adjust timeline and effort estimates

**Continue iterating until the user is satisfied with:**
- Completeness of analysis
- Accuracy of technical approach
- Feasibility of timeline
- Clarity of implementation steps

### 5. Plan Finalization & Documentation

**Generate the final implementation plan:**
- Create the plan document in `thoughts/shared/plans/YYYY-MM-DD-WW-XXXX-description.md`
- Use consistent naming: date, ticket number (if applicable), brief description
- Include all research findings and code references
- Add GitHub permalinks if on stable branch

**Plan document metadata:**
- YAML frontmatter with all relevant fields
- Status tracking (draft -> review -> approved -> in-progress -> complete)
- Complexity and effort estimates
- Tag with relevant Struts components

## Apache Struts Specific Considerations

### Framework Integration Points
- **Action Layer**: ActionSupport patterns, ModelDriven implementations
- **Interceptor Stack**: Ordering dependencies, security interceptors
- **Result Types**: Custom result implementations, view technology integration
- **Plugin Architecture**: Extension points and configuration
- **OGNL Security**: Expression evaluation safety, parameter exclusion patterns

### Security-First Planning
- Always analyze OGNL injection vectors in new features
- Consider parameter pollution and manipulation attacks
- Plan for proper input validation and sanitization
- Review interceptor security configurations
- Include CVE mitigation strategies in all plans

### Maven Module Considerations
- Impact on `/core/`, `/plugins/`, `/apps/`, `/jakarta/` modules
- Build profile implications
- Dependency management across modules
- Test execution strategies: `mvn test -DskipAssembly`

### Performance Planning
- Request processing pipeline impact
- Interceptor stack execution overhead
- Memory usage patterns
- Caching strategies and implications

## Planning Best Practices

1. **Be Skeptical**: Question assumptions, probe requirements deeply
2. **Research Thoroughly**: Use all available agents in parallel for comprehensive analysis
3. **Think Security First**: Always consider OGNL and CVE implications
4. **Plan for Testing**: Include comprehensive testing strategy from the start
5. **Document Everything**: Capture decisions, trade-offs, and rationale
6. **Iterate Frequently**: Refine plan based on user feedback and research findings
7. **Reference Concrete Code**: Always include specific file paths and line numbers
8. **Consider Migration**: Plan for backwards compatibility and user migration
9. **Think Modularly**: Leverage Struts plugin architecture when appropriate
10. **Validate Continuously**: Build validation points throughout implementation phases

## Success Metrics

A successful implementation plan includes:
- ✅ Clear, actionable implementation steps with file references
- ✅ Comprehensive security analysis with CVE considerations
- ✅ Detailed testing strategy with specific Maven commands
- ✅ Performance impact analysis and mitigation
- ✅ Migration strategy for existing users
- ✅ Risk analysis with specific mitigation approaches
- ✅ Timeline with realistic effort estimates
- ✅ Success criteria that are measurable and testable

Remember: Great implementation plans anticipate problems, provide concrete guidance, and set clear expectations for success. Always leverage the full power of Struts' architecture while maintaining security and performance standards.