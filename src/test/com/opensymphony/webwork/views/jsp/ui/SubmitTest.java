/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.TestAction;
import com.opensymphony.webwork.views.jsp.AbstractUITagTest;

import java.util.Map;
import java.util.HashMap;


/**
 * Unit test for {@link SubmitTag}.
 *
 * @author plightbo
 */
public class SubmitTest extends AbstractUITagTest {

    public void testDefaultValues() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setTitle("mytitle");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Submit-2.txt"));
    }

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setAlign("left");
        tag.setName("myname");
        tag.setValue("%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Submit-1.txt"));
    }

    public void testButtonSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);
        tag.setType("button");
        tag.setName("myname");
        tag.setValue("%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Submit-3.txt"));
    }

    public void testButtonWithLabel() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setType("button");
        tag.setAlign("left");
        tag.setName("myname");
        tag.setValue("%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Submit-4.txt"));
    }

    public void testImageSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);
        tag.setType("image");
        tag.setName("myname");
        tag.setValue("%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Submit-5.txt"));
    }

    public void testImageWithSrc() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);
        tag.setType("image");
        tag.setName("myname");
        tag.setLabel("mylabel");
        tag.setValue("%{foo}");
        tag.setSrc("some.gif");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Submit-6.txt"));
    }

    public void testSimpleThemeImageUsingActionAndMethod() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);
        tag.setTheme("simple");
        tag.setType("button");
        tag.setName("myname");
        tag.setLabel("mylabel");
        tag.setAction("manager");
        tag.setMethod("update");
        tag.setAlign("left");

        tag.doStartTag();
        tag.doEndTag();

        assertEquals("<button type=\"submit\" name=\"action:manager!update\" value=\"Submit\">mylabel</button>", writer.toString().trim());
    }

    public void testSimpleThemeImageUsingActionOnly() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);
        tag.setTheme("simple");
        tag.setType("button");
        tag.setName("myname");
        tag.setLabel("mylabel");
        tag.setAction("manager");
        tag.setMethod(null); // no method
        tag.setAlign("left");

        tag.doStartTag();
        tag.doEndTag();

        assertEquals("<button type=\"submit\" name=\"action:manager\" value=\"Submit\">mylabel</button>", writer.toString().trim());
    }

    public void testSimpleThemeImageUsingMethodOnly() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);
        tag.setTheme("simple");
        tag.setType("button");
        tag.setName("myname");
        tag.setLabel("mylabel");
        tag.setAction(null); // no action
        tag.setMethod("update");
        tag.setAlign("left");

        tag.doStartTag();
        tag.doEndTag();

        assertEquals("<button type=\"submit\" name=\"method:update\" value=\"Submit\">mylabel</button>", writer.toString().trim());
    }

    public void testSimpleThemeInput() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);
        tag.setTheme("simple");
        tag.setType("input");
        tag.setName("myname");
        tag.setLabel("mylabel");
        tag.setAction(null);
        tag.setMethod(null);

        tag.doStartTag();
        tag.doEndTag();

        assertEquals("<input type=\"submit\" name=\"myname\" value=\"Submit\"/>", writer.toString().trim());
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
        new PropertyHolder("title", "someTitle").addToMap(result);
        new PropertyHolder("cssClass", "cssClass1", "class=\"cssClass1\"").addToMap(result);
        new PropertyHolder("cssStyle", "cssStyle1", "style=\"cssStyle1\"").addToMap(result);
        new PropertyHolder("name", "someName").addToMap(result);
        new PropertyHolder("value", "someValue").addToMap(result);
        return result;
    }

    public void testGenericSimple() throws Exception {
        SubmitTag tag = new SubmitTag();
        verifyGenericProperties(tag, "simple", null);
    }

    public void testGenericXhtml() throws Exception {
        SubmitTag tag = new SubmitTag();
        verifyGenericProperties(tag, "xhtml", null);
    }

    public void testGenericAjax() throws Exception {
        SubmitTag tag = new SubmitTag();
        verifyGenericProperties(tag, "ajax", null);
    }

}
