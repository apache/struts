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

import freemarker.template.TransformControl;
import org.apache.struts2.TestAction;
import org.apache.struts2.views.freemarker.tags.TextFieldModel;
import org.apache.struts2.views.jsp.AbstractUITagTest;

import java.util.HashMap;
import java.util.Map;


/**
 */
public class TextfieldTest extends AbstractUITagTest {

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
        new PropertyHolder("maxlength", "10").addToMap(result);
        new PropertyHolder("readonly", "true", "readonly=\"readonly\"").addToMap(result);
        new PropertyHolder("size", "12").addToMap(result);
        return result;
    }

    public void testGenericSimple() throws Exception {
        TextFieldTag tag = new TextFieldTag();
        verifyGenericProperties(tag, "simple", null);
    }

    public void testGenericXhtml() throws Exception {
        TextFieldTag tag = new TextFieldTag();
        verifyGenericProperties(tag, "xhtml", null);
    }

    public void testErrors() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setValue("bar");
        tag.setTitle("mytitle");

        testAction.addFieldError("foo", "bar error message");
        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Textfield-2.txt"));
    }

    public void testNoLabelJsp() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setName("myname");
        tag.setValue("%{foo}");
        tag.setSize("10");
        tag.setOnblur("blahescape('somevalue');");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Textfield-3.txt"));
    }
    
    public void testLabelSeparatorJsp() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setName("myname");
        tag.setValue("%{foo}");
        tag.setSize("10");
        tag.setOnblur("blahescape('somevalue');");
        tag.setLabelSeparator("??");
        tag.setLabel("label");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Textfield-4.txt"));
    }

    public void testNoLabelFtl() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextFieldModel model = new TextFieldModel(stack, request, response);
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "myname");
        params.put("value", "%{foo}");
        params.put("size", "10");
        params.put("onblur", "blahescape('somevalue');");
        TransformControl control = (TransformControl) model.getWriter(writer, params);
        control.onStart();
        control.afterBody();

        verify(TextFieldTag.class.getResource("Textfield-3.txt"));
    }

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("%{foo}");
        tag.setSize("10");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Textfield-1.txt"));
    }
    
    public void testSimple_recursionTest() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("%{1+1}");

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("%{foo}");
        tag.setSize("10");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Textfield-5.txt"));
    }
    
    public void testSimple_recursionTestNoValue() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("%{1+1}");

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setSize("10");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Textfield-6.txt"));
    }

    public void testHtml5EmailTag() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setLabel("myemaillabel");
        tag.setName("foo");
        tag.setSize("50");
        tag.setType("email");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Textfield-7.txt"));
    }

    public void testErrorPositionBottom() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setValue("bar");
        tag.setTitle("mytitle");
        tag.setErrorPosition("bottom");

        testAction.addFieldError("foo", "bar error message");
        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Textfield-8.txt"));
    }

    public void testErrorPositionTop() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setValue("bar");
        tag.setTitle("mytitle");
        tag.setErrorPosition("top");

        testAction.addFieldError("foo", "bar error message");
        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Textfield-9.txt"));
    }

    public void testErrorPositionBottomCssXhtmlTheme() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setValue("bar");
        tag.setTitle("mytitle");
        tag.setErrorPosition("bottom");
        tag.setTheme("css_xhtml");

        testAction.addFieldError("foo", "bar error message");
        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Textfield-10.txt"));
    }

    public void testErrorPositionTopCssXhtmlTheme() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setValue("bar");
        tag.setTitle("mytitle");
        tag.setErrorPosition("top");
        tag.setTheme("css_xhtml");

        testAction.addFieldError("foo", "bar error message");
        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Textfield-11.txt"));
    }

    public void testNameEvaluation() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setArray(new String[]{"test", "bar"});
        testAction.setFooInt(1);

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setName("array[%{fooInt}]");

        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Textfield-12.txt"));
    }


}
