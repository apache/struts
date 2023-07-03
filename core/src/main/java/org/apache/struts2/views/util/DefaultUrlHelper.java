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

import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.url.QueryStringBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Default implementation of UrlHelper
 */
public class DefaultUrlHelper implements UrlHelper {

    private static final Logger LOG = LogManager.getLogger(DefaultUrlHelper.class);

    public static final String HTTP_PROTOCOL = "http";
    public static final String HTTPS_PROTOCOL = "https";

    private int httpPort = DEFAULT_HTTP_PORT;
    private int httpsPort = DEFAULT_HTTPS_PORT;

    private QueryStringBuilder queryStringBuilder;

    @Inject(StrutsConstants.STRUTS_URL_HTTP_PORT)
    public void setHttpPort(String httpPort) {
        this.httpPort = Integer.parseInt(httpPort);
    }

    @Inject(StrutsConstants.STRUTS_URL_HTTPS_PORT)
    public void setHttpsPort(String httpsPort) {
        this.httpsPort = Integer.parseInt(httpsPort);
    }

    @Inject
    public void setQueryStringBuilder(QueryStringBuilder builder) {
        this.queryStringBuilder = builder;
    }

    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) {
        return buildUrl(action, request, response, params, null, true, true);
    }

    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String scheme,
                           boolean includeContext, boolean encodeResult) {
        return buildUrl(action, request, response, params, scheme, includeContext, encodeResult, false);
    }

    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String scheme,
                           boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort) {
        return buildUrl(action, request, response, params, scheme, includeContext, encodeResult, forceAddSchemeHostAndPort, true);
    }

    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String urlScheme,
                           boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort, boolean escapeAmp) {

        StringBuilder link = new StringBuilder();
        boolean changedScheme = false;

        String scheme = null;
        if (isValidScheme(urlScheme)) {
            scheme = urlScheme;
        }

        // only append scheme if it is different to the current scheme *OR*
        // if we explicity want it to be appended by having forceAddSchemeHostAndPort = true
        if (forceAddSchemeHostAndPort) {
            String reqScheme = request.getScheme();
            changedScheme = true;
            link.append(scheme != null ? scheme : reqScheme);
            link.append("://");
            link.append(request.getServerName());

            if (scheme != null) {
                // If switching schemes, use the configured port for the particular scheme.
                if (!scheme.equals(reqScheme)) {
                    appendPort(link, scheme, HTTP_PROTOCOL.equals(scheme) ? httpPort : httpsPort);
                    // Else use the port from the current request.
                } else {
                    appendPort(link, scheme, request.getServerPort());
                }
            } else {
                appendPort(link, reqScheme, request.getServerPort());
            }
        } else if ((scheme != null) && !scheme.equals(request.getScheme())) {
            changedScheme = true;
            link.append(scheme);
            link.append("://");
            link.append(request.getServerName());

            appendPort(link, scheme, HTTP_PROTOCOL.equals(scheme) ? httpPort : httpsPort);
        }

        if (action != null) {
            // Check if context path needs to be added
            // Add path to absolute links
            if (action.startsWith("/") && includeContext) {
                String contextPath = request.getContextPath();
                if (!contextPath.equals("/")) {
                    link.append(contextPath);
                }
            } else if (changedScheme) {

                // (Applicable to Servlet 2.4 containers)
                // If the request was forwarded, the attribute below will be set with the original URL
                String uri = (String) request.getAttribute("javax.servlet.forward.request_uri");

                // If the attribute wasn't found, default to the value in the request object
                if (uri == null) {
                    uri = request.getRequestURI();
                }

                link.append(uri, 0, uri.lastIndexOf('/') + 1);
            }

            // Add page
            link.append(action);
        } else {
            // Go to "same page"
            String requestURI = (String) request.getAttribute("struts.request_uri");

            // (Applicable to Servlet 2.4 containers)
            // If the request was forwarded, the attribute below will be set with the original URL
            if (requestURI == null) {
                requestURI = (String) request.getAttribute("javax.servlet.forward.request_uri");
            }

            // If neither request attributes were found, default to the value in the request object
            if (requestURI == null) {
                requestURI = request.getRequestURI();
            }

            link.append(requestURI);
        }

        //if the action was not explicitly set grab the params from the request
        if (escapeAmp) {
            queryStringBuilder.build(params, link, AMP);
        } else {
            queryStringBuilder.build(params, link, "&");
        }

        String result = link.toString();

        if (StringUtils.containsIgnoreCase(result, "<script")) {
            result = StringEscapeUtils.escapeEcmaScript(result);
        }
        try {
            result = encodeResult ? response.encodeURL(result) : result;
        } catch (Exception ex) {
            LOG.debug("Could not encode the URL for some reason, use it unchanged", ex);
            result = link.toString();
        }

        return result;
    }

    private void appendPort(StringBuilder link, String scheme, int port) {
        if ((HTTP_PROTOCOL.equals(scheme) && port != DEFAULT_HTTP_PORT) || (HTTPS_PROTOCOL.equals(scheme) && port != DEFAULT_HTTPS_PORT)) {
            link.append(":");
            link.append(port);
        }
    }

    protected boolean isValidScheme(String scheme) {
        return HTTP_PROTOCOL.equals(scheme) || HTTPS_PROTOCOL.equals(scheme);
    }

}
