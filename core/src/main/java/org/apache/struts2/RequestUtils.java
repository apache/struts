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
package org.apache.struts2;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Request handling utility class.
 */
public class RequestUtils {

    private static final Logger LOG = LogManager.getLogger(RequestUtils.class);

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
     * Retrieves the path of the current request below the context path, as the servlet container
     * reports it: the servlet path followed by the path info. Path parameters such as
     * {@code ;jsessionid=...} never appear in it, the container strips them before computing both values.
     *
     * @param request the request
     * @return the servlet path
     */
    public static String getServletPath(HttpServletRequest request) {
        return StringUtils.defaultString(request.getServletPath()) + StringUtils.defaultString(request.getPathInfo());
    }

    /**
     * Gets the uri from the request
     *
     * @param request The request
     * @return The uri
     */
    public static String getUri(HttpServletRequest request) {
        // handle http dispatcher includes.
        String uri = (String) request.getAttribute("javax.servlet.include.servlet_path");
        if (uri != null) {
            return uri;
        }

        return getServletPath(request);
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
                LOG.debug("Error parsing value [{}] as [{}]!", headerValue, fastDateFormat);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Error parsing value [{}] as date!", headerValue);
        }
        return null;
    }

}
