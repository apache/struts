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

import com.google.gxp.html.HtmlClosure;

import java.lang.reflect.Method;

/**
 * @author Bob Lee
 */
public class Param {

    public static final String BODY_PARAM_NAME = "body";

    String name;
    Class type;
    Class gxpClass;
    boolean optional;
    Object defaultValue;

    Param(Class gxpClass, String name, Class type) {
        this.gxpClass = gxpClass;
        this.name = name;
        this.type = type;

        // if you specify a default parameter value in a GXP, a getDefaultXxx()
        // method will be present.
        try {
            Method defaultGetter = gxpClass.getMethod("getDefault" + capitalize(name));
            this.defaultValue = defaultGetter.invoke(null);
            this.optional = true;
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String capitalize(String s) {
        if (s.isEmpty()) {
            return s;
        }
        char first = s.charAt(0);
        char capitalized = Character.toUpperCase(first);
        return (first == capitalized) ? s : capitalized + s.substring(1);
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    boolean isBody() {
        return name.equals(BODY_PARAM_NAME) && type.equals(HtmlClosure.class);
    }

    public String toString() {
        return "Param[name: " + name
                + ", type: " + type.getName()
                + ", optional: " + optional
                + (optional ? ", defaultValue: " + defaultValue : "")
                + "]";
    }

    public boolean isOptional() {
        return optional;
    }

    public Object getDefaultValue() {
        if (!optional)
            throw new RuntimeException("Parameter '" + name + "' in " + gxpClass.getName() + " is not optional.");
        return defaultValue;
    }

}