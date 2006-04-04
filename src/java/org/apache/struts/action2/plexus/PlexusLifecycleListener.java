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
package org.apache.struts.action2.plexus;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Collections;

public class PlexusLifecycleListener implements ServletContextListener, HttpSessionListener {
    private static final Log log = LogFactory.getLog(PlexusObjectFactory.class);

    public static boolean loaded = false;
    public static final String KEY = "struts.plexus.container";

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
            log.error("Error initializing plexus container (scope: application)", e);
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            ServletContext ctx = servletContextEvent.getServletContext();
            PlexusContainer pc = (PlexusContainer) ctx.getAttribute(KEY);
            pc.dispose();
        } catch (Exception e) {
            log.error("Error disposing plexus container (scope: application)", e);
        }
    }

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
            log.error("Error initializing plexus container (scope: session)", e);
        }
    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        try {
            HttpSession session = httpSessionEvent.getSession();
            PlexusContainer child = (PlexusContainer) session.getAttribute(KEY);
            child.dispose();
        } catch (Exception e) {
            log.error("Error initializing plexus container (scope: session)", e);
        }
    }
}
