/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork.ActionContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <!-- SNIPPET START: description -->
 * Special filter designed to work with the {@link FilterDispatcher} and allow
 * for easier integration with SiteMesh. Normally, ordering your filters to have
 * SiteMesh go first, and then {@link FilterDispatcher} go second is perfectly fine.
 * However, sometimes you may wish to access Struts features, including the
 * value stack, from within your SiteMesh decorators. Because {@link FilterDispatcher}
 * cleans up the {@link ActionContext}, your decorator won't have access to the
 * date you want.
 * <p/>
 * <p/>
 * By adding this filter, the {@link FilterDispatcher} will know to not clean up and
 * instead defer cleanup to this filter. The ordering of the filters should then be:
 * <p/>
 * <ul>
 * <li>this filter</li>
 * <li>SiteMesh filter</li>
 * <li>{@link FilterDispatcher}</li>
 * </ul>
 * <!-- SNIPPET END: description -->
 *
 * @see FilterDispatcher
 */
public class ActionContextCleanUp implements Filter {

    private static final Log LOG = LogFactory.getLog(ActionContextCleanUp.class);

    private static final String COUNTER = "__cleanup_recursion_counter";

    protected FilterConfig filterConfig;

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        DispatcherUtils.initialize(filterConfig.getServletContext());
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // prepare the request no matter what - this ensures that the proper character encoding
        // is used before invoking the mapper (see WW-9127)
        DispatcherUtils du = DispatcherUtils.getInstance();
        du.prepare(request, response);


        ServletContext servletContext = filterConfig.getServletContext();
        try {
            request = du.wrapRequest(request, servletContext);
        } catch (IOException e) {
            String message = "Could not wrap servlet request with MultipartRequestWrapper!";
            LOG.error(message, e);
            throw new ServletException(message, e);
        }

        try {
            Integer count = (Integer)request.getAttribute(COUNTER);
            if (count == null) {
                count = new Integer(1);
            }
            else {
                count = new Integer(count.intValue()+1);
            }
            request.setAttribute(COUNTER, count);
            chain.doFilter(request, response);
        } finally {
            int counterVal = ((Integer)request.getAttribute(COUNTER)).intValue();
            counterVal -= 1;
            request.setAttribute(COUNTER, new Integer(counterVal));
            cleanUp(request);
        }
    }

    protected static void cleanUp(ServletRequest req) {
        // should we clean up yet?
        if (req.getAttribute(COUNTER) != null &&
                 ((Integer)req.getAttribute(COUNTER)).intValue() > 0 ) {
             return;
         }

        // always dontClean up the thread request, even if an action hasn't been executed
        ActionContext.setContext(null);
    }

    public void destroy() {
    }
}
