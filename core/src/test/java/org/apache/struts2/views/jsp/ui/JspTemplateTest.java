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

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;

/**
 * JspTemplateTest
 *
 */
public class JspTemplateTest extends AbstractUITagTest {
    public void testCheckBox() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("true");

        CheckboxTag tag = new CheckboxTag();
        Mock rdMock = new Mock(RequestDispatcher.class);
        rdMock.expect("include",C.args(C.isA(HttpServletRequest.class), C.isA(HttpServletResponse.class)));
        RequestDispatcher dispatcher = (RequestDispatcher) rdMock.proxy();
        request.setupGetRequestDispatcher(dispatcher);
        tag.setPageContext(pageContext);
        tag.setTemplate("/test/checkbox.jsp");
        tag.doStartTag();
        tag.doEndTag();
        rdMock.verify();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        CheckboxTag freshTag = new CheckboxTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testCheckBox_clearTagStateSet()  throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("true");

        CheckboxTag tag = new CheckboxTag();
        Mock rdMock = new Mock(RequestDispatcher.class);
        rdMock.expect("include",C.args(C.isA(HttpServletRequest.class), C.isA(HttpServletResponse.class)));
        RequestDispatcher dispatcher = (RequestDispatcher) rdMock.proxy();
        request.setupGetRequestDispatcher(dispatcher);
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setTemplate("/test/checkbox.jsp");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();
        rdMock.verify();

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        CheckboxTag freshTag = new CheckboxTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }
}
