/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.showcase.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Employee.
 */

public class Employee implements IdEntity {

	private static final long serialVersionUID = -6226845151026823748L;

	private Long empId; //textfield w/ conversion
	private String firstName;
	private String lastName;
	private Date birthDate; //datepicker
	private Float salary; //textfield w/ conversion
	private boolean married; //checkbox
	private String position; //combobox
	private Skill mainSkill; //select
	private List otherSkills; //doubleSelect
	private String password; //password
	private String level; //radio
	private String comment; //textarea

	public Employee() {
	}

	public Employee(Long empId, String firstName, String lastName) {
		this.empId = empId;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Employee(Long empId, String firstName, String lastName, Date birthDate, Float salary, boolean married, String position, Skill mainSkill, List otherSkills, String password, String level, String comment) {
		this.empId = empId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.salary = salary;
		this.married = married;
		this.position = position;
		this.mainSkill = mainSkill;
		this.otherSkills = otherSkills;
		this.password = password;
		this.level = level;
		this.comment = comment;
	}

	public Long getEmpId() {
		return empId;
	}

	public void setEmpId(Long empId) {
		this.empId = empId;
	}

	public Serializable getId() {
		return getEmpId();
	}

	public void setId(Serializable id) {
		setEmpId((Long) id);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Float getSalary() {
		return salary;
	}

	public void setSalary(Float salary) {
		this.salary = salary;
	}

	public boolean isMarried() {
		return married;
	}

	public void setMarried(boolean married) {
		this.married = married;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Skill getMainSkill() {
		return mainSkill;
	}

	public void setMainSkill(Skill mainSkill) {
		this.mainSkill = mainSkill;
	}

	public List getOtherSkills() {
		return otherSkills;
	}

	public void setOtherSkills(List otherSkills) {
		this.otherSkills = otherSkills;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
