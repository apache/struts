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
package org.apache.struts2.components;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.PortletURL;

import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.portlet.PortletActionConstants;
import org.apache.struts2.portlet.servlet.PortletServletRequest;
import org.apache.struts2.portlet.servlet.PortletServletResponse;
import org.springframework.mock.web.portlet.MockPortalContext;
import org.springframework.mock.web.portlet.MockPortletURL;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.util.ValueStack;

public class PortletUrlRendererTest extends StrutsTestCase {

	PortletUrlRenderer renderer;

	public void setUp() throws Exception {
		super.setUp();
		renderer = new PortletUrlRenderer();
	}

	/**
	 * Ensure that the namespace of the current executing action is used when no
	 * namespace is specified. (WW-1875)
	 */
	public void testShouldIncludeCurrentNamespaceIfNoNamespaceSpecifiedForRenderUrl()
			throws Exception {
		final MockPortletURL portletUrl = new MockPortletURL(
				new MockPortalContext(), "render");
		MockRenderRequest request = new MockRenderRequest();
		MockRenderResponse response = new MockRenderResponse() {
			@Override
			public PortletURL createRenderURL() {
				return portletUrl;
			}
		};

		ActionContext ctx = ActionContext.getContext();
		ctx.put(PortletActionConstants.PHASE,
				PortletActionConstants.RENDER_PHASE);
		ctx.put(PortletActionConstants.REQUEST, request);
		ctx.put(PortletActionConstants.RESPONSE, response);

		Map<PortletMode, String> modeMap = new HashMap<PortletMode, String>();
		modeMap.put(PortletMode.VIEW, "/view");
		ctx.put(PortletActionConstants.MODE_NAMESPACE_MAP, modeMap);
		ValueStack stack = ctx.getValueStack();
		URL url = new URL(stack, new PortletServletRequest(request, null),
				new PortletServletResponse(response));

		MockActionInvocation ai = new MockActionInvocation();
		MockActionProxy ap = new MockActionProxy();
		ap.setActionName("testAction");
		ap.setNamespace("/current_namespace");
		ai.setProxy(ap);
		ai.setStack(stack);
		ai.setAction(new Object());
		ctx.setActionInvocation(ai);

		StringWriter renderOutput = new StringWriter();
		renderer.renderUrl(renderOutput, url);

		String action = portletUrl
				.getParameter(PortletActionConstants.ACTION_PARAM);
		assertEquals("/view/current_namespace/testAction", action);
	}

	/**
	 * Ensure that the namespace of the current executing action is used when no
	 * namespace is specified. (WW-1875)
	 */
	public void testShouldIncludeCurrentNamespaceIfNoNamespaceSpecifiedForRenderFormUrl()
			throws Exception {
		final MockPortletURL portletUrl = new MockPortletURL(
				new MockPortalContext(), "render");
		MockRenderRequest request = new MockRenderRequest();
		MockRenderResponse response = new MockRenderResponse() {
			@Override
			public PortletURL createActionURL() {
				return portletUrl;
			}
		};

		ActionContext ctx = ActionContext.getContext();
		ctx.put(PortletActionConstants.PHASE,
				PortletActionConstants.RENDER_PHASE);
		ctx.put(PortletActionConstants.REQUEST, request);
		ctx.put(PortletActionConstants.RESPONSE, response);

		Map<PortletMode, String> modeMap = new HashMap<PortletMode, String>();
		modeMap.put(PortletMode.VIEW, "/view");
		ctx.put(PortletActionConstants.MODE_NAMESPACE_MAP, modeMap);
		ValueStack stack = ctx.getValueStack();
		Form form = new Form(stack, new PortletServletRequest(request, null),
				new PortletServletResponse(response));

		MockActionInvocation ai = new MockActionInvocation();
		MockActionProxy ap = new MockActionProxy();
		ap.setActionName("testAction");
		ap.setNamespace("/current_namespace");
		ai.setProxy(ap);
		ai.setStack(stack);
		ai.setAction(new Object());
		ctx.setActionInvocation(ai);

		StringWriter renderOutput = new StringWriter();
		renderer.renderFormUrl(form);

		String action = portletUrl
				.getParameter(PortletActionConstants.ACTION_PARAM);
		assertEquals("/view/current_namespace/testAction", action);
	}
}
