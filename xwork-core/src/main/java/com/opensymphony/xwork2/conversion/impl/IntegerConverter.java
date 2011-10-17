package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ValueConverter;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

public class IntegerConverter extends AbstractConverter {

    private static Logger LOG = LoggerFactory.getLogger(IntegerConverter.class);

    public IntegerConverter() {
        super(Integer.class, Integer.TYPE);
        register(String.class, new StringToIntegerConverter());
        register(Number.class, new NumberToIntegerConverter());
        register(Boolean.class, new BooleanToIntegerConverter());
        register(Character.class, new CharacterToIntegerConverter());
    }

    private class StringToIntegerConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            try {
                return tryConvertValue((String) value, context);
            } catch (ParseException e) {
                LOG.warn("Could not convert [" + value + "] to Integer!");
                return getDefaultConverter().convertValue(value, context);
            }
        }

        private Integer tryConvertValue(String value, Map<String, Object> context) throws ParseException {
            NumberFormat numberFormat = NumberFormat.getInstance(getLocale(context));
            return numberFormat.parse(value).intValue();
        }

    }

    private class NumberToIntegerConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            return ((Number) value).intValue();
        }

    }

    private class BooleanToIntegerConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            Boolean bool = (Boolean) value;
            if (bool) {
                return 1;
            } else {
                return 0;
            }
        }

    }

    private class CharacterToIntegerConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            Character character = (Character) value;
            return (int) character.charValue();
        }

    }
}
