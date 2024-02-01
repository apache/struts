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
package org.apache.struts2.dispatcher.filter;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Prepares the request for execution by a later {@link org.apache.struts2.dispatcher.filter.StrutsExecuteFilter} filter instance.
 */
public class StrutsPrepareFilter implements StrutsStatics, Filter {

    protected static final String REQUEST_EXCLUDED_FROM_ACTION_MAPPING = StrutsPrepareFilter.class.getName() + ".REQUEST_EXCLUDED_FROM_ACTION_MAPPING";

    protected PrepareOperations prepare;
    protected List<Pattern> excludedPatterns;

    public void init(FilterConfig filterConfig) throws ServletException {
        InitOperations init = createInitOperations();
        Dispatcher dispatcher = null;
        try {
            FilterHostConfig config = new FilterHostConfig(filterConfig);
            dispatcher = init.initDispatcher(config);

            prepare = createPrepareOperations(dispatcher);
            // Note: Currently, excluded patterns are not refreshed following an XWork config reload
            this.excludedPatterns = init.buildExcludedPatternsList(dispatcher);

            postInit(dispatcher, filterConfig);
        } finally {
            if (dispatcher != null) {
                dispatcher.cleanUpAfterInit();
            }
            init.cleanup();
        }
    }

    /**
     * Creates a new instance of {@link InitOperations} to be used during
     * initialising {@link Dispatcher}
     *
     * @return instance of {@link InitOperations}
     */
    protected InitOperations createInitOperations() {
        return new InitOperations();
    }

    /**
     * Creates a new instance of {@link PrepareOperations} to be used during
     * initialising {@link Dispatcher}
     *
     * @return instance of {@link PrepareOperations}
     */
    protected PrepareOperations createPrepareOperations(Dispatcher dispatcher) {
        return new PrepareOperations(dispatcher);
    }

    /**
     * Callback for post initialization
     *
     * @param dispatcher the dispatcher
     * @param filterConfig the filter config
     */
    protected void postInit(Dispatcher dispatcher, FilterConfig filterConfig) {
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        boolean didWrap = false;
        try {
            prepare.trackRecursion(request);
            if (prepare.isUrlExcluded(request, excludedPatterns)) {
                request.setAttribute(REQUEST_EXCLUDED_FROM_ACTION_MAPPING, true);
            } else {
                request.setAttribute(REQUEST_EXCLUDED_FROM_ACTION_MAPPING, false);
                prepare.setEncodingAndLocale(request, response);
                prepare.createActionContext(request, response);
                prepare.assignDispatcherToThread();
                request = prepare.wrapRequest(request);
                didWrap = true;
                prepare.findActionMapping(request, response, true);
            }
            chain.doFilter(request, response);
        } finally {
            if (didWrap) {
                prepare.cleanupWrappedRequest(request);
            }
            prepare.cleanupRequest(request);
        }
    }

    public void destroy() {
        prepare.cleanupDispatcher();
    }

}
