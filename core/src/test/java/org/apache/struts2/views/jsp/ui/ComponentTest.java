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
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.AbstractUITagTest;


/**
 */
public class ComponentTest extends AbstractUITagTest {

    /**
     * Test that id attribute is evaludated against the Ognl Stack.
     * @throws Exception
     */
    public void testIdIsEvaluatedAgainstStack1() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("myFooValue");

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("foo");
        tag.setId("%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(ComponentTag.class.getResource("Component-2.txt"));
    }

    public void testIdIsEvaludatedAgainstStack2() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("myFooValue");

        TextFieldTag tag = new TextFieldTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("foo");
        tag.setId("foo");

        tag.doStartTag();
        tag.doEndTag();

        verify(ComponentTag.class.getResource("Component-3.txt"));
    }


    /**
     * Note -- this test uses empty.vm, so it's basically clear
     */
    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        ComponentTag tag = new ComponentTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("foo");

        tag.doStartTag();
        tag.doEndTag();

        verify(ComponentTag.class.getResource("Component-1.txt"));
    }

    /**
     * executes a component test passing in a custom parameter. it also executes calling a custom template using an
     * absolute reference.
     */
    public void testWithParam() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        ComponentTag tag = new ComponentTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("foo");
        tag.setTheme("test");
        tag.setTemplate("Component");

        tag.doStartTag();
        tag.getComponent().addParameter("hello", "world");
        tag.getComponent().addParameter("argle", "bargle");
        tag.getComponent().addParameter("glip", "glop");
        tag.getComponent().addParameter("array", new String[]{"a", "b", "c"});
        tag.getComponent().addParameter("objClass", tag.getClass().getName());
        tag.doEndTag();

        //        System.out.println(writer);
        verify(ComponentTag.class.getResource("Component-param.txt"));
    }

    public void testTagAttributeExclusion() throws Exception {
        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);

        tag.setDynamicAttribute("uri://some.uri", "includeContext", false);

        tag.doStartTag();

        assertTrue(tag.includeContext);
    }

}

