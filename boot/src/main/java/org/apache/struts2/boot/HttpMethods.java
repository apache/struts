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
package org.apache.struts2.boot;

import org.apache.struts2.interceptor.httpmethod.HttpMethod;

public final class HttpMethods {

    public static final HttpMethod GET = HttpMethod.GET;
    public static final HttpMethod POST = HttpMethod.POST;
    public static final HttpMethod PUT = HttpMethod.PUT;
    public static final HttpMethod DELETE = HttpMethod.DELETE;
    public static final HttpMethod PATCH = HttpMethod.PATCH;
    public static final HttpMethod HEAD = HttpMethod.HEAD;
    public static final HttpMethod OPTIONS = HttpMethod.OPTIONS;

    private HttpMethods() {
    }
}
