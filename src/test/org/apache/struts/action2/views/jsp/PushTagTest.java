/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp;

import javax.servlet.jsp.JspException;


/**
 * @author $Author$
 * @version $Revision$
 */
public class PushTagTest extends AbstractUITagTest {

    public void testSimple() {
        PushTag tag = new PushTag();

        stack.setValue("foo", "bar");

        tag.setPageContext(pageContext);
        tag.setValue("foo");

        try {
            assertEquals(2, stack.size());
            tag.doStartTag();
            assertEquals(3, stack.size());
            tag.doEndTag();
            assertEquals(2, stack.size());
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }
    }
}
