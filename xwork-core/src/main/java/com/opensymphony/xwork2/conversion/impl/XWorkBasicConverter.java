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
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.XWorkList;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.*;
import java.util.*;

import org.apache.commons.lang.StringUtils;


/**
 * <!-- START SNIPPET: javadoc -->
 * <p/>
 * XWork will automatically handle the most common type conversion for you. This includes support for converting to
 * and from Strings for each of the following:
 * <p/>
 * <ul>
 * <li>String</li>
 * <li>boolean / Boolean</li>
 * <li>char / Character</li>
 * <li>int / Integer, float / Float, long / Long, double / Double</li>
 * <li>dates - uses the SHORT format for the Locale associated with the current request</li>
 * <li>arrays - assuming the individual strings can be coverted to the individual items</li>
 * <li>collections - if not object type can be determined, it is assumed to be a String and a new ArrayList is
 * created</li>
 * </ul>
 * <p/> Note that with arrays the type conversion will defer to the type of the array elements and try to convert each
 * item individually. As with any other type conversion, if the conversion can't be performed the standard type
 * conversion error reporting is used to indicate a problem occured while processing the type conversion.
 * <p/>
 * <!-- END SNIPPET: javadoc -->
 *
 * @author <a href="mailto:plightbo@gmail.com">Pat Lightbody</a>
 * @author Mike Mosiewicz
 * @author Rainer Hermanns
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 */
public class XWorkBasicConverter extends DefaultTypeConverter {

    private static String MILLISECOND_FORMAT = ".SSS";
    
    private ObjectTypeDeterminer objectTypeDeterminer;
    private XWorkConverter xworkConverter;
    private ObjectFactory objectFactory;

    @Inject
    public void setObjectTypeDeterminer(ObjectTypeDeterminer det) {
        this.objectTypeDeterminer = det;
    }
    
    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }
    
    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    @Override
    public Object convertValue(Map<String, Object> context, Object o, Member member, String s, Object value, Class toType) {
        Object result = null;

        if (value == null || toType.isAssignableFrom(value.getClass())) {
            // no need to convert at all, right?
            return value;
        }

        if (toType == String.class) {
            /* the code below has been disabled as it causes sideffects in Struts2 (XW-512)
            // if input (value) is a number then use special conversion method (XW-490)
            Class inputType = value.getClass();
            if (Number.class.isAssignableFrom(inputType)) {
                result = doConvertFromNumberToString(context, value, inputType);
                if (result != null) {
                    return result;
                }
            }*/
            // okay use default string conversion
            result = doConvertToString(context, value);
        } else if (toType == boolean.class) {
            result = doConvertToBoolean(value);
        } else if (toType == Boolean.class) {
            result = doConvertToBoolean(value);
        } else if (toType.isArray()) {
            result = doConvertToArray(context, o, member, s, value, toType);
        } else if (Date.class.isAssignableFrom(toType)) {
            result = doConvertToDate(context, value, toType);
        } else if (Calendar.class.isAssignableFrom(toType)) {
            Date dateResult = (Date) doConvertToDate(context, value, Date.class);
            if (dateResult != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateResult);
                result = calendar;
            } 
        } else if (Collection.class.isAssignableFrom(toType)) {
            result = doConvertToCollection(context, o, member, s, value, toType);
        } else if (toType == Character.class) {
            result = doConvertToCharacter(value);
        } else if (toType == char.class) {
            result = doConvertToCharacter(value);
        } else if (Number.class.isAssignableFrom(toType) || toType.isPrimitive()) {
            result = doConvertToNumber(context, value, toType);
        } else if (toType == Class.class) {
            result = doConvertToClass(value);
        }

        if (result == null) {
            if (value instanceof Object[]) {
                Object[] array = (Object[]) value;

                if (array.length >= 1) {
                    value = array[0];
                } else {
                    value = null;
                }

                // let's try to convert the first element only
                result = convertValue(context, o, member, s, value, toType);
            } else if (!"".equals(value)) { // we've already tried the types we know
                result = super.convertValue(context, value, toType);
            }

            if (result == null && value != null && !"".equals(value)) {
                throw new XWorkException("Cannot create type " + toType + " from value " + value);
            }
        }

        return result;
    }

    private Locale getLocale(Map<String, Object> context) {
        if (context == null) {
            return Locale.getDefault();
        }

        Locale locale = (Locale) context.get(ActionContext.LOCALE);

        if (locale == null) {
            locale = Locale.getDefault();
        }

        return locale;
    }

    /**
     * Creates a Collection of the specified type.
     *
     * @param fromObject
     * @param propertyName
     * @param toType       the type of Collection to create
     * @param memberType   the type of object elements in this collection must be
     * @param size         the initial size of the collection (ignored if 0 or less)
     * @return a Collection of the specified type
     */
    private Collection createCollection(Object fromObject, String propertyName, Class toType, Class memberType, int size) {
//        try {
//            Object original = Ognl.getValue(OgnlUtil.compile(propertyName),fromObject);
//            if (original instanceof Collection) {
//                Collection coll = (Collection) original;
//                coll.clear();
//                return coll;
//            }
//        } catch (Exception e) {
//            // fail back to creating a new one
//        }

        Collection result;

        if (toType == Set.class) {
            if (size > 0) {
                result = new HashSet(size);
            } else {
                result = new HashSet();
            }
        } else if (toType == SortedSet.class) {
            result = new TreeSet();
        } else {
            if (size > 0) {
                result = new XWorkList(objectFactory, xworkConverter, memberType, size);
            } else {
                result = new XWorkList(objectFactory, xworkConverter, memberType);
            }
        }

        return result;
    }

    private Object doConvertToArray(Map<String, Object> context, Object o, Member member, String s, Object value, Class toType) {
        Object result = null;
        Class componentType = toType.getComponentType();

        if (componentType != null) {
            TypeConverter converter = getTypeConverter(context);

            if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                result = Array.newInstance(componentType, length);

                for (int i = 0; i < length; i++) {
                    Object valueItem = Array.get(value, i);
                    Array.set(result, i, converter.convertValue(context, o, member, s, valueItem, componentType));
                }
            } else {
                result = Array.newInstance(componentType, 1);
                Array.set(result, 0, converter.convertValue(context, o, member, s, value, componentType));
            }
        }

        return result;
    }

    private Object doConvertToCharacter(Object value) {
        if (value instanceof String) {
            String cStr = (String) value;

            return (cStr.length() > 0) ? new Character(cStr.charAt(0)) : null;
        }

        return null;
    }

    private Object doConvertToBoolean(Object value) {
        if (value instanceof String) {
            String bStr = (String) value;

            return Boolean.valueOf(bStr);
        }

        return null;
    }

    private Class doConvertToClass(Object value) {
        Class clazz = null;

        if (value instanceof String && value != null && ((String) value).length() > 0) {
            try {
                clazz = Class.forName((String) value);
            } catch (ClassNotFoundException e) {
                throw new XWorkException(e.getLocalizedMessage(), e);
            }
        }

        return clazz;
    }

    private Collection doConvertToCollection(Map<String, Object> context, Object o, Member member, String prop, Object value, Class toType) {
        Collection result;
        Class memberType = String.class;

        if (o != null) {
            //memberType = (Class) XWorkConverter.getInstance().getConverter(o.getClass(), XWorkConverter.CONVERSION_COLLECTION_PREFIX + prop);
            memberType = objectTypeDeterminer.getElementClass(o.getClass(), prop, null);

            if (memberType == null) {
                memberType = String.class;
            }
        }

        if (toType.isAssignableFrom(value.getClass())) {
            // no need to do anything
            result = (Collection) value;
        } else if (value.getClass().isArray()) {
            Object[] objArray = (Object[]) value;
            TypeConverter converter = getTypeConverter(context);
            result = createCollection(o, prop, toType, memberType, objArray.length);

            for (Object anObjArray : objArray) {
                result.add(converter.convertValue(context, o, member, prop, anObjArray, memberType));
            }
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            Collection col = (Collection) value;
            TypeConverter converter = getTypeConverter(context);
            result = createCollection(o, prop, toType, memberType, col.size());

            for (Object aCol : col) {
                result.add(converter.convertValue(context, o, member, prop, aCol, memberType));
            }
        } else {
            result = createCollection(o, prop, toType, memberType, -1);
            result.add(value);
        }

        return result;
    }

    private Object doConvertToDate(Map<String, Object> context, Object value, Class toType) {
        Date result = null;

        if (value instanceof String && value != null && ((String) value).length() > 0) {
            String sa = (String) value;
            Locale locale = getLocale(context);

            DateFormat df = null;
            if (java.sql.Time.class == toType) {
                df = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
            } else if (java.sql.Timestamp.class == toType) {
                Date check = null;
                SimpleDateFormat dtfmt = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT,
                        DateFormat.MEDIUM,
                        locale);
                SimpleDateFormat fullfmt = new SimpleDateFormat(dtfmt.toPattern() + MILLISECOND_FORMAT,
                        locale);

                SimpleDateFormat dfmt = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT,
                        locale);

                SimpleDateFormat[] fmts = {fullfmt, dtfmt, dfmt};
                for (SimpleDateFormat fmt : fmts) {
                    try {
                        check = fmt.parse(sa);
                        df = fmt;
                        if (check != null) {
                            break;
                        }
                    } catch (ParseException ignore) {
                    }
                }
            } else if (java.util.Date.class == toType) {
                Date check = null;
                DateFormat[] dfs = getDateFormats(locale);
                for (DateFormat df1 : dfs) {
                    try {
                        check = df1.parse(sa);
                        df = df1;
                        if (check != null) {
                            break;
                        }
                    }
                    catch (ParseException ignore) {
                    }
                }
            }
            //final fallback for dates without time
            if (df == null) {
                df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
            }
            try {
                df.setLenient(false); // let's use strict parsing (XW-341)
                result = df.parse(sa);
                if (!(Date.class == toType)) {
                    try {
                        Constructor constructor = toType.getConstructor(new Class[]{long.class});
                        return constructor.newInstance(new Object[]{Long.valueOf(result.getTime())});
                    } catch (Exception e) {
                        throw new XWorkException("Couldn't create class " + toType + " using default (long) constructor", e);
                    }
                }
            } catch (ParseException e) {
                throw new XWorkException("Could not parse date", e);
            }
        } else if (Date.class.isAssignableFrom(value.getClass())) {
            result = (Date) value;
        }
        return result;
    }

    private DateFormat[] getDateFormats(Locale locale) {
        DateFormat dt1 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, locale);
        DateFormat dt2 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
        DateFormat dt3 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);

        DateFormat d1 = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        DateFormat d2 = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        DateFormat d3 = DateFormat.getDateInstance(DateFormat.LONG, locale);

        DateFormat rfc3399 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        DateFormat[] dfs = {dt1, dt2, dt3, rfc3399, d1, d2, d3}; //added RFC 3339 date format (XW-473)
        return dfs;
    }

    private Object doConvertToNumber(Map<String, Object> context, Object value, Class toType) {
        if (value instanceof String) {
            if (toType == BigDecimal.class) {
                return new BigDecimal((String) value);
            } else if (toType == BigInteger.class) {
                return new BigInteger((String) value);
            } else if (toType.isPrimitive()) {
                Object convertedValue = super.convertValue(context, value, toType);
                String stringValue = (String) value;
                if (!isInRange((Number)convertedValue, stringValue,  toType))
                        throw new XWorkException("Overflow or underflow casting: \"" + stringValue + "\" into class " + convertedValue.getClass().getName());

                return convertedValue;
            } else {
                String stringValue = (String) value;
                if (!toType.isPrimitive() && (stringValue == null || stringValue.length() == 0)) {
                    return null;
                }
                NumberFormat numFormat = NumberFormat.getInstance(getLocale(context));
                ParsePosition parsePos = new ParsePosition(0);
                if (isIntegerType(toType)) {
                    numFormat.setParseIntegerOnly(true);
                }
                numFormat.setGroupingUsed(true);
                Number number = numFormat.parse(stringValue, parsePos);

                if (parsePos.getIndex() != stringValue.length()) {
                    throw new XWorkException("Unparseable number: \"" + stringValue + "\" at position "
                            + parsePos.getIndex());
                } else {
                    if (!isInRange(number, stringValue,  toType))
                        throw new XWorkException("Overflow or underflow casting: \"" + stringValue + "\" into class " + number.getClass().getName());
                    
                    value = super.convertValue(context, number, toType);
                }
            }
        } else if (value instanceof Object[]) {
            Object[] objArray = (Object[]) value;

            if (objArray.length == 1) {
                return doConvertToNumber(context, objArray[0], toType);
            }
        }

        // pass it through DefaultTypeConverter
        return super.convertValue(context, value, toType);
    }

    protected boolean isInRange(Number value, String stringValue, Class toType) {
        Number bigValue = null;
        Number lowerBound = null;
        Number upperBound = null;

        try {
            if (double.class == toType || Double.class == toType) {
                bigValue = new BigDecimal(stringValue);
                // Double.MIN_VALUE is the smallest positive non-zero number
                lowerBound = BigDecimal.valueOf(Double.MAX_VALUE).negate();
                upperBound = BigDecimal.valueOf(Double.MAX_VALUE);
            } else if (float.class == toType || Float.class == toType) {
                bigValue = new BigDecimal(stringValue);
                // Float.MIN_VALUE is the smallest positive non-zero number
                lowerBound = BigDecimal.valueOf(Float.MAX_VALUE).negate();
                upperBound = BigDecimal.valueOf(Float.MAX_VALUE);
            } else if (byte.class == toType || Byte.class == toType) {
                bigValue = new BigInteger(stringValue);
                lowerBound = BigInteger.valueOf(Byte.MIN_VALUE);
                upperBound = BigInteger.valueOf(Byte.MAX_VALUE);
            } else if (char.class == toType || Character.class == toType) {
                bigValue = new BigInteger(stringValue);
                lowerBound = BigInteger.valueOf(Character.MIN_VALUE);
                upperBound = BigInteger.valueOf(Character.MAX_VALUE);
            } else if (short.class == toType || Short.class == toType) {
                bigValue = new BigInteger(stringValue);
                lowerBound = BigInteger.valueOf(Short.MIN_VALUE);
                upperBound = BigInteger.valueOf(Short.MAX_VALUE);
            } else if (int.class == toType || Integer.class == toType) {
                bigValue = new BigInteger(stringValue);
                lowerBound = BigInteger.valueOf(Integer.MIN_VALUE);
                upperBound = BigInteger.valueOf(Integer.MAX_VALUE);
            } else if (long.class == toType || Long.class == toType) {
                bigValue = new BigInteger(stringValue);
                lowerBound = BigInteger.valueOf(Long.MIN_VALUE);
                upperBound = BigInteger.valueOf(Long.MAX_VALUE);
            }
        } catch (NumberFormatException e) {
            //shoult it fail here? BigInteger doesnt seem to be so nice parsing numbers as NumberFormat
            return true;
        }

        return ((Comparable)bigValue).compareTo(lowerBound) >= 0 && ((Comparable)bigValue).compareTo(upperBound) <= 0;
    }

    protected boolean isIntegerType(Class type) {
        if (double.class == type || float.class == type || Double.class == type || Float.class == type
                || char.class == type || Character.class == type) {
            return false;
        }

        return true;
    }

    /**
     * Converts the input as a number using java's number formatter to a string output.
     */
    private String doConvertFromNumberToString(Map<String, Object> context, Object value, Class toType) {
        // XW-409: If the input is a Number we should format it to a string using the choosen locale and use java's numberformatter
        if (Number.class.isAssignableFrom(toType)) {
            NumberFormat numFormat = NumberFormat.getInstance(getLocale(context));
            if (isIntegerType(toType)) {
                numFormat.setParseIntegerOnly(true);
            }
            numFormat.setGroupingUsed(true);
            numFormat.setMaximumFractionDigits(99); // to be sure we include all digits after decimal seperator, otherwise some of the fractions can be chopped

            String number = numFormat.format(value);
            if (number != null) {
                return number;
            }
        }

        return null; // no number
    }


    private String doConvertToString(Map<String, Object> context, Object value) {
        String result = null;

        if (value instanceof int[]) {
            int[] x = (int[]) value;
            List<Integer> intArray = new ArrayList<Integer>(x.length);

            for (int aX : x) {
                intArray.add(Integer.valueOf(aX));
            }

            result = StringUtils.join(intArray, ", ");
        } else if (value instanceof long[]) {
            long[] x = (long[]) value;
            List<Long> longArray = new ArrayList<Long>(x.length);

            for (long aX : x) {
                longArray.add(Long.valueOf(aX));
            }

            result = StringUtils.join(longArray, ", ");
        } else if (value instanceof double[]) {
            double[] x = (double[]) value;
            List<Double> doubleArray = new ArrayList<Double>(x.length);

            for (double aX : x) {
                doubleArray.add(new Double(aX));
            }

            result = StringUtils.join(doubleArray, ", ");
        } else if (value instanceof boolean[]) {
            boolean[] x = (boolean[]) value;
            List<Boolean> booleanArray = new ArrayList<Boolean>(x.length);

            for (boolean aX : x) {
                booleanArray.add(new Boolean(aX));
            }

            result = StringUtils.join(booleanArray, ", ");
        } else if (value instanceof Date) {
            DateFormat df = null;
            if (value instanceof java.sql.Time) {
                df = DateFormat.getTimeInstance(DateFormat.MEDIUM, getLocale(context));
            } else if (value instanceof java.sql.Timestamp) {
                SimpleDateFormat dfmt = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT,
                        DateFormat.MEDIUM,
                        getLocale(context));
                df = new SimpleDateFormat(dfmt.toPattern() + MILLISECOND_FORMAT);
            } else {
                df = DateFormat.getDateInstance(DateFormat.SHORT, getLocale(context));
            }
            result = df.format(value);
        } else if (value instanceof String[]) {
            result = StringUtils.join((String[]) value, ", ");
        }

        return result;
    }
}
