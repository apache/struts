package com.opensymphony.xwork2.conversion;

import java.util.Map;

/**
 * Holds all mappings related to {@link TypeConverter}s
 */
public interface TypeConverterHolder {

    /**
     * Adds mapping for default type converters - application scoped
     *
     * @param className     name of the class with associated converter
     * @param typeConverter {@link TypeConverter} instance for associated class
     */
    void addDefaultMapping(String className, TypeConverter typeConverter);

    /**
     * Checks if converter was already defined for given class
     *
     * @param className name of the class to check for
     * @return true if default mapping was already specified
     */
    boolean containsDefaultMapping(String className);

    /**
     * Returns instance of {@link TypeConverter} associated with given class
     *
     * @param className name of the class to return converter for
     * @return instance of {@link TypeConverter} to be used to convert class
     */
    TypeConverter getDefaultMapping(String className);

    /**
     * Target class conversion Mappings.
     *
     * @param clazz class to convert to/from
     * @return {@link TypeConverter} for given class
     */
    Map<String, Object> getMapping(Class clazz);

    /**
     * Assign mapping of converters for given class
     *
     * @param clazz   class to convert to/from
     * @param mapping property converters
     */
    void addMapping(Class clazz, Map<String, Object> mapping);

    /**
     * Check if there is no mapping for given class to convert
     *
     * @param clazz class to convert to/from
     * @return true if mapping couldn't be found
     */
    boolean containsNoMapping(Class clazz);

    /**
     * Adds no mapping flag for give class
     *
     * @param clazz class to register missing converter
     */
    void addNoMapping(Class clazz);

    /**
     * Checks if no mapping was defined for given class name
     * FIXME lukaszlenart: maybe it should be merged with NoMapping
     *
     * @param className name of the class to check for
     * @return true if converter was defined for given class name
     */
    boolean containsUnknownMapping(String className);

    /**
     * Adds no converter flag for given class name
     * FIXME lukaszlenart: maybe it should be merged with NoMapping
     *
     * @param className name of the class to mark there is no converter for it
     */
    void addUnknownMapping(String className);

}
