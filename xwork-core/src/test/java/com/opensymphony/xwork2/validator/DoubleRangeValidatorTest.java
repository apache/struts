package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.validators.DoubleRangeFieldValidator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Unit test for {@link DoubleRangeFieldValidator}.
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @author Claus Ibsen
 * @version $Id$
 */
public class DoubleRangeValidatorTest extends XWorkTestCase {
    private DoubleRangeFieldValidator val;

    public void testRangeValidationWithError() throws Exception {
        //Explicitly set an out-of-range double for DoubleRangeValidatorTest
        Map<String, Object> context = new HashMap<String, Object>();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("percentage", 100.0123d);
        context.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, context);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

        Map<String, List<String>> errors = ((ValidationAware) proxy.getAction()).getFieldErrors();

        List<String> errorMessages = errors.get("percentage");
        assertNotNull("Expected double range validation error message.", errorMessages);
        assertEquals(1, errorMessages.size());

        String errorMessage = errorMessages.get(0);
        assertNotNull("Expecting: percentage must be between 0.1 and 10.1, current value is 100.0123.", errorMessage);
        assertEquals("percentage must be between 0.1 and 10.1, current value is 100.0123.", errorMessage);
    }

    public void testRangeValidationNoError() throws Exception {
        Map<String, Object> context = new HashMap<String, Object>();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("percentage", 1.234567d);
        context.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "percentage", context);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

        Map<String, List<String>> errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
        Iterator it = errors.entrySet().iterator();

        List<String> errorMessages = errors.get("percentage");
        assertNull("Expected no double range validation error message.", errorMessages);
    }

    public void testRangeNoExclusiveAndNoValueInStack() throws Exception {
        val.setFieldName("hello");
        val.validate("world");
    }

    public void testRangeSimpleDoubleValueInStack() throws Exception {
        MyTestProduct prod = new MyTestProduct();
        prod.setName("coca cola");
        prod.setPrice(5.99);

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(prod);
        ActionContext.getContext().setValueStack(stack);

        val.setMinInclusive(0d);
        val.setMaxInclusive(10d);
        val.setFieldName("price");
        val.validate(prod);
    }

    public void testRangeRealDoubleValueInStack() throws Exception {
        MyTestProduct prod = new MyTestProduct();
        prod.setName("coca cola");
        prod.setPrice(5.99);
        prod.setVolume(12.34d);

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(prod);
        ActionContext.getContext().setValueStack(stack);

        val.setMinInclusive(0d);
        val.setMaxInclusive(30d);
        val.setFieldName("volume");
        val.validate(prod);
    }

    public void testRangeNotADoubleObjectValueInStack() throws Exception {
        MyTestProduct prod = new MyTestProduct();
        prod.setName("coca cola");

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(prod);
        ActionContext.getContext().setValueStack(stack);

        val.setMinInclusive(0d);
        val.setMaxInclusive(10d);
        val.setFieldName("name");

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport());
        val.setValidatorContext(context);

        val.validate(prod);

        assertEquals(0d, val.getMinInclusive());
        assertEquals(10d, val.getMaxInclusive());
    }

    public void testEdgeOfMaxRange() throws Exception {
        MyTestProduct prod = new MyTestProduct();
        prod.setName("coca cola");
        prod.setPrice(9.95);

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(prod);
        ActionContext.getContext().setValueStack(stack);

        val.setFieldName("price");

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport());
        val.setValidatorContext(context);

        val.setMaxInclusive(9.95d);
        val.validate(prod); // should pass
        assertTrue(!context.hasErrors());
        assertEquals(9.95d, val.getMaxInclusive());

        val.setMaxExclusive(9.95d);
        val.validate(prod); // should not pass
        assertTrue(context.hasErrors());
        assertEquals(9.95d, val.getMaxExclusive());
    }

    public void testEdgeOfMinRange() throws Exception {
        MyTestProduct prod = new MyTestProduct();
        prod.setName("coca cola");
        prod.setPrice(9.95);

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(prod);
        ActionContext.getContext().setValueStack(stack);

        val.setFieldName("price");

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport());
        val.setValidatorContext(context);

        val.setMinInclusive(9.95d);
        val.validate(prod); // should pass
        assertTrue(!context.hasErrors());

        val.setMinExclusive(9.95d);
        val.validate(prod); // should not pass
        assertTrue(context.hasErrors());
    }

    public void testNoValue() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        ActionContext.getContext().setValueStack(stack);

        val.setFieldName("price");

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport());
        val.setValidatorContext(context);

        val.setMinInclusive(9.95d);
        val.validate(null);
        assertTrue(!context.hasErrors()); // should pass as null value passed in
    }

    public void testRangeValidationWithExpressionsFail() throws Exception {
        //Explicitly set an out-of-range double for DoubleRangeValidatorTest
        Map<String, Object> context = new HashMap<String, Object>();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("percentage", 100.0123d);
        context.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.EXPRESSION_VALIDATION_ACTION, context);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

        Map<String, List<String>> errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
        List<String> errorMessages = errors.get("percentage");
        assertNotNull("Expected double range validation error message.", errorMessages);
        assertEquals(1, errorMessages.size());

        String errorMessage = errorMessages.get(0);
        assertNotNull("Expecting: percentage must be between 0.1 and 10.1, current value is 100.0123.", errorMessage);
        assertEquals("percentage must be between 0.1 and 10.1, current value is 100.0123.", errorMessage);
    }

    public void testExpressionParams() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        ActionSupport action = new ActionSupport() {

            public Double getMinInclusiveValue() {return 10d;}
            public Double getMaxInclusiveValue() {return 11d;}
            public Double getMinExclusiveValue() {return 13d;}
            public Double getMaxExclusiveValue() {return 14d;}
            public Double getPrice() {return 15d;}
        };

        stack.push(action);

        val.setMinInclusiveExpression("${minInclusiveValue}");
        val.setMaxInclusiveExpression("${maxInclusiveValue}");
        val.setMinExclusiveExpression("${minExclusiveValue}");
        val.setMaxExclusiveExpression("${maxExclusiveValue}");

        val.setFieldName("price");
        val.setDefaultMessage("Price is wrong!");

        DelegatingValidatorContext context = new DelegatingValidatorContext(action);
        val.setValidatorContext(context);

        val.validate(action);
        assertTrue(action.getFieldErrors().get("price").size() == 1);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        XmlConfigurationProvider provider = new XmlConfigurationProvider("xwork-default.xml");
        container.inject(provider);
        loadConfigurationProviders(provider,  new MockConfigurationProvider());
        val = new DoubleRangeFieldValidator();
        val.setValueStack(ActionContext.getContext().getValueStack());
        ActionContext.getContext().setParameters(new HashMap<String, Object>());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        val = null;
    }

    private class MyTestProduct {
        private double price;
        private Double volume;
        private String name;

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getVolume() {
            return volume;
        }

        public void setVolume(Double volume) {
            this.volume = volume;
        }
    }

}
