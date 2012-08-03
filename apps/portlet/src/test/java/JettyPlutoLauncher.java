import org.apache.pluto.core.PortletServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;

public class JettyPlutoLauncher {
	public static void main(String[] args) throws Exception {
	    System.setProperty("org.apache.pluto.embedded.portletId", "StrutsPortlet");
	    Server server = new Server(8080);
	    WebAppContext webapp = new WebAppContext("src/main/webapp", "/test");
	    webapp.setDefaultsDescriptor("/WEB-INF/jetty-pluto-web-default.xml");
	    ServletHolder portletServlet = new ServletHolder(new PortletServlet());
	    portletServlet.setInitParameter("portlet-name", "StrutsPortlet");
	    portletServlet.setInitOrder(1);
	    webapp.addServlet(portletServlet, "/PlutoInvoker/StrutsPortlet");
	    server.addHandler(webapp);
	    server.start();
	}
}
