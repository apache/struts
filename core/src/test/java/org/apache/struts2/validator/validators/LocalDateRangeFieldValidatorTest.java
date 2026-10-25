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
package org.apache.struts2.validator.validators;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.text.StubTextProvider;
import org.apache.struts2.text.TextProviderFactory;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import org.apache.struts2.validator.DummyValidatorContext;
import org.apache.struts2.validator.ValidatorContext;
import org.apache.struts2.validator.ValidatorFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class LocalDateRangeFieldValidatorTest extends XWorkTestCase {

    private static final LocalDate MIN = LocalDate.of(2026, 1, 1);
    private static final LocalDate MAX = LocalDate.of(2026, 12, 31);

    private TextProviderFactory tpf;

    public void testPassesInsideTheRange() {
        assertEquals(0, validate(LocalDate.of(2026, 6, 15)).getFieldErrors().size());
    }

    public void testPassesOnTheMinBound() {
        assertEquals(0, validate(MIN).getFieldErrors().size());
    }

    public void testPassesOnTheMaxBound() {
        assertEquals(0, validate(MAX).getFieldErrors().size());
    }

    public void testFailsBelowTheMinBound() {
        ValidatorContext context = validate(MIN.minusDays(1));

        assertEquals("Birthday must be within 2026-01-01 and 2026-12-31", context.getFieldErrors().get("birthday").get(0));
    }

    public void testFailsAboveTheMaxBound() {
        ValidatorContext context = validate(MAX.plusDays(1));

        assertEquals("Birthday must be within 2026-01-01 and 2026-12-31", context.getFieldErrors().get("birthday").get(0));
    }

    public void testSkipsANullValue() {
        assertEquals(0, validate(null).getFieldErrors().size());
    }

    public void testReadsTheBoundsFromExpressions() throws Exception {
        LocalDateAction action = new LocalDateAction(MIN.minusDays(1));
        ValidatorContext context = new DummyValidatorContext(action, tpf);
        LocalDateRangeFieldValidator validator = prepareValidator(action, context);
        validator.setMinExpression("${minValue}");
        validator.setMaxExpression("${maxValue}");

        validator.validate(action);

        assertEquals(1, context.getFieldErrors().get("birthday").size());
    }

    public void testIsRegisteredAsLocaldate() {
        String className = container.getInstance(ValidatorFactory.class).lookupRegisteredValidatorType("localdate");

        assertEquals(LocalDateRangeFieldValidator.class.getName(), className);
    }

    public void testConvertsIsoBoundsFromConfigurationParams() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("min", "2026-01-01");
        params.put("max", "2026-12-31");

        LocalDateRangeFieldValidator validator = (LocalDateRangeFieldValidator) container.getInstance(ObjectFactory.class)
                .buildValidator(LocalDateRangeFieldValidator.class.getName(), params, ActionContext.getContext().getContextMap());

        assertEquals(MIN, validator.getMin());
        assertEquals(MAX, validator.getMax());
    }

    public void testReadsConfigurationBoundsAsIsoWhateverTheGlobalDateFormat() throws Exception {
        ActionContext.getContext().getValueStack()
                .push(new StubTextProvider(Map.of(org.apache.struts2.components.Date.DATETAG_PROPERTY, "yyyy-dd-MM")));
        Map<String, Object> context = ActionContext.getContext().getContextMap();
        Map<String, Object> params = new HashMap<>();
        params.put("min", "2026-01-02");
        params.put("max", "2026-03-04");

        LocalDateRangeFieldValidator validator = (LocalDateRangeFieldValidator) container.getInstance(ObjectFactory.class)
                .buildValidator(LocalDateRangeFieldValidator.class.getName(), params, context);

        assertEquals(LocalDate.of(2026, 1, 2), validator.getMin());
        assertEquals(LocalDate.of(2026, 3, 4), validator.getMax());
    }

    private ValidatorContext validate(LocalDate birthday) {
        LocalDateAction action = new LocalDateAction(birthday);
        ValidatorContext context = new DummyValidatorContext(action, tpf);
        LocalDateRangeFieldValidator validator = prepareValidator(action, context);
        validator.setMin(MIN);
        validator.setMax(MAX);

        try {
            validator.validate(action);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return context;
    }

    private LocalDateRangeFieldValidator prepareValidator(LocalDateAction action, ValidatorContext context) {
        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(action);

        LocalDateRangeFieldValidator validator = new LocalDateRangeFieldValidator();
        validator.setValueStack(valueStack);
        validator.setValidatorContext(context);
        validator.setFieldName("birthday");
        validator.setDefaultMessage("Birthday must be within ${min} and ${max}");
        return validator;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        tpf = container.getInstance(TextProviderFactory.class);
    }

    public static class LocalDateAction {

        private final LocalDate birthday;

        public LocalDateAction(LocalDate birthday) {
            this.birthday = birthday;
        }

        public LocalDate getBirthday() {
            return birthday;
        }

        public LocalDate getMinValue() {
            return MIN;
        }

        public LocalDate getMaxValue() {
            return MAX;
        }
    }

}
