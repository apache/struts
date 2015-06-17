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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Helper class to extract file paths from different urls
 */
public class URLUtil {

    private static final Logger LOG = LogManager.getLogger(URLUtil.class);

    /**
     * Verify That the given String is in valid URL format.
     * @param url The url string to verify.
     * @return a boolean indicating whether the URL seems to be incorrect.
     */
    @Deprecated
    public static boolean verifyUrl(String url) {
        LOG.debug("Checking if url [{}] is valid", url);
        if (url == null) {
            return false;
        }

        if (url.startsWith("https://")) {
            // URL doesn't understand the https protocol, hack it
            url = "http://" + url.substring(8);
        }

        try {
            URL u = new URL(url);
            URI uri = u.toURI(); // perform a additional url syntax check
            if (uri.getHost() == null) {
                LOG.debug("Url [{}] does not contains a valid host: {}", url, uri);
                return false;
            }
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            LOG.debug("Url [{}] is invalid: {}", url, e.getMessage(), e);
            return false;
        }
    }

}
