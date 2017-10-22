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
package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.portlet.PortletConstants;
import org.springframework.mock.web.portlet.MockMimeResponse;
import org.springframework.mock.web.portlet.MockPortletContext;
import org.springframework.mock.web.portlet.MockPortletRequest;

import javax.portlet.PortletContext;
import javax.portlet.PortletMode;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;

public class PortletUrlRendererTest extends StrutsTestCase {

    private ValueStack stack;

    public void setUp() throws Exception {
        super.setUp();

        ActionProxy actionProxy = getActionProxy("/portlettest/test"); // creates new empty ActionContext
        ActionContext.getContext().put(ActionContext.ACTION_INVOCATION, actionProxy.getInvocation());

        PortletContext portletCtx = new MockPortletContext();
        ActionContext.getContext().put(StrutsStatics.STRUTS_PORTLET_CONTEXT, portletCtx);
        ActionContext.getContext().put(PortletConstants.REQUEST, new MockPortletRequest(portletCtx));
        ActionContext.getContext().put(PortletConstants.RESPONSE, new MockMimeResponse());
        ActionContext.getContext().put(PortletConstants.MODE_NAMESPACE_MAP, Collections.emptyMap());

        stack = actionProxy.getInvocation().getStack();
    }

    public void testRenderUrlWithNamespace() throws Exception {
        // given
        PortletUrlRenderer renderer = new PortletUrlRenderer();
        UrlProvider component = new URL(stack, request, response).getUrlProvider();
        Writer writer = new StringWriter();

        // when
        renderer.renderUrl(writer, component);

        // then
        assertTrue("/portlettest".equals(component.getNamespace()));
    }
    
    public void testIsPortelModeChanged() {
    	PortletUrlRenderer renderer = new PortletUrlRenderer();
    	PortletMode mode = new PortletMode("test");
    	UrlProvider provider = new ComponentUrlProvider(null, null);
    	provider.setPortletMode("test2");
    	
    	assertTrue(renderer.isPortletModeChange(provider, mode));
    }

}
