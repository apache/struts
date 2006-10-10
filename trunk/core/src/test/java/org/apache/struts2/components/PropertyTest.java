/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.components;

import java.io.StringWriter;
import java.util.Map;

import junit.framework.TestCase;
import ognl.Ognl;

import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.XWorkConverter;
 
/**
 *
 */
public class PropertyTest extends TestCase {
    public void testNormalBehaviour() {
        final ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        stack.push(new FooBar("foo-value", "bar-value"));
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("foo");
        assertPropertyOutput("foo-value", property);
    }
 
    public void testDefaultShouldBeOutputIfBeanNotAvailable() {
        final ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("foo");
        assertPropertyOutput("default", property);
    }
 
    public void testDefaultShouldBeOutputIfPropertyIsNull() {
        final ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        stack.push(new FooBar(null, "bar-value"));
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("foo");
        assertPropertyOutput("default", property);
    }
 
    public void testTopValueShouldReturnTopOfValueStack() {
        final ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        stack.push(new FooBar("foo-value", "bar-value"));
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("top");
        assertPropertyOutput("foo-value/bar-value", property);
    }
 
    public void testTypeConverterShouldBeUsed() {
        final ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        Ognl.setTypeConverter(stack.getContext(), new TestDefaultConverter());
 
        stack.push(new FooBar("foo-value", "bar-value"));
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("top");
        assertPropertyOutput("*foo-value + bar-value*", property);
    }
 
    public void testTypeConverterReturningNullShouldLeadToDisplayOfDefaultValue() {
        final ValueStack stack = ValueStackFactory.getFactory().createValueStack();
        Ognl.setTypeConverter(stack.getContext(), new TestDefaultConverter());
 
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
 
    /** a simple hack to simply register a custom converter in our test */
    private final class TestDefaultConverter extends XWorkConverter {
        protected TestDefaultConverter() {
            super();
            registerConverter("org.apache.struts2.components.PropertyTest$FooBar", new FooBarConverter());
        }
    }
}
