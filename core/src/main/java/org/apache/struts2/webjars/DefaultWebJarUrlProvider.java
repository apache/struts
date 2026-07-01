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
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.StaticContentLoader;
import org.apache.struts2.inject.Inject;
import org.webjars.WebJarVersionLocator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Default {@link WebJarUrlProvider} backed by a singleton {@link WebJarVersionLocator}
 * (from {@code webjars-locator-lite}). Thread-safe.
 */
public class DefaultWebJarUrlProvider implements WebJarUrlProvider {

    private static final Logger LOG = LogManager.getLogger(DefaultWebJarUrlProvider.class);

    private static final String WEBJARS_URL_SEGMENT = "/webjars/";

    private final WebJarVersionLocator locator = new WebJarVersionLocator();

    private boolean enabled = true;
    private Set<String> allowlist = Collections.emptySet();
    private String uiStaticContentPath = StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH;

    @Inject(value = StrutsConstants.STRUTS_WEBJARS_ENABLED, required = false)
    public void setEnabled(String enabled) {
        this.enabled = BooleanUtils.toBoolean(enabled);
    }

    @Inject(value = StrutsConstants.STRUTS_WEBJARS_ALLOWLIST, required = false)
    public void setAllowlist(String allowlist) {
        Set<String> names = new HashSet<>();
        if (StringUtils.isNotBlank(allowlist)) {
            for (String name : allowlist.split(",")) {
                String trimmed = name.trim();
                if (!trimmed.isEmpty()) {
                    names.add(trimmed);
                }
            }
        }
        this.allowlist = Collections.unmodifiableSet(names);
    }

    @Inject(StrutsConstants.STRUTS_UI_STATIC_CONTENT_PATH)
    public void setStaticContentPath(String uiStaticContentPath) {
        this.uiStaticContentPath = StaticContentLoader.Validator.validateStaticContentPath(uiStaticContentPath);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Optional<String> resolveResourcePath(String logicalPath) {
        String[] parts = split(logicalPath);
        if (parts == null) {
            return Optional.empty();
        }
        String full = locator.fullPath(parts[0], parts[1]);
        if (full == null || !full.startsWith(WebJarVersionLocator.WEBJARS_PATH_PREFIX + "/")) {
            return Optional.empty();
        }
        return Optional.of(full);
    }

    @Override
    public Optional<String> resolveUrl(String logicalPath, HttpServletRequest request) {
        String[] parts = split(logicalPath);
        if (parts == null) {
            return Optional.empty();
        }
        String versioned = locator.path(parts[0], parts[1]);
        if (versioned == null) {
            return Optional.empty();
        }
        StringBuilder url = new StringBuilder();
        String contextPath = request.getContextPath();
        if (StringUtils.isNotEmpty(contextPath) && !"/".equals(contextPath)) {
            url.append(contextPath);
        }
        url.append(uiStaticContentPath).append(WEBJARS_URL_SEGMENT).append(versioned);
        return Optional.of(url.toString());
    }

    /**
     * Normalize, validate and split a logical path into {webJarName, filePath}.
     *
     * @return a two-element array, or {@code null} if disabled, blank, single-segment,
     *         traversal-tainted, or allowlist-blocked
     */
    private String[] split(String logicalPath) {
        if (!enabled || StringUtils.isBlank(logicalPath)) {
            return null;
        }
        String normalized = StringUtils.stripStart(logicalPath, "/");
        if (normalized.contains("\\")) {
            return null;
        }
        for (String segment : normalized.split("/")) {
            if (segment.equals("..") || segment.equals(".")) {
                LOG.debug("Rejecting WebJar path with traversal segment: {}", logicalPath);
                return null;
            }
        }
        int slash = normalized.indexOf('/');
        if (slash < 1 || slash == normalized.length() - 1) {
            return null;
        }
        String webJarName = normalized.substring(0, slash);
        String filePath = normalized.substring(slash + 1);
        if (!isAllowed(webJarName)) {
            LOG.debug("WebJar '{}' is not on the allowlist", webJarName);
            return null;
        }
        return new String[]{webJarName, filePath};
    }

    private boolean isAllowed(String webJarName) {
        return allowlist.isEmpty() || allowlist.contains(webJarName);
    }
}
