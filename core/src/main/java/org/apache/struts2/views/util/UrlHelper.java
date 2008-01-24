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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


/**
 * UrlHelper
 *
 */
public class UrlHelper {
    private static final Logger LOG = LoggerFactory.getLogger(UrlHelper.class);

    /**
     * Default HTTP port (80).
     */
    private static final int DEFAULT_HTTP_PORT = 80;

    /**
     * Default HTTPS port (443).
     */
    private static final int DEFAULT_HTTPS_PORT = 443;

    private static final String AMP = "&amp;";

    public static String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map params) {
        return buildUrl(action, request, response, params, null, true, true);
    }

    public static String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map params, String scheme, boolean includeContext, boolean encodeResult) {
        return buildUrl(action, request, response, params, scheme, includeContext, encodeResult, false);
    }

    public static String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map params, String scheme, boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort) {
    	return buildUrl(action, request, response, params, scheme, includeContext, encodeResult, forceAddSchemeHostAndPort, true);
    }
    
    public static String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map params, String scheme, boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort, boolean escapeAmp) {
        StringBuffer link = new StringBuffer();

        boolean changedScheme = false;
        
        // FIXME: temporary hack until class is made a properly injected bean
        Container cont = ActionContext.getContext().getContainer();
        int httpPort = Integer.parseInt(cont.getInstance(String.class, StrutsConstants.STRUTS_URL_HTTP_PORT));
        int httpsPort = Integer.parseInt(cont.getInstance(String.class, StrutsConstants.STRUTS_URL_HTTPS_PORT));

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
                    if ((scheme.equals("http") && (httpPort != DEFAULT_HTTP_PORT)) || (scheme.equals("https") && httpsPort != DEFAULT_HTTPS_PORT)) {
                        link.append(":");
                        link.append(scheme.equals("http") ? httpPort : httpsPort);
                    }
                // Else use the port from the current request.
                } else {
                    int reqPort = request.getServerPort();

                    if ((scheme.equals("http") && (reqPort != DEFAULT_HTTP_PORT)) || (scheme.equals("https") && reqPort != DEFAULT_HTTPS_PORT)) {
                        link.append(":");
                        link.append(reqPort);
                    }
                }
            }
        }
        else if ((scheme != null) && !scheme.equals(request.getScheme())) {
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
        if (escapeAmp) {
            buildParametersString(params, link);
        } else {
            buildParametersString(params, link, "&");
        } 

        String result = link.toString();
        
        if (result.indexOf("<script>") >= 0){
        	result = result.replaceAll("<script>", "script");
        }
        
        try {
            result = encodeResult ? response.encodeURL(result) : result;
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


            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                Object value = entry.getValue();


                if (value instanceof Iterable) {
                    for (Iterator iterator = ((Iterable) value).iterator(); iterator
                        .hasNext();) {
                        Object paramValue = iterator.next();
                        link.append(buildParameterSubstring(name, paramValue
                            .toString()));

                        if (iterator.hasNext())
                            link.append(paramSeparator);
                    }
                } else if (value instanceof Object[]) {
                    Object[] array = (Object[]) value;
                    for (int i = 0; i < array.length; i++) {
                        Object paramValue = array[i];
                        link.append(buildParameterSubstring(name, paramValue
                            .toString()));

                        if (i < array.length - 1)
                            link.append(paramSeparator);
                    }
                } else {
                    link.append(buildParameterSubstring(name, value.toString()));
                }
                
                if (iter.hasNext())
                    link.append(paramSeparator);
            }
        }
    }

    
    private static String buildParameterSubstring(String name, String value) {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append('=');
        builder.append(translateAndEncode(value));
        
        return builder.toString();
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
        
        // FIXME: temporary hack until class is made a properly injected bean
        Container cont = ActionContext.getContext().getContainer();
        String customEncoding = cont.getInstance(String.class, StrutsConstants.STRUTS_I18N_ENCODING);
        
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

                        if(queryParams.containsKey(paramName)) {
                            // WW-1619 append new param value to existing value(s)
                            Object currentParam = queryParams.get(paramName);
                            if(currentParam instanceof String) {
                                queryParams.put(paramName, new String[] {
                                        (String) currentParam, translatedParamValue});
                            } else {
                                String currentParamValues[] = (String[]) currentParam;
                                List paramList = new ArrayList(Arrays
                                    .asList(currentParamValues));
                                paramList.add(translatedParamValue);
                                String newParamValues[] = new String[paramList
                                    .size()];
                                queryParams.put(paramName, paramList
                                    .toArray(newParamValues));
                            }
                        } else {
                            queryParams.put(paramName, translatedParamValue);
                        }
                    }
                }
            }
        }
        return queryParams;
    }
}
