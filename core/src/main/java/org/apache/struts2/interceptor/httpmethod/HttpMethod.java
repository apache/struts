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
package org.apache.struts2.interceptor.httpmethod;

import java.util.Locale;

/**
 * Enum represents possible http request types
 *
 * @see HttpMethodInterceptor
 * @since 6.2.0
 */
public enum HttpMethod {

    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    OPTIONS,
    CONNECT,
    PATCH;

    /**
     * Returns the {@link HttpMethod} for the given request method, or {@code null} if it is not
     * one of the methods this enum knows about.
     *
     * <p>Use this for the method of an incoming request. The client chooses that token freely,
     * so an unrecognised value is ordinary input rather than a programming error, and must not
     * raise.</p>
     */
    public static HttpMethod parseOrNull(String httpRequestMethod) {
        if (httpRequestMethod == null) {
            return null;
        }

        try {
            return valueOf(httpRequestMethod.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static HttpMethod parse(String httpRequestMethod) {
        return valueOf(httpRequestMethod.toUpperCase(Locale.ROOT));
    }

}
