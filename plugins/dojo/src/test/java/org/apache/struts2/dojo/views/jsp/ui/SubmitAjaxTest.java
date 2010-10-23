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

package org.apache.struts2.dojo.views.jsp.ui;

import org.apache.struts2.dojo.TestAction;

/**
 * Test Submit component in "ajax" theme.
 */
public class SubmitAjaxTest extends AbstractUITagTest {
    public void testSubmit() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);

        tag.setId("a");
        tag.setHref("b");
        tag.setDisabled("true");
        tag.setLoadingText("c");
        tag.setErrorText("d");
        tag.setListenTopics("e");
        tag.setBeforeNotifyTopics("f");
        tag.setAfterNotifyTopics("g");
        tag.setHandler("h");
        tag.setType("submit");
        tag.setLabel("i");
        tag.setNotifyTopics("k");
        tag.setIndicator("l");
        tag.setShowLoadingText("true");
        tag.setErrorNotifyTopics("m");
        tag.setHighlightColor("n");
        tag.setHighlightDuration("o");
        tag.setValidate("true");
        tag.setAjaxAfterValidation("true");
        tag.setSeparateScripts("true");
        tag.setTabindex("1");
        tag.setTransport("p");
        tag.setParseContent("false");
        tag.doStartTag();
        tag.doEndTag();

        verify(SubmitAjaxTest.class.getResource("submit-ajax-1.txt"));
    }

    public void testButton() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);

        tag.setId("a");
        tag.setDisabled("true");
        tag.setTheme("ajax");
        tag.setHref("b");
        tag.setLoadingText("c");
        tag.setErrorText("d");
        tag.setListenTopics("e");
        tag.setBeforeNotifyTopics("f");
        tag.setAfterNotifyTopics("g");
        tag.setHandler("h");
        tag.setType("button");
        tag.setLabel("i");
        tag.setNotifyTopics("k");
        tag.setIndicator("l");
        tag.setErrorNotifyTopics("m");
        tag.setValidate("true");
        tag.setSeparateScripts("true");
        tag.setTabindex("1");
        tag.doStartTag();
        tag.doEndTag();

        verify(SubmitAjaxTest.class.getResource("submit-ajax-2.txt"));
    }

    public void testImage() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);

        tag.setId("a");
        tag.setDisabled("true");
        tag.setTheme("ajax");
        tag.setHref("b");
        tag.setLoadingText("c");
        tag.setErrorText("d");
        tag.setListenTopics("e");
        tag.setBeforeNotifyTopics("f");
        tag.setAfterNotifyTopics("g");
        tag.setHandler("h");
        tag.setType("image");
        tag.setLabel("i");
        tag.setSrc("j");
        tag.setNotifyTopics("k");
        tag.setIndicator("l");
        tag.setErrorNotifyTopics("m");
        tag.setValidate("true");
        tag.setSeparateScripts("true");
        tag.doStartTag();
        tag.doEndTag();

        verify(SubmitAjaxTest.class.getResource("submit-ajax-3.txt"));
    }
}
