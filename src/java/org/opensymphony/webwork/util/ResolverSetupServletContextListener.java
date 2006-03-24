/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.util;

import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.entities.PackageConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Iterator;


/**
 * A Servlet Context Listener that will loop through all Reference Resolvers available in
 * the xwork Configuration and set the ServletContext on those that are ServletContextAware.
 * The Servlet Context can be used by the External Reference Resolver to initialise it's state. i.e. the
 * Spring framework uses a ContextServletListener to initialise it's IoC container, storing it's
 * container context (ApplicationContext in Spring terms) in the Servlet context, the External
 * Reference Resolver can get a reference to the container context from the servlet context.
 *
 * @author Ross
 */
public class ResolverSetupServletContextListener implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent event) {
    }

    public void contextInitialized(ServletContextEvent event) {
        Configuration config = ConfigurationManager.getConfiguration();
        String key;
        PackageConfig packageConfig;

        for (Iterator iter = config.getPackageConfigNames().iterator();
             iter.hasNext();) {
            key = (String) iter.next();
            packageConfig = config.getPackageConfig(key);

            if (packageConfig.getExternalRefResolver() instanceof ServletContextAware) {
                ((ServletContextAware) packageConfig.getExternalRefResolver()).setServletContext(event.getServletContext());
            }
        }
    }
}
