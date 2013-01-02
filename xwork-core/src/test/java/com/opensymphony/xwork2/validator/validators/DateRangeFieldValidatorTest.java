package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.validator.GenericValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;

import java.util.Calendar;
import java.util.Date;

public class DateRangeFieldValidatorTest extends XWorkTestCase {

    public void testPassValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(createDate(2013, 6, 6));
        ValidatorContext context = new GenericValidatorContext(action);
        DateRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 0);
    }

    public void testMinValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(createDate(2012, Calendar.MARCH, 3));
        ValidatorContext context = new GenericValidatorContext(action);
        DateRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 1);
        assertEquals("Max is 12/12/13, min is 1/1/13 but value is 3/3/12", context.getFieldErrors().get("dateRange").get(0));
    }

    public void testMaxValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(createDate(2014, Calendar.APRIL, 4));
        ValidatorContext context = new GenericValidatorContext(action);
        DateRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 1);
        assertEquals("Max is 12/12/13, min is 1/1/13 but value is 4/4/14", context.getFieldErrors().get("dateRange").get(0));
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
        DateRangeFieldValidator validator = new DateRangeFieldValidator();
        validator.setMaxExpression("${dateMaxValue}");
        validator.setMinExpression("${dateMinValue}");
        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(action);
        validator.setValueStack(valueStack);
        validator.setValidatorContext(context);
        validator.setFieldName("dateRange");
        validator.setParse(true);
        validator.setDefaultMessage("Max is ${dateMaxValue}, min is ${dateMinValue} but value is ${dateRange}");
        return validator;
    }

}
