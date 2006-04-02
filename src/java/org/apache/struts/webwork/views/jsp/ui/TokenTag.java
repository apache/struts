/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp.ui;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.Token;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Token
 */
public class TokenTag extends AbstractUITag {
	
	private static final long serialVersionUID = 722480798151703457L;

	public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Token(stack, req, res);
    }
}
