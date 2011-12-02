package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

public class SpringResult implements Result {

	private static final long serialVersionUID = -2877126768401198951L;

	private boolean initialize = false;

	//  this String should be populated by spring
	private String stringParameter;

	public void initialize() {
		// this method should be called by spring
		this.initialize = true;
	}

	public void execute(ActionInvocation invocation) throws Exception {
		// intetionally empty
	}

	public void setStringParameter(String stringParameter) {
		this.stringParameter = stringParameter;
	}

	public String getStringParameter() {
		return this.stringParameter;
	}

	public boolean isInitialize() {
		return this.initialize;
	}
}

