package org.apache.struts2.osgi;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.struts2.StrutsException;
import org.apache.struts2.osgi.host.OsgiHost;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * ServletContextListener that starts Osgi host
 */
public class StrutsOsgiListener implements ServletContextListener {

    public static final String OSGI_HOST = "__struts_osgi_host";
    public static final String PLATFORM_KEY = "struts.osgi.host";

    private static final Logger LOG = LogManager.getLogger(StrutsOsgiListener.class);

    private OsgiHost osgiHost;

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        String platform = servletContext.getInitParameter(PLATFORM_KEY);
        LOG.debug("Defined OSGi platform as [{}] via context-param [{}]", platform, PLATFORM_KEY);

        osgiHost = OsgiHostFactory.createOsgiHost(platform);
        servletContext.setAttribute(OSGI_HOST, osgiHost);
        try {
            osgiHost.init(servletContext);
        } catch (Exception e) {
            throw new StrutsException("Cannot init OSGi platform!", e);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
            osgiHost.destroy();
        } catch (Exception e) {
            throw new StrutsException("Cannot stop OSGi platform!", e);
        }
    }

}