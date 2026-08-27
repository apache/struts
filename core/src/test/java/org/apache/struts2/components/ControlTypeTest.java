/*
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

import org.apache.struts2.views.jsp.AbstractUITagTest;

public class ControlTypeTest extends AbstractUITagTest {

    public void testTextFieldDefaultsToText() {
        TextField textField = new TextField(stack, request, response);
        assertEquals(HtmlControlType.TEXT, textField.getControlType());
    }

    public void testTextFieldHonoursAnExplicitType() {
        TextField textField = new TextField(stack, request, response);
        textField.addParameter("type", "number");
        assertEquals(HtmlControlType.NUMBER, textField.getControlType());
    }

    public void testTextFieldFallsBackForAnUnknownType() {
        TextField textField = new TextField(stack, request, response);
        textField.addParameter("type", "supercolor");
        assertEquals(HtmlControlType.OTHER, textField.getControlType());
    }

    public void testPasswordIsAlwaysPassword() {
        Password password = new Password(stack, request, response);
        assertEquals(HtmlControlType.PASSWORD, password.getControlType());
    }

    public void testTextAreaIsTextarea() {
        TextArea textArea = new TextArea(stack, request, response);
        assertEquals(HtmlControlType.TEXTAREA, textArea.getControlType());
    }

    public void testSelectIsSelect() {
        Select select = new Select(stack, request, response);
        assertEquals(HtmlControlType.SELECT, select.getControlType());
    }

    public void testRadioIsRadio() {
        Radio radio = new Radio(stack, request, response);
        assertEquals(HtmlControlType.RADIO, radio.getControlType());
    }

    public void testFileIsFile() {
        File file = new File(stack, request, response);
        assertEquals(HtmlControlType.FILE, file.getControlType());
    }

    public void testControlsWithoutAnOverrideAreUnknown() {
        // CheckboxInterceptor substitutes "false" for an unticked box, so the server accepts what
        // a browser "required" would block — that is a real false reject, and the reason Checkbox
        // and Hidden deliberately have no getControlType() override.
        assertEquals(HtmlControlType.OTHER, new Checkbox(stack, request, response).getControlType());
        assertEquals(HtmlControlType.OTHER, new Hidden(stack, request, response).getControlType());
    }
}
