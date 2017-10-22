/*
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
package org.apache.struts2.showcase.action;

import com.opensymphony.xwork2.Preparable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.showcase.application.TestDataProvider;
import org.apache.struts2.showcase.dao.Dao;
import org.apache.struts2.showcase.dao.EmployeeDao;
import org.apache.struts2.showcase.model.Employee;
import org.apache.struts2.showcase.model.Skill;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * JsfEmployeeAction.
 */

public class EmployeeAction extends AbstractCRUDAction implements Preparable {

	private static final long serialVersionUID = 7047317819789938957L;

	private static final Logger log = LogManager.getLogger(EmployeeAction.class);

	@Autowired
	private EmployeeDao employeeDao;

	private Long empId;
	private Employee currentEmployee;
	private List<String> selectedSkills;

	public String execute() throws Exception {
		if (getCurrentEmployee() != null && getCurrentEmployee().getOtherSkills() != null) {
			setSelectedSkills(new ArrayList<String>());
			Iterator it = getCurrentEmployee().getOtherSkills().iterator();
			while (it.hasNext()) {
				getSelectedSkills().add(((Skill) it.next()).getName());
			}
		}
		return super.execute();
	}

	public String save() throws Exception {
		if (getCurrentEmployee() != null) {
			setEmpId((Long) employeeDao.merge(getCurrentEmployee()));
			employeeDao.setSkills(getEmpId(), getSelectedSkills());
		}
		return SUCCESS;
	}

	public Long getEmpId() {
		return empId;
	}

	public void setEmpId(Long empId) {
		this.empId = empId;
	}

	public Employee getCurrentEmployee() {
		return currentEmployee;
	}

	public void setCurrentEmployee(Employee currentEmployee) {
		this.currentEmployee = currentEmployee;
	}

	public String[] getAvailablePositions() {
		return TestDataProvider.POSITIONS;
	}

	public List getAvailableLevels() {
		return Arrays.asList(TestDataProvider.LEVELS);
	}

	public List<String> getSelectedSkills() {
		return selectedSkills;
	}

	public void setSelectedSkills(List<String> selectedSkills) {
		this.selectedSkills = selectedSkills;
	}

	protected Dao getDao() {
		return employeeDao;
	}

	/**
	 * This method is called to allow the action to prepare itself.
	 *
	 * @throws Exception thrown if a system level exception occurs.
	 */
	public void prepare() throws Exception {
		Employee preFetched = (Employee) fetch(getEmpId(), getCurrentEmployee());
		if (preFetched != null) {
			setCurrentEmployee(preFetched);
		}
	}

}
