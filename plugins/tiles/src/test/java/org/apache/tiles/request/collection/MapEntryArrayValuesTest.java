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
 * Tests {@link MapEntryArrayValues}.
 */
public class MapEntryArrayValuesTest {

    /**
     * Test method for {@link MapEntryArrayValues#hashCode()}.
     */
    @Test
    public void testHashCode() {
        MapEntryArrayValues<String, String> entry = new MapEntryArrayValues<>("key", new String[]{"value1", "value2"}, false);
        assertEquals("key".hashCode() ^ ("value1".hashCode() + "value2".hashCode()), entry.hashCode());
        entry = new MapEntryArrayValues<>(null, new String[]{"value1", "value2"}, false);
        assertEquals(("value1".hashCode() + "value2".hashCode()), entry.hashCode());
        entry = new MapEntryArrayValues<>("key", null, false);
        assertEquals("key".hashCode(), entry.hashCode());
        entry = new MapEntryArrayValues<>(null, null, false);
        assertEquals(0, entry.hashCode());
    }

    /**
     * Test method for {@link MapEntryArrayValues#equals(Object)}.
     */
    @Test
    public void testEqualsObject() {
        MapEntryArrayValues<String, String> entry = new MapEntryArrayValues<>("key", new String[]{"value1", "value2"}, false);
        assertNotEquals(null, entry);
        MapEntryArrayValues<String, String> entry2 = new MapEntryArrayValues<>("key", new String[]{"value1", "value2"}, false);
        assertEquals(entry, entry2);
        entry2 = new MapEntryArrayValues<>("key", null, false);
        assertNotEquals(entry, entry2);
        entry2 = new MapEntryArrayValues<>("key2", new String[]{"value1", "value2"}, false);
        assertNotEquals(entry, entry2);
        entry2 = new MapEntryArrayValues<>("key", new String[]{"value1", "value3"}, false);
        assertNotEquals(entry, entry2);
        entry = new MapEntryArrayValues<>(null, new String[]{"value1", "value2"}, false);
        entry2 = new MapEntryArrayValues<>(null, new String[]{"value1", "value2"}, false);
        assertEquals(entry, entry2);
        entry = new MapEntryArrayValues<>("key", null, false);
        entry2 = new MapEntryArrayValues<>("key", null, false);
        assertEquals(entry, entry2);
        entry2 = new MapEntryArrayValues<>("key", new String[]{"value1", "value2"}, false);
        assertNotEquals(entry, entry2);
        entry = new MapEntryArrayValues<>(null, new String[]{null, "value2"}, false);
        entry2 = new MapEntryArrayValues<>(null, new String[]{null, "value2"}, false);
        assertEquals(entry, entry2);
        entry2 = new MapEntryArrayValues<>(null, new String[]{"value1", "value2"}, false);
        assertNotEquals(entry, entry2);
    }

}
