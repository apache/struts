package org.apache.struts2.portlet.test;

import net.sourceforge.jwebunit.junit.WebTestCase;

import org.apache.pluto.core.PortletServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;

public abstract class BasePortletTest extends WebTestCase {
	
	protected Server server;

	private int port = 9876;
	
	private String contextPath = "/test";
	
	public void setUp() throws Exception {
		System.setProperty("org.apache.pluto.embedded.portletId", getPortletName());
		server = new Server(port);
		WebAppContext webapp = new WebAppContext("src/main/webapp", contextPath);
		webapp.setDefaultsDescriptor("/WEB-INF/jetty-pluto-web-default.xml");
		ServletHolder portletServlet = new ServletHolder(new PortletServlet());
		portletServlet.setInitParameter("portlet-name", getPortletName());
		portletServlet.setInitOrder(1);
		webapp.addServlet(portletServlet, "/PlutoInvoker/" + getPortletName());
		server.addHandler(webapp);
		server.start();
		getTestContext().setBaseUrl("http://localhost:" + port + contextPath);
	}
	

	public void tearDown() throws Exception {
		server.stop();
	}
	
	public void minimizeWindow() {
		clickElementByXPath("//span[@class='minimized']/..");
	}
	
	public void maximizeWindow() {
		clickElementByXPath("//span[@class='minimized']/..");
	}
	
	public void restoreWindow() {
		clickElementByXPath("//span[@class='normal']/..");
	}
	
	public void switchEdit() {
		clickElementByXPath("//span[@class='edit']/..");
	}
	
	public void switchView() {
		clickElementByXPath("//span[@class='view']/..");
	}
	
	public void switchHelp() {
		clickElementByXPath("//span[@class='help']/..");
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setContextPath(String contextPath) {
		if(!contextPath.startsWith("/")) {
			this.contextPath = "/" + contextPath;
		}
		else {
			this.contextPath = contextPath;
		}
	}
	
	public abstract String getPortletName();
}
