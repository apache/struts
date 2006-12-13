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

package org.apache.struts2.views.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;


/**
 * UrlHelper
 *
 */
public class UrlHelper {
    private static final Log LOG = LogFactory.getLog(UrlHelper.class);

    /**
     * Default HTTP port (80).
     */
    private static final int DEFAULT_HTTP_PORT = 80;

    /**
     * Default HTTPS port (443).
     */
    private static final int DEFAULT_HTTPS_PORT = 443;

    private static final String AMP = "&amp;";
    
    private static int httpPort = DEFAULT_HTTP_PORT;
    private static int httpsPort = DEFAULT_HTTPS_PORT;
    private static String customEncoding;
    
    @Inject(StrutsConstants.STRUTS_URL_HTTP_PORT)
    public static void setHttpPort(String val) {
        httpPort = Integer.parseInt(val);
    }
    
    @Inject(StrutsConstants.STRUTS_URL_HTTPS_PORT)
    public static void setHttpsPort(String val) {
        httpsPort = Integer.parseInt(val);
    }
    
    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public static void setCustomEncoding(String val) {
        customEncoding = val;
    }

    public static String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map params) {
        return buildUrl(action, request, response, params, null, true, true);
    }

    public static String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map params, String scheme, boolean includeContext, boolean encodeResult) {
        return buildUrl(action, request, response, params, scheme, includeContext, encodeResult, false);
    }

    public static String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map params, String scheme, boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort) {
        StringBuffer link = new StringBuffer();

        boolean changedScheme = false;

        // only append scheme if it is different to the current scheme *OR*
        // if we explicity want it to be appended by having forceAddSchemeHostAndPort = true
        if (forceAddSchemeHostAndPort) {
            String reqScheme = request.getScheme();
            changedScheme = true;
            link.append(scheme != null ? scheme : reqScheme);
            link.append("://");
            link.append(request.getServerName());

            if ((scheme.equals("http") && (httpPort != DEFAULT_HTTP_PORT)) || (scheme.equals("https") && httpsPort != DEFAULT_HTTPS_PORT))
            {
                link.append(":");
                link.append(scheme.equals("http") ? httpPort : httpsPort);
            }
        }
        else if (
           (scheme != null) && !scheme.equals(request.getScheme())) {
            changedScheme = true;
            link.append(scheme);
            link.append("://");
            link.append(request.getServerName());

            if ((scheme.equals("http") && (httpPort != DEFAULT_HTTP_PORT)) || (scheme.equals("https") && httpsPort != DEFAULT_HTTPS_PORT))
            {
                link.append(":");
                link.append(scheme.equals("http") ? httpPort : httpsPort);
            }
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

                link.append(uri.substring(0, uri.lastIndexOf('/') + 1));
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
        buildParametersString(params, link);

        String result;

        try {
            result = encodeResult ? response.encodeURL(link.toString()) : link.toString();
        } catch (Exception ex) {
            // Could not encode the URL for some reason
            // Use it unchanged
            result = link.toString();
        }

        return result;
    }

    public static void buildParametersString(Map params, StringBuffer link) {
        buildParametersString(params, link, AMP);
    }

    public static void buildParametersString(Map params, StringBuffer link, String paramSeparator) {
        if ((params != null) && (params.size() > 0)) {
            if (link.toString().indexOf("?") == -1) {
                link.append("?");
            } else {
                link.append(paramSeparator);
            }

            // Set params
            Iterator iter = params.entrySet().iterator();

            String[] valueHolder = new String[1];

            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                Object value = entry.getValue();

                String[] values;

                if (value instanceof String[]) {
                    values = (String[]) value;
                } else {
                    valueHolder[0] = value.toString();
                    values = valueHolder;
                }

                for (int i = 0; i < values.length; i++) {
                    if (values[i] != null) {
                        link.append(name);
                        link.append('=');
                        link.append(translateAndEncode(values[i]));
                    }

                    if (i < (values.length - 1)) {
                        link.append(paramSeparator);
                    }
                }

                if (iter.hasNext()) {
                    link.append(paramSeparator);
                }
            }
        }
    }

    /**
     * Translates any script expressions using {@link com.opensymphony.xwork2.util.TextParseUtil#translateVariables} and
     * encodes the URL using {@link java.net.URLEncoder#encode} with the encoding specified in the configuration.
     *
     * @param input
     * @return the translated and encoded string
     */
    public static String translateAndEncode(String input) {
        String translatedInput = translateVariable(input);
        String encoding = getEncodingFromConfiguration();

        try {
            return URLEncoder.encode(translatedInput, encoding);
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Could not encode URL parameter '" + input + "', returning value un-encoded");
            return translatedInput;
        }
    }

    public static String translateAndDecode(String input) {
        String translatedInput = translateVariable(input);
        String encoding = getEncodingFromConfiguration();

        try {
            return URLDecoder.decode(translatedInput, encoding);
        } catch (UnsupportedEncodingException e) {
            LOG.warn("Could not encode URL parameter '" + input + "', returning value un-encoded");
            return translatedInput;
        }
    }

    private static String translateVariable(String input) {
        ValueStack valueStack = ServletActionContext.getContext().getValueStack();
        String output = TextParseUtil.translateVariables(input, valueStack);
        return output;
    }

    private static String getEncodingFromConfiguration() {
        final String encoding;
        if (customEncoding != null) {
            encoding = customEncoding;
        } else {
            encoding = "UTF-8";
        }
        return encoding;
    }

    public static Map parseQueryString(String queryString) {
        Map queryParams = new LinkedHashMap();
        if (queryString != null) {
            String[] params = queryString.split("&");
            for (int a=0; a< params.length; a++) {
                if (params[a].trim().length() > 0) {
                    String[] tmpParams = params[a].split("=");
                    String paramName = null;
                    String paramValue = "";
                    if (tmpParams.length > 0) {
                        paramName = tmpParams[0];
                    }
                    if (tmpParams.length > 1) {
                        paramValue = tmpParams[1];
                    }
                    if (paramName != null) {
                        String translatedParamValue = translateAndDecode(paramValue);
                        queryParams.put(paramName, translatedParamValue);
                    }
                }
            }
        }
        return queryParams;
    }
}
