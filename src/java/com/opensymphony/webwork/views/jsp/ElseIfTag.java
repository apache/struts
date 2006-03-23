/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.ElseIf;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see ElseIf
 */
public class ElseIfTag extends ComponentTagSupport {
    protected String test;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ElseIf(stack);
    }

    protected void populateParams() {
        ((ElseIf) getComponent()).setTest(test);
    }

    public void setTest(String test) {
        this.test = test;
    }
}
