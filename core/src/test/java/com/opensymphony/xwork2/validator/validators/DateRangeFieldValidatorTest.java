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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.validator.DummyValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateRangeFieldValidatorTest extends XWorkTestCase {

    private TextProviderFactory tpf;

    public void testPassValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(createDate(2013, 6, 6));
        ValidatorContext context = new DummyValidatorContext(action, tpf);
        DateRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 0);
    }

    public void testMinValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(createDate(2012, Calendar.MARCH, 3));
        ValidatorContext context = new DummyValidatorContext(action, tpf);
        DateRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 1);
        assertEquals("Max is 12.12.13, min is 01.01.13 but value is 03.03.12", context.getFieldErrors().get("dateRange").get(0));
    }

    public void testMaxValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(createDate(2014, Calendar.APRIL, 4));
        ValidatorContext context = new DummyValidatorContext(action, tpf);
        DateRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 1);
        assertEquals("Max is 12.12.13, min is 01.01.13 but value is 04.04.14", context.getFieldErrors().get("dateRange").get(0));
    }

    private ValidationAction prepareAction(Date range) {
        ValidationAction action = new ValidationAction();
        action.setDateMinValue(createDate(2013, Calendar.JANUARY, 1));
        action.setDateMaxValue(createDate(2013, Calendar.DECEMBER, 12));
        action.setDateRange(range);
        return action;
    }

    private Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        return cal.getTime();
    }

    private DateRangeFieldValidator prepareValidator(ValidationAction action, ValidatorContext context) {
        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(action);

        DateRangeFieldValidator validator = new DateRangeFieldValidator();
        validator.setValueStack(valueStack);

        validator.setMaxExpression("${dateMaxValue}");
        validator.setMinExpression("${dateMinValue}");
        validator.setValidatorContext(context);
        validator.setFieldName("dateRange");
        validator.setDefaultMessage("Max is ${dateMaxValue}, min is ${dateMinValue} but value is ${dateRange}");

        return validator;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ActionContext.getContext().setLocale(new Locale("DE"));
        tpf = container.getInstance(TextProviderFactory.class);
    }

}
