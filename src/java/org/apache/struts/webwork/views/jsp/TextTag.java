/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Text;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Text
 */
public class TextTag extends ComponentTagSupport {
	
	private static final long serialVersionUID = -3075088084198264581L;
	
	protected String name;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Text(stack);
    }

    protected void populateParams() {
        super.populateParams();

        ((Text) component).setName(name);
    }

    public void setName(String name) {
        this.name = name;
    }
}
