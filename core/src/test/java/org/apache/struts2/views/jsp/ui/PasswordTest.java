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


/**
 */
public class PasswordTest extends AbstractUITagTest {

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        PasswordTag tag = new PasswordTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setTitle("mytitle");

        tag.doStartTag();
        tag.doEndTag();

        verify(PasswordTag.class.getResource("Password-1.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PasswordTag freshTag = new PasswordTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testSimple_clearTagStateSet() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        PasswordTag tag = new PasswordTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setTitle("mytitle");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(PasswordTag.class.getResource("Password-1.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        PasswordTag freshTag = new PasswordTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testGenericSimple() throws Exception {
        PasswordTag tag = new PasswordTag();
        verifyGenericProperties(tag, "simple", new String[]{"value"});
    }

    public void testGenericXhtml() throws Exception {
        PasswordTag tag = new PasswordTag();
        verifyGenericProperties(tag, "xhtml", new String[]{"value"});
    }
}
