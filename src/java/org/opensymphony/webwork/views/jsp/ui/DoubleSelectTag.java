/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.DoubleSelect;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see DoubleSelect
 */
public class DoubleSelectTag extends AbstractDoubleListTag {
    

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
