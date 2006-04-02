/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp;

import org.apache.struts.webwork.views.jsp.ui.AnchorTag;
import org.apache.struts.webwork.views.jsp.ui.StrutsBodyContent;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.StringWriter;


/**
 *
 * @author Mike Porter
 * @version $Revision: 1.1 $
 */
public class AnchorTagTest extends AbstractUITagTest {
    private StringWriter writer = new StringWriter();
    private AnchorTag tag;

    public void testActionURL() {
        tag.setHref("TestAction.action");
        try {
            tag.doStartTag();
            tag.doEndTag();
            assertTrue( writer.toString().indexOf("href=\"TestAction.action\"") > -1);
        } catch (JspException ex) {
            ex.printStackTrace();
            fail();
        }
    }

    public void testAddParameters() {
        tag.setHref("/TestAction.action");
        String bodyText = "<img src=\"#\"/>";
        try {
            StrutsBodyContent bodyContent = new StrutsBodyContent(null);
            bodyContent.print(bodyText);
            tag.setBodyContent(bodyContent);

            tag.doStartTag();
            tag.doEndTag();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }


    protected void setUp() throws Exception {
        super.setUp();

        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(80);

        tag = new AnchorTag();
        tag.setPageContext(pageContext);
        JspWriter jspWriter = new StrutsMockJspWriter(writer);
        pageContext.setJspWriter(jspWriter);
    }

}
