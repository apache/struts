/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.OptionTransferSelect;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * @author tm_jee
 * @author Rainer Hermanns
 * @version $Date$ $Id$
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
