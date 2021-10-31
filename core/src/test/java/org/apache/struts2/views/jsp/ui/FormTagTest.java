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

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.TestAction;
import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;
import org.apache.struts2.views.jsp.AbstractUITagTest;
import org.apache.struts2.views.jsp.ActionTag;
import org.easymock.EasyMock;

import java.util.HashMap;


/**
 * FormTagTest
 */
public class FormTagTest extends AbstractUITagTest {


    public void testFormWithActionAttributeContainingQueryString() throws Exception {
        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("testAction?paramone=one&paramtwo=two");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");

        stack.getActionContext().getSession().put("nonce", "r4nd0m");

        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-26.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithActionAttributeContainingQueryString_clearTagStateSet() throws Exception {
        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("testAction?paramone=one&paramtwo=two");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");

        stack.getActionContext().getSession().put("nonce", "r4nd0m");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-26.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithActionAttributeContainingBothActionAndMethod() throws Exception {
        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("testAction");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-9.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

        public void testFormWithActionAttributeContainingBothActionAndMethod_clearTagStateSet() throws Exception {
        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("testAction");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-9.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithoutContext() throws Exception {
        request.setupGetContext("somecontext");

        FormTag tag = new FormTag();
        tag.setTheme("xhtml");
        tag.setPageContext(pageContext);
        tag.setAction("testAction");
        tag.setIncludeContext(false);
        tag.doStartTag();
        tag.doEndTag();


        verify(FormTag.class.getResource("Formtag-14.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithoutContext_clearTagStateSet() throws Exception {
        request.setupGetContext("somecontext");

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setTheme("xhtml");
        tag.setPageContext(pageContext);
        tag.setAction("testAction");
        tag.setIncludeContext(false);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();


        verify(FormTag.class.getResource("Formtag-14.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithContext() throws Exception {
        request.setupGetContext("/testNamespace");

        FormTag tag = new FormTag();
        tag.setTheme("xhtml");
        tag.setPageContext(pageContext);
        tag.setAction("testAction");
        tag.doStartTag();
        tag.doEndTag();


        verify(FormTag.class.getResource("Formtag-13.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithContext_clearTagStateSet() throws Exception {
        request.setupGetContext("/testNamespace");

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setTheme("xhtml");
        tag.setPageContext(pageContext);
        tag.setAction("testAction");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();


        verify(FormTag.class.getResource("Formtag-13.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithActionAttributeContainingBothActionAndDMIMethod() throws Exception {
        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("testAction!testMethod");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");

        ((DefaultActionMapper) container.getInstance(ActionMapper.class)).setAllowDynamicMethodCalls("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-23.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }    

    public void testFormWithActionAttributeContainingBothActionAndDMIMethod_clearTagStateSet() throws Exception {
        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("testAction!testMethod");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");

        ((DefaultActionMapper)container.getInstance(ActionMapper.class)).setAllowDynamicMethodCalls("true");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-23.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithFocusElement() throws Exception {
        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setTheme("xhtml");
        tag.setPageContext(pageContext);
        tag.setAction("testAction");
        tag.setFocusElement("felement");
        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-12.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithFocusElement_clearTagStateSet() throws Exception {
        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setTheme("xhtml");
        tag.setPageContext(pageContext);
        tag.setAction("testAction");
        tag.setFocusElement("felement");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-12.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithActionAttributeContainingBothActionAndMethodAndNamespace() throws Exception {
        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setNamespace("/testNamespace");
        tag.setMethod("post");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("testNamespaceAction");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");

        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-10.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithActionAttributeContainingBothActionAndMethodAndNamespace_clearTagStateSet() throws Exception {
        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setNamespace("/testNamespace");
        tag.setMethod("post");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("testNamespaceAction");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-10.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testForm() throws Exception {

        request.setupGetServletPath("/testAction");

        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("myAction");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.setId("myid");

        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-1.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testForm_clearTagStateSet() throws Exception {

        request.setupGetServletPath("/testAction");

        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("myAction");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.setId("myid");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-1.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormId() throws Exception {

        request.setupGetServletPath("/testAction");

        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setMethod("post");
        tag.setAction("myAction");
        tag.setId("myid-%{foo}");

        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-29.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormId_clearTagStateSet() throws Exception {

        request.setupGetServletPath("/testAction");

        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setMethod("post");
        tag.setAction("myAction");
        tag.setId("myid-%{foo}");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-29.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormNoNameOrId() throws Exception {

        request.setupGetServletPath("/testAction");

        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setMethod("post");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("myAction");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");

        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-25.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

     public void testFormNoNameOrId_clearTagStateSet() throws Exception {

        request.setupGetServletPath("/testAction");

        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setMethod("post");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("myAction");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-25.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * This test with form tag validation enabled. Js validation script will appear
     * cause action submited by the form is intercepted by validation interceptor which
     * "include" all methods.
     */
    public void testFormWithCustomOnsubmitEnabledWithValidateEnabled1() throws Exception {

        prepareMockInvocation();

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAction("doubleValidationAction");
        tag.setAcceptcharset("UTF-8");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.setValidate("true");
        tag.setNamespace("");

        UpDownSelectTag t = new UpDownSelectTag();
        t.setPageContext(pageContext);
        t.setName("myUpDownSelectTag");
        t.setList("{}");

        tag.doStartTag();
        t.doStartTag();
        t.doEndTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-2.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        UpDownSelectTag freshParamTag = new UpDownSelectTag();
        freshParamTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(t, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * This test with form tag validation enabled. Js validation script will appear
     * cause action submited by the form is intercepted by validation interceptor which
     * "include" all methods.
     */
    public void testFormWithCustomOnsubmitEnabledWithValidateEnabled1_clearTagStateSet() throws Exception {

        prepareMockInvocation();

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAction("doubleValidationAction");
        tag.setAcceptcharset("UTF-8");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.setValidate("true");
        tag.setNamespace("");

        UpDownSelectTag t = new UpDownSelectTag();
        t.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        t.setPageContext(pageContext);
        t.setName("myUpDownSelectTag");
        t.setList("{}");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        t.doStartTag();
        setComponentTagClearTagState(t, true);  // Ensure component tag state clearing is set true (to match tag).
        t.doEndTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-2.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        UpDownSelectTag freshParamTag = new UpDownSelectTag();
        freshParamTag.setPerformClearTagStateForTagPoolingServers(true);
        freshParamTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(t, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * This test with form tag validation enabled. Js validation script will not appear
     * cause action submited by the form is intercepted by validation interceptor which
     * "excludes" all methods.
     */
    public void testFormWithCustomOnsubmitEnabledWithValidateEnabled2() throws Exception {
        prepareMockInvocation();

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAction("testAction");
        tag.setAcceptcharset("UTF-8");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.setValidate("true");
        tag.setNamespace("");

        UpDownSelectTag t = new UpDownSelectTag();
        t.setPageContext(pageContext);
        t.setName("myUpDownSelectTag");
        t.setList("{}");

        tag.doStartTag();
        t.doStartTag();
        t.doEndTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-11.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        UpDownSelectTag freshParamTag = new UpDownSelectTag();
        freshParamTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(t, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * This test with form tag validation enabled. Js validation script will not appear
     * cause action submited by the form is intercepted by validation interceptor which
     * "excludes" all methods.
     */
    public void testFormWithCustomOnsubmitEnabledWithValidateEnabled2_clearTagStateSet() throws Exception {
        prepareMockInvocation();

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAction("testAction");
        tag.setAcceptcharset("UTF-8");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.setValidate("true");
        tag.setNamespace("");

        UpDownSelectTag t = new UpDownSelectTag();
        t.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        t.setPageContext(pageContext);
        t.setName("myUpDownSelectTag");
        t.setList("{}");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        t.doStartTag();
        setComponentTagClearTagState(t, true);  // Ensure component tag state clearing is set true (to match tag).
        t.doEndTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-11.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        UpDownSelectTag freshParamTag = new UpDownSelectTag();
        freshParamTag.setPerformClearTagStateForTagPoolingServers(true);
        freshParamTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(t, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * Tests the numbers are formatted correctly to not break the javascript
     */
    public void testFormWithCustomOnsubmitEnabledWithValidateEnabled3() throws Exception {

        prepareMockInvocation();
        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAction("doubleValidationAction");
        tag.setAcceptcharset("UTF-8");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.setValidate("true");
        tag.setNamespace("");

        UpDownSelectTag t = new UpDownSelectTag();
        t.setPageContext(pageContext);
        t.setName("myUpDownSelectTag");
        t.setList("{}");

        tag.doStartTag();
        tag.getComponent().getParameters().put("actionClass", IntValidationAction.class);
        t.doStartTag();
        t.doEndTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-22.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        UpDownSelectTag freshParamTag = new UpDownSelectTag();
        freshParamTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(t, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * Tests the numbers are formatted correctly to not break the javascript
     */
    public void testFormWithCustomOnsubmitEnabledWithValidateEnabled3_clearTagStateSet() throws Exception {

        prepareMockInvocation();
        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAction("doubleValidationAction");
        tag.setAcceptcharset("UTF-8");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.setValidate("true");
        tag.setNamespace("");

        UpDownSelectTag t = new UpDownSelectTag();
        t.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        t.setPageContext(pageContext);
        t.setName("myUpDownSelectTag");
        t.setList("{}");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.getComponent().getParameters().put("actionClass", IntValidationAction.class);
        t.doStartTag();
        setComponentTagClearTagState(t, true);  // Ensure component tag state clearing is set true (to match tag).
        t.doEndTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-22.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        UpDownSelectTag freshParamTag = new UpDownSelectTag();
        freshParamTag.setPerformClearTagStateForTagPoolingServers(true);
        freshParamTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(t, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

/**
     * Tests the numbers are formatted correctly to not break the javascript, using doubles
     */
    public void testFormWithCustomOnsubmitEnabledWithValidateEnabled4() throws Exception {
        prepareMockInvocation();

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAction("doubleValidationAction");
        tag.setAcceptcharset("UTF-8");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.setValidate("true");
        tag.setNamespace("");

        UpDownSelectTag t = new UpDownSelectTag();
        t.setPageContext(pageContext);
        t.setName("myUpDownSelectTag");
        t.setList("{}");

        tag.doStartTag();
        tag.getComponent().getParameters().put("actionClass", DoubleValidationAction.class);
        t.doStartTag();
        t.doEndTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-24.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        UpDownSelectTag freshParamTag = new UpDownSelectTag();
        freshParamTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(t, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

/**
     * Tests the numbers are formatted correctly to not break the javascript, using doubles
     */
    public void testFormWithCustomOnsubmitEnabledWithValidateEnabled4_clearTagStateSet() throws Exception {
        prepareMockInvocation();

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAction("doubleValidationAction");
        tag.setAcceptcharset("UTF-8");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.setValidate("true");
        tag.setNamespace("");

        UpDownSelectTag t = new UpDownSelectTag();
        t.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        t.setPageContext(pageContext);
        t.setName("myUpDownSelectTag");
        t.setList("{}");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.getComponent().getParameters().put("actionClass", DoubleValidationAction.class);
        t.doStartTag();
        setComponentTagClearTagState(t, true);  // Ensure component tag state clearing is set true (to match tag).
        t.doEndTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-24.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        UpDownSelectTag freshParamTag = new UpDownSelectTag();
        freshParamTag.setPerformClearTagStateForTagPoolingServers(true);
        freshParamTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(t, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    private void prepareMockInvocation() throws Exception {
        ActionConfig config = new ActionConfig.Builder("", "name", "").build();
        ActionInvocation invocation = EasyMock.createNiceMock(ActionInvocation.class);
        ActionProxy proxy = EasyMock.createNiceMock(ActionProxy.class);

        EasyMock.expect(invocation.getProxy()).andReturn(proxy).anyTimes();
        EasyMock.expect(invocation.getAction()).andReturn(null).anyTimes();
        EasyMock.expect(invocation.invoke()).andReturn(Action.SUCCESS).anyTimes();
        EasyMock.expect(proxy.getMethod()).andReturn("execute").anyTimes();
        EasyMock.expect(proxy.getConfig()).andReturn(config).anyTimes();

        EasyMock.replay(invocation);
        EasyMock.replay(proxy);

        ActionContext.getContext().withActionInvocation(invocation);
    }

    /**
     * This test with form tag validation disabled.
     */
    public void testFormWithCustomOnsubmitEnabledWithValidateDisabled() throws Exception {
        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAction("myAction");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.setValidate("false");

        UpDownSelectTag t = new UpDownSelectTag();
        t.setPageContext(pageContext);
        t.setName("myUpDownSelectTag");
        t.setList("{}");

        tag.doStartTag();
        t.doStartTag();
        t.doEndTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-6.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        UpDownSelectTag freshParamTag = new UpDownSelectTag();
        freshParamTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(t, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * This test with form tag validation disabled.
     */
    public void testFormWithCustomOnsubmitEnabledWithValidateDisabled_clearTagStateSet() throws Exception {
        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAction("myAction");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");
        tag.setValidate("false");

        UpDownSelectTag t = new UpDownSelectTag();
        t.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        t.setPageContext(pageContext);
        t.setName("myUpDownSelectTag");
        t.setList("{}");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        t.doStartTag();
        setComponentTagClearTagState(t, true);  // Ensure component tag state clearing is set true (to match tag).
        t.doEndTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-6.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        UpDownSelectTag freshParamTag = new UpDownSelectTag();
        freshParamTag.setPerformClearTagStateForTagPoolingServers(true);
        freshParamTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(t, freshParamTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * <p>
     * Testing that this:
     * </p>
     * <pre>
     * &lt;a:form name=&quot;'myForm'&quot; namespace=&quot;'/testNamespace'&quot; action=&quot;'testNamespaceAction'&quot; method=&quot;'post'&quot;&gt;
     * </pre>
     * <p>
     * doesn't create an action of &quot;/testNamespace/testNamespaceAction.action&quot; when the &quot;struts.action.extension&quot;
     * config property is set to &quot;jspa&quot;.
     * </p>
     */
    public void testFormTagWithDifferentActionExtension() throws Exception {
        initDispatcher(new HashMap<String, String>() {{
            put(StrutsConstants.STRUTS_ACTION_EXTENSION, "jspa");
            put("configProviders", TestConfigurationProvider.class.getName());
        }});
        createMocks();
        request.setupGetServletPath("/testNamespace/testNamespaceAction");

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setNamespace("/testNamespace");
        tag.setAction("testNamespaceAction");
        tag.setMethod("post");
        tag.setName("myForm");

        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-5.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * <p>
     * Testing that this:
     * </p>
     * <pre>
     * &lt;a:form name=&quot;'myForm'&quot; namespace=&quot;'/testNamespace'&quot; action=&quot;'testNamespaceAction'&quot; method=&quot;'post'&quot;&gt;
     * </pre>
     * <p>
     * doesn't create an action of &quot;/testNamespace/testNamespaceAction.action&quot; when the &quot;struts.action.extension&quot;
     * config property is set to &quot;jspa&quot;.
     * </p>
     */
    public void testFormTagWithDifferentActionExtension_clearTagStateSet() throws Exception {
        initDispatcher(new HashMap<String,String>(){{ 
            put(StrutsConstants.STRUTS_ACTION_EXTENSION, "jspa");
            put("configProviders", TestConfigurationProvider.class.getName());
        }});
        createMocks();
        request.setupGetServletPath("/testNamespace/testNamespaceAction");

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setNamespace("/testNamespace");
        tag.setAction("testNamespaceAction");
        tag.setMethod("post");
        tag.setName("myForm");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-5.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * Testing that this: <br>
     * &lt;a:form name=&quot;'myForm'&quot; action=&quot;'/testNamespace/testNamespaceAction.jspa'&quot; method=&quot;'post'&quot;&gt;
     * <br>
     * doesn't create an action of &quot;/testNamespace/testNamespaceAction.action&quot;
     */
    public void testFormTagWithDifferentActionExtensionHardcoded() throws Exception {
        request.setupGetServletPath("/testNamespace/testNamespaceAction");

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setAction("/testNamespace/testNamespaceAction.jspa");
        tag.setMethod("post");
        tag.setName("myForm");

        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-5.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    /**
     * Testing that this: <br>
     * &lt;a:form name=&quot;'myForm'&quot; action=&quot;'/testNamespace/testNamespaceAction.jspa'&quot; method=&quot;'post'&quot;&gt;
     * <br>
     * doesn't create an action of &quot;/testNamespace/testNamespaceAction.action&quot;
     */
    public void testFormTagWithDifferentActionExtensionHardcoded_clearTagStateSet() throws Exception {
        request.setupGetServletPath("/testNamespace/testNamespaceAction");

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setAction("/testNamespace/testNamespaceAction.jspa");
        tag.setMethod("post");
        tag.setName("myForm");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-5.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithNamespaceDefaulting() throws Exception {
        request.setupGetServletPath("/testNamespace/testNamespaceAction");

        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAction("testNamespaceAction");

        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-3.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithNamespaceDefaulting_clearTagStateSet() throws Exception {
        request.setupGetServletPath("/testNamespace/testNamespaceAction");

        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("post");
        tag.setAction("testNamespaceAction");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-3.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormTagForStackOverflowException1() throws Exception {
        request.setRequestURI("/testAction");

        FormTag form1 = new FormTag();
        form1.setPageContext(pageContext);
        form1.doStartTag();

        assertEquals(form1.getComponent().getComponentStack().size(), 1);

        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setName("testAction");
        tag.doStartTag();

        assertEquals(tag.getComponent().getComponentStack().size(), 2);

        tag.doEndTag();

        assertEquals(form1.getComponent().getComponentStack().size(), 1);

        form1.doEndTag();

        assertNull(form1.getComponent()); // component is removed after end tag

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPageContext(pageContext);
        // FormTag has no non=default state set here, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form1, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormTagForStackOverflowException1_clearTagStateSet() throws Exception {
        request.setRequestURI("/testAction");

        FormTag form1 = new FormTag();
        form1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        form1.setPageContext(pageContext);
        form1.doStartTag();
        setComponentTagClearTagState(form1, true);  // Ensure component tag state clearing is set true (to match tag).

        assertEquals(form1.getComponent().getComponentStack().size(), 1);

        ActionTag tag = new ActionTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("testAction");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).

        assertEquals(tag.getComponent().getComponentStack().size(), 2);

        tag.doEndTag();

        assertEquals(form1.getComponent().getComponentStack().size(), 1);

        form1.doEndTag();

        assertNull(form1.getComponent()); // component is removed after end tag

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPerformClearTagStateForTagPoolingServers(true);
        freshFormTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form1, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormTagForStackOverflowException2() throws Exception {
        request.setRequestURI("/requestUri");

        FormTag form1 = new FormTag();
        form1.setPageContext(pageContext);
        form1.doStartTag();

        assertEquals(form1.getComponent().getComponentStack().size(), 1);

        FormTag form2 = new FormTag();
        form2.setPageContext(pageContext);
        form2.doStartTag();

        assertEquals(form2.getComponent().getComponentStack().size(), 2);

        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setName("testAction");
        tag.doStartTag();

        assertEquals(tag.getComponent().getComponentStack().size(), 3);

        tag.doEndTag();

        assertEquals(form2.getComponent().getComponentStack().size(), 2);

        form2.doEndTag();

        assertEquals(form1.getComponent().getComponentStack().size(), 1);

        form1.doEndTag();

        assertNull(form1.getComponent()); // component is removed after end tag

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPageContext(pageContext);
         // FormTag has no non=default state set here, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form1, freshFormTag));
        // FormTag has no non=default state set here, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form2, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormTagForStackOverflowException2_clearTagStateSet() throws Exception {
        request.setRequestURI("/requestUri");

        FormTag form1 = new FormTag();
        form1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        form1.setPageContext(pageContext);
        form1.doStartTag();
        setComponentTagClearTagState(form1, true);  // Ensure component tag state clearing is set true (to match tag).

        assertEquals(form1.getComponent().getComponentStack().size(), 1);

        FormTag form2 = new FormTag();
        form2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        form2.setPageContext(pageContext);
        form2.doStartTag();
        setComponentTagClearTagState(form2, true);  // Ensure component tag state clearing is set true (to match tag).

        assertEquals(form2.getComponent().getComponentStack().size(), 2);

        ActionTag tag = new ActionTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("testAction");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).

        assertEquals(tag.getComponent().getComponentStack().size(), 3);

        tag.doEndTag();

        assertEquals(form2.getComponent().getComponentStack().size(), 2);

        form2.doEndTag();

        assertEquals(form1.getComponent().getComponentStack().size(), 1);

        form1.doEndTag();

        assertNull(form1.getComponent()); // component is removed after end tag

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPerformClearTagStateForTagPoolingServers(true);
        freshFormTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form1, freshFormTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form2, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormTagForStackOverflowException3() throws Exception {
        request.setRequestURI("/requestUri");

        FormTag form1 = new FormTag();
        form1.setPageContext(pageContext);
        form1.doStartTag();

        assertEquals(form1.getComponent().getComponentStack().size(), 1);

        FormTag form2 = new FormTag();
        form2.setPageContext(pageContext);
        form2.doStartTag();

        assertEquals(form2.getComponent().getComponentStack().size(), 2);

        FormTag form3 = new FormTag();
        form3.setPageContext(pageContext);
        form3.doStartTag();

        assertEquals(form3.getComponent().getComponentStack().size(), 3);

        ActionTag tag = new ActionTag();
        tag.setPageContext(pageContext);
        tag.setName("testAction");
        tag.doStartTag();

        assertEquals(tag.getComponent().getComponentStack().size(), 4);

        tag.doEndTag();

        assertEquals(form3.getComponent().getComponentStack().size(), 3);

        form3.doEndTag();

        assertEquals(form2.getComponent().getComponentStack().size(), 2);

        form2.doEndTag();

        assertEquals(form1.getComponent().getComponentStack().size(), 1);

        form1.doEndTag();

        assertNull(form1.getComponent()); // component is removed after end tag

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPageContext(pageContext);
        // FormTag has no non=default state set here, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form1, freshFormTag));
        // FormTag has no non=default state set here, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form2, freshFormTag));
        // FormTag has no non=default state set here, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form3, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormTagForStackOverflowException3_clearTagStateSet() throws Exception {
        request.setRequestURI("/requestUri");

        FormTag form1 = new FormTag();
        form1.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        form1.setPageContext(pageContext);
        form1.doStartTag();
        setComponentTagClearTagState(form1, true);  // Ensure component tag state clearing is set true (to match tag).

        assertEquals(form1.getComponent().getComponentStack().size(), 1);

        FormTag form2 = new FormTag();
        form2.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        form2.setPageContext(pageContext);
        form2.doStartTag();
        setComponentTagClearTagState(form2, true);  // Ensure component tag state clearing is set true (to match tag).

        assertEquals(form2.getComponent().getComponentStack().size(), 2);

        FormTag form3 = new FormTag();
        form3.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        form3.setPageContext(pageContext);
        form3.doStartTag();
        setComponentTagClearTagState(form3, true);  // Ensure component tag state clearing is set true (to match tag).

        assertEquals(form3.getComponent().getComponentStack().size(), 3);

        ActionTag tag = new ActionTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setName("testAction");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).

        assertEquals(tag.getComponent().getComponentStack().size(), 4);

        tag.doEndTag();

        assertEquals(form3.getComponent().getComponentStack().size(), 3);

        form3.doEndTag();

        assertEquals(form2.getComponent().getComponentStack().size(), 2);

        form2.doEndTag();

        assertEquals(form1.getComponent().getComponentStack().size(), 1);

        form1.doEndTag();

        assertNull(form1.getComponent()); // component is removed after end tag

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPerformClearTagStateForTagPoolingServers(true);
        freshFormTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form1, freshFormTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form2, freshFormTag));
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form3, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        ActionTag freshTag = new ActionTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormComponentIsRemoved() throws Exception {
        request.setRequestURI("/requestUri");

        FormTag form = new FormTag();
        form.setPageContext(pageContext);
        form.doStartTag();

        assertEquals(form.getComponent().getComponentStack().size(), 1);

        form.doEndTag();

        assertNull(form.getComponent());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        // FormTag has no non=default state set here, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form, freshTag));
    }

    public void testFormComponentIsRemoved_clearTagStateSet() throws Exception {
        request.setRequestURI("/requestUri");

        FormTag form = new FormTag();
        form.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        form.setPageContext(pageContext);
        form.doStartTag();
        setComponentTagClearTagState(form, true);  // Ensure component tag state clearing is set true (to match tag).

        assertEquals(form.getComponent().getComponentStack().size(), 1);

        form.doEndTag();

        assertNull(form.getComponent());

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form, freshTag));
    }

    public void testFormWithNoAction() throws Exception {
        request.setupGetServletPath("/");
        request.setupGetContextPath("/");
        request.setRequestURI("/foo.jsp");

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-4.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        // FormTag has no non=default state set here, so it compares as equal with the default tag clear state as well.
        assertTrue("Tag state after doEndTag() under default tag clear state is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithNoAction_clearTagStateSet() throws Exception {
        request.setupGetServletPath("/");
        request.setupGetContextPath("/");
        request.setRequestURI("/foo.jsp");

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-4.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithStaticAction() throws Exception {
        request.setupGetServletPath("/");
        request.setupGetContextPath("/");
        request.setRequestURI("/foo.jsp");

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setAction("test.html");
        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-7.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithStaticAction_clearTagStateSet() throws Exception {
        request.setupGetServletPath("/");
        request.setupGetContextPath("/");
        request.setRequestURI("/foo.jsp");

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setAction("test.html");
        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-7.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithActionAndExtension() throws Exception {
        request.setupGetServletPath("/BLA");

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setAction("/testNamespace/testNamespaceAction.jspa");
        tag.setMethod("post");
        tag.setName("myForm");

        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-8.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithActionAndExtension_clearTagStateSet() throws Exception {
        request.setupGetServletPath("/BLA");

        FormTag tag = new FormTag();
        tag.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        tag.setPageContext(pageContext);
        tag.setAction("/testNamespace/testNamespaceAction.jspa");
        tag.setMethod("post");
        tag.setName("myForm");

        tag.doStartTag();
        setComponentTagClearTagState(tag, true);  // Ensure component tag state clearing is set true (to match tag).
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-8.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshTag = new FormTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(tag, freshTag));
    }

    public void testFormWithTopLabelPosition() throws Exception {
        FormTag form = new FormTag();
        form.setTheme("xhtml");
        form.setAction("testAction");
        form.setPageContext(pageContext);
        form.setIncludeContext(false);
        form.setLabelposition("top");

        TextFieldTag text = new TextFieldTag();
        text.setPageContext(pageContext);
        text.setLabel("label");

        form.doStartTag();
        text.doStartTag();
        text.doEndTag();
        form.doEndTag();

        verify(FormTag.class.getResource("Formtag-27.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextFieldTag freshTag = new TextFieldTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(text, freshTag));
    }

    public void testFormWithTopLabelPosition_clearTagStateSet() throws Exception {
        FormTag form = new FormTag();
        form.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        form.setTheme("xhtml");
        form.setAction("testAction");
        form.setPageContext(pageContext);
        form.setIncludeContext(false);
        form.setLabelposition("top");

        TextFieldTag text = new TextFieldTag();
        text.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        text.setPageContext(pageContext);
        text.setLabel("label");

        form.doStartTag();
        setComponentTagClearTagState(form, true);  // Ensure component tag state clearing is set true (to match tag).
        text.doStartTag();
        setComponentTagClearTagState(text, true);  // Ensure component tag state clearing is set true (to match tag).
        text.doEndTag();
        form.doEndTag();

        verify(FormTag.class.getResource("Formtag-27.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPerformClearTagStateForTagPoolingServers(true);
        freshFormTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextFieldTag freshTag = new TextFieldTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(text, freshTag));
    }

    public void testFormWithTopLabelPositionWithElementOverride() throws Exception {
        FormTag form = new FormTag();
        form.setTheme("xhtml");
        form.setAction("testAction");
        form.setPageContext(pageContext);
        form.setIncludeContext(false);
        form.setLabelposition("left");

        TextFieldTag text = new TextFieldTag();
        text.setPageContext(pageContext);
        text.setLabel("label");
        text.setLabelposition("top");

        form.doStartTag();
        text.doStartTag();
        text.doEndTag();
        form.doEndTag();

        verify(FormTag.class.getResource("Formtag-27.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextFieldTag freshTag = new TextFieldTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(text, freshTag));
    }

    public void testFormWithTopLabelPositionWithElementOverride_clearTagStateSet() throws Exception {
        FormTag form = new FormTag();
        form.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        form.setTheme("xhtml");
        form.setAction("testAction");
        form.setPageContext(pageContext);
        form.setIncludeContext(false);
        form.setLabelposition("left");

        TextFieldTag text = new TextFieldTag();
        text.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        text.setPageContext(pageContext);
        text.setLabel("label");
        text.setLabelposition("top");

        form.doStartTag();
        setComponentTagClearTagState(form, true);  // Ensure component tag state clearing is set true (to match tag).
        text.doStartTag();
        setComponentTagClearTagState(text, true);  // Ensure component tag state clearing is set true (to match tag).
        text.doEndTag();
        form.doEndTag();

        verify(FormTag.class.getResource("Formtag-27.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPerformClearTagStateForTagPoolingServers(true);
        freshFormTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextFieldTag freshTag = new TextFieldTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(text, freshTag));
    }

    public void testFormWithTopLabelPositionCssXhtml() throws Exception {
        FormTag form = new FormTag();
        form.setTheme("css_xhtml");
        form.setAction("testAction");
        form.setPageContext(pageContext);
        form.setIncludeContext(false);
        form.setLabelposition("top");

        TextFieldTag text = new TextFieldTag();
        text.setPageContext(pageContext);
        text.setLabel("label");

        form.doStartTag();
        text.doStartTag();
        text.doEndTag();
        form.doEndTag();

        verify(FormTag.class.getResource("Formtag-28.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextFieldTag freshTag = new TextFieldTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(text, freshTag));
    }

    public void testFormWithTopLabelPositionCssXhtml_clearTagStateSet() throws Exception {
        FormTag form = new FormTag();
        form.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        form.setTheme("css_xhtml");
        form.setAction("testAction");
        form.setPageContext(pageContext);
        form.setIncludeContext(false);
        form.setLabelposition("top");

        TextFieldTag text = new TextFieldTag();
        text.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        text.setPageContext(pageContext);
        text.setLabel("label");

        form.doStartTag();
        setComponentTagClearTagState(form, true);  // Ensure component tag state clearing is set true (to match tag).
        text.doStartTag();
        setComponentTagClearTagState(text, true);  // Ensure component tag state clearing is set true (to match tag).
        text.doEndTag();
        form.doEndTag();

        verify(FormTag.class.getResource("Formtag-28.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPerformClearTagStateForTagPoolingServers(true);
        freshFormTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextFieldTag freshTag = new TextFieldTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(text, freshTag));
    }

    public void testFormWithTopLabelPositionWithElementOverrideCssXhtml() throws Exception {
        FormTag form = new FormTag();
        form.setTheme("css_xhtml");
        form.setAction("testAction");
        form.setPageContext(pageContext);
        form.setIncludeContext(false);
        form.setLabelposition("left");

        TextFieldTag text = new TextFieldTag();
        text.setPageContext(pageContext);
        text.setLabel("label");
        text.setLabelposition("top");

        form.doStartTag();
        text.doStartTag();
        text.doEndTag();
        form.doEndTag();

        verify(FormTag.class.getResource("Formtag-28.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextFieldTag freshTag = new TextFieldTag();
        freshTag.setPageContext(pageContext);
        assertFalse("Tag state after doEndTag() under default tag clear state is equal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(text, freshTag));
    }

    public void testFormWithTopLabelPositionWithElementOverrideCssXhtml_clearTagStateSet() throws Exception {
        FormTag form = new FormTag();
        form.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        form.setTheme("css_xhtml");
        form.setAction("testAction");
        form.setPageContext(pageContext);
        form.setIncludeContext(false);
        form.setLabelposition("left");

        TextFieldTag text = new TextFieldTag();
        text.setPerformClearTagStateForTagPoolingServers(true);  // Explicitly request tag state clearing.
        text.setPageContext(pageContext);
        text.setLabel("label");
        text.setLabelposition("top");

        form.doStartTag();
        setComponentTagClearTagState(form, true);  // Ensure component tag state clearing is set true (to match tag).
        text.doStartTag();
        setComponentTagClearTagState(text, true);  // Ensure component tag state clearing is set true (to match tag).
        text.doEndTag();
        form.doEndTag();

        verify(FormTag.class.getResource("Formtag-28.txt"));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        FormTag freshFormTag = new FormTag();
        freshFormTag.setPerformClearTagStateForTagPoolingServers(true);
        freshFormTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(form, freshFormTag));

        // Basic sanity check of clearTagStateForTagPoolingServers() behaviour for Struts Tags after doEndTag().
        TextFieldTag freshTag = new TextFieldTag();
        freshTag.setPerformClearTagStateForTagPoolingServers(true);
        freshTag.setPageContext(pageContext);
        assertTrue("Tag state after doEndTag() and explicit tag state clearing is inequal to new Tag with pageContext/parent set.  " +
                "May indicate that clearTagStateForTagPoolingServers() calls are not working properly.",
                strutsBodyTagsAreReflectionEqual(text, freshTag));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initDispatcher(new HashMap<String, String>() {{
            put("configProviders", TestConfigurationProvider.class.getName());
        }});
        createMocks();
    }
}
