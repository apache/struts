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

import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.PageContext;

/**
 * JSP utilities for JSP requests and related.
 */
public final class JspUtil {

    /**
     * Constructor.
     */
    private JspUtil() {
    }

    /**
     * Returns the application context. It must be
     * first saved creating an {@link ApplicationContext} and using
     * {@link ApplicationAccess#register(ApplicationContext)}.
     *
     * @param jspContext The JSP context.
     * @return The application context.
     */
    public static ApplicationContext getApplicationContext(JspContext jspContext) {
        return (ApplicationContext) jspContext.getAttribute(
            ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE,
            PageContext.APPLICATION_SCOPE
        );
    }
}
