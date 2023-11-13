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
package org.apache.struts2.dispatcher;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.StrutsStatics;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.PageContext;

public class AttributeMapTest {

    @Test
    public void shouldRetrievePageContextAttribute() {
        // given
        PageContext pc = new MockPageContext();
        pc.setAttribute("attr", "value");

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(StrutsStatics.PAGE_CONTEXT, pc);
        }};

        // when
        AttributeMap am = new AttributeMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);
    }

    @Test
    public void shouldPutAttribute() {
        // given
        PageContext pc = new MockPageContext();

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(StrutsStatics.PAGE_CONTEXT, pc);
        }};

        // when
        AttributeMap am = new AttributeMap(context);
        Object value = am.put("attr", "value");

        // then
        assertEquals("value", value);
        assertEquals("value", pc.getAttribute("attr"));
    }

    @Test
    public void shouldRetrieveRequestAttribute() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("attr", "value");

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(DispatcherConstants.REQUEST, new RequestMap(request));
        }};

        // when
        AttributeMap am = new AttributeMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);
    }

    @Test
    public void shouldRetrieveSessionAttribute() {
        // given
        HttpSession session = new MockHttpSession();
        session.setAttribute("attr", "value");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(DispatcherConstants.SESSION, new SessionMap(request));
        }};

        // when
        AttributeMap am = new AttributeMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);
    }

    @Test
    public void shouldRetrieveApplicationAttribute() {
        // given
        ServletContext sc = new MockServletContext();
        sc.setAttribute("attr", "value");

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(DispatcherConstants.APPLICATION, new ApplicationMap(sc));
        }};

        // when
        AttributeMap am = new AttributeMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);
    }

    @Test
    public void shouldReturnNullIfKeyIsNull() {
        // given
        // when
        AttributeMap am = new AttributeMap(new HashMap<>());
        Object value = am.get(null);

        // then
        assertNull(value);
    }

    @Test
    public void shouldThrowExceptionOnRemove() {
        // given
        PageContext pc = new MockPageContext();
        pc.setAttribute("attr", "value");

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(StrutsStatics.PAGE_CONTEXT, pc);
        }};

        // when
        AttributeMap am = new AttributeMap(context);

        // then
        Object value = am.get("attr");
        assertEquals("value", value);

        assertThrows(UnsupportedOperationException.class, () -> am.remove("attr"));
    }

    @Test
    public void shouldThrowExceptionOnClear() {
        // given
        PageContext pc = new MockPageContext();
        pc.setAttribute("attr", "value");

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(StrutsStatics.PAGE_CONTEXT, pc);
        }};

        // when
        AttributeMap am = new AttributeMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);

        // when
        assertThrows(UnsupportedOperationException.class, am::clear);
    }

    @Test
    public void shouldThrowExceptionOnIsEmpty() {
        // given
        PageContext pc = new MockPageContext();
        pc.setAttribute("attr", "value");

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(StrutsStatics.PAGE_CONTEXT, pc);
        }};

        // when
        AttributeMap am = new AttributeMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);

        // when
        assertThrows(UnsupportedOperationException.class, am::isEmpty);
    }

    @Test
    public void shouldThrowExceptionOnContainsValue() {
        // given
        PageContext pc = new MockPageContext();
        pc.setAttribute("attr", "value");

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(StrutsStatics.PAGE_CONTEXT, pc);
        }};

        // when
        AttributeMap am = new AttributeMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);

        // when
        assertThrows(UnsupportedOperationException.class, () -> am.containsValue("attr"));
    }

    @Test
    public void shouldThrowExceptionOnPutAll() {
        // given
        PageContext pc = new MockPageContext();
        pc.setAttribute("attr", "value");

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(StrutsStatics.PAGE_CONTEXT, pc);
        }};

        Map<String, Object> values = Collections.emptyMap();

        // when
        AttributeMap am = new AttributeMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);

        // when
        assertThrows(UnsupportedOperationException.class, () -> am.putAll(values));
    }

    @Test
    public void shouldThrowExceptionOnKeySet() {
        // given
        PageContext pc = new MockPageContext();
        pc.setAttribute("attr", "value");

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(StrutsStatics.PAGE_CONTEXT, pc);
        }};

        // when
        AttributeMap am = new AttributeMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);

        // when
        assertEquals(am.keySet(), context.keySet());
    }

    @Test
    public void shouldThrowExceptionOnSize() {
        // given
        PageContext pc = new MockPageContext();
        pc.setAttribute("attr", "value");

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(StrutsStatics.PAGE_CONTEXT, pc);
        }};

        // when
        AttributeMap am = new AttributeMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);

        // when
        assertThrows(UnsupportedOperationException.class, am::size);
    }

    @Test
    public void shouldGetAllValues() {
        // given
        PageContext pc = new MockPageContext();
        pc.setAttribute("attr", "value");

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(StrutsStatics.PAGE_CONTEXT, pc);
        }};

        // when
        AttributeMap am = new AttributeMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);

        // when
        Collection<Object> values = am.values();

        // then
        assertThat(values, hasItem(pc));
    }

    @Test
    public void shouldGetEntrySet() {
        // given
        PageContext pc = new MockPageContext();
        pc.setAttribute("attr", "value");

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(StrutsStatics.PAGE_CONTEXT, pc);
        }};

        // when
        AttributeMap am = new AttributeMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);

        // then
        assertEquals(context.entrySet(), am.entrySet());
    }

    @Test
    public void shouldContainsKey() {
        // given
        PageContext pc = new MockPageContext();
        pc.setAttribute("attr", "value");

        Map<String, Object> context = new HashMap<String, Object>() {{
            put(StrutsStatics.PAGE_CONTEXT, pc);
        }};

        // when
        AttributeMap am = new AttributeMap(context);

        // then
        assertTrue(am.containsKey("attr"));

        Object value = am.get("attr");
        assertEquals("value", value);
    }

}