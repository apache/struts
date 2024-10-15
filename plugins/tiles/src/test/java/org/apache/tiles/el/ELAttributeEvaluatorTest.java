/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.el;

import com.sun.el.ExpressionFactoryImpl;
import junit.framework.TestCase;
import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.Expression;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.easymock.EasyMock;

import jakarta.el.ArrayELResolver;
import jakarta.el.BeanELResolver;
import jakarta.el.CompositeELResolver;
import jakarta.el.ELResolver;
import jakarta.el.ListELResolver;
import jakarta.el.MapELResolver;
import jakarta.el.ResourceBundleELResolver;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests {@link ELAttributeEvaluator}.
 */
public class ELAttributeEvaluatorTest extends TestCase {

    /**
     * The evaluator to test.
     */
    private ELAttributeEvaluator evaluator;

    /**
     * The request object to use.
     */
    private Request request;

    /** {@inheritDoc} */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        evaluator = new ELAttributeEvaluator();
        Map<String, Object> requestScope = new HashMap<>();
        Map<String, Object> sessionScope = new HashMap<>();
        Map<String, Object> applicationScope = new HashMap<>();
        requestScope.put("object1", "value");
        sessionScope.put("object2", 1);
        applicationScope.put("object3", 2.0F);
        requestScope.put("paulaBean", new PaulaBean());
        request = EasyMock.createMock(Request.class);
        EasyMock.expect(request.getContext("request")).andReturn(requestScope)
                .anyTimes();
        EasyMock.expect(request.getContext("session")).andReturn(sessionScope)
                .anyTimes();
        EasyMock.expect(request.getContext("application")).andReturn(
                applicationScope).anyTimes();
        EasyMock.expect(request.getAvailableScopes()).andReturn(
                Arrays.asList("request", "session", "application")).anyTimes();
        ApplicationContext applicationContext = EasyMock
                .createMock(ApplicationContext.class);
        EasyMock.expect(request.getApplicationContext()).andReturn(
                applicationContext).anyTimes();
        EasyMock.replay(request, applicationContext);

        evaluator.setExpressionFactory(new ExpressionFactoryImpl());
        ELResolver elResolver = new CompositeELResolver() {
            {
                BeanELResolver beanElResolver = new BeanELResolver(false);
                add(new ScopeELResolver());
                add(new TilesContextELResolver(beanElResolver));
                add(new TilesContextBeanELResolver());
                add(new ArrayELResolver(false));
                add(new ListELResolver(false));
                add(new MapELResolver(false));
                add(new ResourceBundleELResolver());
                add(beanElResolver);
            }
        };
        evaluator.setResolver(elResolver);
    }

    /**
     * Tests
     * {@link ELAttributeEvaluator#evaluate(Attribute, Request)}.
     */
    public void testEvaluate() {
        Attribute attribute = new Attribute();
        attribute.setExpressionObject(new Expression("${requestScope.object1}"));
        assertEquals("The value is not correct", "value", evaluator.evaluate(
                attribute, request));
        attribute.setExpressionObject(new Expression("${sessionScope.object2}"));
        assertEquals("The value is not correct", 1, evaluator
                .evaluate(attribute, request));
        attribute.setExpressionObject(new Expression("${applicationScope.object3}"));
        assertEquals("The value is not correct", 2.0F, evaluator
                .evaluate(attribute, request));
        attribute.setExpressionObject(new Expression("${object1}"));
        assertEquals("The value is not correct", "value", evaluator.evaluate(
                attribute, request));
        attribute.setExpressionObject(new Expression("${object2}"));
        assertEquals("The value is not correct", 1, evaluator
                .evaluate(attribute, request));
        attribute.setExpressionObject(new Expression("${object3}"));
        assertEquals("The value is not correct", 2.0F, evaluator
                .evaluate(attribute, request));
        attribute.setExpressionObject(new Expression("${paulaBean.paula}"));
        assertEquals("The value is not correct", "Brillant", evaluator
                .evaluate(attribute, request));
        attribute.setExpressionObject(new Expression("String literal"));
        assertEquals("The value is not correct", "String literal", evaluator
                .evaluate(attribute, request));
        attribute.setValue(2);
        assertEquals("The value is not correct", 2, evaluator
                .evaluate(attribute, request));
        attribute.setValue("${object1}");
        assertEquals("The value has been evaluated", "${object1}", evaluator
                .evaluate(attribute, request));
    }

    /**
     * Tests
     * {@link ELAttributeEvaluator#evaluate(String, Request)}.
     */
    public void testEvaluateString() {
        String expression = "${requestScope.object1}";
        assertEquals("The value is not correct", "value", evaluator.evaluate(
                expression, request));
        expression = "${sessionScope.object2}";
        assertEquals("The value is not correct", 1, evaluator
                .evaluate(expression, request));
        expression = "${applicationScope.object3}";
        assertEquals("The value is not correct", 2.0F, evaluator
                .evaluate(expression, request));
        expression = "${object1}";
        assertEquals("The value is not correct", "value", evaluator.evaluate(
                expression, request));
        expression = "${object2}";
        assertEquals("The value is not correct", 1, evaluator
                .evaluate(expression, request));
        expression = "${object3}";
        assertEquals("The value is not correct", 2.0F, evaluator
                .evaluate(expression, request));
        expression = "${paulaBean.paula}";
        assertEquals("The value is not correct", "Brillant", evaluator
                .evaluate(expression, request));
        expression = "String literal";
        assertEquals("The value is not correct", expression, evaluator
                .evaluate(expression, request));
    }

    /**
     * This is The Brillant Paula Bean (sic) just like it was posted to:
     * http://thedailywtf.com/Articles/The_Brillant_Paula_Bean.aspx
     * I hope that there is no copyright on it.
     */
    public static class PaulaBean {

        /**
         * Paula is brillant, really.
         */
        private final String paula = "Brillant";

        /**
         * Returns brillant.
         *
         * @return "Brillant".
         */
        public String getPaula() {
            return paula;
        }
    }
}
