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

import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * Manages the Plexus lifecycle for the servlet and session contexts
 */
public class PlexusLifecycleListener implements ServletContextListener, HttpSessionListener {
    private static final Logger LOG = LoggerFactory.getLogger(PlexusObjectFactory.class);

    private static boolean loaded = false;
    public static final String KEY = "struts.plexus.container";

    /**
     * @return Returns if the container is loaded.
     */
    public static boolean isLoaded() {
        return loaded;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        loaded = true;

        try {
            PlexusContainer pc = new DefaultPlexusContainer();
            PlexusUtils.configure(pc, "plexus-application.xml");
            ServletContext ctx = servletContextEvent.getServletContext();
            ctx.setAttribute(KEY, pc);

            pc.initialize();
            pc.start();
        } catch (Exception e) {
            LOG.error("Error initializing plexus container (scope: application)", e);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            ServletContext ctx = servletContextEvent.getServletContext();
            PlexusContainer pc = (PlexusContainer) ctx.getAttribute(KEY);
            pc.dispose();
        } catch (Exception e) {
            LOG.error("Error disposing plexus container (scope: application)", e);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        try {
            HttpSession session = httpSessionEvent.getSession();
            ServletContext ctx = session.getServletContext();
            PlexusContainer parent = (PlexusContainer) ctx.getAttribute(KEY);
            PlexusContainer child = parent.createChildContainer("session", Collections.EMPTY_LIST, Collections.EMPTY_MAP);
            session.setAttribute(KEY, child);
            PlexusUtils.configure(child, "plexus-session.xml");
            child.initialize();
            child.start();
        } catch (Exception e) {
            LOG.error("Error initializing plexus container (scope: session)", e);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        try {
            HttpSession session = httpSessionEvent.getSession();
            PlexusContainer child = (PlexusContainer) session.getAttribute(KEY);
            child.dispose();
        } catch (Exception e) {
            LOG.error("Error initializing plexus container (scope: session)", e);
        }
    }
}
