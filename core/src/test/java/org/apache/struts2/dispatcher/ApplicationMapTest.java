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
import org.springframework.mock.web.MockServletContext;

import jakarta.servlet.ServletContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ApplicationMapTest {

    @Test
    public void shouldRetrieveAttribute() {
        // given
        ServletContext context = new MockServletContext();
        context.setAttribute("attr", "value");

        // when
        ApplicationMap am = new ApplicationMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);
    }

    @Test
    public void shouldReturnNullIfKeyIsNull() {
        // given
        ServletContext context = new MockServletContext();

        // when
        ApplicationMap am = new ApplicationMap(context);
        Object value = am.get(null);

        // then
        assertNull(value);
    }

    @Test
    public void shouldRemoveAttributeFromServletContext() {
        // given
        ServletContext context = new MockServletContext();
        context.setAttribute("attr", "value");

        // when
        ApplicationMap am = new ApplicationMap(context);
        Object value = am.remove("attr");

        // then
        assertEquals("value", value);
        assertNull(context.getAttribute("attr"));
    }

    @Test
    public void shouldClearAttributes() {
        // given
        ServletContext context = new MockServletContext();
        context.setAttribute("attr", "value");

        // when
        ApplicationMap am = new ApplicationMap(context);
        Object value = am.get("attr");

        // then
        assertEquals("value", value);

        // when
        am.clear();

        // then
        assertNull(context.getAttribute("attr"));
    }

}
