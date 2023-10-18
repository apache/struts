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

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 * Renders an HTML input element of type checkbox, populated by the specified property from the ValueStack.
 * <!-- END SNIPPET: javadoc -->
 *
 * <p><b>Examples</b></p>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * JSP:
 * &lt;s:checkbox label="checkbox test" name="checkboxField1" value="aBoolean" fieldValue="true"/&gt;
 *
 * Velocity:
 * #tag( Checkbox "label=checkbox test" "name=checkboxField1" "value=aBoolean" )
 *
 * Resulting HTML (simple template, aBoolean == true):
 * &lt;input type="checkbox" name="checkboxField1" value="true" checked="checked" /&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 */
@StrutsTag(
    name = "checkbox",
    tldTagClass = "org.apache.struts2.views.jsp.ui.CheckboxTag",
    description = "Render a checkbox input field",
    allowDynamicAttributes = true)
public class Checkbox extends UIBean {

    private static final String ATTR_SUBMIT_UNCHECKED = "submitUnchecked";

    public static final String TEMPLATE = "checkbox";

    private String submitUncheckedGlobal;

    protected String fieldValue;
    protected String submitUnchecked;

    public Checkbox(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected void evaluateExtraParams() {
        if (fieldValue != null) {
            addParameter(ATTR_FIELD_VALUE, findString(fieldValue));
        } else {
            addParameter(ATTR_FIELD_VALUE, "true");
        }

        if (submitUnchecked != null) {
            Object parsedValue = findValue(submitUnchecked, Boolean.class);
            addParameter(ATTR_SUBMIT_UNCHECKED, parsedValue == null ? Boolean.valueOf(submitUnchecked) : parsedValue);
        } else if (submitUncheckedGlobal != null) {
            addParameter(ATTR_SUBMIT_UNCHECKED, Boolean.parseBoolean(submitUncheckedGlobal));
        } else {
            addParameter(ATTR_SUBMIT_UNCHECKED, false);
        }
    }

    @Override
    protected Class<?> getValueClassType() {
        return Boolean.class; // for checkboxes, everything needs to end up as a Boolean
    }

    @Inject(value = StrutsConstants.STRUTS_UI_CHECKBOX_SUBMIT_UNCHECKED, required = false)
    public void setSubmitUncheckedGlobal(String submitUncheckedGlobal) {
        this.submitUncheckedGlobal = submitUncheckedGlobal;
    }

    @StrutsTagAttribute(description = "The actual HTML value attribute of the checkbox.", defaultValue = "true")
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    @StrutsTagAttribute(description = "If set to true, unchecked elements will be submitted with the form. " +
        "Since Struts 6.1.1 you can use a constant \"" + StrutsConstants.STRUTS_UI_CHECKBOX_SUBMIT_UNCHECKED + "\" to set this attribute globally",
        type = "Boolean", defaultValue = "false")
    public void setSubmitUnchecked(String submitUnchecked) {
        this.submitUnchecked = submitUnchecked;
    }

    @Override
    @StrutsTagAttribute(description = "Define label position of form element (top/left), also 'right' is supported when using 'xhtml' theme")
    public void setLabelPosition(String labelPosition) {
        super.setLabelPosition(labelPosition);
    }

}
