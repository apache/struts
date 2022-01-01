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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.TextParseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * Interceptor that implements Cross-Origin Embedder Policy on incoming requests used to protect a
 * document from loading any non-same-origin resources which don't explicitly grant the document
 * permission to be loaded.
 *
 * @see <a href="https://web.dev/why-coop-coep/#coep">https://web.dev/why-coop-coep/#coep</a>
 * @see <a href="https://wicg.github.io/cross-origin-embedder-policy/">https://wicg.github.io/cross-origin-embedder-policy/</a>
 **/
public class CoepInterceptor extends AbstractInterceptor implements PreResultListener {

    private static final Logger LOG = LogManager.getLogger(CoepInterceptor.class);

    private static final String REQUIRE_COEP_HEADER = "require-corp";
    private static final String COEP_ENFORCING_HEADER = "Cross-Origin-Embedder-Policy";
    private static final String COEP_REPORT_HEADER = "Cross-Origin-Embedder-Policy-Report-Only";

    private final Set<String> exemptedPaths = new HashSet<>();
    private boolean disabled = false;
    private String header = COEP_ENFORCING_HEADER;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        invocation.addPreResultListener(this);
        return invocation.invoke();
    }

    @Override
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        HttpServletRequest req = invocation.getInvocationContext().getServletRequest();
        HttpServletResponse res = invocation.getInvocationContext().getServletResponse();
        final String path = req.getContextPath();

        if (exemptedPaths.contains(path)) {
            // no need to add headers
            LOG.debug("Skipping COEP header for exempted path {}", path);
        } else if (!disabled) {
            res.setHeader(header, REQUIRE_COEP_HEADER);
        }
    }

    public void setExemptedPaths(String paths) {
        this.exemptedPaths.addAll(TextParseUtil.commaDelimitedStringToSet(paths));
    }

    public void setEnforcingMode(String mode) {
        boolean enforcingMode = Boolean.parseBoolean(mode);
        if (enforcingMode) {
            header = COEP_ENFORCING_HEADER;
        } else {
            header = COEP_REPORT_HEADER;
        }
    }

    public void setDisabled(String value) {
        disabled = Boolean.parseBoolean(value);
    }

}
