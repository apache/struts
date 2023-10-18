/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.servlet.extractor;

import org.apache.tiles.request.attribute.HasKeys;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * Extract parameters from the request.
 */
public class ParameterExtractor implements HasKeys<String> {

    /**
     * The servlet request.
     */
    private final HttpServletRequest request;

    /**
     * Constructor.
     *
     * @param request The servlet request.
     */
    public ParameterExtractor(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public Enumeration<String> getKeys() {
        return request.getParameterNames();
    }

    @Override
    public String getValue(String key) {
        return request.getParameter(key);
    }
}
