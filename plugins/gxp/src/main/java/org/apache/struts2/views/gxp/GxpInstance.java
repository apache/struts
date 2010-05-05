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
package org.apache.struts2.views.gxp;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Inject;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Struts 2 to GXP adapter that uses instances of GXP Interfaces, as created by
 * the {@link ObjectFactory}. Can be used to write a GXP or create a
 * HtmlClosure. Pulls non-constructor GXP parameters from Struts 2 value stack.
 *
 * @author David P. Baker
 */
public class GxpInstance extends Gxp {

    private static final Logger logger = Logger.getLogger(GxpInstance.class.getCanonicalName());

    private Class<?> gxpInterface;
    private Class<?> gxpInstance;
    private ObjectFactory objectFactory;

    GxpInstance(Class<?> gxpClass) {
        super(gxpClass,
                lookupMethodByName(getNestedClass(gxpClass, "Interface"), "write"),
                lookupMethodByName(getNestedClass(gxpClass, "Interface"), "getGxpClosure"));
        this.gxpInterface = getNestedClass(gxpClass, "Interface");
        this.gxpInstance = getNestedClass(gxpClass, "Instance");
    }

    private static Class<?> getNestedClass(Class<?> clazz, String nestedClassName) {
        for (Class<?> nested : clazz.getDeclaredClasses()) {
            if (nestedClassName.equals(nested.getSimpleName())) {
                return nested;
            }
        }
        throw new IllegalArgumentException(String.format("Cannot find class %s.%s", clazz.getCanonicalName(), nestedClassName));
    }

    /**
     * {@inheritDoc}
     * <p> This implementation uses the {@link ObjectFactory} to try to create an
     * instance of the {@code Interface} class that is nested within the GXP
     * class. If that doesn't work, it falls back to trying to use the
     * {@code ObjectFactory} to create an instance of the nested {@code Instance}
     * class, in case there is no binding for the {@code Interface}.
     */
    @Override
    protected Object getGxpInstance() {
        try {
            return objectFactory.buildBean(gxpInterface, null);
        } catch (Exception e) {
            logger.log(
                    Level.INFO, "Error instantiating {0}; trying {1}",
                    new Object[]{gxpInterface.getCanonicalName(), gxpInstance.getCanonicalName(),});
            try {
                return objectFactory.buildBean(gxpInstance, null);
            } catch (Exception e1) {
                throw new RuntimeException(String.format("Error instantiating %s",
                        gxpInterface.getCanonicalName(), gxpInstance.getCanonicalName()),
                        e1);
            }
        }
    }

    @Override
    public Class<?> getGxpClass() {
        return this.gxpInterface;
    }

    private static final Map<Class<?>, GxpInstance> classToGxpInstance = new MapMaker().weakKeys().softValues()
            .makeComputingMap(new Function<Class<?>, GxpInstance>() {
                public GxpInstance apply(Class<?> from) {
                    return classToGxpInstance.containsKey(from) ? classToGxpInstance.get(from) : new GxpInstance(from);
                }
            });

    private static final Map<String, GxpInstance> pathToGxpInstance = new MapMaker().softValues()
            .makeComputingMap(new Function<String, GxpInstance>() {
                public GxpInstance apply(String from) {
                    return pathToGxpInstance.containsKey(from) ? pathToGxpInstance.get(from) : getInstance(getGxpClassForPath(from));
                }
            });

    /**
     * Looks up Gxp instance for GXP with given path.
     */
    public static GxpInstance getInstance(String gxpPath) {
        try {
            return pathToGxpInstance.get(gxpPath);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ClassNotFoundException) {
                // Couldn't find or load the GXP class.  Return null.
                // It would be simpler if classToGxp.create() could return null,
                // but the contract of ReferenceCache doesn't allow it to.
                return null;
            }
            throw e;
        }
    }

    /**
     * Looks up {@code GxpInstance} instance for the given GXP class.
     */
    public static GxpInstance getInstance(Class<?> gxpClass) {
        return classToGxpInstance.get(gxpClass);
    }

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

}
