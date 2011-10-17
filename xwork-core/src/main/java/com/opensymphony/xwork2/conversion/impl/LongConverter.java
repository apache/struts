package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ValueConverter;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

public class LongConverter extends AbstractConverter {

    private static Logger LOG = LoggerFactory.getLogger(LongConverter.class);

    public LongConverter() {
        super(Long.class, Long.TYPE);
        register(String.class, new StringToLongConverter());
        register(Number.class, new NumberToLongConverter());
        register(Boolean.class, new BooleanToLongConverter());
        register(Character.class, new CharacterToLongConverter());
    }

    private class StringToLongConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            try {
                return tryConvertValue((String) value, context);
            } catch (ParseException e) {
                LOG.warn("Could not convert [" + value + "] to Long!");
                return getDefaultConverter().convertValue(value, context);
            }
        }

        private Long tryConvertValue(String value, Map<String, Object> context) throws ParseException {
            NumberFormat numberFormat = NumberFormat.getInstance(getLocale(context));
            return numberFormat.parse(value).longValue();
        }

    }

    private class NumberToLongConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            return ((Number) value).longValue();
        }

    }

    private class BooleanToLongConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            Boolean bool = (Boolean) value;
            if (bool) {
                return 1L;
            } else {
                return 0L;
            }
        }

    }

    private class CharacterToLongConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            Character character = (Character) value;
            return (long) character.charValue();
        }

    }
}
