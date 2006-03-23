/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.webwork;

/**
 * <code>RequestUtilsTest</code>
 *
 * @author Rainer Hermanns
 * @version $Id: RequestUtilsTest.java,v 1.3 2006/03/08 18:09:28 rainerh Exp $
 */
import junit.framework.TestCase;

import javax.servlet.http.HttpServletRequest;

import org.easymock.MockControl;

public class RequestUtilsTest extends TestCase {

    private MockControl control;
    private HttpServletRequest requestMock;

    public void testGetServletPathWithServletPathSet() throws Exception {
        control.expectAndReturn(requestMock.getServletPath(), "/mycontext/");
        control.replay();
        assertEquals("/mycontext/", RequestUtils.getServletPath(requestMock));
        control.verify();
    }

    public void testGetServletPathWithRequestURIAndEmptyContextPath() throws Exception {
        control.expectAndReturn(requestMock.getServletPath(), null);
        control.expectAndReturn(requestMock.getRequestURI(), "/mycontext/test.jsp");
        control.expectAndReturn(requestMock.getContextPath(), "");
        control.expectAndReturn(requestMock.getPathInfo(), "test.jsp");
        control.expectAndReturn(requestMock.getPathInfo(), "test.jsp");
        control.replay();
        assertEquals("/mycontext/", RequestUtils.getServletPath(requestMock));
        control.verify();
    }

    public void testGetServletPathWithRequestURIAndContextPathSet() throws Exception {
        control.expectAndReturn(requestMock.getServletPath(), null);
        control.expectAndReturn(requestMock.getRequestURI(), "/servlet/mycontext/test.jsp");
        control.expectAndReturn(requestMock.getContextPath(), "/servlet");
        control.expectAndReturn(requestMock.getContextPath(), "/servlet");
        control.expectAndReturn(requestMock.getPathInfo(), "test.jsp");
        control.expectAndReturn(requestMock.getPathInfo(), "test.jsp");
        control.replay();
        assertEquals("/mycontext/", RequestUtils.getServletPath(requestMock));
        control.verify();
    }

    public void testGetServletPathWithRequestURIAndContextPathSetButNoPatchInfo() throws Exception {
        control.expectAndReturn(requestMock.getServletPath(), null);
        control.expectAndReturn(requestMock.getRequestURI(), "/servlet/mycontext/");
        control.expectAndReturn(requestMock.getContextPath(), "/servlet");
        control.expectAndReturn(requestMock.getContextPath(), "/servlet");
        control.expectAndReturn(requestMock.getPathInfo(), null);
        control.replay();
        assertEquals("/mycontext/", RequestUtils.getServletPath(requestMock));
        control.verify();
    }

    protected void setUp() {
        control = MockControl.createControl(HttpServletRequest.class);
        requestMock = (HttpServletRequest) control.getMock();
    }

}
