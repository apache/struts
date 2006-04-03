/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Param;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Param
 */
public class ParamTag extends ComponentTagSupport {
	
	private static final long serialVersionUID = -968332732207156408L;
	
	protected String name;
    protected String value;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Param(stack);
    }

    protected void populateParams() {
        super.populateParams();

        Param param = (Param) component;
        param.setName(name);
        param.setValue(value);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
