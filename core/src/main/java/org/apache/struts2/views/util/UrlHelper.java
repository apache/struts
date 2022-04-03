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
package org.apache.struts2.views.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Helper class used to build Urls or parse request params
 */
public interface UrlHelper {

    /**
     * Default HTTP port (80).
     */
    static final int DEFAULT_HTTP_PORT = 80;

    /**
     * Default HTTPS port (443).
     */
    static final int DEFAULT_HTTPS_PORT = 443;

    static final String AMP = "&amp;";

    String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params);

    String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String scheme,
                    boolean includeContext, boolean encodeResult);

    String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String scheme,
                    boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort);

    String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String scheme,
                    boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort, boolean escapeAmp);

    void buildParametersString(Map<String, Object> params, StringBuilder link, String paramSeparator);

    Map<String, Object> parseQueryString(String queryString, boolean forceValueArray);

}
