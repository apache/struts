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
package org.apache.struts2.interceptor.parameter;

/**
 * Primes channel-specific runtime state required for an already-authorized parameter path to be walked by the
 * value-stack — for example, registering the path's classes into the OGNL {@link org.apache.struts2.ognl.ThreadAllowlist}
 * so OGNL may traverse them. Separated from {@link ParameterAuthorizer} so the authorization decision can remain
 * side-effect-free and reusable from non-OGNL channels (Jackson, Juneau).
 *
 * <p>Implementations MUST NOT repeat the authorization decision — that is owned by
 * {@link ParameterAuthorizer#isAuthorized}. A no-op return (e.g. shallow paths, unannotated root) means "no priming
 * needed or possible" and never "rejected": callers must not treat the absence of priming as a negative authorization
 * signal.</p>
 *
 * @since 7.2.0
 */
public interface ParameterAllowlister {

    /**
     * Primes the channel-specific allowlist for an authorized parameter path. Side-effect-only; no return value
     * because a no-op is a valid outcome (see class-level javadoc).
     *
     * @param parameterName the parameter name (e.g. {@code "user.role"}, {@code "items[0].name"})
     * @param target        the object receiving the parameter value (the action, or the model for ModelDriven actions)
     */
    void primeAllowlistForPath(String parameterName, Object target);
}
