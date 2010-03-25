package ${packageName};

import org.apache.pluto.core.PortletServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;

public class JettyPlutoLauncher {
 public static void main(String[] args) throws Exception {
     System.setProperty("org.apache.pluto.embedded.portletIds", "HelloPortlet");
     Server server = new Server(8080);
     WebAppContext webapp = new WebAppContext("src/main/webapp", "/${artifactId}");
     webapp.setDefaultsDescriptor("/WEB-INF/jetty-pluto-web-default.xml");
     ServletHolder portletServlet = new ServletHolder(new PortletServlet());
     portletServlet.setInitParameter("portlet-name", "HelloPortlet");
     portletServlet.setInitOrder(1);
     webapp.addServlet(portletServlet, "/PlutoInvoker/HelloPortlet");
     server.addHandler(webapp);
     server.start();
 }
}
