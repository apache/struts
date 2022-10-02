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
package org.apache.tiles.request.collection;

import org.apache.tiles.request.attribute.HasKeys;
import org.junit.Before;
import org.junit.Test;

import java.util.Enumeration;
import java.util.HashMap;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link ReadOnlyEnumerationMap}.
 */
public class ReadOnlyEnumerationMapTest {

    /**
     * The extractor to use.
     */
    private HasKeys<Integer> extractor;

    /**
     * The map to test.
     */
    private ReadOnlyEnumerationMap<Integer> map;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        extractor = createMock(HasKeys.class);
        map = new ReadOnlyEnumerationMap<>(extractor);
    }

    /**
     * Test method for {@link ReadOnlyEnumerationMap#clear()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testClear() {
        map.clear();
    }

    /**
     * Test method for {@link ReadOnlyEnumerationMap#containsKey(Object)}.
     */
    @Test
    public void testContainsKey() {
        expect(extractor.getValue("one")).andReturn(1);
        expect(extractor.getValue("two")).andReturn(null);

        replay(extractor);
        assertTrue(map.containsKey("one"));
        assertFalse(map.containsKey("two"));
        verify(extractor);
    }

    /**
     * Test method for {@link ReadOnlyEnumerationMap#containsValue(Object)}.
     */
    @Test
    public void testContainsValue() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");

        expect(extractor.getValue("one")).andReturn(1);
        expect(extractor.getValue("two")).andReturn(2);

        replay(extractor, keys);
        assertTrue(map.containsValue(2));
        verify(extractor, keys);
    }

    /**
     * Test method for {@link ReadOnlyEnumerationMap#containsValue(Object)}.
     */
    @Test
    public void testContainsValueFalse() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(false);

        expect(extractor.getValue("one")).andReturn(1);
        expect(extractor.getValue("two")).andReturn(1);

        replay(extractor, keys);
        assertFalse(map.containsValue(3));
        verify(extractor, keys);
    }

    /**
     * Test method for {@link ReadOnlyEnumerationMap#get(Object)}.
     */
    @Test
    public void testGet() {
        expect(extractor.getValue("two")).andReturn(2);

        replay(extractor);
        assertEquals(new Integer(2), map.get("two"));
        verify(extractor);
    }

    /**
     * Test method for {@link ReadOnlyEnumerationMap#isEmpty()}.
     */
    @Test
    public void testIsEmpty() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);

        replay(extractor, keys);
        assertFalse(map.isEmpty());
        verify(extractor, keys);
    }

    /**
     * Test method for {@link ReadOnlyEnumerationMap#isEmpty()}.
     */
    @Test
    public void testIsEmptyTrue() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(false);

        replay(extractor, keys);
        assertTrue(map.isEmpty());
        verify(extractor, keys);
    }

    /**
     * Test method for {@link ReadOnlyEnumerationMap#keySet()}.
     */
    @Test
    public void testKeySet() {
        replay(extractor);
        assertTrue(map.keySet() instanceof KeySet);
        verify(extractor);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPut() {
        map.put("one", 1);
    }

    /**
     * Test method for {@link ReadOnlyEnumerationMap#putAll(java.util.Map)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testPutAll() {
        map.putAll(new HashMap<>());
    }

    /**
     * Test method for {@link ReadOnlyEnumerationMap#remove(Object)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        map.remove("one");
    }

    /**
     * Test method for {@link ReadOnlyEnumerationMap#size()}.
     */
    @Test
    public void testSize() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(false);

        replay(extractor, keys);
        assertEquals(2, map.size());
        verify(extractor, keys);
    }

    @Test
    public void testHashCode() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);

        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("first");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("second");
        expect(keys.hasMoreElements()).andReturn(false);

        Integer value1 = 1;

        expect(extractor.getValue("first")).andReturn(value1);
        expect(extractor.getValue("second")).andReturn(null);

        replay(extractor, keys);
        assertEquals(("first".hashCode() ^ value1.hashCode()) + ("second".hashCode()), map.hashCode());
        verify(extractor, keys);
    }

    @Test
    public void testEqualsObject() {
        HasKeys<Integer> otherRequest = createMock(HasKeys.class);
        ReadOnlyEnumerationMap<Integer> otherMap = createMockBuilder(ReadOnlyEnumerationMap.class).withConstructor(otherRequest).createMock();
        Enumeration<String> keys = createMock(Enumeration.class);
        Enumeration<String> otherKeys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(otherRequest.getKeys()).andReturn(otherKeys);

        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("first");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("second");
        expect(keys.hasMoreElements()).andReturn(false);

        expect(extractor.getValue("first")).andReturn(1);
        expect(extractor.getValue("second")).andReturn(2);

        expect(otherKeys.hasMoreElements()).andReturn(true);
        expect(otherKeys.nextElement()).andReturn("first");
        expect(otherKeys.hasMoreElements()).andReturn(true);
        expect(otherKeys.nextElement()).andReturn("second");
        expect(otherKeys.hasMoreElements()).andReturn(false);

        expect(otherRequest.getValue("first")).andReturn(1);
        expect(otherRequest.getValue("second")).andReturn(2);

        replay(extractor, otherRequest, otherMap, keys, otherKeys);
        assertEquals(map, otherMap);
        verify(extractor, otherRequest, otherMap, keys, otherKeys);
    }

    @Test
    public void testEqualsObjectFalse() {
        HasKeys<Integer> otherRequest = createMock(HasKeys.class);
        ReadOnlyEnumerationMap<Integer> otherMap = createMockBuilder(ReadOnlyEnumerationMap.class).withConstructor(otherRequest).createMock();
        Enumeration<String> keys = createMock(Enumeration.class);
        Enumeration<String> otherKeys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(otherRequest.getKeys()).andReturn(otherKeys);

        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("first");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("second");
        expect(keys.hasMoreElements()).andReturn(false);

        expect(extractor.getValue("first")).andReturn(1);
        expect(extractor.getValue("second")).andReturn(2);

        expect(otherKeys.hasMoreElements()).andReturn(true);
        expect(otherKeys.nextElement()).andReturn("first");
        expect(otherKeys.hasMoreElements()).andReturn(true);
        expect(otherKeys.nextElement()).andReturn("second");
        expect(otherKeys.hasMoreElements()).andReturn(false);

        expect(otherRequest.getValue("first")).andReturn(1);
        expect(otherRequest.getValue("second")).andReturn(3);

        replay(extractor, otherRequest, otherMap, keys, otherKeys);
        assertNotEquals(map, otherMap);
        verify(extractor, otherRequest, otherMap, keys, otherKeys);
    }
}
