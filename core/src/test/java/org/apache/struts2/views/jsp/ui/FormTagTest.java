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

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Scope.Strategy;
import com.opensymphony.xwork2.validator.ValidationInterceptor;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.TestAction;
import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.components.Form;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;
import org.apache.struts2.views.jsp.AbstractUITagTest;
import org.apache.struts2.views.jsp.ActionTag;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.apache.struts2.views.jsp.AbstractUITagTest.normalize;


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
        tag.doStartTag();
        tag.doEndTag();
        
        verify(FormTag.class.getResource("Formtag-26.txt"));
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
    }
    
    public void testFormWithActionAttributeContainingBothActionAndDMIMethod() throws Exception {
        FormTag tag = new FormTag();
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
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-23.txt"));
    }    

    public void testFormWithFocusElement() throws Exception {
        FormTag tag = new FormTag();
        tag.setTheme("xhtml");
        tag.setPageContext(pageContext);
        tag.setAction("testAction");
        tag.setFocusElement("felement");
        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-12.txt"));
    }

    public void testFormWithActionAttributeContainingBothActionAndMethodAndNamespace() throws Exception {
        FormTag tag = new FormTag();
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
    }


    public void testForm() throws Exception {

        request.setupGetServletPath("/testAction");

        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        FormTag tag = new FormTag();
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
    }

    private void prepareMockInvocation() throws Exception {
        ActionContext.getContext().setValueStack(stack);

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

        ActionContext.getContext().setActionInvocation(invocation);
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
    }


    /**
     * Testing that this: <p>
     * &lt;a:form name=&quot;'myForm'&quot; namespace=&quot;'/testNamespace'&quot; action=&quot;'testNamespaceAction'&quot; method=&quot;'post'&quot;&gt;
     * <p/>
     * doesn't create an action of &quot;/testNamespace/testNamespaceAction.action&quot; when the &quot;struts.action.extension&quot;
     * config property is set to &quot;jspa&quot;.
     */
    public void testFormTagWithDifferentActionExtension() throws Exception {
        initDispatcher(new HashMap<String,String>(){{ 
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
    }

    /**
     * Testing that this: <p>
     * &lt;a:form name=&quot;'myForm'&quot; action=&quot;'/testNamespace/testNamespaceAction.jspa'&quot; method=&quot;'post'&quot;&gt;
     * <p/>
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
    }


    public void testFormComponentIsRemoved() throws Exception {
        request.setRequestURI("/requestUri");

        FormTag form = new FormTag();
        form.setPageContext(pageContext);
        form.doStartTag();

        assertEquals(form.getComponent().getComponentStack().size(), 1);

        form.doEndTag();

        assertNull(form.getComponent());
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
