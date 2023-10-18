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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

import static java.lang.String.format;

/**
 * Default implementation of {@link CspSettings}.
 * The default policy implements strict CSP with a nonce based approach and follows the guide: <a href="https://csp.withgoogle.com/docs/index.html">https://csp.withgoogle.com/docs/index.html/</a>
 *
 * @see CspSettings
 * @see CspInterceptor
 */
public class DefaultCspSettings implements CspSettings {

    private final static Logger LOG = LogManager.getLogger(DefaultCspSettings.class);

    private final SecureRandom sRand = new SecureRandom();

    private String reportUri;
    // default to reporting mode
    private String cspHeader = CSP_REPORT_HEADER;

    @Override
    public void addCspHeaders(HttpServletResponse response) {
        throw new UnsupportedOperationException("Unsupported implementation, use #addCspHeaders(HttpServletRequest request, HttpServletResponse response)");
    }

    public void addCspHeaders(HttpServletRequest request, HttpServletResponse response) {
        if (isSessionActive(request)) {
            LOG.trace("Session is active, applying CSP settings");
            associateNonceWithSession(request);
            response.setHeader(cspHeader, cratePolicyFormat(request));
        } else {
            LOG.trace("Session is not active, ignoring CSP settings");
        }
    }

    private boolean isSessionActive(HttpServletRequest request) {
        return request.getSession(false) != null;
    }

    private void associateNonceWithSession(HttpServletRequest request) {
        String nonceValue = Base64.getUrlEncoder().encodeToString(getRandomBytes());
        request.getSession().setAttribute("nonce", nonceValue);
    }

    private String cratePolicyFormat(HttpServletRequest request) {
        StringBuilder policyFormatBuilder = new StringBuilder()
            .append(OBJECT_SRC)
            .append(format(" '%s'; ", NONE))
            .append(SCRIPT_SRC)
            .append(" 'nonce-%s' ") // nonce placeholder
            .append(format("'%s' ", STRICT_DYNAMIC))
            .append(format("%s %s; ", HTTP, HTTPS))
            .append(BASE_URI)
            .append(format(" '%s'; ", NONE));

        if (reportUri != null) {
            policyFormatBuilder
                .append(REPORT_URI)
                .append(format(" %s", reportUri));
        }

        return format(policyFormatBuilder.toString(), getNonceString(request));
    }

    private String getNonceString(HttpServletRequest request) {
        Object nonce = request.getSession().getAttribute("nonce");
        return Objects.toString(nonce);
    }

    private byte[] getRandomBytes() {
        byte[] ret = new byte[NONCE_RANDOM_LENGTH];
        sRand.nextBytes(ret);
        return ret;
    }

    public void setEnforcingMode(boolean enforcingMode) {
        if (enforcingMode) {
            cspHeader = CSP_ENFORCE_HEADER;
        }
    }

    public void setReportUri(String reportUri) {
        this.reportUri = reportUri;
    }

    @Override
    public String toString() {
        return "DefaultCspSettings{" +
            "reportUri='" + reportUri + '\'' +
            ", cspHeader='" + cspHeader + '\'' +
            '}';
    }

}
