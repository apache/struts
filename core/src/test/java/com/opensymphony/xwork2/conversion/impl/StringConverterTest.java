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
