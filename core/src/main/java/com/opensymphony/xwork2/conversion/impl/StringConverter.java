package com.opensymphony.xwork2.conversion.impl;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StringConverter extends DefaultTypeConverter {

    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
        String result;

        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            List<String> converted = new ArrayList<>(length);

            for (int i = 0; i < length; i++) {
                Object o = Array.get(value, i);
                converted.add(convertToString(getLocale(context), o));
            }

            result = StringUtils.join(converted, ", ");
        } else if(value.getClass().isAssignableFrom(Collection.class)) {
            Collection<?> colValue = (Collection) value;
            List<String> converted = new ArrayList<>(colValue.hashCode());

            for (Object o : colValue) {
                converted.add(convertToString(getLocale(context), o));
            }

            result = StringUtils.join(converted, ", ");
        } else if (value instanceof Date) {
            DateFormat df;
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
        } else {
            result = convertToString(getLocale(context), value);
        }

        return result;
    }

    protected String convertToString(Locale locale, Object value) {
        if (Number.class.isInstance(value)) {
            NumberFormat format = NumberFormat.getNumberInstance(locale);
            format.setGroupingUsed(false);
            if (Double.class.isInstance(value) || BigDecimal.class.isInstance(value)) {
                format.setMinimumFractionDigits(1);
            }
            return format.format(value);
        } else {
            return String.valueOf(value);
        }
    }

}
