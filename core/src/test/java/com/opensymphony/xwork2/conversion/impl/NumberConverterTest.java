package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.SimpleFooAction;
import com.opensymphony.xwork2.XWorkTestCase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NumberConverterTest extends XWorkTestCase {

    public void testStringToNumberConversionPL() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        SimpleFooAction foo = new SimpleFooAction();

        // when
        Object value = converter.convertValue(context, foo, null, "id", "1234", Integer.class);

        // then
        assertEquals(1234, value);
    }

    public void testStringToNumberConversionUS() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("en", "US"));

        SimpleFooAction foo = new SimpleFooAction();

        // when
        Object value = converter.convertValue(context, foo, null, "id", ",1234", Integer.class);

        // then
        assertEquals(1234, value);
    }

    public void testStringToBigDecimalConversionPL() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, "1234,4567", BigDecimal.class);

        // then
        assertEquals(BigDecimal.valueOf(1234.4567), value);
    }

    public void testStringToBigDecimalConversionWithDotsPL() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, "1 234,4", BigDecimal.class);

        // then
        assertEquals(BigDecimal.valueOf(1234.4), value);
    }
    
    public void testStringToBigDecimalConversionWithCommasEN() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("en", "US"));

        // when
        Object value = converter.convertValue(context, null, null, null, "100,234.4", BigDecimal.class);

        // then
        assertEquals(BigDecimal.valueOf(100234.4), value);
    }

    public void testStringToDoubleConversionPL() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, "1234,4567", Double.class);

        // then
        assertEquals(1234.4567, value);
    }

    public void testStringToDoubleConversionWithDotsPL() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, "1 234,4", Double.class);

        // then
        assertEquals(1234.4, value);
    }

    public void testStringToFloatConversionPL() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, "1234,4567", Float.class);

        // then
        assertEquals(1234.4567F, value);
    }

    public void testStringToFloatConversionWithDotsPL() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, "1 234,4", Float.class);

        // then
        assertEquals(1234.4F, value);
    }


}
