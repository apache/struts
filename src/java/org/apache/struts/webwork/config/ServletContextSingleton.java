package org.apache.struts.webwork.config;

import javax.servlet.ServletContext;

/**
 * This singleton holds an instance of the web servlet context.
 * <p/>
 * This is needed for running WebWork on Weblogic Server 6.1
 * because there is no provision to retrieve the servlet context
 * from the web session object.
 * <p/>
 * This class is created to bet that this singleton can be set by
 * {@link org.apache.struts.webwork.dispatcher.FilterDispatcherCompatWeblogic61}
 * before the servlet context is needed by
 * {@link org.apache.struts.webwork.lifecycle.SessionLifecycleListener}
 * which will use this object to get it.
 *
 * @author Scott N. Smith scottnelsonsmith@yahoo.com
 * @version $Id: ServletContextSingleton.java,v 1.2 2005/06/25 16:15:15 plightbo Exp $
 */
public class ServletContextSingleton {
    /**
     * The web servlet context.  Holding this is the
     * purpose of this singleton.
     */
    private ServletContext servletContext;

    /**
     * The sole instance of this class.
     */
    private static ServletContextSingleton singleton;

    /**
     * Constructor which cannot be called
     * publicly.
     */
    private ServletContextSingleton() {
    }

    /**
     * answers the singleton.
     * <p/>
     * At some point, the caller must populate the web servlet
     * context.
     *
     * @return answers the singleton instance of this class
     */
    public static ServletContextSingleton getInstance() {
        if (singleton == null) {
            singleton = new ServletContextSingleton();
        }
        return singleton;
    }

    /**
     * @return the web servlet context
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * @param context the web servlet context
     */
    public void setServletContext(ServletContext context) {
        servletContext = context;
    }

}
