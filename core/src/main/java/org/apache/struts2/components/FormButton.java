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

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FormButton.
 */
public abstract class FormButton extends UIBean {

    static final String BUTTONTYPE_INPUT = "input";
    static final String BUTTONTYPE_BUTTON = "button";
    static final String BUTTONTYPE_IMAGE = "image";

    protected String action;
    protected String method;
    protected String align;
    protected String type;

    public FormButton(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public void evaluateParams() {
        if (align == null) {
            align = "right";
        }

        String submitType = BUTTONTYPE_INPUT;
        if (type != null && (BUTTONTYPE_BUTTON.equalsIgnoreCase(type) || (supportsImageType() && BUTTONTYPE_IMAGE.equalsIgnoreCase(type))))
        {
            submitType = type;
        }

        super.evaluateParams();

        addParameter("type", submitType);

        if (!BUTTONTYPE_INPUT.equals(submitType) && (label == null)) {
            addParameter("label", getParameters().get("nameValue"));
        }

        if (action != null || method != null) {
            String name;

            if (action != null) {
                name = "action:" + findString(action);

                if (method != null) {
                    name += "!" + findString(method);
                }
            } else {
                name = "method:" + findString(method);
            }

            addParameter("name", name);
        }

        addParameter("align", findString(align));

    }

    /**
     * Indicate whether the concrete button supports the type "image".
     *
     * @return <tt>true</tt> if type image is supported.
     */
    protected abstract boolean supportsImageType();

    /**
     * Set action attribute.
     *
     * @a2.tagattribute required="false" type="String"
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Set method attribute.
     *
     * @a2.tagattribute required="false" type="String"
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * HTML align attribute.
     *
     * @a2.tagattribute required="false" type="String"
     */
    public void setAlign(String align) {
        this.align = align;
    }

    /**
     * The type of submit to use. Valid values are <i>input</i>, <i>button</i> and <i>image</i>.
     *
     * @a2.tagattribute required="false" type="String" default="input"
     */
    public void setType(String type) {
        this.type = type;
    }
}
