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

import com.opensymphony.xwork2.ActionSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

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
 *                 &lt;param name="statusCode">200&lt;/param&gt;
 *             &lt;/result&gt;
 *         &lt;/action&gt;
 *     &lt;/package&gt;
 * </pre>
 *
 * @see DefaultCspReportAction
 */
public abstract class CspReportAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {
    private HttpServletRequest request;

    @Override
    public void withServletRequest(HttpServletRequest request) {
        if (!isCspReportRequest(request)) {
            return;
        }

        try {
            BufferedReader reader = request.getReader();
            String cspReport = reader.readLine();
            processReport(cspReport);
        } catch (IOException ignored) {
        }
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
