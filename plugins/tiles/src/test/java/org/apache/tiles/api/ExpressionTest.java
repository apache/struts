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

package org.apache.tiles.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link Expression}.
 */
public class ExpressionTest {

    @Test
    public void testHashCode() {
        Expression expression = new Expression("hello", "there");
        assertEquals("hello".hashCode() + "there".hashCode(), expression.hashCode());
    }

    @Test
    public void testExpressionStringString() {
        Expression expression = new Expression("hello", "there");
        assertEquals("hello", expression.getExpression());
        assertEquals("there", expression.getLanguage());
    }

    @Test
    public void testExpressionString() {
        Expression expression = new Expression("hello");
        assertEquals("hello", expression.getExpression());
        assertNull(expression.getLanguage());
    }

    @Test
    public void testExpressionExpression() {
        Expression expression = new Expression("hello", "there");
        Expression expression2 = new Expression(expression);
        assertEquals("hello", expression2.getExpression());
        assertEquals("there", expression2.getLanguage());
    }

    @Test
    public void testCreateExpressionFromDescribedExpression() {
        Expression expression = Expression.createExpressionFromDescribedExpression("hello");
        assertEquals("hello", expression.getExpression());
        assertNull(expression.getLanguage());
        expression = Expression.createExpressionFromDescribedExpression("there:hello");
        assertEquals("hello", expression.getExpression());
        assertEquals("there", expression.getLanguage());
        expression = Expression.createExpressionFromDescribedExpression("there_:hello");
        assertEquals("there_:hello", expression.getExpression());
        assertNull(expression.getLanguage());
        assertNull(Expression.createExpressionFromDescribedExpression(null));
    }

    @Test
    public void testCreateExpression() {
        Expression expression = Expression.createExpression("hello", "there");
        assertEquals("hello", expression.getExpression());
        assertEquals("there", expression.getLanguage());
        expression = Expression.createExpression("hello", null);
        assertEquals("hello", expression.getExpression());
        assertNull(expression.getLanguage());
        expression = Expression.createExpression(null, "there");
        assertNull(expression);
    }

    @Test
    public void testEqualsObject() {
        Expression expression = new Expression("hello", "there");
        Expression expression2 = new Expression("hello", "there");
        assertEquals(expression, expression2);
        expression2 = new Expression("hello", "there2");
        assertNotEquals(expression, expression2);
        expression2 = new Expression("hello");
        assertNotEquals(expression, expression2);
        expression = new Expression("hello");
        assertEquals(expression, expression2);
    }

    @Test
    public void testToString() {
        Expression expression = new Expression("hello", "there");
        assertEquals("there:hello", expression.toString());
        expression = new Expression("hello");
        assertEquals("DEFAULT:hello", expression.toString());
    }

}
