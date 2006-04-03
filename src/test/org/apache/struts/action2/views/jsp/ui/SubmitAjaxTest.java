/**
 * Copyright:	Copyright (c) From Down & Around, Inc.
 */

package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.views.jsp.AbstractUITagTest;

/**
 * @author Ian Roughley
 * @version $Id: SubmitAjaxTest.java,v 1.5 2006/03/08 18:09:24 rainerh Exp $
 */
public class SubmitAjaxTest extends AbstractUITagTest {


    public void testSimple() throws Exception {
        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);

        tag.setId("mylink");
        tag.setValue("submit");
        tag.setTheme("ajax");
        tag.setResultDivId("formId");
        tag.setOnLoadJS("alert('form submitted');");
        tag.setListenTopics("a");
        tag.setNotifyTopics("b");

        tag.doStartTag();
        tag.doEndTag();

        verify(AnchorTest.class.getResource("submit-ajax-1.txt"));
    }

}
