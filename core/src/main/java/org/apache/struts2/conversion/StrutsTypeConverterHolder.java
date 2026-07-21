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
import java.util.HashMap;
import java.util.HashSet;
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
     * Marker stored in {@link #mappings} for classes known to have no conversion mapping, so that
     * negative results are cached in the same atomic operation as positive ones. Deliberately a
     * distinct instance rather than {@link Collections#emptyMap()}, whose shared singleton could
     * collide with an empty mapping supplied by a caller.
     */
    private static final Map<String, Object> NO_MAPPING = Collections.unmodifiableMap(new HashMap<>());

    /**
     * Record classes that doesn't have conversion mapping defined.
     * <pre>
     * - String -&gt; classname as String
     * </pre>
     *
     * @deprecated since 7.3.0, unused - superseded by internal concurrent storage. Retained only
     * for binary compatibility with subclasses compiled against earlier versions, and will be
     * removed in a future release.
     */
    @Deprecated(since = "7.3.0", forRemoval = true)
    protected HashSet<String> unknownMappings = new HashSet<>();

    /**
     * Actual storage for classes with no registered converter. Concurrent, so that lock-free
     * readers in {@code XWorkConverter.lookup} cannot race writers.
     */
    private final Set<String> unknownMappingsInternal = ConcurrentHashMap.newKeySet();

    @Override
    public void addDefaultMapping(String className, TypeConverter typeConverter) {
        if (typeConverter == null) {
            LOG.warn("Ignoring null TypeConverter registered for class [{}]", className);
            return;
        }
        // Order is load-bearing: registering the converter before clearing the unknown flag means a
        // concurrent XWorkConverter.lookup can never observe (unknown=false, default=false) for this
        // class - a state that would otherwise send it down the lookupSuper() path and let it
        // overwrite the more specific converter being registered here with a broader one.
        defaultMappings.put(className, typeConverter);
        unknownMappingsInternal.remove(className);
    }

    @Override
    public boolean containsDefaultMapping(String className) {
        return defaultMappings.containsKey(className);
    }

    @Override
    public TypeConverter getDefaultMapping(String className) {
        return defaultMappings.get(className);
    }

    /**
     * Returns {@code null} if {@code clazz} has been flagged as having no mapping via
     * {@link #addNoMapping(Class)}, even if a real mapping was previously stored for it.
     *
     * @deprecated since 7.3.0, use {@link #computeMappingIfAbsent(Class, Function)} instead.
     */
    @Override
    @Deprecated(since = "7.3.0", forRemoval = true)
    public Map<String, Object> getMapping(Class clazz) {
        Map<String, Object> mapping = mappings.get(clazz);
        return mapping == NO_MAPPING ? null : mapping;
    }

    /**
     * @deprecated since 7.3.0, use {@link #computeMappingIfAbsent(Class, Function)} instead.
     */
    @Override
    @Deprecated(since = "7.3.0", forRemoval = true)
    public void addMapping(Class clazz, Map<String, Object> mapping) {
        mappings.put(clazz, mapping);
    }

    /**
     * @deprecated since 7.3.0, use {@link #computeMappingIfAbsent(Class, Function)} instead.
     */
    @Override
    @Deprecated(since = "7.3.0", forRemoval = true)
    public boolean containsNoMapping(Class clazz) {
        return mappings.get(clazz) == NO_MAPPING;
    }

    /**
     * Stores the {@link #NO_MAPPING} sentinel for the given class, replacing any mapping previously
     * cached for it. This matches the pre-7.3.0 effective behaviour, back when a separate no-mapping
     * collection was consulted independently of the mappings map: the only in-tree caller,
     * {@code XWorkConverter.getConverter}, checked the no-mapping flag first and short-circuited
     * before the cached mapping was ever read, so flagging a class as no-mapping made it behave as
     * though it had no mapping, real cached mapping or not. {@link Map#putIfAbsent} is deliberately
     * not used here: it would leave a stale mapping being served for a class whose conversion build
     * subsequently failed, which is a genuine behaviour change rather than a faithful port.
     */
    @Override
    public void addNoMapping(Class clazz) {
        mappings.put(clazz, NO_MAPPING);
    }

    @Override
    public Map<String, Object> computeMappingIfAbsent(Class clazz, Function<Class, Map<String, Object>> builder) {
        // Deliberately not implemented with mappings.computeIfAbsent(...): that would run the builder
        // while holding the ConcurrentHashMap's internal bin lock. The builder reaches
        // ObjectFactory.buildConverter(...), which instantiates (and, under SpringObjectFactory,
        // autowires) an arbitrary user-supplied TypeConverter - constructors, @PostConstruct,
        // afterPropertiesSet. Running that under a bin lock risks IllegalStateException("Recursive
        // update") or a self-deadlock if any of it re-enters conversion. Instead the builder runs
        // outside any lock, at the cost of allowing it to run more than once under first-access
        // contention; putIfAbsent ensures every caller still converges on the same cached instance.
        Map<String, Object> existing = mappings.get(clazz);
        if (existing != null) {
            return existing;
        }
        Map<String, Object> built = builder.apply(clazz);
        Map<String, Object> value = (built == null || built.isEmpty()) ? NO_MAPPING : built;
        Map<String, Object> previous = mappings.putIfAbsent(clazz, value);
        return previous != null ? previous : value;
    }

    @Override
    public boolean containsUnknownMapping(String className) {
        return unknownMappingsInternal.contains(className);
    }

    @Override
    public void addUnknownMapping(String className) {
        unknownMappingsInternal.add(className);
    }

}
