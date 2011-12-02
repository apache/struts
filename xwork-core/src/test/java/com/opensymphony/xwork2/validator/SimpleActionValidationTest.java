/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.validators.ValidatorSupport;

import java.util.*;


/**
 * SimpleActionValidationTest
 * <p/>
 * Created : Jan 20, 2003 11:04:25 PM
 *
 * @author Jason Carreira
 */
public class SimpleActionValidationTest extends XWorkTestCase {

    private Locale origLocale;


    public void testAliasValidation() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("baz", "10");

        //valid values
        params.put("bar", "7");
        params.put("date", "12/23/2002");
        params.put("percentage", "1.23456789");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
            proxy.execute();

            ValidationAware validationAware = (ValidationAware) proxy.getAction();
            assertFalse(validationAware.hasFieldErrors());

            // put in an out-of-range value to see if the old validators still work
            ActionContext.setContext(new ActionContext(new HashMap<String, Object>()));
            params.put("bar", "42");
            proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_ALIAS_NAME, extraContext);
            proxy.execute();
            validationAware = (ValidationAware) proxy.getAction();
            assertTrue(validationAware.hasFieldErrors());

            Map<String, List<String>> errors = validationAware.getFieldErrors();
            assertTrue(errors.containsKey("baz"));

            List<String> bazErrors = errors.get("baz");
            assertEquals(1, bazErrors.size());

            String message = bazErrors.get(0);
            assertEquals("baz out of range.", message);
            assertTrue(errors.containsKey("bar"));

            List<String> barErrors = errors.get("bar");
            assertEquals(1, barErrors.size());
            message = barErrors.get(0);
            assertEquals("bar must be between 6 and 10, current value is 42.", message);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testLookingUpFieldNameAsTextKey() {
        HashMap<String, Object> params = new HashMap<String, Object>();

        // should cause a message
        params.put("baz", "-1");

        //valid values
        params.put("bar", "7");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
            proxy.execute();
            assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

            Map<String, List<String>> errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
            List<String> bazErrors = errors.get("baz");
            assertEquals(1, bazErrors.size());

            String errorMessage = bazErrors.get(0);
            assertNotNull(errorMessage);
            assertEquals("Baz Field must be greater than 0", errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testMessageKey() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("foo", "200");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
            ValueStack stack = ActionContext.getContext().getValueStack();
            ActionContext.setContext(new ActionContext(stack.getContext()));
            ActionContext.getContext().setLocale(Locale.US);
            proxy.execute();
            assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

            Map<String, List<String>> errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
            List<String> fooErrors = errors.get("foo");
            assertEquals(1, fooErrors.size());

            String errorMessage = fooErrors.get(0);
            assertNotNull(errorMessage);
            assertEquals("Foo Range Message", errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testMessageKeyIsReturnedIfNoOtherDefault() throws ValidationException {
        Validator validator = new ValidatorSupport() {
            public void validate(Object object) throws ValidationException {
                addActionError(object);
            }
        };
        validator.setValueStack(ActionContext.getContext().getValueStack());

        String messageKey = "does.not.exist";
        validator.setMessageKey(messageKey);

        ValidatorContext validatorContext = new DelegatingValidatorContext(new SimpleAction());
        validator.setValidatorContext(validatorContext);
        validator.validate(this);
        assertTrue(validatorContext.hasActionErrors());

        Collection<String> errors = validatorContext.getActionErrors();
        assertEquals(1, errors.size());
        assertEquals(messageKey, errors.toArray()[0]);
    }

    public void testParamterizedMessage() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("bar", "42");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, extraContext);
            proxy.execute();
            assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

            Map<String, List<String>> errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
            List<String> barErrors = errors.get("bar");
            assertEquals(1, barErrors.size());

            String errorMessage = barErrors.get(0);
            assertNotNull(errorMessage);
            assertEquals("bar must be between 6 and 10, current value is 42.", errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testSubPropertiesAreValidated() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("baz", "10");

        //valid values
        params.put("foo", "8");
        params.put("bar", "7");
        params.put("date", "12/23/2002");

        params.put("bean.name", "Name should be valid");

        // this should cause a message
        params.put("bean.count", "100");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_SUBPROPERTY_NAME, extraContext);
            proxy.execute();
            assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

            Map<String, List<String>> errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
            List<String> beanCountErrors = errors.get("bean.count");
            assertEquals(1, beanCountErrors.size());

            String errorMessage = beanCountErrors.get(0);
            assertNotNull(errorMessage);
            assertEquals("bean.count out of range.", errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Override
    protected void setUp() throws Exception {
        origLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        loadConfigurationProviders(new XmlConfigurationProvider("xwork-test-beans.xml"), new MockConfigurationProvider());
    }

    @Override
    protected void tearDown() throws Exception {
        Locale.setDefault(origLocale);
    }
}
