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
package org.apache.struts2.oval.interceptor;

import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import com.opensymphony.xwork2.ActionSupport;

public class SimpleField extends ActionSupport{
    @NotNull()
    @NotEmpty
    @Length(max = 3)
    private String name;
    private boolean validateCalled;
    private boolean validateExecuteCalled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void validate() {
        this.validateCalled = true;
    }

    public void validateExecute() {
        this.validateExecuteCalled = true;
    }

    public boolean isValidateCalled() {
        return validateCalled;
    }

    public boolean isValidateExecuteCalled() {
        return validateExecuteCalled;
    }
}
