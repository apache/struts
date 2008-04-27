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


/**
 */
public class DivTest extends AbstractUITagTest {


    public void testGenericSimple() throws Exception {
        DivTag tag = new DivTag();
        verifyGenericProperties(tag, "simple", new String[]{"value","tabindex","disabled"});
    }

    public void testGenericXhtml() throws Exception {
        DivTag tag = new DivTag();
        verifyGenericProperties(tag, "xhtml", new String[]{"value","tabindex","disabled"});
    }

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        DivTag tag = new DivTag();
        tag.setPageContext(pageContext);
        tag.setId("mylabel");
        tag.doStartTag();
        tag.doEndTag();

        verify(DivTest.class.getResource("div-1.txt"));
    }

}
