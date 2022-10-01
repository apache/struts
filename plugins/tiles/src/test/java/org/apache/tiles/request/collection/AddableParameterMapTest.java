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

import org.apache.tiles.request.attribute.HasAddableKeys;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNull;

/**
 * Tests {@link AddableParameterMap}.
 */
public class AddableParameterMapTest {

    /**
     * The object to test.
     */
    private AddableParameterMap map;

    /**
     * The extractor to use.
     */
    private HasAddableKeys<String> extractor;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        extractor = createMock(HasAddableKeys.class);
        map = new AddableParameterMap(extractor);
    }

    /**
     * Test method for {@link AddableParameterMap#entrySet()}.
     */
    @Test
    public void testEntrySet() {
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        MapEntry<String, String> entry1 = new MapEntry<>("one", "value1", false);
        MapEntry<String, String> entry2 = new MapEntry<>("two", "value2", false);
        List<Map.Entry<String, String>> entries = new ArrayList<>(2);
        entries.add(entry1);
        entries.add(entry2);

        extractor.setValue("one", "value1");
        expectLastCall().times(2);
        extractor.setValue("two", "value2");
        replay(extractor);
        entrySet.add(entry1);
        entrySet.addAll(entries);
        verify(extractor);
    }

    /**
     * Test method for {@link AddableParameterMap#put(String, String)}.
     */
    @Test
    public void testPut() {
        expect(extractor.getValue("one")).andReturn(null);
        extractor.setValue("one", "value1");

        replay(extractor);
        assertNull(map.put("one", "value1"));
        verify(extractor);
    }

    /**
     * Test method for {@link AddableParameterMap#putAll(Map)}.
     */
    @Test
    public void testPutAll() {
        Map<String, String> map = new HashMap<>();
        map.put("one", "value1");
        map.put("two", "value2");

        extractor.setValue("one", "value1");
        extractor.setValue("two", "value2");

        replay(extractor);
        this.map.putAll(map);
        verify(extractor);
    }
}
