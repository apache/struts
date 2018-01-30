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
package com.opensymphony.xwork2.util;

import org.apache.commons.lang3.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <code>AnnotationUtils</code>
 * <p>
 * Various utility methods dealing with annotations
 * </p>
 *
 * @author Rainer Hermanns
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 * @author Dan Oxlade, dan d0t oxlade at gmail d0t c0m
 * @version $Id$
 */
public class AnnotationUtils {

    private static final Pattern SETTER_PATTERN = Pattern.compile("set([A-Z][A-Za-z0-9]*)$");
    private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is|has)([A-Z][A-Za-z0-9]*)$");

    /**
     * Adds all fields with the specified Annotation of class clazz and its superclasses to allFields
     *
     * @param annotationClass the {@link Annotation}s to find
     * @param clazz           The {@link Class} to inspect
     * @param allFields       list of all fields
     */
    public static void addAllFields(Class<? extends Annotation> annotationClass, Class clazz, List<Field> allFields) {

        if (clazz == null) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            Annotation ann = field.getAnnotation(annotationClass);
            if (ann != null) {
                allFields.add(field);
            }
        }
        addAllFields(annotationClass, clazz.getSuperclass(), allFields);
    }

    /**
     * Adds all methods with the specified Annotation of class clazz and its superclasses to allFields
     *
     * @param annotationClass the {@link Annotation}s to find
     * @param clazz           The {@link Class} to inspect
     * @param allMethods      list of all methods
     */
    public static void addAllMethods(Class<? extends Annotation> annotationClass, Class clazz, List<Method> allMethods) {

        if (clazz == null) {
            return;
        }

        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            Annotation ann = method.getAnnotation(annotationClass);
            if (ann != null) {
                allMethods.add(method);
            }
        }
        addAllMethods(annotationClass, clazz.getSuperclass(), allMethods);
    }

    /**
     * @param clazz         The {@link Class} to inspect
     * @param allInterfaces list of all interfaces
     */
    public static void addAllInterfaces(Class clazz, List<Class> allInterfaces) {
        if (clazz == null) {
            return;
        }

        Class[] interfaces = clazz.getInterfaces();
        allInterfaces.addAll(Arrays.asList(interfaces));
        addAllInterfaces(clazz.getSuperclass(), allInterfaces);
    }

    /**
     * Returns the property name for a method.
     * This method is independent from property fields.
     *
     * @param method The method to get the property name for.
     * @return the property name for given method; null if non could be resolved.
     */
    public static String resolvePropertyName(Method method) {

        Matcher matcher = SETTER_PATTERN.matcher(method.getName());
        if (matcher.matches() && method.getParameterTypes().length == 1) {
            String raw = matcher.group(1);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }

        matcher = GETTER_PATTERN.matcher(method.getName());
        if (matcher.matches() && method.getParameterTypes().length == 0) {
            String raw = matcher.group(2);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }

        return null;
    }

    /**
     * Returns the annotation on the given class or the package of the class. This searches up the
     * class hierarchy and the package hierarchy for the closest match.
     *
     * @param <T>             class type
     * @param clazz           The class to search for the annotation.
     * @param annotationClass The Class of the annotation.
     * @return The annotation or null.
     */
    public static <T extends Annotation> T findAnnotation(Class<?> clazz, Class<T> annotationClass) {
        T ann = clazz.getAnnotation(annotationClass);
        while (ann == null && clazz != null) {
            ann = clazz.getAnnotation(annotationClass);
            if (ann == null) {
                ann = clazz.getPackage().getAnnotation(annotationClass);
            }
            if (ann == null) {
                clazz = clazz.getSuperclass();
                if (clazz != null) {
                    ann = clazz.getAnnotation(annotationClass);
                }
            }
        }

        return ann;
    }

    /**
     * Returns a list of the annotation on the given class or the package of the class.
     * This searches up the class hierarchy and the package hierarchy.
     *
     * @param <T>             class type
     * @param clazz           The class to search for the annotation.
     * @param annotationClass The Class of the annotation.
     * @return List of the annotations or an empty list.
     */
    public static <T extends Annotation> List<T> findAnnotations(Class<?> clazz, Class<T> annotationClass) {
        List<T> anns = new ArrayList<>();

        List<Class<?>> classes = new ArrayList<>();
        classes.add(clazz);

        classes.addAll(ClassUtils.getAllSuperclasses(clazz));
        classes.addAll(ClassUtils.getAllInterfaces(clazz));
        for (Class<?> aClass : classes) {
            T ann = aClass.getAnnotation(annotationClass);
            if (ann != null) {
                anns.add(ann);
            }

            ann = aClass.getPackage().getAnnotation(annotationClass);
            if (ann != null) {
                anns.add(ann);
            }
        }

        return anns;
    }
}
