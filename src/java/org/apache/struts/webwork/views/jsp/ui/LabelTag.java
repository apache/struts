/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp.ui;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Label;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Label
 */
public class LabelTag extends AbstractUITag {
	
	private static final long serialVersionUID = 4008321310097730458L;
	
	protected String forAttr;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Label(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ((Label) component).setFor(forAttr);
    }

    public void setFor(String aFor) {
        this.forAttr = aFor;
    }
}
