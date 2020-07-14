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
 *
 * @author Santiago Diaz - saldiaz@google.com
 * @author Giannis Chatziveroglou - giannic@google.com
 **/

public final class DefaultResourceIsolationPolicy implements ResourceIsolationPolicy {

    @Override
    public boolean isRequestAllowed(HttpServletRequest request) {
        String site = request.getHeader(SEC_FETCH_SITE_HEADER);

        // Allow requests from browsers which don't send Fetch Metadata
        if (Strings.isEmpty((site))){
            return true;
        }

        // Allow same-site and browser-initiated requests
        if (SAME_ORIGIN.equals(site) || SAME_SITE.equals(site) || NONE.equals(site)) {
            return true;
        }

        // Allow simple top-level navigations except <object> and <embed>
        return isAllowedTopLevelNavigation(request);
    }

    private boolean isAllowedTopLevelNavigation(HttpServletRequest request)
    {
        String mode = request.getHeader(SEC_FETCH_MODE_HEADER);
        String dest = request.getHeader(SEC_FETCH_DEST_HEADER);

        boolean isSimpleTopLevelNavigation = MODE_NAVIGATE.equals(mode) || "GET".equals(request.getMethod());
        boolean isNotObjectOrEmbedRequest = !DEST_EMBED.equals(dest) && !DEST_OBJECT.equals(dest);

        return isSimpleTopLevelNavigation && isNotObjectOrEmbedRequest;
    }
}
