package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ValueConverter;

import java.util.Map;

public class CharacterConverter extends AbstractConverter {

    public CharacterConverter() {
        super(Character.class, Character.TYPE);
        register(String.class, new StringToCharacterConverter());
        register(Number.class, new NumberToCharacterConverter());
        register(Boolean.class, new BooleanToCharacterConverter());
    }


    private class StringToCharacterConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            String strValue = (String) value;
            return strValue.charAt(0);
        }

    }

    private class NumberToCharacterConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            return (char) ((Number) value).longValue();
        }

    }

    private class BooleanToCharacterConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            Boolean bool = (Boolean) value;
            if (bool) {
                return (char) 1;
            } else {
                return (char) 0;
            }
        }

    }
}
