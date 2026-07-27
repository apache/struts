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
     * Called when a {@code ${...}} parameter could not be resolved for the current invocation.
     * The framework skips the write, leaving the seeded configuration value in place, and
     * notifies the holder so it can decide how to degrade.
     * <p>
     * The default implementation does nothing.
     *
     * @param paramName name of the parameter that could not be resolved
     */
    default void unresolved(String paramName) {
    }
}
