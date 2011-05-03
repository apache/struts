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

package org.apache.struts2.portlet.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * @deprecated
 * 
 * This listener has been deprecated. Do not use it. (WW-2101)
 *
 */
public class ServletContextHolderListener implements ServletContextListener {

    private static ServletContext context = null;
    
    private final static Logger LOG = LoggerFactory.getLogger(ServletContextHolderListener.class);

    /**
     * @return The current servlet context
     */
    public static ServletContext getServletContext() {
        return context;
    }

    /**
     * Stores the reference to the {@link ServletContext}.
     *
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event) {
        if (LOG.isWarnEnabled()) {
    		LOG.warn("The ServletContextHolderListener has been deprecated. It can safely be removed from your web.xml file");
        }
        context = event.getServletContext();
    }

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event) {
        context = null;
    }

}
