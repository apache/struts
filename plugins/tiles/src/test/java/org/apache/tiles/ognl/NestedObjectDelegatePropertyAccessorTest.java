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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link NestedObjectDelegatePropertyAccessor}.
 */
public class NestedObjectDelegatePropertyAccessorTest {

    /**
     * Test method for {@link NestedObjectDelegatePropertyAccessor#getProperty(Map, Object, Object)}.
     * @throws OgnlException If something goes wrong.
     */
    @Test
    public void testGetProperty() throws OgnlException {
        NestedObjectExtractor<Integer> nestedObjectExtractor = createMock(NestedObjectExtractor.class);
        PropertyAccessor propertyAccessor = createMock(PropertyAccessor.class);
        Map<String, Object> context = createMock(Map.class);
        expect(propertyAccessor.getProperty(context, "nested", "property")).andReturn("value");
        expect(nestedObjectExtractor.getNestedObject(1)).andReturn("nested");

        replay(nestedObjectExtractor, propertyAccessor, context);
        PropertyAccessor accessor = new NestedObjectDelegatePropertyAccessor<>(nestedObjectExtractor, propertyAccessor);
        assertEquals("value", accessor.getProperty(context, 1, "property"));
        verify(nestedObjectExtractor, propertyAccessor, context);
    }

    /**
     * Test method for {@link NestedObjectDelegatePropertyAccessor#setProperty(Map, Object, Object, Object)}.
     * @throws OgnlException If something goes wrong.
     */
    @Test
    public void testSetProperty() throws OgnlException {
        NestedObjectExtractor<Integer> nestedObjectExtractor = createMock(NestedObjectExtractor.class);
        PropertyAccessor propertyAccessor = createMock(PropertyAccessor.class);
        Map<String, Object> context = createMock(Map.class);
        propertyAccessor.setProperty(context, "nested", "property", "value");
        expect(nestedObjectExtractor.getNestedObject(1)).andReturn("nested");

        replay(nestedObjectExtractor, propertyAccessor, context);
        PropertyAccessor accessor = new NestedObjectDelegatePropertyAccessor<>(nestedObjectExtractor, propertyAccessor);
        accessor.setProperty(context, 1, "property", "value");
        verify(nestedObjectExtractor, propertyAccessor, context);
    }

    /**
     * Test method for {@link NestedObjectDelegatePropertyAccessor#getSourceAccessor(OgnlContext, Object, Object)}.
     */
    @Test
    public void testGetSourceAccessor() {
        NestedObjectExtractor<Integer> nestedObjectExtractor = createMock(NestedObjectExtractor.class);
        PropertyAccessor propertyAccessor = createMock(PropertyAccessor.class);
        OgnlContext context = createMock(OgnlContext.class);
        expect(propertyAccessor.getSourceAccessor(context, "nested", "property")).andReturn("method");
        expect(nestedObjectExtractor.getNestedObject(1)).andReturn("nested");

        replay(nestedObjectExtractor, propertyAccessor, context);
        PropertyAccessor accessor = new NestedObjectDelegatePropertyAccessor<>(nestedObjectExtractor, propertyAccessor);
        assertEquals("method", accessor.getSourceAccessor(context, 1, "property"));
        verify(nestedObjectExtractor, propertyAccessor, context);
    }

    /**
     * Test method for {@link NestedObjectDelegatePropertyAccessor#getSourceSetter(OgnlContext, Object, Object)}.
     */
    @Test
    public void testGetSourceSetter() {
        NestedObjectExtractor<Integer> nestedObjectExtractor = createMock(NestedObjectExtractor.class);
        PropertyAccessor propertyAccessor = createMock(PropertyAccessor.class);
        OgnlContext context = createMock(OgnlContext.class);
        expect(propertyAccessor.getSourceSetter(context, "nested", "property")).andReturn("method");
        expect(nestedObjectExtractor.getNestedObject(1)).andReturn("nested");

        replay(nestedObjectExtractor, propertyAccessor, context);
        PropertyAccessor accessor = new NestedObjectDelegatePropertyAccessor<>(nestedObjectExtractor, propertyAccessor);
        assertEquals("method", accessor.getSourceSetter(context, 1, "property"));
        verify(nestedObjectExtractor, propertyAccessor, context);
    }

}
