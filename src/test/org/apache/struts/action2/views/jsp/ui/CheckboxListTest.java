/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.TestAction;
import org.apache.struts.action2.views.jsp.AbstractUITagTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


/**
 * Test case for CheckboxList.
 *
 * @author $author$
 * @version $Date: 2006/02/03 12:59:13 $ $Id: CheckboxListTest.java,v 1.14 2006/02/03 12:59:13 rgielen Exp $
 */
public class CheckboxListTest extends AbstractUITagTest {

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
        new PropertyHolder("value", "hello").addToMap(result);
        return result;
    }

    public void testGenericSimple() throws Exception {
        CheckboxListTag tag = new CheckboxListTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "simple", new String[]{"tabindex","cssClass","cssStyle","id"});
    }

    public void testGenericXhtml() throws Exception {
        CheckboxListTag tag = new CheckboxListTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "xhtml", new String[]{"tabindex","cssClass","cssStyle","id"});
    }

    public void testGenericAjax() throws Exception {
        CheckboxListTag tag = new CheckboxListTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "ajax", new String[]{"tabindex","cssClass","cssStyle","id"});
    }

    private void prepareTagGeneric(CheckboxListTag tag) {
        TestAction testAction = (TestAction) action;
        Collection collection = new ArrayList(2);
        collection.add("hello");
        collection.add("foo");
        testAction.setCollection(collection);
        testAction.setList(new String[][]{
                {"hello", "world"},
                {"foo", "bar"},
        });
        tag.setName("collection");
        tag.setList("list");
        tag.setListKey("top[0]");
        tag.setListValue("top[1]");
    }

    public void testMultiple() throws Exception {
        TestAction testAction = (TestAction) action;
        Collection collection = new ArrayList(2);
        collection.add("hello");
        collection.add("foo");
        testAction.setCollection(collection);
        testAction.setList(new String[][]{
                {"hello", "world"},
                {"foo", "bar"},
                {"cat", "dog"}
        });

        CheckboxListTag tag = new CheckboxListTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("collection");
        tag.setList("list");
        tag.setListKey("top[0]");
        tag.setListValue("top[1]");

        tag.doStartTag();
        tag.doEndTag();

        verify(CheckboxListTag.class.getResource("CheckboxList-2.txt"));
    }

    public void testMultipleWithDisabledOn() throws Exception {
        TestAction testAction = (TestAction) action;
        Collection collection = new ArrayList(2);
        collection.add("hello");
        collection.add("foo");
        testAction.setCollection(collection);
        testAction.setList(new String[][]{
                {"hello", "world"},
                {"foo", "bar"},
                {"cat", "dog"}
        });

        CheckboxListTag tag = new CheckboxListTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("collection");
        tag.setList("list");
        tag.setListKey("top[0]");
        tag.setListValue("top[1]");
        tag.setDisabled("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(CheckboxListTag.class.getResource("CheckboxList-3.txt"));
    }

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("hello");
        testAction.setList(new String[][]{
                {"hello", "world"},
                {"foo", "bar"}
        });

        CheckboxListTag tag = new CheckboxListTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setList("list");
        tag.setListKey("top[0]");
        tag.setListValue("top[1]");
        tag.setOnchange("alert('foo');");
        tag.setTitle("mytitle");

        tag.doStartTag();
        tag.doEndTag();

        verify(CheckboxListTag.class.getResource("CheckboxList-1.txt"));
    }

    public void testSimpleWithDisableOn() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("hello");
        testAction.setList(new String[][]{
                {"hello", "world"},
                {"foo", "bar"}
        });

        CheckboxListTag tag = new CheckboxListTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setList("list");
        tag.setListKey("top[0]");
        tag.setListValue("top[1]");
        tag.setOnchange("alert('foo');");
        tag.setDisabled("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(CheckboxListTag.class.getResource("CheckboxList-4.txt"));
    }
}
