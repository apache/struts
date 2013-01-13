package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.validator.GenericValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;

public class LongRangeFieldValidatorTest extends XWorkTestCase {

    public void testPassValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(100);
        ValidatorContext context = new GenericValidatorContext(action);
        LongRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 0);
    }

    public void testMinValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(98);
        ValidatorContext context = new GenericValidatorContext(action);
        LongRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 1);
        assertEquals("Max is 101, min is 99 but value is 98", context.getFieldErrors().get("longRange").get(0));
    }

    public void testMaxValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(102);
        ValidatorContext context = new GenericValidatorContext(action);
        LongRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 1);
        assertEquals("Max is 101, min is 99 but value is 102", context.getFieldErrors().get("longRange").get(0));
    }

    private ValidationAction prepareAction(long longRange) {
        ValidationAction action = new ValidationAction();
        action.setLongMaxValue(101L);
        action.setLongMinValue(99L);
        action.setLongRange(longRange);
        return action;
    }

    private LongRangeFieldValidator prepareValidator(ValidationAction action, ValidatorContext context) {
        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(action);

        LongRangeFieldValidator validator = new LongRangeFieldValidator();
        validator.setValueStack(valueStack);

        validator.setMaxExpression("${longMaxValue}");
        validator.setMinExpression("${longMinValue}");
        validator.setValidatorContext(context);
        validator.setFieldName("longRange");
        validator.setDefaultMessage("Max is ${longMaxValue}, min is ${longMinValue} but value is ${longRange}");

        return validator;
    }

}
