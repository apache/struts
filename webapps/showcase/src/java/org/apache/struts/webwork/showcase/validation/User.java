/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.showcase.validation;

import java.sql.Date;

/**
 * @author tm_jee
 * @version $Date: 2005/12/22 09:17:56 $ $Id: User.java,v 1.1 2005/12/22 09:17:56 tmjee Exp $
 */
public class User {
	
	private String name;
	private Integer age;
	private Date birthday;
	
	
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

