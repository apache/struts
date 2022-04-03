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
package org.apache.struts2.interceptor;


import java.util.HashMap;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.HttpParameters;

/**
 * Unit test for MultiselectInterceptor.
 */
public class MultiselectInterceptorTest extends StrutsInternalTestCase {

    private MultiselectInterceptor interceptor;
    private MockActionInvocation ai;
    private Map<String, Object> param;

    protected void setUp() throws Exception {
        super.setUp();
        param = new HashMap<>();

        interceptor = new MultiselectInterceptor();
        ai = new MockActionInvocation();
        ai.setInvocationContext(ActionContext.getContext());
        ActionContext.getContext().setParameters(HttpParameters.create(param).build());
    }

    public void testNoParam() throws Exception {
        interceptor.init();
        interceptor.intercept(ai);
        interceptor.destroy();

        assertEquals(0, param.size());
    }

    public void testPassthroughOne() throws Exception {
        param.put("user", "batman");
        interceptor.init();
        interceptor.intercept(ai);
        interceptor.destroy();

        assertEquals(1, param.size());
    }

    public void testPassthroughTwo() throws Exception {
        param.put("user", "batman");
        param.put("email", "batman@comic.org");
        interceptor.init();
        interceptor.intercept(ai);
        interceptor.destroy();

        assertEquals(2, param.size());
    }

    public void testSelectedMultiselect() throws Exception {
        param.put("user", "batman");
        param.put("email", "batman@comic.org");
        param.put("superpower", "robin");
        param.put("__multiselect_superpower", "");
        assertTrue(param.containsKey("__multiselect_superpower"));

        ai.getInvocationContext().setParameters(HttpParameters.create(param).build());

        interceptor.init();
        interceptor.intercept(ai);
        interceptor.destroy();

        HttpParameters parameters = ai.getInvocationContext().getParameters();
        assertFalse(parameters.contains("__multiselect_superpower"));
        assertEquals(3, parameters.keySet().size()); // should be 3 as __multiselect_ should be removed
        assertEquals("robin", parameters.get("superpower").getValue());
    }

    public void testMultiselectNoValue() throws Exception {
        param.put("user", "batman");
        param.put("email", "batman@comic.org");
        param.put("__multiselect_superpower", "");
        assertTrue(param.containsKey("__multiselect_superpower"));

        ai.getInvocationContext().setParameters(HttpParameters.create(param).build());

        interceptor.init();
        interceptor.intercept(ai);
        interceptor.destroy();

        HttpParameters parameters = ai.getInvocationContext().getParameters();
        assertFalse(parameters.contains("__multiselect_superpower"));
        assertEquals(3, parameters.keySet().size()); // should be 3 as __multiselect_ should be removed
        assertFalse(parameters.get("superpower").isDefined());
    }

    public void testTwoMultiselect() throws Exception {
        param.put("user", "batman");
        param.put("email", "batman@comic.org");
        param.put("__multiselect_superpower", "");
        param.put("superpower", "yes");
        param.put("__multiselect_cool", "");
        assertTrue(param.containsKey("__multiselect_superpower"));
        assertTrue(param.containsKey("__multiselect_cool"));

        ai.getInvocationContext().setParameters(HttpParameters.create(param).build());

        interceptor.init();
        interceptor.intercept(ai);
        interceptor.destroy();

        HttpParameters parameters = ai.getInvocationContext().getParameters();
        assertFalse(parameters.contains("__multiselect_superpower"));
        assertFalse(parameters.contains("__multiselect_cool"));
        assertEquals(4, parameters.keySet().size()); // should be 4 as __multiselect_ should be removed
        assertEquals("yes", parameters.get("superpower").getValue());
        assertFalse(parameters.get("cool").isDefined());
    }

}
