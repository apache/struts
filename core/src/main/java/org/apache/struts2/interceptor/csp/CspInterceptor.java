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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import java.net.URI;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor that implements Content Security Policy on incoming requests used to protect against
 * common XSS and data injection attacks. Uses {@link CspSettings} to add appropriate Content Security Policy header
 * to the response. These headers determine what the browser will consider a policy violation and the browser's behavior
 * when a violation occurs. A detailed explanation of CSP can be found <a href="https://csp.withgoogle.com/docs/index.html">here</a>.
 *
 * @see <a href="https://csp.withgoogle.com/docs/index.html">https://csp.withgoogle.com/docs/index.html/</a>
 * @see CspSettings
 * @see DefaultCspSettings
 **/
public final class CspInterceptor extends AbstractInterceptor implements PreResultListener {
    private final CspSettings settings = new DefaultCspSettings();

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        invocation.addPreResultListener(this);
        return invocation.invoke();
    }

    public void beforeResult(ActionInvocation invocation, String resultCode) {
        HttpServletResponse response = invocation.getInvocationContext().getServletResponse();
        settings.addCspHeaders(response);
    }

    public void setReportUri(String reportUri) {
        Optional<URI> uri = buildUri(reportUri);
        if (!uri.isPresent()) {
            throw new IllegalArgumentException("Could not parse configured report URI for CSP interceptor: " + reportUri);
        }

        if (!uri.get().isAbsolute() && !reportUri.startsWith("/")) {
            throw new IllegalArgumentException("Illegal configuration: report URI is not relative to the root. Please set a report URI that starts with /");
        }

        settings.setReportUri(reportUri);
    }

    private Optional<URI> buildUri(String reportUri) {
        try {
            return Optional.of(URI.create(reportUri));
        } catch (IllegalArgumentException ignored) {
        }

        return Optional.empty();
    }

    public void setEnforcingMode(String value){
        boolean enforcingMode = Boolean.parseBoolean(value);
        settings.setEnforcingMode(enforcingMode);
    }
}
