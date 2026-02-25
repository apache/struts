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
package org.apache.struts2.interceptor.i18n;

import org.apache.struts2.ActionInvocation;

import java.util.Locale;

/**
 * Strategy used by {@code I18nInterceptor} to resolve and optionally persist the current request locale.
 * <p>
 * Implementations encapsulate locale source-specific behavior (request parameters, session, cookies,
 * or Accept-Language header), while the interceptor orchestrates the overall lifecycle.
 */
public interface LocaleHandler {

    /**
     * Looks for an explicit locale override in request-scoped sources.
     *
     * @return a locale override or {@code null} when no explicit override is present
     */
    Locale find();

    /**
     * Reads locale from persistent/context sources when {@link #find()} did not resolve one.
     *
     * @param invocation current action invocation
     * @return resolved locale or {@code null} when no locale could be resolved
     */
    Locale read(ActionInvocation invocation);

    /**
     * Persists the resolved locale when storage is enabled for the current handler.
     *
     * @param invocation current action invocation
     * @param locale locale to store
     * @return the effective locale to apply to the invocation context
     */
    Locale store(ActionInvocation invocation, Locale locale);

    /**
     * Indicates if the locale should be persisted for the current request.
     *
     * @return {@code true} when {@link #store(ActionInvocation, Locale)} should be invoked
     */
    boolean shouldStore();
}
