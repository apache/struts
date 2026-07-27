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
package org.apache.struts2.interceptor;

import org.apache.struts2.util.TextParseUtil;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Per-invocation upload validation policy for {@link ActionFileUploadInterceptor}.
 * <p>
 * A configured instance is held by the interceptor and copied for each invocation, so lazily
 * resolved values never reach the shared interceptor.
 *
 * @since 7.3.0
 */
public class UploadPolicy extends DisableParams {

    private Long maximumSize;
    private Set<String> allowedTypes = Collections.emptySet();
    private Set<String> allowedExtensions = Collections.emptySet();
    private final Set<String> unresolvedParams = new LinkedHashSet<>();

    public UploadPolicy() {
    }

    private UploadPolicy(UploadPolicy other) {
        super(other);
        this.maximumSize = other.maximumSize;
        this.allowedTypes = other.allowedTypes;
        this.allowedExtensions = other.allowedExtensions;
        this.unresolvedParams.addAll(other.unresolvedParams);
    }

    /**
     * @param allowedTypes a comma-delimited list of content types, or null for no restriction
     */
    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = toSet(allowedTypes);
    }

    /**
     * @param allowedExtensions a comma-delimited list of extensions, or null for no restriction
     */
    public void setAllowedExtensions(String allowedExtensions) {
        this.allowedExtensions = toSet(allowedExtensions);
    }

    /**
     * @param maximumSize the maximum size in bytes, or null for no limit
     */
    public void setMaximumSize(Long maximumSize) {
        this.maximumSize = maximumSize;
    }

    public Long getMaximumSize() {
        return maximumSize;
    }

    public Set<String> getAllowedTypes() {
        return allowedTypes;
    }

    public Set<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    /**
     * A parameter that could not be resolved makes this policy unusable: the upload is rejected
     * rather than validated against a partially-resolved policy, so a broken expression cannot
     * silently relax validation.
     * <p>
     * {@link DisableParams#DISABLED_PARAM} is the one exception and is not recorded. It is not a
     * validation dimension: its unresolved value is simply {@code false}, which leaves the
     * interceptor running and every other part of the policy intact, so it cannot relax validation.
     * Recording it would let a broken {@code <param name="disabled">${...}</param>} reject every
     * upload of the invocation, which is a failure mode of its own rather than a safe default.
     */
    @Override
    public void unresolved(String paramName) {
        if (DISABLED_PARAM.equals(paramName)) {
            return;
        }
        unresolvedParams.add(paramName);
    }

    public boolean isUnresolved() {
        return !unresolvedParams.isEmpty();
    }

    public Set<String> getUnresolvedParams() {
        return Collections.unmodifiableSet(unresolvedParams);
    }

    /**
     * @return an independent copy, used to seed a per-invocation policy from the configured one
     */
    public UploadPolicy copy() {
        return new UploadPolicy(this);
    }

    /**
     * The resulting set is unmodifiable: the copy constructor shares it by reference with the
     * configured policy, so every per-invocation policy that does not override the param would
     * otherwise hand out a live handle on process-wide configuration.
     */
    private static Set<String> toSet(String commaDelimited) {
        return commaDelimited == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(TextParseUtil.commaDelimitedStringToSet(commaDelimited));
    }
}
