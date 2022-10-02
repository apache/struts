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

import org.apache.tiles.request.attribute.HasRemovableKeys;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link RemovableKeySet}.
 */
public class RemovableKeySetTest {

    /**
     * The extractor to use.
     */
    private HasRemovableKeys<Integer> extractor;

    /**
     * The key set to test.
     */
    private RemovableKeySet entrySet;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        extractor = createMock(HasRemovableKeys.class);
        entrySet = new RemovableKeySet(extractor);
    }

    /**
     * Test method for {@link RemovableKeySet#remove(Object)}.
     */
    @Test
    public void testRemove() {
        expect(extractor.getValue("one")).andReturn(1);
        extractor.removeValue("one");

        replay(extractor);
        assertTrue(entrySet.remove("one"));
        verify(extractor);
    }

    /**
     * Test method for {@link RemovableKeySet#remove(Object)}.
     */
    @Test
    public void testRemoveNoEffect() {
        expect(extractor.getValue("one")).andReturn(null);

        replay(extractor);
        assertFalse(entrySet.remove("one"));
        verify(extractor);
    }

    /**
     * Test method for {@link RemovableKeySet#removeAll(java.util.Collection)}.
     */
    @Test
    public void testRemoveAll() {
        expect(extractor.getValue("one")).andReturn(1);
        expect(extractor.getValue("two")).andReturn(2);
        extractor.removeValue("one");
        extractor.removeValue("two");

        replay(extractor);
        List<String> coll = new ArrayList<>();
        coll.add("one");
        coll.add("two");
        assertTrue(entrySet.removeAll(coll));
        verify(extractor);
    }

    /**
     * Test method for {@link RemovableKeySet#retainAll(java.util.Collection)}.
     */
    @Test
    public void testRetainAll() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("three");
        expect(keys.hasMoreElements()).andReturn(false);

        extractor.removeValue("three");

        replay(extractor, keys);
        List<String> coll = new ArrayList<>();
        coll.add("one");
        coll.add("two");
        assertTrue(entrySet.retainAll(coll));
        verify(extractor, keys);
    }

}
