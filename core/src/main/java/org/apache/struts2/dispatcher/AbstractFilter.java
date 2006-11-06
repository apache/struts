/*
 * $Id:  $
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
package org.apache.struts2.dispatcher;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An abstract for superclass for Struts2 filter, encapsulating common logics and
 * helper methods usefull to subclass, to avoid duplication.
 *
 * Common logics encapsulated:-
 * <ul>
 *  <li>
 *      Dispatcher instance creation through <code>createDispatcher</code> method that acts
 *      as a hook subclass could override. By default it creates an instance of Dispatcher.
 *  </li>
 *  <li>
 *      <code>postInit(FilterConfig)</code> is a hook subclass may use to add post initialization
 *      logics in <code>{@link javax.servlet.Filter#init(FilterConfig)}</code>. It is called before
 *      {@link javax.servlet.Filter#init(FilterConfig)} method ends.
 *  </li>
 *  <li>
 *      A default <code>{@link javax.servlet.Filter#destroy()}</code> that clean up Dispatcher by
 *      calling <code>dispatcher.cleanup()</code>
 *  </li>
 *  <li>
 *      <code>prepareDispatcherAndWrapRequest(HttpServletRequest, HttpServletResponse)</code> helper method
 *      that basically called <code>dispatcher.prepare()</code>, wrap the HttpServletRequest and return the
 *      wrapped version.
 *  </li>
 *  <li>
 *      Various other common helper methods like
 *      <ul>
 *          <li>getFilterConfig</li>
 *          <li>getServletContext</li>
 *      </ul>
 *  </li>
 * </ul>
 *
 *
 * @see Dispatcher
 * @see FilterDispatcher
 * @see ActionContextCleanUp
 *
 * @version $Date$ $Id$
 */
public abstract class AbstractFilter implements Filter {

    private static final Log LOG = LogFactory.getLog(AbstractFilter.class);

    /** Internal copy of dispatcher, created when Filter instance gets initialized. */
    private Dispatcher _dispatcher;

    protected FilterConfig filterConfig;

    /** Dispatcher instance to be used by subclass. */
    protected Dispatcher dispatcher;


    /**
     * Initializes the filter
     *
     * @param filterConfig The filter configuration
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        _dispatcher = createDispatcher();
        postInit(filterConfig);
    }

    /**
     * Cleans up the dispatcher
     *
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        if (_dispatcher == null) {
            LOG.warn("something is seriously wrong, Dispatcher is not initialized (null) ");
        } else {
            _dispatcher.cleanup();
        }
    }

    /**
     * Hook for subclass todo custom initialization, called after
     * <code>javax.servlet.Filter.init(FilterConfig)</code>.
     *
     * @param filterConfig
     * @throws ServletException
     */
    protected abstract void postInit(FilterConfig filterConfig) throws ServletException;

    /**
     * Create a {@link Dispatcher}, this serves as a hook for subclass to overried
     * such that a custom {@link Dispatcher} could be created.
     *
     * @return Dispatcher
     */
    protected Dispatcher createDispatcher() {
        return new Dispatcher(filterConfig.getServletContext());
    }

    /**
     * Servlet 2.3 specifies that the servlet context can be retrieved from the session. Unfortunately, some versions of
     * WebLogic can only retrieve the servlet context from the filter config. Hence, this method enables subclasses to
     * retrieve the servlet context from other sources.
     *
     * @param session the HTTP session where, in Servlet 2.3, the servlet context can be retrieved
     * @return the servlet context.
     */
    protected ServletContext getServletContext() {
        return filterConfig.getServletContext();
    }

    /**
     * Gets this filter's configuration
     *
     * @return The filter config
     */
    protected FilterConfig getFilterConfig() {
        return filterConfig;
    }

    /**
     * Helper method that prepare <code>Dispatcher</code>
     * (by calling <code>Dispatcher.prepare(HttpServletRequest, HttpServletResponse)</code>)
     * following by wrapping and returning  the wrapping <code>HttpServletRequest</code> [ through
     * <code>dispatcher.wrapRequest(HttpServletRequest, ServletContext)</code> ]
     *
     * @param request
     * @param response
     * @return HttpServletRequest
     * @throws ServletException
     */
    protected HttpServletRequest prepareDispatcherAndWrapRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        Dispatcher du = Dispatcher.getInstance();

        // Prepare and wrap the request if the cleanup filter hasn't already, cleanup filter should be
        // configured first before struts2 dispatcher filter, hence when its cleanup filter's turn,
        // static instance of Dispatcher should be null.
        if (du == null) {
            dispatcher = _dispatcher;

            Dispatcher.setInstance(dispatcher);

            // prepare the request no matter what - this ensures that the proper character encoding
            // is used before invoking the mapper (see WW-9127)
            dispatcher.prepare(request, response);

            try {
                // Wrap request first, just in case it is multipart/form-data
                // parameters might not be accessible through before encoding (ww-1278)
                request = dispatcher.wrapRequest(request, getServletContext());
            } catch (IOException e) {
                String message = "Could not wrap servlet request with MultipartRequestWrapper!";
                LOG.error(message, e);
                throw new ServletException(message, e);
            }
        }
        else {
            dispatcher = du;
        }
        return request;
    }
}
