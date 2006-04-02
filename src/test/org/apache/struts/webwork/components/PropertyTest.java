package org.apache.struts.webwork.components;

import org.apache.struts.webwork.components.Property;
import org.apache.struts.webwork.util.StrutsTypeConverter;
import com.opensymphony.xwork.util.OgnlValueStack;
import com.opensymphony.xwork.util.XWorkConverter;
import junit.framework.TestCase;
import ognl.Ognl;
 
import java.io.StringWriter;
import java.util.Map;
 
/**
 *
 * @author gjoseph
 * @author $Author: tmjee $ (last edit)
 * @version $Revision: 1.1 $
 */
public class PropertyTest extends TestCase {
    public void testNormalBehaviour() {
        final OgnlValueStack stack = new OgnlValueStack();
        stack.push(new FooBar("foo-value", "bar-value"));
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("foo");
        assertPropertyOutput("foo-value", property);
    }
 
    public void testDefaultShouldBeOutputIfBeanNotAvailable() {
        final OgnlValueStack stack = new OgnlValueStack();
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("foo");
        assertPropertyOutput("default", property);
    }
 
    public void testDefaultShouldBeOutputIfPropertyIsNull() {
        final OgnlValueStack stack = new OgnlValueStack();
        stack.push(new FooBar(null, "bar-value"));
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("foo");
        assertPropertyOutput("default", property);
    }
 
    public void testTopValueShouldReturnTopOfValueStack() {
        final OgnlValueStack stack = new OgnlValueStack();
        stack.push(new FooBar("foo-value", "bar-value"));
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("top");
        assertPropertyOutput("foo-value/bar-value", property);
    }
 
    public void testTypeConverterShouldBeUsed() {
        final OgnlValueStack stack = new OgnlValueStack();
        Ognl.setTypeConverter(stack.getContext(), new TestDefaultConverter());
 
        stack.push(new FooBar("foo-value", "bar-value"));
        final Property property = new Property(stack);
        property.setDefault("default");
        property.setValue("top");
        assertPropertyOutput("*foo-value + bar-value*", property);
    }
 
    public void testTypeConverterReturningNullShouldLeadToDisplayOfDefaultValue() {
        final OgnlValueStack stack = new OgnlValueStack();
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
            registerConverter("org.apache.struts.action2.components.PropertyTest$FooBar", new FooBarConverter());
        }
    }
}
