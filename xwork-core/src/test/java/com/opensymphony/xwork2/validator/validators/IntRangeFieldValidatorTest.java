package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.validator.GenericValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;

public class IntRangeFieldValidatorTest extends XWorkTestCase {

    public void testPassValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(100);
        ValidatorContext context = new GenericValidatorContext(action);
        IntRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 0);
    }

    public void testMinValidation() throws Exception {
        // given
        ValidationAction action = prepareAction(98);
        ValidatorContext context = new GenericValidatorContext(action);
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
        ValidatorContext context = new GenericValidatorContext(action);
        IntRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 1);
        assertEquals("Max is 101, min is 99 but value is 102", context.getFieldErrors().get("intRange").get(0));
    }

    private ValidationAction prepareAction(int intRange) {
        ValidationAction action = new ValidationAction();
        action.setMaxValue(101);
        action.setMinValue(99);
        action.setIntRange(intRange);
        return action;
    }

    private IntRangeFieldValidator prepareValidator(ValidationAction action, ValidatorContext context) {
        IntRangeFieldValidator validator = new IntRangeFieldValidator();
        validator.setMax("${maxValue}");
        validator.setMin("${minValue}");
        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(action);
        validator.setValueStack(valueStack);
        validator.setValidatorContext(context);
        validator.setFieldName("intRange");
        validator.setParse(true);
        validator.setDefaultMessage("Max is ${maxValue}, min is ${minValue} but value is ${intRange}");
        return validator;
    }

}
