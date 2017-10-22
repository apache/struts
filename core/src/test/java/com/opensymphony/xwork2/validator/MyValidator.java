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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * TODO lukaszlenart: write a JavaDoc
 */
public class MyValidator implements FieldValidator, ShortCircuitableValidator {

    private String message;
    private String fieldName;
    private String key;
    private String[] messageParameters;
    private ValidatorContext validatorContext;
    private String type;
    private ValueStack stack;
    private boolean shortcircuit;

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setDefaultMessage(String message) {
        this.message = message;
    }

    public String getDefaultMessage() {
        return message;
    }

    public String getMessage(Object object) {
        return "Message";
    }

    public void setMessageKey(String key) {
        this.key = key;
    }

    public String getMessageKey() {
        return key;
    }

    public void setMessageParameters(String[] messageParameters) {
        this.messageParameters = messageParameters;
    }

    public String[] getMessageParameters() {
        return messageParameters;
    }

    public void setValidatorContext(ValidatorContext validatorContext) {
        this.validatorContext = validatorContext;
    }

    public ValidatorContext getValidatorContext() {
        return validatorContext;
    }

    public void validate(Object object) throws ValidationException {
        // pass
    }

    public void setValidatorType(String type) {
        this.type = type;
    }

    public String getValidatorType() {
        return type;
    }

    public void setValueStack(ValueStack stack) {
        this.stack = stack;
    }

    public void setShortCircuit(boolean shortcircuit) {
        this.shortcircuit = shortcircuit;
    }

    public boolean isShortCircuit() {
        return shortcircuit;
    }
}
