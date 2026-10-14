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
package org.apache.struts2.config;

import org.apache.struts2.config.entities.PackageConfig;

/**
 * Resolves a scopable constant for the current action invocation or a given package: the package's {@code <scoped-constant>}, inherited
 * through {@code extends}, or else the global constant.
 *
 * @since 7.5.0
 */
public interface ScopedConstantProvider {

    /**
     * @param name a constant registered through a {@link ScopableConstants} bean
     * @return the scoped value, or the global value ({@code null} when there is none)
     * @throws IllegalArgumentException when {@code name} is not scopable
     */
    String getValue(String name);

    /**
     * @param name          a constant registered through a {@link ScopableConstants} bean
     * @param packageConfig the package whose scoped constants, inherited ones included, take precedence; may be
     *                      {@code null}
     * @return the package's scoped value, or else {@link #getValue(String)}
     * @throws IllegalArgumentException when {@code name} is not scopable; this default checks only on the
     *                                  {@link #getValue(String)} fallback, as package values were validated at startup
     * @since 7.5.0
     */
    default String getValue(String name, PackageConfig packageConfig) {
        String value = packageConfig != null ? packageConfig.getAllScopedConstants().get(name) : null;
        return value != null ? value : getValue(name);
    }
}
