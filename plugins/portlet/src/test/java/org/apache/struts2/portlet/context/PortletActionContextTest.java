/*
 * $Id: PortletActionContextTest.java 557544 2007-07-19 10:03:06Z nilsga $
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
package org.apache.struts2.portlet.context;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.portlet.PortletPhase;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.HashMap;
import java.util.Map;

import static org.apache.struts2.portlet.PortletConstants.DEFAULT_ACTION_FOR_MODE;
import static org.apache.struts2.portlet.PortletConstants.PHASE;
import static org.apache.struts2.portlet.PortletConstants.PORTLET_CONFIG;
import static org.apache.struts2.portlet.PortletConstants.PORTLET_NAMESPACE;
import static org.apache.struts2.portlet.PortletConstants.REQUEST;
import static org.apache.struts2.portlet.PortletConstants.RESPONSE;
import static org.apache.struts2.portlet.context.PortletActionContext.getActionRequest;
import static org.apache.struts2.portlet.context.PortletActionContext.getActionResponse;
import static org.apache.struts2.portlet.context.PortletActionContext.getDefaultActionForMode;
import static org.apache.struts2.portlet.context.PortletActionContext.getPhase;
import static org.apache.struts2.portlet.context.PortletActionContext.getPortletConfig;
import static org.apache.struts2.portlet.context.PortletActionContext.getPortletNamespace;
import static org.apache.struts2.portlet.context.PortletActionContext.getRenderRequest;
import static org.apache.struts2.portlet.context.PortletActionContext.getRenderResponse;
import static org.apache.struts2.portlet.context.PortletActionContext.getRequest;
import static org.apache.struts2.portlet.context.PortletActionContext.getResponse;

/**
 */
public class PortletActionContextTest extends MockObjectTestCase {

    Mock mockRenderRequest;
    Mock mockRenderResponse;
    Mock mockPortletConfig;
    Mock mockActionRequest;
    Mock mockActionResponse;

    RenderRequest renderRequest;
    RenderResponse renderResponse;

    ActionRequest actionRequest;
    ActionResponse actionResponse;

    PortletConfig portletConfig;

    Map<String, Object> context = new HashMap<String, Object>();

    public void setUp() throws Exception {
        super.setUp();
        mockRenderRequest = mock(RenderRequest.class);
        mockRenderResponse = mock(RenderResponse.class);
        mockActionRequest = mock(ActionRequest.class);
        mockActionResponse = mock(ActionResponse.class);
        mockPortletConfig = mock(PortletConfig.class);

        renderRequest = (RenderRequest)mockRenderRequest.proxy();
        renderResponse = (RenderResponse)mockRenderResponse.proxy();
        actionRequest = (ActionRequest)mockActionRequest.proxy();
        actionResponse = (ActionResponse)mockActionResponse.proxy();
        portletConfig = (PortletConfig)mockPortletConfig.proxy();


        ActionContext.setContext(new ActionContext(context));
    }

    public void testGetPhase() {
        context.put(PHASE, PortletPhase.RENDER_PHASE);

        assertEquals(PortletPhase.RENDER_PHASE, getPhase());
    }

    public void testIsRender() {
        context.put(PHASE, PortletPhase.RENDER_PHASE);

        PortletPhase phase = getPhase();

        assertTrue(phase.isRender());
        assertFalse(phase.isAction());
        assertFalse(phase.isEvent());
    }

    public void testIsAction() {
        context.put(PHASE, PortletPhase.ACTION_PHASE);

        PortletPhase phase = getPhase();

        assertTrue(phase.isAction());
        assertFalse(phase.isRender());
        assertFalse(phase.isEvent());
    }
    
    public void testIsEvent() {
    	context.put(PHASE, PortletPhase.EVENT_PHASE);

        PortletPhase phase = getPhase();

    	assertTrue(phase.isEvent());
    	assertFalse(phase.isAction());
    	assertFalse(phase.isRender());
    }

    public void testGetPortletConfig() {
        context.put(PORTLET_CONFIG, portletConfig);
        assertSame(portletConfig, getPortletConfig());
    }

    public void testGetRenderRequestAndResponse() {
        context.put(REQUEST, renderRequest);
        context.put(RESPONSE, renderResponse);
        context.put(PHASE, PortletPhase.RENDER_PHASE);
        assertSame(renderRequest, getRenderRequest());
        assertSame(renderResponse, getRenderResponse());
        assertSame(renderRequest, getRequest());
        assertSame(renderResponse, getResponse());
    }

    public void testGetRenderRequestAndResponseInEventPhase() {
        context.put(REQUEST, renderRequest);
        context.put(RESPONSE, renderResponse);
        context.put(PHASE, PortletPhase.ACTION_PHASE);
        try {
            getRenderRequest();
            fail("Should throw IllegalStateException!");
        }
        catch(IllegalStateException e) {
            assertTrue(true);
        }
        try {
            getRenderResponse();
            fail("Should throw IllegalStateException!");
        }
        catch(IllegalStateException e) {
            assertTrue(true);
        }
    }

    public void testGetActionRequestAndResponse() {
        context.put(REQUEST, actionRequest);
        context.put(RESPONSE, actionResponse);
        context.put(PHASE, PortletPhase.ACTION_PHASE);
        assertSame(actionRequest, getActionRequest());
        assertSame(actionResponse, getActionResponse());
        assertSame(actionRequest, getRequest());
        assertSame(actionResponse, getResponse());
    }

    public void testGetActionRequestAndResponseInRenderPhase() {
        context.put(REQUEST, actionRequest);
        context.put(RESPONSE, actionResponse);
        context.put(PHASE, PortletPhase.RENDER_PHASE);
        try {
            getActionRequest();
            fail("Should throw IllegalStateException!");
        }
        catch(IllegalStateException e) {
            assertTrue(true);
        }
        try {
            getActionResponse();
            fail("Should throw IllegalStateException!");
        }
        catch(IllegalStateException e) {
            assertTrue(true);
        }
    }

    public void testGetNamespace() {
        context.put(PORTLET_NAMESPACE, "testNamespace");
        assertEquals("testNamespace", getPortletNamespace());
    }

    public void testGetDefaultActionForMode() {
        ActionMapping mapping = new ActionMapping();
        context.put(DEFAULT_ACTION_FOR_MODE, mapping);
        assertEquals(mapping, getDefaultActionForMode());
    }

    public void tearDown() throws Exception {
        ActionContext.setContext(null);
        super.tearDown();
    }

}
