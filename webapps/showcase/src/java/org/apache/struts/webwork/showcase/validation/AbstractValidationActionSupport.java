/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.showcase.validation;

import com.opensymphony.xwork.ActionSupport;

/**
 * @author tm_jee
 * @version $Date: 2005/12/24 09:13:49 $ $Id: AbstractValidationActionSupport.java,v 1.2 2005/12/24 09:13:49 tmjee Exp $
 */
public abstract class AbstractValidationActionSupport extends ActionSupport {
	
	public String submit() throws Exception {
		return "success";
	}
	
	public String input() throws Exception {
		return "input";
	}
}
