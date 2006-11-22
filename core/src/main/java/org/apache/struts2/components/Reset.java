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

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render a reset button. The reset tag is used together with the form tag to provide form resetting.
 * The reset can have two different types of rendering:
 * <ul>
 * <li>input: renders as html &lt;input type="reset"...&gt;</li>
 * <li>button: renders as html &lt;button type="reset"...&gt;</li>
 * </ul>
 * Please note that the button type has advantages by adding the possibility to seperate the submitted value from the
 * text shown on the button face, but has issues with Microsoft Internet Explorer at least up to 6.0
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:reset value="%{'Reset'}" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * Render an button reset:
 * &lt;s:reset type="button" value="%{'Reset'}" label="Reset the form"/&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 * @s.tag name="reset" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.ResetTag"
 * description="Render a reset button"
 */
public class Reset extends FormButton {
    final public static String TEMPLATE = "reset";

    protected String action;
    protected String method;
    protected String align;
    protected String type;

    public Reset(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return Reset.TEMPLATE;
    }

    public void evaluateParams() {

        if ((key == null) && (value == null)) {
            value = "Reset";
        }

        if (((key != null)) && (value == null)) {
            this.value = "%{getText('"+key +"')}";
        }

        super.evaluateParams();

    }

    /**
     * Indicate whether the concrete button supports the type "image".
     *
     * @return <tt>false</tt> to indicate type image is supported.
     */
    protected boolean supportsImageType() {
        return false;
    }

    /**
     * Supply a reset button text apart from reset value. Will have no effect for <i>input</i> type reset, since button
     * text will always be the value parameter.
     *
     * @s.tagattribute required="false"
     */
    public void setLabel(String label) {
        super.setLabel(label);
    }

}
