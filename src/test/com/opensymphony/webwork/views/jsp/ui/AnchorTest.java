/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.TestAction;
import com.opensymphony.webwork.views.jsp.AbstractUITagTest;


/**
 * @author Ian Roughley<a href="mailto:ian@fdar.com">&lt;ian@fdar.com&gt;</a>
 * @version $Id: AnchorTest.java,v 1.1 2005/12/21 00:24:22 plightbo Exp $
 */
public class AnchorTest extends AbstractUITagTest {


    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        AnchorTag tag = new AnchorTag();
        tag.setPageContext(pageContext);

        tag.setId("mylink");
        tag.setTheme("ajax");
        tag.setHref("a");
        tag.setErrorText("c");
        tag.setShowErrorTransportText("true");
        tag.setNotifyTopics("g");
        tag.setAfterLoading("h");

        tag.doStartTag();
        tag.doEndTag();

        verify(AnchorTest.class.getResource("href-1.txt"));
    }

}
