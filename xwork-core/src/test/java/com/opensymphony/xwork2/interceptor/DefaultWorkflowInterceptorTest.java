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
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.validator.ValidationInterceptor;

import java.util.HashMap;

import org.easymock.EasyMock;
import org.easymock.IAnswer;


/**
 * Unit test for {@link DefaultWorkflowInterceptor}.
 *
 * @author Jason Carreira
 */
public class DefaultWorkflowInterceptorTest extends XWorkTestCase {

    DefaultWorkflowInterceptor interceptor;
    private ActionInvocation invocation;
    private Action action;
    private ActionProxy proxy;
    private ActionConfig config;
    private String result = "testing123";


    public void testInvokesActionInvocationIfNoErrors() throws Exception {
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.intercept(invocation);
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testReturnsInputWithoutExecutingIfHasErrors() throws Exception {
        result = Action.INPUT;

        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.intercept(invocation);
        assertEquals(Action.INPUT, interceptor.intercept(invocation));
    }

    public void testExcludesMethod() throws Exception {
        interceptor.setExcludeMethods("execute");

        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("execute");
        interceptor.setExcludeMethods("execute");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testExcludesMethodWithWildCard() throws Exception {
        interceptor.setExcludeMethods("*");

        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.intercept(invocation);
        validationInterceptor.setExcludeMethods("*");
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesMethodWithWildcard() throws Exception {
        interceptor.setIncludeMethods("*");

        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setIncludeMethods("*");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }


    public void testIncludesMethod() throws Exception {
        interceptor.setIncludeMethods("execute");

        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setIncludeMethods("execute");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethod() throws Exception {
        interceptor.setExcludeMethods("execute,input,validate");
        interceptor.setIncludeMethods("execute");
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("execute,input,validate");
        validationInterceptor.setIncludeMethods("execute");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethodAllWildCarded() throws Exception {
        interceptor.setExcludeMethods("*");
        interceptor.setIncludeMethods("*");
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("*");
        validationInterceptor.setIncludeMethods("*");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethodWithExcludeWildcard() throws Exception {
        interceptor.setExcludeMethods("*");
        interceptor.setIncludeMethods("execute");
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("*");
        validationInterceptor.setIncludeMethods("execute");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethodWithIncludeWildcardAndNoMatches() throws Exception {
        interceptor.setExcludeMethods("execute,input,validate");
        interceptor.setIncludeMethods("*");
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("execute,input,validate");
        validationInterceptor.setIncludeMethods("*");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testIncludesAndExcludesMethodWithIncludeWildcard() throws Exception {
        interceptor.setExcludeMethods("input,validate");
        interceptor.setIncludeMethods("*");
        
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("input,validate");
        validationInterceptor.setIncludeMethods("*");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }

    public void testNoValidateAction() throws Exception {
        ValidationInterceptor validationInterceptor = create();
        validationInterceptor.setExcludeMethods("execute,input,validate");
        validationInterceptor.setIncludeMethods("execute");
        validationInterceptor.intercept(invocation);
        
        assertEquals(result, interceptor.intercept(invocation));
    }
    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        config = new ActionConfig.Builder("", "name", "").build();
        action = EasyMock.createNiceMock(ValidateAction.class);
        invocation = EasyMock.createNiceMock(ActionInvocation.class);
        interceptor = new DefaultWorkflowInterceptor();
        proxy = EasyMock.createNiceMock(ActionProxy.class);

        EasyMock.expect(invocation.getProxy()).andReturn(proxy).anyTimes();
        EasyMock.expect(invocation.getAction()).andReturn(action).anyTimes();
        EasyMock.expect(invocation.invoke()).andAnswer(new IAnswer<String>() {
            public String answer() throws Throwable {
                return result;
            }
        }).anyTimes();

        EasyMock.expect(proxy.getConfig()).andReturn(config).anyTimes();
        EasyMock.expect(proxy.getMethod()).andReturn("execute").anyTimes();


        EasyMock.replay(invocation);
        EasyMock.replay(action);
        EasyMock.replay(proxy);

        ActionContext contex = new ActionContext(new HashMap<String, Object>());
        ActionContext.setContext(contex);
        contex.setActionInvocation(invocation);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    protected ValidationInterceptor create() {
        ObjectFactory objectFactory = container.getInstance(ObjectFactory.class);
        return (ValidationInterceptor) objectFactory.buildInterceptor(
                new InterceptorConfig.Builder("model", ValidationInterceptor.class.getName()).build(), new HashMap());
    }

    
    

    private interface ValidateAction extends Action, Validateable, ValidationAware {
    }
}
