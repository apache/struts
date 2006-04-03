/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;


import org.apache.struts.action2.components.ListUIBean;

/**
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public abstract class AbstractRequiredListTag extends AbstractListTag {

	protected void populateParams() {
		super.populateParams();
		
		ListUIBean listUIBean = (ListUIBean) component;
		listUIBean.setThrowExceptionOnNullValueAttribute(true);
	}

}
