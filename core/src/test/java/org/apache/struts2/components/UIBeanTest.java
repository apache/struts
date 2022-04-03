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
package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateEngine;
import org.apache.struts2.components.template.TemplateEngineManager;
import org.apache.struts2.dispatcher.StaticContentLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.opensymphony.xwork2.security.DefaultNotExcludedAcceptedPatternsCheckerTest.NO_EXCLUSION_ACCEPT_ALL_PATTERNS_CHECKER;

public class UIBeanTest extends StrutsInternalTestCase {

    public void testPopulateComponentHtmlId1() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        Form form = new Form(stack, req, res);
        form.getParameters().put("id", "formId");

        TextField txtFld = new TextField(stack, req, res);
        txtFld.setId("txtFldId");

        txtFld.populateComponentHtmlId(form);

        assertEquals("txtFldId", txtFld.getParameters().get("id"));
    }

    public void testPopulateComponentHtmlIdWithOgnl() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        Form form = new Form(stack, req, res);
        form.getParameters().put("id", "formId");

        TextField txtFld = new TextField(stack, req, res);
        txtFld.setName("txtFldName%{'1'}");

        txtFld.populateComponentHtmlId(form);

        assertEquals("formId_txtFldName1", txtFld.getParameters().get("id"));
    }

    public void testPopulateComponentHtmlId2() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        Form form = new Form(stack, req, res);
        form.getParameters().put("id", "formId");

        TextField txtFld = new TextField(stack, req, res);
        txtFld.setName("txtFldName");

        txtFld.populateComponentHtmlId(form);

        assertEquals("formId_txtFldName", txtFld.getParameters().get("id"));
    }

    public void testPopulateComponentHtmlWithoutNameAndId() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        Form form = new Form(stack, req, res);
        form.getParameters().put("id", "formId");

        TextField txtFld = new TextField(stack, req, res);

        txtFld.populateComponentHtmlId(form);

        assertNull(txtFld.getParameters().get("id"));
    }

    public void testEscape() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        UIBean bean = new UIBean(stack, req, res) {
            protected String getDefaultTemplate() {
                return null;
            }
        };

        assertEquals(bean.escape("hello[world"), "hello_world");
        assertEquals(bean.escape("hello.world"), "hello_world");
        assertEquals(bean.escape("hello]world"), "hello_world");
        assertEquals(bean.escape("hello!world"), "hello_world");
        assertEquals(bean.escape("hello!@#$%^&*()world"), "hello__________world");
    }

    public void testEscapeId() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        Form form = new Form(stack, req, res);
        form.getParameters().put("id", "formId");

        TextField txtFld = new TextField(stack, req, res);
        txtFld.setName("foo/bar");
        txtFld.populateComponentHtmlId(form);
        assertEquals("formId_foo_bar", txtFld.getParameters().get("id"));
    }

    public void testGetThemeFromForm() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        Form form = new Form(stack, req, res);
        form.setTheme("foo");

        TextField txtFld = new TextField(stack, req, res);
        assertEquals("foo", txtFld.getTheme());
    }

    public void testGetThemeFromContext() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Map<String, Object> context = Collections.singletonMap("theme", "bar");
        ActionContext.getContext().put("attr", context);

        TextField txtFld = new TextField(stack, req, res);
        assertEquals("bar", txtFld.getTheme());
    }

    public void testGetThemeFromContextNonString() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Map<String, Object> context = Collections.singletonMap("theme", 12);
        ActionContext.getContext().put("attr", context);

        TextField txtFld = new TextField(stack, req, res);
        assertEquals("12", txtFld.getTheme());
    }

    public void testMergeTemplateNullEngineException() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        //templateEngineManager that returns null as TemplateEngine
        TemplateEngineManager templateEngineManager = new TemplateEngineManager() {
            public TemplateEngine getTemplateEngine(Template template, String templateTypeOverride) {
                return null;
            }
        };
        TextField txtFld = new TextField(stack, req, res);

        txtFld.setTemplateEngineManager(templateEngineManager);

        try {
            txtFld.mergeTemplate(null, new Template(null, null, null));
            fail("Exception not thrown");
        } catch(final Exception e){
            assertTrue(e instanceof ConfigurationException);
        }
    }

    public void testBuildTemplate() {
        String defaultTemplateName = "default";
        String customTemplateName = "custom";
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        TextField txtFld = new TextField(stack, req, res);
        
        Template defaultTemplate = txtFld.buildTemplateName(null, defaultTemplateName);
        Template customTemplate = txtFld.buildTemplateName(customTemplateName, defaultTemplateName);

        assertEquals(defaultTemplateName, defaultTemplate.getName());
        assertEquals(customTemplateName, customTemplate.getName());
    }

    public void testGetTemplateDirExplicit() {
        String explicitTemplateDir = "explicitTemplateDirectory";
        String attrTemplateDir = "attrTemplateDirectory";
        String defaultTemplateDir = "defaultTemplateDirectory";
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Map<String, Object> context = Collections.singletonMap("templateDir", attrTemplateDir);
        ActionContext.getContext().put("attr", context);

        TextField txtFld = new TextField(stack, req, res);
        txtFld.setTemplateDir(explicitTemplateDir);
        txtFld.setDefaultTemplateDir(defaultTemplateDir);

        assertEquals(explicitTemplateDir, txtFld.getTemplateDir());
    }

    public void testGetTemplateDirAttr() {
        String attrTemplateDir = "attrTemplateDirectory";
        String defaultTemplateDir = "defaultTemplateDirectory";
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Map<String, Object> context = Collections.singletonMap("templateDir", attrTemplateDir);
        ActionContext.getContext().put("attr", context);

        TextField txtFld = new TextField(stack, req, res);
        txtFld.setDefaultTemplateDir(defaultTemplateDir);

        assertEquals(attrTemplateDir, txtFld.getTemplateDir());
    }

    public void testGetTemplateDirDefault() {
        String defaultTemplateDir = "defaultTemplateDirectory";
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        TextField txtFld = new TextField(stack, req, res);
        txtFld.setDefaultTemplateDir(defaultTemplateDir);

        assertEquals(defaultTemplateDir, txtFld.getTemplateDir());
    }

    public void testGetTemplateDirNoneSet() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        TextField txtFld = new TextField(stack, req, res);

        assertEquals("template", txtFld.getTemplateDir());
    }

    public void testSetAccesskey() {
        String accesskeyValue = "myAccesskey";
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        TextField txtFld = new TextField(stack, req, res);
        txtFld.setAccesskey(accesskeyValue);
        txtFld.evaluateParams();

        assertEquals(accesskeyValue, txtFld.getParameters().get("accesskey"));
    }

    public void testValueParameterEvaluation() {
        String value = "myValue";
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        TextField txtFld = new TextField(stack, req, res);
        txtFld.addParameter("value", value);
        txtFld.evaluateParams();

        assertEquals(value, txtFld.getParameters().get("nameValue"));
    }

    public void testValueParameterRecursion() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        stack.push(new Object() {
            public String getMyValue() {
                return "%{myBad}";
            }
            public String getMyBad() {
                throw new IllegalStateException("Recursion detected!");
            }
        });

        TextField txtFld = new TextField(stack, req, res);
        container.inject(txtFld);
        txtFld.setName("%{myValue}");
        txtFld.evaluateParams();

        assertEquals("%{myBad}", txtFld.getParameters().get("nameValue"));
        assertEquals("%{myBad}", txtFld.getParameters().get("name"));
    }

    public void testValueNameParameterNotAccepted() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        stack.push(new Object() {
            public String getMyValueName() {
                return "getMyValue()";
            }
            public String getMyValue() {
                return "value";
            }
        });

        TextField txtFld = new TextField(stack, req, res);
        container.inject(txtFld);
        txtFld.setName("%{myValueName}");
        txtFld.evaluateParams();
        assertEquals("getMyValue()", txtFld.getParameters().get("name"));
        assertEquals("getMyValue()", txtFld.getParameters().get("nameValue"));

        txtFld.setNotExcludedAcceptedPatterns(NO_EXCLUSION_ACCEPT_ALL_PATTERNS_CHECKER);
        txtFld.evaluateParams();
        assertEquals("getMyValue()", txtFld.getParameters().get("name"));
        assertEquals("value", txtFld.getParameters().get("nameValue"));
    }

    public void testValueNameParameterGetterAccepted() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        stack.push(new Object() {
            public String getMyValue() {
                return "value";
            }
        });

        TextField txtFld = new TextField(stack, req, res);
        container.inject(txtFld);
        txtFld.setName("getMyValue()");
        txtFld.evaluateParams();
        assertEquals("getMyValue()", txtFld.getParameters().get("name"));
        assertEquals("value", txtFld.getParameters().get("nameValue"));
    }

    public void testSetClass() {
        String cssClass = "insertCssClassHere";
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        TextField txtFld = new TextField(stack, req, res);
        txtFld.setClass(cssClass);
        txtFld.evaluateParams();

        assertEquals(cssClass, txtFld.getParameters().get("cssClass"));
    }

    public void testSetStyle() {
        String cssStyle = "insertCssStyleHere";
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        TextField txtFld = new TextField(stack, req, res);
        txtFld.setStyle(cssStyle);
        txtFld.evaluateParams();

        assertEquals(cssStyle, txtFld.getParameters().get("cssStyle"));
    }

    public void testNonce() {
        String nonceVal = "r4nd0m";
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        ActionContext actionContext = stack.getActionContext();
        Map<String, Object> session = new HashMap<>();
        session.put("nonce", nonceVal);
        actionContext.withSession(session);

        DoubleSelect dblSelect = new DoubleSelect(stack, req, res);
        dblSelect.evaluateParams();

        assertEquals(nonceVal, dblSelect.getParameters().get("nonce"));
    }

    public void testSetNullUiStaticContentPath() {
        // given
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        TextField field = new TextField(stack, req, res);

        // when
        field.setStaticContentPath(null);
        // then
        assertEquals(StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH, field.uiStaticContentPath);

        // when
        field.setStaticContentPath(" ");
        // then
        assertEquals(StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH, field.uiStaticContentPath);

        // when
        field.setStaticContentPath("content");
        // then
        assertEquals("/content", field.uiStaticContentPath);

        // when
        field.setStaticContentPath("/content");
        // then
        assertEquals("/content", field.uiStaticContentPath);

        // when
        field.setStaticContentPath("/content/");
        // then
        assertEquals("/content", field.uiStaticContentPath);
    }

}
