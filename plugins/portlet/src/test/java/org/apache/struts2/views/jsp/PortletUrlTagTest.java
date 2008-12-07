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

package org.apache.struts2.views.jsp;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.portlet.PortletActionConstants;
import org.apache.struts2.portlet.servlet.PortletServletRequest;
import org.apache.struts2.portlet.util.PortletUrlHelper;
import org.springframework.mock.web.portlet.MockPortalContext;
import org.springframework.mock.web.portlet.MockPortletContext;
import org.springframework.mock.web.portlet.MockPortletURL;
import org.springframework.mock.web.portlet.MockRenderRequest;
import org.springframework.mock.web.portlet.MockRenderResponse;

import com.mockobjects.servlet.MockJspWriter;
import com.mockobjects.servlet.MockPageContext;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.util.ValueStack;

/**
 */
@SuppressWarnings("unchecked")
public class PortletUrlTagTest extends StrutsTestCase {

	private URLTag tag = new URLTag();

	private ValueStack stack = null;

	private ActionContext context = null;

	private MockRenderRequest renderRequest;

	private MockPortletUrl renderUrl;

	private MockPortletUrl actionUrl;

	private MockRenderResponse renderResponse;

	private MockPageContext pageContext;

	private MockActionInvocation actionInvocation;

	private MockActionProxy actionProxy;

	private MockJspWriter jspWriter;
	
	private MockPortletContext portletContext;

	public void setUp() throws Exception {
		super.setUp();

		context = ActionContext.getContext();
		stack = context.getValueStack();

		portletContext = new MockPortletContext();
		renderRequest = new MockRenderRequest();
		renderRequest.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);
		renderUrl = new MockPortletUrl("render");
		actionUrl = new MockPortletUrl("action");
		renderResponse = new MockRenderResponse() {
			@Override
			public PortletURL createRenderURL() {
				return renderUrl;
			}

			@Override
			public PortletURL createActionURL() {
				return actionUrl;
			}
		};

		Map modeMap = new HashMap();
		modeMap.put(PortletMode.VIEW, "/view");
		modeMap.put(PortletMode.HELP, "/help");
		modeMap.put(PortletMode.EDIT, "/edit");

		context.put(PortletActionConstants.REQUEST, renderRequest);
		context.put(PortletActionConstants.RESPONSE, renderResponse);
		context.put(PortletActionConstants.PHASE, PortletActionConstants.RENDER_PHASE);
		context.put(PortletActionConstants.MODE_NAMESPACE_MAP, modeMap);
		context.put(StrutsStatics.STRUTS_PORTLET_CONTEXT, portletContext);

		actionInvocation = new MockActionInvocation();
		actionProxy = new MockActionProxy();

		actionInvocation.setAction(new Object());
		actionInvocation.setProxy(actionProxy);
		actionInvocation.setStack(stack);

		context.setActionInvocation(actionInvocation);

		pageContext = new MockPageContext();
		pageContext.setRequest(new PortletServletRequest(renderRequest, null));
		jspWriter = new MockJspWriter();
		pageContext.setJspWriter(jspWriter);

		tag.setPageContext(pageContext);

	}

	public void testEnsureParamsAreStringArrays() {
		Map params = new HashMap();
		params.put("param1", "Test1");
		params.put("param2", new String[] { "Test2" });

		Map result = PortletUrlHelper.ensureParamsAreStringArrays(params);
		assertEquals(2, result.size());
		assertTrue(result.get("param1") instanceof String[]);
	}

	public void testSetWindowState() throws Exception {

		tag.setAction("testAction");
		tag.setWindowState("maximized");
		tag.doStartTag();
		tag.doEndTag();

		assertEquals("/view/testAction", renderUrl.getParameter(PortletActionConstants.ACTION_PARAM));
		assertEquals(PortletMode.VIEW.toString(), renderUrl.getParameter(PortletActionConstants.MODE_PARAM));
		assertEquals(PortletMode.VIEW, renderUrl.getPortletMode());
		assertEquals(WindowState.MAXIMIZED, renderUrl.getWindowState());

	}

	public void testSetPortletMode() throws Exception {

		tag.setAction("testAction");
		tag.setPortletMode("help");
		tag.doStartTag();
		tag.doEndTag();

		assertEquals("/help/testAction", renderUrl.getParameter(PortletActionConstants.ACTION_PARAM));
		assertEquals(PortletMode.HELP.toString(), renderUrl.getParameter(PortletActionConstants.MODE_PARAM));
		assertEquals(PortletMode.HELP, renderUrl.getPortletMode());
		assertEquals(WindowState.NORMAL, renderUrl.getWindowState());
	}

	public void testUrlWithQueryParams() throws Exception {

		tag.setAction("testAction?testParam1=testValue1");
		tag.doStartTag();
		tag.doEndTag();

		assertEquals("/view/testAction", renderUrl.getParameter(PortletActionConstants.ACTION_PARAM));
		assertEquals("testValue1", renderUrl.getParameter("testParam1"));
		assertEquals(PortletMode.VIEW.toString(), renderUrl.getParameter(PortletActionConstants.MODE_PARAM));
		assertEquals(PortletMode.VIEW, renderUrl.getPortletMode());
		assertEquals(WindowState.NORMAL, renderUrl.getWindowState());
	}

	public void testActionUrl() throws Exception {

		tag.setAction("testAction");
		tag.setPortletUrlType("action");
		tag.doStartTag();
		tag.doEndTag();

		assertEquals("/view/testAction", actionUrl.getParameter(PortletActionConstants.ACTION_PARAM));
		assertEquals(PortletMode.VIEW, actionUrl.getPortletMode());
		assertEquals(WindowState.NORMAL, actionUrl.getWindowState());
	}

	public void testResourceUrl() throws Exception {
		renderRequest.setContextPath("/myPortlet");
		jspWriter.setExpectedData("/myPortlet/image.gif");
		tag.setValue("image.gif");
		tag.doStartTag();
		tag.doEndTag();
		jspWriter.verify();
	}

	public void testResourceUrlWithNestedParam() throws Exception {
		renderRequest.setContextPath("/myPortlet");
		jspWriter.setExpectedData("/myPortlet/image.gif?testParam1=testValue1");

		ParamTag paramTag = new ParamTag();
		paramTag.setPageContext(pageContext);
		paramTag.setParent(tag);
		paramTag.setName("testParam1");
		paramTag.setValue("'testValue1'");
		tag.setValue("image.gif");
		tag.doStartTag();
		paramTag.doStartTag();
		paramTag.doEndTag();
		tag.doEndTag();
		jspWriter.verify();
	}

	public void testResourceUrlWithTwoNestedParam() throws Exception {
		renderRequest.setContextPath("/myPortlet");
		jspWriter.setExpectedData("/myPortlet/image.gif?testParam1=testValue1&testParam2=testValue2");

		ParamTag paramTag = new ParamTag();
		paramTag.setPageContext(pageContext);
		paramTag.setParent(tag);
		paramTag.setName("testParam1");
		paramTag.setValue("'testValue1'");
		ParamTag paramTag2 = new ParamTag();
		paramTag2.setPageContext(pageContext);
		paramTag2.setParent(tag);
		paramTag2.setName("testParam2");
		paramTag2.setValue("'testValue2'");
		tag.setValue("image.gif");
		tag.doStartTag();
		paramTag.doStartTag();
		paramTag.doEndTag();
		paramTag2.doStartTag();
		paramTag2.doEndTag();
		tag.doEndTag();
		jspWriter.verify();
	}
	
	public void testResourceUrlWithNestedParamThatIsNotString() throws Exception {
		renderRequest.setContextPath("/myPortlet");
		jspWriter.setExpectedData("/myPortlet/image.gif?id=5");
		
		ParamTag paramTag = new ParamTag();
		paramTag.setPageContext(pageContext);
		paramTag.setParent(tag);
		paramTag.setName("id");
		paramTag.setValue("5");
		
		tag.setValue("image.gif");
		tag.doStartTag();
		paramTag.doStartTag();
		paramTag.doEndTag();
		tag.doEndTag();
		jspWriter.verify();
	}
	
	public void testResourceUrlWithNestedOgnlExpressionParamThatIsNotString() throws Exception {
		renderRequest.setContextPath("/myPortlet");
		jspWriter.setExpectedData("/myPortlet/image.gif?id=5");
		
		Object o = new Object() {
			public Integer getId() {
				return 5;
			}
		};
		tag.getStack().push(o);
		
		ParamTag paramTag = new ParamTag();
		paramTag.setPageContext(pageContext);
		paramTag.setParent(tag);
		paramTag.setName("id");
		paramTag.setValue("id");
		
		tag.setValue("image.gif");
		tag.doStartTag();
		paramTag.doStartTag();
		paramTag.doEndTag();
		tag.doEndTag();
		jspWriter.verify();
	}

	public void testUrlWithMethod() throws Exception {
		tag.setAction("testAction");
		tag.setMethod("input");
		tag.doStartTag();
		tag.doEndTag();

		assertEquals("/view/testAction!input", renderUrl.getParameter(PortletActionConstants.ACTION_PARAM));
		assertEquals(PortletMode.VIEW.toString(), renderUrl.getParameter(PortletActionConstants.MODE_PARAM));
		assertEquals(PortletMode.VIEW, renderUrl.getPortletMode());
		assertEquals(WindowState.NORMAL, renderUrl.getWindowState());
	}

	public void testUrlWithNoActionOrMethod() throws Exception {
		actionProxy.setActionName("currentExecutingAction");
		actionProxy.setNamespace("/currentNamespace");
		tag.doStartTag();
		tag.doEndTag();

		assertEquals("/view/currentNamespace/currentExecutingAction", renderUrl
				.getParameter(PortletActionConstants.ACTION_PARAM));
		assertEquals(PortletMode.VIEW.toString(), renderUrl.getParameter(PortletActionConstants.MODE_PARAM));
		assertEquals(PortletMode.VIEW, renderUrl.getPortletMode());
		assertEquals(WindowState.NORMAL, renderUrl.getWindowState());
	}

	public void testUrlShouldNotIncludeParamsFromHttpQueryString() throws Exception {

		PortletServletRequestWithQueryString req = new PortletServletRequestWithQueryString(renderRequest, null);
		req.setQueryString("thisParamShouldNotBeIncluded=thisValueShouldNotBeIncluded");
		pageContext.setRequest(req);
		tag.setAction("testAction?testParam1=testValue1");
		tag.doStartTag();
		tag.doEndTag();

		assertEquals("/view/testAction", renderUrl.getParameter(PortletActionConstants.ACTION_PARAM));
		assertEquals("testValue1", renderUrl.getParameter("testParam1"));
		assertNull(renderUrl.getParameter("thisParamShouldNotBeIncluded"));
		assertEquals(PortletMode.VIEW.toString(), renderUrl.getParameter(PortletActionConstants.MODE_PARAM));
		assertEquals(PortletMode.VIEW, renderUrl.getPortletMode());
		assertEquals(WindowState.NORMAL, renderUrl.getWindowState());
	}

	public void testUrlShouldIgnoreIncludeParams() throws Exception {
		PortletServletRequestWithQueryString req = new PortletServletRequestWithQueryString(renderRequest, null);
		req.setQueryString("thisParamShouldNotBeIncluded=thisValueShouldNotBeIncluded");
		pageContext.setRequest(req);
		tag.setAction("testAction?testParam1=testValue1");
		tag.setIncludeParams("GET");
		tag.doStartTag();
		tag.doEndTag();

		assertEquals("/view/testAction", renderUrl.getParameter(PortletActionConstants.ACTION_PARAM));
		assertEquals("testValue1", renderUrl.getParameter("testParam1"));
		assertNull(renderUrl.getParameter("thisParamShouldNotBeIncluded"));
		assertEquals(PortletMode.VIEW.toString(), renderUrl.getParameter(PortletActionConstants.MODE_PARAM));
		assertEquals(PortletMode.VIEW, renderUrl.getPortletMode());
		assertEquals(WindowState.NORMAL, renderUrl.getWindowState());
	}

	private static class PortletServletRequestWithQueryString extends PortletServletRequest {

		private String queryString;

		public PortletServletRequestWithQueryString(PortletRequest portletRequest, PortletContext portletContext) {
			super(portletRequest, portletContext);
		}

		public void setQueryString(String queryString) {
			this.queryString = queryString;
		}

		@Override
		public String getQueryString() {
			return queryString;
		}

	}

	private static class MockPortletUrl extends MockPortletURL {

		private PortletMode portletMode;

		private WindowState windowState;

		public MockPortletUrl(String urlType) {
			super(new MockPortalContext(), urlType);
		}

		@Override
		public void setPortletMode(PortletMode portletMode) throws PortletModeException {
			super.setPortletMode(portletMode);
			this.portletMode = portletMode;
		}

		public PortletMode getPortletMode() {
			return portletMode;
		}

		@Override
		public void setWindowState(WindowState windowState) throws WindowStateException {
			super.setWindowState(windowState);
			this.windowState = windowState;
		}

		public WindowState getWindowState() {
			return windowState;
		}

	}
}
