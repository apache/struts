/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.webwork.spring;

import com.opensymphony.webwork.spring.lifecycle.ApplicationContextSessionListener;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.spring.SpringProxyableObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * SessionContextSpringProxyableObjectFactory
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class SessionContextSpringProxyableObjectFactory extends SpringProxyableObjectFactory {
    private static final Log log = LogFactory.getLog(SessionContextSpringProxyableObjectFactory.class);

    protected ApplicationContext getApplicationContext(Map context) {
        if (log.isDebugEnabled()) {
            log.debug("Getting the session-scoped app context");
        }
        if (context == null) {
            return appContext;
        }
        Map session = (Map) context.get(ActionContext.SESSION);
        if (session == null) {
            log.warn("There is no session map in the ActionContext.");
            return appContext;
        }
        ApplicationContext sessionContext = (ApplicationContext) session.get(ApplicationContextSessionListener.APP_CONTEXT_SESSION_KEY);
        if (sessionContext == null) {
            throw new IllegalStateException("There is no application context in the user's session.");
        }
        return sessionContext;
    }

    public Object buildBean(String beanName, Map extraContext) throws Exception {
        Object bean = super.buildBean(beanName, extraContext);
        AutowireCapableBeanFactory autoWiringBeanFactory = findAutoWiringBeanFactory(getApplicationContext(extraContext));
        return autoWireBean(bean, autoWiringBeanFactory);
    }

}
