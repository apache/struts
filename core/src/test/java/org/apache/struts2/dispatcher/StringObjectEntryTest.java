package org.apache.struts2.dispatcher;

import static org.junit.Assert.assertNotEquals;

import junit.framework.TestCase;

public class StringObjectEntryTest extends TestCase {
    public void testGetKey() {
        StringObjectEntry entry = new StringObjectEntryTestImpl("theKey", "theValue");
        assertEquals("theKey", entry.getKey());
    }
    
    public void testGetValue() {
        StringObjectEntry entry = new StringObjectEntryTestImpl("theKey", "theValue");
        assertEquals("theValue", entry.getValue());
    }
    
    public void testEquals() {
        StringObjectEntry entry = new StringObjectEntryTestImpl("theKey", "theValue");
        
        assertEquals(entry, new StringObjectEntryTestImpl("theKey", "theValue"));
        assertNotEquals(entry, new StringObjectEntryTestImpl("theKey", "differentValue"));
        assertNotEquals(entry, new StringObjectEntryTestImpl("differentKey", "theValue"));
        assertNotEquals(entry, new StringObjectEntryTestImpl("differentKey", "differentValue"));
    }
    
    public void testHashCode() {
        StringObjectEntry entry = new StringObjectEntryTestImpl("theKey", "theValue");
        assertEquals(-1962296402, entry.hashCode());
    }
    
    static class StringObjectEntryTestImpl extends StringObjectEntry {
        StringObjectEntryTestImpl(final String key, final Object value) {
            super(key, value);
        }

        @Override
        public Object setValue(final Object value) {
            return value;
        }
        
    }
}
