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

package org.apache.struts2.portlet.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;

import junit.framework.TestCase;

import org.apache.struts2.portlet.PortletActionConstants;
import org.easymock.EasyMock;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

public class PortletAwareInterceptorTest extends TestCase implements PortletActionConstants {

	private PortletAwareInterceptor interceptor;
	private TestAction action;
	private PortletRequest portletRequest;
	private PortletConfig portletConfig;
	private Map<String, Object> contextMap;
	private ActionInvocation invocation;
	
	protected void setUp() throws Exception {
		super.setUp();
		interceptor = new PortletAwareInterceptor();
		action = new TestAction();
		portletRequest = EasyMock.createNiceMock(PortletRequest.class);
		portletConfig = EasyMock.createNiceMock(PortletConfig.class);
		contextMap = new HashMap<String, Object>();
		invocation = EasyMock.createNiceMock(ActionInvocation.class);
		EasyMock.expect(invocation.getAction()).andReturn(action);
		EasyMock.expect(invocation.getInvocationContext()).andReturn(new ActionContext(contextMap));
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testPortletRequestIsSet() throws Exception {
		contextMap.put(REQUEST, portletRequest);
		EasyMock.replay(invocation);
		interceptor.intercept(invocation);
		assertEquals(portletRequest, action.getPortletRequest());
	}
	
	public void testPortletConfigIsSet() throws Exception {
		contextMap.put(PORTLET_CONFIG, portletConfig);
		EasyMock.replay(invocation);
		interceptor.intercept(invocation);
		assertEquals(portletConfig, action.getPortletConfig());
	}
	
	public static class TestAction implements PortletRequestAware, PortletConfigAware {

		private PortletRequest request;
		private PortletConfig config;

		public void setPortletRequest(PortletRequest request) {
			this.request = request;
		}

		public void setPortletConfig(PortletConfig portletConfig) {
			this.config = portletConfig;
		}

		public PortletConfig getPortletConfig() {
			return config;
		}

		public PortletRequest getPortletRequest() {
			return request;
		}
		
	}
}
