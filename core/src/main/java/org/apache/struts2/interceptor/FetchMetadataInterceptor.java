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

import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_DEST_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_MODE_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.SEC_FETCH_SITE_HEADER;
import static org.apache.struts2.interceptor.ResourceIsolationPolicy.VARY_HEADER;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Interceptor that implements Fetch Metadata policy on incoming requests used to protect against
 * CSRF, XSSI, and cross-origin information leaks. Uses {@link StrutsResourceIsolationPolicy} to
 * filter the requests allowed to be processed.
 *
 * @see <a href="https://web.dev/fetch-metadata/">https://web.dev/fetch-metadata/</a>
 **/

public class FetchMetadataInterceptor extends AbstractInterceptor {
    private static final Logger logger = LogManager.getLogger(FetchMetadataInterceptor.class);
    private static final String VARY_HEADER_VALUE = String.format("%s,%s,%s", SEC_FETCH_DEST_HEADER, SEC_FETCH_SITE_HEADER, SEC_FETCH_MODE_HEADER);
    private static final String SC_FORBIDDEN = String.valueOf(HttpServletResponse.SC_FORBIDDEN);

    private final Set<String> exemptedPaths = new HashSet<>();
    private final ResourceIsolationPolicy resourceIsolationPolicy = new StrutsResourceIsolationPolicy();

    public void setExemptedPaths(String paths){
        this.exemptedPaths.addAll(TextParseUtil.commaDelimitedStringToSet(paths));
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext context = invocation.getInvocationContext();
        HttpServletRequest request = context.getServletRequest();

        addVaryHeaders(invocation);

        String contextPath = request.getContextPath();
        // Apply exemptions: paths/endpoints meant to be served cross-origin
        if (exemptedPaths.contains(contextPath)) {
            return invocation.invoke();
        }

        // Check if request is allowed
        if (resourceIsolationPolicy.isRequestAllowed(request)) {
            return invocation.invoke();
        }

        logger.atDebug().log(
            "Fetch metadata rejected cross-origin request to %s",
            contextPath
        );
        return SC_FORBIDDEN;
    }

    private void addVaryHeaders(ActionInvocation invocation) {
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        response.setHeader(VARY_HEADER, VARY_HEADER_VALUE);
    }
}
