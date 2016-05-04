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
 * Unit test for {@link SubmitTag}.
 *
 */
public class SubmitTest extends AbstractUITagTest {

    public void testButtonSimpleWithBody() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setTheme("simple");
        tag.setPageContext(pageContext);
        tag.setType("button");
        tag.setName("myname");
        tag.setLabel("yoyoyoyoy");
        tag.setValue("%{foo}");

        StrutsBodyContent body = new StrutsBodyContent(null);
        body.print("foo");
        tag.setBodyContent(body);
        tag.doStartTag();
        tag.doEndTag();

        verify(TextFieldTag.class.getResource("Submit-10.txt"));
    }

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
        tag.setDisabled("true");
        tag.setTabindex("1");

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
        tag.setDisabled("true");
        tag.setTabindex("1");

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
        tag.setDisabled("true");

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

        verify(TextFieldTag.class.getResource("Submit-7.txt"));
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

        verify(TextFieldTag.class.getResource("Submit-8.txt"));
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

        verify(TextFieldTag.class.getResource("Submit-9.txt"));
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

        assertEquals("<input type=\"submit\" value=\"Submit\" id=\"myname\" name=\"myname\"/>", writer.toString().trim());
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

}
