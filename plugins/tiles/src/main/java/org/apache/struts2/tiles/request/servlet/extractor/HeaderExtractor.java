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
package org.apache.struts2.tiles.request.servlet.extractor;

import java.util.Enumeration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tiles.request.attribute.EnumeratedValuesExtractor;

public class HeaderExtractor implements EnumeratedValuesExtractor {
    private HttpServletRequest request;
    private HttpServletResponse response;

    public HeaderExtractor(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public Enumeration<String> getKeys() {
        return this.request.getHeaderNames();
    }

    public String getValue(String key) {
        return this.request.getHeader(key);
    }

    public Enumeration<String> getValues(String key) {
        return this.request.getHeaders(key);
    }

    public void setValue(String key, String value) {
        this.response.setHeader(key, value);
    }
}
