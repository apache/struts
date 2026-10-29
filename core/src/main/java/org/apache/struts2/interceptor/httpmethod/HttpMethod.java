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

import java.util.Arrays;
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
     * @return the matching method, or {@code null} when the request method is not one of the constants
     */
    public static HttpMethod parseOrNull(String httpRequestMethod) {
        return Arrays.stream(values())
            .filter(method -> method.name().equalsIgnoreCase(httpRequestMethod))
            .findFirst()
            .orElse(null);
    }

    public static HttpMethod parse(String httpRequestMethod) {
        return valueOf(httpRequestMethod.toUpperCase(Locale.ROOT));
    }

}
