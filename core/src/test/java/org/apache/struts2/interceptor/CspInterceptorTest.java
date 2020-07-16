package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.logging.log4j.util.Strings;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.interceptor.csp.CspInterceptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.apache.struts2.interceptor.csp.CspSettings.CSP_HEADER;
import static org.apache.struts2.interceptor.csp.CspSettings.SCRIPT_SRC;
import static org.apache.struts2.interceptor.csp.CspSettings.STRICT_DYNAMIC;
import static org.apache.struts2.interceptor.csp.CspSettings.OBJECT_SRC;
import static org.apache.struts2.interceptor.csp.CspSettings.NONE;
import static org.apache.struts2.interceptor.csp.CspSettings.BASE_URI;
import static org.apache.struts2.interceptor.csp.CspSettings.HTTP;
import static org.apache.struts2.interceptor.csp.CspSettings.HTTPS;

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
        checkHeader();
    }

    public void testNonceExists() throws Exception {
        session.put("nonce", "foo");
        interceptor.intercept(mai);
        assertTrue("Nonce key does not exist", session.containsKey("nonce"));
        assertFalse("Nonce value is empty", Strings.isEmpty((String) session.get("nonce")));
        checkHeader();
    }

    public void checkHeader(){
        String header = String.format("%s '%s'; %s 'nonce-%s' '%s' %s %s; %s '%s';",
                OBJECT_SRC, NONE,
                SCRIPT_SRC, session.get("nonce"), STRICT_DYNAMIC, HTTP, HTTPS,
                BASE_URI, NONE
        );
        String expectedCspHeader = response.getHeader(CSP_HEADER);
        assertFalse("No CSP header exists", Strings.isEmpty(header));
        assertEquals("Repsonse headers does not contain nonce header", expectedCspHeader, header);
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
