package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ValueConverter;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

public class DoubleConverter extends AbstractConverter {

    private static Logger LOG = LoggerFactory.getLogger(DoubleConverter.class);

    public DoubleConverter() {
        super(Double.class, Double.TYPE);
        register(String.class, new StringToDoubleConverter());
        register(Number.class, new NumberToDoubleConverter());
        register(Boolean.class, new BooleanToDoubleConverter());
        register(Character.class, new CharacterToDoubleConverter());
    }

    private class StringToDoubleConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            try {
                return tryConvertValue((String) value, context);
            } catch (ParseException e) {
                LOG.warn("Could not convert [" + value + "] to Double!");
                return getDefaultConverter().convertValue(value, context);
            }
        }

        private Double tryConvertValue(String value, Map<String, Object> context) throws ParseException {
            NumberFormat numberFormat = NumberFormat.getInstance(getLocale(context));
            return numberFormat.parse(value).doubleValue();
        }

    }

    private class NumberToDoubleConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            return ((Number) value).doubleValue();
        }

    }

    private class BooleanToDoubleConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            Boolean bool = (Boolean) value;
            if (bool) {
                return 1.0;
            } else {
                return 0.0;
            }
        }

    }

    private class CharacterToDoubleConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            return Double.valueOf(((Character) value));
        }

    }
}
