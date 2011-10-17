package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ValueConverter;

import java.util.Map;

public class BooleanConverter extends AbstractConverter {

    public BooleanConverter() {
        super(Boolean.class, Boolean.TYPE);
        register(String.class, new StringToBooleanConverter());
        register(Character.class, new CharacterToBooleanConverter());
        register(Number.class, new NumberToBooleanConverter());
    }

    public ValueConverter getDefaultConverter() {
        return new ValueConverter() {
            public Object convertValue(Object value, Map<String, Object> context) {
                if (value != null) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
        };
    }

    private class StringToBooleanConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            String strValue = (String) value;
            return Boolean.valueOf(strValue);
        }

    }

    private class CharacterToBooleanConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            return Character.valueOf('1').equals(value);
        }

    }

    private class NumberToBooleanConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            return ((Number) value).doubleValue() != 0;
        }

    }

}
