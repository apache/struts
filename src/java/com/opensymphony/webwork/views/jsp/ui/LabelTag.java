/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Label;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Label
 */
public class LabelTag extends AbstractUITag {
    protected String forAttr;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Label(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ((Label) component).setFor(forAttr);
    }

    public void setFor(String aFor) {
        this.forAttr = aFor;
    }
}
