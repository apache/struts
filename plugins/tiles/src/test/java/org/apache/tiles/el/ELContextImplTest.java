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

import org.junit.Before;
import org.junit.Test;

import jakarta.el.ELResolver;
import jakarta.el.FunctionMapper;
import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link ELContextImpl}.
 */
public class ELContextImplTest {

    /**
     * The EL context to test.
     */
    private ELContextImpl context;

    /**
     * The EL resolver.
     */
    private ELResolver resolver;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        resolver = createMock(ELResolver.class);
        context = new ELContextImpl(resolver);
    }

    /**
     * Test method for {@link ELContextImpl#getELResolver()}.
     */
    @Test
    public void testGetELResolver() {
        replay(resolver);
        assertEquals(resolver, context.getELResolver());
        verify(resolver);
    }

    /**
     * Test method for {@link ELContextImpl#setFunctionMapper(FunctionMapper)}.
     */
    @Test
    public void testSetFunctionMapper() {
        FunctionMapper functionMapper = createMock(FunctionMapper.class);

        replay(resolver, functionMapper);
        context.setFunctionMapper(functionMapper);
        assertEquals(functionMapper, context.getFunctionMapper());
        verify(resolver, functionMapper);
    }

    /**
     * Test method for {@link ELContextImpl#setVariableMapper(VariableMapper)}.
     */
    @Test
    public void testSetVariableMapper() {
        VariableMapper variableMapper = createMock(VariableMapper.class);

        replay(resolver, variableMapper);
        context.setVariableMapper(variableMapper);
        assertEquals(variableMapper, context.getVariableMapper());
        verify(resolver, variableMapper);
    }

    /**
     * Tests {@link ELContextImpl#getFunctionMapper()}.
     */
    @Test
    public void testNullFunctionMapper() {
        replay(resolver);
        FunctionMapper functionMapper = context.getFunctionMapper();
        assertNull(functionMapper.resolveFunction("whatever", "it_IT"));
        verify(resolver);
    }

    /**
     * Tests {@link ELContextImpl#getVariableMapper()}.
     */
    @Test
    public void testVariableMapperImpl() {
        ValueExpression expression = createMock(ValueExpression.class);

        replay(resolver, expression);
        VariableMapper variableMapper = context.getVariableMapper();
        assertNull(variableMapper.resolveVariable("whatever"));
        variableMapper.setVariable("var", expression);
        assertEquals(expression, variableMapper.resolveVariable("var"));
        verify(resolver, expression);
    }
}
