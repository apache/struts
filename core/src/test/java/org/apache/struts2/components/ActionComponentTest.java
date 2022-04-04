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

package org.apache.struts2.components;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.StrutsInternalTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.util.ValueStack;

public class ActionComponentTest extends StrutsInternalTestCase {

    public void testCreateParametersForContext() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Mock mockValueStack = new Mock(ValueStack.class);
        HashMap ctx = new HashMap();
        mockValueStack.expectAndReturn("getContext", ctx);
        mockValueStack.expectAndReturn("getContext", ctx);
        mockValueStack.expectAndReturn("getContext", ctx);
        
        ActionComponent comp = new ActionComponent((ValueStack) mockValueStack.proxy(), req, res);
        comp.addParameter("foo", "bar");
        comp.addParameter("baz", new String[]{"jim", "sarah"});
        Map params = comp.createParametersForContext();
        assertNotNull(params);
        assertEquals(2, params.size());
        assertEquals("bar", ((String[])params.get("foo"))[0]);
        assertEquals(2, ((String[])params.get("baz")).length);
        mockValueStack.verify();
    }
}
