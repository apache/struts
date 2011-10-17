package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ValueConverter;

import java.text.NumberFormat;
import java.util.Map;

public class StringConverter extends AbstractConverter {

    public StringConverter() {
        register(Number.class, new NumberToStringConverter());
    }

    @Override
    protected ValueConverter getDefaultConverter() {
        return new ValueConverter() {
            public Object convertValue(Object value, Map<String, Object> context) {
                return value.toString();
            }
        };
    }

    private class NumberToStringConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            NumberFormat format = NumberFormat.getInstance(getLocale(context));
            updateFractionDigits(format, value.toString());
            return format.format(value);
        }

        private void updateFractionDigits(NumberFormat format, String strValue) {
            int digits = 0;
            int startIndex = (strValue.lastIndexOf(",") > -1 ? strValue.lastIndexOf(",") : strValue.lastIndexOf("."));
            if (startIndex > -1) {
                digits = strValue.substring(startIndex + 1, strValue.length()).length();
            }
            format.setMaximumFractionDigits(digits);
        }

    }
}
