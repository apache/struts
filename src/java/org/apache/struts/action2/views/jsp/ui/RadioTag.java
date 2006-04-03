/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.Radio;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see Radio
 * @author $author$
 * @version $Date: 2005/12/16 17:48:21 $ $Id: RadioTag.java,v 1.10 2005/12/16 17:48:21 tmjee Exp $
 */
public class RadioTag extends AbstractRequiredListTag {
	
	private static final long serialVersionUID = -6497403399521333624L;

	public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Radio(stack, req, res);
    }
}
