package org.apache.struts2.rest;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.DefaultPropertiesProvider;
import org.apache.struts2.config.StrutsBeanSelectionProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * An action annotated {@code @HttpPost} must not run for a GET. The core defaultStack carries the
 * httpMethod interceptor; restDefaultStack (forked from it) does not, so the annotation is ignored.
 */
public class RestStackHttpMethodEnforcementTest extends XWorkTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(
                new StrutsXmlConfigurationProvider("struts-default.xml"),
                new StrutsXmlConfigurationProvider("struts-plugin.xml"),
                new StrutsXmlConfigurationProvider("ww-httpmethod-stack.xml"),
                new DefaultPropertiesProvider(),
                new StrutsBeanSelectionProvider());
    }

    private int getAndReportStatus(String namespace) throws Exception {
        PostOnlyAction.invoked = false;
        MockHttpServletRequest request = new MockHttpServletRequest("GET", namespace + "/mark");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, Object> ctx = new HashMap<>();
        ctx.put(StrutsStatics.HTTP_REQUEST, request);
        ctx.put(StrutsStatics.HTTP_RESPONSE, response);
        ctx.putAll(ActionContext.of(ctx).withParameters(org.apache.struts2.dispatcher.HttpParameters.create(new HashMap<String, Object>()).build()).getContextMap());
        ActionProxy proxy = actionProxyFactory.createActionProxy(namespace, "mark", null, ctx);
        proxy.execute();
        return response.getStatus();
    }

    public void testControlCoreDefaultStackRejectsGetOnPostOnlyAction() throws Exception {
        assertEquals(405, getAndReportStatus("/core"));
        assertFalse("action must not run", PostOnlyAction.invoked);
    }

    public void testRestDefaultStackRejectsGetOnPostOnlyAction() throws Exception {
        getAndReportStatus("/rest");
        assertFalse("@HttpPost action ran for GET under restDefaultStack", PostOnlyAction.invoked);
    }
}
