package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.XWorkException;

import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Map;

public class NumberConverter extends DefaultTypeConverter {

    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
        if (value instanceof String) {
            if (toType == BigDecimal.class) {
                return new BigDecimal((String) value);
            } else if (toType == BigInteger.class) {
                return new BigInteger((String) value);
            } else if (toType.isPrimitive()) {
                Object convertedValue = super.convertValue(context, value, toType);
                String stringValue = (String) value;
                if (!isInRange((Number) convertedValue, stringValue, toType))
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

        return ((Comparable)bigValue).compareTo(lowerBound) >= 0 && ((Comparable)bigValue).compareTo(upperBound) <= 0;
    }

    private boolean isIntegerType(Class type) {
        if (double.class == type || float.class == type || Double.class == type || Float.class == type
                || char.class == type || Character.class == type) {
            return false;
        }

        return true;
    }

}
