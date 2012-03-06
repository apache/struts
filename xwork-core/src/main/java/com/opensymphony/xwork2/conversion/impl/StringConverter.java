package com.opensymphony.xwork2.conversion.impl;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Member;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StringConverter extends DefaultTypeConverter {

    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
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
