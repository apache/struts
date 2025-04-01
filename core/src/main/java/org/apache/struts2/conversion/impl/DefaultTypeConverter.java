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
package org.apache.struts2.conversion.impl;

import org.apache.struts2.ActionContext;
import org.apache.struts2.conversion.TypeConverter;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.locale.LocaleProviderFactory;
import org.apache.struts2.ognl.StrutsContext;
import org.apache.struts2.ognl.XWorkTypeConverterWrapper;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Map;

/**
 * Default type conversion. Converts among numeric types and also strings.  Contains the basic
 * type mapping code from OGNL.
 *
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 */
public abstract class DefaultTypeConverter implements TypeConverter<StrutsContext> {

    protected static final String MILLISECOND_FORMAT = ".SSS";

    private static final String NULL_STRING = "null";

    private static final Map<Class<?>, Object> baseTypeDefaults = Map.of(
            Boolean.TYPE, Boolean.FALSE,
            Byte.TYPE, (byte) 0,
            Short.TYPE, (short) 0,
            Character.TYPE, (char) 0,
            Integer.TYPE, 0,
            Long.TYPE, 0L,
            Float.TYPE, 0.0f,
            Double.TYPE, 0.0,
            BigInteger.class, BigInteger.ZERO,
            BigDecimal.class, BigDecimal.ZERO);

    private Container container;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    public Object convertValue(StrutsContext context, Object value, Class<?> toType) {
        return convertValue(value, toType);
    }

    @Override
    public Object convertValue(StrutsContext context, Object target, Member member, String propertyName, Object value, Class<?> toType) {
        return convertValue(context, value, toType);
    }

    public TypeConverter<StrutsContext> getTypeConverter(StrutsContext context) {
        ognl.TypeConverter<StrutsContext> converter = context.getTypeConverter();

        if (converter != null) {
            if (converter instanceof TypeConverter) {
                return (TypeConverter<StrutsContext>) converter;
            } else {
                return new XWorkTypeConverterWrapper(converter);
            }
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
    public Object convertValue(Object value, Class<?> toType) {
        Object result = null;

        if (value != null) {
            /* If array -> array then convert components of array individually */
            if (value.getClass().isArray() && toType.isArray()) {
                final Class<?> componentType = toType.getComponentType();

                result = Array.newInstance(componentType, Array.getLength(value));
                for (int i = 0, icount = Array.getLength(value); i < icount; i++) {
                    Array.set(result, i, convertValue(Array.get(value, i), componentType));
                }
            } else {
                if ((toType == Integer.class) || (toType == Integer.TYPE))
                    result = (int) longValue(value);
                if ((toType == Double.class) || (toType == Double.TYPE))
                    result = doubleValue(value);
                if ((toType == Boolean.class) || (toType == Boolean.TYPE))
                    result = booleanValue(value) ? Boolean.TRUE : Boolean.FALSE;
                if ((toType == Byte.class) || (toType == Byte.TYPE))
                    result = (byte) longValue(value);
                if ((toType == Character.class) || (toType == Character.TYPE))
                    result = (char) longValue(value);
                if ((toType == Short.class) || (toType == Short.TYPE))
                    result = (short) longValue(value);
                if ((toType == Long.class) || (toType == Long.TYPE))
                    result = longValue(value);
                if ((toType == Float.class) || (toType == Float.TYPE))
                    result = (float) doubleValue(value);
                if (toType == BigInteger.class)
                    result = bigIntValue(value);
                if (toType == BigDecimal.class)
                    result = bigDecValue(value);
                if (toType == String.class)
                    result = stringValue(value);
                if (Enum.class.isAssignableFrom(toType))
                    result = enumValue(toType, value);
            }
        } else {
            result = baseTypeDefaults.get(toType);
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
        final Class<?> c = value.getClass();
        if (c == Boolean.class)
            return (Boolean) value;
        // if ( c == String.class )
        // return ((String)value).length() > 0;
        if (c == Character.class)
            return (Character) value != 0;
        if (value instanceof Number)
            return ((Number) value).doubleValue() != 0;
        return true; // non-null
    }

    public Enum<?> enumValue(Class toClass, Object o) {
        Enum<?> result = null;
        if (o instanceof String[]) {
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
     */
    public static long longValue(Object value) {
        if (value == null)
            return 0L;
        final Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class)
            return ((Number) value).longValue();
        if (c == Boolean.class)
            return (Boolean) value ? 1 : 0;
        if (c == Character.class)
            return (Character) value;
        return Long.parseLong(stringValue(value, true));
    }

    /**
     * Evaluates the given object as a double-precision floating-point number.
     *
     * @param value
     *            an object to interpret as a double
     * @return the double value implied by the given object
     */
    public static double doubleValue(Object value) {
        if (value == null)
            return 0.0;
        final Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class)
            return ((Number) value).doubleValue();
        if (c == Boolean.class)
            return (Boolean) value ? 1 : 0;
        if (c == Character.class)
            return (Character) value;
        final String s = stringValue(value, true);
        return s.isEmpty() ? 0.0 : Double.parseDouble(s);
    }

    /**
     * Evaluates the given object as a BigInteger.
     *
     * @param value
     *            an object to interpret as a BigInteger
     * @return the BigInteger value implied by the given object
     */
    public static BigInteger bigIntValue(Object value) {
        if (value == null)
            return BigInteger.valueOf(0L);
        final Class<?> c = value.getClass();
        if (c == BigInteger.class)
            return (BigInteger) value;
        if (c == BigDecimal.class)
            return ((BigDecimal) value).toBigInteger();
        if (c.getSuperclass() == Number.class)
            return BigInteger.valueOf(((Number) value).longValue());
        if (c == Boolean.class)
            return BigInteger.valueOf((Boolean) value ? 1 : 0);
        if (c == Character.class)
            return BigInteger.valueOf((Character) value);
        return new BigInteger(stringValue(value, true));
    }

    /**
     * Evaluates the given object as a BigDecimal.
     *
     * @param value
     *            an object to interpret as a BigDecimal
     * @return the BigDecimal value implied by the given object
     */
    public static BigDecimal bigDecValue(Object value) {
        if (value == null)
            return BigDecimal.valueOf(0L);
        final Class<?> c = value.getClass();
        if (c == BigDecimal.class)
            return (BigDecimal) value;
        if (c == BigInteger.class)
            return new BigDecimal((BigInteger) value);
        if (c.getSuperclass() == Number.class)
            return BigDecimal.valueOf(((Number) value).doubleValue());
        if (c == Boolean.class)
            return BigDecimal.valueOf((Boolean) value ? 1 : 0);
        if (c == Character.class)
            return BigDecimal.valueOf((Character) value);
        return new BigDecimal(stringValue(value, true));
    }

    /**
     * Evaluates the given object as a String and trims it if the trim flag is
     * true.
     *
     * @param value
     *            an object to interpret as a String
     * @param trim
     *            trims the result if true
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
            locale = ActionContext.of(context).getLocale();
        }
        if (locale == null) {
            LocaleProviderFactory localeProviderFactory = container.getInstance(LocaleProviderFactory.class);
            locale = localeProviderFactory.createLocaleProvider().getLocale();
        }
        return locale;
    }

}
