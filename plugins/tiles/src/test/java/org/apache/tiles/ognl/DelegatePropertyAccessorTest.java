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

package org.apache.tiles.ognl;

import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.PropertyAccessor;
import org.junit.Test;

import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link DelegatePropertyAccessor}.
 */
public class DelegatePropertyAccessorTest {

    /**
     * Test method for {@link DelegatePropertyAccessor#getProperty(Map, Object, Object)}.
     *
     * @throws OgnlException If something goes wrong.
     */
    @Test
    public void testGetProperty() throws OgnlException {
        PropertyAccessorDelegateFactory<Integer> factory = createMock(PropertyAccessorDelegateFactory.class);
        PropertyAccessor mockAccessor = createMock(PropertyAccessor.class);
        Map<String, Object> context = createMock(Map.class);
        expect(factory.getPropertyAccessor("property", 1)).andReturn(mockAccessor);
        expect(mockAccessor.getProperty(context, 1, "property")).andReturn("value");

        replay(factory, mockAccessor, context);
        PropertyAccessor accessor = new DelegatePropertyAccessor<>(factory);
        assertEquals("value", accessor.getProperty(context, 1, "property"));
        verify(factory, mockAccessor, context);
    }

    /**
     * Test method for {@link DelegatePropertyAccessor#setProperty(Map, Object, Object, Object)}.
     *
     * @throws OgnlException If something goes wrong.
     */
    @Test
    public void testSetProperty() throws OgnlException {
        PropertyAccessorDelegateFactory<Integer> factory = createMock(PropertyAccessorDelegateFactory.class);
        PropertyAccessor mockAccessor = createMock(PropertyAccessor.class);
        Map<String, Object> context = createMock(Map.class);
        expect(factory.getPropertyAccessor("property", 1)).andReturn(mockAccessor);
        mockAccessor.setProperty(context, 1, "property", "value");

        replay(factory, mockAccessor, context);
        PropertyAccessor accessor = new DelegatePropertyAccessor<>(factory);
        accessor.setProperty(context, 1, "property", "value");
        verify(factory, mockAccessor, context);
    }

    /**
     * Test method for {@link DelegatePropertyAccessor#getSourceAccessor(OgnlContext, Object, Object)}.
     */
    @Test
    public void testGetSourceAccessor() {
        PropertyAccessorDelegateFactory<Integer> factory = createMock(PropertyAccessorDelegateFactory.class);
        PropertyAccessor mockAccessor = createMock(PropertyAccessor.class);
        OgnlContext context = createMock(OgnlContext.class);
        expect(factory.getPropertyAccessor("property", 1)).andReturn(mockAccessor);
        expect(mockAccessor.getSourceAccessor(context, 1, "property")).andReturn("method");

        replay(factory, mockAccessor, context);
        PropertyAccessor accessor = new DelegatePropertyAccessor<>(factory);
        assertEquals("method", accessor.getSourceAccessor(context, 1, "property"));
        verify(factory, mockAccessor, context);
    }

    /**
     * Test method for {@link DelegatePropertyAccessor#getSourceSetter(OgnlContext, Object, Object)}.
     */
    @Test
    public void testGetSourceSetter() {
        PropertyAccessorDelegateFactory<Integer> factory = createMock(PropertyAccessorDelegateFactory.class);
        PropertyAccessor mockAccessor = createMock(PropertyAccessor.class);
        OgnlContext context = createMock(OgnlContext.class);
        expect(factory.getPropertyAccessor("property", 1)).andReturn(mockAccessor);
        expect(mockAccessor.getSourceSetter(context, 1, "property")).andReturn("method");

        replay(factory, mockAccessor, context);
        PropertyAccessor accessor = new DelegatePropertyAccessor<>(factory);
        assertEquals("method", accessor.getSourceSetter(context, 1, "property"));
        verify(factory, mockAccessor, context);
    }
}
