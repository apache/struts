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
package org.apache.struts2.plexus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.PlexusContainer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;

/**
 */
public class PlexusFilter implements Filter {
    private static final Log log = LogFactory.getLog(PlexusObjectFactory.class);
    private static final String CHILD_CONTAINER_NAME = "request";

    public static boolean loaded = false;

    private ServletContext ctx;

    public void init(FilterConfig filterConfig) throws ServletException {
        ctx = filterConfig.getServletContext();
        loaded = true;
    }

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
                    log.warn("Plexus container (scope: request) alredy exist.");
                    child = parent.getChildContainer(CHILD_CONTAINER_NAME);
                } else {
                    child = parent.createChildContainer(CHILD_CONTAINER_NAME, Collections.EMPTY_LIST, Collections.EMPTY_MAP);
                    PlexusUtils.configure(child, "plexus-request.xml");
                    child.initialize();
                    child.start();
                }
                PlexusThreadLocal.setPlexusContainer(child);
            } catch (Exception e) {
                log.error("Error initializing plexus container (scope: request)", e);
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
                log.error("Error disposing plexus container (scope: request)", e);
            }
        }
    }

    public void destroy() {
    }
}
