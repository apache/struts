package com.opensymphony.xwork2.conversion;

import java.util.Map;

public interface ValueConverter {

    Object convertValue(Object value, Map<String, Object> context);

}
