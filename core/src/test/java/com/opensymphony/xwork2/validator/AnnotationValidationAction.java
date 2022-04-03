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

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.ConditionalVisitorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.CreditCardValidator;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;
import com.opensymphony.xwork2.validator.annotations.DateRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.DoubleRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.EmailValidator;
import com.opensymphony.xwork2.validator.annotations.ExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.LongRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.ShortRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.UrlValidator;
import com.opensymphony.xwork2.validator.annotations.ValidationParameter;
import com.opensymphony.xwork2.validator.annotations.VisitorFieldValidator;

/**
 * Sets up all available validation annotations
 */
public class AnnotationValidationAction extends ActionSupport {

    @RegexFieldValidator(regex = "foo", message = "Foo doesn't match!", key = "regex.key",
            fieldName = "bar", shortCircuit = true, trim = false, caseSensitive = false,
            messageParams = {"one", "two", "three"})
    @ConditionalVisitorFieldValidator(expression = "foo+bar", context = "some", appendPrefix = false, fieldName = "bar",
            key = "conditional.key", message = "Foo doesn't match!", shortCircuit = true,
            messageParams = {"one", "two", "three"})
    @ConversionErrorFieldValidator(fieldName = "bar", key = "conversion.key", message = "Foo conversion error!",
            shortCircuit = true, repopulateField = true, messageParams = {"one", "three"})
    @CustomValidator(type = "myValidator", fieldName = "foo", key = "foo.invalid", message = "Foo is invalid!",
            shortCircuit = true, messageParams = {"one", "two", "three"},
            parameters = {
                    @ValidationParameter(name = "value", value = "1")
            }
    )
    @DateRangeFieldValidator(fieldName = "foo", key = "date.foo", max = "2012", min = "2011", dateFormat = "yyyy",
            message = "Foo isn't in range!", shortCircuit = true, messageParams = {"one", "two", "three"})
    @DoubleRangeFieldValidator(minExclusive = "1.2", maxExclusive = "1.4", minInclusive = "0", maxInclusive = "0.1",
            fieldName = "foo", key = "double.key", message = "Foo is out of range!", shortCircuit = true,
            messageParams = {"one", "two", "three"})
    @EmailValidator(message = "Foo isn't a valid e-mail!", fieldName = "foo", key = "email.key",
            messageParams = {"one", "two", "three"}, shortCircuit = true)
    @CreditCardValidator(message = "Foo isn't a valid credit card!", fieldName = "foo", key = "creditCard.key",
            messageParams = {"one", "two", "three"}, shortCircuit = true)
    @ExpressionValidator(expression = "true", message = "Is not true!", key = "expression.key",
            messageParams = {"one", "two", "three"}, shortCircuit = true)
    @FieldExpressionValidator(expression = "true", fieldName = "foo", key = "fieldexpression.key", message = "It is not true!",
            messageParams = {"one", "two", "three"}, shortCircuit = true)
    @IntRangeFieldValidator(fieldName = "foo", key = "int.key", message = "Foo is out of range!", max = "10", min = "1",
            messageParams = {"one", "two", "three"}, shortCircuit = true)
    @LongRangeFieldValidator(fieldName = "foo", key = "int.key", message = "Foo is out of range!", max = "10", min = "1",
            messageParams = {"one", "two", "three"}, shortCircuit = true)
    @RequiredFieldValidator(fieldName = "foo", key = "required.key", message = "Foo is required!",
            messageParams = {"one", "two", "three"}, shortCircuit = true)
    @RequiredStringValidator(fieldName = "foo", key = "requiredstring.key", message = "Foo is required!",
            messageParams = {"one", "two", "three"}, shortCircuit = true, trim = false)
    @ShortRangeFieldValidator(fieldName = "foo", key = "short.key", message = "Foo is out of range!", min = "1", max = "10",
            messageParams = {"one", "two", "three"}, shortCircuit = true)
    @StringLengthFieldValidator(fieldName = "foo", key = "stringlength.key", message = "Foo is too long!",
            maxLength = "10", minLength = "1", shortCircuit = true, trim = false, messageParams = {"one", "two", "three"})
    @UrlValidator(fieldName = "foo", key = "url.key", message = "Foo isn't a valid URL!", shortCircuit = true,
            messageParams = {"one", "two", "three"})
    @VisitorFieldValidator(message = "Foo isn't valid!", key = "visitorfield.key", fieldName = "foo", appendPrefix = false,
            shortCircuit = true, messageParams = {"one", "two", "three"})
    public String execute() {
        return SUCCESS;
    }

}
