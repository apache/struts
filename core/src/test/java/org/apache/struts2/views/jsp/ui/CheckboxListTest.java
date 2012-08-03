/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


/**
 * Test case for CheckboxList.
 *
 */
public class CheckboxListTest extends AbstractUITagTest {

    /**
     * Initialize a map of {@link org.apache.struts2.views.jsp.AbstractUITagTest.PropertyHolder} for generic tag
     * property testing. Will be used when calling {@link #verifyGenericProperties(org.apache.struts2.views.jsp.ui.AbstractUITag,
     * String, String[])} as properties to verify.<p/> This implementation extends testdata from AbstractUITag.
     *
     * @return A Map of PropertyHolders values bound to {@link org.apache.struts2.views.jsp.AbstractUITagTest.PropertyHolder#getName()}
     *         as key.
     */
    protected Map<String, PropertyHolder> initializedGenericTagTestProperties() {
        Map<String, PropertyHolder> result = super.initializedGenericTagTestProperties();
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

    private void prepareTagGeneric(CheckboxListTag tag) {
        TestAction testAction = (TestAction) action;
        Collection<String> collection = new ArrayList<String>(2);
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
        Collection<String> collection = new ArrayList<String>(2);
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
        Collection<String> collection = new ArrayList<String>(2);
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
                {"foo", "bar"},
                {"baz", null}
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
