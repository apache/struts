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
import com.google.gxp.html.HtmlClosure;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Struts 2 to GXP adapter. Can be used to write a GXP or create
 * a HtmlClosure. Pulls GXP parameters from Struts 2 value stack.
 *
 * @author Bob Lee
 */
public class Gxp extends AbstractGxp<HtmlClosure> {

    Gxp(Class gxpClass) {
        this(gxpClass, lookupMethodByName(gxpClass, "write"), lookupMethodByName(gxpClass, "getGxpClosure"));
    }

    Gxp(Class gxpClass, Method writeMethod, Method getGxpClosureMethod) {
        super(gxpClass, writeMethod, getGxpClosureMethod);
    }

    static final Map<Class, Gxp> classToGxp = new MapMaker().weakKeys().softValues().makeComputingMap(new Function<Class, Gxp>() {
        public Gxp apply(Class from) {
            return classToGxp.containsKey(from) ? classToGxp.get(from) : new Gxp(from);
        }
    });

    static final Map<String, Gxp> pathToGxp = new MapMaker().softValues().makeComputingMap(new Function<String, Gxp>() {
        public Gxp apply(String from) {
            return pathToGxp.containsKey(from) ? pathToGxp.get(from) : getInstance(getGxpClassForPath(from));
        }
    });

    /**
     * Looks up Gxp instance for GXP with given path.
     */
    public static Gxp getInstance(String gxpPath) {
        try {
            return pathToGxp.get(gxpPath);
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
     * Looks up Gxp instance for the given GXP class.
     */
    public static Gxp getInstance(Class gxpClass) {
        return classToGxp.get(gxpClass);
    }

}
