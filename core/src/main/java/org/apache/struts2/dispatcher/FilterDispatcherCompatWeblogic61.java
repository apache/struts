/*
 * $Id$
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

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.struts2.config.ServletContextSingleton;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


/**
 * When running Weblogic Server 6.1, this class should be
 * specified in web.xml instead of {@link FilterDispatcher}.
 * <p/>
 * This class properly handles the weblogic.jar handling
 * of servlet filters.  There is one serious incompatibility, and
 * that is that while {@link FilterDispatcher#init(FilterConfig)}
 * throws a {@link ServletException}, this class's method
 * {@link #setFilterConfig(FilterConfig)} does not throw
 * the exception.  Since {@link #setFilterConfig(FilterConfig)}
 * invokes {@link FilterDispatcher#init(FilterConfig)}, the setter
 * must "swallow" the exception.  This it does by logging the
 * exception as an error.
 *
 * @deprecated Since Struts 2.1.3 as it probably isn't used anymore
 *
 */
public class FilterDispatcherCompatWeblogic61 extends FilterDispatcher {

    private static Logger LOG = LoggerFactory.getLogger(FilterDispatcherCompatWeblogic61.class);

    /**
     * dummy setter for {@link #filterConfig}; this method
     * sets up the {@link org.apache.struts2.config.ServletContextSingleton} with
     * the servlet context from the filter configuration.
     * <p/>
     * This is needed by Weblogic Server 6.1 because it
     * uses a slightly obsolete Servlet 2.3-minus spec
     * whose {@link Filter} interface requires this method.
     * <p/>
     *
     * @param filterConfig the filter configuration.
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        try {
            init(filterConfig);
        } catch (ServletException se) {
            LOG.error("Couldn't set the filter configuration in this filter", se);
        }

        ServletContextSingleton singleton = ServletContextSingleton.getInstance();
        singleton.setServletContext(filterConfig.getServletContext());
    }

    /**
     * answers the servlet context.
     * <p/>
     * Servlet 2.3 specifies that this can be retrieved from
     * the session.  Unfortunately, weblogic.jar can only retrieve
     * the servlet context from the filter config.  Hence, this
     * returns the servlet context from the singleton that was
     * setup by {@link #setFilterConfig(FilterConfig)}.
     *
     * @param session the HTTP session.  Not used
     * @return the servlet context.
     */
    protected ServletContext getServletContext(HttpSession session) {
        ServletContextSingleton singleton =
                ServletContextSingleton.getInstance();
        return singleton.getServletContext();
    }

    /**
     * This method is required by Weblogic 6.1 SP4 because
     * they defined this as a required method just before
     * the Servlet 2.3 specification was finalized.
     *
     * @return the filter's filter configuration
     */
    public FilterConfig getFilterConfig() {
        return super.getFilterConfig();
    }
}
