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
package com.opensymphony.xwork2.validator.validators;

import org.apache.commons.lang3.StringUtils;

/**
 * CreditCardFieldValidator checks that a given String/Array/Collection field,
 * if not empty, is a valid credit card number.
 */
public class CreditCardValidator extends RegexFieldValidator {

    public static final String CREDIT_CARD_PATTERN =
                    "^(?:4[0-9]{12}(?:[0-9]{3})?" + // Visa
                    "|(?:5[1-5][0-9]{2}" + // MasterCard
                    "|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}" +
                    "|3[47][0-9]{13}" + // American Express
                    "|3(?:0[0-5]|[68][0-9])[0-9]{11}" + // Diners Club
                    "|6(?:011|5[0-9]{2})[0-9]{12}" + // Discover
                    "|(?:2131|1800|35\\d{3})\\d{11}" + // JCB
                    ")$";

    public CreditCardValidator() {
        setRegex(CREDIT_CARD_PATTERN);
        setCaseSensitive(false);
    }

    protected void validateFieldValue(Object object, String value, String regexToUse) {
        super.validateFieldValue(object, StringUtils.deleteWhitespace(value), regexToUse);
    }

}
