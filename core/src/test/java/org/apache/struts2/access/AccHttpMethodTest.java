package org.apache.struts2.access;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;

public class AccHttpMethodTest extends StrutsInternalTestCase {

    private int run(String action, String verb) throws Exception {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider("struts-acc-http.xml"));
        MockHttpServletResponse res = new MockHttpServletResponse();
        Map<String, Object> ctx = ActionContext.of()
            .withServletRequest(new MockHttpServletRequest(verb, "/" + action))
            .withServletResponse(res).getContextMap();
        AccHttpActions.RAN.set(0);
        ActionProxy p = actionProxyFactory.createActionProxy("", action, null, ctx, false, true);
        p.execute();
        return AccHttpActions.RAN.get();
    }

    public void testControlNamedMethodGetRejected() throws Exception { assertEquals(0, run("NamedPost", "GET")); }
    public void testControlNamedMethodPostAllowed() throws Exception { assertEquals(1, run("NamedPost", "POST")); }
    public void testControlClassLevelGetRejected() throws Exception { assertEquals(0, run("ClassPost", "GET")); }
    public void testExecuteMethodAnnotationGetRejected() throws Exception { assertEquals("@HttpPost on execute() ignored", 0, run("ExecutePost", "GET")); }
    public void testSubclassOfClassLevelGetRejected() throws Exception { assertEquals("class-level @HttpPost not inherited", 0, run("SubOfClassPost", "GET")); }
    public void testOverrideDropsMethodAnnotation() throws Exception { assertEquals("override drops @HttpPost", 0, run("SubOverride", "GET")); }
    public void testSubclassOfInterfaceDefaultMethodGetRejected() throws Exception { assertEquals("interface default-method @HttpPost, two levels down, not found", 0, run("SubOfImplementsMethodMarker", "GET")); }
    public void testSubclassOfImplementedInterfaceClassLevelGetRejected() throws Exception { assertEquals("interface class-level @HttpPost, two levels down, not found", 0, run("SubOfImplementsClassMarker", "GET")); }
}
