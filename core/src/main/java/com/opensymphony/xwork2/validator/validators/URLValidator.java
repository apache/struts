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

import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * URLValidator checks that a given field is a String and a valid URL
 *
 * <pre>
 * &lt;validators&gt;
 *      &lt;!-- Plain Validator Syntax --&gt;
 *      &lt;validator type="url"&gt;
 *          &lt;param name="fieldName"&gt;myHomePage&lt;/param&gt;
 *          &lt;message&gt;Invalid homepage url&lt;/message&gt;
 *      &lt;/validator&gt;
 *
 *      &lt;!-- Field Validator Syntax --&gt;
 *      &lt;field name="myHomepage"&gt;
 *          &lt;field-validator type="url"&gt;
 *              &lt;message&gt;Invalid homepage url&lt;/message&gt;
 *          &lt;/field-validator&gt;
 *      &lt;/field&gt;
 * &lt;/validators&gt;
 * </pre>
 */
public class URLValidator extends FieldValidatorSupport {

    private static final Logger LOG = LogManager.getLogger(URLValidator.class);

    public static final String DEFAULT_URL_REGEX = "^(?:https?|ftp):\\/\\/" +
            "(?:(?:[a-z0-9$_.+!*'(),;?&=\\-]|%[0-9a-f]{2})+" +
            "(?::(?:[a-z0-9$_.+!*'(),;?&=\\-]|%[0-9a-f]{2})+)?" +
            "@)?#?" +
            "(?:(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)*" +
            "[a-z][a-z0-9-]*[a-z0-9]" +
            "|(?:(?:[1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}" +
            "(?:[1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])" +
            ")(?::\\d+)?" +
            ")(?:(?:\\/(?:[a-z0-9$_.+!*'(),;:@&=\\-]|%[0-9a-f]{2})*)*" +
            "(?:\\?(?:[a-z0-9$_.+!*'(),;:@&=\\-\\/:]|%[0-9a-f]{2})*)?)?" +
            "(?:#(?:[a-z0-9$_.+!*'(),;:@&=\\-]|%[0-9a-f]{2})*)?" +
            "$";

    private String urlRegexExpression;
    private Pattern urlPattern = Pattern.compile(DEFAULT_URL_REGEX, Pattern.CASE_INSENSITIVE);

    public void validate(Object object) throws ValidationException {
        Object value = getFieldValue(fieldName, object);

        String stringValue = Objects.toString(value, EMPTY_STRING).trim();
        if (stringValue.length() == 0) {
            LOG.debug("Value for field {} is empty, won't ba validated, please use a required validator", fieldName);
            return;
        }

        if (value.getClass().isArray()) {
            Object[] values = (Object[]) value;
            for (Object objValue : values) {
                LOG.debug("Validating element of array: {}", objValue);
                validateValue(object, objValue);
            }
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            Collection values = (Collection) value;
            for (Object objValue : values) {
                LOG.debug("Validating element of collection: {}", objValue);
                validateValue(object, objValue);
            }
        } else {
            LOG.debug("Validating field: {}", value);
            validateValue(object, value);
        }
    }

    protected void validateValue(Object object, Object value) {
        String stringValue = Objects.toString(value, EMPTY_STRING).trim();
        if (stringValue.length() == 0) {
            LOG.debug("Value for field {} is empty, won't ba validated, please use a required validator", fieldName);
            return;
        }

        try {
            setCurrentValue(value);
            if (!(value.getClass().equals(String.class)) || !getUrlPattern().matcher(stringValue).matches()) {
                addFieldError(fieldName, object);
            }
        } finally {
            setCurrentValue(null);
        }
    }

    protected Pattern getUrlPattern() {
        if (StringUtils.isNotEmpty(urlRegexExpression)) {
            String regex = (String) parse(urlRegexExpression, String.class);
            if (regex == null) {
                LOG.warn("Provided URL Regex expression [{}] was evaluated to null! Falling back to default!", urlRegexExpression);
                urlPattern = Pattern.compile(DEFAULT_URL_REGEX, Pattern.CASE_INSENSITIVE);
            } else {
                urlPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            }
        }
        return urlPattern;
    }

    /**
     * This is used to support client-side validation, it's based on
     * http://stackoverflow.com/questions/161738/what-is-the-best-regular-expression-to-check-if-a-string-is-a-valid-url
     *
     * @return regex to validate URLs
     */
    public String getUrlRegex() {
        return getUrlPattern().pattern();
    }

    public void setUrlRegex(String urlRegex) {
        urlPattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
    }

    public void setUrlRegexExpression(String urlRegexExpression) {
        this.urlRegexExpression = urlRegexExpression;
    }

}
