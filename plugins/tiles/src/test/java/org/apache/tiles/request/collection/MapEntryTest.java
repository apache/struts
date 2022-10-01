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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Tests {@link MapEntry}.
 */
public class MapEntryTest {

    /**
     * Test method for {@link MapEntry#hashCode()}.
     */
    @Test
    public void testHashCode() {
        MapEntry<String, String> entry = new MapEntry<>("key", "value", false);
        assertEquals("key".hashCode() ^ "value".hashCode(), entry.hashCode());
        entry = new MapEntry<>(null, "value", false);
        assertEquals("value".hashCode(), entry.hashCode());
        entry = new MapEntry<>("key", null, false);
        assertEquals("key".hashCode(), entry.hashCode());
        entry = new MapEntry<>(null, null, false);
        assertEquals(0, entry.hashCode());
    }

    /**
     * Test method for {@link MapEntry#getKey()}.
     */
    @Test
    public void testGetKey() {
        MapEntry<String, String> entry = new MapEntry<>("key", "value", false);
        assertEquals("key", entry.getKey());
    }

    /**
     * Test method for {@link MapEntry#getValue()}.
     */
    @Test
    public void testGetValue() {
        MapEntry<String, String> entry = new MapEntry<>("key", "value", false);
        assertEquals("value", entry.getValue());
    }

    /**
     * Test method for {@link MapEntry#setValue(Object)}.
     */
    @Test
    public void testSetValue() {
        MapEntry<String, String> entry = new MapEntry<>("key", "value", true);
        assertEquals("value", entry.getValue());
        entry.setValue("value2");
        assertEquals("value2", entry.getValue());
    }

    /**
     * Test method for {@link MapEntry#setValue(Object)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSetValueException() {
        MapEntry<String, String> entry = new MapEntry<>("key", "value", false);
        assertEquals("value", entry.getValue());
        entry.setValue("value2");
    }

    /**
     * Test method for {@link MapEntry#equals(Object)}.
     */
    @Test
    public void testEqualsObject() {
        MapEntry<String, String> entry = new MapEntry<>("key", "value", false);
        assertNotEquals(null, entry);
        MapEntry<String, String> entry2 = new MapEntry<>("key", "value", false);
        assertEquals(entry, entry2);
        entry2 = new MapEntry<>("key2", "value", false);
        assertNotEquals(entry, entry2);
        entry2 = new MapEntry<>("key", "value2", false);
        assertNotEquals(entry, entry2);
        entry = new MapEntry<>(null, "value", false);
        entry2 = new MapEntry<>(null, "value", false);
        assertEquals(entry, entry2);
        entry = new MapEntry<>("key", null, false);
        entry2 = new MapEntry<>("key", null, false);
        assertEquals(entry, entry2);
    }

}
