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
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.Element;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;

import java.util.Collections;
import java.util.Map;

public class XWorkMapPropertyAccessorTest extends XWorkTestCase {
    public void testCreateNullObjectsIsFalseByDefault() {
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(new MapHolder(Collections.emptyMap()));
        assertNull(vs.findValue("map[key]"));
    }

    public void testMapContentsAreReturned() {
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(new MapHolder(Collections.singletonMap("key", "value")));
        assertEquals("value", vs.findValue("map['key']"));
    }

    public void testNullIsNotReturnedWhenCreateNullObjectsIsSpecified() {
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(new MapHolder(Collections.emptyMap()));
        ReflectionContextState.setCreatingNullObjects(vs.getContext(), true);

        Object value = vs.findValue("map['key']");
        assertNotNull(value);
        assertSame(Object.class, value.getClass());
    }

    public void testNullIsReturnedWhenCreateNullObjectsIsSpecifiedAsFalse() {
        ValueStack vs = ActionContext.getContext().getValueStack();
        vs.push(new MapHolder(Collections.emptyMap()));
        ReflectionContextState.setCreatingNullObjects(vs.getContext(), false);
        assertNull(vs.findValue("map['key']"));
    }

    private static class MapHolder {
        private final Map map;

        public MapHolder(Map m) {
            this.map = m;
        }

        @Element(value = Object.class)
        public Map getMap() {
            return map;
        }
    }
}
