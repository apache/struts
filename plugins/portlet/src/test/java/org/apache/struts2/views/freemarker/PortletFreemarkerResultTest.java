/*
 * $Id: $
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
package org.apache.struts2.views.freemarker;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.portlet.PortletActionConstants;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.core.Constraint;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;

public class PortletFreemarkerResultTest extends MockObjectTestCase implements PortletActionConstants {

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

    public void testDoExecute_event_locationIsJsp() {
        Mock mockRequest = mock(ActionRequest.class);
        Mock mockResponse = mock(ActionResponse.class);
        Mock mockProxy = mock(ActionProxy.class);

        Constraint[] params = new Constraint[]{eq(PortletActionConstants.ACTION_PARAM), eq("freemarkerDirect")};
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

        PortletFreemarkerResult result = new PortletFreemarkerResult();
        try {
            result.doExecute("/WEB-INF/pages/testJsp.ftl", (ActionInvocation)mockInvocation.proxy());
        }
        catch(Exception e) {
            e.printStackTrace();
            fail("Error occured!");
        }
        assertEquals("/WEB-INF/pages/testJsp.ftl", session.get(RENDER_DIRECT_LOCATION));
    }


    public void tearDown() throws Exception {
        super.tearDown();
        ActionContext.setContext(null);
    }
}