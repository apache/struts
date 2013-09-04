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

import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

import java.lang.reflect.Member;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;


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
 * conversion error reporting is used to indicate a problem occurred while processing the type conversion.
 * <!-- END SNIPPET: javadoc -->
 *
 * @author <a href="mailto:plightbo@gmail.com">Pat Lightbody</a>
 * @author Mike Mosiewicz
 * @author Rainer Hermanns
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 */
public class XWorkBasicConverter extends DefaultTypeConverter {

    private Container container;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Object convertValue(Map<String, Object> context, Object o, Member member, String propertyName, Object value, Class toType) {
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
            result = doConvertToArray(context, o, member, propertyName, value, toType);
        } else if (Date.class.isAssignableFrom(toType)) {
            result = doConvertToDate(context, value, toType);
        } else if (Calendar.class.isAssignableFrom(toType)) {
            result = doConvertToCalendar(context, value);
        } else if (Collection.class.isAssignableFrom(toType)) {
            result = doConvertToCollection(context, o, member, propertyName, value, toType);
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
                result = convertValue(context, o, member, propertyName, value, toType);
            } else if (!"".equals(value)) { // we've already tried the types we know
                result = super.convertValue(context, value, toType);
            }

            if (result == null && value != null && !"".equals(value)) {
                throw new XWorkException("Cannot create type " + toType + " from value " + value);
            }
        }

        return result;
    }

    private Object doConvertToCalendar(Map<String, Object> context, Object value) {
        Object result = null;
        Date dateResult = (Date) doConvertToDate(context, value, Date.class);
        if (dateResult != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateResult);
            result = calendar;
        }
        return result;
    }

    private Object doConvertToCharacter(Object value) {
        if (value instanceof String) {
            String cStr = (String) value;
            return (cStr.length() > 0) ? cStr.charAt(0) : null;
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
        if (value != null && value instanceof String && ((String) value).length() > 0) {
            try {
                clazz = Class.forName((String) value);
            } catch (ClassNotFoundException e) {
                throw new XWorkException(e.getLocalizedMessage(), e);
            }
        }
        return clazz;
    }

    private Object doConvertToCollection(Map<String, Object> context, Object o, Member member, String prop, Object value, Class toType) {
        TypeConverter converter = container.getInstance(CollectionConverter.class);
        if (converter == null) {
            throw new XWorkException("TypeConverter with name [#0] must be registered first!", XWorkConstants.COLLECTION_CONVERTER);
        }
        return converter.convertValue(context, o, member, prop, value, toType);
    }

    private Object doConvertToArray(Map<String, Object> context, Object o, Member member, String prop, Object value, Class toType) {
        TypeConverter converter = container.getInstance(ArrayConverter.class);
        if (converter == null) {
            throw new XWorkException("TypeConverter with name [#0] must be registered first!", XWorkConstants.ARRAY_CONVERTER);
        }
        return converter.convertValue(context, o, member, prop, value, toType);
    }

    private Object doConvertToDate(Map<String, Object> context, Object value, Class toType) {
        TypeConverter converter = container.getInstance(DateConverter.class);
        if (converter == null) {
            throw new XWorkException("TypeConverter with name [#0] must be registered first!", XWorkConstants.DATE_CONVERTER);
        }
        return converter.convertValue(context, null, null, null, value, toType);
    }

    private Object doConvertToNumber(Map<String, Object> context, Object value, Class toType) {
        TypeConverter converter = container.getInstance(NumberConverter.class);
        if (converter == null) {
            throw new XWorkException("TypeConverter with name [#0] must be registered first!", XWorkConstants.NUMBER_CONVERTER);
        }
        return converter.convertValue(context, null, null, null, value, toType);
    }

    private Object doConvertToString(Map<String, Object> context, Object value) {
        TypeConverter converter = container.getInstance(StringConverter.class);
        if (converter == null) {
            throw new XWorkException("TypeConverter with name [#0] must be registered first!", XWorkConstants.STRING_CONVERTER);
        }
        return converter.convertValue(context, null, null, null, value, null);
    }

}
