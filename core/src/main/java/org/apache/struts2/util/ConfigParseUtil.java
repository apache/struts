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
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.ognl.OgnlUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.apache.struts2.util.TextParseUtil.commaDelimitedStringToSet;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.strip;

public class ConfigParseUtil {
    // Size the cache to prevent excessive memory usage in environments with many classloaders and/or large numbers of classes being validated.
    // While still providing a reasonable caching benefit for common cases (e.g. multiple Struts instances in the same container, or multiple calls to validate the same class across different containers).
    private static final int MAX_CLASS_CACHE_SIZE = 50;

    private static final Cache<ClassLookupKey, Class<?>> VALIDATED_CLASS_CACHE = Caffeine.newBuilder()
            .maximumSize(MAX_CLASS_CACHE_SIZE)
            .build();

    private ConfigParseUtil() {
    }

    public static Set<String> toClassesSet(String newDelimitedClasses) throws ConfigurationException {
        Set<String> classNames = commaDelimitedStringToSet(newDelimitedClasses);
        validateClasses(classNames, OgnlUtil.class.getClassLoader());
        return unmodifiableSet(classNames);
    }

    public static Set<Class<?>> toClassObjectsSet(String newDelimitedClasses) throws ConfigurationException {
        Set<String> classNames = commaDelimitedStringToSet(newDelimitedClasses);
        return unmodifiableSet(validateClasses(classNames, OgnlUtil.class.getClassLoader()));
    }

    public static Set<String> toNewClassesSet(Set<String> oldClasses, String newDelimitedClasses) throws ConfigurationException {
        Set<String> classNames = commaDelimitedStringToSet(newDelimitedClasses);
        validateClasses(classNames, OgnlUtil.class.getClassLoader());
        Set<String> excludedClasses = new HashSet<>(oldClasses);
        excludedClasses.addAll(classNames);
        return unmodifiableSet(excludedClasses);
    }

    public static Set<Pattern> toNewPatternsSet(Set<Pattern> oldPatterns, String newDelimitedPatterns) throws ConfigurationException {
        Set<String> patterns = commaDelimitedStringToSet(newDelimitedPatterns);
        Set<Pattern> newPatterns = new HashSet<>(oldPatterns);
        for (String pattern: patterns) {
            try {
                newPatterns.add(Pattern.compile(pattern));
            } catch (PatternSyntaxException e) {
                throw new ConfigurationException("Excluded package name patterns could not be parsed due to invalid regex: " + pattern, e);
            }
        }
        return unmodifiableSet(newPatterns);
    }

    public static Set<Class<?>> validateClasses(Set<String> classNames, ClassLoader validatingClassLoader) throws ConfigurationException {
        Set<Class<?>> classes = new HashSet<>();
        for (String className : classNames) {
            try {
                classes.add(loadAndCacheClass(validatingClassLoader, className));
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException("Cannot load class for exclusion/exemption configuration: " + className, e);
            }
        }
        return classes;
    }

    private static Class<?> loadAndCacheClass(ClassLoader validatingClassLoader, String className) throws ClassNotFoundException {
        ClassLookupKey lookupKey = new ClassLookupKey(classLoaderName(validatingClassLoader), className);

        try {
            return VALIDATED_CLASS_CACHE.get(lookupKey, key -> {
                try {
                    return validatingClassLoader.loadClass(key.className);
                } catch (ClassNotFoundException e) {
                    throw new ClassLookupException(e);
                }
            });
        } catch (ClassLookupException e) {
            throw (ClassNotFoundException) e.getCause();
        }
    }

    private static String classLoaderName(ClassLoader classLoader) {
        return String.valueOf(classLoader);
    }

    private static final class ClassLookupKey {
        private final String classLoaderName;
        private final String className;

        private ClassLookupKey(String classLoaderName, String className) {
            this.classLoaderName = classLoaderName;
            this.className = className;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ClassLookupKey that = (ClassLookupKey) o;
            return Objects.equals(classLoaderName, that.classLoaderName) && Objects.equals(className, that.className);
        }

        @Override
        public int hashCode() {
            return Objects.hash(classLoaderName, className);
        }
    }

    private static final class ClassLookupException extends RuntimeException {
        private ClassLookupException(ClassNotFoundException cause) {
            super(cause);
        }
    }

    public static Set<String> toPackageNamesSet(String newDelimitedPackageNames) throws ConfigurationException {
        Set<String> packageNames = commaDelimitedStringToSet(newDelimitedPackageNames)
                .stream().map(s -> strip(s, ".")).collect(toSet());
        validatePackageNames(packageNames);
        return unmodifiableSet(packageNames);
    }

    public static Set<String> toNewPackageNamesSet(Collection<String> oldPackageNames, String newDelimitedPackageNames) throws ConfigurationException {
        Set<String> packageNames = commaDelimitedStringToSet(newDelimitedPackageNames)
                .stream().map(s -> strip(s, ".")).collect(toSet());
        validatePackageNames(packageNames);
        Set<String> newPackageNames = new HashSet<>(oldPackageNames);
        newPackageNames.addAll(packageNames);
        return unmodifiableSet(newPackageNames);
    }

    public static void validatePackageNames(Collection<String> packageNames) {
        if (packageNames.stream().anyMatch(s -> Pattern.compile("\\s").matcher(s).find())) {
            throw new ConfigurationException("Excluded package names could not be parsed due to erroneous whitespace characters: " + packageNames);
        }
    }
}
