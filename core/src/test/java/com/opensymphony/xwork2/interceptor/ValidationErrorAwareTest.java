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

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.validator.ValidationInterceptor;
import org.junit.Assert;
import org.easymock.EasyMock;
import org.easymock.IAnswer;

import java.util.HashMap;

public class ValidationErrorAwareTest extends XWorkTestCase {

    private DefaultWorkflowInterceptor interceptor;
    private ActionInvocation invocation;
    private String result = "testing123";
    private String actionResult = "action1234";

    public void testChangeResultWhenNotifyAboutValidationErrors() throws Exception {
        // given
        ValidationInterceptor validationInterceptor = create();

        // when
        validationInterceptor.intercept(invocation);

        // then
        assertEquals(actionResult, interceptor.intercept(invocation));
    }

    public void testNotChangeResultWhenNotifyAboutValidationError() throws Exception {
        // given
        actionResult = Action.INPUT;
        ValidationInterceptor validationInterceptor = create();

        // when
        validationInterceptor.intercept(invocation);

        // then
        Assert.assertEquals(Action.INPUT, interceptor.intercept(invocation));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ActionConfig config = new ActionConfig.Builder("", "name", "").build();
        ValidateErrorAction action = EasyMock.createNiceMock(ValidateErrorAction.class);
        invocation = EasyMock.createNiceMock(ActionInvocation.class);
        interceptor = new DefaultWorkflowInterceptor();
        ActionProxy proxy = EasyMock.createNiceMock(ActionProxy.class);

        EasyMock.expect(action.actionErrorOccurred(EasyMock.<String>anyObject())).andAnswer(new IAnswer<String>() {
            public String answer() throws Throwable {
                return actionResult;
            }
        }).anyTimes();
        EasyMock.expect(action.hasErrors()).andReturn(true).anyTimes();

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
                new InterceptorConfig.Builder("model", ValidationInterceptor.class.getName()).build(), new HashMap<String, String>());
    }

    private interface ValidateErrorAction extends Action, Validateable, ValidationAware, ValidationErrorAware {

        String execute();

    }
}
