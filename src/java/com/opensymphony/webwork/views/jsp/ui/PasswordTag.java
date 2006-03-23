/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Password;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Password
 */
public class PasswordTag extends TextFieldTag {
    protected String showPassword;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Password(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ((Password) component).setShowPassword(showPassword);
    }

    public void setShow(String showPassword) {
        this.showPassword = showPassword;
    }

    public void setShowPassword(String showPassword) {
        this.showPassword = showPassword;
    }
}
