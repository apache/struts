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

package org.apache.tiles.api;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link ListAttribute}.
 */
public class ListAttributeTest {

    @Test
    public void testHashCode() {
        ListAttribute attribute = new ListAttribute();
        List<Attribute> list = new ArrayList<>();
        list.add(new Attribute("value1"));
        list.add(new Attribute("value2"));
        attribute.setValue(list);
        attribute.setInherit(true);
        assertEquals(list.hashCode() + Boolean.TRUE.hashCode(), attribute.hashCode());
    }

    @Test
    public void testEqualsObject() {
        ListAttribute attribute = new ListAttribute();
        List<Attribute> list = new ArrayList<>();
        list.add(new Attribute("value1"));
        list.add(new Attribute("value2"));
        attribute.setValue(list);
        attribute.setInherit(true);
        ListAttribute toCheck = new ListAttribute(attribute);
        assertEquals(attribute, toCheck);
        toCheck = new ListAttribute(attribute);
        toCheck.setInherit(false);
        assertNotEquals(attribute, toCheck);
        toCheck = new ListAttribute(attribute);
        toCheck.add(new Attribute("value3"));
        assertNotEquals(attribute, toCheck);
    }

    @Test
    public void testListAttributeListAttribute() {
        ListAttribute attribute = new ListAttribute();
        List<Attribute> list = new ArrayList<>();
        list.add(new Attribute("value1"));
        list.add(new Attribute("value2"));
        list.add(null);
        attribute.setValue(list);
        attribute.setInherit(true);
        ListAttribute toCheck = new ListAttribute(attribute);
        assertEquals(attribute, toCheck);
    }

    @Test
    public void testSetValue() {
        ListAttribute attribute = new ListAttribute();
        List<Attribute> list = new ArrayList<>();
        list.add(new Attribute("value1"));
        list.add(new Attribute("value2"));
        attribute.setValue(list);
        assertEquals(list, attribute.getValue());
    }

    @Test
    public void testSetInherit() {
        ListAttribute attribute = new ListAttribute();
        attribute.setInherit(true);
        assertTrue(attribute.isInherit());
        attribute.setInherit(false);
        assertFalse(attribute.isInherit());
    }

    @Test
    public void testClone() {
        ListAttribute attribute = new ListAttribute();
        List<Attribute> list = new ArrayList<>();
        list.add(new Attribute("value1"));
        list.add(new Attribute("value2"));
        attribute.setValue(list);
        attribute.setInherit(true);
        ListAttribute toCheck = attribute.copy();
        assertEquals(attribute, toCheck);
    }
}
