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

import org.apache.struts2.XWorkTestCase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the {@code default} body of {@link TypeConverterHolder#computeMappingIfAbsent}, the
 * SPI compatibility fallback kept for third-party {@link TypeConverterHolder} implementations
 * written before 7.3.0 that do not override the method. {@link StrutsTypeConverterHolder} - the
 * only in-tree implementation - does override it, so nothing else in the codebase exercises the
 * default body; these tests do so directly against a minimal implementation that deliberately
 * implements only the original (non-default) primitives, using plain, non-concurrent collections,
 * mirroring what a pre-7.3.0 holder looks like.
 */
public class TypeConverterHolderTest extends XWorkTestCase {

    /**
     * Minimal, non-thread-safe {@link TypeConverterHolder} implementing only the pre-7.3.0
     * primitives. Deliberately does not override {@link TypeConverterHolder#computeMappingIfAbsent},
     * so calls against it run the interface's default check-then-act body.
     */
    private static class LegacyTypeConverterHolder implements TypeConverterHolder {
        private final Map<String, TypeConverter> defaultMappings = new HashMap<>();
        private final Map<Class, Map<String, Object>> mappings = new HashMap<>();
        private final Set<Class> noMapping = new HashSet<>();
        private final Set<String> unknownMappings = new HashSet<>();

        @Override
        public void addDefaultMapping(String className, TypeConverter typeConverter) {
            defaultMappings.put(className, typeConverter);
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
        public Map<String, Object> getMapping(Class clazz) {
            return mappings.get(clazz);
        }

        @Override
        public void addMapping(Class clazz, Map<String, Object> mapping) {
            mappings.put(clazz, mapping);
        }

        @Override
        public boolean containsNoMapping(Class clazz) {
            return noMapping.contains(clazz);
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

    public void testDefaultComputeMappingIfAbsentBuildsOnceAndCaches() {
        LegacyTypeConverterHolder holder = new LegacyTypeConverterHolder();
        AtomicInteger builds = new AtomicInteger();

        Map<String, Object> first = holder.computeMappingIfAbsent(String.class, clazz -> {
            builds.incrementAndGet();
            Map<String, Object> built = new HashMap<>();
            built.put("someProperty", "someConverter");
            return built;
        });
        Map<String, Object> second = holder.computeMappingIfAbsent(String.class, clazz -> {
            throw new AssertionError("builder must not run again once the mapping is cached");
        });

        assertThat(builds.get()).isEqualTo(1);
        assertThat(first).containsEntry("someProperty", "someConverter");
        assertThat(second).isSameAs(first);
    }

    public void testDefaultComputeMappingIfAbsentNegativeCachesEmptyResult() {
        LegacyTypeConverterHolder holder = new LegacyTypeConverterHolder();
        AtomicInteger builds = new AtomicInteger();

        Map<String, Object> result = holder.computeMappingIfAbsent(String.class, clazz -> {
            builds.incrementAndGet();
            return new HashMap<>();
        });

        assertThat(result).isNotNull().isEmpty();
        assertThat(builds.get()).as("an empty build result must be negative cached").isEqualTo(1);
        assertThat(holder.containsNoMapping(String.class)).isTrue();
    }

    public void testDefaultComputeMappingIfAbsentNegativeCachesNullResult() {
        LegacyTypeConverterHolder holder = new LegacyTypeConverterHolder();

        Map<String, Object> result = holder.computeMappingIfAbsent(String.class, clazz -> null);

        assertThat(result).isNotNull().isEmpty();
        assertThat(holder.containsNoMapping(String.class)).isTrue();
    }

    public void testDefaultComputeMappingIfAbsentShortCircuitsOnKnownNoMapping() {
        LegacyTypeConverterHolder holder = new LegacyTypeConverterHolder();
        holder.addNoMapping(String.class);

        Map<String, Object> result = holder.computeMappingIfAbsent(String.class, clazz -> {
            throw new AssertionError("builder must not run for a class already flagged as having no mapping");
        });

        assertThat(result).isNotNull().isEmpty();
    }
}
