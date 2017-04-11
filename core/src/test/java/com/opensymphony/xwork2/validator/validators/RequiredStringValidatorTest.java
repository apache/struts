package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.DummyValidatorContext;
import com.opensymphony.xwork2.validator.ValidatorContext;

import java.util.Arrays;

public class RequiredStringValidatorTest extends XWorkTestCase {

    private TextProviderFactory tpf;

    public void setUp() throws Exception {
        super.setUp();
        tpf = container.getInstance(TextProviderFactory.class);
    }

    public void testRequiredStringPass() throws Exception {
        // given
        ValueStack valueStack = ActionContext.getContext().getValueStack();

        ValidationAction action = new ValidationAction();
        action.setStringValue("a string");
        valueStack.push(action);

        ValidatorContext context = new DummyValidatorContext(action, tpf);
        RequiredStringValidator validator = new RequiredStringValidator();
        validator.setValidatorContext(context);
        validator.setFieldName("stringValue");
        validator.setValueStack(valueStack);

        // when
        validator.validate(action);

        // then
        assertTrue(context.getFieldErrors().size() == 0);
    }

    public void testRequiredArrayOfStringsPass() throws Exception {
        // given
        ValueStack valueStack = ActionContext.getContext().getValueStack();

        ValidationAction action = new ValidationAction();
        action.setStrings(new String[]{"", "12334", null});
        valueStack.push(action);

        ValidatorContext context = new DummyValidatorContext(action, tpf);
        RequiredStringValidator validator = new RequiredStringValidator();
        validator.setValidatorContext(context);
        validator.setFieldName("strings");
        validator.setValueStack(valueStack);

        // when
        validator.validate(action);

        // then
        assertTrue(context.hasFieldErrors());
        assertEquals(1, context.getFieldErrors().size());
        assertEquals(2, context.getFieldErrors().get("strings").size());
    }

    public void testRequiredCollectionOfStringsPass() throws Exception {
        // given
        ValueStack valueStack = ActionContext.getContext().getValueStack();

        ValidationAction action = new ValidationAction();
        action.setStringCollection(Arrays.asList("", "123456", null));
        valueStack.push(action);

        ValidatorContext context = new DummyValidatorContext(action, tpf);
        RequiredStringValidator validator = new RequiredStringValidator();
        validator.setValidatorContext(context);
        validator.setFieldName("stringCollection");
        validator.setValueStack(valueStack);

        // when
        validator.validate(action);

        // then
        assertTrue(context.hasFieldErrors());
        assertEquals(1, context.getFieldErrors().size());
        assertEquals(2, context.getFieldErrors().get("stringCollection").size());
    }

    public void testRequiredStringFails() throws Exception {
        // given
        ValueStack valueStack = ActionContext.getContext().getValueStack();

        ValidationAction action = new ValidationAction();
        valueStack.push(action);

        ValidatorContext context = new DummyValidatorContext(action, tpf);
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
