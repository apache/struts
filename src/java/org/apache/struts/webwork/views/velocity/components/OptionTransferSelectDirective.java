/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.OptionTransferSelect;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * @author tm_jee
 * @author Rainer Hermanns
 * @version $Date: 2006/01/31 20:51:25 $ $Id: OptionTransferSelectDirective.java,v 1.2 2006/01/31 20:51:25 rainerh Exp $
 * @see OptionTransferSelect
 */
public class OptionTransferSelectDirective extends AbstractDirective {

	public String getBeanName() {
		return "optiontransferselect";
	}

	protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new OptionTransferSelect(stack, req, res);
	}

}
