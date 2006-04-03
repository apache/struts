/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.showcase.validation;

import com.opensymphony.xwork.ActionSupport;

/**
 * @author tm_jee
 * @version $Date$ $Id$
 */
public abstract class AbstractValidationActionSupport extends ActionSupport {
	
	public String submit() throws Exception {
		return "success";
	}
	
	public String input() throws Exception {
		return "input";
	}
}
