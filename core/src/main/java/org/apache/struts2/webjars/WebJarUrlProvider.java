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
package org.apache.struts2.webjars;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * Resolves version-less WebJar logical paths (e.g. {@code bootstrap/css/bootstrap.min.css}) to either a
 * concrete classpath resource under {@code META-INF/resources/webjars/} (for serving) or a servable URL
 * (for tags/macros). Resolution is constrained to the WebJars root, honours an optional allowlist and the
 * {@code struts.webjars.enabled} switch, and fails closed (empty result) when unresolved or blocked.
 */
public interface WebJarUrlProvider {

    /**
     * @param logicalPath version-less path such as {@code bootstrap/css/bootstrap.min.css}
     * @return the concrete classpath resource path (e.g.
     *         {@code META-INF/resources/webjars/bootstrap/5.3.8/css/bootstrap.min.css}), or empty
     */
    Optional<String> resolveResourcePath(String logicalPath);

    /**
     * @param logicalPath version-less path such as {@code bootstrap/css/bootstrap.min.css}
     * @param request     the current request (used for the servlet context path)
     * @return a servable URL, or empty
     */
    Optional<String> resolveUrl(String logicalPath, HttpServletRequest request);

    /**
     * @return whether WebJars support is enabled
     */
    boolean isEnabled();
}
