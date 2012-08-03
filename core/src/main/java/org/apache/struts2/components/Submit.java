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

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render a submit button. The submit tag is used together with the form tag to provide asynchronous form submissions.
 * The submit can have three different types of rendering:
 * <ul>
 * <li>input: renders as html &lt;input type="submit"...&gt;</li>
 * <li>image: renders as html &lt;input type="image"...&gt;</li>
 * <li>button: renders as html &lt;button type="submit"...&gt;</li>
 * </ul>
 * Please note that the button type has advantages by adding the possibility to seperate the submitted value from the
 * text shown on the button face, but has issues with Microsoft Internet Explorer at least up to 6.0
 * <!-- END SNIPPET: javadoc -->
 */
@StrutsTag(
    name="submit",
    tldTagClass="org.apache.struts2.views.jsp.ui.SubmitTag",
    description="Render a submit button",
    allowDynamicAttributes=true)
public class Submit extends FormButton {

    private static final Logger LOG = LoggerFactory.getLogger(Submit.class);
    final public static String OPEN_TEMPLATE = "submit";
    final public static String TEMPLATE = "submit-close";
    protected String src;

    public Submit(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateParams() {
        if ((key == null) && (value == null)) {
            value = "Submit";
        }

        if (((key != null)) && (value == null)) {
            this.value = "%{getText('"+key +"')}";
        }

        super.evaluateParams();
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (src != null)
            addParameter("src", findString(src));
    }

    /**
     * Indicate whether the concrete button supports the type "image".
     *
     * @return <tt>true</tt> to indicate type image is supported.
     */
    protected boolean supportsImageType() {
        return true;
    }

    @StrutsTagAttribute(description="Supply an image src for <i>image</i> type submit button. Will have no effect for types <i>input</i> and <i>button</i>.")
    public void setSrc(String src) {
        this.src = src;
    }


    @Override
    public boolean usesBody() {
        return true;
    }

    /**
     * Overrides to be able to render body in a template rather than always before the template
     */
    public boolean end(Writer writer, String body) {
        evaluateParams();
        try {
            addParameter("body", body);

            mergeTemplate(writer, buildTemplateName(template, getDefaultTemplate()));
        } catch (Exception e) {
            LOG.error("error when rendering", e);
        }
        finally {
            popComponentStack();
        }

        return false;
    }
}
