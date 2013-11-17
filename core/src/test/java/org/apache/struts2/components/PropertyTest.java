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

import java.io.StringWriter;
import java.util.Map;

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.ValueStack;

/**
 *
 */
public class PropertyTest extends StrutsInternalTestCase {
    private XWorkConverter converter;

    public void setUp() throws Exception {
        super.setUp();
        converter = container.getInstance(XWorkConverter.class);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        converter = null;
    }
    public void testNormalBehaviour() {
        final ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new FooBar("foo-value", "bar-value"));
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("foo");
        assertPropertyOutput("foo-value", property);
    }

    public void testDefaultShouldBeOutputIfBeanNotAvailable() {
        final ValueStack stack = ActionContext.getContext().getValueStack();
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("foo");
        assertPropertyOutput("default", property);
    }

    public void testDefaultShouldBeOutputIfPropertyIsNull() {
        final ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new FooBar(null, "bar-value"));
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("foo");
        assertPropertyOutput("default", property);
    }

    public void testTopValueShouldReturnTopOfValueStack() {
        final ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new FooBar("foo-value", "bar-value"));
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("top");
        assertPropertyOutput("foo-value/bar-value", property);
    }

    public void testTypeConverterShouldBeUsed() {
        final ValueStack stack = ActionContext.getContext().getValueStack();
        converter.registerConverter("org.apache.struts2.components.PropertyTest$FooBar", new FooBarConverter());
        stack.push(new FooBar("foo-value", "bar-value"));
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("top");
        assertPropertyOutput("*foo-value + bar-value*", property);
    }

    public void testTypeConverterReturningNullShouldLeadToDisplayOfDefaultValue() {
        final ValueStack stack = ActionContext.getContext().getValueStack();
        converter.registerConverter("org.apache.struts2.components.PropertyTest$FooBar", new FooBarConverter());
        stack.push(new FooBar("foo-value", null));
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("top");
        assertPropertyOutput("default", property);
    }

    private static void assertPropertyOutput(String expectedOutput, Property property) {
        final StringWriter out = new StringWriter();
        assertTrue(property.start(out));
        assertEquals(expectedOutput, out.getBuffer().toString());
    }

    private final class FooBar {
        private String foo;
        private String bar;

        public FooBar(String foo, String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public String getFoo() {
            return foo;
        }

        public String getBar() {
            return bar;
        }

        public String toString() {
            return foo + "/" + bar;
        }
    }

    private final class FooBarConverter extends StrutsTypeConverter {
        public Object convertFromString(Map context, String[] values, Class toClass) {
            return null;
        }

        public String convertToString(Map context, Object o) {
            FooBar fooBar = (FooBar) o;
            if (fooBar.getBar() == null) {
                return null;
            } else {
                return "*" + fooBar.getFoo() + " + " + fooBar.getBar() + "*";
            }
        }
    }
}
