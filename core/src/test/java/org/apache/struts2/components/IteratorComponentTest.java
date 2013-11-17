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
