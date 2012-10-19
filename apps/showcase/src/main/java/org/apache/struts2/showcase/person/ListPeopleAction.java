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
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Result(location = "list-people.ftl", type = "freemarker")
public class ListPeopleAction extends ActionSupport {

	private static final long serialVersionUID = 3608017189783645371L;

	@Autowired
	private PersonManager personManager;

	private List<Person> people = new ArrayList<Person>();

	public String execute() {
		people.addAll(personManager.getPeople());

		return SUCCESS;
	}

	public List<Person> getPeople() {
		return people;
	}

	public int getPeopleCount() {
		return people.size();
	}

}
