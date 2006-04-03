/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Password;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Password
 */
public class PasswordTag extends TextFieldTag {
	
	private static final long serialVersionUID = 6802043323617377573L;
	
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
