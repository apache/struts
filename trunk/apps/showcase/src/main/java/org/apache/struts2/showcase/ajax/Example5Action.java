package org.apache.struts2.showcase.ajax;

import com.opensymphony.xwork2.ActionSupport;

public class Example5Action extends ActionSupport {

	private static final long serialVersionUID = 2111967621952300611L;
	
	private String name;
	private Integer age;
	
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public Integer getAge() { return age; }
	public void setAge(Integer age) { this.age = age; }
	
	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}
}
