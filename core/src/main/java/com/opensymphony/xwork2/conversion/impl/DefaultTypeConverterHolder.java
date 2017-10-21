/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
    private HashMap<String, TypeConverter> defaultMappings = new HashMap<>();  // non-action (eg. returned value)

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
    private HashMap<Class, Map<String, Object>> mappings = new HashMap<>(); // action

    /**
     * Unavailable target class conversion mappings, serves as a simple cache.
     */
    private HashSet<Class> noMapping = new HashSet<>(); // action

    /**
     * Record classes that doesn't have conversion mapping defined.
     * <pre>
     * - String -&gt; classname as String
     * </pre>
     */
    protected HashSet<String> unknownMappings = new HashSet<>();     // non-action (eg. returned value)

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
