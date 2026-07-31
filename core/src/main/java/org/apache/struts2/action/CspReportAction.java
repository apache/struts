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
package org.apache.struts2.action;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ActionSupport;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.Inject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

import static org.apache.struts2.interceptor.csp.CspSettings.CSP_REPORT_TYPE;

/**
 * An abstract Action that can be extended to process the incoming CSP violation reports. Performs
 * necessary checks to extract the JSON string of the CSP report and make sure it's a valid report.
 * Always returns a 204 response.
 *
 * Override the <code>processReport(String jsonCspReport)</code> method to customize how the action processes
 * the CSP report. See {@link DefaultCspReportAction} for the default implementation.
 *
 * Add the action to the endpoint that is the <code>reportUri</code> in the {@link org.apache.struts2.interceptor.csp.CspInterceptor}
 * to collect the reports.
 *
 * <pre>
 *     &lt;package name="csp-reports" namespace="/" extends="struts-default"&gt;
 *         &lt;action name="csp-reports" class="org.apache.struts2.action.DefaultCspReportAction"&gt;
 *             &lt;result type="httpheader"&gt;
 *                 &lt;param name="status">204&lt;/param&gt;
 *             &lt;/result&gt;
 *         &lt;/action&gt;
 *     &lt;/package&gt;
 * </pre>
 *
 * @see DefaultCspReportAction
 */
public abstract class CspReportAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

    private static final Logger LOG = LogManager.getLogger(CspReportAction.class);

    /**
     * Default upper bound, in characters, on the report body accepted by {@link #withServletRequest}.
     * CSP violation reports are small JSON documents; anything larger is not treated as a report.
     */
    public static final int DEFAULT_MAX_REPORT_SIZE = 8192;

    /**
     * Largest value accepted for {@code struts.csp.report.maxSize}. A configured value above this is
     * ignored, so that a mistyped setting cannot size a per-request buffer large enough to exhaust
     * memory.
     */
    private static final int MAX_REPORT_SIZE_LIMIT = 1024 * 1024;

    private HttpServletRequest request;
    private int maxReportSize = DEFAULT_MAX_REPORT_SIZE;

    /**
     * Sets the upper bound, in characters, on an accepted report body. A body exceeding this size is
     * discarded and not passed to {@link #processReport(String)}.
     * <p>
     * The value is injected from {@code struts.csp.report.maxSize} when the action is built, which is
     * before the interceptor stack runs. It is deliberately not an action property: the report body is
     * read by {@link #withServletRequest(HttpServletRequest)}, which the {@code servletConfig}
     * interceptor invokes ahead of {@code staticParams} and {@code params}, so a value applied by
     * either of those would arrive too late to have any effect.
     *
     * @param maxReportSize maximum accepted report size in characters
     * @since 7.3.0
     */
    @Inject(value = StrutsConstants.STRUTS_CSP_REPORT_MAX_SIZE, required = false)
    public void setMaxReportSize(String maxReportSize) {
        if (StringUtils.isBlank(maxReportSize)) {
            return;
        }
        int size;
        try {
            size = Integer.parseInt(maxReportSize.trim());
        } catch (NumberFormatException e) {
            LOG.warn("Ignoring non-numeric {} value: {}, keeping {}",
                    StrutsConstants.STRUTS_CSP_REPORT_MAX_SIZE, maxReportSize, this.maxReportSize);
            return;
        }
        if (size < 1 || size > MAX_REPORT_SIZE_LIMIT) {
            LOG.warn("Ignoring out-of-range {} value: {}, expected 1..{}, keeping {}",
                    StrutsConstants.STRUTS_CSP_REPORT_MAX_SIZE, size, MAX_REPORT_SIZE_LIMIT, this.maxReportSize);
            return;
        }
        this.maxReportSize = size;
    }

    @Override
    public void withServletRequest(HttpServletRequest request) {
        if (!isCspReportRequest(request)) {
            return;
        }

        try {
            String cspReport = readReport(request.getReader());
            if (cspReport == null) {
                LOG.warn("Discarding CSP report larger than the configured limit of {} characters", maxReportSize);
                return;
            }
            processReport(cspReport);
        } catch (IOException ignored) {
        }
    }

    /**
     * Reads at most {@link #maxReportSize} characters from the report body.
     *
     * @param reader reader over the report body
     * @return the report body, or {@code null} if it exceeds the configured limit
     */
    private String readReport(Reader reader) throws IOException {
        char[] buffer = new char[maxReportSize];
        int total = 0;
        int read;
        while (total < buffer.length && (read = reader.read(buffer, total, buffer.length - total)) != -1) {
            total += read;
        }
        if (total == buffer.length && reader.read() != -1) {
            return null;
        }
        return new String(buffer, 0, total);
    }

    private boolean isCspReportRequest(HttpServletRequest request) {
        if (!"POST".equals(request.getMethod()) || request.getContentLength() <= 0){
            return false;
        }

        String contentType = request.getContentType();
        return CSP_REPORT_TYPE.equals(contentType);
    }

    @Override
    public void withServletResponse(HttpServletResponse response) {
        response.setStatus(204);
    }

    abstract void processReport(String jsonCspReport);

    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getServletRequest() {
        return request;
    }
}
