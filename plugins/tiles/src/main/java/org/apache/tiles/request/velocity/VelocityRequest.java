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

package org.apache.tiles.request.velocity;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.request.AbstractViewRequest;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.DispatchRequest;
import org.apache.tiles.request.servlet.ExternalWriterHttpServletResponse;
import org.apache.tiles.request.servlet.ServletRequest;
import org.apache.tiles.request.servlet.ServletUtil;
import org.apache.velocity.context.Context;

/**
 * The implementation of the Tiles request context specific for Velocity.
 */
public class VelocityRequest extends AbstractViewRequest {

    /**
     * The native available scopes, in fact only "page".
     */
    private final List<String> scopes;

    /**
     * The Velocity current context.
     */
    private final Context ctx;

    /**
     * The writer to use to render the response. It may be null, if not necessary.
     */
    private Writer writer;

    /**
     * The map of the page scope.
     */
    private Map<String, Object> pageScope;

    /**
     * Factory method to create a Velocity request.
     *
     * @param applicationContext The application context.
     * @param request            The request.
     * @param response           The response.
     * @param velocityContext    The Velocity context.
     * @param writer             The writer to write into.
     * @return The request.
     */
    public static VelocityRequest createVelocityRequest(ApplicationContext applicationContext,
            HttpServletRequest request, HttpServletResponse response, Context velocityContext, Writer writer) {
        DispatchRequest servletRequest = new ServletRequest(applicationContext, request, response);
        VelocityRequest velocityRequest = new VelocityRequest(servletRequest, velocityContext, writer);
        return velocityRequest;
    }

    /**
     * Constructor.
     *
     * @param enclosedRequest The request that exposes non-Velocity specific
     *                        properties
     * @param ctx             The Velocity current context.
     * @param writer          The writer to use to render the response. It may be
     *                        null, if not necessary.
     */
    public VelocityRequest(DispatchRequest enclosedRequest, Context ctx, Writer writer) {
        super(enclosedRequest);
        List<String> scopes = new ArrayList<String>();
        scopes.addAll(enclosedRequest.getAvailableScopes());
        scopes.add("page");
        this.scopes = Collections.unmodifiableList(scopes);
        this.ctx = ctx;
        this.writer = writer;
    }

    @Override
    public List<String> getAvailableScopes() {
        return scopes;
    }

    /** {@inheritDoc} */
    @Override
    protected void doInclude(String path) throws IOException {
        ServletRequest servletRequest = org.apache.tiles.request.servlet.ServletUtil.getServletRequest(this);
        HttpServletRequest request = servletRequest.getRequest();
        HttpServletResponse response = servletRequest.getResponse();
        RequestDispatcher rd = request.getRequestDispatcher(path);

        if (rd == null) {
            throw new IOException("No request dispatcher returned for path '" + path + "'");
        }

        PrintWriter printWriter = getPrintWriter();
        try {
            rd.include(request, new ExternalWriterHttpServletResponse(response, printWriter));
        } catch (ServletException ex) {
            throw ServletUtil.wrapServletException(ex, "ServletException including path '" + path + "'.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public PrintWriter getPrintWriter() {
        if (writer == null) {
            throw new IllegalStateException(
                    "A writer-less Tiles request has been created, cannot return a PrintWriter");
        }
        if (writer instanceof PrintWriter) {
            return (PrintWriter) writer;
        }
        return new PrintWriter(writer);
    }

    /** {@inheritDoc} */
    @Override
    public Writer getWriter() {
        if (writer == null) {
            throw new IllegalStateException(
                    "A writer-less Tiles request has been created, cannot return a PrintWriter");
        }
        return writer;
    }

    /**
     * Returns the page scope.
     *
     * @return The page scope.
     */
    public Map<String, Object> getPageScope() {
        if (pageScope == null) {
            pageScope = new VelocityScopeMap(ctx);
        }
        return pageScope;
    }

    @Override
    public Map<String, Object> getContext(String scope) {
        return "page".equals(scope) ? getPageScope() : super.getContext(scope);
    }

}
