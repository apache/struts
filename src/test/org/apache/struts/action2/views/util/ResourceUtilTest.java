/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package org.apache.struts.action2.views.util;
/**
 * <code>ResourceUtilTest</code>
 *
 * @author Rainer Hermanns
 * @version $Id$
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
