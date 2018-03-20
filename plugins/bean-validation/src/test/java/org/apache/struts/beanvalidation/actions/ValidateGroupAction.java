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
package org.apache.struts.beanvalidation.actions;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.struts.beanvalidation.constraints.ValidationGroup;
import org.apache.struts.beanvalidation.models.Person;

import javax.validation.Valid;

public class ValidateGroupAction extends ActionSupport implements ModelDriven<Person> {

    @Valid
    private Person model = new Person();

    public Person getModel() {
        return model;
    }

    public String actionStandard() {
        return SUCCESS;
    }

    @ValidationGroup
    public String actionDefault() {
        return SUCCESS;
    }

    @ValidationGroup(Person.NameChecks.class)
    public String actionNameChecks() {
        return SUCCESS;
    }

    @ValidationGroup(Person.StreetChecks.class)
    public String actionStreetChecks() {
        return SUCCESS;
    }

    @ValidationGroup(Person.NameAndStreetChecks.class)
    public String actionNameAndStreetChecks() {
        return SUCCESS;
    }

    @ValidationGroup({Person.NameChecks.class, Person.FirstNameChecks.class})
    public String actionMultiGroupsChecks() {
        return SUCCESS;
    }

    @ValidationGroup({Person.LongNameChecks.class})
    public String actionLongNameChecks() {
        return SUCCESS;
    }
}