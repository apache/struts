/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Property;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Property
 */
public class PropertyTag extends ComponentTagSupport {
    private String defaultValue;
    private String value;
    private boolean escape = true;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Property(stack);
    }

    protected void populateParams() {
        super.populateParams();

        Property tag = (Property) component;
        tag.setDefault(defaultValue);
        tag.setValue(value);
        tag.setEscape(escape);
    }

    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
