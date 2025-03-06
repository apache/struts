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
package org.apache.struts2.ognl;

import org.apache.commons.lang3.ClassUtils;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

/**
 * Allows any bean to allowlist a class for use in OGNL expressions, for the current thread only. The allowlist can be
 * cleared once any desired OGNL expressions have been evaluated.
 *
 * @since 6.4.0
 */
public class ThreadAllowlist {

    private final ThreadLocal<Set<Class<?>>> allowlist = new ThreadLocal<>();

    /**
     * @since 7.1.0
     */
    public void allowClassHierarchy(Class<?> clazz) {
        allowClass(clazz);
        ClassUtils.getAllSuperclasses(clazz).forEach(this::allowClass);
        ClassUtils.getAllInterfaces(clazz).forEach(this::allowClass);
    }

    public void allowClass(Class<?> clazz) {
        if (allowlist.get() == null) {
            allowlist.set(new HashSet<>());
        }
        allowlist.get().add(clazz);
    }

    public void clearAllowlist() {
        allowlist.remove();
    }

    public Set<Class<?>> getAllowlist() {
        return allowlist.get() != null ? unmodifiableSet(allowlist.get()) : emptySet();
    }
}
