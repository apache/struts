package org.apache.struts.action2.attribute;

import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;

/**
 * @author crazybob@google.com (Bob Lee)
 */
public class ReferenceTest extends TestCase {

    public void testSetGetRemove() {
        Map<String, Object> m = new HashMap<String, Object>();
        Attribute<String> a = new MockAttribute<String>(m, "key");

        assertNull(a.get());
        assertTrue(m.isEmpty());
        a.set("foo");
        assertEquals("foo", a.get());
        assertFalse(m.isEmpty());
        a.remove();
        assertNull(a.get());
        assertTrue(m.isEmpty());
    }

    public void testInitialValue() {
        Map<String, Object> m = new HashMap<String, Object>();
        Attribute<String> a = new MockAttribute<String>(m, "key") {
            protected String initialValue() {
                return "foo";
            }
        };

        getAndRemove(a, m);
        getAndRemove(a, m);
    }

    void getAndRemove(Attribute<String> a, Map<String, Object> m) {
        assertEquals("foo", a.get());
        assertFalse(m.isEmpty());
        a.remove();
        assertTrue(m.isEmpty());
    }

    static class MockAttribute<T> extends AbstractAttribute<T> {

        Map<String, Object> map;

        MockAttribute(Map<String, Object> map, String key) {
            super(key);
            this.map = map;
        }

        T execute(UnitOfWork<T> unitOfWork) {
            return unitOfWork.execute(map);
        }
    }
}
