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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.ValidationAwareSupport;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.validators.DoubleRangeFieldValidator;
import org.apache.struts2.dispatcher.HttpParameters;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit test for {@link DoubleRangeFieldValidator}.
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @author Claus Ibsen
 * @version $Id$
 */
public class DoubleRangeFieldValidatorTest extends XWorkTestCase {

    private DoubleRangeFieldValidator val;
    private TextProviderFactory tpf;

    public void testRangeValidationWithError() throws Exception {
        //Explicitly set an out-of-range double for DoubleRangeValidatorTest
        Map<String, Object> context = new HashMap<>();
        HashMap<String, Object> params = new HashMap<>();
        params.put("percentage", 100.12);
        context.put(ActionContext.PARAMETERS, HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.VALIDATION_ACTION_NAME, null, context);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

        Map<String, List<String>> errors = ((ValidationAware) proxy.getAction()).getFieldErrors();

        List<String> errorMessages = errors.get("percentage");
        assertNotNull("Expected double range validation error message.", errorMessages);
        assertEquals(1, errorMessages.size());

        String errorMessage = errorMessages.get(0);
        assertNotNull("Expecting: percentage must be between 0.1 and 10.1, current value is 100.12.", errorMessage);
        assertEquals("percentage must be between 0.1 and 10.1, current value is 100.12.", errorMessage);
    }

    public void testRangeValidationNoError() throws Exception {
        Map<String, Object> context = new HashMap<>();
        HashMap<String, Object> params = new HashMap<>();
        params.put("percentage", 1.234567d);
        context.put(ActionContext.PARAMETERS, HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "percentage", null, context);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

        Map<String, List<String>> errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
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

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport(), tpf);
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

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport(), tpf);
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

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport(), tpf);
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

        DelegatingValidatorContext context = new DelegatingValidatorContext(new ValidationAwareSupport(), tpf);
        val.setValidatorContext(context);

        val.setMinInclusive(9.95d);
        val.validate(null);
        assertFalse(context.hasErrors()); // should pass as null value passed in
    }

    public void testRangeValidationWithExpressionsFail() throws Exception {
        //Explicitly set an out-of-range double for DoubleRangeValidatorTest
        Map<String, Object> context = new HashMap<>();
        HashMap<String, Object> params = new HashMap<>();
        params.put("percentage", 100.12);
        context.put(ActionContext.PARAMETERS, HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.EXPRESSION_VALIDATION_ACTION, null, context);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasFieldErrors());

        Map<String, List<String>> errors = ((ValidationAware) proxy.getAction()).getFieldErrors();
        List<String> errorMessages = errors.get("percentage");
        assertNotNull("Expected double range validation error message.", errorMessages);
        assertEquals(1, errorMessages.size());

        String errorMessage = errorMessages.get(0);
        assertNotNull("Expecting: percentage must be between 0.1 and 10.1, current value is 100.12.", errorMessage);
        assertEquals("percentage must be between 0.1 and 10.1, current value is 100.12.", errorMessage);
    }

    public void testExpressionParams() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        ActionSupport action = new ActionSupport() {

            public Double getMinInclusiveValue() {
                return 10d;
            }

            public Double getMaxInclusiveValue() {
                return 11d;
            }

            public Double getMinExclusiveValue() {
                return 13d;
            }

            public Double getMaxExclusiveValue() {
                return 14d;
            }

            public Double getPrice() {
                return 15d;
            }
        };

        stack.push(action);

        val.setMinInclusiveExpression("${minInclusiveValue}");
        val.setMaxInclusiveExpression("${maxInclusiveValue}");
        val.setMinExclusiveExpression("${minExclusiveValue}");
        val.setMaxExclusiveExpression("${maxExclusiveValue}");

        val.setFieldName("price");
        val.setDefaultMessage("Price is wrong!");

        DelegatingValidatorContext context = new DelegatingValidatorContext(action, tpf);
        val.setValidatorContext(context);

        val.validate(action);
        assertTrue(action.getFieldErrors().get("price").size() == 1);
    }

    public void testArrayOfDoubles() throws Exception {
        val.setMinInclusive(10d);
        val.setMaxInclusive(14d);

        val.setFieldName("doubleArray");
        val.setDefaultMessage("Value ${currentValue} not in scope!");

        MyTestProduct object = new MyTestProduct();
        object.setDoubleArray(new Double[]{11d, 15d});

        DummyValidatorContext context = new DummyValidatorContext(object, tpf);
        val.setValidatorContext(context);

        val.validate(object);

        assertTrue(context.hasFieldErrors());
        assertEquals(1, context.getFieldErrors().size());
        assertEquals(1, context.getFieldErrors().get("doubleArray").size());
        assertEquals("Value 15 not in scope!", context.getFieldErrors().get("doubleArray").get(0));
    }

    public void testCollectionOfDoubles() throws Exception {
        val.setMinInclusive(10d);
        val.setMaxInclusive(14d);

        val.setFieldName("doubleCollection");
        val.setDefaultMessage("Value ${currentValue} not in scope!");

        MyTestProduct object = new MyTestProduct();
        object.setDoubleCollection(Arrays.asList(11d, 15d));

        DummyValidatorContext context = new DummyValidatorContext(object, tpf);
        val.setValidatorContext(context);

        val.validate(object);

        assertTrue(context.hasFieldErrors());
        assertEquals(1, context.getFieldErrors().size());
        assertEquals(1, context.getFieldErrors().get("doubleCollection").size());
        assertEquals("Value 15 not in scope!", context.getFieldErrors().get("doubleCollection").get(0));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        XmlConfigurationProvider provider = new XmlConfigurationProvider("xwork-default.xml");
        container.inject(provider);
        loadConfigurationProviders(provider, new MockConfigurationProvider());
        val = new DoubleRangeFieldValidator();
        val.setValueStack(ActionContext.getContext().getValueStack());
        ActionContext.getContext().setParameters(HttpParameters.create().build());
        tpf = container.getInstance(TextProviderFactory.class);
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

        private Double[] doubleArray;
        private Collection<Double> doubleCollection;

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

        public Double[] getDoubleArray() {
            return doubleArray;
        }

        public void setDoubleArray(Double[] doubleArray) {
            this.doubleArray = doubleArray;
        }

        public Collection<Double> getDoubleCollection() {
            return doubleCollection;
        }

        public void setDoubleCollection(Collection<Double> doubleCollection) {
            this.doubleCollection = doubleCollection;
        }
    }

}
