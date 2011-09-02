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

package org.apache.struts2.portlet.context;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import junit.textui.TestRunner;

import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.portlet.PortletActionConstants;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import com.opensymphony.xwork2.ActionContext;

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

    Map context = new HashMap();

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
        context.put(PortletActionConstants.PHASE, PortletActionConstants.RENDER_PHASE);

        assertEquals(PortletActionConstants.RENDER_PHASE, PortletActionContext.getPhase());
    }

    public void testIsRender() {
        context.put(PortletActionConstants.PHASE, PortletActionConstants.RENDER_PHASE);

        assertTrue(PortletActionContext.isRender());
        assertFalse(PortletActionContext.isEvent());
    }

    public void testIsEvent() {
        context.put(PortletActionConstants.PHASE, PortletActionConstants.EVENT_PHASE);

        assertTrue(PortletActionContext.isEvent());
        assertFalse(PortletActionContext.isRender());
    }

    public void testGetPortletConfig() {
        context.put(PortletActionConstants.PORTLET_CONFIG, portletConfig);
        assertSame(portletConfig, PortletActionContext.getPortletConfig());
    }

    public void testGetRenderRequestAndResponse() {
        context.put(PortletActionConstants.REQUEST, renderRequest);
        context.put(PortletActionConstants.RESPONSE, renderResponse);
        context.put(PortletActionConstants.PHASE, PortletActionConstants.RENDER_PHASE);
        assertSame(renderRequest, PortletActionContext.getRenderRequest());
        assertSame(renderResponse, PortletActionContext.getRenderResponse());
        assertSame(renderRequest, PortletActionContext.getRequest());
        assertSame(renderResponse, PortletActionContext.getResponse());
    }

    public void testGetRenderRequestAndResponseInEventPhase() {
        context.put(PortletActionConstants.REQUEST, renderRequest);
        context.put(PortletActionConstants.RESPONSE, renderResponse);
        context.put(PortletActionConstants.PHASE, PortletActionConstants.EVENT_PHASE);
        try {
            PortletActionContext.getRenderRequest();
            fail("Should throw IllegalStateException!");
        }
        catch(IllegalStateException e) {
            assertTrue(true);
        }
        try {
            PortletActionContext.getRenderResponse();
            fail("Should throw IllegalStateException!");
        }
        catch(IllegalStateException e) {
            assertTrue(true);
        }
    }

    public void testGetActionRequestAndResponse() {
        context.put(PortletActionConstants.REQUEST, actionRequest);
        context.put(PortletActionConstants.RESPONSE, actionResponse);
        context.put(PortletActionConstants.PHASE, PortletActionConstants.EVENT_PHASE);
        assertSame(actionRequest, PortletActionContext.getActionRequest());
        assertSame(actionResponse, PortletActionContext.getActionResponse());
        assertSame(actionRequest, PortletActionContext.getRequest());
        assertSame(actionResponse, PortletActionContext.getResponse());
    }

    public void testGetActionRequestAndResponseInRenderPhase() {
        context.put(PortletActionConstants.REQUEST, actionRequest);
        context.put(PortletActionConstants.RESPONSE, actionResponse);
        context.put(PortletActionConstants.PHASE, PortletActionConstants.RENDER_PHASE);
        try {
            PortletActionContext.getActionRequest();
            fail("Should throw IllegalStateException!");
        }
        catch(IllegalStateException e) {
            assertTrue(true);
        }
        try {
            PortletActionContext.getActionResponse();
            fail("Should throw IllegalStateException!");
        }
        catch(IllegalStateException e) {
            assertTrue(true);
        }
    }

    public void testGetNamespace() {
        context.put(PortletActionConstants.PORTLET_NAMESPACE, "testNamespace");
        assertEquals("testNamespace", PortletActionContext.getPortletNamespace());
    }

    public void testGetDefaultActionForMode() {
        ActionMapping mapping = new ActionMapping();
        context.put(PortletActionConstants.DEFAULT_ACTION_FOR_MODE, mapping);
        assertEquals(mapping, PortletActionContext.getDefaultActionForMode());
    }

    public void tearDown() throws Exception {
        ActionContext.setContext(null);
        super.tearDown();
    }

    public static void main(String[] args) {
        TestRunner.run(PortletActionContextTest.class);
    }
}
