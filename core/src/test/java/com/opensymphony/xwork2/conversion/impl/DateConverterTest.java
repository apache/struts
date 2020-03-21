package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.TypeConversionException;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsInternalTestCase;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateConverterTest extends StrutsInternalTestCase {
	
	private final static String TIME_00_59_10 = "00:59:10";
	private final static String TIMESTAMP_STR = "2020-03-20 00:59:10";
	private final static String DATE_STR = "2020-03-20";
	private final static String DATE_CONVERTED = "Fri Mar 20 00:00:00 CST 2020";
	private final static String INVALID_DATE = "99/99/2010";
	private final static String MESSAGE_PARSE_ERROR = "Could not parse date";
	private final static String MESSAGE_DEFAULT_CONSTRUCTOR_ERROR = "Couldn't create class null using default (long) constructor";
	
	public void  testSqlTimeType(){
		DateConverter converter = new DateConverter();
		
		Map<String, Object> context = new HashMap<>();
		context.put(ActionContext.LOCALE, new Locale("es_MX", "MX"));
		
		Object value = converter.convertValue(context, null, null, null, TIME_00_59_10, Time.class);
		assertEquals(Time.valueOf(TIME_00_59_10), value);
		
	}
	
	public void  testSqlTimestampType(){
		DateConverter converter = new DateConverter();
		
		Map<String, Object> context = new HashMap<>();
		context.put(ActionContext.LOCALE, new Locale("es_MX", "MX"));
		
		Object value = converter.convertValue(context, null, null, null, TIMESTAMP_STR, Timestamp.class);
		assertEquals(Timestamp.valueOf(TIMESTAMP_STR), value);
		
	}
	
	public void  testDateType(){
		DateConverter converter = new DateConverter();
		
		Map<String, Object> context = new HashMap<>();
		context.put(ActionContext.LOCALE, new Locale("es_MX", "MX"));
		
		Object value = converter.convertValue(context, null, null, null, DATE_STR, Date.class);
		assertEquals(DATE_CONVERTED, ((Date)value).toString());
		
	}

    public void  testTypeConversionExceptionWhenParseError(){
    	DateConverter converter = new DateConverter();
	
			Map<String, Object> context = new HashMap<>();
			context.put(ActionContext.LOCALE, new Locale("es_MX", "MX"));
	
			try{
				Object value = converter.convertValue(context, null, null, null, INVALID_DATE, Date.class);
				fail("TypeConversionException expected - Conversion error occurred");
			}catch(Exception ex){
				assertEquals(TypeConversionException.class, ex.getClass());
				assertEquals(MESSAGE_PARSE_ERROR, ex.getMessage());
			}
			
		}
	
	public void  testTypeConversionExceptionWhenUsingLongConstructor(){
		DateConverter converter = new DateConverter();
		
		Map<String, Object> context = new HashMap<>();
		context.put(ActionContext.LOCALE, new Locale("es_MX", "MX"));
		
		try{
			Object value = converter.convertValue(context, null, null, null, "01-10-10", null);
			fail("TypeConversionException expected - Error using default (long) constructor");
		}catch(Exception ex){
			assertEquals(TypeConversionException.class, ex.getClass());
			assertEquals(MESSAGE_DEFAULT_CONSTRUCTOR_ERROR, ex.getMessage());
		}
		
	}
	
}
