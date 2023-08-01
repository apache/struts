/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.portlet;

import org.apache.tiles.request.AbstractClientRequest;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.attribute.Addable;
import org.apache.tiles.request.collection.HeaderValuesMap;
import org.apache.tiles.request.collection.ReadOnlyEnumerationMap;
import org.apache.tiles.request.collection.ScopeMap;
import org.apache.tiles.request.portlet.delegate.RequestDelegate;
import org.apache.tiles.request.portlet.delegate.ResponseDelegate;
import org.apache.tiles.request.portlet.extractor.HeaderExtractor;
import org.apache.tiles.request.portlet.extractor.RequestScopeExtractor;
import org.apache.tiles.request.portlet.extractor.SessionScopeExtractor;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Portlet-based TilesApplicationContext implementation.
 */
public class PortletRequest extends AbstractClientRequest {

    /**
     * The native available scopes.
     */
    private static final List<String> SCOPES = Collections.unmodifiableList(Arrays.asList(REQUEST_SCOPE, "portletSession", "session", APPLICATION_SCOPE));

    /**
     * <p>The lazily instantiated <code>Map</code> of header name-value
     * combinations (immutable).</p>
     */
    private Map<String, String> header = null;


    /**
     * <p>The lazily instantiated <code>Map</code> of header name-value
     * combinations (write-only).</p>
     */
    private Addable<String> responseHeaders = null;


    /**
     * <p>The lazily instantiated <code>Map</code> of header name-values
     * combinations (immutable).</p>
     */
    private Map<String, String[]> headerValues = null;

    /**
     * The <code>PortletContext</code> for this application.
     */
    protected PortletContext context;

    /**
     * <p>The <code>PortletRequest</code> for this request.</p>
     */
    protected javax.portlet.PortletRequest request;

    /**
     * The delegate to get information about parameters.
     */
    protected RequestDelegate requestDelegate;


    /**
     * <p>The lazily instantiated <code>Map</code> of request scope
     * attributes.</p>
     */
    private Map<String, Object> requestScope = null;


    /**
     * <p>The <code>PortletResponse</code> for this request.</p>
     */
    protected PortletResponse response;

    /**
     * The delegate to get information from a response (output stream, writer, etc.).
     */
    protected ResponseDelegate responseDelegate;


    /**
     * <p>The lazily instantiated <code>Map</code> of session scope
     * attributes.</p>
     */
    private Map<String, Object> sessionScope = null;

    /**
     * <p>The lazily instantiated <code>Map</code> of portlet session scope
     * attributes.</p>
     */
    private Map<String, Object> portletSessionScope = null;


    /**
     * Creates a new instance of PortletTilesRequestContext.
     *
     * @param applicationContext The Tiles application context.
     * @param context            The portlet context to use.
     * @param request            The request object to use.
     * @param response           The response object to use.
     * @param requestDelegate    The request delegate.
     * @param responseDelegate   The response delegate.
     */
    public PortletRequest(ApplicationContext applicationContext,
                          PortletContext context, javax.portlet.PortletRequest request,
                          PortletResponse response, RequestDelegate requestDelegate, ResponseDelegate responseDelegate) {
        super(applicationContext);

        // Save the specified Portlet API object references
        this.context = context;
        this.request = request;
        this.response = response;
        this.requestDelegate = requestDelegate;
        this.responseDelegate = responseDelegate;
    }

    /**
     * <p>Return the {@link PortletRequest} for this context.</p>
     *
     * @return The used portlet request.
     */
    public javax.portlet.PortletRequest getRequest() {
        return (this.request);
    }

    /**
     * Returns the portlet context.
     *
     * @return The portlet context.
     */
    public PortletContext getPortletContext() {
        return context;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getHeader() {
        if ((header == null) && (request != null)) {
            header = new ReadOnlyEnumerationMap<>(new HeaderExtractor(request, null));
        }
        return (header);
    }

    /**
     * {@inheritDoc}
     */
    public Addable<String> getResponseHeaders() {
        if ((responseHeaders == null) && (request != null)) {
            responseHeaders = new HeaderExtractor(null, response);
        }
        return (responseHeaders);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String[]> getHeaderValues() {
        if ((headerValues == null) && (request != null)) {
            headerValues = new HeaderValuesMap(new HeaderExtractor(request, response));
        }
        return (headerValues);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getRequestScope() {
        if ((requestScope == null) && (request != null)) {
            requestScope = new ScopeMap(new RequestScopeExtractor(request));
        }
        return (requestScope);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getSessionScope() {
        if ((sessionScope == null) && (request != null)) {
            sessionScope = new ScopeMap(new SessionScopeExtractor(request,
                PortletSession.APPLICATION_SCOPE));
        }
        return (sessionScope);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getPortletSessionScope() {
        if ((portletSessionScope == null) && (request != null)) {
            portletSessionScope = new ScopeMap(new SessionScopeExtractor(
                request, PortletSession.APPLICATION_SCOPE));
        }
        return (portletSessionScope);
    }

    @Override
    public List<String> getAvailableScopes() {
        return SCOPES;
    }

    /**
     * {@inheritDoc}
     */
    public Locale getRequestLocale() {
        return request.getLocale();
    }

    @Override
    public Map<String, String> getParam() {
        return requestDelegate.getParam();
    }

    @Override
    public Map<String, String[]> getParamValues() {
        return requestDelegate.getParamValues();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return responseDelegate.getOutputStream();
    }

    @Override
    public PrintWriter getPrintWriter() throws IOException {
        return responseDelegate.getPrintWriter();
    }

    @Override
    public Writer getWriter() throws IOException {
        return responseDelegate.getWriter();
    }

    @Override
    public boolean isResponseCommitted() {
        return responseDelegate.isResponseCommitted();
    }

    @Override
    public void setContentType(String contentType) {
        responseDelegate.setContentType(contentType);
    }

    /**
     * {@inheritDoc}
     */
    public void doForward(String path) throws IOException {
        if (responseDelegate.isResponseCommitted()) {
            doInclude(path);
            return;
        }

        try {
            PortletRequestDispatcher rd = getPortletContext()
                .getRequestDispatcher(path);

            if (rd == null) {
                throw new IOException(
                    "No portlet request dispatcher returned for path '"
                        + path + "'");
            }

            rd.forward(request, response);
        } catch (PortletException e) {
            throw new IOException("PortletException while including path '"
                + path + "'.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void doInclude(String path) throws IOException {
        try {
            PortletRequestDispatcher rd = getPortletContext()
                .getRequestDispatcher(path);

            if (rd == null) {
                throw new IOException(
                    "No portlet request dispatcher returned for path '"
                        + path + "'");
            }

            rd.include(request, response);
        } catch (PortletException e) {
            throw new IOException("PortletException while including path '"
                + path + "'.", e);
        }
    }

    @Override
    public Map<String, Object> getContext(String scope) {
        if (REQUEST_SCOPE.equals(scope)) {
            return getRequestScope();
        } else if ("session".equals(scope)) {
            return getSessionScope();
        } else if ("portletSession".equals(scope)) {
            return getPortletSessionScope();
        } else if (APPLICATION_SCOPE.equals(scope)) {
            return getApplicationScope();
        }
        throw new IllegalArgumentException(scope + " does not exist. Call getAvailableScopes() first to check.");
    }
}
