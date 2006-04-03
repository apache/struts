/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.showcase.actionchaining;

import com.opensymphony.xwork.ActionSupport;

/**
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class ActionChain2 extends ActionSupport {

	private static final long serialVersionUID = 3951745956044674809L;
	
	private String actionChain1Property1;
	private String actionChain2Property1 = "Property Set in Action Chain 2";
	
	
	public String getActionChain1Property1() { 
		return actionChain1Property1;
	}
	public void setActionChain1Property1(String actionChain1Property1) {
		this.actionChain1Property1 = actionChain1Property1;
	}
	
	
	
	public String getActionChain2Property1() { 
		return actionChain2Property1;
	}
	public void setActionChain2Property1(String actionChain2Property1) {
		this.actionChain2Property1 = actionChain2Property1;
	}
	
	
	
	
	public String execute() throws Exception {
		return SUCCESS;
	}
}
