/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.showcase.validation;

/**
 * @author tm_jee
 * @version $Date: 2005/12/22 11:36:42 $ $Id: VisitorValidatorsExampleAction.java,v 1.2 2005/12/22 11:36:42 tmjee Exp $
 */

// START SNIPPET: visitorValidatorsExample

public class VisitorValidatorsExampleAction extends AbstractValidationActionSupport {

	private static final long serialVersionUID = 4375454086939598216L;
	
	private User user;
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
}


// END SNIPPET: visitorValidatorsExample
