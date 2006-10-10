/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.dispatcher;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsTestCase;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.util.ValueStack;


/**
 * @version $Date$ $Id$
 */
public class ServletActionRedirectResultTest extends StrutsTestCase {
	
	public void testIncludeParameterInResultWithConditionParseOn() throws Exception {
		
		ResultConfig resultConfig = new ResultConfig();
		resultConfig.addParam("actionName", "someActionName");
		resultConfig.addParam("namespace", "someNamespace");
		resultConfig.addParam("encode", "true");
		resultConfig.addParam("parse", "true");
		resultConfig.addParam("location", "someLocation");
		resultConfig.addParam("prependServletContext", "true");
		resultConfig.addParam("method", "someMethod");
		resultConfig.addParam("param1", "${#value1}");
		resultConfig.addParam("param2", "${#value2}");
		resultConfig.addParam("param3", "${#value3}");
		
		
		
		ActionContext context = ActionContext.getContext();
		ValueStack stack = context.getValueStack();
		context.getContextMap().put("value1", "value 1");
		context.getContextMap().put("value2", "value 2");
		context.getContextMap().put("value3", "value 3");
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse res = new MockHttpServletResponse();
		context.put(ServletActionContext.HTTP_REQUEST, req);
		context.put(ServletActionContext.HTTP_RESPONSE, res);
		
		
		Map<String, ResultConfig> results=  new HashMap<String, ResultConfig>();
		results.put("myResult", resultConfig);
		
		ActionConfig actionConfig = new ActionConfig();
		actionConfig.setResults(results);
		
		ServletActionRedirectResult result = new ServletActionRedirectResult();
		result.setActionName("myAction");
		result.setNamespace("/myNamespace");
		result.setParse(true);
		result.setEncode(false);
		result.setPrependServletContext(false);
		
		IMocksControl control = EasyMock.createControl();
		ActionProxy mockActionProxy = control.createMock(ActionProxy.class);
		ActionInvocation mockInvocation = control.createMock(ActionInvocation.class);
		mockInvocation.getProxy();
		control.andReturn(mockActionProxy);
		mockInvocation.getResultCode();
		control.andReturn("myResult");
		mockActionProxy.getConfig();
		control.andReturn(actionConfig);
		mockInvocation.getInvocationContext();
		control.andReturn(context);
		mockInvocation.getStack();
		control.andReturn(stack);
		control.anyTimes();
		
		control.replay();
		
		result.execute(mockInvocation);
		assertEquals("/myNamespace/myAction.action?param2=value+2&param1=value+1&param3=value+3", res.getRedirectedUrl());
		
		control.verify();
	}
	
	public void testIncludeParameterInResult() throws Exception {
		
		ResultConfig resultConfig = new ResultConfig();
		resultConfig.addParam("actionName", "someActionName");
		resultConfig.addParam("namespace", "someNamespace");
		resultConfig.addParam("encode", "true");
		resultConfig.addParam("parse", "true");
		resultConfig.addParam("location", "someLocation");
		resultConfig.addParam("prependServletContext", "true");
		resultConfig.addParam("method", "someMethod");
		resultConfig.addParam("param1", "value 1");
		resultConfig.addParam("param2", "value 2");
		resultConfig.addParam("param3", "value 3");
		
		ActionContext context = ActionContext.getContext();
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse res = new MockHttpServletResponse();
		context.put(ServletActionContext.HTTP_REQUEST, req);
		context.put(ServletActionContext.HTTP_RESPONSE, res);
		
		
		Map<String, ResultConfig> results=  new HashMap<String, ResultConfig>();
		results.put("myResult", resultConfig);
		
		ActionConfig actionConfig = new ActionConfig();
		actionConfig.setResults(results);
		
		ServletActionRedirectResult result = new ServletActionRedirectResult();
		result.setActionName("myAction");
		result.setNamespace("/myNamespace");
		result.setParse(false);
		result.setEncode(false);
		result.setPrependServletContext(false);
		
		IMocksControl control = EasyMock.createControl();
		ActionProxy mockActionProxy = control.createMock(ActionProxy.class);
		ActionInvocation mockInvocation = control.createMock(ActionInvocation.class);
		mockInvocation.getProxy();
		control.andReturn(mockActionProxy);
		mockInvocation.getResultCode();
		control.andReturn("myResult");
		mockActionProxy.getConfig();
		control.andReturn(actionConfig);
		mockInvocation.getInvocationContext();
		control.andReturn(context);
		
		control.replay();
		
		result.execute(mockInvocation);
		assertEquals("/myNamespace/myAction.action?param2=value+2&param1=value+1&param3=value+3", res.getRedirectedUrl());
		
		control.verify();
	}
}
