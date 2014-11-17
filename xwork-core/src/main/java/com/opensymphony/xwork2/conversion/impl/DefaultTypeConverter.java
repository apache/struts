//--------------------------------------------------------------------------
//  Copyright (c) 1998-2004, Drew Davidson and Luke Blanshard
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions are
//  met:
//
//  Redistributions of source code must retain the above copyright notice,
//  this list of conditions and the following disclaimer.
//  Redistributions in binary form must reproduce the above copyright
//  notice, this list of conditions and the following disclaimer in the
//  documentation and/or other materials provided with the distribution.
//  Neither the name of the Drew Davidson nor the names of its contributors
//  may be used to endorse or promote products derived from this software
//  without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
//  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
//  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
//  FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
//  COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
//  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
//  OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
//  AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
//  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
//  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
//  DAMAGE.
//--------------------------------------------------------------------------
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.XWorkTypeConverterWrapper;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Default type conversion. Converts among numeric types and also strings.  Contains the basic 
 * type mapping code from OGNL.
 * 
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 */
public abstract class DefaultTypeConverter implements TypeConverter {

    protected static String MILLISECOND_FORMAT = ".SSS";

    private static final String NULL_STRING = "null";

    private static final Map<Class, Object> primitiveDefaults;

    private Container container;

    static {
        Map<Class, Object> map = new HashMap<Class, Object>();
        map.put(Boolean.TYPE, Boolean.FALSE);
        map.put(Byte.TYPE, Byte.valueOf((byte) 0));
        map.put(Short.TYPE, Short.valueOf((short) 0));
        map.put(Character.TYPE, new Character((char) 0));
        map.put(Integer.TYPE, Integer.valueOf(0));
        map.put(Long.TYPE, Long.valueOf(0L));
        map.put(Float.TYPE, new Float(0.0f));
        map.put(Double.TYPE, new Double(0.0));
        map.put(BigInteger.class, new BigInteger("0"));
        map.put(BigDecimal.class, new BigDecimal(0.0));
        primitiveDefaults = Collections.unmodifiableMap(map);
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    public Object convertValue(Map<String, Object> context, Object value, Class toType) {
        return convertValue(value, toType);
    }

    public Object convertValue(Map<String, Object> context, Object target, Member member,
            String propertyName, Object value, Class toType) {
        return convertValue(context, value, toType);
    }
    
    public TypeConverter getTypeConverter( Map<String, Object> context )
    {
        Object obj = context.get(TypeConverter.TYPE_CONVERTER_CONTEXT_KEY);
        if (obj instanceof TypeConverter) {
            return (TypeConverter) obj;
            
        // for backwards-compatibility
        } else if (obj instanceof ognl.TypeConverter) {
            return new XWorkTypeConverterWrapper((ognl.TypeConverter) obj);
        }
        return null; 
    }

    /**
     * Returns the value converted numerically to the given class type
     * 
     * This method also detects when arrays are being converted and converts the
     * components of one array to the type of the other.
     * 
     * @param value
     *            an object to be converted to the given type
     * @param toType
     *            class type to be converted to
     * @return converted value of the type given, or value if the value cannot
     *         be converted to the given type.
     */
    public Object convertValue(Object value, Class toType) {
        Object result = null;

        if (value != null) {
            /* If array -> array then convert components of array individually */
            if (value.getClass().isArray() && toType.isArray()) {
                Class componentType = toType.getComponentType();

                result = Array.newInstance(componentType, Array
                        .getLength(value));
                for (int i = 0, icount = Array.getLength(value); i < icount; i++) {
                    Array.set(result, i, convertValue(Array.get(value, i),
                            componentType));
                }
            } else {
                if ((toType == Integer.class) || (toType == Integer.TYPE))
                    result = Integer.valueOf((int) longValue(value));
                if ((toType == Double.class) || (toType == Double.TYPE))
                    result = new Double(doubleValue(value));
                if ((toType == Boolean.class) || (toType == Boolean.TYPE))
                    result = booleanValue(value) ? Boolean.TRUE : Boolean.FALSE;
                if ((toType == Byte.class) || (toType == Byte.TYPE))
                    result = Byte.valueOf((byte) longValue(value));
                if ((toType == Character.class) || (toType == Character.TYPE))
                    result = new Character((char) longValue(value));
                if ((toType == Short.class) || (toType == Short.TYPE))
                    result = Short.valueOf((short) longValue(value));
                if ((toType == Long.class) || (toType == Long.TYPE))
                    result = Long.valueOf(longValue(value));
                if ((toType == Float.class) || (toType == Float.TYPE))
                    result = new Float(doubleValue(value));
                if (toType == BigInteger.class)
                    result = bigIntValue(value);
                if (toType == BigDecimal.class)
                    result = bigDecValue(value);
                if (toType == String.class)
                    result = stringValue(value);
                if (Enum.class.isAssignableFrom(toType))
                    result = enumValue((Class<Enum>)toType, value);
            }
        } else {
            if (toType.isPrimitive()) {
                result = primitiveDefaults.get(toType);
            }
        }
        return result;
    }

    /**
     * Evaluates the given object as a boolean: if it is a Boolean object, it's
     * easy; if it's a Number or a Character, returns true for non-zero objects;
     * and otherwise returns true for non-null objects.
     * 
     * @param value
     *            an object to interpret as a boolean
     * @return the boolean value implied by the given object
     */
    public static boolean booleanValue(Object value) {
        if (value == null)
            return false;
        Class c = value.getClass();
        if (c == Boolean.class)
            return ((Boolean) value).booleanValue();
        // if ( c == String.class )
        // return ((String)value).length() > 0;
        if (c == Character.class)
            return ((Character) value).charValue() != 0;
        if (value instanceof Number)
            return ((Number) value).doubleValue() != 0;
        return true; // non-null
    }
    
    public Enum<?> enumValue(Class toClass, Object o) {
        Enum<?> result = null;
        if (o == null) {
            result = null;
        } else if (o instanceof String[]) {
            result = Enum.valueOf(toClass, ((String[]) o)[0]);
        } else if (o instanceof String) {
            result = Enum.valueOf(toClass, (String) o);
        }
        return result;
    }

    /**
     * Evaluates the given object as a long integer.
     * 
     * @param value
     *            an object to interpret as a long integer
     * @return the long integer value implied by the given object
     * @throws NumberFormatException
     *             if the given object can't be understood as a long integer
     */
    public static long longValue(Object value) throws NumberFormatException {
        if (value == null)
            return 0L;
        Class c = value.getClass();
        if (c.getSuperclass() == Number.class)
            return ((Number) value).longValue();
        if (c == Boolean.class)
            return ((Boolean) value).booleanValue() ? 1 : 0;
        if (c == Character.class)
            return ((Character) value).charValue();
        return Long.parseLong(stringValue(value, true));
    }

    /**
     * Evaluates the given object as a double-precision floating-point number.
     * 
     * @param value
     *            an object to interpret as a double
     * @return the double value implied by the given object
     * @throws NumberFormatException
     *             if the given object can't be understood as a double
     */
    public static double doubleValue(Object value) throws NumberFormatException {
        if (value == null)
            return 0.0;
        Class c = value.getClass();
        if (c.getSuperclass() == Number.class)
            return ((Number) value).doubleValue();
        if (c == Boolean.class)
            return ((Boolean) value).booleanValue() ? 1 : 0;
        if (c == Character.class)
            return ((Character) value).charValue();
        String s = stringValue(value, true);

        return (s.length() == 0) ? 0.0 : Double.parseDouble(s);
        /*
         * For 1.1 parseDouble() is not available
         */
        // return Double.valueOf( value.toString() ).doubleValue();
    }

    /**
     * Evaluates the given object as a BigInteger.
     * 
     * @param value
     *            an object to interpret as a BigInteger
     * @return the BigInteger value implied by the given object
     * @throws NumberFormatException
     *             if the given object can't be understood as a BigInteger
     */
    public static BigInteger bigIntValue(Object value)
            throws NumberFormatException {
        if (value == null)
            return BigInteger.valueOf(0L);
        Class c = value.getClass();
        if (c == BigInteger.class)
            return (BigInteger) value;
        if (c == BigDecimal.class)
            return ((BigDecimal) value).toBigInteger();
        if (c.getSuperclass() == Number.class)
            return BigInteger.valueOf(((Number) value).longValue());
        if (c == Boolean.class)
            return BigInteger.valueOf(((Boolean) value).booleanValue() ? 1 : 0);
        if (c == Character.class)
            return BigInteger.valueOf(((Character) value).charValue());
        return new BigInteger(stringValue(value, true));
    }

    /**
     * Evaluates the given object as a BigDecimal.
     * 
     * @param value
     *            an object to interpret as a BigDecimal
     * @return the BigDecimal value implied by the given object
     * @throws NumberFormatException
     *             if the given object can't be understood as a BigDecimal
     */
    public static BigDecimal bigDecValue(Object value)
            throws NumberFormatException {
        if (value == null)
            return BigDecimal.valueOf(0L);
        Class c = value.getClass();
        if (c == BigDecimal.class)
            return (BigDecimal) value;
        if (c == BigInteger.class)
            return new BigDecimal((BigInteger) value);
        if (c.getSuperclass() == Number.class)
            return new BigDecimal(((Number) value).doubleValue());
        if (c == Boolean.class)
            return BigDecimal.valueOf(((Boolean) value).booleanValue() ? 1 : 0);
        if (c == Character.class)
            return BigDecimal.valueOf(((Character) value).charValue());
        return new BigDecimal(stringValue(value, true));
    }

    /**
     * Evaluates the given object as a String and trims it if the trim flag is
     * true.
     * 
     * @param value
     *            an object to interpret as a String
     * @return the String value implied by the given object as returned by the
     *         toString() method, or "null" if the object is null.
     */
    public static String stringValue(Object value, boolean trim) {
        String result;

        if (value == null) {
            result = NULL_STRING;
        } else {
            result = value.toString();
            if (trim) {
                result = result.trim();
            }
        }
        return result;
    }

    /**
     * Evaluates the given object as a String.
     * 
     * @param value
     *            an object to interpret as a String
     * @return the String value implied by the given object as returned by the
     *         toString() method, or "null" if the object is null.
     */
    public static String stringValue(Object value) {
        return stringValue(value, false);
    }

    protected Locale getLocale(Map<String, Object> context) {
        Locale locale = null;
        if (context != null) {
            locale = (Locale) context.get(ActionContext.LOCALE);
        }
        if (locale == null) {
            locale = container.getInstance(LocaleProvider.class).getLocale();
        }
        return locale;
    }

}
