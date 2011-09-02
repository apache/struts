/*
 * Copyright 2002-2007,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.validator;

/**
 * The FieldValidator interface defines the methods to be implemented by FieldValidators.
 * Which are used by the XWork validation framework to validate Action properties before
 * executing the Action.
 */
public interface FieldValidator extends Validator {

    /**
     * Sets the field name to validate with this FieldValidator
     *
     * @param fieldName the field name
     */
    void setFieldName(String fieldName);

    /**
     * Gets the field name to be validated
     *
     * @return the field name
     */
    String getFieldName();

}
