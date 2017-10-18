package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.SimpleFooAction;
import com.opensymphony.xwork2.XWorkTestCase;
import org.apache.commons.lang3.StringUtils;

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

        // when a bit bigger than double
        String aBitBiggerThanDouble = "17976931348623157" + StringUtils.repeat('0', 291) + "1,"
                + StringUtils.repeat('0', 324) + "49";
        Object value = converter.convertValue(context, null, null, null, aBitBiggerThanDouble, BigDecimal.class);

        // then does not lose integer and fraction digits
        assertEquals(new BigDecimal(aBitBiggerThanDouble.substring(0, 309) + "." + aBitBiggerThanDouble.substring(310)), value);
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

        // when has max fraction digits
        Object value = converter.convertValue(context, null, null, null,
                "0," + StringUtils.repeat('0', 323) + "49", Double.class);

        // then does not lose fraction digits
        assertEquals(Double.MIN_VALUE, value);

        // when has max integer digits
        value = converter.convertValue(context, null, null, null,
                "17976931348623157" + StringUtils.repeat('0', 292) + ",0", Double.class);

        // then does not lose integer digits
        assertEquals(Double.MAX_VALUE, value);
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

        // when has max fraction digits
        Object value = converter.convertValue(context, null, null, null,
                "0," + StringUtils.repeat('0', 44) + "1401298464324817", Float.class);

        // then does not lose fraction digits
        assertEquals(Float.MIN_VALUE, value);

        // when has max integer digits
        value = converter.convertValue(context, null, null, null,
                "34028234663852886" + StringUtils.repeat('0', 22) + ",0", Float.class);

        // then does not lose integer digits
        assertEquals(Float.MAX_VALUE, value);
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
