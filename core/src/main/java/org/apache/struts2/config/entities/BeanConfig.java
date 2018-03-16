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
package org.apache.struts2.config.entities;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Scope;

public class BeanConfig {
    private final Class<?> clazz;
    private final String name;
    private final Class<?> type;
    private final Scope scope;
    private final boolean onlyStatic;
    private final boolean optional;

    public BeanConfig(Class<?> clazz) {
        this(clazz, Container.DEFAULT_NAME);
    }

    public BeanConfig(Class<?> clazz, String name) {
        this(clazz, name, clazz);
    }

    public BeanConfig(Class<?> clazz, String name, Class<?> type) {
        this(clazz, name, type, Scope.SINGLETON, false, false);
    }

    public BeanConfig(Class<?> clazz, String name, Class<?> type, Scope scope, boolean onlyStatic, boolean optional) {
        this.clazz = clazz;
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.onlyStatic = onlyStatic;
        this.optional = optional;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public Scope getScope() {
        return scope;
    }

    public boolean isOnlyStatic() {
        return onlyStatic;
    }

    public boolean isOptional() {
        return optional;
    }
}
