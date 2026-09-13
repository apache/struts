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
package org.apache.struts2.interceptor.csp;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Source of the nonce value
 */
public enum CspNonceSource {
    REQUEST,
    SESSION;

    private static final Logger LOG = LogManager.getLogger(CspNonceSource.class);
    private static final AtomicBoolean LEGACY_WARNED = new AtomicBoolean();

    /**
     * Resolves the configured source: {@link StrutsConstants#STRUTS_CSP_NONCE_SOURCE} wins, then the deprecated
     * {@link StrutsConstants#STRUTS_CSP_NONCE_SOURCE_LEGACY}, otherwise {@link #SESSION}.
     *
     * @since 7.4.0
     */
    @SuppressWarnings("removal")
    public static CspNonceSource resolve(String canonical, String legacy) {
        if (StringUtils.isNotBlank(canonical)) {
            return valueOf(canonical.trim().toUpperCase());
        }
        if (StringUtils.isNotBlank(legacy)) {
            if (LEGACY_WARNED.compareAndSet(false, true)) {
                LOG.warn("Constant '{}' is deprecated, use '{}' instead",
                        StrutsConstants.STRUTS_CSP_NONCE_SOURCE_LEGACY, StrutsConstants.STRUTS_CSP_NONCE_SOURCE);
            }
            return valueOf(legacy.trim().toUpperCase());
        }
        return SESSION;
    }
}
