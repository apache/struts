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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


import java.util.Collections;
import java.util.Map;

public class UIBeanTest extends StrutsInternalTestCase {

    public void testPopulateComponentHtmlId1() throws Exception {
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

    public void testPopulateComponentHtmlIdWithOgnl() throws Exception {
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

    public void testPopulateComponentHtmlId2() throws Exception {
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

    public void testPopulateComponentHtmlWithoutNameAndId() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        Form form = new Form(stack, req, res);
        form.getParameters().put("id", "formId");

        TextField txtFld = new TextField(stack, req, res);

        txtFld.populateComponentHtmlId(form);

        assertEquals(null, txtFld.getParameters().get("id"));
    }

    public void testEscape() throws Exception {
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
        assertEquals(bean.escape("hello!world"), "hello!world");
        assertEquals(bean.escape("hello!@#$%^&*()world"), "hello!@#$%^&*()world");
    }

    public void testEscapeId() throws Exception {
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

    public void testGetThemeFromForm() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        Form form = new Form(stack, req, res);
        form.setTheme("foo");

        TextField txtFld = new TextField(stack, req, res);
        assertEquals("foo", txtFld.getTheme());
    }

    public void testGetThemeFromContext() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Map context = Collections.singletonMap("theme", "bar");
        ActionContext.getContext().put("attr", context);

        TextField txtFld = new TextField(stack, req, res);
        assertEquals("bar", txtFld.getTheme());
    }

    public void testGetThemeFromContextNonString() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Map context = Collections.singletonMap("theme", 12);
        ActionContext.getContext().put("attr", context);

        TextField txtFld = new TextField(stack, req, res);
        assertEquals("12", txtFld.getTheme());
    }

    public void testMergeTemplateNullEngineException() throws Exception {
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

    public void testBuildTemplate() throws Exception {
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

    public void testGetTemplateDirExplicit() throws Exception {
        String explicitTemplateDir = "explicitTemplateDirectory";
        String attrTemplateDir = "attrTemplateDirectory";
        String defaultTemplateDir = "defaultTemplateDirectory";
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Map context = Collections.singletonMap("templateDir", attrTemplateDir);
        ActionContext.getContext().put("attr", context);

        TextField txtFld = new TextField(stack, req, res);
        txtFld.setTemplateDir(explicitTemplateDir);
        txtFld.setDefaultTemplateDir(defaultTemplateDir);

        assertEquals(explicitTemplateDir, txtFld.getTemplateDir());
    }

    public void testGetTemplateDirAttr() throws Exception {
        String attrTemplateDir = "attrTemplateDirectory";
        String defaultTemplateDir = "defaultTemplateDirectory";
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        Map context = Collections.singletonMap("templateDir", attrTemplateDir);
        ActionContext.getContext().put("attr", context);

        TextField txtFld = new TextField(stack, req, res);
        txtFld.setDefaultTemplateDir(defaultTemplateDir);

        assertEquals(attrTemplateDir, txtFld.getTemplateDir());
    }

    public void testGetTemplateDirDefault() throws Exception {
        String defaultTemplateDir = "defaultTemplateDirectory";
        ValueStack stack = ActionContext.getContext().getValueStack();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        TextField txtFld = new TextField(stack, req, res);
        txtFld.setDefaultTemplateDir(defaultTemplateDir);

        assertEquals(defaultTemplateDir, txtFld.getTemplateDir());
    }

    public void testGetTemplateDirNoneSet() throws Exception {
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
}
