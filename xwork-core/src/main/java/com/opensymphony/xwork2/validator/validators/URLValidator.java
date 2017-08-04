/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.util.URLUtil;
import org.apache.commons.lang3.StringUtils;

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

    private String urlRegex;
    private String urlRegexExpression;

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);

        // if there is no value - don't do comparison
        // if a value is required, a required validator should be added to the field
        if (value == null || value.toString().length() == 0) {
            return;
        }

        // FIXME deprecated! the same regex below should be used instead
        // replace logic with next major release
        if (!(value.getClass().equals(String.class)) || !URLUtil.verifyUrl((String) value)) {
            addFieldError(fieldName, object);
        }
    }

    /**
     * This is used to support client-side validation, it's based on
     * http://stackoverflow.com/questions/161738/what-is-the-best-regular-expression-to-check-if-a-string-is-a-valid-url
     *
     * @return regex to validate URLs
     */
    public String getUrlRegex() {
        if (StringUtils.isNotEmpty(urlRegexExpression)) {
            return (String) parse(urlRegexExpression, String.class);
        } else if (StringUtils.isNotEmpty(urlRegex)) {
            return urlRegex;
        } else {
            return "^(?:https?|ftp):\\/\\/" +
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
        }
    }

    public void setUrlRegex(String urlRegex) {
        this.urlRegex = urlRegex;
    }

    public void setUrlRegexExpression(String urlRegexExpression) {
        this.urlRegexExpression = urlRegexExpression;
    }

}
