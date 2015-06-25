package com.opensymphony.xwork2.interceptor.annotations;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author martin.gilday
 *
 */
@BlockByDefault
public class BlockingByDefaultAction extends ActionSupport {
	
	@Allowed
	private String name;
	private String job;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setJob(String job) {
		this.job = job;
	}

}
