package org.apache.struts.action2.showcase.jsf;

import java.util.Arrays;
import java.util.List;

public class Employee {
	private String name;
	private int id;
	private List skills;
	
	public Employee() {}
	
	public Employee(int id, String name, String[] skills) {
		this.id = id;
		this.name = name;
		this.skills = Arrays.asList(skills);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List getSkills() {
		return skills;
	}
	public void setSkills(List skills) {
		this.skills = skills;
	}
}
