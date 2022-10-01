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

import org.apache.tiles.request.Request;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link ScopePropertyAccessor}.
 */
public class ScopePropertyAccessorTest {

    /**
     * The accessor to test.
     */
    private ScopePropertyAccessor accessor;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        accessor = new ScopePropertyAccessor();
    }

    /**
     * Test method for {@link ScopePropertyAccessor#getProperty(Map, Object, Object)}.
     */
    @Test
    public void testGetProperty() {
        Request request = createMock(Request.class);
        Map<String, Object> oneScope = createMock(Map.class);

        expect(request.getContext("one")).andReturn(oneScope);

        replay(request);
        assertEquals(oneScope, accessor.getProperty(null, request, "oneScope"));
        assertNull(accessor.getProperty(null, request, "whatever"));
        verify(request);
    }

    @Test
    public void testGetSourceAccessor() {
        Request request = createMock(Request.class);

        replay(request);
        assertEquals(".getContext(\"one\")", accessor.getSourceAccessor(null, request, "oneScope"));
        assertNull(accessor.getSourceAccessor(null, request, "whatever"));
        verify(request);
    }

    @Test
    public void testGetSourceSetter() {
        assertNull(accessor.getSourceSetter(null, null, "whatever"));
    }

    /**
     * Test method for {@link ScopePropertyAccessor#setProperty(Map, Object, Object, Object)}.
     */
    @Test
    public void testSetProperty() {
        accessor.setProperty(null, null, "whatever", "whateverValue");
    }

}
