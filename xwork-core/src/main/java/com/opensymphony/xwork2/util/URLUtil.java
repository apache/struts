/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Helper class to extract file paths from different urls
 */
public class URLUtil {

    private static final Logger LOG = LoggerFactory.getLogger(URLUtil.class);

    /**
     * Verify That the given String is in valid URL format.
     * @param url The url string to verify.
     * @return a boolean indicating whether the URL seems to be incorrect.
     */
    @Deprecated
    public static boolean verifyUrl(String url) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Checking if url [#0] is valid", url);
        }
        if (url == null) {
            return false;
        }

        if (url.startsWith("https://")) {
            // URL doesn't understand the https protocol, hack it
            url = "http://" + url.substring(8);
        }

        try {
            new URL(url);

            return true;
        } catch (MalformedURLException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Url [#0] is invalid: #1", e, url, e.getMessage());
            }
            return false;
        }
    }

}
