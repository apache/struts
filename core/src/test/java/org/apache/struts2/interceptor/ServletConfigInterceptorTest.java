/*
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

import org.apache.struts2.action.Action;
import org.apache.struts2.ActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.action.ApplicationAware;
import org.apache.struts2.action.ParametersAware;
import org.apache.struts2.action.PrincipalAware;
import org.apache.struts2.action.ServletContextAware;
import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.action.ServletResponseAware;
import org.apache.struts2.action.SessionAware;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.interceptor.servlet.ServletPrincipalProxy;
import org.apache.struts2.mock.MockActionInvocation;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * Unit test for {@link ServletConfigInterceptor}.
 */
public class ServletConfigInterceptorTest extends StrutsInternalTestCase {

    private ServletConfigInterceptor interceptor;

    public void testServletRequestAware() throws Exception {
        ServletRequestAware mock = createMock(ServletRequestAware.class);

        MockHttpServletRequest req = new MockHttpServletRequest();

        MockActionInvocation mai = createActionInvocation(mock);
        mai.getInvocationContext().put(StrutsStatics.HTTP_REQUEST, req);

        mock.withServletRequest(req);
        expectLastCall();

        replay(mock);
        interceptor.intercept(mai);
        verify(mock);
    }

    public void testServletResponseAware() throws Exception {
        ServletResponseAware mock = createMock(ServletResponseAware.class);

        MockHttpServletResponse res = new MockHttpServletResponse();

        MockActionInvocation mai = createActionInvocation(mock);
        mai.getInvocationContext().put(StrutsStatics.HTTP_RESPONSE, res);

        mock.withServletResponse(res);
        expectLastCall().times(1);

        replay(mock);
        interceptor.intercept(mai);
        verify(mock);
    }

    public void testParametersAware() throws Exception {
        ParametersAware mock = createMock(ParametersAware.class);

        MockActionInvocation mai = createActionInvocation(mock);

        HttpParameters params = HttpParameters.create().build();
        mai.getInvocationContext().withParameters(params);

        mock.withParameters(params);
        expectLastCall().times(1);

        replay(mock);
        interceptor.intercept(mai);
        verify(mock);
    }

    public void testSessionAware() throws Exception {
        SessionAware mock = createMock(SessionAware.class);

        MockActionInvocation mai = createActionInvocation(mock);

        Map<String, Object> session = new HashMap<String, Object>();
        mai.getInvocationContext().withSession(session);

        mock.withSession(session);
        expectLastCall().times(1);

        replay(mock);
        interceptor.intercept(mai);
        verify(mock);
    }

    public void testApplicationAware() throws Exception {
        ApplicationAware mock = createMock(ApplicationAware.class);

        MockActionInvocation mai = createActionInvocation(mock);

        Map<String, Object> app = new HashMap<>();
        mai.getInvocationContext().withApplication(app);

        mock.withApplication(app);
        expectLastCall().times(1);

        replay(mock);
        interceptor.intercept(mai);
        verify(mock);
    }

    public void testPrincipalAware() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setUserPrincipal(null);
        req.setRemoteUser("Santa");
        PrincipalAware mock = createMock(PrincipalAware.class);

        MockActionInvocation mai = createActionInvocation(mock);
        mai.getInvocationContext().put(StrutsStatics.HTTP_REQUEST, req);

        MockServletContext ctx = new MockServletContext();
        mai.getInvocationContext().put(StrutsStatics.SERVLET_CONTEXT, ctx);

        mock.withPrincipalProxy(anyObject(ServletPrincipalProxy.class)); // less strict match is needed for this unit test to be conducted using mocks
        expectLastCall().times(1);

        replay(mock);
        interceptor.intercept(mai);
        verify(mock);
    }

    public void testActionPrincipalProxy() throws Exception {
        // unit test that does not use mock, but an Action so we also get code coverage for the PrincipalProxy class
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
        assertNull(proxy.getUserPrincipal());
        assertFalse(proxy.isRequestSecure());
        assertFalse(proxy.isUserInRole("no.role"));
        assertEquals("Santa", proxy.getRemoteUser());

    }

    public void testActionServletContextAware() throws Exception {
        ServletContextAware mock = createMock(ServletContextAware.class);

        MockActionInvocation mai = createActionInvocation(mock);

        MockServletContext ctx = new MockServletContext();
        mai.getInvocationContext().put(StrutsStatics.SERVLET_CONTEXT, ctx);

        mock.withServletContext(ctx);
        expectLastCall().times(1);

        replay(mock);
        interceptor.intercept(mai);
        verify(mock);
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

        public void withPrincipalProxy(PrincipalProxy proxy) {
            this.proxy = proxy;
        }

        public PrincipalProxy getProxy() {
            return proxy;
        }
    }

}
