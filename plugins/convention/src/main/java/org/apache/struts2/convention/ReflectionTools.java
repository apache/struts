/*
 * $Id$
 *
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
package org.apache.struts2.convention;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>
 * This class has some reflection helpers.
 * </p>
 */
public class ReflectionTools {
    /**
     * Determines if the class given contains the method.
     *
     * @param   klass The class to check for the method.
     * @param   method The method name.
     * @param   parameterTypes The parameter types of the method.
     * @return  True if the method exists, false if not.
     */
    public static boolean containsMethod(Class<?> klass, String method, Class<?>... parameterTypes) {
        try {
            klass.getMethod(method, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Retrieves the annotation from the given method in the given class.
     *
     * @param   klass The class.
     * @param   methodName The method.
     * @param   annotationClass The annotation to get.
     * @return  The annotation or null if it doesn't exist.
     */
    public static <T extends Annotation> T getAnnotation(Class<?> klass, String methodName, Class<T> annotationClass) {
        try {
            Method method = klass.getMethod(methodName);
            return method.getAnnotation(annotationClass);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return the list of parent classes in order (Object will be at index 0)
     * @param clazz class to process
     * @return hierarchy of classes
     */
    public static List<Class<?>> getClassHierarchy(Class<?> clazz) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        while (clazz != null) {
            classes.add(0, clazz);
            clazz = clazz.getSuperclass();
        }

        return classes;
    }
}