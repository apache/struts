/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.TestAction;
import org.apache.struts.action2.views.jsp.AbstractUITagTest;

import java.util.Map;


/**
 * @author Matt Ho <a href="mailto:matt@enginegreen.com">&lt;matt@enginegreen.com&gt;</a>
 * @version $Id$
 */
public class CheckboxTest extends AbstractUITagTest {

    public CheckboxTest() {
    }

    /**
     * Initialize a map of {@link org.apache.struts.action2.views.jsp.AbstractUITagTest.PropertyHolder} for generic tag
     * property testing. Will be used when calling {@link #verifyGenericProperties(org.apache.struts.action2.views.jsp.ui.AbstractUITag,
     * String, String[])} as properties to verify.<p/> This implementation extends testdata from AbstractUITag.
     *
     * @return A Map of PropertyHolders values bound to {@link org.apache.struts.action2.views.jsp.AbstractUITagTest.PropertyHolder#getName()}
     *         as key.
     */
    protected Map initializedGenericTagTestProperties() {
        Map result = super.initializedGenericTagTestProperties();
        new PropertyHolder("value", "true").addToMap(result);
        return result;
    }

    public void testGenericSimple() throws Exception {
        CheckboxTag tag = new CheckboxTag();
        verifyGenericProperties(tag, "simple", null);
    }

    public void testGenericXhtml() throws Exception {
        CheckboxTag tag = new CheckboxTag();
        verifyGenericProperties(tag, "xhtml", null);
    }

    public void testGenericAjax() throws Exception {
        CheckboxTag tag = new CheckboxTag();
        verifyGenericProperties(tag, "ajax", null);
    }

    public void testChecked() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("true");

        CheckboxTag tag = new CheckboxTag();
        tag.setPageContext(pageContext);
        tag.setId("someId");
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setFieldValue("baz");
        tag.setOnfocus("test();");
        tag.setTitle("mytitle");

        tag.doStartTag();
        tag.doEndTag();

        verify(CheckboxTag.class.getResource("Checkbox-1.txt"));
    }

    public void testCheckedWithTopLabelPosition() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("true");

        CheckboxTag tag = new CheckboxTag();
        tag.setPageContext(pageContext);
        tag.setId("someId");
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setFieldValue("baz");
        tag.setOnfocus("test();");
        tag.setTitle("mytitle");
        tag.setLabelPosition("top");

        tag.doStartTag();
        tag.doEndTag();

        verify(CheckboxTag.class.getResource("Checkbox-4.txt"));
    }

    public void testCheckedWithLeftLabelPosition() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("true");

        CheckboxTag tag = new CheckboxTag();
        tag.setPageContext(pageContext);
        tag.setId("someId");
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setFieldValue("baz");
        tag.setOnfocus("test();");
        tag.setTitle("mytitle");
        tag.setLabelPosition("left");

        tag.doStartTag();
        tag.doEndTag();

        verify(CheckboxTag.class.getResource("Checkbox-5.txt"));
    }

    public void testCheckedWithError() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("true");
        testAction.addFieldError("foo", "Some Foo Error");
        testAction.addFieldError("foo", "Another Foo Error");

        CheckboxTag tag = new CheckboxTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setFieldValue("baz");
        tag.setOndblclick("test();");
        tag.setOnclick("test();");
        tag.setTitle("mytitle");

        tag.doStartTag();
        tag.doEndTag();

        verify(CheckboxTag.class.getResource("Checkbox-3.txt"));
    }

    public void testUnchecked() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("false");

        CheckboxTag tag = new CheckboxTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setFieldValue("baz");
        tag.setTitle("mytitle");

        tag.doStartTag();
        tag.doEndTag();

        verify(CheckboxTag.class.getResource("Checkbox-2.txt"));
    }
}
