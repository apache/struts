/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.util.ServletContextAware;
import org.easymock.MockControl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;

/**
 * Unit test for {@link ServletConfigInterceptor}.
 *
 */
public class ServletConfigInterceptorTest extends StrutsInternalTestCase {

    private ServletConfigInterceptor interceptor;

    public void testServletRequestAware() throws Exception {
        MockControl control = MockControl.createControl(ServletRequestAware.class);
        ServletRequestAware mock = (ServletRequestAware) control.getMock();

        MockHttpServletRequest req = new MockHttpServletRequest();

        MockActionInvocation mai = createActionInvocation(mock);
        mai.getInvocationContext().put(StrutsStatics.HTTP_REQUEST, req);

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
        mai.getInvocationContext().put(StrutsStatics.HTTP_RESPONSE, res);

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

        Map<String, Object> param = new HashMap<String, Object>();
        mai.getInvocationContext().setParameters(param);

        mock.setParameters((Map)param);
        control.setVoidCallable();

        control.replay();
        interceptor.intercept(mai);
        control.verify();
    }

    public void testSessionAware() throws Exception {
        MockControl control = MockControl.createControl(SessionAware.class);
        SessionAware mock = (SessionAware) control.getMock();

        MockActionInvocation mai = createActionInvocation(mock);

        Map<String, Object> session = new HashMap<String, Object>();
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

        Map<String, Object> app = new HashMap<String, Object>();
        mai.getInvocationContext().setApplication(app);

        mock.setApplication(app);
        control.setVoidCallable();

        control.replay();
        interceptor.intercept(mai);
        control.verify();
    }

    public void testPrincipalAware() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setUserPrincipal(null);
        req.setRemoteUser("Santa");
        MockControl control = MockControl.createControl(PrincipalAware.class);
        control.setDefaultMatcher(MockControl.ALWAYS_MATCHER); // less strick match is needed for this unit test to be conducted using mocks
        PrincipalAware mock = (PrincipalAware) control.getMock();

        MockActionInvocation mai = createActionInvocation(mock);
        mai.getInvocationContext().put(StrutsStatics.HTTP_REQUEST, req);
        
        MockServletContext ctx = new MockServletContext();
        mai.getInvocationContext().put(StrutsStatics.SERVLET_CONTEXT, ctx);

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
        mai.getInvocationContext().put(StrutsStatics.HTTP_REQUEST, req);

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
        mai.getInvocationContext().put(StrutsStatics.SERVLET_CONTEXT, ctx);

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
