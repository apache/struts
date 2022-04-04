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

package org.apache.struts2.components;

import org.apache.struts2.StrutsInternalTestCase;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

/**
 *
 * @version $Date$ $Id$
 */
public class FormButtonTest extends StrutsInternalTestCase {

    public void testPopulateComponentHtmlId1() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        ValueStack stack = ActionContext.getContext().getValueStack();

        Form form = new Form(stack, req, res);
        form.getParameters().put("id", "formId");

        Submit submit = new Submit(stack, req, res);
        submit.setId("submitId");

        submit.populateComponentHtmlId(form);

        assertEquals("submitId", submit.getParameters().get("id"));
    }

    public void testPopulateComponentHtmlId2() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        ValueStack stack = ActionContext.getContext().getValueStack();

        Form form = new Form(stack, req, res);
        form.getParameters().put("id", "formId");

        Submit submit = new Submit(stack, req, res);
        submit.setName("submitName");

        submit.populateComponentHtmlId(form);

        assertEquals("formId_submitName", submit.getParameters().get("id"));
    }

    public void testPopulateComponentHtmlId3() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        ValueStack stack = ActionContext.getContext().getValueStack();

        Form form = new Form(stack, req, res);
        form.getParameters().put("id", "formId");

        Submit submit = new Submit(stack, req, res);
        submit.setAction("submitAction");
        submit.setMethod("submitMethod");

        submit.populateComponentHtmlId(form);

        assertEquals("formId_submitAction_submitMethod", submit.getParameters().get("id"));
    }

    public void testPopulateComponentHtmlId4() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        ValueStack stack = ActionContext.getContext().getValueStack();

        Submit submit = new Submit(stack, req, res);
        submit.setId("submitId");

        submit.populateComponentHtmlId(null);

        assertEquals("submitId", submit.getParameters().get("id"));
    }

    public void testPopulateComponentHtmlId5() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        ValueStack stack = ActionContext.getContext().getValueStack();

        Submit submit = new Submit(stack, req, res);
        submit.setName("submitName");

        submit.populateComponentHtmlId(null);

        assertEquals("submitName", submit.getParameters().get("id"));
    }

    public void testPopulateComponentHtmlId6() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        ValueStack stack = ActionContext.getContext().getValueStack();

        Submit submit = new Submit(stack, req, res);
        submit.setAction("submitAction");
        submit.setMethod("submitMethod");

        submit.populateComponentHtmlId(null);

        assertEquals("submitAction_submitMethod", submit.getParameters().get("id"));
    }
}
