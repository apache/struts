package com.opensymphony.xwork2.conversion;

import java.util.Map;

public interface Converter {

    Object convert(Object value, Map<String, Object> context);

}
