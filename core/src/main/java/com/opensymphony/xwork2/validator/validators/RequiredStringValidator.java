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


/**
 * <!-- START SNIPPET: javadoc -->
 * RequiredStringValidator checks that a String field is non-null and has a length > 0.
 * (i.e. it isn't "").  The "trim" parameter determines whether it will {@link String#trim() trim}
 * the String before performing the length check.  If unspecified, the String will be trimmed.
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * 		<li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 *      <li>trim - (Optional) Boolean, default true. Trims the field name value before validating.</li>
 *      <li>trimExpression - (Optional) String. Specifies the trim param as an OGNL expression.</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 * 
 * <!-- START SNIPPET: parameters-warning -->
 * Do not use ${trimExpression} as an expression as this will turn into infinitive loop!
 * <!-- END SNIPPET: parameters-warning -->
 *
 * <pre>
 * <!-- START SNIPPET: examples -->
 *     &lt;validators&gt;
 *         &lt;!-- Plain-Validator Syntax --&gt;
 *         &lt;validator type="requiredstring"&gt;
 *             &lt;param name="fieldName"&gt;username&lt;/param&gt;
 *             &lt;param name="trim"&gt;true&lt;/param&gt;
 *             &lt;message&gt;username is required&lt;/message&gt;
 *         &lt;/validator&gt;
 *         
 *         &lt;!-- Field-Validator Syntax --&gt;
 *         &lt;field name="username"&gt;
 *         	  &lt;field-validator type="requiredstring"&gt;
 *                 &lt;param name="trim"&gt;true&lt;/param&gt;
 *                 &lt;message&gt;username is required&lt;/message&gt;
 *            &lt;/field-validator&gt;
 *         &lt;/field&gt;
 *
 *         &lt;!-- Field-Validator Syntax with expression --&gt;
 *         &lt;field name="username"&gt;
 *         	  &lt;field-validator type="requiredstring"&gt;
 *                 &lt;param name="trimExpression"&gt;${trimValue}&lt;/param&gt; &lt;!-- will be evaluated as: boolean getTrimValue() --&gt;
 *                 &lt;message&gt;username is required&lt;/message&gt;
 *            &lt;/field-validator&gt;
 *         &lt;/field&gt;
 *     &lt;/validators&gt;
 * <!-- END SNIPPET: examples -->
 * </pre>
 * 
 * @author rainerh
 * @version $Date$ $Id$
 */
public class RequiredStringValidator extends FieldValidatorSupport {

    private boolean trim = true;

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public void setTrimExpression(String trimExpression) {
        trim = (Boolean) parse(trimExpression, Boolean.class);
    }

    public boolean isTrim() {
        return trim;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);

        if (!(value instanceof String)) {
            addFieldError(fieldName, object);
        } else {
            String s = (String) value;

            if (trim) {
                s = s.trim();
            }

            if (s.length() == 0) {
                addFieldError(fieldName, object);
            }
        }
    }

}
