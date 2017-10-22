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
