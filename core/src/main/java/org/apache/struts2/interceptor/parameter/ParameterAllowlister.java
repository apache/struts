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
 * Service for priming downstream allowlists (e.g. the OGNL {@link org.apache.struts2.ognl.ThreadAllowlist}) for a
 * parameter path that has already been authorized by {@link ParameterAuthorizer}. Separated from the authorizer so
 * that the authorizer can remain side-effect-free and reusable from non-OGNL channels (Jackson, Juneau).
 *
 * <p>Implementations are expected to no-op when {@code parameterName} is depth-0 if their downstream engine does not
 * require root-level priming. Callers must have already verified authorization via
 * {@link ParameterAuthorizer#isAuthorized}; this service does NOT enforce annotations.</p>
 *
 * @since 7.2.0
 */
public interface ParameterAllowlister {

    /**
     * Primes the underlying allowlist for an authorized parameter path.
     *
     * @param parameterName the parameter name (e.g. {@code "user.role"}, {@code "items[0].name"})
     * @param target        the object receiving the parameter value (the action, or the model for ModelDriven actions)
     */
    void allowlistAuthorizedPath(String parameterName, Object target);
}
