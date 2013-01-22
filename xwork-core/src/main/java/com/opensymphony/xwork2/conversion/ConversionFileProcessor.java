package com.opensymphony.xwork2.conversion;

import java.util.Map;

/**
 * Used to process <clazz>-conversion.properties file to read defined Converters
 */
public interface ConversionFileProcessor {

    /**
     * Process conversion file to create mapping for key (property, type) and corresponding converter
     *
     * @param mapping keeps converters per given key
     * @param clazz class which should be converted by the converter
     * @param converterFilename to read converters from
     */
    void process(Map<String, Object> mapping, Class clazz, String converterFilename);

}
