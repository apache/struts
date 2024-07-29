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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.showcase.dao.EmployeeDao;
import org.apache.struts2.showcase.dao.SkillDao;
import org.apache.struts2.showcase.exception.StorageException;
import org.apache.struts2.showcase.model.Employee;
import org.apache.struts2.showcase.model.Skill;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * TestDataProvider.
 */
@Service
public class TestDataProvider implements Serializable, InitializingBean {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger log = LogManager.getLogger(TestDataProvider.class);

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
            new Employee(1L, "Alan", "Smithee", new Date(), 2000f, true, POSITIONS[0],
                    TEST_SKILLS[0], null, "alan", LEVELS[0], "Nice guy"),
            new Employee(2L, "Robert", "Robson", new Date(), 10000f, false, POSITIONS[1],
                    TEST_SKILLS[1], List.of(TEST_SKILLS).subList(1, TEST_SKILLS.length), "rob", LEVELS[1], "Smart guy")
    };

    @Autowired
    private SkillDao skillDao;

    @Autowired
    private EmployeeDao employeeDao;

    protected void addTestSkills() {
        try {
            for (Skill testSkill : TEST_SKILLS) {
                skillDao.merge(testSkill);
            }
            if (log.isInfoEnabled()) {
                log.info("TestDataProvider - [addTestSkills]: Added test skill data.");
            }
        } catch (StorageException e) {
            log.error("TestDataProvider - [addTestSkills]: Exception caught: {}", e.getMessage());
        }
    }

    protected void addTestEmployees() {
        try {
            for (Employee testEmployee : TEST_EMPLOYEES) {
                employeeDao.merge(testEmployee);
            }
            if (log.isInfoEnabled()) {
                log.info("TestDataProvider - [addTestEmployees]: Added test employee data.");
            }
        } catch (StorageException e) {
            log.error("TestDataProvider - [addTestEmployees]: Exception caught: {}", e.getMessage());
        }
    }

    protected void addTestData() {
        addTestSkills();
        addTestEmployees();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        addTestData();
    }

}
