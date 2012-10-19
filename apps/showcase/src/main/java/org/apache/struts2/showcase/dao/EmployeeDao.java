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
package org.apache.struts2.showcase.dao;

import org.apache.struts2.showcase.model.Employee;
import org.apache.struts2.showcase.model.Skill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * EmployeeDao.
 */
@Repository
public class EmployeeDao extends AbstractDao {

	private static final long serialVersionUID = -6615310540042830594L;

	@Autowired
	protected SkillDao skillDao;

	public void setSkillDao(SkillDao skillDao) {
		this.skillDao = skillDao;
	}

	public Class getFeaturedClass() {
		return Employee.class;
	}

	public Employee getEmployee(Long id) {
		return (Employee) get(id);
	}

	public Employee setSkills(Employee employee, List<String> skillNames) {
		if (employee != null && skillNames != null) {
			employee.setOtherSkills(new ArrayList());
			for (int i = 0, j = skillNames.size(); i < j; i++) {
				Skill skill = (Skill) skillDao.get(skillNames.get(i));
				employee.getOtherSkills().add(skill);
			}
		}
		return employee;
	}

	public Employee setSkills(Long empId, List skillNames) {
		return setSkills((Employee) get(empId), skillNames);
	}

}
