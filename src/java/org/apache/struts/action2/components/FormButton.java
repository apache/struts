/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FormButton.
 *
 * @author <a href="mailto:gielen@it-neering.net">Rene Gielen</a>
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
