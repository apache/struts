/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.ComboBox;
import com.opensymphony.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see ComboBox
 */
public class ComboBoxTag extends TextFieldTag {
    protected String list;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ComboBox(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ((ComboBox) component).setList(list);
    }

    public void setList(String list) {
        this.list = list;
    }
}
