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

public class BindTest extends AbstractUITagTest {
    public void testAll() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        BindTag tag = new BindTag();
        tag.setPageContext(pageContext);

        tag.setId("a");
        tag.setHref("b");
        tag.setLoadingText("c");
        tag.setErrorText("d");
        tag.setListenTopics("e");
        tag.setBeforeNotifyTopics("f");
        tag.setAfterNotifyTopics("g");
        tag.setHandler("h");
        tag.setNotifyTopics("k");
        tag.setIndicator("l");
        tag.setShowLoadingText("true");
        tag.setErrorNotifyTopics("m");
        tag.setSources("n");
        tag.setEvents("o");
        tag.setHighlightColor("p");
        tag.setHighlightDuration("q");
        tag.setValidate("true");
        tag.setSeparateScripts("true");
        tag.setTransport("q");
        tag.setParseContent("false");
        tag.doStartTag();
        tag.doEndTag();

        verify(BindTest.class.getResource("Bind-1.txt"));
    }
}
