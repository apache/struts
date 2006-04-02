/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp.ui;

import org.apache.struts.webwork.components.CheckboxList;
import org.apache.struts.webwork.components.Component;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see CheckboxList
 * @author $author$
 * @version $Date: 2005/12/16 17:48:21 $ $Id: CheckboxListTag.java,v 1.9 2005/12/16 17:48:21 tmjee Exp $
 */
public class CheckboxListTag extends AbstractRequiredListTag {
	
	private static final long serialVersionUID = 4023034029558150010L;

	public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new CheckboxList(stack, req, res);
    }
}
