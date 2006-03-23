/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.util;

import junit.framework.TestCase;

/**
 * Unit test for {@link TextUtil}.
 *
 * @author Claus Ibsen
 */
public class TextUtilTest extends TestCase {

    private static char EURO_SIGN = 0x20AC;

    public void testEscape() throws Exception {
        assertEquals("", TextUtil.escapeHTML(""));
        assertEquals("   ", TextUtil.escapeHTML("   "));

        assertEquals("Hello World", TextUtil.escapeHTML("Hello World"));
        assertEquals("Hello &amp; World", TextUtil.escapeHTML("Hello & World"));

        assertEquals("Cost is 1999&euro; and this is cheap", TextUtil.escapeHTML("Cost is 1999" + EURO_SIGN + " and this is cheap"));

        assertEquals("Now some &lt;&gt; and &lt; - &gt; and we have &lt;/ and /&gt;", TextUtil.escapeHTML("Now some <> and < - > and we have </ and />"));
        assertEquals("&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;", TextUtil.escapeHTML("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
    }

    public void testEscapeEmpty() throws Exception {
        assertEquals("", TextUtil.escapeHTML("", true));
        assertEquals("   ", TextUtil.escapeHTML("   ", true));

        assertEquals("Hello World", TextUtil.escapeHTML("Hello World", true));
        assertEquals("Hello &amp; World", TextUtil.escapeHTML("Hello & World", true));

        assertEquals("Cost is 1999&euro; and this is cheap", TextUtil.escapeHTML("Cost is 1999" + EURO_SIGN + " and this is cheap", true));

        assertEquals("Now some &lt;&gt; and &lt; - &gt; and we have &lt;/ and /&gt;", TextUtil.escapeHTML("Now some <> and < - > and we have </ and />", true));
        assertEquals("&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;", TextUtil.escapeHTML("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", true));
    }

    public void testLongText() throws Exception {
        // TextUtil behaves special internally for long texts 
        String s = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                   "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                   "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890 and now s" +
                   "ome < that should be escaped. But this text is to long (> 300)";
        String res = TextUtil.escapeHTML(s);
        assertEquals(368, res.length());
        assertTrue(res.indexOf("<") == -1);
        assertTrue(res.indexOf(">") == -1);
    }

}
