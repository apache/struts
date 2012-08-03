/*
 * $Id: PortletStateInterceptorTest.java 590812 2007-10-31 20:32:54Z apetrelli $
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
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.apache.struts2.portlet.PortletPhase;
import org.apache.struts2.portlet.dispatcher.DirectRenderFromEventAction;
import org.easymock.EasyMock;

import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import java.util.HashMap;
import java.util.Map;

import static org.apache.struts2.portlet.PortletConstants.EVENT_ACTION;
import static org.apache.struts2.portlet.PortletConstants.PHASE;
import static org.apache.struts2.portlet.PortletConstants.REQUEST;
import static org.apache.struts2.portlet.PortletConstants.RESPONSE;
import static org.apache.struts2.portlet.PortletConstants.STACK_FROM_EVENT_PHASE;

public class PortletStateInterceptorTest extends StrutsTestCase {

	private PortletStateInterceptor interceptor;
	
	public void setUp() throws Exception {
	    super.setUp();
		interceptor = new PortletStateInterceptor();
	}
	
	public void testCopyValueStackFromEventToRenderPhase() throws Exception {
		ActionResponse actionResponse = EasyMock.createNiceMock(ActionResponse.class);
		ActionInvocation invocation = EasyMock.createNiceMock(ActionInvocation.class);
		
		Map<String, Object> ctxMap = new HashMap<String, Object>();
		ctxMap.put(PHASE, PortletPhase.ACTION_PHASE);
		ctxMap.put(RESPONSE, actionResponse);
		Map<String, Object> session = new HashMap<String, Object>();
		
		ActionContext ctx = new ActionContext(ctxMap);
		ctx.setSession(session);
		EasyMock.expect(invocation.getInvocationContext()).andStubReturn(ctx);
		actionResponse.setRenderParameter(EVENT_ACTION, "true");
		
		ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
		EasyMock.expect(invocation.getStack()).andStubReturn(stack);
		
		EasyMock.replay(actionResponse);
		EasyMock.replay(invocation);
		
		interceptor.intercept(invocation);
		
		EasyMock.verify(actionResponse);
		EasyMock.verify(invocation);
		
		assertSame(stack, session.get(STACK_FROM_EVENT_PHASE));
		
	}
	
	public void testDoNotRestoreValueStackInRenderPhaseWhenProperPrg() throws Exception {
		RenderRequest renderRequest = EasyMock.createNiceMock(RenderRequest.class);
		ActionInvocation invocation = EasyMock.createNiceMock(ActionInvocation.class);
		
		
		ValueStack eventPhaseStack = container.getInstance(ValueStackFactory.class).createValueStack();
		eventPhaseStack.set("testKey", "testValue");
		
		ValueStack currentStack = container.getInstance(ValueStackFactory.class).createValueStack();
		currentStack.set("anotherTestKey", "anotherTestValue");
		
		Map<String, Object> ctxMap = new HashMap<String, Object>();
		Map<String, Object> session = new HashMap<String, Object>();
		
		session.put(STACK_FROM_EVENT_PHASE, eventPhaseStack);
		
		ctxMap.put(PHASE, PortletPhase.RENDER_PHASE);
		ctxMap.put(REQUEST, renderRequest);
		
		ActionContext ctx = new ActionContext(ctxMap);
		ctx.setSession(session);
		
		EasyMock.expect(invocation.getInvocationContext()).andStubReturn(ctx);
		EasyMock.expect(invocation.getStack()).andStubReturn(currentStack);
		EasyMock.expect(invocation.getAction()).andStubReturn(new DefaultActionSupport());
		EasyMock.expect(renderRequest.getParameter(EVENT_ACTION)).andStubReturn("true");
		
		EasyMock.replay(renderRequest);
		EasyMock.replay(invocation);
		
		interceptor.intercept(invocation);
		
		ValueStack resultingStack = invocation.getStack();
		
		assertNull(resultingStack.findValue("testKey"));
		assertEquals("anotherTestValue", resultingStack.findValue("anotherTestKey"));
		
		
	}
	
	public void testRestoreValueStackInRenderPhaseWhenNotProperPrg() throws Exception {
		RenderRequest renderRequest = EasyMock.createNiceMock(RenderRequest.class);
		ActionInvocation invocation = EasyMock.createNiceMock(ActionInvocation.class);
		
		ValueStack eventPhaseStack = container.getInstance(ValueStackFactory.class).createValueStack();
		eventPhaseStack.set("testKey", "testValue");
		
		ValueStack currentStack = container.getInstance(ValueStackFactory.class).createValueStack();
		currentStack.set("anotherTestKey", "anotherTestValue");
		
		EasyMock.expect(invocation.getStack()).andStubReturn(currentStack);
		
		Map<String, Object> ctxMap = new HashMap<String, Object>();
		Map<String, Object> session = new HashMap<String, Object>();
		
		session.put(STACK_FROM_EVENT_PHASE, eventPhaseStack);
		
		ctxMap.put(PHASE, PortletPhase.RENDER_PHASE);
		ctxMap.put(REQUEST, renderRequest);
		
		ActionContext ctx = new ActionContext(ctxMap);
		ctx.setSession(session);
		
		EasyMock.expect(invocation.getInvocationContext()).andStubReturn(ctx);
		EasyMock.expect(invocation.getStack()).andStubReturn(currentStack);
		EasyMock.expect(invocation.getAction()).andStubReturn(new DirectRenderFromEventAction());
		EasyMock.expect(renderRequest.getParameter(EVENT_ACTION)).andStubReturn("true");
		
		EasyMock.replay(renderRequest);
		EasyMock.replay(invocation);
		
		interceptor.intercept(invocation);
		
		ValueStack resultingStack = invocation.getStack();
		assertEquals("testValue", resultingStack.findValue("testKey"));
		assertEquals("anotherTestValue", resultingStack.findValue("anotherTestKey"));
		
		
	}
}
