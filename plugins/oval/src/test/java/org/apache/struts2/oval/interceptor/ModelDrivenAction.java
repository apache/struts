package org.apache.struts2.oval.interceptor;

import net.sf.oval.constraint.AssertValid;

import org.apache.struts2.oval.interceptor.domain.Person;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public class ModelDrivenAction extends ActionSupport implements ModelDriven<Person> {

	@AssertValid
	private Person model = new Person();

	public Person getModel() {
		return model;
	}

}
