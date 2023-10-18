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

package org.apache.tiles.velocity.template;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

/**
 * An object that holds the current state of Velocity in a Servlet environment.
 *
 * @since 2.2.0
 */
public class ContextHolder {

    /**
     * The Velocity context.
     */
    private Context velocityContext;

    /**
     * The HTTP request.
     */
    private HttpServletRequest request;

    /**
     * The HTTP response.
     */
    private HttpServletResponse response;

    /**
     * The servlet context.
     */
    private ServletContext application;

    /**
     * Sets the current {@link HttpServletRequest}. This is required for this tool
     * to operate and will throw a NullPointerException if this is not set or is set
     * to {@code null}.
     *
     * @param request The HTTP request.
     * @since 2.2.0
     */
    public void setRequest(HttpServletRequest request) {
        if (request == null) {
            throw new NullPointerException("request should not be null");
        }
        this.request = request;
    }

    /**
     * Sets the current {@link HttpServletResponse}. This is required for this tool
     * to operate and will throw a NullPointerException if this is not set or is set
     * to {@code null}.
     *
     * @param response The HTTP response.
     * @since 2.2.0
     */
    public void setResponse(HttpServletResponse response) {
        if (response == null) {
            throw new NullPointerException("response should not be null");
        }
        this.response = response;
    }

    /**
     * Sets the {@link ServletContext}. This is required for this tool to operate
     * and will throw a NullPointerException if this is not set or is set to
     * {@code null}.
     *
     * @param application The Servlet context.
     * @since 2.2.0
     */
    public void setServletContext(ServletContext application) {
        if (application == null) {
            throw new NullPointerException("servlet context should not be null");
        }
        this.application = application;
    }

    /**
     * Sets the Velocity {@link Context}. This is required for this tool to operate
     * and will throw a NullPointerException if this is not set or is set to
     * {@code null}.
     *
     * @param context The Velocity context.
     * @since 2.2.0
     */
    public void setVelocityContext(Context context) {
        if (context == null) {
            throw new NullPointerException("velocity context should not be null");
        }
        this.velocityContext = context;
    }

    /**
     * Returns the HTTP request.
     *
     * @return The HTTP request.
     * @since 2.2.0
     */
    protected HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Returns the HTTP response.
     *
     * @return The HTTP response.
     * @since 2.2.0
     */
    protected HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Returns the Servlet context.
     *
     * @return The Servlet context..
     * @since 2.2.0
     */
    protected ServletContext getServletContext() {
        return application;
    }

    /**
     * Returns the Velocity context..
     *
     * @return The Velocity context.
     * @since 2.2.0
     */
    protected Context getVelocityContext() {
        return velocityContext;
    }
}
