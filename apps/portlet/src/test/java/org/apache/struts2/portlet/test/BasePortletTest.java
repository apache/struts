package org.apache.struts2.portlet.test;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import org.apache.pluto.core.PortletServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;

import junit.framework.TestCase;

import java.io.File;

public abstract class BasePortletTest extends TestCase {
	
	protected Server server;
	
	private String contextPath = "/test";
	
	private int port;
	
	public void setUp() throws Exception {
		System.setProperty("org.apache.pluto.embedded.portletIds", getPortletName());
		server = new Server(port);

		WebAppContext webapp = new WebAppContext("src/main/webapp", contextPath);
		webapp.setTempDirectory(new File("target/work"));
		webapp.setDefaultsDescriptor("/WEB-INF/jetty-pluto-web-default.xml");
		ServletHolder portletServlet = new ServletHolder(new PortletServlet());
		portletServlet.setInitParameter("portlet-name", getPortletName());
		portletServlet.setInitOrder(1);
		webapp.addServlet(portletServlet, "/PlutoInvoker/" + getPortletName());
		server.addHandler(webapp);
		server.start();
		// Retrieve the actual port that is used, in case a random, free port is
		// picked
		int port = server.getConnectors()[0].getLocalPort();
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
	
	public void setContextPath(String contextPath) {
		if(!contextPath.startsWith("/")) {
			this.contextPath = "/" + contextPath;
		}
		else {
			this.contextPath = contextPath;
		}
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public abstract String getPortletName();
}
