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
package org.apache.struts2.views.jasperreports;

import org.apache.struts2.ActionContext;
import org.apache.struts2.junit.StrutsTestCase;
import org.apache.struts2.util.ValueStack;

import java.util.Map;

public class ValueStackShadowMapTest extends StrutsTestCase {

    private ValueStackShadowMap map;

    public void testFallsBackToValueStackThroughMapInterface() {
        Map<String, Object> asMap = map;

        assertTrue(asMap.containsKey("title"));
        assertEquals("Shadow", asMap.get("title"));
        assertFalse(asMap.containsKey("missing"));
        assertNull(asMap.get("missing"));
    }

    public void testExplicitEntryWinsOverValueStack() {
        map.put("title", "Explicit");
        map.put("other", null);

        assertEquals("Explicit", map.get((Object) "title"));
        assertTrue(map.containsKey((Object) "other"));
        assertNull(map.get((Object) "other"));
    }

    public void testNullKeyDoesNotReachValueStack() {
        assertFalse(map.containsKey((Object) null));
        assertNull(map.get((Object) null));
    }

    @SuppressWarnings("removal")
    public void testStringOverloadsDelegateToOverrides() {
        assertTrue(map.containsKey("title"));
        assertEquals("Shadow", map.get("title"));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new Object() {
            public String getTitle() {
                return "Shadow";
            }
        });
        map = new ValueStackShadowMap(stack);
    }
}
