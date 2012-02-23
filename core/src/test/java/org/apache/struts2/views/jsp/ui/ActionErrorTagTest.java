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

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * ActionErrorTag test case.
 *
 */
public class ActionErrorTagTest extends AbstractUITagTest {

    boolean shouldActionHaveError = false;

    public void testNoActionErrors() throws Exception {
        ActionErrorTag tag = new ActionErrorTag();
        ((InternalActionSupport)action).setHasActionErrors(false);
        tag.setPageContext(pageContext);
        tag.doStartTag();
        tag.doEndTag();

        //assertEquals("", writer.toString());
        verify(ActionErrorTagTest.class.getResource("actionerror-1.txt"));
    }

     public void testActionErrorsEscape() throws Exception {

        ActionErrorTag tag = new ActionErrorTag();
        TestAction testAction = new TestAction();
        testAction.addActionError("<p>hey</p>");
        stack.pop();
        stack.push(testAction);
        tag.setEscape(true);
        tag.setPageContext(pageContext);
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(normalize("<ul class=\"errorMessage\"><li><span>&lt;p&gt;hey&lt;/p&gt;</span></li></ul>", true),
                normalize(writer.toString(), true));
    }

    public void testActionErrorsDontEscape() throws Exception {

        ActionErrorTag tag = new ActionErrorTag();
        TestAction testAction = new TestAction();
        testAction.addActionError("<p>hey</p>");
        stack.pop();
        stack.push(testAction);
        tag.setEscape(false);
        tag.setPageContext(pageContext);
        tag.doStartTag();
        tag.doEndTag();

        assertEquals(normalize("<ul class=\"errorMessage\"><li><span><p>hey</p></span></li></ul>", true),
                normalize(writer.toString(), true));
    }

    public void testHaveActionErrors() throws Exception {

        ActionErrorTag tag = new ActionErrorTag();
        tag.setId("someid");
        ((InternalActionSupport)action).setHasActionErrors(true);
        tag.setPageContext(pageContext);
        tag.doStartTag();
        tag.doEndTag();

        verify(ActionErrorTagTest.class.getResource("actionerror-2.txt"));
    }

    public void testNullError() throws Exception {

        ActionErrorTag tag = new ActionErrorTag();
        tag.setId("someid");
        ((InternalActionSupport)action).setHasActionErrors(true);
        ((InternalActionSupport)action).addActionError(null);
        tag.setPageContext(pageContext);
        tag.doStartTag();
        tag.doEndTag();

        verify(ActionErrorTagTest.class.getResource("actionerror-2.txt"));
    }

     public void testEmptyErrorList() throws Exception {

        ActionErrorTag tag = new ActionErrorTag();
        tag.setId("someid");
        ((InternalActionSupport)action).setHasActionErrors(true);
        ((InternalActionSupport)action).setJustNullElement(true);
        tag.setPageContext(pageContext);
        tag.doStartTag();
        tag.doEndTag();

        assertTrue(StringUtils.isBlank(writer.toString()));
    }


    public Action getAction() {
        return new InternalActionSupport();
    }


    public class InternalActionSupport extends ActionSupport {

        private static final long serialVersionUID = -4777466640658557661L;

        private boolean yesActionErrors;
        private boolean justNullElement;

        public void setJustNullElement(boolean justNullElement) {
            this.justNullElement = justNullElement;
        }

        public void setHasActionErrors(boolean aYesActionErrors) {
            yesActionErrors = aYesActionErrors;
        }

        public boolean hasActionErrors() {
            return yesActionErrors;
        }

        public Collection getActionErrors() {
             if (justNullElement) {
                return Arrays.asList(null);
            } else if (yesActionErrors) {
                List errors = new ArrayList();
                errors.add("action error number 1");
                errors.add("action error number 2");
                errors.add("action error number 3");
                return errors;
            }
            else {
                return Collections.EMPTY_LIST;
            }
        }
    }
}
