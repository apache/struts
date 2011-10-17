package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ValueConverter;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

public class FloatConverter extends AbstractConverter {

    private static Logger LOG = LoggerFactory.getLogger(FloatConverter.class);

    public FloatConverter() {
        super(Float.class, Float.TYPE);
        register(String.class, new StringToFloatConverter());
        register(Number.class, new NumberToFloatConverter());
        register(Boolean.class, new BooleanToFloatConverter());
        register(Character.class, new CharacterToFloatConverter());
    }

    private class StringToFloatConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            try {
                return tryConvertValue((String) value, context);
            } catch (ParseException e) {
                LOG.warn("Could not convert [" + value + "] to Float!");
                return getDefaultConverter().convertValue(value, context);
            }
        }

        private Float tryConvertValue(String value, Map<String, Object> context) throws ParseException {
            NumberFormat numberFormat = NumberFormat.getInstance(getLocale(context));
            return numberFormat.parse(value).floatValue();
        }

    }

    private class NumberToFloatConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            return ((Number) value).floatValue();
        }

    }

    private class BooleanToFloatConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            Boolean bool = (Boolean) value;
            if (bool) {
                return 1.0F;
            } else {
                return 0.0F;
            }
        }

    }

    private class CharacterToFloatConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            return Float.valueOf((Character) value);
        }

    }
}
