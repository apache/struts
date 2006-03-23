/**
 * 
 */
package com.opensymphony.webwork.dispatcher;


import javax.servlet.http.HttpServletRequest;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionSupport;

/**
 * A simple action support class that sets properties to be able to serve 
 * 
 * @author amarch
 *
 */
public class DefaultActionSupport extends ActionSupport {

	String successResultValue;
	
	
	/**
	 * 
	 */
	public DefaultActionSupport() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.opensymphony.xwork.ActionSupport#execute()
	 */
	public String execute() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String requestedUrl = request.getPathInfo();
		if (successResultValue == null) successResultValue = requestedUrl;
		return SUCCESS;
	}

	/**
	 * @return Returns the successResultValue.
	 */
	public String getSuccessResultValue() {
		return successResultValue;
	}

	/**
	 * @param successResultValue The successResultValue to set.
	 */
	public void setSuccessResultValue(String successResultValue) {
		this.successResultValue = successResultValue;
	}
	

}
