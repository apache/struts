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

import org.apache.logging.log4j.util.Strings;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * Default resource isolation policy used in {@link FetchMetadataInterceptor} that
 * implements the {@link ResourceIsolationPolicy} interface. This default policy is based on
 * <a href="https://web.dev/fetch-metadata/">https://web.dev/fetch-metadata/</a>.
 *
 * @see <a href="https://web.dev/fetch-metadata/">https://web.dev/fetch-metadata/</a>
 * @see <a href="https://www.w3.org/TR/fetch-metadata/">https://www.w3.org/TR/fetch-metadata/</a>
 **/

public final class StrutsResourceIsolationPolicy implements ResourceIsolationPolicy {

    @Override
    public boolean isRequestAllowed(HttpServletRequest request) {
        String site = request.getHeader(SEC_FETCH_SITE_HEADER);

        // Allow requests from browsers which don't send Fetch Metadata
        if (Strings.isEmpty(site)) {
            return true;
        }

        // Allow same-site and browser-initiated requests
        if (SITE_SAME_ORIGIN.equalsIgnoreCase(site) || SITE_SAME_SITE.equalsIgnoreCase(site) || SITE_NONE.equalsIgnoreCase(site)) {
            return true;
        }

        // Allow simple top-level navigations except <object> and <embed>
        return isAllowedTopLevelNavigation(request);
    }

    private boolean isAllowedTopLevelNavigation(HttpServletRequest request) {
        String mode = request.getHeader(SEC_FETCH_MODE_HEADER);
        String dest = request.getHeader(SEC_FETCH_DEST_HEADER);

        boolean isSimpleTopLevelNavigation = MODE_NAVIGATE.equalsIgnoreCase(mode) || "GET".equalsIgnoreCase(request.getMethod());
        boolean isNotObjectOrEmbedRequest = !DEST_EMBED.equalsIgnoreCase(dest) && !DEST_OBJECT.equalsIgnoreCase(dest);

        return isSimpleTopLevelNavigation && isNotObjectOrEmbedRequest;
    }
}
