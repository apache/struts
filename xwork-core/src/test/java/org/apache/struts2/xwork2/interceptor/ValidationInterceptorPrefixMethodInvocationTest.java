/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package org.apache.struts2.xwork2.interceptor;

import org.apache.struts2.xwork2.ActionContext;
import org.apache.struts2.xwork2.config.entities.InterceptorConfig;
import org.apache.struts2.xwork2.Action;
import org.apache.struts2.xwork2.ActionInvocation;
import org.apache.struts2.xwork2.ActionProxy;
import org.apache.struts2.xwork2.ObjectFactory;
import org.apache.struts2.xwork2.ValidationAware;
import org.apache.struts2.xwork2.config.entities.ActionConfig;
import org.apache.struts2.xwork2.validator.ValidationInterceptor;
import org.apache.struts2.xwork2.Validateable;
import org.apache.struts2.xwork2.XWorkTestCase;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

import java.util.HashMap;

/**
 * Test ValidationInterceptor's prefix method invocation capabilities.
 * 
 * @author tm_jee
 * @version $Date: 2011-12-02 12:24:48 +0100 (Fri, 02 Dec 2011) $ $Id: ValidationInterceptorPrefixMethodInvocationTest.java 1209415 2011-12-02 11:24:48Z lukaszlenart $
 */
public class ValidationInterceptorPrefixMethodInvocationTest extends XWorkTestCase {
    private ActionInvocation invocation;
    private ActionConfig config;
    private ActionProxy proxy;
    private ValidateAction action;
    private String result;
    private String method;

    public void testPrefixMethodInvocation1() throws Exception {
		method = "save";
		result = Action.INPUT;
		
		ValidationInterceptor interceptor = create();
		String result = interceptor.intercept(invocation);
		
		assertEquals(Action.INPUT, result);
	}
	
	public void testPrefixMethodInvocation2() throws Exception {
		method = "save";
		result = "okok";

		ValidationInterceptor interceptor = create();
		String result = interceptor.intercept(invocation);
		
		assertEquals("okok", result);
	}
	
	protected ValidationInterceptor create() {
	    ObjectFactory objectFactory = container.getInstance(ObjectFactory.class);
	    return (ValidationInterceptor) objectFactory.buildInterceptor(
                new InterceptorConfig.Builder("model", ValidationInterceptor.class.getName()).build(), new HashMap());
	}
	
	private interface ValidateAction extends Action, Validateable, ValidationAware {
		void validateDoSave();
		void validateSubmit();
		String submit();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        config = new ActionConfig.Builder("", "action", "").build();
        invocation = EasyMock.createNiceMock(ActionInvocation.class);
        proxy = EasyMock.createNiceMock(ActionProxy.class);
        action = EasyMock.createNiceMock(ValidateAction.class);


        EasyMock.expect(invocation.getProxy()).andReturn(proxy).anyTimes();
        EasyMock.expect(invocation.getAction()).andReturn(action).anyTimes();
        EasyMock.expect(invocation.invoke()).andAnswer(new IAnswer<String>() {
            public String answer() throws Throwable {
                return result;
            }
        }).anyTimes();

        EasyMock.expect(proxy.getConfig()).andReturn(config).anyTimes();
        EasyMock.expect(proxy.getMethod()).andAnswer(new IAnswer<String>() {
            public String answer() throws Throwable {
                return method;
            }
        }).anyTimes();


        EasyMock.replay(invocation);
        EasyMock.replay(action);
        EasyMock.replay(proxy);

        ActionContext contex = new ActionContext(new HashMap<String, Object>());
        ActionContext.setContext(contex);
        contex.setActionInvocation(invocation);
    }
}
