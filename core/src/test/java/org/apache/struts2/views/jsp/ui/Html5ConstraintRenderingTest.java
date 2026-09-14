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

import ognl.NoSuchPropertyException;
import org.apache.struts2.StrutsException;
import org.apache.struts2.action.Action;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.components.ConstraintAction;
import org.apache.struts2.TestConfigurationProvider;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.views.jsp.AbstractUITagTest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Html5ConstraintRenderingTest extends AbstractUITagTest {

    /**
     * Push the action whose validators run, so a field's bound value is the one the provider sees.
     */
    @Override
    public Action getAction() {
        return new ConstraintAction();
    }

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

    /**
     * The field carries only a regex validator, so the pattern also has to admit the whitespace-only
     * input the server skips; the suffix must survive FreeMarker's attribute escaping unchanged.
     */
    public void testRendersPatternOnATextField() throws Exception {
        String output = render("true", "code", null);

        assertTrue("expected pattern in: " + output,
            output.contains("pattern=\"(?:^[A-Z]{3}\\d{2}$)|[\\x00-\\x20]*\""));
    }

    /**
     * radiomap.ftl includes common-attributes.ftl once per option, so every input of the group
     * carries the attribute; HTML applies {@code required} to the group as a whole.
     */
    public void testRendersRequiredOnEveryRadioOfTheGroup() throws Exception {
        String output = renderTag("true", () -> {
            RadioTag radio = new RadioTag();
            radio.setName("choice");
            radio.setList("{'yes','no'}");
            return radio;
        });

        assertEquals("expected required on both radios in: " + output,
            2, output.split("required=\"required\"", -1).length - 1);
    }

    /**
     * The bound int renders as 0, which is not in the list, so no radio is checked; the server would
     * still accept the empty submit because 0 is a non-null Integer.
     */
    public void testRendersNoRequiredOnARadioBackedByAPrimitive() throws Exception {
        String output = renderTag("true", () -> {
            RadioTag radio = new RadioTag();
            radio.setName("priority");
            radio.setList("{1,2,3}");
            return radio;
        });

        assertTrue("expected the radios in: " + output, output.contains("name=\"priority\""));
        assertFalse("expected no required in: " + output, output.contains("required=\"required\""));
    }

    public void testRendersRequiredOnAFileInput() throws Exception {
        String output = renderTag("true", () -> {
            FileTag file = new FileTag();
            file.setName("attachment");
            return file;
        });

        assertTrue("expected required in: " + output,
            output.contains("type=\"file\" name=\"attachment\"") && output.contains("required=\"required\""));
    }

    /**
     * The edit flow: prepare() loaded the existing attachment, so an empty submit keeps it and the
     * server accepts; the browser must not insist on a new file.
     */
    public void testRendersNoRequiredOnAFileInputWhoseAttachmentIsAlreadyLoaded() throws Exception {
        String output = renderTag("true", action -> action.setAttachment(new Object()), () -> {
            FileTag file = new FileTag();
            file.setName("attachment");
            return file;
        });

        assertTrue("expected the file input in: " + output, output.contains("type=\"file\" name=\"attachment\""));
        assertFalse("expected no required in: " + output, output.contains("required=\"required\""));
    }

    /**
     * An UploadedFilesAware action receives the part by name and has no property behind the input;
     * with struts.el.throwExceptionOnFailure the missing property must not turn into a 500.
     */
    public void testRendersAFileInputBoundToNoPropertyWhenElFailuresThrow() throws Exception {
        String output = renderTag("true", Map.of(StrutsConstants.STRUTS_EL_THROW_EXCEPTION, "true"), action -> { }, () -> {
            FileTag file = new FileTag();
            file.setName("upload");
            return file;
        });

        assertTrue("expected the file input in: " + output, output.contains("type=\"file\" name=\"upload\""));
    }

    public void testAFileInputStillSurfacesABrokenExpressionWhenElFailuresThrow() throws Exception {
        try {
            renderTag("true", Map.of(StrutsConstants.STRUTS_EL_THROW_EXCEPTION, "true"), action -> { }, () -> {
                FileTag file = new FileTag();
                file.setName("upload[");
                return file;
            });
            fail("expected the broken expression to throw");
        } catch (StrutsException e) {
            assertFalse("only a missing property is tolerated", e.getCause() instanceof NoSuchPropertyException);
        }
    }

    /**
     * combobox.ftl reaches constraints.ftl through html5/text.ftl, so the text half of the control
     * already carries constraints; this pins that against a template rewrite.
     */
    public void testRendersConstraintsOnTheTextHalfOfACombobox() throws Exception {
        String output = renderTag("true", () -> {
            ComboBoxTag combo = new ComboBoxTag();
            combo.setName("username");
            combo.setList("{'a','b'}");
            return combo;
        });

        assertTrue("expected minlength on the text input in: " + output,
            output.contains("name=\"username\" value=\"\" id=\"constraintAction_username\" minlength=\"3\""));
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
        return renderTag(constraintsEnabled, () -> {
            TextFieldTag field = new TextFieldTag();
            field.setName(fieldName);
            if (requiredLabel != null) {
                field.setRequiredLabel(requiredLabel);
            }
            if (maxlength != null) {
                field.setMaxlength(maxlength);
            }
            return field;
        });
    }

    private String renderTag(String constraintsEnabled, Supplier<AbstractUITag> tagFactory) throws Exception {
        return renderTag(constraintsEnabled, action -> { }, tagFactory);
    }

    private String renderTag(String constraintsEnabled, Consumer<ConstraintAction> prepare,
                             Supplier<AbstractUITag> tagFactory) throws Exception {
        return renderTag(constraintsEnabled, Map.of(), prepare, tagFactory);
    }

    private String renderTag(String constraintsEnabled, Map<String, String> extraConstants,
                             Consumer<ConstraintAction> prepare, Supplier<AbstractUITag> tagFactory) throws Exception {
        initDispatcher(new HashMap<String, String>() {{
            put("configProviders", TestConfigurationProvider.class.getName());
            put(StrutsConstants.STRUTS_UI_HTML5_CONSTRAINTS, constraintsEnabled);
            putAll(extraConstants);
        }});
        createMocks();
        prepare.accept((ConstraintAction) action);
        ((MockActionProxy) actionProxy).setConfig(configuration.getRuntimeConfiguration().getActionConfig("", "constraintAction"));

        FormTag form = new FormTag();
        form.setPageContext(pageContext);
        form.setTheme("html5");
        form.setAction("constraintAction");
        form.setNamespace("");
        form.doStartTag();

        AbstractUITag field = tagFactory.get();
        field.setPageContext(pageContext);
        field.setTheme("html5");
        field.doStartTag();
        field.doEndTag();
        form.doEndTag();

        return writer.toString();
    }
}
