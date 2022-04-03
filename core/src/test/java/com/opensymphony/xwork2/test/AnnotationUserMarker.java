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
package com.opensymphony.xwork2.test;

import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * Marker interface to help test hierarchy traversal.
 */
@Validations(
    requiredFields = {
        @RequiredFieldValidator(fieldName = "email", shortCircuit = true, message = "You must enter a value for email."),
        @RequiredFieldValidator(fieldName = "email2", shortCircuit = true, message = "You must enter a value for email2.")
    },
    expressions = {
        @ExpressionValidator(shortCircuit = true, expression = "email.equals(email2)", message = "Email not the same as email2")
    }
)
public interface AnnotationUserMarker {
}
