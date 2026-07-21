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
package org.apache.struts2.conversion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Default implementation of {@link TypeConverterHolder}
 */
public class StrutsTypeConverterHolder implements TypeConverterHolder {

    private static final Logger LOG = LogManager.getLogger(StrutsTypeConverterHolder.class);

    /**
     * Record class and its type converter mapping.
     * <pre>
     * - String - classname as String
     * - TypeConverter - instance of TypeConverter
     * </pre>
     */
    private final Map<String, TypeConverter> defaultMappings = new ConcurrentHashMap<>();  // non-action (eg. returned value)

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
    private final Map<Class, Map<String, Object>> mappings = new ConcurrentHashMap<>(); // action

    /**
     * Unavailable target class conversion mappings, serves as a simple cache.
     */
    private final Set<Class> noMapping = ConcurrentHashMap.newKeySet(); // action

    /**
     * Record classes that doesn't have conversion mapping defined.
     * <pre>
     * - String -&gt; classname as String
     * </pre>
     *
     * @deprecated since 7.3.0, this field is an implementation detail and will be made private.
     */
    @Deprecated
    protected final Set<String> unknownMappings = ConcurrentHashMap.newKeySet();     // non-action (eg. returned value)

    @Override
    public void addDefaultMapping(String className, TypeConverter typeConverter) {
        if (typeConverter == null) {
            LOG.warn("Ignoring null TypeConverter registered for class [{}]", className);
            return;
        }
        defaultMappings.put(className, typeConverter);
        unknownMappings.remove(className);
    }

    @Override
    public boolean containsDefaultMapping(String className) {
        return defaultMappings.containsKey(className);
    }

    @Override
    public TypeConverter getDefaultMapping(String className) {
        return defaultMappings.get(className);
    }

    @Override
    @Deprecated
    public Map<String, Object> getMapping(Class clazz) {
        return mappings.get(clazz);
    }

    @Override
    @Deprecated
    public void addMapping(Class clazz, Map<String, Object> mapping) {
        mappings.put(clazz, mapping);
    }

    @Override
    @Deprecated
    public boolean containsNoMapping(Class clazz) {
        return noMapping.contains(clazz);
    }

    @Override
    public Map<String, Object> computeMappingIfAbsent(Class clazz, Function<Class, Map<String, Object>> builder) {
        if (noMapping.contains(clazz)) {
            return Collections.emptyMap();
        }
        Map<String, Object> mapping = mappings.computeIfAbsent(clazz, c -> {
            Map<String, Object> built = builder.apply(c);
            return (built == null || built.isEmpty()) ? null : built;
        });
        if (mapping == null) {
            noMapping.add(clazz);
            return Collections.emptyMap();
        }
        return mapping;
    }

    @Override
    public void addNoMapping(Class clazz) {
        noMapping.add(clazz);
    }

    @Override
    public boolean containsUnknownMapping(String className) {
        return unknownMappings.contains(className);
    }

    @Override
    public void addUnknownMapping(String className) {
        unknownMappings.add(className);
    }

}
