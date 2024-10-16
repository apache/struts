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

import org.apache.struts2.ActionContext;
import org.apache.struts2.test.User;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.ognl.ThreadAllowlist;
import org.apache.struts2.TestAction;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IteratorComponentTest extends StrutsInternalTestCase {

    private ValueStack stack;
    private IteratorComponent ic;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        stack = ActionContext.getContext().getValueStack();
        ic = new IteratorComponent(stack);
        ThreadAllowlist threadAllowlist = new ThreadAllowlist();
        ic.setThreadAllowlist(threadAllowlist);
    }

    public void testIterator() throws Exception {
        // given
        stack.push(new FooAction());

        StringWriter out = new StringWriter();

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

    public void testSimpleIterator() {
        // given
        stack.push(new FooAction());

        StringWriter out = new StringWriter();

        ic.setBegin("1");
        ic.setEnd("8");
        ic.setStep("2");
        ic.setStatus("status");

        Property prop = new Property(stack);
        Property status = new Property(stack);
        status.setValue("#status.index");

        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(status);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(status);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(status);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(status);

        String body = " ";

        // when
        assertTrue(ic.start(out));

        for (int i = 0; i < 4; i++) {
            status.start(out);
            status.end(out, body);
            prop.start(out);
            prop.end(out, body);
            ic.end(out, null);
        }

        // then
        assertEquals("0 1 1 3 2 5 3 7 ", out.getBuffer().toString());
    }

    public void testIteratorWithBegin() {
        // given
        stack.push(new FooAction());

        StringWriter out = new StringWriter();

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

    public void testIteratorWithNulls() {
        // given
        stack.push(new FooAction() {
            private final List<String> items = Arrays.asList("1", "2", null, "4");

            public List<String> getItems() {
                return items;
            }
        });

        StringWriter out = new StringWriter();

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

    public void testIteratorWithNullsOnly() {
        // given
        stack.push(new FooAction() {
            private final List<String> items = Arrays.asList(null, null, null);

            public List<String> getItems() {
                return items;
            }
        });

        StringWriter out = new StringWriter();

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

        for (int i = 0; i < 3; i++) {
            prop.start(out);
            prop.end(out, body);
            ic.end(out, null);
        }

        // then
        assertEquals(", , , ", out.getBuffer().toString());
    }

    public void testIteratorWithDifferentLocale() {
        // given
        ActionContext.getContext().withLocale(new Locale("fa_IR"));
        stack.push(new FooAction());

        StringWriter out = new StringWriter();

        ic.setBegin("1");
        ic.setEnd("3");
        ic.setStatus("status");

        Property prop = new Property(stack);
        Property status = new Property(stack);
        status.setValue("#status.count");

        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(status);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(status);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(status);

        String body = ",";

        // when
        assertTrue(ic.start(out));

        for (int i = 0; i < 3; i++) {
            status.start(out);
            status.end(out, body);

            prop.start(out);
            prop.end(out, body);
            ic.end(out, null);
        }

        // then
        assertEquals("1,1,2,2,3,3,", out.getBuffer().toString());
    }

    public void testListOfBeansIterator() {
        // given
        TestAction action = new TestAction();
        action.setList2(new ArrayList<User>() {{
            add(new User("Anton"));
            add(new User("Tym"));
            add(new User("Luk"));
        }});
        stack.push(action);

        StringWriter out = new StringWriter();

        ic.setValue("list2");
        ic.setStatus("status");

        Property prop = new Property(stack);
        prop.setValue("name");
        Property status = new Property(stack);
        status.setValue("#status.indexStr");

        ic.getComponentStack().push(status);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(status);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(status);
        ic.getComponentStack().push(prop);

        String body = ",";

        // when
        assertTrue(ic.start(out));

        for (int i = 0; i < 3; i++) {
            status.start(out);
            status.end(out, body);

            prop.start(out);
            prop.end(out, body);

            ic.end(out, null);
        }

        // then
        assertEquals("0,Anton,1,Tym,2,Luk,", out.getBuffer().toString());
    }

    public void testArrayOfBeansIterator() {
        // given
        TestAction action = new TestAction();
        action.setObjectArray(new ArrayList<User>() {{
            add(new User("Anton"));
            add(new User("Tym"));
            add(new User("Luk"));
        }}.toArray());
        stack.push(action);

        StringWriter out = new StringWriter();

        ic.setValue("objectArray");
        ic.setStatus("status");

        Property prop = new Property(stack);
        prop.setValue("name");
        Property status = new Property(stack);
        status.setValue("#status.countStr");

        ic.getComponentStack().push(status);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(status);
        ic.getComponentStack().push(prop);
        ic.getComponentStack().push(status);
        ic.getComponentStack().push(prop);

        String body = " ";

        // when
        assertTrue(ic.start(out));

        for (int i = 0; i < 3; i++) {
            status.start(out);
            status.end(out, body);

            prop.start(out);
            prop.end(out, body);

            ic.end(out, null);
        }

        // then
        assertEquals("1 Anton 2 Tym 3 Luk ", out.getBuffer().toString());
    }

    static class FooAction {

        private final List<String> items;

        public FooAction() {
            items = Arrays.asList("item1", "item2", "item3", "item4");
        }

        public List<String> getItems() {
            return items;
        }
    }
}
