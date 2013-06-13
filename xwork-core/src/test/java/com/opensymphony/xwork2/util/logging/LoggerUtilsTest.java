package com.opensymphony.xwork2.util.logging;


import junit.framework.TestCase;

public class LoggerUtilsTest extends TestCase {

    public void testFormatMessage() {
        assertEquals("foo", LoggerUtils.format("foo"));
        assertEquals("foo #", LoggerUtils.format("foo #"));
        assertEquals("#foo", LoggerUtils.format("#foo"));
        assertEquals("foo #1", LoggerUtils.format("foo #1"));
        assertEquals("foo bob", LoggerUtils.format("foo #0", "bob"));
        assertEquals("foo bob joe", LoggerUtils.format("foo #0 #1", "bob", "joe"));
        assertEquals("foo bob joe #8", LoggerUtils.format("foo #0 #1 #8", "bob", "joe"));
        assertEquals("foo (bob/ally)", LoggerUtils.format("foo (#0/#1)", "bob", "ally"));
        assertEquals("foo (bobally)", LoggerUtils.format("foo (#0#1)", "bob", "ally"));

        assertEquals(null, LoggerUtils.format(null));
        assertEquals("", LoggerUtils.format(""));
        
    }

}
