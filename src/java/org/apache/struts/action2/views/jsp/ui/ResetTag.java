/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Reset;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see org.apache.struts.action2.components.Reset
 */
public class ResetTag extends AbstractUITag {
	
	private static final long serialVersionUID = 4742704832277392108L;
	
	protected String action;
    protected String method;
    protected String align;
    protected String type;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Reset(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        Reset reset = ((Reset) component);
        reset.setAction(action);
        reset.setMethod(method);
        reset.setAlign(align);
        reset.setType(type);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public void setType(String type) {
        this.type = type;
    }

}
