/*
 *  Copyright (c) 2002-2006 by OpenSymphony
 *  All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.mockobjects.servlet.MockRequestDispatcher;
import com.opensymphony.webwork.components.Include;
import org.easymock.MockControl;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * Unit test of {@link IncludeTag}.
 *
 * @author Claus Ibsen
 */
public class IncludeTagTest extends AbstractTagTest {

    private MockControl controlRequestDispatcher;
    private RequestDispatcher mockRequestDispatcher;

    private IncludeTag tag;

    public void testNoURL() throws Exception {
        try {
            tag.doStartTag();
            tag.doEndTag();
            fail("Should have thrown exception as no URL is specified in setValue");
        } catch (RuntimeException e) {
            assertEquals("tag include, field value: You must specify the URL to include. Example: /foo.jsp", e.getMessage());
        }
    }

    public void testIncludeNoParam() throws Exception {
        mockRequestDispatcher.include(null, null);
        controlRequestDispatcher.setVoidCallable();
        controlRequestDispatcher.replay();

        tag.setValue("person/list.jsp");
        tag.doStartTag();
        tag.doEndTag();

        controlRequestDispatcher.verify();
        assertEquals("/person/list.jsp", request.getRequestDispatherString());
        assertEquals("", writer.toString());
    }

    public void testIncludeWithParameters() throws Exception {
        mockRequestDispatcher.include(null, null);
        controlRequestDispatcher.setVoidCallable();
        controlRequestDispatcher.replay();

        tag.setValue("person/create.jsp");
        tag.doStartTag();
        // adding param must be done after doStartTag()
        Include include = (Include) tag.getComponent();
        include.addParameter("user", "Santa Claus");
        tag.doEndTag();

        controlRequestDispatcher.verify();
        assertEquals("/person/create.jsp?user=Santa+Claus", request.getRequestDispatherString());
        assertEquals("", writer.toString());
    }

    public void testIncludeRelative2Dots() throws Exception {
        // TODO: we should test for .. in unit test - is this test correct?
        mockRequestDispatcher.include(null, null);
        controlRequestDispatcher.setVoidCallable();
        controlRequestDispatcher.replay();

        request.setupGetServletPath("app/manager");
        tag.setValue("../car/view.jsp");
        tag.doStartTag();
        tag.doEndTag();

        controlRequestDispatcher.verify();
        assertEquals("/car/view.jsp", request.getRequestDispatherString());
        assertEquals("", writer.toString());
    }

    protected void setUp() throws Exception {
        super.setUp();
        request.setupGetRequestDispatcher(new MockRequestDispatcher());
        tag = new IncludeTag();

        controlRequestDispatcher = MockControl.createNiceControl(RequestDispatcher.class);
        // use always matcher as we can not determine the excact objects used in mock.include(req, res) call
        controlRequestDispatcher.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
        mockRequestDispatcher = (RequestDispatcher) controlRequestDispatcher.getMock();

        request.setupGetRequestDispatcher(mockRequestDispatcher);
        tag.setPageContext(pageContext);
        tag.setPageContext(pageContext);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        tag = null;
        controlRequestDispatcher = null;
        mockRequestDispatcher = null;
    }

}
