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
import org.apache.struts2.dispatcher.ExecuteOperations;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Executes the discovered request information.  This filter requires the {@link StrutsPrepareFilter} to have already
 * been executed in the current chain.
 */
public class StrutsExecuteFilter implements StrutsStatics, Filter {
    protected PrepareOperations prepare;
    protected ExecuteOperations execute;

    protected FilterConfig filterConfig;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    protected synchronized void lazyInit() {
        if (execute == null) {
            InitOperations init = createInitOperations();
            Dispatcher dispatcher = init.findDispatcherOnThread();
            init.initStaticContentLoader(new FilterHostConfig(filterConfig), dispatcher);

            prepare = createPrepareOperations(dispatcher);
            execute = createExecuteOperations(dispatcher);
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
     * Creates a new instance of {@link ExecuteOperations} to be used during
     * initialising {@link Dispatcher}
     *
     * @return instance of {@link ExecuteOperations}
     */
    protected ExecuteOperations createExecuteOperations(Dispatcher dispatcher) {
        return new ExecuteOperations(dispatcher);
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (excludeUrl(request)) {
            chain.doFilter(request, response);
            return;
        }

        // This is necessary since we need the dispatcher instance, which was created by the prepare filter
        if (execute == null) {
            lazyInit();
        }

        ActionMapping mapping = prepare.findActionMapping(request, response);

        if (mapping != null) {
            execute.executeAction(request, response, mapping);
        } else if (!execute.executeStaticResourceRequest(request, response)) {
            chain.doFilter(request, response);
        }
    }

    private boolean excludeUrl(HttpServletRequest request) {
        return Boolean.TRUE.equals(request.getAttribute(StrutsPrepareFilter.REQUEST_EXCLUDED_FROM_ACTION_MAPPING));
    }

    public void destroy() {
        prepare = null;
        execute = null;
        filterConfig = null;
    }

}
