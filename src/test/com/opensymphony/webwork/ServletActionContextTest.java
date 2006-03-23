/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork;

import com.mockobjects.servlet.MockHttpServletRequest;
import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockServletContext;
import com.opensymphony.xwork.ActionContext;
import junit.framework.TestCase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * Unit test for ServletActionContext. Based loosly on Jason's ActionContextTest.
 * My first attempt at unit testing. Please hack away as needed.
 *
 * @author <a href="mailto:nightfal@etherlands.net">Erik Beeson</a>
 */
public class ServletActionContextTest extends TestCase implements WebWorkStatics {

    ActionContext actionContext;
    ServletActionContext servletActionContext;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private MockServletContext servletContext;


    public void setUp() {
        Map extraContext = new HashMap();

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        servletContext = new MockServletContext();

        extraContext.put(HTTP_REQUEST, request);
        extraContext.put(HTTP_RESPONSE, response);
        extraContext.put(SERVLET_CONTEXT, servletContext);

        actionContext = new ActionContext(extraContext);
        ServletActionContext.setContext(actionContext);
    }

    public void testContextParams() {
        assertEquals(ServletActionContext.getRequest(), request);
        assertEquals(ServletActionContext.getResponse(), response);
        assertEquals(ServletActionContext.getServletContext(), servletContext);
    }

    public void testGetContext() {
        ActionContext threadContext = ServletActionContext.getContext();
        assertEquals(actionContext, threadContext);
    }
}
