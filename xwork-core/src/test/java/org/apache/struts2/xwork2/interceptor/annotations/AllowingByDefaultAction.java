package org.apache.struts2.xwork2.interceptor.annotations;

import org.apache.struts2.xwork2.ActionSupport;

/**
 * @author martin.gilday
 *
 */
public class AllowingByDefaultAction extends ActionSupport {
	
	@Blocked
	private String name;
	private String job;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setJob(String job) {
		this.job = job;
	}

}
