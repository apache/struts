/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.TestAction;
import com.opensymphony.webwork.views.jsp.AbstractUITagTest;


/**
 * @author Matt Ho <a href="mailto:matt@enginegreen.com">&lt;matt@enginegreen.com&gt;</a>
 * @version $Id: PasswordTest.java,v 1.13 2006/02/03 13:14:43 rgielen Exp $
 */
public class PasswordTest extends AbstractUITagTest {

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        PasswordTag tag = new PasswordTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setTitle("mytitle");

        tag.doStartTag();
        tag.doEndTag();

        verify(PasswordTag.class.getResource("Password-1.txt"));
    }

    public void testGenericSimple() throws Exception {
        PasswordTag tag = new PasswordTag();
        verifyGenericProperties(tag, "simple", new String[]{"value"});
    }

    public void testGenericXhtml() throws Exception {
        PasswordTag tag = new PasswordTag();
        verifyGenericProperties(tag, "xhtml", new String[]{"value"});
    }

    public void testGenericAjax() throws Exception {
        PasswordTag tag = new PasswordTag();
        verifyGenericProperties(tag, "ajax", new String[]{"value"});
    }

}
