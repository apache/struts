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
package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsInternalTestCase;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class IteratorComponentTest extends StrutsInternalTestCase {

    public void testIterator() throws Exception {
        // given
        final ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new FooAction());

        StringWriter out = new StringWriter();

        IteratorComponent ic = new IteratorComponent(stack);
        ic.setValue("items");
        ic.setVar("val");

        Property prop = new Property(stack);

        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(prop);

        String body = " ";

        // when
        assertTrue(ic.start(out));


        for (int i = 0; i < 4; i++) {
            prop.start(out);
            prop.end(out, body);
            ic.end(out, null);
        }

        // then
        assertEquals("item1 item2 item3 item4 ", out.getBuffer().toString());
    }

    public void testIteratorWithBegin() throws Exception {
        // given
        final ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new FooAction());

        StringWriter out = new StringWriter();

        IteratorComponent ic = new IteratorComponent(stack);
        ic.setValue("items");
        ic.setVar("val");
        ic.setBegin("1");
        Property prop = new Property(stack);

        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(prop);

        String body = " ";

        // when
        assertTrue(ic.start(out));

        for (int i = 0; i < 3; i++) {
            prop.start(out);
            prop.end(out, body);
            ic.end(out, null);
        }

        // then
        assertEquals("item2 item3 item4 ", out.getBuffer().toString());
    }

    public void testIteratorWithNulls() throws Exception {
        // given
        final ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new FooAction() {
            private List items  = Arrays.asList("1", "2", null, "4");

            public List getItems() {
                return items;
            }
        });

        StringWriter out = new StringWriter();

        IteratorComponent ic = new IteratorComponent(stack);
        ic.setValue("items");
        ic.setVar("val");
        Property prop = new Property(stack);

        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(prop);

        String body = ", ";

        // when
        assertTrue(ic.start(out));

        for (int i = 0; i < 4; i++) {
            prop.start(out);
            prop.end(out, body);
            ic.end(out, null);
        }

        // then
        assertEquals("1, 2, , 4, ", out.getBuffer().toString());
    }

    static class FooAction {

        private List items;

        public FooAction() {
            items = Arrays.asList("item1", "item2", "item3", "item4");
        }

        public List getItems() {
            return items;
        }
    }
}
