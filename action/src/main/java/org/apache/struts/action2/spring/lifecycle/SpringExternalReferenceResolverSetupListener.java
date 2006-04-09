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

import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.ExternalReferenceResolver;
import com.opensymphony.xwork.config.entities.PackageConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Iterator;
import java.util.Map;

/**
 * Setup any {@link com.opensymphony.xwork.config.ExternalReferenceResolver}s
 * that implement the ApplicationContextAware interface from the Spring
 * framework. Relies on Spring's
 * {@link org.springframework.web.context.ContextLoaderListener}having been
 * called first.
 * 
 * @author Simon Stewart
 */
public class SpringExternalReferenceResolverSetupListener implements
        ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        ApplicationContext appContext = WebApplicationContextUtils
                .getWebApplicationContext(event.getServletContext());

        Configuration xworkConfig = ConfigurationManager.getConfiguration();
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

    public void contextDestroyed(ServletContextEvent event) {
        // Nothing to do
    }

}
