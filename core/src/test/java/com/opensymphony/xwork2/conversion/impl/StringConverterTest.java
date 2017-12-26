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

import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.StringUtils;
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
        Object value = converter.convertValue(context, null, null, null, Double.MIN_VALUE, null);

        // then does not lose fraction digits
        assertEquals("0," + StringUtils.repeat('0', 323) + "49", value);

        // when has max integer digits
        value = converter.convertValue(context, null, null, null, Double.MAX_VALUE, null);

        // then does not lose integer digits
        assertEquals("17976931348623157" + StringUtils.repeat('0', 292), value);

        // when cannot be represented exactly with a finite binary number
        value = converter.convertValue(context, null, null, null, 0.1d, null);

        // then produce the shortest decimal representation that can unambiguously identify the true value of the floating-point number
        assertEquals("0,1", value);
    }

    public void testFloatToStringConversionPL() throws Exception {
        // given
        StringConverter converter = new StringConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when has max fraction digits
        Object value = converter.convertValue(context, null, null, null, Float.MIN_VALUE, null);

        // then does not lose fraction digits
        assertEquals("0," + StringUtils.repeat('0', 44) + "14", value);

        // when has max integer digits
        value = converter.convertValue(context, null, null, null, Float.MAX_VALUE, null);

        // then does not lose integer digits
        assertEquals("34028235" + StringUtils.repeat('0', 31), value);

        // when cannot be represented exactly with a finite binary number
        value = converter.convertValue(context, null, null, null, 0.1f, null);

        // then produce the shortest decimal representation that can unambiguously identify the true value of the floating-point number
        assertEquals("0,1", value);
    }

    public void testBigDecimalToStringConversionPL() throws Exception {
        // given
        StringConverter converter = new StringConverter();
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.LOCALE, new Locale("pl", "PL"));

        // when a bit bigger than double
        String aBitBiggerThanDouble = "17976931348623157" + StringUtils.repeat('0', 291) + "1."
                + StringUtils.repeat('0', 324) + "49";
        Object value = converter.convertValue(context, null, null, null,
                new BigDecimal(aBitBiggerThanDouble), null);

        // then does not lose integer and fraction digits
        assertEquals(aBitBiggerThanDouble.substring(0, 309) + "," + aBitBiggerThanDouble.substring(310), value);
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
