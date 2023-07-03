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
package org.apache.struts2.tiles.request.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts2.tiles.request.servlet.extractor.HeaderExtractor;
import org.apache.struts2.tiles.request.servlet.extractor.ParameterExtractor;
import org.apache.struts2.tiles.request.servlet.extractor.RequestScopeExtractor;
import org.apache.struts2.tiles.request.servlet.extractor.SessionScopeExtractor;
import org.apache.tiles.request.AbstractClientRequest;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.attribute.Addable;
import org.apache.tiles.request.collection.HeaderValuesMap;
import org.apache.tiles.request.collection.ReadOnlyEnumerationMap;
import org.apache.tiles.request.collection.ScopeMap;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ServletRequest extends AbstractClientRequest {
    private static final List<String> SCOPES = Collections.unmodifiableList(Arrays.asList("request", "session", "application"));
    private HttpServletRequest request;
    private HttpServletResponse response;
    private OutputStream outputStream;
    private PrintWriter writer;
    private Map<String, String> header = null;
    private Addable<String> responseHeaders = null;
    private Map<String, String[]> headerValues = null;
    private Map<String, String> param = null;
    private Map<String, Object> requestScope = null;
    private Map<String, Object> sessionScope = null;

    public ServletRequest(ApplicationContext applicationContext, HttpServletRequest request, HttpServletResponse response) {
        super(applicationContext);
        this.request = request;
        this.response = response;
    }

    public Map<String, String> getHeader() {
        if (this.header == null && this.request != null) {
            this.header = new ReadOnlyEnumerationMap(new HeaderExtractor(this.request, (HttpServletResponse)null));
        }

        return this.header;
    }

    public Addable<String> getResponseHeaders() {
        if (this.responseHeaders == null && this.response != null) {
            this.responseHeaders = new HeaderExtractor((HttpServletRequest)null, this.response);
        }

        return this.responseHeaders;
    }

    public Map<String, String[]> getHeaderValues() {
        if (this.headerValues == null && this.request != null) {
            this.headerValues = new HeaderValuesMap(new HeaderExtractor(this.request, this.response));
        }

        return this.headerValues;
    }

    public Map<String, String> getParam() {
        if (this.param == null && this.request != null) {
            this.param = new ReadOnlyEnumerationMap(new ParameterExtractor(this.request));
        }

        return this.param;
    }

    public Map<String, String[]> getParamValues() {
        return this.request.getParameterMap();
    }

    public Map<String, Object> getContext(String scope) {
        if ("request".equals(scope)) {
            return this.getRequestScope();
        } else if ("session".equals(scope)) {
            return this.getSessionScope();
        } else if ("application".equals(scope)) {
            return this.getApplicationScope();
        } else {
            throw new IllegalArgumentException(scope + " does not exist. Call getAvailableScopes() first to check.");
        }
    }

    public Map<String, Object> getRequestScope() {
        if (this.requestScope == null && this.request != null) {
            this.requestScope = new ScopeMap(new RequestScopeExtractor(this.request));
        }

        return this.requestScope;
    }

    public Map<String, Object> getSessionScope() {
        if (this.sessionScope == null && this.request != null) {
            this.sessionScope = new ScopeMap(new SessionScopeExtractor(this.request));
        }

        return this.sessionScope;
    }

    public List<String> getAvailableScopes() {
        return SCOPES;
    }

    public void doForward(String path) throws IOException {
        if (this.response.isCommitted()) {
            this.doInclude(path);
        } else {
            this.forward(path);
        }

    }

    public void doInclude(String path) throws IOException {
        RequestDispatcher rd = this.request.getRequestDispatcher(path);
        if (rd == null) {
            throw new IOException("No request dispatcher returned for path '" + path + "'");
        } else {
            try {
                rd.include(this.request, this.response);
            } catch (ServletException exception) {
                throw ServletUtil.wrapServletException(exception, "ServletException including path '" + path + "'.");
            }
        }
    }

    private void forward(String path) throws IOException {
        RequestDispatcher rd = this.request.getRequestDispatcher(path);
        if (rd == null) {
            throw new IOException("No request dispatcher returned for path '" + path + "'");
        } else {
            try {
                rd.forward(this.request, this.response);
            } catch (ServletException var4) {
                throw ServletUtil.wrapServletException(var4, "ServletException including path '" + path + "'.");
            }
        }
    }

    public OutputStream getOutputStream() throws IOException {
        if (this.outputStream == null) {
            this.outputStream = this.response.getOutputStream();
        }

        return this.outputStream;
    }

    public Writer getWriter() throws IOException {
        return this.getPrintWriter();
    }

    public PrintWriter getPrintWriter() throws IOException {
        if (this.writer == null) {
            this.writer = this.response.getWriter();
        }

        return this.writer;
    }

    public boolean isResponseCommitted() {
        return this.response.isCommitted();
    }

    public void setContentType(String contentType) {
        this.response.setContentType(contentType);
    }

    public Locale getRequestLocale() {
        return this.request.getLocale();
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public boolean isUserInRole(String role) {
        return this.request.isUserInRole(role);
    }
}
