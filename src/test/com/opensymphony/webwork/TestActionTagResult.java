package com.opensymphony.webwork;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;

public class TestActionTagResult implements Result {

	private boolean _isExecuted;
	
	public void execute(ActionInvocation invocation) throws Exception {
		_isExecuted = true;
	}
	
	public boolean isExecuted() {
		return _isExecuted;
	}

	public void reset() {
		_isExecuted = false;
	}
}
