package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.StrutsInternalTestCase;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StringConverterTest extends StrutsInternalTestCase {

    public void testIntegerToStringConversionPL() throws Exception {
        // given
        StringConverter converter = new StringConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, Integer.MIN_VALUE, null);

        // then
        assertEquals("" + Integer.MIN_VALUE, value);
    }

    public void testDoubleToStringConversionPL() throws Exception {
        // given
        StringConverter converter = new StringConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when has max fraction digits
        Object value = converter.convertValue(context, null, null, null, Math.PI, null);

        // then does not lose fraction digits
        assertEquals("3,141592653589793", value);

        // when has max integer digits
        value = converter.convertValue(context, null, null, null, Double.MAX_VALUE, null);

        // then does not lose integer digits
        assertEquals(String.format("%.0f", Double.MAX_VALUE), value);
    }

    public void testFloatToStringConversionPL() throws Exception {
        // given
        StringConverter converter = new StringConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when has max fraction digits
        Object value = converter.convertValue(context, null, null, null, ((Double)Math.PI).floatValue(), null);

        // then does not lose fraction digits
        assertEquals("3,1415927", value);

        // when has max integer digits
        value = converter.convertValue(context, null, null, null, Float.MAX_VALUE, null);

        // then does not lose integer digits
        assertEquals(String.format("%.0f", Float.MAX_VALUE), value);
    }

    public void testBigDecimalToStringConversionPL() throws Exception {
        // given
        StringConverter converter = new StringConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when a bit bigger than double (310 integer and 17 fraction digits)
        BigDecimal bd = new BigDecimal(String.format("%.0f", Double.MAX_VALUE) + "1.00000000000000001");
        Object value = converter.convertValue(context, null, null, null, bd, null);

        // then does not lose integer and fraction digits
        assertEquals(String.format("%.0f", Double.MAX_VALUE) + "1,00000000000000001", value);
    }

    public void testStringArrayToStringConversion() {
        // given
        StringConverter converter = new StringConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, new String[] {"foo", "baz"}, null);

        // then
        assertEquals("foo, baz", value);
   }

    public void testArrayOfNullToStringConversion() {
        // given
        StringConverter converter = new StringConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, new String[] {null}, null);

        // then
        assertEquals("", value);
   }

}
