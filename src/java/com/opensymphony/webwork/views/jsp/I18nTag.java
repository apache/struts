/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.I18n;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see I18n
 */
public class I18nTag extends ComponentTagSupport {
    protected String name;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new I18n(stack);
    }

    protected void populateParams() {
        super.populateParams();

        ((I18n) component).setName(name);
    }

    public void setName(String name) {
        this.name = name;
    }
}
