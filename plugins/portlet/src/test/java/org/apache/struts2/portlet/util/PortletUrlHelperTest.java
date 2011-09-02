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

package org.apache.struts2.portlet.util;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import junit.framework.TestCase;

import org.apache.struts2.portlet.context.PortletActionContext;
import org.easymock.MockControl;

import com.opensymphony.xwork2.ActionContext;

/**
 */
public class PortletUrlHelperTest extends TestCase {

    RenderResponse renderResponse;

    RenderRequest renderRequest;

    PortletURL url;

    MockControl renderResponseControl;

    MockControl renderRequestControl;

    MockControl portletUrlControl;

    public void setUp() throws Exception {
        super.setUp();

        renderRequestControl = MockControl.createControl(RenderRequest.class);
        renderResponseControl = MockControl.createControl(RenderResponse.class);
        portletUrlControl = MockControl.createControl(PortletURL.class);

        renderRequest = (RenderRequest) renderRequestControl.getMock();
        renderResponse = (RenderResponse) renderResponseControl.getMock();
        url = (PortletURL) portletUrlControl.getMock();

        renderRequestControl.expectAndDefaultReturn(renderRequest
                .getPortletMode(), PortletMode.VIEW);
        renderRequestControl.expectAndDefaultReturn(renderRequest
                .getWindowState(), WindowState.NORMAL);

        Map modeNamespaceMap = new HashMap();
        modeNamespaceMap.put("view", "/view");
        modeNamespaceMap.put("edit", "/edit");
        modeNamespaceMap.put("help", "/help");

        Map context = new HashMap();
        context.put(PortletActionContext.REQUEST, renderRequest);
        context.put(PortletActionContext.RESPONSE, renderResponse);
        context.put(PortletActionContext.PHASE,
                PortletActionContext.RENDER_PHASE);
        context.put(PortletActionContext.MODE_NAMESPACE_MAP, modeNamespaceMap);

        ActionContext.setContext(new ActionContext(context));

    }

    public void testCreateRenderUrlWithNoModeOrState() throws Exception {
        renderResponseControl.expectAndReturn(renderResponse.createRenderURL(),
                url);

        url.setPortletMode(PortletMode.VIEW);
        url.setWindowState(WindowState.NORMAL);
        url.setParameters(null);
        portletUrlControl.setMatcher(MockControl.ALWAYS_MATCHER);
        renderRequestControl.replay();
        renderResponseControl.replay();
        portletUrlControl.replay();
        PortletUrlHelper.buildUrl("testAction", null, null,
                new HashMap(), null, null, null);
        portletUrlControl.verify();
        renderRequestControl.verify();
        renderResponseControl.verify();
    }

    public void testCreateRenderUrlWithDifferentPortletMode() throws Exception {
        renderResponseControl.expectAndReturn(renderResponse.createRenderURL(),
                url);

        url.setPortletMode(PortletMode.EDIT);
        url.setWindowState(WindowState.NORMAL);
        url.setParameters(null);
        portletUrlControl.setMatcher(MockControl.ALWAYS_MATCHER);
        renderRequestControl.replay();
        renderResponseControl.replay();
        portletUrlControl.replay();
        PortletUrlHelper.buildUrl("testAction", null, null,
                new HashMap(), null, "edit", null);
        portletUrlControl.verify();
        renderRequestControl.verify();
        renderResponseControl.verify();
    }

    public void testCreateRenderUrlWithDifferentWindowState() throws Exception {
        renderResponseControl.expectAndReturn(renderResponse.createRenderURL(),
                url);

        url.setPortletMode(PortletMode.VIEW);
        url.setWindowState(WindowState.MAXIMIZED);
        url.setParameters(null);
        portletUrlControl.setMatcher(MockControl.ALWAYS_MATCHER);
        renderRequestControl.replay();
        renderResponseControl.replay();
        portletUrlControl.replay();
        PortletUrlHelper.buildUrl("testAction", null, null,
                new HashMap(), null, null, "maximized");
        portletUrlControl.verify();
        renderRequestControl.verify();
        renderResponseControl.verify();
    }

    public void testCreateActionUrl() throws Exception {
        renderResponseControl.expectAndReturn(renderResponse.createActionURL(),
                url);

        url.setPortletMode(PortletMode.VIEW);
        url.setWindowState(WindowState.NORMAL);
        url.setParameters(null);
        portletUrlControl.setMatcher(MockControl.ALWAYS_MATCHER);
        renderRequestControl.replay();
        renderResponseControl.replay();
        portletUrlControl.replay();
        PortletUrlHelper.buildUrl("testAction", null, null,
                new HashMap(), "action", null, null);
        portletUrlControl.verify();
        renderRequestControl.verify();
        renderResponseControl.verify();
    }

}
