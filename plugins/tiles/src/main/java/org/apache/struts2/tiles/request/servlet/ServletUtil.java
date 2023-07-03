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

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.RequestWrapper;
import org.apache.tiles.request.servlet.NotAServletEnvironmentException;
import org.apache.tiles.request.servlet.ServletApplicationContext;

import java.io.IOException;

public class ServletUtil {

    public static IOException wrapServletException(ServletException ex, String message) {
        Throwable rootCause = ex.getRootCause();
        IOException retValue;
        if (rootCause != null) {
            retValue = new IOException(message, rootCause);
        } else {
            retValue = new IOException(message, ex);
        }

        return retValue;
    }

    public static ApplicationContext getApplicationContext(ServletContext servletContext) {
        return (ApplicationContext)servletContext.getAttribute(ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE);
    }

    public static ServletRequest getServletRequest(Request request) {
        for(Request currentRequest = request; currentRequest != null; currentRequest = ((RequestWrapper)currentRequest).getWrappedRequest()) {
            if (currentRequest instanceof ServletRequest) {
                return (ServletRequest)currentRequest;
            }

            if (!(currentRequest instanceof RequestWrapper)) {
                throw new NotAServletEnvironmentException("Not a Servlet environment, not supported");
            }
        }

        throw new NotAServletEnvironmentException("Last Tiles request context is null");
    }

    public static ServletContext getServletContext(ApplicationContext applicationContext) {
        if (applicationContext instanceof org.apache.tiles.request.servlet.ServletApplicationContext) {
            return (ServletContext)((ServletApplicationContext)applicationContext).getContext();
        } else {
            throw new NotAServletEnvironmentException("Not a Servlet-based environment");
        }
    }
}
