package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ValueConverter;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

public class ShortConverter extends AbstractConverter {

    private static Logger LOG = LoggerFactory.getLogger(ShortConverter.class);

    public ShortConverter() {
        super(Short.class, Short.TYPE);
        register(String.class, new StringToShortConverter());
        register(Number.class, new NumberToShortConverter());
        register(Boolean.class, new BooleanToShortConverter());
        register(Character.class, new CharacterToShortConverter());
    }

    private class StringToShortConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            try {
                return tryConvertValue((String) value, context);
            } catch (ParseException e) {
                LOG.warn("Could not convert [" + value + "] to Short!");
                return getDefaultConverter().convertValue(value, context);
            }
        }

        private Short tryConvertValue(String value, Map<String, Object> context) throws ParseException {
            NumberFormat numberFormat = NumberFormat.getInstance(getLocale(context));
            return numberFormat.parse(value).shortValue();
        }

    }

    private class NumberToShortConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            return ((Number) value).shortValue();
        }

    }

    private class BooleanToShortConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            Boolean bool = (Boolean) value;
            if (bool) {
                return (short) 1;
            } else {
                return (short) 0;
            }
        }

    }

    private class CharacterToShortConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            Character character = (Character) value;
            return (short) character.charValue();
        }

    }
}
