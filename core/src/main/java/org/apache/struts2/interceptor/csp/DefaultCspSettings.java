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

import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.action.CspSettingsAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Base64;

import static java.lang.String.format;

/**
 * Default implementation of {@link CspSettings}.
 * The default policy implements strict CSP with a nonce based approach and follows the guide:
 * <a href="https://csp.withgoogle.com/docs/index.html">https://csp.withgoogle.com/docs/index.html/</a>
 * You may extend or replace this class if you wish to customize the default policy further, and use your class
 * by setting the {@link CspInterceptor} defaultCspSettingsClassName parameter. Actions that
 * implement the {@link CspSettingsAware} interface will ignore the defaultCspSettingsClassName parameter.
 *
 * @see CspSettings
 * @see CspInterceptor
 */
public class DefaultCspSettings implements CspSettings {

    private static final Logger LOG = LogManager.getLogger(DefaultCspSettings.class);
    private static final String NONCE_KEY = "nonce";

    private final SecureRandom sRand = new SecureRandom();

    private CspNonceSource nonceSource = CspNonceSource.SESSION;

    protected String reportUri;
    protected String reportTo;
    // default to reporting mode
    protected String cspHeader = CSP_REPORT_HEADER;

    @Inject(value = StrutsConstants.STRUTS_CSP_NONCE_SOURCE, required = false)
    public void setNonceSource(String nonceSource) {
        if (StringUtils.isBlank(nonceSource)) {
            this.nonceSource = CspNonceSource.SESSION;
        } else {
            this.nonceSource = CspNonceSource.valueOf(nonceSource.toUpperCase());
        }
    }

    @Override
    public void addCspHeaders(HttpServletResponse response) {
        throw new UnsupportedOperationException("Unsupported implementation, use #addCspHeaders(HttpServletRequest request, HttpServletResponse response)");
    }

    @Override
    public void addCspHeaders(HttpServletRequest request, HttpServletResponse response) {
        if (this.nonceSource == CspNonceSource.SESSION) {
            addCspHeadersWithSession(request, response);
        } else if (this.nonceSource == CspNonceSource.REQUEST) {
            addCspHeadersWithRequest(request, response);
        } else {
            LOG.warn("Unknown nonce source: {}, ignoring CSP settings", nonceSource);
        }
    }

    private void addCspHeadersWithSession(HttpServletRequest request, HttpServletResponse response) {
        if (isSessionActive(request)) {
            LOG.trace("Session is active, applying CSP settings");
            String nonceValue = generateNonceValue();
            request.getSession().setAttribute(NONCE_KEY, nonceValue);
            response.setHeader(cspHeader, createPolicyFormat(nonceValue));
        } else {
            LOG.debug("Session is not active, ignoring CSP settings");
        }
    }

    private void addCspHeadersWithRequest(HttpServletRequest request, HttpServletResponse response) {
        String nonceValue = generateNonceValue();
        request.setAttribute(NONCE_KEY, nonceValue);
        response.setHeader(cspHeader, createPolicyFormat(nonceValue));
    }

    private boolean isSessionActive(HttpServletRequest request) {
        return request.getSession(false) != null;
    }

    private String generateNonceValue() {
        return Base64.getUrlEncoder().encodeToString(getRandomBytes());
    }

    protected String createPolicyFormat(String nonceValue) {
        StringBuilder builder = new StringBuilder()
                .append(OBJECT_SRC)
                .append(format(" '%s'; ", NONE))
                .append(SCRIPT_SRC)
                .append(format(" 'nonce-%s' ", nonceValue))
                .append(format("'%s' ", STRICT_DYNAMIC))
                .append(format("%s %s; ", HTTP, HTTPS))
                .append(BASE_URI)
                .append(format(" '%s'; ", NONE));

        if (reportUri != null) {
            builder
                    .append(REPORT_URI)
                    .append(format(" %s; ", reportUri));
            if (reportTo != null) {
                builder
                        .append(REPORT_TO)
                        .append(format(" %s; ", reportTo));
            }
        }

        return builder.toString();
    }

    /**
     * @deprecated since 6.8.0, for removal
     */
    @Deprecated
    protected String createPolicyFormat(HttpServletRequest request) {
        throw new UnsupportedOperationException("Unsupported implementation, use #createPolicyFormat(String) instead!");
    }

    /**
     * @deprecated since 6.8.0, for removal
     */
    @Deprecated
    protected String getNonceString(HttpServletRequest request) {
        throw new UnsupportedOperationException("Unsupported implementation, don't use!");
    }

    private byte[] getRandomBytes() {
        byte[] ret = new byte[NONCE_RANDOM_LENGTH];
        sRand.nextBytes(ret);
        return ret;
    }

    @Override
    public void setEnforcingMode(boolean enforcingMode) {
        if (enforcingMode) {
            cspHeader = CSP_ENFORCE_HEADER;
        }
    }

    @Override
    public void setReportUri(String reportUri) {
        this.reportUri = reportUri;
    }

    @Override
    public void setReportTo(String reportTo) {
        this.reportTo = reportTo;
    }

    @Override
    public String toString() {
        return "DefaultCspSettings{" +
                "reportUri='" + reportUri + '\'' +
                ", reportTo='" + reportTo + '\'' +
                ", cspHeader='" + cspHeader + '\'' +
                '}';
    }

}
