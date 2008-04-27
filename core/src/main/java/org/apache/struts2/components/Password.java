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
 * Render an HTML input tag of type password.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <p/>
 * <!-- START SNIPPET: exdescription -->
 * In this example, a password control is displayed. For the label, we are calling ActionSupport's getText() to
 * retrieve password label from a resource bundle.<p/>
 * <!-- END SNIPPET: exdescription -->
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:password label="%{text('password')}" name="password" size="10" maxlength="15" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(
    name="password",
    tldTagClass="org.apache.struts2.views.jsp.ui.PasswordTag",
    description="Render an HTML input tag of type password",
    allowDynamicAttributes=true)
public class Password extends TextField {
    final public static String TEMPLATE = "password";

    protected String showPassword;

    public Password(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (showPassword != null) {
            addParameter("showPassword", findValue(showPassword, Boolean.class));
        }
    }

    @StrutsTagAttribute(description="Whether to show input", type="Boolean", defaultValue="false")
    public void setShowPassword(String showPassword) {
        this.showPassword = showPassword;
    }

}
