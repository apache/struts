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
package org.apache.struts2.jasper.runtime;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;


/**
 * A helper module for processing instance objects using InstanceManager instances.
 * 
 * Since AnnotationProcessor was removed in Tomcat 7+, this module performs a similar purpose using
 * InstanceManager processing (the effective replacement).  This module's logic is a blending
 * of the original Struts 2 AnnotationHelper and logic ideas from an old Geronimo update
 * (https://issues.apache.org/jira/browse/GERONIMO-3010)
 * 
 * Original AnnotationHelper:  Verify the annotation and Process it (@author Fabien Carrion, @author Remy Maucherat)
 */
public class InstanceHelper {

    private static final Map<ClassLoader, InstanceManager> INSTANCE_MANAGERS = 
            Collections.synchronizedMap(new WeakHashMap<ClassLoader, InstanceManager>());


    /**
     * Private constructor (all methods static, no instance required)
     */
    private InstanceHelper() {}

    /**
     * Get the <code>{@link InstanceManager}</code> instance manager for a given
     * <code>{@link ServletConfig}</code>.
     * 
     * For any non-null servletConfig with a non-null <code>{@link ServletContext}</code>,
     * if no instance manager is defined, this method will produce a
     * <code>{@link SimpleInstanceManager}</code> and associate it with that
     * <code>{@link ServletConfig}</code> and its <code>{@link ClassLoader}</code>.
     * 
     * @param servletConfig
     * 
     * @return 
     */
    public static InstanceManager getServletInstanceManager(ServletConfig servletConfig) {
        if (servletConfig == null) {
            return null;
        }
        final ServletContext servletContext = servletConfig.getServletContext();
        if (servletContext == null) {
            return null;
        }
        final ClassLoader classLoader = servletContext.getClassLoader();
        final Object potentialInstanceManager = servletContext.getAttribute(InstanceManager.class.getName());
        InstanceManager instanceManager;
        if (potentialInstanceManager == null) {
            instanceManager = new SimpleInstanceManager();
            setClassLoaderInstanceManager(classLoader, instanceManager);
            servletContext.setAttribute(InstanceManager.class.getName(), instanceManager);
            return instanceManager;
        }
        else {
            instanceManager = getClassLoaderInstanceManager(classLoader);
            if (instanceManager == null || ! instanceManager.equals(potentialInstanceManager) ) {
                if (potentialInstanceManager instanceof InstanceManager) {
                    instanceManager = (InstanceManager) potentialInstanceManager;
                    setClassLoaderInstanceManager(classLoader, instanceManager);
                    return instanceManager;
                } else {
                    return null;
                }
            } else {
                return instanceManager;
            }
        }
    }

    /**
     * Get the <code>{@link InstanceManager}</code> instance manager for a given
     * <code>{@link ClassLoader}</code> instance, stored within the
     * <code>{@link InstanceHelper} {@link Map}</code>.
     * 
     * For any non-null classLoader, if no instance manager is defined,
     * this method will produce a <code>{@link SimpleInstanceManager}</code>
     * and associate it with that <code>{@link ClassLoader}</code>.
     * 
     * @param classLoader
     * 
     * @return 
     */
    public static InstanceManager getClassLoaderInstanceManager(ClassLoader classLoader) {
        if (classLoader == null) {
            return null;
        }
        InstanceManager instanceManager = INSTANCE_MANAGERS.get(classLoader);
        if (instanceManager == null) {
            instanceManager = new SimpleInstanceManager();
            setClassLoaderInstanceManager(classLoader, instanceManager);
        }
        return instanceManager;
    }

    /**
     * Set the <code>{@link InstanceManager}</code> instance manager for a given
     * <code>{@link ClassLoader}</code> instance, stored within the
     * <code>{@link InstanceHelper} {@link Map}</code>.
     * 
     * @param classLoader
     * @param instanceManager 
     */
    protected static void setClassLoaderInstanceManager(ClassLoader classLoader, InstanceManager instanceManager) {
        if (classLoader != null) {
            INSTANCE_MANAGERS.put(classLoader, instanceManager);
        }
    }

    /**
     * Post-construct an instance of the specified <code>{@link Object}</code> using
     * the provided <code>{@link InstanceManager}</code>.
     * 
     * Note: This method replaces the old postContruct method in the AnnotationHelper.
     * Note: In Jasper, this calls naming resources injection as well.
     *
     * @param instanceManager
     * @param instance
     * 
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws NamingException
     */
    public static void postConstruct(InstanceManager instanceManager, Object instance)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
               NamingException {
        if (instanceManager == null) {
            throw new IllegalArgumentException("postConstruct - instanceManager is null");
        } else if (instance == null) {
            throw new IllegalArgumentException("postConstruct - instanceClass is null");
        } else {
            instanceManager.newInstance(instance);
        }
    }

    /**
     * Pre-destroy an instance <code>{@link Object}</code> previously post-constructed
     * with the provided <code>{@link InstanceManager}</code>.
     * 
     * Note: This method replaces the old preDestroy method in the AnnotationHelper.
     *
     * @param instanceManager instance manager
     * @param instance object instance
     *
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static void preDestroy(InstanceManager instanceManager, Object instance)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (instanceManager == null) {
            throw new IllegalArgumentException("preDestroy - instanceManager is null");
        } else if (instance == null) {
            throw new IllegalArgumentException("preDestroy - instance is null");
        } else {
            instanceManager.destroyInstance(instance);
        }
    }

}
