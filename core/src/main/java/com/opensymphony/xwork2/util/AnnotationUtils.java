/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
     * For the given <code>Class</code> get a collection of the the {@link AnnotatedElement}s
     * that match the given <code>annotation</code>s or if no <code>annotation</code>s are
     * specified then return all of the annotated elements of the given <code>Class</code>.
     * Includes only the method level annotations.
     *
     * @param clazz      The {@link Class} to inspect
     * @param annotation the {@link Annotation}s to find
     * @return A {@link Collection}&lt;{@link AnnotatedElement}&gt; containing all of the
     * method {@link AnnotatedElement}s matching the specified {@link Annotation}s
     * @deprecated Will be removed after release of <a href="https://github.com/apache/commons-lang/pull/261">LANG-1317</a>
     */
    @Deprecated
    public static Collection<Method> getAnnotatedMethods(Class clazz, Class<? extends Annotation>... annotation) {
        List<Class<?>> allSuperclasses = ClassUtils.getAllSuperclasses(clazz);
        allSuperclasses.add(0, clazz);
        int sci = 0;
        List<Class<?>> allInterfaces = ClassUtils.getAllInterfaces(clazz);
        int ifi = 0;
        final List<Method> annotatedMethods = new ArrayList<>();
        while (ifi < allInterfaces.size() ||
                sci < allSuperclasses.size()) {
            Class<?> acls;
            if (ifi >= allInterfaces.size()) {
                acls = allSuperclasses.get(sci++);
            }
            else if (sci >= allSuperclasses.size()) {
                acls = allInterfaces.get(ifi++);
            }
            else if (sci <= ifi) {
                acls = allSuperclasses.get(sci++);
            }
            else {
                acls = allInterfaces.get(ifi++);
            }
            final Method[] allMethods = acls.getDeclaredMethods();
            for (final Method method : allMethods) {
                if (ArrayUtils.isEmpty(annotation) && ArrayUtils.isNotEmpty(method.getAnnotations())) {
                    annotatedMethods.add(method);
                    continue;
                }
                for (Class<? extends Annotation> c : annotation) {
                    if (method.getAnnotation(c) != null) {
                        annotatedMethods.add(method);
                    }
                }
            }
        }

        return annotatedMethods;
    }

    /**
     * <p>BFS to find the annotation object that is present on the given method or any equivalent method in
     * super classes and interfaces, with the given annotation type. Returns null if the annotation type was not present
     * on any of them.</p>
     * @param <A>
     *            the annotation type
     * @param method
     *            the {@link Method} to query
     * @param annotationCls
     *            the {@link Annotation} to check if is present on the method
     * @return an Annotation (possibly null).
     * @deprecated Will be removed after release of <a href="https://github.com/apache/commons-lang/pull/261">LANG-1317</a>
     */
    @Deprecated
    public static <A extends Annotation> A findAnnotation(final Method method, final Class<A> annotationCls) {
        A annotation = method.getAnnotation(annotationCls);

        if(annotation == null) {
            Class<?> mcls = method.getDeclaringClass();
            List<Class<?>> allSuperclasses = ClassUtils.getAllSuperclasses(mcls);
            int sci = 0;
            List<Class<?>> allInterfaces = ClassUtils.getAllInterfaces(mcls);
            int ifi = 0;
            while (ifi < allInterfaces.size() ||
                    sci < allSuperclasses.size()) {
                Class<?> acls;
                if(ifi >= allInterfaces.size()) {
                    acls = allSuperclasses.get(sci++);
                }
                else if(sci >= allSuperclasses.size()) {
                    acls = allInterfaces.get(ifi++);
                }
                else if(ifi <= sci) {
                    acls = allInterfaces.get(ifi++);
                }
                else {
                    acls = allSuperclasses.get(sci++);
                }
                Method equivalentMethod = null;
                try {
                    equivalentMethod = acls.getDeclaredMethod(method.getName(), method.getParameterTypes());
                } catch (NoSuchMethodException e) {
                    // If not found, just keep on breadth first search
                }
                if(equivalentMethod != null) {
                    annotation = equivalentMethod.getAnnotation(annotationCls);
                    if(annotation != null) {
                        break;
                    }
                }
            }
        }
        return annotation;
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
     * Returns the annotation on the given class or the package of the class. This searchs up the
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
}
