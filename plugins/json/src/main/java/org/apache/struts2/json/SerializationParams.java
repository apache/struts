/*
 * $Id$
 *
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
package org.apache.struts2.json;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

public class SerializationParams {
    private static final String DEFAULT_CONTENT_TYPE = "application/json";

    private final HttpServletResponse response;
    private final String encoding;
    private final boolean wrapWithComments;
    private final String serializedJSON;
    private final boolean smd;
    private final boolean gzip;
    private final boolean noCache;
    private final int statusCode;
    private final int errorCode;
    private final boolean prefix;
    private String contentType = DEFAULT_CONTENT_TYPE;
    private String wrapPrefix;
    private String wrapSuffix;

    public SerializationParams(HttpServletResponse response, String encoding, boolean wrapWithComments,
            String serializedJSON, boolean smd, boolean gzip, boolean noCache, int statusCode, int errorCode,
            boolean prefix, String contentType, String wrapPrefix, String wrapSuffix) {
        this.response = response;
        this.encoding = encoding;
        this.wrapWithComments = wrapWithComments;
        this.serializedJSON = serializedJSON;
        this.smd = smd;
        this.gzip = gzip;
        this.noCache = noCache;
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.prefix = prefix;
        this.contentType = StringUtils.defaultString(contentType, DEFAULT_CONTENT_TYPE);
        this.wrapPrefix = wrapPrefix;
        this.wrapSuffix = wrapSuffix;
    }

    public SerializationParams(HttpServletResponse response, String defaultEncoding,
            boolean wrapWithComments, String json, boolean b, boolean b1, boolean noCache, int i, int i1,
            boolean prefix, String contentType) {
        this(response, defaultEncoding, wrapWithComments, json, b, b1, noCache, i, i1, prefix, contentType,
                null, null);
    }

    public String getWrapSuffix() {
        return wrapSuffix;
    }

    public String getWrapPrefix() {
        return wrapPrefix;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getEncoding() {
        return encoding;
    }

    public boolean isWrapWithComments() {
        return wrapWithComments;
    }

    public String getSerializedJSON() {
        return serializedJSON;
    }

    public boolean isSmd() {
        return smd;
    }

    public boolean isGzip() {
        return gzip;
    }

    public boolean isNoCache() {
        return noCache;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public boolean isPrefix() {
        return prefix;
    }

    public String getContentType() {
        return contentType;
    }
}
