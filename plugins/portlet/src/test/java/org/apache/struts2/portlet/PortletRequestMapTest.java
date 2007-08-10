/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.portlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;


/**
 * PortletRequestMapTest. Insert description.
 *
 */
public class PortletRequestMapTest extends MockObjectTestCase {

    public void testGet() {
    	PortletRequest request = new MockPortletRequest(new HashMap<String, String[]>(), new HashMap<String, Object>(), null);
    	request.setAttribute("testAttribute", "testValue");

    	PortletRequestMap map = new PortletRequestMap(request);
        String value = (String)map.get("testAttribute");
        assertEquals("testValue", value);
    }

    public void testPut() {
    	PortletRequest request = new MockPortletRequest(new HashMap<String, String[]>(), new HashMap<String, Object>(), null);
    	PortletRequestMap map = new PortletRequestMap(request);
        Object obj = map.put("testAttribute", "testValue1");
        
        assertEquals(obj, "testValue1");
        assertEquals("testValue1", request.getAttribute("testAttribute"));
    }

    public void testClear() {
    	Map<String, Object> attribs = new HashMap<String, Object>();
    	attribs.put("testAttribute1", "testValue1");
    	attribs.put("testAttribute2", "testValue2");
    	PortletRequest request = new MockPortletRequest(new HashMap<String, String[]>(), attribs, null);


        PortletRequestMap map = new PortletRequestMap(request);
        map.clear();

        assertEquals(0, attribs.size());
    }

    public void testRemove() {
    	Map<String, Object> attribs = new HashMap<String, Object>();
    	attribs.put("testAttribute1", "testValue1");

        PortletRequest request = new MockPortletRequest(new HashMap<String, String[]>(), attribs, null);

        PortletRequestMap map = new PortletRequestMap(request);
        assertEquals("testValue1", map.remove("testAttribute1"));
        assertNull(request.getAttribute("testAttribute1"));
    }

    public void testEntrySet() {
    	PortletRequest request = new MockPortletRequest(new HashMap<String, String[]>(), new HashMap<String, Object>(), null);
    	request.setAttribute("testAttribute1", "testValue1");
    	request.setAttribute("testAttribute2", "testValue2");

        PortletRequestMap map = new PortletRequestMap(request);
        Set entries = map.entrySet();

        assertEquals(2, entries.size());
        Iterator it = entries.iterator();
        Map.Entry entry = (Map.Entry)it.next();
        assertEquals("testAttribute1", entry.getKey());
        assertEquals("testValue1", entry.getValue());
        entry = (Map.Entry)it.next();
        assertEquals("testAttribute2", entry.getKey());
        assertEquals("testValue2", entry.getValue());

    }

}
