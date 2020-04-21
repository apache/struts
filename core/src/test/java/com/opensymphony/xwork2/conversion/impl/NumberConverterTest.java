/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.SimpleFooAction;
import com.opensymphony.xwork2.XWorkTestCase;
import org.apache.struts2.conversion.TypeConversionException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

public class NumberConverterTest extends XWorkTestCase {

    private final static String FLOAT_OUT_OF_RANGE = "3.5028235E38";
    private final static String DOUBLE_OUT_OF_RANGE = "1.7976931348623157E309";
    private final static String INTEGER_OUT_OF_RANGE = "2147483648";
    private final static String MSG_OUT_OF_RANGE_CASTING = "Overflow or underflow casting";
    private final static String MSG_OUT_OF_RANGE_CONVERTING = "Overflow or underflow converting";
    private final static String MSG_UNPARSEABLE_NUMBER = "Unparseable number";
    private final static String MSG_TEST_FAILS_OUT_OF_RANGE = "TypeConversionException expected when OUT OF RANGE";
    private final static String MSG_TEST_FAILS_UNPARSEABLE_NUMBER = "TypeConversionException expected when UNPARSEABLE NUMBER";
    private final static Locale LOCALE_MEXICO = new Locale("es_MX", "MX");

    public void testStringToNumberConversionPL() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(new Locale("pl", "PL"));

        SimpleFooAction foo = new SimpleFooAction();

        // when
        Object value = converter.convertValue(context, foo, null, "id", "1234", Integer.class);

        // then
        assertEquals(1234, value);
    }

    public void testStringToNumberConversionUS() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(new Locale("en", "US"));

        SimpleFooAction foo = new SimpleFooAction();

        // when
        Object value = converter.convertValue(context, foo, null, "id", ",1234", Integer.class);

        // then
        assertEquals(1234, value);
    }

    public void testStringToBigDecimalConversionPL() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(new Locale("pl", "PL"));

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
        Map<String, Object> context = createContextWithLocale(new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, "1 234,4", BigDecimal.class);

        // then
        assertEquals(BigDecimal.valueOf(1234.4), value);
    }

    public void testStringToBigDecimalConversionWithCommasEN() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(new Locale("en", "US"));

        // when
        Object value = converter.convertValue(context, null, null, null, "100,234.4", BigDecimal.class);

        // then
        assertEquals(BigDecimal.valueOf(100234.4), value);
    }

    public void testStringToDoubleConversionPL() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(new Locale("pl", "PL"));

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
        Map<String, Object> context = createContextWithLocale(new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, "1 234,4", Double.class);

        // then
        assertEquals(1234.4, value);
    }

    public void testStringToFloatConversionPL() throws Exception {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(new Locale("pl", "PL"));

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
        Map<String, Object> context = createContextWithLocale(new Locale("pl", "PL"));

        // when
        Object value = converter.convertValue(context, null, null, null, "1 234,4", Float.class);

        // then
        assertEquals(1234.4F, value);
    }

    public void testExceptionWhenPrimitiveIsOutOfRange() {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(LOCALE_MEXICO);

        // when
        try {
            Object value = converter.convertValue(context, null, null, null, INTEGER_OUT_OF_RANGE, int.class);
            fail(MSG_TEST_FAILS_OUT_OF_RANGE);
        } catch (Exception ex) {
            // then
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith(MSG_OUT_OF_RANGE_CASTING));
        }
    }

    public void testExceptionWhenANotPrimitiveIsUnparsable() {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(LOCALE_MEXICO);
        String strValue = "1.2";

        // when
        try {
            Object value = converter.convertValue(context, null, null, null, strValue, Byte.class);
            fail(MSG_TEST_FAILS_UNPARSEABLE_NUMBER);
        } catch (Exception ex) {
            // then
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith(MSG_UNPARSEABLE_NUMBER));
        }
    }

    public void testExceptionWhenANotPrimitiveIsOutOfRange() {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(LOCALE_MEXICO);
        String strValue = "129";

        // when
        try {
            Object value = converter.convertValue(context, null, null, null, strValue, Byte.class);
            fail(MSG_TEST_FAILS_OUT_OF_RANGE);
        } catch (Exception ex) {
            // then
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith(MSG_OUT_OF_RANGE_CASTING));
        }
    }

    public void testExceptionWhenUnparseableInConvertToBigDecimal() {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(LOCALE_MEXICO);
        String strValue = "1-23";

        // when
        try {
            Object value = converter.convertValue(context, null, null, null, strValue, BigDecimal.class);
            fail(MSG_TEST_FAILS_UNPARSEABLE_NUMBER);
        } catch (Exception ex) {
            // then
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith(MSG_UNPARSEABLE_NUMBER));
        }
    }

    public void testExceptionWhenUnparseableInConvertToDouble() {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(LOCALE_MEXICO);
        String strValue = "1-23";

        // when
        try {
            Object value = converter.convertValue(context, null, null, null, strValue, Double.class);
            fail(MSG_TEST_FAILS_UNPARSEABLE_NUMBER);
        } catch (Exception ex) {
            // then
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith(MSG_UNPARSEABLE_NUMBER));
        }
    }

    public void testExceptionWhenOutOfRangeInConvertToDouble() {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(LOCALE_MEXICO);

        // when
        try {
            Object value = converter.convertValue(context, null, null, null, DOUBLE_OUT_OF_RANGE, Double.class);
            fail(MSG_TEST_FAILS_OUT_OF_RANGE);
        } catch (Exception ex) {
            // then
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith(MSG_OUT_OF_RANGE_CONVERTING));
        }
    }

    public void testExceptionWhenOutOfRangeInConvertToFloat() {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(LOCALE_MEXICO);

        // when
        try {
            Object value = converter.convertValue(context, null, null, null, FLOAT_OUT_OF_RANGE, Float.class);
            fail(MSG_TEST_FAILS_OUT_OF_RANGE);
        } catch (Exception ex) {
            // then
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith(MSG_OUT_OF_RANGE_CONVERTING));
        }
    }

    public void testExceptionWhenUnparseableInConvertToFloat() {
        // given
        NumberConverter converter = new NumberConverter();
        Map<String, Object> context = createContextWithLocale(LOCALE_MEXICO);
        String strValue = "1-23";

        // when
        try {
            Object value = converter.convertValue(context, null, null, null, strValue, Float.class);
            fail(MSG_TEST_FAILS_UNPARSEABLE_NUMBER);
        } catch (Exception ex) {
            // then
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith(MSG_UNPARSEABLE_NUMBER));
        }
    }

}
