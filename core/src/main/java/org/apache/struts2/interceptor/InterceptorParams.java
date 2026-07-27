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
package org.apache.struts2.interceptor;

/**
 * Contract for an object holding the parameters of a single interceptor.
 * <p>
 * Implementations are per-invocation value objects: the framework resolves configured
 * parameters into a fresh instance for each action invocation, so nothing is written back
 * onto the interceptor, which stays immutable after {@link Interceptor#init()}.
 *
 * @since 7.3.0
 */
public interface InterceptorParams {

    /**
     * Called when a configured parameter could not be applied for the current invocation - either a
     * {@code ${...}} expression that did not resolve, or a value the holder's setter could not
     * accept. The framework skips the write and notifies the holder so it can decide how to degrade.
     * <p>
     * Whatever the holder was seeded with stays in place, but it is rarely a usable default: the
     * seed comes from applying the raw configuration string at build time, so for a {@code ${...}}
     * param it is the unevaluated literal, or nothing at all when that literal could not be
     * converted to the property's type. A holder guarding a security-sensitive dimension should
     * therefore treat this as a reason to fail closed rather than carry on.
     * <p>
     * The default implementation does nothing.
     *
     * @param paramName name of the parameter that could not be applied
     */
    default void unresolved(String paramName) {
    }
}
