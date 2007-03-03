/*
 * $Id: DivTest.java 511300 2007-02-24 16:41:38Z musachy $
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


/**
 */
public class DivTest extends AbstractUITagTest {


    public void testGenericSimple() throws Exception {
        AbstractRemoteCallUITag tag = new DivTag();
        verifyGenericProperties(tag, "simple", new String[]{"value","tabindex","disabled"});
    }

    public void testGenericXhtml() throws Exception {
        AbstractRemoteCallUITag tag = new DivTag();
        verifyGenericProperties(tag, "xhtml", new String[]{"value","tabindex","disabled"});
    }

    public void testGenericAjax() throws Exception {
        AbstractRemoteCallUITag tag = new DivTag();
        verifyGenericProperties(tag, "ajax", new String[]{"value","tabindex","disabled"});
    }

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        DivTag tag = new DivTag();
        tag.setPageContext(pageContext);

        tag.setId("mylabel");
        tag.setTheme("ajax");
        tag.setHref("a");
        tag.setLoadingText("b");
        tag.setErrorText("c");
        tag.setAutoStart("true");
        tag.setDelay("4000");
        tag.setUpdateFreq("1000");
        tag.setListenTopics("g");
        tag.setStartTimerListenTopics("h");
        tag.setStopTimerListenTopics("i");
        tag.setBeforeLoading("j");
        tag.setAfterLoading("k");
        tag.setRefreshOnShow("true");
        tag.setHandler("l");
        tag.setIndicator("m");
        tag.setShowLoadingText("true");
        tag.setSeparateScripts("false");
        tag.doStartTag();
        tag.doEndTag();

        verify(DivTest.class.getResource("div-1.txt"));
    }

}
