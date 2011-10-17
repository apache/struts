package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.Converter;
import com.opensymphony.xwork2.conversion.TypeConversionException;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.ValueConverter;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public abstract class AbstractConverter implements TypeConverter, Converter {

    private final Map<Class, ValueConverter> converters;
    private Class clazz;
    private Class type;

    protected AbstractConverter() {
        converters = new HashMap<Class, ValueConverter>();
    }

    public AbstractConverter(Class clazz, Class type) {
        this();
        this.clazz = clazz;
        this.type = type;
    }

    public Object convert(Object value, Map<String, Object> context) {
        if (value == null) {
            return getDefaultConverter().convertValue(value, context);
        }
        return findConverter(value.getClass()).convertValue(value, context);
    }

    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
        return convert(value, context);
    }

    private ValueConverter findConverter(Class clazz) {
        if (converters.containsKey(clazz)) {
            return converters.get(clazz);
        } else if (converters.containsKey(clazz.getSuperclass())) {
            return converters.get(clazz.getSuperclass());
        } else {
            return getDefaultConverter();
        }
    }

    protected void register(Class clazz, ValueConverter valueConverter) {
        converters.put(clazz, valueConverter);
    }

    protected ValueConverter getDefaultConverter() {
        return new ValueConverter() {
            public Object convertValue(Object value, Map<String, Object> context) {
                if (isAssignable(value)) {
                    return value;
                }
                throw new TypeConversionException("Cannot convert [" + value + "] to class [" + clazz +"]");
            }
        };
    }

    private boolean isAssignable(Object value) {
        return value != null && ((value.getClass().isAssignableFrom(clazz)) || value.getClass().isAssignableFrom(type));
    }

    protected Locale getLocale(Map<String, Object> context) {
        Locale locale = null;
        if (context != null) {
            locale = (Locale) context.get(ActionContext.LOCALE);
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }
}
