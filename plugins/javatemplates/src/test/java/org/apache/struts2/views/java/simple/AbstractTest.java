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

package org.apache.struts2.views.java.simple;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.TextParser;
import com.opensymphony.xwork2.util.OgnlTextParser;
import com.opensymphony.xwork2.util.ValueStack;
import junit.framework.TestCase;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateRenderingContext;
import org.easymock.EasyMock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public abstract class AbstractTest extends TestCase {
    private Map<String, String> scriptingAttrs = new HashMap<String, String>();
    private Map<String, String> commonAttrs = new HashMap<String, String>();
    private Map<String, Object> dynamicAttrs = new HashMap<String, Object>();

    protected SimpleTheme theme;

    protected StringWriter writer;
    protected Map map;

    protected Template template;
    protected Map stackContext;
    protected ValueStack stack;
    protected TemplateRenderingContext context;
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    protected abstract UIBean getUIBean() throws Exception;

    protected abstract String getTagName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();    
        scriptingAttrs.put("onclick", "onclick_");
        scriptingAttrs.put("ondblclick", "ondblclick_");
        scriptingAttrs.put("onmousedown", "onmousedown_");
        scriptingAttrs.put("onmouseup", "onmouseup_");
        scriptingAttrs.put("onmouseover", "onmouseover_");
        scriptingAttrs.put("onmousemove", "onmousemove_");
        scriptingAttrs.put("onmouseout", "onmouseout_");
        scriptingAttrs.put("onfocus", "onfocus_");
        scriptingAttrs.put("onblur", "onblur_");
        scriptingAttrs.put("onkeypress", "onkeypress_");
        scriptingAttrs.put("onkeydown", "onkeydown_");
        scriptingAttrs.put("onkeyup", "onkeyup_");
        scriptingAttrs.put("onselect", "onselect_");
        scriptingAttrs.put("onchange", "onchange_");

        commonAttrs.put("accesskey", "accesskey_");

        dynamicAttrs.put("data-remote", "data-remote_");
        dynamicAttrs.put("data-label", "data-label_");

        theme = new SimpleTheme();
        writer = new StringWriter();
        map = new HashMap();

        template = org.easymock.classextension.EasyMock.createMock(Template.class);
        stack = EasyMock.createNiceMock(ValueStack.class);
        setUpStack();
        stackContext = new HashMap();

        context = new TemplateRenderingContext(template, writer, stack, map, null);
        stackContext.put(Component.COMPONENT_STACK, new Stack());

        request = EasyMock.createNiceMock(HttpServletRequest.class);
        EasyMock.expect(request.getContextPath()).andReturn("/some/path").anyTimes();
        response = EasyMock.createNiceMock(HttpServletResponse.class);

        EasyMock.expect(stack.getContext()).andReturn(stackContext).anyTimes();

        Container container = EasyMock.createNiceMock(Container.class);
        XWorkConverter converter = new ConverterEx();
        EasyMock.expect(container.getInstance(String.class, StrutsConstants.STRUTS_TAG_ALTSYNTAX)).andReturn("true").anyTimes();
        EasyMock.expect(container.getInstance(XWorkConverter.class)).andReturn(converter).anyTimes();
        TextParser parser = new OgnlTextParser();
        EasyMock.expect(container.getInstance(TextParser.class)).andReturn(parser).anyTimes();
        stackContext.put(ActionContext.CONTAINER, container);


        EasyMock.replay(request);
        EasyMock.replay(stack);
        EasyMock.replay(container);

        ActionContext.setContext(new ActionContext(stackContext));
        ServletActionContext.setRequest(request);
    }

    protected static String s(String input) {
        return input.replaceAll("'", "\"");
    }

    protected void expectFind(String expr, Class toClass, Object returnVal) {
        EasyMock.expect(stack.findValue(expr, toClass)).andReturn(returnVal);
        EasyMock.expect(stack.findValue(expr, toClass, false)).andReturn(returnVal);
    }

    protected void expectFind(String expr, Object returnVal) {
        EasyMock.expect(stack.findValue(expr)).andReturn(returnVal).anyTimes();
        EasyMock.expect(stack.findValue(expr, false)).andReturn(returnVal).anyTimes();
    }

    protected void setUpStack() {
        //TODO setup a config with stack and all..for real
    }

    protected void applyScriptingAttrs(UIBean bean) {
        bean.setOnclick(scriptingAttrs.get("onclick"));
        bean.setOndblclick(scriptingAttrs.get("ondblclick"));
        bean.setOnmousedown(scriptingAttrs.get("onmousedown"));
        bean.setOnmouseup(scriptingAttrs.get("onmouseup"));
        bean.setOnmouseover(scriptingAttrs.get("onmouseover"));
        bean.setOnmousemove(scriptingAttrs.get("onmousemove"));
        bean.setOnmouseout(scriptingAttrs.get("onmouseout"));
        bean.setOnfocus(scriptingAttrs.get("onfocus"));
        bean.setOnblur(scriptingAttrs.get("onblur"));
        bean.setOnkeypress(scriptingAttrs.get("onkeypress"));
        bean.setOnkeydown(scriptingAttrs.get("onkeydown"));
        bean.setOnkeyup(scriptingAttrs.get("onkeyup"));
        bean.setOnselect(scriptingAttrs.get("onselect"));
        bean.setOnchange(scriptingAttrs.get("onchange"));
    }

    protected void applyCommonAttrs(UIBean bean) {
        bean.setAccesskey("accesskey_");
    }

    protected void applyDynamicAttrs(UIBean bean) {
    	bean.setDynamicAttributes(dynamicAttrs);
    }

    protected void assertScriptingAttrs(String str) {
        for (Map.Entry<String, String> entry : scriptingAttrs.entrySet()) {
            String substr = entry.getKey() + "=\"" + entry.getValue() + "\"";
            assertTrue("String [" + substr + "] was not found in [" + str + "]", str.indexOf(substr) >= 0);
        }
    }

    protected void assertCommonAttrs(String str) {
        for (Map.Entry<String, String> entry : commonAttrs.entrySet()) {
            String substr = entry.getKey() + "=\"" + entry.getValue() + "\"";
            assertTrue("String [" + substr + "] was not found in [" + str + "]", str.indexOf(substr) >= 0);
        }
    }

    protected void assertDynamicAttrs(String str) {
        for (Map.Entry<String, Object> entry : dynamicAttrs.entrySet()) {
            String substr = entry.getKey() + "=\"" + entry.getValue() + "\"";
            assertTrue("String [" + substr + "] was not found in [" + str + "]", str.indexOf(substr) >= 0);
        }
    }

    protected Object doFindValue(String expr, Class toType) {
        Object val = stack.findValue(expr);

        if (toType == String.class)
            return val == null ? expr : val;
        else
            return val == null ? null : val;
    }

    //XWorkConverter doesnt have a public onstructor (the one with parameters will require mor config)
    public class ConverterEx extends XWorkConverter {
        public ConverterEx() {

        }
    }
}
