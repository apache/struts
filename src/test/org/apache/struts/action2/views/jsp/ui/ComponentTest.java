/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.TestAction;
import org.apache.struts.action2.views.jsp.AbstractUITagTest;


/**
 * @author Matt Ho <a href="mailto:matt@enginegreen.com">&lt;matt@enginegreen.com&gt;</a>
 * @version $Id: ComponentTest.java,v 1.17 2005/10/09 04:26:51 plightbo Exp $
 */
public class ComponentTest extends AbstractUITagTest {

    /**
     * Note -- this test uses empty.vm, so it's basically clear
     */
    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        ComponentTag tag = new ComponentTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("foo");

        tag.doStartTag();
        tag.doEndTag();

        verify(ComponentTag.class.getResource("Component-1.txt"));
    }

    /**
     * executes a component test passing in a custom parameter. it also executes calling a custom template using an
     * absolute reference.
     */
    public void testWithParam() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        ComponentTag tag = new ComponentTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("foo");
        tag.setTheme("test");
        tag.setTemplate("Component");

        tag.doStartTag();
        tag.getComponent().addParameter("hello", "world");
        tag.getComponent().addParameter("argle", "bargle");
        tag.getComponent().addParameter("glip", "glop");
        tag.getComponent().addParameter("array", new String[]{"a", "b", "c"});
        tag.getComponent().addParameter("obj", tag);
        tag.doEndTag();

        //        System.out.println(writer);
        verify(ComponentTag.class.getResource("Component-param.txt"));
    }
}
