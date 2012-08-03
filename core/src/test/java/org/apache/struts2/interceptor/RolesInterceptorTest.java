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

package org.apache.struts2.interceptor;

import java.util.List;

import org.apache.struts2.StrutsTestCase;

import com.mockobjects.servlet.MockHttpServletRequest;
import com.mockobjects.servlet.MockHttpServletResponse;

public class RolesInterceptorTest extends StrutsTestCase {

    private RolesInterceptor interceptor = new RolesInterceptor();

    public void setUp() throws Exception {
        super.setUp();
        interceptor = new RolesInterceptor();
    }

    public void testStringToList() {
        List list = interceptor.stringToList("foo");
        assertNotNull(list);
        assertEquals(1, list.size());

        list = interceptor.stringToList("foo,bar");
        assertEquals(2, list.size());
        assertEquals("foo", (String)list.get(0));

        list = interceptor.stringToList("foo, bar");
        assertEquals(2, list.size());
        assertEquals("bar", (String)list.get(1));

        list = interceptor.stringToList("foo  , bar");
        assertEquals(2, list.size());
        assertEquals("bar", (String)list.get(1));
    }

    public void testIsAllowed() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest() {
            public boolean isUserInRole(String role) {
                return "admin".equals(role);
            }
        };
        interceptor.setAllowedRoles("admin");
        assertTrue(interceptor.isAllowed(request, null));

        interceptor.setAllowedRoles("bar, admin");
        assertTrue(interceptor.isAllowed(request, null));

        interceptor.setAllowedRoles(null);
        assertTrue(interceptor.isAllowed(request, null));

        interceptor.setDisallowedRoles("bar");
        assertTrue(interceptor.isAllowed(request, null));

        interceptor.setDisallowedRoles("bar, admin");
        assertTrue(!interceptor.isAllowed(request, null));

    }

    public void testHandleRejection() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setExpectedError(response.SC_FORBIDDEN);
        interceptor.handleRejection(null, response);
        response.verify();
    }
}
