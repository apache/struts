/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;


import com.opensymphony.webwork.components.ListUIBean;

/**
 * 
 * @author tm_jee
 * @version $Date: 2005/12/16 17:48:21 $ $Id: AbstractRequiredListTag.java,v 1.1 2005/12/16 17:48:21 tmjee Exp $
 */
public abstract class AbstractRequiredListTag extends AbstractListTag {

	protected void populateParams() {
		super.populateParams();
		
		ListUIBean listUIBean = (ListUIBean) component;
		listUIBean.setThrowExceptionOnNullValueAttribute(true);
	}

}
