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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.ValidationAwareSupport;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.validators.ConversionErrorFieldValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ConversionErrorFieldValidatorTest
 *
 * @author Jason Carreira
 *         Date: Nov 28, 2003 3:45:37 PM
 */
public class ConversionErrorFieldValidatorTest extends XWorkTestCase {

    private static final String defaultFooMessage = "Invalid field value for field \"foo\".";


    private ConversionErrorFieldValidator validator;
    private ValidationAware validationAware;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        ValueStack stack = ActionContext.getContext().getValueStack();
        ActionContext context = new ActionContext(stack.getContext());

        Map<String, Object> conversionErrors = new HashMap<String, Object>();
        conversionErrors.put("foo", "bar");
        context.setConversionErrors(conversionErrors);
        validator = new ConversionErrorFieldValidator();
        validationAware = new ValidationAwareSupport();

        DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(validationAware);
        stack.push(validatorContext);
        validator.setValidatorContext(validatorContext);
        validator.setFieldName("foo");
        validator.setValueStack(ActionContext.getContext().getValueStack());
        assertEquals(0, validationAware.getFieldErrors().size());
    }

    public void testConversionErrorMessageUsesProvidedMessage() throws ValidationException {
        String message = "default message";
        validator.setDefaultMessage(message);
        validator.validate(validationAware);


        Map fieldErrors = validationAware.getFieldErrors();
        assertTrue(fieldErrors.containsKey("foo"));
        assertEquals(message, ((List) fieldErrors.get("foo")).get(0));
    }

    public void testConversionErrorsAreAddedToFieldErrors() throws ValidationException {
        validator.validate(validationAware);

        Map fieldErrors = validationAware.getFieldErrors();
        assertTrue(fieldErrors.containsKey("foo"));
        assertEquals(defaultFooMessage, ((List) fieldErrors.get("foo")).get(0));
    }

}
