/*
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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;

import org.jmock.MockObjectTestCase;
import org.springframework.mock.web.portlet.MockPortletRequest;


/**
 * PortletRequestMapTest. Insert description.
 *
 */
public class PortletRequestMapTest extends MockObjectTestCase {

    public void testGet() {
    	PortletRequest request = new MockPortletRequest();
    	request.setAttribute("testAttribute", "testValue");

    	PortletRequestMap map = new PortletRequestMap(request);
        String value = (String)map.get("testAttribute");
        assertEquals("testValue", value);
    }

    public void testPut() {
    	PortletRequest request = new MockPortletRequest();
    	PortletRequestMap map = new PortletRequestMap(request);
        Object obj = map.put("testAttribute", "testValue1");
        
        assertEquals(obj, "testValue1");
        assertEquals("testValue1", request.getAttribute("testAttribute"));
    }

    public void testClear() {
    	MockPortletRequest request = new MockPortletRequest();
    	request.setAttribute("testAttribute1", "testValue1");
    	request.setAttribute("testAttribute2", "testValue2");


        PortletRequestMap map = new PortletRequestMap(request);
        map.clear();

        assertFalse(request.getAttributeNames().hasMoreElements());
    }

    public void testRemove() {
        MockPortletRequest request = new MockPortletRequest();
        request.setAttribute("testAttribute1", "testValue1");
        
        PortletRequestMap map = new PortletRequestMap(request);
        assertEquals("testValue1", map.remove("testAttribute1"));
        assertNull(request.getAttribute("testAttribute1"));
    }

    public void testEntrySet() {
    	MockPortletRequest request = new MockPortletRequest();
    	request.setAttribute("testAttribute1", "testValue1");
    	request.setAttribute("testAttribute2", "testValue2");

        PortletRequestMap map = new PortletRequestMap(request);
        Set entries = map.entrySet();

        assertEquals(3, entries.size());
        Iterator it = entries.iterator();
        for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            checkEntry(entry);
	}
    }
    
    private void checkEntry(Map.Entry entry) {
	if(entry.getKey().equals("testAttribute1")) {
        	assertEquals("testValue1", entry.getValue());
        }
        else if(entry.getKey().equals("testAttribute2")) {
        	assertEquals("testValue2", entry.getValue());
        }
        else if(entry.getKey().equals("javax.portlet.lifecycle_phase")) {
    		assertNull(entry.getValue());
        }
        else {
        	fail("Unexpected entry in entry set: " + entry);
        }
    }

}
