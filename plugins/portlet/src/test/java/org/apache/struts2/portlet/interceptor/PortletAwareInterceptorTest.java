/*
 * $Id: PortletAwareInterceptorTest.java 590812 2007-10-31 20:32:54Z apetrelli $
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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import junit.framework.TestCase;
import org.apache.struts2.portlet.PortletConstants;
import org.easymock.EasyMock;

import javax.portlet.PortletRequest;
import java.util.HashMap;
import java.util.Map;

public class PortletAwareInterceptorTest extends TestCase {

	private PortletAwareInterceptor interceptor;
	
	protected void setUp() throws Exception {
		super.setUp();
		interceptor = new PortletAwareInterceptor();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testPortletRequestIsSet() throws Exception {
		PortletRequest request = EasyMock.createMock(PortletRequest.class);
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put(PortletConstants.REQUEST, request);
		PortletRequestAware action = EasyMock.createMock(PortletRequestAware.class);
		action.setPortletRequest(request);
		
		ActionInvocation invocation = EasyMock.createNiceMock(ActionInvocation.class);
		EasyMock.expect(invocation.getInvocationContext()).andReturn(new ActionContext(ctx));
		EasyMock.expect(invocation.getAction()).andReturn(action);
		
		EasyMock.replay(action);
		EasyMock.replay(invocation);
		
		interceptor.intercept(invocation);
		
		EasyMock.verify(action);
	}
}
