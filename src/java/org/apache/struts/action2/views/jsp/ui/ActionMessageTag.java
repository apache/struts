/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action2.components.ActionMessage;
import org.apache.struts.action2.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * ActionMessage Tag.
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class ActionMessageTag extends AbstractUITag {

	private static final long serialVersionUID = 243396927554182506L;

	public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new ActionMessage(stack, req, res);
	}
}
