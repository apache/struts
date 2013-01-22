package com.opensymphony.xwork2.conversion;

import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

import java.util.Map;

/**
 * Used to process {@link com.opensymphony.xwork2.conversion.annotations.TypeConversion}
 * annotation to read defined Converters
 */
public interface ConversionAnnotationProcessor {

    /**
     * Process annotation and build {@link TypeConverter} base on provided annotation
     * and assigning it under given key
     *
     * @param mapping keeps converters per given key
     * @param tc annotation which keeps information about converter
     * @param key key under which converter should be registered
     */
    void process(Map<String, Object> mapping, TypeConversion tc, String key);

}
