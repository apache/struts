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
 * Service for determining whether a given parameter name is authorized for injection into a target object, based on
 * {@link StrutsParameter} annotation presence and depth.
 *
 * <p>This service extracts the authorization logic from {@link ParametersInterceptor} so that it can be reused by other
 * input channels (e.g. JSON plugin, REST plugin) that also need to enforce {@code @StrutsParameter} rules.</p>
 *
 * <p>Implementations must NOT perform OGNL ThreadAllowlist side effects — those remain specific to
 * {@link ParametersInterceptor}.</p>
 *
 * @since 7.2.0
 */
public interface ParameterAuthorizer {

    /**
     * Determines whether a parameter with the given name is authorized for injection into the given target object.
     *
     * <p>When {@code struts.parameters.requireAnnotations} is {@code false}, this method always returns {@code true}
     * for backward compatibility.</p>
     *
     * @param parameterName the parameter name (e.g. "name", "address.city", "items[0].name")
     * @param target        the object receiving the parameter value (the action, or the model for ModelDriven actions)
     * @param action        the action instance; used to detect ModelDriven exemption (when {@code target != action},
     *                      the target is the model and is exempt from annotation requirements)
     * @return {@code true} if the parameter is authorized for injection, {@code false} otherwise
     */
    boolean isAuthorized(String parameterName, Object target, Object action);
}
