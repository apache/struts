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
package org.apache.struts2.showcase.person;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>EditPerson</code>
 */
@Results({
		@Result(name = "list", location = "list-people.action", type = "redirect"),
		@Result(name = "input", location = "edit-person.jsp")
})
public class EditPersonAction extends ActionSupport {

	private static final long serialVersionUID = 7699491775215130850L;

	@Autowired
	private PersonManager personManager;
	private List<Person> persons = new ArrayList<Person>();

	/**
	 * A default implementation that does nothing an returns "success".
	 *
	 * @return {@link #INPUT}
	 */
	public String execute() throws Exception {
		persons.addAll(personManager.getPeople());
		return INPUT;
	}

	/**
	 * A default implementation that does nothing an returns "success".
	 *
	 * @return {@link #SUCCESS}
	 */
	public String save() throws Exception {

		for (Person p : persons) {
			personManager.getPeople().remove(p);
			personManager.getPeople().add(p);
		}
		return "list";
	}

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}
}
