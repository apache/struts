/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render an HTML input field of type text</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <p/>
 * <!-- START SNIPPET: exdescription -->
 * In this example, a text control is rendered. The label is retrieved from a ResourceBundle by calling
 * ActionSupport's getText() method.<p/>
 * <!-- END SNIPPET: exdescription -->
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:textfield label="%{text('user_name')}" name="user" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @s.tag name="textfield" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.TextFieldTag"
 * description="Render an HTML input field of type text"
 */
public class TextField extends UIBean {
    /**
     * The name of the default template for the TextFieldTag
     */
    final public static String TEMPLATE = "text";


    protected String maxlength;
    protected String readonly;
    protected String size;

    public TextField(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (size != null) {
            addParameter("size", findString(size));
        }

        if (maxlength != null) {
            addParameter("maxlength", findString(maxlength));
        }

        if (readonly != null) {
            addParameter("readonly", findValue(readonly, Boolean.class));
        }
    }

    /**
     * HTML maxlength attribute
     * @s.tagattribute required="false" type="Integer"
     */
    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    /**
     * Deprecated. Use maxlength instead.
     * @s.tagattribute required="false"
     */
    public void setMaxLength(String maxlength) {
        this.maxlength = maxlength;
    }

    /**
     * Whether the input is readonly
     * @s.tagattribute required="false" type="Boolean" default="false"
     */
    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    /**
     * HTML size attribute
     * @s.tagattribute required="false" type="Integer"
     */
    public void setSize(String size) {
        this.size = size;
    }
}
