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
import com.opensymphony.xwork2.conversion.TypeConversionException;
import org.apache.struts2.StrutsInternalTestCase;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateConverterTest extends StrutsInternalTestCase {
	
	private String INPUT_TIME_STAMP_STR;
	private String INPUT_WHEN_LONG_CONSTRUCTOR_STR;
	private Locale mxLocale = new Locale("es_MX", "MX");
	private final static String RES_TIME_STAMP_STR = "2020-03-20 00:00:00.0";
	private final static String TIME_01_59_10 = "01:59:10 AM";
	private final static String DATE_STR = "2020-03-20";
	private final static String DATE_CONVERTED = "Fri Mar 20 00:00:00";
	private final static String INVALID_DATE = "99/99/2010";
	private final static String MESSAGE_PARSE_ERROR = "Could not parse date";
	private final static String MESSAGE_DEFAULT_CONSTRUCTOR_ERROR = "Couldn't create class null using default (long) constructor";
	
	public void testSqlTimeType() {
		DateConverter converter = new DateConverter();
		
		Map<String, Object> context = new HashMap<>();
		context.put(ActionContext.LOCALE, new Locale("es_MX", "MX"));
		
		Object value = converter.convertValue(context, null, null, null, TIME_01_59_10, Time.class);
		assertEquals("01:59:10", value.toString());
	}
	
	public void testSqlTimestampType() {
		DateConverter converter = new DateConverter();
		Map<String, Object> context = new HashMap<>();
		context.put(ActionContext.LOCALE, mxLocale);
		
		Object value = converter.convertValue(context, null, null, null, INPUT_TIME_STAMP_STR, Timestamp.class);
		assertEquals(RES_TIME_STAMP_STR, value.toString());
	}
	
	public void testDateType() {
		DateConverter converter = new DateConverter();
		
		Map<String, Object> context = new HashMap<>();
		context.put(ActionContext.LOCALE, new Locale("es_MX", "MX"));
		
		Object value = converter.convertValue(context, null, null, null, DATE_STR, Date.class);
		assertTrue(((Date) value).toString().startsWith(DATE_CONVERTED));
	}
	
	public void testTypeConversionExceptionWhenParseError() {
		DateConverter converter = new DateConverter();
		
		Map<String, Object> context = new HashMap<>();
		context.put(ActionContext.LOCALE, new Locale("es_MX", "MX"));
		
		try {
			Object value = converter.convertValue(context, null, null, null, INVALID_DATE, Date.class);
			fail("TypeConversionException expected - Conversion error occurred");
		} catch (Exception ex) {
			assertEquals(TypeConversionException.class, ex.getClass());
			assertEquals(MESSAGE_PARSE_ERROR, ex.getMessage());
		}
	}
	
	public void testTypeConversionExceptionWhenUsingLongConstructor() {
		DateConverter converter = new DateConverter();
		
		Map<String, Object> context = new HashMap<>();
		context.put(ActionContext.LOCALE, new Locale("es_MX", "MX"));
		
		try {
			Object value = converter.convertValue(context, null, null, null, INPUT_WHEN_LONG_CONSTRUCTOR_STR, null);
			fail("TypeConversionException expected - Error using default (long) constructor");
		} catch (Exception ex) {
			assertEquals(TypeConversionException.class, ex.getClass());
			assertEquals(MESSAGE_DEFAULT_CONSTRUCTOR_ERROR, ex.getMessage());
		}
	}
	
	@Override
	protected void setUp() {
		//Due to JEP 252: Use CLDR Locale Data by Default
		DateFormat dFormat = DateFormat.getDateInstance(DateFormat.SHORT, mxLocale);
		if(dFormat.format(new Date()).contains("-")){ 			// Format when Java 9 or greater
			INPUT_TIME_STAMP_STR = "2020-03-20 00:00:00.000";
			INPUT_WHEN_LONG_CONSTRUCTOR_STR = "2020-03-20";
		}else{ 																							// Format when Java 8 or lower
			INPUT_TIME_STAMP_STR = "03/20/2020 00:00:00.000";
			INPUT_WHEN_LONG_CONSTRUCTOR_STR = "03/31/20";
		}
	}
	
}
