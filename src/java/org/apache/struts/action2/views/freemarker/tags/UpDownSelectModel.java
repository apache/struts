/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.webwork.views.freemarker.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.UpDownSelect;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * 
 * @author tm_jee
 * @version $Date: 2006/02/01 16:30:08 $ $Id: UpDownSelectModel.java,v 1.1 2006/02/01 16:30:08 tmjee Exp $
 */
public class UpDownSelectModel extends TagModel {

	public UpDownSelectModel(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		super(stack, req, res);
	}

	protected Component getBean() {
		return new UpDownSelect(stack, req, res);
	}

}
