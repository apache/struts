/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;


/**
 * Test case for Struts Type Converter.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class StrutsTypeConverterTest extends TestCase {

	/**
	 * Typically form Struts -> html
	 * 
	 * @throws Exception
	 */
	public void testConvertToString() throws Exception {
		InternalStrutsTypeConverter strutsTypeConverter = new InternalStrutsTypeConverter();
		strutsTypeConverter.convertValue(new HashMap(), "", String.class);
		assertTrue(strutsTypeConverter.isConvertToString);
		assertEquals(strutsTypeConverter.objToBeConverted, "");
	}
	
	/**
	 * Typically form html -> Struts
	 * 
	 * @throws Exception
	 */
	public void testConvertFromString() throws Exception {
		InternalStrutsTypeConverter strutsTypeConverter = new InternalStrutsTypeConverter();
		strutsTypeConverter.convertValue(new HashMap(), "12/12/1997", Date.class);
		assertTrue(strutsTypeConverter.isConvertFromString);
		assertTrue(strutsTypeConverter.objToBeConverted instanceof String[]);
		assertEquals(((String[])strutsTypeConverter.objToBeConverted).length, 1);
	}
	
	/**
	 * Typically from html -> Struts (in array due to the nature of html, param 
	 * being able to have many values).
	 * 
	 * @throws Exception
	 */
	public void testConvertFromStringInArrayForm() throws Exception {
		InternalStrutsTypeConverter strutsTypeConverter = new InternalStrutsTypeConverter();
		strutsTypeConverter.convertValue(new HashMap(), new String[] { "12/12/1997", "1/1/1977" }, Date.class);
		assertTrue(strutsTypeConverter.isConvertFromString);
		assertTrue(strutsTypeConverter.objToBeConverted instanceof String[]);
		assertEquals(((String[])strutsTypeConverter.objToBeConverted).length, 2);
	}
	
	
	public void testFallbackConversion() throws Exception {
		InternalStrutsTypeConverter strutsTypeConverter = new InternalStrutsTypeConverter();
		strutsTypeConverter.convertValue(new HashMap(), new Object(), Date.class);
		assertTrue(strutsTypeConverter.fallbackConversion);
	}
	
	// === internal class for testing 
	class InternalStrutsTypeConverter extends StrutsTypeConverter {

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
