package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.GenericValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;

public class RequiredStringValidatorTest extends XWorkTestCase {

    public void testRequiredStringPass() throws Exception {
        // given
        ValueStack valueStack = ActionContext.getContext().getValueStack();

        ValidationAction action = new ValidationAction();
        action.setStringValue("a string");
        valueStack.push(action);

        ValidatorContext context = new GenericValidatorContext(action);
        RequiredStringValidator validator = new RequiredStringValidator();
        validator.setValidatorContext(context);
        validator.setFieldName("stringValue");
        validator.setValueStack(valueStack);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 0);
    }

    public void testRequiredStringFails() throws Exception {
        // given
        ValueStack valueStack = ActionContext.getContext().getValueStack();

        ValidationAction action = new ValidationAction();
        valueStack.push(action);

        ValidatorContext context = new GenericValidatorContext(action);
        RequiredStringValidator validator = new RequiredStringValidator();
        validator.setValidatorContext(context);
        validator.setFieldName("stringValue");
        validator.setValueStack(valueStack);
        validator.setDefaultMessage("Field ${fieldName} is required");

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 1);
        assertEquals(context.getFieldErrors().get("stringValue").get(0), "Field stringValue is required");
    }


    public void testTrimAsExpression() throws Exception {
        // given
        ValueStack valueStack = ActionContext.getContext().getValueStack();

        ActionSupport action = new ActionSupport() {
            public boolean getTrimValue() {
                return false;
            }
        };
        valueStack.push(action);

        RequiredStringValidator validator = new RequiredStringValidator();
        validator.setValueStack(valueStack);

        assertTrue(validator.isTrim());

        // when
        validator.setTrimExpression("${trimValue}");

        // then
        assertFalse(validator.isTrim());
    }

}
