package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.TypeConverter;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.Map;

public class ArrayConverter extends DefaultTypeConverter {

    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType) {
        Object result = null;
        Class componentType = toType.getComponentType();

        if (componentType != null) {
            TypeConverter converter = getTypeConverter(context);

            if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                result = Array.newInstance(componentType, length);

                for (int i = 0; i < length; i++) {
                    Object valueItem = Array.get(value, i);
                    Array.set(result, i, converter.convertValue(context, target, member, propertyName, valueItem, componentType));
                }
            } else {
                result = Array.newInstance(componentType, 1);
                Array.set(result, 0, converter.convertValue(context, target, member, propertyName, value, componentType));
            }
        }

        return result;
    }

}
