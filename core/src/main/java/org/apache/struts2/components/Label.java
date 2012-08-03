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
import org.apache.struts2.util.TextProviderHelper;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * Renders an HTML LABEL that will allow you to output label:name combination that has the same format treatment as
 * the rest of your UI controls.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <p/>
 * <!-- START SNIPPET: exdescription -->
 * In this example, a label is rendered. The label is retrieved from a ResourceBundle via the key attribute
 * giving you an output of 'User Name: Ford.Prefect'. Assuming that i18n message userName corresponds
 * to 'User Name' and the action's getUserName() method returns 'Ford.Prefect'<p/>
 * <!-- END SNIPPET: exdescription -->
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:label key="userName" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * &lt;s:label name="userName" label="User Name" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(
    name="label",
    tldTagClass="org.apache.struts2.views.jsp.ui.LabelTag",
    description="Render a label that displays read-only information",
    allowDynamicAttributes=true)
public class Label extends UIBean {
    final public static String TEMPLATE = "label";

    protected String forAttr;

    public Label(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (forAttr != null) {
            addParameter("for", findString(forAttr));
        }

        // try value, then key, then name (this overrides the default behavior in the superclass)
        if (value != null) {
            addParameter("nameValue", findString(value));
        } else if (key != null) {
            Object nameValue = parameters.get("nameValue");
            if (nameValue == null || nameValue.toString().length() == 0) {
                // get the label from a TextProvider (default value is the key)
                String providedLabel = TextProviderHelper.getText(key, key, stack);
                addParameter("nameValue", providedLabel);
            }
        } else if (name != null) {
            String expr = completeExpressionIfAltSyntax(name);
            addParameter("nameValue", findString(expr));
        }
    }

    @StrutsTagAttribute(description=" HTML for attribute")
    public void setFor(String forAttr) {
        this.forAttr = forAttr;
    }
}
