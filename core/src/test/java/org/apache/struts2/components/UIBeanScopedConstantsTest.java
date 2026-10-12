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

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionSupport;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.dispatcher.AttributeMap;
import org.apache.struts2.dispatcher.DispatcherConstants;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.util.ValueStack;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;

public class UIBeanScopedConstantsTest extends StrutsInternalTestCase {

    private ValueStack stack;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initDispatcherWithConfigs("struts-default.xml,org/apache/struts2/components/struts-scoped-ui.xml");
        stack = ActionContext.getContext().getValueStack();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    public void testPackageOverrideIsUsedWithoutTagAttribute() {
        enterPackage("admin");

        TextField txtFld = newTextField();

        assertEquals("css_xhtml", txtFld.getTheme());
        assertEquals("admin-template", txtFld.getTemplateDir());
    }

    public void testChildPackageInheritsOverride() {
        enterPackage("adminChild");

        TextField txtFld = newTextField();

        assertEquals("css_xhtml", txtFld.getTheme());
        assertEquals("admin-template", txtFld.getTemplateDir());
    }

    public void testGlobalValueWithoutOverride() {
        enterPackage("plain");

        TextField txtFld = newTextField();

        assertEquals("xhtml", txtFld.getTheme());
        assertEquals("template", txtFld.getTemplateDir());
    }

    public void testGlobalValueWithoutInvocation() {
        TextField txtFld = newTextField();

        assertEquals("xhtml", txtFld.getTheme());
        assertEquals("template", txtFld.getTemplateDir());
    }

    public void testTagAttributeWinsOverPackageOverride() {
        enterPackage("admin");

        TextField txtFld = newTextField();
        txtFld.setTheme("simple");
        txtFld.setTemplateDir("tag-template");

        assertEquals("simple", txtFld.getTheme());
        assertEquals("tag-template", txtFld.getTemplateDir());
    }

    public void testFormThemeWinsOverPackageOverride() {
        enterPackage("admin");

        Form form = new Form(stack, request, response);
        container.inject(form);
        form.setTheme("simple");

        assertEquals("simple", newTextField().getTheme());
    }

    public void testScopeAttributeWinsOverPackageOverride() {
        enterPackage("admin");
        request.setAttribute("theme", "simple");
        request.setAttribute("templateDir", "attr-template");
        Map<String, Object> context = stack.getContext();
        context.put(DispatcherConstants.REQUEST, new RequestMap(request));
        context.put("attr", new AttributeMap(context));

        TextField txtFld = newTextField();

        assertEquals("simple", txtFld.getTheme());
        assertEquals("attr-template", txtFld.getTemplateDir());
    }

    private TextField newTextField() {
        TextField txtFld = new TextField(stack, request, response);
        container.inject(txtFld);
        return txtFld;
    }

    private void enterPackage(String packageName) {
        MockActionProxy proxy = new MockActionProxy();
        proxy.setConfig(new ActionConfig.Builder(packageName, "test", ActionSupport.class.getName()).build());
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setProxy(proxy);
        ActionContext.getContext().withActionInvocation(invocation);
    }
}
