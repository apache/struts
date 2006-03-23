/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.webwork.spring.lifecycle;

import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.context.ApplicationContextException;

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import java.io.Serializable;

/**
 * Used to set up a Spring {@link org.springframework.context.ApplicationContext}
 * in the HttpSession. It can be configured similarly to the
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class ApplicationContextSessionListener implements HttpSessionListener, Serializable {

    /**
	 * Config param for the root WebApplicationContext implementation class to
	 * use: "sessionContextClass"
	 */
	public static final String SESSION_CONTEXT_CLASS_PARAM = "sessionContextClass";

	/**
	 * Default context class for ContextLoader.
	 * @see org.springframework.web.context.support.XmlWebApplicationContext
	 */
	public static final Class DEFAULT_SESSION_CONTEXT_CLASS = XmlWebApplicationContext.class;

	/**
	 * Name of servlet context parameter that can specify the config location
	 * for the root context, falling back to the implementation's default
	 * otherwise.
	 * @see org.springframework.web.context.support.XmlWebApplicationContext#DEFAULT_CONFIG_LOCATION
	 */
	public static final String SESSION_CONFIG_LOCATION_PARAM = "sessionContextConfigLocation";

    /**
     * Key to map the session-scoped application context into the session attributes.
     */
    public static final String APP_CONTEXT_SESSION_KEY = "com.opensymphony.webwork.spring.ApplicationContextSessionListener_APP_CONTEXT";

    /**
     * The default session context configuration string:"classpath:session.xml"
     */
    public static final String[] DEFAULT_SESSION_CONFIG = {"classpath:session.xml"};

    /**
     * The application context instance created when the session is created, to be cleaned up when the session is destroyed
     */
    private ConfigurableWebApplicationContext sessionContext;

    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        final HttpSession session = httpSessionEvent.getSession();
        final ServletContext servletContext = session.getServletContext();
        final WebApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

        String contextClassName = servletContext.getInitParameter(SESSION_CONTEXT_CLASS_PARAM);
		Class contextClass = DEFAULT_SESSION_CONTEXT_CLASS;
		if (contextClassName != null) {
			try {
				contextClass = Class.forName(contextClassName, true, Thread.currentThread().getContextClassLoader());
			}
			catch (ClassNotFoundException ex) {
				throw new ApplicationContextException("Failed to load context class [" + contextClassName + "]", ex);
			}
			if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
				throw new ApplicationContextException("Custom context class [" + contextClassName +
						"] is not of type ConfigurableWebApplicationContext");
			}
		}

        sessionContext = (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);
        sessionContext.setParent(appContext);
        sessionContext.setServletContext(servletContext);
        String configLocation = servletContext.getInitParameter(SESSION_CONFIG_LOCATION_PARAM);
        if (configLocation != null) {
            sessionContext.setConfigLocations(StringUtils.tokenizeToStringArray(configLocation,
                    ConfigurableWebApplicationContext.CONFIG_LOCATION_DELIMITERS));
        } else {
            sessionContext.setConfigLocations(DEFAULT_SESSION_CONFIG);
        }

        sessionContext.refresh();
        session.setAttribute(APP_CONTEXT_SESSION_KEY, sessionContext);
    }

    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        sessionContext.close();
        this.sessionContext = null;
    }
}
