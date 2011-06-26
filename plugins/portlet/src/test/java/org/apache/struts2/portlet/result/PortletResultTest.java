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

package org.apache.struts2.portlet.result;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import junit.textui.TestRunner;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.portlet.PortletActionConstants;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.core.Constraint;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.mock.MockActionInvocation;

/**
 * PortletResultTest. Insert description.
 *
 */
public class PortletResultTest extends MockObjectTestCase implements PortletActionConstants {

    Mock mockInvocation = null;
    Mock mockConfig = null;
    Mock mockCtx = null;

    public void setUp() throws Exception {
        super.setUp();
        mockInvocation = mock(ActionInvocation.class);
        mockCtx = mock(PortletContext.class);

        Map paramMap = new HashMap();
        Map sessionMap = new HashMap();

        Map context = new HashMap();
        context.put(ActionContext.SESSION, sessionMap);
        context.put(ActionContext.PARAMETERS, paramMap);
        context.put(StrutsStatics.STRUTS_PORTLET_CONTEXT, mockCtx.proxy());

        ActionContext.setContext(new ActionContext(context));

        mockInvocation.stubs().method("getInvocationContext").will(returnValue(ActionContext.getContext()));

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
        mockResponse.expects(once()).method("setContentType").with(eq("text/html"));

        mockRequest.stubs().method("getPortletMode").will(returnValue(PortletMode.VIEW));

        ActionContext ctxMap = ActionContext.getContext();
        ctxMap.put(PortletActionConstants.RESPONSE, res);
        ctxMap.put(PortletActionConstants.REQUEST, req);
        ctxMap.put(StrutsStatics.SERVLET_CONTEXT, ctx);
        ctxMap.put(PortletActionConstants.PHASE, PortletActionConstants.RENDER_PHASE);

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

        Constraint[] params = new Constraint[]{eq(PortletActionConstants.ACTION_PARAM), eq("testView")};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        params = new Constraint[]{eq(PortletActionConstants.MODE_PARAM), eq(PortletMode.VIEW.toString())};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        mockRequest.stubs().method("getPortletMode").will(returnValue(PortletMode.VIEW));
        ActionContext ctx = ActionContext.getContext();

        ctx.put(PortletActionConstants.REQUEST, mockRequest.proxy());
        ctx.put(PortletActionConstants.RESPONSE, mockResponse.proxy());
        ctx.put(PortletActionConstants.PHASE, PortletActionConstants.EVENT_PHASE);

        PortletResult result = new PortletResult();
        try {
            result.doExecute("testView.action", (ActionInvocation)mockInvocation.proxy());
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Error occured!");
        }

    }

    public void testDoExecute_event_locationIsJsp() {
        Mock mockRequest = mock(ActionRequest.class);
        Mock mockResponse = mock(ActionResponse.class);
        Mock mockProxy = mock(ActionProxy.class);

        Constraint[] params = new Constraint[]{eq(PortletActionConstants.ACTION_PARAM), eq("renderDirect")};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        params = new Constraint[]{eq(PortletActionConstants.MODE_PARAM), eq(PortletMode.VIEW.toString())};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        mockRequest.stubs().method("getPortletMode").will(returnValue(PortletMode.VIEW));
        mockProxy.stubs().method("getNamespace").will(returnValue(""));

        mockInvocation.stubs().method("getProxy").will(returnValue(mockProxy.proxy()));

        ActionContext ctx = ActionContext.getContext();

        Map session = new HashMap();
        
        ctx.put(PortletActionConstants.REQUEST, mockRequest.proxy());
        ctx.put(PortletActionConstants.RESPONSE, mockResponse.proxy());
        ctx.put(PortletActionConstants.PHASE, PortletActionConstants.EVENT_PHASE);
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

        Constraint[] params = new Constraint[]{eq(PortletActionConstants.ACTION_PARAM), eq("testView")};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        params = new Constraint[]{eq("testParam1"), eq("testValue1")};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        params = new Constraint[]{eq("testParam2"), eq("testValue2")};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        params = new Constraint[]{eq(PortletActionConstants.MODE_PARAM), eq(PortletMode.VIEW.toString())};
        mockResponse.expects(once()).method("setRenderParameter").with(params);
        mockRequest.stubs().method("getPortletMode").will(returnValue(PortletMode.VIEW));

        ActionContext ctx = ActionContext.getContext();

        ctx.put(PortletActionConstants.REQUEST, mockRequest.proxy());
        ctx.put(PortletActionConstants.RESPONSE, mockResponse.proxy());
        ctx.put(PortletActionConstants.PHASE, PortletActionConstants.EVENT_PHASE);

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

        mockRequest.stubs().method("getPortletMode").will(returnValue(PortletMode.VIEW));

        ActionContext ctxMap = ActionContext.getContext();
        ctxMap.put(PortletActionConstants.RESPONSE, res);
        ctxMap.put(PortletActionConstants.REQUEST, req);
        ctxMap.put(StrutsStatics.SERVLET_CONTEXT, ctx);
        ctxMap.put(PortletActionConstants.PHASE, PortletActionConstants.RENDER_PHASE);

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
