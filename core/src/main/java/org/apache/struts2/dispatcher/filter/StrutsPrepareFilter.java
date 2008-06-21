/*
 * $Id: DefaultActionSupport.java 651946 2008-04-27 13:41:38Z apetrelli $
 *
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
package org.apache.struts2.dispatcher.filter;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Prepares the request for execution by a later {@link StrutsExecuteFilter} filter instance.
 */
public class StrutsPrepareFilter implements StrutsStatics, Filter {
    private PrepareOperations prepare;
    private CleanupOperations cleanup;

    public void init(FilterConfig filterConfig) throws ServletException {
        InitOperations init = new InitOperations();
        try {
            init.initLogging(filterConfig);
            Dispatcher dispatcher = init.initDispatcher(filterConfig);

            prepare = new PrepareOperations(filterConfig.getServletContext(), dispatcher);
            cleanup = new CleanupOperations(dispatcher);
        } finally {
            cleanup.cleanupInit();
        }

    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            prepare.createActionContext();
            prepare.assignDispatcherToThread();
            prepare.setEncodingAndLocale(request, response);
            request = prepare.wrapRequest(request);
            prepare.findActionMapping(request, response);

            chain.doFilter(request, response);
        } finally {
            cleanup.cleanupRequest();
        }
    }

    public void destroy() {
        cleanup.cleanupDispatcher();
    }
}