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
package org.apache.struts2.showcase.application;

import org.apache.log4j.Logger;
import org.apache.struts2.showcase.dao.EmployeeDao;
import org.apache.struts2.showcase.dao.SkillDao;
import org.apache.struts2.showcase.exception.StorageException;
import org.apache.struts2.showcase.model.Employee;
import org.apache.struts2.showcase.model.Skill;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * TestDataProvider.
 */
@Service
public class TestDataProvider implements Serializable, InitializingBean {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(TestDataProvider.class);

	public static final String[] POSITIONS = {
			"Developer",
			"System Architect",
			"Sales Manager",
			"CEO"
	};

	public static final String[] LEVELS = {
			"Junior",
			"Senior",
			"Master"
	};

	private static final Skill[] TEST_SKILLS = {
			new Skill("WW-SEN", "Struts Senior Developer"),
			new Skill("WW-JUN", "Struts Junior Developer"),
			new Skill("SPRING-DEV", "Spring Developer")
	};

	public static final Employee[] TEST_EMPLOYEES = {
			new Employee(new Long(1), "Alan", "Smithee", new Date(), new Float(2000f), true, POSITIONS[0],
					TEST_SKILLS[0], null, "alan", LEVELS[0], "Nice guy"),
			new Employee(new Long(2), "Robert", "Robson", new Date(), new Float(10000f), false, POSITIONS[1],
					TEST_SKILLS[1], Arrays.asList(TEST_SKILLS).subList(1, TEST_SKILLS.length), "rob", LEVELS[1], "Smart guy")
	};

	@Autowired
	private SkillDao skillDao;

	@Autowired
	private EmployeeDao employeeDao;

	protected void addTestSkills() {
		try {
			for (int i = 0, j = TEST_SKILLS.length; i < j; i++) {
				skillDao.merge(TEST_SKILLS[i]);
			}
			if (log.isInfoEnabled()) {
				log.info("TestDataProvider - [addTestSkills]: Added test skill data.");
			}
		} catch (StorageException e) {
			log.error("TestDataProvider - [addTestSkills]: Exception catched: " + e.getMessage());
		}
	}

	protected void addTestEmployees() {
		try {
			for (int i = 0, j = TEST_EMPLOYEES.length; i < j; i++) {
				employeeDao.merge(TEST_EMPLOYEES[i]);
			}
			if (log.isInfoEnabled()) {
				log.info("TestDataProvider - [addTestEmployees]: Added test employee data.");
			}
		} catch (StorageException e) {
			log.error("TestDataProvider - [addTestEmployees]: Exception catched: " + e.getMessage());
		}
	}

	protected void addTestData() {
		addTestSkills();
		addTestEmployees();
	}

	public void afterPropertiesSet() throws Exception {
		addTestData();
	}

}
