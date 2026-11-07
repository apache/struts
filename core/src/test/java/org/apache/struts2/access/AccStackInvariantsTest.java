package org.apache.struts2.access;

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.config.entities.InterceptorMapping;
import org.apache.struts2.config.entities.InterceptorStackConfig;
import org.apache.struts2.config.entities.PackageConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Guard test for the "bundled stack omits httpMethod" class (WW-5718 only resynced coep/coop/fetchMetadata;
 * httpMethod and csp were left out of restDefaultStack/beanValidationDefaultStack - see ~/hunt/reports2/plugins2.md
 * F1 and ~/hunt/reports2/deep-httpmethod.md). Walks every interceptor-stack bundled in core's struts-default.xml
 * (InterceptorStackConfig.getInterceptors() is already flattened by InterceptorBuilder, so stack-of-stack refs
 * like validationWorkflowStack -> basicStack are resolved to the atomic interceptor list) and asserts: any stack
 * that performs request-parameter binding (contains "params") also enforces the HTTP-method guard (contains
 * "httpMethod"), UNLESS the stack is explicitly allowlisted as an intentionally bare building block that is never
 * an application's own primary request stack (e.g. emptyStack: no params, so not even reachable; nothing in core
 * is currently allowlisted - the invariant holds for every core stack today).
 * <p>
 * This cannot see plugin struts-plugin.xml files (core has no dependency on the plugins), so restDefaultStack and
 * beanValidationDefaultStack need their own per-plugin analogue - see
 * plugins/rest/.../RestStackHttpMethodEnforcementTest and
 * plugins/bean-validation/.../BeanValidationStackHttpMethodEnforcementTest.
 */
public class AccStackInvariantsTest extends StrutsInternalTestCase {

    /** Stacks that are deliberately bare (never bound to a package's default-interceptor-ref as its primary
     *  request stack; building blocks or dev-tool stacks only) are allowlisted here, by design, not by accident. */
    private static final Set<String> ALLOWED_BARE = Set.of();

    public void testEveryStackWithParamsAlsoHasHttpMethod() {
        PackageConfig pc = configuration.getPackageConfig("struts-default");
        Map<String, InterceptorStackConfig> stacks = allStacks(pc);
        assertTrue("expected to find bundled interceptor-stacks", stacks.size() > 5);

        List<String> violations = new ArrayList<>();
        for (Map.Entry<String, InterceptorStackConfig> e : stacks.entrySet()) {
            String name = e.getKey();
            if (ALLOWED_BARE.contains(name)) {
                continue;
            }
            List<String> names = e.getValue().getInterceptors().stream().map(InterceptorMapping::getName).toList();
            if (names.contains("params") && !names.contains("httpMethod")) {
                violations.add(name);
            }
        }
        assertTrue("stacks binding params but not enforcing httpMethod: " + violations, violations.isEmpty());
    }

    private Map<String, InterceptorStackConfig> allStacks(PackageConfig pc) {
        Map<String, Object> all = pc.getAllInterceptorConfigs();
        Map<String, InterceptorStackConfig> out = new java.util.LinkedHashMap<>();
        for (Map.Entry<String, Object> e : all.entrySet()) {
            if (e.getValue() instanceof InterceptorStackConfig stackConfig) {
                out.put(e.getKey(), stackConfig);
            }
        }
        return out;
    }
}
