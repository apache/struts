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

package org.apache.tiles.request.servlet;

import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.RequestWrapper;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.io.IOException;

/**
 * Utilities for Tiles request servlet support.
 */
public final class ServletUtil {

    /**
     * Constructor.
     */
    private ServletUtil() {
    }

    /**
     * Wraps a ServletException to create an IOException with the root cause if present.
     *
     * @param ex The exception to wrap.
     * @param message The message of the exception.
     * @return The wrapped exception.
     */
    public static IOException wrapServletException(ServletException ex,
            String message) {
        IOException retValue;
        Throwable rootCause = ex.getRootCause();
        if (rootCause != null) {
            // Replace the ServletException with an IOException, with the root
            // cause of the first as the cause of the latter.
            retValue = new IOException(message, rootCause);
        } else {
            retValue = new IOException(message, ex);
        }

        return retValue;
    }

    /**
     * Returns the application context getting it from the servlet context. It must be
     * first saved creating a {@link ServletApplicationContext} and using
     * {@link ApplicationAccess#register(ApplicationContext)}.
     *
     * @param servletContext The servlet context.
     * @return The application context, if found, <code>null</code> otherwise.
     */
    public static ApplicationContext getApplicationContext(ServletContext servletContext) {
        return (ApplicationContext) servletContext
                .getAttribute(ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE);
    }

    /**
     * Opens a TilesRequestContext until it finds a ServletTilesRequestContext.
     *
     * @param request The request to open.
     * @return The servlet-based request context.
     * @throws NotAServletEnvironmentException If a servlet-based request
     * context could not be found.
     */
    public static ServletRequest getServletRequest(Request request) {
        Request currentRequest = request;
        while (true) {
            if (currentRequest == null) {
                throw new NotAServletEnvironmentException("Last Tiles request context is null");
            }

            if (currentRequest instanceof ServletRequest) {
                return (ServletRequest) currentRequest;
            }
            if (!(currentRequest instanceof RequestWrapper)) {
                throw new NotAServletEnvironmentException("Not a Servlet environment, not supported");
            }
            currentRequest = ((RequestWrapper) currentRequest).getWrappedRequest();
        }
    }

    /**
     * Gets a servlet context from a TilesApplicationContext.
     *
     * @param applicationContext The application context to analyze.
     * @return The servlet context.
     * @throws NotAServletEnvironmentException If the application context is not
     * servlet-based.
     */
    public static ServletContext getServletContext(ApplicationContext applicationContext) {
        if (applicationContext instanceof ServletApplicationContext) {
            return (ServletContext) applicationContext.getContext();
        }

        throw new NotAServletEnvironmentException("Not a Servlet-based environment");
    }
}
