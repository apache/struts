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
package com.opensymphony.xwork2.validator;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.validator.validators.ExpressionValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;

/**
 * Unit test for ExpressionValidator.
 *
 * @author Jason Carreira
 * @author Claus Ibsen
 */
public class ExpressionValidatorTest extends XWorkTestCase {

    public void testExpressionValidationOfStringLength() throws ValidationException {
        TestBean bean = new TestBean();
        bean.setName("abc");
        ActionContext.getContext().getValueStack().push(bean);

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport());
        container.getInstance(ActionValidatorManager.class).validate(bean, "expressionValidation", context);
        assertTrue(context.hasFieldErrors());

        final Map fieldErrors = context.getFieldErrors();
        assertTrue(fieldErrors.containsKey("name"));

        List nameErrors = (List) fieldErrors.get("name");
        assertEquals(1, nameErrors.size());
        assertEquals("Name must be greater than 5 characters, it is currently 'abc'", nameErrors.get(0));

        bean.setName("abcdefg");
        context = new DelegatingValidatorContext(new ValidationAwareSupport());
        container.getInstance(ActionValidatorManager.class).validate(bean, "expressionValidation", context);
        assertFalse(context.hasFieldErrors());
    }

    public void testExpressionValidatorFailure() throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("date", "12/23/2002");
        params.put("foo", "5");
        params.put("bar", "7");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasActionErrors());

        Collection errors = ((ValidationAware) proxy.getAction()).getActionErrors();
        assertEquals(1, errors.size());

        String message = (String) errors.iterator().next();
        assertNotNull(message);
        assertEquals("Foo must be greater than Bar. Foo = 5, Bar = 7.", message);
    }

    public void testExpressionValidatorSuccess() throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();

        //make it not fail
        params.put("date", "12/23/2002");
        params.put("foo", "10");
        params.put("bar", "7");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
        proxy.execute();
        assertFalse(((ValidationAware) proxy.getAction()).hasActionErrors());
    }

    public void testGetSetExpresion() {
        ExpressionValidator ev = new ExpressionValidator();
        ev.setExpression("{top}");
        assertEquals("{top}", ev.getExpression());
    }

    public void testNoBooleanExpression() throws Exception {
        Mock mock = new Mock(ValidationAware.class);
        mock.expect("addActionError", C.ANY_ARGS);

        ExpressionValidator ev = new ExpressionValidator();
        ev.setValidatorContext(new DelegatingValidatorContext(mock.proxy()));
        ev.setExpression("{top}");
        ev.setValueStack(ActionContext.getContext().getValueStack());
        ev.validate("Hello"); // {top} will evalute to Hello that is not a Boolean
        mock.verify();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        loadConfigurationProviders(new MockConfigurationProvider());

        ActionConfig config = new ActionConfig.Builder("", "name", "").build();
        ActionInvocation invocation = EasyMock.createNiceMock(ActionInvocation.class);
        ActionProxy proxy = EasyMock.createNiceMock(ActionProxy.class);

        EasyMock.expect(invocation.getProxy()).andReturn(proxy).anyTimes();
        EasyMock.expect(invocation.getAction()).andReturn(null).anyTimes();
        EasyMock.expect(invocation.invoke()).andReturn(Action.SUCCESS).anyTimes();
        EasyMock.expect(proxy.getMethod()).andReturn("execute").anyTimes();
        EasyMock.expect(proxy.getConfig()).andReturn(config).anyTimes();


        EasyMock.replay(invocation);
        EasyMock.replay(proxy);

        ActionContext.getContext().setActionInvocation(invocation);
    }

}
