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
package org.apache.struts.action2.spring.lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * ApplicationContextListener that sets up the environment so that XWork and
 * Struts can load data and information from Spring. Relies on Spring's
 * {@link org.springframework.web.context.ContextLoaderListener}having been
 * called first.
 *
 * @author sms
 * @deprecated Please configure struts.property to use {@link org.apache.struts.action2.spring.StrutsSpringObjectFactory}
 */
public class SpringObjectFactoryListener implements ServletContextListener {
    private static final Log log = LogFactory.getLog(SpringObjectFactoryListener.class);

    public void contextInitialized(ServletContextEvent event) {
        log.fatal("SpringObjectFactoryListener is deprecated and no longer does anything - you should remove it from web.xml\n" +
                "Please set struts.objectFactory = spring to enable Spring-Struts integration.");
    }

    public void contextDestroyed(ServletContextEvent arg0) {
        // Nothing to do.
    }
}
