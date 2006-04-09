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
package org.apache.struts.action2.lifecycle;

import com.opensymphony.xwork.interceptor.component.ComponentConfiguration;
import com.opensymphony.xwork.interceptor.component.ComponentManager;
import com.opensymphony.xwork.interceptor.component.DefaultComponentManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;


/**
 * A servlet context listener to handle the lifecycle of an application-based XWork component manager.
 *
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Patrick Lightbody
 * @author Bill Lynch (docs)
 * @deprecated XWork IoC has been deprecated in favor of Spring.
 *             Please refer to the Spring-Struts integration documentation for more info.
 */
public class ApplicationLifecycleListener implements ServletContextListener {

    private static final Log log = LogFactory.getLog(ApplicationLifecycleListener.class);


    /**
     * Destroys the XWork component manager because the server is shutting down.
     *
     * @param event the servlet context event.
     */
    public void contextDestroyed(ServletContextEvent event) {
        ServletContext application = event.getServletContext();
        ComponentManager container = (ComponentManager) application.getAttribute(ComponentManager.COMPONENT_MANAGER_KEY);

        if (container != null) {
            container.dispose();
        }

        // do some cleanup

        // If the JavaBeans Introspector has been used to analyze application classes,
        // the Introspector cache will hold a hard reference to those classes.
        // Consequently, those classes and the web app class loader will not be
        // garbage collected on web app shutdown!
        Introspector.flushCaches(); // WW-758

        LogFactory.releaseAll();
    }

    /**
     * Initializes the XWork compontent manager. Loads component config from the  <tt>components.xml</tt> file
     * in the classpath. Adds the component manager and compontent config as attributes of the servlet context.
     *
     * @param event the servlet context event.
     */
    public void contextInitialized(ServletContextEvent event) {
        ServletContext application = event.getServletContext();
        ComponentManager container = createComponentManager();
        ComponentConfiguration config = loadConfiguration();

        if (config != null) {
            config.configure(container, "application");
            application.setAttribute(ComponentManager.COMPONENT_MANAGER_KEY, container);
            application.setAttribute("ComponentConfiguration", config);
        }
    }

    /**
     * Returns a new <tt>DefaultComponentManager</tt> instance. This method is useful for developers
     * wishing to subclass this class and provide a different implementation of <tt>DefaultComponentManager</tt>.
     *
     * @return a new <tt>DefaultComponentManager</tt> instance.
     */
    protected DefaultComponentManager createComponentManager() {
        return new DefaultComponentManager();
    }

    private ComponentConfiguration loadConfiguration() {
        ComponentConfiguration config = new ComponentConfiguration();
        InputStream configXml = Thread.currentThread().getContextClassLoader().getResourceAsStream("components.xml");

        if (configXml == null) {
            final String message = "Unable to find the file components.xml in the classpath, XWork IoC *not* initialized.";
            log.warn(message);
            return null;
        }

        try {
            config.loadFromXml(configXml);
        } catch (IOException ioe) {
            log.error(ioe);
            throw new RuntimeException("Unable to load component configuration");
        } catch (SAXException sae) {
            log.error(sae);
            throw new RuntimeException("Unable to load component configuration");
        }

        return config;
    }
}
