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

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render a reset button. The reset tag is used together with the form tag to provide form resetting.
 * The reset can have two different types of rendering:
 * <ul>
 * <li>input: renders as html &lt;input type="reset"...&gt;</li>
 * <li>button: renders as html &lt;button type="reset"...&gt;</li>
 * </ul>
 * Please note that the button type has advantages by adding the possibility to separate the submitted value from the
 * text shown on the button face, but has issues with Microsoft Internet Explorer at least up to 6.0
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:reset value="Reset" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * Render a reset button:
 * &lt;s:reset type="button" key="reset"/&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 */
@StrutsTag(
    name="reset",
    tldTagClass="org.apache.struts2.views.jsp.ui.ResetTag",
    description="Render a reset button",
    allowDynamicAttributes=true)
public class Reset extends FormButton {
    final public static String TEMPLATE = "reset";

    protected String src;

    public Reset(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public String getDefaultOpenTemplate() {
        return "empty";
    }

    protected String getDefaultTemplate() {
        return Reset.TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (src != null)
            addParameter("src", findString(src));
    }

    public void evaluateParams() {
        if (value == null) {
            value = (key != null ? "%{getText('"+key+"')}" : "Reset");
        }
        super.evaluateParams();
    }

    /**
     * Indicate whether the concrete button supports the type "image".
     *
     * @return <tt>false</tt> to indicate type image is not supported.
     */
    protected boolean supportsImageType() {
        return false;
    }

    @StrutsTagAttribute(description="Supply a reset button text apart from reset value. Will have no effect for " +
                "<i>input</i> type reset, since button text will always be the value parameter.")
    public void setLabel(String label) {
        super.setLabel(label);
    }

    @StrutsTagAttribute(description="Supply an image src for <i>image</i> type reset button. Will have no effect for types <i>input</i> and <i>button</i>.")
    public void setSrc(String src) {
        this.src = src;
    }

}
