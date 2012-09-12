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

import java.util.HashMap;
import java.util.Map;


/**
 */
public class LabelTest extends AbstractUITagTest {

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        LabelTag tag = new LabelTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setTitle("mytitle");
        tag.setValue("%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(LabelTest.class.getResource("Label-1.txt"));
    }

    public void testSimpleWithLabelposition() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        LabelTag tag = new LabelTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("%{foo}");
        tag.setLabelposition("top");

        tag.doStartTag();
        tag.doEndTag();

        verify(LabelTest.class.getResource("Label-3.txt"));
    }

    /**
     * Initialize a map of {@link org.apache.struts2.views.jsp.AbstractUITagTest.PropertyHolder} for generic tag
     * property testing. Will be used when calling {@link #verifyGenericProperties(org.apache.struts2.views.jsp.ui.AbstractUITag,
     * String, String[])} as properties to verify.<p/> This implementation extends testdata from AbstractUITag.
     *
     * @return A Map of PropertyHolders values bound to {@link org.apache.struts2.views.jsp.AbstractUITagTest.PropertyHolder#getName()}
     *         as key.
     */
    protected Map initializedGenericTagTestProperties() {
        Map result = new HashMap();
        new PropertyHolder("title", "someTitle").addToMap(result);
        new PropertyHolder("cssClass", "cssClass1", "class=\"cssClass1\"").addToMap(result);
        new PropertyHolder("cssStyle", "cssStyle1", "style=\"cssStyle1\"").addToMap(result);
        new PropertyHolder("id", "someId").addToMap(result);
        new PropertyHolder("for", "someFor").addToMap(result);
        return result;
    }

    public void testWithNoValue() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("baz");

        LabelTag tag = new LabelTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setFor("for");

        tag.doStartTag();
        tag.doEndTag();

        verify(LabelTest.class.getResource("Label-5.txt"));
    }

    public void testGenericSimple() throws Exception {
        LabelTag tag = new LabelTag();
        verifyGenericProperties(tag, "simple", null);
    }

    public void testGenericXhtml() throws Exception {
        LabelTag tag = new LabelTag();
        verifyGenericProperties(tag, "xhtml", null);
    }

    public void testWithKeyNoValueFromStack() throws Exception {
        TestAction testAction = (TestAction) action;
        final String key = "labelKey";
        final String value = "baz";
        testAction.setText(key, value);

        testAction.setFoo("notToBeOutput");

        LabelTag tag = new LabelTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setFor("for");
        tag.setName("foo2");
        tag.setKey(key);

        tag.doStartTag();
        tag.doEndTag();

        verify(LabelTest.class.getResource("Label-2.txt"));
    }

     public void testWithKeyValueFromStack() throws Exception {
        TestAction testAction = (TestAction) action;
        final String key = "labelKey";
        final String value = "baz";
        testAction.setText(key, value);

        testAction.setFoo("output");

        LabelTag tag = new LabelTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setFor("for");
        tag.setName("foo");
        tag.setKey(key);

        tag.doStartTag();
        tag.doEndTag();

        verify(LabelTest.class.getResource("Label-4.txt"));
    }

    public void testWithJustKeyValueFromStack() throws Exception {
        TestAction testAction = (TestAction) action;
        final String key = "labelKey";
        final String value = "baz";

        // put key with message in a "resource"
        testAction.setText(key, value);

        LabelTag tag = new LabelTag();
        tag.setPageContext(pageContext);
        tag.setKey(key);
        tag.setTheme("simple");

        tag.doStartTag();
        tag.doEndTag();

        verify(LabelTest.class.getResource("Label-6.txt"));
    }

}
