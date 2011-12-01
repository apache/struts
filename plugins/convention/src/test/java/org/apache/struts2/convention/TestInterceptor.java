package org.apache.struts2.convention;

import org.apache.struts2.xwork2.ActionInvocation;
import org.apache.struts2.xwork2.interceptor.AbstractInterceptor;

public class TestInterceptor extends AbstractInterceptor {
	private String string1;
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		return null;
	}

	public String getString1() {
		return string1;
	}

	public void setString1(String string1) {
		this.string1 = string1;
	}
}
