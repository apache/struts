/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.portlet.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.MockControl;

import org.apache.struts.action2.ServletActionContext;

import junit.framework.TestCase;

/**
 * 
 * Test for the {@link PreparatorServletTest}
 * 
 * @author Nils-Helge Garli
 *
 */
public class PreparatorServletTest extends TestCase {

    /**
     * Test that the service method stores the request, response and servlet context
     * in the {@link com.opensymphony.xwork.ActionContext}
     */
    public void testServiceHttpServletRequestHttpServletResponse() throws Exception {
        MockControl mockRequest = MockControl.createNiceControl(HttpServletRequest.class);
        MockControl mockResponse = MockControl.createNiceControl(HttpServletResponse.class);
        MockControl mockContext = MockControl.createNiceControl(ServletContext.class);
        MockControl mockConfig = MockControl.createNiceControl(ServletConfig.class);
        
        HttpServletRequest req = (HttpServletRequest)mockRequest.getMock();
        HttpServletResponse res = (HttpServletResponse)mockResponse.getMock();
        ServletContext context = (ServletContext)mockContext.getMock();
        ServletConfig config = (ServletConfig)mockConfig.getMock();
        
        mockConfig.expectAndDefaultReturn(config.getServletContext(), context);
        mockConfig.replay();
        
        PreparatorServlet servlet = new PreparatorServlet();
        servlet.init(config);
        servlet.service(req, res);
        assertSame(req, ServletActionContext.getRequest());
        assertSame(res, ServletActionContext.getResponse());
        assertSame(context, ServletActionContext.getServletContext());
        
        mockConfig.verify();
    }

}
