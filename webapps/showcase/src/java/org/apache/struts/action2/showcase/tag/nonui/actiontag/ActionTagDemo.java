/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.showcase.tag.nonui.actiontag;

import com.opensymphony.xwork.ActionSupport;

/**
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class ActionTagDemo extends ActionSupport {
	
	private static final long serialVersionUID = -2749145880590245184L;

	public String show() throws Exception {
		return SUCCESS;
	}
	
	public String doInclude() throws Exception {
		return SUCCESS;
	}
}	
