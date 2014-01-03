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

package org.apache.struts2;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang3.time.FastDateFormat;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Request handling utility class.
 */
public class RequestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(RequestUtils.class);

    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private static final String FORMAT_PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String FORMAT_PATTERN_RFC1036 = "EEE, dd-MMM-yy HH:mm:ss zzz";
    private static final String FORMAT_PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";

    private static final FastDateFormat[] IF_MODIFIED_SINCE_FORMATS = {
            FastDateFormat.getInstance(FORMAT_PATTERN_RFC1123, GMT, Locale.US),
            FastDateFormat.getInstance(FORMAT_PATTERN_RFC1036, GMT, Locale.US),
            FastDateFormat.getInstance(FORMAT_PATTERN_ASCTIME, GMT, Locale.US)
    };

    /**
     * Retrieves the current request servlet path.
     * Deals with differences between servlet specs (2.2 vs 2.3+)
     *
     * @param request the request
     * @return the servlet path
     */
    public static String getServletPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        
        String requestUri = request.getRequestURI();
        // Detecting other characters that the servlet container cut off (like anything after ';')
        if (requestUri != null && servletPath != null && !requestUri.endsWith(servletPath)) {
            int pos = requestUri.indexOf(servletPath);
            if (pos > -1) {
                servletPath = requestUri.substring(requestUri.indexOf(servletPath));
            }
        }
        
        if (null != servletPath && !"".equals(servletPath)) {
            return servletPath;
        }
        
        int startIndex = request.getContextPath().equals("") ? 0 : request.getContextPath().length();
        int endIndex = request.getPathInfo() == null ? requestUri.length() : requestUri.lastIndexOf(request.getPathInfo());

        if (startIndex > endIndex) { // this should not happen
            endIndex = startIndex;
        }

        return requestUri.substring(startIndex, endIndex);
    }

    /**
     * Gets the uri from the request
     *
     * @param request The request
     * @return The uri
     */
    public static String getUri(HttpServletRequest request) {
        // handle http dispatcher includes.
        String uri = (String) request
                .getAttribute("javax.servlet.include.servlet_path");
        if (uri != null) {
            return uri;
        }

        uri = getServletPath(request);
        if (uri != null && !"".equals(uri)) {
            return uri;
        }

        uri = request.getRequestURI();
        return uri.substring(request.getContextPath().length());
    }

    /**
     * Parse input string as date in formats defined for If-Modified-Since header,
     * see:
     * https://issues.apache.org/jira/browse/WW-4263
     * https://web.archive.org/web/20081014021349/http://rfc.net/rfc2616.html#p20
     *
     * @param headerValue value of If-Modified-Since header
     * @return proper date or null
     */
    public static Date parseIfModifiedSince(String headerValue) {
        for (FastDateFormat fastDateFormat : IF_MODIFIED_SINCE_FORMATS) {
            try {
                return fastDateFormat.parse(headerValue);
            } catch (ParseException ignore) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error parsing value [#0] as [#1]!", headerValue, fastDateFormat);
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Error parsing value [#0] as date!", headerValue);
        }
        return null;
    }

}
