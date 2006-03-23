/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.webwork.views.util;
/**
 * <code>ResourceUtilTest</code>
 *
 * @author Rainer Hermanns
 * @version $Id: ResourceUtilTest.java,v 1.2 2006/03/13 05:02:07 rainerh Exp $
 */
import junit.framework.TestCase;
import org.easymock.MockControl;

import javax.servlet.http.HttpServletRequest;

public class ResourceUtilTest extends TestCase {

    private MockControl control;
    private HttpServletRequest requestMock;

    public void testGetResourceBase() throws Exception {
        control.expectAndReturn(requestMock.getServletPath(), "/mycontext/");
        control.replay();
        assertEquals("/mycontext", ResourceUtil.getResourceBase(requestMock));
        control.verify();

        control.reset();

        control.expectAndReturn(requestMock.getServletPath(), "/mycontext/test.jsp");
        control.replay();
        assertEquals("/mycontext", ResourceUtil.getResourceBase(requestMock));
        control.verify();

    }


    protected void setUp() {
        control = MockControl.createControl(HttpServletRequest.class);
        requestMock = (HttpServletRequest) control.getMock();
    }
}
