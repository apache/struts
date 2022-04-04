package org.apache.struts2.oval.interceptor.domain;

import net.sf.oval.constraint.MinLength;

public class Address {

	@MinLength(value=7)
	private String street;

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreet() {
		return street;
	}

}
