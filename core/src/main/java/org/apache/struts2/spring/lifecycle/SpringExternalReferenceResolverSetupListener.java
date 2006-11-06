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
package org.apache.struts2.spring.lifecycle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.DispatcherListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ExternalReferenceResolver;
import com.opensymphony.xwork2.config.entities.PackageConfig;

/**
 * Setup any {@link com.opensymphony.xwork2.config.ExternalReferenceResolver}s
 * that implement the ApplicationContextAware interface from the Spring
 * framework. Relies on Spring's
 * {@link org.springframework.web.context.ContextLoaderListener}having been
 * called first.
 */
public class SpringExternalReferenceResolverSetupListener implements
        ServletContextListener {

    private Map<ServletContext,Listener> listeners = new HashMap<ServletContext,Listener>();

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public synchronized void contextDestroyed(ServletContextEvent event) {
        Listener l = listeners.get(event.getServletContext());
        Dispatcher.removeDispatcherListener(l);
        listeners.remove(event.getServletContext());
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public synchronized void contextInitialized(ServletContextEvent event) {
        Listener l = new Listener(event.getServletContext());
        Dispatcher.addDispatcherListener(l);
        listeners.put(event.getServletContext(), l);
    }

    /**
     * Handles initializing and cleaning up the dispatcher
     * @author brownd
     *
     */
    private class Listener implements DispatcherListener {

        private ServletContext servletContext;

        /**
         * Constructs the listener
         *
         * @param ctx The servlet context
         */
        public Listener(ServletContext ctx) {
            this.servletContext = ctx;
        }

        /* (non-Javadoc)
         * @see org.apache.struts2.dispatcher.DispatcherListener#dispatcherInitialized(org.apache.struts2.dispatcher.Dispatcher)
         */
        public void dispatcherInitialized(Dispatcher du) {
            ApplicationContext appContext = WebApplicationContextUtils
            .getWebApplicationContext(servletContext);

            Configuration xworkConfig = du.getConfigurationManager().getConfiguration();
            Map packageConfigs = xworkConfig.getPackageConfigs();
            Iterator i = packageConfigs.values().iterator();

            while (i.hasNext()) {
                PackageConfig packageConfig = (PackageConfig) i.next();
                ExternalReferenceResolver resolver = packageConfig.getExternalRefResolver();
                if (resolver == null || !(resolver instanceof ApplicationContextAware))
                    continue;
                ApplicationContextAware contextAware = (ApplicationContextAware) resolver;
                contextAware.setApplicationContext(appContext);
            }

        }

        /* (non-Javadoc)
         * @see org.apache.struts2.dispatcher.DispatcherListener#dispatcherDestroyed(org.apache.struts2.dispatcher.Dispatcher)
         */
        public void dispatcherDestroyed(Dispatcher du) {
        }
    }
}
