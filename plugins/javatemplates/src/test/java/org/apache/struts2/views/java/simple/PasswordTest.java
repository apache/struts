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

import org.apache.struts2.components.Password;
import org.apache.struts2.components.UIBean;

public class PasswordTest extends AbstractCommonAttributesTest {
    private Password tag;
    private boolean showPassword;

    public void testRenderPassword() throws Exception {
        this.showPassword = false;
        super.setUp();
        this.tag = new Password(stack, request, response);

        tag.setName("name");
        tag.setValue("val1");
        tag.setSize("10");
        tag.setDisabled("true");
        tag.setTabindex("1");
        tag.setId("id1");
        tag.setCssClass("class1");
        tag.setCssStyle("style1");
        tag.setTitle("title");


        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<input name='name' type='password' size='10' tabindex='1' id='id1' class='class1' style='style1' title='title'></input>");
        assertEquals(expected, output);
    }

    public void testRenderPasswordShowIt() throws Exception {
        this.showPassword = true;
        super.setUp();
        this.tag = new Password(stack, request, response);

        tag.setName("name");
        tag.setValue("val1");
        tag.setSize("10");
        tag.setDisabled("true");
        tag.setTabindex("1");
        tag.setId("id1");
        tag.setCssClass("class1");
        tag.setCssStyle("style1");
        tag.setTitle("title");
        tag.setShowPassword("%{'true'}");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<input value='val1' name='name' type='password' size='10' tabindex='1' id='id1' class='class1' style='style1' title='title'></input>");
        assertEquals(expected, output);
    }

    @Override
    protected void setUp() throws Exception {
        //dont call base setup
    }

    @Override
    protected void setUpStack() {
        expectFind("'true'", Boolean.class, true);
    }

    @Override
    protected UIBean getUIBean() throws Exception {
        super.setUp();
        this.tag = new Password(stack, request, response);
        return tag;
    }

    @Override
    protected String getTagName() {
        return "password";
    }
}


