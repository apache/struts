package mailreader2;

import junit.framework.TestCase;

import java.util.List;

public class BaseAppDataTest extends TestCase {

    protected void assertNotEmpty(String value) {
        assertNotNull(value);
        assertTrue(value.length()>0);
    }

    protected void assertNotEmpty(String message, String value, int min, int max) {
        assertNotNull(message,value);
        assertTrue(message,value.length()>=min);
        assertTrue(message,value.length()<=max);
     }

    protected void assertNotEmpty(String message, String value) {
        assertNotEmpty(message,value,0,1024);
    }

    protected void assertLocale_list(List list) {
        assertNotNull(list);
        assertTrue("Expected one or more locales",list.size()>0);
        AppData entry = null;
        try {
            entry = (AppData) list.get(0);
        } catch (Throwable t) {
            fail("Expected a list of AppData objects");
        }
        assertNotEmpty("Expected a locale code",entry.getLocale_code(),2,2);
        assertNotEmpty("Expected a locale name",entry.getLocale_name());
        assertNotEmpty("Expected a locale kehy",entry.getLocale_key());
    }
}
