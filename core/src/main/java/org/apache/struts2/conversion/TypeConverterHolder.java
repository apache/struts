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

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

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
     * @deprecated since 7.3.0, use {@link #computeMappingIfAbsent(Class, Function)} which resolves
     * and caches the mapping atomically instead of requiring a check-then-act at the call site.
     */
    @Deprecated
    Map<String, Object> getMapping(Class clazz);

    /**
     * Assign mapping of converters for given class
     *
     * @param clazz   class to convert to/from
     * @param mapping property converters
     * @deprecated since 7.3.0, use {@link #computeMappingIfAbsent(Class, Function)} which stores
     * the built mapping itself.
     */
    @Deprecated
    void addMapping(Class clazz, Map<String, Object> mapping);

    /**
     * Check if there is no mapping for given class to convert
     *
     * @param clazz class to convert to/from
     * @return true if mapping couldn't be found
     * @deprecated since 7.3.0, use {@link #computeMappingIfAbsent(Class, Function)} which returns
     * an empty map for classes known to have no mapping.
     */
    @Deprecated
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

    /**
     * Returns the property-converter mapping for the given class, building and caching it on first
     * use. Never returns {@code null}: a class known to have no mapping yields
     * {@link Collections#emptyMap()}.
     *
     * <p>If the builder returns {@code null} or an empty map, the class is recorded in the negative
     * cache so the builder is not invoked for it again.</p>
     *
     * <p>Implementations are expected to make this atomic so that the builder runs at most once per
     * class. The default implementation is a non-atomic check-then-act using the deprecated
     * primitives, preserving pre-7.3.0 behaviour for third-party holders that do not override it.</p>
     *
     * @param clazz   class to convert to/from
     * @param builder builds the property-converter mapping for the class when it is not yet cached
     * @return the mapping for the class, or an empty map if it has none
     * @since 7.3.0
     */
    @SuppressWarnings("deprecation")
    default Map<String, Object> computeMappingIfAbsent(Class clazz, Function<Class, Map<String, Object>> builder) {
        if (containsNoMapping(clazz)) {
            return Collections.emptyMap();
        }
        Map<String, Object> mapping = getMapping(clazz);
        if (mapping != null) {
            return mapping;
        }
        mapping = builder.apply(clazz);
        if (mapping == null || mapping.isEmpty()) {
            addNoMapping(clazz);
            return Collections.emptyMap();
        }
        addMapping(clazz, mapping);
        return mapping;
    }

}
