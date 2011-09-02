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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;


/**
 * Test case for ComboBox component.
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
        tag.setId("cb");
        tag.setList("collection");

        tag.doStartTag();
        tag.doEndTag();

        verify(ComboBoxTag.class.getResource("ComboBox-1.txt"));
    }

    public void testWithEmptyOptionAndHeader() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("banana");

        List l = new ArrayList();
        l.add("apple");
        l.add("banana");
        l.add("pineaple");
        l.add("grapes");
        testAction.setCollection(l);

        ComboBoxTag tag = new ComboBoxTag();
        tag.setPageContext(pageContext);
        tag.setLabel("My Favourite Fruit");
        tag.setName("myFavouriteFruit");
        tag.setEmptyOption("true");
        tag.setHeaderKey("-1");
        tag.setHeaderValue("--- Please Select ---");
        tag.setList("collection");
        tag.setValue("%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(ComboBoxTag.class.getResource("ComboBox-2.txt"));
    }

    public void testWithMap() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("banana");

        Map m = new LinkedHashMap();
        m.put("apple", "apple");
        m.put("banana", "banana");
        m.put("pineaple", "pineaple");
        m.put("grapes", "grapes");
        testAction.setMap(m);

        ComboBoxTag tag = new ComboBoxTag();
        tag.setPageContext(pageContext);
        tag.setLabel("My Favourite Fruit");
        tag.setName("myFavouriteFruit");
        tag.setHeaderKey("-1");
        tag.setHeaderValue("--- Please Select ---");
        tag.setEmptyOption("true");
        tag.setList("map");
        tag.setValue("%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(ComboBoxTag.class.getResource("ComboBox-3.txt"));
    }

    public void testJsCallNamingUsesEscapedId() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("hello");

        ArrayList collection = new ArrayList();
        collection.add("foo");
        testAction.setCollection(collection);

        ComboBoxTag tag = new ComboBoxTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setId("cb.bc");
        tag.setList("collection");

        tag.doStartTag();
        tag.doEndTag();

        verify(ComboBoxTag.class.getResource("ComboBox-4.txt"));
    }

}
