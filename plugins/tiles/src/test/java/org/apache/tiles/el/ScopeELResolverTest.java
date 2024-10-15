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

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.junit.Before;
import org.junit.Test;

import jakarta.el.ELContext;
import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link ScopeELResolver}.
 */
public class ScopeELResolverTest {

    /**
     * The resolver to test.
     */
    private ScopeELResolver resolver;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() {
        resolver = new ScopeELResolver();
    }

    /**
     * Tests {@link ScopeELResolver#getCommonPropertyType(ELContext, Object)}.
     */
    @Test
    public void testGetCommonPropertyType() {
        ELContext elContext = createMock(ELContext.class);

        replay(elContext);
        assertNull(resolver.getCommonPropertyType(elContext, 1));
        assertEquals(Map.class, resolver.getCommonPropertyType(elContext, null));
        verify(elContext);
    }

    /**
     * Tests {@link ScopeELResolver#getFeatureDescriptors(ELContext, Object)}.
     */
    @Test
    public void testGetFeatureDescriptors() {
        ELContext elContext = createMock(ELContext.class);
        Request request = createMock(Request.class);

        expect(elContext.getContext(Request.class)).andReturn(request);
        expect(request.getAvailableScopes()).andReturn(Arrays.asList("one", "two"));

        replay(elContext, request);
        assertFalse(resolver.getFeatureDescriptors(elContext, 1).hasNext());
        Iterator<FeatureDescriptor> descriptors = resolver.getFeatureDescriptors(elContext, null);
        FeatureDescriptor descriptor = descriptors.next();
        assertEquals("oneScope", descriptor.getName());
        descriptor = descriptors.next();
        assertEquals("twoScope", descriptor.getName());
        assertFalse(descriptors.hasNext());
        verify(elContext, request);
    }

    /**
     * Test method for
     * {@link ScopeELResolver#getType(ELContext, Object, Object)}.
     */
    @Test
    public void testGetType() {
        Request request = createMock(Request.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        ELContext context = new ELContextImpl(resolver);
        replay(request, applicationContext);
        context.putContext(Request.class, request);
        context.putContext(ApplicationContext.class, applicationContext);
        assertNull(resolver.getType(context, 1, "whatever"));
        assertEquals("The requestScope object is not a map.", Map.class,
            resolver.getType(context, null, "requestScope"));
        assertEquals("The sessionScope object is not a map.", Map.class,
            resolver.getType(context, null, "sessionScope"));
        assertEquals("The applicationScope object is not a map.", Map.class,
            resolver.getType(context, null, "applicationScope"));
    }

    /**
     * Test method for
     * {@link ScopeELResolver#getValue(ELContext, Object, Object)}.
     */
    @Test
    public void testGetValue() {
        Map<String, Object> requestScope = new HashMap<>();
        requestScope.put("objectKey", "objectValue");
        Map<String, Object> sessionScope = new HashMap<>();
        sessionScope.put("sessionObjectKey", "sessionObjectValue");
        Map<String, Object> applicationScope = new HashMap<>();
        applicationScope.put("applicationObjectKey", "applicationObjectValue");
        Request request = createMock(Request.class);
        expect(request.getContext("request")).andReturn(requestScope);
        expect(request.getContext("session")).andReturn(sessionScope);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        expect(request.getContext("application")).andReturn(applicationScope);
        ELContext context = new ELContextImpl(resolver);
        replay(request, applicationContext);
        context.putContext(Request.class, request);
        context.putContext(ApplicationContext.class, applicationContext);
        assertNull(resolver.getValue(context, 1, "whatever"));
        assertEquals("The requestScope map does not correspond", requestScope,
            resolver.getValue(context, null, "requestScope"));
        assertEquals("The sessionScope map does not correspond", sessionScope,
            resolver.getValue(context, null, "sessionScope"));
        assertEquals("The applicationScope map does not correspond",
            applicationScope, resolver.getValue(context, null,
                "applicationScope"));
    }

    /**
     * Tests {@link ScopeELResolver#isReadOnly(ELContext, Object, Object)}.
     */
    @Test
    public void testIsReadOnly() {
        ELContext elContext = createMock(ELContext.class);

        replay(elContext);
        assertTrue(resolver.isReadOnly(elContext, null, "whatever"));
        verify(elContext);
    }

    /**
     * Tests {@link ScopeELResolver#isReadOnly(ELContext, Object, Object)}.
     */
    @Test(expected = NullPointerException.class)
    public void testIsReadOnlyNPE() {
        resolver.isReadOnly(null, null, "whatever");
    }

    /**
     * Tests {@link ScopeELResolver#setValue(ELContext, Object, Object, Object)}.
     */
    @Test
    public void testSetValue() {
        // Just to complete code coverage!
        resolver.setValue(null, null, null, null);
    }
}
