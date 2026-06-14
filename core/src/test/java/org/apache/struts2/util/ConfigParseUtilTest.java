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
package org.apache.struts2.util;

import com.github.benmanes.caffeine.cache.Cache;
import org.apache.struts2.config.ConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConfigParseUtilTest {

    @Before
    public void setUp() {
        validatedClassCache().invalidateAll();
    }

    @After
    public void tearDown() {
        validatedClassCache().invalidateAll();
    }

    /**
     * (a) Single-loader caching: one loader validates several distinct classes; repeating the call
     * loads each class exactly once. Covers both "repeated calls hit the cache" and "the inner cache
     * is keyed per class name".
     */
    @Test
    public void testSameLoaderCachesEachDistinctClassOnce() {
        CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader(), "single-loader");
        Set<String> classNames = new HashSet<>();
        classNames.add(String.class.getName());
        classNames.add(Integer.class.getName());
        classNames.add(Boolean.class.getName());

        ConfigParseUtil.validateClasses(classNames, loader);
        ConfigParseUtil.validateClasses(classNames, loader);

        assertEquals(1, loader.getLoadCount(String.class.getName()));
        assertEquals(1, loader.getLoadCount(Integer.class.getName()));
        assertEquals(1, loader.getLoadCount(Boolean.class.getName()));
    }

    /**
     * (b) Per-loader isolation: the outer cache is keyed by classloader identity, not by toString().
     * Two loaders that share the same toString() each load the class once, and re-validating one
     * loader still hits its own cache.
     */
    @Test
    public void testDifferentLoadersWithSameNameCacheIndependently() {
        CountingClassLoader firstLoader = new CountingClassLoader(getClass().getClassLoader(), "same-name");
        CountingClassLoader secondLoader = new CountingClassLoader(getClass().getClassLoader(), "same-name");
        Set<String> classNames = Collections.singleton(String.class.getName());

        ConfigParseUtil.validateClasses(classNames, firstLoader);
        ConfigParseUtil.validateClasses(classNames, secondLoader);

        assertEquals(1, firstLoader.getStringClassLoads());
        assertEquals(1, secondLoader.getStringClassLoads());

        // Re-validating the first loader still hits its own cache.
        ConfigParseUtil.validateClasses(classNames, firstLoader);
        assertEquals(1, firstLoader.getStringClassLoads());
    }

    /**
     * Negative case: a missing class throws ConfigurationException (cause ClassNotFoundException) on
     * every call, and the failure is not cached (each call re-attempts the load).
     */
    @Test
    public void testMissingClassThrowsAndIsNotCached() {
        String missingClassName = "org.apache.struts2.util.NonExistingClassForValidationTest";
        Set<String> classNames = Collections.singleton(missingClassName);
        int[] missingClassLoads = new int[1];
        ClassLoader loader = new ClassLoader(getClass().getClassLoader()) {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                if (missingClassName.equals(name)) {
                    missingClassLoads[0]++;
                    throw new ClassNotFoundException(name);
                }
                return super.loadClass(name);
            }

            @Override
            public String toString() {
                return "missing-class-loader";
            }
        };

        for (int i = 0; i < 2; i++) {
            try {
                ConfigParseUtil.validateClasses(classNames, loader);
                fail("Expected ConfigurationException for class: " + missingClassName);
            } catch (ConfigurationException e) {
                assertTrue(e.getMessage().contains(missingClassName));
                assertNotNull(e.getCause());
                assertEquals(ClassNotFoundException.class, e.getCause().getClass());
            }
        }

        assertEquals(2, missingClassLoads[0]);
    }

    /**
     * (c) Outer cache bound: registering more classloaders than the maximum keeps the outer cache at
     * or below its configured size.
     */
    @Test
    public void testOuterCacheBoundedByMaxClassloaders() {
        Set<String> classNames = Collections.singleton(String.class.getName());

        for (int i = 0; i < outerCacheLimit() + 10; i++) {
            CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader(), "loader-" + i);
            ConfigParseUtil.validateClasses(classNames, loader);
        }

        Cache<Object, Object> cache = validatedClassCache();
        cache.cleanUp();

        assertTrue("Outer cache size should not exceed configured maximum",
                cache.estimatedSize() <= outerCacheLimit());
    }

    /**
     * (c) Inner cache bound: validating more class names than the per-loader maximum keeps that
     * loader's inner cache at or below its configured size. Synthetic names are resolved to a real
     * class so the count is driven by distinct keys, not by which JDK classes happen to exist.
     */
    @Test
    public void testInnerCacheBoundedByMaxClassesPerLoader() {
        int limit = innerCacheLimit();
        ClassLoader loader = new ClassLoader(getClass().getClassLoader()) {
            @Override
            public Class<?> loadClass(String name) {
                // Resolve any synthetic name to a strongly-reachable class so weakValues never evicts it.
                return Object.class;
            }

            @Override
            public String toString() {
                return "inner-bound-loader";
            }
        };

        Set<String> classNames = new LinkedHashSet<>();
        for (int i = 0; i <= limit + 10; i++) {
            classNames.add("synthetic.Class" + i);
        }
        assertTrue("Test must request more class names than the inner cache capacity",
                classNames.size() > limit);

        ConfigParseUtil.validateClasses(classNames, loader);

        Cache<Object, Object> innerCache = innerCacheFor(loader);
        innerCache.cleanUp();
        assertTrue("Inner cache size should not exceed configured maximum per loader",
                innerCache.estimatedSize() <= limit);
    }

    @SuppressWarnings("unchecked")
    private static Cache<Object, Object> validatedClassCache() {
        try {
            Field cacheField = ConfigParseUtil.class.getDeclaredField("VALIDATED_CLASS_CACHE");
            cacheField.setAccessible(true);
            return (Cache<Object, Object>) cacheField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AssertionError("Cannot access ConfigParseUtil cache field", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Cache<Object, Object> innerCacheFor(ClassLoader loader) {
        Cache<Object, Object> outer = validatedClassCache();
        Object inner = outer.getIfPresent(loader);
        assertNotNull("Expected an inner cache entry for loader", inner);
        return (Cache<Object, Object>) inner;
    }

    private static int outerCacheLimit() {
        return intConstant("MAX_CLASSLOADER_CACHE_SIZE");
    }

    private static int innerCacheLimit() {
        return intConstant("MAX_CLASS_CACHE_PER_LOADER_SIZE");
    }

    private static int intConstant(String fieldName) {
        try {
            Field field = ConfigParseUtil.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AssertionError("Cannot access ConfigParseUtil constant: " + fieldName, e);
        }
    }

    private static final class CountingClassLoader extends ClassLoader {
        private final String loaderName;
        private final Map<String, Integer> loadCounts = new HashMap<>();

        private CountingClassLoader(ClassLoader parent, String loaderName) {
            super(parent);
            this.loaderName = loaderName;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            loadCounts.merge(name, 1, Integer::sum);
            return super.loadClass(name);
        }

        private int getStringClassLoads() {
            return loadCounts.getOrDefault(String.class.getName(), 0);
        }

        private int getLoadCount(String className) {
            return loadCounts.getOrDefault(className, 0);
        }

        @Override
        public String toString() {
            return loaderName;
        }
    }
}
