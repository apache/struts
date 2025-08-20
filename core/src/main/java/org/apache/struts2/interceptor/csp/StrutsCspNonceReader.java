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
import org.apache.struts2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Reads nonce value from session or request attribute.
 * @since 6.8.0
 */
public class StrutsCspNonceReader implements CspNonceReader {

    private static final Logger LOG = LogManager.getLogger(StrutsCspNonceReader.class);

    private final CspNonceSource nonceSource;

    @Inject(value = StrutsConstants.STRUTS_CSP_NONCE_SOURCE, required = false)
    public StrutsCspNonceReader(String source) {
        if (StringUtils.isBlank(source)) {
            this.nonceSource = CspNonceSource.SESSION;
        } else {
            this.nonceSource = CspNonceSource.valueOf(source.toUpperCase());
        }
    }

    @Override
    public NonceValue readNonceValue(ValueStack stack) {
        HttpServletRequest request = stack.getActionContext().getServletRequest();
        NonceValue nonceValue;

        if (nonceSource == CspNonceSource.SESSION) {
            LOG.debug("Reading nonce value from session");
            nonceValue = readNonceFromSession(request);
        } else if (nonceSource == CspNonceSource.REQUEST) {
            LOG.debug("Reading nonce value from request attribute");
            nonceValue = readNonceFromRequest(request);
        } else {
            LOG.warn("Unknown nonce source: {}, reading nonce value from session", nonceSource);
            nonceValue = readNonceFromSession(request);
        }
        return nonceValue;
    }

    private NonceValue readNonceFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object nonceValue = session != null ? session.getAttribute("nonce") : null;
        if (nonceValue == null) {
            LOG.debug("Session is not active, cannot obtain nonce value");
            return NonceValue.ofNullSession();
        }
        return NonceValue.ofSession(nonceValue.toString());
    }

    private NonceValue readNonceFromRequest(HttpServletRequest request) {
        Object nonceValue = request.getAttribute("nonce");
        if (nonceValue == null) {
            LOG.warn("Request attribute 'nonce' is not set, cannot obtain nonce value");
            return NonceValue.ofNullRequest();
        }

        return NonceValue.ofRequest(nonceValue.toString());
    }
}
