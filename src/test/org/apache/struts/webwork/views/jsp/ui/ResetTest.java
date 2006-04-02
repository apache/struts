/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp.ui;

import org.apache.struts.webwork.TestAction;
import org.apache.struts.webwork.views.jsp.AbstractUITagTest;

import java.util.HashMap;
import java.util.Map;

/**
 * Reset Component Test.
 *
 * @author Rene Gielen
 */
public class ResetTest extends AbstractUITagTest {

    public void testDefaultValues() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        ResetTag tag = new ResetTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setTitle("mytitle");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Reset-2.txt"));
    }

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        ResetTag tag = new ResetTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setAlign("left");
        tag.setName("myname");
        tag.setValue("%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Reset-1.txt"));
    }

    public void testButtonSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        ResetTag tag = new ResetTag();
        tag.setPageContext(pageContext);
        tag.setType("button");
        tag.setName("myname");
        tag.setValue("%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Reset-3.txt"));
    }

    public void testButtonWithLabel() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        ResetTag tag = new ResetTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setType("button");
        tag.setAlign("left");
        tag.setName("myname");
        tag.setValue("%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Reset-4.txt"));
    }

    /**
     * Initialize a map of {@link org.apache.struts.webwork.views.jsp.AbstractUITagTest.PropertyHolder} for generic tag
     * property testing. Will be used when calling {@link #verifyGenericProperties(AbstractUITag,
     * String, String[])} as properties to verify.<p/> This implementation extends testdata from AbstractUITag.
     *
     * @return A Map of PropertyHolders values bound to {@link org.apache.struts.webwork.views.jsp.AbstractUITagTest.PropertyHolder#getName()}
     *         as key.
     */
    protected Map initializedGenericTagTestProperties() {
        Map result = new HashMap();
        new PropertyHolder("title", "someTitle").addToMap(result);
        new PropertyHolder("cssClass", "cssClass1", "class=\"cssClass1\"").addToMap(result);
        new PropertyHolder("cssStyle", "cssStyle1", "style=\"cssStyle1\"").addToMap(result);
        new PropertyHolder("name", "someName").addToMap(result);
        new PropertyHolder("value", "someValue").addToMap(result);
        return result;
    }

    public void testGenericSimple() throws Exception {
        ResetTag tag = new ResetTag();
        verifyGenericProperties(tag, "simple", null);
    }

    public void testGenericXhtml() throws Exception {
        ResetTag tag = new ResetTag();
        verifyGenericProperties(tag, "xhtml", null);
    }

    public void testGenericAjax() throws Exception {
        ResetTag tag = new ResetTag();
        verifyGenericProperties(tag, "ajax", null);
    }

}
