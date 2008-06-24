package org.apache.struts2.portlet.servlet;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.portlet.PortletActionConstants;
import org.apache.struts2.portlet.context.PortletActionContext;
import org.springframework.mock.web.portlet.MockPortletContext;
import org.springframework.mock.web.portlet.MockPortletRequest;

import com.opensymphony.xwork2.ActionContext;

public class PortletServletRequestTest extends StrutsTestCase {
	
	private MockPortletRequest portletRequest;
	private MockPortletContext portletContext;
	private PortletServletRequest request;
	
	protected void setUp() throws Exception {
		super.setUp();
		portletRequest = new MockPortletRequest();
		portletContext = new MockPortletContext();
		request = new PortletServletRequest(portletRequest, portletContext);
	}
	
	public void testGetServletPathShouldHandleDefaultActionExtension() throws Exception {
		portletRequest.setParameter(PortletActionConstants.ACTION_PARAM, "actionName");
		request.setExtension("action");
		assertEquals("actionName.action", request.getServletPath());
	}
	
	public void testGetServletPathShouldHandleCustomActionExtension() throws Exception {
		portletRequest.setParameter(PortletActionConstants.ACTION_PARAM, "actionName");
		request.setExtension("custom");
		assertEquals("actionName.custom", request.getServletPath());
	}
	
	public void testGetServletPathShouldHandleNoExtension() throws Exception {
		portletRequest.setParameter(PortletActionConstants.ACTION_PARAM, "actionName");
		request.setExtension("");
		assertEquals("actionName", request.getServletPath());
	}
	
	public void testGetServletPathShouldHandleMultipleExtensionsByUsingTheFirst() throws Exception {
		portletRequest.setParameter(PortletActionConstants.ACTION_PARAM, "actionName");
		request.setExtension("action,,");
		assertEquals("actionName.action", request.getServletPath());
	}
}
