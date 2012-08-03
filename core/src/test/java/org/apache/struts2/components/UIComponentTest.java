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

package org.apache.struts2.components;

import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.views.jsp.AbstractUITagTest;
import org.apache.struts2.views.jsp.ui.ActionErrorTag;
import org.apache.struts2.views.jsp.ui.ActionMessageTag;
import org.apache.struts2.views.jsp.ui.AnchorTag;
import org.apache.struts2.views.jsp.ui.CheckboxTag;
import org.apache.struts2.views.jsp.ui.ComboBoxTag;
import org.apache.struts2.views.jsp.ui.DivTag;
import org.apache.struts2.views.jsp.ui.DoubleSelectTag;
import org.apache.struts2.views.jsp.ui.FieldErrorTag;
import org.apache.struts2.views.jsp.ui.FileTag;
import org.apache.struts2.views.jsp.ui.FormTag;
import org.apache.struts2.views.jsp.ui.HiddenTag;
import org.apache.struts2.views.jsp.ui.LabelTag;
import org.apache.struts2.views.jsp.ui.OptionTransferSelectTag;
import org.apache.struts2.views.jsp.ui.PasswordTag;
import org.apache.struts2.views.jsp.ui.RadioTag;
import org.apache.struts2.views.jsp.ui.SelectTag;
import org.apache.struts2.views.jsp.ui.SubmitTag;
import org.apache.struts2.views.jsp.ui.TextFieldTag;
import org.apache.struts2.views.jsp.ui.TextareaTag;
import org.apache.struts2.views.jsp.ui.TokenTag;

import com.opensymphony.xwork2.ActionContext;


/**
 * Test case common for all UI component in general.
 */
public class UIComponentTest extends AbstractUITagTest {

    // actionError
    public void testActionErrorComponentDisposeItselfFromComponentStack() throws Exception {
        ActionMessageTag t = new ActionMessageTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            ActionErrorTag tag = new ActionErrorTag();
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }

    }

    // actionMessage
    public void testActionMessageDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            ActionMessageTag tag = new ActionMessageTag();
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    //   Anchor
    public void testAnchorComponentDisposeItselfFromComponentStack() throws Exception {

        TextFieldTag t = new TextFieldTag();
        t.setPageContext(pageContext);
        t.setName("textFieldName");

        AnchorTag tag = new AnchorTag();
        tag.setPageContext(pageContext);

        try {
            t.doStartTag();
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // checkbox
    public void testCheckboxDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            CheckboxTag tag = new CheckboxTag();
            tag.setName("name");
            tag.setLabel("label");
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // combobox
    public void testComboboxDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            ComboBoxTag tag = new ComboBoxTag();
            tag.setName("name");
            tag.setLabel("label");
            tag.setList("{'aaa','bbb','ccc'}");
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // div
    public void testDivComponentDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            DivTag tag = new DivTag();
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // doubleselect
    public void testDoubleselectComponentDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            DoubleSelectTag tag = new DoubleSelectTag();
            tag.setName("name");
            tag.setLabel("label");
            tag.setList("#{1:'one',2:'two'}");
            tag.setDoubleName("doubleName");
            tag.setDoubleList("1?({'aa','bb'}:{'cc','dd'}");
            tag.setFormName("formName");
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // fieldError
    public void testFielderrorComponentDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            FieldErrorTag tag = new FieldErrorTag();
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // file
    public void testFileDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            FileTag tag = new FileTag();
            tag.setName("name");
            tag.setLabel("label");
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // form
    public void testFormComponentDisposeItselfFromComponentStack() throws Exception {
        configurationManager.clearContainerProviders();
        configurationManager.addContainerProvider(new TestConfigurationProvider());
        ActionContext.getContext().setValueStack(stack);

        request.setupGetServletPath("/testAction");

        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            FormTag tag = new FormTag();
            tag.setName("myForm");
            tag.setMethod("POST");
            tag.setAction("myAction");
            tag.setEnctype("myEncType");
            tag.setTitle("mytitle");
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // hidden
    public void testHiddenComponentDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            HiddenTag tag = new HiddenTag();
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // label
    public void testLabelComponentDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            LabelTag tag = new LabelTag();
            tag.setName("name");
            tag.setLabel("label");
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // optiontransferselect
    public void testOptiontransferselectComponentDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            OptionTransferSelectTag tag = new OptionTransferSelectTag();
            tag.setId("myId");
            tag.setDoubleId("myDoubleId");
            tag.setName("name");
            tag.setLabel("label");
            tag.setList("{}");
            tag.setDoubleList("{}");
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // password
    public void testPasswordComponentDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            PasswordTag tag = new PasswordTag();
            tag.setName("name");
            tag.setLabel("label");
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // radio
    public void testRadioComponentDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            RadioTag tag = new RadioTag();
            tag.setList("{}");
            tag.setName("name");
            tag.setLabel("label");
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // select
    public void testSelectComponentDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            SelectTag tag = new SelectTag();
            tag.setList("{}");
            tag.setName("name");
            tag.setLabel("label");
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();

            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // submit
    public void testSubmitDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            SubmitTag tag = new SubmitTag();
            tag.setName("name");
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // textarea
    public void testTextareaComponentDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            TextareaTag tag = new TextareaTag();
            tag.setName("name");
            tag.setLabel("label");
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // textfield
    public void testTextfieldComponentDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            TextFieldTag tag = new TextFieldTag();
            tag.setName("name");
            tag.setLabel("label");
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    // token
    public void testTokenComponentDisposeItselfFromComponentStack() throws Exception {
        ActionErrorTag t = new ActionErrorTag();
        t.setPageContext(pageContext);

        try {
            t.doStartTag();
            TokenTag tag = new TokenTag();
            tag.setPageContext(pageContext);
            tag.doStartTag();
            assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
            tag.doEndTag();
            assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());

            t.doEndTag();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
}
