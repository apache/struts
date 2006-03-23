package com.opensymphony.webwork.plexus;

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
    public static final String KEY = "webwork.plexus.container";

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
