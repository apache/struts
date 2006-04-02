/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp;

import javax.servlet.jsp.JspException;


/**
 * @author $Author: plightbo $
 * @version $Revision: 1.8 $
 */
public class SetTagTest extends AbstractUITagTest {

    Chewbacca chewie;
    SetTag tag;


    public void testApplicationScope() throws JspException {
        tag.setName("foo");
        tag.setValue("name");
        tag.setScope("application");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("chewie", servletContext.getAttribute("foo"));
    }

    public void testPageScope() throws JspException {
        tag.setName("foo");
        tag.setValue("name");
        tag.setScope("page");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("chewie", pageContext.getAttribute("foo"));
    }

    public void testRequestScope() throws JspException {
        tag.setName("foo");
        tag.setValue("name");
        tag.setScope("request");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("chewie", request.getAttribute("foo"));
    }

    public void testSessionScope() throws JspException {
        tag.setName("foo");
        tag.setValue("name");
        tag.setScope("session");
        tag.doStartTag();
        tag.doEndTag();

        assertEquals("chewie", session.get("foo"));
    }

    public void testStrutsScope() throws JspException {
        tag.setName("foo");
        tag.setValue("name");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals("chewie", context.get("foo"));
    }

    public void testStrutsScope2() throws JspException {
        tag.setName("chewie");
        tag.doStartTag();
        tag.doEndTag();
        assertEquals(chewie, context.get("chewie"));
    }

    protected void setUp() throws Exception {
        super.setUp();

        tag = new SetTag();
        chewie = new Chewbacca("chewie", true);
        stack.push(chewie);
        tag.setPageContext(pageContext);
    }


    public class Chewbacca {
        String name;
        boolean furry;

        public Chewbacca(String name, boolean furry) {
            this.name = name;
            this.furry = furry;
        }

        public void setFurry(boolean furry) {
            this.furry = furry;
        }

        public boolean isFurry() {
            return furry;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
