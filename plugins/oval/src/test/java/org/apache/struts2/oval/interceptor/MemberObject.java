package org.apache.struts2.oval.interceptor;

import net.sf.oval.constraint.AssertValid;

import org.apache.struts2.oval.interceptor.domain.Person;

import com.opensymphony.xwork2.ActionSupport;

public class MemberObject extends ActionSupport {

	@AssertValid
	private Person person = new Person();

	public Person getPerson() {
		return person;
	}

}
