/*
 * $Id: QuizAction.java 394498 2006-04-16 15:28:06Z tmjee $
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.showcase.jsf;

import java.util.List;

import com.opensymphony.xwork.ActionSupport;

public class EditEmployeeAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private Employee employee;

    private int id;

    public EditEmployeeAction() {
        employee = new Employee();
    }

    public Employee getEmployee() {
        return employee;
    }

    public String[] getAllSkills() {
        return EmployeeDao.getSkills();
    }

    public void setId(int id) {
        this.id = id;
    }

    public String execute() {
        if (id > 0) {
            this.employee = EmployeeDao.getEmployee(id);
        }
        return SUCCESS;
    }

    public String save() {
        EmployeeDao.save(employee);
        return "index";
    }

}
