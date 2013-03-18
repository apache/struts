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
 * A Non-Field Level validator that validates based on regular expression supplied.
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 * 
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * 	 <li>expression - the Ognl expression to be evaluated against the stack (Must evaluate to a Boolean)</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 *
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 *     &lt;validators&gt;
 *           &lt;validator type="expression"&gt;
 *              &lt;param name="expression"&gt; .... &lt;/param&gt;
 *              &lt;message&gt;Failed to meet Ognl Expression  .... &lt;/message&gt;
 *           &lt;/validator&gt;
 *     &lt;/validators&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Jason Carreira
 */
public class ExpressionValidator extends ValidatorSupport {

    private String expression;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void validate(Object object) throws ValidationException {
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
            log.warn("Got result of [#0] when trying to get Boolean.", obj);
        }

        if (!answer) {
            if (log.isDebugEnabled()) {
                log.debug("Validation failed on expression [#0] with validated object [#1]", expression, object);
            }
            addActionError(object);
        }
    }
}
