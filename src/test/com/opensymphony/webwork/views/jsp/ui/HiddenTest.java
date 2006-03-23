/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.TestAction;
import com.opensymphony.webwork.views.jsp.AbstractUITagTest;

import java.util.Map;
import java.util.HashMap;


/**
 * User: plightbo
 * Date: Oct 16, 2003
 * Time: 10:49:54 PM
 */
public class HiddenTest extends AbstractUITagTest {

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        HiddenTag tag = new HiddenTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Hidden-1.txt"));
    }

    /**
     * Initialize a map of {@link com.opensymphony.webwork.views.jsp.AbstractUITagTest.PropertyHolder} for generic tag
     * property testing. Will be used when calling {@link #verifyGenericProperties(com.opensymphony.webwork.views.jsp.ui.AbstractUITag,
     * String, String[])} as properties to verify.<p/> This implementation extends testdata from AbstractUITag.
     *
     * @return A Map of PropertyHolders values bound to {@link com.opensymphony.webwork.views.jsp.AbstractUITagTest.PropertyHolder#getName()}
     *         as key.
     */
    protected Map initializedGenericTagTestProperties() {
        Map result = new HashMap();
        new PropertyHolder("name", "someName").addToMap(result);
        new PropertyHolder("value", "someValue").addToMap(result);
        new PropertyHolder("cssClass", "cssClass1", "class=\"cssClass1\"").addToMap(result);
        new PropertyHolder("cssStyle", "cssStyle1", "style=\"cssStyle1\"").addToMap(result);
        new PropertyHolder("id", "someId").addToMap(result);
        return result;
    }

    public void testGenericSimple() throws Exception {
        HiddenTag tag = new HiddenTag();
        verifyGenericProperties(tag, "simple", null);
    }

    public void testGenericXhtml() throws Exception {
        HiddenTag tag = new HiddenTag();
        verifyGenericProperties(tag, "xhtml", null);
    }

    public void testGenericAjax() throws Exception {
        HiddenTag tag = new HiddenTag();
        verifyGenericProperties(tag, "ajax", null);
    }

}
