package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ValueConverter;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

public class ByteConverter extends AbstractConverter {

    private static Logger LOG = LoggerFactory.getLogger(ByteConverter.class);

    public ByteConverter() {
        super(Byte.class, Byte.TYPE);
        register(String.class, new StringToByteConverter());
        register(Number.class, new NumberToByteConverter());
        register(Boolean.class, new BooleanToByteConverter());
        register(Character.class, new CharacterToByteConverter());
    }

    private class StringToByteConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            try {
                return tryConvertValue((String) value, context);
            } catch (ParseException e) {
                LOG.warn("Could not convert [" + value + "] to Byte!");
                return getDefaultConverter().convertValue(value, context);
            }
        }

        private Byte tryConvertValue(String value, Map<String, Object> context) throws ParseException {
            NumberFormat numberFormat = NumberFormat.getInstance(getLocale(context));
            return numberFormat.parse(value).byteValue();
        }

    }

    private class NumberToByteConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            return ((Number) value).byteValue();
        }

    }

    private class BooleanToByteConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            Boolean bool = (Boolean) value;
            if (bool) {
                return (byte) 1;
            } else{
                return (byte) 0;
            }
        }
    }

    private class CharacterToByteConverter implements ValueConverter {

        public Object convertValue(Object value, Map<String, Object> context) {
            Character character = (Character) value;
            return (byte) character.charValue();
        }

    }
}
