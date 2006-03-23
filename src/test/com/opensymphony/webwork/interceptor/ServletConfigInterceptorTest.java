/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.interceptor;

import com.opensymphony.webwork.WebWorkStatics;
import com.opensymphony.webwork.WebWorkTestCase;
import com.opensymphony.webwork.util.ServletContextAware;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.mock.MockActionInvocation;
import org.easymock.MockControl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for {@link ServletConfigInterceptor}.
 *
 * @author Claus Ibsen
 */
public class ServletConfigInterceptorTest extends WebWorkTestCase {

    private ServletConfigInterceptor interceptor;

    public void testServletRequestAware() throws Exception {
        MockControl control = MockControl.createControl(ServletRequestAware.class);
        ServletRequestAware mock = (ServletRequestAware) control.getMock();

        MockHttpServletRequest req = new MockHttpServletRequest();

        MockActionInvocation mai = createActionInvocation(mock);
        mai.getInvocationContext().put(WebWorkStatics.HTTP_REQUEST, req);

        mock.setServletRequest((HttpServletRequest) req);
        control.setVoidCallable();

        control.replay();
        interceptor.intercept(mai);
        control.verify();
    }

    public void testServletResponseAware() throws Exception {
        MockControl control = MockControl.createControl(ServletResponseAware.class);
        ServletResponseAware mock = (ServletResponseAware) control.getMock();

        MockHttpServletResponse res = new MockHttpServletResponse();

        MockActionInvocation mai = createActionInvocation(mock);
        mai.getInvocationContext().put(WebWorkStatics.HTTP_RESPONSE, res);

        mock.setServletResponse((HttpServletResponse) res);
        control.setVoidCallable();

        control.replay();
        interceptor.intercept(mai);
        control.verify();
    }

    public void testParameterAware() throws Exception {
        MockControl control = MockControl.createControl(ParameterAware.class);
        ParameterAware mock = (ParameterAware) control.getMock();

        MockActionInvocation mai = createActionInvocation(mock);

        Map param = new HashMap();
        mai.getInvocationContext().setParameters(param);

        mock.setParameters(param);
        control.setVoidCallable();

        control.replay();
        interceptor.intercept(mai);
        control.verify();
    }

    public void testSessionAware() throws Exception {
        MockControl control = MockControl.createControl(SessionAware.class);
        SessionAware mock = (SessionAware) control.getMock();

        MockActionInvocation mai = createActionInvocation(mock);

        Map session = new HashMap();
        mai.getInvocationContext().setSession(session);

        mock.setSession(session);
        control.setVoidCallable();

        control.replay();
        interceptor.intercept(mai);
        control.verify();
    }

    public void testApplicationAware() throws Exception {
        MockControl control = MockControl.createControl(ApplicationAware.class);
        ApplicationAware mock = (ApplicationAware) control.getMock();

        MockActionInvocation mai = createActionInvocation(mock);

        Map app = new HashMap();
        mai.getInvocationContext().setApplication(app);

        mock.setApplication(app);
        control.setVoidCallable();

        control.replay();
        interceptor.intercept(mai);
        control.verify();
    }

    public void testPrincipalAware() throws Exception {
        MockControl control = MockControl.createControl(PrincipalAware.class);
        control.setDefaultMatcher(MockControl.ALWAYS_MATCHER); // less strick match is needed for this unit test to be conducted using mocks
        PrincipalAware mock = (PrincipalAware) control.getMock();

        MockActionInvocation mai = createActionInvocation(mock);

        MockServletContext ctx = new MockServletContext();
        mai.getInvocationContext().put(WebWorkStatics.SERVLET_CONTEXT, ctx);

        mock.setPrincipalProxy(null); // we can do this because of ALWAYS_MATCHER
        control.setVoidCallable();

        control.replay();
        interceptor.intercept(mai);
        control.verify();
    }

    public void testPrincipalProxy() throws Exception {
        // uni test that does not use mock, but an Action so we also get code coverage for the PrincipalProxy class
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setUserPrincipal(null);
        req.setRemoteUser("Santa");

        MyPrincipalAction action = new MyPrincipalAction();
        MockActionInvocation mai = createActionInvocation(action);
        mai.getInvocationContext().put(WebWorkStatics.HTTP_REQUEST, req);

        assertNull(action.getProxy());
        interceptor.intercept(mai);
        assertNotNull(action.getProxy());

        PrincipalProxy proxy = action.getProxy();
        assertEquals(proxy.getRequest(), req);
        assertNull(proxy.getUserPrincipal());
        assertTrue(! proxy.isRequestSecure());
        assertTrue(! proxy.isUserInRole("no.role"));
        assertEquals("Santa", proxy.getRemoteUser());

    }

    public void testServletContextAware() throws Exception {
        MockControl control = MockControl.createControl(ServletContextAware.class);
        ServletContextAware mock = (ServletContextAware) control.getMock();

        MockActionInvocation mai = createActionInvocation(mock);

        MockServletContext ctx = new MockServletContext();
        mai.getInvocationContext().put(WebWorkStatics.SERVLET_CONTEXT, ctx);

        mock.setServletContext((ServletContext) ctx);
        control.setVoidCallable();

        control.replay();
        interceptor.intercept(mai);
        control.verify();
    }

    private MockActionInvocation createActionInvocation(Object mock) {
        MockActionInvocation mai = new MockActionInvocation();
        mai.setResultCode("success");
        mai.setInvocationContext(ActionContext.getContext());
        mai.setAction(mock);
        return mai;
    }


    protected void setUp() throws Exception {
        super.setUp();
        interceptor = new ServletConfigInterceptor();
        interceptor.init();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        interceptor.destroy();
        interceptor = null;
    }

    private class MyPrincipalAction implements Action, PrincipalAware {

        private PrincipalProxy proxy;

        public String execute() throws Exception {
            return SUCCESS;
        }

        public void setPrincipalProxy(PrincipalProxy proxy) {
            this.proxy = proxy;
        }

        public PrincipalProxy getProxy() {
            return proxy;
        }
    }

}
