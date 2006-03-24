/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.TestAction;
import org.apache.struts.action2.views.jsp.AbstractUITagTest;


/**
 * @author Ian Roughley<a href="mailto:ian@fdar.com">&lt;ian@fdar.com&gt;</a>
 * @version $Id: DivTest.java,v 1.3 2006/02/03 12:28:51 rgielen Exp $
 */
public class DivTest extends AbstractUITagTest {


    public void testGenericSimple() throws Exception {
        DivTag tag = new DivTag();
        verifyGenericProperties(tag, "simple", new String[]{"value","tabindex","disabled"});
    }

    public void testGenericXhtml() throws Exception {
        DivTag tag = new DivTag();
        verifyGenericProperties(tag, "xhtml", new String[]{"value","tabindex","disabled"});
    }

    public void testGenericAjax() throws Exception {
        DivTag tag = new DivTag();
        verifyGenericProperties(tag, "ajax", new String[]{"value","tabindex","disabled"});
    }

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        DivTag tag = new DivTag();
        tag.setPageContext(pageContext);

        tag.setId("mylabel");
        tag.setTheme("ajax");
        tag.setHref("a");
        tag.setLoadingText("b");
        tag.setErrorText("c");
        tag.setShowErrorTransportText("true");
        tag.setDelay("4000");
        tag.setUpdateFreq("1000");
        tag.setListenTopics("g");
        tag.setAfterLoading("h");

        tag.doStartTag();
        tag.doEndTag();

        verify(DivTest.class.getResource("div-1.txt"));
    }

}
