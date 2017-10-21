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
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.validator.DummyValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;

public class IntRangeFieldValidatorTest extends XWorkTestCase {

    private TextProviderFactory tpf;

    public void setUp() throws Exception {
        super.setUp();
        tpf = container.getInstance(TextProviderFactory.class);
    }

    public void testPassValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(100);
        ValidatorContext context = new DummyValidatorContext(action, tpf);
        IntRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 0);
    }

    public void testMinValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(98);
        ValidatorContext context = new DummyValidatorContext(action, tpf);
        IntRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 1);
        assertEquals("Max is 101, min is 99 but value is 98", context.getFieldErrors().get("intRange").get(0));
    }

    public void testMaxValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(102);
        ValidatorContext context = new DummyValidatorContext(action, tpf);
        IntRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 1);
        assertEquals("Max is 101, min is 99 but value is 102", context.getFieldErrors().get("intRange").get(0));
    }

    public void testArrayOfIntValidation() throws Exception {
        // given
        ValidationAction action = new ValidationAction();
        action.setInts(new Integer[] {99, 100, 101, 102});

        ValidatorContext context = new DummyValidatorContext(action, tpf);
        IntRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.setMin(100);
        validator.setMax(101);
        validator.setFieldName("ints");
        validator.setDefaultMessage("Max is ${max}, min is ${min} but value is ${currentValue}");
        validator.validate(action);

        // then
        assertEquals(1, context.getFieldErrors().size());
        assertEquals(2, context.getFieldErrors().get("ints").size());
        assertEquals("Max is 101, min is 100 but value is 99", context.getFieldErrors().get("ints").get(0));
        assertEquals("Max is 101, min is 100 but value is 102", context.getFieldErrors().get("ints").get(1));
    }

    private ValidationAction prepareAction(int intRange) {
        ValidationAction action = new ValidationAction();
        action.setIntMaxValue(101);
        action.setIntMinValue(99);
        action.setIntRange(intRange);
        action.setInts(new Integer[] {101, 99, 100, 102});
        return action;
    }

    private IntRangeFieldValidator prepareValidator(ValidationAction action, ValidatorContext context) {
        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(action);

        IntRangeFieldValidator validator = new IntRangeFieldValidator();
        validator.setValueStack(valueStack);

        validator.setMaxExpression("${intMaxValue}");
        validator.setMinExpression("${intMinValue}");
        validator.setValidatorContext(context);
        validator.setFieldName("intRange");
        validator.setDefaultMessage("Max is ${intMaxValue}, min is ${intMinValue} but value is ${intRange}");

        return validator;
    }

}
