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

import java.time.LocalDate;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Field Validator that checks if a {@link LocalDate} is within a specific range, both bounds inclusive.
 * Use {@link DateRangeFieldValidator} for a {@link java.util.Date} property.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 *      <li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 *      <li>min - the min date as an ISO date (yyyy-MM-dd). If not specified will not be checked.</li>
 *      <li>max - the max date as an ISO date (yyyy-MM-dd). If not specified will not be checked.</li>
 *      <li>minExpression - expression to calculate the minimum value (if none is specified, it will not be checked) </li>
 *      <li>maxExpression - expression to calculate the maximum value (if none is specified, it will not be checked) </li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: parameters-warning -->
 * Do not use ${minExpression} and ${maxExpression} as an expression as this will turn into infinitive loop!
 * <!-- END SNIPPET: parameters-warning -->
 *
 * <pre>
 * <!-- START SNIPPET: examples -->
 * &lt;validators&gt;
 *     &lt;field name="birthday"&gt;
 *         &lt;field-validator type="localdate"&gt;
 *             &lt;param name="min"&gt;1990-01-01&lt;/param&gt;
 *             &lt;param name="max"&gt;2000-01-01&lt;/param&gt;
 *             &lt;message&gt;Birthday must be within ${min} and ${max}&lt;/message&gt;
 *         &lt;/field-validator&gt;
 *     &lt;/field&gt;
 *
 *     &lt;field name="birthday"&gt;
 *         &lt;field-validator type="localdate"&gt;
 *             &lt;param name="minExpression"&gt;${minValue}&lt;/param&gt; &lt;!-- will be evaluated as: LocalDate getMinValue() --&gt;
 *             &lt;param name="maxExpression"&gt;${maxValue}&lt;/param&gt; &lt;!-- will be evaluated as: LocalDate getMaxValue() --&gt;
 *             &lt;message&gt;Birthday must be within ${min} and ${max}&lt;/message&gt;
 *         &lt;/field-validator&gt;
 *     &lt;/field&gt;
 * &lt;/validators&gt;
 * <!-- END SNIPPET: examples -->
 * </pre>
 *
 * @since 7.5.0
 */
public final class LocalDateRangeFieldValidator extends RangeValidatorSupport<LocalDate> {

    public LocalDateRangeFieldValidator() {
        super(LocalDate.class);
    }

}
