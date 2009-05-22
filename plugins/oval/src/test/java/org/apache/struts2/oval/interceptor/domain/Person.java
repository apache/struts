package org.apache.struts2.oval.interceptor.domain;

import javax.persistence.Column;

import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.NotNull;

public class Person {

	@Column(nullable=false)
	private String name;

	@NotNull
	private String email;

	@AssertValid
	private Address address = new Address();

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}

}
