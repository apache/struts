/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.freemarker.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.OptionTransferSelect;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * @author tm_jee
 * @version $Date: 2006/01/07 15:08:36 $ $Id: OptionTransferSelectModel.java,v 1.1 2006/01/07 15:08:36 tmjee Exp $
 * @see OptionTransferSelect
 */
public class OptionTransferSelectModel extends TagModel {

	public OptionTransferSelectModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		super(stack, req, res);
	}

	protected Component getBean() {
		return new OptionTransferSelect(stack, req, res);
	}

}
