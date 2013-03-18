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
import org.apache.commons.lang3.StringUtils;

/**
 * <!-- START SNIPPET: javadoc -->
 * StringLengthFieldValidator checks that a String field is of a certain length.  If the "minLength"
 * parameter is specified, it will make sure that the String has at least that many characters.  If
 * the "maxLength" parameter is specified, it will make sure that the String has at most that many
 * characters.  The "trim" parameter determines whether it will {@link String#trim() trim} the
 * String before performing the length check.  If unspecified, the String will be trimmed.
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 * <p/>
 * <p/>
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
 * <p/>
 * <!-- START SNIPPET: parameters-warning -->
 * Do not use ${minLengthExpression}, ${maxLengthExpression} and ${trimExpression} as an expression as this will turn into infinitive loop!
 * <!-- END SNIPPET: parameters-warning -->
 * <p/>
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
 * @version $Date$ $Id$
 */
public class StringLengthFieldValidator extends FieldValidatorSupport {

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
        String fieldName = getFieldName();
        String val = (String) getFieldValue(fieldName, object);

        if (val == null || val.length() <= 0) {
            // use a required validator for these
            return;
        }
        if (isTrim()) {
            val = val.trim();
            if (val.length() <= 0) {
                // use a required validator
                return;
            }
        }

        int minLengthToUse = getMinLength();
        int maxLengthToUse = getMaxLength();

        if ((minLengthToUse > -1) && (val.length() < minLengthToUse)) {
            addFieldError(fieldName, object);
        } else if ((maxLengthToUse > -1) && (val.length() > maxLengthToUse)) {
            addFieldError(fieldName, object);
        }
    }

}
