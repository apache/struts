/*
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
package org.apache.struts2.osgi;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.struts2.StrutsException;
import org.apache.struts2.osgi.host.OsgiHost;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * ServletContextListener that starts OSGi host
 */
public class StrutsOsgiListener implements ServletContextListener {

    public static final String OSGI_HOST = "__struts_osgi_host";
    public static final String PLATFORM_KEY = "struts.osgi.host";

    private static final Logger LOG = LogManager.getLogger(StrutsOsgiListener.class);

    private OsgiHost osgiHost;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOG.trace("StrutsOsgiListener attempting to start. ContextInitialized called.  SCE: [{}]", sce);

        if (sce == null) {
            throw new StrutsException("ServletContextEvent is null.  Cannot init OSGi platform!");  // Better than a NPE.
        }
        ServletContext servletContext = sce.getServletContext();

        LOG.trace("StrutsOsgiListener attempting to start. ServletContext: [{}]", servletContext);

        if (servletContext == null) {
            throw new StrutsException("ServletContext is null within the ServletContextEvent.  Cannot init OSGi platform!");  // Better than a NPE.
        }

        String platform = servletContext.getInitParameter(PLATFORM_KEY);
        LOG.debug("Defined OSGi platform as [{}] via context-param [{}]", platform, PLATFORM_KEY);

        try {
            osgiHost = OsgiHostFactory.createOsgiHost(platform);

            LOG.trace("StrutsOsgiListener attempting to start. OSGi Host constructed: [{}]", osgiHost);
        } catch (Throwable t) {
            throw new StrutsException("StrutsOsgiListener failed to create an OSGi Host!", t);
        }

        servletContext.setAttribute(OSGI_HOST, osgiHost);
        try {
            osgiHost.init(servletContext);
        } catch (Exception e) {
            throw new StrutsException("StrutsOsgiListener failed to initialize the OSGi Host platform!", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            osgiHost.destroy();
        } catch (Exception e) {
            throw new StrutsException("StrutsOsgiListener cannot stop the OSGi Host platform!", e);
        }
    }

}