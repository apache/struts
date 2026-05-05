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
package org.apache.struts2.rest.handler;

/**
 * Marker interface for {@link ContentTypeHandler} implementations that respect the
 * {@code ParameterAuthorizationContext} ThreadLocal during deserialization, enforcing
 * {@code @StrutsParameter} authorization per-property.
 *
 * <p>When {@code struts.parameters.requireAnnotations=true}, the REST plugin's
 * {@code ContentTypeInterceptor} binds the authorization context before invoking handlers that
 * implement this interface, allowing them to filter unauthorized properties during deserialization
 * (rather than after, via reflection-based copying).</p>
 *
 * <p>Handlers that do NOT implement this interface fall back to the legacy two-phase copy in
 * {@code ContentTypeInterceptor} — correct but more expensive (and requires a no-arg constructor
 * on the target).</p>
 *
 * <p><strong>Implementer responsibility:</strong> A handler that declares this interface MUST register
 * the authorization-aware mechanism on its underlying parser (e.g. for Jackson, register
 * {@code ParameterAuthorizingModule} on the {@code ObjectMapper}). If the handler implements the
 * interface but its parser does not honor the context, authorization will silently do nothing —
 * a serious security bug. The marker interface is the contract; implementations must uphold it.</p>
 *
 * @since 7.2.0
 */
public interface AuthorizationAwareContentTypeHandler extends ContentTypeHandler {
    // Marker interface — no methods. Implementations signal that their toObject() method
    // honors ParameterAuthorizationContext for per-property @StrutsParameter enforcement.
}
