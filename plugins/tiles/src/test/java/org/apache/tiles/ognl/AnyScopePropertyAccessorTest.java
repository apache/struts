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

import java.util.Arrays;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link AnyScopePropertyAccessor}.
 */
public class AnyScopePropertyAccessorTest {

    /**
     * The accessor to test.
     */
    private AnyScopePropertyAccessor accessor;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        accessor = new AnyScopePropertyAccessor();
    }

    /**
     * Test method for {@link AnyScopePropertyAccessor#getProperty(Map, Object, Object)}.
     */
    @Test
    public void testGetProperty() {
        Request request = createMock(Request.class);
        Map<String, Object> oneScope = createMock(Map.class);
        Map<String, Object> twoScope = createMock(Map.class);

        expect(request.getAvailableScopes()).andReturn(Arrays.asList("one", "two")).anyTimes();
        expect(request.getContext("one")).andReturn(oneScope).anyTimes();
        expect(request.getContext("two")).andReturn(twoScope).anyTimes();
        expect(oneScope.containsKey("name1")).andReturn(true);
        expect(oneScope.get("name1")).andReturn("value1");
        expect(oneScope.containsKey("name2")).andReturn(false);
        expect(oneScope.containsKey("name3")).andReturn(false);
        expect(twoScope.containsKey("name2")).andReturn(true);
        expect(twoScope.get("name2")).andReturn("value2");
        expect(twoScope.containsKey("name3")).andReturn(false);

        replay(request, oneScope, twoScope);
        assertEquals("value1", accessor.getProperty(null, request, "name1"));
        assertEquals("value2", accessor.getProperty(null, request, "name2"));
        assertNull(accessor.getProperty(null, request, "name3"));
        verify(request, oneScope, twoScope);
    }

    @Test
    public void testGetSourceAccessor() {
        Request request = createMock(Request.class);
        Map<String, Object> oneScope = createMock(Map.class);
        Map<String, Object> twoScope = createMock(Map.class);

        expect(request.getAvailableScopes()).andReturn(Arrays.asList("one", "two")).anyTimes();
        expect(request.getContext("one")).andReturn(oneScope).anyTimes();
        expect(request.getContext("two")).andReturn(twoScope).anyTimes();
        expect(oneScope.containsKey("name1")).andReturn(true);
        expect(oneScope.containsKey("name2")).andReturn(false);
        expect(oneScope.containsKey("name3")).andReturn(false);
        expect(twoScope.containsKey("name2")).andReturn(true);
        expect(twoScope.containsKey("name3")).andReturn(false);

        replay(request, oneScope, twoScope);
        assertEquals(".getContext(\"one\").get(index)", accessor.getSourceAccessor(null, request, "name1"));
        assertEquals(".getContext(\"two\").get(index)", accessor.getSourceAccessor(null, request, "name2"));
        assertNull(accessor.getSourceAccessor(null, request, "name3"));
        verify(request, oneScope, twoScope);
    }

    @Test
    public void testGetSourceSetter() {
        Request request = createMock(Request.class);
        Map<String, Object> oneScope = createMock(Map.class);
        Map<String, Object> twoScope = createMock(Map.class);

        expect(request.getAvailableScopes()).andReturn(Arrays.asList("one", "two")).anyTimes();
        expect(request.getContext("one")).andReturn(oneScope).anyTimes();
        expect(request.getContext("two")).andReturn(twoScope).anyTimes();
        expect(oneScope.containsKey("name1")).andReturn(true);
        expect(oneScope.containsKey("name2")).andReturn(false);
        expect(oneScope.containsKey("name3")).andReturn(false);
        expect(twoScope.containsKey("name2")).andReturn(true);
        expect(twoScope.containsKey("name3")).andReturn(false);

        replay(request, oneScope, twoScope);
        assertEquals(".getContext(\"one\").put(index, target)", accessor.getSourceSetter(null, request, "name1"));
        assertEquals(".getContext(\"two\").put(index, target)", accessor.getSourceSetter(null, request, "name2"));
        assertEquals(".getContext(\"one\").put(index, target)", accessor.getSourceSetter(null, request, "name3"));
        verify(request, oneScope, twoScope);
    }

    /**
     * Test method for {@link AnyScopePropertyAccessor#setProperty(Map, Object, Object, Object)}.
     */
    @Test
    public void testSetProperty() {
        Request request = createMock(Request.class);
        Map<String, Object> oneScope = createMock(Map.class);
        Map<String, Object> twoScope = createMock(Map.class);

        expect(request.getAvailableScopes()).andReturn(Arrays.asList("one", "two")).anyTimes();
        expect(request.getContext("one")).andReturn(oneScope).anyTimes();
        expect(request.getContext("two")).andReturn(twoScope).anyTimes();
        expect(oneScope.containsKey("name1")).andReturn(true);
        expect(oneScope.put("name1", "otherValue1")).andReturn("value1");
        expect(oneScope.containsKey("name2")).andReturn(false);
        expect(oneScope.containsKey("name3")).andReturn(false);
        expect(twoScope.containsKey("name2")).andReturn(true);
        expect(twoScope.put("name2", "otherValue2")).andReturn("value2");
        expect(twoScope.containsKey("name3")).andReturn(false);
        expect(oneScope.put("name3", "otherValue3")).andReturn(null);

        replay(request, oneScope, twoScope);
        accessor.setProperty(null, request, "name1", "otherValue1");
        accessor.setProperty(null, request, "name2", "otherValue2");
        accessor.setProperty(null, request, "name3", "otherValue3");
        verify(request, oneScope, twoScope);
    }

}
