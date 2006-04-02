/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Set;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Set
 */
public class SetTag extends ComponentTagSupport {
	
	private static final long serialVersionUID = -5074213926790716974L;
	
	protected String name;
    protected String scope;
    protected String value;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Set(stack);
    }

    protected void populateParams() {
        super.populateParams();

        Set set = (Set) component;
        set.setName(name);
        set.setScope(scope);
        set.setValue(value);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
