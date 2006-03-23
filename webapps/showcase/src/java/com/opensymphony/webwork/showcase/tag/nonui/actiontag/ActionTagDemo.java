/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.showcase.tag.nonui.actiontag;

import com.opensymphony.xwork.ActionSupport;

/**
 * @author tm_jee
 * @version $Date: 2005/12/24 11:55:00 $ $Id: ActionTagDemo.java,v 1.1 2005/12/24 11:55:00 tmjee Exp $
 */
public class ActionTagDemo extends ActionSupport {
	
	public String show() throws Exception {
		return SUCCESS;
	}
	
	public String doInclude() throws Exception {
		return SUCCESS;
	}
}	
