/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp.ui;

import org.apache.struts.webwork.TestAction;
import org.apache.struts.webwork.views.jsp.AbstractUITagTest;

import java.util.Map;


/**
 * User: plightbo
 * Date: Oct 16, 2003
 * Time: 10:43:24 PM
 */
public class FileTest extends AbstractUITagTest {

    public FileTest() {
    }


    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        FileTag tag = new FileTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setAccept("*.txt");
        tag.setValue("%{foo}");
        tag.setSize("10");
        tag.setTitle("mytitle");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("File-1.txt"));
    }

    /**
     * Initialize a map of {@link org.apache.struts.webwork.views.jsp.AbstractUITagTest.PropertyHolder} for generic tag
     * property testing. Will be used when calling {@link #verifyGenericProperties(org.apache.struts.webwork.views.jsp.ui.AbstractUITag,
     * String, String[])} as properties to verify.<p/> This implementation extends testdata from AbstractUITag.
     *
     * @return A Map of PropertyHolders values bound to {@link org.apache.struts.webwork.views.jsp.AbstractUITagTest.PropertyHolder#getName()}
     *         as key.
     */
    protected Map initializedGenericTagTestProperties() {
        Map result = super.initializedGenericTagTestProperties();
        new PropertyHolder("accept", "someAccepted").addToMap(result);
        new PropertyHolder("size", "101").addToMap(result);
        return result;
    }

    public void testGenericSimple() throws Exception {
        FileTag tag = new FileTag();
        verifyGenericProperties(tag, "simple", null);
    }

    public void testGenericXhtml() throws Exception {
        FileTag tag = new FileTag();
        verifyGenericProperties(tag, "xhtml", null);
    }

    public void testGenericAjax() throws Exception {
        FileTag tag = new FileTag();
        verifyGenericProperties(tag, "ajax", null);
    }

}
