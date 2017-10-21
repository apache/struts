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
package org.apache.struts2.portlet.result;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import junit.textui.TestRunner;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.portlet.PortletConstants;
import org.apache.struts2.portlet.PortletPhase;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.core.Constraint;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.HashMap;
import java.util.Map;

import static com.opensymphony.xwork2.ActionContext.PARAMETERS;
import static com.opensymphony.xwork2.ActionContext.SESSION;
import static org.apache.struts2.portlet.PortletConstants.ACTION_PARAM;
import static org.apache.struts2.portlet.PortletConstants.MODE_PARAM;
import static org.apache.struts2.portlet.PortletConstants.PHASE;
import static org.apache.struts2.portlet.PortletConstants.RENDER_DIRECT_LOCATION;
import static org.apache.struts2.portlet.PortletConstants.REQUEST;
import static org.apache.struts2.portlet.PortletConstants.RESPONSE;

/**
 * PortletResultTest. Insert description.
 *
 */
public class PortletResultTest extends MockObjectTestCase implements StrutsStatics {

    Mock mockInvocation = null;
    Mock mockCtx = null;
    Mock mockProxy = null;
	ActionProxy proxy = null;
	ActionInvocation invocation = null;

    public void setUp() throws Exception {
        super.setUp();
        mockInvocation = mock(ActionInvocation.class);
        mockCtx = mock(PortletContext.class);
        mockProxy = mock(ActionProxy.class);

        Map<String, Object> sessionMap = new HashMap<String, Object>();

        Map<String, Object> context = new HashMap<String, Object>();
        context.put(SESSION, sessionMap);
        context.put(PARAMETERS, HttpParameters.create().build());
        context.put(STRUTS_PORTLET_CONTEXT, mockCtx.proxy());

        ActionContext.setContext(new ActionContext(context));
        mockProxy.stubs().method("getNamespace").will(returnValue("/test"));
        proxy = (ActionProxy) mockProxy.proxy();
        mockInvocation.stubs().method("getInvocationContext").will(returnValue(ActionContext.getContext()));
        mockInvocation.stubs().method("getProxy").will(returnValue(proxy));
        invocation = (ActionInvocation) mockInvocation.proxy();

    }

    public void testDoExecute_render() {
        Mock mockRequest = mock(RenderRequest.class);
        Mock mockResponse = mock(RenderResponse.class);
        Mock mockRd = mock(PortletRequestDispatcher.class);

        RenderRequest req = (RenderRequest)mockRequest.proxy();
        RenderResponse res = (RenderResponse)mockResponse.proxy();
        PortletRequestDispatcher rd = (PortletRequestDispatcher)mockRd.proxy();
        PortletContext ctx = (PortletContext)mockCtx.proxy();
        ActionInvocation inv = (ActionInvocation)mockInvocation.proxy();

        Constraint[] params = new Constraint[]{same(req), same(res)};
        mockRd.expects(once()).method("include").with(params);
        mockCtx.expects(once()).method("getRequestDispatcher").with(eq("/WEB-INF/pages/testPage.jsp")).will(returnValue(rd));
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));
        mockResponse.expects(once()).method("setContentType").with(eq("text/html"));

        mockRequest.stubs().method("getPortletMode").will(returnValue(PortletMode.VIEW));

        ActionContext ctxMap = ActionContext.getContext();
        ctxMap.put(RESPONSE, res);
        ctxMap.put(REQUEST, req);
        ctxMap.put(SERVLET_CONTEXT, ctx);
        ctxMap.put(PHASE, PortletPhase.RENDER_PHASE);

        PortletResult result = new PortletResult();
        try {
            result.doExecute("/WEB-INF/pages/testPage.jsp", inv);
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Error occured!");
        }

    }

    public void testDoExecute_event_locationIsAction() {

        Mock mockRequest = mock(ActionRequest.class);
        Mock mockResponse = mock(ActionResponse.class);

        Constraint[] params = new Constraint[]{eq(ACTION_PARAM), eq("testView")};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        params = new Constraint[]{eq(MODE_PARAM), eq(PortletMode.VIEW.toString())};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        params = new Constraint[]{eq(PortletConstants.RENDER_DIRECT_NAMESPACE), eq("/test")};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        
        mockRequest.stubs().method("getPortletMode").will(returnValue(PortletMode.VIEW));
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));
        ActionContext ctx = ActionContext.getContext();

        ctx.put(REQUEST, mockRequest.proxy());
        ctx.put(RESPONSE, mockResponse.proxy());
        ctx.put(PHASE, PortletPhase.ACTION_PHASE);

        PortletResult result = new PortletResult();
        try {
            result.doExecute("testView.action", invocation);
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Error occured!");
        }

    }

    public void testDoExecute_event_locationIsJsp() {
        Mock mockRequest = mock(ActionRequest.class);
        Mock mockResponse = mock(ActionResponse.class);

        Constraint[] params = new Constraint[]{eq(ACTION_PARAM), eq("renderDirect")};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        params = new Constraint[]{eq(MODE_PARAM), eq(PortletMode.VIEW.toString())};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        params = new Constraint[]{eq(PortletConstants.RENDER_DIRECT_NAMESPACE), eq("/test")};
        mockResponse.expects(once()).method("setRenderParameter").with(params);

        mockRequest.stubs().method("getPortletMode").will(returnValue(PortletMode.VIEW));
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));
 
        ActionContext ctx = ActionContext.getContext();

        Map session = new HashMap();
        
        ctx.put(REQUEST, mockRequest.proxy());
        ctx.put(RESPONSE, mockResponse.proxy());
        ctx.put(PHASE, PortletPhase.ACTION_PHASE);
        ctx.put(ActionContext.SESSION, session);

        PortletResult result = new PortletResult();
        try {
            result.doExecute("/WEB-INF/pages/testJsp.jsp", (ActionInvocation)mockInvocation.proxy());
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Error occured!");
        }
        assertEquals("/WEB-INF/pages/testJsp.jsp", session.get(RENDER_DIRECT_LOCATION));
    }

    public void testDoExecute_event_locationHasQueryParams() {
        Mock mockRequest = mock(ActionRequest.class);
        Mock mockResponse = mock(ActionResponse.class);

        Constraint[] params = new Constraint[]{eq(ACTION_PARAM), eq("testView")};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        params = new Constraint[]{eq("testParam1"), eq("testValue1")};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        params = new Constraint[]{eq("testParam2"), eq("testValue2")};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        params = new Constraint[]{eq(MODE_PARAM), eq(PortletMode.VIEW.toString())};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        params = new Constraint[]{eq(PortletConstants.RENDER_DIRECT_NAMESPACE), eq("/test")};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        
        mockRequest.stubs().method("getPortletMode").will(returnValue(PortletMode.VIEW));
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));

        ActionContext ctx = ActionContext.getContext();

        ctx.put(REQUEST, mockRequest.proxy());
        ctx.put(RESPONSE, mockResponse.proxy());
        ctx.put(PHASE, PortletPhase.ACTION_PHASE);

        PortletResult result = new PortletResult();
        try {
            result.doExecute("testView.action?testParam1=testValue1&testParam2=testValue2", (ActionInvocation)mockInvocation.proxy());
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Error occured!");
        }
    }

    public void testTitleAndContentType() throws Exception {
        Mock mockRequest = mock(RenderRequest.class);
        Mock mockResponse = mock(RenderResponse.class);
        Mock mockRd = mock(PortletRequestDispatcher.class);

        RenderRequest req = (RenderRequest)mockRequest.proxy();
        RenderResponse res = (RenderResponse)mockResponse.proxy();
        PortletRequestDispatcher rd = (PortletRequestDispatcher)mockRd.proxy();
        PortletContext ctx = (PortletContext)mockCtx.proxy();

        Constraint[] params = new Constraint[]{same(req), same(res)};
        mockRd.expects(once()).method("include").with(params);
        mockCtx.expects(once()).method("getRequestDispatcher").with(eq("/WEB-INF/pages/testPage.jsp")).will(returnValue(rd));
        mockCtx.expects(atLeastOnce()).method("getMajorVersion").will(returnValue(1));

        mockRequest.stubs().method("getPortletMode").will(returnValue(PortletMode.VIEW));

        ActionContext ctxMap = ActionContext.getContext();
        ctxMap.put(RESPONSE, res);
        ctxMap.put(REQUEST, req);
        ctxMap.put(SERVLET_CONTEXT, ctx);
        ctxMap.put(PHASE, PortletPhase.RENDER_PHASE);

        mockResponse.expects(atLeastOnce()).method("setTitle").with(eq("testTitle"));
        mockResponse.expects(atLeastOnce()).method("setContentType").with(eq("testContentType"));

        PortletResult result = new PortletResult();
        result.setTitle("testTitle");
        result.setContentType("testContentType");
        result.doExecute("/WEB-INF/pages/testPage.jsp", (ActionInvocation)mockInvocation.proxy());
    }

    public void tearDown() throws Exception {
        super.tearDown();
        ActionContext.setContext(null);
    }

    public static void main(String[] args) {
        TestRunner.run(PortletResultTest.class);
    }

}
