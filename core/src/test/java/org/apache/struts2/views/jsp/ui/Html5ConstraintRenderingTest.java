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

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.views.jsp.AbstractUITagTest;

import java.util.HashMap;

public class Html5ConstraintRenderingTest extends AbstractUITagTest {

    public void testRendersConstraintAttributes() throws Exception {
        String output = render("true");

        assertTrue("expected minlength in: " + output, output.contains("minlength=\"3\""));
    }

    public void testRendersNothingWhenTheConstantIsOff() throws Exception {
        String output = render("false");

        assertFalse("expected no minlength in: " + output, output.contains("minlength="));
    }

    public void testRendersExactMarkupWhenTheConstantIsOff() throws Exception {
        String output = render("false");

        assertEquals("<form id=\"constraintAction\" name=\"constraintAction\" action=\"/constraintAction.action\" method=\"post\">"
            + "<input type=\"text\" name=\"username\" value=\"\" id=\"constraintAction_username\"/></form>", output);
    }

    public void testRequiredLabelDoesNotBecomeARequiredAttribute() throws Exception {
        String output = render("true", "username", "true");

        assertTrue("expected minlength in: " + output, output.contains("minlength=\"3\""));
        assertFalse("requiredLabel draws an asterisk; it must never emit a required attribute: " + output,
            output.contains("required=\"required\""));
    }

    /**
     * text.ftl renders {@code attributes.maxlength} (the developer's own tag attribute) before
     * including common-attributes.ftl, which renders the derived {@code attributes.constraints} map.
     * Without suppressing the derived duplicate, a stringlength validator on this field would render
     * {@code maxlength} twice: once from the tag attribute, once from the constraint.
     */
    public void testDeveloperSetMaxlengthSuppressesTheDerivedOne() throws Exception {
        String output = renderWithMaxlength("bio", "20");

        int firstIndex = output.indexOf("maxlength=");
        assertTrue("expected a maxlength attribute in: " + output, firstIndex >= 0);
        assertEquals("expected exactly one maxlength attribute in: " + output,
            firstIndex, output.lastIndexOf("maxlength="));
        assertTrue("expected the developer's own value to win: " + output,
            output.contains("maxlength=\"20\""));
    }

    /**
     * data-msg-* values pass through TextParseUtil.translateVariables and can carry user-submitted
     * content into an HTML attribute. Escaping is applied by FreemarkerManager's HTMLOutputFormat
     * configuration, not by the template, so this pins it against regression.
     */
    public void testDataMsgAttributesAreHtmlEscaped() throws Exception {
        String output = render("true", "comment", null);

        assertTrue("expected the escaped message in: " + output,
            output.contains("data-msg-requiredstring=\"Contains &quot;quotes&quot; and &lt;brackets&gt;\""));
        assertFalse("the raw, unescaped message must never appear in: " + output,
            output.contains("Contains \"quotes\" and <brackets>"));
    }

    private String render(String constraintsEnabled) throws Exception {
        return render(constraintsEnabled, "username", null);
    }

    private String render(String constraintsEnabled, String fieldName, String requiredLabel) throws Exception {
        return render(constraintsEnabled, fieldName, requiredLabel, null);
    }

    private String renderWithMaxlength(String fieldName, String maxlength) throws Exception {
        return render("true", fieldName, null, maxlength);
    }

    private String render(String constraintsEnabled, String fieldName, String requiredLabel, String maxlength) throws Exception {
        initDispatcher(new HashMap<String, String>() {{
            put("configProviders", TestConfigurationProvider.class.getName());
            put(StrutsConstants.STRUTS_UI_HTML5_CONSTRAINTS, constraintsEnabled);
        }});
        createMocks();
        ((MockActionProxy) actionProxy).setConfig(configuration.getRuntimeConfiguration().getActionConfig("", "constraintAction"));

        FormTag form = new FormTag();
        form.setPageContext(pageContext);
        form.setTheme("html5");
        form.setAction("constraintAction");
        form.setNamespace("");
        form.doStartTag();

        TextFieldTag field = new TextFieldTag();
        field.setPageContext(pageContext);
        field.setTheme("html5");
        field.setName(fieldName);
        if (requiredLabel != null) {
            field.setRequiredLabel(requiredLabel);
        }
        if (maxlength != null) {
            field.setMaxlength(maxlength);
        }
        field.doStartTag();
        field.doEndTag();
        form.doEndTag();

        return writer.toString();
    }
}
