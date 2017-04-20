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
