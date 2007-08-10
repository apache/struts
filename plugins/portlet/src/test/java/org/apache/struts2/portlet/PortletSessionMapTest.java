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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import junit.framework.TestCase;


/**
 * PortletSessionMapTest. Insert description.
 *
 */
public class PortletSessionMapTest extends TestCase {

    public void testPut() {

    	MockPortletRequest request = new MockPortletRequest(null, null, new HashMap<String, Object>());

        PortletSessionMap map = new PortletSessionMap(request);
        assertEquals("testValue1", map.put("testAttribute1", "testValue1"));
        assertEquals("testValue2", map.put("testAttribute2", "testValue2"));

        PortletSession session = request.getPortletSession();
        // Assert that the values has been propagated to the session
        assertEquals("testValue1", session.getAttribute("testAttribute1"));
        assertEquals("testValue2", session.getAttribute("testAttribute2"));
    }

    public void testGet() {
    	MockPortletRequest request = new MockPortletRequest(null, null, new HashMap<String, Object>());
    	PortletSession session = request.getPortletSession();
    	session.setAttribute("testAttribute1", "testValue1");
    	session.setAttribute("testAttribute2", "testValue2");
        PortletSessionMap map = new PortletSessionMap(request);
        Object val1 = map.get("testAttribute1");
        Object val2 = map.get("testAttribute2");
        assertEquals("testValue1", val1);
        assertEquals("testValue2", val2);
    }

    public void testClear() {
    	Map<String, Object> sessionMap = new HashMap<String, Object>();
    	sessionMap.put("testAttribute1", "testValue1");
    	sessionMap.put("testAttribute2", "testValue2");

        PortletRequest req = new MockPortletRequest(null, null, sessionMap);

        PortletSessionMap map = new PortletSessionMap(req);
        map.clear();
        
        assertEquals(0, sessionMap.size());
    }

    public void testRemove() {
    	MockPortletRequest request = new MockPortletRequest(null, null, new HashMap<String, Object>());
    	PortletSession session = request.getPortletSession();
    	session.setAttribute("testAttribute1", "testValue1");

        PortletSessionMap map = new PortletSessionMap(request);
        Object ret = map.remove("testAttribute1");
        assertEquals("testValue1", ret);
        assertNull(session.getAttribute("testAttribute1"));
    }

    public void testEntrySet() {
    	MockPortletRequest request = new MockPortletRequest(null, null, new HashMap<String, Object>());
    	PortletSession session = request.getPortletSession();
    	session.setAttribute("testAttribute1", "testValue1");
    	session.setAttribute("testAttribute2", "testValue2");

        PortletSessionMap map = new PortletSessionMap(request);
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
