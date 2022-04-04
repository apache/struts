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
package org.apache.struts2.views.java.simple;

import org.apache.struts2.components.Checkbox;
import org.apache.struts2.components.UIBean;

public class CheckboxTest extends AbstractCommonAttributesTest {
    private Checkbox tag;

    public void testRenderCheckbox() {
        tag.setName("name_");
        tag.setDisabled("true");
        tag.setTabindex("1");
        tag.setId("id_");
        tag.setCssClass("class");
        tag.setCssStyle("style");
        tag.setTitle("title");
        tag.setFieldValue("xyz");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<input type='checkbox' name='name_' value='xyz' tabindex='1' id='id_' class='class' style='style' title='title'></input><input type='hidden' id='__checkbox_id_' name='__checkbox_name_' value='__checkbox_xyz'></input>");
        assertEquals(expected, output);
    }

    public void testRenderCheckboxWithNameValue() {
        tag.setName("name_");
        tag.setValue("%{someValue}");
        tag.setDisabled("true");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<input type='checkbox' name='name_' value='true' checked='checked' id='name_'></input><input type='hidden' id='__checkbox_name_' name='__checkbox_name_' value='__checkbox_true'></input>");
        assertEquals(expected, output);
    }

    @Override
    protected void setUpStack() {
        super.setUpStack();

        expectFind("someValue", Boolean.class, true);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tag = new Checkbox(stack, request, response);
    }

    @Override
    protected UIBean getUIBean() {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "checkbox";
    }
}
