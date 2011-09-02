package org.apache.struts2.osgi;

import org.apache.struts2.StrutsException;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

/**
 * ServletContextListener that starts Apache Felix
 */
public class StrutsOsgiListener implements ServletContextListener {
    public static final String OSGI_HOST = "__struts_osgi_host";
    private FelixOsgiHost osgiHost;

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        osgiHost = new FelixOsgiHost();
        servletContext.setAttribute(OSGI_HOST, osgiHost);
        try {
            osgiHost.init(servletContext);
        } catch (Exception e) {
            throw new StrutsException("Apache Felix failed to start", e);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
            osgiHost.destroy();
        } catch (Exception e) {
            throw new StrutsException("Apache Felix failed to stop", e);
        }
    }
}
