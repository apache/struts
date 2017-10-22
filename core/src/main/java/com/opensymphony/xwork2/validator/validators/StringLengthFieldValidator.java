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

/**
 * <!-- START SNIPPET: javadoc -->
 * StringLengthFieldValidator checks that a String field is of a certain length.  If the "minLength"
 * parameter is specified, it will make sure that the String has at least that many characters.  If
 * the "maxLength" parameter is specified, it will make sure that the String has at most that many
 * characters.  The "trim" parameter determines whether it will {@link String#trim() trim} the
 * String before performing the length check.  If unspecified, the String will be trimmed.
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * <li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 * <li>maxLength - Integer. The max length of the field value. Default ignore.</li>
 * <li>minLength - Integer. The min length of the field value. Default ignore.</li>
 * <li>trim - (Optional) Boolean, default true. Trim the field value before evaluating its min/max length. Default true.</li>
 * <li>maxLengthExpression - (Optional) String. Defines the max length param as an OGNL expression</li>
 * <li>minLengthExpression - (Optional) String. Defines the min length param as an OGNL expression</li>
 * <li>trimExpression - (Optional) String. Defines th trim param as an OGNL expression</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: parameters-warning -->
 * Do not use ${minLengthExpression}, ${maxLengthExpression} and ${trimExpression} as an expression as this will turn into infinitive loop!
 * <!-- END SNIPPET: parameters-warning -->
 *
 * <pre>
 * <!--START SNIPPET: example -->
 * &lt;validators&gt;
 *     &lt;!-- Plain Validator Syntax --&gt;
 *     &lt;validator type="stringlength"&gt;
 *         &lt;param name="fieldName"&gt;myPurchaseCode&lt;/param&gt;
 *         &lt;param name="minLength"&gt;10&lt;/param&gt;
 *         &lt;param name="maxLength"&gt;10&lt;/param&gt;
 *         &lt;param name="trim"&gt;true&lt;/param&gt;
 *         &lt;message&gt;Your purchase code needs to be 10 characters long&lt;/message&gt;
 *     &lt;/validator&gt;
 *
 *     &lt;!-- Field Validator Syntax --&gt;
 *     &lt;field name="myPurchaseCode"&gt;
 *         &lt;field-validator type="stringlength"&gt;
 *              &lt;param name="minLength"&gt;10&lt;/param&gt;
 *              &lt;param name="maxLength"&gt;10&lt;/param&gt;
 *              &lt;param name="trim"&gt;true&lt;/param&gt;
 *              &lt;message&gt;Your purchase code needs to be 10 characters long&lt;/message&gt;
 *         &lt;/field-validator&gt;
 *     &lt;/field&gt;
 *
 *     &lt;!-- Field Validator Syntax with expression --&gt;
 *     &lt;field name="myPurchaseCode"&gt;
 *         &lt;field-validator type="stringlength"&gt;
 *              &lt;param name="minLengthExpression"&gt;${minLengthValue}&lt;/param&gt; &lt;!-- will be evaluated as: Integer getMinLengthValue() --&gt;
 *              &lt;param name="maxLengthExpression"&gt;${maxLengthValue}&lt;/param&gt; &lt;!-- will be evaluated as: Integer getMaxLengthValue() --&gt;
 *              &lt;param name="trimExpression"&gt;${trimValue}&lt;/param&gt; &lt;!-- will be evaluated as: boolean getTrimValue() --&gt;
 *              &lt;message&gt;Your purchase code needs to be 10 characters long&lt;/message&gt;
 *         &lt;/field-validator&gt;
 *     &lt;/field&gt;
 * &lt;/validators&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Jason Carreira
 * @author Mark Woon
 * @author tmjee
 */
public class StringLengthFieldValidator extends FieldValidatorSupport {

    private static final Logger LOG = LogManager.getLogger(StringLengthFieldValidator.class);

    private boolean trim = true;
    private int maxLength = -1;
    private int minLength = -1;

    private String maxLengthExpression;
    private String minLengthExpression;
    private String trimExpression;

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setMaxLengthExpression(String maxLengthExpression) {
        this.maxLengthExpression = maxLengthExpression;
    }

    public int getMaxLength() {
        if (StringUtils.isNotEmpty(maxLengthExpression)) {
            return (Integer) parse(maxLengthExpression, Integer.class);
        }
        return maxLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public void setMinLengthExpression(String minLengthExpression) {
        this.minLengthExpression = minLengthExpression;
    }

    public int getMinLength() {
        if (StringUtils.isNotEmpty(minLengthExpression)) {
            return (Integer) parse(minLengthExpression, Integer.class);
        }
        return minLength;
    }

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public void setTrimExpression(String trimExpression) {
        this.trimExpression = trimExpression;
    }

    public boolean isTrim() {
        if (StringUtils.isNotEmpty(trimExpression)) {
            return (Boolean) parse(trimExpression, Boolean.class);
        }
        return trim;
    }

    public void validate(Object object) throws ValidationException {
        Object fieldValue = getFieldValue(fieldName, object);

        if (fieldValue == null) {
            LOG.debug("Value for field {} is null, use a required validator", getFieldName());
        } else if (fieldValue.getClass().isArray()) {
            Object[] values = (Object[]) fieldValue;
            for (Object value : values) {
                validateValue(object, value);
            }
        } else if (Collection.class.isAssignableFrom(fieldValue.getClass())) {
            Collection values = (Collection) fieldValue;
            for (Object value : values) {
                validateValue(object, value);
            }
        } else {
            validateValue(object, fieldValue);
        }
    }

    protected void validateValue(Object object, Object value) {
        String stringValue = Objects.toString(value, "");

        if (StringUtils.isEmpty(stringValue)) {
            LOG.debug("Value is empty, use a required validator");
            return;
        }

        if (isTrim()) {
            stringValue = stringValue.trim();
            if (StringUtils.isEmpty(stringValue)) {
                LOG.debug("Value is empty, use a required validator");
                return;
            }
        }

        int minLengthToUse = getMinLength();
        int maxLengthToUse = getMaxLength();

        try {
            setCurrentValue(stringValue);
            if ((minLengthToUse > -1) && (stringValue.length() < minLengthToUse)) {
                addFieldError(fieldName, object);
            } else if ((maxLengthToUse > -1) && (stringValue.length() > maxLengthToUse)) {
                addFieldError(fieldName, object);
            }
        } finally {
            setCurrentValue(null);
        }
    }

}
