/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.FieldError;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * FieldError Tag.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class FieldErrorTag extends AbstractUITag {

	private static final long serialVersionUID = -182532967507726323L;

	public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new FieldError(stack, req, res);
	}
}

