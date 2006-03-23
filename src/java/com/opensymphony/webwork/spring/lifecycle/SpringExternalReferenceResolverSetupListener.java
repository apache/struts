/*
 * Created on Jun 14, 2004
 */
package com.opensymphony.webwork.spring.lifecycle;

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
