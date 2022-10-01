/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.reflect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Map;

/**
 * Utilities to work with dynamic class loading and instantiation.
 */
public final class ClassUtil {

    private static final Logger LOG = LogManager.getLogger(ClassUtil.class);

    /**
     * Constructor, private to avoid instantiation.
     */
    private ClassUtil() {
    }

    /**
     * Returns the class and casts it to the correct subclass.<br>
     * It tries to use the thread's current classloader first and, if it does
     * not succeed, uses the classloader of ClassUtil.
     *
     * @param <T>       The subclass to use.
     * @param className The name of the class to load.
     * @param baseClass The base class to subclass to.
     * @return The loaded class.
     * @throws ClassNotFoundException If the class has not been found.
     */
    public static <T> Class<? extends T> getClass(String className, Class<T> baseClass) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassUtil.class.getClassLoader();
        }
        return Class.forName(className, true, classLoader).asSubclass(baseClass);
    }

    /**
     * Returns an instance of the given class name, by calling the default
     * constructor.
     *
     * @param className The class name to load and to instantiate.
     * @return The new instance of the class name.
     * @throws CannotInstantiateObjectException If something goes wrong during
     *                                          instantiation.
     */
    public static Object instantiate(String className) {
        return instantiate(className, false);
    }

    /**
     * Returns an instance of the given class name, by calling the default
     * constructor.
     *
     * @param className  The class name to load and to instantiate.
     * @param returnNull If <code>true</code>, if the class is not found it
     *                   returns <code>true</code>, otherwise it throws a
     *                   <code>TilesException</code>.
     * @return The new instance of the class name.
     * @throws CannotInstantiateObjectException If something goes wrong during instantiation.
     */
    public static Object instantiate(String className, boolean returnNull) {
        try {
            Class<?> namedClass = getClass(className, Object.class);
            return namedClass.newInstance();
        } catch (ClassNotFoundException e) {
            if (returnNull) {
                return null;
            }
            throw new CannotInstantiateObjectException("Unable to resolve factory class: '" + className + "'", e);
        } catch (IllegalAccessException e) {
            throw new CannotInstantiateObjectException("Unable to access factory class: '" + className + "'", e);
        } catch (InstantiationException e) {
            throw new CannotInstantiateObjectException("Unable to instantiate factory class: '" + className + "'. Make sure that this class has a default constructor", e);
        }
    }

    /**
     * Collects bean infos from a class and filling a list.
     *
     * @param clazz           The class to be inspected.
     * @param name2descriptor The map in the form: name of the property -> descriptor.
     */
    public static void collectBeanInfo(Class<?> clazz, Map<String, PropertyDescriptor> name2descriptor) {
        BeanInfo info = null;
        try {
            info = Introspector.getBeanInfo(clazz);
        } catch (Exception ex) {
            LOG.debug("Cannot inspect class " + clazz, ex);
        }
        if (info == null) {
            return;
        }
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            pd.setValue("type", pd.getPropertyType());
            pd.setValue("resolvableAtDesignTime", Boolean.TRUE);
            name2descriptor.put(pd.getName(), pd);
        }
    }
}
