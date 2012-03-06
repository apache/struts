/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.test.annotations.Person;

import java.lang.reflect.Member;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Test case for XWorkBasicConverter
 *
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class XWorkBasicConverterTest extends XWorkTestCase {

    private XWorkBasicConverter basicConverter;

    // TODO: test for every possible conversion
    // take into account of empty string
    // primitive -> conversion error when empty string is passed
    // object -> return null when empty string is passed

    public void testDateConversionWithEmptyValue() {
        Object convertedObject = basicConverter.convertValue(new HashMap<String, Object>(), null, null, null, "", Date.class);
        // we must not get XWorkException as that will caused a conversion error
        assertNull(convertedObject);
    }

    public void testDateConversionWithInvalidValue() throws Exception {
        try {
            Object convertedObject = basicConverter.convertValue(new HashMap<String, Object>(), null, null, null, "asdsd", Date.class);
            fail("XWorkException expected - conversion error occurred");
        } catch (XWorkException e) {
            // we MUST get this exception as this is a conversion error
        }
    }

    public void testDateWithLocalePoland() throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        Locale locale = new Locale("pl", "PL");
        map.put(ActionContext.LOCALE, locale);

        String reference = "2009-01-09";
        Object convertedObject = basicConverter.convertValue(map, null, null, null, reference, Date.class);

        assertNotNull(convertedObject);

        compareDates(locale, convertedObject);
    }

    public void testDateWithLocaleFrance() throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        Locale locale = new Locale("fr", "FR");
        map.put(ActionContext.LOCALE, locale);

        String reference = "09/01/2009";
        Object convertedObject = basicConverter.convertValue(map, null, null, null, reference, Date.class);

        assertNotNull(convertedObject);

        compareDates(locale, convertedObject);
    }

    public void testDateWithLocaleUK() throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();
        Locale locale = new Locale("en", "US");
        map.put(ActionContext.LOCALE, locale);

        String reference = "01/09/2009";
        Object convertedObject = basicConverter.convertValue(map, null, null, null, reference, Date.class);

        assertNotNull(convertedObject);

        compareDates(locale, convertedObject);
    }

    private void compareDates(Locale locale, Object convertedObject) {
        Calendar cal = Calendar.getInstance(locale);
        cal.set(Calendar.YEAR, 2009);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 9);

        Calendar cal1 = Calendar.getInstance(locale);
        cal1.setTime((Date) convertedObject);

        assertEquals(cal.get(Calendar.YEAR), cal1.get(Calendar.YEAR));
        assertEquals(cal.get(Calendar.MONTH), cal1.get(Calendar.MONTH));
        assertEquals(cal.get(Calendar.DATE), cal1.get(Calendar.DATE));

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        assertEquals(df.format(cal.getTime()), df.format(convertedObject));
    }

    public void testEmptyArrayConversion() throws Exception {
        Object convertedObject = basicConverter.convertValue(new HashMap<String, Object>(), null, null, null, new Object[]{}, Object[].class);
        // we must not get XWorkException as that will caused a conversion error
        assertEquals(Object[].class, convertedObject.getClass());
        Object[] obj = (Object[]) convertedObject;
        assertEquals(0, obj.length);
    }

    public void testNullArrayConversion() throws Exception {
        Object convertedObject = basicConverter.convertValue(new HashMap<String, Object>(), null, null, null, null, Object[].class);
        // we must not get XWorkException as that will caused a conversion error
        assertNull(convertedObject);
    }

    /* the code below has been disabled as it causes sideffects in Strtus2 (XW-512)
    public void testXW490ConvertStringToDouble() throws Exception {
        Locale locale = new Locale("DA"); // let's use a not common locale such as Denmark

        Map ctx = new HashMap();
        ctx.put(ActionContext.LOCALE, locale);

        XWorkBasicConverter conv = new XWorkBasicConverter();
        // decimal seperator is , in Denmark so we should write 123,99 as input
        Double value = (Double) conv.convertValue(ctx, null, null, null, "123,99", Double.class);
        assertNotNull(value);

        // output is as expected a real double value converted using Denmark as locale
        assertEquals(123.99d, value.doubleValue(), 0.001d);
    }

    public void testXW49ConvertDoubleToString() throws Exception {
        Locale locale = new Locale("DA"); // let's use a not common locale such as Denmark

        Map ctx = new HashMap();
        ctx.put(ActionContext.LOCALE, locale);

        XWorkBasicConverter conv = new XWorkBasicConverter();
        // decimal seperator is , in Denmark so we should write 123,99 as input
        String value = (String) conv.convertValue(ctx, null, null, null, new Double("123.99"), String.class);
        assertNotNull(value);

        // output should be formatted according to Danish locale using , as decimal seperator
        assertEquals("123,99", value);
    }    
    */

    public void testDoubleValues() {
        NumberConverter numberConverter = new NumberConverter();

        assertTrue(numberConverter.isInRange(-1.2, "-1.2", Double.class));
        assertTrue(numberConverter.isInRange(1.5, "1.5", Double.class));

        Object value = basicConverter.convertValue("-1.3", double.class);
        assertNotNull(value);
        assertEquals(-1.3, value);

        value = basicConverter.convertValue("1.8", double.class);
        assertNotNull(value);
        assertEquals(1.8, value);

        value = basicConverter.convertValue("-1.9", double.class);
        assertNotNull(value);
        assertEquals(-1.9, value);

        value = basicConverter.convertValue("1.7", Double.class);
        assertNotNull(value);
        assertEquals(1.7, value);

        value = basicConverter.convertValue("0.0", Double.class);
        assertNotNull(value);
        assertEquals(0.0, value);

        value = basicConverter.convertValue("0.0", double.class);
        assertNotNull(value);
        assertEquals(0.0, value);
    }

    public void testFloatValues() {
        NumberConverter numberConverter = new NumberConverter();

        assertTrue(numberConverter.isInRange(-1.65, "-1.65", Float.class));
        assertTrue(numberConverter.isInRange(1.9876, "1.9876", float.class));

        Float value = (Float) basicConverter.convertValue("-1.444401", Float.class);
        assertNotNull(value);
        assertEquals(Float.valueOf("-1.444401"), value);

        value = (Float) basicConverter.convertValue("1.46464989", Float.class);
        assertNotNull(value);
        assertEquals(Float.valueOf(1.46464989f), value);
    }

    public void testNegativeFloatValue() throws Exception {
        Object convertedObject = basicConverter.convertValue("-94.1231233", Float.class);
        assertTrue(convertedObject instanceof Float);
        assertEquals(-94.1231233f, ((Float) convertedObject).floatValue(), 0.0001);
    }

    public void testPositiveFloatValue() throws Exception {
        Object convertedObject = basicConverter.convertValue("94.1231233", Float.class);
        assertTrue(convertedObject instanceof Float);
        assertEquals(94.1231233f, ((Float) convertedObject).floatValue(), 0.0001);
    }


    public void testNegativeDoubleValue() throws Exception {
        Object convertedObject = basicConverter.convertValue("-94.1231233", Double.class);
        assertTrue(convertedObject instanceof Double);
        assertEquals(-94.1231233d, ((Double) convertedObject).doubleValue(), 0.0001);
    }

    public void testPositiveDoubleValue() throws Exception {
        Object convertedObject = basicConverter.convertValue("94.1231233", Double.class);
        assertTrue(convertedObject instanceof Double);
        assertEquals(94.1231233d, ((Double) convertedObject).doubleValue(), 0.0001);
    }

    public void testNestedEnumValue() throws Exception {
        Object convertedObject = basicConverter.convertValue(ParentClass.NestedEnum.TEST.name(), ParentClass.NestedEnum.class);
        assertTrue(convertedObject instanceof ParentClass.NestedEnum);
        assertEquals(ParentClass.NestedEnum.TEST, convertedObject);
    }


    public void testConvert() {
        Map context = new HashMap();
        Person o = new Person();
        Member member = null;
        String s = "names";
        Object value = new Person[0];
        Class toType = String.class;
        basicConverter.convertValue(context, value, member, s, value, toType);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        basicConverter = container.getInstance(XWorkBasicConverter.class);
    }

    @Override
    protected void tearDown() throws Exception {
        ActionContext.setContext(null);
    }


}
