/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action2.components.ActionError;
import org.apache.struts.action2.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * ActionError Tag.
 * 
 * @author tm_jee
 * @version $Date: 2005/12/22 15:05:24 $ $Id: ActionErrorTag.java,v 1.1 2005/12/22 15:05:24 tmjee Exp $
 */
public class ActionErrorTag extends AbstractUITag {

	public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new ActionError(stack, req, res);
	}

}
