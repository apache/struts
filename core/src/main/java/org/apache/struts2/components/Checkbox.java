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

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * Renders an HTML input element of type checkbox, populated by the specified property from the ValueStack.
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
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
 *
 */
@StrutsTag(
    name="checkbox",
    tldTagClass="org.apache.struts2.views.jsp.ui.CheckboxTag",
    description="Render a checkbox input field",
    allowDynamicAttributes=true)
public class Checkbox extends UIBean {
    final public static String TEMPLATE = "checkbox";

    protected String fieldValue;
    protected String readonly;

    public Checkbox(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected void evaluateExtraParams() {
        if (fieldValue != null) {
            addParameter("fieldValue", findString(fieldValue));
        } else {
            addParameter("fieldValue", "true");
        }
        
        if (readonly != null) {
            addParameter("readonly", findValue(readonly, Boolean.class));
        }
    }

    protected Class getValueClassType() {
        return Boolean.class; // for checkboxes, everything needs to end up as a Boolean
    }

    @StrutsTagAttribute(description="The actual HTML value attribute of the checkbox.", defaultValue="true")
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
    
    @StrutsTagAttribute(description="Whether the input is readonly", type="Boolean", defaultValue="false")
    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

}
