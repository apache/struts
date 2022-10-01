/*
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
package org.apache.tiles.request.jsp;

import org.apache.tiles.request.AbstractViewRequest;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.DispatchRequest;
import org.apache.tiles.request.collection.ScopeMap;
import org.apache.tiles.request.jsp.extractor.ScopeExtractor;
import org.apache.tiles.request.jsp.extractor.SessionScopeExtractor;
import org.apache.tiles.request.servlet.ServletRequest;
import org.apache.tiles.request.servlet.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Context implementation used for executing tiles within a jsp tag library.
 */
public class JspRequest extends AbstractViewRequest {

    /**
     * The native available scopes.
     */
    private static final List<String> SCOPES = Collections.unmodifiableList(Arrays.asList("page", REQUEST_SCOPE, "session", APPLICATION_SCOPE));

    /**
     * The current page context.
     */
    private final PageContext pageContext;

    /**
     * <p>The lazily instantiated <code>Map</code> of page scope
     * attributes.</p>
     */
    private Map<String, Object> pageScope = null;

    /**
     * <p>The lazily instantiated <code>Map</code> of request scope
     * attributes.</p>
     */
    private Map<String, Object> requestScope = null;

    /**
     * <p>The lazily instantiated <code>Map</code> of session scope
     * attributes.</p>
     */
    private Map<String, Object> sessionScope = null;

    /**
     * <p>The lazily instantiated <code>Map</code> of application scope
     * attributes.</p>
     */
    private Map<String, Object> applicationScope = null;

    /**
     * Creates a JSP request.
     *
     * @param applicationContext The application context.
     * @param pageContext The page context.
     * @return A new JSP request.
     */
    public static JspRequest createServletJspRequest(ApplicationContext applicationContext, PageContext pageContext) {
        return new JspRequest(new ServletRequest(
                applicationContext, (HttpServletRequest) pageContext
                        .getRequest(), (HttpServletResponse) pageContext
                        .getResponse()), pageContext);
    }

    /**
     * Constructor.
     *
     * @param enclosedRequest The request that is wrapped here.
     * @param pageContext The page context to use.
     */
    public JspRequest(DispatchRequest enclosedRequest,
            PageContext pageContext) {
        super(enclosedRequest);
        this.pageContext = pageContext;
    }

    @Override
    public List<String> getAvailableScopes() {
        return SCOPES;
    }

    /** {@inheritDoc} */
    @Override
    protected void doInclude(String path) throws IOException {
        try {
            pageContext.include(path, false);
        } catch (ServletException e) {
            throw ServletUtil.wrapServletException(e, "JSPException including path '"
                    + path + "'.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public PrintWriter getPrintWriter() {
        return new JspPrintWriterAdapter(pageContext.getOut());
    }

    /** {@inheritDoc} */
    @Override
    public Writer getWriter() {
        return pageContext.getOut();
    }

    /**
     * Returns the page scope.
     *
     * @return The page scope.
     */
    public Map<String, Object> getPageScope() {
        if ((pageScope == null) && (pageContext != null)) {
            pageScope = new ScopeMap(new ScopeExtractor(pageContext,
                    PageContext.PAGE_SCOPE));
        }
        return (pageScope);
    }

    /**
     * Returns the request scope.
     *
     * @return The request scope.
     */
    public Map<String, Object> getRequestScope() {
        if ((requestScope == null) && (pageContext != null)) {
            requestScope = new ScopeMap(new ScopeExtractor(pageContext,
                    PageContext.REQUEST_SCOPE));
        }
        return (requestScope);
    }

    /**
     * Returns the session scope.
     *
     * @return The session scope.
     */
    public Map<String, Object> getSessionScope() {
        if ((sessionScope == null) && (pageContext != null)) {
            sessionScope = new ScopeMap(new SessionScopeExtractor(pageContext));
        }
        return (sessionScope);
    }

    /**
     * Returns the application scope.
     *
     * @return The application scope.
     */
    public Map<String, Object> getApplicationScope() {
        if ((applicationScope == null) && (pageContext != null)) {
            applicationScope = new ScopeMap(new ScopeExtractor(pageContext,
                    PageContext.APPLICATION_SCOPE));
        }
        return (applicationScope);
    }

    @Override
    public Map<String, Object> getContext(String scope) {
        if("page".equals(scope)){
            return getPageScope();
        }else if(REQUEST_SCOPE.equals(scope)){
            return getRequestScope();
        }else if("session".equals(scope)){
            return getSessionScope();
        }else if(APPLICATION_SCOPE.equals(scope)){
            return getApplicationScope();
        }
        throw new IllegalArgumentException(scope + " does not exist. Call getAvailableScopes() first to check.");
    }
}
