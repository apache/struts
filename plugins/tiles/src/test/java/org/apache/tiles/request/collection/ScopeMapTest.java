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

import org.apache.tiles.request.attribute.AttributeExtractor;
import org.junit.Before;
import org.junit.Test;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link ScopeMap}.
 */
public class ScopeMapTest {

    /**
     * The map tot est.
     */
    private ScopeMap map;

    /**
     * The extractor to use.
     */
    private AttributeExtractor extractor;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        extractor = createMock(AttributeExtractor.class);
        map = new ScopeMap(extractor);
    }

    /**
     * Test method for {@link ScopeMap#clear()}.
     */
    @Test
    public void testClear() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(false);

        extractor.removeValue("one");
        extractor.removeValue("two");

        replay(extractor, keys);
        map.clear();
        verify(extractor, keys);
    }

    /**
     * Test method for {@link ScopeMap#keySet()}.
     */
    @Test
    public void testKeySet() {
        replay(extractor);
        assertTrue(map.keySet() instanceof RemovableKeySet);
        verify(extractor);
    }

    /**
     * Test method for {@link ScopeMap#put(String, Object)}.
     */
    @Test
    public void testPutStringObject() {
        expect(extractor.getValue("one")).andReturn(null);
        extractor.setValue("one", 1);

        replay(extractor);
        assertNull(map.put("one", 1));
        verify(extractor);
    }

    /**
     * Test method for {@link ScopeMap#putAll(Map)}.
     */
    @Test
    public void testPutAllMapOfQextendsStringQextendsObject() {
        Map<String, Object> items = new LinkedHashMap<>();
        items.put("one", 1);
        items.put("two", 2);

        extractor.setValue("one", 1);
        extractor.setValue("two", 2);

        replay(extractor);
        map.putAll(items);
        verify(extractor);
    }

    /**
     * Test method for {@link ScopeMap#remove(Object)}.
     */
    @Test
    public void testRemoveObject() {
        expect(extractor.getValue("one")).andReturn(1);
        extractor.removeValue("one");

        replay(extractor);
        assertEquals(1, map.remove("one"));
        verify(extractor);
    }
}
