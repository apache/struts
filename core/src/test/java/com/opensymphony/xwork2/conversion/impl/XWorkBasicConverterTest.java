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
import com.opensymphony.xwork2.StubTextProvider;
import com.opensymphony.xwork2.StubValueStack;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.conversion.TypeConversionException;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.test.annotations.Person;
import org.apache.struts2.StrutsException;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.*;

/**
 * Test case for XWorkBasicConverter
 *
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class XWorkBasicConverterTest extends XWorkTestCase {

    private XWorkBasicConverter basicConverter;
    private Container mockedContainer;
    private final static String MSG_EXCEPTION_EXPECTED = "TypeConversionException expected";
    private final static String MSG_TYPE_CONVERTER_EXCEPTION = "TypeConverter with name";

    // TODO: test for every possible conversion
    // take into account of empty string
    // primitive -> conversion error when empty string is passed
    // object -> return null when empty string is passed

    public void testDateConversionWithEmptyValue() {
        Object convertedObject = basicConverter.convertValue(new HashMap<>(), null, null, null, "", Date.class);
        // we must not get StrutsException as that will caused a conversion error
        assertNull(convertedObject);
    }

    public void testDateConversionWithInvalidValue() {
        Map<String, String> map = new HashMap<>();
        map.put(org.apache.struts2.components.Date.DATETAG_PROPERTY, "yyyy-MM-dd");
        ValueStack stack = new StubValueStack();
        stack.push(new StubTextProvider(map));

        ActionContext context = ActionContext.of(new HashMap<>())
            .withLocale(new Locale("es_MX", "MX"))
            .withValueStack(stack);

        try {
            basicConverter.convertValue(context.getContextMap(), null, null, null, "asdsd", Date.class);
            fail("StrutsException expected - conversion error occurred");
        } catch (StrutsException e) {
            assertEquals("Could not parse date", e.getMessage());
        }
    }

    public void testDateWithLocalePoland() {
        Map<String, String> map = new HashMap<>();
        ValueStack stack = new StubValueStack();
        stack.push(new StubTextProvider(map));

        Locale locale = new Locale("pl", "PL");

        ActionContext context = ActionContext.of(new HashMap<>())
            .withLocale(locale)
            .withValueStack(stack);

        String reference = "2009-01-09";
        Object convertedObject = basicConverter.convertValue(context.getContextMap(), null, null, null, reference, Date.class);

        assertNotNull(convertedObject);

        compareDates(locale, convertedObject);
    }

    public void testDateWithLocaleFrance() {
        Map<String, String> map = new HashMap<>();
        ValueStack stack = new StubValueStack();
        stack.push(new StubTextProvider(map));

        Locale locale = new Locale("fr", "FR");

        ActionContext context = ActionContext.of(new HashMap<>())
            .withLocale(locale)
            .withValueStack(stack);

        String reference = "09/01/2009";
        Object convertedObject = basicConverter.convertValue(context.getContextMap(), null, null, null, reference, Date.class);

        assertNotNull(convertedObject);

        compareDates(locale, convertedObject);
    }

    public void testDateWithLocaleUK() {
        Map<String, String> map = new HashMap<>();
        ValueStack stack = new StubValueStack();
        stack.push(new StubTextProvider(map));

        Locale locale = new Locale("en", "US");

        ActionContext context = ActionContext.of(new HashMap<>())
            .withLocale(locale)
            .withValueStack(stack);

        String reference = "01/09/2009";
        Object convertedObject = basicConverter.convertValue(context.getContextMap(), null, null, null, reference, Date.class);

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

    public void testEmptyArrayConversion() {
        Object convertedObject = basicConverter.convertValue(new HashMap<>(), null, null, null, new Object[]{}, Object[].class);
        // we must not get StrutsException as that will caused a conversion error
        assertEquals(Object[].class, convertedObject.getClass());
        Object[] obj = (Object[]) convertedObject;
        assertEquals(0, obj.length);
    }

    public void testNullArrayConversion() {
        Object convertedObject = basicConverter.convertValue(new HashMap<>(), null, null, null, null, Object[].class);
        // we must not get StrutsException as that will caused a conversion error
        assertNull(convertedObject);
    }

    public void testXW490ConvertStringToDouble() {
        Locale locale = new Locale("DA"); // let's use a not common locale such as Denmark
        Map<String, Object> context = createContextWithLocale(locale);

        // decimal seperator is , in Denmark so we should write 123,99 as input
        Double value = (Double) basicConverter.convertValue(context, null, null, null, "123,99", Double.class);
        assertNotNull(value);

        // output is as expected a real double value converted using Denmark as locale
        assertEquals(123.99d, value, 0.001d);
    }

    public void testXW49ConvertDoubleToString() {
        Locale locale = new Locale("DA"); // let's use a not common locale such as Denmark
        Map<String, Object> context = createContextWithLocale(locale);

        // decimal seperator is , in Denmark so we should write 123,99 as input
        String value = (String) basicConverter.convertValue(context, null, null, null, new Double("123.99"), String.class);
        assertNotNull(value);

        // output should be formatted according to Danish locale using , as decimal seperator
        assertEquals("123,99", value);
    }    

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
        assertEquals(1.46464989f, value);
    }

    public void testNegativeFloatValue() {
        Object convertedObject = basicConverter.convertValue("-94.1231233", Float.class);
        assertTrue(convertedObject instanceof Float);
        assertEquals(-94.1231233f, (Float) convertedObject, 0.0001);
    }

    public void testPositiveFloatValue() {
        Object convertedObject = basicConverter.convertValue("94.1231233", Float.class);
        assertTrue(convertedObject instanceof Float);
        assertEquals(94.1231233f, (Float) convertedObject, 0.0001);
    }


    public void testNegativeDoubleValue() {
        Object convertedObject = basicConverter.convertValue("-94.1231233", Double.class);
        assertTrue(convertedObject instanceof Double);
        assertEquals(-94.1231233d, (Double) convertedObject, 0.0001);
    }

    public void testPositiveDoubleValue() {
        Object convertedObject = basicConverter.convertValue("94.1231233", Double.class);
        assertTrue(convertedObject instanceof Double);
        assertEquals(94.1231233d, (Double) convertedObject, 0.0001);
    }

    public void testBigInteger() {
        Object convertedObject = basicConverter.convertValue(null, BigInteger.class);
        assertEquals(BigInteger.ZERO, convertedObject);
        assertEquals(0, BigInteger.ZERO.compareTo((BigInteger) convertedObject));

        convertedObject = basicConverter.convertValue(BigInteger.ZERO, BigInteger.class);
        assertEquals(BigInteger.ZERO, convertedObject);
        assertEquals(0, BigInteger.ZERO.compareTo((BigInteger) convertedObject));

        convertedObject = basicConverter.convertValue(new BigInteger("0"), BigInteger.class);
        assertEquals(BigInteger.ZERO, convertedObject);
        assertEquals(0, BigInteger.ZERO.compareTo((BigInteger) convertedObject));

        convertedObject = basicConverter.convertValue(BigInteger.TEN, BigInteger.class);
        assertEquals(BigInteger.TEN, convertedObject);
        assertEquals(0, BigInteger.TEN.compareTo((BigInteger) convertedObject));
    }

    public void testBigDecimal() {
        Object convertedObject = basicConverter.convertValue(null, BigDecimal.class);
        assertEquals(BigDecimal.ZERO, convertedObject);
        assertTrue(convertedObject instanceof BigDecimal);
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) convertedObject));
        
        convertedObject = basicConverter.convertValue(new BigDecimal(0), BigDecimal.class);
        assertEquals(BigDecimal.ZERO, convertedObject);
        assertTrue(convertedObject instanceof BigDecimal);
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) convertedObject));
        
        convertedObject = basicConverter.convertValue(BigDecimal.valueOf(0), BigDecimal.class);
        assertEquals(BigDecimal.ZERO, convertedObject);
        assertTrue(convertedObject instanceof BigDecimal);
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) convertedObject));
        
        convertedObject = basicConverter.convertValue(BigDecimal.valueOf(0.0), BigDecimal.class);
        assertEquals(BigDecimal.valueOf(0.0), convertedObject);
        assertTrue(convertedObject instanceof BigDecimal);
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) convertedObject));

        convertedObject = basicConverter.convertValue(BigDecimal.valueOf(0.000), BigDecimal.class);
        assertEquals(BigDecimal.valueOf(0.000), convertedObject);
        assertTrue(convertedObject instanceof BigDecimal);
        assertEquals(0, BigDecimal.ZERO.compareTo((BigDecimal) convertedObject));

        convertedObject = basicConverter.convertValue(BigDecimal.valueOf(10), BigDecimal.class);
        assertEquals(BigDecimal.TEN, convertedObject);

        convertedObject = basicConverter.convertValue(BigDecimal.valueOf(12345.67890), BigDecimal.class);
        assertEquals(BigDecimal.valueOf(12345.67890), convertedObject);
    }

    public void testNestedEnumValue() {
        Object convertedObject = basicConverter.convertValue(ParentClass.NestedEnum.TEST.name(), ParentClass.NestedEnum.class);
        assertTrue(convertedObject instanceof ParentClass.NestedEnum);
        assertEquals(ParentClass.NestedEnum.TEST, convertedObject);
    }

    public void testConvert() {
        Map<String, Object> context = new HashMap<>();
        String s = "names";
        Object value = new Person[0];
        Class<?> toType = String.class;
        basicConverter.convertValue(context, value, null, s, value, toType);
    }
    
    public void testExceptionWhenCantCreateTypeFromValue() {
        try{
            basicConverter.convertValue(new HashMap<>(), null, null, null, 4, Date.class);
            fail(MSG_EXCEPTION_EXPECTED);
        }catch(Exception ex){
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith("Cannot create type"));
        }
    }
    
    public void testExceptionInDoConvertToClass() {
        try{
            basicConverter.convertValue(new HashMap<>(), null, null, null, "Foo", Class.class);
            fail(MSG_EXCEPTION_EXPECTED);
        }catch(Exception ex){
            assertEquals(TypeConversionException.class, ex.getClass());
        }
    }
    
    public void testExceptionInDoConvertToCollection() {
        try{
            Mockito.when(mockedContainer.getInstanceNames(CollectionConverter.class)).thenReturn(null);
            basicConverter.setContainer(mockedContainer);
            basicConverter.convertValue(new HashMap<>(), null, null, null, "Foo", ArrayList.class);
            fail(MSG_EXCEPTION_EXPECTED);
        }catch(Exception ex){
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith(MSG_TYPE_CONVERTER_EXCEPTION));
        }
    }
    
    public void testExceptionInDoConvertToArray() {
        try{
            int[] arrayInt = new int[1];
            Mockito.when(mockedContainer.getInstanceNames(ArrayConverter.class)).thenReturn(null);
            basicConverter.setContainer(mockedContainer);
            basicConverter.convertValue(new HashMap<>(), null, null, null, "Foo", arrayInt.getClass());
            fail(MSG_EXCEPTION_EXPECTED);
        }catch(Exception ex){
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith(MSG_TYPE_CONVERTER_EXCEPTION));
        }
    }
    
    public void testExceptionInDoConvertToDate() {
        try{
            Mockito.when(mockedContainer.getInstanceNames(DateConverter.class)).thenReturn(null);
            basicConverter.setContainer(mockedContainer);
            basicConverter.convertValue(new HashMap<>(), null, null, null, "Foo", Date.class);
            fail(MSG_EXCEPTION_EXPECTED);
        }catch(Exception ex){
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith(MSG_TYPE_CONVERTER_EXCEPTION));
        }
    }
    
    public void testExceptionInDoConvertToNumber() {
        try{
            Mockito.when(mockedContainer.getInstanceNames(NumberConverter.class)).thenReturn(null);
            basicConverter.setContainer(mockedContainer);
            basicConverter.convertValue(new HashMap<>(), null, null, null, "Foo", int.class);
            fail(MSG_EXCEPTION_EXPECTED);
        }catch(Exception ex){
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith(MSG_TYPE_CONVERTER_EXCEPTION));
        }
    }
    
    public void testExceptionInDoConvertToString() {
        try{
            Mockito.when(mockedContainer.getInstanceNames(StringConverter.class)).thenReturn(null);
            basicConverter.setContainer(mockedContainer);
            basicConverter.convertValue(new HashMap<>(), null, null, null, 1, String.class);
            fail(MSG_EXCEPTION_EXPECTED);
        }catch(Exception ex){
            assertEquals(TypeConversionException.class, ex.getClass());
            assertTrue(ex.getMessage().startsWith(MSG_TYPE_CONVERTER_EXCEPTION));
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        basicConverter = container.getInstance(XWorkBasicConverter.class);
        mockedContainer = Mockito.mock(Container.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        ActionContext.clear();
    }
    
}
