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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.ValidationAwareSupport;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.conversion.impl.ConversionData;
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

        Map<String, ConversionData> conversionErrors = new HashMap<>();
        conversionErrors.put("foo", new ConversionData("bar", Integer.class));
        ActionContext.of(stack.getContext())
            .withConversionErrors(conversionErrors)
            .bind();

        validator = new ConversionErrorFieldValidator();
        validationAware = new ValidationAwareSupport();

        DelegatingValidatorContext validatorContext = new DelegatingValidatorContext(validationAware, container.getInstance(TextProviderFactory.class));
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


        Map<String, List<String>> fieldErrors = validationAware.getFieldErrors();
        assertTrue(fieldErrors.containsKey("foo"));
        assertEquals(message, fieldErrors.get("foo").get(0));
    }

    public void testConversionErrorsAreAddedToFieldErrors() throws ValidationException {
        validator.validate(validationAware);

        Map<String, List<String>> fieldErrors = validationAware.getFieldErrors();
        assertTrue(fieldErrors.containsKey("foo"));
        assertEquals(defaultFooMessage, fieldErrors.get("foo").get(0));
    }

}
