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
 * This validator checks that a String field is of the right length. It assumes that the field is a String.
 * If neither minLength nor maxLength is set, nothing will be done.
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
 * <td class='confluenceTd'> trim </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'> true </td>
 * <td class='confluenceTd'> Boolean property.  Determines whether the String is trimmed before performing the length check.  </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> minLength </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'> Integer property.  The minimum length the String must be. </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> minLengthExpression </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>OGNL expression used to obtain the minimum length the String must be. </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> maxLength </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'> Integer property.  The maximum length the String can be. </td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'> maxLengthExpression </td>
 * <td class='confluenceTd'> no </td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>OGNL expression used to obtain the maximum length the String can be. </td>
 * </tr>
 * </table>
 *
 * <p>If neither <em>minLength</em> nor <em>maxLength</em> is set, nothing will be done.</p>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p><u>Example code:</u></p>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &#64;StringLengthFieldValidator(message = "Default message", key = "i18n.key", shortCircuit = true, trim = true, minLength = "5",  maxLength = "12")
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rainer Hermanns
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringLengthFieldValidator {

    /**
     *  Boolean property.
     *
     *  @return Determines whether the String is trimmed before performing the length check.
     */
    boolean trim() default true;

    /**
     * @return Determines whether the String is trimmed before performing the length check but defined as an expression
     */
    String trimExpression() default "";

    /**
     *  Integer property.
     *
     *  @return The minimum length the String must be.
     */
    String minLength() default "";

    /**
     * @return The minimum length the String must be defined as an expression
     */
    String minLengthExpression() default "";

    /**
     *  Integer property.
     *
     *  @return The maximum length the String can be.
     */
    String maxLength() default "";

    /**
     * @return The maximum length the String can be defined as an expression
     */
    String maxLengthExpression() default "";

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
     *
     * @return true if validator will be used as short-circuit. Default is false.
     */
    boolean shortCircuit() default false;

    /**
     * @return The validation type for this field/method.
     */
    ValidatorType[] type() default {ValidatorType.FIELD};

}
