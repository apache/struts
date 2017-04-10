package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.validator.DummyValidatorContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.junit.Test;

import java.util.ArrayList;

public class RequiredFieldValidatorTest extends StrutsInternalTestCase {

    @Test
    public void testNullObject() throws Exception {
        // given
        RequiredFieldValidator rfv = container.inject(RequiredFieldValidator.class);
        rfv.setValueStack(ActionContext.getContext().getValueStack());
        rfv.setFieldName("stringValue");
        rfv.setDefaultMessage("${fieldName} field is required!");
        ValidationAction action = new ValidationAction();
        DummyValidatorContext context = new DummyValidatorContext(action, container.getInstance(TextProviderFactory.class));
        rfv.setValidatorContext(context);

        // when
        rfv.validate(action);

        // then
        assertTrue(context.hasFieldErrors());
        assertEquals(1, context.getFieldErrors().size());
        assertNotNull(context.getFieldErrors().get("stringValue"));
        assertEquals("stringValue field is required!", context.getFieldErrors().get("stringValue").get(0));
    }

    @Test
    public void testArrayObject() throws Exception {
        // given
        RequiredFieldValidator rfv = container.inject(RequiredFieldValidator.class);
        rfv.setValueStack(ActionContext.getContext().getValueStack());
        rfv.setFieldName("ints");
        rfv.setDefaultMessage("${fieldName} field is required!");
        ValidationAction action = new ValidationAction();
        action.setInts(new Integer[]{});
        DummyValidatorContext context = new DummyValidatorContext(action, container.getInstance(TextProviderFactory.class));
        rfv.setValidatorContext(context);

        // when
        rfv.validate(action);

        // then
        assertTrue(context.hasFieldErrors());
        assertEquals(1, context.getFieldErrors().size());
        assertNotNull(context.getFieldErrors().get("ints"));
        assertEquals("ints field is required!", context.getFieldErrors().get("ints").get(0));
    }
    
    @Test
    public void testCollectionObject() throws Exception {
        // given
        RequiredFieldValidator rfv = container.inject(RequiredFieldValidator.class);
        rfv.setValueStack(ActionContext.getContext().getValueStack());
        rfv.setFieldName("shorts");
        rfv.setDefaultMessage("${fieldName} field is required!");
        ValidationAction action = new ValidationAction();
        action.setShorts(new ArrayList<Short>());
        DummyValidatorContext context = new DummyValidatorContext(action, container.getInstance(TextProviderFactory.class));
        rfv.setValidatorContext(context);

        // when
        rfv.validate(action);

        // then
        assertTrue(context.hasFieldErrors());
        assertEquals(1, context.getFieldErrors().size());
        assertNotNull(context.getFieldErrors().get("shorts"));
        assertEquals("shorts field is required!", context.getFieldErrors().get("shorts").get(0));
    }

}