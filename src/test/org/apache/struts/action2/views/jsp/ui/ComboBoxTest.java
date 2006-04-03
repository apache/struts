/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.TestAction;
import org.apache.struts.action2.views.jsp.AbstractUITagTest;

import java.util.ArrayList;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ComboBoxTest extends AbstractUITagTest {

    public void testGenericSimple() throws Exception {
        ComboBoxTag tag = new ComboBoxTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "simple", null);
    }

    public void testGenericXhtml() throws Exception {
        ComboBoxTag tag = new ComboBoxTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "xhtml", null);
    }

    public void testGenericAjax() throws Exception {
        ComboBoxTag tag = new ComboBoxTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "ajax", null);
    }

    private void prepareTagGeneric(ComboBoxTag tag) {
        TestAction testAction = (TestAction) action;
        ArrayList collection = new ArrayList();
        collection.add("foo");
        collection.add("bar");
        collection.add("baz");

        testAction.setCollection(collection);

        tag.setList("collection");
    }

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("hello");

        ArrayList collection = new ArrayList();
        collection.add("foo");
        collection.add("bar");
        collection.add("baz");
        testAction.setCollection(collection);

        ComboBoxTag tag = new ComboBoxTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setList("collection");

        tag.doStartTag();
        tag.doEndTag();

        verify(ComboBoxTag.class.getResource("ComboBox-1.txt"));
    }
}
