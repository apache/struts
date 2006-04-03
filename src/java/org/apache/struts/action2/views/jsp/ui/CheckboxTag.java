/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.components.Checkbox;
import org.apache.struts.action2.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Checkbox
 */
public class CheckboxTag extends AbstractUITag {
	
	private static final long serialVersionUID = -350752809266337636L;
	
	protected String fieldValue;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Checkbox(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ((Checkbox) component).setFieldValue(fieldValue);
    }

    public void setFieldValue(String aValue) {
        this.fieldValue = aValue;
    }
}
