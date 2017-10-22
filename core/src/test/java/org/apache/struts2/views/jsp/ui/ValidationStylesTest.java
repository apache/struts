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
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;

public class ValidationStylesTest extends AbstractUITagTest {
    private TextFieldTag tag;

    public void testNormalStyle() throws Exception {
        tag.setCssStyle("style");
        tag.doStartTag();
        tag.doEndTag();

        verify(ValidationStylesTest.class.getResource("validationstyles-1.txt"));
    }

    public void testErrorStyle() throws Exception {
        tag.setCssErrorStyle("errstyle");
        tag.doStartTag();
        tag.doEndTag();

        verify(ValidationStylesTest.class.getResource("validationstyles-2.txt"));
    }

    public void testErrorClass() throws Exception {
        tag.setCssErrorClass("errclass");
        tag.doStartTag();
        tag.doEndTag();

        verify(ValidationStylesTest.class.getResource("validationstyles-3.txt"));
    }

    public void testStyleAndErrorStyle() throws Exception {
        tag.setCssStyle("style");
        tag.setCssErrorStyle("errstyle");
        tag.doStartTag();
        tag.doEndTag();

        verify(ValidationStylesTest.class.getResource("validationstyles-2.txt"));
    }

     public void testStyleAndErrorClass() throws Exception {
        tag.setCssStyle("style");
        tag.setCssErrorClass("errclass");
        tag.doStartTag();
        tag.doEndTag();

        verify(ValidationStylesTest.class.getResource("validationstyles-3.txt"));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestAction testAction = (TestAction) action;
        tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setLabel("mylabel");
        tag.setName("foo");
        tag.setValue("bar");
        tag.setTitle("mytitle");

        testAction.addFieldError("foo", "bar error message");
    }
}
