/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;


/**
 * Test case for WebWork Type Converter.
 * 
 * @author tm_jee
 * @version $Date: 2006/03/14 17:35:38 $ $Id: WebWorkTypeConverterTest.java,v 1.1 2006/03/14 17:35:38 tmjee Exp $
 */
public class WebWorkTypeConverterTest extends TestCase {

	/**
	 * Typically form webwork -> html
	 * 
	 * @throws Exception
	 */
	public void testConvertToString() throws Exception {
		InternalWebWorkTypeConverter webWorkTypeConverter = new InternalWebWorkTypeConverter();
		webWorkTypeConverter.convertValue(new HashMap(), "", String.class);
		assertTrue(webWorkTypeConverter.isConvertToString);
		assertEquals(webWorkTypeConverter.objToBeConverted, "");
	}
	
	/**
	 * Typically form html -> WebWork
	 * 
	 * @throws Exception
	 */
	public void testConvertFromString() throws Exception {
		InternalWebWorkTypeConverter webWorkTypeConverter = new InternalWebWorkTypeConverter();
		webWorkTypeConverter.convertValue(new HashMap(), "12/12/1997", Date.class);
		assertTrue(webWorkTypeConverter.isConvertFromString);
		assertTrue(webWorkTypeConverter.objToBeConverted instanceof String[]);
		assertEquals(((String[])webWorkTypeConverter.objToBeConverted).length, 1);
	}
	
	/**
	 * Typically from html -> WebWork (in array due to the nature of html, param 
	 * being able to have many values).
	 * 
	 * @throws Exception
	 */
	public void testConvertFromStringInArrayForm() throws Exception {
		InternalWebWorkTypeConverter webWorkTypeConverter = new InternalWebWorkTypeConverter();
		webWorkTypeConverter.convertValue(new HashMap(), new String[] { "12/12/1997", "1/1/1977" }, Date.class);
		assertTrue(webWorkTypeConverter.isConvertFromString);
		assertTrue(webWorkTypeConverter.objToBeConverted instanceof String[]);
		assertEquals(((String[])webWorkTypeConverter.objToBeConverted).length, 2);
	}
	
	
	public void testFallbackConversion() throws Exception {
		InternalWebWorkTypeConverter webWorkTypeConverter = new InternalWebWorkTypeConverter();
		webWorkTypeConverter.convertValue(new HashMap(), new Object(), Date.class);
		assertTrue(webWorkTypeConverter.fallbackConversion);
	}
	
	// === internal class for testing 
	class InternalWebWorkTypeConverter extends WebWorkTypeConverter {

		boolean isConvertFromString = false;
		boolean isConvertToString = false;
		boolean fallbackConversion = false;
		
		Object objToBeConverted;
		
		public Object convertFromString(Map context, String[] values, Class toClass) {
			isConvertFromString = true;
			objToBeConverted = values;
			return null;
		}

		public String convertToString(Map context, Object o) {
			isConvertToString = true;
			objToBeConverted = o;
			return null;
		}
		
		protected Object performFallbackConversion(Map context, Object o, Class toClass) {
			fallbackConversion = true;
			return null;
		}
		
	}
	
}
