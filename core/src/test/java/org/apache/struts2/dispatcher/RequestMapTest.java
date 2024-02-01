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

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RequestMapTest {

    @Test
    public void shouldRetrieveRequestAttribute() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("attr", "value");

        // when
        RequestMap rm = new RequestMap(request);
        Object value = rm.get("attr");

        // then
        assertEquals("value", value);
    }

    @Test
    public void shouldReturnNullIfKeyIsNull() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();

        // when
        RequestMap rm = new RequestMap(request);
        Object value = rm.get(null);

        // then
        assertNull(value);
    }

    @Test
    public void shouldRemoveAttributeFromRequest() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("attr", "value");

        // when
        RequestMap rm = new RequestMap(request);
        Object value = rm.remove("attr");

        // then
        assertEquals("value", value);
        assertNull(request.getAttribute("attr"));
    }

    @Test
    public void shouldClearAttributes() {
        // given
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("attr", "value");

        // when
        RequestMap rm = new RequestMap(request);
        Object value = rm.get("attr");

        // then
        assertEquals("value", value);

        // when
        rm.clear();

        // then
        assertNull(request.getAttribute("attr"));
    }

}
