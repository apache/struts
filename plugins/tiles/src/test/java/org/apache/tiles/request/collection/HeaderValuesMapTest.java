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

import org.apache.tiles.request.attribute.EnumeratedValuesExtractor;
import org.junit.Before;
import org.junit.Test;

import java.util.Enumeration;
import java.util.HashMap;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link HeaderValuesMap}.
 */
public class HeaderValuesMapTest {

    /**
     * The extractor to use.
     */
    private EnumeratedValuesExtractor extractor;

    /**
     * The map to test.
     */
    private HeaderValuesMap map;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        extractor = createMock(EnumeratedValuesExtractor.class);
        map = new HeaderValuesMap(extractor);
    }

    /**
     * Test method for {@link HeaderValuesMap#hashCode()}.
     */
    @Test
    public void testHashCode() {
        Enumeration<String> keys = createMock(Enumeration.class);
        Enumeration<String> values1 = createMock(Enumeration.class);
        Enumeration<String> values2 = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(false);

        expect(extractor.getValues("one")).andReturn(values1);
        expect(values1.hasMoreElements()).andReturn(true);
        expect(values1.nextElement()).andReturn("value1");
        expect(values1.hasMoreElements()).andReturn(false);

        expect(extractor.getValues("two")).andReturn(values2);
        expect(values2.hasMoreElements()).andReturn(true);
        expect(values2.nextElement()).andReturn("value2");
        expect(values2.hasMoreElements()).andReturn(true);
        expect(values2.nextElement()).andReturn("value3");
        expect(values2.hasMoreElements()).andReturn(false);

        replay(extractor, keys, values1, values2);
        assertEquals(
            ("one".hashCode() ^ "value1".hashCode())
                + ("two".hashCode() ^ ("value2".hashCode() + "value3"
                .hashCode())),
            map.hashCode());
        verify(extractor, keys, values1, values2);
    }

    /**
     * Test method for {@link HeaderValuesMap#clear()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testClear() {
        map.clear();
    }

    /**
     * Test method for {@link HeaderValuesMap#containsKey(Object)}.
     */
    @Test
    public void testContainsKey() {
        expect(extractor.getValue("one")).andReturn("value1");
        expect(extractor.getValue("two")).andReturn(null);

        replay(extractor);
        assertTrue(map.containsKey("one"));
        assertFalse(map.containsKey("two"));
        verify(extractor);
    }

    /**
     * Test method for {@link HeaderValuesMap#containsValue(Object)}.
     */
    @Test
    public void testContainsValue() {
        assertFalse(map.containsValue(1));

        Enumeration<String> keys = createMock(Enumeration.class);
        Enumeration<String> values1 = createMock(Enumeration.class);
        Enumeration<String> values2 = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");

        expect(extractor.getValues("one")).andReturn(values1);
        expect(values1.hasMoreElements()).andReturn(true);
        expect(values1.nextElement()).andReturn("value1");
        expect(values1.hasMoreElements()).andReturn(false);

        expect(extractor.getValues("two")).andReturn(values2);
        expect(values2.hasMoreElements()).andReturn(true);
        expect(values2.nextElement()).andReturn("value2");
        expect(values2.hasMoreElements()).andReturn(true);
        expect(values2.nextElement()).andReturn("value3");
        expect(values2.hasMoreElements()).andReturn(false);

        replay(extractor, keys, values1, values2);
        assertTrue(map.containsValue(new String[]{"value2", "value3"}));
        verify(extractor, keys, values1, values2);
    }

    /**
     * Test method for {@link HeaderValuesMap#containsValue(Object)}.
     */
    @Test
    public void testContainsValueFalse() {
        Enumeration<String> keys = createMock(Enumeration.class);
        Enumeration<String> values1 = createMock(Enumeration.class);
        Enumeration<String> values2 = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(false);

        expect(extractor.getValues("one")).andReturn(values1);
        expect(values1.hasMoreElements()).andReturn(true);
        expect(values1.nextElement()).andReturn("value1");
        expect(values1.hasMoreElements()).andReturn(false);

        expect(extractor.getValues("two")).andReturn(values2);
        expect(values2.hasMoreElements()).andReturn(true);
        expect(values2.nextElement()).andReturn("value2");
        expect(values2.hasMoreElements()).andReturn(true);
        expect(values2.nextElement()).andReturn("value3");
        expect(values2.hasMoreElements()).andReturn(false);

        replay(extractor, keys, values1, values2);
        assertFalse(map.containsValue(new String[]{"value2", "value4"}));
        verify(extractor, keys, values1, values2);
    }

    /**
     * Test method for {@link HeaderValuesMap#equals(Object)}.
     */
    @Test
    public void testEqualsObject() {
        Enumeration<String> keys = createMock(Enumeration.class);
        Enumeration<String> values1 = createMock(Enumeration.class);
        Enumeration<String> values2 = createMock(Enumeration.class);
        EnumeratedValuesExtractor otherExtractor = createMock(EnumeratedValuesExtractor.class);
        Enumeration<String> otherValues1 = createMock(Enumeration.class);
        Enumeration<String> otherValues2 = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(false);

        expect(extractor.getValues("one")).andReturn(values1);
        expect(values1.hasMoreElements()).andReturn(true);
        expect(values1.nextElement()).andReturn("value1");
        expect(values1.hasMoreElements()).andReturn(false);

        expect(extractor.getValues("two")).andReturn(values2);
        expect(values2.hasMoreElements()).andReturn(true);
        expect(values2.nextElement()).andReturn("value2");
        expect(values2.hasMoreElements()).andReturn(true);
        expect(values2.nextElement()).andReturn("value3");
        expect(values2.hasMoreElements()).andReturn(false);

        expect(otherExtractor.getValues("one")).andReturn(otherValues1);
        expect(otherValues1.hasMoreElements()).andReturn(true);
        expect(otherValues1.nextElement()).andReturn("value1");
        expect(otherValues1.hasMoreElements()).andReturn(false);

        expect(otherExtractor.getValues("two")).andReturn(otherValues2);
        expect(otherValues2.hasMoreElements()).andReturn(true);
        expect(otherValues2.nextElement()).andReturn("value2");
        expect(otherValues2.hasMoreElements()).andReturn(true);
        expect(otherValues2.nextElement()).andReturn("value3");
        expect(otherValues2.hasMoreElements()).andReturn(false);

        replay(extractor, otherExtractor, keys, values1, values2, otherValues1, otherValues2);
        HeaderValuesMap otherMap = new HeaderValuesMap(otherExtractor);
        assertTrue(map.equals(otherMap));
        verify(extractor, otherExtractor, keys, values1, values2, otherValues1, otherValues2);
    }

    /**
     * Test method for {@link HeaderValuesMap#get(Object)}.
     */
    @Test
    public void testGet() {
        Enumeration<String> values2 = createMock(Enumeration.class);

        expect(extractor.getValues("two")).andReturn(values2);
        expect(values2.hasMoreElements()).andReturn(true);
        expect(values2.nextElement()).andReturn("value2");
        expect(values2.hasMoreElements()).andReturn(true);
        expect(values2.nextElement()).andReturn("value3");
        expect(values2.hasMoreElements()).andReturn(false);

        replay(extractor, values2);
        assertArrayEquals(new String[]{"value2", "value3"}, map.get("two"));
        verify(extractor, values2);
    }

    /**
     * Test method for {@link HeaderValuesMap#isEmpty()}.
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
     * Test method for {@link HeaderValuesMap#isEmpty()}.
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
     * Test method for {@link HeaderValuesMap#keySet()}.
     */
    @Test
    public void testKeySet() {
        replay(extractor);
        assertTrue(map.keySet() instanceof KeySet);
        verify(extractor);
    }

    /**
     * Test method for {@link HeaderValuesMap#put(String, String[])}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testPut() {
        map.put("one", new String[]{"value1", "value2"});
    }

    /**
     * Test method for {@link HeaderValuesMap#putAll(java.util.Map)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testPutAll() {
        map.putAll(new HashMap<>());
    }

    /**
     * Test method for {@link HeaderValuesMap#remove(Object)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        map.remove("one");
    }

    /**
     * Test method for {@link HeaderValuesMap#size()}.
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
}
