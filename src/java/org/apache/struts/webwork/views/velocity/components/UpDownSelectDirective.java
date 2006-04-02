/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.UpDownSelect;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * 
 * @author tm_jee
 * @version $Date: 2006/03/15 16:35:38 $ $Id: UpDownSelectDirective.java,v 1.1 2006/03/15 16:35:38 tmjee Exp $
 */
public class UpDownSelectDirective extends AbstractDirective {

	public String getBeanName() {
		return "updownselect";
	}

	protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new UpDownSelect(stack, req, res);
	}
}
