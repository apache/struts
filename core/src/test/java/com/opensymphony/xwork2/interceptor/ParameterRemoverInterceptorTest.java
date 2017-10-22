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
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import junit.framework.TestCase;
import org.apache.struts2.dispatcher.HttpParameters;

import static org.easymock.EasyMock.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author tmjee
 * @version $Date$ $Id$
 */
public class ParameterRemoverInterceptorTest extends TestCase {

	protected Map<String, Object> contextMap;
	protected ActionContext context;
	protected ActionInvocation actionInvocation;
	
	@Override
    protected void setUp() throws Exception {
		contextMap = new LinkedHashMap<>();
		context = new ActionContext(contextMap);
		
		actionInvocation = (ActionInvocation) createMock(ActionInvocation.class);
		expect(actionInvocation.getAction()).andStubReturn(new SampleAction());
		expect(actionInvocation.getInvocationContext()).andStubReturn(context);
		expect(actionInvocation.invoke()).andStubReturn("success");
	}
	
	public void testInterception1() throws Exception {
		contextMap.put(ActionContext.PARAMETERS, HttpParameters.create(new LinkedHashMap<String, Object>() {
			{
				put("param1", new String[]{"paramValue1"});
				put("param2", new String[]{"paramValue2"});
				put("param3", new String[]{"paramValue3"});
				put("param", new String[]{"paramValue"});
			}
		}).build());
		
		replay(actionInvocation);
		
		ParameterRemoverInterceptor interceptor = new ParameterRemoverInterceptor();
		interceptor.setParamNames("param1,param2");
		interceptor.setParamValues("paramValue1,paramValue2");
		interceptor.intercept(actionInvocation);
		
		HttpParameters params = (HttpParameters) contextMap.get(ActionContext.PARAMETERS);
		assertEquals(params.keySet().size(), 2);
		assertTrue(params.contains("param3"));
		assertTrue(params.contains("param"));
		assertEquals(params.get("param3").getValue(), "paramValue3");
		assertEquals(params.get("param").getValue(), "paramValue");
		
		verify(actionInvocation);
	}
	
	
	public void testInterception2() throws Exception {
		contextMap.put(ActionContext.PARAMETERS, HttpParameters.create(new LinkedHashMap<String, Object>() {
			{
				put("param1", new String[] { "paramValue2" });
				put("param2", new String[] { "paramValue1" });
			}
		}).build());
		
		replay(actionInvocation);
		
		ParameterRemoverInterceptor interceptor = new ParameterRemoverInterceptor();
		interceptor.setParamNames("param1,param2");
		interceptor.setParamValues("paramValue1,paramValue2");
		interceptor.intercept(actionInvocation);
		
		HttpParameters params = (HttpParameters) contextMap.get(ActionContext.PARAMETERS);
		assertEquals(params.keySet().size(), 0);
		
		verify(actionInvocation);
	}
	
	
	public void testInterception3() throws Exception {
		contextMap.put(ActionContext.PARAMETERS, HttpParameters.create(new LinkedHashMap<String, Object>() {
			{
				put("param1", new String[] { "paramValueOne" });
				put("param2", new String[] { "paramValueTwo" });
			}
		}).build());
		
		replay(actionInvocation);
		
		ParameterRemoverInterceptor interceptor = new ParameterRemoverInterceptor();
		interceptor.setParamNames("param1,param2");
		interceptor.setParamValues("paramValue1,paramValue2");
		interceptor.intercept(actionInvocation);
		
		HttpParameters params = (HttpParameters) contextMap.get(ActionContext.PARAMETERS);
		assertEquals(params.keySet().size(), 2);
		assertTrue(params.contains("param1"));
		assertTrue(params.contains("param2"));
		assertEquals(params.get("param1").getValue(), "paramValueOne");
		assertEquals(params.get("param2").getValue(), "paramValueTwo");
		
		verify(actionInvocation);
	}
	
	class SampleAction extends ActionSupport {
		private static final long serialVersionUID = 7489487258845368260L;
	}
}
