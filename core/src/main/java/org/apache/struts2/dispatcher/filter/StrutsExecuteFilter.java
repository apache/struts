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
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.ExecuteOperations;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            InitOperations init = new InitOperations();
            Dispatcher dispatcher = init.findDispatcherOnThread();
            init.initStaticContentLoader(new FilterHostConfig(filterConfig), dispatcher);

            prepare = new PrepareOperations(dispatcher);
            execute = new ExecuteOperations(dispatcher);
        }

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

        //if recursion counter is > 1, it means we are in a "forward", in that case a mapping will still be
        //in the request, if we handle it, it will lead to an infinite loop, see WW-3077
        Integer recursionCounter = (Integer) request.getAttribute(PrepareOperations.CLEANUP_RECURSION_COUNTER);

        if (mapping == null || recursionCounter > 1) {
            boolean handled = execute.executeStaticResourceRequest(request, response);
            if (!handled) {
                chain.doFilter(request, response);
            }
        } else {
            execute.executeAction(request, response, mapping);
        }
    }

    private boolean excludeUrl(HttpServletRequest request) {
        return request.getAttribute(StrutsPrepareFilter.REQUEST_EXCLUDED_FROM_ACTION_MAPPING) != null;
    }

    public void destroy() {
        prepare = null;
        execute = null;
        filterConfig = null;
    }

}
