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
package org.apache.struts2.result.plain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class HttpHeaders {

    private final List<HttpHeader<String>> stringHeaders = new ArrayList<>();
    private final List<HttpHeader<Long>> dateHeaders = new ArrayList<>();
    private final List<HttpHeader<Integer>> intHeaders = new ArrayList<>();

    public HttpHeaders add(String name, String value) {
        stringHeaders.add(new StringHttpHeader(name, value));
        return this;
    }

    public HttpHeaders add(String name, Long value) {
        dateHeaders.add(new DateHttpHeader(name, value));
        return this;
    }

    public HttpHeaders add(String name, Integer value) {
        intHeaders.add(new IntHttpHeader(name, value));
        return this;
    }

    public List<HttpHeader<String>> getStringHeaders() {
        return Collections.unmodifiableList(stringHeaders);
    }

    public List<HttpHeader<Long>> getDateHeaders() {
        return Collections.unmodifiableList(dateHeaders);
    }

    public List<HttpHeader<Integer>> getIntHeaders() {
        return Collections.unmodifiableList(intHeaders);
    }

}
