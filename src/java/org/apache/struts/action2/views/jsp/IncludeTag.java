/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
/*
 * WebWork, Web Application Framework
 *
 * Distributable under Apache license.
 * See terms of license at opensource.org
 */
package org.apache.struts.action2.views.jsp;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Include;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Include
 */
public class IncludeTag extends ComponentTagSupport {
	
	private static final long serialVersionUID = -1585165567043278243L;
	
	protected String value;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Include(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ((Include) component).setValue(value);
    }

    public void setValue(String value) {
        this.value = value;
    }
}
