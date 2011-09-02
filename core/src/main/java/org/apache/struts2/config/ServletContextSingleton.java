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

package org.apache.struts2.config;

import javax.servlet.ServletContext;

/**
 * This singleton holds an instance of the web servlet context.
 * <p/>
 * This is needed for running Struts on Weblogic Server 6.1
 * because there is no provision to retrieve the servlet context
 * from the web session object.
 * <p/>
 * This class is created to bet that this singleton can be set by
 * {@link org.apache.struts2.dispatcher.FilterDispatcherCompatWeblogic61}
 * before the servlet context is needed by
 * {@link org.apache.struts2.lifecycle.SessionLifecycleListener}
 * which will use this object to get it.
 *
 */
public class ServletContextSingleton {
    /**
     * The web servlet context.  Holding this is the
     * purpose of this singleton.
     */
    private ServletContext servletContext;

    /**
     * The sole instance of this class.
     */
    private static ServletContextSingleton singleton;

    /**
     * Constructor which cannot be called
     * publicly.
     */
    private ServletContextSingleton() {
    }

    /**
     * Answers the singleton.
     * <p/>
     * At some point, the caller must populate the web servlet
     * context.
     *
     * @return Answers the singleton instance of this class
     */
    public static ServletContextSingleton getInstance() {
        if (singleton == null) {
            singleton = new ServletContextSingleton();
        }
        return singleton;
    }

    /**
     * Gets the servlet context
     *
     * @return The web servlet context
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * Sets the servlet context
     *
     * @param context The web servlet context
     */
    public void setServletContext(ServletContext context) {
        servletContext = context;
    }

}
