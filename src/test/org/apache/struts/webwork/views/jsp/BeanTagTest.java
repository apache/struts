/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp;

import javax.servlet.jsp.JspException;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.12 $
 */
public class BeanTagTest extends AbstractUITagTest {

    public void testSimple() {
        BeanTag tag = new BeanTag();
        tag.setPageContext(pageContext);
        tag.setName("org.apache.struts.webwork.TestAction");

        try {
            tag.doStartTag();
            tag.component.addParameter("result", "success");

            assertEquals("success", stack.findValue("result"));
            // TestAction from bean tag, Action from execution and DefaultTextProvider 
            assertEquals(3, stack.size());
            tag.doEndTag();
            assertEquals(2, stack.size());
        } catch (JspException ex) {
            ex.printStackTrace();
            fail();
        }

        request.verify();
        pageContext.verify();
    }
}
