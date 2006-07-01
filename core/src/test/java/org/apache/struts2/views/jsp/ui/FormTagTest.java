/*
 * $Id$
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

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.ObjectFactory;
import com.opensymphony.xwork.XWorkStatic;
import com.opensymphony.xwork.config.RuntimeConfiguration;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.InterceptorMapping;
import com.opensymphony.xwork.validator.ValidationInterceptor;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.TestAction;
import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.dispatcher.mapper.DefaultActionMapper;
import org.apache.struts2.views.jsp.AbstractUITagTest;
import org.apache.struts2.views.jsp.ActionTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * FormTagTest
 */
public class FormTagTest extends AbstractUITagTest {

    public void testFormWithActionAttributeContainingBothActionAndMethod() throws Exception {
        TestAction testAction = (TestAction) action;

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("POST");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("testAction!myLittleMethod");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");

        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-9.txt"));
    }


    public void testFormWithActionAttributeContainingBothActionAndMethodAndNamespace() throws Exception {
        TestAction testAction = (TestAction) action;

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setNamespace("/testNamespace");
        tag.setMethod("POST");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("testNamespaceAction!myLittleMethod");
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
        tag.setMethod("POST");
        tag.setAcceptcharset("UTF-8");
        tag.setAction("myAction");
        tag.setEnctype("myEncType");
        tag.setTitle("mytitle");
        tag.setOnsubmit("submitMe()");

        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-1.txt"));
    }

    /**
     * This test with form tag validation enabled. Js validation script will appear
     * cause action submited by the form is intercepted by validation interceptor which
     * "include" all methods.
     */
    public void testFormWithCustomOnsubmitEnabledWithValidateEnabled1() throws Exception {

        com.opensymphony.xwork.config.Configuration originalConfiguration = XWorkStatic.getConfigurationManager().getConfiguration();
        ObjectFactory originalObjectFactory = ObjectFactory.getObjectFactory();

        try {
            // used to determined if the form action needs js validation
            XWorkStatic.getConfigurationManager().setConfiguration(new com.opensymphony.xwork.config.impl.DefaultConfiguration() {
                public RuntimeConfiguration getRuntimeConfiguration() {
                    return new RuntimeConfiguration() {
                        public ActionConfig getActionConfig(String namespace, String name) {
                            ActionConfig actionConfig = new ActionConfig() {
                                public List getInterceptors() {
                                    List interceptors = new ArrayList();

                                    ValidationInterceptor validationInterceptor = new ValidationInterceptor();
                                    validationInterceptor.setIncludeMethods("*");

                                    InterceptorMapping interceptorMapping = new InterceptorMapping();
                                    interceptorMapping.setName("validation");
                                    interceptorMapping.setInterceptor(validationInterceptor);
                                    interceptors.add(interceptorMapping);

                                    return interceptors;
                                }
                            };
                            return actionConfig;
                        }

                        public Map getActionConfigs() {
                            return null;
                        }
                    };
                }
            });

            // used by form tag to get "actionClass" parameter
            ObjectFactory.setObjectFactory(new ObjectFactory() {
                public Class getClassInstance(String className) throws ClassNotFoundException {
                    if (DefaultActionMapper.class.getName().equals(className)) {
                        return DefaultActionMapper.class;
                    }
                    return ActionSupport.class;
                }
            });


            FormTag tag = new FormTag();
            tag.setPageContext(pageContext);
            tag.setName("myForm");
            tag.setMethod("POST");
            tag.setAction("myAction");
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
        finally {
            XWorkStatic.getConfigurationManager().setConfiguration(originalConfiguration);
            ObjectFactory.setObjectFactory(originalObjectFactory);
        }
    }


    /**
     * This test with form tag validation enabled. Js validation script will not appear
     * cause action submited by the form is intercepted by validation interceptor which
     * "excludes" all methods.
     */
    public void testFormWithCustomOnsubmitEnabledWithValidateEnabled2() throws Exception {

        com.opensymphony.xwork.config.Configuration originalConfiguration = XWorkStatic.getConfigurationManager().getConfiguration();
        ObjectFactory originalObjectFactory = ObjectFactory.getObjectFactory();

        try {
            // used to determined if the form action needs js validation
            XWorkStatic.getConfigurationManager().setConfiguration(new com.opensymphony.xwork.config.impl.DefaultConfiguration() {
                public RuntimeConfiguration getRuntimeConfiguration() {
                    return new RuntimeConfiguration() {
                        public ActionConfig getActionConfig(String namespace, String name) {
                            ActionConfig actionConfig = new ActionConfig() {
                                public List getInterceptors() {
                                    List interceptors = new ArrayList();

                                    ValidationInterceptor validationInterceptor = new ValidationInterceptor();
                                    validationInterceptor.setExcludeMethods("*");

                                    InterceptorMapping interceptorMapping = new InterceptorMapping();
                                    interceptorMapping.setName("validation");
                                    interceptorMapping.setInterceptor(validationInterceptor);
                                    interceptors.add(interceptorMapping);

                                    return interceptors;
                                }
                            };
                            return actionConfig;
                        }

                        public Map getActionConfigs() {
                            return null;
                        }
                    };
                }
            });

            // used by form tag to get "actionClass" parameter
            ObjectFactory.setObjectFactory(new ObjectFactory() {
                public Class getClassInstance(String className) throws ClassNotFoundException {
                    if (DefaultActionMapper.class.getName().equals(className)) {
                        return DefaultActionMapper.class;
                    }
                    return ActionSupport.class;
                }
            });


            FormTag tag = new FormTag();
            tag.setPageContext(pageContext);
            tag.setName("myForm");
            tag.setMethod("POST");
            tag.setAction("myAction");
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
        finally {
            XWorkStatic.getConfigurationManager().setConfiguration(originalConfiguration);
            ObjectFactory.setObjectFactory(originalObjectFactory);
        }
    }

    /**
     * This test with form tag validation disabled.
     */
    public void testFormWithCustomOnsubmitEnabledWithValidateDisabled() throws Exception {
        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setName("myForm");
        tag.setMethod("POST");
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
     * &lt;a:form name=&quot;'myForm'&quot; namespace=&quot;'/testNamespace'&quot; action=&quot;'testNamespaceAction'&quot; method=&quot;'POST'&quot;&gt;
     * <p/>
     * doesn't create an action of &quot;/testNamespace/testNamespaceAction.action&quot; when the &quot;struts.action.extension&quot;
     * config property is set to &quot;jspa&quot;.
     */
    public void testFormTagWithDifferentActionExtension() throws Exception {
        request.setupGetServletPath("/testNamespace/testNamespaceAction");
        String oldConfiguration = (String) Configuration.get(StrutsConstants.STRUTS_ACTION_EXTENSION);
        Configuration.set(StrutsConstants.STRUTS_ACTION_EXTENSION, "jspa");

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setNamespace("/testNamespace");
        tag.setAction("testNamespaceAction");
        tag.setMethod("POST");
        tag.setName("myForm");

        tag.doStartTag();
        tag.doEndTag();

        Configuration.set(StrutsConstants.STRUTS_ACTION_EXTENSION, oldConfiguration);

        verify(FormTag.class.getResource("Formtag-5.txt"));

        // set it back to the default
        Configuration.set(StrutsConstants.STRUTS_ACTION_EXTENSION, "action");
    }

    /**
     * Testing that this: <p>
     * &lt;a:form name=&quot;'myForm'&quot; action=&quot;'/testNamespace/testNamespaceAction.jspa'&quot; method=&quot;'POST'&quot;&gt;
     * <p/>
     * doesn't create an action of &quot;/testNamespace/testNamespaceAction.action&quot;
     */
    public void testFormTagWithDifferentActionExtensionHardcoded() throws Exception {
        request.setupGetServletPath("/testNamespace/testNamespaceAction");

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setAction("/testNamespace/testNamespaceAction.jspa");
        tag.setMethod("POST");
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
        tag.setMethod("POST");
        tag.setAction("testNamespaceAction");

        tag.doStartTag();
        tag.doEndTag();

        verify(FormTag.class.getResource("Formtag-3.txt"));
    }

    public void testFormTagForStackOverflowException1() throws Exception {
        request.setRequestURI("/requestUri");

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
        String oldConfiguration = (String) Configuration.get(StrutsConstants.STRUTS_ACTION_EXTENSION);
        Configuration.set(StrutsConstants.STRUTS_ACTION_EXTENSION, "jspa");

        FormTag tag = new FormTag();
        tag.setPageContext(pageContext);
        tag.setAction("/testNamespace/testNamespaceAction.jspa");
        tag.setMethod("POST");
        tag.setName("myForm");

        tag.doStartTag();
        tag.doEndTag();
        Configuration.set(StrutsConstants.STRUTS_ACTION_EXTENSION, oldConfiguration);

        verify(FormTag.class.getResource("Formtag-8.txt"));

        // set it back to the default
        Configuration.set(StrutsConstants.STRUTS_ACTION_EXTENSION, "action");

    }

    protected void setUp() throws Exception {
        super.setUp();
        XWorkStatic.getConfigurationManager().clearConfigurationProviders();
        XWorkStatic.getConfigurationManager().addConfigurationProvider(new TestConfigurationProvider());
        ActionContext.getContext().setValueStack(stack);
    }
}
