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
package org.apache.struts2.validator.validators;

import org.apache.struts2.validator.ValidationException;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * <!-- START SNIPPET: javadoc -->
 * RequiredFieldValidator checks if the specified field is not null.
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * 		<li>fieldName - field name if plain-validator syntax is used, not needed if field-validator syntax is used</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * 	   &lt;validators&gt;
 *
 *         &lt;!-- Plain Validator Syntax --&gt;
 *         &lt;validator type="required"&gt;
 *             &lt;param name="fieldName"&gt;username&lt;/param&gt;
 *             &lt;message&gt;username must not be null&lt;/message&gt;
 *         &lt;/validator&gt;
 *
 *
 *         &lt;!-- Field Validator Syntax --&gt;
 *         &lt;field name="username"&gt;
 *             &lt;field-validator type="required"&gt;
 *             	   &lt;message&gt;username must not be null&lt;/message&gt;
 *             &lt;/field-validator&gt;
 *         &lt;/field&gt;
 *
 *     &lt;/validators&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 *
 *
 * @author rainerh
 */
public class RequiredFieldValidator extends FieldValidatorSupport {

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);

        if (isMissing(value)) {
            addFieldError(fieldName, object);
        }
    }

    /**
     * @return whether this validator rejects the value: null, an empty array or an empty collection
     * @since 7.4.0
     */
    public boolean isMissing(Object value) {
        if (value == null) {
            return true;
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        }
        return value instanceof Collection<?> collection && collection.isEmpty();
    }
}
