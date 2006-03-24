package org.apache.struts.action2.lifecycle;

import org.apache.struts.action2.config.ServletContextSingleton;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * @author Scott N. Smith scottnelsonsmith@yahoo.com
 * @version $Id: SessionLifecycleListenerCompatWeblogic61.java,v 1.2 2005/09/21 03:04:53 plightbo Exp $
 * @deprecated XWork IoC has been deprecated in favor of Spring.
 *             Please refer to the Spring-WebWork integration documentation for more info.
 */
public class SessionLifecycleListenerCompatWeblogic61
        extends SessionLifecycleListener {
    /**
     * This is needed by Weblogic Server 6.1 because it
     * uses a slightly obsolete Servlet 2.3-minus spec.
     * In this obsolete spec, the servlet context is not
     * available from the web session object, and so
     * it is retrieved from a special singleton whose sole
     * purpose is to hold the servlet context for this listenter.
     *
     * @param session the HTTP session.  Here is it not used.
     * @return the servlet context
     * @see ServletContextSingleton
     * @see SessionLifecycleListener#getServletContext(javax.servlet.http.HttpSession)
     */
    protected ServletContext getServletContext(HttpSession session) {
        return ServletContextSingleton.getInstance().getServletContext();
    }
}
