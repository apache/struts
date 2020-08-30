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

import static java.lang.String.format;

import com.opensymphony.xwork2.ActionContext;

import java.util.function.Supplier;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;


/**
 * Default implementation of {@link CspSettings}.
 * The default policy implements strict CSP with a nonce based approach and follows the guide: <a href="https://csp.withgoogle.com/docs/index.html">https://csp.withgoogle.com/docs/index.html/</a>
 *
 * @see CspSettings
 * @see CspInterceptor
 */
public class DefaultCspSettings implements CspSettings {
    private final SecureRandom sRand  = new SecureRandom();
    // this lazy supplier computes a policy format the first time it's called and caches the result
    // to reduce string operations when attaching policies to HTTP responses
    private final Supplier<String> lazyPolicyBuilder = new Supplier<String>() {
        boolean hasBeenCalled;
        String policyFormat;

        @Override
        public String get() {
            if (!hasBeenCalled) {
                StringBuilder policyFormatBuilder = new StringBuilder()
                    .append(OBJECT_SRC)
                    .append(format(" '%s'; ", NONE))
                    .append(SCRIPT_SRC)
                    .append(" 'nonce-%s' ")             // nonce placeholder
                    .append(format("'%s' ", STRICT_DYNAMIC))
                    .append(format("%s %s; ", HTTP, HTTPS))
                    .append(BASE_URI)
                    .append(format(" '%s'; ", NONE));

                if (reportUri != null) {
                    policyFormatBuilder
                        .append(REPORT_URI)
                        .append(format(" %s", reportUri));
                }

                policyFormat = policyFormatBuilder.toString();
            }

            return format(policyFormat, getNonceString());
        }
    };

    private String reportUri;
    // default to reporting mode
    private String cspHeader = CSP_REPORT_HEADER;

    public void addCspHeaders(HttpServletResponse response) {
        associateNonceWithSession();
        response.setHeader(cspHeader, lazyPolicyBuilder.get());
    }

    private String getNonceString() {
        Map<String, Object> session = ActionContext.getContext().getSession();
        return (String) session.get("nonce");
    }

    private void associateNonceWithSession() {
        Map<String, Object> session = ActionContext.getContext().getSession();
        String nonceValue = Base64.getUrlEncoder().encodeToString(getRandomBytes());
        session.put("nonce", nonceValue);
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
}
