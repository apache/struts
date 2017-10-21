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
package org.apache.struts2.components;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.validator.ValidationInterceptor;
import com.opensymphony.xwork2.validator.validators.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.validators.RequiredStringValidator;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;
import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;
import org.easymock.EasyMock;

import java.util.List;
import java.util.Map;

/**
 * <code>FormTest</code>
 *
 */
public class FormTest extends AbstractUITagTest {
	private ValidationInterceptor validationInterceptor;

    public void testTestFormGetValidators() {
        checkValidateAnnotatedMethodOnly(false, null, 1, 2, 1);
    }
    public void testAnnotatedFormGetValidators() {
        checkValidateAnnotatedMethodOnly(true, null, 1, 2, 1);
    }
    public void testValidateAnnotatedMethodOnlyGetValidators1() {
        checkValidateAnnotatedMethodOnly(true, "annotatedExecute1", 0, 1, 1);
    }
    public void testValidateAnnotatedMethodOnlyGetValidators2() {
        checkValidateAnnotatedMethodOnly(true, "annotatedExecute2", 0, 1, 0);
    }

    private void checkValidateAnnotatedMethodOnly(boolean validateAnnotatedMethodOnly, String methodName,
    		int expectedFooValidators, int expectedStatusValidators, int expectedResultValidators) {
		Form form = new Form(stack, request, response);
        container.inject(form);
        form.getParameters().put("actionClass", TestAction.class);

        form.setAction("actionName" + (methodName != null ? "!" + methodName : ""));
        validationInterceptor.setValidateAnnotatedMethodOnly(validateAnnotatedMethodOnly);

        List v = form.getValidators("foo");
        assertEquals(expectedFooValidators, v.size());
        for (Object validator : v) {
        	assertEquals(RequiredFieldValidator.class, validator.getClass());
		}

        v = form.getValidators("status");
        assertEquals(expectedStatusValidators, v.size());
        for (Object validator : v) {
        	assertEquals(RequiredFieldValidator.class, validator.getClass());
		}

        v = form.getValidators("result");
        assertEquals(expectedResultValidators, v.size());
        for (Object validator : v) {
        	assertEquals(RequiredStringValidator.class, validator.getClass());
		}
	}

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validationInterceptor = new ValidationInterceptor();
        validationInterceptor.setIncludeMethods("*");

        ActionConfig config = new ActionConfig.Builder("", "name", "")
        	.addInterceptor(new InterceptorMapping("validationInterceptor", validationInterceptor))
        	.build();
        ActionInvocation invocation = EasyMock.createNiceMock(ActionInvocation.class);
        ActionProxy proxy = EasyMock.createNiceMock(ActionProxy.class);

        EasyMock.expect(invocation.getProxy()).andReturn(proxy).anyTimes();
        EasyMock.expect(invocation.getAction()).andReturn(null).anyTimes();
        EasyMock.expect(invocation.invoke()).andReturn(Action.SUCCESS).anyTimes();
        EasyMock.expect(proxy.getMethod()).andReturn("execute").anyTimes();
        EasyMock.expect(proxy.getConfig()).andReturn(config).anyTimes();
        
        EasyMock.replay(invocation);
        EasyMock.replay(proxy);

        Map<String, ActionConfig> defaultNamespace = configuration.getRuntimeConfiguration().getActionConfigs().get("");
        defaultNamespace.put("actionName", config);

        ((DefaultActionMapper) container.getInstance(ActionMapper.class)).setAllowDynamicMethodCalls("true");
        
        ActionContext.getContext().setActionInvocation(invocation);
    }
}
