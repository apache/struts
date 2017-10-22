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

import com.opensymphony.xwork2.XWorkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Map;

public class NumberConverter extends DefaultTypeConverter {

    private static final Logger LOG = LogManager.getLogger(NumberConverter.class);

    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
        if (value instanceof String) {
            String stringValue = String.valueOf(value);

            if (toType == BigDecimal.class) {
                return convertToBigDecimal(context, stringValue);
            } else if (toType == BigInteger.class) {
                return new BigInteger(stringValue);
            } else if (toType == Double.class || toType == double.class) {
                return convertToDouble(context, stringValue);
            } else if (toType == Float.class || toType == float.class) {
                return convertToFloat(context, stringValue);
            } else if (toType.isPrimitive()) {
                Object convertedValue = super.convertValue(context, value, toType);

                if (!isInRange((Number) convertedValue, stringValue, toType))
                    throw new XWorkException("Overflow or underflow casting: \"" + stringValue + "\" into class " + convertedValue.getClass().getName());

                return convertedValue;
            } else {
                if (!toType.isPrimitive() && stringValue.isEmpty()) {
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
                    if (!isInRange(number, stringValue, toType))
                        throw new XWorkException("Overflow or underflow casting: \"" + stringValue + "\" into class " + number.getClass().getName());

                    value = super.convertValue(context, number, toType);
                }
            }
        } else if (value instanceof Object[]) {
            Object[] objArray = (Object[]) value;

            if (objArray.length == 1) {
                return convertValue(context, null, null, null, objArray[0], toType);
            }
        }

        // pass it through DefaultTypeConverter
        return super.convertValue(context, value, toType);
    }

    protected Object convertToBigDecimal(Map<String, Object> context, String stringValue) {
        Locale locale = getLocale(context);

        NumberFormat format = getNumberFormat(locale);
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setParseBigDecimal(true);
            char separator = ((DecimalFormat) format).getDecimalFormatSymbols().getGroupingSeparator();
            stringValue = normalize(stringValue, separator);
        }

        LOG.debug("Trying to convert a value {} with locale {} to BigDecimal", stringValue, locale);
        ParsePosition parsePosition = new ParsePosition(0);
        Number number = format.parse(stringValue, parsePosition);

        if (parsePosition.getIndex() != stringValue.length()) {
            throw new XWorkException("Unparseable number: \"" + stringValue + "\" at position " + parsePosition.getIndex());
        }

        return number;
    }

    protected Object convertToDouble(Map<String, Object> context, String stringValue) {
        Locale locale = getLocale(context);

        NumberFormat format = getNumberFormat(locale);
        if (format instanceof DecimalFormat) {
            char separator = ((DecimalFormat) format).getDecimalFormatSymbols().getGroupingSeparator();
            stringValue = normalize(stringValue, separator);
        }

        LOG.debug("Trying to convert a value {} with locale {} to Double", stringValue, locale);
        ParsePosition parsePosition = new ParsePosition(0);
        Number number = format.parse(stringValue, parsePosition);

        if (parsePosition.getIndex() != stringValue.length()) {
            throw new XWorkException("Unparseable number: \"" + stringValue + "\" at position " + parsePosition.getIndex());
        }

        if (!isInRange(number, stringValue, Double.class)) {
            throw new XWorkException("Overflow or underflow converting: \"" + stringValue + "\" into class " + number.getClass().getName());
        }

        if (number != null) {
            return number.doubleValue();
        }

        return null;
    }

    protected Object convertToFloat(Map<String, Object> context, String stringValue) {
        Locale locale = getLocale(context);

        NumberFormat format = getNumberFormat(locale);
        if (format instanceof DecimalFormat) {
            char separator = ((DecimalFormat) format).getDecimalFormatSymbols().getGroupingSeparator();
            stringValue = normalize(stringValue, separator);
        }

        LOG.debug("Trying to convert a value {} with locale {} to Float", stringValue, locale);
        ParsePosition parsePosition = new ParsePosition(0);
        Number number = format.parse(stringValue, parsePosition);

        if (parsePosition.getIndex() != stringValue.length()) {
            throw new XWorkException("Unparseable number: \"" + stringValue + "\" at position " + parsePosition.getIndex());
        }

        if (!isInRange(number, stringValue, Float.class)) {
            throw new XWorkException("Overflow or underflow converting: \"" + stringValue + "\" into class " + number.getClass().getName());
        }

        if (number != null) {
            return number.floatValue();
        }

        return null;
    }

    protected NumberFormat getNumberFormat(Locale locale) {
        NumberFormat format = NumberFormat.getNumberInstance(locale);
        format.setGroupingUsed(true);
        return format;
    }

    protected String normalize(String strValue, char separator) {
        // this is a hack as \160 isn't the same as " " (an empty space)
        if (separator == 160) {
            strValue = strValue.replaceAll(" ", String.valueOf(separator));
        }
        return strValue;
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
            } else {
                throw new IllegalArgumentException("Unexpected numeric type: " + toType.getName());
            }
        } catch (NumberFormatException e) {
            //shoult it fail here? BigInteger doesnt seem to be so nice parsing numbers as NumberFormat
            return true;
        }

        return ((Comparable) bigValue).compareTo(lowerBound) >= 0 && ((Comparable) bigValue).compareTo(upperBound) <= 0;
    }

    private boolean isIntegerType(Class type) {
        if (double.class == type || float.class == type || Double.class == type || Float.class == type
                || char.class == type || Character.class == type) {
            return false;
        }

        return true;
    }

}
