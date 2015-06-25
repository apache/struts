package com.opensymphony.xwork2.conversion.impl;

import java.util.Map;

public class FooNumberConverter extends DefaultTypeConverter {
    @Override
    public Object convertValue(Map<String, Object> map, Object object, Class aClass) {
        String s = (String) object;

        int length = s.length();
        StringBuilder r = new StringBuilder();
        for (int i = length; i > 0; i--) {
            r.append(s.charAt(i - 1));
        }

        return super.convertValue(map, r.toString(), aClass);
    }
}
