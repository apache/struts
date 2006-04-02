package org.apache.struts.webwork.dispatcher;

import org.apache.struts.webwork.config.ServletContextSingleton;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;


/**
 * When running Weblogic Server 6.1, this class should be
 * specified in web.xml instead of {@link FilterDispatcher}.
 * <p/>
 * This class properly handles the weblogic.jar handling
 * of servlet filters.  There is one serious incompatibility, and
 * that is that while {@link FilterDispatcher#init(FilterConfig)}
 * throws a {@link ServletException}, this class's method
 * {@link #setFilterConfig(FilterConfig)} does not throw
 * the exception.  Since {@link #setFilterConfig(FilterConfig)}
 * invokes {@link FilterDispatcher#init(FilterConfig)}, the setter
 * must "swallow" the exception.  This it does by logging the
 * exception as an error.
 *
 * @author Scott N. Smith scottnelsonsmith@yahoo.com
 * @version $Id: FilterDispatcherCompatWeblogic61.java,v 1.3 2006/03/08 20:28:56 rainerh Exp $
 */
public class FilterDispatcherCompatWeblogic61
        extends FilterDispatcher
        implements Filter {
    /**
     * the standard logger
     */
    private static Log log =
            LogFactory.getLog(FilterDispatcherCompatWeblogic61.class);

    /**
     * dummy setter for {@link #filterConfig}; this method
     * sets up the {@link org.apache.struts.webwork.config.ServletContextSingleton} with
     * the servlet context from the filter configuration.
     * <p/>
     * This is needed by Weblogic Server 6.1 because it
     * uses a slightly obsolete Servlet 2.3-minus spec
     * whose {@link Filter} interface requires this method.
     * <p/>
     *
     * @param filterConfig the filter configuration.
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        try {
            init(filterConfig);
        } catch (ServletException se) {
            log.error("Couldn't set the filter configuration in this filter", se);
        }

        ServletContextSingleton singleton = ServletContextSingleton.getInstance();
        singleton.setServletContext(filterConfig.getServletContext());
    }

    /**
     * answers the servlet context.
     * <p/>
     * Servlet 2.3 specifies that this can be retrieved from
     * the session.  Unfortunately, weblogic.jar can only retrieve
     * the servlet context from the filter config.  Hence, this
     * returns the servlet context from the singleton that was
     * setup by {@link #setFilterConfig(FilterConfig)}.
     *
     * @param session the HTTP session.  Not used
     * @return the servlet context.
     */
    protected ServletContext getServletContext(HttpSession session) {
        ServletContextSingleton singleton =
                ServletContextSingleton.getInstance();
        return singleton.getServletContext();
    }

    /**
     * This method is required by Weblogic 6.1 SP4 because
     * they defined this as a required method just before
     * the Servlet 2.3 specification was finalized.
     *
     * @return the filter's filter configuration
     */
    public FilterConfig getFilterConfig() {
        return super.getFilterConfig();
    }
}
