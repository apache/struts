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
package org.apache.struts2.validator;

import org.apache.struts2.FileManagerFactory;
import org.apache.struts2.SimpleAction;
import org.apache.struts2.TestBean;
import org.apache.struts2.ValidationOrderAction;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.interceptor.ValidationAware;
import org.apache.struts2.test.DataAware2;
import org.apache.struts2.test.SimpleAction3;
import org.apache.struts2.test.User;
import org.apache.struts2.validator.validators.DateRangeFieldValidator;
import org.apache.struts2.validator.validators.DoubleRangeFieldValidator;
import org.apache.struts2.validator.validators.ExpressionValidator;
import org.apache.struts2.validator.validators.IntRangeFieldValidator;
import org.apache.struts2.validator.validators.LongRangeFieldValidator;
import org.apache.struts2.validator.validators.RequiredFieldValidator;
import org.apache.struts2.validator.validators.RequiredStringValidator;
import org.apache.struts2.validator.validators.ShortRangeFieldValidator;
import org.apache.struts2.StrutsException;
import org.assertj.core.api.Assertions;
import org.xml.sax.SAXParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DefaultActionValidatorManagerTest extends XWorkTestCase {

    protected final String alias = "validationAlias";

    DefaultActionValidatorManager actionValidatorManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        actionValidatorManager = container.inject(DefaultActionValidatorManager.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        actionValidatorManager = null;
    }

    public void testBuildValidatorKey() {
        String validatorKey = actionValidatorManager.buildValidatorKey(SimpleAction.class, alias);
        assertEquals(SimpleAction.class.getName() + "/" + alias, validatorKey);
    }

    public void testBuildsValidatorsForAlias() {
        List<Validator> validators = actionValidatorManager.getValidators(SimpleAction.class, alias);

        Assertions.assertThat(validators).hasSize(11).map(Validator::getClass).containsExactly(
                ExpressionValidator.class,
                RequiredFieldValidator.class,
                IntRangeFieldValidator.class,
                DoubleRangeFieldValidator.class,
                DateRangeFieldValidator.class,
                IntRangeFieldValidator.class,
                IntRangeFieldValidator.class,
                LongRangeFieldValidator.class,
                ShortRangeFieldValidator.class,
                RequiredFieldValidator.class,
                IntRangeFieldValidator.class
        );
        Assertions.assertThat(validators).hasSize(11).map(Validator::getDefaultMessage).containsExactly(
                "Foo must be greater than Bar. Foo = ${foo}, Bar = ${bar}.",
                "You must enter a value for bar.",
                "bar must be between ${min} and ${max}, current value is ${bar}.",
                "percentage must be between ${minExclusive} and ${maxExclusive}, current value is ${percentage}.",
                "The date must be between 12-22-2002 and 12-25-2002.",
                "Could not find foo.range!",
                "Could not find baz.range!",
                "Could not find foo.range!",
                "Could not find foo.range!",
                "You must enter a value for baz.",
                "baz out of range."
        );
    }

    public void testBuildsValidatorsForAliasError() {
        assertThatThrownBy(() -> actionValidatorManager.getValidators(TestBean.class, "badtest"))
                .isInstanceOf(StrutsException.class)
                .hasCause(new SAXParseException("Attribute \"foo\" must be declared for element type \"field-validator\".", null));
    }


    public void testGetValidatorsForInterface() {
        List<Validator> validators = actionValidatorManager.getValidators(DataAware2.class, alias);

        Assertions.assertThat(validators).hasSize(3).map(Validator::getClass).containsExactly(
                RequiredFieldValidator.class,
                RequiredStringValidator.class,
                RequiredStringValidator.class
        );
        Assertions.assertThat(validators).hasSize(3).map(Validator::getValidatorType).containsExactly(
                "required",
                "requiredstring",
                "requiredstring"
        );
        Assertions.assertThat(validators).hasSize(3).map(Validator::getDefaultMessage).containsExactly(
                "You must enter a value for data.",
                "You must enter a value for data.",
                "You must enter a value for data."
        );
    }

    public void testGetValidatorsFromInterface() {
        List<Validator> validators = actionValidatorManager.getValidators(SimpleAction3.class, alias);

        Assertions.assertThat(validators).hasSize(13).map(Validator::getClass).containsExactly(
                ExpressionValidator.class,
                RequiredFieldValidator.class,
                IntRangeFieldValidator.class,
                DoubleRangeFieldValidator.class,
                DateRangeFieldValidator.class,
                IntRangeFieldValidator.class,
                IntRangeFieldValidator.class,
                LongRangeFieldValidator.class,
                ShortRangeFieldValidator.class,
                RequiredFieldValidator.class,
                IntRangeFieldValidator.class,
                RequiredFieldValidator.class,
                RequiredStringValidator.class
        );
        Assertions.assertThat(validators).hasSize(13).map(Validator::getValidatorType).containsExactly(
                "expression",
                "required",
                "int",
                "double",
                "date",
                "int",
                "int",
                "long",
                "short",
                "required",
                "int",
                "required",
                "requiredstring"
        );
        Assertions.assertThat(validators).hasSize(13).map(Validator::getDefaultMessage).containsExactly(
                "Foo must be greater than Bar. Foo = ${foo}, Bar = ${bar}.",
                "You must enter a value for bar.",
                "bar must be between ${min} and ${max}, current value is ${bar}.",
                "percentage must be between ${minExclusive} and ${maxExclusive}, current value is ${percentage}.",
                "The date must be between 12-22-2002 and 12-25-2002.",
                "Could not find foo.range!",
                "Could not find baz.range!",
                "Could not find foo.range!",
                "Could not find foo.range!",
                "You must enter a value for baz.",
                "baz out of range.",
                "You must enter a value for data.",
                "You must enter a value for data."
        );
    }

    /**
     * Test to verify WW-3850.
     */
    public void testBuildsValidatorsForClassError() {
        // for this test we need to have a file manager with reloadingConfigs to true
        container.getInstance(FileManagerFactory.class).getFileManager().setReloadingConfigs(true);
        // no validator found, but no check on file since it is not in cache
        actionValidatorManager.getValidators(List.class, null);
        // this second call will try reload a not existing file
        // and causes a NPE (see WW-3850)
        try {
            actionValidatorManager.getValidators(List.class, null);
        } catch (Exception e) {
            fail("Exception occurred " + e);
        }
    }

    public void testSkipUserMarkerActionLevelShortCircuit() {
        List<Validator> validatorList = actionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        try {
            User user = new User();
            user.setName("Mark");
            user.setEmail("bad_email");
            user.setEmail2("bad_email");

            ValidationAware validationAware = new SimpleAction();
            ValidatorContext context = new DelegatingValidatorContext(validationAware, actionValidatorManager.textProviderFactory);
            actionValidatorManager.validate(user, null, context);
            assertTrue(context.hasFieldErrors());

            // check field errors
            List<String> l = context.getFieldErrors().get("email");
            assertNotNull(l);
            assertEquals(1, l.size());
            assertEquals("Not a valid e-mail.", l.get(0));
            l = context.getFieldErrors().get("email2");
            assertNotNull(l);
            assertEquals(2, l.size());
            assertEquals("Not a valid e-mail2.", l.get(0));
            assertEquals("Email2 not from the right company.", l.get(1));

            // check action errors
            assertTrue(context.hasActionErrors());
            l = new ArrayList<>(context.getActionErrors());
            assertNotNull(l);
            assertEquals(2, l.size()); // both expression test failed see User-validation.xml
            assertEquals("Email does not start with mark", l.get(0));
        } catch (ValidationException ex) {
            fail("Validation error: " + ex.getMessage());
        }
    }

    public void testSkipAllActionLevelShortCircuit2() {
        List<Validator> validatorList = actionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        try {
            User user = new User();
            user.setName("Mark");
            // * mark both email to starts with mark to get pass the action-level validator,
            // so we could concentrate on testing the field-level validators (User-validation.xml)
            // * make both email the same to pass the action-level validator at
            // UserMarker-validation.xml
            user.setEmail("mark_bad_email_for_field_val@foo.com");
            user.setEmail2("mark_bad_email_for_field_val@foo.com");

            ValidationAware validationAware = new SimpleAction();
            ValidatorContext context = new DelegatingValidatorContext(validationAware, actionValidatorManager.textProviderFactory);
            actionValidatorManager.validate(user, null, context);
            assertTrue(context.hasFieldErrors());

            // check field errors
            // we have an error in this field level, email does not ends with mycompany.com
            List<String> l = context.getFieldErrors().get("email");
            assertNotNull(l);
            assertEquals(1, l.size()); // because email-field-val is short-circuit
            assertEquals("Email not from the right company.", l.get(0));

            // check action errors
            l = new ArrayList<>(context.getActionErrors());
            assertFalse(context.hasActionErrors());
            assertEquals(0, l.size());
        } catch (ValidationException ex) {
            fail("Validation error: " + ex.getMessage());
        }
    }


    public void testActionLevelShortCircuit() throws Exception {
        List<Validator> validatorList = actionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        User user = new User();
        // all fields will trigger error, but sc of action-level, cause it to not appear
        user.setName(null);
        user.setEmail("tmjee(at)yahoo.co.uk");
        user.setEmail("tm_jee(at)yahoo.co.uk");

        ValidationAware validationAware = new SimpleAction();
        ValidatorContext context = new DelegatingValidatorContext(validationAware, actionValidatorManager.textProviderFactory);
        actionValidatorManager.validate(user, null, context);

        // check field level errors
        // shouldn't have any because action error prevents validation of anything else
        List<String> l = context.getFieldErrors().get("email2");
        assertNull(l);

        // check action errors
        assertTrue(context.hasActionErrors());
        l = new ArrayList<>(context.getActionErrors());
        assertNotNull(l);
        // we only get one, because UserMarker-validation.xml action-level validator
        // already sc it   :-)
        assertEquals(1, l.size());
        assertEquals("Email not the same as email2", l.get(0));
    }

    public void testShortCircuitNoErrors() {
        List<Validator> validatorList = actionValidatorManager.getValidators(User.class, null);
        assertEquals(10, validatorList.size());

        try {
            User user = new User();
            user.setName("Mark");
            user.setEmail("mark@mycompany.com");
            user.setEmail2("mark@mycompany.com");

            ValidationAware validationAware = new SimpleAction();
            ValidatorContext context = new DelegatingValidatorContext(validationAware, actionValidatorManager.textProviderFactory);
            actionValidatorManager.validate(user, null, context);
            assertFalse(context.hasErrors());
        } catch (ValidationException ex) {
            fail("Validation error: " + ex.getMessage());
        }
    }

    public void testFieldErrorsOrder() throws Exception {
        ValidationOrderAction action = new ValidationOrderAction();
        actionValidatorManager.validate(action, "actionContext");
        Map<String, List<String>> fieldErrors = action.getFieldErrors();
        Iterator<Map.Entry<String, List<String>>> i = fieldErrors.entrySet().iterator();

        assertNotNull(fieldErrors);
        assertEquals(fieldErrors.size(), 12);


        Map.Entry<String, List<String>> e = i.next();
        assertEquals(e.getKey(), "username");
        assertEquals(e.getValue().get(0), "username required");

        e = i.next();
        assertEquals(e.getKey(), "password");
        assertEquals((e.getValue()).get(0), "password required");

        e = i.next();
        assertEquals(e.getKey(), "confirmPassword");
        assertEquals((e.getValue()).get(0), "confirm password required");

        e = i.next();
        assertEquals(e.getKey(), "firstName");
        assertEquals((e.getValue()).get(0), "first name required");

        e = i.next();
        assertEquals(e.getKey(), "lastName");
        assertEquals((e.getValue()).get(0), "last name required");

        e = i.next();
        assertEquals(e.getKey(), "city");
        assertEquals((e.getValue()).get(0), "city is required");

        e = i.next();
        assertEquals(e.getKey(), "province");
        assertEquals((e.getValue()).get(0), "province is required");

        e = i.next();
        assertEquals(e.getKey(), "country");
        assertEquals((e.getValue()).get(0), "country is required");

        e = i.next();
        assertEquals(e.getKey(), "postalCode");
        assertEquals((e.getValue()).get(0), "postal code is required");

        e = i.next();
        assertEquals(e.getKey(), "email");
        assertEquals((e.getValue()).get(0), "email is required");

        e = i.next();
        assertEquals(e.getKey(), "website");
        assertEquals((e.getValue()).get(0), "website is required");

        e = i.next();
        assertEquals(e.getKey(), "passwordHint");
        assertEquals((e.getValue()).get(0), "password hint is required");
    }

}
