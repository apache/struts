/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Push;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Push
 */
public class PushTag extends ComponentTagSupport {
	
	private static final long serialVersionUID = -1357895305148907931L;
	
	protected String value;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Push(stack);
    }

    protected void populateParams() {
        super.populateParams();

        ((Push) component).setValue(value);
    }

    public void setValue(String value) {
        this.value = value;
    }
}
