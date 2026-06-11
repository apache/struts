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
import junit.framework.TestCase;
import org.apache.struts2.config.ConfigurationException;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;

public class ConfigParseUtilTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validatedClassCache().invalidateAll();
    }

    @Override
    protected void tearDown() throws Exception {
        validatedClassCache().invalidateAll();
        super.tearDown();
    }

    public void testValidateClassesCachesByClassLoaderAndClassName() {
        CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader(), "loader-one");
        Set<String> classNames = Collections.singleton(String.class.getName());

        ConfigParseUtil.validateClasses(classNames, loader);
        ConfigParseUtil.validateClasses(classNames, loader);

        assertEquals(1, loader.getStringClassLoads());
    }

    public void testValidateClassesCachesAcrossMultipleRepeatedCallsWithSameClassLoader() {
        CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader(), "loader-one");
        Set<String> classNames = Collections.singleton(String.class.getName());

        for (int i = 0; i < 10; i++) {
            ConfigParseUtil.validateClasses(classNames, loader);
        }

        assertEquals(1, loader.getStringClassLoads());
    }

    public void testValidateClassesSeparatesEntriesAcrossDifferentClassLoaders() {
        CountingClassLoader firstLoader = new CountingClassLoader(getClass().getClassLoader(), "loader-one");
        CountingClassLoader secondLoader = new CountingClassLoader(getClass().getClassLoader(), "loader-two");
        Set<String> classNames = Collections.singleton(String.class.getName());

        ConfigParseUtil.validateClasses(classNames, firstLoader);
        ConfigParseUtil.validateClasses(classNames, secondLoader);

        assertEquals(1, firstLoader.getStringClassLoads());
        assertEquals(1, secondLoader.getStringClassLoads());
    }

    public void testValidateClassesSeparatesEntriesAcrossDifferentClassLoadersWithSameToString() {
        CountingClassLoader firstLoader = new CountingClassLoader(getClass().getClassLoader(), "same-loader-name");
        CountingClassLoader secondLoader = new CountingClassLoader(getClass().getClassLoader(), "same-loader-name");
        Set<String> classNames = Collections.singleton(String.class.getName());

        ConfigParseUtil.validateClasses(classNames, firstLoader);
        ConfigParseUtil.validateClasses(classNames, secondLoader);

        assertEquals(1, firstLoader.getStringClassLoads());
        assertEquals(1, secondLoader.getStringClassLoads());
    }

    public void testValidateClassesCacheIsLimitedTo50Entries() {
        Set<String> classNames = Collections.singleton(String.class.getName());

        for (int i = 0; i < 60; i++) {
            CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader(), "loader-" + i);
            ConfigParseUtil.validateClasses(classNames, loader);
        }

        Cache<Object, Object> cache = validatedClassCache();
        cache.cleanUp();

        assertTrue(cache.estimatedSize() <= 50);
    }

    public void testValidateClassesThrowsForNonExistingClassNameOnEachCall() {
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

    public void testValidateClassesLoadsMultipleDifferentClassesPerLoaderOnce() {
        CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader(), "multi-class-loader");
        Set<String> classNames = new java.util.HashSet<>();
        classNames.add(String.class.getName());
        classNames.add(Integer.class.getName());
        classNames.add(Boolean.class.getName());

        ConfigParseUtil.validateClasses(classNames, loader);
        ConfigParseUtil.validateClasses(classNames, loader);

        assertEquals(1, loader.getStringClassLoads());
    }

    public void testValidateClassesNestedCacheIsReusedForSameLoader() {
        CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader(), "reuse-cache-loader");
        Set<String> classNames = Collections.singleton(String.class.getName());

        ConfigParseUtil.validateClasses(classNames, loader);
        int firstCallLoadCount = loader.getStringClassLoads();

        ConfigParseUtil.validateClasses(classNames, loader);
        int secondCallLoadCount = loader.getStringClassLoads();

        assertEquals(1, firstCallLoadCount);
        assertEquals(1, secondCallLoadCount);
    }

    public void testValidateClassesEnforcesMaximumCacheSize() {
        Set<String> classNames = Collections.singleton(String.class.getName());
        Cache<Object, Object> cache = validatedClassCache();

        for (int i = 0; i < 60; i++) {
            CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader(), "size-limit-loader-" + i);
            ConfigParseUtil.validateClasses(classNames, loader);
        }

        cache.cleanUp();
        assertTrue("Cache size should not exceed 50 after loading 60 loaders", cache.estimatedSize() <= 50);
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

    private static final class CountingClassLoader extends ClassLoader {
        private final String loaderName;
        private int stringClassLoads;

        private CountingClassLoader(ClassLoader parent, String loaderName) {
            super(parent);
            this.loaderName = loaderName;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (String.class.getName().equals(name)) {
                stringClassLoads++;
            }
            return super.loadClass(name);
        }

        private int getStringClassLoads() {
            return stringClassLoads;
        }

        @Override
        public String toString() {
            return loaderName;
        }
    }
}
