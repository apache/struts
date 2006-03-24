/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.lifecycle;

import com.opensymphony.xwork.interceptor.component.ComponentConfiguration;
import com.opensymphony.xwork.interceptor.component.ComponentManager;
import com.opensymphony.xwork.interceptor.component.DefaultComponentManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.*;
import java.io.Serializable;


/**
 * A filter to handle the lifecycle of an HTTP session-based XWork component manager.
 *
 * @author <a href="mailto:joew@thoughtworks.com">Joe Walnes</a>
 * @author Cameron Braid
 * @author Bill Lynch (docs)
 * @deprecated XWork IoC has been deprecated in favor of Spring.
 *             Please refer to the Spring-WebWork integration documentation for more info.
 */
public class SessionLifecycleListener implements HttpSessionListener, Serializable {

    private static final Log log = LogFactory.getLog(SessionLifecycleListener.class);


    /**
     * Initializes an XWork component manager for the lifetime of the user's session.
     *
     * @param event an HttpSessionEvent object.
     */
    public void sessionCreated(HttpSessionEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("Session DefaultComponentManager : init");
        }

        HttpSession session = event.getSession();
        ComponentManager container = createComponentManager();
        ServletContext application = getServletContext(session);
        ComponentManager fallback = (ComponentManager) application.getAttribute(ComponentManager.COMPONENT_MANAGER_KEY);
        if (fallback != null) {
            container.setFallback(fallback);
        }

        ComponentConfiguration config = (ComponentConfiguration) application.getAttribute("ComponentConfiguration");
        if (config != null) {
            config.configure(container, "session");
            session.setAttribute(ComponentManager.COMPONENT_MANAGER_KEY, container);
        }
    }

    /**
     * Does nothing - when the session is destroyed the component manager reference will go away as well.
     *
     * @param event an HttpSessionEvent object.
     */
    public void sessionDestroyed(HttpSessionEvent event) {
    }

    /**
     * Servlet 2.3 specifies that the servlet context can be retrieved from the session. Unfortunately, some
     * versions of WebLogic can only retrieve the servlet context from the filter config. Hence, this method
     * enables subclasses to retrieve the servlet context from other sources.
     *
     * @param session the HTTP session where, in Servlet 2.3, the servlet context can be retrieved
     * @return the servlet context.
     */
    protected ServletContext getServletContext(HttpSession session) {
        return session.getServletContext();
    }

    /**
     * Returns a new <tt>DefaultComponentManager</tt> instance. This method is useful for developers
     * wishing to subclass this class and provide a different implementation of <tt>DefaultComponentManager</tt>.
     *
     * @return a new <tt>DefaultComponentManager</tt> instance.
     */
    protected DefaultComponentManager createComponentManager() {
        return new SessionComponentManager();
    }

    /**
     * @deprecated XWork IoC has been deprecated in favor of Spring.
     *             Please refer to the Spring-WebWork integration documentation for more info.
     */
    class SessionComponentManager extends DefaultComponentManager implements HttpSessionBindingListener {
        public void valueBound(HttpSessionBindingEvent event) {
        }

        public void valueUnbound(HttpSessionBindingEvent event) {
            if (log.isDebugEnabled()) {
                log.debug("Session DefaultComponentManager : destroy");
            }

            this.dispose();
        }
    }
}
