/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.Checkbox;
import com.opensymphony.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Checkbox
 */
public class CheckboxTag extends AbstractUITag {
    protected String fieldValue;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Checkbox(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ((Checkbox) component).setFieldValue(fieldValue);
    }

    public void setFieldValue(String aValue) {
        this.fieldValue = aValue;
    }
}
