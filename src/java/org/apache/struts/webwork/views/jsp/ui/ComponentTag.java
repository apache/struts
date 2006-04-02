/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp.ui;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.GenericUIBean;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see GenericUIBean
 */
public class ComponentTag extends AbstractUITag {
	
	private static final long serialVersionUID = 5448365363044104731L;

	public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new GenericUIBean(stack, req, res);
    }
}
