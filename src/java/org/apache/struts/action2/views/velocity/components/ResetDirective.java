/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.velocity.components;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Reset;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see org.apache.struts.action2.components.Reset
 */
public class ResetDirective extends AbstractDirective {
    public String getBeanName() {
        return "reset";
    }

    protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Reset(stack, req, res);
    }
}
