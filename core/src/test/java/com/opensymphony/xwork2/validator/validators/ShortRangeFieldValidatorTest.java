package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.validator.DummyValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;

public class ShortRangeFieldValidatorTest extends XWorkTestCase {

    private TextProviderFactory tpf;

    public void setUp() throws Exception {
        super.setUp();
        tpf = container.getInstance(TextProviderFactory.class);
    }

    public void testPassValidation() throws Exception {
        // given
        ValidationAction action = prepareAction((short) 5);
        ValidatorContext context = new DummyValidatorContext(action, tpf);
        ShortRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 0);
    }

    public void testMinValidation() throws Exception {
        // given
        ValidationAction action = prepareAction((short) 1);
        ValidatorContext context = new DummyValidatorContext(action, tpf);
        ShortRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 1);
        assertEquals("Max is 10, min is 2 but value is 1", context.getFieldErrors().get("shortRange").get(0));
    }

    public void testMaxValidation() throws Exception {
        // given
        ValidationAction action = prepareAction((short) 11);
        ValidatorContext context = new DummyValidatorContext(action, tpf);
        ShortRangeFieldValidator validator = prepareValidator(action, context);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 1);
        assertEquals("Max is 10, min is 2 but value is 11", context.getFieldErrors().get("shortRange").get(0));
    }

    private ValidationAction prepareAction(short range) {
        ValidationAction action = new ValidationAction();
        action.setShortMaxValue((short) 10);
        action.setShortMinValue((short) 2);
        action.setShortRange(range);
        return action;
    }

    private ShortRangeFieldValidator prepareValidator(ValidationAction action, ValidatorContext context) {
        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        valueStack.push(action);

        ShortRangeFieldValidator validator = new ShortRangeFieldValidator();
        validator.setValueStack(valueStack);

        validator.setMaxExpression("${shortMaxValue}");
        validator.setMinExpression("${shortMinValue}");
        validator.setValidatorContext(context);
        validator.setFieldName("shortRange");
        validator.setDefaultMessage("Max is ${shortMaxValue}, min is ${shortMinValue} but value is ${shortRange}");

        return validator;
    }

}
