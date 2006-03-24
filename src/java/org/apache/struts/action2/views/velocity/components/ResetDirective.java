/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.velocity.components;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Reset;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see com.opensymphony.webwork.components.Reset
 */
public class ResetDirective extends AbstractDirective {
    public String getBeanName() {
        return "reset";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Reset(stack, req, res);
    }
}
