package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Default implementation of {@link TypeConverterHolder}
 */
public class DefaultTypeConverterHolder implements TypeConverterHolder {

    /**
     * Record class and its type converter mapping.
     * <pre>
     * - String - classname as String
     * - TypeConverter - instance of TypeConverter
     * </pre>
     */
    private HashMap<String, TypeConverter> defaultMappings = new HashMap<String, TypeConverter>();  // non-action (eg. returned value)

    /**
     * Target class conversion Mappings.
     * <pre>
     * Map<Class, Map<String, Object>>
     *  - Class -> convert to class
     *  - Map<String, Object>
     *    - String -> property name
     *                eg. Element_property, property etc.
     *    - Object -> String to represent properties
     *                eg. value part of
     *                    KeyProperty_property=id
     *             -> TypeConverter to represent an Ognl TypeConverter
     *                eg. value part of
     *                    property=foo.bar.MyConverter
     *             -> Class to represent a class
     *                eg. value part of
     *                    Element_property=foo.bar.MyObject
     * </pre>
     */
    private HashMap<Class, Map<String, Object>> mappings = new HashMap<Class, Map<String, Object>>(); // action

    /**
     * Unavailable target class conversion mappings, serves as a simple cache.
     */
    private HashSet<Class> noMapping = new HashSet<Class>(); // action

    /**
     * Record classes that doesn't have conversion mapping defined.
     * <pre>
     * - String -> classname as String
     * </pre>
     */
    protected HashSet<String> unknownMappings = new HashSet<String>();     // non-action (eg. returned value)

    public void addDefaultMapping(String className, TypeConverter typeConverter) {
        defaultMappings.put(className, typeConverter);
        if (unknownMappings.contains(className)) {
            unknownMappings.remove(className);
        }
    }

    public boolean containsDefaultMapping(String className) {
        return defaultMappings.containsKey(className);
    }

    public TypeConverter getDefaultMapping(String className) {
        return defaultMappings.get(className);
    }

    public Map<String, Object> getMapping(Class clazz) {
        return mappings.get(clazz);
    }

    public void addMapping(Class clazz, Map<String, Object> mapping) {
        mappings.put(clazz, mapping);
    }

    public boolean containsNoMapping(Class clazz) {
        return noMapping.contains(clazz);
    }

    public void addNoMapping(Class clazz) {
        noMapping.add(clazz);
    }

    public boolean containsUnknownMapping(String className) {
        return unknownMappings.contains(className);
    }

    public void addUnknownMapping(String className) {
        unknownMappings.add(className);
    }

}
