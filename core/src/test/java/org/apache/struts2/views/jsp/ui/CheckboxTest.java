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

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;


/**
 */
public class CheckboxTest extends AbstractUITagTest {

    public CheckboxTest() {
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
        tag.setLabelposition("top");

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
        tag.setLabelposition("left");

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
        tag.setCssErrorClass("myErrorClass");

        tag.doStartTag();
        tag.doEndTag();

        verify(CheckboxTag.class.getResource("Checkbox-3.txt"));
    }

    public void testCheckedWithErrorStyle() throws Exception {
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
        tag.setCssErrorStyle("color:red");

        tag.doStartTag();
        tag.doEndTag();

        verify(CheckboxTag.class.getResource("Checkbox-33.txt"));
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
    
    public void testDisabled() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("true");

        CheckboxTag tag = new CheckboxTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setFieldValue("baz");
        tag.setTitle("mytitle");
        tag.setDisabled("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(CheckboxTag.class.getResource("Checkbox-6.txt"));
    }
}
