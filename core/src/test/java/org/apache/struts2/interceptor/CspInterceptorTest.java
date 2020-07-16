package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.logging.log4j.util.Strings;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.interceptor.csp.CspInterceptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashMap;
import java.util.Map;

public class CspInterceptorTest extends StrutsInternalTestCase {


    private final CspInterceptor interceptor = new CspInterceptor();
    private final MockActionInvocation mai = new MockActionInvocation();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();
    private final Map<String, Object> session = new HashMap<>();

    public void testNonceNotExists() throws Exception {
        interceptor.intercept(mai);
        assertTrue("Nonce key does not exist", session.containsKey("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.get("nonce")));
    }

    public void testNonceExists() throws Exception {
        mai.getInvocationContext().getSession().put("nonce", "foo");
        interceptor.intercept(mai);
        assertTrue("Nonce key does not exist", session.containsKey("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.get("nonce")));
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        container.inject(interceptor);
        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        ActionContext context = ServletActionContext.getActionContext().bind();
        context.withSession(session);
        mai.setInvocationContext(context);

    }

}
