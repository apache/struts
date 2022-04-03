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

/**
 * <!-- START SNIPPET: javadoc -->
 * Field Validator that checks if the integer specified is within a certain range.
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * 		<li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 * 		<li>min - the minimum value (if none is specified, it will not be checked) </li>
 * 		<li>max - the maximum value (if none is specified, it will not be checked) </li>
 *      <li>parse - if set to true, minExpression and maxExpression will be evaluated to find min/max</li>
 *      <li>minExpression - expression to calculate the minimum value (if none is specified, it will not be checked) </li>
 *      <li>maxExpression - expression to calculate the maximum value (if none is specified, it will not be checked) </li>
 * </ul>
 *
 * You can either use the min / max value or minExpression / maxExpression (when parse is set to true) -
 * using expression can be slightly slower, see the example below.
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: parameters-warning -->
 * Do not use ${minExpression} and ${maxExpression} as an expression as this will turn into infinitive loop!
 * <!-- END SNIPPET: parameters-warning -->
 *
 * <pre>
 * <!-- START SNIPPET: examples -->
 * &lt;validators&gt;
 *      &lt;!-- Plain Validator Syntax --&gt;
 *      &lt;validator type="int"&gt;
 *          &lt;param name="fieldName"&gt;age&lt;/param&gt;
 *          &lt;param name="min"&gt;20&lt;/param&gt;
 *          &lt;param name="max"&gt;50&lt;/param&gt;
 *          &lt;message&gt;Age needs to be between ${min} and ${max}&lt;/message&gt;
 *      &lt;/validator&gt;
 *
 *      &lt;!-- Field Validator Syntax --&gt;
 *      &lt;field name="age"&gt;
 *          &lt;field-validator type="int"&gt;
 *              &lt;param name="min"&gt;20&lt;/param&gt;
 *              &lt;param name="max"&gt;50&lt;/param&gt;
 *              &lt;message&gt;Age needs to be between ${min} and ${max}&lt;/message&gt;
 *          &lt;/field-validator&gt;
 *      &lt;/field&gt;
 *
 *      &lt;!-- Field Validator Syntax with expression --&gt;
 *      &lt;field name="age"&gt;
 *          &lt;field-validator type="int"&gt;
 *              &lt;param name="minExpression"&gt;${minValue}&lt;/param&gt; &lt;!-- will be evaluated as: Integer getMinValue() --&gt;
 *              &lt;param name="maxExpression"&gt;${maxValue}&lt;/param&gt; &lt;!-- will be evaluated as: Integer getMaxValue() --&gt;
 *              &lt;message&gt;Age needs to be between ${min} and ${max}&lt;/message&gt;
 *          &lt;/field-validator&gt;
 *      &lt;/field&gt;
 * &lt;/validators&gt;
 * <!-- END SNIPPET: examples -->
 * </pre>
 *
 * @author Jason Carreira
 * @version $Date$ $Id$
 */
public final class IntRangeFieldValidator extends RangeValidatorSupport<Integer> {

    public IntRangeFieldValidator() {
        super(Integer.class);
    }

}
