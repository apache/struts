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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <!-- START SNIPPET: javadoc -->
 * Validates a field using an OGNL expression.
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 *    <li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 *    <li>expression - The Ognl expression (must evaluate to a boolean) which is to be evalidated the stack</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 *    &lt;!-- Plain Validator Syntax --&gt;
 *    &lt;validators&gt;
 *        &lt;!-- Plain Validator Syntax --&gt;
 *        &lt;validator type="fieldexpression"&gt;
 *           &lt;param name="fieldName"&gt;myField&lt;/param&gt;
 *           &lt;param name="expression"&gt;&lt;![CDATA[#myCreditLimit &gt; #myGirfriendCreditLimit]]&gt;&lt;/param&gt;
 *           &lt;message&gt;My credit limit should be MORE than my girlfriend&lt;/message&gt;
 *        &lt;validator&gt;
 *        
 *        &lt;!-- Field Validator Syntax --&gt;
 *        &lt;field name="myField"&gt;
 *            &lt;field-validator type="fieldexpression"&gt;
 *                &lt;param name="expression"&gt;&lt;![CDATA[#myCreditLimit &gt; #myGirfriendCreditLimit]]&gt;&lt;/param&gt;
 *                &lt;message&gt;My credit limit should be MORE than my girlfriend&lt;/message&gt;
 *            &lt;/field-validator&gt;
 *        &lt;/field&gt;
 *        
 *    &lt;/vaidators&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 */
public class FieldExpressionValidator extends FieldValidatorSupport {

    private static final Logger LOG = LogManager.getLogger(FieldExpressionValidator.class);

    private String expression;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();

        Boolean answer = Boolean.FALSE;
        Object obj = null;

        try {
            obj = getFieldValue(expression, object);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            // let this pass, but it will be logged right below
        }

        if ((obj != null) && (obj instanceof Boolean)) {
            answer = (Boolean) obj;
        } else {
            LOG.warn("Got result of {} when trying to get Boolean.", obj);
        }

        if (!answer) {
            addFieldError(fieldName, object);
        }
    }
}
