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
package org.apache.struts2.interceptor;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface for the resource isolation policies to be used for fetch metadata checks.
 *
 * Resource isolation policies are designed to protect against cross origin attacks and use the
 * {@code sec-fetch-*} request headers to decide whether to accept or reject a request. Read more
 * about <a href="https://web.dev/fetch-metadata/">Fetch Metadata.</a>
 *
 * See {@link StrutsResourceIsolationPolicy} for the default implementation used.
 *
 * @see <a href="https://web.dev/fetch-metadata/">https://web.dev/fetch-metadata/</a>
 * @see <a href="https://www.w3.org/TR/fetch-metadata/">https://www.w3.org/TR/fetch-metadata/</a>
 **/

@FunctionalInterface
public interface ResourceIsolationPolicy {
    String SEC_FETCH_DEST_HEADER = "Sec-Fetch-Dest";
    String SEC_FETCH_MODE_HEADER = "Sec-Fetch-Mode";
    String SEC_FETCH_SITE_HEADER = "Sec-Fetch-Site";
    String SEC_FETCH_USER_HEADER = "Sec-Fetch-User";
    String VARY_HEADER = "Vary";
    // Valid values for the SEC_FETCH_DEST_HEADER.  Note: The specifications says servers should ignore the header if it contains an invalid value.
    String DEST_AUDIO = "audio";
    String DEST_AUDIOWORKLET = "audioworklet";
    String DEST_DOCUMENT = "document";
    String DEST_EMBED = "embed";
    String DEST_EMPTY = "empty";
    String DEST_FONT = "font";
    String DEST_IMAGE = "image";
    String DEST_MANIFEST = "manifest";
    String DEST_NESTED_DOCUMENT = "nested-document";
    String DEST_OBJECT = "object";
    String DEST_PAINTWORKLET = "paintworklet";
    String DEST_REPORT = "report";
    String DEST_SCRIPT = "script";
    String DEST_SERVICEWORKER = "serviceworker";
    String DEST_SHAREDWORKER = "sharedworker";
    String DEST_STYLE = "style";
    String DEST_TRACK = "track";
    String DEST_VIDEO = "video";
    String DEST_WORKER = "worker";
    String DEST_XSLT = "xslt";
    // Valid values for the SEC_FETCH_MODE_HEADER.  Note: The specifications says servers should ignore the header if it contains an invalid value.
    String MODE_CORS = "cors";
    String MODE_NAVIGATE = "navigate";
    String MODE_NESTED_NAVIGATE = "nested-navigate";
    String MODE_NO_CORS = "no-cors";
    String MODE_SAME_ORIGIN = "same-origin";
    String MODE_WEBSOCKET = "websocket";
     // Valid values for the SEC_FETCH_SITE_HEADER.  Note: The specifications says servers should ignore the header if it contains an invalid value.
    String SITE_CROSS_SITE = "cross-site";
    String SITE_SAME_ORIGIN = "same-origin";
    String SITE_SAME_SITE = "same-site";
    String SITE_NONE = "none";

    boolean isRequestAllowed(HttpServletRequest request);
}
