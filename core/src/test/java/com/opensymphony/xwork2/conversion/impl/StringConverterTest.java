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
        Object value = converter.convertValue(context, null, null, null, 234, null);

        // then
        assertEquals("234", value);
    }

    public void testDoubleToStringConversionPL() throws Exception {
        // given
        StringConverter converter = new StringConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, 234.12, null);

        // then
        assertEquals("234,12", value);
    }

    public void testBigDecimalToStringConversionPL() throws Exception {
        // given
        StringConverter converter = new StringConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, BigDecimal.valueOf(234.12), null);

        // then
        assertEquals("234,12", value);
    }

}