/*
 * $Id: PortletUrlHelperTest.java 564967 2007-08-11 20:20:33Z nilsga $
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

import com.opensymphony.xwork2.ActionContext;
import junit.framework.TestCase;
import org.apache.struts2.portlet.PortletPhase;
import org.easymock.EasyMock;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.apache.struts2.portlet.PortletConstants.ACTION_PARAM;
import static org.apache.struts2.portlet.PortletConstants.MODE_NAMESPACE_MAP;
import static org.apache.struts2.portlet.PortletConstants.MODE_PARAM;
import static org.apache.struts2.portlet.PortletConstants.PHASE;
import static org.apache.struts2.portlet.PortletConstants.REQUEST;
import static org.apache.struts2.portlet.PortletConstants.RESPONSE;

/**
 */
public class PortletUrlHelperTest extends TestCase {

    RenderResponse renderResponse;

    RenderRequest renderRequest;

    MockUrl url;

    public void setUp() throws Exception {
        super.setUp();

        renderResponse = EasyMock.createMock(RenderResponse.class);
        renderRequest = EasyMock.createMock(RenderRequest.class);
        url = new MockUrl();
        
        EasyMock.expect(renderRequest.getPortletMode()).andReturn(PortletMode.VIEW).anyTimes();
        EasyMock.expect(renderRequest.getWindowState()).andReturn(WindowState.NORMAL).anyTimes();

        Map<String, String> modeNamespaceMap = new HashMap<String, String>();
        modeNamespaceMap.put("view", "/view");
        modeNamespaceMap.put("edit", "/edit");
        modeNamespaceMap.put("help", "/help");

        Map<String, Object> context = new HashMap<String, Object>();
        context.put(REQUEST, renderRequest);
        context.put(RESPONSE, renderResponse);
        context.put(PHASE, PortletPhase.RENDER_PHASE);
        context.put(MODE_NAMESPACE_MAP, modeNamespaceMap);

        ActionContext.setContext(new ActionContext(context));

    }

    public void testCreateRenderUrlWithNoModeOrState() throws Exception {
    	EasyMock.expect(renderResponse.createRenderURL()).andReturn(url);

        EasyMock.replay(renderRequest);
        EasyMock.replay(renderResponse);

        (new PortletUrlHelper()).buildUrl("testAction", null, null,
                new HashMap<String, Object>(), null, null, null);
        assertEquals(PortletMode.VIEW, url.getPortletMode());
        assertEquals(WindowState.NORMAL, url.getWindowState());
        assertEquals("testAction", url.getParameterMap().get(ACTION_PARAM)[0]);
        assertEquals("view", url.getParameterMap().get(MODE_PARAM)[0]);
    }

    public void testCreateRenderUrlWithDifferentPortletMode() throws Exception {
    	EasyMock.expect(renderResponse.createRenderURL()).andReturn(url);

        EasyMock.replay(renderRequest);
        EasyMock.replay(renderResponse);

        (new PortletUrlHelper()).buildUrl("testAction", null, null,
                new HashMap<String, Object>(), null, "edit", null);
        
        assertEquals(PortletMode.EDIT, url.getPortletMode());
        assertEquals(WindowState.NORMAL, url.getWindowState());
        assertEquals("testAction", url.getParameterMap().get(ACTION_PARAM)[0]);
        assertEquals("edit", url.getParameterMap().get(MODE_PARAM)[0]);
    }

    public void testCreateRenderUrlWithDifferentWindowState() throws Exception {
    	EasyMock.expect(renderResponse.createRenderURL()).andReturn(url);
        
        EasyMock.replay(renderRequest);
        EasyMock.replay(renderResponse);
        
        (new PortletUrlHelper()).buildUrl("testAction", null, null,
                new HashMap<String, Object>(), null, null, "maximized");
        
        assertEquals(PortletMode.VIEW, url.getPortletMode());
        assertEquals(WindowState.MAXIMIZED, url.getWindowState());
        assertEquals("testAction", url.getParameterMap().get(ACTION_PARAM)[0]);
        assertEquals("view", url.getParameterMap().get(MODE_PARAM)[0]);
    }

    public void testCreateActionUrl() throws Exception {
    	EasyMock.expect(renderResponse.createActionURL()).andReturn(url);
        
        EasyMock.replay(renderResponse);
        EasyMock.replay(renderRequest);
        
        (new PortletUrlHelper()).buildUrl("testAction", null, null,
                new HashMap<String, Object>(), "action", null, null);
        
        assertEquals(PortletMode.VIEW, url.getPortletMode());
        assertEquals(WindowState.NORMAL, url.getWindowState());
        assertEquals("testAction", url.getParameterMap().get(ACTION_PARAM)[0]);
        assertEquals("view", url.getParameterMap().get(MODE_PARAM)[0]);
    }
    
    @Override
    public void tearDown() {
    	EasyMock.verify(renderResponse);
    	EasyMock.verify(renderRequest);
    }
    
    private class MockUrl implements PortletURL {

    	private PortletMode portletMode;
		private WindowState windowState;
		private Map<String, String[]> parameters;
    	
		public PortletMode getPortletMode() {
			return portletMode;
		}

		public WindowState getWindowState() {
			return windowState;
		}

		public void removePublicRenderParameter(String name) {
		}

		public void setPortletMode(PortletMode portletMode) throws PortletModeException {
			this.portletMode = portletMode;
		}

		public void setWindowState(WindowState windowState) throws WindowStateException {
			this.windowState = windowState;
		}

		public void addProperty(String arg0, String arg1) {
		}

		public Map<String, String[]> getParameterMap() {
			return parameters;
		}

		public void setParameter(String name, String value) {
			parameters.put(name, new String[]{value});
		}

		public void setParameter(String name, String[] values) {
			parameters.put(name, values);
		}

		public void setParameters(Map<String, String[]> parameters) {
			this.parameters = parameters;
		}

		public void setProperty(String arg0, String arg1) {
		}

		public void setSecure(boolean arg0) throws PortletSecurityException {
		}

		public void write(Writer arg0) throws IOException {
		}

		public void write(Writer arg0, boolean arg1) throws IOException {
		}
    	
    }

}
