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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.convention.annotation.Results;

/**
 * <code>EditPerson</code>
 *
 */
@Results({
    @Result(name="list", location="list-people.action", type="redirect"),
    @Result(name="input", location="new-person.ftl", type="freemarker")
})
public class EditPersonAction extends ActionSupport {

    private static final long serialVersionUID = 7699491775215130850L;

    PersonManager personManager;
    List persons = new ArrayList();

    public void setPersonManager(PersonManager personManager) {
        this.personManager = personManager;
    }

    public List getPersons() {
        return persons;
    }

    public void setPersons(List persons) {
        this.persons = persons;
    }

    /**
     * A default implementation that does nothing an returns "success".
     *
     * @return {@link #SUCCESS}
     */
    public String execute() throws Exception {
        persons.addAll(personManager.getPeople());
        return SUCCESS;
    }

    /**
     * A default implementation that does nothing an returns "success".
     *
     * @return {@link #SUCCESS}
     */
    public String save() throws Exception {

        // Set people = personManager.getPeople();

        for ( Iterator iter = persons.iterator(); iter.hasNext();) {
            Person p = (Person) iter.next();
            personManager.getPeople().remove(p);
            personManager.getPeople().add(p);
        }
        return "list";
    }

}
