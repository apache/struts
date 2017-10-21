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
package com.opensymphony.xwork2.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <!-- START SNIPPET: description -->
 * This validator checks that a date field has a value within a specified range.
 * <!-- END SNIPPET: description -->
 *
 * <p><u>Annotation usage:</u></p>
 *
 * <!-- START SNIPPET: usage -->
 * <p>The annotation must be applied at method level.</p>
 * <!-- END SNIPPET: usage -->
 *
 * <p><u>Annotation parameters:</u></p>
 *
 * <!-- START SNIPPET: parameters -->
 * <table class='confluenceTable' summary=''>
 * <tr>
 * <th class='confluenceTh'> Parameter </th>
 * <th class='confluenceTh'> Required </th>
 * <th class='confluenceTh'> Default </th>
 * <th class='confluenceTh'> Notes </th>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>message</td>
 * <td class='confluenceTd'>yes</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>field error message</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>key</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>i18n key from language specific properties file.</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>messageParams</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>Additional params to be used to customize message - will be evaluated against the Value Stack</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>fieldName</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>shortCircuit</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>false</td>
 * <td class='confluenceTd'>If this validator should be used as shortCircuit.</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>type</td>
 * <td class='confluenceTd'>yes</td>
 * <td class='confluenceTd'>ValidatorType.FIELD</td>
 * <td class='confluenceTd'>Enum value from ValidatorType. Either FIELD or SIMPLE can be used here.</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> min </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'> Date property.  The minimum the date must be. </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>minExpression</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>OGNL expression used to obtain the minimum the date must be.</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> max </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'> Date property.  The maximum date can be. </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>maxExpression</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>OGNL expression used to obtain the maximum date can be.</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>maxExpression</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>OGNL expression used to obtain the maximum date can be.</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>dateFormat</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>Format used to parse min/max value.</td>
 * </tr>
 * </table>
 *
 * <p>If neither <em>min</em> nor <em>max</em> is set, nothing will be done.</p>
 * <!-- END SNIPPET: parameters -->
 *
 * <p><u>Example code:</u></p>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &#64;DateRangeFieldValidator(message = "Default message", key = "i18n.key", shortCircuit = true, min = "2005/01/01", max = "2005/12/31")
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rainer Hermanns
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateRangeFieldValidator {

    /**
     *  Date property.
     *
     *  @return The minimum the date must be.
     */
    String min() default "";

    /**
     * @return An expression which will be evaluated against the Value Stack to get the min value
     */
    String minExpression() default "";

    /**
     *  Date property.
     *
     *  @return The maximum date can be.
     */
    String max() default "";

    /**
     * @return An expression which will be evaluated against the Value Stack to get the max value
     */
    String maxExpression() default "";

    /**
     * @return Date format used to parse min and max value
     */
    String dateFormat() default "";

    /**
     * @return The default error message for this validator.
     * NOTE: It is required to set a message, if you are not using the message key for 18n lookup!
     */
    String message() default "";

    /**
     * @return The message key to lookup for i18n.
     */
    String key() default "";

    /**
     * @return Additional params to be used to customize message - will be evaluated against the Value Stack
     */
    String[] messageParams() default {};

    /**
     * @return The optional fieldName for SIMPLE validator types.
     */
    String fieldName() default "";

    /**
     * If this is activated, the validator will be used as short-circuit.
     *
     * Adds the short-circuit="true" attribute value if <tt>true</tt>.
     * @return true if validator will be used as short-circuit. Default is false.
     */
    boolean shortCircuit() default false;

    /**
     * @return The validation type for this field/method.
     */
    ValidatorType type() default ValidatorType.FIELD;

}
