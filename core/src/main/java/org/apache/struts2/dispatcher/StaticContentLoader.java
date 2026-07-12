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
package org.apache.struts2.dispatcher;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.config.StrutsBeanSelectionProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
/**
 * Interface for loading static resources, based on a path. After implementing your own static content loader
 * you must tell the framework how to use it, eg.
 *
 * &lt;bean name="myContentLoader" type="org.apache.struts2.dispatcher" class="com.company.struts.MyContentLoader"/&gt;
 * &lt;constant name="struts.staticContentLoader" value="myContentLoader"/&gt;
 *
 * Check {@link StrutsBeanSelectionProvider} for more details.
 */
public interface StaticContentLoader {

    /**
     * Default path at which static content is served, can be changed
     * by using {@link org.apache.struts2.StrutsConstants#STRUTS_UI_STATIC_CONTENT_PATH}
     */
    String DEFAULT_STATIC_CONTENT_PATH = "/static";

    /**
     * @param path Requested resource path
     * @return true if this loader is able to load this type of resource, false otherwise
     */
    boolean canHandle(String path);

    /**
     * @param filterConfig The filter configuration
     */
    void setHostConfig(HostConfig filterConfig);

    /**
     * Locate a static resource and copy directly to the response, setting the
     * appropriate caching headers.
     *
     * @param path     The resource name
     * @param request  The request
     * @param response The response
     * @throws IOException If anything goes wrong
     */
    void findStaticResource(String path, HttpServletRequest request, HttpServletResponse response) throws IOException;

    class Validator {

        private static final Logger LOG = LogManager.getLogger(DefaultStaticContentLoader.class);

        public static String validateStaticContentPath(String uiStaticContentPath) {
            if (StringUtils.isBlank(uiStaticContentPath)) {
                LOG.warn("\"{}\" has been set to \"{}\", falling back into default value \"{}\"",
                    StrutsConstants.STRUTS_UI_STATIC_CONTENT_PATH,
                    uiStaticContentPath,
                    StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH);
                return StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH;
            } else if ("/".equals(uiStaticContentPath)) {
                LOG.warn("\"{}\" cannot be set to \"{}\", falling back into default value \"{}\"",
                    StrutsConstants.STRUTS_UI_STATIC_CONTENT_PATH,
                    uiStaticContentPath,
                    StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH);
                return StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH;
            } else if (!uiStaticContentPath.startsWith("/")) {
                LOG.warn("\"{}\" must start with \"/\", but has been set to \"{}\", prepending the missing \"/\"!",
                    StrutsConstants.STRUTS_UI_STATIC_CONTENT_PATH,
                    uiStaticContentPath);
                return "/" + uiStaticContentPath;
            } else if (uiStaticContentPath.endsWith("/")) {
                LOG.warn("\"{}\" must not end with \"/\", but has been set to \"{}\", removing all trailing \"/\"!",
                    StrutsConstants.STRUTS_UI_STATIC_CONTENT_PATH,
                    uiStaticContentPath);
                return StringUtils.stripEnd(uiStaticContentPath, "/");
            } else {
                LOG.debug("\"{}\" has been set to \"{}\"", StrutsConstants.STRUTS_UI_STATIC_CONTENT_PATH, uiStaticContentPath);
                return uiStaticContentPath;
            }
        }
        /**
         * Normalises a resource path by resolving {@code .} and {@code ..}
         * segments and converting backslash separators to forward slashes.
         *
         * <p>Returns {@link Optional#empty()} if the resolved path would
         * escape above the root (i.e. more {@code ..} segments than
         * preceding path components).</p>
         *
         * @param path the raw path to normalise (must not be null)
         * @return the canonical path without leading slash, or empty if
         *         the path escapes above the root
         */
        public static Optional<String> canonicalisePath(String path) {
            String normalised = path.replace('\\', '/');
            Deque<String> segments = new ArrayDeque<>();
            for (String segment : normalised.split("/", -1)) {
                if ("..".equals(segment)) {
                    if (segments.isEmpty()) {
                        return Optional.empty();
                    }
                    segments.removeLast();
                } else if (!".".equals(segment) && !segment.isEmpty()) {
                    segments.addLast(segment);
                }
            }
            return Optional.of(String.join("/", segments));
        }
    }
}
