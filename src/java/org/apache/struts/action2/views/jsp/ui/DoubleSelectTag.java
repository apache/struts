/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.DoubleSelect;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see DoubleSelect
 */
public class DoubleSelectTag extends AbstractDoubleListTag {
    
	private static final long serialVersionUID = 7426011596359509386L;

	public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DoubleSelect(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        DoubleSelect doubleSelect = ((DoubleSelect) component);
        doubleSelect.setEmptyOption(emptyOption);
        doubleSelect.setHeaderKey(headerKey);
        doubleSelect.setHeaderValue(headerValue);
        doubleSelect.setMultiple(multiple);
        doubleSelect.setSize(size);
        
    }
}
