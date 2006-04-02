/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.spring;

import org.apache.struts.webwork.config.Configuration;
import org.apache.struts.webwork.util.ObjectFactoryInitializable;
import org.apache.struts.webwork.StrutsConstants;
import com.opensymphony.xwork.spring.SpringObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * Struts object factory that integrates with Spring.
 * <p/>
 * Spring should be loaded using a web context listener
 * <code>org.springframework.web.context.ContextLoaderListener</code> defined in <code>web.xml</code>.
 *
 * @author plightbo
 */
public class StrutsSpringObjectFactory extends SpringObjectFactory implements ObjectFactoryInitializable {
    private static final Log log = LogFactory.getLog(StrutsSpringObjectFactory.class);

    public void init(ServletContext servletContext) {
        log.info("Initializing Struts-Spring integration...");

        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        if (appContext == null) {
            // uh oh! looks like the lifecycle listener wasn't installed. Let's inform the user
            String message = "********** FATAL ERROR STARTING UP SPRING-STRUTS INTEGRATION **********\n" +
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

        String autoWire = Configuration.getString(StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE);
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
        
        boolean useClassCache = "true".equals(Configuration.getString(StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_USE_CLASS_CACHE));
        this.setUseClassCache(useClassCache);

        log.info("... initialized Struts-Spring integration successfully");
    }
}
