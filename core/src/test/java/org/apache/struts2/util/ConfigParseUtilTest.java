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

    public void testValidateClassesEnforcesOuterCacheMaximumSize() {
        Set<String> classNames = Collections.singleton(String.class.getName());

        for (int i = 0; i < 60; i++) {
            CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader(), "loader-" + i);
            ConfigParseUtil.validateClasses(classNames, loader);
        }

        Cache<Object, Object> cache = validatedClassCache();
        cache.cleanUp();

        assertTrue("Outer cache size should not exceed configured maximum", cache.estimatedSize() <= outerCacheLimit());
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

        // Verify each class was loaded only once per loader through the cache
        assertEquals(1, loader.getLoadCount(String.class.getName()));
        assertEquals(1, loader.getLoadCount(Integer.class.getName()));
        assertEquals(1, loader.getLoadCount(Boolean.class.getName()));
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

    public void testInnerCacheEnforcesMaximumSizePerClassLoader() {
        CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader(), "many-classes-loader");
        Set<String> classNames = new java.util.HashSet<>();
        
        // Use real built-in classes that are guaranteed to exist (70+ classes)
        String[] realClasses = {
            String.class.getName(), Integer.class.getName(), Long.class.getName(),
            Double.class.getName(), Float.class.getName(), Boolean.class.getName(),
            Character.class.getName(), Byte.class.getName(), Short.class.getName(),
            Object.class.getName(), Class.class.getName(), Thread.class.getName(),
            Exception.class.getName(), RuntimeException.class.getName(), java.io.File.class.getName(),
            java.util.ArrayList.class.getName(), java.util.HashMap.class.getName(), 
            java.util.HashSet.class.getName(), java.util.LinkedList.class.getName(),
            java.util.TreeMap.class.getName(), java.util.regex.Pattern.class.getName(),
            java.io.InputStream.class.getName(), java.io.OutputStream.class.getName(),
            java.io.Reader.class.getName(), java.io.Writer.class.getName(),
            java.net.URL.class.getName(), java.net.URI.class.getName(),
            java.nio.file.Path.class.getName(), java.nio.file.Paths.class.getName(),
            java.nio.file.Files.class.getName(), java.nio.file.StandardOpenOption.class.getName(),
            java.lang.reflect.Method.class.getName(), java.lang.reflect.Field.class.getName(),
            java.lang.reflect.Constructor.class.getName(), java.lang.annotation.Annotation.class.getName(),
            java.util.concurrent.Future.class.getName(), java.util.concurrent.ExecutorService.class.getName(),
            java.time.LocalDate.class.getName(), java.time.LocalTime.class.getName(),
            java.time.LocalDateTime.class.getName(), java.time.ZonedDateTime.class.getName(),
            java.time.Instant.class.getName(), java.time.Duration.class.getName(),
            java.time.Period.class.getName(), java.util.stream.Stream.class.getName(),
            java.util.stream.Collectors.class.getName(), java.util.Optional.class.getName(),
            java.util.function.Function.class.getName(), java.util.function.Predicate.class.getName(),
            java.util.function.Consumer.class.getName(), java.util.function.Supplier.class.getName(),
            java.util.function.BiFunction.class.getName(), java.util.function.BiConsumer.class.getName(),
            java.util.function.BiPredicate.class.getName(), java.lang.StringBuilder.class.getName(),
            java.lang.StringBuffer.class.getName(), java.util.Collections.class.getName(),
            java.util.Arrays.class.getName(), java.util.Objects.class.getName(),
            java.util.UUID.class.getName(), java.util.Locale.class.getName(),
            java.util.TimeZone.class.getName(), java.util.Calendar.class.getName(),
            java.util.Date.class.getName(), java.util.GregorianCalendar.class.getName(),
            java.util.Random.class.getName(), java.util.Scanner.class.getName(),
            java.util.Formatter.class.getName(), java.util.Properties.class.getName(),
            java.util.concurrent.ConcurrentHashMap.class.getName(), java.util.concurrent.atomic.AtomicInteger.class.getName(),
            java.util.concurrent.atomic.AtomicLong.class.getName(), java.util.concurrent.locks.Lock.class.getName(),
            java.util.concurrent.locks.ReentrantLock.class.getName(), java.lang.Comparable.class.getName(),
            java.lang.Cloneable.class.getName(), java.io.Serializable.class.getName(),
            java.nio.ByteBuffer.class.getName(), java.nio.CharBuffer.class.getName(),
            java.nio.charset.Charset.class.getName(), java.security.MessageDigest.class.getName()
        };
        
        // Add all real classes to the set (should be 72)
        for (String className : realClasses) {
            classNames.add(className);
        }

        assertTrue("Test setup requires more class names than inner cache capacity", classNames.size() > innerCacheLimit());
        
        ConfigParseUtil.validateClasses(classNames, loader);

        Cache<Object, Object> innerCache = innerCacheFor(loader);
        innerCache.cleanUp();
        assertTrue("Inner cache size should not exceed configured maximum per loader", innerCache.estimatedSize() <= innerCacheLimit());
    }

    public void testInnerCacheIndependentPerClassLoader() {
        CountingClassLoader loaderA = new CountingClassLoader(getClass().getClassLoader(), "loader-a");
        CountingClassLoader loaderB = new CountingClassLoader(getClass().getClassLoader(), "loader-b");
        
        Set<String> classNamesA = new java.util.HashSet<>();
        classNamesA.add(String.class.getName());
        classNamesA.add(Integer.class.getName());
        
        Set<String> classNamesB = new java.util.HashSet<>();
        classNamesB.add(Boolean.class.getName());
        classNamesB.add(Double.class.getName());
        
        ConfigParseUtil.validateClasses(classNamesA, loaderA);
        ConfigParseUtil.validateClasses(classNamesB, loaderB);
        
        // Validate load counts show independent caching - each loader loaded its own classes once
        assertEquals(1, loaderA.getLoadCount(String.class.getName()));
        assertEquals(1, loaderA.getLoadCount(Integer.class.getName()));
        assertEquals(0, loaderB.getLoadCount(String.class.getName()));
        assertEquals(1, loaderB.getLoadCount(Boolean.class.getName()));
        assertEquals(1, loaderB.getLoadCount(Double.class.getName()));
    }

    public void testInnerCacheReusesCachedClassLookupForSameLoader() {
        CountingClassLoader loader = new CountingClassLoader(getClass().getClassLoader(), "reload-test-loader");
        Set<String> classNames = Collections.singleton(String.class.getName());
        
        // Load once
        ConfigParseUtil.validateClasses(classNames, loader);
        assertEquals(1, loader.getStringClassLoads());
        
        // Reload - should use cache
        ConfigParseUtil.validateClasses(classNames, loader);
        assertEquals(1, loader.getStringClassLoads());
    }

    public void testMultipleClassLoadersWithDistinctInnerCaches() {
        CountingClassLoader loader1 = new CountingClassLoader(getClass().getClassLoader(), "distinct-1");
        CountingClassLoader loader2 = new CountingClassLoader(getClass().getClassLoader(), "distinct-2");
        CountingClassLoader loader3 = new CountingClassLoader(getClass().getClassLoader(), "distinct-3");
        
        Set<String> classNames = Collections.singleton(String.class.getName());
        
        ConfigParseUtil.validateClasses(classNames, loader1);
        ConfigParseUtil.validateClasses(classNames, loader2);
        ConfigParseUtil.validateClasses(classNames, loader3);
        
        // Each loader should have loaded String once independently
        assertEquals(1, loader1.getLoadCount(String.class.getName()));
        assertEquals(1, loader2.getLoadCount(String.class.getName()));
        assertEquals(1, loader3.getLoadCount(String.class.getName()));
        
        // Revalidating with loader1 should still use cache
        ConfigParseUtil.validateClasses(classNames, loader1);
        assertEquals(1, loader1.getLoadCount(String.class.getName()));
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
        private final java.util.Map<String, Integer> loadCounts = new java.util.HashMap<>();

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
