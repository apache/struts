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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.ExecuteOperations;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Handles both the preparation and execution phases of the Struts dispatching process.  This filter is better to use
 * when you don't have another filter that needs access to action context information, such as Sitemesh.
 */
public class StrutsPrepareAndExecuteFilter implements StrutsStatics, Filter {

    private static final Logger LOG = LogManager.getLogger(StrutsPrepareAndExecuteFilter.class);

    protected PrepareOperations prepare;
    protected ExecuteOperations execute;
    protected List<Pattern> excludedPatterns = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        InitOperations init = createInitOperations();
        Dispatcher dispatcher = null;
        try {
            FilterHostConfig config = new FilterHostConfig(filterConfig);
            init.initLogging(config);
            dispatcher = init.initDispatcher(config);
            init.initStaticContentLoader(config, dispatcher);

            prepare = createPrepareOperations(dispatcher);
            execute = createExecuteOperations(dispatcher);
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
     * Creates a new instance of {@link ExecuteOperations} to be used during
     * initialising {@link Dispatcher}
     *
     * @return instance of {@link ExecuteOperations}
     */
    protected ExecuteOperations createExecuteOperations(Dispatcher dispatcher) {
        return new ExecuteOperations(dispatcher);
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

        try {
            String uri = RequestUtils.getUri(request);
            if (excludedPatterns != null && prepare.isUrlExcluded(request, excludedPatterns)) {
                LOG.trace("Request {} is excluded from handling by Struts, passing request to other filters", uri);
                chain.doFilter(request, response);
            } else {
                LOG.trace("Checking if {} is a static resource", uri);
                boolean handled = execute.executeStaticResourceRequest(request, response);
                if (!handled) {
                    LOG.trace("Assuming uri {} as a normal action", uri);
                    prepare.setEncodingAndLocale(request, response);
                    prepare.createActionContext(request, response);
                    prepare.assignDispatcherToThread();
                    request = prepare.wrapRequest(request);
                    ActionMapping mapping = prepare.findActionMapping(request, response, true);
                    if (mapping == null) {
                        LOG.trace("Cannot find mapping for {}, passing to other filters", uri);
                        chain.doFilter(request, response);
                    } else {
                        LOG.trace("Found mapping {} for {}", mapping, uri);
                        execute.executeAction(request, response, mapping);
                    }
                }
            }
        } finally {
            prepare.cleanupRequest(request);
        }
    }

    public void destroy() {
        prepare.cleanupDispatcher();
    }

}
