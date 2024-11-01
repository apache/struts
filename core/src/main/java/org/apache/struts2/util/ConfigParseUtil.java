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

import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.ognl.OgnlUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.apache.struts2.util.TextParseUtil.commaDelimitedStringToSet;
import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.strip;

public class ConfigParseUtil {

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
                classes.add(validatingClassLoader.loadClass(className));
            } catch (ClassNotFoundException e) {
                throw new ConfigurationException("Cannot load class for exclusion/exemption configuration: " + className, e);
            }
        }
        return classes;
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
