package org.apache.struts2.json;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;
import org.apache.struts2.views.util.DefaultUrlHelper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.util.ValueStack;

public class JSONActionRedirectResultTest extends StrutsTestCase {

    MockActionInvocation invocation;
    MockHttpServletResponse response;
    MockServletContext servletContext;
    ActionContext context;
    ValueStack stack;
    MockHttpServletRequest request;

    public void testNormalRedirect() throws Exception {
        JSONActionRedirectResult result = new JSONActionRedirectResult();
        result.setActionName("targetAction");
        result.setActionMapper(new DefaultActionMapper());
        result.setUrlHelper(new DefaultUrlHelper());

        Object action = new Object();
        stack.push(action);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String content = response.getContentAsString();
        assertEquals("", content);

        // spring MockHttpServletResponse does not set Location-Header and status code on sendRedirect()
//        String location = response.getHeader("Location").toString();
//        assertEquals("/targetAction.action", location);
//        assertEquals(302, response.getStatus());
        String redirectedUrl = response.getRedirectedUrl();
        assertEquals("/targetAction.action", redirectedUrl);
    }

    public void testJsonRedirect() throws Exception {
    	JSONActionRedirectResult result = new JSONActionRedirectResult();
        result.setActionName("targetAction");
        result.setActionMapper(new DefaultActionMapper());
        result.setUrlHelper(new DefaultUrlHelper());

        request.setParameter("struts.enableJSONValidation", "true");
        request.setParameter("struts.validateOnly", "false");

        Object action = new Object();
        stack.push(action);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String content = response.getContentAsString();
        assertEquals("{\"location\": \"/targetAction.action\"}", content);
        assertEquals(200, response.getStatus());
    }

    public void testValidateOnlyFalse() throws Exception {
        JSONActionRedirectResult result = new JSONActionRedirectResult();
        result.setActionName("targetAction");
        result.setActionMapper(new DefaultActionMapper());
        result.setUrlHelper(new DefaultUrlHelper());

        request.setParameter("struts.enableJSONValidation", "true");
        request.setParameter("struts.validateOnly", "true");

        Object action = new Object();
        stack.push(action);

        this.invocation.setAction(action);
        result.execute(this.invocation);

        String content = response.getContentAsString();
        assertEquals("", content);

        // spring MockHttpServletResponse does not set Location-Header and status code on sendRedirect()
//      String location = response.getHeader("Location").toString();
//      assertEquals("/targetAction.action", location);
//      assertEquals(302, response.getStatus());
      String redirectedUrl = response.getRedirectedUrl();
      assertEquals("/targetAction.action", redirectedUrl);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.response = new MockHttpServletResponse();
        this.request = new MockHttpServletRequest();
        this.context = ActionContext.getContext();
        this.context.put(StrutsStatics.HTTP_RESPONSE, this.response);
        this.context.put(StrutsStatics.HTTP_REQUEST, this.request);
        this.stack = context.getValueStack();
        this.servletContext = new MockServletContext();
        this.context.put(StrutsStatics.SERVLET_CONTEXT, this.servletContext);
        this.invocation = new MockActionInvocation();
        this.invocation.setInvocationContext(this.context);
        this.invocation.setStack(this.stack);
        MockActionProxy mockActionProxy = new MockActionProxy();
        mockActionProxy.setConfig(new ActionConfig.Builder(null, null, null).build());
        this.invocation.setProxy(mockActionProxy);
    }
}
