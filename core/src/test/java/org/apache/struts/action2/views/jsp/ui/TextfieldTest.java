/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.TestAction;
import org.apache.struts.action2.views.freemarker.tags.TextFieldModel;
import org.apache.struts.action2.views.jsp.AbstractUITagTest;
import freemarker.template.TransformControl;

import java.util.HashMap;
import java.util.Map;


/**
 */
public class TextfieldTest extends AbstractUITagTest {

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

    public void testGenericAjax() throws Exception {
        TextFieldTag tag = new TextFieldTag();
        verifyGenericProperties(tag, "ajax", null);
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

    public void testNoLabelFtl() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        TextFieldModel model = new TextFieldModel(stack, request, response);
        HashMap params = new HashMap();
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
}
