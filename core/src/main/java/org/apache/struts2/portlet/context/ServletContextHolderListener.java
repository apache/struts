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
package org.apache.struts2.portlet.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Some of the factory/managers (e.g. the ObjectFactory) need access to 
 * the {@link org.apache.struts2.ServletActionContext} object when initializing.
 * This {@link javax.servlet.ServletContextListener} keeps a reference to the 
 * {@link javax.servlet.ServletContext} and exposes it through a <code>public static</code>
 * method.
 * 
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
