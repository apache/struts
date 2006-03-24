/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.portlet.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Some of the factory/managers (e.g. the ObjectFactory) need access to 
 * the {@link com.opensymphony.webwork.ServletActionContext} object when initializing.
 * This {@link javax.servlet.ServletContextListener} keeps a reference to the 
 * {@link javax.servlet.ServletContext} and exposes it through a <code>public static</code>
 * method.
 * 
 * @author Nils-Helge Garli
 */
public class ServletContextHolderListener implements ServletContextListener {

    private static ServletContext context = null;

    public static ServletContext getServletContext() {
        return context;
    }

    /**
     * Stores the reference to the {@link ServletContext}.
     * 
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event) {
        context = event.getServletContext();
        
    }

    /**
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event) {

    }

}
