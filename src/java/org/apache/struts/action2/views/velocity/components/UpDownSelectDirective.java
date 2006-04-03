/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.UpDownSelect;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class UpDownSelectDirective extends AbstractDirective {

	public String getBeanName() {
		return "updownselect";
	}

	protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new UpDownSelect(stack, req, res);
	}
}
