package org.apache.struts2.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class URLDecoderUtilTest {

    @Test
    public void testURLDecodeStringInvalid() {
        // %n rather than %nn should throw an IAE according to the Javadoc
        Exception exception = null;
        try {
            URLDecoderUtil.decode("%5xxxxx", "ISO-8859-1");
        } catch (Exception e) {
            exception = e;
        }
        assertTrue(exception instanceof IllegalArgumentException);

        // Edge case trying to trigger ArrayIndexOutOfBoundsException
        exception = null;
        try {
            URLDecoderUtil.decode("%5", "ISO-8859-1");
        } catch (Exception e) {
            exception = e;
        }
        assertTrue(exception instanceof IllegalArgumentException);
    }

    @Test
    public void testURLDecodeStringValidIso88591Start() {

        String result = URLDecoderUtil.decode("%41xxxx", "ISO-8859-1");
        assertEquals("Axxxx", result);
    }

    @Test
    public void testURLDecodeStringValidIso88591Middle() {

        String result = URLDecoderUtil.decode("xx%41xx", "ISO-8859-1");
        assertEquals("xxAxx", result);
    }

    @Test
    public void testURLDecodeStringValidIso88591End() {

        String result = URLDecoderUtil.decode("xxxx%41", "ISO-8859-1");
        assertEquals("xxxxA", result);
    }

    @Test
    public void testURLDecodeStringValidUtf8Start() {
        String result = URLDecoderUtil.decode("%c3%aaxxxx", "UTF-8");
        assertEquals("\u00eaxxxx", result);
    }

    @Test
    public void testURLDecodeStringValidUtf8Middle() {

        String result = URLDecoderUtil.decode("xx%c3%aaxx", "UTF-8");
        assertEquals("xx\u00eaxx", result);
    }

    @Test
    public void testURLDecodeStringValidUtf8End() {

        String result = URLDecoderUtil.decode("xxxx%c3%aa", "UTF-8");
        assertEquals("xxxx\u00ea", result);
    }

    @Test
    public void testURLDecodePlusCharAsSpace() {

        String result = URLDecoderUtil.decode("a+b", "UTF-8", true);
        assertEquals("a b", result);
    }

}