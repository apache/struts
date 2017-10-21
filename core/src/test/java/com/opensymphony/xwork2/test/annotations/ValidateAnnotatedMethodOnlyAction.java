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
package com.opensymphony.xwork2.test.annotations;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * <code>ValidateAnnotatedMethodOnlyAction</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
@Validation
public class ValidateAnnotatedMethodOnlyAction extends ActionSupport {

    String param1;
    String param2;


    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    @ExpressionValidator(expression = "(param1 != null) || (param2 != null)",
            message = "Need param1 or param2.")
    public String annotatedMethod() {
        try {
            // do search
        } catch (Exception e) {
            return INPUT;
        }
        return SUCCESS;
    }

    public String notAnnotatedMethod() {
        try {
            // do different search
        } catch (Exception e) {
            return INPUT;
        }
        return SUCCESS;
    }
}
