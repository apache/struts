/*
 * $Id: SubmitAjaxTest.java 508285 2007-02-16 02:42:24Z musachy $
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.ui.dojo.AbstractRemoteCallUITag;
import org.apache.struts2.views.jsp.ui.dojo.DivTag;
import org.apache.struts2.views.jsp.ui.dojo.SubmitTag;

/**
 * Test Submit component in "ajax" theme.
 */
public class SubmitAjaxTest extends AbstractUITagTest {

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

    public void testSubmit() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        SubmitTag tag = new SubmitTag();
        tag.setPageContext(pageContext);

        tag.setId("a");
        tag.setTheme("ajax");
        tag.setHref("b");
        tag.setLoadingText("c");
        tag.setErrorText("d");
        tag.setListenTopics("e");
        tag.setPreInvokeJS("f");
        tag.setOnLoadJS("g");
        tag.setHandler("h");
        tag.setType("submit");
        tag.setLabel("i");
        tag.setNotifyTopics("k");
        tag.setIndicator("l");
        tag.setShowLoadingText("true");
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
        tag.setTheme("ajax");
        tag.setHref("b");
        tag.setLoadingText("c");
        tag.setErrorText("d");
        tag.setListenTopics("e");
        tag.setPreInvokeJS("f");
        tag.setOnLoadJS("g");
        tag.setHandler("h");
        tag.setType("button");
        tag.setLabel("i");
        tag.setNotifyTopics("k");
        tag.setIndicator("l");
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
        tag.setTheme("ajax");
        tag.setHref("b");
        tag.setLoadingText("c");
        tag.setErrorText("d");
        tag.setListenTopics("e");
        tag.setPreInvokeJS("f");
        tag.setOnLoadJS("g");
        tag.setHandler("h");
        tag.setType("image");
        tag.setLabel("i");
        tag.setSrc("j");
        tag.setNotifyTopics("k");
        tag.setIndicator("l");
        tag.doStartTag();
        tag.doEndTag();

        verify(SubmitAjaxTest.class.getResource("submit-ajax-3.txt"));
    }
}
