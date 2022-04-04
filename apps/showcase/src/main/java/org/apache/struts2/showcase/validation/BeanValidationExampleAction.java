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
package org.apache.struts2.showcase.validation;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts.beanvalidation.constraints.FieldMatch;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.ScriptAssert;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * <!-- START SNIPPET: beanValidatationExample -->
 */
@Namespace("/bean-validation")
@ParentPackage("bean-validation")
@Action(results = {
        @Result(name = "input", location = "bean-validation.jsp"),
        @Result(name = "success", location = "/WEB-INF/validation/successFieldValidatorsExample.jsp")
})
@FieldMatch(first = "fieldExpressionValidatorField", second = "requiredValidatorField", message = "requiredValidatorField and fieldExpressionValidatorField are not matching")
@ScriptAssert(lang = "javascript", script = "_this.dateValidatorField != null && _this.dateValidatorField.before(new java.util.Date())", message = "Date need to before now")
public class BeanValidationExampleAction extends ActionSupport {

    @NotNull
    private String requiredValidatorField = null;

    @NotBlank
    private String requiredStringValidatorField = null;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer integerValidatorField = null;

    @NotNull
    private Date dateValidatorField = null;

    @NotNull
    @Size(min = 4, max = 64)
    @Email
    private String emailValidatorField = null;

    @NotNull
    @Size(min = 4, max = 64)
    @URL
    private String urlValidatorField = null;

    @NotNull
    @Size(min = 2, max = 4)
    private String stringLengthValidatorField = null;

    @Pattern(regexp = "[^<>]+")
    private String regexValidatorField = null;

    private String fieldExpressionValidatorField = null;

    @Action(value = "bean-validation", results = {
            @Result(name = "success", location = "bean-validation.jsp")
    })
    @SkipValidation
    public String beanValidation(){
        return SUCCESS;
    }

    public Date getDateValidatorField() {
        return dateValidatorField;
    }

    public void setDateValidatorField(Date dateValidatorField) {
        this.dateValidatorField = dateValidatorField;
    }

    public String getEmailValidatorField() {
        return emailValidatorField;
    }

    public void setEmailValidatorField(String emailValidatorField) {
        this.emailValidatorField = emailValidatorField;
    }

    public Integer getIntegerValidatorField() {
        return integerValidatorField;
    }

    public void setIntegerValidatorField(Integer integerValidatorField) {
        this.integerValidatorField = integerValidatorField;
    }

    public String getRegexValidatorField() {
        return regexValidatorField;
    }

    public void setRegexValidatorField(String regexValidatorField) {
        this.regexValidatorField = regexValidatorField;
    }

    public String getRequiredStringValidatorField() {
        return requiredStringValidatorField;
    }

    public void setRequiredStringValidatorField(String requiredStringValidatorField) {
        this.requiredStringValidatorField = requiredStringValidatorField;
    }

    public String getRequiredValidatorField() {
        return requiredValidatorField;
    }

    public void setRequiredValidatorField(String requiredValidatorField) {
        this.requiredValidatorField = requiredValidatorField;
    }

    public String getStringLengthValidatorField() {
        return stringLengthValidatorField;
    }

    public void setStringLengthValidatorField(String stringLengthValidatorField) {
        this.stringLengthValidatorField = stringLengthValidatorField;
    }

    public String getFieldExpressionValidatorField() {
        return fieldExpressionValidatorField;
    }

    public void setFieldExpressionValidatorField(
            String fieldExpressionValidatorField) {
        this.fieldExpressionValidatorField = fieldExpressionValidatorField;
    }

    public String getUrlValidatorField() {
        return urlValidatorField;
    }

    public void setUrlValidatorField(String urlValidatorField) {
        this.urlValidatorField = urlValidatorField;
    }
}

/**
 * <!-- END SNIPPET: beanValidatationExample -->
 */


