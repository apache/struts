package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.conversion.TypeConverter;

import java.util.Map;

/**
 * Dedicated interface used by {@link com.opensymphony.xwork2.ObjectFactory} to build {@link TypeConverter}
 */
public interface ConverterFactory {

    /**
     * Build converter of given type
     *
     * @param converterClass to instantiate
     * @param extraContext a Map of extra context which uses the same keys as the {@link com.opensymphony.xwork2.ActionContext}
     * @return instance of converterClass with inject dependencies
     */
    TypeConverter buildConverter(Class<? extends TypeConverter> converterClass, Map<String, Object> extraContext) throws Exception;

}
