package com.opensymphony.xwork2.interceptor;


import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.junit.jupiter.api.Assertions;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

// TODO Vary headers tests

public class CSRFPreventionInterceptorTest extends XWorkTestCase {

    CSRFPreventionInterceptor interceptor;


    public void testNoSite() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.removeHeader("sec-fetch-site");

        ServletActionContext.setRequest(request);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        Assertions.assertDoesNotThrow(() -> interceptor.intercept(mai));
    }

    public void testValidSite() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();

        for (String header : List.of("same-origin", "same-site", "none")){
            request.addHeader("sec-fetch-site", "same-origin");

            ServletActionContext.setRequest(request);
            ActionContext context = ServletActionContext.getActionContext();
            mai.setInvocationContext(context);

            Assertions.assertDoesNotThrow(() -> interceptor.intercept(mai));
            request.removeHeader("sec-fetch-site");
        }

    }

    public void testValidTopLevelNavigation() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader("sec-fetch-mode", "navigate");
        request.addHeader("sec-fetch-dest", "script");
        request.setMethod("GET");

        ServletActionContext.setRequest(request);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        Assertions.assertDoesNotThrow(() -> interceptor.intercept(mai));
    }

    public void testInValidTopLevelNavigation() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();

        for (String header : List.of("object", "embed")) {
            request.addHeader("sec-fetch-site", "foo");
            request.addHeader("sec-fetch-mode", "navigate");
            request.addHeader("sec-fetch-dest", header);
            request.setMethod("GET");

            ServletActionContext.setRequest(request);
            ActionContext context = ServletActionContext.getActionContext();
            mai.setInvocationContext(context);

            Assertions.assertThrows(RuntimeException.class, () -> {
                interceptor.intercept(mai);
            });
            request.removeHeader("sec-fetch-dest");
        }
    }

    public void testPathInExemptedPaths() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader("sec-fetch-site", "foo");
        request.setContextPath("/foobar");
        interceptor.setExemptedPaths(List.of("/foobar", "/test"));

        ServletActionContext.setRequest(request);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        Assertions.assertDoesNotThrow(() -> interceptor.intercept(mai));
    }

    public void testPathNotInExemptedPaths() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addHeader("sec-fetch-site", "foo");
        request.setContextPath("/foobar");
        interceptor.setExemptedPaths(List.of("/test"));

        ServletActionContext.setRequest(request);
        ActionContext context = ServletActionContext.getActionContext();
        mai.setInvocationContext(context);

        Assertions.assertThrows(RuntimeException.class, () -> {
            interceptor.intercept(mai);
        });
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        interceptor = new CSRFPreventionInterceptor();
        container.inject(interceptor);
    }

}
