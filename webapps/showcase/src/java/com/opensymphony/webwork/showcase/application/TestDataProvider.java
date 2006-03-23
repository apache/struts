/* ====================================================================
 * The OpenSymphony Software License, Version 1.1
 *
 * (this license is derived and fully compatible with the Apache Software
 * License - see http://www.apache.org/LICENSE.txt)
 *
 * Copyright (c) 2001-2005 The OpenSymphony Group. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        OpenSymphony Group (http://www.opensymphony.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "OpenSymphony" and "The OpenSymphony Group"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact license@opensymphony.com .
 *
 * 5. Products derived from this software may not be called "OpenSymphony"
 *    or "WebWork", nor may "OpenSymphony" or "WebWork" appear in their
 *    name, without prior written permission of the OpenSymphony Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package com.opensymphony.webwork.showcase.application;

import com.opensymphony.webwork.showcase.dao.EmployeeDao;
import com.opensymphony.webwork.showcase.dao.SkillDao;
import com.opensymphony.webwork.showcase.exception.StorageException;
import com.opensymphony.webwork.showcase.model.Employee;
import com.opensymphony.webwork.showcase.model.Skill;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;
import java.util.Date;
import java.util.Arrays;

/**
 * TestDataProvider.
 *
 * @author <a href="mailto:gielen@it-neering.net">Rene Gielen</a>
 */

public class TestDataProvider implements Serializable, InitializingBean {

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
            new Skill("WW-SEN", "WebWork Senior Developer"),
            new Skill("WW-JUN", "WebWork Junior Developer"),
            new Skill("SPRING-DEV", "Spring Developer")
    };

    public static final Employee[] TEST_EMPLOYEES = {
            new Employee(new Long(1), "Alan", "Smithee", new Date(), new Float(2000f), true, POSITIONS[0],
                    TEST_SKILLS[0], null, "alan", LEVELS[0], "Nice guy"),
            new Employee(new Long(2), "Robert", "Robson", new Date(), new Float(10000f), false, POSITIONS[1],
                    TEST_SKILLS[1], Arrays.asList(TEST_SKILLS).subList(1,TEST_SKILLS.length), "rob", LEVELS[1], "Smart guy")
    };

    private SkillDao skillDao;
    private EmployeeDao employeeDao;

    public void setSkillDao(SkillDao skillDao) {
        this.skillDao = skillDao;
    }

    public void setEmployeeDao(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

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
