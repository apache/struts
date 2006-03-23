/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.spring;

import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.webwork.util.ObjectFactoryInitializable;
import com.opensymphony.webwork.WebWorkConstants;
import com.opensymphony.xwork.spring.SpringObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * WebWork object factory that integrates with Spring.
 * <p/>
 * Spring should be loaded using a web context listener
 * <code>org.springframework.web.context.ContextLoaderListener</code> defined in <code>web.xml</code>.
 *
 * @author plightbo
 */
public class WebWorkSpringObjectFactory extends SpringObjectFactory implements ObjectFactoryInitializable {
    private static final Log log = LogFactory.getLog(WebWorkSpringObjectFactory.class);

    public void init(ServletContext servletContext) {
        log.info("Initializing WebWork-Spring integration...");

        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        if (appContext == null) {
            // uh oh! looks like the lifecycle listener wasn't installed. Let's inform the user
            String message = "********** FATAL ERROR STARTING UP SPRING-WEBWORK INTEGRATION **********\n" +
                    "Looks like the Spring listener was not configured for your web app! \n" +
                    "Nothing will work until WebApplicationContextUtils returns a valid ApplicationContext.\n" +
                    "You might need to add the following to web.xml: \n" +
                    "    <listener>\n" +
                    "        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>\n" +
                    "    </listener>";
            log.fatal(message);
            return;
        }
        
        this.setApplicationContext(appContext);

        String autoWire = Configuration.getString(WebWorkConstants.WEBWORK_OBJECTFACTORY_SPRING_AUTOWIRE);
        int type = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;   // default
        if ("name".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
        } else if ("type".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;
        } else if ("auto".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;
        } else if ("constructor".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;
        }
        this.setAutowireStrategy(type);
        
        boolean useClassCache = "true".equals(Configuration.getString(WebWorkConstants.WEBWORK_OBJECTFACTORY_SPRING_USE_CLASS_CACHE));
        this.setUseClassCache(useClassCache);

        log.info("... initialized WebWork-Spring integration successfully");
    }
}
