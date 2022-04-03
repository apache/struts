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
package org.apache.struts.beanvalidation.models;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

public class Person {

    public interface NameChecks {
    }

    public interface FirstNameChecks {
    }

    public interface StreetChecks {
    }

    public interface NameAndStreetChecks extends NameChecks, StreetChecks {
    }

    public interface LongNameChecks extends Default {
    }

    @NotNull(message = "nameNotNull", groups = {Default.class, NameChecks.class, NameAndStreetChecks.class})
    @Size.List({
            @Size(min = 4, max = 64, message = "nameSize", groups = {Default.class, NameChecks.class, NameAndStreetChecks.class}),
            @Size(min = 20, max = 64, message = "nameSize20", groups = {LongNameChecks.class})

    })
    private String name;

    @NotBlank(message = "firstNameNotBlank", groups = FirstNameChecks.class)
    private String firstName;

    @NotNull(message = "emailNotNull")
    @Email(message = "emailNotValid")
    private String email;

    @Valid
    private Address address = new Address();

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

}
