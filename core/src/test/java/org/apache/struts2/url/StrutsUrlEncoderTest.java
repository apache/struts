package org.apache.struts2.url;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StrutsUrlEncoderTest {

    private StrutsUrlEncoder encoder;

    @Test
    public void testEncodeValidIso88591Start() {
        String result = encoder.encode("%xxxx", "ISO-8859-1");
        assertEquals("%25xxxx", result);
    }

    @Test
    public void testEncodeValidIso88591Middle() {
        String result = encoder.encode("xx%xx", "ISO-8859-1");
        assertEquals("xx%25xx", result);
    }

    @Test
    public void testEncodeValidIso88591End() {
        String result = encoder.encode("xxxx%", "ISO-8859-1");
        assertEquals("xxxx%25", result);
    }

    @Test
    public void testEncodeValidUtf8Start() {
        String result = encoder.encode("\u00eaxxxx", "UTF-8");
        assertEquals("%C3%AAxxxx", result);
    }

    @Test
    public void testEncodeValidUtf8Middle() {
        String result = encoder.encode("xx\u00eaxx", "UTF-8");
        assertEquals("xx%C3%AAxx", result);
    }

    @Test
    public void testEncodeValidUtf8End() {
        String result = encoder.encode("xxxx\u00ea", "UTF-8");
        assertEquals("xxxx%C3%AA", result);
    }

    @Test
    public void testEncodePlusCharAsSpace() {
        String result = encoder.encode("a b", "UTF-8");
        assertEquals("a+b", result);
    }

    @Before
    public void setUp() throws Exception {
        this.encoder = new StrutsUrlEncoder();
    }

}
