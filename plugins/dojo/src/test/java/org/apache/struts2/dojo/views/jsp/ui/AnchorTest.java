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
 */
public class AnchorTest extends AbstractUITagTest {

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        AnchorTag tag = new AnchorTag();
        tag.setPageContext(pageContext);

        tag.setId("mylink");
        tag.setHref("a");
        tag.setErrorText("c");
        tag.setLoadingText("d");
        tag.setBeforeNotifyTopics("e");
        tag.setAfterNotifyTopics("f");
        tag.setListenTopics("g");
        tag.setTargets("h");
        tag.setHandler("i");
        tag.setNotifyTopics("j");
        tag.setIndicator("k");
        tag.setShowErrorTransportText("true");
        tag.setShowLoadingText("true");
        tag.setErrorNotifyTopics("l");
        tag.setHighlightColor("m");
        tag.setHighlightDuration("n");
        tag.setValidate("true");
        tag.setAjaxAfterValidation("true");
        tag.setSeparateScripts("true");
        tag.setTransport("o");
        tag.setParseContent("false");
        tag.doStartTag();
        tag.doEndTag();

        verify(AnchorTest.class.getResource("href-1.txt"));
    }

}
