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

package org.apache.tiles.core.evaluator;

import org.apache.tiles.api.Attribute;
import org.apache.tiles.api.Expression;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertSame;

/**
 * Tests {@link BasicAttributeEvaluatorFactory}.
 */
public class BasicAttributeEvaluatorFactoryTest {

    /**
     * Test method for {@link BasicAttributeEvaluatorFactory#getAttributeEvaluator(String)}.
     */
    @Test
    public void testGetAttributeEvaluatorString() {
        AttributeEvaluator defaultEvaluator = createMock(AttributeEvaluator.class);
        AttributeEvaluator evaluator1 = createMock(AttributeEvaluator.class);
        AttributeEvaluator evaluator2 = createMock(AttributeEvaluator.class);
        replay(defaultEvaluator, evaluator1, evaluator2);
        BasicAttributeEvaluatorFactory factory = new BasicAttributeEvaluatorFactory(defaultEvaluator);
        factory.registerAttributeEvaluator("LANG1", evaluator1);
        factory.registerAttributeEvaluator("LANG2", evaluator2);
        assertSame(evaluator1, factory.getAttributeEvaluator("LANG1"));
        assertSame(evaluator2, factory.getAttributeEvaluator("LANG2"));
        assertSame(defaultEvaluator, factory.getAttributeEvaluator("LANG3"));
        verify(defaultEvaluator, evaluator1, evaluator2);
    }

    /**
     * Test method for {@link BasicAttributeEvaluatorFactory#getAttributeEvaluator(Attribute)}.
     */
    @Test
    public void testGetAttributeEvaluatorAttribute() {
        AttributeEvaluator defaultEvaluator = createMock(AttributeEvaluator.class);
        AttributeEvaluator evaluator1 = createMock(AttributeEvaluator.class);
        AttributeEvaluator evaluator2 = createMock(AttributeEvaluator.class);
        replay(defaultEvaluator, evaluator1, evaluator2);
        BasicAttributeEvaluatorFactory factory = new BasicAttributeEvaluatorFactory(defaultEvaluator);
        factory.registerAttributeEvaluator("LANG1", evaluator1);
        factory.registerAttributeEvaluator("LANG2", evaluator2);
        assertSame(evaluator1, factory
            .getAttributeEvaluator(createExpressionAttribute("LANG1")));
        assertSame(evaluator2, factory
            .getAttributeEvaluator(createExpressionAttribute("LANG2")));
        assertSame(defaultEvaluator, factory
            .getAttributeEvaluator(createExpressionAttribute("LANG3")));
        verify(defaultEvaluator, evaluator1, evaluator2);
    }

    /**
     * Creates a sample attribute with an expression.
     *
     * @param language The expression language.
     * @return The attribute.
     */
    private Attribute createExpressionAttribute(String language) {
        return new Attribute(null, Expression.createExpression("myExpression", language), null, "string");
    }

}
