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

public abstract class CspReportAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

    private HttpServletRequest request;

    public String execute() throws IOException {
        return "success";
    }

    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getServletRequest() {
        return this.request;
    }

    @Override
    public void withServletRequest(HttpServletRequest request) {
        BufferedReader reader = null;

        if (!isCspReportRequest(request)) {
            return;
        }

        try {
            reader = request.getReader();
            String cspReport = reader.readLine();
            processReport(cspReport);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void withServletResponse(HttpServletResponse response) {
        response.setStatus(204);
    }

    abstract void processReport(String jsonCspReport);

    //TODO nit should we change this into an if block within withServletRequest?
    private boolean isCspReportRequest(HttpServletRequest request) {
        if (!"POST".equals(request.getMethod()) || request.getContentLength() <= 0){
            return false;
        }

        String contentType = request.getContentType();
        if (contentType == null || !"application/csp-report".equals(contentType)) {
            return false;
        }

        return true;
    }
}
