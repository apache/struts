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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.portlet.PortletContext;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;

/**
 */
public class PortletApplicationMapTest extends MockObjectTestCase {

    Mock mockPortletContext;

    PortletContext portletContext;

    public void setUp() throws Exception {
        super.setUp();
        mockPortletContext = mock(PortletContext.class);
        portletContext = (PortletContext) mockPortletContext.proxy();
    }

    public void testGetFromAttributes() {
        mockPortletContext.stubs().method("getAttribute").with(eq("dummyKey"))
                .will(returnValue("dummyValue"));

        PortletApplicationMap map = new PortletApplicationMap(
                (PortletContext) mockPortletContext.proxy());

        assertEquals("dummyValue", map.get("dummyKey"));
    }

    public void testGetFromInitParameters() {
        mockPortletContext.stubs().method("getAttribute").with(eq("dummyKey"));
        mockPortletContext.stubs().method("getInitParameter").with(
                eq("dummyKey")).will(returnValue("dummyValue"));

        PortletApplicationMap map = new PortletApplicationMap(
                (PortletContext) mockPortletContext.proxy());

        assertEquals("dummyValue", map.get("dummyKey"));
    }

    public void testPut() {
        mockPortletContext.expects(once()).method("setAttribute").with(
                new Constraint[] { eq("dummyKey"), eq("dummyValue") });
        mockPortletContext.expects(once()).method("getAttribute").with(
                eq("dummyKey")).will(returnValue("dummyValue"));
        PortletApplicationMap map = new PortletApplicationMap(portletContext);
        Object val = map.put("dummyKey", "dummyValue");
        assertEquals("dummyValue", val);
    }

    public void testRemove() {
        mockPortletContext.expects(once()).method("getAttribute").with(
                eq("dummyKey")).will(returnValue("dummyValue"));
        mockPortletContext.expects(once()).method("removeAttribute").with(
                eq("dummyKey"));
        PortletApplicationMap map = new PortletApplicationMap(portletContext);
        Object val = map.remove("dummyKey");
        assertEquals("dummyValue", val);
    }

    public void testEntrySet() {

        Enumeration names = new Enumeration() {

            List keys = Arrays.asList("key1", "key2");

            Iterator it = keys.iterator();

            public boolean hasMoreElements() {
                return it.hasNext();
            }

            public Object nextElement() {
                return it.next();
            }

        };
        Enumeration initParamNames = new Enumeration() {

            List keys = Arrays.asList("key3");

            Iterator it = keys.iterator();

            public boolean hasMoreElements() {
                return it.hasNext();
            }

            public Object nextElement() {
                return it.next();
            }

        };

        mockPortletContext.stubs().method("getAttributeNames").will(
                returnValue(names));
        mockPortletContext.stubs().method("getInitParameterNames").will(
                returnValue(initParamNames));
        mockPortletContext.stubs().method("getAttribute").with(eq("key1"))
                .will(returnValue("value1"));
        mockPortletContext.stubs().method("getAttribute").with(eq("key2"))
                .will(returnValue("value2"));
        mockPortletContext.stubs().method("getAttribute").with(eq("key3"))
                .will(returnValue(null));
        mockPortletContext.stubs().method("getInitParameter").with(eq("key3"))
                .will(returnValue("value3"));

        PortletApplicationMap map = new PortletApplicationMap(portletContext);

        Set<String> keySet = map.keySet();
        assertEquals(3, keySet.size());
        assertTrue(keySet.contains("key1"));
        assertTrue(keySet.contains("key2"));
        assertTrue(keySet.contains("key3"));
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
        assertEquals("value3", map.get("key3"));

        // test iterator
        Set<String> expectedKeys = new HashSet<String>();
        expectedKeys.add("key1");
        expectedKeys.add("key2");
        expectedKeys.add("key3");

        Set<Entry<String,Object>> entrySet = map.entrySet();
        for (Entry<String,Object> entry : entrySet) {
            String key = entry.getKey();
            assertTrue(expectedKeys.contains(key));
        }

    }

    public void testClear() {

        mockPortletContext.expects(once()).method("removeAttribute").with(eq("key1"));
        mockPortletContext.expects(once()).method("removeAttribute").with(eq("key2"));

        ArrayList<String> dummy = new ArrayList<String>();
        dummy.add("key1");
        dummy.add("key2");

        mockPortletContext.expects(once()).method("getAttributeNames").will(
                returnValue(Collections.enumeration(dummy)));

        PortletApplicationMap map = new PortletApplicationMap(portletContext);
        map.clear();
    }
}
