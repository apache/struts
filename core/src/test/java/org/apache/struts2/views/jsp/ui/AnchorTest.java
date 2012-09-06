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
public class AnchorTest extends AbstractUITagTest {

    public void testSimple() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setHref("a");
        tag.doStartTag();
        tag.doEndTag();

        verifyResource("href-1.txt");
    }

    public void testSimpleBadQuote() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setHref("a\"");
        tag.doStartTag();
        tag.doEndTag();

        verifyResource("href-2.txt");
    }

    public void testDynamicAttribute() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setHref("a");

        tag.setDynamicAttribute("uri", "dynAttrName", "dynAttrValue");

        tag.doStartTag();
        tag.doEndTag();

        verifyResource("Anchor-2.txt");
    }

    public void testDynamicAttributeAsExpression() throws Exception {
        createAction();

        AnchorTag tag = createTag();
        tag.setHref("a");

        tag.setDynamicAttribute("uri", "placeholder", "%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verifyResource("Anchor-3.txt");
    }

    private void createAction() {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");
    }

    private AnchorTag createTag() {
        AnchorTag tag = new AnchorTag();
        tag.setPageContext(pageContext);

        tag.setId("mylink");
        return tag;
    }
}
