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
package org.apache.struts2.views.java.simple;

import org.apache.struts2.components.Radio;
import org.apache.struts2.components.TextField;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.interceptor.csp.CspNonceSource;
import org.apache.struts2.interceptor.csp.StrutsCspNonceReader;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.easymock.EasyMock.expect;

public class DynamicAttributesHandlerTest extends AbstractTest {

    private TextField textField;
    private Radio radio;

    public void testResolvedValueIsHtmlEscaped() throws Exception {
        textField.setName("q");
        textField.setTitle("%{quoted}");
        textField.setDynamicAttributes(dynamic("data-hint", "%{quoted}"));

        String output = render(textField, "textfield");

        assertEquals(s("<input type='text' name='q' id='q' title='a &quot;b&quot; &lt;c&gt;' data-hint='a &quot;b&quot; &lt;c&gt;'></input>"), output);
    }

    public void testLiteralValueIsHtmlEscaped() throws Exception {
        textField.setName("q");
        textField.setDynamicAttributes(dynamic("data-hint", "a \"b\""));

        String output = render(textField, "textfield");

        assertEquals(s("<input type='text' name='q' id='q' data-hint='a &quot;b&quot;'></input>"), output);
    }

    public void testResolvedValueInExpressionFormIsNotEvaluatedAgain() throws Exception {
        textField.setName("q");
        textField.setDynamicAttributes(dynamic("data-hint", "%{expressionLike}"));

        String output = render(textField, "textfield");

        assertEquals(s("<input type='text' name='q' id='q' data-hint='%{1+1}'></input>"), output);
    }

    public void testLazyComponentStillEvaluatesPerItem() throws Exception {
        radio.setName("r");
        radio.setId("r");
        radio.setList(Arrays.asList("one", "two"));
        radio.setDynamicAttributes(dynamic("data-item", "%{top}"));

        String output = render(radio, "radiomap");

        assertEquals(s("<input type='radio' name='r' value='x' id='r0' data-item='x'></input>"
                + "<label for='r0' data-item='x'>x</label>"
                + "<input type='radio' name='r' value='x' id='r1' data-item='x'></input>"
                + "<label for='r1' data-item='x'>x</label>"), output);
    }

    private String render(UIBean bean, String tagName) throws Exception {
        bean.evaluateParams();
        map.putAll(bean.getAttributes());
        theme.renderTag(tagName, context);
        return writer.getBuffer().toString();
    }

    private static Map<String, String> dynamic(String name, String value) {
        Map<String, String> dynamic = new LinkedHashMap<>();
        dynamic.put(name, value);
        return dynamic;
    }

    @Override
    protected void setUpStack() {
        expect(stack.findValue("quoted", String.class)).andReturn("a \"b\" <c>").anyTimes();
        expect(stack.findValue("expressionLike", String.class)).andReturn("%{1+1}").anyTimes();
        expect(stack.findValue("1+1", String.class)).andReturn("2").anyTimes();
        expect(stack.findValue("top", String.class)).andReturn("x").anyTimes();
        expect(stack.findValue("top")).andReturn("x").anyTimes();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        textField = new TextField(stack, request, response);
        textField.setCspNonceReader(new StrutsCspNonceReader(CspNonceSource.SESSION.name()));
        textField.setScopedConstantProvider(name -> null);
        radio = new Radio(stack, request, response);
        radio.setCspNonceReader(new StrutsCspNonceReader(CspNonceSource.SESSION.name()));
        radio.setScopedConstantProvider(name -> null);
    }

    @Override
    protected UIBean getUIBean() {
        return textField;
    }

    @Override
    protected String getTagName() {
        return "textfield";
    }
}
