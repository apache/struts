/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Head;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Head
 */
public class HeadTag extends AbstractUITag {

    private String calendarcss;
    private String debug;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Head(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();
        if (calendarcss != null) {
        	((Head) component).setCalendarcss(calendarcss);
        }
        if (debug != null) {
        	((Head) component).setDebug(Boolean.valueOf(debug).booleanValue());
        }
    }

    public String getCalendarcss() {
        return calendarcss;
    }

    public void setCalendarcss(String calendarcss) {
        this.calendarcss = calendarcss;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }
}
