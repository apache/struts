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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link ReadOnlyEnumerationMap#entrySet()}.
 */
public class ReadOnlyEnumerationMapEntrySetTest {

    /**
     * The extractor to use.
     */
    private HasKeys<Integer> extractor;

    /**
     * The set to test.
     */
    private Set<Map.Entry<String, Integer>> entrySet;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        extractor = createMock(HasKeys.class);
        ReadOnlyEnumerationMap<Integer> map = new ReadOnlyEnumerationMap<>(extractor);
        entrySet = map.entrySet();
    }

    /**
     * Tests {@link Set#add(Object)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testAdd() {
        entrySet.add(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddAll() {
        entrySet.addAll(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testClear() {
        entrySet.clear();
    }

    /**
     * Tests {@link Set#contains(Object)}.
     */
    @Test
    public void testContains() {
        Map.Entry<String, Integer> entry = createMock(Map.Entry.class);
        Enumeration<String> values2 = createMock(Enumeration.class);

        expect(entry.getKey()).andReturn("two");
        expect(entry.getValue()).andReturn(2);

        expect(extractor.getValue("two")).andReturn(2);

        replay(extractor, entry, values2);
        assertTrue(entrySet.contains(entry));
        verify(extractor, entry, values2);
    }

    @Test
    public void testContainsAll() {
        Map.Entry<String, Integer> entry1 = createMock(Map.Entry.class);
        Map.Entry<String, Integer> entry2 = createMock(Map.Entry.class);

        expect(entry1.getKey()).andReturn("one");
        expect(entry1.getValue()).andReturn(1);
        expect(entry2.getKey()).andReturn("two");
        expect(entry2.getValue()).andReturn(2);

        expect(extractor.getValue("one")).andReturn(1);
        expect(extractor.getValue("two")).andReturn(2);

        replay(extractor, entry1, entry2);
        List<Map.Entry<String, Integer>> coll = new ArrayList<>();
        coll.add(entry1);
        coll.add(entry2);
        assertTrue(entrySet.containsAll(coll));
        verify(extractor, entry1, entry2);
    }

    @Test
    public void testContainsAllFalse() {
        Map.Entry<String, String> entry1 = createMock(Map.Entry.class);

        expect(entry1.getKey()).andReturn("one");
        expect(entry1.getValue()).andReturn("value4");

        expect(extractor.getValue("one")).andReturn(1);

        replay(extractor, entry1);
        List<Map.Entry<String, String>> coll = new ArrayList<>();
        coll.add(entry1);
        assertFalse(entrySet.containsAll(coll));
        verify(extractor, entry1);
    }

    /**
     * Test method for {@link Set#isEmpty()}.
     */
    @Test
    public void testIsEmpty() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);

        replay(extractor, keys);
        assertFalse(entrySet.isEmpty());
        verify(extractor, keys);
    }

    /**
     * Test method for {@link Set#iterator()}.
     */
    @Test
    public void testIterator() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");

        expect(extractor.getValue("two")).andReturn(2);

        replay(extractor, keys);
        Iterator<Map.Entry<String, Integer>> entryIt = entrySet.iterator();
        assertTrue(entryIt.hasNext());
        MapEntry<String, Integer> entry = new MapEntry<>("two", 2, false);
        assertEquals(entry, entryIt.next());
        verify(extractor, keys);
    }

    /**
     * Test method for {@link Set#iterator()}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorRemove() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);

        try {
            replay(extractor, keys);
            entrySet.iterator().remove();
        } finally {
            verify(extractor, keys);
        }
    }

    /**
     * Tests {@link Set#remove(Object)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        entrySet.remove(null);
    }

    /**
     * Tests {@link Set#removeAll(java.util.Collection)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveAll() {
        entrySet.removeAll(null);
    }

    /**
     * Tests {@link Set#retainAll(java.util.Collection)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRetainAll() {
        entrySet.retainAll(null);
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
        assertEquals(2, entrySet.size());
        verify(extractor, keys);
    }

    /**
     * Test method for {@link Set#toArray()}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testToArray() {
        Enumeration<String> keys = createMock(Enumeration.class);
        Enumeration<String> values1 = createMock(Enumeration.class);
        Enumeration<String> values2 = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(false);

        expect(extractor.getValue("one")).andReturn(1);
        expect(extractor.getValue("two")).andReturn(2);

        MapEntry<String, Integer>[] entryArray = new MapEntry[2];
        entryArray[0] = new MapEntry<>("one", 1, false);
        entryArray[1] = new MapEntry<>("two", 2, false);

        replay(extractor, keys, values1, values2);
        assertArrayEquals(entryArray, entrySet.toArray());
        verify(extractor, keys, values1, values2);
    }

    /**
     * Test method for {@link Set#toArray(Object[])}.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testToArrayTArray() {
        Enumeration<String> keys = createMock(Enumeration.class);
        Enumeration<String> values1 = createMock(Enumeration.class);
        Enumeration<String> values2 = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(false);

        expect(extractor.getValue("one")).andReturn(1);
        expect(extractor.getValue("two")).andReturn(2);

        MapEntry<String, Integer>[] entryArray = new MapEntry[2];
        entryArray[0] = new MapEntry<>("one", 1, false);
        entryArray[1] = new MapEntry<>("two", 2, false);

        replay(extractor, keys, values1, values2);
        MapEntry<String, String>[] realArray = new MapEntry[2];
        assertArrayEquals(entryArray, entrySet.toArray(realArray));
        verify(extractor, keys, values1, values2);
    }
}
