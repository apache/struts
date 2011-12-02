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

package org.apache.struts2.plexus;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codehaus.plexus.PlexusContainer;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * Creates a plexus container for the application, session, and request
 */
public class PlexusFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(PlexusObjectFactory.class);
    private static final String CHILD_CONTAINER_NAME = "request";

    private static boolean loaded = false;

    private ServletContext ctx;

    /**
     * @return Returns if the container is loaded.
     */
    public static boolean isLoaded() {
        return loaded;
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        ctx = filterConfig.getServletContext();
        loaded = true;
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        PlexusContainer child = null;
        try {
            try {
                HttpServletRequest request = (HttpServletRequest) req;
                HttpSession session = request.getSession(false);
                PlexusContainer parent;
                if (session != null) {
                    parent = (PlexusContainer) session.getAttribute(PlexusLifecycleListener.KEY);
                } else {
                    parent = (PlexusContainer) ctx.getAttribute(PlexusLifecycleListener.KEY);
                }

                if (parent.hasChildContainer(CHILD_CONTAINER_NAME)) {
                    if (LOG.isWarnEnabled()) {
                	LOG.warn("Plexus container (scope: request) alredy exist.");
                    }
                    child = parent.getChildContainer(CHILD_CONTAINER_NAME);
                } else {
                    child = parent.createChildContainer(CHILD_CONTAINER_NAME, Collections.EMPTY_LIST, Collections.EMPTY_MAP);
                    PlexusUtils.configure(child, "plexus-request.xml");
                    child.initialize();
                    child.start();
                }
                PlexusThreadLocal.setPlexusContainer(child);
            } catch (Exception e) {
                LOG.error("Error initializing plexus container (scope: request)", e);
            }

            chain.doFilter(req, res);
        }
        finally {
            try {
                if (child != null) {
                    child.dispose();
                }
                PlexusThreadLocal.setPlexusContainer(null);
            } catch (Exception e) {
                LOG.error("Error disposing plexus container (scope: request)", e);
            }
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
    }
}
